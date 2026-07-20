import re

with open('app/src/main/java/com/example/AppWebViewScreen.kt', 'r') as f:
    content = f.read()

replacement = """androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_logo),
                            contentDescription = "ThreatShield AI Logo",
                            modifier = Modifier.androidx.compose.foundation.layout.size(32.dp).androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.androidx.compose.foundation.layout.width(12.dp))
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }"""

content = re.sub(
    r'Text\(\s*text = title,\s*fontSize = 20\.sp,\s*fontWeight = FontWeight\.Bold,\s*color = textPrimary\s*\)',
    replacement,
    content
)

with open('app/src/main/java/com/example/AppWebViewScreen.kt', 'w') as f:
    f.write(content)
