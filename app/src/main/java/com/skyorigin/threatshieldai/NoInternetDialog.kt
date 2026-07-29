package com.skyorigin.threatshieldai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun NoInternetDialog(
    isHindi: Boolean = false,
    onDismiss: () -> Unit,
    onRetry: suspend () -> Boolean
) {
    var isChecking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDark = com.skyorigin.threatshieldai.ui.theme.LocalIsDark.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
        tonalElevation = 8.dp,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WifiOff,
                    contentDescription = "No Internet Connection",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = if (isHindi) "कोई इंटरनेट कनेक्शन नहीं" else "No Internet Connection",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
            )
        },
        text = {
            Text(
                text = if (isHindi) 
                    "ThreatShield AI को इंटरनेट कनेक्शन की आवश्यकता है। अपना कनेक्शन जांचें और पुनः प्रयास करें।" 
                else 
                    "ThreatShield AI requires an internet connection. Check your connection and try again.",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isChecking) return@Button
                    isChecking = true
                    scope.launch {
                        val isConnected = onRetry()
                        isChecking = false
                        if (!isConnected) {
                            android.widget.Toast.makeText(
                                context,
                                if (isHindi) "अभी भी ऑफलाइन हैं। कृपया इंटरनेट जांचें।" else "Still offline. Please check your internet connection.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                ),
                enabled = !isChecking
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isHindi) "पुनः प्रयास करें" else "Retry",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        dismissButton = null
    )
}
