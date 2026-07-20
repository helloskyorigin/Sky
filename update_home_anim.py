import sys

with open("app/src/main/java/com/example/HomeScreen.kt", "r") as f:
    content = f.read()

target = """    // Animations
    val fadeAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
    }"""

replacement = """    // Animations
    var isFirstLaunchHome by rememberSaveable { mutableStateOf(true) }
    var lastFade by rememberSaveable { mutableFloatStateOf(0f) }
    val fadeAnim = remember { Animatable(lastFade) }
    LaunchedEffect(isFirstLaunchHome) {
        if (isFirstLaunchHome) {
            isFirstLaunchHome = false
            fadeAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        } else {
            fadeAnim.snapTo(1f)
        }
        lastFade = 1f
    }"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("Not found!")
    sys.exit(1)

if "import androidx.compose.runtime.saveable.rememberSaveable" not in content:
    content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.compose.runtime.saveable.rememberSaveable")

with open("app/src/main/java/com/example/HomeScreen.kt", "w") as f:
    f.write(content)
