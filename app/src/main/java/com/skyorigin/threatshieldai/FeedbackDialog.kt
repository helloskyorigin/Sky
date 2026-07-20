package com.skyorigin.threatshieldai

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import com.google.android.play.core.review.ReviewManagerFactory

enum class FeedbackStage {
    RATING, FORM, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    viewModel: ScamLensViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialRating: Int = 0,
    forceFormStage: Boolean = false,
    preSelectedCategory: String? = null
) {
    val isDark = LocalIsDark.current
    val isHindi = false
    val context = LocalContext.current
    
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant

    // Find host activity for Google Play In-App Review
    val activity = remember(context) {
        var curr = context
        while (curr is ContextWrapper) {
            if (curr is Activity) break
            curr = curr.baseContext
        }
        curr as? Activity
    }

    var stage by remember { mutableStateOf(if (forceFormStage) FeedbackStage.FORM else FeedbackStage.RATING) }
    var rating by remember { mutableIntStateOf(initialRating) }
    
    // Form Inputs
    val categoriesEn = listOf("Bug Report", "Feature Request", "General Feedback", "Other")
    val categoriesHi = listOf("बग रिपोर्ट (Bug Report)", "सुझाव (Feature Request)", "सामान्य प्रतिक्रिया (General Feedback)", "अन्य (Other)")
    val categories = if (isHindi) categoriesHi else categoriesEn
    
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { 
        mutableStateOf(
            preSelectedCategory ?: (if (isHindi) "सामान्य प्रतिक्रिया (General Feedback)" else "General Feedback")
        ) 
    }
    
    var message by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { 
            if (!isSubmitting) {
                if (!forceFormStage) viewModel.dismissFeedbackLater()
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isSubmitting,
            dismissOnClickOutside = !isSubmitting,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, cardBorderColor),
            modifier = modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .premiumShadow(isDark, borderRadius = 28.dp)
                .testTag("feedback_dialog")
        ) {
            AnimatedContent(
                targetState = stage,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "FeedbackStageTransition"
            ) { targetStage ->
                when (targetStage) {
                    FeedbackStage.RATING -> {
                        RatingStageContent(
                            isHindi = isHindi,
                            rating = rating,
                            onRatingChange = { rating = it },
                            onLater = {
                                viewModel.dismissFeedbackLater()
                                onDismiss()
                            },
                            onSubmit = {
                                if (rating == 5) {
                                    isSubmitting = true
                                    triggerPlayStoreRating(context, activity, viewModel) {
                                        isSubmitting = false
                                        stage = FeedbackStage.SUCCESS
                                    }
                                } else {
                                    // Pre-select Category based on Rating
                                    selectedCategory = if (isHindi) {
                                        "सामान्य प्रतिक्रिया (General Feedback)"
                                    } else {
                                        "General Feedback"
                                    }
                                    stage = FeedbackStage.FORM
                                }
                            },
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                    FeedbackStage.FORM -> {
                        FormStageContent(
                            isHindi = isHindi,
                            rating = rating,
                            categories = categories,
                            selectedCategory = selectedCategory,
                            categoryExpanded = categoryExpanded,
                            onCategorySelect = { selectedCategory = it },
                            onCategoryExpandChange = { categoryExpanded = it },
                            message = message,
                            onMessageChange = { if (it.length <= 500) message = it },
                            isSubmitting = isSubmitting,
                            onBack = {
                                if (!forceFormStage) {
                                    stage = FeedbackStage.RATING
                                } else {
                                    onDismiss()
                                }
                            },
                            onSubmit = {
                                isSubmitting = true
                                viewModel.submitUserFeedback(
                                    rating = rating,
                                    category = selectedCategory,
                                    message = message
                                ) { success ->
                                    isSubmitting = false
                                    if (success) {
                                        stage = FeedbackStage.SUCCESS
                                    } else {
                                        Toast.makeText(
                                            context,
                                            if (isHindi) "प्रतिक्रिया सबमिट करने में विफल" else "Failed to submit feedback",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            cardBorderColor = cardBorderColor
                        )
                    }
                    FeedbackStage.SUCCESS -> {
                        SuccessStageContent(
                            isHindi = isHindi,
                            onDone = onDismiss,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingStageContent(
    isHindi: Boolean,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    onLater: () -> Unit,
    onSubmit: () -> Unit,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Stars,
            contentDescription = null,
            tint = PremiumColors.PrimaryAccent,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = if (isHindi) "Enjoying ThreatShield AI?" else "Enjoying ThreatShield AI?",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isHindi) "Your feedback helps us improve ThreatShield AI." else "Your feedback helps us improve ThreatShield AI.",
            style = TextStyle(
                fontSize = 14.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Star rating bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                val isSelected = i <= rating
                val scale by animateFloatAsState(targetValue = if (isSelected) 1.2f else 1.0f, label = "star_scale")
                
                Icon(
                    imageVector = if (isSelected) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "Star $i",
                    tint = if (isSelected) Color(0xFFFFB300) else textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(40.dp)
                        .scale(scale)
                        .clickable { onRatingChange(i) }
                        .testTag("star_rating_$i")
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onLater,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("feedback_later_button")
            ) {
                Text(
                    text = if (isHindi) "बाद में (Later)" else "Later",
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            }

            Button(
                onClick = onSubmit,
                enabled = rating > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremiumColors.PrimaryAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("feedback_submit_button")
            ) {
                Text(
                    text = if (rating == 5) {
                        if (isHindi) "रेट करें (Rate)" else "Rate App"
                    } else {
                        if (isHindi) "आगे बढ़ें (Next)" else "Next"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormStageContent(
    isHindi: Boolean,
    rating: Int,
    categories: List<String>,
    selectedCategory: String,
    categoryExpanded: Boolean,
    onCategorySelect: (String) -> Unit,
    onCategoryExpandChange: (Boolean) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBorderColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                enabled = !isSubmitting
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = textPrimary
                )
            }
            Text(
                text = if (isHindi) "अपनी प्रतिक्रिया साझा करें" else "Share your feedback",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selection Dropdown
        Text(
            text = if (isHindi) "श्रेणी चुनें (Select Category)" else "Select Category",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PremiumColors.PrimaryAccent
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { if (!isSubmitting) onCategoryExpandChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("feedback_category_dropdown")
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumColors.PrimaryAccent,
                    unfocusedBorderColor = cardBorderColor,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { onCategoryExpandChange(false) },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, color = textPrimary) },
                        onClick = {
                            onCategorySelect(category)
                            onCategoryExpandChange(false)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments Field
        Text(
            text = if (isHindi) "विवरण (Message)" else "Describe your experience",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PremiumColors.PrimaryAccent
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { 
                Text(
                    text = if (isHindi) "यहाँ लिखें..." else "Write here...",
                    color = textSecondary.copy(alpha = 0.6f)
                ) 
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PremiumColors.PrimaryAccent,
                unfocusedBorderColor = cardBorderColor,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = 4,
            maxLines = 6,
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("feedback_message_input")
        )

        // Character limit indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "${message.length} / 500",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = if (message.length >= 450) PremiumColors.Danger else textSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            enabled = message.isNotBlank() && !isSubmitting,
            colors = ButtonDefaults.buttonColors(
                containerColor = PremiumColors.PrimaryAccent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("feedback_submit_button")
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = if (isHindi) "सबमिट करें" else "Submit Feedback",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SuccessStageContent(
    isHindi: Boolean,
    onDone: () -> Unit,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981), // Semantic Green
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = if (isHindi) "धन्यवाद!" else "Thank You!",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isHindi) 
                "आपकी प्रतिक्रिया ThreatShield AI को बेहतर बनाने में हमारी मदद करती है।" 
                else "Your feedback helps us make ThreatShield AI even better.",
            style = TextStyle(
                fontSize = 14.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(
                containerColor = PremiumColors.PrimaryAccent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (isHindi) "ठीक है" else "Done",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun triggerPlayStoreRating(
    context: Context,
    activity: Activity?,
    viewModel: ScamLensViewModel,
    onComplete: () -> Unit
) {
    if (activity == null) {
        openPlayStoreFallback(context, viewModel, onComplete)
        return
    }

    val reviewManager = ReviewManagerFactory.create(context)
    val request = reviewManager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener { _ ->
                // Save Play Store feedback document locally and schedule sync
                viewModel.submitUserFeedback(
                    rating = 5,
                    category = "Play Store",
                    message = "In-App Play Store review successfully completed."
                ) {
                    onComplete()
                }
            }
        } else {
            openPlayStoreFallback(context, viewModel, onComplete)
        }
    }
}

private fun openPlayStoreFallback(
    context: Context,
    viewModel: ScamLensViewModel,
    onComplete: () -> Unit
) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    
    viewModel.submitUserFeedback(
        rating = 5,
        category = "Play Store Fallback",
        message = "Fallback to Play Store URL opened."
    ) {
        onComplete()
    }
}
