import os
import re

directories = ["app/src/main/java/com/example"]

icon_pattern = re.compile(r'Icon\(\s*imageVector\s*=\s*Icons\.Rounded\.Shield,\s*contentDescription\s*=\s*null,\s*tint\s*=\s*primaryBlue,\s*modifier\s*=\s*Modifier\.size\(\d+\.dp\)\s*\)')

replacement = """Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "ThreatShield AI Logo",
                            modifier = Modifier.size(24.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                        )"""

for dirpath, _, filenames in os.walk(directories[0]):
    for filename in filenames:
        if filename.endswith(".kt"):
            filepath = os.path.join(dirpath, filename)
            with open(filepath, "r") as f:
                content = f.read()
            
            new_content = icon_pattern.sub(replacement, content)
            
            if new_content != content:
                print(f"Updated {filepath}")
                # We need to add painterResource import if not present
                if "import androidx.compose.ui.res.painterResource" not in new_content:
                    new_content = new_content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.res.painterResource")
                with open(filepath, "w") as f:
                    f.write(new_content)
