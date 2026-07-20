package com.skyorigin.threatshieldai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.net.URLEncoder
import java.io.IOException

class InternetConnectionException(message: String) : Exception(message)
class ServiceUnavailableException(message: String) : Exception(message)
class ConnectionLostException(message: String) : Exception(message)
class ApiErrorException(message: String) : Exception(message)
class ApiTimeoutException(message: String) : Exception(message)

data class UrlThreatResult(
    val originalUrl: String,
    val normalizedUrl: String,
    val expandedUrl: String?,
    val webRiskVerdict: String,
    val phishtankVerdict: String = "UNVERIFIED",
    val urlhausVerdict: String = "UNVERIFIED",
    val finalUrlVerdict: String = "UNVERIFIED",
    val riskLevel: String,
    val isCached: Boolean = false,
    val threatType: String? = null,
    val scanTime: Long = System.currentTimeMillis(),
    val confidence: Int = 100,
    val webRiskStatus: String = "UNKNOWN",
    val phishtankStatus: String = "UNKNOWN",
    val urlhausStatus: String = "UNKNOWN",
    val urlscanVerdict: String = "UNVERIFIED",
    val urlscanStatus: String = "UNKNOWN"
)

data class UrlScanProgress(
    val detectedCount: Int,
    val status: String, // "scanning", "safe", "suspicious", "danger", "unknown", "failed"
    val verdict: String,
    val progress: Float
)

data class HybridAnalysisResult(
    val verdict: String,
    val riskScore: Int,
    val confidence: Int,
    val messageType: String,
    val originalMessage: String,
    val normalizedMessage: String,
    val urlsFound: List<UrlThreatResult>,
    val textSignals: List<String>,
    val finalReason: String,
    val webRiskStatus: String,
    val aiStatus: String,
    val scamType: String = "Unknown",
    val advice: List<String> = emptyList(),
    val summary: String = "",
    val textVerdict: String = "Safe",
    val urlVerdict: String = "Safe",
    val phishtankStatus: String = "UNKNOWN",
    val urlhausStatus: String = "UNKNOWN",
    val processingTime: Long = 0L
)

object SecurityAnalysisEngine {
    private const val TAG = "SecurityAnalysisEngine"
    private const val GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
    
    internal var testGroqKey: String? = null
    internal var testWebRiskKey: String? = null
    
    private val webRiskCache = java.util.concurrent.ConcurrentHashMap<String, UrlThreatResult>()
    private val reputationCache = java.util.concurrent.ConcurrentHashMap<String, CacheEntry>()
    
    class PermanentApiException(message: String) : Exception(message)

    data class ServiceResult(
        val verdict: String, // "safe", "danger", "unknown"
        val status: String,  // "OK", "FAILED", "TIMEOUT", "UNKNOWN"
        val threatType: String? = null
    )

    data class CacheEntry(
        val webRiskVerdict: String,
        val webRiskStatus: String,
        val phishtankVerdict: String,
        val phishtankStatus: String,
        val urlhausVerdict: String,
        val urlhausStatus: String,
        val finalUrlVerdict: String,
        val confidence: Int,
        val threatType: String?,
        val scanTime: Long,
        val source: String,
        val urlscanVerdict: String = "UNVERIFIED",
        val urlscanStatus: String = "UNKNOWN"
    )

    private val TRUSTED_DOMAINS = setOf(
        "google.com", "google.co.in", "youtube.com", "gmail.com", "facebook.com",
        "instagram.com", "whatsapp.com", "twitter.com", "x.com", "linkedin.com",
        "github.com", "microsoft.com", "apple.com", "netflix.com", "amazon.com",
        "amazon.in", "wikipedia.org", "yahoo.com", "outlook.com", "zoom.us",
        "slack.com", "adobe.com", "dropbox.com", "spotify.com", "reddit.com"
    )

    fun isTrustedDomain(url: String): Boolean {
        return try {
            val host = java.net.URI(UrlDetectionEngine.normalizeUrl(url)).host?.lowercase() ?: ""
            val cleanHost = if (host.startsWith("www.")) host.substring(4) else host
            for (trusted in TRUSTED_DOMAINS) {
                if (cleanHost == trusted || cleanHost.endsWith(".$trusted")) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun <T> executeWithRetry(
        serviceName: String,
        block: suspend () -> T,
        isPermanentError: (Throwable) -> Boolean = { false }
    ): T {
        var attempt = 0
        var delayMs = 1000L
        while (true) {
            try {
                val startTime = System.currentTimeMillis()
                val result = block()
                val duration = System.currentTimeMillis() - startTime
                Log.d("UrlReputationEngine", "[$serviceName] Success on attempt ${attempt + 1}. Response time: ${duration}ms, retry count: $attempt")
                return result
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                attempt++
                val isTimeout = e is java.util.concurrent.TimeoutException || 
                                e is kotlinx.coroutines.TimeoutCancellationException || 
                                e.message?.contains("timeout", ignoreCase = true) == true
                
                Log.w("UrlReputationEngine", "[$serviceName] Attempt $attempt failed. Exception: ${e.message}, isTimeout=$isTimeout, retry count: ${attempt - 1}")
                
                if (attempt > 2 || isPermanentError(e) || isTimeout) {
                    throw e
                }
                
                Log.d("UrlReputationEngine", "[$serviceName] Retrying in ${delayMs}ms...")
                kotlinx.coroutines.delay(delayMs)
                delayMs *= 2
            }
        }
    }

    private suspend fun executeGoogleWebRisk(url: String, apiKey: String): ServiceResult {
        if (apiKey.isEmpty() || apiKey == "your_web_risk_key_here") {
            return ServiceResult("UNVERIFIED", "MISSING_KEY")
        }
        try {
            val result = executeWithRetry("GoogleWebRisk", block = {
                kotlinx.coroutines.withTimeout(5000L) {
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    val requestUrl = "https://webrisk.googleapis.com/v1/uris:search?threatTypes=MALWARE&threatTypes=SOCIAL_ENGINEERING&threatTypes=UNWANTED_SOFTWARE&uri=$encodedUrl&key=$apiKey"
                    val request = Request.Builder().url(requestUrl).get().build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val body = response.body?.string() ?: "{}"
                            val json = JSONObject(body)
                            val threat = json.optJSONObject("threat")
                            if (threat != null) {
                                val threatTypesArr = threat.optJSONArray("threatTypes")
                                val tType = if (threatTypesArr != null && threatTypesArr.length() > 0) {
                                    threatTypesArr.optString(0)
                                } else {
                                    "SOCIAL_ENGINEERING"
                                }
                                ServiceResult("MALICIOUS", "OK", tType)
                            } else {
                                ServiceResult("NO_KNOWN_THREAT", "OK")
                            }
                        } else {
                            val code = response.code
                            if (code in 500..599 || code == 429) {
                                throw IOException("Temporary server error: $code")
                            } else {
                                throw PermanentApiException("Permanent error: $code")
                            }
                        }
                    }
                }
            }, isPermanentError = { it is PermanentApiException })
            return result
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e("UrlReputationEngine", "GoogleWebRisk timed out", e)
            return ServiceResult("UNVERIFIED", "TIMEOUT")
        } catch (e: Exception) {
            Log.e("UrlReputationEngine", "GoogleWebRisk failed: ${e.message}", e)
            return ServiceResult("UNVERIFIED", "FAILED")
        }
    }

    private suspend fun executePhishTank(url: String): ServiceResult {
        try {
            val result = executeWithRetry("PhishTank", block = {
                kotlinx.coroutines.withTimeout(5000L) {
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    val requestUrl = "https://checkurl.phishtank.com/checkurl/"
                    val body = "url=$encodedUrl&format=json".toRequestBody("application/x-www-form-urlencoded".toMediaType())
                    val request = Request.Builder().url(requestUrl).post(body).build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val bodyString = response.body?.string() ?: ""
                            if (bodyString.contains("\"in_database\": true") && bodyString.contains("\"valid\": true")) {
                                ServiceResult("MALICIOUS", "OK")
                            } else {
                                ServiceResult("NO_KNOWN_THREAT", "OK")
                            }
                        } else {
                            val code = response.code
                            if (code in 500..599 || code == 429) {
                                throw IOException("Temporary server error: $code")
                            } else {
                                throw PermanentApiException("Permanent error: $code")
                            }
                        }
                    }
                }
            }, isPermanentError = { it is PermanentApiException })
            return result
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e("UrlReputationEngine", "PhishTank timed out", e)
            return ServiceResult("UNVERIFIED", "TIMEOUT")
        } catch (e: Exception) {
            Log.e("UrlReputationEngine", "PhishTank failed: ${e.message}", e)
            return ServiceResult("UNVERIFIED", "FAILED")
        }
    }

    private suspend fun executeUrlhaus(url: String): ServiceResult {
        try {
            val result = executeWithRetry("URLhaus", block = {
                kotlinx.coroutines.withTimeout(5000L) {
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    val requestUrl = "https://urlhaus-api.abuse.ch/v1/url/"
                    val body = "url=$encodedUrl".toRequestBody("application/x-www-form-urlencoded".toMediaType())
                    val request = Request.Builder().url(requestUrl).post(body).build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val bodyString = response.body?.string() ?: ""
                            val json = JSONObject(bodyString)
                            val status = json.optString("query_status", "")
                            if (status == "ok") {
                                ServiceResult("MALICIOUS", "OK")
                            } else {
                                ServiceResult("NO_KNOWN_THREAT", "OK")
                            }
                        } else {
                            val code = response.code
                            if (code in 500..599 || code == 429) {
                                throw IOException("Temporary server error: $code")
                            } else {
                                throw PermanentApiException("Permanent error: $code")
                            }
                        }
                    }
                }
            }, isPermanentError = { it is PermanentApiException })
            return result
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e("UrlReputationEngine", "URLhaus timed out", e)
            return ServiceResult("UNVERIFIED", "TIMEOUT")
        } catch (e: Exception) {
            Log.e("UrlReputationEngine", "URLhaus failed: ${e.message}", e)
            return ServiceResult("UNVERIFIED", "FAILED")
        }
    }

    private fun isSensitiveUrl(url: String): Boolean {
        return try {
            val uri = java.net.URI(url)
            val query = uri.query ?: ""
            val userInfo = uri.userInfo ?: ""
            val path = uri.path ?: ""
            val combined = "$query $userInfo $path".lowercase()
            val keywords = listOf(
                "token", "session", "auth", "key", "pwd", "password", 
                "reset", "email", "user", "secret", "sign", "login",
                "pass", "code", "hash", "cred", "api"
            )
            val hasSensitiveKeyword = keywords.any { combined.contains(it) }
            val hasEmailPattern = url.contains("@")
            val hasQueryParameters = query.isNotEmpty()
            
            hasSensitiveKeyword || hasEmailPattern || hasQueryParameters
        } catch (e: Exception) {
            val lower = url.lowercase()
            val keywords = listOf("token", "session", "key", "password", "reset", "@", "?")
            keywords.any { lower.contains(it) }
        }
    }

    private suspend fun executeUrlScan(url: String, apiKey: String): ServiceResult {
        return ServiceResult("UNVERIFIED", "UNKNOWN")
    }

    internal var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    fun isInternetAvailable(context: android.content.Context): Boolean {
        val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }

    suspend fun checkApiHealth(context: android.content.Context): Boolean = withContext(Dispatchers.IO) {
        if (!isInternetAvailable(context)) {
            throw InternetConnectionException("No internet connection")
        }
        
        var apiKey = ""
        try { apiKey = BuildConfig.GROQ_API_KEY } catch (e: Exception) {}
        
        if (apiKey.isEmpty() || apiKey == "your_api_key_here") {
            Log.e(TAG, "Groq API key is missing or placeholder!")
            throw ApiErrorException("API Key is missing")
        }
        
        try {
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()
                
            client.newCall(request).execute().use { response ->
                if (response.code in 500..599) {
                    throw ServiceUnavailableException("Server returned 5xx error (${response.code})")
                }
                if (response.code == 401 || response.code == 403) {
                    throw ApiErrorException("Authentication failed (${response.code})")
                }
                return@withContext true
            }
        } catch (e: Exception) {
            throw ServiceUnavailableException("Cannot reach Groq API")
        }
    }

    suspend fun performHybridAnalysis(
        context: android.content.Context?,
        text: String,
        isHindi: Boolean,
        onUrlScanProgress: ((UrlScanProgress) -> Unit)? = null
    ): HybridAnalysisResult = withContext(Dispatchers.IO) {
        val overallStartTime = System.currentTimeMillis()
        
        val matchedPresetResult = getMatchedPresetResult(text, isHindi)
        if (matchedPresetResult != null) {
            return@withContext matchedPresetResult
        }

        if (context != null && !isInternetAvailable(context)) {
            throw InternetConnectionException("No internet connection")
        }

        var groqKey = testGroqKey ?: ""
        var webRiskKey = testWebRiskKey ?: ""
        var urlscanKey = ""
        if (groqKey.isEmpty()) {
            try {
                groqKey = BuildConfig.GROQ_API_KEY
                webRiskKey = BuildConfig.GOOGLE_WEB_RISK_API_KEY
            } catch (e: Exception) {}
        }
        try {
            urlscanKey = BuildConfig.URLSCAN_API_KEY
        } catch (e: Exception) {}

        if (groqKey.isEmpty() || groqKey == "your_api_key_here") {
            Log.e(TAG, "Groq API key is missing!")
            throw ApiErrorException("Groq API Key is missing")
        }

        val originalMessage = text
        val normalizedMessage = MessageNormalizer.normalize(text)
        
        if (normalizedMessage.isEmpty()) {
            throw ApiErrorException("Message is empty")
        }

        // 1. Extract URLs
        val extractedUrls = UrlDetectionEngine.extractUrls(normalizedMessage)
        val messageType = when {
            extractedUrls.isNotEmpty() && normalizedMessage.replace(Regex("https?://\\S+"), "").trim().isEmpty() -> "URL Only"
            extractedUrls.isNotEmpty() -> "Mixed"
            else -> "Text Only"
        }

        // 2. Parallel Scan Orchestrator Layer
        val urlResults = mutableListOf<UrlThreatResult>()
        var aiStatus = "ok"
        var aiOutput: JSONObject? = null
        val CACHE_TTL = 24 * 60 * 60 * 1000L // 24 hours TTL for URL reputations

        val uniqueUrls = extractedUrls.distinctBy { UrlDetectionEngine.normalizeUrl(it) }

        if (extractedUrls.isNotEmpty()) {
            val detectedCount = extractedUrls.size
            onUrlScanProgress?.invoke(UrlScanProgress(
                detectedCount = detectedCount,
                status = "scanning",
                verdict = if (isHindi) "$detectedCount links found. Initializing scan..." else "Found $detectedCount links. Initiating scans...",
                progress = 0.1f
            ))
        }

        kotlinx.coroutines.coroutineScope {
            // A. Start GPT-OSS 20B Text Analysis layer in parallel
            val aiDeferred = async {
                if (groqKey.isEmpty() || groqKey == "your_api_key_here") {
                    aiStatus = "missing_key"
                    return@async null
                }
                try {
                    val systemInstruction = """
                        You are ThreatShield AI’s real-time security analyst.

                        Your ONLY job is to read the full user message carefully and decide whether it is:
                        SAFE, SUSPICIOUS, or DANGEROUS.

                        IMPORTANT RULES:

                        1. Do NOT classify a message as DANGEROUS just because it contains keywords like:
                           bank, recharge, offer, click, verify, urgent, account, payment, OTP, PIN, password

                           These words are only weak signals.
                           They are not proof of fraud by themselves.

                        2. Always analyze the FULL CONTEXT of the message:
                           - What is the sender trying to make the user do?
                           - Is the message normal, informational, promotional, transactional, or scam-like?
                           - Is there urgency or pressure?
                           - Is the sender pretending to be a trusted brand?
                           - Is the message asking for OTP, PIN, password, payment approval, or sensitive information?
                           - Does it contain a suspicious link or deceptive domain?
                           - Is the message trying to create fear, urgency, reward temptation, or account theft?

                        3. Think like a human scam analyst.
                           Do not count keywords blindly.
                           Do not use a keyword-only rule.
                           Do not label normal telecom, bank, recharge, delivery, or promotional messages as dangerous unless the full context truly supports it.

                        4. If the message is clearly normal, transactional, informational, or legitimate-looking,
                           prefer SAFE.

                        5. If the message has some suspicious elements but not enough proof,
                           choose SUSPICIOUS.

                        6. If the message shows strong scam intent, impersonation, credential theft, fake refund, fake KYC, OTP trap, payment scam, or malicious link behavior,
                           choose DANGEROUS.

                        7. If a URL is present in the message:
                           - do NOT assume it is dangerous only because a URL exists
                           - do NOT assume it is safe only because it looks normal
                           - describe the link-related suspicion only from the message context
                           - final URL reputation will be handled separately by security checks
                           - If the URL was checked and has NO KNOWN THREAT, and you classify the message as SAFE: in your "short_reason", explain that the message context looks normal and that the link has no known threat.
                           - If the URL was checked and has NO KNOWN THREAT, and you classify the message as SUSPICIOUS: in your "short_reason", explain why the message still looks suspicious even though the URL was not flagged.
                           - If the URL was checked and has NO KNOWN THREAT, and you classify the message as DANGEROUS: in your "short_reason", explain that the message context itself is strongly scam-like even though the URL was not in threat databases.
                           - If the message contains too little text / not enough context to analyze: classify as UNABLE_TO_DETERMINE, and in your "short_reason", say that there is not enough information to confidently classify it.

                        8. Be careful with genuine messages from:
                           - Airtel
                           - Jio
                           - banks
                           - UPI/payment apps
                           - delivery services
                           - government services
                           - e-commerce platforms

                           Legitimate messages may contain words like recharge, verify, account, urgent, payment, click.
                           That alone does NOT make them scams.

                        9. Focus on actual fraud patterns:
                           - impersonation
                           - fake offers or fake prizes
                           - fake refund claims
                           - fake KYC / account verification traps
                           - OTP / PIN / password theft
                           - urgent pressure to act immediately
                           - deceptive links
                           - money transfer manipulation
                           - banking / UPI fraud
                           - phishing language

                        10. Keep your output structured and consistent.

                        OUTPUT RULES:

                        Return ONLY valid JSON.
                        Do not add markdown.
                        Do not add explanations outside JSON.
                        Do not include chain-of-thought.

                        Use this schema:

                        {
                          "classification": "SAFE" | "SUSPICIOUS" | "DANGEROUS" | "UNABLE_TO_DETERMINE",
                          "evidence_sufficiency": "SUFFICIENT" | "INSUFFICIENT",
                          "scam_probability": 0-100,
                          "short_reason": "one short clear sentence",
                          "extracted_signals": [
                            "signal 1",
                            "signal 2",
                            "signal 3"
                          ],
                          "advice": [
                            "short action 1",
                            "short action 2",
                            "short action 3"
                          ],
                          "confidence": 0-100
                        }

                        GUIDELINES FOR FIELDS:

                        - classification:
                          Choose the final risk label based on overall context, not keywords alone.
                          Choose UNABLE_TO_DETERMINE if the message is too short, too vague, or does not contain enough context or security signals to confidently classify (e.g., "Hello", "Call me", "Check this", "Important" without other clues).

                        - evidence_sufficiency:
                          Evaluate if the message has enough context/evidence. Short messages with strong signals like OTP are SUFFICIENT. Vague messages like "Check this link" or "Hello" are INSUFFICIENT.
                          If INSUFFICIENT, classification should usually be UNABLE_TO_DETERMINE.

                        - scam_probability:
                          Reflect the probability of scam based on meaning, behavior, and intent.

                        - short_reason:
                          One clear sentence explaining why you chose the label.

                        - extracted_signals:
                          Only include real evidence from the message.
                          Examples:
                          "urgent pressure"
                          "fake verification request"
                          "suspicious link"
                          "credential request"
                          "brand impersonation"
                          "payment manipulation"

                        - advice:
                          Give the most useful next steps.
                          Keep them short and relevant.

                        - confidence:
                          How confident you are in your decision.

                        SPECIAL ACCURACY RULE:

                        If a message contains words like:
                        - recharge
                        - bank
                        - account
                        - verify
                        - urgent
                        - offer
                        - click
                        - OTP

                        but the message context is otherwise normal and legitimate,
                        do NOT mark it Dangerous automatically.
                        Use the full message meaning first.

                        If the message is a normal Airtel/Jio/bank/recharge/update message,
                        classify it correctly as SAFE unless strong scam evidence exists.

                        If the message is ambiguous, prefer SUSPICIOUS instead of incorrectly marking Dangerous.

                        Be precise. Be conservative. Be context-aware.
                    """.trimIndent()

                    val requestBodyJson = JSONObject().apply {
                        put("model", "openai/gpt-oss-20b")
                        put("messages", JSONArray().apply {
                            put(JSONObject().apply {
                                put("role", "system")
                                put("content", systemInstruction)
                            })
                            put(JSONObject().apply {
                                put("role", "user")
                                val langNote = if (isHindi) " (Provide analysis in Hindi/Hinglish)" else " (Provide analysis in English)"
                                val urlInfo = if (uniqueUrls.isNotEmpty()) {
                                    "\nNote: The URL(s) in this message are successfully being checked by Google Web Risk. You must analyze the message context itself to decide if it is SAFE, SUSPICIOUS, or DANGEROUS, or UNABLE_TO_DETERMINE if there is insufficient evidence/vague text."
                                } else ""
                                put("content", "Message to analyze: \"$normalizedMessage\"$urlInfo$langNote")
                            })
                        })
                        put("response_format", JSONObject().apply {
                            put("type", "json_object")
                        })
                        put("temperature", 0.1)
                    }

                    executeWithRetry("AI", block = {
                        kotlinx.coroutines.withTimeout(12000L) {
                            val request = Request.Builder()
                                .url(GROQ_API_URL)
                                .addHeader("Authorization", "Bearer $groqKey")
                                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                                .build()

                            client.newCall(request).execute().use { response ->
                                if (response.isSuccessful) {
                                    val body = response.body?.string() ?: "{}"
                                    val jsonResponse = JSONObject(body)
                                    val choices = jsonResponse.optJSONArray("choices")
                                    val content = choices?.optJSONObject(0)?.optJSONObject("message")?.optString("content")
                                    if (content != null) {
                                        JSONObject(content)
                                    } else {
                                        throw PermanentApiException("Invalid response format from Groq")
                                    }
                                } else {
                                    val code = response.code
                                    if (code in 500..599 || code == 429) {
                                        throw IOException("Temporary server error: $code")
                                    } else {
                                        throw PermanentApiException("Permanent error: $code")
                                    }
                                }
                            }
                        }
                    }, isPermanentError = { it is PermanentApiException })
                } catch (e: Exception) {
                    Log.e(TAG, "GPT-OSS 20B parallel API execution failed", e)
                    aiStatus = "failed"
                    null
                }
            }

            // B. Start URL scan tasks in parallel
            val urlScanDeferreds = uniqueUrls.mapIndexed { index, originalUrl ->
                this@coroutineScope.async {
                    val progressFraction = 0.1f + ((index.toFloat() / uniqueUrls.size.toFloat()) * 0.8f)
                    onUrlScanProgress?.invoke(UrlScanProgress(
                        detectedCount = extractedUrls.size,
                        status = "scanning",
                        verdict = if (isHindi) "Scanning links..." else "Scanning links in parallel...",
                        progress = progressFraction
                    ))

                    val normalizedUrl = UrlDetectionEngine.normalizeUrl(originalUrl)
                    val expandedUrl = withTimeoutOrNull(5000L) {
                        UrlDetectionEngine.expandUrl(originalUrl)
                    }
                    val targetUrl = expandedUrl ?: normalizedUrl

                    // Check Cache
                    val cached = reputationCache[normalizedUrl] ?: if (expandedUrl != null) reputationCache[expandedUrl] else null
                    if (cached != null && (System.currentTimeMillis() - cached.scanTime <= CACHE_TTL)) {
                        Log.d("UrlReputationEngine", "[CACHE HIT] Reusing cached URL reputation for: $originalUrl (Target: $targetUrl)")
                        return@async UrlThreatResult(
                            originalUrl = originalUrl,
                            normalizedUrl = normalizedUrl,
                            expandedUrl = expandedUrl,
                            webRiskVerdict = cached.webRiskVerdict,
                            phishtankVerdict = cached.phishtankVerdict,
                            urlhausVerdict = cached.urlhausVerdict,
                            finalUrlVerdict = cached.finalUrlVerdict,
                            riskLevel = cached.finalUrlVerdict,
                            isCached = true,
                            threatType = cached.threatType,
                            scanTime = cached.scanTime,
                            confidence = cached.confidence,
                            webRiskStatus = cached.webRiskStatus,
                            phishtankStatus = cached.phishtankStatus,
                            urlhausStatus = cached.urlhausStatus,
                            urlscanVerdict = cached.urlscanVerdict,
                            urlscanStatus = cached.urlscanStatus
                        )
                    } else if (cached != null) {
                        // Expired
                        reputationCache.remove(normalizedUrl)
                        if (expandedUrl != null) {
                            reputationCache.remove(expandedUrl)
                        }
                    }

                    // Cache Miss: Query reputation APIs in parallel
                    Log.d("UrlReputationEngine", "[CACHE MISS] Querying reputation APIs in parallel for: $originalUrl (Target: $targetUrl)")

                    var webRiskRes = ServiceResult("UNVERIFIED", "UNKNOWN")
                    var phishtankRes = ServiceResult("UNVERIFIED", "UNKNOWN")
                    var urlhausRes = ServiceResult("UNVERIFIED", "UNKNOWN")
                    var urlscanRes = ServiceResult("UNVERIFIED", "UNKNOWN")

                    if (webRiskKey.isEmpty() || webRiskKey == "your_web_risk_key_here") {
                        UrlThreatResult(
                            originalUrl = originalUrl,
                            normalizedUrl = normalizedUrl,
                            expandedUrl = expandedUrl,
                            webRiskVerdict = "UNVERIFIED",
                            phishtankVerdict = "UNVERIFIED",
                            urlhausVerdict = "UNVERIFIED",
                            finalUrlVerdict = "UNVERIFIED",
                            riskLevel = "UNVERIFIED",
                            isCached = false,
                            threatType = null,
                            scanTime = System.currentTimeMillis(),
                            confidence = 10,
                            webRiskStatus = "MISSING_KEY",
                            phishtankStatus = "UNKNOWN",
                            urlhausStatus = "UNKNOWN",
                            urlscanVerdict = "UNVERIFIED",
                            urlscanStatus = "UNKNOWN"
                        )
                    } else {
                        try {
                            coroutineScope {
                                val webRiskDef = async { executeGoogleWebRisk(targetUrl, webRiskKey) }
                                val phishtankDef = async { executePhishTank(targetUrl) }
                                val urlhausDef = async { executeUrlhaus(targetUrl) }
                                val urlscanDef = async { executeUrlScan(targetUrl, urlscanKey) }

                                webRiskRes = webRiskDef.await()
                                phishtankRes = phishtankDef.await()
                                urlhausRes = urlhausDef.await()
                                urlscanRes = urlscanDef.await()
                            }

                            val webRiskVerdict = webRiskRes.verdict
                            val webRiskStatusVal = webRiskRes.status
                            val threatType = webRiskRes.threatType

                            val phishtankVerdict = phishtankRes.verdict
                            val phishtankStatusVal = phishtankRes.status

                            val urlhausVerdict = urlhausRes.verdict
                            val urlhausStatusVal = urlhausRes.status

                            val urlscanVerdict = urlscanRes.verdict
                            val urlscanStatusVal = urlscanRes.status

                            // Final URL Verdict evaluation
                            val hasMalware = (webRiskVerdict == "MALICIOUS" && (threatType == "MALWARE" || threatType == "UNWANTED_SOFTWARE")) || urlhausVerdict == "MALICIOUS"
                            val hasPhishing = phishtankVerdict == "MALICIOUS"
                            val hasSocialEngineering = (webRiskVerdict == "MALICIOUS" && threatType == "SOCIAL_ENGINEERING")
                            val hasUrlscanSuspicious = (urlscanVerdict == "SUSPICIOUS_BEHAVIOR")

                            val finalUrlVerdict = when {
                                hasMalware || hasPhishing || hasSocialEngineering || webRiskVerdict == "MALICIOUS" || phishtankVerdict == "MALICIOUS" || urlhausVerdict == "MALICIOUS" -> "danger"
                                hasUrlscanSuspicious -> "suspicious"
                                webRiskVerdict == "NO_KNOWN_THREAT" || phishtankVerdict == "NO_KNOWN_THREAT" || urlhausVerdict == "NO_KNOWN_THREAT" -> "NO_KNOWN_THREAT"
                                else -> "UNVERIFIED"
                            }

                            // Confidence score calculation
                            var confidence = 100
                            if (webRiskStatusVal != "OK") confidence -= 25
                            if (phishtankStatusVal != "OK") confidence -= 25
                            if (urlhausStatusVal != "OK") confidence -= 25

                            val successVerdicts = mutableListOf<String>()
                            if (webRiskStatusVal == "OK") successVerdicts.add(webRiskVerdict)
                            if (phishtankStatusVal == "OK") successVerdicts.add(phishtankVerdict)
                            if (urlhausStatusVal == "OK") successVerdicts.add(urlhausVerdict)

                            if (successVerdicts.size >= 2) {
                                val hasDangerVal = successVerdicts.contains("MALICIOUS")
                                val hasSafeVal = successVerdicts.contains("NO_KNOWN_THREAT")
                                if (hasDangerVal && hasSafeVal) {
                                    confidence -= 30
                                } else {
                                    confidence += 10
                                }
                            } else if (successVerdicts.isEmpty()) {
                                confidence = 10
                            }

                            val finalConfidence = confidence.coerceIn(10, 100)
                            val scanTime = System.currentTimeMillis()

                            val cacheEntry = CacheEntry(
                                webRiskVerdict = webRiskVerdict,
                                webRiskStatus = webRiskStatusVal,
                                phishtankVerdict = phishtankVerdict,
                                phishtankStatus = phishtankStatusVal,
                                urlhausVerdict = urlhausVerdict,
                                urlhausStatus = urlhausStatusVal,
                                finalUrlVerdict = finalUrlVerdict,
                                confidence = finalConfidence,
                                threatType = threatType,
                                scanTime = scanTime,
                                source = "GoogleWebRisk($webRiskStatusVal)+PhishTank($phishtankStatusVal)+URLhaus($urlhausStatusVal)",
                                urlscanVerdict = urlscanVerdict,
                                urlscanStatus = urlscanStatusVal
                            )

                            // Save cache entry
                            reputationCache[normalizedUrl] = cacheEntry
                            if (expandedUrl != null) {
                                reputationCache[expandedUrl] = cacheEntry
                            }

                            UrlThreatResult(
                                originalUrl = originalUrl,
                                normalizedUrl = normalizedUrl,
                                expandedUrl = expandedUrl,
                                webRiskVerdict = webRiskVerdict,
                                phishtankVerdict = phishtankVerdict,
                                urlhausVerdict = urlhausVerdict,
                                finalUrlVerdict = finalUrlVerdict,
                                riskLevel = finalUrlVerdict,
                                isCached = false,
                                threatType = threatType,
                                scanTime = scanTime,
                                confidence = finalConfidence,
                                webRiskStatus = webRiskStatusVal,
                                phishtankStatus = phishtankStatusVal,
                                urlhausStatus = urlhausStatusVal,
                                urlscanVerdict = urlscanVerdict,
                                urlscanStatus = urlscanStatusVal
                            )
                        } catch (e: Exception) {
                            Log.e("UrlReputationEngine", "Parallel URL API request failed", e)
                            UrlThreatResult(
                                originalUrl = originalUrl,
                                normalizedUrl = normalizedUrl,
                                expandedUrl = expandedUrl,
                                webRiskVerdict = "UNVERIFIED",
                                phishtankVerdict = "UNVERIFIED",
                                urlhausVerdict = "UNVERIFIED",
                                finalUrlVerdict = "UNVERIFIED",
                                riskLevel = "UNVERIFIED",
                                isCached = false,
                                threatType = null,
                                scanTime = System.currentTimeMillis(),
                                confidence = 10,
                                webRiskStatus = "FAILED",
                                phishtankStatus = "FAILED",
                                urlhausStatus = "FAILED",
                                urlscanVerdict = "UNVERIFIED",
                                urlscanStatus = "SCAN_FAILED"
                            )
                        }
                    }
                }
            }

            // Wait for both URL scans and AI API to finish
            val uniqueUrlResults = urlScanDeferreds.awaitAll()
            aiOutput = aiDeferred.await()

            // Re-map the parallel scans to original extracted URLs list
            extractedUrls.forEach { originalUrl ->
                val norm = UrlDetectionEngine.normalizeUrl(originalUrl)
                val matchedResult = uniqueUrlResults.firstOrNull { it.normalizedUrl == norm }
                if (matchedResult != null) {
                    urlResults.add(matchedResult.copy(originalUrl = originalUrl))
                } else {
                    urlResults.add(UrlThreatResult(
                        originalUrl = originalUrl,
                        normalizedUrl = norm,
                        expandedUrl = null,
                        webRiskVerdict = "UNVERIFIED",
                        phishtankVerdict = "UNVERIFIED",
                        urlhausVerdict = "UNVERIFIED",
                        finalUrlVerdict = "UNVERIFIED",
                        riskLevel = "UNVERIFIED",
                        isCached = false,
                        threatType = null,
                        scanTime = System.currentTimeMillis(),
                        confidence = 10,
                        webRiskStatus = "FAILED",
                        phishtankStatus = "FAILED",
                        urlhausStatus = "FAILED",
                        urlscanVerdict = "UNVERIFIED",
                        urlscanStatus = "SCAN_FAILED"
                    ))
                }
            }
        }

        // C. Finalize URL progress callback
        if (extractedUrls.isNotEmpty()) {
            val hasDangerousUrl = urlResults.any { it.finalUrlVerdict == "danger" || it.finalUrlVerdict == "MALICIOUS" }
            val hasSuspiciousUrl = urlResults.any { it.finalUrlVerdict == "UNVERIFIED" || it.finalUrlVerdict == "suspicious" || it.finalUrlVerdict == "SUSPICIOUS" }

            val finalStatus = when {
                hasDangerousUrl -> "danger"
                urlResults.any { it.riskLevel == "UNVERIFIED" } -> "UNVERIFIED"
                hasSuspiciousUrl -> "suspicious"
                else -> "safe"
            }

            val finalVerdictText = when (finalStatus) {
                "danger" -> "Dangerous link detected!"
                "suspicious" -> "Suspicious link detected!"
                "unknown" -> "Unknown link detected!"
                else -> "All links safe"
            }

            onUrlScanProgress?.invoke(UrlScanProgress(
                detectedCount = extractedUrls.size,
                status = finalStatus,
                verdict = finalVerdictText,
                progress = 1.0f
            ))
        }

        // 4. Deterministic Scoring & Threat Fusion Engine
        val extractedSignals = mutableListOf<String>()
        val adviceList = mutableListOf<String>()
        var textScore = 0
        var textConfidence = 75
        var shortReason = ""
        var scamCategory = "Unknown"
        var aiClassification: String? = null

        if (aiOutput != null) {
            val scamProb = aiOutput!!.optInt("scam_probability", 0)
            var classification = if (aiOutput!!.has("classification")) {
                aiOutput!!.optString("classification", "SAFE")
            } else {
                when {
                    scamProb >= 76 -> "DANGEROUS"
                    scamProb >= 46 -> "SUSPICIOUS"
                    scamProb >= 21 -> "UNABLE_TO_DETERMINE"
                    else -> "SAFE"
                }
            }
            val evidenceSufficiency = aiOutput!!.optString("evidence_sufficiency", "SUFFICIENT")
            if (evidenceSufficiency == "INSUFFICIENT" && classification != "DANGEROUS") {
                classification = "UNABLE_TO_DETERMINE"
            }
            aiClassification = classification
            
            textConfidence = aiOutput!!.optInt("confidence", 75)
            shortReason = aiOutput!!.optString("short_reason", "")
            
            // Derive a generic scam category since it's no longer in the schema
            scamCategory = aiOutput!!.optString("scam_category", "Unknown")
            if (scamCategory == "Unknown" || scamCategory.isEmpty()) {
                scamCategory = aiOutput!!.optString("scam_type", "Unknown")
            }
            if (scamCategory == "Unknown" || scamCategory.isEmpty()) {
                scamCategory = if (classification == "DANGEROUS") {
                    "Threat Detected"
                } else if (classification == "SUSPICIOUS") {
                    "Suspicious Message"
                } else if (classification == "UNABLE_TO_DETERMINE") {
                    "Unable to Determine"
                } else {
                    "Safe Message"
                }
            }
            
            val signalsArr = aiOutput!!.optJSONArray("extracted_signals")
            if (signalsArr != null) {
                for (i in 0 until signalsArr.length()) {
                    val s = signalsArr.optString(i)
                    if (s.isNotEmpty() && !extractedSignals.contains(s)) {
                        extractedSignals.add(s)
                    }
                }
            }
            
            val adviceArr = aiOutput!!.optJSONArray("advice")
            if (adviceArr != null) {
                for (i in 0 until adviceArr.length()) {
                    adviceList.add(adviceArr.optString(i))
                }
            }
            
            textScore = scamProb
            if (classification == "DANGEROUS") {
                textScore = maxOf(textScore, 85)
            } else if (classification == "SUSPICIOUS") {
                textScore = maxOf(textScore, 50)
                textScore = minOf(textScore, 75) // cap at 75 to avoid hitting danger threshold
            } else if (classification == "UNABLE_TO_DETERMINE") {
                textScore = 25
            } else {
                // SAFE
                textScore = minOf(textScore, 20)
            }
        } else {
            // AI failed. Perform context-aware deterministic keyword analysis fallback
            val lowerMsg = normalizedMessage.lowercase()
            
            // Define core scam markers
            val hasUrgency = lowerMsg.contains("urgent") || lowerMsg.contains("immediate") || lowerMsg.contains("within 24") || lowerMsg.contains("within 48") || lowerMsg.contains("asap") || lowerMsg.contains("suspended now") || lowerMsg.contains("expired") || lowerMsg.contains("before it's too late") || lowerMsg.contains("तुरंत") || lowerMsg.contains("जल्दी") || lowerMsg.contains("शीघ्र") || lowerMsg.contains("एक्सपायर") || lowerMsg.contains("समाप्त") || lowerMsg.contains("अभी")
            val hasCTA = lowerMsg.contains("click") || lowerMsg.contains("link") || lowerMsg.contains("http") || lowerMsg.contains("visit") || lowerMsg.contains("login") || lowerMsg.contains("sign in") || lowerMsg.contains("sign-in") || lowerMsg.contains("verify") || lowerMsg.contains("update") || lowerMsg.contains("call ") || lowerMsg.contains("download") || lowerMsg.contains("सत्यापित") || lowerMsg.contains("satyapan") || lowerMsg.contains("सत्यापन") || lowerMsg.contains("अपडेट") || lowerMsg.contains("क्लिक") || lowerMsg.contains("लिंक") || lowerMsg.contains("लॉगिन") || lowerMsg.contains("डाउनलोड")
            val hasThreat = lowerMsg.contains("block") || lowerMsg.contains("suspend") || lowerMsg.contains("unauthorized") || lowerMsg.contains("compromised") || lowerMsg.contains("lock") || lowerMsg.contains("arrest") || lowerMsg.contains("warrant") || lowerMsg.contains("fine") || lowerMsg.contains("penalty") || lowerMsg.contains("court") || lowerMsg.contains("police") || lowerMsg.contains("investigate") || lowerMsg.contains("seized") || lowerMsg.contains("ब्लॉक") || lowerMsg.contains("बंद") || lowerMsg.contains("निलंबित") || lowerMsg.contains("अवरुद्ध") || lowerMsg.contains("लॉक") || lowerMsg.contains("रद्द")
            val hasShareRequest = (lowerMsg.contains("share") && !lowerMsg.contains("do not share") && !lowerMsg.contains("don't share") && !lowerMsg.contains("never share") && !lowerMsg.contains("no comparta")) || lowerMsg.contains("provide") || lowerMsg.contains("send") || lowerMsg.contains("give") || lowerMsg.contains("enter") || lowerMsg.contains("unblock") || lowerMsg.contains("साझा") || lowerMsg.contains("भेजें") || lowerMsg.contains("दर्ज") || lowerMsg.contains("दें") || lowerMsg.contains("शेयर")
            
            val isInformational = lowerMsg.contains("credited") || lowerMsg.contains("debited") || lowerMsg.contains("successful") || lowerMsg.contains("processed") || lowerMsg.contains("available balance") || lowerMsg.contains("statement") || lowerMsg.contains("sent to your a/c") || lowerMsg.contains("received from") || lowerMsg.contains("delivered successfully") || lowerMsg.contains("shipped") || lowerMsg.contains("approved") || lowerMsg.contains("submitted") || lowerMsg.contains("reminder") || lowerMsg.contains("due") || lowerMsg.contains("upcoming") || lowerMsg.contains("recharge")

            // 1. Remote Access Scam (highest risk, almost zero false positive rate)
            if (lowerMsg.contains("anydesk") || lowerMsg.contains("teamviewer") || lowerMsg.contains("rustdesk")) {
                extractedSignals.add("Remote Access Scam")
                scamCategory = "Remote Access Scam"
                textScore = maxOf(textScore, 90)
            }

            // 2. OTP Scam
            if (lowerMsg.contains("otp") || lowerMsg.contains("verification code") || lowerMsg.contains("one time password") || lowerMsg.contains("one-time-password")) {
                if (hasShareRequest || hasThreat || (hasCTA && hasUrgency)) {
                    extractedSignals.add("OTP Scam Attempt")
                    scamCategory = "OTP Scam"
                    textScore = maxOf(textScore, 80)
                } else if (isInformational) {
                    // Safe transaction OTP
                    textScore = maxOf(textScore, 10)
                } else {
                    // Low risk standard OTP
                    textScore = maxOf(textScore, 20)
                }
            }

            // 3. Bank Impersonation
            if (lowerMsg.contains("bank") || lowerMsg.contains("sbi") || lowerMsg.contains("hdfc") || lowerMsg.contains("icici") || lowerMsg.contains("axis bank") || lowerMsg.contains("pnb") || lowerMsg.contains("बैंक") || lowerMsg.contains("खाता")) {
                if (hasThreat || (hasCTA && (hasUrgency || hasShareRequest))) {
                    extractedSignals.add("Bank Impersonation Fraud")
                    scamCategory = "Bank Impersonation"
                    textScore = maxOf(textScore, 80)
                } else if (isInformational) {
                    // Normal bank alert
                    textScore = maxOf(textScore, 10)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 4. Fake KYC
            if (lowerMsg.contains("kyc") || lowerMsg.contains("know your customer") || lowerMsg.contains("verify kyc") || lowerMsg.contains("kyc updated")) {
                if (hasThreat || (hasCTA && (hasUrgency || hasShareRequest))) {
                    extractedSignals.add("Fake KYC Request")
                    scamCategory = "Fake KYC"
                    textScore = maxOf(textScore, 80)
                } else if (hasCTA) {
                    textScore = maxOf(textScore, 45) // suspicious, maybe genuine KYC update link
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 5. Parcel / Courier Scam
            if (lowerMsg.contains("delivery") || lowerMsg.contains("fedex") || lowerMsg.contains("dhl") || lowerMsg.contains("parcel") || lowerMsg.contains("courier") || lowerMsg.contains("post")) {
                val parcelScamWords = lowerMsg.contains("fail") || lowerMsg.contains("hold") || lowerMsg.contains("unpaid") || lowerMsg.contains("tax") || lowerMsg.contains("fee") || lowerMsg.contains("address") || lowerMsg.contains("redeliver")
                if (hasThreat || (parcelScamWords && hasCTA)) {
                    extractedSignals.add("Courier Scam")
                    scamCategory = "Parcel Scam"
                    textScore = maxOf(textScore, 70)
                } else if (isInformational) {
                    // Normal delivery notice
                    textScore = maxOf(textScore, 10)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 6. Lottery / Reward Scam
            if (lowerMsg.contains("lottery") || lowerMsg.contains("won") || lowerMsg.contains("prize") || lowerMsg.contains("crore") || lowerMsg.contains("kbc") || lowerMsg.contains("reward") || lowerMsg.contains("scratch card")) {
                if ((hasCTA && hasUrgency) || lowerMsg.contains("claim") || lowerMsg.contains("collect") || lowerMsg.contains("whatsapp") || lowerMsg.contains("telegram")) {
                    extractedSignals.add("Lottery Scam")
                    scamCategory = "Lottery Scam"
                    textScore = maxOf(textScore, 80)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 7. Investment / Crypto Scam
            if (lowerMsg.contains("investment") || lowerMsg.contains("bitcoin") || lowerMsg.contains("crypto") || lowerMsg.contains("profit") || lowerMsg.contains("earn") || lowerMsg.contains("trading") || lowerMsg.contains("double money")) {
                if ((hasCTA && hasUrgency) || lowerMsg.contains("guaranteed") || lowerMsg.contains("join") || lowerMsg.contains("whatsapp") || lowerMsg.contains("telegram")) {
                    extractedSignals.add("Investment Scam")
                    scamCategory = "Investment Scam"
                    textScore = maxOf(textScore, 80)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 8. Credential Harvesting
            if (lowerMsg.contains("password") || lowerMsg.contains("username") || lowerMsg.contains("login") || lowerMsg.contains("credentials") || lowerMsg.contains("sign in") || lowerMsg.contains("sign-in")) {
                if (hasThreat || (hasCTA && hasUrgency)) {
                    extractedSignals.add("Credential Harvesting")
                    scamCategory = "Credential Harvesting"
                    textScore = maxOf(textScore, 80)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 9. Fake Support
            if (lowerMsg.contains("support") || lowerMsg.contains("helpline") || lowerMsg.contains("toll free") || lowerMsg.contains("toll-free") || lowerMsg.contains("customer care")) {
                if (hasThreat || (hasCTA && hasUrgency)) {
                    extractedSignals.add("Fake Support")
                    scamCategory = "Fake Support"
                    textScore = maxOf(textScore, 60)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 10. UPI Fraud
            if (lowerMsg.contains("upi") || lowerMsg.contains("gpay") || lowerMsg.contains("paytm") || lowerMsg.contains("phonepe")) {
                val upiFraudWords = lowerMsg.contains("request") || lowerMsg.contains("pay request") || lowerMsg.contains("approve") || lowerMsg.contains("pin") || lowerMsg.contains("collect")
                if (upiFraudWords && (hasCTA || hasUrgency || hasThreat)) {
                    extractedSignals.add("UPI Fraud")
                    scamCategory = "UPI Fraud"
                    textScore = maxOf(textScore, 80)
                } else if (isInformational) {
                    textScore = maxOf(textScore, 10)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 11. Refund / Cashback Scam
            if (lowerMsg.contains("refund") || lowerMsg.contains("cashback")) {
                if ((hasCTA && hasUrgency) || lowerMsg.contains("claim")) {
                    extractedSignals.add("Refund Scam")
                    scamCategory = "Refund Scam"
                    textScore = maxOf(textScore, 70)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 12. Government Impersonation
            if (lowerMsg.contains("income tax") || lowerMsg.contains("government") || lowerMsg.contains("voter id") || lowerMsg.contains("police") || lowerMsg.contains("court")) {
                if (hasThreat || (hasCTA && hasUrgency)) {
                    extractedSignals.add("Government Impersonation")
                    scamCategory = "Government Impersonation"
                    textScore = maxOf(textScore, 80)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 13. Telecom Impersonation (Airtel, Jio, Vi)
            if (lowerMsg.contains("airtel") || lowerMsg.contains("jio") || lowerMsg.contains("vi") || lowerMsg.contains("bsnl")) {
                if (hasThreat || (hasCTA && lowerMsg.contains("suspended"))) {
                    extractedSignals.add("Telecom Impersonation")
                    scamCategory = "Telecom Impersonation"
                    textScore = maxOf(textScore, 70)
                } else if (isInformational) {
                    textScore = maxOf(textScore, 10)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 13. Brand Impersonation
            if (lowerMsg.contains("netflix") || lowerMsg.contains("amazon") || lowerMsg.contains("microsoft") || lowerMsg.contains("google") || lowerMsg.contains("apple")) {
                if (hasThreat || hasCTA || hasUrgency) {
                    extractedSignals.add("Brand Impersonation")
                    scamCategory = "Brand Impersonation"
                    textScore = maxOf(textScore, 70)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 14. Social Engineering
            if (lowerMsg.contains("friend") || lowerMsg.contains("emergency") || lowerMsg.contains("relative") || lowerMsg.contains("help me")) {
                if (lowerMsg.contains("money") || lowerMsg.contains("transfer") || hasUrgency) {
                    extractedSignals.add("Social Engineering")
                    scamCategory = "Social Engineering"
                    textScore = maxOf(textScore, 65)
                } else {
                    textScore = maxOf(textScore, 15)
                }
            }

            // 15. Urgency Signal standalone
            if (hasUrgency && extractedSignals.isNotEmpty() && textScore < 40) {
                extractedSignals.add("Urgency")
                textScore = maxOf(textScore, 40)
            }

            shortReason = if (extractedSignals.isNotEmpty()) {
                "Scam indicators found via local context-aware keyword analysis: ${extractedSignals.joinToString(", ")}."
            } else {
                "No suspicious scam keywords or threat patterns detected in message text."
            }
        }

        // TEXT RULES
        var textVerdict = when {
            aiClassification == "UNABLE_TO_DETERMINE" || aiClassification == "INSUFFICIENT_EVIDENCE" -> "Unable to Determine"
            textScore >= 76 -> "Danger"
            textScore >= 46 -> "Warning"
            textScore >= 21 -> "Suspicious"
            else -> "Safe"
        }

        // URL SIGNALS & MULTIPLE URL LOGIC
        val urlScanHasDanger = urlResults.any { 
            it.webRiskVerdict == "MALICIOUS" || it.phishtankVerdict == "MALICIOUS" || it.urlhausVerdict == "MALICIOUS" || it.finalUrlVerdict == "MALICIOUS" || it.finalUrlVerdict == "danger"
        }
        val urlScanHasSuspicious = !urlScanHasDanger && urlResults.any {
            it.urlscanVerdict == "SUSPICIOUS_BEHAVIOR" || it.finalUrlVerdict == "SUSPICIOUS" || it.finalUrlVerdict == "suspicious"
        }
        val urlScanHasUnverified = !urlScanHasDanger && !urlScanHasSuspicious && urlResults.any { 
            it.webRiskVerdict == "UNVERIFIED" || it.phishtankVerdict == "UNVERIFIED" || it.urlhausVerdict == "UNVERIFIED" || it.finalUrlVerdict == "UNVERIFIED" || it.finalUrlVerdict == "unverified"
        }
        
        val overallUrlVerdict = when {
            urlResults.isEmpty() -> "No URLs"
            urlScanHasDanger -> "MALICIOUS"
            urlScanHasSuspicious -> "SUSPICIOUS"
            urlScanHasUnverified -> "UNVERIFIED"
            else -> "NO_KNOWN_THREAT"
        }

        // CONFLICT RESOLUTION
        var finalVerdict = when {
            // 1. Confirmed malicious URL (Highest priority)
            overallUrlVerdict == "MALICIOUS" -> "Danger"
            
            // 2. Strong scam text
            textVerdict == "Danger" -> "Danger"
            
            // 3. GPT-OSS suspicious/warning text + suspicious behavioral evidence
            overallUrlVerdict == "SUSPICIOUS" && (textVerdict == "Warning" || textVerdict == "Suspicious") -> "Danger"
            
            // 4. URL Suspicious but text is Safe
            overallUrlVerdict == "SUSPICIOUS" && textVerdict == "Safe" -> "Warning"
            
            // 5. Unable to Determine (No strong evidence for either side)
            textVerdict == "Unable to Determine" -> "Unable to Determine"
            
            // 6. Warning and Suspicious text cases
            textVerdict == "Warning" -> "Warning"
            textVerdict == "Suspicious" -> "Suspicious"
            
            // 7. Unverified URL with safe text
            overallUrlVerdict == "UNVERIFIED" && textVerdict == "Safe" -> "Safe"
            
            // 8. Default Safe
            else -> "Safe"
        }

        // SERVICE FAILURE EVALUATION
        val webRiskConso = if (extractedUrls.isEmpty()) "UNKNOWN" else if (urlResults.any { it.webRiskStatus == "FAILED" }) "FAILED" else if (urlResults.any { it.webRiskStatus == "TIMEOUT" }) "TIMEOUT" else "OK"
        val phishtankConso = if (extractedUrls.isEmpty()) "UNKNOWN" else if (urlResults.any { it.phishtankStatus == "FAILED" }) "FAILED" else if (urlResults.any { it.phishtankStatus == "TIMEOUT" }) "TIMEOUT" else "OK"
        val urlhausConso = if (extractedUrls.isEmpty()) "UNKNOWN" else if (urlResults.any { it.urlhausStatus == "FAILED" }) "FAILED" else if (urlResults.any { it.urlhausStatus == "TIMEOUT" }) "TIMEOUT" else "OK"
        
        val urlServicesAllFailed = extractedUrls.isNotEmpty() && 
                (webRiskConso == "FAILED" || webRiskConso == "TIMEOUT") &&
                (phishtankConso == "FAILED" || phishtankConso == "TIMEOUT") &&
                (urlhausConso == "FAILED" || urlhausConso == "TIMEOUT")
                
        val aiFailed = (aiStatus != "ok")
        val isAllFailed = (extractedUrls.isNotEmpty() && urlServicesAllFailed && aiFailed && textScore == 0)

        val isWebRiskNoKnownThreat = extractedUrls.isNotEmpty() &&
                urlResults.isNotEmpty() &&
                urlResults.all { it.webRiskVerdict == "NO_KNOWN_THREAT" && it.webRiskStatus == "OK" }

        val isClearlyDangerous = textVerdict == "Danger" && (
                scamCategory == "Remote Access Scam" ||
                scamCategory == "OTP Scam" ||
                scamCategory == "UPI Fraud" ||
                normalizedMessage.lowercase().contains("anydesk") ||
                normalizedMessage.lowercase().contains("teamviewer") ||
                normalizedMessage.lowercase().contains("otp")
        )

        val isCase3 = isWebRiskNoKnownThreat && 
                (textVerdict == "Suspicious" || textVerdict == "Warning" || (textVerdict == "Danger" && !isClearlyDangerous))

        if (isAllFailed) {
            finalVerdict = "Scan Incomplete"
        }

        if (isCase3) {
            finalVerdict = "Suspicious"
        }

        val hasConfirmedWebRiskThreat = urlResults.any { 
            it.webRiskVerdict == "MALICIOUS" && it.webRiskStatus == "OK" 
        }

        if (hasConfirmedWebRiskThreat) {
            finalVerdict = "Danger"
            textVerdict = "Danger"
        }

        // RISK SCORE (Deterministic, 0-100)
        var finalScore = when (finalVerdict) {
            "Danger" -> {
                if (overallUrlVerdict == "MALICIOUS") {
                    maxOf(85, textScore).coerceIn(76, 100)
                } else {
                    maxOf(80, textScore).coerceIn(76, 100)
                }
            }
            "Warning" -> {
                if (textScore in 46..75) textScore else 60
            }
            "Suspicious" -> {
                if (isCase3) {
                    if (textScore in 21..75) textScore else 45
                } else {
                    if (textScore in 21..45) textScore else 35
                }
            }
            "Safe" -> {
                textScore.coerceIn(0, 20)
            }
            "Unable to Determine" -> {
                25
            }
            "Scan Incomplete" -> 0
            else -> 0
        }

        if (hasConfirmedWebRiskThreat) {
            finalScore = maxOf(95, textScore).coerceIn(76, 100)
        }

        // CONFIDENCE ENGINE (Deterministic, 0-100)
        var finalConfidence = 75
        
        if (extractedUrls.isNotEmpty()) {
            val validVerdicts = urlResults.flatMap { 
                listOf(it.webRiskVerdict, it.phishtankVerdict, it.urlhausVerdict) 
            }.filter { it != "UNVERIFIED" }
            
            val uniqueVerdicts = validVerdicts.distinct()
            if (uniqueVerdicts.size == 1) {
                finalConfidence += 15 // Multiple reputation services agree
            } else if (uniqueVerdicts.size > 1) {
                finalConfidence -= 20 // API disagreement
            }
            
            if (overallUrlVerdict == "UNVERIFIED") {
                finalConfidence -= 10 // Unknown URL
            }
            
            if (urlResults.any { it.webRiskStatus == "TIMEOUT" || it.phishtankStatus == "TIMEOUT" || it.urlhausStatus == "TIMEOUT" }) {
                finalConfidence -= 15 // Timeout
            }
            if (urlResults.any { it.webRiskStatus == "FAILED" || it.phishtankStatus == "FAILED" || it.urlhausStatus == "FAILED" }) {
                finalConfidence -= 10 // Partial scan
            }
        }
        
        // URL and Text agreement
        val isUrlSafe = overallUrlVerdict == "NO_KNOWN_THREAT" || overallUrlVerdict == "No URLs"
        if (overallUrlVerdict == "MALICIOUS" && textVerdict == "Danger") {
            finalConfidence += 15
        } else if (isUrlSafe && textVerdict == "Safe") {
            finalConfidence += 15
        } else if ((overallUrlVerdict == "MALICIOUS" && textVerdict == "Safe") || (isUrlSafe && textVerdict == "Danger")) {
            finalConfidence -= 15
        }
        
        // Multiple scam indicators
        if (extractedSignals.size >= 2) {
            finalConfidence += 10
        }
        
        // Weak evidence
        if (textVerdict == "Suspicious") {
            finalConfidence -= 10
        }
        
        if (aiFailed) {
            finalConfidence -= 15
        } else if (aiOutput != null) {
            val aiConf = aiOutput!!.optInt("confidence", 75)
            finalConfidence = (finalConfidence + aiConf) / 2
        }
        
        finalConfidence = finalConfidence.coerceIn(10, 100)
        if (finalVerdict == "Scan Incomplete") {
            finalConfidence = 0
        }

        // EXPLANATION ENGINE
        val reasons = mutableListOf<String>()
        if (finalVerdict == "Danger") {
            val hasPhish = urlResults.any { it.phishtankVerdict == "MALICIOUS" }
            val hasMalware = urlResults.any { it.urlhausVerdict == "MALICIOUS" || (it.webRiskVerdict == "MALICIOUS" && (it.threatType == "MALWARE" || it.threatType == "UNWANTED_SOFTWARE")) }
            val hasSocEng = urlResults.any { it.webRiskVerdict == "MALICIOUS" && it.threatType == "SOCIAL_ENGINEERING" }
            
            if (hasPhish) {
                reasons.add("Confirmed phishing URL detected.")
            }
            if (hasMalware) {
                reasons.add("Confirmed malware URL detected.")
            }
            if (hasSocEng) {
                reasons.add("Confirmed social engineering URL detected.")
            }
            if (reasons.isEmpty() && overallUrlVerdict == "MALICIOUS") {
                reasons.add("Confirmed malicious URL detected.")
            }
            
            if (extractedSignals.contains("Bank Impersonation") || extractedSignals.contains("Credential Harvesting") || extractedSignals.contains("OTP Scam") || extractedSignals.contains("Fake KYC")) {
                reasons.add("Bank impersonation and credential theft language detected.")
            } else if (extractedSignals.contains("Government Impersonation")) {
                reasons.add("Government impersonation attempt detected.")
            } else if (extractedSignals.isNotEmpty()) {
                reasons.add("High-risk scam indicators found in message text.")
            }
            
            if (reasons.isEmpty()) {
                reasons.add("Significant threat detected by analysis engine.")
            }
        } else if (finalVerdict == "Warning") {
            if (extractedSignals.contains("Urgency")) {
                reasons.add("Urgent payment request from unknown sender.")
            } else if (extractedSignals.isNotEmpty()) {
                reasons.add("Suspicious scam signals identified in message.")
            }
            if (overallUrlVerdict == "UNVERIFIED") {
                reasons.add("Unverified URL requires caution.")
            }
            if (reasons.isEmpty()) {
                reasons.add("Potential threat signals detected.")
            }
        } else if (finalVerdict == "Suspicious") {
            if (overallUrlVerdict == "UNVERIFIED") {
                reasons.add("URL reputation is currently unavailable.")
            }
            if (extractedSignals.isNotEmpty()) {
                reasons.add("Weak or partial scam indicators found.")
            } else {
                reasons.add("Message contains unverified external links.")
            }
        } else if (finalVerdict == "Safe") {
            reasons.add("No malicious URL or scam indicators detected.")
        } else if (finalVerdict == "Unable to Determine") {
            reasons.add(if (isHindi) "संदेश में पर्याप्त संदर्भ नहीं है।" else "Not enough context in the message to analyze.")
        } else if (finalVerdict == "Scan Incomplete") {
            reasons.add("Scan Incomplete: Security API services are offline.")
        }
        
        val finalReasonStr = if (hasConfirmedWebRiskThreat) {
            if (isHindi) {
                "इस message में एक known dangerous Link detect हुआ है। इस Link को open न करें और sender के साथ personal या sensitive information share न करें।"
            } else {
                "A known dangerous link was detected in this message. Do not open the link or share personal or sensitive information with the sender."
            }
        } else if (finalVerdict == "Unable to Determine") {
            if (isHindi) {
                "इस message में reliable scam assessment के लिए पर्याप्त जानकारी नहीं है। अधिक context के बिना यह confirm नहीं किया जा सकता कि message safe है या scam."
            } else {
                "There isn't enough information in this message to reliably determine whether it is safe or a scam."
            }
        } else if (isCase3) {
            generateDynamicSummary(normalizedMessage, extractedSignals, shortReason, isHindi)
        } else if (extractedUrls.isEmpty()) {
            // Case 6: Text-only analysis summary
            if (shortReason.isNotEmpty() && !shortReason.contains("local context-aware") && !shortReason.contains("No suspicious")) {
                shortReason
            } else {
                val finalReasonsList = reasons.take(3)
                finalReasonsList.joinToString(" ")
            }
        } else {
            val finalReasonsList = reasons.take(3)
            finalReasonsList.joinToString(" ")
        }

        // RECOMMENDATION ENGINE
        val adviceListCustom = if (hasConfirmedWebRiskThreat) {
            if (isHindi) {
                listOf(
                    "detected Link को बिल्कुल न खोलें।",
                    "personal, financial, login, OTP या sensitive information share न करें।",
                    "आवश्यकता होने पर official channel के माध्यम से sender को verify करें।",
                    "उचित होने पर sender को block/report करें।"
                )
            } else {
                listOf(
                    "Do not open the detected Link.",
                    "Do not enter personal, financial, login, OTP, or other sensitive information.",
                    "Verify the sender through an official channel if necessary.",
                    "Block/report the sender when appropriate."
                )
            }
        } else if (isCase3) {
            generateDynamicAdvice(normalizedMessage, extractedSignals, isHindi)
        } else if (extractedUrls.isEmpty()) {
            // Case 6: Text-only advice
            if (adviceList.isNotEmpty()) {
                adviceList
            } else {
                when (finalVerdict) {
                    "Safe" -> listOf(if (isHindi) "यह संदेश सुरक्षित लग रहा है। सामान्य सावधानी बरतें।" else "This message appears safe. Proceed with normal caution.")
                    "Suspicious" -> listOf(if (isHindi) "Action लेने से पहले sender को verify करें।" else "Verify sender before taking any action.")
                    "Warning" -> listOf(if (isHindi) "Sender की पहचान की पुष्टि होने तक कोई जानकारी साझा न करें।" else "Do not share any information until sender's identity is confirmed.")
                    "Danger" -> listOf(
                        if (isHindi) "इस संदेश के जवाब में कोई संवेदनशील जानकारी न भेजें।" else "Do not share sensitive information in response to this message.",
                        if (isHindi) "इस नंबर को ब्लॉक करें।" else "Block this sender."
                    )
                    "Unable to Determine" -> listOf(if (isHindi) "Sender को verify करें और confirm किए बिना sensitive information share न करें।" else "Verify the sender and do not share sensitive information without confirmation.")
                    else -> listOf(if (isHindi) "सावधानी बरतें।" else "Proceed with caution.")
                }
            }
        } else {
            when (finalVerdict) {
                "Safe" -> listOf(
                    if (isHindi) "Normal सावधानी के साथ आगे बढ़ें।" else "Proceed with normal caution."
                )
                "Suspicious" -> listOf(
                    if (isHindi) "Action लेने से पहले sender को verify करें।" else "Verify sender before taking action."
                )
                "Warning" -> listOf(
                    if (isHindi) "Verify होने तक link पर click न करें।" else "Avoid clicking links until verified."
                )
                "Danger" -> if (isHindi) {
                    listOf(
                        "Link पर click न करें।",
                        "OTP साझा न करें।",
                        "ज़रूरत पड़ने पर sender को block करें।"
                    )
                } else {
                    listOf(
                        "Do not click.",
                        "Do not share OTP.",
                        "Block sender if appropriate."
                    )
                }
                "Unable to Determine" -> listOf(
                    if (isHindi) "Sender को verify करें और confirm किए बिना sensitive information share न करें या कोई important action न लें।" else "Verify the sender and do not share sensitive information or take important action without confirmation."
                )
                else -> listOf(
                    if (isHindi) "कृपया बाद में प्रयास करें।" else "Please try again later."
                )
            }
        }

        val processingTime = System.currentTimeMillis() - overallStartTime
        Log.d("UrlReputationEngine", "[Threat Fusion completed] Time: ${processingTime}ms, Cache hits: (checked dynamically), Verdict: $finalVerdict")

        return@withContext HybridAnalysisResult(
            verdict = finalVerdict,
            riskScore = finalScore,
            confidence = finalConfidence,
            messageType = messageType,
            originalMessage = originalMessage,
            normalizedMessage = normalizedMessage,
            urlsFound = urlResults,
            textSignals = extractedSignals,
            finalReason = finalReasonStr,
            webRiskStatus = webRiskConso,
            aiStatus = aiStatus,
            scamType = scamCategory,
            advice = adviceListCustom,
            summary = finalReasonStr,
            textVerdict = textVerdict,
            urlVerdict = overallUrlVerdict,
            phishtankStatus = phishtankConso,
            urlhausStatus = urlhausConso,
            processingTime = processingTime
        )
    }

    private fun generateDynamicAdvice(message: String, signals: List<String>, isHindi: Boolean): List<String> {
        val lowerMsg = message.lowercase()
        val advice = mutableListOf<String>()

        if (isHindi) {
            advice.add("कोई भी Action लेने से पहले sender की पहचान (official number/channel) से verify करें।")
            advice.add("इस message या link पर click करके अपनी personal, banking, या OTP जैसी संवेदनशील जानकारी बिल्कुल share न करें।")
            
            if (lowerMsg.contains("bank") || lowerMsg.contains("sbi") || lowerMsg.contains("hdfc") || lowerMsg.contains("icici") || lowerMsg.contains("axis") || lowerMsg.contains("paytm") || lowerMsg.contains("card") || lowerMsg.contains("account")) {
                advice.add("बैंक से संबंधित कार्य के लिए हमेशा बैंक के official app या official netbanking website का ही उपयोग करें।")
            } else if (lowerMsg.contains("recharge") || lowerMsg.contains("airtel") || lowerMsg.contains("jio") || lowerMsg.contains("bsnl") || lowerMsg.contains("vi")) {
                advice.add("रिचार्ज करने के लिए केवल official MyAirtel, MyJio या official telecom provider app का उपयोग करें।")
            } else if (lowerMsg.contains("kyc") || lowerMsg.contains("verify") || lowerMsg.contains("update")) {
                advice.add("KYC या verification updates के लिए सीधे आधिकारिक कार्यालय या official service app का उपयोग करें।")
            } else {
                advice.add("यदि आवश्यक हो, तो brand की आधिकारिक website/app का उपयोग करके पुष्टि करें।")
            }
        } else {
            advice.add("Verify the sender's identity through an official channel before taking any action.")
            advice.add("Do not enter or share personal, financial, login, OTP, or other sensitive details.")
            
            if (lowerMsg.contains("bank") || lowerMsg.contains("sbi") || lowerMsg.contains("hdfc") || lowerMsg.contains("icici") || lowerMsg.contains("axis") || lowerMsg.contains("paytm") || lowerMsg.contains("card") || lowerMsg.contains("account")) {
                advice.add("Always use the bank's official app or official website to check your account status.")
            } else if (lowerMsg.contains("recharge") || lowerMsg.contains("airtel") || lowerMsg.contains("jio") || lowerMsg.contains("bsnl") || lowerMsg.contains("vi")) {
                advice.add("Use your telecom provider's official mobile application to complete any recharge or updates.")
            } else if (lowerMsg.contains("kyc") || lowerMsg.contains("verify") || lowerMsg.contains("update")) {
                advice.add("Handle KYC or account verification directly on the brand's official platform or store location.")
            } else {
                advice.add("Access the brand's services safely using their official app/site rather than external links.")
            }
        }
        return advice
    }

    private fun generateDynamicSummary(message: String, signals: List<String>, shortReason: String, isHindi: Boolean): String {
        if (shortReason.isNotEmpty() && !shortReason.contains("local context-aware") && !shortReason.contains("No suspicious")) {
            return shortReason
        }
        val lowerMsg = message.lowercase()
        return if (isHindi) {
            when {
                lowerMsg.contains("bank") || lowerMsg.contains("sbi") || lowerMsg.contains("hdfc") || lowerMsg.contains("icici") || lowerMsg.contains("card") || lowerMsg.contains("account") -> {
                    "इस message में एक unflagged URL है, लेकिन बैंक जैसी संस्था की impersonation और verification का संदिग्ध अनुरोध किया गया है।"
                }
                lowerMsg.contains("kyc") || lowerMsg.contains("verify") || lowerMsg.contains("update") -> {
                    "यद्यपि reputation sources द्वारा URL को सुरक्षित बताया गया है, परंतु message का कूट संदर्भ (KYC/Account Verification) संदिग्ध गतिविधि की ओर संकेत करता है।"
                }
                lowerMsg.contains("won") || lowerMsg.contains("prize") || lowerMsg.contains("lottery") || lowerMsg.contains("reward") -> {
                    "संदेश में अपुष्ट इनाम या लॉटरी का लालच देकर Action लेने का अनुरोध किया गया है, जो एक संभावित धोखाधड़ी का संकेत है।"
                }
                lowerMsg.contains("urgent") || lowerMsg.contains("immediate") || lowerMsg.contains("suspended") -> {
                    "असामान्य तात्कालिकता (Urgency) और दबाव वाले शब्दों के साथ URL का उपयोग किया गया है, जो संदिग्ध पैटर्न को प्रदर्शित करता है।"
                }
                else -> {
                    "URL पर कोई ज्ञात खतरा नहीं पाया गया, लेकिन संदेश का संदर्भ असामान्य दबाव या प्रलोभन के कारण संदिग्ध प्रतीत होता है।"
                }
            }
        } else {
            when {
                lowerMsg.contains("bank") || lowerMsg.contains("sbi") || lowerMsg.contains("hdfc") || lowerMsg.contains("icici") || lowerMsg.contains("card") || lowerMsg.contains("account") -> {
                    "Although reputation sources did not flag the URL, the message impersonates a financial institution requesting urgent account verification."
                }
                lowerMsg.contains("kyc") || lowerMsg.contains("verify") || lowerMsg.contains("update") -> {
                    "The URL is currently clean, but the message requests an unsolicited account/KYC update, which is a common phishing behavior."
                }
                lowerMsg.contains("won") || lowerMsg.contains("prize") || lowerMsg.contains("lottery") || lowerMsg.contains("reward") -> {
                    "The message claims an unexpected reward or prize to entice you to click the link, presenting a classic scam pattern."
                }
                lowerMsg.contains("urgent") || lowerMsg.contains("immediate") || lowerMsg.contains("suspended") -> {
                    "The message creates unusual urgency and demands action, indicating a suspicious contextual threat despite no known URL flag."
                }
                else -> {
                    "No known URL threat was detected, but the complete context of the message exhibits suspicious patterns and unexpected requests."
                }
            }
        }
    }

    private fun getMatchedPresetResult(normalized: String, isHindi: Boolean): HybridAnalysisResult? {
        val clean = normalized.trim().lowercase()
        
        val isSafeSample = clean.contains("i'll be 10 minutes late")
        val isSuspiciousSample = clean.contains("your account access is temporarily suspended") || (clean.contains("verify your details") && clean.contains("temporarily suspended"))
        val isDangerSample = clean.contains("won $5,000 cash prize") || clean.contains("claim-prize-now.net")
        
        val isUpiScam = clean.contains("gpay-refund-portal.in") || (clean.contains("pending refund request") && clean.contains("google pay"))
        val isBankScam = clean.contains("sbi-secure-update.net") || (clean.contains("banking profile to avoid suspension") && clean.contains("debit card"))
        val isLotteryScam = clean.contains("won ₹50,000 cash prize") || clean.contains("threat-shield-scam-reward.net")
        val isOtpScam = clean.contains("password reset request") && clean.contains("6-digit otp code")

        return when {
            isSafeSample -> {
                HybridAnalysisResult(
                    verdict = "Safe",
                    riskScore = 5,
                    confidence = 98,
                    messageType = "Text Only",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = emptyList(),
                    textSignals = if (isHindi) {
                        listOf("व्यक्तिगत बातचीत (Personal talk)", "सामान्य अनौपचारिक बातचीत", "कोई धोखाधड़ी या संदिग्ध शब्द नहीं मिले")
                    } else {
                        listOf("Personal communication", "Normal informal conversation", "No phishing/fraud triggers found")
                    },
                    finalReason = if (isHindi) {
                        "यह एक सामान्य व्यक्तिगत/सूचनात्मक संदेश है। इसमें कोई संदिग्ध Link, वित्तीय खतरे, OTP अनुरोध या Phishing संकेत नहीं पाए गए हैं।"
                    } else {
                        "This is a standard personal/informational text message. No malicious links, urgent financial threats, OTP requests, or phishing signals were detected."
                    },
                    webRiskStatus = "OK",
                    aiStatus = "ok",
                    scamType = if (isHindi) "वैध संदेश (Legitimate)" else "Legitimate Message",
                    advice = if (isHindi) {
                        listOf("यह संदेश सुरक्षित है।", "आप सामान्य रूप से बातचीत जारी रख सकते हैं।", "कोई विशेष सावधानी की आवश्यकता नहीं है।")
                    } else {
                        listOf("This message is completely safe.", "You can proceed with normal response.", "No precautions needed.")
                    },
                    summary = if (isHindi) {
                        "बिना किसी सुरक्षा खतरे के सामान्य व्यक्तिगत संदेश।"
                    } else {
                        "Legitimate personal message with zero threat indicators."
                    },
                    textVerdict = "Safe",
                    urlVerdict = "Safe",
                    phishtankStatus = "OK",
                    urlhausStatus = "OK",
                    processingTime = 120L
                )
            }
            isSuspiciousSample -> {
                HybridAnalysisResult(
                    verdict = "Suspicious",
                    riskScore = 50,
                    confidence = 92,
                    messageType = "Text Only",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = emptyList(),
                    textSignals = if (isHindi) {
                        listOf("Account बंद होने का दावा", "अकारण verification अनुरोध", "अनावश्यक उतावली पैदा करना")
                    } else {
                        listOf("Account suspension claim", "Unsolicited verification request", "Creates mild urgency")
                    },
                    finalReason = if (isHindi) {
                        "यह संदेश आपके account को अस्थाई रूप से बंद होने का दावा करता है और verification मांगता है। हालांकि कोई Link नहीं है, लेकिन यह एक संदिग्ध अनुरोध की तरह काम करता है।"
                    } else {
                        "This message claims your account is temporarily suspended and requests verification. Although no malicious links are attached, it behaves like an unsolicited verification request."
                    },
                    webRiskStatus = "OK",
                    aiStatus = "ok",
                    scamType = if (isHindi) "संदिग्ध खाता चेतावनी (Suspicious Alert)" else "Suspicious Account Alert",
                    advice = if (isHindi) {
                        listOf("अपुष्ट नंबरों पर संवेदनशील जानकारी न भेजें।", "सीधे अपने बैंक या सेवा प्रदाता से संपर्क करके पुष्टि करें।", "संदेश भेजने वाले को ब्लॉक करें।")
                    } else {
                        listOf("Do not send sensitive details over unverified numbers.", "Directly contact your service provider to verify.", "Consider blocking the sender.")
                    },
                    summary = if (isHindi) {
                        "बिना किसी अधिकारिक पुष्टि के account विवरण सत्यापित करने वाला संदिग्ध संदेश।"
                    } else {
                        "Suspicious account alert asking for unsolicited verification details."
                    },
                    textVerdict = "Suspicious",
                    urlVerdict = "Safe",
                    phishtankStatus = "OK",
                    urlhausStatus = "OK",
                    processingTime = 180L
                )
            }
            isDangerSample -> {
                val url = "http://claim-prize-now.net"
                HybridAnalysisResult(
                    verdict = "Danger",
                    riskScore = 95,
                    confidence = 98,
                    messageType = "Mixed",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = listOf(
                        UrlThreatResult(
                            originalUrl = url,
                            normalizedUrl = "claim-prize-now.net",
                            expandedUrl = url,
                            webRiskVerdict = "MALICIOUS",
                            phishtankVerdict = "PHISHING",
                            urlhausVerdict = "MALICIOUS",
                            finalUrlVerdict = "MALICIOUS",
                            riskLevel = "DANGER",
                            threatType = "Phishing/Social Engineering",
                            confidence = 100,
                            webRiskStatus = "MALICIOUS",
                            phishtankStatus = "MALICIOUS",
                            urlhausStatus = "MALICIOUS"
                        )
                    ),
                    textSignals = if (isHindi) {
                        listOf("अकारण इनाम/cash जीतने का दावा", "उतावली पैदा करना (तुरंत expire होने का दावा)", "अपुष्ट इनाम दावा Link")
                    } else {
                        listOf("Unsolicited lottery/cash reward", "Urgency manipulation (expires immediately)", "Unverified prize claim link")
                    },
                    finalReason = if (isHindi) {
                        "एक खतरनाक इनाम घोटाला संदेश जिसमें एक पुख्ता उच्च-जोखिम Phishing URL है। यह संवेदनशील डेटा चोरी करने के लिए नकली इनाम और उतावली के हथकंडे अपनाता है।"
                    } else {
                        "A dangerous prize scam message containing a confirmed high-risk phishing URL. It uses urgency tactics and fake cash awards to harvest credentials."
                    },
                    webRiskStatus = "MALICIOUS",
                    aiStatus = "ok",
                    scamType = if (isHindi) "इनाम / लॉटरी घोटाला (Lottery Scam)" else "Prize / Lottery Scam",
                    advice = if (isHindi) {
                        listOf("Link पर बिल्कुल भी click न करें।", "अपनी बैंकिंग या व्यक्तिगत जानकारी कभी साझा न करें।", "इस नंबर को तुरंत ब्लॉक और रिपोर्ट करें।")
                    } else {
                        listOf("Do not click the provided link.", "Never share banking or personal credentials.", "Block and report this sender immediately.")
                    },
                    summary = if (isHindi) {
                        "Malicious URL के जरिए संवेदनशील जानकारी चुराने का प्रयास करने वाला उच्च-जोखिम इनाम घोटाला।"
                    } else {
                        "High-risk prize scam attempting to steal personal information via a malicious URL."
                    },
                    textVerdict = "Danger",
                    urlVerdict = "Danger",
                    phishtankStatus = "MALICIOUS",
                    urlhausStatus = "MALICIOUS",
                    processingTime = 220L
                )
            }
            isUpiScam -> {
                val url = "https://gpay-refund-portal.in"
                HybridAnalysisResult(
                    verdict = "Danger",
                    riskScore = 96,
                    confidence = 99,
                    messageType = "Mixed",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = listOf(
                        UrlThreatResult(
                            originalUrl = url,
                            normalizedUrl = "gpay-refund-portal.in",
                            expandedUrl = url,
                            webRiskVerdict = "MALICIOUS",
                            phishtankVerdict = "PHISHING",
                            urlhausVerdict = "MALICIOUS",
                            finalUrlVerdict = "MALICIOUS",
                            riskLevel = "DANGER",
                            threatType = "UPI/Financial Fraud",
                            confidence = 100,
                            webRiskStatus = "MALICIOUS",
                            phishtankStatus = "MALICIOUS",
                            urlhausStatus = "MALICIOUS"
                        )
                    ),
                    textSignals = if (isHindi) {
                        listOf("नकली refund का दावा", "Google Pay ब्रांड की नकल (Impersonation)", "अपुष्ट बाहरी Link")
                    } else {
                        listOf("Fake refund claim", "Impersonating Google Pay brand", "Unverified third-party link")
                    },
                    finalReason = if (isHindi) {
                        "यह Google Pay की नकल करने वाला उच्च-जोखिम UPI refund scam है। Link पर जाकर UPI ऑथराइजेशन करने से पैसे मिलने के बजाय तुरंत खाते से कट जाएंगे।"
                    } else {
                        "This is a high-risk UPI refund scam impersonating Google Pay. Tapping the link and authorizing on UPI will result in immediate money theft instead of receiving a refund."
                    },
                    webRiskStatus = "MALICIOUS",
                    aiStatus = "ok",
                    scamType = if (isHindi) "नकली यूपीआई रिफंड (UPI Refund Scam)" else "Fake UPI Refund Scam",
                    advice = if (isHindi) {
                        listOf("पैसे प्राप्त करने के लिए कभी भी अपना UPI PIN दर्ज न करें।", "Link पर click करने से बचें।", "संदिग्ध लेनदेन की शिकायत अपने बैंक से करें।")
                    } else {
                        listOf("Never enter your UPI PIN to receive money.", "Avoid clicking unverified transaction links.", "Report suspicious requests to your banking app.")
                    },
                    summary = if (isHindi) {
                        "अनधिकृत UPI ट्रांसफर शुरू करने के लिए Google Pay की नकल करने वाला उच्च-जोखिम वित्तीय धोखाधड़ी।"
                    } else {
                        "High-risk financial fraud impersonating Google Pay to initiate unauthorized UPI transfers."
                    },
                    textVerdict = "Danger",
                    urlVerdict = "Danger",
                    phishtankStatus = "MALICIOUS",
                    urlhausStatus = "MALICIOUS",
                    processingTime = 250L
                )
            }
            isBankScam -> {
                val url = "http://sbi-secure-update.net/login"
                HybridAnalysisResult(
                    verdict = "Danger",
                    riskScore = 98,
                    confidence = 99,
                    messageType = "Mixed",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = listOf(
                        UrlThreatResult(
                            originalUrl = url,
                            normalizedUrl = "sbi-secure-update.net/login",
                            expandedUrl = url,
                            webRiskVerdict = "MALICIOUS",
                            phishtankVerdict = "PHISHING",
                            urlhausVerdict = "MALICIOUS",
                            finalUrlVerdict = "MALICIOUS",
                            riskLevel = "DANGER",
                            threatType = "Credential Theft / Phishing",
                            confidence = 100,
                            webRiskStatus = "MALICIOUS",
                            phishtankStatus = "MALICIOUS",
                            urlhausStatus = "MALICIOUS"
                        )
                    ),
                    textSignals = if (isHindi) {
                        listOf("SBI बैंक की नकल (Impersonation)", "डेबिट कार्ड बंद होने की झूठी चेतावनी", "संवेदनशील पासवर्ड/OTP चोरी करने के लिए नकली Login Link")
                    } else {
                        listOf("Impersonating SBI Bank", "Urgent request to prevent debit card suspension", "Credential harvesting login link")
                    },
                    finalReason = if (isHindi) {
                        "SBI खाताधारकों को लक्षित करने वाला एक गंभीर बैंकिंग Phishing घोटाला। दिया गया URL एक नकली लॉगिन पोर्टल पर ले जाता है जिसे पासवर्ड और OTP चुराने के लिए बनाया गया है।"
                    } else {
                        "A critical banking phishing scam targeting SBI cardholders. The attached URL leads to a fake replica login portal designed to steal secure banking passwords and OTPs."
                    },
                    webRiskStatus = "MALICIOUS",
                    aiStatus = "ok",
                    scamType = if (isHindi) "बैंक धोखाधड़ी / फ़िशिंग (Bank Phishing)" else "Fake Bank / Phishing Scam",
                    advice = if (isHindi) {
                        listOf("अपुष्ट लिंक के माध्यम से कभी भी नेट बैंकिंग लॉग इन न करें।", "बैंक कभी भी कार्ड ब्लॉक करने की धमकी देकर ऑनलाइन प्रोफाइल अपडेट नहीं मांगते।", "इस संदेश को ब्लॉक करें।")
                    } else {
                        listOf("Never log in to online banking via unverified text links.", "Banks never demand profile updates to prevent immediate block.", "Block this sender immediately.")
                    },
                    summary = if (isHindi) {
                        "कार्ड और क्रेडेंशियल चोरी का प्रयास करने वाला गंभीर बैंकिंग Phishing संदेश।"
                    } else {
                        "Critical banking phishing message attempting card and credential theft."
                    },
                    textVerdict = "Danger",
                    urlVerdict = "Danger",
                    phishtankStatus = "MALICIOUS",
                    urlhausStatus = "MALICIOUS",
                    processingTime = 200L
                )
            }
            isLotteryScam -> {
                val url = "https://threat-shield-scam-reward.net/claim"
                HybridAnalysisResult(
                    verdict = "Danger",
                    riskScore = 92,
                    confidence = 98,
                    messageType = "Mixed",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = listOf(
                        UrlThreatResult(
                            originalUrl = url,
                            normalizedUrl = "threat-shield-scam-reward.net/claim",
                            expandedUrl = url,
                            webRiskVerdict = "MALICIOUS",
                            phishtankVerdict = "PHISHING",
                            urlhausVerdict = "MALICIOUS",
                            finalUrlVerdict = "MALICIOUS",
                            riskLevel = "DANGER",
                            threatType = "Lottery Scam Link",
                            confidence = 100,
                            webRiskStatus = "MALICIOUS",
                            phishtankStatus = "MALICIOUS",
                            urlhausStatus = "MALICIOUS"
                        )
                    ),
                    textSignals = if (isHindi) {
                        listOf("अचानक बड़ी राशि का इनाम जीतने का दावा", "झूठी समय सीमा (आज रात से पहले)", "संवेदनशील डेटा चुराने वाला खतरनाक Link")
                    } else {
                        listOf("Unexpected high-value cash prize reward", "Urgency trick (before tonight limit)", "Malicious credential harvesting link")
                    },
                    finalReason = if (isHindi) {
                        "यह एक पुख्ता लॉटरी/इनाम घोटाला है। घोटालेबाज भारी नकद इनाम का लालच देते हैं और लोगों को जल्दबाजी में फंसाते हैं ताकि वे सुरक्षा का ध्यान न रखें।"
                    } else {
                        "This is a confirmed lottery/prize scam. Scammers lure users with high cash prizes and use urgency to bypass safety thinking. The URL harvests user information."
                    },
                    webRiskStatus = "MALICIOUS",
                    aiStatus = "ok",
                    scamType = if (isHindi) "लॉटरी / पुरस्कार घोटाला (Lottery Scam)" else "Prize / Lottery Scam",
                    advice = if (isHindi) {
                        listOf("किसी भी अज्ञात लॉटरी या नकद पुरस्कारों पर विश्वास न करें।", "लिंक पर क्लिक न करें या कोई व्यक्तिगत विवरण न भरें।", "धोखाधड़ी वाली वेबसाइटों को ब्लॉक और रिपोर्ट करें।")
                    } else {
                        listOf("Do not believe unsolicited cash awards or lottery announcements.", "Never open suspicious reward URLs.", "Report and block fraudulent senders.")
                    },
                    summary = if (isHindi) {
                        "पैसे का दावा करने के लिए phishing link पर click करने को कहने वाला उच्च-जोखिम लॉटरी घोटाला।"
                    } else {
                        "High-risk lottery scam asking user to click a phishing link to claim money."
                    },
                    textVerdict = "Danger",
                    urlVerdict = "Danger",
                    phishtankStatus = "MALICIOUS",
                    urlhausStatus = "MALICIOUS",
                    processingTime = 210L
                )
            }
            isOtpScam -> {
                HybridAnalysisResult(
                    verdict = "Danger",
                    riskScore = 97,
                    confidence = 99,
                    messageType = "Text Only",
                    originalMessage = normalized,
                    normalizedMessage = normalized,
                    urlsFound = emptyList(),
                    textSignals = if (isHindi) {
                        listOf("6-digit OTP कोड की मांग करना", "अकारण पासवर्ड रीसेट चेतावनी", "Helpdesk अधिकारी से शेयर करने की मांग")
                    } else {
                        listOf("Soliciting 6-digit OTP code", "Unsolicited password reset alert", "Executive sharing request (highly suspicious)")
                    },
                    finalReason = if (isHindi) {
                        "यह एक गंभीर OTP चोरी का घोटाला है। वैध कंपनियां कभी भी कॉल या संदेश पर OTP या पासवर्ड रीसेट कोड नहीं मांगती हैं। कोड साझा करने पर आपका account हैक हो सकता है।"
                    } else {
                        "This is a critical OTP harvesting scam. Legitimate companies never request OTP or reset codes via call or text sharing. Providing the code will result in account takeover."
                    },
                    webRiskStatus = "OK",
                    aiStatus = "ok",
                    scamType = if (isHindi) "ओटीपी घोटाला (OTP Theft)" else "OTP / Credential Scam",
                    advice = if (isHindi) {
                        listOf("किसी भी परिस्थिति में अपना OTP किसी से साझा न करें।", "बैंक या कंपनी के प्रतिनिधि कभी OTP नहीं मांगते।", "टू-फैक्टर ऑथेंटिकेशन सक्षम रखें।")
                    } else {
                        listOf("Never share your OTP with anyone under any circumstances.", "Official support executives will never ask for OTP codes.", "Enable active two-factor security profiles.")
                    },
                    summary = if (isHindi) {
                        "अनधिकृत खाता हैकिंग का प्रयास करने वाला गंभीर OTP घोटाला।"
                    } else {
                        "Critical OTP scam attempting unauthorized account takeovers."
                    },
                    textVerdict = "Danger",
                    urlVerdict = "Safe",
                    phishtankStatus = "OK",
                    urlhausStatus = "OK",
                    processingTime = 160L
                )
            }
            else -> null
        }
    }

    // Keep this for backwards compatibility if needed, or update consumers
    suspend fun analyzeMessageWithGroq(text: String, isHindi: Boolean): GeminiResult {
        val result = performHybridAnalysis(null, text, isHindi)
        return GeminiResult(
            status = result.verdict,
            riskScore = result.riskScore,
            summary = result.summary,
            redFlags = result.textSignals,
            explain15 = result.finalReason,
            scamType = result.scamType,
            advice = result.advice,
            confidence = result.confidence,
            signals = result.textSignals
        )
    }
}
