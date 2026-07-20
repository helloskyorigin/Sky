import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()
        
    old_reason_ui = """                    val detectedReasons = (analysis.reasons + analysis.signals).distinct().filter { it.isNotBlank() }
                    
                    if (detectedReasons.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        detectedReasons.take(3).forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp)
                                        .size(6.dp)
                                        .background(riskColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = reason,
                                    color = textSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
"""

    new_reason_ui = """                    val detectedReasons = (analysis.reasons + analysis.signals).distinct().filter { it.isNotBlank() }
                    
                    if (detectedReasons.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        detectedReasons.take(3).forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp)
                                        .size(6.dp)
                                        .background(riskColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = reason,
                                    color = textSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                    
                    // Add URL Summary if links exist
                    if (analysis.links.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = cardBorder.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        var knownThreats = 0
                        var unverified = 0
                        var safeLinks = 0
                        
                        analysis.urlStatuses.forEach { statusStr ->
                            if (!statusStr.startsWith("METADATA:")) {
                                val parsed = parseUrlStatus(statusStr, isHindi)
                                if (parsed != null) {
                                    when (parsed.riskLevel.uppercase()) {
                                        "MALICIOUS" -> knownThreats++
                                        "UNVERIFIED" -> unverified++
                                        "NO_KNOWN_THREAT" -> safeLinks++
                                        "SAFE" -> safeLinks++ // Backwards compatibility
                                        "DANGER" -> knownThreats++
                                        else -> unverified++
                                    }
                                }
                            }
                        }
                        
                        val totalUrls = knownThreats + unverified + safeLinks
                        
                        if (totalUrls == 1) {
                            val linkStatus = when {
                                knownThreats > 0 -> if (isHindi) "Known Threat Detected" else "Known Threat Detected"
                                unverified > 0 -> if (isHindi) "Unverified" else "Unverified"
                                else -> if (isHindi) "No known threat detected" else "No known threat detected"
                            }
                            Text(text = "Link Status", fontSize = 12.sp, color = textSecondary)
                            Text(text = linkStatus, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (knownThreats > 0) Color(0xFFE53935) else textPrimary)
                        } else if (totalUrls > 1) {
                            Text(text = "Links Checked: $totalUrls", fontSize = 12.sp, color = textSecondary)
                            val summaryStr = buildString {
                                if (knownThreats > 0) append("Known Threats: $knownThreats ")
                                if (unverified > 0) append("Unverified: $unverified ")
                                if (safeLinks > 0) append("No Known Threat: $safeLinks ")
                            }.trim()
                            Text(text = summaryStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (knownThreats > 0) Color(0xFFE53935) else textPrimary)
                        }
                    }
                }
            }
"""
    content = content.replace(old_reason_ui, new_reason_ui)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

apply_patch()
