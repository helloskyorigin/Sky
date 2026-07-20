import sys

with open("app/src/main/java/com/example/AnalysisResultScreen.kt", "r") as f:
    content = f.read()

target = """    // Animations
    val enterAnim = remember { Animatable(0f) }
    var animatedScore by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        enterAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        val targetScore = analysis.score
        if (targetScore > 0) {
            val stepTime = if (targetScore > 50) 10L else 20L
            for (i in 1..targetScore) {
                animatedScore = i
                delay(stepTime)
            }
        }
    }"""

# Remove whitespace before comparing
import re
target_stripped = re.sub(r'\s+', '', target)
content_stripped = re.sub(r'\s+', '', content)
if target_stripped not in content_stripped:
    print("Not found even stripped!")
else:
    print("Found! Using regex replacement.")
    # Match the block
    pattern = r"// Animations\s*val enterAnim = remember { Animatable\(0f\) }\s*var animatedScore by remember { mutableStateOf\(0\) }\s*LaunchedEffect\(Unit\) \{\s*enterAnim\.animateTo\(1f, animationSpec = tween\(600, easing = EaseOutCubic\)\)\s*val targetScore = analysis\.score\s*if \(targetScore > 0\) \{\s*val stepTime = if \(targetScore > 50\) 10L else 20L\s*for \(i in 1\.\.targetScore\) \{\s*animatedScore = i\s*delay\(stepTime\)\s*\}\s*\}\s*\}"
    
    replacement = """    // Animations
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var lastAlpha by rememberSaveable { mutableFloatStateOf(0f) }
    val enterAnim = remember { Animatable(lastAlpha) }
    
    var lastSeenScore by rememberSaveable { mutableIntStateOf(0) }
    val animatedScoreAnim = remember { Animatable(lastSeenScore, Int.VectorConverter) }
    
    LaunchedEffect(analysis.score, isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            launch { enterAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic)) }
            launch {
                delay(300) // slight delay before score animates
                animatedScoreAnim.animateTo(analysis.score, tween(1200, easing = EaseOutCubic))
            }
        } else {
            enterAnim.snapTo(1f)
            animatedScoreAnim.animateTo(analysis.score, tween(1200, easing = EaseOutCubic))
        }
        lastAlpha = 1f
        lastSeenScore = analysis.score
    }
    
    val animatedScore = animatedScoreAnim.value"""

    content = re.sub(pattern, replacement, content)
    
    if "import androidx.compose.runtime.saveable.rememberSaveable" not in content:
        content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.compose.runtime.saveable.rememberSaveable")

    with open("app/src/main/java/com/example/AnalysisResultScreen.kt", "w") as f:
        f.write(content)

