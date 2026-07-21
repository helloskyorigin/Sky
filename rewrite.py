import re
import os

with open('app/src/main/java/com/skyorigin/threatshieldai/ScamExamplesData.kt', 'r') as f:
    text = f.read()

# Define the Hinglish translations for all 15 scams + the else block
replacements = [
    (
        "titleHi = \"नेटफ्लिक्स अकाउंट सस्पेंशन अलर्ट\"",
        "titleHi = \"Netflix Account Suspension Alert\""
    ),
    (
        "messageHi = \"प्रिय सदस्य, आपका सब्सक्रिप्शन भुगतान विफल हो गया। यदि आप अपनी बिलिंग जानकारी अपडेट नहीं करते हैं तो हम 24 घंटों के भीतर आपकी सदस्यता निलंबित कर देंगे। रीएक्टिवेट करने के लिए यहाँ क्लिक करें: http://netflix-billing-update.com\"",
        "messageHi = \"Dear Member, aapka subscription Payment fail ho gaya hai. Agar aap apni billing details update nahi karte, toh 24 ghante ke andar aapki membership suspend kar di jayegi. Reactivate karne ke liye yahan click karein: http://netflix-billing-update.com\""
    ),
    (
        "dangerHi = \"नकली वेबसाइटें आपके क्रेडिट कार्ड विवरण, सीवीवी और लॉगिन पासवर्ड चुरा लेती हैं, जिससे अनधिकृत लेनदेन होने लगते हैं।\"",
        "dangerHi = \"Fake websites aapke Credit Card details, CVV aur login password chura leti hain, jisse unauthorized charges hone lagte hain.\""
    ),
    (
        "redFlagsHi = listOf(\"24 घंटे के निलंबन की तत्काल धमकी\", \"netflix.com के बजाय गैर-सुरक्षित डोमेन netflix-billing-update.com\", \"सामान्य अभिवादन 'प्रिय सदस्य'\")",
        "redFlagsHi = listOf(\"24 ghante mein suspension ki urgent warning\", \"netflix.com ke bajaye non-secure Fake Link netflix-billing-update.com\", \"Generic greeting 'Dear Member'\")"
    ),
    (
        "safeResponseHi = \"एसएमएस या ईमेल अलर्ट में कभी भी लिंक पर क्लिक न करें। अपनी बिलिंग स्थिति की जांच करने के लिए आधिकारिक नेटफ्लिक्स ऐप या वेबसाइट अलग से खोलें।\"",
        "safeResponseHi = \"SMS ya email Alert mein diye Fake Link par kabhi click na karein. Apni billing status check karne ke liye Official Website ya Netflix app alag se open karein.\""
    ),

    (
        "titleHi = \"आधार-लिंक्ड बैंक एक्टिवेशन ओटीपी\"",
        "titleHi = \"Aadhaar-Linked Bank Activation OTP\""
    ),
    (
        "messageHi = \"एक कॉलर जो सरकारी अधिकारी होने का ढोंग करता है, दावा करता है कि आपके आधार कार्ड को सत्यापन की आवश्यकता है। 'मैंने सत्यापित करने के लिए एक एसएमएस कोड भेजा है। कृपया मुझे तुरंत ओटीपी बताएं वरना आपका खाता लॉक कर दिया जाएगा।'\"",
        "messageHi = \"Ek caller government official bankar claim karta hai ki aapke Aadhaar ki verification baaki hai. 'Maine Verify karne ke liye SMS code bheja hai. Turant OTP batayein warna aapka account lock ho jayega.'\""
    ),
    (
        "dangerHi = \"ओटीपी स्कैमर को अपना नंबर लिंक करने या आपके बैंक से सीधे पैसे निकालने का पूरा अधिकार देता है।\"",
        "dangerHi = \"OTP share karte hi Scammer ko aapke Bank account se paise nikalne ya apna number link karne ka pura access mil jata hai.\""
    ),
    (
        "redFlagsHi = listOf(\"कॉलर सत्यापन ओटीपी की मांग कर रहा है\", \"तत्काल खाता लॉक होने का खतरा\", \"बिना सोचे-समझे तुरंत कार्रवाई करने का दबाव\")",
        "redFlagsHi = listOf(\"Caller ka verification ke liye OTP maangna\", \"Account turant lock hone ki Warning\", \"Bina soche jaldi Safe Action lene ka pressure\")"
    ),
    (
        "safeResponseHi = \"फोन काट दें। कोई भी प्राधिकरण या बैंक कभी भी फोन कॉल पर लेनदेन या सत्यापन ओटीपी का अनुरोध नहीं करता है।\"",
        "safeResponseHi = \"Call cut karein. Koi bhi Bank ya official authority kabhi bhi phone call par verification OTP nahi maangti.\""
    ),

    (
        "titleHi = \"OLX खरीदार डबल पेमेंट यूपीआई अनुरोध\"",
        "titleHi = \"OLX Buyer Double Payment UPI Request\""
    ),
    (
        "messageHi = \"OLX पर एक खरीदार का दावा है कि उसने गलती से आपको डबल भुगतान भेज दिया है। वे एक यूपीआई अनुरोध (Request) भेजते हैं जिसमें आपसे 'स्वीकार' करने और अतिरिक्त पैसे वापस करने के लिए अपना यूपीआई पिन टाइप करने के लिए कहा जाता है।\"",
        "messageHi = \"OLX par ek buyer claim karta hai ki usne galti se aapko double Payment bhej di hai. Woh ek UPI Request bhej kar aapse 'Approve' karne aur extra paise wapas karne ke liye UPI PIN type karne ko kehta hai.\""
    ),
    (
        "dangerHi = \"अपने पिन के साथ यूपीआई कलेक्ट अनुरोध को स्वीकार करने से आपके खाते से तुरंत पैसे कट जाते हैं, न कि प्राप्त होते हैं।\"",
        "dangerHi = \"UPI collect request ko apna PIN daal kar approve karne se aapke Bank account se paise turant kat jaate hain.\""
    ),
    (
        "redFlagsHi = listOf(\"पैसे प्राप्त करने के लिए यूपीआई पिन दर्ज करने का अनुरोध\", \"क्रेडिट के बजाय 'कलेक्ट' के रूप में चिह्नित यूपीआई अधिसूचना\", \"खरीदार की ओर से जल्दबाजी\")",
        "redFlagsHi = listOf(\"Paise receive karne ke liye UPI PIN daalne ka request\", \"Credit ke bajaye 'Collect' tag wali UPI notification\", \"Buyer ki taraf se bohot jaldbazi dikhana\")"
    ),
    (
        "safeResponseHi = \"अनुरोध को तुरंत अस्वीकार करें। याद रखें, यूपीआई के माध्यम से पैसे प्राप्त करने के लिए कभी भी अपना पिन दर्ज करने की आवश्यकता नहीं होती है।\"",
        "safeResponseHi = \"Is request ko turant decline karein. Yaad rakhein, UPI se paise receive karne ke liye kabhi bhi apna PIN daalne ki zaroorat nahi hoti.\""
    ),

    (
        "titleHi = \"लकी ड्रा इनाम स्क्रैच कार्ड क्यूआर\"",
        "titleHi = \"Lucky Draw Reward Scratch Card QR\""
    ),
    (
        "messageHi = \"आपको एक भौतिक पर्चा या व्हाट्सएप संदेश प्राप्त होता है जिसमें एक क्यूआर कोड होता है और दावा किया जाता है 'स्क्रैच करने के लिए इस कोड को स्कैन करें और सीधे अपने खाते में 2,000 रुपये का नकद पुरस्कार प्राप्त करें।'\"",
        "messageHi = \"Aapko WhatsApp par ya physical pamphlet mein ek QR Code milta hai jo claim karta hai 'Scratch karne aur 2,000 Rs ka cash prize apne account mein receive karne ke liye yeh code scan karein.'\""
    ),
    (
        "dangerHi = \"इस कोड को स्कैन करने से आप एक नकली भुगतान पृष्ठ पर पहुंच जाते हैं, जिसे पिन के साथ अधिकृत होने पर आपके खाते से पैसे काटने के लिए डिज़ाइन किया गया है।\"",
        "dangerHi = \"QR Code scan karte hi aapko ek Fake Payment page par bheja jata hai jahan PIN daalte hi aapke account se paise kat jaate hain.\""
    ),
    (
        "redFlagsHi = listOf(\"नकद पुरस्कार प्राप्त करने के लिए क्यूआर कोड स्कैन करना\", \"अवांछित पुरस्कार घोषणाएं\", \"भुगतान पिन या क्रेडिट विवरण मांगने वाला वेबपेज\")",
        "redFlagsHi = listOf(\"Cash prize claim karne ke liye QR Code scan karne ko kehna\", \"Unsolicited reward announcements\", \"Payment PIN ya credit details maangne wala webpage\")"
    ),
    (
        "safeResponseHi = \"पैसे प्राप्त करने के लिए कभी भी क्यूआर कोड स्कैन न करें। क्यूआर कोड विशेष रूप से भुगतान करने के लिए होते हैं।\"",
        "safeResponseHi = \"Paise receive karne ke liye kabhi bhi QR Code scan na karein. QR Code sirf Payment karne ke liye hote hain.\""
    ),

    (
        "titleHi = \"क्रेडिट कार्ड लिमिट बढ़ाने का जाल\"",
        "titleHi = \"Credit Card Limit Increase Trap\""
    ),
    (
        "messageHi = \"आपके क्रेडिट कार्ड प्रदाता का प्रतिनिधित्व करने का दावा करने वाला एक कॉलर बिना दस्तावेजों के आपकी सीमा को तुरंत दोगुना करने की पेशकश करता है। वे आपके कार्ड नंबर, सीवीवी और ओटीपी का अनुरोध करते हैं।\"",
        "messageHi = \"Aapke Bank ka representative bankar ek caller bina documents ke aapki credit limit double karne ka offer deta hai. Woh aapke card details, CVV, aur OTP maangta hai.\""
    ),
    (
        "dangerHi = \"इन विवरणों के साथ, हमलावर ऑनलाइन खरीदारी करते हैं और आपके क्रेडिट बैलेंस को साफ कर देते हैं।\"",
        "dangerHi = \"In details ki madad se Scammer online shopping karke aapka poora credit balance empty kar dete hain.\""
    ),
    (
        "redFlagsHi = listOf(\"सीवीवी या समाप्ति तिथि के लिए मौखिक अनुरोध\", \"बिना दस्तावेज के तुरंत लिमिट बढ़ाने का वादा\", \"सामान्य 10-अंकीय मोबाइल नंबर से आने वाली कॉल\")",
        "redFlagsHi = listOf(\"Call par CVV ya expiry date maangna\", \"Bina documents ke instant zero-document upgrades ka wada\", \"Regular 10-digit mobile number se Bank call aana\")"
    ),
    (
        "safeResponseHi = \"कॉल काट दें और कहें कि आप सीधे अपने बैंकिंग ऐप या स्थानीय शाखा के अंदर सीमा में सुधार का प्रबंधन करेंगे।\"",
        "safeResponseHi = \"Call cut karein aur bole ki aap apni credit limit directly banking app ya official Bank branch se badha lenge.\""
    ),

    (
        "titleHi = \"सिम कार्ड डीएक्टिवेशन खतरा केवाईसी\"",
        "titleHi = \"SIM Card Deactivation Threat KYC\""
    ),
    (
        "messageHi = \"एक एसएमएस में लिखा है: 'प्रिय ग्राहक, आपका एयरटेल सिम केवाईसी समाप्त हो गया है। आपकी आउटगोइंग सेवाएं आज निलंबित कर दी जाएंगी। कृपया तुरंत +91-XXXXXXXXXX पर कॉल करके सत्यापन पूरा करें।'\"",
        "messageHi = \"Aapko SMS milta hai: 'Dear Customer, aapka Airtel SIM KYC expire ho gaya hai. Aapki outgoing services aaj suspend ho jayengi. Verify karne ke liye turant +91-XXXXXXXXXX par call karein.'\""
    ),
    (
        "dangerHi = \"जालसाज आपकी बैंकिंग लॉगिन सेशन को देखने और चुराने के लिए रिमोट डेस्कटॉप स्क्रीन शेयरिंग टूल (जैसे AnyDesk) इंस्टॉल करने के लिए आपको गुमराह करते हैं।\"",
        "dangerHi = \"Scammers aapko screen sharing tools (jaise AnyDesk) install karne ko kehte hain taaki woh aapki banking session dekh kar paise chura sakein.\""
    ),
    (
        "redFlagsHi = listOf(\"कुछ ही घंटों के भीतर सिम ब्लॉक करने की तत्काल धमकी\", \"सपोर्ट के लिए व्यक्तिगत 10-अंकीय फोन नंबर पर कॉल करने का अनुरोध\", \"भेजने वाले का एसएमएस हेडर एक व्यक्तिगत मोबाइल नंबर है, न कि कोई ब्रांड कोड\")",
        "redFlagsHi = listOf(\"Kuch ghanton mein SIM block hone ki urgent Warning\", \"Support ke liye personal 10-digit number par call karne ka request\", \"SMS kisi personal number se aana, official brand code se nahi\")"
    ),
    (
        "safeResponseHi = \"अधिकृत ऑपरेटर आउटलेट पर जाकर या उनके आधिकारिक टेलीकॉम ऐप के माध्यम से सीधे केवाईसी सत्यापित करें।\"",
        "safeResponseHi = \"Apna KYC sirf official telecom app ya authorized operator outlet par jaakar Verify karein.\""
    ),

    (
        "titleHi = \"डाकघर पता सुधार घोटाला\"",
        "titleHi = \"Post Office Address Correction Scam\""
    ),
    (
        "messageHi = \"एक एसएमएस जिसमें दावा किया गया है कि अधूरे पते के कारण आपका इंडिया पोस्ट पैकेज डिलीवर नहीं किया जा सका। 'पता अपडेट करें और यहां 5 रुपये शुल्क देकर डिलीवरी शेड्यूल करें: http://indiapost-delivery-tracking.info'\"",
        "messageHi = \"Ek SMS claim karta hai ki address incomplete hone ki wajah se aapka India Post package deliver nahi ho saka. 'Address update karein aur 5 Rs ki fee dekar delivery schedule karein: http://indiapost-delivery-tracking.info'\""
    ),
    (
        "dangerHi = \"जब आप मामूली 5 रुपये का शुल्क देने का प्रयास करते हैं, तो साइट आपके कार्ड विवरण कैप्चर कर लेती है और इसके बजाय आपके खाते से हजारों रुपये निकाल लेती है।\"",
        "dangerHi = \"Jab aap sirf 5 Rs ka Payment karne ki koshish karte hain, toh Fake Link aapke card details capture karke aapke account se hazaro rupaye kaat leta hai.\""
    ),
    (
        "redFlagsHi = listOf(\"अप्रत्याशित पार्सल डिलीवरी अलर्ट\", \"पार्सल जारी करने के लिए मामूली शुल्क मांगने वाला संदेश\", \"आधिकारिक .gov.in के बजाय अनौपचारिक ट्रैकिंग यूआरएल\")",
        "redFlagsHi = listOf(\"Unexpected parcel delivery Alert\", \"Package release karne ke liye choti si Payment maangna\", \"Official .gov.in ke bajaye unofficial Fake Link URL\")"
    ),
    (
        "safeResponseHi = \"अज्ञात पार्सल पर कभी प्रतिक्रिया न दें। डाक सेवा प्रदाता पार्सल जारी करने के लिए ऑनलाइन लिंक के माध्यम से मामूली शुल्क नहीं मांगते हैं।\"",
        "safeResponseHi = \"Unknown parcels par kabhi response na karein. Official delivery services package release karne ke liye SMS Link se payment nahi maangti.\""
    ),

    (
        "titleHi = \"वर्क फ्रॉम होम यूट्यूब वीडियो लाइक घोटाला\"",
        "titleHi = \"Work From Home YouTube Video Like Scam\""
    ),
    (
        "messageHi = \"एक व्हाट्सएप भर्तीकर्ता आपको एक आकर्षक नौकरी का प्रस्ताव देता है: 'प्रति दिन 2,000 रुपये कमाएं, बस हमारे यूट्यूब वीडियो को लाइक करें और स्क्रीनशॉट भेजें।'\"",
        "messageHi = \"Ek WhatsApp recruiter aapko ek attractive Job Scam offer bhejta hai: 'Rozana 2,000 Rs kamayein, bas hamare YouTube videos ko like karein aur screenshot bhejein.'\""
    ),
    (
        "dangerHi = \"आपको शुरुआत में कुछ पैसे दिए जाते हैं ताकि विश्वास बन सके। फिर वे आपको बड़े रिटर्न का लालच देकर उनके 'प्रीमियम टास्क' में भारी निवेश करने के लिए मजबूर करते हैं, और अंततः आपके पैसे लेकर गायब हो जाते हैं।\"",
        "dangerHi = \"Shuru mein aapko thode paise dekar trust banaya jata hai. Phir woh aapko bade returns ka lalach dekar 'premium tasks' mein paise invest karne ko bolte hain, aur paise lekar gayab ho jaate hain.\""
    ),
    (
        "redFlagsHi = listOf(\"लाइक और सब्सक्राइब करने जैसे बुनियादी कार्यों के लिए अत्यधिक वेतन\", \"टेलीग्राम समूहों के माध्यम से काम का संचालन\", \"उच्च कार्यों को अनलॉक करने के लिए सुरक्षा जमा की आवश्यकता\")",
        "redFlagsHi = listOf(\"Basic tasks (like aur subscribe) ke liye abnormally high salary\", \"Telegram groups ke through work operate karna\", \"High-level tasks unlock karne ke liye Security deposit ki Warning\")"
    ),
    (
        "safeResponseHi = \"प्रस्ताव को तुरंत ब्लॉक करें। याद रखें, कोई भी वास्तविक कंपनी वीडियो लाइक करने के लिए भुगतान नहीं करती है।\"",
        "safeResponseHi = \"Offer ko turant block karein. Yaad rakhein, koi bhi asli company videos like karne ke paise nahi deti.\""
    ),

    (
        "titleHi = \"टेलीग्राम स्टॉक ट्रेडिंग टिप्स घोटाला\"",
        "titleHi = \"Telegram Stock Trading Tips Scam\""
    ),
    (
        "messageHi = \"एक टेलीग्राम चैनल 'इंडिया बुल्स वीआईपी टिप्स' विज्ञापन देता है: 'पंजीकरण करने के लिए 999 रुपये का भुगतान करें, और स्टॉक ट्रेडिंग के लिए रोजाना गारंटीड संकेत प्राप्त करें जो 400% लाभ की गारंटी देते हैं।'\"",
        "messageHi = \"Ek Telegram channel 'India Bulls VIP Tips' promote karta hai: 'Register karne ke liye 999 Rs Payment karein, aur rozana stock trading ki guaranteed tips paayein jisme 400% profit ka wada ho.'\""
    ),
    (
        "dangerHi = \"चैनल नकली रसीदें प्रकाशित करता है, और पंजीकरण या प्रारंभिक निवेश का भुगतान करने के बाद, वे आपके प्रोफाइल को स्थायी रूप से ब्लॉक कर देते हैं।\"",
        "dangerHi = \"Channel nakli receipts dikhata hai, aur registration ya initial investment Payment karne ke baad woh aapko block kar dete hain.\""
    ),
    (
        "redFlagsHi = listOf(\"कम जोखिम वाले दैनिक ट्रेडों से भारी गारंटीड मुनाफे का आश्वासन\", \"चैनल एडमिन द्वारा कोई कानूनी वित्तीय सलाहकार क्रेडेंशियल प्रस्तुत न करना\", \"निजी यूपीआई हैंडल के माध्यम से भुगतान\")",
        "redFlagsHi = listOf(\"Low-risk daily trades se massive guaranteed profits ka lalach\", \"Channel admin ke paas koi legal financial advisor credentials na hona\", \"Private UPI handle ke through Payment maangna\")"
    ),
    (
        "safeResponseHi = \"चैनल को रिपोर्ट और ब्लॉक करें। वास्तविक वित्तीय सलाहकारों को सेबी (SEBI) पंजीकरण कोड प्रदर्शित करना आवश्यक है।\"",
        "safeResponseHi = \"Channel ko Report aur block karein. Asli financial advisors ke paas hamesha official SEBI registration code hota hai.\""
    ),

    (
        "titleHi = \"केबीसी करोड़पति लकी ड्रा विजेता\"",
        "titleHi = \"KBC Crorepati Lucky Draw Winner\""
    ),
    (
        "messageHi = \"एक व्हाट्सएप वॉयस नोट के साथ एक पोस्टर जिसमें दिखाया गया है कि आपके नंबर ने 25 लाख रुपये की केबीसी लॉटरी जीती है। 'दावा करने के लिए, हमारे मैनेजर को 15,000 रुपये बैंक पंजीकरण शुल्क ट्रांसफर करें।'\"",
        "messageHi = \"Aapko WhatsApp par ek voice note aur poster milta hai jisme claim hota hai ki aapke number ne 25 Lakh ki KBC Lottery jeeti hai. 'Claim karne ke liye 15,000 Rs Bank registration fee transfer karein.'\""
    ),
    (
        "dangerHi = \"एक बार शुल्क स्थानांतरित हो जाने के बाद, वे सीमा शुल्क निकासी के लिए अतिरिक्त शुल्क का अनुरोध करते हैं और फिर संपर्क बंद कर देते हैं।\"",
        "dangerHi = \"Ek baar fee transfer karne ke baad, Scamsters custom clearing ke naam par aur extra paise maangte hain aur phir block kar dete hain.\""
    ),
    (
        "redFlagsHi = listOf(\"लॉटरी जीतना जिसके लिए आपने कभी टिकट नहीं खरीदा\", \"पैसे वितरण से पहले अग्रिम प्रसंस्करण शुल्क की मांग\", \"अत्यंत अव्यवसायिक भाषा में रिकॉर्ड किए गए वॉयस नोट\")",
        "redFlagsHi = listOf(\"Bina ticket kharide Lottery jeetne ka Fake Alert\", \"Paise bhejne se pehle advance processing fees maangna\", \"Highly unprofessional language mein record kiye gaye voice notes\")"
    ),
    (
        "safeResponseHi = \"अनदेखा करें और हटा दें। कोई भी वास्तविक लॉटरी कार्यक्रम विजेताओं से अग्रिम रूप से टैक्स या प्रोसेसिंग शुल्क का भुगतान करने का अनुरोध नहीं करता है।\"",
        "safeResponseHi = \"Ignore karke delete karein. Koi bhi real Lottery Scam winner se advance tax ya processing fee nahi maangti.\""
    ),

    (
        "titleHi = \"माइक्रोसॉफ्ट विंडोज वायरस फ़ायरवॉल अलर्ट\"",
        "titleHi = \"Microsoft Windows Virus Firewall Alert\""
    ),
    (
        "messageHi = \"एक फुल-स्क्रीन ब्राउज़र पॉप-अप तेज़ ऑडियो के साथ आपके पीसी को फ्रीज कर देता है और दावा करता है: 'विंडोज से समझौता किया गया है। क्रेडिट विवरण लीक हो रहे हैं। माइक्रोसॉफ्ट सपोर्ट को तुरंत कॉल करें: 1800-XXX-XXXX।'\"",
        "messageHi = \"Ek full-screen browser pop-up loud audio ke saath aapke PC ko freeze kar deta hai aur claim karta hai: 'Windows Virus Alert. Credit details leak ho rahe hain. Microsoft Support ko turant call karein: 1800-XXX-XXXX.'\""
    ),
    (
        "dangerHi = \"स्कैमर बुनियादी डिस्क क्लीनर टूल चलाने या दुर्भावनापूर्ण ट्रैकिंग ट्रोजन इंस्टॉल करने के लिए हजारों रुपये वसूलते हैं।\"",
        "dangerHi = \"Tech Support Scammers basic disk cleaner tool chalane ya Malware install karne ke hazaro rupaye charge karte hain.\""
    ),
    (
        "redFlagsHi = listOf(\"तेज़ अलार्म या पॉप-अप जो आपके ब्राउज़र टैब को लॉक कर देते हैं\", \"आपातकालीन सहायता हॉटलाइन पर तुरंत कॉल करने की मांग\", \"मरम्मत के भुगतान के लिए गिफ्ट कार्ड खरीदने का अनुरोध\")",
        "redFlagsHi = listOf(\"Loud alarm aur pop-ups jo aapke browser tab ko lock kar de\", \"Emergency support helpline par turant call karne ka Fake Alert\", \"Repair cover karne ke liye gift cards kharidne ki maang\")"
    ),
    (
        "safeResponseHi = \"टास्क मैनेजर का उपयोग करके अपना वेब ब्राउज़र बंद करें। प्रतिष्ठित स्थानीय एंटी-वायरस स्कैनर इंस्टॉल करें।\"",
        "safeResponseHi = \"Task Manager use karke apna browser band karein. Apne PC mein trusted Anti-Virus scanner install karein.\""
    ),

    (
        "titleHi = \"आपातकालीन स्थिति में मित्र द्वारा पैसे का अनुरोध\"",
        "titleHi = \"Friend in Emergency Money Request\""
    ),
    (
        "messageHi = \"आपको अपने करीबी दोस्त की तस्वीर के साथ एक अज्ञात नंबर से संदेश प्राप्त होता है: 'मैंने अपना नंबर बदल लिया है। मैं आपातकालीन स्थिति में अस्पताल में हूं, कृपया इस डॉक्टर के नंबर पर तुरंत 10,000 रुपये भेजें। मैं इसे आज रात वापस कर दूंगा।'\"",
        "messageHi = \"Aapko apne close friend ki profile picture ke saath ek unknown number se message aata hai: 'Maine apna number change kar liya hai. Main emergency mein hospital mein hoon, please is doctor ke number par turant 10,000 Rs GPay kar de. Main raat ko wapas kar dunga.'\""
    ),
    (
        "dangerHi = \"आप हमलावर के बर्नर वॉलेट में पैसे ट्रांसफर करते हैं, जिसे तुरंत निकाल लिया जाता है और उसका पता नहीं लगाया जा सकता है।\"",
        "dangerHi = \"Aap Scammer ke burner wallet mein paise transfer kar dete hain, jise turant nikal liya jata hai aur track nahi kiya ja sakta.\""
    ),
    (
        "redFlagsHi = listOf(\"नए बदले गए नंबर से पैसे के लिए तत्काल अनुरोध\", \"चिकित्सा संकटों के बारे में भावनात्मक पैनिक तरकीबें\", \"नियमित फोन कॉल लेने में आनाकानी करना\")",
        "redFlagsHi = listOf(\"Naye number se paise ka urgent request aana\", \"Medical emergency ka hawala dekar emotional panic create karna\", \"Direct phone call lene se bachna ya excuse dena\")"
    ),
    (
        "safeResponseHi = \"लेन-देन करने से पहले पुष्टि करने के लिए अपने मित्र को उनके मूल, सत्यापित टेलीफोन नंबर पर कॉल करें।\"",
        "safeResponseHi = \"Koi bhi Payment karne se pehle Verify karne ke liye apne dost ke original number par call karein.\""
    ),

    (
        "titleHi = \"अत्यधिक रिटर्न टेलीग्राम क्रिप्टो निवेश\"",
        "titleHi = \"High-Return Telegram Crypto Investment\""
    ),
    (
        "messageHi = \"आपको एक टेलीग्राम समूह में जोड़ा जाता है जहां एक 'क्रिप्टो विशेषज्ञ' चार्ट साझा करता है और वादे करता है: 'विदेशी क्लाउड माइनिंग फार्म में 5,000 रुपये का निवेश करें और 48 घंटों में 25,000 रुपये प्राप्त करें।'\"",
        "messageHi = \"Aapko ek Telegram group mein add kiya jata hai jahan ek 'Crypto Expert' charts share karke promise karta hai: 'Foreign cloud mining mein 5,000 Rs invest karein aur 48 hours mein 25,000 Rs payein.'\""
    ),
    (
        "dangerHi = \"निवेश मंच एक छलावा है। जब आप अपनी कथित 'कमाई' निकालने का प्रयास करते हैं, तो वे आपकी निकासी को जारी करने के लिए भारी निकासी करों की मांग करते हैं।\"",
        "dangerHi = \"Investment platform ek Fake app hota hai. Jab aap apni dikhayi hui 'earnings' nikalne ki koshish karte hain, toh Scamsters withdrawal tax ke naam par aur paise maangte hain.\""
    ),
    (
        "redFlagsHi = listOf(\"बिना जोखिम के बहुत ही कम समय में भारी रिटर्न की गारंटी\", \"अज्ञात विदेशी प्लेटफार्मों के माध्यम से निवेश\", \"टेलीग्राम समूहों में अजनबियों को जोड़ना\")",
        "redFlagsHi = listOf(\"Bina risk ke shor-term mein huge guaranteed returns ka wada\", \"Unknown foreign Crypto platforms par invest karne ka bolna\", \"Bina pooche random Telegram groups mein add karna\")"
    ),
    (
        "safeResponseHi = \"ऐसे सभी अवांछित प्रस्तावों को अनदेखा करें और प्रतिष्ठित, विनियमित भारतीय संस्थानों के माध्यम से निवेश करें।\"",
        "safeResponseHi = \"Aise sabhi Fake offers ko block karein aur sirf verified aur regulated Indian Investment apps ka use karein.\""
    ),

    (
        "titleHi = \"इंस्टाग्राम ब्रांड एंबेसडर प्रायोजन\"",
        "titleHi = \"Instagram Brand Ambassador Sponsorship\""
    ),
    (
        "messageHi = \"एक फैशन ब्रांड खाता आपको डीएम करता है: 'हमें आपकी प्रोफाइल स्टाइल बहुत पसंद है! हम आपको 3 मुफ्त उत्पादों के साथ प्रायोजित करना चाहते हैं। बस इस ट्रैकिंग पंजीकरण लिंक पर क्लिक करें और शिपिंग का भुगतान करें।'\"",
        "messageHi = \"Ek fashion brand account aapko DM karta hai: 'Humein aapki profile bohot pasand aayi! Hum aapko 3 free products sponsor karna chahte hain. Bas is registration Link par click karein aur shipping Payment karein.'\""
    ),
    (
        "dangerHi = \"शिपिंग पेज एक फ़िशिंग जाल है जो आपके ऑनलाइन क्रेडेंशियल या डेबिट कार्ड को चुरा लेता है, जिससे आपका खाता खाली हो जाता है।\"",
        "dangerHi = \"Shipping page ek Phishing trap hota hai jo aapke online credentials ya debit card details chura leta hai, aur aapka Bank account khali kar deta hai.\""
    ),
    (
        "redFlagsHi = listOf(\"प्रभावशाली सहयोग के अवांछित प्रस्ताव\", \"सरल शिपिंग भुगतान के लिए आपको बाहरी गैर-सुरक्षित चेकआउट साइटों पर निर्देशित करना\", \"ब्रांड डीएम में खराब व्याकरण या स्वचालित अनुवाद\")",
        "redFlagsHi = listOf(\"Unsolicited influencer collaboration ke offers aana\", \"Shipping payment ke liye kisi third-party non-secure Fake Link par bhejna\", \"Brand DMs mein poor grammar ya copy-paste messages hona\")"
    ),
    (
        "safeResponseHi = \"ब्रांड की सत्यापित वेबसाइट की स्वतंत्र रूप से समीक्षा करें। यादृच्छिक डीएम के माध्यम से भेजे गए प्रायोजन पंजीकरण लिंक पर कभी क्लिक न करें।\"",
        "safeResponseHi = \"Brand ki Official Website ko independently Verify karein. Kisi random DM se aaye hue Fake Link par kabhi click na karein.\""
    ),

    (
        "titleHi = \"फर्जी \\$brand ऑनलाइन हेल्पलाइन इंटरसेप्शन\"",
        "titleHi = \"Fake \\$brand Online Helpline Interception\""
    ),
    (
        "messageHi = \"क्या आपको अपनी \\$brand सेवा के साथ तकनीकी समस्याओं का सामना करना पड़ा? त्वरित सहायता के लिए इस मोबाइल नंबर पर हमारे लाइव चैट कर्मचारी से संपर्क करें: +91-XXXXXXXXXX।\"",
        "messageHi = \"Kya aapko \\$brand service ke saath technical problem aa rahi hai? Quick help ke liye is mobile number par hamare support executive se contact karein: +91-XXXXXXXXXX.\""
    ),
    (
        "dangerHi = \"नकली सहायता प्रतिनिधि आपको उन प्रक्रियाओं को करने के लिए मार्गदर्शन करते हैं जो आपके खाता पासवर्ड या बैंक बैलेंस को खतरे में डालती हैं।\"",
        "dangerHi = \"Fake customer care aapko aisi process follow karne ko bolta hai jisse aapka password ya Bank balance Scam ho jata hai.\""
    ),
    (
        "redFlagsHi = listOf(\"व्यक्तिगत मोबाइल नंबरों का उपयोग करने वाली हेल्पलाइन संपर्क जानकारी\", \"प्राधिकरण कोड या क्रेडिट डेटा साझा करने के निर्देश\", \"आधिकारिक प्लेटफॉर्म टिकटिंग सिस्टम का अभाव\")",
        "redFlagsHi = listOf(\"Helpline ke naam par personal mobile number ka use karna\", \"OTP, UPI PIN ya Bank details share karne ko kehna\", \"Official app mein ticket ya support history na hona\")"
    ),
    (
        "safeResponseHi = \"हमेशा उनके आधिकारिक इन-ऐप फीडबैक चैनलों या सुरक्षित संपर्क पोर्टलों के माध्यम से \\$brand के साथ संवाद करें।\"",
        "safeResponseHi = \"Hamesha \\$brand ke official in-app support ya verified Official Website portals ke through hi help maangein.\""
    )
]

for old, new in replacements:
    if old in text:
        text = text.replace(old, new)
    else:
        print(f"Warning: Could not find '{old}' in text")

with open('app/src/main/java/com/skyorigin/threatshieldai/ScamExamplesData.kt', 'w') as f:
    f.write(text)

