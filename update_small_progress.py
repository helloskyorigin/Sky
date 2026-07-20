import sys

with open("app/src/main/java/com/example/DashboardScreen.kt", "r") as f:
    content = f.read()

target = """    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "circular_progress"
    )"""

replacement = """    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var lastSeenProgress by rememberSaveable { mutableFloatStateOf(0f) }
    val animProgressAnim = remember { Animatable(lastSeenProgress) }
    
    LaunchedEffect(progress, isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            delay(100)
            animProgressAnim.animateTo(progress, tween(1000, easing = EaseOutCubic))
        } else {
            animProgressAnim.animateTo(progress, tween(1000, easing = EaseOutCubic))
        }
        lastSeenProgress = progress
    }
    val animProgress = animProgressAnim.value"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("Not found!")
    sys.exit(1)

with open("app/src/main/java/com/example/DashboardScreen.kt", "w") as f:
    f.write(content)
