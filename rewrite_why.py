import sys

def rewrite_why():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()

    old_why = """                                Icon(
                                    imageVector = Icons.Rounded.Link, // A default icon
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )"""

    new_why = """                                val reasonText = reason.lowercase()
                                val icon = when {
                                    reasonText.contains("link") || reasonText.contains("url") -> Icons.Rounded.Link
                                    reasonText.contains("fake") || reasonText.contains("impersonat") -> Icons.Rounded.Policy
                                    reasonText.contains("urgent") || reasonText.contains("pressure") -> Icons.Rounded.WarningAmber
                                    reasonText.contains("private") || reasonText.contains("info") || reasonText.contains("credential") -> Icons.Rounded.PersonSearch
                                    reasonText.contains("bank") || reasonText.contains("finance") -> Icons.Rounded.AccountBalance
                                    else -> Icons.Rounded.Info
                                }
                                val iconColor = when {
                                    isDanger -> dangerRed
                                    isWarning || isSuspicious -> warningOrange
                                    else -> safeGreen
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(16.dp)
                                )"""
                                
    content = content.replace(old_why, new_why)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

rewrite_why()
