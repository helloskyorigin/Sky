package com.skyorigin.threatshieldai

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecurityAnalysisEngineTest {

    private lateinit var context: Context
    private lateinit var originalClient: OkHttpClient
    private lateinit var mockInterceptor: MockInterceptor

    class MockInterceptor : Interceptor {
        var shouldFailLlama = false
        var shouldFailUrls = false
        var isCase3Test = false
        var isCase5Test = false
        var isCase6SafeTest = false
        var isCase6DangerTest = false
        var lastRequestedUrl: String? = null

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val url = request.url.toString()
            lastRequestedUrl = url

            if (url.contains("api.groq.com")) {
                if (shouldFailLlama) {
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(500)
                        .message("Internal Server Error")
                        .body("{\"error\": \"Failed\"}".toResponseBody("application/json".toMediaType()))
                        .build()
                } else if (isCase5Test) {
                    val json = """
                        {
                            "choices": [
                                {
                                    "message": {
                                        "content": "{\"classification\": \"UNABLE_TO_DETERMINE\", \"evidence_sufficiency\": \"INSUFFICIENT\", \"scam_probability\": 10, \"confidence\": 50, \"short_reason\": \"Not enough context\", \"extracted_signals\": [], \"advice\": [\"Verify sender\", \"Do not share sensitive info\"]}"
                                    }
                                }
                            ]
                        }
                    """.trimIndent()
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(json.toResponseBody("application/json".toMediaType()))
                        .build()
                } else if (isCase6SafeTest) {
                    val json = """
                        {
                            "choices": [
                                {
                                    "message": {
                                        "content": "{\"classification\": \"SAFE\", \"evidence_sufficiency\": \"SUFFICIENT\", \"scam_probability\": 5, \"confidence\": 98, \"short_reason\": \"Standard recharge confirmation\", \"extracted_signals\": [], \"advice\": [\"No action needed\"]}"
                                    }
                                }
                            ]
                        }
                    """.trimIndent()
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(json.toResponseBody("application/json".toMediaType()))
                        .build()
                } else if (isCase6DangerTest) {
                    val json = """
                        {
                            "choices": [
                                {
                                    "message": {
                                        "content": "{\"classification\": \"DANGEROUS\", \"evidence_sufficiency\": \"SUFFICIENT\", \"scam_probability\": 95, \"confidence\": 99, \"short_reason\": \"Strong OTP scam intent\", \"extracted_signals\": [\"OTP solicitation\"], \"advice\": [\"Never share OTP\"]}"
                                    }
                                }
                            ]
                        }
                    """.trimIndent()
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(json.toResponseBody("application/json".toMediaType()))
                        .build()
                } else {
                    val json = """
                        {
                            "choices": [
                                {
                                    "message": {
                                        "content": "{\"scam_probability\": 90, \"phishing_probability\": 80, \"impersonation_probability\": 70, \"urgency_score\": 95, \"financial_fraud_score\": 85, \"identity_theft_score\": 75, \"confidence\": 95, \"short_reason\": \"Highly dangerous impersonation scam\", \"extracted_signals\": [\"impersonation\", \"urgency\"], \"scam_category\": \"Phishing\", \"advice\": [\"Do not click\", \"Delete immediately\"]}"
                                    }
                                }
                            ]
                        }
                    """.trimIndent()
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(json.toResponseBody("application/json".toMediaType()))
                        .build()
                }
            }

            if (url.contains("webrisk.googleapis.com")) {
                if (shouldFailUrls) {
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(500)
                        .message("Error")
                        .body("{}".toResponseBody("application/json".toMediaType()))
                        .build()
                }
                val queryUri = request.url.queryParameter("uri") ?: ""
                val isMalicious = queryUri.contains("malicious") || url.contains("malicious")
                val json = if (isMalicious) {
                    """
                        {
                            "threat": {
                                "threatTypes": ["SOCIAL_ENGINEERING"]
                            }
                        }
                    """.trimIndent()
                } else {
                    "{}"
                }
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(json.toResponseBody("application/json".toMediaType()))
                    .build()
            }

            // Return safe default for any other requests (PhishTank, URLhaus API, etc.)
            val queryUri = request.url.queryParameter("uri") ?: ""
            val isMaliciousUrl = queryUri.contains("malicious") || url.contains("malicious")
            val isSafeUrl = isCase3Test || isCase5Test
            val code = if (isMaliciousUrl || isSafeUrl) 200 else 404

            val responseBody = if (url.contains("phishtank")) {
                if (isMaliciousUrl) {
                    "{\"results\": {\"in_database\": true, \"verified\": true, \"valid\": true}}"
                } else {
                    "{\"results\": {\"in_database\": false}}"
                }
            } else if (url.contains("urlhaus")) {
                if (isMaliciousUrl) {
                    "{\"query_status\": \"ok\", \"url_status\": \"online\", \"threat\": \"malware\"}"
                } else {
                    "{\"query_status\": \"no_results\"}"
                }
            } else {
                "{}"
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code == 200) "OK" else "Not Found")
                .body(responseBody.toResponseBody("application/json".toMediaType()))
                .build()
        }
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        originalClient = SecurityAnalysisEngine.client
        mockInterceptor = MockInterceptor()
        SecurityAnalysisEngine.client = OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()
        SecurityAnalysisEngine.testGroqKey = "dummy_test_groq_key"
        SecurityAnalysisEngine.testWebRiskKey = "dummy_test_webrisk_key"
    }

    @After
    fun tearDown() {
        SecurityAnalysisEngine.client = originalClient
        SecurityAnalysisEngine.testGroqKey = null
        SecurityAnalysisEngine.testWebRiskKey = null
    }

    // ==========================================
    // 1. SAFE MESSAGES & FALSE POSITIVE REDUCTION
    // ==========================================

    @Test
    fun testSafeBankingNotification() = runBlocking {
        // Fallback-only local context check
        mockInterceptor.shouldFailLlama = true

        val message = "Dear Customer, your a/c ending 1234 is credited with Rs 10,000 via UPI. Available balance is Rs 45,200. - SBI"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.textVerdict)
        assertTrue("Score should be very low for genuine info", result.riskScore <= 20)
    }

    @Test
    fun testSafeOtpNotification() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Your OTP for transaction at Amazon India is 984120. Valid for 5 minutes. Do not share this with anyone."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.textVerdict)
        assertTrue("Score should be very low for standard OTP notification", result.riskScore <= 20)
    }

    @Test
    fun testSafeDeliveryUpdate() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Your FedEx parcel with tracking ID 98712345 has been delivered successfully. Thank you for shipping with us!"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.textVerdict)
        assertTrue("Should be classified as Safe", result.riskScore <= 20)
    }

    @Test
    fun testSafeGovernmentAlert() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "National Electoral Officer: Please download your Voter Information Slip from the official portal."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.textVerdict)
        assertTrue(result.riskScore <= 20)
    }

    @Test
    fun testSafeShoppingConfirmation() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Thank you for shopping at Myntra. Your order has been processed and is on its way. Track it on our official app."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.textVerdict)
        assertTrue(result.riskScore <= 20)
    }

    @Test
    fun testSafeUtilityBill() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Your electricity bill for a/c 109238491 of Rs. 1,240 is generated. Please pay by the due date 25th July."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.textVerdict)
        assertTrue(result.riskScore <= 20)
    }

    // ==========================================
    // 2. SCAM MESSAGES & FALSE NEGATIVE REDUCTION
    // ==========================================

    @Test
    fun testFakeKycScam() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Dear Customer, your HDFC bank KYC has expired. Please verify and update immediately to avoid card blocking at http://fake-hdfc.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        println("DEBUG testFakeKycScam: verdict=${result.verdict}, riskScore=${result.riskScore}, textVerdict=${result.textVerdict}, urlVerdict=${result.urlVerdict}, textSignals=${result.textSignals}")

        assertEquals("Suspicious", result.verdict)
        assertEquals(45, result.riskScore)
        assertTrue(result.textSignals.contains("Fake KYC Request") || result.textSignals.contains("Bank Impersonation Fraud"))
    }

    @Test
    fun testFakeBankSuspensionScam() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "URGENT! Your SBI account has been suspended due to unauthorized login. Visit http://sbi-verify.com now to unlock."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        println("DEBUG testFakeBankSuspensionScam: verdict=${result.verdict}, riskScore=${result.riskScore}, textVerdict=${result.textVerdict}, urlVerdict=${result.urlVerdict}, textSignals=${result.textSignals}")

        assertEquals("Suspicious", result.verdict)
        assertEquals(45, result.riskScore)
        assertTrue(result.textSignals.contains("Bank Impersonation Fraud"))
    }

    @Test
    fun testRemoteAccessScam() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Urgent tech support: unauthorized access detected on your system. Download Anydesk to connect to our helpline."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Danger", result.textVerdict)
        assertTrue(result.riskScore >= 76)
        assertTrue(result.textSignals.contains("Remote Access Scam"))
    }

    @Test
    fun testLotteryScam() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Congratulations! You have won a lottery of Rs. 25 Lakhs in KBC. Contact our helpline on WhatsApp immediately to claim!"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Danger", result.textVerdict)
        assertTrue(result.riskScore >= 76)
        assertTrue(result.textSignals.contains("Lottery Scam"))
    }

    @Test
    fun testInvestmentScam() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Guaranteed 500% daily profit! Join our VIP Telegram trading channel for secure bitcoin investment now!"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Danger", result.textVerdict)
        assertTrue(result.riskScore >= 76)
        assertTrue(result.textSignals.contains("Investment Scam"))
    }

    @Test
    fun testCredentialTheftScam() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Your netbanking login credentials have expired. Please sign in immediately to update passwords at http://login-expired.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        println("DEBUG testCredentialTheftScam: verdict=${result.verdict}, riskScore=${result.riskScore}, textVerdict=${result.textVerdict}, urlVerdict=${result.urlVerdict}, textSignals=${result.textSignals}")

        assertEquals("Suspicious", result.verdict)
        assertEquals(45, result.riskScore)
        assertTrue(result.textSignals.contains("Credential Harvesting"))
    }

    // ==========================================
    // 3. URL VALIDATION & REPUTATION CHECKS
    // ==========================================

    @Test
    fun testMaliciousUrlScannedAndDetected() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "Please check this link: http://malicious-scam.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        // Verifies URL extraction, parallel scanning and reputation detection
        assertTrue(result.urlsFound.isNotEmpty())
        val urlResult = result.urlsFound.first()
        assertEquals("http://malicious-scam.com", urlResult.originalUrl)
        assertEquals("danger", urlResult.finalUrlVerdict)
    }

    @Test
    fun testTrustedDomainValidation() = runBlocking {
        assertTrue(SecurityAnalysisEngine.isTrustedDomain("https://www.google.com/search"))
        assertTrue(SecurityAnalysisEngine.isTrustedDomain("http://youtube.com/watch"))
        assertFalse(SecurityAnalysisEngine.isTrustedDomain("http://malicious-google.com"))
    }

    // ==========================================
    // 4. CONFIDENCE & DECISION CALIBRATION
    // ==========================================

    @Test
    fun testLlamaSuccessConfidenceAndDecisionCalibration() = runBlocking {
        // Verifies Llama API flow behaves correctly when online
        mockInterceptor.shouldFailLlama = false

        val message = "This is a dangerous message"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Phishing", result.scamType)
        assertEquals("Danger", result.textVerdict)
        assertTrue("Confidence should be dynamic and high", result.confidence >= 80)
    }

    @Test
    fun testDecisionConsistency() = runBlocking {
        mockInterceptor.shouldFailLlama = true

        val message = "SBI: Account suspended. Update KYC at http://sbi-kyc.com"

        // Scan multiple times
        val result1 = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        val result2 = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        // Verify output consistency and deterministic routing
        assertEquals(result1.riskScore, result2.riskScore)
        assertEquals(result1.textVerdict, result2.textVerdict)
        assertEquals(result1.scamType, result2.scamType)
    }

    // ==========================================
    // 5. MISSION FALCON — CASE 3 & NEGATIVE CONSTRAINTS
    // ==========================================

    @Test
    fun testNegativeConstraintUrgentAlone() = runBlocking {
        mockInterceptor.shouldFailLlama = true
        val message = "Urgent: Please reply as soon as possible."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        
        // Words like "urgent" alone must NOT trigger Suspicious
        assertEquals("Safe", result.verdict)
    }

    @Test
    fun testNegativeConstraintRechargeAlone() = runBlocking {
        mockInterceptor.shouldFailLlama = true
        val message = "Your monthly telecom recharge of Rs 299 was successful."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        
        // Words like "recharge" alone must NOT trigger Suspicious
        assertEquals("Safe", result.verdict)
    }

    @Test
    fun testCase3SuspiciousContextNoKnownThreatUrl() = runBlocking {
        mockInterceptor.shouldFailLlama = true
        mockInterceptor.isCase3Test = true
        
        // This is a suspicious HDFC KYC warning with a clean URL (not flagged by Web Risk)
        val message = "Dear HDFC customer, your netbanking access is temporarily suspended. Verify details at http://hdfc-safe-check.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        // Web Risk returned safe (NO_KNOWN_THREAT), but context is suspicious
        assertEquals("Suspicious", result.verdict)
        assertEquals("NO_KNOWN_THREAT", result.urlVerdict)
        
        // Verification of AI Summary (finalReason)
        assertTrue(result.finalReason.contains("flag") || result.finalReason.contains("Safe") || result.finalReason.contains("reputation") || result.finalReason.contains("clean"))
        
        // Verification of Dynamic advice
        assertTrue(result.advice.size >= 2)
        assertTrue(result.advice.any { it.contains("sender") || it.contains("Verify") })
        assertTrue(result.advice.any { it.contains("official") || it.contains("app") })
    }

    @Test
    fun testCase3HindiSuspiciousContextNoKnownThreatUrl() = runBlocking {
        mockInterceptor.shouldFailLlama = true
        mockInterceptor.isCase3Test = true
        
        // Suspicious SBI warning in Hindi with a clean URL
        val message = "प्रिय ग्राहक, आपका SBI खाता ब्लॉक कर दिया गया है। विवरण सत्यापित करें http://sbi-safe-verify.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, true)

        assertEquals("Suspicious", result.verdict)
        assertEquals("NO_KNOWN_THREAT", result.urlVerdict)
        
        // Verification of Hindi AI Summary (finalReason)
        assertTrue(result.finalReason.contains("URL") || result.finalReason.contains("सुरक्षित") || result.finalReason.contains("संदिग्ध"))
        
        // Verification of Hindi Dynamic advice
        assertTrue(result.advice.any { it.contains("sender") || it.contains("पहचान") })
        assertTrue(result.advice.any { it.contains("official") || it.contains("आधिकारिक") })
    }

    @Test
    fun testCase5InsufficientEvidence() = runBlocking {
        mockInterceptor.shouldFailLlama = false
        mockInterceptor.isCase5Test = true
        
        val message = "Check this: http://example.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        println("testCase5 result verdict: ${result.verdict}")
        println("testCase5 result urlVerdict: ${result.urlVerdict}")
        println("testCase5 finalReason: ${result.finalReason}")
        println("testCase5 riskScore: ${result.riskScore}")
        
        assertEquals("Unable to Determine", result.verdict)
        assertEquals("NO_KNOWN_THREAT", result.urlVerdict)
    }

    @Test
    fun testCase5HindiInsufficientEvidence() = runBlocking {
        mockInterceptor.shouldFailLlama = false
        mockInterceptor.isCase5Test = true
        
        val message = "यह देखें: http://example.com"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, true)

        assertEquals("Unable to Determine", result.verdict)
        assertEquals("NO_KNOWN_THREAT", result.urlVerdict)
    }

    @Test
    fun testCase6TextOnlySafe() = runBlocking {
        mockInterceptor.shouldFailLlama = false
        mockInterceptor.isCase6SafeTest = true
        
        val message = "Your recharge of ₹299 was successful."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Safe", result.verdict)
        assertEquals("No URLs", result.urlVerdict)
        assertTrue(result.finalReason.contains("recharge confirmation") || result.finalReason.contains("No malicious URL"))
    }

    @Test
    fun testCase6TextOnlyDanger() = runBlocking {
        mockInterceptor.shouldFailLlama = false
        mockInterceptor.isCase6DangerTest = true
        
        val message = "Dear customer, your account is locked. Share your 6-digit OTP to unlock now."
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)

        assertEquals("Danger", result.verdict)
        assertEquals("No URLs", result.urlVerdict)
        assertTrue(result.finalReason.contains("OTP scam") || result.finalReason.contains("High-risk scam"))
    }

    @Test
    fun testAirtelPromotionalMessageWithCleanUrl() = runBlocking {
        mockInterceptor.shouldFailLlama = false
        val message = "Badhai Ho! Ab apne Airtel recharge ke saath aapko mil raha hai movies, TV shows aur Live channels ka free access. Download karein Airtel Xstream Play aur dekhna shuru karein. open.airtelxstream.in/xOTTss"
        val result = SecurityAnalysisEngine.performHybridAnalysis(null, message, false)
        
        println("=== AIRTEL PROMOTIONAL TEST RESULT ===")
        println("1. Individual API/Security Signals: WebRisk=${result.webRiskStatus}, UrlVerdict=${result.urlVerdict}, URLs=${result.urlsFound.map { "${it.originalUrl} -> ${it.webRiskVerdict}" }}")
        println("2. LLM Risk Assessment / AI Status: ${result.aiStatus}, TextVerdict=${result.textVerdict}")
        println("3. Final Fusion Calculation: Verdict=${result.verdict}, Confidence=${result.confidence}")
        println("4. Final Risk Score: ${result.riskScore}")
        println("5. Category: ${result.scamType}")
        println("6. Generated Reasons: ${result.finalReason}, TextSignals=${result.textSignals}")

        assertEquals("Safe", result.verdict)
        assertTrue("Risk score must be low (<= 20), got ${result.riskScore}", result.riskScore <= 20)
        assertFalse("Must NOT contain Credential theft attempt", result.textSignals.contains("Credential theft attempt"))
        assertFalse("Must NOT contain Urgency tactic detected", result.textSignals.contains("Urgency tactic detected"))
        assertFalse("Must NOT contain Fake Support", result.textSignals.contains("Fake Support"))
        assertFalse("Must NOT contain Unverified domain", result.textSignals.contains("Unverified domain"))
    }

    @Test
    fun testDynamicConfidenceCalculation() = runBlocking {
        // 1. High confidence for clear safe message
        mockInterceptor.shouldFailLlama = false
        mockInterceptor.isCase6SafeTest = true
        val safeResult = SecurityAnalysisEngine.performHybridAnalysis(null, "Your recharge of ₹299 was successful.", false)
        assertTrue("Safe message confidence should be high (>85), got ${safeResult.confidence}", safeResult.confidence > 85)

        // 2. High confidence for clear danger message
        mockInterceptor.isCase6SafeTest = false
        mockInterceptor.isCase6DangerTest = true
        val dangerResult = SecurityAnalysisEngine.performHybridAnalysis(null, "Dear customer, your account is locked. Share your 6-digit OTP to unlock now.", false)
        assertTrue("Danger message confidence should be high (>85), got ${dangerResult.confidence}", dangerResult.confidence > 85)

        // 3. Lower confidence for insufficient evidence / ambiguous message
        mockInterceptor.isCase6DangerTest = false
        mockInterceptor.isCase5Test = true
        val ambiguousResult = SecurityAnalysisEngine.performHybridAnalysis(null, "Check this: http://example.com", false)
        assertTrue("Ambiguous message confidence should be lower (<= 65), got ${ambiguousResult.confidence}", ambiguousResult.confidence <= 65)
    }
}
