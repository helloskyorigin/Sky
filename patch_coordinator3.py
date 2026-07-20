import sys, re

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisCoordinator.kt', 'r') as f:
        content = f.read()

    content = content.replace("val aiResult = SecurityAnalysisEngine.analyzeMessageWithGroq(text, isHindi)", "val aiResult = SecurityAnalysisEngine.performHybridAnalysis(null, text, isHindi)")
    content = content.replace("status = mapAiStatus(aiResult.status),", "status = mapAiStatus(aiResult.verdict),")
    content = content.replace("reasons = aiResult.redFlags,", "reasons = aiResult.textSignals,")
    content = content.replace("explain15 = aiResult.explain15,", "explain15 = aiResult.finalReason,")
    content = content.replace("signals = aiResult.signals", "signals = aiResult.textSignals")
    
    # URL status populations
    replacement = """
            // Populate URL statuses
            val urlStatusStrings = aiResult.urlsFound.map { threatRes ->
                val json = org.json.JSONObject()
                json.put("url", threatRes.originalUrl)
                json.put("risk_level", threatRes.riskLevel)
                json.put("webrisk", threatRes.webRiskVerdict)
                json.put("phishtank", threatRes.phishtankVerdict)
                json.put("urlhaus", threatRes.urlhausVerdict)
                json.toString()
            }
            
            // Add METADATA string for text/url verdicts if needed
            val metaJson = org.json.JSONObject()
            metaJson.put("text_verdict", aiResult.textVerdict)
            metaJson.put("url_verdict", aiResult.urlVerdict)
            val fullUrlStatuses = urlStatusStrings.toMutableList()
            fullUrlStatuses.add("METADATA:" + metaJson.toString())
            
            MessageAnalysis(
                text = text,
                date = "", // formatted in UI
                status = mapAiStatus(aiResult.verdict),
                score = aiResult.riskScore,
                summary = aiResult.summary,
                reasons = aiResult.textSignals,
                links = urls,
                explain15 = aiResult.finalReason,
                timestamp = System.currentTimeMillis(),
                scamType = aiResult.scamType,
                urlStatuses = fullUrlStatuses,
                advice = aiResult.advice,
                confidence = aiResult.confidence,
                signals = aiResult.textSignals
            )
    """
    
    # We replace the MessageAnalysis( ... ) part completely.
    pattern = r'MessageAnalysis\([\s\S]*?signals = aiResult\.textSignals\s*\)'
    content = re.sub(pattern, replacement.strip(), content)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisCoordinator.kt', 'w') as f:
        f.write(content)

apply_patch()
