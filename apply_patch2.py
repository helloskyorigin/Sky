import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'r') as f:
        content = f.read()

    old_verdict_eval = """                            // Final URL Verdict evaluation
                            val hasMalware = (webRiskVerdict == "danger" && (threatType == "MALWARE" || threatType == "UNWANTED_SOFTWARE")) || urlhausVerdict == "danger"
                            val hasPhishing = phishtankVerdict == "danger"
                            val hasSocialEngineering = (webRiskVerdict == "danger" && threatType == "SOCIAL_ENGINEERING")

                            val finalUrlVerdict = when {
                                hasMalware || hasPhishing || hasSocialEngineering -> "danger"
                                webRiskVerdict == "safe" && phishtankVerdict == "safe" && urlhausVerdict == "safe" -> "safe"
                                else -> "unknown"
                            }"""

    new_verdict_eval = """                            // Final URL Verdict evaluation
                            val hasMalware = (webRiskVerdict == "MALICIOUS" && (threatType == "MALWARE" || threatType == "UNWANTED_SOFTWARE")) || urlhausVerdict == "MALICIOUS"
                            val hasPhishing = phishtankVerdict == "MALICIOUS"
                            val hasSocialEngineering = (webRiskVerdict == "MALICIOUS" && threatType == "SOCIAL_ENGINEERING")

                            val finalUrlVerdict = when {
                                hasMalware || hasPhishing || hasSocialEngineering || webRiskVerdict == "MALICIOUS" || phishtankVerdict == "MALICIOUS" || urlhausVerdict == "MALICIOUS" -> "MALICIOUS"
                                webRiskVerdict == "NO_KNOWN_THREAT" || phishtankVerdict == "NO_KNOWN_THREAT" || urlhausVerdict == "NO_KNOWN_THREAT" -> "NO_KNOWN_THREAT"
                                else -> "UNVERIFIED"
                            }"""
    content = content.replace(old_verdict_eval, new_verdict_eval)
    
    # replace confidence calculation safe/danger hardcodes
    content = content.replace('successVerdicts.contains("danger")', 'successVerdicts.contains("MALICIOUS")')
    content = content.replace('successVerdicts.contains("safe")', 'successVerdicts.contains("NO_KNOWN_THREAT")')
    
    # replace URL SIGNALS logic
    old_url_signals = """        // URL SIGNALS & MULTIPLE URL LOGIC
        val urlScanHasDanger = urlResults.any { 
            it.webRiskVerdict == "danger" || it.phishtankVerdict == "danger" || it.urlhausVerdict == "danger" || it.finalUrlVerdict == "danger"
        }
        val urlScanHasUnknown = !urlScanHasDanger && urlResults.any { 
            it.webRiskVerdict == "unknown" || it.phishtankVerdict == "unknown" || it.urlhausVerdict == "unknown" || it.finalUrlVerdict == "unknown"
        }
        
        val overallUrlVerdict = when {
            urlResults.isEmpty() -> "Safe"
            urlScanHasDanger -> "Danger"
            urlScanHasUnknown -> "Unknown"
            else -> "Safe"
        }"""
        
    new_url_signals = """        // URL SIGNALS & MULTIPLE URL LOGIC
        val urlScanHasDanger = urlResults.any { 
            it.webRiskVerdict == "MALICIOUS" || it.phishtankVerdict == "MALICIOUS" || it.urlhausVerdict == "MALICIOUS" || it.finalUrlVerdict == "MALICIOUS"
        }
        val urlScanHasUnverified = !urlScanHasDanger && urlResults.any { 
            it.webRiskVerdict == "UNVERIFIED" || it.phishtankVerdict == "UNVERIFIED" || it.urlhausVerdict == "UNVERIFIED" || it.finalUrlVerdict == "UNVERIFIED"
        }
        
        val overallUrlVerdict = when {
            urlResults.isEmpty() -> "No URLs"
            urlScanHasDanger -> "MALICIOUS"
            urlScanHasUnverified -> "UNVERIFIED"
            else -> "NO_KNOWN_THREAT"
        }"""
        
    content = content.replace(old_url_signals, new_url_signals)

    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'w') as f:
        f.write(content)

apply_patch()
