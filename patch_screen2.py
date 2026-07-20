import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()
        
    old_confidence_end = """            // 3. WHY THIS RESULT? CARD"""

    new_link_security_ui = """            val parsedUrls = analysis.urlStatuses
                .filter { !it.startsWith("METADATA:") }
                .mapNotNull { parseUrlStatus(it, isHindi) }
                .sortedBy { 
                    when (it.riskLevel.uppercase()) {
                        "MALICIOUS", "DANGER" -> 0
                        "UNVERIFIED", "UNKNOWN", "FAILED", "TIMEOUT" -> 1
                        else -> 2
                    }
                }

            if (parsedUrls.isNotEmpty()) {
                var isUrlsExpanded by remember { mutableStateOf(false) }
                val highestRiskUrl = parsedUrls.first()
                val hasMultiple = parsedUrls.size > 1
                
                // 2.5 LINK SECURITY CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp), clip = false)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Link,
                                contentDescription = "Link Security",
                                tint = textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Link Security",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        if (hasMultiple) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "${parsedUrls.size} Links Detected",
                                fontSize = 12.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val urlsToShow = if (isUrlsExpanded) parsedUrls else listOf(highestRiskUrl)
                        
                        urlsToShow.forEachIndexed { index, urlStatus ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(16.dp))
                                androidx.compose.material3.HorizontalDivider(color = cardBorder.copy(alpha = 0.3f), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            val domain = try {
                                java.net.URI(urlStatus.originalUrl).host ?: urlStatus.originalUrl
                            } catch (e: Exception) { urlStatus.originalUrl }
                            
                            Text(
                                text = "Detected Link",
                                fontSize = 11.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = domain,
                                fontSize = 15.sp,
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val overallLabel = when (urlStatus.riskLevel.uppercase()) {
                                "MALICIOUS", "DANGER" -> "Known Threat Detected"
                                "NO_KNOWN_THREAT", "SAFE" -> "No Known Threat Detected"
                                else -> "Unverified"
                            }
                            
                            val overallColor = when (urlStatus.riskLevel.uppercase()) {
                                "MALICIOUS", "DANGER" -> Color(0xFFE53935)
                                "NO_KNOWN_THREAT", "SAFE" -> Color(0xFF43A047)
                                else -> Color(0xFFF57C00)
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Overall Status: ",
                                    fontSize = 13.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = overallLabel,
                                    fontSize = 13.sp,
                                    color = overallColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Breakdown
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                val sources = listOf(
                                    "Google Web Risk" to urlStatus.webRiskVerdict,
                                    "PhishTank" to urlStatus.phishtankVerdict,
                                    "URLhaus" to urlStatus.urlhausVerdict
                                )
                                
                                sources.forEachIndexed { i, (name, verdict) ->
                                    val vLabel = when (verdict.uppercase()) {
                                        "MALICIOUS", "DANGER" -> "Known Threat"
                                        "NO_KNOWN_THREAT", "SAFE" -> "No Known Threat"
                                        else -> "Unverified"
                                    }
                                    val vColor = when (verdict.uppercase()) {
                                        "MALICIOUS", "DANGER" -> Color(0xFFE53935)
                                        "NO_KNOWN_THREAT", "SAFE" -> Color(0xFF43A047)
                                        else -> Color(0xFFF57C00)
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = name, fontSize = 12.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                                        Text(text = vLabel, fontSize = 12.sp, color = vColor, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        
                        if (hasMultiple) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = { isUrlsExpanded = !isUrlsExpanded },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = if (isUrlsExpanded) "Hide Links" else "View All Links",
                                    color = primaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. WHY THIS RESULT? CARD"""

    content = content.replace(old_confidence_end, new_link_security_ui)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

apply_patch()
