import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'r') as f:
        content = f.read()

    old_conflict = """        // CONFLICT RESOLUTION
        var finalVerdict = when {
            // Confirmed malicious URL (Hard Override priority 1)
            overallUrlVerdict == "Danger" -> "Danger"
            
            // Strong scam text (priority 2)
            textVerdict == "Danger" -> "Danger"
            
            // URL Unknown cases
            overallUrlVerdict == "Unknown" -> {
                when (textVerdict) {
                    "Warning" -> "Warning"
                    "Suspicious" -> "Warning" // Multiple indicators upgrade
                    "Safe" -> "Suspicious"    // Case 5: URL Unknown, Text Safe -> Final Suspicious
                    else -> "Suspicious"
                }
            }
            
            // URL Safe cases
            overallUrlVerdict == "Safe" -> {
                when (textVerdict) {
                    "Warning" -> "Warning"       // Case 4: URL Safe, Text Warning -> Final Warning
                    "Suspicious" -> "Suspicious"
                    "Safe" -> "Safe"
                    else -> "Safe"
                }
            }
            
            else -> "Safe"
        }"""

    new_conflict = """        // CONFLICT RESOLUTION
        var finalVerdict = when {
            // Confirmed malicious URL (Hard Override priority 1)
            overallUrlVerdict == "MALICIOUS" -> "Danger"
            
            // Strong scam text (priority 2)
            textVerdict == "Danger" -> "Danger"
            
            // URL Unverified cases
            overallUrlVerdict == "UNVERIFIED" -> {
                when (textVerdict) {
                    "Warning" -> "Warning"
                    "Suspicious" -> "Suspicious" 
                    "Safe" -> "Safe" // A safe message with unverified URL shouldn't be automatically suspicious unless we have other clues. Wait, the prompt says "Consider the uncertainty in the final score. Depending on context, the final result may remain SAFE with a caution note or become SUSPICIOUS if additional uncertainty/risk signals exist."
                    else -> "Safe"
                }
            }
            
            // URL No Known Threat cases
            overallUrlVerdict == "NO_KNOWN_THREAT" || overallUrlVerdict == "No URLs" -> {
                when (textVerdict) {
                    "Warning" -> "Warning"
                    "Suspicious" -> "Suspicious"
                    "Safe" -> "Safe"
                    else -> "Safe"
                }
            }
            
            else -> "Safe"
        }"""
    content = content.replace(old_conflict, new_conflict)

    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'w') as f:
        f.write(content)

apply_patch()
