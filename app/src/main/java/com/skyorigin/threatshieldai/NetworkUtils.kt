package com.skyorigin.threatshieldai

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object NetworkUtils {
    private const val TAG = "NetworkUtils"

    private val _isOnline = MutableStateFlow(true)
    val isOnlineState: StateFlow<Boolean> = _isOnline.asStateFlow()

    private var registeredCallback: ConnectivityManager.NetworkCallback? = null

    /**
     * Checks if the device has an active network interface with INTERNET capability.
     */
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        
        val hasInternetCapability = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasValidTransport = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

        return hasInternetCapability && hasValidTransport
    }

    /**
     * Performs a validated check for genuine internet connectivity.
     * Prevents false positives where Wi-Fi or Cellular is connected without actual internet access.
     */
    suspend fun isInternetAvailable(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (!isNetworkConnected(context)) {
            _isOnline.value = false
            return@withContext false
        }

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)

        // If OS has already validated network capability
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            _isOnline.value = true
            return@withContext true
        }

        // Active socket/HTTPS verification as fallback to guarantee internet access
        val isVerified = verifyConnectionWithSocket() || verifyConnectionWithHttps()
        _isOnline.value = isVerified
        return@withContext isVerified
    }

    private fun verifyConnectionWithSocket(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun verifyConnectionWithHttps(): Boolean {
        return try {
            val url = URL("https://www.google.com")
            val connection = url.openConnection() as HttpsURLConnection
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.requestMethod = "HEAD"
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode in 200..399
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Registers a ConnectivityManager.NetworkCallback to observe real-time network changes.
     */
    fun startMonitoring(context: Context) {
        if (registeredCallback != null) return
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = isNetworkConnected(context)
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                _isOnline.value = hasInternet && isValidated
            }
        }

        try {
            connectivityManager.registerNetworkCallback(request, callback)
            registeredCallback = callback
            _isOnline.value = isNetworkConnected(context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register network callback", e)
        }
    }

    fun stopMonitoring(context: Context) {
        registeredCallback?.let {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            try {
                connectivityManager?.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unregister network callback", e)
            }
            registeredCallback = null
        }
    }
}
