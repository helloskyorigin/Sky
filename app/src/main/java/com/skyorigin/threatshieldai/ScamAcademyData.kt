package com.skyorigin.threatshieldai

object ScamAcademyData {
    val categories = listOf(
        ScamCategory(
            id = "phishing",
            titleEn = "Phishing",
            titleHi = "फ़िशिंग (Phishing)",
            shortDescEn = "Test and identify fraudulent emails, SMS, and suspicious hyperlinks designed to steal accounts.",
            shortDescHi = "खाता विवरण चुराने के लिए डिज़ाइन किए गए धोखाधड़ी वाले ईमेल, एसएमएस और संदिग्ध लिंक की पहचान करना सीखें।",
            iconName = "phishing",
            whatIsItEn = "Phishing is a highly prevalent cyber attack where scammers pretend to be legitimate, trustworthy entities (like banks, streaming sites, or government agencies) to manipulate you into sharing sensitive information.",
            whatIsItHi = "फ़िशिंग एक व्यापक साइबर हमला है जहाँ धोखेबाज खुद को वैध और विश्वसनीय संस्था (जैसे बैंक, स्ट्रीमिंग साइट या सरकारी एजेंसी) बताकर आपको संवेदनशील जानकारी साझा करने के लिए हेरफेर करते हैं।",
            howItWorksEn = listOf(
                "You receive an urgent message (email/SMS/WhatsApp) claiming your account is suspended or a package failed to deliver.",
                "You are directed to click a link to 'verify' or 'resolve' the emergency.",
                "The link takes you to a fake website designed to look exactly like the authentic login portal.",
                "Any username, password, or credit card info you enter is instantly harvested by the scammer."
            ),
            howItWorksHi = listOf(
                "आपको एक तत्काल संदेश (ईमेल/एसएमएस/व्हाट्सएप) मिलता है जिसमें दावा किया जाता है कि आपका खाता निलंबित है या पार्सल डिलीवर नहीं हो सका।",
                "आपातकाल को 'सत्यापित' या 'हल' करने के लिए आपको एक लिंक पर क्लिक करने के लिए निर्देशित किया जाता है।",
                "लिंक आपको एक नकली वेबसाइट पर ले जाता है जिसे बिल्कुल असली लॉगिन पोर्टल जैसा दिखने के लिए डिज़ाइन किया गया है।",
                "आपके द्वारा दर्ज किया गया कोई भी यूज़रनेम, पासवर्ड या क्रेडिट कार्ड विवरण तुरंत घोटालेबाज द्वारा चुरा लिया जाता है।"
            ),
            warningSignsEn = listOf(
                "Urgent, demanding language causing panic or fear ('Act within 24 hours to avoid a penalty').",
                "Sender's email address has subtle typos or uses a generic public domain instead of an official corporate domain.",
                "Generic greetings like 'Dear Customer' rather than using your real registered name.",
                "Links that redirect to unrecognized domain names or do not begin with secure HTTPS protocol."
            ),
            warningSignsHi = listOf(
                "अत्यधिक दबाव या तात्कालिकता वाली भाषा जो डर पैदा करती है ('जुर्माने से बचने के लिए 24 घंटे के भीतर कार्रवाई करें')।",
                "प्रेषक के ईमेल पते में मामूली गलतियाँ होती हैं या आधिकारिक डोमेन के बजाय एक सामान्य ईमेल सेवा का उपयोग होता है।",
                "आपके वास्तविक पंजीकृत नाम के बजाय 'प्रिय ग्राहक' जैसे सामान्य अभिवादन का उपयोग।",
                "ऐसे लिंक जो अपरिचित डोमेन नामों पर ले जाते हैं या सुरक्षित HTTPS प्रोटोकॉल से शुरू नहीं होते हैं।"
            ),
            howToStaySafeEn = listOf(
                "Never click on unsolicited links or attachments received via SMS or email.",
                "Always access online banking or shopping portals by typing the official URL directly into your browser.",
                "Enable Multi-Factor Authentication (MFA) on every critical account.",
                "If in doubt, contact the company directly through their official, independently sourced hotline."
            ),
            howToStaySafeHi = listOf(
                "एसएमएस या ईमेल के माध्यम से प्राप्त अवांछित लिंक या अटैचमेंट पर कभी क्लिक न करें।",
                "अपने ब्राउज़र में सीधे आधिकारिक यूआरएल टाइप करके ही हमेशा ऑनलाइन बैंकिंग या शॉपिंग पोर्टल खोलें।",
                "हर महत्वपूर्ण खाते पर मल्टी-फैक्टर ऑथेंटिकेशन (MFA) सक्षम करें।",
                "यदि संदेह हो, तो सीधे कंपनी के आधिकारिक हेल्पलाइन नंबर पर संपर्क करें।"
            ),
            quickSummaryEn = "Always verify the sender's real address, reject high-pressure links, and never type credentials into external popups.",
            quickSummaryHi = "हमेशा प्रेषक के वास्तविक पते को सत्यापित करें, संदिग्ध लिंक को अस्वीकार करें, और बाहरी पॉपअप में कभी भी पासवर्ड दर्ज न करें।"
        ),
        ScamCategory(
            id = "otp_fraud",
            titleEn = "OTP Fraud",
            titleHi = "ओटीपी धोखाधड़ी (OTP Fraud)",
            shortDescEn = "Never share authentication codes. Understand how scammers trick you into reading out OTPs.",
            shortDescHi = "प्रमाणीकरण कोड कभी साझा न करें। समझें कि घोटालेबाज आपको ओटीपी बताने के लिए कैसे बहकाते हैं।",
            iconName = "otp_fraud",
            whatIsItEn = "One-Time Password (OTP) Fraud is a social engineering scheme where attackers contact victims and trick them into revealing temporary security codes sent to their phones to complete unauthorized purchases or access accounts.",
            whatIsItHi = "वन-टाइम पासवर्ड (OTP) धोखाधड़ी एक ऐसी योजना है जहाँ हमलावर पीड़ितों से संपर्क करते हैं और उन्हें बैंक खातों तक पहुँचने या अनधिकृत खरीदारी करने के लिए उनके फोन पर भेजे गए अस्थायी सुरक्षा कोड बताने के लिए बरगलाते हैं।",
            howItWorksEn = listOf(
                "The scammer initiates an unauthorized transaction or password reset request using your username/card details.",
                "This triggers a security OTP sent from your bank or app provider to your registered phone number.",
                "The scammer calls you posing as a banking executive, telecom support, or tech assistant.",
                "They ask for the OTP to 'verify your identity,' 'cancel a fraudulent transaction,' or 'update your SIM.'"
            ),
            howItWorksHi = listOf(
                "घोटालेबाज आपके यूज़रनेम/कार्ड विवरण का उपयोग करके एक अनधिकृत लेनदेन या पासवर्ड रीसेट अनुरोध शुरू करता है।",
                "इससे आपके बैंक या ऐप प्रदाता द्वारा आपके पंजीकृत फोन नंबर पर एक सुरक्षा ओटीपी भेजा जाता है।",
                "घोटालेबाज आपको बैंक अधिकारी, टेलीकॉम सपोर्ट या तकनीकी सहायक बनकर कॉल करता है।",
                "वे 'पहचान सत्यापित करने', 'धोखाधड़ी वाले लेनदेन को रद्द करने', या 'सिम अपडेट करने' के लिए ओटीपी मांगते हैं।"
            ),
            warningSignsEn = listOf(
                "Incoming phone calls demanding a temporary pin or code that you did not request.",
                "An unknown caller telling you to stay on the line and check your SMS inbox.",
                "An OTP message text that includes warning labels like 'DO NOT SHARE' or displays an unrecognized transaction amount."
            ),
            warningSignsHi = listOf(
                "अचानक आई फोन कॉल जिसमें अस्थायी पिन या कोड मांगा जाता है जिसकी आपने मांग नहीं की थी।",
                "एक अज्ञात कॉलर जो आपसे कॉल पर बने रहने और अपना एसएमएस इनबॉक्स चेक करने के लिए कहता है।",
                "एक ओटीपी संदेश जिसमें 'साझा न करें' जैसी स्पष्ट चेतावनी लिखी होती है या एक अपरिचित लेनदेन राशि दिखाई देती है।"
            ),
            howToStaySafeEn = listOf(
                "Treat OTPs as highly confidential information. Never share an OTP with anyone, under any circumstances.",
                "Banks and reputable agencies will never call you on the phone and ask you to read out an OTP.",
                "Read the exact text of OTP messages carefully to understand what specific action or payment is being authorized.",
                "Hang up on anyone who insists on receiving a security verification code."
            ),
            howToStaySafeHi = listOf(
                "ओटीपी को अत्यधिक गोपनीय जानकारी समझें। किसी भी परिस्थिति में किसी के साथ ओटीपी साझा न करें।",
                "बैंक और प्रतिष्ठित एजेंसियां कभी भी आपको फोन करके ओटीपी बताने के लिए नहीं कहेंगी।",
                "यह समझने के लिए कि किस विशिष्ट कार्रवाई या भुगतान को अधिकृत किया जा रहा है, ओटीपी संदेशों के पाठ को ध्यान से पढ़ें।",
                "यदि कोई सुरक्षा कोड प्राप्त करने पर अड़ जाता है, तो तुरंत फोन काट दें।"
            ),
            quickSummaryEn = "An OTP is your private security check. Legitimate organizations will never demand it. Keep it to yourself.",
            quickSummaryHi = "ओटीपी आपका निजी सुरक्षा चेक है। वैध संगठन कभी भी इसकी मांग नहीं करेंगे। इसे केवल अपने पास रखें।"
        ),
        ScamCategory(
            id = "upi_fraud",
            titleEn = "UPI Fraud",
            titleHi = "यूपीआई धोखाधड़ी (UPI Fraud)",
            shortDescEn = "Spot fraudulent payment request notifications and fake cashbacks on UPI apps.",
            shortDescHi = "यूपीआई ऐप्स पर नकली कैशबैक और धोखाधड़ी वाले भुगतान अनुरोध नोटिफिकेशन की पहचान करें।",
            iconName = "upi_fraud",
            whatIsItEn = "Unified Payments Interface (UPI) Fraud involves tricking victims into initiating money transfers or typing their secret PINs on mobile apps under the illusion of receiving prizes or buyer payments.",
            whatIsItHi = "यूनिफाइड पेमेंट्स इंटरफेस (UPI) धोखाधड़ी में पीड़ितों को पुरस्कार या खरीदार भुगतान प्राप्त करने के भ्रम में अपने सीक्रेट पिन दर्ज करने या पैसे ट्रांसफर करने के लिए बहकाया जाता है।",
            howItWorksEn = listOf(
                "Scammers contact you posing as buyers on classified websites (like OLX) or send fake reward notifications.",
                "Instead of sending you money, they send a UPI 'Collect Request' or link to your payment app.",
                "They instruct you to accept the request and enter your UPI PIN to 'receive the payment.'",
                "The moment you input your PIN, funds are debited from your account instead of credited."
            ),
            howItWorksHi = listOf(
                "घोटालेबाज ओएलएक्स जैसी वेबसाइटों पर खरीदार बनकर आपसे संपर्क करते हैं या नकली इनाम वाले नोटिफिकेशन भेजते हैं।",
                "आपको पैसे भेजने के बजाय, वे आपके भुगतान ऐप पर एक यूपीआई 'कलेक्ट रिक्वेस्ट' (Collect Request) या लिंक भेजते हैं।",
                "वे आपको भुगतान 'प्राप्त करने' के लिए अनुरोध स्वीकार करने और अपना यूपीआई पिन दर्ज करने का निर्देश देते हैं।",
                "जैसे ही आप अपना पिन दर्ज करते हैं, आपके खाते से पैसे कट जाते हैं।"
            ),
            warningSignsEn = listOf(
                "Being asked to enter your UPI PIN to receive money. (Receiving money NEVER requires entering your PIN).",
                "Unsolicited request notifications appearing on Google Pay, PhonePe, or Paytm from unknown contacts.",
                "Callers trying to rush you through the payment steps before you can read the screen."
            ),
            warningSignsHi = listOf(
                "पैसे प्राप्त करने के लिए आपसे यूपीआई पिन दर्ज करने को कहा जाना। (पैसे प्राप्त करने के लिए कभी भी पिन दर्ज करने की आवश्यकता नहीं होती)।",
                "अपरिचित संपर्कों से गूगल पे, फोनपे, या पेटीएम पर भुगतान अनुरोध नोटिफिकेशन प्राप्त होना।",
                "कॉल करने वाले जो स्क्रीन को पढ़ने से पहले ही आपको भुगतान चरणों को पूरा करने के लिए जल्दबाजी कराते हैं।"
            ),
            howToStaySafeEn = listOf(
                "Remember the ultimate golden rule: UPI PIN is only used for sending money, never for receiving.",
                "Immediately decline and report any unrecognized 'Collect' requests inside your financial applications.",
                "Verify the beneficiary's actual verified name on the payment confirmation sheet before authorizing transactions."
            ),
            howToStaySafeHi = listOf(
                "परम सुनहरा नियम याद रखें: यूपीआई पिन का उपयोग केवल पैसे भेजने के लिए किया जाता है, प्राप्त करने के लिए कभी नहीं।",
                "वित्तीय ऐप्स के भीतर किसी भी अपरिचित 'कलेक्ट' अनुरोध को तुरंत अस्वीकार और रिपोर्ट करें।",
                "लेनदेन को अधिकृत करने से पहले भुगतान स्क्रीन पर प्राप्तकर्ता के वास्तविक नाम की जांच करें।"
            ),
            quickSummaryEn = "Receiving funds never requires entering a PIN. If an app asks for your UPI PIN, you are paying, not receiving.",
            quickSummaryHi = "पैसे प्राप्त करने के लिए कभी भी पिन दर्ज करने की आवश्यकता नहीं होती है। यदि कोई ऐप यूपीआई पिन मांगता है, तो आप पैसे भेज रहे हैं।"
        ),
        ScamCategory(
            id = "qr_code_scam",
            titleEn = "QR Code Scam",
            titleHi = "क्यूआर कोड घोटाला (QR Code Scam)",
            shortDescEn = "Never scan a QR code to receive money. Scan codes only when you make a payment.",
            shortDescHi = "पैसे प्राप्त करने के लिए कभी भी क्यूआर कोड स्कैन न करें। कोड केवल भुगतान करते समय स्कैन करें।",
            iconName = "qr_code_scam",
            whatIsItEn = "QR Code Scams rely on the misunderstanding of how QR codes operate. Fraudsters trick victims into scanning QR codes sent via chat apps, falsely claiming it will transfer money into the victim's account.",
            whatIsItHi = "क्यूआर कोड घोटाले इस गलतफहमी पर आधारित होते हैं कि क्यूआर कोड कैसे काम करते हैं। धोखेबाज पीड़ितों को चैट ऐप्स पर भेजे गए क्यूआर कोड को स्कैन करने के लिए बरगलाते हैं, और यह झूठा दावा करते हैं कि इससे पीड़ित के खाते में पैसे आ जाएंगे।",
            howItWorksEn = listOf(
                "You post an item for sale online. A fake buyer offers to pay immediately via UPI.",
                "They send a QR code image via WhatsApp and instruct you: 'Scan this code to receive your advance payment.'",
                "When you scan it using a payment app, the app triggers a debit transaction asking for your PIN.",
                "Entering your PIN transfers money straight out to the scammer's digital wallet."
            ),
            howItWorksHi = listOf(
                "आप ऑनलाइन बिक्री के लिए एक वस्तु पोस्ट करते हैं। एक नकली खरीदार यूपीआई के माध्यम से तुरंत भुगतान करने की पेशकश करता है।",
                "वे व्हाट्सएप के माध्यम से एक क्यूआर कोड भेजते हैं और निर्देश देते हैं: 'अग्रिम भुगतान प्राप्त करने के लिए इस कोड को स्कैन करें।'",
                "जब आप इसे स्कैन करते हैं, तो ऐप आपके पिन की मांग करते हुए डेबिट लेनदेन शुरू कर देता है।",
                "अपना पिन दर्ज करने पर पैसे सीधे घोटालेबाज के डिजिटल वॉलेट में चले जाते हैं।"
            ),
            warningSignsEn = listOf(
                "Receiving a QR code from a stranger who claims they want to send you money.",
                "A QR code labeled with misleading text overlay like 'Receive Money' or 'Cashback Auto-Credit.'",
                "The seller or buyer acting overly pushy and telling you to keep scanning despite errors."
            ),
            warningSignsHi = listOf(
                "किसी अजनबी से क्यूआर कोड प्राप्त होना जो दावा करता है कि वह आपको पैसे भेजना चाहता है।",
                "क्यूआर कोड पर 'पैसे प्राप्त करें' या 'कैशबैक ऑटो-क्रेडिट' जैसे भ्रामक शब्द लिखे होना।",
                "विक्रेता या खरीदार का अत्यधिक आक्रामक होना और त्रुटियों के बावजूद आपको स्कैन करते रहने के लिए कहना।"
            ),
            howToStaySafeEn = listOf(
                "Never scan a QR code to receive payments. Scanning is exclusively for making payments.",
                "Read the transaction popup text on your mobile screen carefully immediately after scanning any code.",
                "Inspect physical QR stickers printed on displays in shops to make sure they are not superimposed."
            ),
            howToStaySafeHi = listOf(
                "पैसे प्राप्त करने के लिए कभी भी क्यूआर कोड स्कैन न करें। स्कैनिंग विशेष रूप से भुगतान करने के लिए है।",
                "किसी भी कोड को स्कैन करने के तुरंत बाद अपने मोबाइल स्क्रीन पर दिखाई देने वाले लेनदेन विवरण को ध्यान से पढ़ें।",
                "दुकानों में प्रदर्शित भौतिक क्यूआर स्टिकर की जांच करें ताकि सुनिश्चित हो सके कि वे मूल स्टिकर के ऊपर नहीं चिपकाए गए हैं।"
            ),
            quickSummaryEn = "Scanning a QR code always pulls money from your bank account. Do not scan to receive money.",
            quickSummaryHi = "क्यूआर कोड स्कैन करने से हमेशा आपके बैंक खाते से पैसे कटते हैं। पैसे प्राप्त करने के लिए कभी भी स्कैन न करें।"
        ),
        ScamCategory(
            id = "job_scam",
            titleEn = "Job Scam",
            titleHi = "नौकरी का घोटाला (Job Scam)",
            shortDescEn = "Spot fake part-time job offers on WhatsApp/Telegram that charge registration fees.",
            shortDescHi = "व्हाट्सएप/टेलीग्राम पर नकली अंशकालिक नौकरी के प्रस्तावों की पहचान करें जो पंजीकरण शुल्क मांगते हैं।",
            iconName = "job_scam",
            whatIsItEn = "Job Scams trick job seekers into paying upfront fees or completing easy tasks under the promise of a lucrative, work-from-home career, only to lock them out once the money has been sent.",
            whatIsItHi = "नौकरी के घोटाले नौकरी चाहने वालों को आकर्षक वर्क-फ्रॉम-होम नौकरी के वादे के तहत अग्रिम शुल्क का भुगतान करने या आसान कार्यों को पूरा करने के लिए बरगलाते हैं, और पैसे भेजने के बाद उन्हें ब्लॉक कर देते हैं।",
            howItWorksEn = listOf(
                "You receive an unsolicited message offering a simple job (e.g., 'Earn $100 daily just by liking video links').",
                "The scammers pay you a small real commission initially to establish fake trust.",
                "They invite you to a 'VIP' group and demand security deposits or registration fees to access higher-paying tasks.",
                "As you deposit larger sums, they freeze your funds and demand more 'release fees' before blocking you completely."
            ),
            howItWorksHi = listOf(
                "आपको एक सरल नौकरी की पेशकश करने वाला संदेश मिलता है (जैसे, 'वीडियो लिंक लाइक करके प्रतिदिन ₹5000 कमाएं')।",
                "घोटालेबाज नकली विश्वास स्थापित करने के लिए शुरू में आपको एक छोटा वास्तविक कमीशन देते हैं।",
                "वे आपको एक 'वीआईपी' ग्रुप में शामिल करते हैं और उच्च भुगतान वाले कार्यों तक पहुंचने के लिए सुरक्षा जमा या पंजीकरण शुल्क की मांग करते हैं।",
                "जैसे ही आप बड़ी रकम जमा करते हैं, वे आपके धन को फ्रीज कर देते हैं और पूरी तरह से ब्लॉक करने से पहले और अधिक 'निकासी शुल्क' की मांग करते हैं।"
            ),
            warningSignsEn = listOf(
                "Job opportunities with high wages that require absolutely no skills, interview, or qualification.",
                "Recruitment conducted exclusively through chat applications like WhatsApp or Telegram with no official email contact.",
                "Requests for advance payment for registration, training modules, or equipment kits."
            ),
            warningSignsHi = listOf(
                "बिना किसी कौशल, साक्षात्कार या योग्यता के अत्यधिक उच्च वेतन देने वाले नौकरी के अवसर।",
                "बिना किसी आधिकारिक ईमेल संपर्क के केवल व्हाट्सएप या टेलीग्राम जैसे चैट ऐप्स के माध्यम से की जाने वाली भर्ती प्रक्रिया।",
                "पंजीकरण, प्रशिक्षण मॉड्यूल, या उपकरण किट के लिए अग्रिम भुगतान की मांग।"
            ),
            howToStaySafeEn = listOf(
                "Never pay any amount to secure a job. Legitimate companies never charge candidates under any pretext.",
                "Verify the job offer directly on the company's verified 'Careers' webpage or LinkedIn page.",
                "Verify that recruiters correspond from corporate email addresses (@companyname.com) rather than generic free accounts."
            ),
            howToStaySafeHi = listOf(
                "नौकरी सुरक्षित करने के लिए कभी भी किसी राशि का भुगतान न करें। वैध कंपनियां कभी भी किसी भी बहाने से उम्मीदवारों से शुल्क नहीं लेती हैं।",
                "नौकरी के प्रस्ताव को सीधे कंपनी के सत्यापित 'करियर' वेबपेज या लिंक्डइन पेज पर जाकर सत्यापित करें।",
                "पुष्टि करें कि भर्तीकर्ता सामान्य मुफ्त ईमेल खातों के बजाय कॉर्पोरेट ईमेल पते (@companyname.com) से संपर्क कर रहे हैं।"
            ),
            quickSummaryEn = "Real employers pay you for your work; they never ask you to pay them first to unlock a job.",
            quickSummaryHi = "असली नियोक्ता आपको आपके काम के लिए भुगतान करते हैं; वे नौकरी देने के लिए आपसे पहले पैसे कभी नहीं मांगते।"
        ),
        ScamCategory(
            id = "loan_scam",
            titleEn = "Loan Scam",
            titleHi = "लोन घोटाला (Loan Scam)",
            shortDescEn = "Recognize predatory loan apps that request phone permissions to blackmail you.",
            shortDescHi = "उन शिकारी ऋण ऐप्स को पहचानें जो ब्लैकमेल करने के लिए फोन अनुमतियों की मांग करते हैं।",
            iconName = "loan_scam",
            whatIsItEn = "Loan Scams involve unauthorized, fraudulent mobile apps that promise instant, paperless loans with no credit checks, only to extract excessive fees, impose absurd interest, and blackmail you with personal data.",
            whatIsItHi = "लोन घोटालों में अनधिकृत, धोखाधड़ी वाले मोबाइल ऐप्स शामिल होते हैं जो बिना किसी क्रेडिट चेक के तत्काल, कागजरहित ऋण का वादा करते हैं, केवल अत्यधिक शुल्क वसूलने, बेतुका ब्याज लगाने और आपके व्यक्तिगत डेटा के साथ आपको ब्लैकमेल करने के लिए।",
            howItWorksEn = listOf(
                "You download an instant loan app advertised heavily on social media platforms.",
                "Upon installation, the app forces you to grant sensitive permissions including access to your contacts, gallery, and SMS.",
                "They disburse a tiny loan amount and immediately demand extortionate repayments within 5 to 7 days.",
                "If you refuse to pay, they edit your photos maliciously and threaten to send them to everyone in your contact list."
            ),
            howItWorksHi = listOf(
                "आप सोशल मीडिया प्लेटफॉर्म पर विज्ञापित एक इंस्टेंट लोन ऐप डाउनलोड करते हैं।",
                "इंस्टॉलेशन पर, ऐप आपको अपने संपर्क, गैलरी और एसएमएस सहित संवेदनशील अनुमतियां देने के लिए मजबूर करता है।",
                "वे एक छोटी ऋण राशि वितरित करते हैं और तुरंत 5 से 7 दिनों के भीतर अत्यधिक पुनर्भुगतान की मांग करते हैं।",
                "यदि आप भुगतान करने से इनकार करते हैं, तो वे आपकी तस्वीरों को संपादित करते हैं और उन्हें आपकी संपर्क सूची में सभी को भेजने की धमकी देते हैं।"
            ),
            warningSignsEn = listOf(
                "The application demands full contact list and photo library permissions before showing loan terms.",
                "Absurdly high processing charges deducted upfront from the loan principal.",
                "Lack of registered physical address, official customer support, or registration with regulatory agencies."
            ),
            warningSignsHi = listOf(
                "ऋण की शर्तें दिखाने से पहले ऐप पूर्ण संपर्क सूची और फोटो गैलरी अनुमतियों की मांग करता है।",
                "ऋण राशि से ही पहले ही काट लिया जाने वाला अत्यधिक प्रसंस्करण शुल्क।",
                "पंजीकृत भौतिक पते, आधिकारिक ग्राहक सहायता, या नियामक एजेंसियों (जैसे आरबीआई) के साथ पंजीकरण का अभाव।"
            ),
            howToStaySafeEn = listOf(
                "Only download financial apps from authorized banks and RBI-registered NBFCs.",
                "Never grant contact book or photo gallery access to simple loan or utility applications.",
                "Check the App Store reviews carefully for extortion reports or high-interest complaints before downloading."
            ),
            howToStaySafeHi = listOf(
                "केवल अधिकृत बैंकों और आरबीआई-पंजीकृत एनबीएफसी से ही वित्तीय ऐप्स डाउनलोड करें।",
                "किसी भी ऋण या सामान्य ऐप्स को कभी भी संपर्क सूची या फोटो गैलरी की अनुमति न दें।",
                "डाउनलोड करने से पहले ऐप स्टोर पर जबरन वसूली की रिपोर्ट या अत्यधिक ब्याज की शिकायतों के लिए समीक्षाओं की सावधानीपूर्वक जांच करें।"
            ),
            quickSummaryEn = "Instant loan apps can turn into severe blackmail traps. Never share your contacts or photos to obtain quick cash.",
            quickSummaryHi = "इंस्टेंट लोन ऐप्स ब्लैकमेलिंग का शिकार बना सकते हैं। त्वरित नकद प्राप्त करने के लिए कभी भी अपने संपर्क या तस्वीरें साझा न करें।"
        ),
        ScamCategory(
            id = "investment_scam",
            titleEn = "Investment Scam",
            titleHi = "निवेश घोटाला (Investment Scam)",
            shortDescEn = "Detect fake trading portals promising guaranteed high returns with zero risk.",
            shortDescHi = "शून्य जोखिम के साथ गारंटीकृत उच्च रिटर्न का वादा करने वाले नकली ट्रेडिंग पोर्टल की पहचान करें।",
            iconName = "investment_scam",
            whatIsItEn = "Investment Scams convince victims to deposit their hard-earned money into fraudulent schemes, cryptocurrency double-your-money offers, or fake stock advisory platforms with the promise of guaranteed, risk-free returns.",
            whatIsItHi = "निवेश घोटाले पीड़ितों को शून्य जोखिम के साथ गारंटीकृत रिटर्न के वादे के साथ धोखाधड़ी वाली योजनाओं, क्रिप्टोकरेंसी दोहरीकरण ऑफ़र या नकली स्टॉक सलाहकार प्लेटफार्मों में अपनी गाढ़ी कमाई जमा करने के लिए मनाते हैं।",
            howItWorksEn = listOf(
                "Scammers invite you to exclusive stock tip groups on Telegram or WhatsApp.",
                "They showcase screenshots of massive trading profits to build fear of missing out (FOMO).",
                "They direct you to register on an unverified, custom-designed trading app or portal.",
                "Your dashboard displays huge virtual profits, but when you attempt to withdraw, they freeze your account and demand high fees."
            ),
            howItWorksHi = listOf(
                "घोटालेबाज आपको टेलीग्राम या व्हाट्सएप पर विशेष स्टॉक टिप्स समूहों में आमंत्रित करते हैं।",
                "वे विश्वास और लालच पैदा करने के लिए भारी ट्रेडिंग मुनाफे के स्क्रीनशॉट प्रदर्शित करते हैं।",
                "वे आपको एक असत्यापित, कस्टम-डिज़ाइन किए गए ट्रेडिंग ऐप या पोर्टल पर पंजीकरण करने के लिए निर्देशित करते हैं।",
                "आपका डैशबोर्ड बड़ा आभासी मुनाफा दिखाता है, लेकिन जब आप वापस लेने का प्रयास करते हैं, तो वे खाते को फ्रीज कर देते हैं और शुल्क मांगते हैं।"
            ),
            warningSignsEn = listOf(
                "Guaranteed, risk-free high payouts (all legitimate financial investments carry some degree of risk).",
                "Pressure to act immediately to get in on 'secret, exclusive insider opportunities.'",
                "Depositing investment funds into individual personal bank accounts instead of registered corporate escrow portals."
            ),
            warningSignsHi = listOf(
                "गारंटीकृत, जोखिम-मुक्त उच्च भुगतान का वादा (सभी वैध वित्तीय निवेशों में कुछ हद तक जोखिम होता है)।",
                "'गुप्त, विशेष अंदरूनी अवसरों' का लाभ उठाने के लिए तुरंत कार्रवाई करने का दबाव।",
                "पंजीकृत कॉर्पोरेट खातों के बजाय व्यक्तिगत बैंक खातों में निवेश राशि जमा करने को कहना।"
            ),
            howToStaySafeEn = listOf(
                "Only invest through SEBI-registered, officially certified financial institutions and brokers.",
                "Cross-check credentials of investment managers against official regulatory web portal registries.",
                "If an investment opportunity sounds incredibly profitable with zero risk, it is highly likely a scam."
            ),
            howToStaySafeHi = listOf(
                "केवल सेबी-पंजीकृत, आधिकारिक रूप से प्रमाणित वित्तीय संस्थानों और दलालों के माध्यम से निवेश करें।",
                "आधिकारिक नियामक वेब पोर्टलों पर निवेश प्रबंधकों की साख और पंजीकरण की जांच करें।",
                "यदि कोई निवेश अवसर बिना किसी जोखिम के अविश्वसनीय रूप से लाभदायक लगता है, तो यह निश्चित रूप से एक घोटाला है।"
            ),
            quickSummaryEn = "Virtual profits displayed on unknown apps are purely fictional. Rely only on accredited, certified brokers.",
            quickSummaryHi = "अज्ञात ऐप्स पर प्रदर्शित आभासी मुनाफा पूरी तरह से काल्पनिक होता है। केवल मान्यता प्राप्त दलालों पर ही भरोसा करें।"
        ),
        ScamCategory(
            id = "fake_customer_care",
            titleEn = "Fake Customer Care",
            titleHi = "नकली ग्राहक सहायता (Fake Customer Care)",
            shortDescEn = "Identify fake support numbers listed on Google Maps, Search, or social channels.",
            shortDescHi = "गूगल मैप्स, सर्च या सोशल मीडिया पर सूचीबद्ध नकली सहायता नंबरों की पहचान करें।",
            iconName = "fake_customer_care",
            whatIsItEn = "Fake Customer Care fraud involves scammers placing fraudulent contact numbers on Google Search, Maps, or social media to intercept victims who are searching for help with couriers, banks, or online apps.",
            whatIsItHi = "नकली ग्राहक सेवा धोखाधड़ी में घोटालेबाज कूरियर, बैंक या ऑनलाइन ऐप्स के लिए मदद खोजने वाले पीड़ितों को फंसाने के लिए गूगल सर्च, मैप्स या सोशल मीडिया पर धोखाधड़ी वाले संपर्क नंबर डाल देते हैं।",
            howItWorksEn = listOf(
                "You search for an organization's helpline (e.g., 'E-Wallet support') on Google and dial a mobile number listed on a search card.",
                "The call is answered by a scammer who pretends to be a polite customer service agent.",
                "They claim a pending fee is blocking your service and ask you to install a remote-control application (like AnyDesk/TeamViewer).",
                "Once installed, the scammer views your smartphone screen, captures your banking passwords, and empties your account."
            ),
            howItWorksHi = listOf(
                "आप गूगल पर किसी संगठन की हेल्पलाइन (जैसे, 'ई-वॉलेट सपोर्ट') खोजते हैं और सर्च कार्ड पर सूचीबद्ध मोबाइल नंबर डायल करते हैं।",
                "कॉल का उत्तर एक घोटालेबाज द्वारा दिया जाता है जो एक विनम्र ग्राहक सेवा एजेंट होने का नाटक करता है।",
                "वे दावा करते हैं कि एक लंबित शुल्क आपकी सेवा को रोक रहा है और आपसे रिमोट-कंट्रोल ऐप (जैसे AnyDesk/TeamViewer) इंस्टॉल करने के लिए कहते हैं।",
                "एक बार इंस्टॉल होने के बाद, घोटालेबाज आपके फोन की स्क्रीन देखता है, बैंकिंग पासवर्ड चुराता है और खाता खाली कर देता है।"
            ),
            warningSignsEn = listOf(
                "Support contacts listed as standard 10-digit individual mobile numbers rather than official toll-free numbers or verified shortcodes.",
                "Support representatives requesting that you download remote screen-sharing or remote-control apps.",
                "Demands to pay small service fees or authenticate a secure PIN during support diagnostics."
            ),
            warningSignsHi = listOf(
                "आधिकारिक टोल-फ्री नंबरों या सत्यापित शॉर्टकोड के बजाय सामान्य 10-अंकों के व्यक्तिगत मोबाइल नंबर के रूप में सूचीबद्ध नंबर।",
                "सपोर्ट प्रतिनिधि द्वारा आपसे रिमोट स्क्रीन-शेयरिंग या रिमोट-कंट्रोल ऐप्स डाउनलोड करने का अनुरोध किया जाना।",
                "सपोर्ट प्रक्रिया के दौरान छोटे सेवा शुल्क का भुगतान करने या सुरक्षित पिन दर्ज करने की मांग।"
            ),
            howToStaySafeEn = listOf(
                "Do not trust contact numbers obtained from Google Search snippets or maps listings. Find hotlines on official websites only.",
                "Never download screen-sharing utilities (AnyDesk, TeamViewer) at the request of any calling agent.",
                "Legitimate customer support services will never ask you to disclose confidential security credentials or input UPI PINs."
            ),
            howToStaySafeHi = listOf(
                "गूगल सर्च परिणामों या मैप्स से प्राप्त संपर्क नंबरों पर भरोसा न करें। हेल्पलाइन नंबर केवल आधिकारिक वेबसाइटों पर ही खोजें।",
                "किसी भी कॉलिंग एजेंट के अनुरोध पर कभी भी स्क्रीन-शेयरिंग ऐप्स (AnyDesk, TeamViewer) डाउनलोड न करें।",
                "वैध ग्राहक सहायता सेवाएं कभी भी आपसे गोपनीय पासवर्ड बताने या यूपीआई पिन दर्ज करने के लिए नहीं कहेंगी।"
            ),
            quickSummaryEn = "Search engine results can easily display fake phone numbers. Find helpline details strictly within verified apps.",
            quickSummaryHi = "सर्च इंजन परिणाम आसानी से नकली फोन नंबर प्रदर्शित कर सकते हैं। हेल्पलाइन विवरण केवल सत्यापित ऐप्स के भीतर ही खोजें।"
        )
    )
}
