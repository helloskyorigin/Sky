import sys

def apply_patch():
    # just an inline replacement script to avoid diff issues
    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'r') as f:
        content = f.read()

    # executeGoogleWebRisk
    content = content.replace('return ServiceResult("unknown", "UNKNOWN")', 'return ServiceResult("UNVERIFIED", "MISSING_KEY")')
    content = content.replace('ServiceResult("danger", "OK", tType)', 'ServiceResult("MALICIOUS", "OK", tType)')
    content = content.replace('ServiceResult("safe", "OK")', 'ServiceResult("NO_KNOWN_THREAT", "OK")')
    content = content.replace('ServiceResult("unknown", "TIMEOUT")', 'ServiceResult("UNVERIFIED", "TIMEOUT")')
    content = content.replace('ServiceResult("unknown", "FAILED")', 'ServiceResult("UNVERIFIED", "FAILED")')
    
    # executePhishTank
    content = content.replace('ServiceResult("danger", "OK")', 'ServiceResult("MALICIOUS", "OK")')
    content = content.replace('ServiceResult("unknown", "OK")', 'ServiceResult("NO_KNOWN_THREAT", "OK")')

    with open('app/src/main/java/com/skyorigin/threatshieldai/SecurityAnalysisEngine.kt', 'w') as f:
        f.write(content)

apply_patch()
