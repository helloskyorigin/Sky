import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()

    old_text = "detectedReasons.take(4).forEach { reason ->"
    new_text = "detectedReasons.take(4).forEachIndexed { index, reason ->"
    content = content.replace(old_text, new_text)

    old_text2 = "text = if (isDanger) \"High\" else if (isWarning || isSuspicious) \"Medium\" else \"Low\","
    new_text2 = "text = if (isDanger && index < 2) \"High\" else if (isDanger) \"Medium\" else if (isWarning || isSuspicious) \"Medium\" else \"Low\","
    content = content.replace(old_text2, new_text2)

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

apply_patch()
