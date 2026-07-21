package com.skyorigin.threatshieldai

import java.util.Calendar

object ScamChallengeGenerator {

    private val BANKS = listOf("SBI", "HDFC Bank", "ICICI Bank", "Axis Bank", "Punjab National Bank", "Kotak Mahindra Bank", "Bank of Baroda", "Canara Bank")
    private val BANKS_HI = listOf("एसबीआई", "एचडीएफसी बैंक", "आईसीआईसीआई बैंक", "एक्सिस बैंक", "पंजाब नेशनल बैंक", "कोटक महिंद्रा बैंक", "बैंक ऑफ बड़ौदा", "केनरा बैंक")

    private val COURIERS = listOf("FedEx", "DTDC", "Delhivery", "Blue Dart", "India Post", "DHL Express")
    private val COURIERS_HI = listOf("फेडेक्स", "डीटीडीसी", "दिल्लीवरी", "ब्लू डार्ट", "इंडिया पोस्ट", "डीएचएल एक्सप्रेस")

    private val NAMES = listOf("Rajesh", "Amit", "Rahul", "Priya", "Neha", "Vikram", "Sneha", "Sanjay", "Karan", "Anjali")
    private val NAMES_HI = listOf("राजेश", "अमित", "राहुल", "प्रिया", "नेहा", "विक्रम", "स्नेहा", "संजय", "करण", "अंजलि")

    private val BRANDS = listOf("Amazon", "Flipkart", "Netflix", "Google", "Paytm", "Airtel", "Jio", "WhatsApp", "Telegram")
    private val BRANDS_HI = listOf("अमेज़न", "फ्लिपकार्ट", "नेटफ्लिक्स", "गूगल", "पेटीएम", "एयरटेल", "जियो", "व्हाट्सएप", "टेलीग्राम")

    private val JOB_ROLES = listOf("YouTube Video Liker", "Google Maps Reviewer", "Data Entry Operator", "Part-Time typist", "Hotel Review Assistant")
    private val JOB_ROLES_HI = listOf("यूट्यूब वीडियो लाइकर", "गूगल मैप्स रिव्यूअर", "डेटा एंट्री ऑपरेटर", "पार्ट-टाइम टाइपिस्ट", "होटल रिव्यू असिस्टेंट")

    private val CRYPTO_COINS = listOf("Bitcoin", "Ethereum", "USDT", "Ripple", "Dogecoin")
    private val CRYPTO_COINS_HI = listOf("बिटकॉइन", "इथेरियम", "यूएसडीटी", "रिपल", "डॉगकॉइन")

    fun getChallengeForDay(day: Int): DailyChallenge {
        // Clamp to 1..365 range
        val dayClamped = if (day < 1) 1 else if (day > 365) 365 else day
        val categoryIndex = (dayClamped - 1) % 15
        
        // Deterministic parameter generation using math based on day
        val bankIdx = (dayClamped * 3 + 7) % BANKS.size
        val bank = BANKS[bankIdx]
        val bankHi = BANKS_HI[bankIdx]

        val courierIdx = (dayClamped * 2 + 11) % COURIERS.size
        val courier = COURIERS[courierIdx]
        val courierHi = COURIERS_HI[courierIdx]

        val nameIdx = (dayClamped * 5 + 13) % NAMES.size
        val name = NAMES[nameIdx]
        val nameHi = NAMES_HI[nameIdx]

        val brandIdx = (dayClamped * 7 + 19) % BRANDS.size
        val brand = BRANDS[brandIdx]
        val brandHi = BRANDS_HI[brandIdx]

        val jobIdx = (dayClamped * 4 + 5) % JOB_ROLES.size
        val job = JOB_ROLES[jobIdx]
        val jobHi = JOB_ROLES_HI[jobIdx]

        val coinIdx = (dayClamped * 6 + 17) % CRYPTO_COINS.size
        val coin = CRYPTO_COINS[coinIdx]
        val coinHi = CRYPTO_COINS_HI[coinIdx]

        val amount = 1500 + (dayClamped * 1450) % 85000
        val fee = 100 + (dayClamped * 150) % 2500
        val url = "http://${brand.lowercase()}-security-${dayClamped % 100}.org/login"
        val otp = 100000 + (dayClamped * 98765) % 900000

        val difficulty = when {
            dayClamped % 3 == 0 -> "Easy"
            dayClamped % 3 == 1 -> "Medium"
            else -> "Hard"
        }
        val difficultyHi = when (difficulty) {
            "Easy" -> "आसान"
            "Medium" -> "मध्यम"
            else -> "कठिन"
        }

        return when (categoryIndex) {
            0 -> generatePhishing(dayClamped, bank, bankHi, url, difficulty, difficultyHi)
            1 -> generateOtpFraud(dayClamped, brand, brandHi, otp, difficulty, difficultyHi)
            2 -> generateUpiFraud(dayClamped, name, nameHi, amount, difficulty, difficultyHi)
            3 -> generateQrScam(dayClamped, name, nameHi, amount, difficulty, difficultyHi)
            4 -> generateJobScam(dayClamped, brand, brandHi, job, jobHi, fee, difficulty, difficultyHi)
            5 -> generateFakeBankCall(dayClamped, bank, bankHi, name, nameHi, difficulty, difficultyHi)
            6 -> generateFakeDelivery(dayClamped, courier, courierHi, fee, difficulty, difficultyHi)
            7 -> generateInvestmentScam(dayClamped, name, nameHi, amount, coin, coinHi, difficulty, difficultyHi)
            8 -> generateLoanScam(dayClamped, brand, brandHi, amount, difficulty, difficultyHi)
            9 -> generateWhatsAppScam(dayClamped, name, nameHi, amount, difficulty, difficultyHi)
            10 -> generateTelegramScam(dayClamped, name, nameHi, amount, difficulty, difficultyHi)
            11 -> generateSocialEngineering(dayClamped, name, nameHi, amount, difficulty, difficultyHi)
            12 -> generateAiVoiceScam(dayClamped, name, nameHi, amount, difficulty, difficultyHi)
            13 -> generateFakeApps(dayClamped, brand, brandHi, difficulty, difficultyHi)
            else -> generateFakeCustomerCare(dayClamped, brand, brandHi, difficulty, difficultyHi)
        }
    }

    private fun generatePhishing(day: Int, bank: String, bankHi: String, url: String, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_PHISH_$day",
            category = "PHISHING",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Phishing Link",
            scamTypeLabelHi = "फ़िशिंग लिंक",
            title = LocalizedText(
                en = "Account Suspended Warning",
                hi = "खाता निलंबन की चेतावनी"
            ),
            scenario = LocalizedText(
                en = "You receive an urgent SMS: 'Dear $bank user, your NetBanking account has been suspended due to suspicious transactions. To reactivate your account immediately, click here: $url.'",
                hi = "आपको एक तत्काल SMS प्राप्त होता है: 'प्रिय $bank उपयोगकर्ता, संदिग्ध लेनदेन के कारण आपका नेटबैंकिंग खाता निलंबित कर दिया गया है। अपने खाते को तुरंत पुनः सक्रिय करने के लिए, यहाँ क्लिक करें: $url।'"
            ),
            question = LocalizedText(
                en = "What is the safest action to take here?",
                hi = "यहाँ कौन सी कार्रवाई सबसे सुरक्षित है?"
            ),
            options = listOf(
                LocalizedText(en = "Click the link and log in to verify your details", hi = "लिंक पर क्लिक करें और विवरण सत्यापित करने के लिए लॉगिन करें"),
                LocalizedText(en = "Delete the SMS and log in only through the official bank app or website", hi = "SMS को डिलीट करें और केवल आधिकारिक बैंक ऐप या वेबसाइट के माध्यम से लॉग इन करें"),
                LocalizedText(en = "Forward the text to your contact list to ask if they got it", hi = "यह जानने के लिए कि क्या दूसरों को भी यह मिला है, इसे अपनी संपर्क सूची में फॉरवर्ड करें"),
                LocalizedText(en = "Call the number that sent the SMS and tell them your PIN", hi = "SMS भेजने वाले नंबर पर कॉल करें और उन्हें अपना पिन बताएं")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "This is a typical phishing scam. Banks will never ask you to click an unofficial link (like $url) to update KYC or netbanking details.",
                hi = "यह एक विशिष्ट फ़िशिंग घोटाला है। बैंक कभी भी केवाईसी या नेटबैंकिंग विवरण अपडेट करने के लिए किसी अनौपचारिक लिंक (जैसे $url) पर क्लिक करने के लिए नहीं कहेंगे।"
            ),
            didYouKnow = LocalizedText(
                en = "Phishing attacks grew by over 120% globally last year, with financial services being the most targeted sector.",
                hi = "पिछले साल वैश्विक स्तर पर फ़िशिंग हमलों में 120% से अधिक की वृद्धि हुई, जिसमें वित्तीय सेवाएँ सबसे अधिक लक्षित क्षेत्र थीं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Sender is a personal mobile number instead of an official bank sender code.", hi = "प्रेषक आधिकारिक बैंक सेंडर कोड के बजाय एक व्यक्तिगत मोबाइल नंबर है।"),
                LocalizedText(en = "The URL ($url) does not match the official bank domain.", hi = "URL ($url) आधिकारिक बैंक डोमेन से मेल नहीं खाता है।"),
                LocalizedText(en = "Highly urgent and threatening language demanding immediate login.", hi = "तत्काल लॉगिन की मांग करने वाली अत्यधिक दबावपूर्ण और धमकी भरी भाषा।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Never log in or share credit/debit credentials via links received in SMS.", hi = "SMS में प्राप्त लिंक के माध्यम से कभी भी लॉगिन न करें या क्रेडिट/डेबिट क्रेडेंशियल साझा न करें।"),
                LocalizedText(en = "Always check for 'https://' and verify the correct spelling of the bank's official website.", hi = "हमेशा 'https://' की जांच करें और बैंक की आधिकारिक वेबसाइट की सही वर्तनी सत्यापित करें।")
            )
        )
    }

    private fun generateOtpFraud(day: Int, brand: String, brandHi: String, otp: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_OTP_$day",
            category = "OTP_FRAUD",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "OTP Hijacking",
            scamTypeLabelHi = "ओटीपी अपहरण",
            title = LocalizedText(
                en = "Verification Code Request",
                hi = "सत्यापन कोड अनुरोध"
            ),
            scenario = LocalizedText(
                en = "An executive calling from '$brand Helpdesk' claims they sent a code ($otp) to your mobile by mistake while updating another customer's profile. They ask you to read it out.",
                hi = "'$brand हेल्पडेस्क' से बात कर रहे एक अधिकारी का दावा है कि उन्होंने किसी अन्य ग्राहक का प्रोफाइल अपडेट करते समय गलती से आपके मोबाइल पर एक कोड ($otp) भेज दिया है। वे आपसे इसे पढ़कर सुनाने को कहते हैं।"
            ),
            question = LocalizedText(
                en = "What is the correct way to handle this phone call?",
                hi = "इस फोन कॉल को संभालने का सही तरीका क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Help them out by reading the verification code aloud", hi = "सत्यापन कोड को ज़ोर से पढ़कर उनकी मदद करें"),
                LocalizedText(en = "Refuse to share the code and hang up immediately", hi = "कोड साझा करने से इंकार करें और तुरंत कॉल काट दें"),
                LocalizedText(en = "Tell them a wrong code to test if they are genuine", hi = "परीक्षण के लिए गलत कोड बताएं कि क्या वे वास्तविक हैं"),
                LocalizedText(en = "Tell them to send a written official letter instead", hi = "इसके बजाय उन्हें एक लिखित आधिकारिक पत्र भेजने के लिए कहें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "An OTP (One-Time Password) is a secure, single-use key to authenticate your login or authorize transactions. Telling it to anyone gives them direct access to your accounts or funds.",
                hi = "ओटीपी (वन-टाइम पासवर्ड) आपके लॉगिन को प्रमाणित करने या लेनदेन को अधिकृत करने के लिए एक सुरक्षित, एकल-उपयोग कुंजी है। इसे किसी को भी बताने से उन्हें आपके खातों या धन तक सीधी पहुंच मिल जाती है।"
            ),
            didYouKnow = LocalizedText(
                en = "Legitimate customer support representatives will NEVER ask for an OTP or security code to cancel, update, or initiate transactions.",
                hi = "वैध ग्राहक सहायता प्रतिनिधि लेनदेन को रद्द करने, अपडेट करने या शुरू करने के लिए कभी भी ओटीपी या सुरक्षा कोड नहीं मांगेंगे।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "The caller is asking for a code that says 'Do Not Share' in the SMS body.", hi = "कॉलर एक ऐसे कोड की मांग कर रहा है जिसके SMS बॉडी में 'साझा न करें' लिखा है।"),
                LocalizedText(en = "The request is based on a fabricated 'mistake' or emergency story.", hi = "अनुरोध एक मनगढ़ंत 'गलती' या आपातकालीन कहानी पर आधारित है।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Treat OTPs with the same level of confidentiality as your ATM PIN.", hi = "ओटीपी को अपने एटीएम पिन के समान ही गोपनीय मानें।"),
                LocalizedText(en = "Read the transaction details mentioned in the OTP SMS carefully before acting.", hi = "कार्रवाई करने से पहले ओटीपी SMS में उल्लिखित लेनदेन विवरण को ध्यान से पढ़ें।")
            )
        )
    }

    private fun generateUpiFraud(day: Int, name: String, nameHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_UPI_$day",
            category = "UPI_FRAUD",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "UPI Request Scam",
            scamTypeLabelHi = "यूपीआई अनुरोध धोखाधड़ी",
            title = LocalizedText(
                en = "UPI 'Collect Money' Alarm",
                hi = "यूपीआई 'पैसे प्राप्त करें' चेतावनी"
            ),
            scenario = LocalizedText(
                en = "A user named '$name' on Facebook Marketplace wants to buy your listed item. They tell you they have sent the payment and you just need to click 'Approve' and enter your UPI PIN to receive ₹$amount.",
                hi = "फेसबुक मार्केटप्लेस पर '$name' नाम का एक उपयोगकर्ता आपका सूचीबद्ध आइटम खरीदना चाहता है। वे आपको बताते हैं कि उन्होंने भुगतान भेज दिया है और ₹$amount प्राप्त करने के लिए आपको बस 'स्वीकृत' पर क्लिक करके अपना यूपीआई पिन दर्ज करना होगा।"
            ),
            question = LocalizedText(
                en = "Should you click approve and type your UPI PIN?",
                hi = "क्या आपको 'स्वीकृत' पर क्लिक करके अपना यूपीआई पिन टाइप करना चाहिए?"
            ),
            options = listOf(
                LocalizedText(en = "Yes, enter the PIN so that the funds are transferred", hi = "हाँ, पिन दर्ज करें ताकि धन आपके खाते में स्थानांतरित हो जाए"),
                LocalizedText(en = "Decline the request. PIN is only needed to send money, never to receive it", hi = "अनुरोध को अस्वीकार करें। पिन की आवश्यकता केवल पैसे भेजने के लिए होती है, प्राप्त करने के लिए कभी नहीं"),
                LocalizedText(en = "Decline, but send ₹10 to verify their UPI account is active", hi = "अस्वीकार करें, लेकिन यह सत्यापित करने के लिए ₹10 भेजें कि उनका यूपीआई खाता सक्रिय है"),
                LocalizedText(en = "Enter a wrong PIN so they don't get your bank info", hi = "गलत पिन दर्ज करें ताकि उन्हें आपकी बैंक जानकारी न मिले")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "UPI PIN is only required to authorize debit/payment from your bank account. You NEVER need to enter a UPI PIN to receive money. Clicking 'Approve' on a collect request is authorizing a deduction.",
                hi = "आपके बैंक खाते से पैसे डेबिट करने/भुगतान करने के लिए ही यूपीआई पिन की आवश्यकता होती है। पैसे प्राप्त करने के लिए आपको कभी भी यूपीआई पिन दर्ज करने की आवश्यकता नहीं होती है। कलेक्ट अनुरोध पर 'स्वीकृत' पर क्लिक करना पैसे काटने की स्वीकृति देना है।"
            ),
            didYouKnow = LocalizedText(
                en = "Over 60% of peer-to-peer online marketplace frauds in India involve fake UPI collect requests targeting unsuspecting sellers.",
                hi = "भारत में ऑनलाइन मार्केटप्लेस धोखाधड़ी के 60% से अधिक मामलों में अनुभवहीन विक्रेताओं को लक्षित करने वाले नकली यूपीआई कलेक्ट अनुरोध शामिल होते हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Receiving a UPI notification asking you to 'Pay' or 'Approve Request' when you expect to receive money.", hi = "पैसे प्राप्त होने की उम्मीद होने पर 'भुगतान' या 'अनुरोध स्वीकृत' करने वाला यूपीआई नोटिफिकेशन प्राप्त होना।"),
                LocalizedText(en = "The buyer is in a rush and pressures you to enter your PIN immediately.", hi = "खरीदार जल्दी में है और आपको तुरंत अपना पिन दर्ज करने के लिए दबाव डालता है।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Never enter your UPI PIN on any screen if you are receiving money.", hi = "यदि आप पैसे प्राप्त कर रहे हैं तो किसी भी स्क्रीन पर अपना यूपीआई पिन कभी दर्ज न करें।"),
                LocalizedText(en = "Use secure payment features offered directly within the marketplace app rather than private chat.", hi = "निजी चैट के बजाय सीधे मार्केटप्लेस ऐप के भीतर दी गई सुरक्षित भुगतान सुविधाओं का उपयोग करें।")
            )
        )
    }

    private fun generateQrScam(day: Int, name: String, nameHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_QR_$day",
            category = "QR_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "QR Code Scan Trick",
            scamTypeLabelHi = "क्यूआर कोड स्कैन चाल",
            title = LocalizedText(
                en = "Scan to Receive Cash",
                hi = "कैश प्राप्त करने के लिए स्कैन करें"
            ),
            scenario = LocalizedText(
                en = "An unknown buyer '$name' sends you a QR code on WhatsApp saying: 'Please scan this QR code using Paytm or PhonePe to receive ₹$amount instantly in your bank account.'",
                hi = "एक अज्ञात खरीदार '$name' आपको व्हाट्सएप पर एक क्यूआर कोड भेजता है जिसमें लिखा होता है: 'कृपया अपने बैंक खाते में तुरंत ₹$amount प्राप्त करने के लिए पेटीएम या फोनपे का उपयोग करके इस क्यूआर कोड को स्कैन करें।'"
            ),
            question = LocalizedText(
                en = "What is the correct action to protect your funds?",
                hi = "अपने धन की रक्षा के लिए सही कार्रवाई क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Scan the QR code and enter your UPI PIN", hi = "क्यूआर कोड स्कैन करें और अपना यूपीआई पिन दर्ज करें"),
                LocalizedText(en = "Refuse to scan. QR codes are only scanned to PAY money, never to RECEIVE it", hi = "स्कैन करने से मना करें। क्यूआर कोड केवल पैसे भुगतान करने के लिए स्कैन किए जाते हैं, प्राप्त करने के लिए कभी नहीं"),
                LocalizedText(en = "Scan it using an old phone that has no bank apps installed", hi = "इसे किसी पुराने फोन का उपयोग करके स्कैन करें जिसमें कोई बैंक ऐप इंस्टॉल न हो"),
                LocalizedText(en = "Tell them to scan your QR code first before you scan theirs", hi = "उनसे कहें कि आपके स्कैन करने से पहले वे आपका क्यूआर कोड स्कैन करें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "A QR code (Quick Response code) contains routing details to send money. Scanning a QR code inside a payment app and entering your UPI PIN authorizes a transfer OUT of your account, not into it.",
                hi = "एक क्यूआर कोड (क्विक रिस्पांस कोड) में पैसे भेजने के लिए राउटिंग विवरण होता है। भुगतान ऐप के भीतर क्यूआर कोड को स्कैन करना और अपना यूपीआई पिन दर्ज करना आपके खाते से पैसे भेजने की स्वीकृति देता है, न कि प्राप्त करने की।"
            ),
            didYouKnow = LocalizedText(
                en = "QR codes are fundamentally standard static strings represented visually; they can never pull money into your bank account under any circumstances.",
                hi = "क्यूआर कोड मौलिक रूप से केवल एक विजुअल स्ट्रिंग हैं; वे किसी भी परिस्थिति में आपके बैंक खाते में पैसे जमा नहीं करवा सकते।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "The buyer sends an image of a QR code with logos like Google Pay or Paytm over WhatsApp.", hi = "खरीदार व्हाट्सएप पर गूगल पे या पेटीएम जैसे लोगो वाला क्यूआर कोड भेजता है।"),
                LocalizedText(en = "The text on the QR image says 'Scan to Receive' or 'Scan to get Cashback'.", hi = "क्यूआर इमेज पर लिखा होता है 'प्राप्त करने के लिए स्कैन करें' या 'कैशबैक पाने के लिए स्कैन करें' जो कि संपादन योग्य इमेज हैं।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Always remember: Scanning a QR code is strictly for making a debit payment.", hi = "हमेशा याद रखें: क्यूआर कोड को स्कैन करना केवल डेबिट भुगतान करने के लिए होता है।"),
                LocalizedText(en = "Directly share your mobile number or UPI ID if someone needs to pay you—no scan required.", hi = "यदि किसी को आपको भुगतान करना है तो सीधे अपना मोबाइल नंबर या यूपीआई आईडी साझा करें—स्कैन की आवश्यकता नहीं है।")
            )
        )
    }

    private fun generateJobScam(day: Int, brand: String, brandHi: String, job: String, jobHi: String, fee: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_JOB_$day",
            category = "JOB_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Fake Job Offer",
            scamTypeLabelHi = "फर्जी नौकरी का ऑफर",
            title = LocalizedText(
                en = "Easy Remote Earning Scam",
                hi = "आसान रिमोट कमाई घोटाला"
            ),
            scenario = LocalizedText(
                en = "You get a message on WhatsApp from '$brand Recruitment' offering a part-time remote job as a '$job' paying up to ₹5,000 daily. To initiate onboarding, they require a refundable registration fee of ₹$fee.",
                hi = "आपको व्हाट्सएप पर '$brand भर्ती' से एक संदेश मिलता है जिसमें '$job' के रूप में अंशकालिक रिमोट जॉब की पेशकश की जाती है, जो प्रतिदिन ₹5,000 तक का भुगतान करती है। ऑनबोर्डिंग शुरू करने के लिए, उन्हें ₹$fee के वापसी योग्य पंजीकरण शुल्क की आवश्यकता है।"
            ),
            question = LocalizedText(
                en = "Should you pay this registration fee?",
                hi = "क्या आपको यह पंजीकरण शुल्क देना चाहिए?"
            ),
            options = listOf(
                LocalizedText(en = "Pay the fee since it's refundable and you will earn it back fast", hi = "शुल्क का भुगतान करें क्योंकि यह वापसी योग्य है और आप इसे तेजी से कमा लेंगे"),
                LocalizedText(en = "Block the contact. Legitimate companies never charge fees to hire or pay employees", hi = "संपर्क को ब्लॉक करें। वैध कंपनियां कभी भी कर्मचारियों को काम पर रखने या भुगतान करने के लिए शुल्क नहीं लेती हैं"),
                LocalizedText(en = "Pay half the fee and ask them to deduct the rest from your salary", hi = "आधे शुल्क का भुगतान करें और उनसे बाकी राशि आपके वेतन से काटने के लिए कहें"),
                LocalizedText(en = "Send your banking login username and password instead of cash", hi = "नकद के बजाय अपने बैंकिंग लॉगिन उपयोगकर्ता नाम और पासवर्ड भेजें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "This is a widespread employment scam. Fraudsters use famous brand names to advertise easy tasks. Charging upfront fees for 'training', 'processing', or 'verification' is a major indicator of fraud.",
                hi = "यह एक व्यापक रूप से फैला रोजगार घोटाला है। धोखेबाज आसान कार्यों का विज्ञापन करने के लिए प्रसिद्ध ब्रांड नामों का उपयोग करते हैं। 'प्रशिक्षण', 'प्रसंस्करण' या 'सत्यापन' के लिए अग्रिम शुल्क लेना धोखाधड़ी का एक प्रमुख संकेतक है।"
            ),
            didYouKnow = LocalizedText(
                en = "Under labor guidelines in India, recruiters and job portals are legally forbidden from charging candidates any upfront processing or recruitment fees.",
                hi = "भारत में श्रम दिशानिर्देशों के तहत, नियोक्ताओं और नौकरी पोर्टलों को उम्मीदवारों से कोई भी अग्रिम प्रसंस्करण या भर्ती शुल्क लेने की कानूनी रूप से मनाही है।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Unsolicited job offer received via WhatsApp/Telegram from an unofficial personal number.", hi = "गैर-आधिकारिक व्यक्तिगत नंबर से व्हाट्सएप/टेलीग्राम के माध्यम से प्राप्त अनपेक्षित नौकरी का ऑफर।"),
                LocalizedText(en = "Demands money upfront for materials, registration, or system onboarding.", hi = "सामग्री, पंजीकरण, या सिस्टम ऑनबोर्डिंग के लिए अग्रिम पैसे की मांग करना।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Verify the job listing directly on the official careers page of the company.", hi = "कंपनी के आधिकारिक करियर पेज पर सीधे जाकर नौकरी की सूची सत्यापित करें।"),
                LocalizedText(en = "Never pay any money to get a job. Real employers provide equipment and training for free.", hi = "नौकरी पाने के लिए कभी कोई पैसा न दें। वास्तविक नियोक्ता उपकरण और प्रशिक्षण मुफ्त में प्रदान करते हैं।")
            )
        )
    }

    private fun generateFakeBankCall(day: Int, bank: String, bankHi: String, name: String, nameHi: String, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_BANKCALL_$day",
            category = "FAKE_BANK_CALL",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Fake Bank Call",
            scamTypeLabelHi = "फर्जी बैंक कॉल",
            title = LocalizedText(
                en = "Urgent Debit Card Blockage",
                hi = "तत्काल डेबिट कार्ड ब्लॉक चेतावनी"
            ),
            scenario = LocalizedText(
                en = "A caller claiming to be '$name', Senior Manager at $bank, states that your debit card is being blocked due to a security audit. They ask for your card number, expiry date, and CVV to verify and unlock it immediately.",
                hi = "$bank के वरिष्ठ प्रबंधक '$name' होने का दावा करने वाले एक कॉलर का कहना है कि सुरक्षा ऑडिट के कारण आपका डेबिट कार्ड ब्लॉक किया जा रहा है। वे तुरंत इसे सत्यापित करने और अनलॉक करने के लिए आपका कार्ड नंबर, समाप्ति तिथि और सीवीवी मांगते हैं।"
            ),
            question = LocalizedText(
                en = "What is the correct protocol in this situation?",
                hi = "इस स्थिति में सही प्रोटोकॉल क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Provide the requested card details so your card stays active", hi = "अनुरोधित कार्ड विवरण प्रदान करें ताकि आपका कार्ड सक्रिय रहे"),
                LocalizedText(en = "Refuse and call your bank's official number listed on their website or card", hi = "इनकार करें और अपनी वेबसाइट या कार्ड पर सूचीबद्ध अपने बैंक के आधिकारिक नंबर पर कॉल करें"),
                LocalizedText(en = "Provide card details but refuse to share the CVV or OTP", hi = "कार्ड विवरण प्रदान करें लेकिन सीवीवी या ओटीपी साझा करने से इनकार करें"),
                LocalizedText(en = "Give them a fake card number to teach them a lesson", hi = "उन्हें सबक सिखाने के लिए गलत कार्ड नंबर दें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Bank employees never call to ask for highly sensitive details like CVV, NetBanking passwords, or card numbers. Any caller demanding these details under pressure is a criminal attempting a card-cloning/online theft scam.",
                hi = "बैंक कर्मचारी कभी भी सीवीवी, नेटबैंकिंग पासवर्ड या कार्ड नंबर जैसे संवेदनशील विवरण मांगने के लिए कॉल नहीं करते हैं। दबाव में इन विवरणों की मांग करने वाला कोई भी कॉलर कार्ड-क्लोनिंग या ऑनलाइन चोरी का प्रयास कर रहा अपराधी है।"
            ),
            didYouKnow = LocalizedText(
                en = "Banks have automated systems to manage card fraud; they will never require manual verification of your physical CVV over a phone conversation.",
                hi = "कार्ड धोखाधड़ी के प्रबंधन के लिए बैंकों के पास स्वचालित प्रणालियाँ होती हैं; उन्हें फोन पर आपके भौतिक सीवीवी के मैन्युअल सत्यापन की कभी आवश्यकता नहीं होगी।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Caller creates a high-pressure, panic scenario about your account closing or getting fined.", hi = "कॉलर आपके खाते के बंद होने या जुर्माना लगने के बारे में अत्यधिक दबाव और घबराहट की स्थिति पैदा करता है।"),
                LocalizedText(en = "Asking for the 3-digit CVV number on the back of your card.", hi = "आपके कार्ड के पीछे लिखे 3-अंकीय सीवीवी नंबर की मांग करना।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Hang up immediately if an incoming caller asks for bank passwords, OTPs, or CVV.", hi = "यदि कोई इनकमिंग कॉलर बैंक पासवर्ड, ओटीपी या सीवीवी मांगता है तो तुरंत कॉल काट दें।"),
                LocalizedText(en = "The official bank support number is printed on the back of your physical plastic card.", hi = "आधिकारिक बैंक सहायता नंबर आपके भौतिक प्लास्टिक कार्ड के पीछे मुद्रित होता है।")
            )
        )
    }

    private fun generateFakeDelivery(day: Int, courier: String, courierHi: String, fee: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_DELIVERY_$day",
            category = "DELIVERY_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Fake Courier Delivery",
            scamTypeLabelHi = "फर्जी कूरियर डिलीवरी",
            title = LocalizedText(
                en = "Incomplete Address Delivery Scam",
                hi = "अधूरे पते का डिलीवरी घोटाला"
            ),
            scenario = LocalizedText(
                en = "You receive an SMS from '$courier': 'Your package is on hold due to an incomplete address. To update your home address and reschedule delivery, pay a ₹$fee re-delivery fee at http://${courier.lowercase()}-update.in.'",
                hi = "आपको '$courier' से एक SMS मिलता है: 'अधूरे पते के कारण आपका पैकेज रुका हुआ है। अपना घर का पता अपडेट करने और डिलीवरी को फिर से शेड्यूल करने के लिए, http://${courier.lowercase()}-update.in पर ₹$fee पुनः डिलीवरी शुल्क का भुगतान करें।'"
            ),
            question = LocalizedText(
                en = "What is the safest way to verify this package status?",
                hi = "इस पैकेज की स्थिति को सत्यापित करने का सबसे सुरक्षित तरीका क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Click the link and complete the ₹$fee payment with UPI", hi = "लिंक पर क्लिक करें और यूपीआई के साथ ₹$fee भुगतान पूरा करें"),
                LocalizedText(en = "Ignore the SMS and check the tracking ID directly on the official website of $courier", hi = "SMS को अनदेखा करें और ट्रैकिंग आईडी को सीधे $courier की आधिकारिक वेबसाइट पर देखें"),
                LocalizedText(en = "Reply to the SMS with your full home address and card details", hi = "SMS के उत्तर में अपना पूरा घर का पता और कार्ड विवरण भेजें"),
                LocalizedText(en = "Pay the delivery agent in cash only when they arrive", hi = "डिलीवरी एजेंट के आने पर ही उन्हें नकद भुगतान करें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "This courier redirect scam aims to steal banking credentials and identity details. Legitimate shipping firms do not charge redelivery fees via SMS links or block packages for minor address updates.",
                hi = "इस कूरियर रीडायरेक्ट घोटाले का उद्देश्य बैंकिंग क्रेडेंशियल और पहचान विवरण चुराना है। वैध शिपिंग कंपनियां SMS लिंक के माध्यम से पुनः वितरण शुल्क नहीं लेती हैं या मामूली पता अपडेट के लिए पैकेज नहीं रोकती हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "Fraudsters send bulk courier SMS alerts during festival seasons, betting that you have ordered something online.",
                hi = "धोखेबाज त्योहारों के मौसम में थोक कूरियर SMS अलर्ट भेजते हैं, यह उम्मीद करते हुए कि आपने ऑनलाइन कुछ ऑर्डर किया होगा।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "The SMS contains a generic link instead of the official logistics portal address.", hi = "SMS में आधिकारिक लॉजिस्टिक्स पोर्टल पते के बजाय एक सामान्य लिंक होता है।"),
                LocalizedText(en = "No tracking ID is mentioned in the SMS, or the ID doesn't work on the official tracker.", hi = "SMS में कोई ट्रैकिंग आईडी उल्लिखित नहीं है, या आईडी आधिकारिक ट्रैकर पर काम नहीं करती है।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Never authorize online payments to unlock any physical shipment.", hi = "किसी भी भौतिक शिपमेंट को अनलॉक करने के लिए ऑनलाइन भुगतान को कभी भी अधिकृत न करें।"),
                LocalizedText(en = "Always access delivery platforms by typing their verified URL manually.", hi = "सत्यापित URL को मैन्युअल रूप से टाइप करके हमेशा डिलीवरी प्लेटफॉर्म तक पहुंचें।")
            )
        )
    }

    private fun generateInvestmentScam(day: Int, name: String, nameHi: String, amount: Int, coin: String, coinHi: String, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_INVEST_$day",
            category = "INVESTMENT_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Investment Fraud",
            scamTypeLabelHi = "निवेश धोखाधड़ी",
            title = LocalizedText(
                en = "Guaranteed High Yield Profit",
                hi = "गारंटीकृत उच्च रिटर्न लाभ"
            ),
            scenario = LocalizedText(
                en = "An online expert '$name' contacts you promising a guaranteed 200% profit in 48 hours if you buy $coin and transfer it to their specialized private pool. They show screenshots of other members earning lakhs.",
                hi = "एक ऑनलाइन विशेषज्ञ '$name' आपसे संपर्क करता है और वादा करता है कि यदि आप $coin खरीदकर उनके विशेष निजी पूल में स्थानांतरित करते हैं तो 48 घंटों में 200% गारंटीकृत लाभ मिलेगा। वे अन्य सदस्यों के लाखों कमाने के स्क्रीनशॉट दिखाते हैं।"
            ),
            question = LocalizedText(
                en = "What is the truth behind guaranteed online investment returns?",
                hi = "गारंटीकृत ऑनलाइन निवेश रिटर्न के पीछे क्या सच्चाई है?"
            ),
            options = listOf(
                LocalizedText(en = "It is highly genuine since they shared active payout screenshots", hi = "यह अत्यधिक वास्तविक है क्योंकि उन्होंने सक्रिय भुगतान स्क्रीनशॉट साझा किए हैं"),
                LocalizedText(en = "It is a Ponzi scam. No legitimate financial platform offers guaranteed high returns", hi = "यह एक पोंजी घोटाला है। कोई भी वैध वित्तीय मंच गारंटीकृत उच्च रिटर्न की पेशकश नहीं करता है"),
                LocalizedText(en = "Invest a small sum of ₹$amount to test if they payout", hi = "यह जांचने के लिए कि क्या वे भुगतान करते हैं, ₹$amount की एक छोटी राशि का निवेश करें"),
                LocalizedText(en = "Ask for their physical address before transferring your crypto", hi = "अपनी क्रिप्टोकरेंसी स्थानांतरित करने से पहले उनका भौतिक पता मांगें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Guaranteed high returns do not exist in genuine financial markets. Scammers build fake trading dashboards showing high dummy profits, but when you attempt to withdraw, they demand 'taxes' and vanish.",
                hi = "वास्तविक वित्तीय बाजारों में गारंटीकृत उच्च रिटर्न मौजूद नहीं होते हैं। घोटालेबाज नकली ट्रेडिंग डैशबोर्ड बनाते हैं जो उच्च काल्पनिक मुनाफा दिखाते हैं, लेकिन जब आप वापस लेने का प्रयास करते हैं, तो वे 'टैक्स' की मांग करते हैं और गायब हो जाते हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "Investment scams constitute the highest monetary losses among all reported digital crimes in India, with victims often losing life savings.",
                hi = "भारत में रिपोर्ट किए गए सभी डिजिटल अपराधों में निवेश घोटाले सबसे अधिक वित्तीय नुकसान का कारण बनते हैं, जिसमें पीड़ित अक्सर अपनी जीवन भर की बचत खो देते हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Claims of zero risk with exceptionally high guaranteed daily/weekly payouts.", hi = "असाधारण रूप से उच्च गारंटीकृत दैनिक/साप्ताहिक भुगतान के साथ शून्य जोखिम का दावा।"),
                LocalizedText(en = "Using group chats where everyone praises the admin's investment strategy (highly manipulated chats).", hi = "समूह चैट का उपयोग करना जहां हर कोई व्यवस्थापक की निवेश रणनीति की प्रशंसा करता है (अत्यधिक हेरफेर की गई चैट)।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Only invest through SEBI-registered brokers, mutual funds, and established banks.", hi = "केवल सेबी-पंजीकृत दलालों, म्यूचुअल फंड और स्थापित बैंकों के माध्यम से निवेश करें।"),
                LocalizedText(en = "Remember: High return always implies high risk. Guaranteed return with zero risk is ALWAYS a scam.", hi = "याद रखें: उच्च रिटर्न का अर्थ हमेशा उच्च जोखिम होता है। शून्य जोखिम के साथ गारंटीकृत रिटर्न हमेशा एक घोटाला है।")
            )
        )
    }

    private fun generateLoanScam(day: Int, brand: String, brandHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_LOAN_$day",
            category = "LOAN_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Instant Loan Scam",
            scamTypeLabelHi = "त्वरित ऋण घोटाला",
            title = LocalizedText(
                en = "Collateral-Free Instant Credit",
                hi = "जमानत-मुक्त त्वरित ऋण"
            ),
            scenario = LocalizedText(
                en = "You see an online advertisement for '$brand Loans' offering instant, hassle-free credit of ₹$amount with no documentation. Upon installing the app, it demands mandatory access to your entire contact list and gallery.",
                hi = "आप बिना किसी दस्तावेज के ₹$amount के त्वरित ऋण की पेशकश करने वाले '$brand ऋण' का एक ऑनलाइन विज्ञापन देखते हैं। ऐप इंस्टॉल करने पर, यह आपकी पूरी संपर्क सूची और गैलरी तक अनिवार्य पहुंच की मांग करता है।"
            ),
            question = LocalizedText(
                en = "Should you download this app and grant the permissions?",
                hi = "क्या आपको यह ऐप डाउनलोड करना चाहिए और अनुमतियां देनी चाहिए?"
            ),
            options = listOf(
                LocalizedText(en = "Download and approve permissions to resolve your cash urgency", hi = "अपनी नकदी की तात्कालिकता को हल करने के लिए डाउनलोड करें और अनुमतियां स्वीकृत करें"),
                LocalizedText(en = "Do not download. Unverified loan apps steal personal data to blackmail and harass you", hi = "डाउनलोड न करें। असत्यापित ऋण ऐप ब्लैकमेल और परेशान करने के लिए व्यक्तिगत डेटा चुराते हैं"),
                LocalizedText(en = "Download, but provide fake contact numbers to trick the app", hi = "डाउनलोड करें, लेकिन ऐप को चकमा देने के लिए नकली संपर्क नंबर प्रदान करें"),
                LocalizedText(en = "Grant contact access but deny the gallery permission", hi = "संपर्क पहुंच की अनुमति दें लेकिन गैलरी अनुमति को अस्वीकार करें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Illegal instant loan applications exploit users by charging exorbitant interest rates and processing fees. They steal your contacts and gallery, then threaten to send edited sensitive pictures to your friends/family if you do not pay.",
                hi = "अवैध त्वरित ऋण एप्लिकेशन अत्यधिक ब्याज दरें और प्रसंस्करण शुल्क वसूल कर उपयोगकर्ताओं का शोषण करते हैं। वे आपके संपर्कों और गैलरी को चुराते हैं, फिर यदि आप भुगतान नहीं करते हैं तो आपके दोस्तों/परिवार को संपादित संवेदनशील तस्वीरें भेजने की धमकी देते हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "The Reserve Bank of India (RBI) mandates that all legitimate digital lending apps must display their associated registered Bank or NBFC prominently.",
                hi = "भारतीय रिजर्व बैंक (RBI) का निर्देश है कि सभी वैध डिजिटल लेंडिंग ऐप को अपने संबद्ध पंजीकृत बैंक या NBFC को प्रमुखता से प्रदर्शित करना चाहिए।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Demanding permission to read your private contacts, SMS, and photo gallery.", hi = "आपके निजी संपर्कों, SMS और फोटो गैलरी को पढ़ने की अनुमति मांगना।"),
                LocalizedText(en = "The loan app is distributed via external link (APK) rather than verified Play Store listing.", hi = "ऋण ऐप सत्यापित प्ले स्टोर लिस्टिंग के बजाय बाहरी लिंक (APK) के माध्यम से वितरित किया जाता है।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Only borrow money from established banks or RBI-approved NBFCs.", hi = "केवल स्थापित बैंकों या आरबीआई द्वारा अनुमोदित एनबीएफसी से ही ऋण लें।"),
                LocalizedText(en = "Never install unknown financial apps that request excessive administrative device permissions.", hi = "अत्यधिक प्रशासनिक डिवाइस अनुमतियों का अनुरोध करने वाले अज्ञात वित्तीय ऐप को कभी इंस्टॉल न करें।")
            )
        )
    }

    private fun generateWhatsAppScam(day: Int, name: String, nameHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_WHATSAPP_$day",
            category = "WHATSAPP_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Impersonation Scam",
            scamTypeLabelHi = "भेष बदलना घोटाला",
            title = LocalizedText(
                en = "Family Member Emergency Call",
                hi = "पारिवारिक सदस्य आपातकालीन कॉल"
            ),
            scenario = LocalizedText(
                en = "You receive a message from an unknown number on WhatsApp. The profile picture is of your close relative '$name'. They write: 'I had an accident, my phone is damaged, and I need ₹$amount for urgent hospital bills. Please transfer to this UPI.'",
                hi = "आपको व्हाट्सएप पर एक अज्ञात नंबर से संदेश प्राप्त होता है। प्रोफाइल पिक्चर आपके करीबी रिश्तेदार '$name' की है। वे लिखते हैं: 'मेरा एक्सीडेंट हो गया है, मेरा फोन खराब है, और मुझे अस्पताल के बिलों के लिए ₹$amount की जरूरत है। कृपया इस यूपीआई पर ट्रांसफर करें।'"
            ),
            question = LocalizedText(
                en = "How can you securely verify this request?",
                hi = "आप इस अनुरोध को सुरक्षित रूप से कैसे सत्यापित कर सकते हैं?"
            ),
            options = listOf(
                LocalizedText(en = "Transfer ₹$amount immediately to save your relative's life", hi = "अपने रिश्तेदार की जान बचाने के लिए तुरंत ₹$amount ट्रांसफर करें"),
                LocalizedText(en = "Call your relative on their known original number to verify the story directly", hi = "कहानी को सीधे सत्यापित करने के लिए अपने रिश्तेदार को उनके ज्ञात मूल नंबर पर कॉल करें"),
                LocalizedText(en = "Send half the money and ask for hospital receipts first", hi = "आधे पैसे भेजें और पहले अस्पताल की रसीदें मांगें"),
                LocalizedText(en = "Report the number to WhatsApp but make the transfer anyway", hi = "व्हाट्सएप को नंबर की रिपोर्ट करें लेकिन फिर भी ट्रांसफर कर दें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Fraudsters pull publicly available DP photos from social media profiles, and create fake WhatsApp accounts to target friends and family with urgent 'accident' or 'arrest' emergency stories.",
                hi = "धोखेबाज सोशल मीडिया प्रोफाइल से सार्वजनिक रूप से उपलब्ध डीपी फोटो निकालते हैं, और तत्काल 'दुर्घटना' या 'गिरफ्तारी' की आपातकालीन कहानियों के साथ दोस्तों और परिवार को लक्षित करने के लिए नकली व्हाट्सएप खाते बनाते हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "Impersonation scams succeed because they trigger protective human emotions—care and urgency—preventing critical logical thinking.",
                hi = "भेष बदलने वाले घोटाले इसलिए सफल होते हैं क्योंकि वे सुरक्षात्मक मानवीय भावनाओं—देखभाल और तात्कालिकता—को सक्रिय करते हैं, जिससे तार्किक सोच अवरुद्ध हो जाती है।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "A message claiming to be a relative using a completely unknown 'new' mobile number.", hi = "पूरी तरह से अज्ञात 'नए' मोबाइल नंबर का उपयोग करके किसी रिश्तेदार के होने का दावा करने वाला संदेश।"),
                LocalizedText(en = "Demanding immediate transfer of funds via UPI to a random personal name.", hi = "एक यादृच्छिक व्यक्तिगत नाम पर यूपीआई के माध्यम से तुरंत धन हस्तांतरित करने की मांग करना।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Always establish voice confirmation over their official personal number before transferring funds.", hi = "धन हस्तांतरित करने से पहले हमेशा उनके आधिकारिक व्यक्तिगत नंबर पर आवाज द्वारा पुष्टि स्थापित करें।"),
                LocalizedText(en = "Set your WhatsApp profile photo visibility to 'My Contacts Only' to prevent photo harvesting.", hi = "फोटो चोरी को रोकने के लिए अपनी व्हाट्सएप प्रोफाइल फोटो दृश्यता को 'केवल मेरे संपर्क' पर सेट करें।")
            )
        )
    }

    private fun generateTelegramScam(day: Int, name: String, nameHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_TELEGRAM_$day",
            category = "TELEGRAM_SCAM",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Telegram Task Fraud",
            scamTypeLabelHi = "टेलीग्राम कार्य घोटाला",
            title = LocalizedText(
                en = "Telegram VIP Commission Pool",
                hi = "टेलीग्राम वीआईपी कमीशन पूल"
            ),
            scenario = LocalizedText(
                en = "You are added to a Telegram group run by '$name' where users post screenshots of huge profits. The admin tells you that if you invest ₹$amount in their specialized VIP tier, they will triple your return in 2 hours.",
                hi = "आपको '$name' द्वारा चलाए जा रहे एक टेलीग्राम समूह में जोड़ा जाता है जहां उपयोगकर्ता भारी मुनाफे के स्क्रीनशॉट पोस्ट करते हैं। व्यवस्थापक आपको बताता है कि यदि आप उनके विशेष वीआईपी टियर में ₹$amount का निवेश करते हैं, तो वे 2 घंटे में आपका रिटर्न तिगुना कर देंगे।"
            ),
            question = LocalizedText(
                en = "What is the safest action here?",
                hi = "यहाँ सबसे सुरक्षित कार्रवाई क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Invest ₹$amount to see if you can withdraw the profits", hi = "यह देखने के लिए ₹$amount का निवेश करें कि क्या आप मुनाफा निकाल सकते हैं"),
                LocalizedText(en = "Leave the group and report the admin for cyber-fraud immediately", hi = "समूह छोड़ें और साइबर धोखाधड़ी के लिए तुरंत व्यवस्थापक की रिपोर्ट करें"),
                LocalizedText(en = "Ask other group members in private if they really got paid", hi = "निजी तौर पर समूह के अन्य सदस्यों से पूछें कि क्या उन्हें वास्तव में भुगतान मिला है"),
                LocalizedText(en = "Tell the admin you will invest once you receive a free trial profit", hi = "व्यवस्थापक से कहें कि जब आपको एक मुफ्त परीक्षण लाभ प्राप्त होगा तब आप निवेश करेंगे")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "These Telegram task channels are coordinated traps. The screenshots posted are either fake or from other scammers in the same gang. Any channel that asks you to pay money to withdraw earned commissions is an investment scam.",
                hi = "ये टेलीग्राम कार्य चैनल समन्वित जाल हैं। पोस्ट किए गए स्क्रीनशॉट या तो नकली हैं या उसी गिरोह के अन्य घोटालेबाजों के हैं। कोई भी चैनल जो आपसे अर्जित कमीशन वापस लेने के लिए पैसे का भुगतान करने के लिए कहता है, वह एक निवेश घोटाला है।"
            ),
            didYouKnow = LocalizedText(
                en = "Telegram groups can host up to 200,000 members, allowing fraudsters to run large-scale automated social engineering operations simultaneously.",
                hi = "टेलीग्राम समूहों में 200,000 तक सदस्य हो सकते हैं, जिससे धोखेबाज बड़े पैमाने पर स्वचालित सोशल इंजीनियरिंग संचालन एक साथ चला सकते हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Being added to an unknown group chat without your active consent.", hi = "आपकी सक्रिय सहमति के बिना किसी अज्ञात समूह चैट में जोड़ा जाना।"),
                LocalizedText(en = "The group restricts normal members from posting messages, leaving only admins to post screenshots.", hi = "समूह सामान्य सदस्यों को संदेश पोस्ट करने से रोकता है, जिससे केवल व्यवस्थापक ही स्क्रीनशॉट पोस्ट कर सकते हैं।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Adjust your Telegram privacy settings: Settings > Privacy and Security > Groups > My Contacts Only.", hi = "अपनी टेलीग्राम गोपनीयता सेटिंग्स समायोजित करें: सेटिंग्स > गोपनीयता और सुरक्षा > समूह > केवल मेरे संपर्क।"),
                LocalizedText(en = "Never transfer funds to private bank accounts in hopes of unlocking digital work salaries.", hi = "डिजिटल कार्य वेतन अनलॉक करने की उम्मीद में कभी भी निजी बैंक खातों में धन हस्तांतरित न करें।")
            )
        )
    }

    private fun generateSocialEngineering(day: Int, name: String, nameHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_SOCIAL_$day",
            category = "SOCIAL_ENG",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Social Engineering",
            scamTypeLabelHi = "सामाजिक इंजीनियरिंग",
            title = LocalizedText(
                en = "Accidental UPI Fund Transfer",
                hi = "गलती से यूपीआई फंड ट्रांसफर"
            ),
            scenario = LocalizedText(
                en = "A distressed caller '$name' claims they sent ₹$amount to your UPI phone number by mistake while paying for their family medical emergency. They request you to immediately transfer the money back to their UPI ID.",
                hi = "एक परेशान कॉलर '$name' का दावा है कि उन्होंने अपने परिवार की चिकित्सा आपात स्थिति के लिए भुगतान करते समय गलती से आपके यूपीआई फोन नंबर पर ₹$amount भेज दिया था। वे आपसे तुरंत पैसे उनके यूपीआई आईडी पर वापस स्थानांतरित करने का अनुरोध करते हैं।"
            ),
            question = LocalizedText(
                en = "What is the secure way to address this scenario?",
                hi = "इस परिदृश्य को संबोधित करने का सुरक्षित तरीका क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Transfer ₹$amount back immediately out of human kindness", hi = "मानवीय दयालुता के कारण तुरंत ₹$amount वापस स्थानांतरित करें"),
                LocalizedText(en = "Check your bank statement. If received, ask them to request a formal bank reversal", hi = "अपना बैंक विवरण जांचें। यदि प्राप्त हुआ है, तो उनसे औपचारिक बैंक रिवर्सल का अनुरोध करने के लिए कहें"),
                LocalizedText(en = "Give them cash instead of making a digital transfer", hi = "डिजिटल ट्रांसफर करने के बजाय उन्हें नकद दें"),
                LocalizedText(en = "Keep the money and block their phone number immediately", hi = "पैसे रख लें और तुरंत उनके फोन नंबर को ब्लॉक कर दें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "This is a common social engineering tactic. Scammers use fake SMS notifications to mimic credit alerts without actually transferring any money. If you send them 'refunds' before checking your bank statement, you lose your own money.",
                hi = "यह एक सामान्य सामाजिक इंजीनियरिंग रणनीति है। घोटालेबाज वास्तव में कोई पैसा स्थानांतरित किए बिना क्रेडिट अलर्ट की नकल करने के लिए नकली SMS सूचनाओं का उपयोग करते हैं। यदि आप अपना बैंक विवरण जांचने से पहले उन्हें 'रिफंड' भेजते हैं, तो आप अपना पैसा खो देते हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "Fake SMS generation apps can duplicate bank alphanumeric sender headers (e.g. IM-BANK) to craft highly deceptive fake transaction alerts.",
                hi = "फर्जी SMS जनरेशन ऐप बैंक के अल्फ़ान्यूमेरिक सेंडर हेडर (जैसे IM-BANK) की नकल कर अत्यधिक भ्रामक नकली लेनदेन अलर्ट बना सकते हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "The transaction alert SMS was received from a standard personal mobile number instead of the official bank carrier.", hi = "लेनदेन अलर्ट SMS आधिकारिक बैंक कैरियर के बजाय एक सामान्य व्यक्तिगत मोबाइल नंबर से प्राप्त हुआ था।"),
                LocalizedText(en = "No actual balance update is visible when opening your official banking application.", hi = "अपना आधिकारिक बैंकिंग एप्लिकेशन खोलने पर कोई वास्तविक शेष अपडेट दिखाई नहीं देता है।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Never believe incoming transaction SMS alerts blindly. Verify your balance directly via bank app.", hi = "आने वाले लेनदेन SMS अलर्ट पर कभी भी आँख मूंदकर विश्वास न करें। बैंक ऐप के माध्यम से सीधे अपना बैलेंस सत्यापित करें।"),
                LocalizedText(en = "Ask the caller to contact their UPI provider to initiate standard chargeback reversals formally.", hi = "कॉलर से कहें कि वे औपचारिक रूप से मानक चार्जबैक रिवर्सल शुरू करने के लिए अपने यूपीआई प्रदाता से संपर्क करें।")
            )
        )
    }

    private fun generateAiVoiceScam(day: Int, name: String, nameHi: String, amount: Int, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_AIVOICE_$day",
            category = "AI_VOICE",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "AI Voice Cloning",
            scamTypeLabelHi = "एआई वॉयस क्लोनिंग",
            title = LocalizedText(
                en = "Deepfake Kidnapping Call",
                hi = "डीपफेक अपहरण कॉल"
            ),
            scenario = LocalizedText(
                en = "You receive a phone call from an unknown number. A voice that sounds exactly like your friend '$name' is crying, claiming they have been arrested or kidnapped, and begs you to transfer ₹$amount immediately to a lawyer's UPI ID.",
                hi = "आपको किसी अज्ञात नंबर से फोन कॉल आता है। एक आवाज़ जो बिल्कुल आपके दोस्त '$name' की तरह लगती है, रो रही है, दावा कर रही है कि उसे गिरफ्तार या अगवा कर लिया गया है, और आपसे तुरंत एक वकील की यूपीआई आईडी पर ₹$amount ट्रांसफर करने की भीख मांगती है।"
            ),
            question = LocalizedText(
                en = "What is the safest immediate action?",
                hi = "सबसे सुरक्षित तत्काल कार्रवाई क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Transfer ₹$amount to secure their release immediately", hi = "उनकी रिहाई सुनिश्चित करने के लिए तुरंत ₹$amount ट्रांसफर करें"),
                LocalizedText(en = "Hang up and call your friend on their known personal phone number directly", hi = "फोन काटें और सीधे अपने दोस्त को उनके ज्ञात व्यक्तिगत फोन नंबर पर कॉल करें"),
                LocalizedText(en = "Keep talking to negotiate a lower ransom with the caller", hi = "कॉलर के साथ कम फिरौती के लिए बातचीत जारी रखें"),
                LocalizedText(en = "Provide your credit card details to the caller to process payment", hi = "भुगतान की प्रक्रिया के लिए कॉलर को अपने क्रेडिट कार्ड का विवरण प्रदान करें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Generative AI tools can duplicate anyone's voice with 99% accuracy using just a 5-second audio sample from public social media reels. Fraudsters use deepfake voice clones to stage emergency calls targeting friends and family.",
                hi = "जेनेरेटिव एआई टूल सार्वजनिक सोशल मीडिया रील्स से केवल 5 सेकंड के ऑडियो नमूने का उपयोग करके 99% सटीकता के साथ किसी की भी आवाज की नकल कर सकते हैं। धोखेबाज दोस्तों और परिवार को लक्षित करने वाले आपातकालीन कॉल करने के लिए डीपफेक वॉयस क्लोन का उपयोग करते हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "AI voice synthesis can recreate emotional inflections, crying sounds, and background noises, making deepfake voice scams incredibly convincing.",
                hi = "एआई आवाज संश्लेषण भावनात्मक उतार-चढ़ाव, रोने की आवाजों और पृष्ठभूमि के शोर को फिर से बना सकता है, जिससे डीपफेक आवाज घोटाले अविश्वसनीय रूप से कायल हो जाते हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "The caller is using an unknown number but claiming to be someone close to you.", hi = "कॉलर एक अज्ञात नंबर का उपयोग कर रहा है लेकिन आपके करीबी होने का दावा कर रहा है।"),
                LocalizedText(en = "Extreme pressure to prevent you from hanging up or speaking to anyone else.", hi = "आपको फोन काटने या किसी और से बात करने से रोकने के लिए अत्यधिक दबाव।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Establish a secret 'family safety word' that only you and your close relatives know to verify identity during emergencies.", hi = "आपात स्थिति के दौरान पहचान सत्यापित करने के लिए एक गुप्त 'पारिवारिक सुरक्षा शब्द' स्थापित करें जिसे केवल आप और आपके करीबी रिश्तेदार जानते हों।"),
                LocalizedText(en = "Always verify the situation by contacting the person directly on their established original number.", hi = "व्यक्ति से सीधे उनके स्थापित मूल नंबर पर संपर्क करके हमेशा स्थिति का सत्यापन करें।")
            )
        )
    }

    private fun generateFakeApps(day: Int, brand: String, brandHi: String, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_FAKEAPP_$day",
            category = "FAKE_APPS",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Fake Malware App",
            scamTypeLabelHi = "फर्जी मैलवेयर ऐप",
            title = LocalizedText(
                en = "Unofficial Premium App Download",
                hi = "अनौपचारिक प्रीमियम ऐप डाउनलोड"
            ),
            scenario = LocalizedText(
                en = "While browsing a website to watch free live events, a popup forces you to download '$brand Premium Companion'. Upon installing, the app asks for absolute device permissions to read your SMS and listen to notifications.",
                hi = "मुफ्त लाइव इवेंट देखने के लिए एक वेबसाइट ब्राउज़ करते समय, एक पॉपअप आपको '$brand प्रीमियम कंपेनियन' डाउनलोड करने के लिए मजबूर करता है। इंस्टॉल करने पर, ऐप आपके SMS को पढ़ने और सूचनाएं सुनने के लिए पूर्ण डिवाइस अनुमतियां मांगता है।"
            ),
            question = LocalizedText(
                en = "What is the correct security protocol here?",
                hi = "यहाँ सही सुरक्षा प्रोटोकॉल क्या है?"
            ),
            options = listOf(
                LocalizedText(en = "Approve all permissions to enjoy free premium content", hi = "मुफ्त प्रीमियम सामग्री का आनंद लेने के लिए सभी अनुमतियों को स्वीकृत करें"),
                LocalizedText(en = "Uninstall the app immediately. SMS permissions can allow hackers to steal your OTPs", hi = "ऐप को तुरंत अनइंस्टॉल करें। SMS अनुमतियां हैकर्स को आपके ओटीपी चोरी करने की अनुमति दे सकती हैं"),
                LocalizedText(en = "Grant notifications permission but decline SMS access", hi = "सूचनाओं की अनुमति दें लेकिन SMS पहुंच को अस्वीकार करें"),
                LocalizedText(en = "Share the APK file with your colleagues to check if they like it", hi = "यह जांचने के लिए कि क्या वे इसे पसंद करते हैं, APK फ़ाइल को अपने सहयोगियों के साथ साझा करें")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "This is malware posing as a premium app. Granting read SMS and Notification Listener permissions allows the malware to intercept your secure 2FA/OTP tokens silently and drain your bank account without your knowledge.",
                hi = "यह एक प्रीमियम ऐप के रूप में प्रच्छन्न मैलवेयर है। SMS पढ़ने और अधिसूचना श्रोता (Notification Listener) अनुमतियों को मंजूरी देने से मैलवेयर चुपचाप आपके सुरक्षित 2FA/ओटीपी टोकन को रोक सकता है और आपकी जानकारी के बिना आपके बैंक खाते को खाली कर सकता है।"
            ),
            didYouKnow = LocalizedText(
                en = "Notification listener permissions allow apps to read every incoming chat, transaction alerts, and private OTP popups from your status bar.",
                hi = "अधिसूचना श्रोता अनुमतियां ऐप्स को आपके स्टेटस बार से आने वाले प्रत्येक चैट, लेनदेन अलर्ट और निजी ओटीपी पॉपअप को पढ़ने की अनुमति देती हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "The app requires manual installation from outside official platforms (sideloading).", hi = "ऐप को आधिकारिक प्लेटफॉर्म के बाहर से मैन्युअल इंस्टॉलेशन (साइडलोडिंग) की आवश्यकता होती है।"),
                LocalizedText(en = "App requests sensitive administrative rights that are completely unrelated to its advertised features.", hi = "ऐप संवेदनशील प्रशासनिक अधिकारों का अनुरोध करता है जो इसकी विज्ञापित विशेषताओं से पूरी तरह से असंबंधित हैं।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Strictly download applications only from verified official app stores like Google Play.", hi = "कड़ाई से केवल गूगल प्ले जैसे सत्यापित आधिकारिक ऐप स्टोर से ही एप्लिकेशन डाउनलोड करें।"),
                LocalizedText(en = "Never approve SMS or overlay permissions for media streaming or utility tool apps.", hi = "मीडिया स्ट्रीमिंग या उपयोगिता उपकरण ऐप के लिए कभी भी SMS या ओवरले अनुमतियों को स्वीकृत न करें।")
            )
        )
    }

    private fun generateFakeCustomerCare(day: Int, brand: String, brandHi: String, difficulty: String, difficultyHi: String): DailyChallenge {
        return DailyChallenge(
            id = "CH_CARE_$day",
            category = "FAKE_CUSTOMER_CARE",
            difficulty = difficulty,
            difficultyHi = difficultyHi,
            scamTypeLabel = "Fake Support Support",
            scamTypeLabelHi = "फर्जी सपोर्ट कॉल",
            title = LocalizedText(
                en = "Search Engine Support Scam",
                hi = "सर्च इंजन सपोर्ट घोटाला"
            ),
            scenario = LocalizedText(
                en = "While searching Google for '$brand Customer Care Number', you find a 10-digit mobile number listed. Upon calling, the support agent tells you that your wallet is locked, and asks you to install 'AnyDesk' or 'TeamViewer' to resolve it.",
                hi = "गूगल पर '$brand कस्टमर केयर नंबर' सर्च करते समय, आपको एक 10-अंकीय मोबाइल नंबर सूचीबद्ध मिलता है। कॉल करने पर, सपोर्ट एजेंट आपको बताता है कि आपका वॉलेट लॉक है, और इसे हल करने के लिए आपको 'AnyDesk' या 'TeamViewer' इंस्टॉल करने के लिए कहता है।"
            ),
            question = LocalizedText(
                en = "Should you download the screen sharing application?",
                hi = "क्या आपको स्क्रीन शेयरिंग एप्लिकेशन डाउनलोड करना चाहिए?"
            ),
            options = listOf(
                LocalizedText(en = "Install AnyDesk and share the remote login code to fix the wallet issue", hi = "वॉलेट की समस्या को ठीक करने के लिए AnyDesk इंस्टॉल करें और रिमोट लॉगिन कोड साझा करें"),
                LocalizedText(en = "Refuse to install screen sharing apps. Official supports never demand remote device control", hi = "स्क्रीन शेयरिंग ऐप्स इंस्टॉल करने से इंकार करें। आधिकारिक सपोर्ट कभी भी रिमोट डिवाइस नियंत्रण की मांग नहीं करते"),
                LocalizedText(en = "Install AnyDesk but log out of your banking apps first", hi = "AnyDesk इंस्टॉल करें लेकिन पहले अपने बैंकिंग ऐप्स से लॉग आउट कर लें"),
                LocalizedText(en = "Share your phone password with the agent so they don't need AnyDesk", hi = "एजेंट के साथ अपने फोन का पासवर्ड साझा करें ताकि उन्हें AnyDesk की आवश्यकता न हो")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Google search results can be manipulated by scammers using SEO tricks. Official supports will never ask you to install screen-sharing tools like AnyDesk. Once shared, they can see your screens, read your OTPs, and steal your money.",
                hi = "एसईओ ट्रिक्स का उपयोग करके घोटालेबाजों द्वारा गूगल सर्च परिणामों में हेरफेर किया जा सकता है। आधिकारिक सहायता कभी भी आपको AnyDesk जैसे स्क्रीन-शेयरिंग टूल इंस्टॉल करने के लिए नहीं कहेगी। एक बार साझा करने के बाद, वे आपकी स्क्रीन देख सकते हैं, आपके ओटीपी पढ़ सकते हैं और आपके पैसे चुरा सकते हैं।"
            ),
            didYouKnow = LocalizedText(
                en = "Remote access tools let external actors record your screen inputs, tap on buttons, and bypass secure verification systems entirely.",
                hi = "रिमोट एक्सेस टूल बाहरी लोगों को आपकी स्क्रीन इनपुट रिकॉर्ड करने, बटन पर टैप करने और सुरक्षित सत्यापन प्रणालियों को पूरी तरह से बायपास करने की अनुमति देते हैं।"
            ),
            warningSigns = listOf(
                LocalizedText(en = "Customer care number listed as a standard personal 10-digit mobile number starting with 9, 8, 7, or 6.", hi = "कस्टमर केयर नंबर एक सामान्य व्यक्तिगत 10-अंकीय मोबाइल नंबर के रूप में सूचीबद्ध है जो 9, 8, 7 या 6 से शुरू होता है।"),
                LocalizedText(en = "Demanding installation of screen sharing applications to assist with standard financial queries.", hi = "मानक वित्तीय प्रश्नों में सहायता के लिए स्क्रीन शेयरिंग एप्लिकेशन इंस्टॉल करने की मांग करना।")
            ),
            howToStaySafe = listOf(
                LocalizedText(en = "Always look up contact details only inside the official contact section of the platform's app or official verified web domain.", hi = "हमेशा प्लेटफॉर्म के ऐप या आधिकारिक सत्यापित वेब डोमेन के आधिकारिक संपर्क अनुभाग के भीतर ही संपर्क विवरण देखें।"),
                LocalizedText(en = "Never share screen streaming codes or administrative permissions with external actors.", hi = "बाहरी लोगों के साथ कभी भी स्क्रीन स्ट्रीमिंग कोड या प्रशासनिक अनुमतियां साझा न करें।")
            )
        )
    }
}
