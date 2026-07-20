import sys

with open("app/src/main/java/com/example/DashboardScreen.kt", "r") as f:
    content = f.read()

target = """    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "stat_count"
    )"""

replacement = """    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var lastSeenCount by rememberSaveable { mutableIntStateOf(0) }
    val animatedCountAnim = remember { Animatable(lastSeenCount, Int.VectorConverter) }
    
    LaunchedEffect(count, isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            delay(delayMs.toLong() + 100) // sync with AnimatedSecurityCard appearance
            animatedCountAnim.animateTo(count, tween(1000, easing = EaseOutCubic))
        } else {
            animatedCountAnim.animateTo(count, tween(1000, easing = EaseOutCubic))
        }
        lastSeenCount = count
    }
    val animatedCount = animatedCountAnim.value"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("Not found!")
    sys.exit(1)

with open("app/src/main/java/com/example/DashboardScreen.kt", "w") as f:
    f.write(content)
