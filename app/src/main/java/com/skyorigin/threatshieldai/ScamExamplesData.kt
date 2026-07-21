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
                titleHi = "Netflix Account Suspension Alert",
                messageEn = "Dear Member, Your subscription payment failed. We will suspend your membership within 24 hours if you do not update your billing info. Click here to reactivate: http://netflix-billing-update.com",
                messageHi = "Dear Member, aapka subscription Payment fail ho gaya hai. Agar aap apni billing details update nahi karte, toh 24 ghante ke andar aapki membership suspend kar di jayegi. Reactivate karne ke liye yahan click karein: http://netflix-billing-update.com",
                dangerEn = "Harvester websites steal your credit card details, CVV, and login passwords, allowing unauthorized billing charges.",
                dangerHi = "Fake websites aapke Credit Card details, CVV aur login password chura leti hain, jisse unauthorized charges hone lagte hain.",
                redFlagsEn = listOf("Urgent threat of 24-hour suspension", "Non-secure domain name netflix-billing-update.com instead of netflix.com", "Generic greeting 'Dear Member'"),
                redFlagsHi = listOf("24 ghante mein suspension ki urgent warning", "netflix.com ke bajaye non-secure Fake Link netflix-billing-update.com", "Generic greeting 'Dear Member'"),
                safeResponseEn = "Never click links in SMS or email alerts. Open the official Netflix app or website separately to check your billing status.",
                safeResponseHi = "SMS ya email Alert mein diye Fake Link par kabhi click na karein. Apni billing status check karne ke liye Official Website ya Netflix app alag se open karein."
            )
        )

        // 2. OTP Fraud
        baseScams.add(
            ScamExample(
                id = 2,
                category = "OTP Fraud",
                difficulty = "Hard",
                titleEn = "Aadhar-Linked Bank Activation OTP",
                titleHi = "Aadhaar-Linked Bank Activation OTP",
                messageEn = "A caller posing as a government official claims your Aadhaar card needs verification. 'I have sent an SMS code to verify. Please tell me the OTP immediately or your account will be locked.'",
                messageHi = "Ek caller government official bankar claim karta hai ki aapke Aadhaar ki verification baaki hai. 'Maine Verify karne ke liye SMS code bheja hai. Turant OTP batayein warna aapka account lock ho jayega.'",
                dangerEn = "The OTP grants the scammer full authority to link their own number or authorize direct fund withdrawals from your bank.",
                dangerHi = "OTP share karte hi Scammer ko aapke Bank account se paise nikalne ya apna number link karne ka pura access mil jata hai.",
                redFlagsEn = listOf("Caller demanding verification OTP", "Threat of immediate account locking", "Pressure to act quickly without thinking"),
                redFlagsHi = listOf("Caller ka verification ke liye OTP maangna", "Account turant lock hone ki Warning", "Bina soche jaldi Safe Action lene ka pressure"),
                safeResponseEn = "Hang up. No authority or bank ever requests a transaction or verification OTP over a phone call.",
                safeResponseHi = "Call cut karein. Koi bhi Bank ya official authority kabhi phone call par verification OTP nahi maangti."
            )
        )

        // 3. UPI Scam
        baseScams.add(
            ScamExample(
                id = 3,
                category = "UPI Scam",
                difficulty = "Easy",
                titleEn = "OLX Buyer Double Payment UPI Request",
                titleHi = "OLX Buyer Double Payment UPI Request",
                messageEn = "A buyer on OLX claims they accidentally sent you double payment. They send a UPI Request asking you to 'Approve' and type your UPI PIN to return the extra money.",
                messageHi = "OLX par ek buyer claim karta hai ki usne galti se aapko double Payment bhej di hai. Woh ek UPI Request bhej kar aapse 'Approve' karne aur extra paise wapas karne ke liye UPI PIN type karne ko kehta hai.",
                dangerEn = "Approving a UPI collection request with your PIN immediately debits money from your account, rather than receiving it.",
                dangerHi = "UPI collect request ko apna PIN daal kar approve karne se aapke Bank account se paise turant kat jaate hain.",
                redFlagsEn = listOf("Request to input UPI PIN to receive money", "UPI notification marked as 'Collect' rather than credit", "Urgency from buyer"),
                redFlagsHi = listOf("Paise receive karne ke liye UPI PIN daalne ka request", "Credit ke bajaye 'Collect' tag wali UPI notification", "Buyer ki taraf se bohot jaldbazi dikhana"),
                safeResponseEn = "Decline the request immediately. Remember, receiving money via UPI NEVER requires entering your PIN.",
                safeResponseHi = "Is request ko turant decline karein. Yaad rakhein, UPI se paise receive karne ke liye kabhi bhi apna PIN daalne ki zaroorat nahi hoti."
            )
        )

        // 4. QR Code Scam
        baseScams.add(
            ScamExample(
                id = 4,
                category = "QR Code Scam",
                difficulty = "Medium",
                titleEn = "Lucky Draw Reward Scratch Card QR",
                titleHi = "Lucky Draw Reward Scratch Card QR",
                messageEn = "You receive a physical pamphlet or WhatsApp message with a QR code claiming 'Scan this code to scratch and claim Rs. 2,000 cash prize directly in your account.'",
                messageHi = "Aapko WhatsApp par ya physical pamphlet mein ek QR Code milta hai jo claim karta hai 'Scratch karne aur 2,000 Rs ka cash prize apne account mein receive karne ke liye yeh code scan karein.'",
                dangerEn = "Scanning this code redirects you to a malicious payment page designed to debit funds once authorized with a PIN.",
                dangerHi = "QR Code scan karte hi aapko ek Fake Payment page par bheja jata hai jahan PIN daalte hi aapke account se paise kat jaate hain.",
                redFlagsEn = listOf("QR code scanned to receive a cash prize", "Unsolicited reward announcements", "Webpage requesting payment PIN or credit details"),
                redFlagsHi = listOf("Cash prize claim karne ke liye QR Code scan karne ko kehna", "Unsolicited reward announcements", "Payment PIN ya credit details maangne wala webpage"),
                safeResponseEn = "Never scan QR codes to receive funds. QR codes are strictly meant for outgoing payments.",
                safeResponseHi = "Paise receive karne ke liye kabhi bhi QR Code scan na karein. QR Code sirf Payment karne ke liye hote hain."
            )
        )

        // 5. Fake Bank Call
        baseScams.add(
            ScamExample(
                id = 5,
                category = "Fake Bank Call",
                difficulty = "Medium",
                titleEn = "Credit Card Limit Increase Trap",
                titleHi = "Credit Card Limit Increase Trap",
                messageEn = "A caller claiming to represent your credit card provider offers to double your limit instantly without documentation. They request your card number, CVV, and OTP.",
                messageHi = "Aapke Bank ka representative bankar ek caller bina documents ke aapki credit limit double karne ka offer deta hai. Woh aapke card details, CVV, aur OTP maangta hai.",
                dangerEn = "Armed with these details, attackers make online purchases and empty your credit balance.",
                dangerHi = "In details ki madad se Scammer online shopping karke aapka poora credit balance empty kar dete hain.",
                redFlagsEn = listOf("Spoken request for CVV or expiry date", "Promises of instant zero-document upgrades", "Call originating from a standard 10-digit mobile number"),
                redFlagsHi = listOf("Call par CVV ya expiry date maangna", "Bina documents ke instant limit badhane ka wada", "Regular 10-digit mobile number se Bank call aana"),
                safeResponseEn = "State that you will manage upgrades directly inside your banking app or local branch, then hang up.",
                safeResponseHi = "Call cut karein aur bole ki aap apni credit limit directly banking app ya official Bank branch se badha lenge."
            )
        )

        // 6. Fake KYC
        baseScams.add(
            ScamExample(
                id = 6,
                category = "Fake KYC",
                difficulty = "Hard",
                titleEn = "SIM Card Deactivation Threat KYC",
                titleHi = "SIM Card Deactivation Threat KYC",
                messageEn = "An SMS states: 'Dear Customer, your Airtel SIM KYC has expired. Your outgoing services will suspend today. Please complete verification by calling +91-XXXXXXXXXX immediately.'",
                messageHi = "Aapko SMS milta hai: 'Dear Customer, aapka Airtel SIM KYC expire ho gaya hai. Aapki outgoing services aaj suspend ho jayengi. Verify karne ke liye turant +91-XXXXXXXXXX par call karein.'",
                dangerEn = "Fraudsters trick you into installing remote desktop screen sharing tools (like AnyDesk) to view and steal your banking login session.",
                dangerHi = "Scammers aapko screen sharing tools (jaise AnyDesk) install karne ko kehte hain taaki woh aapki banking session dekh kar paise chura sakein.",
                redFlagsEn = listOf("Urgent threat of SIM block within hours", "Requests to call a personal 10-digit phone number for support", "Sender SMS header is a personal mobile number, not a brand code"),
                redFlagsHi = listOf("Kuch ghanton mein SIM block hone ki urgent Warning", "Support ke liye personal 10-digit number par call karne ka request", "SMS kisi personal number se aana, official brand code se nahi"),
                safeResponseEn = "Verify KYC directly at an authorized operator outlet or through their official telecom utility application.",
                safeResponseHi = "Apna KYC sirf official telecom app ya authorized operator outlet par jaakar Verify karein."
            )
        )

        // 7. Fake Delivery
        baseScams.add(
            ScamExample(
                id = 7,
                category = "Fake Delivery",
                difficulty = "Medium",
                titleEn = "Post Office Address Correction Scam",
                titleHi = "Post Office Address Correction Scam",
                messageEn = "An SMS claiming your India Post package could not be delivered due to an incomplete address. 'Update address and schedule delivery by paying Rs 5 fee here: http://indiapost-delivery-tracking.info'",
                messageHi = "Ek SMS claim karta hai ki address incomplete hone ki wajah se aapka India Post package deliver nahi ho saka. 'Address update karein aur 5 Rs ki fee dekar delivery schedule karein: http://indiapost-delivery-tracking.info'",
                dangerEn = "The minor fee payment form captures your card credentials, and the script registers a massive automated transaction soon after.",
                dangerHi = "Jab aap minor 5 Rs fee Payment karne ki koshish karte hain, toh woh aapke card details capture karke aapke account se bada amount kaat lete hain.",
                redFlagsEn = listOf("SMS from standard personal mobile numbers", "Links mimicking official India Post using slightly altered text", "Payment requested to fix a basic delivery address error"),
                redFlagsHi = listOf("Unexpected parcel delivery Alert", "Package release karne ke liye choti si Payment maangna", "Official .gov.in ke bajaye unofficial Fake Link URL"),
                safeResponseEn = "Do not pay. Check the official tracking ID on indiapost.gov.in manually, ignoring the external hyperlink.",
                safeResponseHi = "Unknown parcels par kabhi respond na karein. Official delivery services package release karne ke liye SMS Link se payment nahi maangti."
            )
        )

        // 8. Job Scam
        baseScams.add(
            ScamExample(
                id = 8,
                category = "Job Scam",
                difficulty = "Easy",
                titleEn = "Part-Time YouTube Video Liking Job",
                titleHi = "Part-Time YouTube Video Liking Job",
                messageEn = "A WhatsApp recruitment message offers Rs. 3,000 to Rs. 8,000 daily for simply liking YouTube videos. 'Work from home, 30 minutes daily. Contact our agent on Telegram.'",
                messageHi = "Ek WhatsApp recruiter aapko ek attractive Job Scam offer bhejta hai: 'Rozana 3,000 se 8,000 Rs kamayein, bas hamare YouTube videos ko like karein.'",
                dangerEn = "They build trust by paying small rewards first, then demand large deposit 'investment packages' to unlock higher tasks, freezing your money.",
                dangerHi = "Shuru mein aapko thode paise dekar trust banaya jata hai. Phir woh aapko bade returns ka lalach dekar 'premium tasks' mein paise invest karne ko bolte hain, aur paise lekar gayab ho jaate hain.",
                redFlagsEn = listOf("Unrealistically high payout for low-skill tasks", "Instructions to shift the conversation to Telegram", "Requirement to deposit funds or register on unverified portal schemes"),
                redFlagsHi = listOf("Basic tasks (like aur subscribe) ke liye abnormally high salary", "Telegram groups ke through work operate karna", "High-level tasks unlock karne ke liye Security deposit ki Warning"),
                safeResponseEn = "Block the recruiter. Legitimate businesses never demand monetary deposits to unlock working rights or tasks.",
                safeResponseHi = "Recruiter ko turant block karein. Yaad rakhein, koi bhi asli company videos like karne ke paise nahi deti."
            )
        )

        // 9. Investment Scam
        baseScams.add(
            ScamExample(
                id = 9,
                category = "Investment Scam",
                difficulty = "Hard",
                titleEn = "Guaranteed 500% Crypto Arbitrage Scheme",
                titleHi = "High-Return Crypto Mining Scam",
                messageEn = "An investor group claims they use AI bot technology to earn guaranteed 10% compounding interest daily. 'Send USDT to our wallet address. Watch your funds multiply on our tracking dashboard.'",
                messageHi = "Aapko ek Telegram group mein add kiya jata hai jahan ek Crypto Expert charts share karke promise karta hai: 'Foreign cloud mining mein 5,000 Rs invest karein aur 48 hours mein 25,000 Rs payein.'",
                dangerEn = "The web dashboard shows fake artificial gains, but when you attempt to withdraw, they demand heavy 'taxes' or freeze your access.",
                dangerHi = "Investment platform ek Fake app hota hai. Jab aap apni dikhayi hui 'earnings' nikalne ki koshish karte hain, toh Scamsters withdrawal tax ke naam par aur paise maangte hain.",
                redFlagsEn = listOf("Promises of 'guaranteed' triple digit returns", "Requiring deposits in cryptocurrency or anonymous bank transfers", "No registered SEBI or regulatory license numbers displayed"),
                redFlagsHi = listOf("Bina risk ke short-term mein huge guaranteed returns ka wada", "Unknown foreign Crypto platforms par invest karne ka bolna", "Bina pooche random Telegram groups mein add karna") ,
                safeResponseEn = "Avoid unverified, unregulated digital asset platforms. Invest only through SEBI-registered institutions.",
                safeResponseHi = "Aise sabhi Fake offers ko block karein aur sirf verified aur regulated Indian Investment apps ka use karein."
            )
        )

        // 10. Lottery Scam
        baseScams.add(
            ScamExample(
                id = 10,
                category = "Lottery Scam",
                difficulty = "Easy",
                titleEn = "KBC Crorepati Lucky Draw Winner",
                titleHi = "KBC Crorepati Lucky Draw Winner",
                messageEn = "A WhatsApp voice note accompanied by a poster showing your number won a Rs. 25 Lakh KBC lottery. 'To claim, transfer Rs. 15,000 bank registration fee to our manager.'",
                messageHi = "Aapko WhatsApp par ek voice note aur poster milta hai jisme claim hota hai ki aapke number ne 25 Lakh ki KBC Lottery jeeti hai. 'Claim karne ke liye 15,000 Rs Bank registration fee transfer karein.'",
                dangerEn = "Once the fee is transferred, they request additional charges for custom clearing and then cease contact.",
                dangerHi = "Ek baar fee transfer karne ke baad, Scamsters custom clearing ke naam par aur extra paise maangte hain aur phir block kar dete hain.",
                redFlagsEn = listOf("Winning a lottery you never bought a ticket for", "Demands for advance processing fees before money distribution", "Voice notes recorded in highly unprofessional language"),
                redFlagsHi = listOf("Bina ticket kharide Lottery jeetne ka Fake Alert", "Paise bhejne se pehle advance processing fees maangna", "Highly unprofessional language mein record kiye gaye voice notes"),
                safeResponseEn = "Ignore and delete. No legitimate lottery program requests winners to pay tax or processing charges upfront.",
                safeResponseHi = "Ignore karke delete karein. Koi bhi real Lottery Scam winner se advance tax ya processing fee nahi maangti."
            )
        )

        // 11. Tech Support Scam
        baseScams.add(
            ScamExample(
                id = 11,
                category = "Tech Support Scam",
                difficulty = "Medium",
                titleEn = "Microsoft Windows Virus Firewall Alert",
                titleHi = "Microsoft Windows Virus Firewall Alert",
                messageEn = "A full-screen browser pop-up freezes your PC with loud audio claiming: 'WINDOWS HAS BEEN COMPROMISED. Credit details are being leaked. Call Microsoft Support immediately: 1800-XXX-XXXX.'",
                messageHi = "Ek full-screen browser pop-up loud audio ke saath aapke PC ko freeze kar deta hai aur claim karta hai: 'Windows Virus Alert. Credit details leak ho rahe hain. Microsoft Support ko turant call karein.'",
                dangerEn = "Scammers charge thousands of rupees to run basic disk cleaner tools or install malicious tracking trojans.",
                dangerHi = "Tech Support Scammers basic disk cleaner tool chalane ya Malware install karne ke hazaro rupaye charge karte hain.",
                redFlagsEn = listOf("Loud alarm or pop-ups that lock your browser tabs", "Demands to call emergency support hotlines immediately", "Requests to purchase commercial gift cards to cover repairs"),
                redFlagsHi = listOf("Loud alarm aur pop-ups jo aapke browser tab ko lock kar de", "Emergency support helpline par turant call karne ka Fake Alert", "Repair cover karne ke liye gift cards kharidne ki maang"),
                safeResponseEn = "Close your web browser using Task Manager. Install reputable local anti-virus scanners.",
                safeResponseHi = "Task Manager use karke apna browser band karein. Apne PC mein trusted Anti-Virus scanner install karein."
            )
        )

        // 12. WhatsApp Scam
        baseScams.add(
            ScamExample(
                id = 12,
                category = "WhatsApp Scam",
                difficulty = "Hard",
                titleEn = "Friend in Emergency Money Request",
                titleHi = "Friend in Emergency Money Request",
                messageEn = "You receive a message from an unknown number with a picture of your close friend: 'I changed my number. I am in the hospital with an emergency, please GPay Rs. 10,000 to this doctor's number immediately. I will return it tonight.'",
                messageHi = "Aapko apne close friend ki profile picture ke saath ek unknown number se message aata hai: 'Maine apna number change kar liya hai. Main emergency mein hospital mein hoon, please is doctor ke number par turant 10,000 Rs GPay kar de.'",
                dangerEn = "You transfer money to an attacker's burner wallet, which is instantly withdrawn and untraceable.",
                dangerHi = "Aap Scammer ke burner wallet mein paise transfer kar dete hain, jise turant nikal liya jata hai aur track nahi kiya ja sakta.",
                redFlagsEn = listOf("Urgent request for money from a newly changed number", "Emotional panic tactics regarding medical crises", "Hesitancy to take regular phone calls"),
                redFlagsHi = listOf("Naye number se paise ka urgent request aana", "Medical emergency ka hawala dekar emotional panic create karna", "Direct phone call lene se bachna ya excuse dena"),
                safeResponseEn = "Call your friend on their original, verified telephone number to confirm before performing any transaction.",
                safeResponseHi = "Koi bhi Payment karne se pehle Verify karne ke liye apne dost ke original number par call karein."
            )
        )

        // 13. Telegram Scam
        baseScams.add(
            ScamExample(
                id = 13,
                category = "Telegram Scam",
                difficulty = "Medium",
                titleEn = "Telegram Crypto Signal Group Admin Trade",
                titleHi = "Telegram Stock Trading Tips Scam",
                messageEn = "An admin of a crypto channel messages you: 'We are launching a VIP pool for small investors. Send 100 TRX, and get back 1,000 TRX within 2 hours. Limited spots remaining.'",
                messageHi = "Ek Telegram channel 'India Bulls VIP Tips' promote karta hai: 'Register karne ke liye 999 Rs Payment karein, aur rozana stock trading ki guaranteed tips paayein jisme 400% profit ka wada ho.'",
                dangerEn = "The admin accounts are fake identities, and the funds sent are immediately transferred to tumbler networks.",
                dangerHi = "Channel nakli receipts dikhata hai, aur registration ya initial investment Payment karne ke baad woh aapko block kar dete hain.",
                redFlagsEn = listOf("Unsolicited direct messages from 'Group Admins'", "Unrealistically fast compounding returns (10x in 2 hours)", "Requirement to send digital assets directly to a private wallet"),
                redFlagsHi = listOf("Low-risk daily trades se massive guaranteed profits ka lalach", "Channel admin ke paas koi legal financial advisor credentials na hona", "Private UPI handle ke through Payment maangna"),
                safeResponseEn = "Disable 'Who can add me to groups' in Telegram settings. Block and report any direct message pitching financial pools.",
                safeResponseHi = "Channel ko Report aur block karein. Asli financial advisors ke paas hamesha official SEBI registration code hota hai."
            )
        )

        // 14. Instagram Scam
        baseScams.add(
            ScamExample(
                id = 14,
                category = "Instagram Scam",
                difficulty = "Medium",
                titleEn = "Verified Account Giveaway Winner",
                titleHi = "Instagram Brand Ambassador Sponsorship",
                messageEn = "A verified profile (or clone account) DMs you: 'Congratulations, you won our luxury iPhone giveaway! Just click this verification link to log in with Instagram and fill your address details.'",
                messageHi = "Ek fashion brand account aapko DM karta hai: 'Humein aapki profile bohot pasand aayi! Hum aapko 3 free products sponsor karna chahte hain. Bas is registration Link par click karein aur shipping Payment karein.'",
                dangerEn = "The link takes you to a duplicate Instagram login page designed to record and hijack your profile credentials.",
                dangerHi = "Shipping page ek Phishing trap hota hai jo aapke online credentials ya debit card details chura leta hai, aur aapka Bank account khali kar deta hai.",
                redFlagsEn = listOf("Unsolicited DM claiming giveaway wins", "Log-in screen prompts to claim external items", "Slightly misspelled profile name variant of the actual famous brand"),
                redFlagsHi = listOf("Unsolicited influencer collaboration ke offers aana", "Shipping payment ke liye kisi third-party non-secure Fake Link par bhejna", "Brand DMs mein poor grammar ya copy-paste messages hona"),
                safeResponseEn = "Never input credentials on pages opened through Instagram direct messages. Inspect the sender's account creation date and country.",
                safeResponseHi = "Brand ki Official Website ko independently Verify karein. Kisi random DM se aaye hue Fake Link par kabhi click na karein."
            )
        )

        // 15. Fake Customer Care
        baseScams.add(
            ScamExample(
                id = 15,
                category = "Fake Customer Care",
                difficulty = "Easy",
                titleEn = "Google Map Helpline Scam",
                titleHi = "Google Map Helpline Scam",
                messageEn = "You search for 'Zomato Customer Care Number' on Google Search and find a mobile number added on a Google Map review or community forum. Calling it, the executive requests your payment app password.",
                messageHi = "Kya aapko apni Google Map service ke saath technical problem aa rahi hai? Quick help ke liye is mobile number par hamare support executive se contact karein: +91-XXXXXXXXXX.",
                dangerEn = "These numbers are set up by cybercriminals to intercept inquiries, enabling them to execute fraudulent refund debits.",
                dangerHi = "Fake customer care aapko aisi process follow karne ko bolta hai jisse aapka password ya Bank balance Scam ho jata hai.",
                redFlagsEn = listOf("Customer care listings showing standard mobile numbers", "Instructions to share passwords or pin numbers to register claims", "Calling numbers listed on public community forum threads"),
                redFlagsHi = listOf("Helpline ke naam par personal mobile number ka use karna", "OTP, UPI PIN ya Bank details share karne ko kehna", "Official app mein ticket ya support history na hona"),
                safeResponseEn = "Retrieve customer support info strictly from the brand's verified website or official application interface.",
                safeResponseHi = "Hamesha official in-app support ya verified Official Website portals ke through hi help maangein."
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
                    titleHi = "$brand Security Alert Attempt #$i"
                    messageEn = "Alert: Your $brand account was accessed from an unknown location. If this wasn't you, log in immediately to secure it at http://$brand-security-alert-$i.com"
                    messageHi = "Dear User, humein aapke account mein suspicious login attempt mila hai. Please apni details Verify karein is Fake Link par click karke: http://secure-update-${brand.lowercase()}.com"
                    dangerEn = "Attackers gain direct access to your account and personal cloud data by logging credentials on a fake mirror portal."
                    dangerHi = "Yeh Fake Link aapke account credentials capture kar leta hai, jisse Scammer aapka data chura kar fraud kar sakta hai."
                    redFlagsEn = listOf("Spelling variants in the domain name", "High sense of fear and urgency", "Requests to input old and new passwords")
                    redFlagsHi = listOf("Unknown sender se urgent Security Alert", "Suspicious URL jo official brand jaisa dikhta ho", "Link par click karke login karne ka pressure")
                    safeResponseEn = "Check your login history inside the official $brand account portal directly, skipping external hyperlinks."
                    safeResponseHi = "Aise messages par kabhi click na karein. Hamesha official app ya website manually open karke apna account check karein."
                }
                "OTP Fraud" -> {
                    val bank = banks[i % banks.size]
                    titleEn = "Urgent $bank Reward Points OTP #$i"
                    titleHi = "Urgent $bank Reward Points OTP #$i"
                    messageEn = "A support caller states: 'You have Rs. 5,000 worth of pending reward points on your $bank card. I have sent an activation OTP. Please read it to me to claim.'"
                    messageHi = "Aapke $bank credit card mein 5,000 reward points expire hone wale hain. Points redeem karne ke liye OTP share karein jo aapke number par bheja gaya hai."
                    dangerEn = "This OTP is actually authorizing a fund transfer or adding a beneficiary to your banking dashboard."
                    dangerHi = "OTP share karte hi Scammer aapke Bank account se transactions authorize kar leta hai."
                    redFlagsEn = listOf("Calling from an unofficial mobile number", "Demanding OTP to claim prize cash", "Aggressive insistence to read the SMS text quickly")
                    redFlagsHi = listOf("Reward points claim karne ke liye OTP maangna", "Call par urgent action lene ka pressure", "Unverified caller Bank representative ban kar call kare")
                    safeResponseEn = "Never read OTPs over a phone call. Reward points are claimed inside official net-banking portals safely."
                    safeResponseHi = "OTP kabhi kisi ke saath share na karein. Bank ya official customer care kabhi OTP nahi maangte."
                }
                "UPI Scam" -> {
                    val app = if (i % 2 == 0) "Google Pay" else "PhonePe"
                    titleEn = "$app Cash Refund UPI Trap $i"
                    titleHi = "$app Cash Refund UPI Trap $i"
                    messageEn = "An SMS states: 'Congratulations! You received an instant cash refund of Rs. 1,500 on $app. Tap here to approve credit: upi-refund-collect-req-$i@okaxis'"
                    messageHi = "Aapka recent $app order cancel ho gaya hai. Refund process karne ke liye is UPI Link par click karein aur apna UPI PIN enter karein."
                    dangerEn = "Tapping the UPI link opens a payment screen asking for your UPI PIN. Entering it transfers Rs. 1,500 to the scammer."
                    dangerHi = "UPI PIN daalte hi aapke account se paise kat jaate hain, refund aane ki bajaye."
                    redFlagsEn = listOf("Receiving refunds requires UPI transaction clicks", "Use of an unofficial UPI ID (@okaxis) with suspicious naming", "Entering PIN to receive a credit benefit")
                    redFlagsHi = listOf("Refund receive karne ke liye UPI PIN maangna", "Unknown sender se payment link aana", "Customer care ka payment approve karne ko kehna")
                    safeResponseEn = "Decline any collect requests. Do not enter your PIN unless you explicitly want to pay money."
                    safeResponseHi = "Paise receive karne ke liye UPI PIN ki zaroorat nahi hoti. Aise sabhi requests ko turant decline karein."
                }
                "QR Code Scam" -> {
                    val item = if (i % 2 == 0) "Premium Smartwatch" else "Discount Furniture"
                    titleEn = "Marketplace QR Code Voucher for $item"
                    titleHi = "Marketplace QR Code Voucher for $item"
                    messageEn = "A buyer on Olx sends a QR code claiming: 'I am paying for the $item. Just scan this receipt QR to receive the cash advance into your bank account.'"
                    messageHi = "Main aapka $item kharidne ke liye ready hoon. Maine advance payment ke liye ek QR code bheja hai, isko scan karke apna payment receive karein."
                    dangerEn = "The QR code contains a hidden debit request. Scanning and typing your PIN executes an outgoing transaction."
                    dangerHi = "QR code scan karte hi aapke account se paise debit ho jaate hain."
                    redFlagsEn = listOf("Scanning a QR to receive buyer funds", "Buyer refusing standard bank transfers or cash", "Hurry to scan the code immediately")
                    redFlagsHi = listOf("Payment receive karne ke liye QR Code scan karne ko kehna", "Buyer ka bina soche advance payment offer karna", "UPI app mein 'Pay' ki bajaye scan request aana")
                    safeResponseEn = "Decline Olx buyers who demand scanning QR codes. Ask for cash on delivery or direct bank transfer."
                    safeResponseHi = "Paise receive karne ke liye kabhi QR Code scan na karein. QR Code sirf payment dene ke liye use hota hai."
                }
                "Fake Bank Call" -> {
                    val bank = banks[i % banks.size]
                    titleEn = "$bank Suspicious Transaction Verification #$i"
                    titleHi = "$bank Suspicious Transaction Verification #$i"
                    messageEn = "A caller claiming to be a $bank fraud manager says: 'We detected a fraudulent charge of Rs. 49,999 on your account. To block it, please verify your internet banking password and OTP.'"
                    messageHi = "Hum $bank se call kar rahe hain. Aapke account se 25,000 Rs ka suspicious transaction hua hai. Isko block karne ke liye apna card number aur OTP batayein."
                    dangerEn = "The scammer logs into your account using your shared credentials and transfers your remaining funds instantly."
                    dangerHi = "Card details aur OTP milte hi Scamsters aapke account se paise nikal lete hain."
                    redFlagsEn = listOf("Urgent threat of Rs. 49,999 loss", "Direct requests for net-banking passwords", "No official IVR validation channel used")
                    redFlagsHi = listOf("Call par card details ya OTP maangna", "Account block karne ka darr dikhana", "Personal number se Bank ka call aana")
                    safeResponseEn = "Hang up and check your account transactions via the official mobile banking app. If concerned, call the support number listed on your physical debit card."
                    safeResponseHi = "Aise calls ko cut karein aur apne Bank ke official customer care number par call karke apna account Verify karein."
                }
                "Fake KYC" -> {
                    val service = if (i % 2 == 0) "Electricity Board" else "Gas Pipeline Support"
                    titleEn = "Urgent $service KYC Suspension Notice $i"
                    titleHi = "Urgent $service KYC Suspension Notice $i"
                    messageEn = "An SMS warns: 'Power/Gas supply to your household will be disconnected tonight at 9:30 PM due to unpaid bills / incomplete KYC. Call +91-XXXXXXXXXX to verify.'"
                    messageHi = "Aapka $service KYC complete nahi hai. Aapki services aaj raat band ho jayengi. Apna KYC complete karne ke liye is link par click karein aur Rs. 10 payment karein."
                    dangerEn = "Scammers collect security payments or manipulate you into downloading screen sharing utilities to access digital wallets."
                    dangerHi = "Jab aap small payment karte hain, Fake Link aapke card details save kar leta hai aur bada amount debit kar leta hai."
                    redFlagsEn = listOf("Short timeline threat of utility cutoffs", "Directing you to a personal phone number rather than official billing portals", "Vague descriptions of verification processes")
                    redFlagsHi = listOf("KYC update ke liye urgent suspension warning", "Third-party link se payment ya details maangna", "Unverified sender SMS")
                    safeResponseEn = "Ignore the threat. Pay and verify bills only via official consumer service portals or authorized state utility apps."
                    safeResponseHi = "Official app ya authorized store par jaakar apna KYC Verify karein. SMS links par kabhi click na karein."
                }
                "Fake Delivery" -> {
                    val carrier = if (i % 2 == 0) "Bluedart" else "Delhivery"
                    titleEn = "Failed $carrier Package Tracking Fee #$i"
                    titleHi = "Failed $carrier Package Tracking Fee #$i"
                    messageEn = "Your $carrier package is on hold at our regional warehouse due to a wrong street number. Pay Rs. 10 to reschedule delivery: http://$carrier-tracking-fee-$i.info"
                    messageHi = "Aapka $carrier parcel deliver nahi ho paaya kyunki address incomplete tha. Address update karne aur redelivery ke liye Rs. 5 fee pay karein: http://$carrier-tracking-update.com"
                    dangerEn = "This minor fee serves as a gateway to steal card details, which are subsequently used for high-value offshore transfers."
                    dangerHi = "Fee pay karne ke time aapke card details steal ho jaate hain aur aapke bank se fraud transaction hota hai."
                    redFlagsEn = listOf("Slightly altered URL mimicking $carrier", "Unexpected request to pay fees for delivery adjustments", "Links inside SMS coming from anonymous personal senders")
                    redFlagsHi = listOf("Package delivery ke liye small fee maangna", "Unexpected parcel ki notification", "Official website ke badle fake tracking link")
                    safeResponseEn = "Contact the merchant who shipped your product or open the carrier's official portal directly to track details."
                    safeResponseHi = "Agar aapne kuch order nahi kiya, toh SMS ko ignore karein. Official delivery services aise links se payment nahi maangti."
                }
                "Job Scam" -> {
                    val jobTitle = jobs[i % jobs.size]
                    titleEn = "Work From Home $jobTitle Offer #$i"
                    titleHi = "Work From Home $jobTitle Offer #$i"
                    messageEn = "Exciting Job: Earn up to Rs. 4,500 daily as a $jobTitle. No experience required. Pay Rs. 299 registration fee to receive the starting kit."
                    messageHi = "Hum aapko $jobTitle ki post offer kar rahe hain. Rozana 2-3 ghante kaam karein aur 5,000 Rs kamayein. Registration ke liye Rs. 1,000 security deposit pay karein."
                    dangerEn = "The registration fee is lost, and they will demand further training fees while never providing real employment."
                    dangerHi = "Deposit lene ke baad recruiter gayab ho jaata hai, aur aapko koi kaam nahi milta."
                    redFlagsEn = listOf("Requirement to pay money to get a job", "Atypical high payouts for simple administrative tasks", "No official corporate domain email used during recruitment")
                    redFlagsHi = listOf("Job ke liye registration ya security deposit maangna", "Bina interview ke direct job offer", "Too good to be true salary offer")
                    safeResponseEn = "Never pay money to apply or receive training for a job. Verified companies pay employees, not the other way around."
                    safeResponseHi = "Real companies kabhi job dene ke liye paise nahi maangti. Aise offers ko turant block karein."
                }
                "Investment Scam" -> {
                    titleEn = "VIP Trading Signal Club Success $i"
                    titleHi = "VIP Trading Signal Club Success $i"
                    messageEn = "Earn 50% returns weekly trading options with our expert group. Deposit capital to our secure UPI account. Over 20,000 satisfied members already!"
                    messageHi = "Hamare VIP Trading Club se judein aur rozana 50% profit kamayein. Start karne ke liye sirf Rs. 2,000 invest karein. Guaranteed returns milenge!"
                    dangerEn = "Once deposited, your funds are routed into untraceable private accounts, and the group admins block you from the chat room."
                    dangerHi = "Fake trading platform par aapka paisa block ho jaata hai aur profit nikalne ke liye aur fees maangi jaati hai."
                    redFlagsEn = listOf("Promises of extremely high weekly trading gains", "Deposits made to individual UPI handles rather than brokerage accounts", "Lack of verified performance records or registration checks")
                    redFlagsHi = listOf("Guaranteed high returns ka wada", "Unverified trading platform par invest karne ko kehna", "Withdrawal ke time hidden fees maangna")
                    safeResponseEn = "Ignore public group pitches on social apps. Only invest via registered broker platforms monitored by financial regulators."
                    safeResponseHi = "Aise fake investment schemes se door rahein. Hamesha SEBI-registered brokers ke through invest karein."
                }
                "Lottery Scam" -> {
                    val prize = if (i % 2 == 0) "Tata Nexon SUV" else "Free Trip to Dubai"
                    titleEn = "Sponsorship Sweepstakes Winner: $prize"
                    titleHi = "Sponsorship Sweepstakes Winner: $prize"
                    messageEn = "Dear User, Your mobile number has won a brand new $prize in our annual lucky draw. Pay Rs. 8,999 shipping insurance fee to claim."
                    messageHi = "Congratulations! Aapne hamara Lucky Draw jeeta hai. Apna $prize claim karne ke liye tax aur processing fee Rs. 5,000 is account mein transfer karein."
                    dangerEn = "Scammers pocket your 'shipping insurance fee' and continue demanding extra custom clearance fees before vanishing."
                    dangerHi = "Aap fee transfer karte hain aur scammer paise lekar block kar deta hai, prize kabhi nahi milta."
                    redFlagsEn = listOf("Winning a high-value physical asset from a contest you never registered in", "Direct request to pay shipping, customs, or insurance fees upfront", "Communication originating from standard WhatsApp personal profiles")
                    redFlagsHi = listOf("Bina participate kiye lucky draw jeetna", "Prize claim karne ke liye advance tax maangna", "Unprofessional message aur bank account details")
                    safeResponseEn = "Delete the alert immediately. Legitimate sweepstakes deduct necessary taxes directly from the winnings and do not charge upfront insurance fees."
                    safeResponseHi = "Is message ko ignore aur delete karein. Asli lottery winners ko advance paise nahi dene padte."
                }
                "Tech Support Scam" -> {
                    val app = eComs[i % eComs.size]
                    titleEn = "$app Fake Refund Helpline Trap"
                    titleHi = "$app Fake Refund Helpline Trap"
                    messageEn = "Having trouble with your $app order refund? Call our quick support line: 1800-FAKE-HELP. We will assist you with immediate credit transfers."
                    messageHi = "Agar aapko $app se related koi payment issue hai, toh hamare customer support executive se is number par call karein: +91-XXXXXXXXXX. Hum turant refund karenge."
                    dangerEn = "Scammers pretend to process a refund, tricking you into scanning a UPI code that actually transfers funds from your wallet."
                    dangerHi = "Scammer aapko screen sharing app install karne ko bolta hai aur aapke phone ka access lekar bank account khali kar deta hai."
                    redFlagsEn = listOf("Phone support numbers posted on social media comments or unsourced forums", "Requiring a payment scanner or pin to trigger a refund credit", "Executive speaking in an unprofessional or informal tone")
                    redFlagsHi = listOf("Social media par customer support numbers search karna", "Refund ke liye AnyDesk ya screen share app install karne ko bolna", "Executive ka UPI PIN ya OTP maangna")
                    safeResponseEn = "Only communicate through the official $app application support chat or verified contact details published on their main website."
                    safeResponseHi = "Customer support ke liye hamesha official app ka use karein. Google ya social media se mile numbers par call na karein."
                }
                "WhatsApp Scam" -> {
                    titleEn = "WhatsApp 6-Digit Verification Code Scam"
                    titleHi = "WhatsApp 6-Digit Verification Code Scam"
                    messageEn = "A relative's compromised account DMs you: 'I accidentally sent my WhatsApp login verification code to your phone. Can you please forward me that 6-digit SMS code quickly?'"
                    messageHi = "Ek relative ka hacked account message karta hai: 'Maine galti se apna WhatsApp verification code tumhare number par bhej diya hai. Kya tum mujhe woh 6-digit SMS code jaldi bhej sakte ho?'"
                    dangerEn = "If you share the code, the attacker takes over your WhatsApp account, locks you out, and starts scamming your contacts."
                    dangerHi = "Jaise hi aap code share karte hain, scammer aapka WhatsApp account hack kar leta hai aur aapke contacts ko scam karta hai."
                    redFlagsEn = listOf("Request to share WhatsApp verification codes", "Irregular messages from family members demanding quick favors", "SMS message warning 'Do not share this code'")
                    redFlagsHi = listOf("WhatsApp verification code share karne ka request", "Family ya friends se achanak urgent help maangna", "SMS mein 'Do not share this code' likha hona")
                    safeResponseEn = "Never share verification codes with anyone, even close family. Call them via a regular voice call to confirm if their profile is hacked."
                    safeResponseHi = "Apna verification code kabhi kisi ke saath share na karein, chahe woh family hi kyun na ho. Unhein call karke Verify karein."
                }
                "Telegram Scam" -> {
                    titleEn = "Telegram Channel Investment Expert Setup"
                    titleHi = "Telegram Channel Investment Expert Setup"
                    messageEn = "Join our exclusive Telegram channel. Pay Rs. 999 to enter, and receive guaranteed micro-signals daily for stock trading that guarantee 400% profits."
                    messageHi = "Hamare exclusive Telegram channel se judein. Entry ke liye 999 Rs pay karein aur rozana stock trading signals paayein jo 400% profit guarantee karte hain."
                    dangerEn = "The channel publishes fake curated receipts, and after paying the registration or initial trade investment, they permanently block your profile."
                    dangerHi = "Registration fee pay karne ke baad channel aapko block kar deta hai ya aur paise invest karne ko bolta hai."
                    redFlagsEn = listOf("Assurances of massive guaranteed profits from low-risk daily trades", "No legal financial advisory credentials presented by channel admin", "Payment via private UPI handles")
                    redFlagsHi = listOf("Guaranteed massive profits from low-risk trades", "Channel admin ke paas legal financial advisor license na hona", "Private UPI id par payment maangna")
                    safeResponseEn = "Report and block the channel. Legitimate financial advisors must display SEBI registration codes."
                    safeResponseHi = "Aise channels ko Report aur block karein. Real financial advisors apna SEBI registration code display karte hain."
                }
                "Instagram Scam" -> {
                    titleEn = "Instagram Brand Ambassador Sponsorship"
                    titleHi = "Instagram Brand Ambassador Sponsorship"
                    messageEn = "A fashion brand account DMs you: 'We love your profile style! We want to sponsor you with 3 free products. Just click this tracking registration link and pay shipping.'"
                    messageHi = "Ek fashion brand account DM karta hai: 'Humein aapki profile bohot pasand aayi! Hum aapko 3 free products sponsor karna chahte hain. Bas is link par click karein aur shipping fee pay karein.'"
                    dangerEn = "The shipping page is a phishing trap that captures your online credentials or debit cards, draining your accounts."
                    dangerHi = "Shipping page ek phishing trap hota hai jo aapke online credentials ya card details chura leta hai."
                    redFlagsEn = listOf("Unsolicited influencer collaboration offers", "Directing you to external non-secure checkout sites for simple shipping payments", "Poor grammar or automated translation layouts in brand DMs")
                    redFlagsHi = listOf("Unsolicited brand ambassador offers", "Third-party site par shipping fee pay karne ko kehna", "Brand account ki poor grammar aur fake followers")
                    safeResponseEn = "Review the brand's verified website independently. Never click sponsorship registration links sent via random DMs."
                    safeResponseHi = "Random DMs se aaye links par kabhi click na karein. Brand ki Official Website par jakar verify karein."
                }
                else -> { // Fake Customer Care
                    val brand = techBrands[i % techBrands.size]
                    titleEn = "Fake $brand Online Helpline Interception"
                    titleHi = "Fake $brand Online Helpline Interception"
                    messageEn = "Faced technical glitches with your $brand service? Contact our live chat executive on this mobile number: +91-XXXXXXXXXX for prompt assistance."
                    messageHi = "Kya aapko apni $brand service ke saath technical problem aa rahi hai? Quick help ke liye is mobile number par hamare support executive se contact karein: +91-XXXXXXXXXX."
                    dangerEn = "The fake help representatives guide you to execute operations that compromise your account passwords or bank balances."
                    dangerHi = "Fake customer care aapko aisi process follow karne ko bolta hai jisse aapka password ya Bank balance Scam ho jata hai."
                    redFlagsEn = listOf("Helpline contact info using personal mobile numbers", "Instructions to share authorization codes or credit data", "Absence of official platform ticketing systems")
                    redFlagsHi = listOf("Helpline ke naam par personal mobile number ka use karna", "OTP, UPI PIN ya Bank details share karne ko kehna", "Official app mein ticket ya support history na hona")
                    safeResponseEn = "Always communicate with $brand through their official in-app feedback channels or secure contact portals."
                    safeResponseHi = "Hamesha $brand ke official in-app support ya verified Official Website portals ke through hi help maangein."
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
