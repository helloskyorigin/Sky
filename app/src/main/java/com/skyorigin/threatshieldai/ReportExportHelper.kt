package com.skyorigin.threatshieldai

import android.content.ContentValues
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.toArgb
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ReportExportHelper {

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
            val words = paragraph.split(" ")
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine)
                    }
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }
        }
        return lines
    }

    fun generateReportBitmap(context: Context, analysis: MessageAnalysis, isHindi: Boolean): Bitmap {
        val width = 800
        val height = 1500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Palette definitions
        val bgPaint = Paint().apply { color = 0xFF0F172A.toInt(); style = Paint.Style.FILL }
        val cardBgPaint = Paint().apply { color = 0xFF1E293B.toInt(); style = Paint.Style.FILL }
        val cardBorderPaint = Paint().apply {
            color = 0xFF334155.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        // Color markers
        val dangerColor = 0xFFEF4444.toInt()
        val warningColor = 0xFFF59E0B.toInt()
        val successColor = 0xFF10B981.toInt()
        val accentColor = 0xFF3B82F6.toInt()

        val statusColor = when (analysis.status) {
            "Danger" -> dangerColor
            "Suspicious" -> warningColor
            else -> successColor
        }

        // Draw solid background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 1. HEADER (Branding)
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subTitlePaint = Paint().apply {
            color = 0xFF94A3B8.toInt()
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            letterSpacing = 0.1f
        }

        // Draw custom shield icon
        val shieldPaint = Paint().apply {
            color = statusColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val path = android.graphics.Path()
        path.moveTo(60f, 40f)
        path.lineTo(90f, 25f)
        path.lineTo(120f, 40f)
        path.lineTo(120f, 75f)
        path.quadTo(90f, 105f, 60f, 115f)
        path.quadTo(30f, 105f, 0f, 75f)
        path.lineTo(0f, 40f)
        path.close()

        canvas.save()
        canvas.translate(40f, 30f)
        canvas.scale(0.45f, 0.45f)
        canvas.drawPath(path, shieldPaint)
        canvas.restore()

        // Shield check mark in contrast
        val checkPaint = Paint().apply {
            color = 0xFF0F172A.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        canvas.drawLine(70f, 54f, 77f, 61f, checkPaint)
        canvas.drawLine(77f, 61f, 88f, 46f, checkPaint)

        canvas.drawText("ThreatShield AI", 110f, 58f, titlePaint)
        canvas.drawText(
            "THREAT EVALUATION & SAFETY REPORT",
            110f, 80f, subTitlePaint
        )

        // Divider
        val divPaint = Paint().apply { color = 0xFF1E293B.toInt(); strokeWidth = 2f }
        canvas.drawLine(40f, 105f, 760f, 105f, divPaint)

        // 2. HERO STATUS CARD (Large Risk Meter)
        val cardRect1 = RectF(40f, 125f, 760f, 440f)
        canvas.drawRoundRect(cardRect1, 24f, 24f, cardBgPaint)
        canvas.drawRoundRect(cardRect1, 24f, 24f, cardBorderPaint)

        val sectionTitlePaint = Paint().apply {
            color = 0xFF94A3B8.toInt()
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.08f
        }
        canvas.drawText(
            "OVERALL RISK LEVEL",
            70f, 165f, sectionTitlePaint
        )

        // Draw Gauge
        val gaugePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        val gaugeRect = RectF(120f, 200f, 280f, 360f)
        
        // Track
        gaugePaint.color = 0xFF1F2937.toInt()
        gaugePaint.strokeWidth = 14f
        canvas.drawArc(gaugeRect, 150f, 240f, false, gaugePaint)

        // Progress
        gaugePaint.color = statusColor
        gaugePaint.strokeWidth = 18f
        val progressSweep = 240f * (analysis.score.toFloat() / 100f)
        canvas.drawArc(gaugeRect, 150f, progressSweep, false, gaugePaint)

        // Score text inside gauge
        val scoreTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val scoreLabelPaint = Paint().apply {
            color = 0xFF94A3B8.toInt()
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${analysis.score}", 200f, 275f, scoreTextPaint)
        canvas.drawText("/100", 200f, 305f, scoreLabelPaint)

        // Status Banner right side
        val verdictHeaderPaint = Paint().apply {
            color = statusColor
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val verdictDescPaint = Paint().apply {
            color = Color.WHITE
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        canvas.drawText(analysis.getRiskLevelLabel(isHindi).uppercase(), 330f, 225f, verdictHeaderPaint)
        
        val typeLabel = "Threat Type: "
        canvas.drawText(typeLabel + (if (analysis.scamType.isNotEmpty()) analysis.scamType else "Unspecified"), 330f, 265f, verdictDescPaint)

        // AI Confidence Badge
        val certVal = if (analysis.confidence > 0) analysis.confidence else analysis.score
        val certText = "${certVal}% AI Certainty"
        val badgeBgPaint = Paint().apply { color = 0xFF0F172A.toInt(); style = Paint.Style.FILL }
        val badgeBorderPaint = Paint().apply { color = 0xFF334155.toInt(); style = Paint.Style.STROKE; strokeWidth = 1f; isAntiAlias = true }
        val badgeTextPaint = Paint().apply { color = 0xFF3B82F6.toInt(); textSize = 12f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); isAntiAlias = true }
        
        val badgeRect = RectF(330f, 290f, 630f, 330f)
        canvas.drawRoundRect(badgeRect, 10f, 10f, badgeBgPaint)
        canvas.drawRoundRect(badgeRect, 10f, 10f, badgeBorderPaint)
        canvas.drawText(certText, 350f, 315f, badgeTextPaint)

        // 3. AI SUMMARY CARD
        val cardRect2 = RectF(40f, 460f, 760f, 630f)
        canvas.drawRoundRect(cardRect2, 20f, 20f, cardBgPaint)
        canvas.drawRoundRect(cardRect2, 20f, 20f, cardBorderPaint)

        canvas.drawText(
            "AI THREAT SUMMARY",
            70f, 500f, sectionTitlePaint
        )

        val bodyTextPaint = Paint().apply {
            color = 0xFFE2E8F0.toInt()
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val summaryLines = wrapText(analysis.getLocalizedSummary(isHindi), bodyTextPaint, 660f)
        var textY = 535f
        for (line in summaryLines.take(3)) {
            canvas.drawText(line, 70f, textY, bodyTextPaint)
            textY += 26f
        }

        // 4. RISK DIMENSIONS (Progress Bars)
        val cardRect3 = RectF(40f, 650f, 760f, 960f)
        canvas.drawRoundRect(cardRect3, 20f, 20f, cardBgPaint)
        canvas.drawRoundRect(cardRect3, 20f, 20f, cardBorderPaint)

        canvas.drawText(
            "RISK DIMENSION BREAKDOWN",
            70f, 690f, sectionTitlePaint
        )

        // Risk factors calculation (simulated to match display)
        val riskFactors = calculateRiskFactorsForExport(analysis.text, analysis.score)
        var barY = 730f
        
        val barBgPaint = Paint().apply { color = 0xFF0F172A.toInt(); style = Paint.Style.FILL }
        val factorLabelPaint = Paint().apply {
            color = 0xFFCBD5E1.toInt()
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val factorValPaint = Paint().apply {
            color = Color.WHITE
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        riskFactors.forEach { factor ->
            canvas.drawText(factor.name, 70f, barY, factorLabelPaint)
            canvas.drawText("${factor.percentage}%", 690f, barY, factorValPaint)
            
            // Draw progress bar
            val barRectBg = RectF(70f, barY + 10f, 710f, barY + 18f)
            canvas.drawRoundRect(barRectBg, 4f, 4f, barBgPaint)

            val progressWidth = 640f * (factor.percentage / 100f)
            val barRectFg = RectF(70f, barY + 10f, 70f + progressWidth, barY + 18f)
            val fgPaint = Paint().apply {
                color = factor.color.toArgb()
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawRoundRect(barRectFg, 4f, 4f, fgPaint)

            barY += 45f
        }

        // 5. RED FLAGS
        val cardRect4 = RectF(40f, 980f, 760f, 1180f)
        canvas.drawRoundRect(cardRect4, 20f, 20f, cardBgPaint)
        canvas.drawRoundRect(cardRect4, 20f, 20f, cardBorderPaint)

        canvas.drawText(
            "KEY RED FLAGS",
            70f, 1020f, sectionTitlePaint
        )

        val bulletPaint = Paint().apply {
            color = statusColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val reasons = analysis.reasons.ifEmpty {
            listOf("Suspicious sender activity")
        }
        var reasonY = 1055f
        reasons.take(3).forEach { reason ->
            canvas.drawCircle(80f, reasonY - 5f, 4f, bulletPaint)
            val wrappedReason = wrapText(reason, bodyTextPaint, 630f)
            if (wrappedReason.isNotEmpty()) {
                canvas.drawText(wrappedReason[0], 100f, reasonY, bodyTextPaint)
            }
            reasonY += 32f
        }

        // 6. RECOMMENDED PRECAUTIONS
        val cardRect5 = RectF(40f, 1200f, 760f, 1420f)
        canvas.drawRoundRect(cardRect5, 20f, 20f, cardBgPaint)
        canvas.drawRoundRect(cardRect5, 20f, 20f, cardBorderPaint)

        canvas.drawText(
            "RECOMMENDED PRECAUTIONS",
            70f, 1240f, sectionTitlePaint
        )

        val steps = analysis.getLocalAdvice(isHindi)
        var stepY = 1275f
        
        steps.take(3).forEachIndexed { idx, step ->
            // Draw numbered badge
            val badgeBg = Paint().apply { color = 0xFF10B981.toInt(); style = Paint.Style.FILL; isAntiAlias = true }
            val badgeText = Paint().apply { color = Color.WHITE; textSize = 11f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); isAntiAlias = true; textAlign = Paint.Align.CENTER }
            
            canvas.drawCircle(85f, stepY - 5f, 12f, badgeBg)
            canvas.drawText("${idx + 1}", 85f, stepY - 1f, badgeText)

            val stepWrapped = wrapText(step, bodyTextPaint, 610f)
            if (stepWrapped.isNotEmpty()) {
                canvas.drawText(stepWrapped[0], 115f, stepY, bodyTextPaint)
            }
            stepY += 42f
        }

        // Footer App Branding
        val footerPaint = Paint().apply {
            color = 0xFF64748B.toInt()
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            if (isHindi) "Powered by ThreatShield AI • सतर्क रहें, सुरक्षित रहें" else "Powered by ThreatShield AI • Stay Vigilant, Stay Safe",
            width / 2f, height - 30f, footerPaint
        )

        return bitmap
    }

    fun shareReportAsImage(context: Context, analysis: MessageAnalysis, isHindi: Boolean) {
        try {
            val bitmap = generateReportBitmap(context, analysis, isHindi)
            val cachePath = File(context.cacheDir, "reports")
            cachePath.mkdirs()
            
            val file = File(cachePath, "threatshield_report_${analysis.timestamp}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(context, "com.skyorigin.threatshieldai.fileprovider", file)

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                }
                val chooser = Intent.createChooser(shareIntent, if (isHindi) "Threat Report Share करें" else "Share Threat Report").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share report: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareReportAsPdf(context: Context, analysis: MessageAnalysis, isHindi: Boolean) {
        try {
            val cachePath = File(context.cacheDir, "reports")
            cachePath.mkdirs()
            
            // Delete old reports in cache to prevent storage build-up and stale file access
            val existingFiles = cachePath.listFiles()
            if (existingFiles != null) {
                for (f in existingFiles) {
                    try {
                        f.delete()
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }

            // Generate totally unique filename to completely bypass any potential Android OS URI/File caching
            val file = File(cachePath, "threatshield_report_${analysis.timestamp}_${System.currentTimeMillis()}.pdf")
            
            // Generate the fresh PDF report using the unified builder function writePdfReportToStream!
            FileOutputStream(file).use { outputStream ->
                writePdfReportToStream(context, analysis, isHindi, outputStream)
            }

            val contentUri = FileProvider.getUriForFile(context, "com.skyorigin.threatshieldai.fileprovider", file)
            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, "application/pdf")
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_SUBJECT, if (isHindi) "ThreatShield AI स्कैन रिपोर्ट" else "ThreatShield AI Scan Report")
                }
                val chooser = Intent.createChooser(shareIntent, if (isHindi) "PDF Report Share करें" else "Share PDF Report").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateRiskFactorsForExport(text: String, score: Int): List<RiskFactor> {
        val lowercaseText = text.lowercase()
        val scale = score / 100f
        
        var urgency = 10
        var impersonation = 10
        var financial = 10
        var credential = 10
        var malware = 10
        
        if (lowercaseText.contains("urgent") || lowercaseText.contains("now") || lowercaseText.contains("quick") || 
            lowercaseText.contains("immediate") || lowercaseText.contains("within") || lowercaseText.contains("block") || 
            lowercaseText.contains("suspend") || lowercaseText.contains("cancel") || lowercaseText.contains("expire") || 
            lowercaseText.contains("soon") || lowercaseText.contains("fast") || lowercaseText.contains("action")) {
            urgency += 50
        }
        
        if (lowercaseText.contains("bank") || lowercaseText.contains("sbi") || lowercaseText.contains("hdfc") || 
            lowercaseText.contains("icici") || lowercaseText.contains("amazon") || lowercaseText.contains("flipkart") || 
            lowercaseText.contains("kyc") || lowercaseText.contains("delivery") || lowercaseText.contains("support")) {
            impersonation += 50
        }
        
        if (lowercaseText.contains("pay") || lowercaseText.contains("money") || lowercaseText.contains("rs") || 
            lowercaseText.contains("inr") || lowercaseText.contains("card") || lowercaseText.contains("wallet") || 
            lowercaseText.contains("upi") || lowercaseText.contains("payment") || lowercaseText.contains("transfer") || 
            lowercaseText.contains("prize") || lowercaseText.contains("reward") || lowercaseText.contains("win")) {
            financial += 55
        }
        
        if (lowercaseText.contains("otp") || lowercaseText.contains("pin") || lowercaseText.contains("password") || 
            lowercaseText.contains("login") || lowercaseText.contains("verify") || lowercaseText.contains("verification") || 
            lowercaseText.contains("credential") || lowercaseText.contains("pan") || lowercaseText.contains("aadhar")) {
            credential += 60
        }
        
        if (lowercaseText.contains("http") || lowercaseText.contains("link") || lowercaseText.contains("bit.ly") || 
            lowercaseText.contains("tinyurl") || lowercaseText.contains("apk") || lowercaseText.contains("install") || 
            lowercaseText.contains("download") || lowercaseText.contains("app")) {
            malware += 50
        }
        
        val finalUrgency = ((urgency * scale) + (score * 0.2f)).coerceIn(5f, 100f).toInt()
        val finalImpersonation = ((impersonation * scale) + (score * 0.15f)).coerceIn(5f, 100f).toInt()
        val finalFinancial = ((financial * scale) + (score * 0.15f)).coerceIn(5f, 100f).toInt()
        val finalCredential = ((credential * scale) + (score * 0.2f)).coerceIn(5f, 100f).toInt()
        val finalMalware = ((malware * scale) + (score * 0.2f)).coerceIn(5f, 100f).toInt()
        
        val dangerColor = 0xFFEF4444.toInt()
        val warningColor = 0xFFF59E0B.toInt()
        val successColor = 0xFF10B981.toInt()
        
        fun getColorForScore(factorScore: Int): ComposeColor {
            return when {
                factorScore >= 70 -> ComposeColor(0xFFEF4444)
                factorScore >= 40 -> ComposeColor(0xFFF59E0B)
                else -> ComposeColor(0xFF10B981)
            }
        }
        
        return listOf(
            RiskFactor("Urgency & Pressure", finalUrgency, getColorForScore(finalUrgency)),
            RiskFactor("Identity Impersonation", finalImpersonation, getColorForScore(finalImpersonation)),
            RiskFactor("Financial Request", finalFinancial, getColorForScore(finalFinancial)),
            RiskFactor("Credential Theft", finalCredential, getColorForScore(finalCredential)),
            RiskFactor("Malware & Link Risk", finalMalware, getColorForScore(finalMalware))
        )
    }

    fun savePdfReportLocally(context: Context, analysis: MessageAnalysis, isHindi: Boolean, onComplete: (Boolean, String?) -> Unit) {
        try {
            val pdfDocument = PdfDocument()
            var pageNum = 1
            var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNum).create())
            var canvas = page.canvas
            
            // Draw initial background
            canvas.drawColor(Color.WHITE)
            
            // Paints
            val primaryColor = 0xFF1E3A8A.toInt() // Navy Blue
            val charcoalColor = 0xFF1E293B.toInt()
            val greyColor = 0xFF64748B.toInt()
            val lightGreyColor = 0xFFE5E7EB.toInt()
            
            val titlePaint = Paint().apply {
                color = primaryColor
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val subtitlePaint = Paint().apply {
                color = greyColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            
            val headingPaint = Paint().apply {
                color = primaryColor
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val boldTextPaint = Paint().apply {
                color = charcoalColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val normalTextPaint = Paint().apply {
                color = charcoalColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            
            val infoLabelPaint = Paint().apply {
                color = greyColor
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            
            val infoValPaint = Paint().apply {
                color = charcoalColor
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            var currentY = 50f
            
            fun drawFooter(canv: Canvas, pNum: Int) {
                val footerPaint = Paint().apply {
                    color = 0xFF9CA3AF.toInt()
                    textSize = 8f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                canv.drawText("Generated by ThreatShield AI  |  Page $pNum", 297.5f, 820f, footerPaint)
            }

            // Helper to draw header
            fun drawPageHeaderAndFooter(canv: Canvas, pNum: Int) {
                // We draw header branding on every page!
                canv.drawText("ThreatShield AI", 40f, 40f, titlePaint)
                canv.drawText("SECURITY THREAT EVALUATION & AI REPORT", 40f, 55f, subtitlePaint)
                val linePaint = Paint().apply { color = lightGreyColor; strokeWidth = 1f }
                canv.drawLine(40f, 65f, 555f, 65f, linePaint)
            }
            
            fun checkPageOverflow(requiredHeight: Float) {
                if (currentY + requiredHeight > 790f) {
                    // Finish current page
                    drawFooter(canvas, pageNum)
                    pdfDocument.finishPage(page)
                    
                    // Start new page
                    pageNum++
                    page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNum).create())
                    canvas = page.canvas
                    canvas.drawColor(Color.WHITE)
                    
                    // Draw header on new page
                    drawPageHeaderAndFooter(canvas, pageNum)
                    currentY = 85f // Reset currentY below header
                }
            }

            // Draw header on first page
            drawPageHeaderAndFooter(canvas, pageNum)
            currentY = 85f
            
            // 1. Metadata Section (Clean 2-column key-value)
            checkPageOverflow(60f)
            currentY += 15f
            
            val formattedDate = try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                sdf.format(java.util.Date(analysis.timestamp))
            } catch (e: Exception) {
                "Unknown"
            }
            
            // Col 1
            canvas.drawText("Scan Date:", 40f, currentY, infoLabelPaint)
            canvas.drawText(formattedDate, 110f, currentY, infoValPaint)
            
            // Col 2
            canvas.drawText("Confidence:", 320f, currentY, infoLabelPaint)
            canvas.drawText(analysis.getConfidenceLabel(isHindi), 400f, currentY, infoValPaint)
            
            currentY += 16f
            
            // Col 1
            canvas.drawText("Threat Level:", 40f, currentY, infoLabelPaint)
            val levelLabel = analysis.getRiskLevelLabel(isHindi)
            canvas.drawText(levelLabel, 110f, currentY, infoValPaint)
            
            // Col 2
            canvas.drawText("Scan Depth:", 320f, currentY, infoLabelPaint)
            canvas.drawText("Heuristic + AI Deep Scan", 400f, currentY, infoValPaint)
            
            currentY += 20f
            
            // 2. Verdict Banner Block
            checkPageOverflow(65f)
            currentY += 10f
            
            val isDanger = analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe"
            val isSuspicious = analysis.status.lowercase() == "warning" || analysis.status.lowercase() == "suspicious"
            
            val (bannerBg, bannerBorder, bannerTextColor) = when {
                isDanger -> Triple(0xFFFEE2E2.toInt(), 0xFFEF4444.toInt(), 0xFF991B1B.toInt())
                isSuspicious -> Triple(0xFFFEF3C7.toInt(), 0xFFF59E0B.toInt(), 0xFF92400E.toInt())
                else -> Triple(0xFFD1FAE5.toInt(), 0xFF10B981.toInt(), 0xFF065F46.toInt())
            }
            
            val bannerRect = RectF(40f, currentY, 555f, currentY + 40f)
            val bannerBgPaint = Paint().apply { color = bannerBg; style = Paint.Style.FILL }
            val bannerBorderPaint = Paint().apply { color = bannerBorder; style = Paint.Style.STROKE; strokeWidth = 1.5f; isAntiAlias = true }
            val bannerTextPaint = Paint().apply {
                color = bannerTextColor
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            
            canvas.drawRoundRect(bannerRect, 8f, 8f, bannerBgPaint)
            canvas.drawRoundRect(bannerRect, 8f, 8f, bannerBorderPaint)
            
            val verdictStr = if (isHindi) "Verdict: ${analysis.getLocalizedStatus(isHindi)}" else "Verdict: ${analysis.status.uppercase()}"
            canvas.drawText("$verdictStr   |   Risk Score: ${analysis.score}/100", bannerRect.centerX(), bannerRect.centerY() + 4f, bannerTextPaint)
            
            currentY += 55f
            
            // 3. Scanned Message Card
            val wrappedMessageLines = wrapText(analysis.text, normalTextPaint, 490f)
            val cardHeight = wrappedMessageLines.size * 14f + 25f
            
            checkPageOverflow(cardHeight + 25f)
            
            canvas.drawText("Scanned Message", 40f, currentY, headingPaint)
            currentY += 10f
            
            val msgCardRect = RectF(40f, currentY, 555f, currentY + cardHeight)
            val cardPaint = Paint().apply { color = 0xFFF8FAFC.toInt(); style = Paint.Style.FILL }
            val cardBorder = Paint().apply { color = 0xFFE2E8F0.toInt(); style = Paint.Style.STROKE; strokeWidth = 1f; isAntiAlias = true }
            
            canvas.drawRoundRect(msgCardRect, 8f, 8f, cardPaint)
            canvas.drawRoundRect(msgCardRect, 8f, 8f, cardBorder)
            
            var msgTextY = currentY + 18f
            wrappedMessageLines.forEach { line ->
                canvas.drawText(line, 52f, msgTextY, normalTextPaint)
                msgTextY += 14f
            }
            
            currentY += cardHeight + 20f
            
            // 4. Detected URL Analysis
            if (analysis.links.isNotEmpty()) {
                val parsedUrls = mutableListOf<ParsedUrlStatus>()
                analysis.urlStatuses.forEach { statusStr ->
                    if (!statusStr.startsWith("METADATA:")) {
                        val parsed = parseUrlStatus(statusStr, false)
                        if (parsed != null) {
                            parsedUrls.add(parsed)
                        }
                    }
                }
                
                checkPageOverflow(40f)
                canvas.drawText("Link Security Analysis", 40f, currentY, headingPaint)
                currentY += 10f
                
                if (parsedUrls.isEmpty()) {
                    checkPageOverflow(30f)
                    canvas.drawText("• Detected Link: ${analysis.links.first()}", 50f, currentY, boldTextPaint)
                    currentY += 14f
                    canvas.drawText("  Verdict: Dangerous / High Risk scam link detected.", 50f, currentY, normalTextPaint)
                    currentY += 20f
                } else {
                    parsedUrls.forEach { parsed ->
                        val urlSectionHeight = 85f
                        checkPageOverflow(urlSectionHeight)
                        
                        val linkCardRect = RectF(40f, currentY, 555f, currentY + 70f)
                        canvas.drawRoundRect(linkCardRect, 8f, 8f, cardPaint)
                        canvas.drawRoundRect(linkCardRect, 8f, 8f, cardBorder)
                        
                        val linkY = currentY + 15f
                        // URL
                        canvas.drawText("Link:", 50f, linkY, boldTextPaint)
                        val shortUrl = if (parsed.originalUrl.length > 65) parsed.originalUrl.take(62) + "..." else parsed.originalUrl
                        canvas.drawText(shortUrl, 110f, linkY, normalTextPaint)
                        
                        val gridY = linkY + 14f
                        canvas.drawText("Web Reputation:", 50f, gridY, infoLabelPaint)
                        val wrText = if (parsed.webRiskStatus.uppercase() in listOf("FAILED", "TIMEOUT")) "FAILED" else parsed.webRiskVerdict.uppercase()
                        canvas.drawText(wrText, 145f, gridY, infoValPaint)
                        
                        canvas.drawText("PhishTank DB:", 300f, gridY, infoLabelPaint)
                        val ptText = if (parsed.phishtankStatus.uppercase() in listOf("FAILED", "TIMEOUT")) "FAILED" else parsed.phishtankVerdict.uppercase()
                        canvas.drawText(ptText, 395f, gridY, infoValPaint)
                        
                        val gridY2 = gridY + 14f
                        canvas.drawText("URLhaus Feed:", 50f, gridY2, infoLabelPaint)
                        val uhText = if (parsed.urlhausStatus.uppercase() in listOf("FAILED", "TIMEOUT")) "FAILED" else parsed.urlhausVerdict.uppercase()
                        canvas.drawText(uhText, 145f, gridY2, infoValPaint)
                        
                        canvas.drawText("Link Verdict:", 300f, gridY2, infoLabelPaint)
                        val parsedRisk = when (parsed.riskLevel.lowercase()) {
                            "danger", "unsafe" -> "DANGEROUS"
                            "suspicious", "warning" -> "SUSPICIOUS"
                            else -> "SAFE"
                        }
                        val finalLinkPaint = Paint(infoValPaint).apply {
                            color = when (parsedRisk) {
                                "DANGEROUS" -> 0xFFEF4444.toInt()
                                "SUSPICIOUS" -> 0xFFF59E0B.toInt()
                                else -> 0xFF10B981.toInt()
                            }
                        }
                        canvas.drawText(parsedRisk, 395f, gridY2, finalLinkPaint)
                        
                        currentY += 85f
                    }
                }
            }
            
            // 5. Threat Indicators Section
            val reasonsList = analysis.reasons.ifEmpty { analysis.signals }.ifEmpty { listOf("Unusual message pattern") }
            checkPageOverflow(30f + reasonsList.size * 18f)
            
            canvas.drawText("Threat Indicators & Flags", 40f, currentY, headingPaint)
            currentY += 12f
            
            val flagBulletPaint = Paint().apply {
                color = if (isDanger) 0xFFEF4444.toInt() else if (isSuspicious) 0xFFF59E0B.toInt() else 0xFF10B981.toInt()
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            
            reasonsList.forEach { reason ->
                canvas.drawCircle(50f, currentY - 3f, 3f, flagBulletPaint)
                canvas.drawText(reason, 60f, currentY, normalTextPaint)
                currentY += 16f
            }
            
            currentY += 12f
            
            // 6. Recommended Precautions
            val adviceList = analysis.getLocalAdvice(isHindi)
            checkPageOverflow(30f + adviceList.size * 18f)
            
            canvas.drawText("Recommended Precautions", 40f, currentY, headingPaint)
            currentY += 12f
            
            val checkmarkPaint = Paint().apply {
                color = 0xFF10B981.toInt()
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            adviceList.forEach { advice ->
                canvas.drawText("✓", 48f, currentY, checkmarkPaint)
                canvas.drawText(advice, 60f, currentY, normalTextPaint)
                currentY += 16f
            }
            
            // Draw last page's footer
            drawFooter(canvas, pageNum)
            
            pdfDocument.finishPage(page)
            
            // Save the document to Downloads using MediaStore or file storage
            val filename = "ThreatShield_Scan_Report_${System.currentTimeMillis()}.pdf"
            var success = false
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    val resolverStream = context.contentResolver.openOutputStream(uri)
                    if (resolverStream != null) {
                        pdfDocument.writeTo(resolverStream)
                        resolverStream.close()
                        success = true
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                downloadsDir.mkdirs()
                val file = File(downloadsDir, filename)
                val outputStream = FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                outputStream.close()
                success = true
            }
            
            pdfDocument.close()
            
            if (success) {
                onComplete(true, "PDF saved successfully.")
            } else {
                onComplete(false, "Storage access failed.")
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false, e.localizedMessage ?: "Unknown error occurred.")
        }
    }
    
    fun writePdfReportToStream(context: Context, analysis: MessageAnalysis, isHindi: Boolean, outputStream: OutputStream) {
        try {
            val pdfDocument = PdfDocument()
            var pageNum = 1
            var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNum).create())
            var canvas = page.canvas
            
            // Draw initial background
            canvas.drawColor(Color.WHITE)
            
            // Paints
            val primaryColor = 0xFF1E3A8A.toInt() // Navy Blue
            val charcoalColor = 0xFF1E293B.toInt()
            val greyColor = 0xFF64748B.toInt()
            val lightGreyColor = 0xFFE5E7EB.toInt()
            
            val titlePaint = Paint().apply {
                color = primaryColor
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val subtitlePaint = Paint().apply {
                color = greyColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            
            val headingPaint = Paint().apply {
                color = primaryColor
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val boldTextPaint = Paint().apply {
                color = charcoalColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val normalTextPaint = Paint().apply {
                color = charcoalColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            
            val infoLabelPaint = Paint().apply {
                color = greyColor
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            
            val infoValPaint = Paint().apply {
                color = charcoalColor
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            var currentY = 50f
            
            fun drawFooter(canv: Canvas, pNum: Int) {
                val footerPaint = Paint().apply {
                    color = 0xFF9CA3AF.toInt()
                    textSize = 8f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                canv.drawText("Generated by ThreatShield AI  |  Page $pNum", 297.5f, 820f, footerPaint)
            }

            // Helper to draw header
            fun drawPageHeaderAndFooter(canv: Canvas, pNum: Int) {
                // We draw header branding on every page!
                canv.drawText("ThreatShield AI", 40f, 40f, titlePaint)
                canv.drawText("SECURITY THREAT EVALUATION & AI REPORT", 40f, 55f, subtitlePaint)
                val linePaint = Paint().apply { color = lightGreyColor; strokeWidth = 1f }
                canv.drawLine(40f, 65f, 555f, 65f, linePaint)
            }
            
            fun checkPageOverflow(requiredHeight: Float) {
                if (currentY + requiredHeight > 790f) {
                    // Finish current page
                    drawFooter(canvas, pageNum)
                    pdfDocument.finishPage(page)
                    
                    // Start new page
                    pageNum++
                    page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNum).create())
                    canvas = page.canvas
                    canvas.drawColor(Color.WHITE)
                    
                    // Draw header on new page
                    drawPageHeaderAndFooter(canvas, pageNum)
                    currentY = 85f // Reset currentY below header
                }
            }

            // Draw header on first page
            drawPageHeaderAndFooter(canvas, pageNum)
            currentY = 85f
            
            // 1. Metadata Section (Clean 2-column key-value)
            checkPageOverflow(60f)
            currentY += 15f
            
            val formattedDate = try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                sdf.format(java.util.Date(analysis.timestamp))
            } catch (e: Exception) {
                "Unknown"
            }
            
            // Col 1
            canvas.drawText("Scan Date:", 40f, currentY, infoLabelPaint)
            canvas.drawText(formattedDate, 110f, currentY, infoValPaint)
            
            // Col 2
            canvas.drawText("Confidence:", 320f, currentY, infoLabelPaint)
            canvas.drawText(analysis.getConfidenceLabel(isHindi), 400f, currentY, infoValPaint)
            
            currentY += 16f
            
            // Col 1
            canvas.drawText("Threat Level:", 40f, currentY, infoLabelPaint)
            val levelLabel = analysis.getRiskLevelLabel(isHindi)
            canvas.drawText(levelLabel, 110f, currentY, infoValPaint)
            
            // Col 2
            canvas.drawText("Scan Depth:", 320f, currentY, infoLabelPaint)
            canvas.drawText("Heuristic + AI Deep Scan", 400f, currentY, infoValPaint)
            
            currentY += 20f
            
            // 2. Verdict Banner Block
            checkPageOverflow(65f)
            currentY += 10f
            
            val isDanger = analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe"
            val isSuspicious = analysis.status.lowercase() == "warning" || analysis.status.lowercase() == "suspicious"
            
            val (bannerBg, bannerBorder, bannerTextColor) = when {
                isDanger -> Triple(0xFFFEE2E2.toInt(), 0xFFEF4444.toInt(), 0xFF991B1B.toInt())
                isSuspicious -> Triple(0xFFFEF3C7.toInt(), 0xFFF59E0B.toInt(), 0xFF92400E.toInt())
                else -> Triple(0xFFD1FAE5.toInt(), 0xFF10B981.toInt(), 0xFF065F46.toInt())
            }
            
            val bannerRect = RectF(40f, currentY, 555f, currentY + 40f)
            val bannerBgPaint = Paint().apply { color = bannerBg; style = Paint.Style.FILL }
            val bannerBorderPaint = Paint().apply { color = bannerBorder; style = Paint.Style.STROKE; strokeWidth = 1.5f; isAntiAlias = true }
            val bannerTextPaint = Paint().apply {
                color = bannerTextColor
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            
            canvas.drawRoundRect(bannerRect, 8f, 8f, bannerBgPaint)
            canvas.drawRoundRect(bannerRect, 8f, 8f, bannerBorderPaint)
            
            val verdictStr = if (isHindi) "Verdict: ${analysis.getLocalizedStatus(isHindi)}" else "Verdict: ${analysis.status.uppercase()}"
            canvas.drawText("$verdictStr   |   Risk Score: ${analysis.score}/100", bannerRect.centerX(), bannerRect.centerY() + 4f, bannerTextPaint)
            
            currentY += 55f
            
            // 3. Scanned Message Card
            val wrappedMessageLines = wrapText(analysis.text, normalTextPaint, 490f)
            val cardHeight = wrappedMessageLines.size * 14f + 25f
            
            checkPageOverflow(cardHeight + 25f)
            
            canvas.drawText("Scanned Message", 40f, currentY, headingPaint)
            currentY += 10f
            
            val msgCardRect = RectF(40f, currentY, 555f, currentY + cardHeight)
            val cardPaint = Paint().apply { color = 0xFFF8FAFC.toInt(); style = Paint.Style.FILL }
            val cardBorder = Paint().apply { color = 0xFFE2E8F0.toInt(); style = Paint.Style.STROKE; strokeWidth = 1f; isAntiAlias = true }
            
            canvas.drawRoundRect(msgCardRect, 8f, 8f, cardPaint)
            canvas.drawRoundRect(msgCardRect, 8f, 8f, cardBorder)
            
            var msgTextY = currentY + 18f
            wrappedMessageLines.forEach { line ->
                canvas.drawText(line, 52f, msgTextY, normalTextPaint)
                msgTextY += 14f
            }
            
            currentY += cardHeight + 20f
            
            // 4. Detected URL Analysis
            if (analysis.links.isNotEmpty()) {
                val parsedUrls = mutableListOf<ParsedUrlStatus>()
                analysis.urlStatuses.forEach { statusStr ->
                    if (!statusStr.startsWith("METADATA:")) {
                        val parsed = parseUrlStatus(statusStr, false)
                        if (parsed != null) {
                            parsedUrls.add(parsed)
                        }
                    }
                }
                
                checkPageOverflow(40f)
                canvas.drawText("Link Security Analysis", 40f, currentY, headingPaint)
                currentY += 10f
                
                if (parsedUrls.isEmpty()) {
                    checkPageOverflow(30f)
                    canvas.drawText("• Detected Link: ${analysis.links.first()}", 50f, currentY, boldTextPaint)
                    currentY += 14f
                    canvas.drawText("  Verdict: Dangerous / High Risk scam link detected.", 50f, currentY, normalTextPaint)
                    currentY += 20f
                } else {
                    parsedUrls.forEach { parsed ->
                        val urlSectionHeight = 85f
                        checkPageOverflow(urlSectionHeight)
                        
                        val linkCardRect = RectF(40f, currentY, 555f, currentY + 70f)
                        canvas.drawRoundRect(linkCardRect, 8f, 8f, cardPaint)
                        canvas.drawRoundRect(linkCardRect, 8f, 8f, cardBorder)
                        
                        val linkY = currentY + 15f
                        // URL
                        canvas.drawText("Link:", 50f, linkY, boldTextPaint)
                        val shortUrl = if (parsed.originalUrl.length > 65) parsed.originalUrl.take(62) + "..." else parsed.originalUrl
                        canvas.drawText(shortUrl, 110f, linkY, normalTextPaint)
                        
                        val gridY = linkY + 14f
                        canvas.drawText("Web Reputation:", 50f, gridY, infoLabelPaint)
                        val wrText = if (parsed.webRiskStatus.uppercase() in listOf("FAILED", "TIMEOUT")) "FAILED" else parsed.webRiskVerdict.uppercase()
                        canvas.drawText(wrText, 145f, gridY, infoValPaint)
                        
                        canvas.drawText("PhishTank DB:", 300f, gridY, infoLabelPaint)
                        val ptText = if (parsed.phishtankStatus.uppercase() in listOf("FAILED", "TIMEOUT")) "FAILED" else parsed.phishtankVerdict.uppercase()
                        canvas.drawText(ptText, 395f, gridY, infoValPaint)
                        
                        val gridY2 = gridY + 14f
                        canvas.drawText("URLhaus Feed:", 50f, gridY2, infoLabelPaint)
                        val uhText = if (parsed.urlhausStatus.uppercase() in listOf("FAILED", "TIMEOUT")) "FAILED" else parsed.urlhausVerdict.uppercase()
                        canvas.drawText(uhText, 145f, gridY2, infoValPaint)
                        
                        canvas.drawText("Link Verdict:", 300f, gridY2, infoLabelPaint)
                        val parsedRisk = when (parsed.riskLevel.lowercase()) {
                            "danger", "unsafe" -> "DANGEROUS"
                            "suspicious", "warning" -> "SUSPICIOUS"
                            else -> "SAFE"
                        }
                        val finalLinkPaint = Paint(infoValPaint).apply {
                            color = when (parsedRisk) {
                                "DANGEROUS" -> 0xFFEF4444.toInt()
                                "SUSPICIOUS" -> 0xFFF59E0B.toInt()
                                else -> 0xFF10B981.toInt()
                            }
                        }
                        canvas.drawText(parsedRisk, 395f, gridY2, finalLinkPaint)
                        
                        currentY += 85f
                    }
                }
            }
            
            // 5. Threat Indicators Section
            val reasonsList = analysis.reasons.ifEmpty { analysis.signals }.ifEmpty { listOf("Unusual message pattern") }
            checkPageOverflow(30f + reasonsList.size * 18f)
            
            canvas.drawText("Threat Indicators & Flags", 40f, currentY, headingPaint)
            currentY += 12f
            
            val flagBulletPaint = Paint().apply {
                color = if (isDanger) 0xFFEF4444.toInt() else if (isSuspicious) 0xFFF59E0B.toInt() else 0xFF10B981.toInt()
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            
            reasonsList.forEach { reason ->
                canvas.drawCircle(50f, currentY - 3f, 3f, flagBulletPaint)
                canvas.drawText(reason, 60f, currentY, normalTextPaint)
                currentY += 16f
            }
            
            currentY += 12f
            
            // 6. Recommended Precautions
            val adviceList = analysis.getLocalAdvice(isHindi)
            checkPageOverflow(30f + adviceList.size * 18f)
            
            canvas.drawText("Recommended Precautions", 40f, currentY, headingPaint)
            currentY += 12f
            
            val checkmarkPaint = Paint().apply {
                color = 0xFF10B981.toInt()
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            adviceList.forEach { advice ->
                canvas.drawText("✓", 48f, currentY, checkmarkPaint)
                canvas.drawText(advice, 60f, currentY, normalTextPaint)
                currentY += 16f
            }
            
            // Draw last page's footer
            drawFooter(canvas, pageNum)
            
            pdfDocument.finishPage(page)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
