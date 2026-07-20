import sys
import os
import re

screens = [
    "AboutScreen.kt",
    "HistoryScreen.kt",
    "TermsScreen.kt",
    "PrivacyScreen.kt",
    "SettingsScreen.kt",
    "DisclaimerScreen.kt"
]

for screen in screens:
    path = os.path.join("app/src/main/java/com/example", screen)
    if not os.path.exists(path): continue
    with open(path, "r") as f:
        content = f.read()
    
    # We want to find:
    # val (something) = remember { Animatable(0f) }
    # LaunchedEffect(Unit) {
    #     (something).animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic)) (time can vary)
    # }
    
    pattern = re.compile(r'val\s+(\w+)\s*=\s*remember\s*\{\s*Animatable\(0f\)\s*\}\s*LaunchedEffect\(Unit\)\s*\{\s*\1\.animateTo\(1f,\s*animationSpec\s*=\s*tween\(\d+,\s*easing\s*=\s*EaseOutCubic\)\)\s*\}')
    
    def replacer(match):
        var_name = match.group(1)
        return f"""var isFirstLaunch_{var_name} by rememberSaveable {{ mutableStateOf(true) }}
    var last_{var_name} by rememberSaveable {{ mutableFloatStateOf(0f) }}
    val {var_name} = remember {{ Animatable(last_{var_name}) }}
    LaunchedEffect(isFirstLaunch_{var_name}) {{
        if (isFirstLaunch_{var_name}) {{
            isFirstLaunch_{var_name} = false
            {var_name}.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic))
        }} else {{
            {var_name}.snapTo(1f)
        }}
        last_{var_name} = 1f
    }}"""
    
    new_content = pattern.sub(replacer, content)
    
    if new_content != content:
        if "import androidx.compose.runtime.saveable.rememberSaveable" not in new_content:
            new_content = new_content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.compose.runtime.saveable.rememberSaveable")
        with open(path, "w") as f:
            f.write(new_content)
        print(f"Updated {screen}")
    else:
        print(f"Pattern not found in {screen}")
