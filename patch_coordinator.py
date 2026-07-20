import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisCoordinator.kt', 'r') as f:
        content = f.read()

    old_coordinator_call = """        // 4. Decision Maker - AI ONLY for V1
        return try {
            val aiResult = SecurityAnalysisEngine.analyzeMessageWithGroq(text, isHindi)
            MessageAnalysis(
                text = text,
                date = "", // formatted in UI
                status = mapAiStatus(aiResult.status),
                score = aiResult.riskScore,
                summary = aiResult.summary,
                reasons = aiResult.redFlags,
                links = urls,
                explain15 = aiResult.explain15,
                timestamp = System.currentTimeMillis(),
                scamType = aiResult.scamType,
                urlStatuses = urlStatuses,
                advice = aiResult.advice,
                confidence = aiResult.confidence,
                signals = aiResult.signals
            )
        }"""

    new_coordinator_call = """        // 4. Decision Maker - AI ONLY for V1
        return try {
            val aiResult = SecurityAnalysisEngine.performHybridAnalysis(null, text, isHindi)
            
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
        }"""

    content = content.replace(old_coordinator_call, new_coordinator_call)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisCoordinator.kt', 'w') as f:
        f.write(content)

apply_patch()
