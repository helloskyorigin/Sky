import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'r') as f:
        content = f.read()

    # Fixing EXPLANATION ENGINE hardcodes
    content = content.replace('phishtankVerdict == "danger"', 'phishtankVerdict == "MALICIOUS"')
    content = content.replace('urlhausVerdict == "danger"', 'urlhausVerdict == "MALICIOUS"')
    content = content.replace('webRiskVerdict == "danger"', 'webRiskVerdict == "MALICIOUS"')
    content = content.replace('overallUrlVerdict == "Danger"', 'overallUrlVerdict == "MALICIOUS"')
    content = content.replace('overallUrlVerdict == "Unknown"', 'overallUrlVerdict == "UNVERIFIED"')
    content = content.replace('overallUrlVerdict == "Safe"', 'overallUrlVerdict == "NO_KNOWN_THREAT"')
    
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'w') as f:
        f.write(content)

apply_patch()
