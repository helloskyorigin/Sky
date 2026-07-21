package com.skyorigin.threatshieldai

object ScamExamplesData {
    val categories = listOf(
        "Phishing",
        "OTP Fraud",
        "UPI Scam",
        "QR Code Scam",
        "Fake Bank Call",
        "Fake KYC",
        "Fake Delivery",
        "Job Scam",
        "Investment Scam",
        "Lottery Scam",
        "Tech Support Scam",
        "WhatsApp Scam",
        "Telegram Scam",
        "Instagram Scam",
        "Fake Customer Care"
    )

    val scams: List<ScamExample>

    init {
        val baseScams = mutableListOf<ScamExample>()

        // 1. Phishing
        baseScams.add(
            ScamExample(
                id = 1,
                category = "Phishing",
                difficulty = "Medium",
                titleEn = "Netflix Account Suspended Alert",
                titleHi = "नेटफ्लिक्स अकाउंट सस्पेंशन अलर्ट",
                messageEn = "Dear Member, Your subscription payment failed. We will suspend your membership within 24 hours if you do not update your billing info. Click here to reactivate: http://netflix-billing-update.com",
                messageHi = "प्रिय सदस्य, आपका सब्सक्रिप्शन भुगतान विफल हो गया। यदि आप अपनी बिलिंग जानकारी अपडेट नहीं करते हैं तो हम 24 घंटों के भीतर आपकी सदस्यता निलंबित कर देंगे। रीएक्टिवेट करने के लिए यहाँ क्लिक करें: http://netflix-billing-update.com",
                dangerEn = "Harvester websites steal your credit card details, CVV, and login passwords, allowing unauthorized billing charges.",
                dangerHi = "नकली वेबसाइटें आपके क्रेडिट कार्ड विवरण, सीवीवी और लॉगिन पासवर्ड चुरा लेती हैं, जिससे अनधिकृत लेनदेन होने लगते हैं।",
                redFlagsEn = listOf("Urgent threat of 24-hour suspension", "Non-secure domain name netflix-billing-update.com instead of netflix.com", "Generic greeting 'Dear Member'"),
                redFlagsHi = listOf("24 घंटे के निलंबन की तत्काल धमकी", "netflix.com के बजाय गैर-सुरक्षित डोमेन netflix-billing-update.com", "सामान्य अभिवादन 'प्रिय सदस्य'"),
                safeResponseEn = "Never click links in SMS or email alerts. Open the official Netflix app or website separately to check your billing status.",
                safeResponseHi = "एसएमएस या ईमेल अलर्ट में कभी भी लिंक पर क्लिक न करें। अपनी बिलिंग स्थिति की जांच करने के लिए आधिकारिक नेटफ्लिक्स ऐप या वेबसाइट अलग से खोलें।"
            )
        )

        // 2. OTP Fraud
        baseScams.add(
            ScamExample(
                id = 2,
                category = "OTP Fraud",
                difficulty = "Hard",
                titleEn = "Aadhar-Linked Bank Activation OTP",
                titleHi = "आधार-लिंक्ड बैंक एक्टिवेशन ओटीपी",
                messageEn = "A caller posing as a government official claims your Aadhaar card needs verification. 'I have sent an SMS code to verify. Please tell me the OTP immediately or your account will be locked.'",
                messageHi = "एक कॉलर जो सरकारी अधिकारी होने का ढोंग करता है, दावा करता है कि आपके आधार कार्ड को सत्यापन की आवश्यकता है। 'मैंने सत्यापित करने के लिए एक एसएमएस कोड भेजा है। कृपया मुझे तुरंत ओटीपी बताएं वरना आपका खाता लॉक कर दिया जाएगा।'",
                dangerEn = "The OTP grants the scammer full authority to link their own number or authorize direct fund withdrawals from your bank.",
                dangerHi = "ओटीपी स्कैमर को अपना नंबर लिंक करने या आपके बैंक से सीधे पैसे निकालने का पूरा अधिकार देता है।",
                redFlagsEn = listOf("Caller demanding verification OTP", "Threat of immediate account locking", "Pressure to act quickly without thinking"),
                redFlagsHi = listOf("कॉलर सत्यापन ओटीपी की मांग कर रहा है", "तत्काल खाता लॉक होने का खतरा", "बिना सोचे-समझे तुरंत कार्रवाई करने का दबाव"),
                safeResponseEn = "Hang up. No authority or bank ever requests a transaction or verification OTP over a phone call.",
                safeResponseHi = "फोन काट दें। कोई भी प्राधिकरण या बैंक कभी भी फोन कॉल पर लेनदेन या सत्यापन ओटीपी का अनुरोध नहीं करता है।"
            )
        )

        // 3. UPI Scam
        baseScams.add(
            ScamExample(
                id = 3,
                category = "UPI Scam",
                difficulty = "Easy",
                titleEn = "OLX Buyer Double Payment UPI Request",
                titleHi = "OLX खरीदार डबल पेमेंट यूपीआई अनुरोध",
                messageEn = "A buyer on OLX claims they accidentally sent you double payment. They send a UPI Request asking you to 'Approve' and type your UPI PIN to return the extra money.",
                messageHi = "OLX पर एक खरीदार का दावा है कि उसने गलती से आपको डबल भुगतान भेज दिया है। वे एक यूपीआई अनुरोध (Request) भेजते हैं जिसमें आपसे 'स्वीकार' करने और अतिरिक्त पैसे वापस करने के लिए अपना यूपीआई पिन टाइप करने के लिए कहा जाता है।",
                dangerEn = "Approving a UPI collection request with your PIN immediately debits money from your account, rather than receiving it.",
                dangerHi = "अपने पिन के साथ यूपीआई कलेक्ट अनुरोध को स्वीकार करने से आपके खाते से तुरंत पैसे कट जाते हैं, न कि प्राप्त होते हैं।",
                redFlagsEn = listOf("Request to input UPI PIN to receive money", "UPI notification marked as 'Collect' rather than credit", "Urgency from buyer"),
                redFlagsHi = listOf("पैसे प्राप्त करने के लिए यूपीआई पिन दर्ज करने का अनुरोध", "क्रेडिट के बजाय 'कलेक्ट' के रूप में चिह्नित यूपीआई अधिसूचना", "खरीदार की ओर से जल्दबाजी"),
                safeResponseEn = "Decline the request immediately. Remember, receiving money via UPI NEVER requires entering your PIN.",
                safeResponseHi = "अनुरोध को तुरंत अस्वीकार करें। याद रखें, यूपीआई के माध्यम से पैसे प्राप्त करने के लिए कभी भी अपना पिन दर्ज करने की आवश्यकता नहीं होती है।"
            )
        )

        // 4. QR Code Scam
        baseScams.add(
            ScamExample(
                id = 4,
                category = "QR Code Scam",
                difficulty = "Medium",
                titleEn = "Lucky Draw Reward Scratch Card QR",
                titleHi = "लकी ड्रा इनाम स्क्रैच कार्ड क्यूआर",
                messageEn = "You receive a physical pamphlet or WhatsApp message with a QR code claiming 'Scan this code to scratch and claim Rs. 2,000 cash prize directly in your account.'",
                messageHi = "आपको एक भौतिक पर्चा या व्हाट्सएप संदेश प्राप्त होता है जिसमें एक क्यूआर कोड होता है और दावा किया जाता है 'स्क्रैच करने के लिए इस कोड को स्कैन करें और सीधे अपने खाते में 2,000 रुपये का नकद पुरस्कार प्राप्त करें।'",
                dangerEn = "Scanning this code redirects you to a malicious payment page designed to debit funds once authorized with a PIN.",
                dangerHi = "इस कोड को स्कैन करने से आप एक नकली भुगतान पृष्ठ पर पहुंच जाते हैं, जिसे पिन के साथ अधिकृत होने पर आपके खाते से पैसे काटने के लिए डिज़ाइन किया गया है।",
                redFlagsEn = listOf("QR code scanned to receive a cash prize", "Unsolicited reward announcements", "Webpage requesting payment PIN or credit details"),
                redFlagsHi = listOf("नकद पुरस्कार प्राप्त करने के लिए क्यूआर कोड स्कैन करना", "अवांछित पुरस्कार घोषणाएं", "भुगतान पिन या क्रेडिट विवरण मांगने वाला वेबपेज"),
                safeResponseEn = "Never scan QR codes to receive funds. QR codes are strictly meant for outgoing payments.",
                safeResponseHi = "पैसे प्राप्त करने के लिए कभी भी क्यूआर कोड स्कैन न करें। क्यूआर कोड विशेष रूप से भुगतान करने के लिए होते हैं।"
            )
        )

        // 5. Fake Bank Call
        baseScams.add(
            ScamExample(
                id = 5,
                category = "Fake Bank Call",
                difficulty = "Medium",
                titleEn = "Credit Card Limit Increase Trap",
                titleHi = "क्रेडिट कार्ड लिमिट बढ़ाने का जाल",
                messageEn = "A caller claiming to represent your credit card provider offers to double your limit instantly without documentation. They request your card number, CVV, and OTP.",
                messageHi = "आपके क्रेडिट कार्ड प्रदाता का प्रतिनिधित्व करने का दावा करने वाला एक कॉलर बिना दस्तावेजों के आपकी सीमा को तुरंत दोगुना करने की पेशकश करता है। वे आपके कार्ड नंबर, सीवीवी और ओटीपी का अनुरोध करते हैं।",
                dangerEn = "Armed with these details, attackers make online purchases and empty your credit balance.",
                dangerHi = "इन विवरणों के साथ, हमलावर ऑनलाइन खरीदारी करते हैं और आपके क्रेडिट बैलेंस को साफ कर देते हैं।",
                redFlagsEn = listOf("Spoken request for CVV or expiry date", "Promises of instant zero-document upgrades", "Call originating from a standard 10-digit mobile number"),
                redFlagsHi = listOf("सीवीवी या समाप्ति तिथि के लिए मौखिक अनुरोध", "बिना दस्तावेज के तुरंत लिमिट बढ़ाने का वादा", "सामान्य 10-अंकीय मोबाइल नंबर से आने वाली कॉल"),
                safeResponseEn = "State that you will manage upgrades directly inside your banking app or local branch, then hang up.",
                safeResponseHi = "कॉल काट दें और कहें कि आप सीधे अपने बैंकिंग ऐप या स्थानीय शाखा के अंदर सीमा में सुधार का प्रबंधन करेंगे।"
            )
        )

        // 6. Fake KYC
        baseScams.add(
            ScamExample(
                id = 6,
                category = "Fake KYC",
                difficulty = "Hard",
                titleEn = "SIM Card Deactivation Threat KYC",
                titleHi = "सिम कार्ड डीएक्टिवेशन खतरा केवाईसी",
                messageEn = "An SMS states: 'Dear Customer, your Airtel SIM KYC has expired. Your outgoing services will suspend today. Please complete verification by calling +91-XXXXXXXXXX immediately.'",
                messageHi = "एक एसएमएस में लिखा है: 'प्रिय ग्राहक, आपका एयरटेल सिम केवाईसी समाप्त हो गया है। आपकी आउटगोइंग सेवाएं आज निलंबित कर दी जाएंगी। कृपया तुरंत +91-XXXXXXXXXX पर कॉल करके सत्यापन पूरा करें।'",
                dangerEn = "Fraudsters trick you into installing remote desktop screen sharing tools (like AnyDesk) to view and steal your banking login session.",
                dangerHi = "जालसाज आपकी बैंकिंग लॉगिन सेशन को देखने और चुराने के लिए रिमोट डेस्कटॉप स्क्रीन शेयरिंग टूल (जैसे AnyDesk) इंस्टॉल करने के लिए आपको गुमराह करते हैं।",
                redFlagsEn = listOf("Urgent threat of SIM block within hours", "Requests to call a personal 10-digit phone number for support", "Sender SMS header is a personal mobile number, not a brand code"),
                redFlagsHi = listOf("कुछ ही घंटों के भीतर सिम ब्लॉक करने की तत्काल धमकी", "सपोर्ट के लिए व्यक्तिगत 10-अंकीय फोन नंबर पर कॉल करने का अनुरोध", "भेजने वाले का एसएमएस हेडर एक व्यक्तिगत मोबाइल नंबर है, न कि कोई ब्रांड कोड"),
                safeResponseEn = "Verify KYC directly at an authorized operator outlet or through their official telecom utility application.",
                safeResponseHi = "अधिकृत ऑपरेटर आउटलेट पर जाकर या उनके आधिकारिक टेलीकॉम ऐप के माध्यम से सीधे केवाईसी सत्यापित करें।"
            )
        )

        // 7. Fake Delivery
        baseScams.add(
            ScamExample(
                id = 7,
                category = "Fake Delivery",
                difficulty = "Medium",
                titleEn = "Post Office Address Correction Scam",
                titleHi = "डाकघर पता सुधार घोटाला",
                messageEn = "An SMS claiming your India Post package could not be delivered due to an incomplete address. 'Update address and schedule delivery by paying Rs 5 fee here: http://indiapost-delivery-tracking.info'",
                messageHi = "एक एसएमएस जिसमें दावा किया गया है कि अधूरे पते के कारण आपका इंडिया पोस्ट पैकेज डिलीवर नहीं किया जा सका। 'पता अपडेट करें और यहां 5 रुपये शुल्क देकर डिलीवरी शेड्यूल करें: http://indiapost-delivery-tracking.info'",
                dangerEn = "The minor fee payment form captures your card credentials, and the script registers a massive automated transaction soon after.",
                dangerHi = "मामूली शुल्क भुगतान फॉर्म आपके कार्ड क्रेडेंशियल्स चुरा लेता है, और जल्द ही आपके खाते से एक बड़ा स्वचालित लेनदेन कर दिया जाता है।",
                redFlagsEn = listOf("SMS from standard personal mobile numbers", "Links mimicking official India Post using slightly altered text", "Payment requested to fix a basic delivery address error"),
                redFlagsHi = listOf("मानक व्यक्तिगत मोबाइल नंबरों से एसएमएस", "मामूली बदलाव के साथ इंडिया पोस्ट की नकल करने वाले लिंक", "सामान्य डिलीवरी पते की त्रुटि को ठीक करने के लिए भुगतान का अनुरोध"),
                safeResponseEn = "Do not pay. Check the official tracking ID on indiapost.gov.in manually, ignoring the external hyperlink.",
                safeResponseHi = "भुगतान न करें। बाहरी हाइपरलिंक को अनदेखा करते हुए, indiapost.gov.in पर मैन्युअल रूप से आधिकारिक ट्रैकिंग आईडी की जांच करें।"
            )
        )

        // 8. Job Scam
        baseScams.add(
            ScamExample(
                id = 8,
                category = "Job Scam",
                difficulty = "Easy",
                titleEn = "Part-Time YouTube Video Liking Job",
                titleHi = "पार्ट-टाइम यूट्यूब वीडियो लाइक करने का काम",
                messageEn = "A WhatsApp recruitment message offers Rs. 3,000 to Rs. 8,000 daily for simply liking YouTube videos. 'Work from home, 30 minutes daily. Contact our agent on Telegram.'",
                messageHi = "एक व्हाट्सएप संदेश में केवल यूट्यूब वीडियो पसंद करने के लिए रोजाना 3,000 से 8,000 रुपये देने की पेशकश की गई है। 'घर से काम करें, रोजाना 30 मिनट। टेलीग्राम पर हमारे एजेंट से संपर्क करें।'",
                dangerEn = "They build trust by paying small rewards first, then demand large deposit 'investment packages' to unlock higher tasks, freezing your money.",
                dangerHi = "वे पहले छोटे पुरस्कार देकर विश्वास बनाते हैं, फिर उच्च कार्यों को अनलॉक करने के लिए बड़ी जमा 'निवेश पैकेज' की मांग करते हैं, जिससे आपका पैसा ब्लॉक हो जाता है।",
                redFlagsEn = listOf("Unrealistically high payout for low-skill tasks", "Instructions to shift the conversation to Telegram", "Requirement to deposit funds or register on unverified portal schemes"),
                redFlagsHi = listOf("कम-कौशल वाले कार्यों के लिए अवास्तविक रूप से उच्च भुगतान", "बातचीत को टेलीग्राम पर शिफ्ट करने के निर्देश", "धनराशि जमा करने या असत्यापित पोर्टल योजनाओं पर पंजीकरण करने की आवश्यकता"),
                safeResponseEn = "Block the recruiter. Legitimate businesses never demand monetary deposits to unlock working rights or tasks.",
                safeResponseHi = "भर्ती करने वाले को ब्लॉक करें। वैध व्यवसाय कभी भी काम करने के अधिकारों या कार्यों को शुरू करने के लिए मौद्रिक जमा की मांग नहीं करते हैं।"
            )
        )

        // 9. Investment Scam
        baseScams.add(
            ScamExample(
                id = 9,
                category = "Investment Scam",
                difficulty = "Hard",
                titleEn = "Guaranteed 500% Crypto Arbitrage Scheme",
                titleHi = "गारंटीड 500% क्रिप्टो आर्बिट्राज योजना",
                messageEn = "An investor group claims they use AI bot technology to earn guaranteed 10% compounding interest daily. 'Send USDT to our wallet address. Watch your funds multiply on our tracking dashboard.'",
                messageHi = "एक निवेशक समूह का दावा है कि वे दैनिक रूप से 10% चक्रवृद्धि ब्याज अर्जित करने के लिए एआई बॉट तकनीक का उपयोग करते हैं। 'हमारे वॉलेट पते पर यूएसडीटी भेजें। हमारे ट्रैकिंग डैशबोर्ड पर अपने फंड को बढ़ता हुआ देखें।'",
                dangerEn = "The web dashboard shows fake artificial gains, but when you attempt to withdraw, they demand heavy 'taxes' or freeze your access.",
                dangerHi = "वेब डैशबोर्ड नकली कृत्रिम लाभ दिखाता है, लेकिन जब आप वापस निकालने का प्रयास करते हैं, तो वे भारी 'टैक्स' की मांग करते हैं या आपकी पहुंच को फ्रीज कर देते हैं।",
                redFlagsEn = listOf("Promises of 'guaranteed' triple digit returns", "Requiring deposits in cryptocurrency or anonymous bank transfers", "No registered SEBI or regulatory license numbers displayed"),
                redFlagsHi = listOf("'गारंटीड' तीन अंकों के रिटर्न का वादा", "क्रिप्टोकरेंसी या अज्ञात बैंक ट्रांसफर में जमा करने की आवश्यकता", "कोई पंजीकृत सेबी (SEBI) या विनियामक लाइसेंस नंबर प्रदर्शित नहीं होना"),
                safeResponseEn = "Avoid unverified, unregulated digital asset platforms. Invest only through SEBI-registered institutions.",
                safeResponseHi = "असत्यापित, अनियमित डिजिटल एसेट प्लेटफॉर्म से बचें। केवल सेबी-पंजीकृत संस्थानों के माध्यम से निवेश करें।"
            )
        )

        // 10. Lottery Scam
        baseScams.add(
            ScamExample(
                id = 10,
                category = "Lottery Scam",
                difficulty = "Easy",
                titleEn = "KBC Crorepati Lucky Draw Winner",
                titleHi = "केबीसी करोड़पति लकी ड्रा विजेता",
                messageEn = "A WhatsApp voice note accompanied by a poster showing your number won a Rs. 25 Lakh KBC lottery. 'To claim, transfer Rs. 15,000 bank registration fee to our manager.'",
                messageHi = "एक व्हाट्सएप वॉयस नोट के साथ एक पोस्टर जिसमें दिखाया गया है कि आपके नंबर ने 25 लाख रुपये की केबीसी लॉटरी जीती है। 'दावा करने के लिए, हमारे मैनेजर को 15,000 रुपये बैंक पंजीकरण शुल्क ट्रांसफर करें।'",
                dangerEn = "Once the fee is transferred, they request additional charges for custom clearing and then cease contact.",
                dangerHi = "एक बार शुल्क स्थानांतरित हो जाने के बाद, वे सीमा शुल्क निकासी के लिए अतिरिक्त शुल्क का अनुरोध करते हैं और फिर संपर्क बंद कर देते हैं।",
                redFlagsEn = listOf("Winning a lottery you never bought a ticket for", "Demands for advance processing fees before money distribution", "Voice notes recorded in highly unprofessional language"),
                redFlagsHi = listOf("लॉटरी जीतना जिसके लिए आपने कभी टिकट नहीं खरीदा", "पैसे वितरण से पहले अग्रिम प्रसंस्करण शुल्क की मांग", "अत्यंत अव्यवसायिक भाषा में रिकॉर्ड किए गए वॉयस नोट"),
                safeResponseEn = "Ignore and delete. No legitimate lottery program requests winners to pay tax or processing charges upfront.",
                safeResponseHi = "अनदेखा करें और हटा दें। कोई भी वास्तविक लॉटरी कार्यक्रम विजेताओं से अग्रिम रूप से टैक्स या प्रोसेसिंग शुल्क का भुगतान करने का अनुरोध नहीं करता है।"
            )
        )

        // 11. Tech Support Scam
        baseScams.add(
            ScamExample(
                id = 11,
                category = "Tech Support Scam",
                difficulty = "Medium",
                titleEn = "Microsoft Windows Virus Firewall Alert",
                titleHi = "माइक्रोसॉफ्ट विंडोज वायरस फ़ायरवॉल अलर्ट",
                messageEn = "A full-screen browser pop-up freezes your PC with loud audio claiming: 'WINDOWS HAS BEEN COMPROMISED. Credit details are being leaked. Call Microsoft Support immediately: 1800-XXX-XXXX.'",
                messageHi = "एक फुल-स्क्रीन ब्राउज़र पॉप-अप तेज़ ऑडियो के साथ आपके पीसी को फ्रीज कर देता है और दावा करता है: 'विंडोज से समझौता किया गया है। क्रेडिट विवरण लीक हो रहे हैं। माइक्रोसॉफ्ट सपोर्ट को तुरंत कॉल करें: 1800-XXX-XXXX।'",
                dangerEn = "Scammers charge thousands of rupees to run basic disk cleaner tools or install malicious tracking trojans.",
                dangerHi = "स्कैमर बुनियादी डिस्क क्लीनर टूल चलाने या दुर्भावनापूर्ण ट्रैकिंग ट्रोजन इंस्टॉल करने के लिए हजारों रुपये वसूलते हैं।",
                redFlagsEn = listOf("Loud alarm or pop-ups that lock your browser tabs", "Demands to call emergency support hotlines immediately", "Requests to purchase commercial gift cards to cover repairs"),
                redFlagsHi = listOf("तेज़ अलार्म या पॉप-अप जो आपके ब्राउज़र टैब को लॉक कर देते हैं", "आपातकालीन सहायता हॉटलाइन पर तुरंत कॉल करने की मांग", "मरम्मत के भुगतान के लिए गिफ्ट कार्ड खरीदने का अनुरोध"),
                safeResponseEn = "Close your web browser using Task Manager. Install reputable local anti-virus scanners.",
                safeResponseHi = "टास्क मैनेजर का उपयोग करके अपना वेब ब्राउज़र बंद करें। प्रतिष्ठित स्थानीय एंटी-वायरस स्कैनर इंस्टॉल करें।"
            )
        )

        // 12. WhatsApp Scam
        baseScams.add(
            ScamExample(
                id = 12,
                category = "WhatsApp Scam",
                difficulty = "Hard",
                titleEn = "Friend in Emergency Money Request",
                titleHi = "आपातकालीन स्थिति में मित्र द्वारा पैसे का अनुरोध",
                messageEn = "You receive a message from an unknown number with a picture of your close friend: 'I changed my number. I am in the hospital with an emergency, please GPay Rs. 10,000 to this doctor's number immediately. I will return it tonight.'",
                messageHi = "आपको अपने करीबी दोस्त की तस्वीर के साथ एक अज्ञात नंबर से संदेश प्राप्त होता है: 'मैंने अपना नंबर बदल लिया है। मैं आपातकालीन स्थिति में अस्पताल में हूं, कृपया इस डॉक्टर के नंबर पर तुरंत 10,000 रुपये भेजें। मैं इसे आज रात वापस कर दूंगा।'",
                dangerEn = "You transfer money to an attacker's burner wallet, which is instantly withdrawn and untraceable.",
                dangerHi = "आप हमलावर के बर्नर वॉलेट में पैसे ट्रांसफर करते हैं, जिसे तुरंत निकाल लिया जाता है और उसका पता नहीं लगाया जा सकता है।",
                redFlagsEn = listOf("Urgent request for money from a newly changed number", "Emotional panic tactics regarding medical crises", "Hesitancy to take regular phone calls"),
                redFlagsHi = listOf("नए बदले गए नंबर से पैसे के लिए तत्काल अनुरोध", "चिकित्सा संकटों के बारे में भावनात्मक पैनिक तरकीबें", "नियमित फोन कॉल लेने में आनाकानी करना"),
                safeResponseEn = "Call your friend on their original, verified telephone number to confirm before performing any transaction.",
                safeResponseHi = "कोई भी लेनदेन करने से पहले पुष्टि करने के लिए अपने मित्र को उनके मूल, सत्यापित टेलीफोन नंबर पर कॉल करें।"
            )
        )

        // 13. Telegram Scam
        baseScams.add(
            ScamExample(
                id = 13,
                category = "Telegram Scam",
                difficulty = "Medium",
                titleEn = "Telegram Crypto Signal Group Admin Trade",
                titleHi = "टेलीग्राम क्रिप्टो सिग्नल ग्रुप एडमिन ट्रेड",
                messageEn = "An admin of a crypto channel messages you: 'We are launching a VIP pool for small investors. Send 100 TRX, and get back 1,000 TRX within 2 hours. Limited spots remaining.'",
                messageHi = "एक क्रिप्टो चैनल के एडमिन ने आपको संदेश भेजा: 'हम छोटे निवेशकों के लिए एक वीआईपी पूल लॉन्च कर रहे हैं। 100 टीआरएक्स भेजें, और 2 घंटे के भीतर 1,000 टीआरएक्स वापस पाएं। सीमित स्थान उपलब्ध हैं।'",
                dangerEn = "The admin accounts are fake identities, and the funds sent are immediately transferred to tumbler networks.",
                dangerHi = "एडमिन खाते नकली पहचान होते हैं, और भेजे गए फंड तुरंत टंबलर नेटवर्क में स्थानांतरित कर दिए जाते हैं।",
                redFlagsEn = listOf("Unsolicited direct messages from 'Group Admins'", "Unrealistically fast compounding returns (10x in 2 hours)", "Requirement to send digital assets directly to a private wallet"),
                redFlagsHi = listOf("'ग्रुप एडमिन' से अवांछित सीधे संदेश", "अवास्तविक रूप से तेज़ कंपाउंडिंग रिटर्न (2 घंटे में 10 गुना)", "डिजिटल संपत्ति को सीधे एक निजी वॉलेट में भेजने की आवश्यकता"),
                safeResponseEn = "Disable 'Who can add me to groups' in Telegram settings. Block and report any direct message pitching financial pools.",
                safeResponseHi = "टेलीग्राम सेटिंग्स में 'मुझे ग्रुप में कौन जोड़ सकता है' को अक्षम करें। वित्तीय निवेश की बात करने वाले किसी भी सीधे संदेश को ब्लॉक और रिपोर्ट करें।"
            )
        )

        // 14. Instagram Scam
        baseScams.add(
            ScamExample(
                id = 14,
                category = "Instagram Scam",
                difficulty = "Medium",
                titleEn = "Verified Account Giveaway Winner",
                titleHi = "सत्यापित खाता गिवअवे विजेता",
                messageEn = "A verified profile (or clone account) DMs you: 'Congratulations, you won our luxury iPhone giveaway! Just click this verification link to log in with Instagram and fill your address details.'",
                messageHi = "एक सत्यापित प्रोफ़ाइल (या क्लोन खाता) आपको डीएम करता है: 'बधाई हो, आपने हमारा लक्ज़री आईफोन गिवअवे जीता है! बस इंस्टाग्राम के साथ लॉग इन करने और अपना पता विवरण भरने के लिए इस सत्यापन लिंक पर क्लिक करें।'",
                dangerEn = "The link takes you to a duplicate Instagram login page designed to record and hijack your profile credentials.",
                dangerHi = "लिंक आपको एक डुप्लिकेट इंस्टाग्राम लॉगिन पेज पर ले जाता है जिसे आपके प्रोफाइल क्रेडेंशियल रिकॉर्ड करने और हैक करने के लिए डिज़ाइन किया गया है।",
                redFlagsEn = listOf("Unsolicited DM claiming giveaway wins", "Log-in screen prompts to claim external items", "Slightly misspelled profile name variant of the actual famous brand"),
                redFlagsHi = listOf("गिवअवे जीतने का दावा करने वाला अवांछित डीएम", "बाहरी सामानों का दावा करने के लिए लॉग-इन स्क्रीन का खुलना", "वास्तविक प्रसिद्ध ब्रांड के नाम में मामूली वर्तनी परिवर्तन"),
                safeResponseEn = "Never input credentials on pages opened through Instagram direct messages. Inspect the sender's account creation date and country.",
                safeResponseHi = "इंस्टाग्राम सीधे संदेशों के माध्यम से खोले गए पेजों पर कभी भी क्रेडेंशियल दर्ज न करें। प्रेषक के खाता निर्माण की तारीख और देश का निरीक्षण करें।"
            )
        )

        // 15. Fake Customer Care
        baseScams.add(
            ScamExample(
                id = 15,
                category = "Fake Customer Care",
                difficulty = "Easy",
                titleEn = "Google Map Helpline Scam",
                titleHi = "गूगल मैप हेल्पलाइन घोटाला",
                messageEn = "You search for 'Zomato Customer Care Number' on Google Search and find a mobile number added on a Google Map review or community forum. Calling it, the executive requests your payment app password.",
                messageHi = "आप Google सर्च पर 'ज़ोमैटो कस्टमर केयर नंबर' खोजते हैं और Google मैप रिव्यू या कम्युनिटी फ़ोरम पर जोड़ा गया एक मोबाइल नंबर पाते हैं। इसे कॉल करने पर, कर्मचारी आपके भुगतान ऐप पासवर्ड का अनुरोध करता है।",
                dangerEn = "These numbers are set up by cybercriminals to intercept inquiries, enabling them to execute fraudulent refund debits.",
                dangerHi = "ये नंबर साइबर अपराधियों द्वारा पूछताछ को बीच में रोकने के लिए स्थापित किए जाते हैं, जिससे वे धोखाधड़ी वाले रिफंड लेनदेन को अंजाम दे सकें।",
                redFlagsEn = listOf("Customer care listings showing standard mobile numbers", "Instructions to share passwords or pin numbers to register claims", "Calling numbers listed on public community forum threads"),
                redFlagsHi = listOf("ग्राहक सेवा लिस्टिंग में मानक मोबाइल नंबरों का दिखना", "दावों को दर्ज करने के लिए पासवर्ड या पिन नंबर साझा करने के निर्देश", "सार्वजनिक कम्युनिटी फोरम थ्रेड्स पर सूचीबद्ध नंबरों पर कॉल करना"),
                safeResponseEn = "Retrieve customer support info strictly from the brand's verified website or official application interface.",
                safeResponseHi = "ग्राहक सहायता की जानकारी विशेष रूप से ब्रांड की सत्यापित वेबसाइट या आधिकारिक एप्लिकेशन इंटरफ़ेस से प्राप्त करें।"
            )
        )

        // Now programmatically generate 85+ remaining scams (ids 16 to 105) covering all 15 categories
        val techBrands = listOf("Microsoft", "Apple", "Google", "Facebook", "Amazon", "Netflix", "Steam", "Uber")
        val banks = listOf("HDFC Bank", "ICICI Bank", "SBI", "Axis Bank", "PNB", "Canara Bank", "Bank of Baroda")
        val eComs = listOf("Amazon", "Flipkart", "Myntra", "Meesho", "Ajio")
        val jobs = listOf("Data Entry Operator", "Social Media Moderator", "Ad Clicker Specialist", "Form Filler", "Hotel Review Writer")

        for (i in 16..105) {
            val category = categories[i % categories.size]
            val difficulty = when (i % 3) {
                0 -> "Easy"
                1 -> "Medium"
                else -> "Hard"
            }

            val titleEn: String
            val titleHi: String
            val messageEn: String
            val messageHi: String
            val dangerEn: String
            val dangerHi: String
            val redFlagsEn: List<String>
            val redFlagsHi: List<String>
            val safeResponseEn: String
            val safeResponseHi: String

            when (category) {
                "Phishing" -> {
                    val brand = techBrands[i % techBrands.size]
                    titleEn = "$brand Security Alert Attempt #$i"
                    titleHi = "$brand सुरक्षा चेतावनी प्रयास #$i"
                    messageEn = "Alert: Your $brand account was accessed from an unknown location. If this wasn't you, log in immediately to secure it at http://$brand-security-alert-$i.com"
                    messageHi = "चेतावनी: आपके $brand खाते को किसी अज्ञात स्थान से एक्सेस किया गया था। यदि यह आप नहीं थे, तो इसे http://$brand-security-alert-$i.com पर तुरंत सुरक्षित करने के लिए लॉग इन करें।"
                    dangerEn = "Attackers gain direct access to your account and personal cloud data by logging credentials on a fake mirror portal."
                    dangerHi = "नकली पोर्टल पर क्रेडेंशियल दर्ज करने से हमलावर आपके खाते और व्यक्तिगत क्लाउड डेटा तक सीधी पहुंच प्राप्त कर लेते हैं।"
                    redFlagsEn = listOf("Spelling variants in the domain name", "High sense of fear and urgency", "Requests to input old and new passwords")
                    redFlagsHi = listOf("डोमेन नाम में मामूली बदलाव", "डर और जल्दबाजी की उच्च भावना", "पुराने और नए पासवर्ड दर्ज करने का अनुरोध")
                    safeResponseEn = "Check your login history inside the official $brand account portal directly, skipping external hyperlinks."
                    safeResponseHi = "बाहरी लिंक को छोड़कर सीधे आधिकारिक $brand खाता पोर्टल के अंदर अपने लॉगिन इतिहास की जांच करें।"
                }
                "OTP Fraud" -> {
                    val bank = banks[i % banks.size]
                    titleEn = "Urgent $bank Reward Points OTP #$i"
                    titleHi = "तत्काल $bank रिवॉर्ड पॉइंट ओटीपी #$i"
                    messageEn = "A support caller states: 'You have Rs. 5,000 worth of pending reward points on your $bank card. I have sent an activation OTP. Please read it to me to claim.'"
                    messageHi = "एक सहायता कॉलर कहता है: 'आपके $bank कार्ड पर 5,000 रुपये के लंबित रिवॉर्ड पॉइंट हैं। मैंने एक एक्टिवेशन ओटीपी भेजा है। दावा करने के लिए कृपया इसे मुझे बताएं।'"
                    dangerEn = "This OTP is actually authorizing a fund transfer or adding a beneficiary to your banking dashboard."
                    dangerHi = "यह ओटीपी वास्तव में आपके बैंकिंग डैशबोर्ड पर धन हस्तांतरण या लाभार्थी को जोड़ने के लिए अधिकृत कर रहा है।"
                    redFlagsEn = listOf("Calling from an unofficial mobile number", "Demanding OTP to claim prize cash", "Aggressive insistence to read the SMS text quickly")
                    redFlagsHi = listOf("गैर-आधिकारिक मोबाइल नंबर से कॉल आना", "इनाम नकद का दावा करने के लिए ओटीपी मांगना", "एसएमएस संदेश को तुरंत पढ़ने के लिए आक्रामक दबाव")
                    safeResponseEn = "Never read OTPs over a phone call. Reward points are claimed inside official net-banking portals safely."
                    safeResponseHi = "फोन कॉल पर कभी भी ओटीपी न बताएं। रिवॉर्ड पॉइंट का दावा सुरक्षित रूप से आधिकारिक नेट-बैंकिंग पोर्टल के अंदर किया जाता है।"
                }
                "UPI Scam" -> {
                    val app = if (i % 2 == 0) "Google Pay" else "PhonePe"
                    titleEn = "$app Cash Refund UPI Trap $i"
                    titleHi = "$app कैश रिफंड यूपीआई जाल $i"
                    messageEn = "An SMS states: 'Congratulations! You received an instant cash refund of Rs. 1,500 on $app. Tap here to approve credit: upi-refund-collect-req-$i@okaxis'"
                    messageHi = "एक एसएमएस में लिखा है: 'बधाई हो! आपको $app पर 1,500 रुपये का तत्काल नकद रिफंड प्राप्त हुआ। क्रेडिट स्वीकार करने के लिए यहाँ टैप करें: upi-refund-collect-req-$i@okaxis'"
                    dangerEn = "Tapping the UPI link opens a payment screen asking for your UPI PIN. Entering it transfers Rs. 1,500 to the scammer."
                    dangerHi = "यूपीआई लिंक पर टैप करने से एक भुगतान स्क्रीन खुलती है जो आपके यूपीआई पिन की मांग करती है। इसे दर्ज करने से स्कैमर को 1,500 रुपये ट्रांसफर हो जाते हैं।"
                    redFlagsEn = listOf("Receiving refunds requires UPI transaction clicks", "Use of an unofficial UPI ID (@okaxis) with suspicious naming", "Entering PIN to receive a credit benefit")
                    redFlagsHi = listOf("रिफंड प्राप्त करने के लिए यूपीआई लेनदेन क्लिक की आवश्यकता", "संदिग्ध नाम वाले गैर-आधिकारिक यूपीआई आईडी का उपयोग", "क्रेडिट लाभ प्राप्त करने के लिए पिन दर्ज करना")
                    safeResponseEn = "Decline any collect requests. Do not enter your PIN unless you explicitly want to pay money."
                    safeResponseHi = "किसी भी कलेक्ट अनुरोध को अस्वीकार करें। जब तक आप स्पष्ट रूप से पैसे का भुगतान नहीं करना चाहते, तब तक अपना पिन दर्ज न करें।"
                }
                "QR Code Scam" -> {
                    val item = if (i % 2 == 0) "Premium Smartwatch" else "Discount Furniture"
                    titleEn = "Marketplace QR Code Voucher for $item"
                    titleHi = "$item के लिए मार्केटप्लेस क्यूआर कोड वाउचर"
                    messageEn = "A buyer on Olx sends a QR code claiming: 'I am paying for the $item. Just scan this receipt QR to receive the cash advance into your bank account.'"
                    messageHi = "Olx पर एक खरीदार एक क्यूआर कोड भेजता है जिसमें दावा किया जाता है: 'मैं $item के लिए भुगतान कर रहा हूं। अपने बैंक खाते में नकद पेशगी प्राप्त करने के लिए बस इस रसीद क्यूआर को स्कैन करें।'"
                    dangerEn = "The QR code contains a hidden debit request. Scanning and typing your PIN executes an outgoing transaction."
                    dangerHi = "क्यूआर कोड में एक छिपा हुआ पैसे निकालने का अनुरोध होता है। स्कैन करने और अपना पिन टाइप करने से आपके खाते से पैसे कट जाते हैं।"
                    redFlagsEn = listOf("Scanning a QR to receive buyer funds", "Buyer refusing standard bank transfers or cash", "Hurry to scan the code immediately")
                    redFlagsHi = listOf("खरीदार से पैसे प्राप्त करने के लिए क्यूआर स्कैन करना", "खरीदार का सामान्य बैंक ट्रांसफर या नकद देने से इनकार करना", "तुरंत कोड स्कैन करने की जल्दबाजी")
                    safeResponseEn = "Decline Olx buyers who demand scanning QR codes. Ask for cash on delivery or direct bank transfer."
                    safeResponseHi = "क्यूआर कोड स्कैन करने की मांग करने वाले खरीदारों को अस्वीकार करें। नकद भुगतान या सीधे बैंक ट्रांसफर के लिए कहें।"
                }
                "Fake Bank Call" -> {
                    val bank = banks[i % banks.size]
                    titleEn = "$bank Suspicious Transaction Verification #$i"
                    titleHi = "$bank संदिग्ध लेनदेन सत्यापन #$i"
                    messageEn = "A caller claiming to be a $bank fraud manager says: 'We detected a fraudulent charge of Rs. 49,999 on your account. To block it, please verify your internet banking password and OTP.'"
                    messageHi = "खुद को $bank का धोखाधड़ी प्रबंधक बताने वाला एक कॉलर कहता है: 'हमने आपके खाते पर 49,999 रुपये का संदिग्ध शुल्क देखा है। इसे रोकने के लिए, कृपया अपना इंटरनेट बैंकिंग पासवर्ड और ओटीपी सत्यापित करें।'"
                    dangerEn = "The scammer logs into your account using your shared credentials and transfers your remaining funds instantly."
                    dangerHi = "स्कैमर आपके साझा क्रेडेंशियल्स का उपयोग करके आपके खाते में लॉग इन करता है और आपके बचे हुए पैसे तुरंत ट्रांसफर कर लेता है।"
                    redFlagsEn = listOf("Urgent threat of Rs. 49,999 loss", "Direct requests for net-banking passwords", "No official IVR validation channel used")
                    redFlagsHi = listOf("49,999 रुपये के नुकसान की तत्काल धमकी", "नेट-बैंकिंग पासवर्ड के लिए सीधे अनुरोध", "किसी आधिकारिक आईवीआर सिस्टम का उपयोग न होना")
                    safeResponseEn = "Hang up and check your account transactions via the official mobile banking app. If concerned, call the support number listed on your physical debit card."
                    safeResponseHi = "फोन काट दें और आधिकारिक मोबाइल बैंकिंग ऐप के माध्यम से अपने खाते के लेनदेन की जांच करें। यदि चिंतित हैं, तो अपने डेबिट कार्ड पर सूचीबद्ध सहायता नंबर पर कॉल करें।"
                }
                "Fake KYC" -> {
                    val service = if (i % 2 == 0) "Electricity Board" else "Gas Pipeline Support"
                    titleEn = "Urgent $service KYC Suspension Notice $i"
                    titleHi = "तत्काल $service केवाईसी निलंबन सूचना $i"
                    messageEn = "An SMS warns: 'Power/Gas supply to your household will be disconnected tonight at 9:30 PM due to unpaid bills / incomplete KYC. Call +91-XXXXXXXXXX to verify.'"
                    messageHi = "एक एसएमएस चेतावनी देता है: 'अवैतनिक बिलों / अधूरे केवाईसी के कारण आज रात 9:30 बजे आपके घर की बिजली/गैस आपूर्ति काट दी जाएगी। सत्यापित करने के लिए +91-XXXXXXXXXX पर कॉल करें।'"
                    dangerEn = "Scammers collect security payments or manipulate you into downloading screen sharing utilities to access digital wallets."
                    dangerHi = "स्कैमर सुरक्षा भुगतान एकत्र करते हैं या डिजिटल वॉलेट तक पहुंचने के लिए स्क्रीन शेयरिंग सॉफ्टवेयर डाउनलोड करने के लिए आपके साथ हेरफेर करते हैं।"
                    redFlagsEn = listOf("Short timeline threat of utility cutoffs", "Directing you to a personal phone number rather than official billing portals", "Vague descriptions of verification processes")
                    redFlagsHi = listOf("बिजली/गैस कटौती की बहुत कम समय की धमकी", "आधिकारिक बिलिंग पोर्टल्स के बजाय आपको व्यक्तिगत फोन नंबर पर भेजना", "सत्यापन प्रक्रियाओं का अस्पष्ट विवरण")
                    safeResponseEn = "Ignore the threat. Pay and verify bills only via official consumer service portals or authorized state utility apps."
                    safeResponseHi = "धमकी को नजरअंदाज करें। केवल आधिकारिक उपभोक्ता सेवा पोर्टल्स या अधिकृत सरकारी यूटिलिटी ऐप्स के माध्यम से बिलों का भुगतान और सत्यापन करें।"
                }
                "Fake Delivery" -> {
                    val carrier = if (i % 2 == 0) "Bluedart" else "Delhivery"
                    titleEn = "Failed $carrier Package Tracking Fee #$i"
                    titleHi = "विफल $carrier पैकेज ट्रैकिंग शुल्क #$i"
                    messageEn = "Your $carrier package is on hold at our regional warehouse due to a wrong street number. Pay Rs. 10 to reschedule delivery: http://$carrier-tracking-fee-$i.info"
                    messageHi = "गलत गली नंबर के कारण आपका $carrier पैकेज हमारे क्षेत्रीय गोदाम में रुका हुआ है। डिलीवरी रीशेड्यूल करने के लिए 10 रुपये का भुगतान करें: http://$carrier-tracking-fee-$i.info"
                    dangerEn = "This minor fee serves as a gateway to steal card details, which are subsequently used for high-value offshore transfers."
                    dangerHi = "यह मामूली शुल्क कार्ड विवरण चुराने का एक जरिया है, जिसका उपयोग बाद में उच्च-मूल्य वाले अपतटीय हस्तांतरण के लिए किया जाता है।"
                    redFlagsEn = listOf("Slightly altered URL mimicking $carrier", "Unexpected request to pay fees for delivery adjustments", "Links inside SMS coming from anonymous personal senders")
                    redFlagsHi = listOf("$carrier की नकल करने वाला थोड़ा बदला हुआ यूआरएल", "डिलीवरी समायोजन के लिए शुल्क भुगतान का अप्रत्याशित अनुरोध", "अनाम व्यक्तिगत सेंडर से आने वाले एसएमएस के अंदर लिंक")
                    safeResponseEn = "Contact the merchant who shipped your product or open the carrier's official portal directly to track details."
                    safeResponseHi = "अपने उत्पाद को भेजने वाले विक्रेता से संपर्क करें या विवरण ट्रैक करने के लिए सीधे कैरियर का आधिकारिक पोर्टल खोलें।"
                }
                "Job Scam" -> {
                    val jobTitle = jobs[i % jobs.size]
                    titleEn = "Work From Home $jobTitle Offer #$i"
                    titleHi = "घर से काम करें $jobTitle ऑफर #$i"
                    messageEn = "Exciting Job: Earn up to Rs. 4,500 daily as a $jobTitle. No experience required. Pay Rs. 299 registration fee to receive the starting kit."
                    messageHi = "रोमांचक नौकरी: $jobTitle के रूप में रोजाना 4,500 रुपये तक कमाएं। किसी अनुभव की आवश्यकता नहीं है। शुरुआती किट प्राप्त करने के लिए 299 रुपये पंजीकरण शुल्क का भुगतान करें।"
                    dangerEn = "The registration fee is lost, and they will demand further training fees while never providing real employment."
                    dangerHi = "पंजीकरण शुल्क डूब जाता है, और वे वास्तविक रोजगार प्रदान किए बिना और अधिक प्रशिक्षण शुल्क की मांग करते रहेंगे।"
                    redFlagsEn = listOf("Requirement to pay money to get a job", "Atypical high payouts for simple administrative tasks", "No official corporate domain email used during recruitment")
                    redFlagsHi = listOf("नौकरी पाने के लिए पैसे देने की आवश्यकता", "सरल प्रशासनिक कार्यों के लिए अस्वाभाविक रूप से अधिक भुगतान", "भर्ती के दौरान किसी आधिकारिक कॉर्पोरेट डोमेन ईमेल का उपयोग न होना")
                    safeResponseEn = "Never pay money to apply or receive training for a job. Verified companies pay employees, not the other way around."
                    safeResponseHi = "नौकरी के लिए आवेदन करने या प्रशिक्षण प्राप्त करने के लिए कभी भी पैसे न दें। सत्यापित कंपनियां कर्मचारियों को भुगतान करती हैं, न कि उनसे पैसे लेती हैं।"
                }
                "Investment Scam" -> {
                    titleEn = "VIP Trading Signal Club Success $i"
                    titleHi = "वीआईपी ट्रेडिंग सिग्नल क्लब सफलता $i"
                    messageEn = "Earn 50% returns weekly trading options with our expert group. Deposit capital to our secure UPI account. Over 20,000 satisfied members already!"
                    messageHi = "हमारे विशेषज्ञ समूह के साथ साप्ताहिक 50% रिटर्न कमाएं। हमारे सुरक्षित यूपीआई खाते में पूंजी जमा करें। पहले से ही 20,000 से अधिक संतुष्ट सदस्य हैं!"
                    dangerEn = "Once deposited, your funds are routed into untraceable private accounts, and the group admins block you from the chat room."
                    dangerHi = "एक बार जमा होने के बाद, आपका फंड अज्ञात निजी खातों में भेज दिया जाता है, और समूह के एडमिन आपको चैट रूम से ब्लॉक कर देते हैं।"
                    redFlagsEn = listOf("Promises of extremely high weekly trading gains", "Deposits made to individual UPI handles rather than brokerage accounts", "Lack of verified performance records or registration checks")
                    redFlagsHi = listOf("अत्यंत उच्च साप्ताहिक ट्रेडिंग लाभ का वादा", "ब्रोकरेज खातों के बजाय व्यक्तिगत यूपीआई हैंडल पर जमा करना", "सत्यापित प्रदर्शन रिकॉर्ड या पंजीकरण जांच का अभाव")
                    safeResponseEn = "Ignore public group pitches on social apps. Only invest via registered broker platforms monitored by financial regulators."
                    safeResponseHi = "सोशल ऐप्स पर सार्वजनिक समूह के विज्ञापनों को अनदेखा करें। केवल वित्तीय नियामकों द्वारा निगरानी किए जाने वाले पंजीकृत ब्रोकर प्लेटफॉर्म के माध्यम से निवेश करें।"
                }
                "Lottery Scam" -> {
                    val prize = if (i % 2 == 0) "Tata Nexon SUV" else "Free Trip to Dubai"
                    titleEn = "Sponsorship Sweepstakes Winner: $prize"
                    titleHi = "प्रायोजन स्वीपस्टेक्स विजेता: $prize"
                    messageEn = "Dear User, Your mobile number has won a brand new $prize in our annual lucky draw. Pay Rs. 8,999 shipping insurance fee to claim."
                    messageHi = "प्रिय उपयोगकर्ता, हमारे वार्षिक लकी ड्रा में आपके मोबाइल नंबर ने एक नया $prize जीता है। दावा करने के लिए 8,999 रुपये शिपिंग बीमा शुल्क का भुगतान करें।"
                    dangerEn = "Scammers pocket your 'shipping insurance fee' and continue demanding extra custom clearance fees before vanishing."
                    dangerHi = "स्कैमर आपका 'शिपिंग बीमा शुल्क' रख लेते हैं और गायब होने से पहले अतिरिक्त सीमा शुल्क निकासी शुल्क की मांग करते रहते हैं।"
                    redFlagsEn = listOf("Winning a high-value physical asset from a contest you never registered in", "Direct request to pay shipping, customs, or insurance fees upfront", "Communication originating from standard WhatsApp personal profiles")
                    redFlagsHi = listOf("ऐसी प्रतियोगिता से उच्च-मूल्य वाली वस्तु जीतना जिसमें आपने कभी पंजीकरण नहीं कराया", "अग्रिम रूप से शिपिंग, सीमा शुल्क या बीमा शुल्क का भुगतान करने का सीधा अनुरोध", "सामान्य व्हाट्सएप व्यक्तिगत प्रोफाइल से आने वाला संचार")
                    safeResponseEn = "Delete the alert immediately. Legitimate sweepstakes deduct necessary taxes directly from the winnings and do not charge upfront insurance fees."
                    safeResponseHi = "अलर्ट को तुरंत हटा दें। वैध स्वीपस्टेक्स जीत की राशि से सीधे आवश्यक टैक्स काटते हैं और अग्रिम बीमा शुल्क नहीं लेते हैं।"
                }
                "Tech Support Scam" -> {
                    val app = eComs[i % eComs.size]
                    titleEn = "$app Fake Refund Helpline Trap"
                    titleHi = "$app फर्जी रिफंड हेल्पलाइन जाल"
                    messageEn = "Having trouble with your $app order refund? Call our quick support line: 1800-FAKE-HELP. We will assist you with immediate credit transfers."
                    messageHi = "क्या आपको अपने $app ऑर्डर रिफंड में परेशानी हो रही है? हमारी त्वरित सहायता लाइन पर कॉल करें: 1800-FAKE-HELP। हम तत्काल ट्रांसफर में आपकी सहायता करेंगे।"
                    dangerEn = "Scammers pretend to process a refund, tricking you into scanning a UPI code that actually transfers funds from your wallet."
                    dangerHi = "स्कैमर रिफंड की प्रक्रिया का नाटक करते हैं, आपको एक यूपीआई कोड स्कैन करने के लिए गुमराह करते हैं जो वास्तव में आपके वॉलेट से पैसे भेज देता है।"
                    redFlagsEn = listOf("Phone support numbers posted on social media comments or unsourced forums", "Requiring a payment scanner or pin to trigger a refund credit", "Executive speaking in an unprofessional or informal tone")
                    redFlagsHi = listOf("सोशल मीडिया टिप्पणियों या असत्यापित मंचों पर पोस्ट किए गए फोन नंबर", "रिफंड क्रेडिट प्राप्त करने के लिए पेमेंट स्कैनर या पिन की आवश्यकता", "कर्मचारी का अव्यवसायिक या अनौपचारिक लहजे में बात करना")
                    safeResponseEn = "Only communicate through the official $app application support chat or verified contact details published on their main website."
                    safeResponseHi = "केवल आधिकारिक $app एप्लिकेशन सहायता चैट या उनकी मुख्य वेबसाइट पर प्रकाशित सत्यापित संपर्क विवरण के माध्यम से संवाद करें।"
                }
                "WhatsApp Scam" -> {
                    titleEn = "WhatsApp 6-Digit Verification Code Scam"
                    titleHi = "व्हाट्सएप 6-अंकीय सत्यापन कोड घोटाला"
                    messageEn = "A relative's compromised account DMs you: 'I accidentally sent my WhatsApp login verification code to your phone. Can you please forward me that 6-digit SMS code quickly?'"
                    messageHi = "एक रिश्तेदार का हैक किया गया खाता आपको डीएम करता है: 'मैंने गलती से अपना व्हाट्सएप लॉगिन सत्यापन कोड आपके फोन पर भेज दिया है। क्या आप कृपया मुझे वह 6-अंकीय एसएमएस कोड जल्दी भेज सकते हैं?'"
                    dangerEn = "If you share the code, the attacker takes over your WhatsApp account, locks you out, and starts scamming your contacts."
                    dangerHi = "यदि आप कोड साझा करते हैं, तो हमलावर आपके व्हाट्सएप खाते पर कब्जा कर लेता है, आपको बाहर कर देता है, और आपके संपर्कों को ठगना शुरू कर देता है।"
                    redFlagsEn = listOf("Request to share WhatsApp verification codes", "Irregular messages from family members demanding quick favors", "SMS message warning 'Do not share this code'")
                    redFlagsHi = listOf("व्हाट्सएप सत्यापन कोड साझा करने का अनुरोध", "त्वरित मदद की मांग करने वाले परिवार के सदस्यों के असामान्य संदेश", "'इस कोड को साझा न करें' की चेतावनी देने वाला एसएमएस")
                    safeResponseEn = "Never share verification codes with anyone, even close family. Call them via a regular voice call to confirm if their profile is hacked."
                    safeResponseHi = "सत्यापन कोड कभी भी किसी के साथ साझा न करें, यहाँ तक कि करीबी परिवार के साथ भी नहीं। यह पुष्टि करने के लिए कि क्या उनका प्रोफाइल हैक हो गया है, उन्हें फोन कॉल करें।"
                }
                "Telegram Scam" -> {
                    titleEn = "Telegram Channel Investment Expert Setup"
                    titleHi = "टेलीग्राम चैनल निवेश विशेषज्ञ सेटअप"
                    messageEn = "Join our exclusive Telegram channel. Pay Rs. 999 to enter, and receive guaranteed micro-signals daily for stock trading that guarantee 400% profits."
                    messageHi = "हमारे विशेष टेलीग्राम चैनल से जुड़ें। प्रवेश करने के लिए 999 रुपये का भुगतान करें, और स्टॉक ट्रेडिंग के लिए रोजाना गारंटीड संकेत प्राप्त करें जो 400% लाभ की गारंटी देते हैं।"
                    dangerEn = "The channel publishes fake curated receipts, and after paying the registration or initial trade investment, they permanently block your profile."
                    dangerHi = "चैनल नकली रसीदें प्रकाशित करता है, और पंजीकरण या प्रारंभिक निवेश का भुगतान करने के बाद, वे आपके प्रोफाइल को स्थायी रूप से ब्लॉक कर देते हैं।"
                    redFlagsEn = listOf("Assurances of massive guaranteed profits from low-risk daily trades", "No legal financial advisory credentials presented by channel admin", "Payment via private UPI handles")
                    redFlagsHi = listOf("कम जोखिम वाले दैनिक ट्रेडों से भारी गारंटीड मुनाफे का आश्वासन", "चैनल एडमिन द्वारा कोई कानूनी वित्तीय सलाहकार क्रेडेंशियल प्रस्तुत न करना", "निजी यूपीआई हैंडल के माध्यम से भुगतान")
                    safeResponseEn = "Report and block the channel. Legitimate financial advisors must display SEBI registration codes."
                    safeResponseHi = "चैनल को रिपोर्ट और ब्लॉक करें। वास्तविक वित्तीय सलाहकारों को सेबी (SEBI) पंजीकरण कोड प्रदर्शित करना आवश्यक है।"
                }
                "Instagram Scam" -> {
                    titleEn = "Instagram Brand Ambassador Sponsorship"
                    titleHi = "इंस्टाग्राम ब्रांड एंबेसडर प्रायोजन"
                    messageEn = "A fashion brand account DMs you: 'We love your profile style! We want to sponsor you with 3 free products. Just click this tracking registration link and pay shipping.'"
                    messageHi = "एक फैशन ब्रांड खाता आपको डीएम करता है: 'हमें आपकी प्रोफाइल स्टाइल बहुत पसंद है! हम आपको 3 मुफ्त उत्पादों के साथ प्रायोजित करना चाहते हैं। बस इस ट्रैकिंग पंजीकरण लिंक पर क्लिक करें और शिपिंग का भुगतान करें।'"
                    dangerEn = "The shipping page is a phishing trap that captures your online credentials or debit cards, draining your accounts."
                    dangerHi = "शिपिंग पेज एक फ़िशिंग जाल है जो आपके ऑनलाइन क्रेडेंशियल या डेबिट कार्ड को चुरा लेता है, जिससे आपका खाता खाली हो जाता है।"
                    redFlagsEn = listOf("Unsolicited influencer collaboration offers", "Directing you to external non-secure checkout sites for simple shipping payments", "Poor grammar or automated translation layouts in brand DMs")
                    redFlagsHi = listOf("प्रभावशाली सहयोग के अवांछित प्रस्ताव", "सरल शिपिंग भुगतान के लिए आपको बाहरी गैर-सुरक्षित चेकआउट साइटों पर निर्देशित करना", "ब्रांड डीएम में खराब व्याकरण या स्वचालित अनुवाद")
                    safeResponseEn = "Review the brand's verified website independently. Never click sponsorship registration links sent via random DMs."
                    safeResponseHi = "ब्रांड की सत्यापित वेबसाइट की स्वतंत्र रूप से समीक्षा करें। यादृच्छिक डीएम के माध्यम से भेजे गए प्रायोजन पंजीकरण लिंक पर कभी क्लिक न करें।"
                }
                else -> { // Fake Customer Care
                    val brand = techBrands[i % techBrands.size]
                    titleEn = "Fake $brand Online Helpline Interception"
                    titleHi = "फर्जी $brand ऑनलाइन हेल्पलाइन इंटरसेप्शन"
                    messageEn = "Faced technical glitches with your $brand service? Contact our live chat executive on this mobile number: +91-XXXXXXXXXX for prompt assistance."
                    messageHi = "क्या आपको अपनी $brand सेवा के साथ तकनीकी समस्याओं का सामना करना पड़ा? त्वरित सहायता के लिए इस मोबाइल नंबर पर हमारे लाइव चैट कर्मचारी से संपर्क करें: +91-XXXXXXXXXX।"
                    dangerEn = "The fake help representatives guide you to execute operations that compromise your account passwords or bank balances."
                    dangerHi = "नकली सहायता प्रतिनिधि आपको उन प्रक्रियाओं को करने के लिए मार्गदर्शन करते हैं जो आपके खाता पासवर्ड या बैंक बैलेंस को खतरे में डालती हैं।"
                    redFlagsEn = listOf("Helpline contact info using personal mobile numbers", "Instructions to share authorization codes or credit data", "Absence of official platform ticketing systems")
                    redFlagsHi = listOf("व्यक्तिगत मोबाइल नंबरों का उपयोग करने वाली हेल्पलाइन संपर्क जानकारी", "प्राधिकरण कोड या क्रेडिट डेटा साझा करने के निर्देश", "आधिकारिक प्लेटफॉर्म टिकटिंग सिस्टम का अभाव")
                    safeResponseEn = "Always communicate with $brand through their official in-app feedback channels or secure contact portals."
                    safeResponseHi = "हमेशा उनके आधिकारिक इन-ऐप फीडबैक चैनलों या सुरक्षित संपर्क पोर्टलों के माध्यम से $brand के साथ संवाद करें।"
                }
            }

            baseScams.add(
                ScamExample(
                    id = i,
                    category = category,
                    difficulty = difficulty,
                    titleEn = titleEn,
                    titleHi = titleHi,
                    messageEn = messageEn,
                    messageHi = messageHi,
                    dangerEn = dangerEn,
                    dangerHi = dangerHi,
                    redFlagsEn = redFlagsEn,
                    redFlagsHi = redFlagsHi,
                    safeResponseEn = safeResponseEn,
                    safeResponseHi = safeResponseHi
                )
            )
        }

        scams = baseScams
    }
}
