import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()

    content = content.replace('text = "Link Security",', 'text = if (isHindi) "Link Security" else "Link Security",')
    content = content.replace('text = "${parsedUrls.size} Links Detected",', 'text = if (isHindi) "${parsedUrls.size} Links Detected" else "${parsedUrls.size} Links Detected",')
    content = content.replace('text = "Detected Link",', 'text = if (isHindi) "Detected Link" else "Detected Link",')
    content = content.replace('text = "Overall Status: ",', 'text = if (isHindi) "Overall Status: " else "Overall Status: ",')
    
    # We used variables for labels, let's fix them:
    old_overallLabel = """                            val overallLabel = when (urlStatus.riskLevel.uppercase()) {
                                "MALICIOUS", "DANGER" -> "Known Threat Detected"
                                "NO_KNOWN_THREAT", "SAFE" -> "No Known Threat Detected"
                                else -> "Unverified"
                            }"""
    
    new_overallLabel = """                            val overallLabel = when (urlStatus.riskLevel.uppercase()) {
                                "MALICIOUS", "DANGER" -> if (isHindi) "Known Threat Detected" else "Known Threat Detected"
                                "NO_KNOWN_THREAT", "SAFE" -> if (isHindi) "No Known Threat Detected" else "No Known Threat Detected"
                                else -> if (isHindi) "Unverified" else "Unverified"
                            }"""
                            
    content = content.replace(old_overallLabel, new_overallLabel)

    old_vLabel = """                                    val vLabel = when (verdict.uppercase()) {
                                        "MALICIOUS", "DANGER" -> "Known Threat"
                                        "NO_KNOWN_THREAT", "SAFE" -> "No Known Threat"
                                        else -> "Unverified"
                                    }"""
                                    
    new_vLabel = """                                    val vLabel = when (verdict.uppercase()) {
                                        "MALICIOUS", "DANGER" -> if (isHindi) "Known Threat" else "Known Threat"
                                        "NO_KNOWN_THREAT", "SAFE" -> if (isHindi) "No Known Threat" else "No Known Threat"
                                        else -> if (isHindi) "Unverified" else "Unverified"
                                    }"""

    content = content.replace(old_vLabel, new_vLabel)

    old_hideButton = """                                    text = if (isUrlsExpanded) "Hide Links" else "View All Links","""
    new_hideButton = """                                    text = if (isUrlsExpanded) (if (isHindi) "Hide Links" else "Hide Links") else (if (isHindi) "View All Links" else "View All Links"),"""
    content = content.replace(old_hideButton, new_hideButton)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

apply_patch()
