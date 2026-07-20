import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()

    # replace Link icon with Security icon
    content = content.replace('androidx.compose.material.icons.Icons.Default.Link', 'androidx.compose.material.icons.Icons.Default.Security')

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

apply_patch()
