import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'r') as f:
        content = f.read()

    # Fixing some remaining "unknown" cases for the URL verdicts to map to "UNVERIFIED"
    content = content.replace('webRiskVerdict = "unknown"', 'webRiskVerdict = "UNVERIFIED"')
    content = content.replace('phishtankVerdict = "unknown"', 'phishtankVerdict = "UNVERIFIED"')
    content = content.replace('urlhausVerdict = "unknown"', 'urlhausVerdict = "UNVERIFIED"')
    content = content.replace('finalUrlVerdict = "unknown"', 'finalUrlVerdict = "UNVERIFIED"')
    content = content.replace('riskLevel = "unknown"', 'riskLevel = "UNVERIFIED"')

    # Also fix "unknown" default values in the data class UrlThreatResult (if it has defaults like that)
    content = content.replace('val phishtankVerdict: String = "unknown"', 'val phishtankVerdict: String = "UNVERIFIED"')
    content = content.replace('val urlhausVerdict: String = "unknown"', 'val urlhausVerdict: String = "UNVERIFIED"')
    content = content.replace('val finalUrlVerdict: String = "unknown"', 'val finalUrlVerdict: String = "UNVERIFIED"')
    
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'w') as f:
        f.write(content)

apply_patch()
