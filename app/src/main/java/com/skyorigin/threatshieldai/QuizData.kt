package com.skyorigin.threatshieldai

object QuizData {
    val questions = listOf(
        // === TOPIC 1: Phishing ===
        QuizQuestion(
            id = 1,
            topic = "Phishing",
            questionEn = "You receive an email from 'secure-alert@paypal-update.com' stating your account is suspended. What should you do?",
            questionHi = "आपको 'secure-alert@paypal-update.com' से एक ईमेल प्राप्त होता है जिसमें लिखा है कि आपका खाता निलंबित कर दिया गया है। आपको क्या करना चाहिए?",
            optionsEn = listOf(
                "Click the link provided to log in and dispute the suspension.",
                "Reply to the email with your credit card details to verify your identity.",
                "Delete the email immediately and check your account via the official app.",
                "Forward the email to all your contacts to warn them."
            ),
            optionsHi = listOf(
                "लॉग इन करने और निलंबन का विरोध करने के लिए दिए गए लिंक पर क्लिक करें।",
                "अपनी पहचान सत्यापित करने के लिए अपने क्रेडिट कार्ड विवरण के साथ ईमेल का उत्तर दें।",
                "ईमेल को तुरंत हटा दें और आधिकारिक ऐप के माध्यम से अपना खाता जांचें।",
                "चेतावनी देने के लिए ईमेल को अपने सभी संपर्कों को अग्रेषित करें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Always ignore emails from unverified sender domains (like @paypal-update.com). Legitimate alerts will always be visible directly inside the official app or portal.",
            explanationHi = "हमेशा असत्यापित सेंडर डोमेन वाले ईमेल को अनदेखा करें। वैध सूचनाएं हमेशा सीधे आधिकारिक ऐप या पोर्टल के भीतर दिखाई देंगी।"
        ),
        QuizQuestion(
            id = 2,
            topic = "Phishing",
            questionEn = "What is the primary indicator of a phishing website?",
            questionHi = "फ़िशिंग वेबसाइट का प्राथमिक संकेतक क्या है?",
            optionsEn = listOf(
                "The page has too many colorful pictures.",
                "The URL has minor spelling errors or uses an unusual domain (e.g., faceb00k-login.co).",
                "The page loads slowly on mobile data.",
                "The website has a dark mode switch."
            ),
            optionsHi = listOf(
                "पेज में बहुत सारे रंगीन चित्र हैं।",
                "यूआरएल में मामूली वर्तनी की गलतियाँ हैं या असामान्य डोमेन (जैसे, faceb00k-login.co) का उपयोग किया गया है।",
                "पेज मोबाइल डेटा पर धीरे-धीरे लोड होता है।",
                "वेबसाइट में डार्क मोड स्विच है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Phishing sites mimic popular brands but must use a different URL. Look closely for spelling tricks like replacing 'o' with '0' or '.com' with '.co'.",
            explanationHi = "फ़िशिंग साइटें लोकप्रिय ब्रांडों की नकल करती हैं लेकिन उन्हें एक अलग यूआरएल का उपयोग करना पड़ता है। 'o' को '0' से या '.com' को '.co' से बदलने जैसी वर्तनी की तरकीबों को ध्यान से देखें।"
        ),
        QuizQuestion(
            id = 3,
            topic = "Phishing",
            questionEn = "What does a 'Spear Phishing' attack refer to?",
            questionHi = "'स्पीयर फ़िशिंग' हमले का क्या अर्थ है?",
            optionsEn = listOf(
                "A phishing attack sent to millions of random emails simultaneously.",
                "A highly targeted phishing attempt customized with your real name and personal details.",
                "A phone call scam where fraudsters request computer remote access.",
                "Malicious software that deletes hard drive configurations."
            ),
            optionsHi = listOf(
                "एक साथ लाखों यादृच्छिक ईमेल पर भेजा गया फ़िशिंग हमला।",
                "आपके वास्तविक नाम और व्यक्तिगत विवरणों के साथ अनुकूलित एक अत्यधिक लक्षित फ़िशिंग प्रयास।",
                "एक फोन कॉल घोटाला जहां जालसाज कंप्यूटर रिमोट एक्सेस का अनुरोध करते हैं।",
                "हानिकारक सॉफ़्टवेयर जो हार्ड ड्राइव कॉन्फ़िगरेशन को हटा देता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Spear phishing is highly personalized. Attackers gather background details from social media to make the malicious email look extremely authentic.",
            explanationHi = "स्पीयर फ़िशिंग अत्यधिक व्यक्तिगत होती है। हमलावर सोशल मीडिया से पृष्ठभूमि विवरण एकत्र करते हैं ताकि दुर्भावनापूर्ण ईमेल बेहद वास्तविक दिखे।"
        ),
        QuizQuestion(
            id = 4,
            topic = "Phishing",
            questionEn = "If an email includes an unexpected link to check your taxation refund, what is the safest procedure?",
            questionHi = "यदि किसी ईमेल में आपके टैक्स रिफंड की जांच करने के लिए एक अप्रत्याशित लिंक शामिल है, तो सबसे सुरक्षित प्रक्रिया क्या है?",
            optionsEn = listOf(
                "Click the link and input your bank account and tax credentials.",
                "Call the sender number specified inside the email body.",
                "Open the official tax department website in a separate tab to check your status.",
                "Download the PDF attachment containing your tax calculation details."
            ),
            optionsHi = listOf(
                "लिंक पर क्लिक करें और अपना बैंक खाता और टैक्स क्रेडेंशियल दर्ज करें।",
                "ईमेल बॉडी के अंदर निर्दिष्ट सेंडर नंबर पर कॉल करें।",
                "अपनी स्थिति जांचने के लिए एक अलग टैब में आधिकारिक आयकर विभाग की वेबसाइट खोलें।",
                "अपने टैक्स गणना विवरण वाली पीडीएफ अटैचमेंट डाउनलोड करें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Government agencies never distribute direct links to collect sensitive bank details over casual emails. Navigate to the official state portal manually.",
            explanationHi = "सरकारी एजेंसियां कभी भी सामान्य ईमेल पर संवेदनशील बैंक विवरण एकत्र करने के लिए सीधे लिंक नहीं भेजती हैं। मैन्युअल रूप से आधिकारिक सरकारी पोर्टल पर जाएं।"
        ),
        QuizQuestion(
            id = 5,
            topic = "Phishing",
            questionEn = "Why is it dangerous to click links inside unsolicited emails?",
            questionHi = "अनचाहे ईमेल के अंदर के लिंक पर क्लिक करना क्यों खतरनाक है?",
            optionsEn = listOf(
                "It will immediately double your monthly internet bill.",
                "It can initiate drive-by malware downloads or lead you to high-fidelity credential harvesting sites.",
                "It notifies the government about your browsing patterns.",
                "It deletes all saved contacts from your SIM card."
            ),
            optionsHi = listOf(
                "यह आपके मासिक इंटरनेट बिल को तुरंत दोगुना कर देगा।",
                "यह ड्राइव-बाय मैलवेयर डाउनलोड शुरू कर सकता है या आपको क्रेडेंशियल चुराने वाली नकली साइटों पर ले जा सकता है।",
                "यह सरकार को आपके ब्राउज़िंग पैटर्न के बारे में सूचित करता है।",
                "यह आपके सिम कार्ड से सभी सहेजे गए संपर्कों को हटा देता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Unsolicited links can execute script exploits in your browser to download background spyware or trick you into entering credentials on replica portals.",
            explanationHi = "अनचाहे लिंक आपके ब्राउज़र में पृष्ठभूमि में स्पाइवेयर डाउनलोड करने या नकली पोर्टलों पर क्रेडेंशियल दर्ज करने के लिए स्क्रिप्ट का उपयोग कर सकते हैं।"
        ),

        // === TOPIC 2: OTP Fraud ===
        QuizQuestion(
            id = 6,
            topic = "OTP Fraud",
            questionEn = "A caller claiming to be your bank manager asks for the 6-digit OTP sent to your phone to 'activate benefits'. What should you do?",
            questionHi = "बैंक मैनेजर होने का दावा करने वाला एक कॉलर 'लाभ सक्रिय करने' के लिए आपके फोन पर भेजा गया 6-अंकीय ओटीपी मांगता है। आपको क्या करना चाहिए?",
            optionsEn = listOf(
                "Give them the OTP immediately so you don't lose the benefits.",
                "Tell them the OTP but change one digit to test them.",
                "Hang up, block the number, and never share any OTP with anyone.",
                "Ask them to hold and post the OTP on Twitter to verify."
            ),
            optionsHi = listOf(
                "उन्हें तुरंत ओटीपी दे दें ताकि आपके लाभ न छूटें।",
                "उन्हें ओटीपी बताएं लेकिन उनकी परीक्षा लेने के लिए एक अंक बदल दें।",
                "फोन काट दें, नंबर ब्लॉक करें और किसी के साथ कोई भी ओटीपी साझा न करें।",
                "उन्हें रुकने के लिए कहें और सत्यापित करने के लिए ट्विटर पर ओटीपी पोस्ट करें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "No legitimate bank employee is authorized to ask for your OTP. OTPs are private keys meant strictly for self-input.",
            explanationHi = "किसी भी वास्तविक बैंक कर्मचारी को आपका ओटीपी मांगने का अधिकार नहीं है। ओटीपी निजी कोड होते हैं जो केवल खुद दर्ज करने के लिए होते हैं।"
        ),
        QuizQuestion(
            id = 7,
            topic = "OTP Fraud",
            questionEn = "What does 'OTP' stand for, and what is its role in authentication?",
            questionHi = "'OTP' का क्या अर्थ है, और प्रमाणीकरण में इसकी क्या भूमिका है?",
            optionsEn = listOf(
                "Over-The-Phone, used for call recording authorization.",
                "One-Time Password, acting as a second layer of security for transaction approval.",
                "Open Transfer Protocol, used to sync banking apps.",
                "Official Transaction Pin, used to withdraw ATM cash."
            ),
            optionsHi = listOf(
                "ओवर-द-फोन (Over-The-Phone), कॉल रिकॉर्डिंग प्राधिकरण के लिए उपयोग किया जाता है।",
                "वन-टाइम पासवर्ड (One-Time Password), लेनदेन अनुमोदन के लिए सुरक्षा की दूसरी परत के रूप में कार्य करता है।",
                "ओपन ट्रांसफर प्रोटोकॉल, बैंकिंग ऐप्स को सिंक करने के लिए उपयोग किया जाता है।",
                "आधिकारिक लेनदेन पिन, एटीएम नकद निकालने के लिए उपयोग किया जाता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "OTP stands for One-Time Password. It is valid for only one session and proves that the person initiating the transaction physically possesses the registered SIM.",
            explanationHi = "ओटीपी का अर्थ वन-टाइम पासवर्ड है। यह केवल एक सत्र के लिए मान्य होता है और यह साबित करता है कि लेनदेन शुरू करने वाले व्यक्ति के पास पंजीकृत सिम मौजूद है।"
        ),
        QuizQuestion(
            id = 8,
            topic = "OTP Fraud",
            questionEn = "Under what conditions should you share a One-Time Password received on your mobile with a customer service agent?",
            questionHi = "अपने मोबाइल पर प्राप्त वन-टाइम पासवर्ड को आपको किस परिस्थिति में ग्राहक सेवा एजेंट के साथ साझा करना चाहिए?",
            optionsEn = listOf(
                "Only when they speak politely and claim they are verifying your name.",
                "Only when the SMS text explicitly says 'Share this OTP with agents'.",
                "Never. No legitimate agent requires your transaction OTP under any condition.",
                "Only during online refund procedures to cancel transactions."
            ),
            optionsHi = listOf(
                "केवल तब जब वे विनम्रता से बात करें और दावा करें कि वे आपके नाम की पुष्टि कर रहे हैं।",
                "केवल तब जब एसएमएस पाठ स्पष्ट रूप से कहता है 'इस ओटीपी को एजेंटों के साथ साझा करें'।",
                "कभी नहीं। किसी भी परिस्थिति में किसी भी वास्तविक एजेंट को आपके लेनदेन ओटीपी की आवश्यकता नहीं होती है।",
                "केवल ऑनलाइन रिफंड प्रक्रियाओं के दौरान लेनदेन रद्द करने के लिए।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Read SMS headers carefully. They almost always warn: 'Do not share this OTP with anyone, including bank staff'. Abide by this warning strictly.",
            explanationHi = "एसएमएस संदेशों को ध्यान से पढ़ें। वे लगभग हमेशा चेतावनी देते हैं: 'बैंक कर्मचारियों सहित किसी के भी साथ यह ओटीपी साझा न करें'। इस चेतावनी का सख्ती से पालन करें।"
        ),
        QuizQuestion(
            id = 9,
            topic = "OTP Fraud",
            questionEn = "If you receive an OTP without requesting one, what does this indicate?",
            questionHi = "यदि आपको बिना अनुरोध किए कोई ओटीपी प्राप्त होता है, तो यह क्या दर्शाता है?",
            optionsEn = listOf(
                "Your mobile network is experiencing a routine upgrade.",
                "An attacker has guessed your password or username and is trying to bypass the secondary auth wall.",
                "You have won a lottery sweepstake from your service provider.",
                "The bank is checking if your mobile is active."
            ),
            optionsHi = listOf(
                "आपका मोबाइल नेटवर्क सामान्य अपग्रेड का अनुभव कर रहा है।",
                "एक हमलावर ने आपके पासवर्ड या उपयोगकर्ता नाम का अनुमान लगा लिया है और दूसरी सुरक्षा दीवार को तोड़ने की कोशिश कर रहा है।",
                "आपने अपने सेवा प्रदाता से लॉटरी जीती है।",
                "बैंक यह जांच रहा है कि आपका मोबाइल चालू है या नहीं।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Receiving unsolicited OTP alerts means someone has initiated an action on your account using your credentials. Change your passwords immediately.",
            explanationHi = "बिना अनुरोध के ओटीपी प्राप्त होने का मतलब है कि किसी ने आपके क्रेडेंशियल का उपयोग करके आपके खाते पर कोई कार्रवाई शुरू की है। तुरंत अपने पासवर्ड बदलें।"
        ),
        QuizQuestion(
            id = 10,
            topic = "OTP Fraud",
            questionEn = "Why is sharing an OTP over WhatsApp or SMS as risky as telling someone over a call?",
            questionHi = "व्हाट्सएप या एसएमएस पर ओटीपी साझा करना कॉल पर किसी को बताने जितना ही जोखिम भरा क्यों है?",
            optionsEn = listOf(
                "It is not risky because text messages are encrypted by the phone.",
                "Because digital logs can be stored, forwarded, and instantly exploited by automated script attacks.",
                "It is only risky if your smartphone battery is below 20%.",
                "Because WhatsApp charges extra transaction fees for sharing numbers."
            ),
            optionsHi = listOf(
                "यह जोखिम भरा नहीं है क्योंकि पाठ संदेश फोन द्वारा एन्क्रिप्ट किए जाते हैं।",
                "क्योंकि डिजिटल लॉग को सहेजा, अग्रेषित किया जा सकता है और स्वचालित स्क्रिप्ट हमलों द्वारा तुरंत दुरुपयोग किया जा सकता है।",
                "यह केवल तभी जोखिम भरा है जब आपकी स्मार्टफोन बैटरी 20% से कम हो।",
                "क्योंकि व्हाट्सएप नंबर साझा करने के लिए अतिरिक्त लेनदेन शुल्क लेता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Any digital message sharing a security key creates a permanent record that attackers can copy, scrape, or feed into automated transfer tools.",
            explanationHi = "सुरक्षा कोड साझा करने वाला कोई भी डिजिटल संदेश एक स्थायी रिकॉर्ड बनाता है जिसे हमलावर कॉपी कर सकते हैं या स्वचालित उपकरणों में फीड कर सकते हैं।"
        ),

        // === TOPIC 3: UPI Scam ===
        QuizQuestion(
            id = 11,
            topic = "UPI Scam",
            questionEn = "When receiving money through UPI (Unified Payments Interface), what action is required from you?",
            questionHi = "यूपीआई (Unified Payments Interface) के माध्यम से पैसे प्राप्त करते समय, आपको क्या कार्रवाई करनी पड़ती है?",
            optionsEn = listOf(
                "You must enter your secret UPI PIN to authorize the receipt.",
                "You must scan the sender's payment QR code.",
                "No action is required. The money is directly credited without entering pins or scanning codes.",
                "You must send Rs. 1 back to verify the connection."
            ),
            optionsHi = listOf(
                "आपको पैसे प्राप्त करने के लिए अपना गुप्त यूपीआई पिन दर्ज करना होगा।",
                "आपको भेजने वाले का भुगतान क्यूआर कोड स्कैन करना होगा।",
                "किसी कार्रवाई की आवश्यकता नहीं है। बिना पिन दर्ज किए या कोड स्कैन किए पैसे सीधे जमा हो जाते हैं।",
                "कनेक्शन सत्यापित करने के लिए आपको 1 रुपये वापस भेजने होंगे।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Plausible scams claim you must enter your PIN to 'receive' money. Remember: UPI PIN is ONLY entered when you are paying out money.",
            explanationHi = "धोखेबाज दावा करते हैं कि पैसे 'प्राप्त' करने के लिए आपको अपना पिन दर्ज करना होगा। याद रखें: यूपीआई पिन केवल तभी दर्ज किया जाता है जब आप पैसे भेज रहे हों।"
        ),
        QuizQuestion(
            id = 12,
            topic = "UPI Scam",
            questionEn = "What happens when you scan a UPI QR code sent by a buyer claiming they want to transfer money to you?",
            questionHi = "जब आप किसी खरीदार द्वारा भेजे गए यूपीआई क्यूआर कोड को स्कैन करते हैं जो दावा करता है कि वह आपको पैसे ट्रांसफर करना चाहता है, तो क्या होता है?",
            optionsEn = listOf(
                "The funds are instantly credited to your bank wallet.",
                "You are directed to a payment interface where funds will be debited from your account.",
                "Your smartphone automatically locks out incoming calls.",
                "The scanning app checks if the buyer has sufficient balance."
            ),
            optionsHi = listOf(
                "पैसे तुरंत आपके बैंक वॉलेट में जमा हो जाते हैं।",
                "आपको एक भुगतान इंटरफ़ेस पर निर्देशित किया जाता है जहाँ आपके खाते से पैसे कट जाएंगे।",
                "आपका स्मार्टफोन आने वाले कॉल को स्वचालित रूप से ब्लॉक कर देता है।",
                "स्कैनिंग ऐप यह जांचता है कि खरीदार के पास पर्याप्त शेष राशि है या नहीं।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Scanning a QR code initiates a 'pay' intent. It sets up an outgoing transfer from your account to the destination mapped in the QR code.",
            explanationHi = "क्यूआर कोड को स्कैन करना एक भुगतान प्रक्रिया को शुरू करता है। यह आपके खाते से क्यूआर कोड में मैप किए गए पते पर पैसे भेजने का काम करता है।"
        ),
        QuizQuestion(
            id = 13,
            topic = "UPI Scam",
            questionEn = "A buyer on an online marketplace sends a 'Collect Request' via UPI to pay you. What does accepting this request do?",
            questionHi = "ऑनलाइन मार्केटप्लेस पर एक खरीदार आपको भुगतान करने के लिए यूपीआई के माध्यम से 'कलेक्ट रिक्वेस्ट' भेजता है। इस अनुरोध को स्वीकार करने से क्या होता है?",
            optionsEn = listOf(
                "It accepts their payment and deposits the money into your account.",
                "It authorizes a deduction from your account, transferring your money to the requester.",
                "It registers the buyer as a trusted safe contact.",
                "It triggers a secure delivery tracker for the product."
            ),
            optionsHi = listOf(
                "यह उनके भुगतान को स्वीकार करता है और पैसे आपके खाते में जमा करता है।",
                "यह आपके खाते से कटौती को अधिकृत करता है, जिससे आपके पैसे अनुरोधकर्ता को ट्रांसफर हो जाते हैं।",
                "यह खरीदार को एक विश्वसनीय सुरक्षित संपर्क के रूप में पंजीकृत करता है।",
                "यह उत्पाद के लिए एक सुरक्षित डिलीवरी ट्रैकर शुरू करता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "A UPI Collect Request is a demand for money from your account. Approving it and typing your PIN transfers your money to the requester immediately.",
            explanationHi = "यूपीआई कलेक्ट रिक्वेस्ट आपके खाते से पैसे की मांग है। इसे स्वीकार करने और अपना पिन दर्ज करने से आपके पैसे तुरंत अनुरोधकर्ता को ट्रांसफर हो जाते हैं।"
        ),
        QuizQuestion(
            id = 14,
            topic = "UPI Scam",
            questionEn = "Where is your UPI PIN stored, and who has access to it?",
            questionHi = "आपका यूपीआई पिन कहां संग्रहीत होता है, और उस तक किसकी पहुंच होती है?",
            optionsEn = listOf(
                "It is stored in clear text on the Google Play Store.",
                "It is encrypted securely inside banking server modules; only you should know it.",
                "It is saved inside your SIM card card registry.",
                "Your local bank manager has a list of all user pins."
            ),
            optionsHi = listOf(
                "यह Google Play Store पर स्पष्ट रूप से सहेजा जाता है।",
                "यह बैंकिंग सर्वर मॉड्यूल के भीतर सुरक्षित रूप से एन्क्रिप्टेड होता है; केवल आपको ही यह पता होना चाहिए।",
                "यह आपके सिम कार्ड रजिस्ट्री के अंदर सहेजा जाता है।",
                "आपके स्थानीय बैंक मैनेजर के पास सभी उपयोगकर्ता पिनों की एक सूची होती है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Your UPI PIN is a highly confidential secret credential. No app staff, bank manager, or support technician has access to it. Never write it down or share it.",
            explanationHi = "आपका यूपीआई पिन एक अत्यधिक गोपनीय क्रेडेंशियल है। किसी भी ऐप कर्मचारी, बैंक मैनेजर या तकनीशियन के पास इस तक पहुंच नहीं होती है। इसे कभी भी कहीं न लिखें और न साझा करें।"
        ),
        QuizQuestion(
            id = 15,
            topic = "UPI Scam",
            questionEn = "What is the main security purpose of setting up a UPI transaction limit in your banking app?",
            questionHi = "आपके बैंकिंग ऐप में यूपीआई लेनदेन सीमा (Transaction Limit) निर्धारित करने का मुख्य सुरक्षा उद्देश्य क्या है?",
            optionsEn = listOf(
                "It reduces the amount of battery used during payment scans.",
                "It limits the maximum funds that can be stolen in case your account is compromised.",
                "It helps the bank track your monthly tax brackets.",
                "It automatically blocks spam calls."
            ),
            optionsHi = listOf(
                "यह भुगतान स्कैन के दौरान उपयोग होने वाली बैटरी की मात्रा को कम करता है।",
                "यदि आपका खाता हैक हो जाता है, तो यह चोरी होने वाले अधिकतम धन को सीमित कर देता है।",
                "यह बैंक को आपके मासिक टैक्स स्लैब को ट्रैक करने में मदद करता है।",
                "यह स्वचालित रूप से स्पैम कॉल को ब्लॉक करता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Setting a reasonable daily transaction limit prevents fraudsters from draining your entire account balance in a single compromised session.",
            explanationHi = "एक उचित दैनिक लेनदेन सीमा निर्धारित करना जालसाजों को एक ही बार में आपके खाते की पूरी राशि साफ करने से रोकता है।"
        ),

        // === TOPIC 4: QR Scam ===
        QuizQuestion(
            id = 16,
            topic = "QR Scam",
            questionEn = "A sticker of a payment QR code on a shop's counter is peeling. What risk does this present?",
            questionHi = "दुकान के काउंटर पर लगे पेमेंट क्यूआर कोड का स्टिकर उखड़ा हुआ है। यह क्या जोखिम पेश करता है?",
            optionsEn = listOf(
                "The scanner will not be able to read the pixel layouts.",
                "An attacker may have pasted a malicious QR sticker over the original shop code to steal payments.",
                "The transaction speed will be significantly delayed.",
                "The payment app will charge extra commission fees."
            ),
            optionsHi = listOf(
                "स्कैनर पिक्सेल लेआउट को पढ़ने में सक्षम नहीं होगा।",
                "किसी हमलावर ने भुगतान चोरी करने के लिए मूल कोड के ऊपर एक नकली क्यूआर स्टिकर चिपका दिया हो सकता है।",
                "लेनदेन की गति में काफी देरी होगी।",
                "भुगतान ऐप अतिरिक्त कमीशन शुल्क लेगा।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Physical tampering of retail QR codes is a common scam. Always verify the display name matches the merchant's business title before sending funds.",
            explanationHi = "दुकानों के क्यूआर कोड के साथ छेड़छाड़ एक आम घोटाला है। पैसे भेजने से पहले हमेशा सत्यापित करें कि स्क्रीन पर दिखने वाला नाम दुकानदार के व्यापारिक नाम से मेल खाता हो।"
        ),
        QuizQuestion(
            id = 17,
            topic = "QR Scam",
            questionEn = "Can a QR code directly install malware on your Android device?",
            questionHi = "क्या एक क्यूआर कोड सीधे आपके एंड्रॉइड डिवाइस पर मैलवेयर इंस्टॉल कर सकता है?",
            optionsEn = listOf(
                "Yes, QR codes bypass all OS security clearances instantly.",
                "No. It can only redirect your browser to a webpage, which might then trick you into downloading malware.",
                "Only if the QR code is printed in black and white.",
                "Yes, if scanned using the default camera app."
            ),
            optionsHi = listOf(
                "हां, क्यूआर कोड तुरंत सभी ओएस सुरक्षा प्रणालियों को बायपास कर देते हैं।",
                "नहीं। यह केवल आपके ब्राउज़र को एक वेबपेज पर भेज सकता है, जो फिर आपको मैलवेयर डाउनलोड करने के लिए फंसा सकता है।",
                "केवल तभी जब क्यूआर कोड काले और सफेद रंग में मुद्रित हो।",
                "हाँ, यदि डिफ़ॉल्ट कैमरा ऐप का उपयोग करके स्कैन किया गया हो।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "A QR code is just encoded text (usually a URL). The risk arises when the scanned link takes you to a malicious site that prompts you to download harmful files.",
            explanationHi = "एक क्यूआर कोड सिर्फ एन्कोडेड टेक्स्ट (आमतौर पर एक यूआरएल) होता है। जोखिम तब उत्पन्न होता है जब स्कैन किया गया लिंक आपको किसी दुर्भावनापूर्ण साइट पर ले जाता है और आपको हानिकारक फाइलें डाउनलोड करने के लिए प्रेरित करता है।"
        ),
        QuizQuestion(
            id = 18,
            topic = "QR Scam",
            questionEn = "You scan a QR code expecting a discount coupon, but it opens a web page requesting your UPI PIN. What should you do?",
            questionHi = "आप डिस्काउंट कूपन की उम्मीद में एक क्यूआर कोड स्कैन करते हैं, लेकिन यह यूपीआई पिन मांगने वाला वेब पेज खोल देता है। आपको क्या करना चाहिए?",
            optionsEn = listOf(
                "Enter your PIN to verify your coupon discount.",
                "Enter a fake PIN first to verify if the site is real.",
                "Close the page immediately and delete the scanned content.",
                "Restart your phone to clean up cookies."
            ),
            optionsHi = listOf(
                "अपने कूपन डिस्काउंट को सत्यापित करने के लिए अपना पिन दर्ज करें।",
                "साइट असली है या नहीं यह जांचने के लिए पहले एक नकली पिन दर्ज करें।",
                "पेज को तुरंत बंद करें और स्कैन की गई सामग्री को हटा दें।",
                "कुकीज़ साफ करने के लिए अपना फोन रीस्टार्ट करें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "No discount coupon or gift card distribution system requires entering a secure UPI PIN. This is a clear authorization harvesting trap.",
            explanationHi = "किसी भी डिस्काउंट कूपन या उपहार योजना के लिए सुरक्षित यूपीआई पिन दर्ज करने की आवश्यकता नहीं होती है। यह स्पष्ट रूप से पैसे ऐंठने का जाल है।"
        ),
        QuizQuestion(
            id = 19,
            topic = "QR Scam",
            questionEn = "Why should you always double-check the recipient's display name after scanning a payment QR code?",
            questionHi = "पेमेंट क्यूआर कोड स्कैन करने के बाद आपको हमेशा प्राप्तकर्ता के नाम की दोबारा जांच क्यों करनी चाहिए?",
            optionsEn = listOf(
                "To ensure the recipient receives notification points.",
                "To verify that the payment is routing to the actual business or intended individual and not an attacker's account.",
                "To help the payment app calculate cashbacks.",
                "To check if the merchant is currently online."
            ),
            optionsHi = listOf(
                "यह सुनिश्चित करने के लिए कि प्राप्तकर्ता को नोटिफिकेशन पॉइंट्स मिले।",
                "यह सत्यापित करने के लिए कि भुगतान वास्तविक व्यवसाय या इच्छित व्यक्ति को जा रहा है न कि किसी जालसाज के खाते में।",
                "भुगतान ऐप को कैशबैक की गणना करने में मदद करने के लिए।",
                "यह जांचने के लिए कि दुकानदार इस समय ऑनलाइन है या नहीं।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Double-checking the display name is the easiest way to detect tampered QR codes. If the name is completely unrelated, stop the payment.",
            explanationHi = "स्क्रीन पर दिखने वाले नाम की दोबारा जांच करना छेड़छाड़ किए गए क्यूआर कोड का पता लगाने का सबसे आसान तरीका है। यदि नाम पूरी तरह से अलग है, तो भुगतान रोक दें।"
        ),
        QuizQuestion(
            id = 20,
            topic = "QR Scam",
            questionEn = "A WhatsApp message from a friend's compromised account sends a QR code claiming 'Scan to win Rs. 5000'. What is the best action?",
            questionHi = "दोस्त के हैक किए गए खाते से एक व्हाट्सएप संदेश आता है जिसमें एक क्यूआर कोड भेजकर दावा किया जाता है '5000 रुपये जीतने के लिए स्कैन करें'। सबसे अच्छा उपाय क्या है?",
            optionsEn = listOf(
                "Scan the code immediately to secure the prize.",
                "Scan the code but only enter a small amount.",
                "Ignore and delete the message. Call your friend through regular phone call to inform them.",
                "Forward the QR code to your other family groups."
            ),
            optionsHi = listOf(
                "इनाम सुरक्षित करने के लिए तुरंत कोड स्कैन करें।",
                "कोड स्कैन करें लेकिन केवल एक छोटी राशि दर्ज करें।",
                "अनदेखा करें और संदेश हटाएं। अपने दोस्त को सूचित करने के लिए सामान्य फोन कॉल करें।",
                "क्यूआर कोड को अपने अन्य पारिवारिक समूहों में अग्रेषित करें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Compromised social media profiles are widely used to spread QR and donation scams to the victim's close contact lists.",
            explanationHi = "हैक किए गए सोशल मीडिया प्रोफाइल का उपयोग अक्सर पीड़ित के करीबी संपर्कों को क्यूआर कोड या दान के नाम पर ठगने के लिए किया जाता है।"
        ),

        // === TOPIC 5: Fake Banking ===
        QuizQuestion(
            id = 21,
            topic = "Fake Banking",
            questionEn = "Which of the following is the official format for a true bank's SMS transactional alert?",
            questionHi = "निम्नलिखित में से कौन सा प्रारूप एक वास्तविक बैंक के एसएमएस लेनदेन अलर्ट का आधिकारिक प्रारूप है?",
            optionsEn = listOf(
                "A standard 10-digit personal mobile number (e.g., +91 98765 43210).",
                "An authorized alphabetic header with regional prefix (e.g., VK-HDFCBK or AD-ICICIB).",
                "An anonymous email address ending with gmail.com.",
                "A private WhatsApp message from 'Bank Support'."
            ),
            optionsHi = listOf(
                "एक सामान्य 10-अंकीय व्यक्तिगत मोबाइल नंबर (जैसे, +91 98765 43210)।",
                "क्षेत्रीय उपसर्ग के साथ एक अधिकृत अल्फ़ाबेटिक हेडर (जैसे, VK-HDFCBK या AD-ICICIB)।",
                "gmail.com पर समाप्त होने वाला एक अज्ञात ईमेल पता।",
                "'बैंक सपोर्ट' से एक निजी व्हाट्सएप संदेश।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Real transactional alerts from registered commercial banks utilize specialized short codes mandated by telecommunication regulators.",
            explanationHi = "पंजीकृत व्यावसायिक बैंकों से वास्तविक लेनदेन अलर्ट दूरसंचार नियामकों द्वारा अनिवार्य विशेष शॉर्ट कोड (जैसे VK-HDFCBK) का उपयोग करते हैं।"
        ),
        QuizQuestion(
            id = 22,
            topic = "Fake Banking",
            questionEn = "You get a call saying your debit card is blocked and they need your CVV and expiry date to unblock it. What is this?",
            questionHi = "आपको एक कॉल आती है जिसमें कहा जाता है कि आपका डेबिट कार्ड ब्लॉक हो गया है और इसे अनब्लॉक करने के लिए उन्हें आपका सीवीवी (CVV) और समाप्ति तिथि की आवश्यकता है। यह क्या है?",
            optionsEn = listOf(
                "A routine security check performed by bank staff.",
                "An automated service call to update card chips.",
                "A social engineering scam designed to steal your card data and execute fraudulent transactions.",
                "A credit score verification call."
            ),
            optionsHi = listOf(
                "बैंक कर्मचारियों द्वारा की जाने वाली एक सामान्य सुरक्षा जांच।",
                "कार्ड चिप्स को अपडेट करने के लिए एक स्वचालित सेवा कॉल।",
                "आपके कार्ड का डेटा चुराने और धोखाधड़ी से पैसे निकालने के लिए तैयार किया गया एक घोटाला।",
                "एक क्रेडिट स्कोर सत्यापन कॉल।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "No financial body needs your private CVV code or expiry date to resolve card blocks. This is a classic card harvesting fraud.",
            explanationHi = "कार्ड ब्लॉक को ठीक करने के लिए किसी भी वित्तीय संस्थान को आपके निजी सीवीवी कोड या समाप्ति तिथि की आवश्यकता नहीं होती है। यह एक क्लासिक कार्ड फ्रॉड है।"
        ),
        QuizQuestion(
            id = 23,
            topic = "Fake Banking",
            questionEn = "What is the safest way to find the official customer care number of your bank?",
            questionHi = "अपने बैंक का आधिकारिक ग्राहक सेवा नंबर (Customer Care Number) खोजने का सबसे सुरक्षित तरीका क्या है?",
            optionsEn = listOf(
                "Search 'Bank Customer Care' on Google and call the top ad result.",
                "Look for contact details on the back of your physical debit/credit card or on the official bank portal.",
                "Ask in a public Telegram channel.",
                "Check telephone numbers listed on Google Maps locations."
            ),
            optionsHi = listOf(
                "Google पर 'बैंक कस्टमर केयर' खोजें और शीर्ष विज्ञापन परिणाम पर कॉल करें।",
                "अपने भौतिक डेबिट/क्रेडिट कार्ड के पीछे या आधिकारिक बैंक पोर्टल पर संपर्क विवरण देखें।",
                "एक सार्वजनिक टेलीग्राम चैनल में पूछें।",
                "गूगल मैप्स स्थानों पर सूचीबद्ध टेलीफोन नंबरों की जांच करें।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Scammers upload fake customer care numbers on Google Search ads and Google Maps entries to intercept stranded users.",
            explanationHi = "जालसाज उपयोगकर्ताओं को फंसाने के लिए Google खोज विज्ञापनों और Google मानचित्र प्रविष्टियों पर नकली ग्राहक सेवा नंबर अपलोड करते हैं।"
        ),
        QuizQuestion(
            id = 24,
            topic = "Fake Banking",
            questionEn = "If you find an unauthorized small transaction of Rs. 10 in your bank statement, what should you do?",
            questionHi = "यदि आप अपने बैंक स्टेटमेंट में 10 रुपये का एक छोटा सा अनधिकृत लेनदेन पाते हैं, तो आपको क्या करना चाहिए?",
            optionsEn = listOf(
                "Ignore it because the amount is too small to worry about.",
                "Wait for a month to see if the merchant issues a refund.",
                "Notify your bank immediately and lock/freeze your card.",
                "Delete your banking application and reinstall it."
            ),
            optionsHi = listOf(
                "इसे अनदेखा करें क्योंकि राशि इतनी छोटी है कि चिंता करने की आवश्यकता नहीं है।",
                "यह देखने के लिए एक महीने प्रतीक्षा करें कि क्या व्यापारी रिफंड जारी करता है।",
                "तुरंत अपने बैंक को सूचित करें और अपने कार्ड को ब्लॉक/फ्रीज करें।",
                "अपने बैंकिंग एप्लिकेशन को हटा दें और इसे फिर से इंस्टॉल करें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Attackers execute micro-payments first to verify if card credentials are valid before executing large, devastating transfers.",
            explanationHi = "हमलावर बड़े और नुकसानदेह लेनदेन करने से पहले यह सत्यापित करने के लिए छोटे लेनदेन (माइक्रो-पेमेंट) करते हैं कि कार्ड विवरण काम कर रहा है या नहीं।"
        ),
        QuizQuestion(
            id = 25,
            topic = "Fake Banking",
            questionEn = "What is 'SIM Swap' fraud in digital banking?",
            questionHi = "डिजिटल बैंकिंग में 'सिम स्वैप' (SIM Swap) धोखाधड़ी क्या है?",
            optionsEn = listOf(
                "Stealing someone's phone physically and replacing the SIM chip.",
                "Tricking a telecom operator into issuing a duplicate SIM card of your number to the hacker to intercept OTPs.",
                "Changing SIM networks inside the smartphone settings.",
                "Buying a prepaid SIM card using fake identity documents."
            ),
            optionsHi = listOf(
                "किसी का फोन शारीरिक रूप से चुराना और सिम चिप बदलना।",
                "दूरसंचार ऑपरेटर को धोखा देकर आपके नंबर का डुप्लीकेट सिम कार्ड हैकर को जारी करवाना ताकि वह आपके ओटीपी पढ़ सके।",
                "स्मार्टफोन सेटिंग्स के अंदर सिम नेटवर्क बदलना।",
                "फर्जी पहचान दस्तावेजों का उपयोग करके प्रीपेड सिम कार्ड खरीदना।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "In SIM Swap, the victim's active SIM is deactivated, and the attacker receives all secure call and SMS alerts containing banking OTPs on a duplicate SIM.",
            explanationHi = "सिम स्वैप में, पीड़ित का सक्रिय सिम बंद हो जाता है, और हमलावर को डुप्लीकेट सिम पर बैंकिंग ओटीपी वाले सभी सुरक्षित कॉल और एसएमएस अलर्ट प्राप्त होने लगते हैं।"
        ),

        // === TOPIC 6: Social Engineering ===
        QuizQuestion(
            id = 26,
            topic = "Social Engineering",
            questionEn = "A stranger calls crying, pretending to be your nephew who has been arrested and needs bail money immediately. What is this called?",
            questionHi = "एक अजनबी रोते हुए फोन करता है, और आपका भतीजा होने का नाटक करता है जो गिरफ्तार हो गया है और उसे तुरंत जमानत के पैसे चाहिए। इसे क्या कहा जाता है?",
            optionsEn = listOf(
                "Phishing attack.",
                "Emergency / Grandparent Scam (Social Engineering).",
                "Ransomware threat.",
                "Denial of service."
            ),
            optionsHi = listOf(
                "फ़िशिंग हमला।",
                "इमरजेंसी / ग्रैंडपेरेंट स्कैम (सोशल इंजीनियरिंग)।",
                "रैंसमवेयर का खतरा।",
                "सेवा से इनकार (Denial of service)।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Emergency scams exploit emotional panic. Always hang up and call the family member directly through known contact numbers to verify.",
            explanationHi = "आपातकालीन घोटाले भावनात्मक घबराहट का फायदा उठाते हैं। हमेशा फोन काट दें और पुष्टि करने के लिए संबंधित पारिवारिक सदस्य को सीधे कॉल करें।"
        ),
        QuizQuestion(
            id = 27,
            topic = "Social Engineering",
            questionEn = "Why do cybercriminals use high-pressure tactics or artificial emergencies?",
            questionHi = "साइबर अपराधी जल्दबाजी कराने वाले तरीकों या कृत्रिम आपात स्थिति का उपयोग क्यों करते हैं?",
            optionsEn = listOf(
                "To save bandwidth on telecom channels.",
                "To trigger panic, which bypasses your analytical thinking and prompts immediate compliance.",
                "Because they are legally required to complete tasks quickly.",
                "To ensure you receive their calls during off-duty hours."
            ),
            optionsHi = listOf(
                "दूरसंचार चैनलों पर बैंडविड्थ बचाने के लिए।",
                "घबराहट पैदा करने के लिए, जिससे आपकी सोचने-समझने की क्षमता प्रभावित हो जाती है और आप तुरंत उनकी बात मान लें।",
                "क्योंकि उन्हें कानूनी रूप से कार्यों को जल्दी पूरा करने की आवश्यकता होती है।",
                "यह सुनिश्चित करने के लिए कि आप उनके फोन कॉल खाली समय में प्राप्त करें।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Fear and urgency override critical thinking. Scammers create a 'act now or suffer consequences' setup to prevent you from researching.",
            explanationHi = "डर और जल्दबाजी सोचने-समझने की क्षमता को दबा देती है। घोटालेबाज एक 'अभी करें या परिणाम भुगतें' जैसी स्थिति बनाते हैं ताकि आपको सोचने का समय न मिले।"
        ),
        QuizQuestion(
            id = 28,
            topic = "Social Engineering",
            questionEn = "What is 'Pretexting' in the context of cyber scams?",
            questionHi = "साइबर घोटालों के संदर्भ में 'प्रीटेक्सटिंग' (Pretexting) क्या है?",
            optionsEn = listOf(
                "Writing security reports prior to application deployments.",
                "Creating an invented scenario (pretext) to convince victims to release information or perform actions.",
                "Sending test text messages to verify cellular network signals.",
                "Scanning websites for open security ports."
            ),
            optionsHi = listOf(
                "एप्लिकेशन लॉन्च करने से पहले सुरक्षा रिपोर्ट लिखना।",
                "पीड़ितों को जानकारी देने या कार्रवाई करने के लिए मनाने के लिए एक काल्पनिक परिदृश्य (बहाना) बनाना।",
                "सेलुलर नेटवर्क सिग्नल को सत्यापित करने के लिए परीक्षण संदेश भेजना।",
                "खुले सुरक्षा पोर्ट के लिए वेबसाइटों को स्कैन करना।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Pretexting involves the scammer role-playing as an authority figure, courier delivery agent, or IT technician to build mock credibility.",
            explanationHi = "प्रीटेक्सटिंग में घोटालेबाज खुद को कोई अधिकारी, कूरियर डिलीवरी एजेंट या आईटी तकनीशियन बताकर झूठा विश्वास हासिल करने की कोशिश करता है।"
        ),
        QuizQuestion(
            id = 29,
            topic = "Social Engineering",
            questionEn = "An online acquaintance asks you to receive a package for them and pay a small customs clearance fee online. What is the danger?",
            questionHi = "एक ऑनलाइन परिचित आपसे उनके लिए एक पार्सल प्राप्त करने और उसके लिए ऑनलाइन एक छोटा सीमा शुल्क (Customs fee) देने के लिए कहता है। इसमें क्या खतरा है?",
            optionsEn = listOf(
                "The package might be too heavy to lift.",
                "The courier agent might charge extra tips.",
                "The scenario is a scam; there is no package, and your fee payment goes straight to the fraudster.",
                "The package contains cheap products."
            ),
            optionsHi = listOf(
                "पार्सल उठाने में बहुत भारी हो सकता है।",
                "कूरियर एजेंट अतिरिक्त बख्शिश मांग सकता है।",
                "यह परिदृश्य एक घोटाला है; वहां वास्तव में कोई पार्सल नहीं है, और आपके पैसे सीधे जालसाज के पास जाते हैं।",
                "पार्सल में सस्ते उत्पाद हैं।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Customs fee fraud is highly common. Scammers build fake shipping track logs showing fake packages stuck at borders to extract clearances.",
            explanationHi = "कस्टम्स शुल्क धोखाधड़ी बहुत आम है। घोटालेबाज सीमा पर फंसे पार्सल दिखाने वाले नकली ट्रैकिंग लॉग बनाते हैं ताकि आपसे पैसे ऐंठ सकें।"
        ),
        QuizQuestion(
            id = 30,
            topic = "Social Engineering",
            questionEn = "How should you handle a social media message from an old friend asking for financial help because they are 'stranded'?",
            questionHi = "सोशल मीडिया पर एक पुराने दोस्त का संदेश आने पर आप क्या करेंगे जिसमें लिखा हो कि वह कहीं 'फंस' गया है और उसे आर्थिक मदद चाहिए?",
            optionsEn = listOf(
                "Transfer the money immediately to show your friendship.",
                "Ignore them because true friends never ask for money on social media.",
                "Call them on their known phone number or speak with a mutual friend to confirm the message is genuine.",
                "Reply asking for their banking password to check their balance."
            ),
            optionsHi = listOf(
                "अपनी दोस्ती दिखाने के लिए तुरंत पैसे ट्रांसफर करें।",
                "उन्हें अनदेखा करें क्योंकि सच्चे दोस्त कभी सोशल मीडिया पर पैसे नहीं मांगते।",
                "उनके ज्ञात फोन नंबर पर कॉल करें या किसी साझा मित्र से बात करके पुष्टि करें कि संदेश वास्तविक है।",
                "उनके खाते की जांच करने के लिए उनका बैंकिंग पासवर्ड मांगें।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "Social media accounts are easily hacked. Scammers send mass emergency requests to all contacts on the compromised account's friend list.",
            explanationHi = "सोशल मीडिया खाते आसानी से हैक हो जाते हैं। हैकर्स हैक किए गए खाते की मित्र सूची में शामिल सभी संपर्कों को सामूहिक रूप से ऐसे संदेश भेजते हैं।"
        ),

        // === TOPIC 7: Password Safety ===
        QuizQuestion(
            id = 31,
            topic = "Password Safety",
            questionEn = "Which of the following represents the strongest password?",
            questionHi = "निम्नलिखित में से कौन सा सबसे मजबूत पासवर्ड का प्रतिनिधित्व करता है?",
            optionsEn = listOf(
                "P@ssword123!",
                "Tr34t\$h1eld_AI_2026!",
                "JohnDoe_1995",
                "abcdefgh"
            ),
            optionsHi = listOf(
                "P@ssword123!",
                "Tr34t\$h1eld_AI_2026!",
                "JohnDoe_1995",
                "abcdefgh"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Strong passwords are long (12+ characters), unique, and contain a mix of uppercase, lowercase, numbers, and special symbols.",
            explanationHi = "मजबूत पासवर्ड लंबे (12+ अक्षर), अनोखे होते हैं और उनमें बड़े अक्षरों, छोटे अक्षरों, संख्याओं और विशेष प्रतीकों का मिश्रण होता है।"
        ),
        QuizQuestion(
            id = 32,
            topic = "Password Safety",
            questionEn = "What is the primary security flaw of using the same password across multiple online accounts?",
            questionHi = "कई ऑनलाइन खातों में एक ही पासवर्ड का उपयोग करने का प्राथमिक सुरक्षा दोष क्या है?",
            optionsEn = listOf(
                "It makes it harder for you to remember your passwords.",
                "If one website suffers a data breach, hackers will immediately gain access to all your other accounts.",
                "It automatically signs you out of your email.",
                "The banking applications will charge higher transactional fees."
            ),
            optionsHi = listOf(
                "यह आपके लिए अपने पासवर्ड याद रखना कठिन बनाता है।",
                "यदि एक वेबसाइट का डेटा लीक हो जाता है, तो हैकर्स तुरंत आपके अन्य सभी खातों तक पहुंच जाएंगे।",
                "यह स्वचालित रूप से आपको आपके ईमेल से साइन आउट कर देता है।",
                "बैंकिंग एप्लिकेशन अधिक लेनदेन शुल्क लेंगे।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Reusing passwords makes you vulnerable to 'Credential Stuffing' attacks, where hackers test stolen login pairs on hundreds of other platforms.",
            explanationHi = "पासवर्ड दोबारा इस्तेमाल करने से आप 'क्रेडेंशियल स्टफिंग' हमलों के प्रति संवेदनशील हो जाते हैं, जहां हैकर्स चोरी किए गए पासवर्ड को सैकड़ों अन्य प्लेटफॉर्मों पर आजमाते हैं।"
        ),
        QuizQuestion(
            id = 33,
            topic = "Password Safety",
            questionEn = "What is the best tool to manage dozens of complex, unique passwords securely?",
            questionHi = "दर्जनों जटिल, अनूठे पासवर्डों को सुरक्षित रूप से प्रबंधित करने का सबसे अच्छा साधन क्या है?",
            optionsEn = listOf(
                "A physical sticky note on your computer screen.",
                "An encrypted Password Manager.",
                "A clear text file named 'passwords.txt' on your desktop.",
                "Your WhatsApp starred messages list."
            ),
            optionsHi = listOf(
                "कंप्यूटर स्क्रीन पर चिपकाया गया एक स्टिकर।",
                "एक एन्क्रिप्टेड पासवर्ड मैनेजर (Password Manager)।",
                "डेस्कटॉप पर 'passwords.txt' नाम की एक सामान्य फाइल।",
                "आपके व्हाट्सएप के तारांकित संदेशों (Starred messages) की सूची।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "An encrypted Password Manager stores credentials securely, generates strong random codes, and auto-fills them securely on authentic portals.",
            explanationHi = "एक एन्क्रिप्टेड पासवर्ड मैनेजर क्रेडेंशियल को सुरक्षित रूप से सहेजता है, मजबूत यादृच्छिक कोड बनाता है और उन्हें वास्तविक साइटों पर सुरक्षित रूप से ऑटो-फिल करता है।"
        ),
        QuizQuestion(
            id = 34,
            topic = "Password Safety",
            questionEn = "How often should you update passwords for critical accounts (like email or primary bank)?",
            questionHi = "महत्वपूर्ण खातों (जैसे ईमेल या प्राथमिक बैंक) के पासवर्ड आपको कितनी बार अपडेट करने चाहिए?",
            optionsEn = listOf(
                "Every single day.",
                "Only when you suspect a data breach or at periodic regular safety intervals (e.g., 6 months).",
                "Never, once a password is set, changing it decreases security.",
                "Only when purchasing a new mobile device."
            ),
            optionsHi = listOf(
                "हर एक दिन।",
                "केवल तब जब आपको डेटा लीक का संदेह हो या नियमित सुरक्षा अंतरालों पर (जैसे, हर 6 महीने में)।",
                "कभी नहीं, एक बार पासवर्ड सेट हो जाने पर इसे बदलने से सुरक्षा कम हो जाती है।",
                "केवल नया मोबाइल फोन खरीदते समय।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Keep credentials updated. If any platform alerts you to a system leak, change passwords immediately on that and any historically linked platforms.",
            explanationHi = "क्रेडेंशियल अपडेट रखें। यदि कोई प्लेटफॉर्म सिस्टम लीक की चेतावनी देता है, तो तुरंत उस पर और उससे जुड़े किसी भी प्लेटफॉर्म पर पासवर्ड बदलें।"
        ),
        QuizQuestion(
            id = 35,
            topic = "Password Safety",
            questionEn = "What is 'Credential Stuffing' used by hackers?",
            questionHi = "हैकर्स द्वारा उपयोग की जाने वाली 'क्रेडेंशियल स्टफिंग' (Credential Stuffing) क्या है?",
            optionsEn = listOf(
                "Guessing password sequences based on physical keyboard layouts.",
                "Using automated tools to feed stolen email/password lists into hundreds of popular websites to find matching logins.",
                "Sending fake credit cards to random citizens.",
                "Recovering deleted files from old computer disks."
            ),
            optionsHi = listOf(
                "भौतिक कीबोर्ड लेआउट के आधार पर पासवर्ड अनुक्रमों का अनुमान लगाना।",
                "सैकड़ों लोकप्रिय वेबसाइटों में चोरी की गई ईमेल/पासवर्ड सूची दर्ज करने के लिए स्वचालित उपकरणों का उपयोग करना ताकि मिलान वाले लॉगिन मिल सकें।",
                "यादृच्छिक नागरिकों को नकली क्रेडिट कार्ड भेजना।",
                "पुराने कंप्यूटर डिस्क से हटाई गई फ़ाइलों को पुनर्प्राप्त करना।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Credential Stuffing relies on human laziness. Since users reuse passwords, one breach gives hackers keys to unlock accounts on secondary sites.",
            explanationHi = "क्रेडेंशियल स्टफिंग मानवीय लापरवाही पर निर्भर करती है। चूँकि उपयोगकर्ता पासवर्ड दोबारा उपयोग करते हैं, एक लीक हैकर्स को अन्य साइटों पर खाते खोलने की चाबी दे देता है।"
        ),

        // === TOPIC 8: Malware ===
        QuizQuestion(
            id = 36,
            topic = "Malware",
            questionEn = "You want to download a premium game for free and find an 'unlocked APK' on a third-party website. What is the primary risk?",
            questionHi = "आप एक प्रीमियम गेम मुफ्त में डाउनलोड करना चाहते हैं और किसी तीसरे पक्ष की वेबसाइट पर 'अनलॉक एपीके' पाते हैं। प्राथमिक जोखिम क्या है?",
            optionsEn = listOf(
                "The game might load with black and white graphics.",
                "The APK likely contains embedded malware or spyware that can monitor your banking apps and SMS logs in the background.",
                "Your mobile carrier will charge you extra download fees.",
                "The game will consume less space than the original."
            ),
            optionsHi = listOf(
                "गेम ब्लैक एंड व्हाइट ग्राफिक्स के साथ लोड हो सकता है।",
                "एपीके में एम्बेडेड मैलवेयर या स्पाइवेयर होने की संभावना है जो पृष्ठभूमि में आपके बैंकिंग ऐप्स और एसएमएस लॉग की निगरानी कर सकता है।",
                "आपका मोबाइल कैरियर आपसे अतिरिक्त डाउनलोड शुल्क लेगा।",
                "गेम मूल से कम जगह लेगा।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Modded or cracked APKs from untrusted web forums frequently hide remote access trojans (RATs) to monitor user keyboard logs and security alerts.",
            explanationHi = "अविश्वसनीय वेब फ़ोरम से संशोधित या क्रैक किए गए एपीके अक्सर रिमोट एक्सेस ट्रोजन (RAT) छुपाते हैं जो उपयोगकर्ता की कीबोर्ड गतिविधियों और सुरक्षा अलर्ट की निगरानी करते हैं।"
        ),
        QuizQuestion(
            id = 37,
            topic = "Malware",
            questionEn = "What does 'Ransomware' do to your device?",
            questionHi = "रैंसमवेयर (Ransomware) आपके डिवाइस पर क्या करता है?",
            optionsEn = listOf(
                "It automatically calls premium billing numbers to exhaust balance.",
                "It encrypts your local files/documents and demands a financial payment to restore access.",
                "It physically damages your internal battery charger.",
                "It changes your wallpaper to random advertisements."
            ),
            optionsHi = listOf(
                "यह बैलेंस खत्म करने के लिए स्वचालित रूप से प्रीमियम नंबरों पर कॉल करता है।",
                "यह आपकी स्थानीय फाइलों/दस्तावेजों को लॉक (एन्क्रिप्ट) कर देता है और पहुंच बहाल करने के लिए पैसे की मांग करता है।",
                "यह आपके आंतरिक बैटरी चार्जर को शारीरिक रूप से नुकसान पहुंचाता है।",
                "यह आपके वॉलपेपर को यादृच्छिक विज्ञापनों में बदल देता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Ransomware holds critical personal or corporate data hostage through advanced encryption, asking for ransom payments (usually in cryptocurrency).",
            explanationHi = "रैंसमवेयर उन्नत एन्क्रिप्शन के माध्यम से महत्वपूर्ण व्यक्तिगत या कॉर्पोरेट डेटा को बंधक बना लेता है, और इसे अनलॉक करने के लिए फिरौती (आमतौर पर क्रिप्टोकरेंसी में) मांगता है।"
        ),
        QuizQuestion(
            id = 38,
            topic = "Malware",
            questionEn = "How does a 'Keylogger' compromise your personal security?",
            questionHi = "एक 'कीलॉगर' (Keylogger) आपकी व्यक्तिगत सुरक्षा को कैसे खतरे में डालता है?",
            optionsEn = listOf(
                "It records external ambient sounds around your smartphone.",
                "It captures every keystroke typed on your touch screen, harvesting passwords, bank logins, and confidential texts.",
                "It disables your lock screen pin configuration.",
                "It increases the screen brightness dynamically."
            ),
            optionsHi = listOf(
                "यह आपके स्मार्टफोन के आसपास की बाहरी आवाज रिकॉर्ड करता है।",
                "यह आपकी टच स्क्रीन पर टाइप किए गए प्रत्येक बटन/अक्षर को रिकॉर्ड करता है, जिससे पासवर्ड, बैंक लॉगिन और गोपनीय संदेश चोरी हो जाते हैं।",
                "यह आपके लॉक स्क्रीन पिन कॉन्फ़िगरेशन को निष्क्रिय कर देता है।",
                "यह स्क्रीन की चमक को तेजी से बढ़ाता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Keyloggers are silent monitoring tools that capture input feeds in real-time, sending credentials straight to remote attacker servers.",
            explanationHi = "कीलॉगर मूक निगरानी उपकरण हैं जो रीयल-टाइम में इनपुट फीड को रिकॉर्ड करते हैं और क्रेडेंशियल्स सीधे हमलावर के सर्वर पर भेजते हैं।"
        ),
        QuizQuestion(
            id = 39,
            topic = "Malware",
            questionEn = "What is a common sign that your Android phone has been infected with background malware?",
            questionHi = "आपका एंड्रॉइड फोन पृष्ठभूमि में चल रहे मैलवेयर से संक्रमित है, इसका एक सामान्य लक्षण क्या है?",
            optionsEn = listOf(
                "The system displays beautiful widgets automatically.",
                "Extremely fast battery drain, overheating while idle, and unexpected mass pop-up advertisements.",
                "The storage capacity of the device suddenly doubles.",
                "The camera quality improves dramatically."
            ),
            optionsHi = listOf(
                "सिस्टम स्वचालित रूप से सुंदर विजेट प्रदर्शित करता है।",
                "अत्यधिक तेजी से बैटरी खत्म होना, फोन का बिना उपयोग के भी गर्म होना और अचानक बहुत सारे विज्ञापन (Pop-up ads) दिखाई देना।",
                "डिवाइस की भंडारण क्षमता अचानक दोगुनी हो जाती है।",
                "कैमरे की गुणवत्ता में नाटकीय रूप से सुधार होता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Background spy scripts consume heavy CPU and cellular bandwidth, leading to sudden thermal issues and accelerated battery exhaustion.",
            explanationHi = "पृष्ठभूमि में चलने वाली जासूसी स्क्रिप्ट भारी सीपीयू और सेलुलर डेटा का उपयोग करती हैं, जिससे फोन गर्म होता है और बैटरी तेजी से खत्म होती है।"
        ),
        QuizQuestion(
            id = 40,
            topic = "Malware",
            questionEn = "What is the role of Google Play Protect on your Android device?",
            questionHi = "आपके एंड्रॉइड डिवाइस पर 'Google Play Protect' की क्या भूमिका है?",
            optionsEn = listOf(
                "It encrypts your video streams.",
                "It automatically runs security scans on apps downloaded from Google Play and flags potentially harmful software.",
                "It allows you to download apps without any storage limits.",
                "It acts as a parental lock for social media apps."
            ),
            optionsHi = listOf(
                "यह आपके वीडियो स्ट्रीम को एन्क्रिप्ट करता है।",
                "यह Google Play से डाउनलोड किए गए ऐप्स पर स्वचालित रूप से सुरक्षा स्कैन चलाता है और संभावित हानिकारक सॉफ़्टवेयर को चिह्नित करता है।",
                "यह आपको बिना किसी संग्रहण सीमा के ऐप्स डाउनलोड करने की अनुमति देता है।",
                "यह सोशल मीडिया ऐप्स के लिए पेरेंटल लॉक के रूप में कार्य करता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Google Play Protect constantly monitors apps on your device for malicious behaviors, scanning them on installation and periodically in the background.",
            explanationHi = "गूगल प्ले प्रोटेक्ट दुर्भावनापूर्ण व्यवहारों के लिए आपके डिवाइस पर ऐप्स की लगातार निगरानी करता है, इंस्टॉलेशन के समय और समय-समय पर पृष्ठभूमि में उन्हें स्कैन करता है।"
        ),

        // === TOPIC 9: Online Shopping ===
        QuizQuestion(
            id = 41,
            topic = "Online Shopping",
            questionEn = "You see an ad on social media selling a popular Rs. 80,000 smartphone for just Rs. 4,999. What does this indicate?",
            questionHi = "आप सोशल मीडिया पर एक विज्ञापन देखते हैं जिसमें 80,000 रुपये का एक लोकप्रिय स्मार्टफोन केवल 4,999 रुपये में बेचा जा रहा है। यह क्या दर्शाता है?",
            optionsEn = listOf(
                "An official clearance sale directly from the manufacturer.",
                "A dangerous shopping scam designed to steal your credit card details or collect payments for zero delivery.",
                "A government subsidised digital promotion.",
                "A promotional sweepstake run by the social network."
            ),
            optionsHi = listOf(
                "निर्माता से सीधे आधिकारिक निकासी बिक्री (Clearance sale)।",
                "एक खतरनाक शॉपिंग घोटाला जो आपके क्रेडिट कार्ड विवरण चोरी करने या बिना डिलीवरी के पैसे ऐंठने के लिए बनाया गया है।",
                "सरकार द्वारा प्रायोजित डिजिटल संवर्धन।",
                "सोशल नेटवर्क द्वारा चलाया जाने वाला एक प्रचारक इनाम।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "If an offer is too good to be true, it is almost certainly a scam. Authentic retailers will never list products at deep, ruinous discounts on casual ads.",
            explanationHi = "यदि कोई प्रस्ताव अविश्वसनीय रूप से सस्ता है, तो यह निश्चित रूप से एक घोटाला है। वास्तविक विक्रेता कभी भी सामान्य विज्ञापनों पर इतने भारी नुकसानदेह छूट के साथ उत्पाद सूचीबद्ध नहीं करेंगे।"
        ),
        QuizQuestion(
            id = 42,
            topic = "Online Shopping",
            questionEn = "Before making a purchase on a new e-commerce website, which security aspect must you verify?",
            questionHi = "एक नई ई-कॉमर्स वेबसाइट पर खरीदारी करने से पहले, आपको किस सुरक्षा पहलू को सत्यापित करना चाहिए?",
            optionsEn = listOf(
                "The background layout of the landing screen.",
                "Valid company physical contact, verifiable reviews, secure HTTPS portal connection, and clean refund policies.",
                "The speed of loading animations.",
                "Whether the site support 4K video feeds."
            ),
            optionsHi = listOf(
                "लैंडिंग स्क्रीन का पृष्ठभूमि लेआउट।",
                "वैध कंपनी का भौतिक संपर्क पता, वास्तविक समीक्षाएं, सुरक्षित HTTPS कनेक्शन और स्पष्ट रिफंड नीतियां।",
                "लोडिंग एनिमेशन की गति।",
                "क्या साइट 4K वीडियो फीड का समर्थन करती है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Check reputation metrics before sharing cards. Scam websites display flashy pictures but lack official address registry records and clear refund documentation.",
            explanationHi = "कार्ड विवरण साझा करने से पहले प्रतिष्ठा की जांच करें। घोटाला करने वाली वेबसाइटें चमकदार तस्वीरें प्रदर्शित करती हैं लेकिन उनमें आधिकारिक संपर्क विवरण और स्पष्ट रिफंड नीतियों का अभाव होता है।"
        ),
        QuizQuestion(
            id = 43,
            topic = "Online Shopping",
            questionEn = "What is the safest payment method when buying from an unfamiliar online seller?",
            questionHi = "अपरिचित ऑनलाइन विक्रेता से खरीदते समय सबसे सुरक्षित भुगतान विधि कौन सी है?",
            optionsEn = listOf(
                "Direct bank wire transfer prior to shipping.",
                "Cash-on-Delivery (after physical inspection) or virtual credit card services offering dispute protection.",
                "Sending gift card codes via chat.",
                "Authorizing international wire transfers."
            ),
            optionsHi = listOf(
                "शिपिंग से पहले सीधे बैंक ट्रांसफर।",
                "कैश-ऑन-डिलीवरी (पैकेट प्राप्त करने और जांचने के बाद) या विवाद संरक्षण देने वाले क्रेडिट कार्ड का उपयोग।",
                "चैट के माध्यम से गिफ्ट कार्ड कोड भेजना।",
                "अंतरराष्ट्रीय वायर ट्रांसफर को अधिकृत करना।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Avoid direct bank wire transfers to unverified parties. Use methods that allow transaction disputes or provide physical inspection upon arrival.",
            explanationHi = "अपुष्ट पक्षों को सीधे बैंक वायर ट्रांसफर करने से बचें। ऐसे तरीकों का उपयोग करें जो विवाद दर्ज करने की अनुमति देते हैं या पैकेट प्राप्त होने पर नकद भुगतान की अनुमति देते हैं।"
        ),
        QuizQuestion(
            id = 44,
            topic = "Online Shopping",
            questionEn = "Why do scammers insist on taking the communication off the official shopping platform (like Amazon or eBay) to WhatsApp?",
            questionHi = "घोटालेबाज बातचीत को आधिकारिक शॉपिंग प्लेटफॉर्म (जैसे अमेज़न या ईबे) से हटाकर व्हाट्सएप पर ले जाने पर क्यों जोर देते हैं?",
            optionsEn = listOf(
                "Because WhatsApp charges zero transaction listing fees.",
                "To bypass the platform's security algorithms, anti-fraud filters, and formal purchase protection guarantees.",
                "Because they want to build personal friendships with customers.",
                "To save mobile data."
            ),
            optionsHi = listOf(
                "क्योंकि व्हाट्सएप पर कोई लेनदेन सूची शुल्क नहीं लगता है।",
                "प्लेटफॉर्म के सुरक्षा एल्गोरिदम, धोखाधड़ी विरोधी फिल्टर और औपचारिक खरीद सुरक्षा गारंटी को बायपास करने के लिए।",
                "क्योंकि वे ग्राहकों के साथ व्यक्तिगत मित्रता बनाना चाहते हैं।",
                "मोबाइल डेटा बचाने के लिए।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Keep all dialogs inside official application chats. Platform support can only trace and resolve fraud if the transactions and communication occurred strictly on their monitored portal.",
            explanationHi = "हमेशा बातचीत को आधिकारिक ऐप चैट के अंदर ही रखें। प्लेटफॉर्म सहायता टीम केवल तभी धोखाधड़ी का पता लगा सकती है और समाधान कर सकती है जब संचार उनके पोर्टल पर हुआ हो।"
        ),
        QuizQuestion(
            id = 45,
            topic = "Online Shopping",
            questionEn = "You receive a package you never ordered, and the courier demands Cash-on-Delivery payment. What should you do?",
            questionHi = "आपको एक ऐसा पैकेट प्राप्त होता है जिसका आपने कभी ऑर्डर नहीं दिया था, और कूरियर एजेंट नकद भुगतान (COD) की मांग करता है। आपको क्या करना चाहिए?",
            optionsEn = listOf(
                "Pay the amount immediately; it might be a surprise gift from a friend.",
                "Open the package first, then decide whether to pay.",
                "Refuse the delivery immediately. Do not pay any amount for unsolicited parcels.",
                "Keep the package and tell the courier you will pay tomorrow."
            ),
            optionsHi = listOf(
                "तुरंत राशि का भुगतान करें; यह किसी मित्र की ओर से एक सरप्राइज गिफ्ट हो सकता है।",
                "पहले पैकेट खोलें, फिर तय करें कि भुगतान करना है या नहीं।",
                "डिलीवरी को तुरंत अस्वीकार करें। अनचाहे पार्सल के लिए किसी भी राशि का भुगतान न करें।",
                "पैकेट रख लें और कूरियर से कहें कि आप कल भुगतान करेंगे।"
            ),
            correctAnswerIndex = 2,
            explanationEn = "This is 'Brush/Unordered COD Delivery' scam. Scammers send worthless items to collected addresses, trying to extract cash from unsuspecting household members.",
            explanationHi = "यह एक धोखाधड़ी है। घोटालेबाज अज्ञात पतों पर बेकार चीजें भेजते हैं और घर के अनजान सदस्यों से पैसे वसूलने की कोशिश करते हैं।"
        ),

        // === TOPIC 10: General Cyber Safety ===
        QuizQuestion(
            id = 46,
            topic = "General Cyber Safety",
            questionEn = "What is the security risk of connecting to a free, public Wi-Fi network at an airport or coffee shop?",
            questionHi = "हवाई अड्डे या कॉफी शॉप पर मुफ्त, सार्वजनिक वाई-फाई नेटवर्क से जुड़ने का सुरक्षा जोखिम क्या है?",
            optionsEn = listOf(
                "The connection speed will damage your mobile antenna.",
                "Traffic on unencrypted networks can be easily intercepted, harvested, or redirected by attackers on the same connection.",
                "The airport will charge you additional location tax.",
                "Your smartphone will require a physical scan."
            ),
            optionsHi = listOf(
                "कनेक्शन की गति आपके मोबाइल एंटीना को नुकसान पहुंचाएगी।",
                "असुरक्षित नेटवर्क पर आपके द्वारा भेजी जाने वाली जानकारी को उसी नेटवर्क पर मौजूद हमलावर आसानी से पढ़ या चुरा सकते हैं।",
                "हवाई अड्डा आपसे अतिरिक्त स्थानीय कर वसूल करेगा।",
                "आपके स्मार्टफोन को भौतिक स्कैन की आवश्यकता होगी।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Avoid open networks for critical actions. Use cellular data or encrypt your packets using a trusted Virtual Private Network (VPN) tool.",
            explanationHi = "महत्वपूर्ण कार्यों के लिए खुले वाई-फाई नेटवर्क से बचें। मोबाइल डेटा का उपयोग करें या वीपीएन (VPN) के माध्यम से अपनी जानकारी सुरक्षित रखें।"
        ),
        QuizQuestion(
            id = 47,
            topic = "General Cyber Safety",
            questionEn = "Why is it important to log out of public computers after checking your email?",
            questionHi = "सार्वजनिक कंप्यूटरों (जैसे साइबर कैफे में) पर अपना ईमेल चेक करने के बाद लॉग आउट करना क्यों महत्वपूर्ण है?",
            optionsEn = listOf(
                "To let the next person use the device without delays.",
                "Because browsers store active login cookies; if you stay logged in, subsequent users can access your entire personal inbox.",
                "To reduce power utility costs for the cyber cafe.",
                "To delete your browsing files automatically."
            ),
            optionsHi = listOf(
                "अगले व्यक्ति को बिना देरी के डिवाइस का उपयोग करने देने के लिए।",
                "क्योंकि ब्राउज़र सक्रिय लॉगिन कुकीज़ सहेजते हैं; यदि आप लॉग इन रहते हैं, तो बाद वाले उपयोगकर्ता आपके पूरे इनबॉक्स तक पहुँच सकते हैं।",
                "साइबर कैफे के लिए बिजली की लागत को कम करने के लिए।",
                "अपनी ब्राउज़िंग फ़ाइलों को स्वचालित रूप से हटाने के लिए।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Browsers cache session keys. Always use 'Incognito' mode on shared machines and explicitly hit 'Log Out' before leaving.",
            explanationHi = "ब्राउज़र सत्र कुकीज़ सहेजते हैं। साझा मशीनों पर हमेशा 'इन्कोग्निटो' (Incognito) मोड का उपयोग करें और जाने से पहले स्पष्ट रूप से 'लॉग आउट' दबाएं।"
        ),
        QuizQuestion(
            id = 48,
            topic = "General Cyber Safety",
            questionEn = "What does Multi-Factor Authentication (2FA) add to your standard login process?",
            questionHi = "मल्टी-फैक्टर ऑथेंटिकेशन (2FA) आपके सामान्य लॉगिन प्रक्रिया में क्या जोड़ता है?",
            optionsEn = listOf(
                "It requires typing your password twice.",
                "It adds a second verification barrier (like biometric check or app OTP) to block access even if your password is stolen.",
                "It automatically changes your profile pictures.",
                "It disables background location tracking."
            ),
            optionsHi = listOf(
                "इसके लिए आपका पासवर्ड दो बार टाइप करना पड़ता है।",
                "यह एक दूसरा सत्यापन द्वार (जैसे बायोमेट्रिक या ऐप ओटीपी) जोड़ता है ताकि पासवर्ड चोरी होने पर भी लॉगिन रोका जा सके।",
                "यह स्वचालित रूप से आपकी प्रोफ़ाइल फ़ोटो बदल देता है।",
                "यह पृष्ठभूमि स्थान ट्रैकिंग को अक्षम करता है।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "2FA is one of the strongest protective rules. It means compromise of the password is not sufficient for attackers to compromise the account.",
            explanationHi = "2FA सुरक्षा के सबसे मजबूत नियमों में से एक है। इसका मतलब है कि केवल पासवर्ड जानना ही हमलावरों के लिए आपका खाता हैक करने के लिए पर्याप्त नहीं होगा।"
        ),
        QuizQuestion(
            id = 49,
            topic = "General Cyber Safety",
            questionEn = "If your social media account is hacked, what is the first action you should perform?",
            questionHi = "यदि आपका सोशल मीडिया खाता हैक हो जाता है, तो आपको सबसे पहले क्या कार्रवाई करनी चाहिए?",
            optionsEn = listOf(
                "Delete your account from the application settings.",
                "Contact official platform support, change passwords on linked recovery email accounts, and notify your friends of the compromise.",
                "Call the cyber police line immediately to arrest the hacker.",
                "Post a public complaint on public message boards."
            ),
            optionsHi = listOf(
                "एप्लिकेशन सेटिंग्स से अपना खाता हटा दें।",
                "आधिकारिक प्लेटफ़ॉर्म सहायता टीम से संपर्क करें, लिंक किए गए रिकवरी ईमेल का पासवर्ड बदलें और अपने दोस्तों को सूचित करें।",
                "हैकर को गिरफ्तार करने के लिए तुरंत साइबर पुलिस हेल्पलाइन पर कॉल करें।",
                "सार्वजनिक संदेश बोर्डों पर एक सार्वजनिक शिकायत पोस्ट करें।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Recover the account using registered backup options. Immediately warn contacts to prevent them from falling for emergency scams sent from your profile.",
            explanationHi = "पंजीकृत बैकअप विकल्पों का उपयोग करके खाता पुनर्प्राप्त करें। संपर्कों को तुरंत सचेत करें ताकि वे आपके प्रोफाइल से भेजे गए आपातकालीन घोटालों का शिकार न बनें।"
        ),
        QuizQuestion(
            id = 50,
            topic = "General Cyber Safety",
            questionEn = "Why should you disable Bluetooth and NFC on your smartphone when not actively using them?",
            questionHi = "जब आप सक्रिय रूप से उपयोग नहीं कर रहे हों तो आपको अपने स्मार्टफोन पर ब्लूटूथ और एनएफसी (NFC) को क्यों बंद कर देना चाहिए?",
            optionsEn = listOf(
                "To prevent the phone from executing automatic system updates.",
                "To block unauthorized nearby wireless connection attempts, data sniffing, and accidental contactless payment debits.",
                "To avoid cellular network billing charges.",
                "To keep your smartphone screen safe."
            ),
            optionsHi = listOf(
                "फोन को स्वचालित सिस्टम अपडेट करने से रोकने के लिए।",
                "आस-पास के अवांछित वायरलेस कनेक्शन प्रयासों, डेटा चोरी और दुर्घटनावश होने वाले संपर्क रहित भुगतान कटौतियों को रोकने के लिए।",
                "सेलुलर नेटवर्क बिलिंग शुल्क से बचने के लिए।",
                "अपने स्मार्टफोन की स्क्रीन को सुरक्षित रखने के लिए।"
            ),
            correctAnswerIndex = 1,
            explanationEn = "Unused wireless protocols present a local attack vector. Keeping Bluetooth and NFC off shuts down close-range penetration and payment skimming risks.",
            explanationHi = "अनुपयोगी वायरलेस प्रोटोकॉल स्थानीय सुरक्षा खतरे पैदा कर सकते हैं। ब्लूटूथ और एनएफसी को बंद रखने से पास की रेंज वाले हमलों और भुगतान चोरी का खतरा खत्म हो जाता है।"
        )
    )
}
