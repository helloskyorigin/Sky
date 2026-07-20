import sys

with open("app/src/main/java/com/example/DashboardScreen.kt", "r") as f:
    content = f.read()

target = """    // Dynamic animated score progress bar
    val scoreAnimatedValue by animateFloatAsState(
        targetValue = scoreVal / 100f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "score_progress"
    )"""

replacement = """    // Dynamic animated score progress bar
    val targetScoreProgress = scoreVal / 100f
    var isFirstLaunchScore by rememberSaveable { mutableStateOf(true) }
    var lastSeenScoreProgress by rememberSaveable { mutableFloatStateOf(0f) }
    val scoreAnimatedValueAnim = remember { Animatable(lastSeenScoreProgress) }
    
    LaunchedEffect(targetScoreProgress, isFirstLaunchScore) {
        if (isFirstLaunchScore) {
            isFirstLaunchScore = false
            delay(100)
            scoreAnimatedValueAnim.animateTo(targetScoreProgress, tween(1000, easing = EaseOutCubic))
        } else {
            scoreAnimatedValueAnim.animateTo(targetScoreProgress, tween(1000, easing = EaseOutCubic))
        }
        lastSeenScoreProgress = targetScoreProgress
    }
    val scoreAnimatedValue = scoreAnimatedValueAnim.value"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("Not found!")
    sys.exit(1)

with open("app/src/main/java/com/example/DashboardScreen.kt", "w") as f:
    f.write(content)
