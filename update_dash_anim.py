import sys

with open("app/src/main/java/com/example/DashboardScreen.kt", "r") as f:
    content = f.read()

target = """    // Animations
    val contentAlpha = remember { Animatable(0f) }
    val slideUp = remember { Animatable(40f) }
    
    LaunchedEffect(Unit) {
        launch { contentAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic)) }
        launch { slideUp.animateTo(0f, animationSpec = tween(500, easing = EaseOutCubic)) }
    }"""

replacement = """    // Animations
    var isFirstLaunchDash by rememberSaveable { mutableStateOf(true) }
    var lastContentAlpha by rememberSaveable { mutableFloatStateOf(0f) }
    var lastSlideUp by rememberSaveable { mutableFloatStateOf(40f) }
    val contentAlpha = remember { Animatable(lastContentAlpha) }
    val slideUp = remember { Animatable(lastSlideUp) }
    
    LaunchedEffect(isFirstLaunchDash) {
        if (isFirstLaunchDash) {
            isFirstLaunchDash = false
            launch { contentAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic)) }
            launch { slideUp.animateTo(0f, animationSpec = tween(500, easing = EaseOutCubic)) }
        } else {
            contentAlpha.snapTo(1f)
            slideUp.snapTo(0f)
        }
        lastContentAlpha = 1f
        lastSlideUp = 0f
    }"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("Not found!")
    sys.exit(1)

with open("app/src/main/java/com/example/DashboardScreen.kt", "w") as f:
    f.write(content)
