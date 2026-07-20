import re

with open('app/src/main/java/com/example/HistoryScreen.kt', 'r') as f:
    history_content = f.read()

empty_replacement = """Box(
                        modifier = Modifier
                            .size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_notification_logo),
                            contentDescription = "Empty History Logo",
                            modifier = Modifier.fillMaxSize().alpha(0.3f),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(primaryBlue)
                        )
                    }"""

history_content = re.sub(
    r'Box\(\s*modifier = Modifier\s*\.size\(100\.dp\)\s*\.background\(primaryBlue\.copy\(alpha = 0\.05f\), shape = CircleShape\),\s*contentAlignment = Alignment\.Center\s*\)\s*\{[\s\S]*?\}\s*\}',
    empty_replacement,
    history_content
)

with open('app/src/main/java/com/example/HistoryScreen.kt', 'w') as f:
    f.write(history_content)
