import re
import os

screens = [
    'app/src/main/java/com/example/HistoryScreen.kt',
    'app/src/main/java/com/example/AnalysisResultScreen.kt',
    'app/src/main/java/com/example/DailyChallengeScreen.kt'
]

replacement = """androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_logo),
                            contentDescription = "ThreatShield AI Logo",
                            modifier = Modifier.size(24.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        \\g<0>
                    }"""

for screen in screens:
    if not os.path.exists(screen): continue
    with open(screen, 'r') as f:
        content = f.read()
    
    # We find the Text element inside title = { ... }
    # This might be tricky with regex, let's use a targeted replace for each.
    
    if 'HistoryScreen' in screen:
        content = re.sub(
            r'Text\(\s*text = if \(isHindi\) "Scan History" else "Scan History",[\s\S]*?\)',
            replacement,
            content
        )
    elif 'AnalysisResultScreen' in screen:
        content = re.sub(
            r'Text\(\s*text = if \(isHindi\) "विश्लेषण परिणाम" else "Threat Assessment",[\s\S]*?\)',
            replacement,
            content
        )
    elif 'DailyChallengeScreen' in screen:
        content = re.sub(
            r'Text\(\s*text = if \(isCompleted\) \{[\s\S]*?\},',
            replacement + ',',
            content
        )

    with open(screen, 'w') as f:
        f.write(content)

