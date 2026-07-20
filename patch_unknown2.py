import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'r') as f:
        content = f.read()

    content = content.replace('var webRiskRes = ServiceResult("unknown", "UNKNOWN")', 'var webRiskRes = ServiceResult("UNVERIFIED", "UNKNOWN")')
    content = content.replace('var phishtankRes = ServiceResult("unknown", "UNKNOWN")', 'var phishtankRes = ServiceResult("UNVERIFIED", "UNKNOWN")')
    content = content.replace('var urlhausRes = ServiceResult("unknown", "UNKNOWN")', 'var urlhausRes = ServiceResult("UNVERIFIED", "UNKNOWN")')
    
    content = content.replace('it.finalUrlVerdict == "unknown"', 'it.finalUrlVerdict == "UNVERIFIED"')
    content = content.replace('it.riskLevel == "unknown"', 'it.riskLevel == "UNVERIFIED"')
    
    content = content.replace('-> "unknown"', '-> "UNVERIFIED"')
    content = content.replace('it != "unknown"', 'it != "UNVERIFIED"')
    
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'w') as f:
        f.write(content)

apply_patch()
