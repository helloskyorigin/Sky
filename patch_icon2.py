import sys

def apply_patch():
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'r') as f:
        content = f.read()

    # replace Security icon with Rounded.Link
    content = content.replace('androidx.compose.material.icons.Icons.Default.Security', 'androidx.compose.material.icons.Icons.Rounded.Link')

    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

apply_patch()
