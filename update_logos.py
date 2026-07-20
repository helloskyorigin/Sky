import re

# Update AboutScreen
with open('app/src/main/java/com/example/AboutScreen.kt', 'r') as f:
    about_content = f.read()

about_replacement = """Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .drawBehind {
                        val glowRadius = size.width * 0.7f
                        drawCircle(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6).copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = glowRadius
                            ),
                            radius = glowRadius,
                            center = center
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_logo),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }"""

about_content = re.sub(
    r'PremiumIconContainer\([\s\S]*?contentDescription = "App Icon"\s*\)',
    about_replacement,
    about_content
)

about_content = about_content.replace('Text(\n                text = "Version 1.0.0 (Build 124)"', 'Text(\n                text = "Version 1.0.0 (Build 124)\\nDeveloper: ThreatShield Team\\n© 2026 ThreatShield AI"')

with open('app/src/main/java/com/example/AboutScreen.kt', 'w') as f:
    f.write(about_content)


# Update SettingsScreen
with open('app/src/main/java/com/example/SettingsScreen.kt', 'r') as f:
    settings_content = f.read()

settings_logo_code = """
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .drawBehind {
                            val glowRadius = size.width * 0.6f
                            drawCircle(
                                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF3B82F6).copy(alpha = 0.15f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = glowRadius
                                ),
                                radius = glowRadius,
                                center = center
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_logo),
                        contentDescription = "ThreatShield AI Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }
"""

settings_content = settings_content.replace(
    "            verticalArrangement = Arrangement.spacedBy(16.dp)\n        ) {",
    "            verticalArrangement = Arrangement.spacedBy(16.dp)\n        ) {\n" + settings_logo_code
)

with open('app/src/main/java/com/example/SettingsScreen.kt', 'w') as f:
    f.write(settings_content)


# Update FeedbackDialog
with open('app/src/main/java/com/example/FeedbackDialog.kt', 'r') as f:
    feedback_content = f.read()

feedback_replacement = """Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_logo),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }"""

feedback_content = re.sub(
    r'PremiumIconContainer\(\s*icon\s*=\s*Icons\.Rounded\.Feedback[\s\S]*?contentDescription\s*=\s*"Feedback Icon"\s*\)',
    feedback_replacement,
    feedback_content
)
with open('app/src/main/java/com/example/FeedbackDialog.kt', 'w') as f:
    f.write(feedback_content)
