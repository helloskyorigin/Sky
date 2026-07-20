import sys

with open("app/src/main/java/com/example/SecurityOverviewHeroCard.kt", "r") as f:
    content = f.read()

target = """    val gaugeProgress by animateFloatAsState(
        targetValue = if (isVisible) score / 100f else 0f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "gauge"
    )
    val animatedScore by animateIntAsState(
        targetValue = if (isVisible) score else 0,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "score"
    )"""

replacement = """    var lastSeenScore by rememberSaveable { mutableIntStateOf(0) }
    val animatedScoreAnim = remember { Animatable(lastSeenScore, Int.VectorConverter) }
    LaunchedEffect(score, isVisible) {
        if (isVisible) {
            animatedScoreAnim.animateTo(score, tween(1200, easing = EaseOutCubic))
            lastSeenScore = score
        }
    }
    val animatedScore = animatedScoreAnim.value

    val targetGauge = score / 100f
    var lastSeenGauge by rememberSaveable { mutableFloatStateOf(0f) }
    val animatedGaugeAnim = remember { Animatable(lastSeenGauge) }
    LaunchedEffect(targetGauge, isVisible) {
        if (isVisible) {
            animatedGaugeAnim.animateTo(targetGauge, tween(1200, easing = EaseOutCubic))
            lastSeenGauge = targetGauge
        }
    }
    val gaugeProgress = animatedGaugeAnim.value"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/SecurityOverviewHeroCard.kt", "w") as f:
    f.write(content)
