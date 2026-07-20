package com.example

class DangerRuleEngine {
    private val rules = mutableListOf<Rule>()

    init {
        // ==========================================
        // BATCH 1 DANGER RULES (1 - 50)
        // ==========================================

        // --- 1. OTP SHARING SCAM (15 Rules) ---
        registerRule(PhraseDangerRule("DNG_OTP_01", "OTP Sharing Scam", listOf("share your otp", "share otp with"), "Scammer asking to share OTP"))
        registerRule(PhraseDangerRule("DNG_OTP_02", "OTP Sharing Scam", listOf("tell us your otp", "tell your otp to"), "Scammer asking to disclose OTP"))
        registerRule(PhraseDangerRule("DNG_OTP_03", "OTP Sharing Scam", listOf("send otp immediately", "send otp now"), "Scammer urgently requesting OTP"))
        registerRule(PhraseDangerRule("DNG_OTP_04", "OTP Sharing Scam", listOf("otp batao", "apna otp batao"), "Hindi request asking user to share OTP"))
        registerRule(PhraseDangerRule("DNG_OTP_05", "OTP Sharing Scam", listOf("otp forward karo", "forward your otp"), "Request to forward OTP to phone or agent"))
        registerRule(PhraseDangerRule("DNG_OTP_06", "OTP Sharing Scam", listOf("verification code bhejo", "code bhejo jald"), "Hindi request asking for verification code"))
        registerRule(PhraseDangerRule("DNG_OTP_07", "OTP Sharing Scam", listOf("give me your otp", "give otp to agent"), "Direct request to give OTP to agent"))
        registerRule(PhraseDangerRule("DNG_OTP_08", "OTP Sharing Scam", listOf("otp required for verification", "otp mandatory for verification"), "Claiming OTP is mandatory/required to verify/unblock"))
        registerRule(PhraseDangerRule("DNG_OTP_09", "OTP Sharing Scam", listOf("read your otp", "read otp to verify"), "Request to read out OTP over call or SMS"))
        registerRule(PhraseDangerRule("DNG_OTP_10", "OTP Sharing Scam", listOf("confirm your otp now", "confirm your otp with us"), "Asking to confirm OTP over chat/call"))
        registerRule(PhraseDangerRule("DNG_OTP_11", "OTP Sharing Scam", listOf("share verification code", "share your verification code"), "Asking to share dynamic verification code"))
        registerRule(PhraseDangerRule("DNG_OTP_12", "OTP Sharing Scam", listOf("forward verification code", "forward verification code to"), "Asking to forward verification code"))
        registerRule(PhraseDangerRule("DNG_OTP_13", "OTP Sharing Scam", listOf("otp bhejo", "verification code bheje"), "Direct Hindi command to send OTP"))
        registerRule(PhraseDangerRule("DNG_OTP_14", "OTP Sharing Scam", listOf("provide your otp", "provide otp immediately"), "Formal phrasing asking to provide OTP"))
        registerRule(PhraseDangerRule("DNG_OTP_15", "OTP Sharing Scam", listOf("tell us the otp", "tell the otp to"), "Direct request to tell the OTP"))

        // --- 2. FAKE LOTTERY SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_LOT_01", "Fake Lottery Scam", listOf("congratulations you won", "congratulations, you have won"), "Deceptive lottery winner notification"))
        registerRule(PhraseDangerRule("DNG_LOT_02", "Fake Lottery Scam", listOf("claim your prize", "claim prize money"), "Deceptive call-to-action to claim cash/prize"))
        registerRule(PhraseDangerRule("DNG_LOT_03", "Fake Lottery Scam", listOf("lottery winner", "winner of lottery"), "Deceptive lottery draw winning claim"))
        registerRule(PhraseDangerRule("DNG_LOT_04", "Fake Lottery Scam", listOf("you have won ₹", "you won ₹"), "Deceptive cash-winning lottery bait"))
        registerRule(PhraseDangerRule("DNG_LOT_05", "Fake Lottery Scam", listOf("mega lottery", "bumper lottery"), "Deceptive high-value sweepstake scheme"))
        registerRule(PhraseDangerRule("DNG_LOT_06", "Fake Lottery Scam", listOf("lucky winner", "lucky draw winner"), "Deceptive random selection lottery bait"))
        registerRule(PhraseDangerRule("DNG_LOT_07", "Fake Lottery Scam", listOf("cash prize waiting", "cash prize is waiting"), "Deceptive alert claiming unclaimed prize funds"))
        registerRule(PhraseDangerRule("DNG_LOT_08", "Fake Lottery Scam", listOf("reward expires today", "prize expires today"), "Deceptive urgency tactic on fake lottery claims"))
        registerRule(PhraseDangerRule("DNG_LOT_09", "Fake Lottery Scam", listOf("prize claim", "claim prize money"), "Deceptive instructions to process prize release"))
        registerRule(PhraseDangerRule("DNG_LOT_10", "Fake Lottery Scam", listOf("international lottery", "global lucky draw"), "Deceptive foreign lottery / global jackpot trap"))

        // --- 3. FAKE REWARD SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_RWD_01", "Fake Reward Scam", listOf("claim cashback now", "claim your cashback"), "Deceptive link bait to claim unapplied cashback"))
        registerRule(PhraseDangerRule("DNG_RWD_02", "Fake Reward Scam", listOf("claim reward now", "claim your reward"), "Deceptive portal click to retrieve reward"))
        registerRule(PhraseDangerRule("DNG_RWD_03", "Fake Reward Scam", listOf("free gift waiting", "free gift is waiting"), "Deceptive incentive claiming a gift is held"))
        registerRule(PhraseDangerRule("DNG_RWD_04", "Fake Reward Scam", listOf("exclusive reward", "exclusive cashback"), "Deceptive premium gift/reward eligibility lure"))
        registerRule(PhraseDangerRule("DNG_RWD_05", "Fake Reward Scam", listOf("bonus reward", "bonus cashback"), "Deceptive payout targeting employee or customer bonus"))
        registerRule(PhraseDangerRule("DNG_RWD_06", "Fake Reward Scam", listOf("free iphone winner", "won a free iphone"), "Deceptive high-value consumer electronic prize bait"))
        registerRule(PhraseDangerRule("DNG_RWD_07", "Fake Reward Scam", listOf("gift voucher waiting", "gift card waiting"), "Deceptive commercial brand gift card release lure"))
        registerRule(PhraseDangerRule("DNG_RWD_08", "Fake Reward Scam", listOf("congratulations reward", "congratulations on your reward"), "Deceptive greetings announcing a reward payout"))
        registerRule(PhraseDangerRule("DNG_RWD_09", "Fake Reward Scam", listOf("festival reward", "festive reward"), "Deceptive seasonal/festival gift incentive"))
        registerRule(PhraseDangerRule("DNG_RWD_10", "Fake Reward Scam", listOf("limited reward", "limited time reward"), "Deceptive scarcity tactic to hasten reward extraction"))

        // --- 4. FAKE URGENCY SCAM (15 Rules) ---
        registerRule(PhraseDangerRule("DNG_URG_01", "Fake Urgency Scam", listOf("act immediately", "please act immediately to avoid"), "Deceptive extreme urgency prompt"))
        registerRule(PhraseDangerRule("DNG_URG_02", "Fake Urgency Scam", listOf("urgent action required", "action required immediately"), "Deceptive administrative action threat"))
        registerRule(PhraseDangerRule("DNG_URG_03", "Fake Urgency Scam", listOf("immediate verification required", "verify account immediately"), "Deceptive identity verification blackmail"))
        registerRule(PhraseDangerRule("DNG_URG_04", "Fake Urgency Scam", listOf("account will be blocked today", "account is blocked today"), "Deceptive direct threat of banking block"))
        registerRule(PhraseDangerRule("DNG_URG_05", "Fake Urgency Scam", listOf("account suspended today", "account will be suspended today"), "Deceptive direct threat of profile suspension"))
        registerRule(PhraseDangerRule("DNG_URG_06", "Fake Urgency Scam", listOf("respond immediately", "please respond immediately to avoid"), "Deceptive command demanding instant response"))
        registerRule(PhraseDangerRule("DNG_URG_07", "Fake Urgency Scam", listOf("block within 30 minutes", "suspended within 30 minutes", "pay within 30 minutes"), "Deceptive hyper-urgency time constraint coercion"))
        registerRule(PhraseDangerRule("DNG_URG_08", "Fake Urgency Scam", listOf("failure to respond will result in", "failing to respond will result"), "Deceptive negative consequence warning"))
        registerRule(PhraseDangerRule("DNG_URG_09", "Fake Urgency Scam", listOf("last warning", "this is your last warning"), "Deceptive intimidation threat sequence"))
        registerRule(PhraseDangerRule("DNG_URG_10", "Fake Urgency Scam", listOf("final notice", "this is the final notice"), "Deceptive formal deadline warning"))
        registerRule(PhraseDangerRule("DNG_URG_11", "Fake Urgency Scam", listOf("immediate payment required", "make immediate payment"), "Deceptive bill/fine settlement pressure"))
        registerRule(PhraseDangerRule("DNG_URG_12", "Fake Urgency Scam", listOf("click immediately", "click here immediately to"), "Deceptive prompt to force instant URL click"))
        registerRule(PhraseDangerRule("DNG_URG_13", "Fake Urgency Scam", listOf("time running out", "time is running out for your"), "Deceptive clock ticking psychological pressure"))
        registerRule(PhraseDangerRule("DNG_URG_14", "Fake Urgency Scam", listOf("avoid suspension", "to avoid suspension of your"), "Deceptive warning targeting user's fear of suspension"))
        registerRule(PhraseDangerRule("DNG_URG_15", "Fake Urgency Scam", listOf("verify your account now to prevent", "verify your account now to avoid"), "Deceptive prompt to force instant credential verification"))

        // ==========================================
        // BATCH 2 DANGER RULES (51 - 100)
        // ==========================================

        // --- 5. FAKE JOB / PART-TIME INCOME (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_JOB_01", "Fake Job / Part-time Income", listOf("earn ₹5000 daily", "daily income rs", "earn rs 5000 daily"), "Fake high daily earning job offer scam"))
        registerRule(PhraseDangerRule("DNG_JOB_02", "Fake Job / Part-time Income", listOf("part time job from home", "work from home job", "part time job work from home"), "Fake work-from-home job scam"))
        registerRule(PhraseDangerRule("DNG_JOB_03", "Fake Job / Part-time Income", listOf("earn money by liking", "youtube like job", "like youtube videos to earn"), "Social media video liking scam"))
        registerRule(PhraseDangerRule("DNG_JOB_04", "Fake Job / Part-time Income", listOf("earn money by rating", "google map review job", "rate on google maps to earn"), "Google review rating scam"))
        registerRule(PhraseDangerRule("DNG_JOB_05", "Fake Job / Part-time Income", listOf("telegram task job", "complete telegram tasks", "telegram tasks to earn"), "Telegram task-based deposit scam"))
        registerRule(PhraseDangerRule("DNG_JOB_06", "Fake Job / Part-time Income", listOf("earn extra salary", "hourly wage rs", "extra monthly income"), "Deceptive hourly wage/extra salary bait"))
        registerRule(PhraseDangerRule("DNG_JOB_07", "Fake Job / Part-time Income", listOf("no experience required job", "qualification not needed", "no qualification required"), "Low-barrier fake job lure"))
        registerRule(PhraseDangerRule("DNG_JOB_08", "Fake Job / Part-time Income", listOf("register to earn money", "signup bonus earn", "register now to get rs"), "Fake registration earning scam"))
        registerRule(PhraseDangerRule("DNG_JOB_09", "Fake Job / Part-time Income", listOf("commission income", "daily commission payout", "high commission job"), "Fake high commission task scam"))
        registerRule(PhraseDangerRule("DNG_JOB_10", "Fake Job / Part-time Income", listOf("part-time job vacancy", "hiring online workers", "online workers needed"), "Fake part-time vacancy scam"))

        // --- 6. FAKE KYC / PAN UPDATE (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_KYC_01", "Fake KYC / PAN Update", listOf("update your kyc", "kyc expired", "kyc is expired"), "Fake KYC expiration panic link"))
        registerRule(PhraseDangerRule("DNG_KYC_02", "Fake KYC / PAN Update", listOf("update pan card", "pan card blocked", "update your pan card"), "Fake PAN card blocking alert"))
        registerRule(PhraseDangerRule("DNG_KYC_03", "Fake KYC / PAN Update", listOf("verify kyc immediately", "complete kyc update", "verify your kyc to avoid"), "Fake immediate KYC compliance trap"))
        registerRule(PhraseDangerRule("DNG_KYC_04", "Fake KYC / PAN Update", listOf("sbi kyc pending", "sbi account blocked kyc", "sbi kyc update"), "Fake SBI banking KYC update scam"))
        registerRule(PhraseDangerRule("DNG_KYC_05", "Fake KYC / PAN Update", listOf("account blocked due to kyc", "kyc suspension", "account suspended due to kyc"), "Fake account suspension warning"))
        registerRule(PhraseDangerRule("DNG_KYC_06", "Fake KYC / PAN Update", listOf("aadhaar card kyc update", "link aadhaar immediately", "update aadhaar card kyc"), "Fake Aadhaar linking requirement"))
        registerRule(PhraseDangerRule("DNG_KYC_07", "Fake KYC / PAN Update", listOf("sim kyc update", "sim card block kyc", "verify sim card kyc"), "Fake mobile SIM block KYC scam"))
        registerRule(PhraseDangerRule("DNG_KYC_08", "Fake KYC / PAN Update", listOf("kyc verification pending", "submit kyc details", "kyc upload link"), "Fake KYC document collection link"))
        registerRule(PhraseDangerRule("DNG_KYC_09", "Fake KYC / PAN Update", listOf("netbanking kyc block", "ebanking kyc link", "net banking kyc"), "Fake netbanking KYC block lure"))
        registerRule(PhraseDangerRule("DNG_KYC_10", "Fake KYC / PAN Update", listOf("re-verify kyc now", "kyc validation link", "reverify kyc immediately"), "Fake KYC re-verification link"))

        // --- 7. ELECTRICITY BILL SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_ELE_01", "Electricity Bill Scam", listOf("electricity will be disconnected", "power cut today", "power disconnection warning"), "Fake electricity disconnection threat"))
        registerRule(PhraseDangerRule("DNG_ELE_02", "Electricity Bill Scam", listOf("call electricity officer", "contact power inspector", "contact electricity office"), "Fake support call numbers for power bill"))
        registerRule(PhraseDangerRule("DNG_ELE_03", "Electricity Bill Scam", listOf("pay your light bill", "electricity dues pending", "pending electricity bill"), "Fake light bill urgent reminder"))
        registerRule(PhraseDangerRule("DNG_ELE_04", "Electricity Bill Scam", listOf("power disconnected tonight", "light cut at 9:30pm", "power cut at 9:30pm"), "Fake immediate night blackout threat"))
        registerRule(PhraseDangerRule("DNG_ELE_05", "Electricity Bill Scam", listOf("electricity bill verification", "verify electricity payment", "verify light bill"), "Fake utility payment validation"))
        registerRule(PhraseDangerRule("DNG_ELE_06", "Electricity Bill Scam", listOf("update electricity bill", "light bill kyc", "electricity kyc update"), "Fake electricity ledger updating"))
        registerRule(PhraseDangerRule("DNG_ELE_07", "Electricity Bill Scam", listOf("bses electricity block", "bses disconnection", "bses power cut"), "Fake BSES Delhi electricity scam"))
        registerRule(PhraseDangerRule("DNG_ELE_08", "Electricity Bill Scam", listOf("mseb power cut", "mseb disconnection", "mseb electricity bill"), "Fake MSEB Maharashtra electricity scam"))
        registerRule(PhraseDangerRule("DNG_ELE_09", "Electricity Bill Scam", listOf("uppcl electricity disconnect", "uppcl dues", "uppcl power cut"), "Fake UPPCL Uttar Pradesh electricity scam"))
        registerRule(PhraseDangerRule("DNG_ELE_10", "Electricity Bill Scam", listOf("power supply suspend", "suspension of electricity", "electricity supply suspended"), "Fake power supply suspension notice"))

        // --- 8. CUSTOMS / POLICE / PARCEL SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_PAR_01", "Customs / Police / Parcel Scam", listOf("parcel seized by customs", "customs held parcel", "parcel held by customs"), "Fake international package seizure"))
        registerRule(PhraseDangerRule("DNG_PAR_02", "Customs / Police / Parcel Scam", listOf("customs clearance fee", "pay customs tax", "customs duty payment"), "Fake customs import duty payment request"))
        registerRule(PhraseDangerRule("DNG_PAR_03", "Customs / Police / Parcel Scam", listOf("police courier block", "illegal drugs in parcel", "illegal substances in parcel"), "Fake police/customs package block extortion"))
        registerRule(PhraseDangerRule("DNG_PAR_04", "Customs / Police / Parcel Scam", listOf("fedex parcel illegal", "fedex shipment seized", "fedex parcel seized"), "Fake FedEx shipment seizure scam"))
        registerRule(PhraseDangerRule("DNG_PAR_05", "Customs / Police / Parcel Scam", listOf("dhl parcel block", "dhl customs hold", "dhl parcel seized"), "Fake DHL courier package seizure scam"))
        registerRule(PhraseDangerRule("DNG_PAR_06", "Customs / Police / Parcel Scam", listOf("unpaid tax courier", "courier clearance charge", "parcel clearance fee"), "Fake logistics shipping tax scam"))
        registerRule(PhraseDangerRule("DNG_PAR_07", "Customs / Police / Parcel Scam", listOf("postal package address update", "post office package hold", "india post package hold"), "Fake India Post address updating link"))
        registerRule(PhraseDangerRule("DNG_PAR_08", "Customs / Police / Parcel Scam", listOf("police verification parcel", "legal action parcel", "police verification for parcel"), "Fake police legal action parcel threat"))
        registerRule(PhraseDangerRule("DNG_PAR_09", "Customs / Police / Parcel Scam", listOf("contraband found parcel", "narcotics package hold", "contraband in package"), "Fake narcotics containment seizure scam"))
        registerRule(PhraseDangerRule("DNG_PAR_10", "Customs / Police / Parcel Scam", listOf("delivery address failure link", "update delivery address immediately", "correct delivery address to receive"), "Fake shipping redelivery phishing scam"))

        // --- 9. FAKE LOAN / CREDIT CARD SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_LON_01", "Fake Loan / Credit Card", listOf("loan approved instantly", "instant loan ₹", "instant personal loan approved"), "Fake instant loan pre-approval scam"))
        registerRule(PhraseDangerRule("DNG_LON_02", "Fake Loan / Credit Card", listOf("loan without documents", "no document loan", "loans without documentation"), "Fake no-documentation high interest loan trap"))
        registerRule(PhraseDangerRule("DNG_LON_03", "Fake Loan / Credit Card", listOf("urgent loan clearance", "loan sanction letter", "sanction letter of loan"), "Fake advance-fee loan sanction fraud"))
        registerRule(PhraseDangerRule("DNG_LON_04", "Fake Loan / Credit Card", listOf("credit card limit upgrade", "increase credit card limit", "upgrade credit limit now"), "Fake credit limit expansion phishing link"))
        registerRule(PhraseDangerRule("DNG_LON_05", "Fake Loan / Credit Card", listOf("dhani credit limit", "dhani loan approved", "dhani personal loan"), "Fake Dhani / credit service brand scam"))
        registerRule(PhraseDangerRule("DNG_LON_06", "Fake Loan / Credit Card", listOf("pre-approved personal loan", "personal loan offer ₹", "pre approved loan of rs"), "Deceptive high-value loan offer link"))
        registerRule(PhraseDangerRule("DNG_LON_07", "Fake Loan / Credit Card", listOf("zero interest loan", "interest free loan offer", "0% interest loan"), "Deceptive zero interest rate loan trap"))
        registerRule(PhraseDangerRule("DNG_LON_08", "Fake Loan / Credit Card", listOf("loan processing fee advance", "pay loan fee first", "advance processing fee for loan"), "Fake upfront loan processing fee scam"))
        registerRule(PhraseDangerRule("DNG_LON_09", "Fake Loan / Credit Card", listOf("credit score repair", "improve credit score instantly", "boost credit score link"), "Fake credit score repair service scam"))
        registerRule(PhraseDangerRule("DNG_LON_10", "Fake Loan / Credit Card", listOf("loan settlement discount", "settle loan at discount", "one-time settlement loan"), "Fake debt settlement discount offer"))

        // ==========================================
        // BATCH 3 DANGER RULES (101 - 150)
        // ==========================================

        // --- 1. FAKE COLLECT REQUEST (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_COL_01", "Fake Collect Request", listOf("accept payment to receive money", "accept to receive ₹", "accept to receive rs"), "Fake collect request claiming user receives funds"))
        registerRule(PhraseDangerRule("DNG_COL_02", "Fake Collect Request", listOf("approve collect request", "approve upi request to get", "approve collect request to receive"), "Deceptive request to approve a UPI debit"))
        registerRule(PhraseDangerRule("DNG_COL_03", "Fake Collect Request", listOf("receive money by accepting request", "accept request to get cashback", "accept request to get money"), "Fake receive-money-via-debit coercion"))
        registerRule(PhraseDangerRule("DNG_COL_04", "Fake Collect Request", listOf("tap accept to get cashback", "click accept to claim", "tap accept to claim"), "Deceptive click-accept-to-claim cashback trap"))
        registerRule(PhraseDangerRule("DNG_COL_05", "Fake Collect Request", listOf("approve upi request", "approve pending upi", "approve your pending upi"), "Deceptive UPI collect transaction authorization request"))
        registerRule(PhraseDangerRule("DNG_COL_06", "Fake Collect Request", listOf("accept pending payment", "approve pending payment", "accept payment request to receive"), "Deceptive request to approve pending debit"))
        registerRule(PhraseDangerRule("DNG_COL_07", "Fake Collect Request", listOf("receive ₹5000 instantly", "accept to receive ₹5000", "receive rs 5000 instantly"), "High-value fake cashback collect alert"))
        registerRule(PhraseDangerRule("DNG_COL_08", "Fake Collect Request", listOf("money waiting for you", "amount waiting to be received", "payment waiting for you"), "Deceptive claim of funds waiting on acceptance"))
        registerRule(PhraseDangerRule("DNG_COL_09", "Fake Collect Request", listOf("accept payment now", "accept payment link", "click accept to get payment"), "Deceptive call-to-action to accept debit"))
        registerRule(PhraseDangerRule("DNG_COL_10", "Fake Collect Request", listOf("payment pending acceptance", "upi payment pending acceptance", "collect request pending acceptance"), "Deceptive pending transaction notification"))

        // --- 2. QR CODE SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_QRC_01", "QR Code Scam", listOf("scan this qr to receive money", "scan qr to receive", "scan this qr code to receive money"), "Fake QR code scanning claiming user receives money"))
        registerRule(PhraseDangerRule("DNG_QRC_02", "QR Code Scam", listOf("scan qr to claim reward", "scan qr code to claim", "scan qr code to receive reward"), "Fake QR scan reward claim lure"))
        registerRule(PhraseDangerRule("DNG_QRC_03", "QR Code Scam", listOf("qr verification required", "verify qr code", "scan qr to verify"), "Deceptive dynamic qr verification request"))
        registerRule(PhraseDangerRule("DNG_QRC_04", "QR Code Scam", listOf("scan qr for prize", "scan qr to win", "scan qr code to win"), "Deceptive prize scanning lottery trap"))
        registerRule(PhraseDangerRule("DNG_QRC_05", "QR Code Scam", listOf("scan qr to activate account", "scan qr to unblock", "scan qr code to unblock"), "Deceptive account recovery scanning lure"))
        registerRule(PhraseDangerRule("DNG_QRC_06", "QR Code Scam", listOf("scan qr to receive refund", "scan qr for refund", "scan this qr to receive refund"), "Deceptive scan-to-receive-refund scam"))
        registerRule(PhraseDangerRule("DNG_QRC_07", "QR Code Scam", listOf("reward qr code", "cashback qr code", "scan qr reward"), "Deceptive reward qr code distribution"))
        registerRule(PhraseDangerRule("DNG_QRC_08", "QR Code Scam", listOf("scan to receive cashback", "scan qr code for cashback", "scan this qr to receive cashback"), "Deceptive scan-for-cashback bait"))
        registerRule(PhraseDangerRule("DNG_QRC_09", "QR Code Scam", listOf("payment verification qr", "verify payment qr", "verify scan qr"), "Deceptive payment clearance scan trap"))
        registerRule(PhraseDangerRule("DNG_QRC_10", "QR Code Scam", listOf("claim amount using qr", "claim reward qr", "scan qr to claim cashback"), "Deceptive claim ledger qr scanner bait"))

        // --- 3. FAKE REFUND SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_RFD_01", "Fake Refund Scam", listOf("refund pending", "refund is pending in your", "pending refund request"), "Fake refund pending clearance alert"))
        registerRule(PhraseDangerRule("DNG_RFD_02", "Fake Refund Scam", listOf("claim refund immediately", "claim refund link", "click to claim refund"), "Fake immediate refund payout click link"))
        registerRule(PhraseDangerRule("DNG_RFD_03", "Fake Refund Scam", listOf("refund waiting", "your refund is waiting", "refund is waiting for you"), "Fake unclaimed refund reminder"))
        registerRule(PhraseDangerRule("DNG_RFD_04", "Fake Refund Scam", listOf("refund verification", "verify refund account", "refund verification required"), "Fake refund account verification portal"))
        registerRule(PhraseDangerRule("DNG_RFD_05", "Fake Refund Scam", listOf("receive refund now", "get your refund instantly", "click here to get refund"), "Fake instant refund retrieval link"))
        registerRule(PhraseDangerRule("DNG_RFD_06", "Fake Refund Scam", listOf("refund expires today", "claim refund before it expires", "refund link expires"), "Fake refund expiration time pressure"))
        registerRule(PhraseDangerRule("DNG_RFD_07", "Fake Refund Scam", listOf("refund released", "refund amount released", "your refund has been released"), "Fake refund release transaction ledger link"))
        registerRule(PhraseDangerRule("DNG_RFD_08", "Fake Refund Scam", listOf("verify refund", "verify bank for refund", "verify account for refund"), "Fake bank credential refund check"))
        registerRule(PhraseDangerRule("DNG_RFD_09", "Fake Refund Scam", listOf("refund approval pending", "approve refund now", "refund approval required"), "Fake refund approval transaction debit"))
        registerRule(PhraseDangerRule("DNG_RFD_10", "Fake Refund Scam", listOf("refund processing fee", "pay processing fee for refund", "refund fee required"), "Fake advance-fee refund processing scam"))

        // --- 4. FAKE CASHBACK SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_CSH_01", "Fake Cashback Scam", listOf("cashback waiting", "your cashback is waiting", "cashback is waiting for you"), "Fake pending cashback collection alert"))
        registerRule(PhraseDangerRule("DNG_CSH_02", "Fake Cashback Scam", listOf("claim cashback", "claim cashback link", "click to claim cashback"), "Fake cashback redemption phishing link"))
        registerRule(PhraseDangerRule("DNG_CSH_03", "Fake Cashback Scam", listOf("exclusive cashback", "special cashback offer", "exclusive cashback reward"), "Fake premium loyalty cashback lure"))
        registerRule(PhraseDangerRule("DNG_CSH_04", "Fake Cashback Scam", listOf("festival cashback", "diwali cashback offer", "festive cashback reward"), "Fake seasonal festival cashback bait"))
        registerRule(PhraseDangerRule("DNG_CSH_05", "Fake Cashback Scam", listOf("free cashback", "get free cashback on", "free cashback reward"), "Fake unconditional free cashback lure"))
        registerRule(PhraseDangerRule("DNG_CSH_06", "Fake Cashback Scam", listOf("cashback expires today", "cashback expiring", "cashback offer expires"), "Fake cashback urgency pressure trap"))
        registerRule(PhraseDangerRule("DNG_CSH_07", "Fake Cashback Scam", listOf("special cashback reward", "super cashback offer", "mega cashback reward"), "Fake high-value cashback payout claim"))
        registerRule(PhraseDangerRule("DNG_CSH_08", "Fake Cashback Scam", listOf("bonus cashback", "paytm bonus cashback", "phonepe bonus cashback"), "Fake wallet brand bonus cashback scam"))
        registerRule(PhraseDangerRule("DNG_CSH_09", "Fake Cashback Scam", listOf("cashback unlocked", "scratch card cashback unlocked", "scratch card unlocked cashback"), "Fake scratch card cashback unlock lure"))
        registerRule(PhraseDangerRule("DNG_CSH_10", "Fake Cashback Scam", listOf("instant cashback", "get instant cashback of", "win instant cashback"), "Fake immediate cashback payout credit alert"))

        // --- 5. FAKE MERCHANT PAYMENT SCAM (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_MER_01", "Fake Merchant Payment Scam", listOf("merchant payment pending", "merchant payment is pending", "pending merchant payment"), "Fake merchant transaction clearance scam"))
        registerRule(PhraseDangerRule("DNG_MER_02", "Fake Merchant Payment Scam", listOf("payment verification required", "verify merchant payment", "merchant payment verification"), "Fake payment verification credential prompt"))
        registerRule(PhraseDangerRule("DNG_MER_03", "Fake Merchant Payment Scam", listOf("merchant verification pending", "verify merchant account", "merchant verification required"), "Fake merchant credentials verification trap"))
        registerRule(PhraseDangerRule("DNG_MER_04", "Fake Merchant Payment Scam", listOf("payment release required", "release payment to", "release pending payment to"), "Fake transaction hold release payment scam"))
        registerRule(PhraseDangerRule("DNG_MER_05", "Fake Merchant Payment Scam", listOf("payment approval pending", "approve payment request to", "approve pending merchant payment"), "Fake pending merchant payment debit"))
        registerRule(PhraseDangerRule("DNG_MER_06", "Fake Merchant Payment Scam", listOf("payment confirmation required", "confirm payment transaction", "confirm merchant payment"), "Fake transaction authorization confirmation"))
        registerRule(PhraseDangerRule("DNG_MER_07", "Fake Merchant Payment Scam", listOf("pending merchant transfer", "merchant transfer pending", "merchant transfer is pending"), "Fake commercial vendor credit ledger scam"))
        registerRule(PhraseDangerRule("DNG_MER_08", "Fake Merchant Payment Scam", listOf("receive merchant reward", "merchant voucher claim", "merchant reward voucher"), "Fake partner brand merchant voucher lure"))
        registerRule(PhraseDangerRule("DNG_MER_09", "Fake Merchant Payment Scam", listOf("merchant settlement link", "merchant settlement pending", "merchant payout pending"), "Fake B2B seller payout settlement scam"))
        registerRule(PhraseDangerRule("DNG_MER_10", "Fake Merchant Payment Scam", listOf("business payment verification", "verify business account", "verify business payment"), "Fake corporate merchant credential block"))

        // ==========================================
        // BATCH 4 DANGER RULES (151 - 200)
        // ==========================================

        // --- 1. FAKE KYC EXPIRY (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_KXP_01", "Fake KYC Expiry", listOf("your kyc has expired", "your kyc expired"), "Deceptive notification claiming KYC has expired"))
        registerRule(PhraseDangerRule("DNG_KXP_02", "Fake KYC Expiry", listOf("kyc expired today", "kyc expiring today"), "Deceptive urgency alert regarding same-day KYC expiration"))
        registerRule(PhraseDangerRule("DNG_KXP_03", "Fake KYC Expiry", listOf("complete kyc now", "complete kyc immediately"), "Deceptive pressure to complete KYC"))
        registerRule(PhraseDangerRule("DNG_KXP_04", "Fake KYC Expiry", listOf("last chance for kyc", "last chance to complete kyc"), "Deceptive warning claiming last opportunity for KYC"))
        registerRule(PhraseDangerRule("DNG_KXP_05", "Fake KYC Expiry", listOf("kyc failure detected", "kyc verification failed"), "Deceptive status claiming KYC failure"))
        registerRule(PhraseDangerRule("DNG_KXP_06", "Fake KYC Expiry", listOf("kyc pending urgently", "urgent kyc pending"), "Deceptive panic-inducing pending KYC status"))
        registerRule(PhraseDangerRule("DNG_KXP_07", "Fake KYC Expiry", listOf("update kyc immediately", "update kyc now"), "Deceptive command to immediately update KYC"))
        registerRule(PhraseDangerRule("DNG_KXP_08", "Fake KYC Expiry", listOf("final kyc reminder", "last kyc reminder"), "Deceptive notification of final KYC warning"))
        registerRule(PhraseDangerRule("DNG_KXP_09", "Fake KYC Expiry", listOf("kyc suspension notice", "kyc suspension warning"), "Deceptive threat of KYC-related account suspension"))
        registerRule(PhraseDangerRule("DNG_KXP_10", "Fake KYC Expiry", listOf("kyc deadline today", "kyc deadline expired"), "Deceptive warning about today's KYC deadline"))

        // --- 2. FAKE AADHAAR / PAN UPDATE (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_APU_01", "Fake Aadhaar / PAN Update", listOf("update aadhaar immediately", "update adhaar immediately"), "Deceptive request to immediately update Aadhaar details"))
        registerRule(PhraseDangerRule("DNG_APU_02", "Fake Aadhaar / PAN Update", listOf("pan verification pending", "pan card verification pending"), "Deceptive alert claiming PAN card verification is pending"))
        registerRule(PhraseDangerRule("DNG_APU_03", "Fake Aadhaar / PAN Update", listOf("aadhaar verification required", "adhaar verification required"), "Deceptive notification requiring Aadhaar verification"))
        registerRule(PhraseDangerRule("DNG_APU_04", "Fake Aadhaar / PAN Update", listOf("pan expired", "pan card expired"), "Deceptive claim that PAN has expired"))
        registerRule(PhraseDangerRule("DNG_APU_05", "Fake Aadhaar / PAN Update", listOf("link aadhaar now", "link adhaar card now"), "Deceptive command to link Aadhaar card"))
        registerRule(PhraseDangerRule("DNG_APU_06", "Fake Aadhaar / PAN Update", listOf("link pan now", "link pan card now"), "Deceptive command to link PAN card"))
        registerRule(PhraseDangerRule("DNG_APU_07", "Fake Aadhaar / PAN Update", listOf("identity verification failed", "id verification failed"), "Deceptive status claiming identity verification failure"))
        registerRule(PhraseDangerRule("DNG_APU_08", "Fake Aadhaar / PAN Update", listOf("aadhaar suspended", "aadhaar card suspended"), "Deceptive alert of Aadhaar suspension"))
        registerRule(PhraseDangerRule("DNG_APU_09", "Fake Aadhaar / PAN Update", listOf("pan blocked", "pan card blocked"), "Deceptive claim that PAN card is blocked"))
        registerRule(PhraseDangerRule("DNG_APU_10", "Fake Aadhaar / PAN Update", listOf("government verification pending", "govt verification pending"), "Deceptive claim of pending government verification"))

        // --- 3. FAKE ACCOUNT VERIFICATION (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_ACV_01", "Fake Account Verification", listOf("verify your identity now", "verify identity immediately"), "Deceptive request to verify identity"))
        registerRule(PhraseDangerRule("DNG_ACV_02", "Fake Account Verification", listOf("complete verification", "complete your verification now"), "Deceptive call-to-action to complete verification"))
        registerRule(PhraseDangerRule("DNG_ACV_03", "Fake Account Verification", listOf("identity check failed", "identity check is failed"), "Deceptive status claiming identity check has failed"))
        registerRule(PhraseDangerRule("DNG_ACV_04", "Fake Account Verification", listOf("customer verification pending", "cust verification pending"), "Deceptive claim of pending customer verification"))
        registerRule(PhraseDangerRule("DNG_ACV_05", "Fake Account Verification", listOf("account verification failed", "acc verification failed"), "Deceptive status claiming account verification failure"))
        registerRule(PhraseDangerRule("DNG_ACV_06", "Fake Account Verification", listOf("security verification needed", "security check required"), "Deceptive alert demanding security verification"))
        registerRule(PhraseDangerRule("DNG_ACV_07", "Fake Account Verification", listOf("document verification required", "doc verification required"), "Deceptive requirement for document verification"))
        registerRule(PhraseDangerRule("DNG_ACV_08", "Fake Account Verification", listOf("identity confirmation needed", "id confirmation required"), "Deceptive request for identity confirmation"))
        registerRule(PhraseDangerRule("DNG_ACV_09", "Fake Account Verification", listOf("re-verify your profile", "reverify profile now"), "Deceptive command to re-verify user profile"))
        registerRule(PhraseDangerRule("DNG_ACV_10", "Fake Account Verification", listOf("verification expires today", "verification link expires today"), "Deceptive urgency alert regarding verification expiration"))

        // --- 4. FAKE EKYC LINKS (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_EKY_01", "Fake eKYC Links", listOf("click here to complete ekyc", "click to complete e-kyc"), "Deceptive link for completing electronic KYC"))
        registerRule(PhraseDangerRule("DNG_EKY_02", "Fake eKYC Links", listOf("verify kyc using this link", "use this link to verify kyc"), "Deceptive link bait for KYC verification"))
        registerRule(PhraseDangerRule("DNG_EKY_03", "Fake eKYC Links", listOf("complete verification online", "complete your verification online"), "Deceptive online verification link"))
        registerRule(PhraseDangerRule("DNG_EKY_04", "Fake eKYC Links", listOf("secure kyc portal", "secure ekyc link"), "Deceptive claim of a secure KYC portal"))
        registerRule(PhraseDangerRule("DNG_EKY_05", "Fake eKYC Links", listOf("instant kyc verification", "instant kyc update"), "Deceptive instant KYC service lure"))
        registerRule(PhraseDangerRule("DNG_EKY_06", "Fake eKYC Links", listOf("identity verification portal", "id verification portal"), "Deceptive identity verification link bait"))
        registerRule(PhraseDangerRule("DNG_EKY_07", "Fake eKYC Links", listOf("customer verification link", "client verification link"), "Deceptive link to verify customer details"))
        registerRule(PhraseDangerRule("DNG_EKY_08", "Fake eKYC Links", listOf("update details here", "update kyc details here"), "Deceptive link to update confidential details"))
        registerRule(PhraseDangerRule("DNG_EKY_09", "Fake eKYC Links", listOf("submit documents now", "upload documents now"), "Deceptive call-to-action to upload private documents"))
        registerRule(PhraseDangerRule("DNG_EKY_10", "Fake eKYC Links", listOf("open verification page", "open verification link"), "Deceptive command to open a phishing verification page"))

        // --- 5. FAKE SUSPENSION WARNINGS (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_FSW_01", "Fake Suspension Warnings", listOf("account will be suspended", "your account will be suspended"), "Deceptive warning of account suspension"))
        registerRule(PhraseDangerRule("DNG_FSW_02", "Fake Suspension Warnings", listOf("services will stop", "your services will stop"), "Deceptive threat of service cessation"))
        registerRule(PhraseDangerRule("DNG_FSW_03", "Fake Suspension Warnings", listOf("profile blocked", "your profile has been blocked"), "Deceptive alert claiming user profile is blocked"))
        registerRule(PhraseDangerRule("DNG_FSW_04", "Fake Suspension Warnings", listOf("wallet suspended", "your wallet will be suspended"), "Deceptive threat of wallet suspension"))
        registerRule(PhraseDangerRule("DNG_FSW_05", "Fake Suspension Warnings", listOf("banking access suspended", "netbanking access suspended"), "Deceptive threat of netbanking block"))
        registerRule(PhraseDangerRule("DNG_FSW_06", "Fake Suspension Warnings", listOf("your id will be blocked", "your identity will be blocked"), "Deceptive threat of ID blocking"))
        registerRule(PhraseDangerRule("DNG_FSW_07", "Fake Suspension Warnings", listOf("verification failed", "verification is failed"), "Deceptive status claiming verification failure"))
        registerRule(PhraseDangerRule("DNG_FSW_08", "Fake Suspension Warnings", listOf("account disabled", "your account is disabled"), "Deceptive alert claiming account has been disabled"))
        registerRule(PhraseDangerRule("DNG_FSW_09", "Fake Suspension Warnings", listOf("profile restricted", "your profile is restricted"), "Deceptive alert of profile restrictions"))
        registerRule(PhraseDangerRule("DNG_FSW_10", "Fake Suspension Warnings", listOf("access denied until verification", "access is blocked until verification"), "Deceptive access block pending verification"))

        // ==========================================
        // BATCH 5 DANGER RULES (201 - 250)
        // ==========================================

        // --- 1. FAKE DELIVERY FAILURE (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_FDF_01", "Fake Delivery Failure", listOf("delivery failed due to incorrect address", "delivery failed incorrect address"), "Deceptive failure alert blaming incorrect address"))
        registerRule(PhraseDangerRule("DNG_FDF_02", "Fake Delivery Failure", listOf("package on hold", "your package is on hold"), "Deceptive package hold warning to force link clicks"))
        registerRule(PhraseDangerRule("DNG_FDF_03", "Fake Delivery Failure", listOf("parcel cannot be delivered", "parcel could not be delivered"), "Deceptive notification of non-delivery status"))
        registerRule(PhraseDangerRule("DNG_FDF_04", "Fake Delivery Failure", listOf("delivery suspended", "your delivery has been suspended"), "Deceptive threat of suspension of delivery services"))
        registerRule(PhraseDangerRule("DNG_FDF_05", "Fake Delivery Failure", listOf("final delivery attempt", "last delivery attempt"), "Deceptive urgency alert regarding final delivery attempt"))
        registerRule(PhraseDangerRule("DNG_FDF_06", "Fake Delivery Failure", listOf("delivery failed verify address", "delivery failed verify your address"), "Deceptive notice requiring immediate address verification"))
        registerRule(PhraseDangerRule("DNG_FDF_07", "Fake Delivery Failure", listOf("parcel waiting for confirmation", "package waiting for confirmation"), "Deceptive status claiming package is awaiting confirmation"))
        registerRule(PhraseDangerRule("DNG_FDF_08", "Fake Delivery Failure", listOf("address verification required", "verify your delivery address to"), "Deceptive prompt to verify/update shipping details"))
        registerRule(PhraseDangerRule("DNG_FDF_09", "Fake Delivery Failure", listOf("shipment blocked", "your shipment is blocked"), "Deceptive status claiming a shipment has been blocked"))
        registerRule(PhraseDangerRule("DNG_FDF_10", "Fake Delivery Failure", listOf("delivery pending confirmation", "pending delivery confirmation"), "Deceptive notice of pending delivery confirmation"))

        // --- 2. FAKE CUSTOMS & IMPORT FEE (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_CST_01", "Fake Customs & Import Fee", listOf("pay customs fee", "pay customs duty"), "Deceptive fee request to release international shipment"))
        registerRule(PhraseDangerRule("DNG_CST_02", "Fake Customs & Import Fee", listOf("import duty pending", "pending import duty"), "Deceptive claim of outstanding import fees/taxes"))
        registerRule(PhraseDangerRule("DNG_CST_03", "Fake Customs & Import Fee", listOf("clear customs now", "clear customs immediately"), "Deceptive urgency command to settle customs clearance"))
        registerRule(PhraseDangerRule("DNG_CST_04", "Fake Customs & Import Fee", listOf("international parcel waiting", "international package waiting"), "Deceptive bait claiming an international parcel is waiting"))
        registerRule(PhraseDangerRule("DNG_CST_05", "Fake Customs & Import Fee", listOf("package held at customs", "parcel held at customs"), "Deceptive alert claiming package is seized/held by customs"))
        registerRule(PhraseDangerRule("DNG_CST_06", "Fake Customs & Import Fee", listOf("customs clearance required", "customs verification required"), "Deceptive demand for customs clearance processing"))
        registerRule(PhraseDangerRule("DNG_CST_07", "Fake Customs & Import Fee", listOf("pay handling fee", "shipping handling fee"), "Deceptive small-amount advance fee for logistics handling"))
        registerRule(PhraseDangerRule("DNG_CST_08", "Fake Customs & Import Fee", listOf("parcel release payment", "package release payment"), "Deceptive requirement of payment to release a package"))
        registerRule(PhraseDangerRule("DNG_CST_09", "Fake Customs & Import Fee", listOf("border clearance pending", "border customs pending"), "Deceptive status of border clearance hold"))
        registerRule(PhraseDangerRule("DNG_CST_10", "Fake Customs & Import Fee", listOf("shipping tax required", "pay shipping tax"), "Deceptive demand for shipping/customs tax payment"))

        // --- 3. FAKE DELIVERY PAYMENT (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_FDP_01", "Fake Delivery Payment", listOf("pay delivery charge", "pay delivery charges"), "Deceptive request to pay an unexpected delivery charge"))
        registerRule(PhraseDangerRule("DNG_FDP_02", "Fake Delivery Payment", listOf("small delivery fee pending", "delivery fee pending"), "Deceptive advance fee request for package delivery"))
        registerRule(PhraseDangerRule("DNG_FDP_03", "Fake Delivery Payment", listOf("complete payment for delivery", "complete delivery payment"), "Deceptive demand to complete delivery payment"))
        registerRule(PhraseDangerRule("DNG_FDP_04", "Fake Delivery Payment", listOf("delivery fee required", "shipping fee required"), "Deceptive notification of mandatory delivery fee"))
        registerRule(PhraseDangerRule("DNG_FDP_05", "Fake Delivery Payment", listOf("parcel payment pending", "package payment pending"), "Deceptive claim of outstanding transaction fee for parcel"))
        registerRule(PhraseDangerRule("DNG_FDP_06", "Fake Delivery Payment", listOf("shipping payment required", "shipping charges required"), "Deceptive request for immediate shipping charges"))
        registerRule(PhraseDangerRule("DNG_FDP_07", "Fake Delivery Payment", listOf("processing fee for parcel", "processing fee for package"), "Deceptive processing fee requirement to release package"))
        registerRule(PhraseDangerRule("DNG_FDP_08", "Fake Delivery Payment", listOf("pay before delivery", "payment before delivery"), "Deceptive demand for advance payment prior to drop-off"))
        registerRule(PhraseDangerRule("DNG_FDP_09", "Fake Delivery Payment", listOf("courier payment pending", "pending courier payment"), "Deceptive billing prompt for courier handling"))
        registerRule(PhraseDangerRule("DNG_FDP_10", "Fake Delivery Payment", listOf("delivery cannot continue", "delivery cannot proceed"), "Deceptive notification of delivery halt due to pending fees"))

        // --- 4. FAKE COURIER VERIFICATION (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_FCV_01", "Fake Courier Verification", listOf("verify your delivery", "verify delivery details"), "Deceptive request to verify package delivery details"))
        registerRule(PhraseDangerRule("DNG_FCV_02", "Fake Courier Verification", listOf("confirm shipping details", "confirm your shipping details"), "Deceptive validation request for logistics details"))
        registerRule(PhraseDangerRule("DNG_FCV_03", "Fake Courier Verification", listOf("update delivery address", "update your delivery address"), "Deceptive prompt to redirect or correct shipping details"))
        registerRule(PhraseDangerRule("DNG_FCV_04", "Fake Courier Verification", listOf("delivery verification required", "delivery verify now"), "Deceptive verification trap for package drop-off"))
        registerRule(PhraseDangerRule("DNG_FCV_05", "Fake Courier Verification", listOf("courier verification pending", "courier status verification"), "Deceptive claim of pending courier verification"))
        registerRule(PhraseDangerRule("DNG_FCV_06", "Fake Courier Verification", listOf("package confirmation required", "parcel confirmation required"), "Deceptive demand to confirm package ownership/details"))
        registerRule(PhraseDangerRule("DNG_FCV_07", "Fake Courier Verification", listOf("shipment verification", "verify shipment details"), "Deceptive validation link for dynamic shipments"))
        registerRule(PhraseDangerRule("DNG_FCV_08", "Fake Courier Verification", listOf("delivery authentication", "authenticate delivery link"), "Deceptive requirement to authenticate recipient identity"))
        registerRule(PhraseDangerRule("DNG_FCV_09", "Fake Courier Verification", listOf("verify recipient", "recipient verification pending"), "Deceptive identity check on recipient"))
        registerRule(PhraseDangerRule("DNG_FCV_10", "Fake Courier Verification", listOf("confirm parcel ownership", "verify parcel ownership"), "Deceptive demand for ownership validation of held goods"))

        // --- 5. FAKE TRACKING LINKS (10 Rules) ---
        registerRule(PhraseDangerRule("DNG_FTL_01", "Fake Tracking Links", listOf("track your parcel here", "track parcel here"), "Deceptive tracking link directing to phishing forms"))
        registerRule(PhraseDangerRule("DNG_FTL_02", "Fake Tracking Links", listOf("open tracking link", "open tracking page"), "Deceptive click-to-open tracking portal request"))
        registerRule(PhraseDangerRule("DNG_FTL_03", "Fake Tracking Links", listOf("click to track package", "click to track parcel"), "Deceptive link bait claiming tracking status update"))
        registerRule(PhraseDangerRule("DNG_FTL_04", "Fake Tracking Links", listOf("delivery tracking portal", "courier tracking portal"), "Deceptive tracking portal login credential trap"))
        registerRule(PhraseDangerRule("DNG_FTL_05", "Fake Tracking Links", listOf("shipment verification page", "shipment tracking page"), "Deceptive landing page link to capture user details"))
        registerRule(PhraseDangerRule("DNG_FTL_06", "Fake Tracking Links", listOf("parcel status update", "package status update"), "Deceptive tracking update notification"))
        registerRule(PhraseDangerRule("DNG_FTL_07", "Fake Tracking Links", listOf("track shipment immediately", "track parcel immediately"), "Deceptive hyper-urgency to check fake shipment status"))
        registerRule(PhraseDangerRule("DNG_FTL_08", "Fake Tracking Links", listOf("secure delivery link", "secure tracking link"), "Deceptive trust-badged delivery link redirection"))
        registerRule(PhraseDangerRule("DNG_FTL_09", "Fake Tracking Links", listOf("courier tracking confirmation", "confirm courier tracking"), "Deceptive tracking code confirmation request"))
        registerRule(PhraseDangerRule("DNG_FTL_10", "Fake Tracking Links", listOf("delivery portal login", "courier portal login"), "Deceptive credentials harvesting login form"))

        // ==========================================
        // BATCH 6 DANGER RULES (251 - 350)
        // ==========================================

        // --- 1. APK INSTALLATION SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_APK_01", "APK Installation Scam", listOf("download apk", "download the apk"), "Request to download a suspicious Android Package (APK)"))
        registerRule(PhraseDangerRule("DNG_APK_02", "APK Installation Scam", listOf("install apk", "install the apk"), "Request to install an untrusted Android Package (APK)"))
        registerRule(PhraseDangerRule("DNG_APK_03", "APK Installation Scam", listOf("install security app", "install security application"), "Deceptive instruction to install a fake security application"))
        registerRule(PhraseDangerRule("DNG_APK_04", "APK Installation Scam", listOf("install banking app", "install banking application"), "Deceptive instruction to install a malicious banking application"))
        registerRule(PhraseDangerRule("DNG_APK_05", "APK Installation Scam", listOf("install update manually", "manually install update"), "Request to bypass app store and manually install an update"))
        registerRule(PhraseDangerRule("DNG_APK_06", "APK Installation Scam", listOf("download verification app", "download verification application"), "Deceptive requirement to download an app for verification"))
        registerRule(PhraseDangerRule("DNG_APK_07", "APK Installation Scam", listOf("install latest version", "install latest version of"), "Deceptive prompt to install an unverified app version"))
        registerRule(PhraseDangerRule("DNG_APK_08", "APK Installation Scam", listOf("download attachment", "download the attachment"), "Deceptive request to download an email or message attachment"))
        registerRule(PhraseDangerRule("DNG_APK_09", "APK Installation Scam", listOf("install from this link", "install using this link"), "Dangerous request to install software from an external link"))
        registerRule(PhraseDangerRule("DNG_APK_10", "APK Installation Scam", listOf("unknown application required", "unknown app required"), "Claim that an unknown application must be installed"))
        registerRule(PhraseDangerRule("DNG_APK_11", "APK Installation Scam", listOf("download android package", "install android package"), "Deceptive instructions involving raw Android Package installation"))
        registerRule(PhraseDangerRule("DNG_APK_12", "APK Installation Scam", listOf("install customer app", "download customer app"), "Phishing instructions to install a custom client application"))
        registerRule(PhraseDangerRule("DNG_APK_13", "APK Installation Scam", listOf("get apk", "get the apk file"), "Luring user to fetch and run a raw apk executable"))
        registerRule(PhraseDangerRule("DNG_APK_14", "APK Installation Scam", listOf("download certificate app", "install certificate app"), "Deceptive prompt to install a fraudulent security certificate app"))
        registerRule(PhraseDangerRule("DNG_APK_15", "APK Installation Scam", listOf("install support application", "download support application"), "Fraudulent request to download remote support utilities"))
        registerRule(PhraseDangerRule("DNG_APK_16", "APK Installation Scam", listOf("download portal app", "install portal app"), "Deceptive guidance to install portal applications"))
        registerRule(PhraseDangerRule("DNG_APK_17", "APK Installation Scam", listOf("install kyc app", "download kyc app"), "Fraudulent prompt to install an app specifically for KYC"))
        registerRule(PhraseDangerRule("DNG_APK_18", "APK Installation Scam", listOf("install utility app", "download utility app"), "Prompt to install unauthorized utilities"))
        registerRule(PhraseDangerRule("DNG_APK_19", "APK Installation Scam", listOf("install helper app", "download helper app"), "Request to install a suspicious sidecar/helper application"))
        registerRule(PhraseDangerRule("DNG_APK_20", "APK Installation Scam", listOf("manually download app", "manually download application"), "Instruction to bypass standard app stores"))

        // --- 2. REMOTE ACCESS SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_REM_01", "Remote Access Scam", listOf("install anydesk", "download anydesk"), "Instruction to install AnyDesk remote desktop software"))
        registerRule(PhraseDangerRule("DNG_REM_02", "Remote Access Scam", listOf("install teamviewer", "download teamviewer"), "Instruction to install TeamViewer remote access tool"))
        registerRule(PhraseDangerRule("DNG_REM_03", "Remote Access Scam", listOf("install quicksupport", "download quicksupport"), "Instruction to install TeamViewer QuickSupport"))
        registerRule(PhraseDangerRule("DNG_REM_04", "Remote Access Scam", listOf("install rustdesk", "download rustdesk"), "Instruction to install RustDesk open-source remote access software"))
        registerRule(PhraseDangerRule("DNG_REM_05", "Remote Access Scam", listOf("install remote support", "download remote support"), "Suspicious request to download remote support tools"))
        registerRule(PhraseDangerRule("DNG_REM_06", "Remote Access Scam", listOf("allow screen sharing", "enable screen sharing"), "Dangerous request to allow remote desktop screen sharing"))
        registerRule(PhraseDangerRule("DNG_REM_07", "Remote Access Scam", listOf("give remote access", "grant remote access"), "Deceptive prompt to grant remote network/device access"))
        registerRule(PhraseDangerRule("DNG_REM_08", "Remote Access Scam", listOf("share your screen", "share screen now"), "Direct instruction to share display with customer representative"))
        registerRule(PhraseDangerRule("DNG_REM_09", "Remote Access Scam", listOf("start remote session", "begin remote session"), "Initiation of a deceptive remote support control session"))
        registerRule(PhraseDangerRule("DNG_REM_10", "Remote Access Scam", listOf("customer support remote access", "support remote access"), "Fraudulent request for remote access by claiming to be customer support"))
        registerRule(PhraseDangerRule("DNG_REM_11", "Remote Access Scam", listOf("install screen sharing", "download screen sharing"), "Dangerous instruction to install screen broadcasting tools"))
        registerRule(PhraseDangerRule("DNG_REM_12", "Remote Access Scam", listOf("share screen control", "allow screen control"), "Dangerous prompt to hand over screen touch or input controls"))
        registerRule(PhraseDangerRule("DNG_REM_13", "Remote Access Scam", listOf("install control app", "download control app"), "Deceptive instruction to download device management/control apps"))
        registerRule(PhraseDangerRule("DNG_REM_14", "Remote Access Scam", listOf("install screen viewer", "download screen viewer"), "Suspicious request to download remote screen viewers"))
        registerRule(PhraseDangerRule("DNG_REM_15", "Remote Access Scam", listOf("start anydesk session", "open anydesk"), "Direct command to initiate a dangerous AnyDesk connection"))
        registerRule(PhraseDangerRule("DNG_REM_16", "Remote Access Scam", listOf("start teamviewer session", "open teamviewer"), "Direct command to initiate a dangerous TeamViewer connection"))
        registerRule(PhraseDangerRule("DNG_REM_17", "Remote Access Scam", listOf("grant remote control", "allow remote control"), "Deceptive prompt to hand over operating system control"))
        registerRule(PhraseDangerRule("DNG_REM_18", "Remote Access Scam", listOf("remote assistance tool", "remote support tool"), "Suspicious reference to third-party remote control applications"))
        registerRule(PhraseDangerRule("DNG_REM_19", "Remote Access Scam", listOf("share screen password", "share screen code"), "Dangerous request to share the connection code or password of a remote access tool"))
        registerRule(PhraseDangerRule("DNG_REM_20", "Remote Access Scam", listOf("provide remote access code", "give remote access code"), "Deceptive request for remote connection passwords"))

        // --- 3. FAKE CUSTOMER CARE SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_FCC_01", "Fake Customer Care Scam", listOf("bank executive speaking", "i am bank executive"), "Deceptive claim of representing a bank over SMS/chat"))
        registerRule(PhraseDangerRule("DNG_FCC_02", "Fake Customer Care Scam", listOf("customer support verification", "customer care verification"), "Deceptive prompt for customer service identity verification"))
        registerRule(PhraseDangerRule("DNG_FCC_03", "Fake Customer Care Scam", listOf("technical support required", "tech support required"), "Deceptive claim that technical support intervention is mandatory"))
        registerRule(PhraseDangerRule("DNG_FCC_04", "Fake Customer Care Scam", listOf("refund officer", "payout officer"), "Social engineering impersonating a financial refund agent"))
        registerRule(PhraseDangerRule("DNG_FCC_05", "Fake Customer Care Scam", listOf("service engineer", "service technician"), "Social engineering impersonating a technical service technician"))
        registerRule(PhraseDangerRule("DNG_FCC_06", "Fake Customer Care Scam", listOf("account recovery team", "profile recovery team"), "Deceptive message impersonating an account recovery group"))
        registerRule(PhraseDangerRule("DNG_FCC_07", "Fake Customer Care Scam", listOf("security department", "bank security department"), "Impersonating the bank's security or fraud prevention department"))
        registerRule(PhraseDangerRule("DNG_FCC_08", "Fake Customer Care Scam", listOf("verification executive", "verification officer"), "Impersonation of a verification agent to capture private info"))
        registerRule(PhraseDangerRule("DNG_FCC_09", "Fake Customer Care Scam", listOf("help desk confirmation", "support desk confirmation"), "Deceptive confirmation from fake helpline helpdesk"))
        registerRule(PhraseDangerRule("DNG_FCC_10", "Fake Customer Care Scam", listOf("fraud investigation team", "cyber cell executive"), "Social engineering claiming to investigate fraud to steal details"))
        registerRule(PhraseDangerRule("DNG_FCC_11", "Fake Customer Care Scam", listOf("talk to support executive", "connect with executive"), "Deceptive redirection to call a fraudulent support phone number"))
        registerRule(PhraseDangerRule("DNG_FCC_12", "Fake Customer Care Scam", listOf("call helpline number", "call toll free"), "Deceptive push to call a fake customer service helpline"))
        registerRule(PhraseDangerRule("DNG_FCC_13", "Fake Customer Care Scam", listOf("authorized customer support", "authorized support desk"), "Deceptive claiming of authorized helpdesk assistance"))
        registerRule(PhraseDangerRule("DNG_FCC_14", "Fake Customer Care Scam", listOf("telecom executive", "telecommunication executive"), "Impersonating telecom providers' support desk"))
        registerRule(PhraseDangerRule("DNG_FCC_15", "Fake Customer Care Scam", listOf("officer will call you", "executive will call you"), "Scammer warning of an upcoming social engineering voice call"))
        registerRule(PhraseDangerRule("DNG_FCC_16", "Fake Customer Care Scam", listOf("contact support manager", "contact service manager"), "Deceptive advice to contact a fake manager"))
        registerRule(PhraseDangerRule("DNG_FCC_17", "Fake Customer Care Scam", listOf("head office verification", "main branch verification"), "Deceptive escalation claims about central bank verification"))
        registerRule(PhraseDangerRule("DNG_FCC_18", "Fake Customer Care Scam", listOf("system administrator support", "admin support desk"), "Impersonating network or software admin support staff"))
        registerRule(PhraseDangerRule("DNG_FCC_19", "Fake Customer Care Scam", listOf("card verification officer", "credit card executive"), "Deceptive agent claiming card processing issues"))
        registerRule(PhraseDangerRule("DNG_FCC_20", "Fake Customer Care Scam", listOf("fraud block department", "security block department"), "Impersonation of fraud monitoring and prevention teams"))

        // --- 4. DEVICE CONTROL SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_DCS_01", "Device Control Scam", listOf("allow accessibility permission", "enable accessibility"), "Highly dangerous instruction to enable Android Accessibility services"))
        registerRule(PhraseDangerRule("DNG_DCS_02", "Device Control Scam", listOf("enable unknown sources", "allow unknown sources"), "Dangerous instruction to allow app installations from untrusted origins"))
        registerRule(PhraseDangerRule("DNG_DCS_03", "Device Control Scam", listOf("enable developer mode", "enable developer options"), "Dangerous prompt to activate developer debugging features"))
        registerRule(PhraseDangerRule("DNG_DCS_04", "Device Control Scam", listOf("disable play protect", "turn off play protect"), "Extremely high-risk command to disable Google Play Protect antivirus"))
        registerRule(PhraseDangerRule("DNG_DCS_05", "Device Control Scam", listOf("allow installation", "permit installation"), "Deceptive command to approve sideloading of malicious packages"))
        registerRule(PhraseDangerRule("DNG_DCS_06", "Device Control Scam", listOf("grant admin permission", "grant administrator permission"), "Dangerous prompt to allow Android Device Administrator controls"))
        registerRule(PhraseDangerRule("DNG_DCS_07", "Device Control Scam", listOf("allow full device control", "grant full control"), "Extremely dangerous request for total administrative access to OS"))
        registerRule(PhraseDangerRule("DNG_DCS_08", "Device Control Scam", listOf("enable screen recording", "allow screen recording"), "Deceptive instruction to authorize capturing on-screen inputs"))
        registerRule(PhraseDangerRule("DNG_DCS_09", "Device Control Scam", listOf("share display", "share screen display"), "Deceptive request to broadcast display outputs"))
        registerRule(PhraseDangerRule("DNG_DCS_10", "Device Control Scam", listOf("grant permission", "grant all permissions"), "Suspicious command to bypass security prompts by granting all permissions"))
        registerRule(PhraseDangerRule("DNG_DCS_11", "Device Control Scam", listOf("allow draw over other apps", "enable overlay"), "Request to enable screen overlay permissions, facilitating clickjacking"))
        registerRule(PhraseDangerRule("DNG_DCS_12", "Device Control Scam", listOf("grant sms access", "allow sms permission"), "Dangerous instruction to allow reading or interception of incoming SMS/OTPs"))
        registerRule(PhraseDangerRule("DNG_DCS_13", "Device Control Scam", listOf("allow notification access", "enable notification access"), "Request to intercept notifications, exposing secret tokens and PINs"))
        registerRule(PhraseDangerRule("DNG_DCS_14", "Device Control Scam", listOf("bypass security warning", "ignore security warning"), "Instruction to disregard OS or browser security alerts"))
        registerRule(PhraseDangerRule("DNG_DCS_15", "Device Control Scam", listOf("disable antivirus", "turn off antivirus"), "Extremely high-risk instruction to disable security scanners"))
        registerRule(PhraseDangerRule("DNG_DCS_16", "Device Control Scam", listOf("allow background activity", "enable background running"), "Deceptive request to run malware persistently in the background"))
        registerRule(PhraseDangerRule("DNG_DCS_17", "Device Control Scam", listOf("trust this enterprise certificate", "install enterprise certificate"), "Request to install rogue security roots to intercept HTTPS network traffic"))
        registerRule(PhraseDangerRule("DNG_DCS_18", "Device Control Scam", listOf("grant media projection", "start media projection"), "Deceptive prompt to enable Android's media projection API"))
        registerRule(PhraseDangerRule("DNG_DCS_19", "Device Control Scam", listOf("allow remote monitoring", "enable monitoring"), "Deceptive request to allow real-time device tracking"))
        registerRule(PhraseDangerRule("DNG_DCS_20", "Device Control Scam", listOf("allow screen control permission", "grant control permission"), "Deceptive command to allow remote clicks or inputs"))

        // --- 5. FAKE APP UPDATE SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_FAU_01", "Fake App Update Scam", listOf("update banking app", "banking app update"), "Deceptive alert insisting on updating a bank application outside the Play Store"))
        registerRule(PhraseDangerRule("DNG_FAU_02", "Fake App Update Scam", listOf("mandatory update", "mandatory app update"), "Deceptive claim that a certain app has a critical, mandatory update"))
        registerRule(PhraseDangerRule("DNG_FAU_03", "Fake App Update Scam", listOf("security update", "security patch update"), "Using fake security updates to coerce users into sideloading malware"))
        registerRule(PhraseDangerRule("DNG_FAU_04", "Fake App Update Scam", listOf("update immediately", "update app immediately"), "Deceptive urgency surrounding application updates"))
        registerRule(PhraseDangerRule("DNG_FAU_05", "Fake App Update Scam", listOf("install latest app", "install new version of app"), "Request to install a fresh application version from unverified URLs"))
        registerRule(PhraseDangerRule("DNG_FAU_06", "Fake App Update Scam", listOf("download latest version", "download new version"), "Deceptive push to download updated packages from external servers"))
        registerRule(PhraseDangerRule("DNG_FAU_07", "Fake App Update Scam", listOf("app verification update", "verify and update app"), "Deceptive request to run a verification utility to update programs"))
        registerRule(PhraseDangerRule("DNG_FAU_08", "Fake App Update Scam", listOf("critical update", "critical application update"), "Creating alarm around crucial system or application software patches"))
        registerRule(PhraseDangerRule("DNG_FAU_09", "Fake App Update Scam", listOf("update wallet app", "update payment app"), "Deceptive requirement to upgrade money or digital payment apps"))
        registerRule(PhraseDangerRule("DNG_FAU_10", "Fake App Update Scam", listOf("update payment app", "update upi app"), "Fraudulent upgrade instruction focusing on financial UPI portals"))
        registerRule(PhraseDangerRule("DNG_FAU_11", "Fake App Update Scam", listOf("install software update", "install system update"), "Phishing system updates to load spyware or adware"))
        registerRule(PhraseDangerRule("DNG_FAU_12", "Fake App Update Scam", listOf("force update", "force app update"), "Deceptive claim of blocked access unless user updates immediately"))
        registerRule(PhraseDangerRule("DNG_FAU_13", "Fake App Update Scam", listOf("download security update", "get security update"), "Luring users to get an external file framed as a security patch"))
        registerRule(PhraseDangerRule("DNG_FAU_14", "Fake App Update Scam", listOf("manually update app", "manually update application"), "Instruction to manually update apps using direct files"))
        registerRule(PhraseDangerRule("DNG_FAU_15", "Fake App Update Scam", listOf("upgrade browser now", "update browser now"), "Deceptive alert saying the browser is out-of-date to deliver trojans"))
        registerRule(PhraseDangerRule("DNG_FAU_16", "Fake App Update Scam", listOf("essential update", "essential app update"), "Deceptive claim that an update is essential for continuing use"))
        registerRule(PhraseDangerRule("DNG_FAU_17", "Fake App Update Scam", listOf("account security update", "profile security update"), "Fake updates purported to protect account information"))
        registerRule(PhraseDangerRule("DNG_FAU_18", "Fake App Update Scam", listOf("download update package", "install update package"), "Deceptive instructions to manually apply packages"))
        registerRule(PhraseDangerRule("DNG_FAU_19", "Fake App Update Scam", listOf("update support app", "upgrade support app"), "Fraudulent instructions to update assistant/support utilities"))
        registerRule(PhraseDangerRule("DNG_FAU_20", "Fake App Update Scam", listOf("patch update file", "install hotfix"), "Deceptive installation of direct software fixes"))

        // ==========================================
        // BATCH 7 DANGER RULES (351 - 450)
        // ==========================================

        // --- 1. INVESTMENT SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_INV_01", "Investment Scam", listOf("guaranteed returns", "guaranteed investment returns"), "Deceptive guarantee of high investment returns", weight = 80))
        registerRule(PhraseDangerRule("DNG_INV_02", "Investment Scam", listOf("double your money", "double your investment"), "Deceptive promise to double the user's money", weight = 100))
        registerRule(PhraseDangerRule("DNG_INV_03", "Investment Scam", listOf("earn 5% daily", "5% daily profit"), "Deceptive promise of unrealistic daily profit rate", weight = 100))
        registerRule(PhraseDangerRule("DNG_INV_04", "Investment Scam", listOf("risk free investment", "risk-free investment"), "Deceptive claim of zero risk in investment", weight = 60))
        registerRule(PhraseDangerRule("DNG_INV_05", "Investment Scam", listOf("100% profit", "100% guaranteed profit"), "Deceptive promise of absolute certain profit", weight = 80))
        registerRule(PhraseDangerRule("DNG_INV_06", "Investment Scam", listOf("high return investment", "high returns guaranteed"), "Deceptive promise of guaranteed high yield returns", weight = 60))
        registerRule(PhraseDangerRule("DNG_INV_07", "Investment Scam", listOf("limited investment offer", "limited-time investment"), "Deceptive rush/urgency around investment sign up", weight = 40))
        registerRule(PhraseDangerRule("DNG_INV_08", "Investment Scam", listOf("investment expires today", "investment plan expires"), "Deceptive urgency tactic on fake investment closing", weight = 40))
        registerRule(PhraseDangerRule("DNG_INV_09", "Investment Scam", listOf("multiply your money", "multiply your wealth"), "Deceptive pitch promising wealth multiplication", weight = 40))
        registerRule(PhraseDangerRule("DNG_INV_10", "Investment Scam", listOf("become rich quickly", "get rich quick"), "Social engineering bait promoting quick riches", weight = 40))
        registerRule(PhraseDangerRule("DNG_INV_11", "Investment Scam", listOf("unprecedented gains", "unprecedented returns"), "Unrealistic market return promises", weight = 60))
        registerRule(PhraseDangerRule("DNG_INV_12", "Investment Scam", listOf("no risk profit", "zero risk profit"), "Deceptive claim of completely risk-free gains", weight = 80))
        registerRule(PhraseDangerRule("DNG_INV_13", "Investment Scam", listOf("triple your deposit", "triple your money"), "Extremely suspicious multiplier scam", weight = 100))
        registerRule(PhraseDangerRule("DNG_INV_14", "Investment Scam", listOf("passive income guarantee", "guaranteed passive income"), "Baiting users with guaranteed passive revenue", weight = 80))
        registerRule(PhraseDangerRule("DNG_INV_15", "Investment Scam", listOf("fixed daily profit", "guaranteed daily profit"), "Unrealistic daily payout investment scam", weight = 100))
        registerRule(PhraseDangerRule("DNG_INV_16", "Investment Scam", listOf("secure high returns", "safe high yield"), "Baiting users with secure high-yield assets", weight = 60))
        registerRule(PhraseDangerRule("DNG_INV_17", "Investment Scam", listOf("exclusive investment club", "private investment opportunity"), "Exclusive circle/club membership investment bait", weight = 60))
        registerRule(PhraseDangerRule("DNG_INV_18", "Investment Scam", listOf("retire rich early", "retire early wealth"), "Retirement financial freedom scam scheme", weight = 40))
        registerRule(PhraseDangerRule("DNG_INV_19", "Investment Scam", listOf("secret investment formula", "investment loophole"), "Scam claiming to exploit a secret financial loophole", weight = 80))
        registerRule(PhraseDangerRule("DNG_INV_20", "Investment Scam", listOf("wealth generator system", "automatic wealth builder"), "Deceptive promotion of automated wealth engines", weight = 100))

        // --- 2. CRYPTO SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_CRY_01", "Crypto Scam", listOf("crypto giveaway", "free crypto giveaway"), "Fraudulent free cryptocurrency giveaway", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_02", "Crypto Scam", listOf("bitcoin reward", "free bitcoin reward"), "Fraudulent free bitcoin reward trap", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_03", "Crypto Scam", listOf("usdt bonus", "free usdt reward"), "Deceptive USD Tether bonus promotion", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_04", "Crypto Scam", listOf("crypto investment plan", "cryptocurrency investment plan"), "High risk crypto multiplying/investment plan", weight = 80))
        registerRule(PhraseDangerRule("DNG_CRY_05", "Crypto Scam", listOf("wallet verification", "verify your crypto wallet"), "Phishing credentials for cryptocurrency wallets", weight = 40))
        registerRule(PhraseDangerRule("DNG_CRY_06", "Crypto Scam", listOf("free bitcoin", "free btc"), "Deceptive free Bitcoin baiting", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_07", "Crypto Scam", listOf("ethereum reward", "free ethereum"), "Fraudulent free Ethereum reward", weight = 60))
        registerRule(PhraseDangerRule("DNG_CRY_08", "Crypto Scam", listOf("claim crypto", "claim your cryptocurrency"), "Unverified platform crypto release link", weight = 60))
        registerRule(PhraseDangerRule("DNG_CRY_09", "Crypto Scam", listOf("crypto mining reward", "cloud mining reward"), "Deceptive cloud/hardware mining returns", weight = 60))
        registerRule(PhraseDangerRule("DNG_CRY_10", "Crypto Scam", listOf("airdrop reward", "crypto airdrop claim"), "Unverified and dangerous crypto airdrop scheme", weight = 40))
        registerRule(PhraseDangerRule("DNG_CRY_11", "Crypto Scam", listOf("crypto doubling site", "double your bitcoin"), "Classic cryptocurrency doubling website scam", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_12", "Crypto Scam", listOf("trust wallet authorization", "trust wallet verification"), "Phishing targeting Trust Wallet credentials", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_13", "Crypto Scam", listOf("metamask update required", "metamask verification"), "Phishing targeting Metamask seed phrases", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_14", "Crypto Scam", listOf("secure your seed phrase", "restore seed phrase"), "High-risk social engineering targeting recovery seed phrase", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_15", "Crypto Scam", listOf("unlocked token airdrop", "exclusive token claim"), "Deceptive token distribution phishing", weight = 80))
        registerRule(PhraseDangerRule("DNG_CRY_16", "Crypto Scam", listOf("guaranteed crypto returns", "passive crypto income"), "Guaranteed returns scam based on cryptocurrency", weight = 80))
        registerRule(PhraseDangerRule("DNG_CRY_17", "Crypto Scam", listOf("claim free shiba", "doge giveaway"), "Meme coin giveaway scams on social media", weight = 100))
        registerRule(PhraseDangerRule("DNG_CRY_18", "Crypto Scam", listOf("defi staking high yield", "high yield staking pool"), "High-yield investment program under DeFi staging guise", weight = 60))
        registerRule(PhraseDangerRule("DNG_CRY_19", "Crypto Scam", listOf("uniswap bonus pool", "pancakeswap giveaway"), "Exploiting decentralized exchange names for fake rewards", weight = 80))
        registerRule(PhraseDangerRule("DNG_CRY_20", "Crypto Scam", listOf("crypto cashout system", "withdraw crypto earnings"), "Scam requiring advance fee to withdraw fictional crypto balances", weight = 40))

        // --- 3. TRADING SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_TRD_01", "Trading Scam", listOf("join vip trading group", "vip telegram signals"), "Luring users into closed VIP signal channels", weight = 80))
        registerRule(PhraseDangerRule("DNG_TRD_02", "Trading Scam", listOf("guaranteed trading signal", "guaranteed accuracy signal"), "Deceptive guaranteed accuracy trading signal", weight = 100))
        registerRule(PhraseDangerRule("DNG_TRD_03", "Trading Scam", listOf("forex profit guaranteed", "forex guaranteed returns"), "Deceptive forex market returns", weight = 100))
        registerRule(PhraseDangerRule("DNG_TRD_04", "Trading Scam", listOf("stock tips guaranteed", "guaranteed stock tips"), "Guaranteed success in stock pick manipulation", weight = 40))
        registerRule(PhraseDangerRule("DNG_TRD_05", "Trading Scam", listOf("daily trading income", "guaranteed trading income"), "Deceptive promise of regular market income", weight = 80))
        registerRule(PhraseDangerRule("DNG_TRD_06", "Trading Scam", listOf("become millionaire", "become a millionaire today"), "Outrageous claims of rapid financial status elevation", weight = 80))
        registerRule(PhraseDangerRule("DNG_TRD_07", "Trading Scam", listOf("premium trading signal", "insider trading signals"), "Baiting users with inside or premium trading suggestions", weight = 40))
        registerRule(PhraseDangerRule("DNG_TRD_08", "Trading Scam", listOf("trading bonus", "free trading margin"), "Deceptive brokers offering high leveraged trading margins", weight = 40))
        registerRule(PhraseDangerRule("DNG_TRD_09", "Trading Scam", listOf("zero risk trading", "no risk trading"), "Deceptive statement claiming trading contains zero risk", weight = 60))
        registerRule(PhraseDangerRule("DNG_TRD_10", "Trading Scam", listOf("investment mentor", "professional trading mentor"), "Deceptive social engineering claiming a personal trading guide", weight = 40))
        registerRule(PhraseDangerRule("DNG_TRD_11", "Trading Scam", listOf("insider trading tip", "confidential stock tip"), "Social engineering offering confidential/inside information", weight = 100))
        registerRule(PhraseDangerRule("DNG_TRD_12", "Trading Scam", listOf("automated trading bot profit", "guaranteed bot profits"), "Fake algorithmic trading bot promising zero losses", weight = 80))
        registerRule(PhraseDangerRule("DNG_TRD_13", "Trading Scam", listOf("binary options cheat", "binary options winning formula"), "Promoting binary options schemes with false win guarantees", weight = 100))
        registerRule(PhraseDangerRule("DNG_TRD_14", "Trading Scam", listOf("exclusive trading class", "vip trading course"), "Selling overpriced courses under fake professional credentials", weight = 60))
        registerRule(PhraseDangerRule("DNG_TRD_15", "Trading Scam", listOf("100% accurate forex", "100% accurate signal"), "Claim of a flawless trading signal model", weight = 80))
        registerRule(PhraseDangerRule("DNG_TRD_16", "Trading Scam", listOf("multiply forex deposit", "forex leverage secret"), "Coercing deposits with high leverage forex multipliers", weight = 100))
        registerRule(PhraseDangerRule("DNG_TRD_17", "Trading Scam", listOf("trading loophole", "arbitrage loophole profit"), "Promoting non-existent system arbitrage loops", weight = 80))
        registerRule(PhraseDangerRule("DNG_TRD_18", "Trading Scam", listOf("join pump channel", "pump and dump group"), "Urging users to join highly volatile asset pumps", weight = 60))
        registerRule(PhraseDangerRule("DNG_TRD_19", "Trading Scam", listOf("secret trading strategy", "unbeatable trading algorithm"), "Selling unbeatable trading models", weight = 60))
        registerRule(PhraseDangerRule("DNG_TRD_20", "Trading Scam", listOf("free signal trial", "guaranteed trading wins"), "Low-commitment hook leading to paid subscription trap", weight = 40))

        // --- 4. LOAN SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_LNS_01", "Loan Scam", listOf("instant loan approval", "loan approved instantly"), "Deceptive high-urgency loan approval", weight = 100))
        registerRule(PhraseDangerRule("DNG_LNS_02", "Loan Scam", listOf("no documents loan", "loan without any documents"), "Deceptive low-barrier loan with zero validation", weight = 60))
        registerRule(PhraseDangerRule("DNG_LNS_03", "Loan Scam", listOf("loan without verification", "no verification loan"), "Deceptive zero check loan application", weight = 60))
        registerRule(PhraseDangerRule("DNG_LNS_04", "Loan Scam", listOf("loan approved instantly", "instant approval loan"), "Fast automated loan approval traps", weight = 80))
        registerRule(PhraseDangerRule("DNG_LNS_05", "Loan Scam", listOf("processing fee first", "pay processing fee in advance"), "Deceptive advance processing fee charge", weight = 100))
        registerRule(PhraseDangerRule("DNG_LNS_06", "Loan Scam", listOf("advance payment required", "advance payment for loan"), "Scam requiring direct up-front cash before transfer", weight = 100))
        registerRule(PhraseDangerRule("DNG_LNS_07", "Loan Scam", listOf("loan waiting", "your loan is waiting for transfer"), "Urgent notifications of a fictitious ready-to-transfer loan", weight = 40))
        registerRule(PhraseDangerRule("DNG_LNS_08", "Loan Scam", listOf("instant cash loan", "get instant cash loan"), "Luring low-income users into predatory short term loops", weight = 40))
        registerRule(PhraseDangerRule("DNG_LNS_09", "Loan Scam", listOf("fast approval", "fastest loan approval"), "Baiting users with fast/express financing terms", weight = 40))
        registerRule(PhraseDangerRule("DNG_LNS_10", "Loan Scam", listOf("guaranteed approval", "100% loan approval guaranteed"), "False claims of unconditional loan approvals", weight = 40))
        registerRule(PhraseDangerRule("DNG_LNS_11", "Loan Scam", listOf("no credit check loan", "bad credit loan guaranteed"), "Deceptive bad credit loan acceptance guarantees", weight = 80))
        registerRule(PhraseDangerRule("DNG_LNS_12", "Loan Scam", listOf("pre-approved personal loan without documentation", "approved loan no documentation"), "Deceptive pre-approved zero barrier loans", weight = 100))
        registerRule(PhraseDangerRule("DNG_LNS_13", "Loan Scam", listOf("pay security deposit for loan", "loan security deposit"), "Predatory requests for loan security deposits", weight = 100))
        registerRule(PhraseDangerRule("DNG_LNS_14", "Loan Scam", listOf("unsecured loan guaranteed", "guaranteed unsecured personal loan"), "Suspicious unsecured loans targeting vulnerable borrowers", weight = 60))
        registerRule(PhraseDangerRule("DNG_LNS_15", "Loan Scam", listOf("low interest easy loan", "0% interest loan offer"), "Deceptive zero-interest lending hooks", weight = 60))
        registerRule(PhraseDangerRule("DNG_LNS_16", "Loan Scam", listOf("pay insurance fee for loan", "loan insurance payment"), "Scammer requesting upfront fee under loan insurance guise", weight = 100))
        registerRule(PhraseDangerRule("DNG_LNS_17", "Loan Scam", listOf("bypass loan eligibility", "approve loan manually"), "Fraudulent agent offering to bypass credit verification", weight = 80))
        registerRule(PhraseDangerRule("DNG_LNS_18", "Loan Scam", listOf("instant credit line without check", "no check credit line"), "Unchecked credit line offers", weight = 80))
        registerRule(PhraseDangerRule("DNG_LNS_19", "Loan Scam", listOf("urgent loan transfer", "release your loan fund"), "Pushing urgent upfront payments to release locked loans", weight = 60))
        registerRule(PhraseDangerRule("DNG_LNS_20", "Loan Scam", listOf("loan file charge", "pay loan verification charge"), "Requesting fees for manual loan file creation", weight = 100))

        // --- 5. JOB & WORK FROM HOME SCAM (20 Rules) ---
        registerRule(PhraseDangerRule("DNG_WFH_01", "Job & Work From Home Scam", listOf("earn ₹5000 daily", "earn 5000 daily"), "Deceptive high daily salary job listing", weight = 100))
        registerRule(PhraseDangerRule("DNG_WFH_02", "Job & Work From Home Scam", listOf("part time job", "part-time job offer"), "Baiting with unverified casual jobs", weight = 60))
        registerRule(PhraseDangerRule("DNG_WFH_03", "Job & Work From Home Scam", listOf("work from home salary", "wfh salary"), "Suspicious WFH listings with excessive compensation", weight = 80))
        registerRule(PhraseDangerRule("DNG_WFH_04", "Job & Work From Home Scam", listOf("online typing job", "home typing job"), "Deceptive data transcription/typing listing scam", weight = 80))
        registerRule(PhraseDangerRule("DNG_WFH_05", "Job & Work From Home Scam", listOf("easy income", "easy online income"), "Baiting users with zero effort income models", weight = 60))
        registerRule(PhraseDangerRule("DNG_WFH_06", "Job & Work From Home Scam", listOf("daily earning", "guaranteed daily earning"), "Unrealistic daily performance job compensation", weight = 60))
        registerRule(PhraseDangerRule("DNG_WFH_07", "Job & Work From Home Scam", listOf("registration fee", "pay registration fee"), "Scammer requiring upfront fees to start a job", weight = 100))
        registerRule(PhraseDangerRule("DNG_WFH_08", "Job & Work From Home Scam", listOf("join immediately", "start work today"), "Urgent and vague immediate start job postings", weight = 40))
        registerRule(PhraseDangerRule("DNG_WFH_09", "Job & Work From Home Scam", listOf("limited vacancies", "only few seats left"), "Urgency tactics around fake employment availability", weight = 40))
        registerRule(PhraseDangerRule("DNG_WFH_10", "Job & Work From Home Scam", listOf("earn money from mobile", "earn from mobile phone"), "WFH scams targeted at smartphone users", weight = 80))
        registerRule(PhraseDangerRule("DNG_WFH_11", "Job & Work From Home Scam", listOf("youtube like job", "earn by liking videos"), "Predatory tasks pay scams requiring likes or reviews", weight = 100))
        registerRule(PhraseDangerRule("DNG_WFH_12", "Job & Work From Home Scam", listOf("amazon product reviewer job", "earn by reviewing products"), "Fraudulent ecommerce reviews jobs requiring deposits", weight = 100))
        registerRule(PhraseDangerRule("DNG_WFH_13", "Job & Work From Home Scam", listOf("telegram task job", "earn by completing tasks"), "Task schemes moving users to dangerous Telegram chats", weight = 100))
        registerRule(PhraseDangerRule("DNG_WFH_14", "Job & Work From Home Scam", listOf("pay training deposit", "job security deposit"), "Requiring up-front fees for training or equipment", weight = 100))
        registerRule(PhraseDangerRule("DNG_WFH_15", "Job & Work From Home Scam", listOf("guaranteed monthly stipend", "unconditional salary"), "Unrealistic unconditional monthly stipend guarantees", weight = 80))
        registerRule(PhraseDangerRule("DNG_WFH_16", "Job & Work From Home Scam", listOf("online data entry work", "copy paste job"), "Low-skilled high payout copywriting scams", weight = 60))
        registerRule(PhraseDangerRule("DNG_WFH_17", "Job & Work From Home Scam", listOf("earn commission from home", "home based commission"), "Task commission based multilevel marketing schemes", weight = 60))
        registerRule(PhraseDangerRule("DNG_WFH_18", "Job & Work From Home Scam", listOf("high salary partial hours", "flexible hour high pay"), "Promoting high-compensating small working hours", weight = 40))
        registerRule(PhraseDangerRule("DNG_WFH_19", "Job & Work From Home Scam", listOf("no experience required job", "no resume required"), "Zero barrier work listings targeted to capture personal info", weight = 40))
        registerRule(PhraseDangerRule("DNG_WFH_20", "Job & Work From Home Scam", listOf("pay processing fee for offer letter", "offer letter processing fee"), "Scammers demanding fees to issue fake employment offer letters", weight = 100))
    }

    fun registerRule(rule: Rule) {
        rules.add(rule)
    }

    fun getRules(): List<Rule> = rules

    fun evaluate(text: String): EngineResult {
        val matchedRules = mutableListOf<String>()
        var maxScore = 0
        var maxConfidence = 0
        var maxWeight = 0
        val reasons = mutableListOf<String>()

        for (rule in rules) {
            val res = rule.evaluate(text)
            if (res.isMatched) {
                matchedRules.add(rule.id)
                maxScore = maxOf(maxScore, res.score)
                maxConfidence = maxOf(maxConfidence, res.confidence)
                maxWeight = maxOf(maxWeight, res.weight)
                if (res.reason.isNotEmpty()) {
                    reasons.add(res.reason)
                }
            }
        }

        return EngineResult(
            score = maxScore,
            matchedRules = matchedRules,
            reason = reasons.joinToString(", "),
            confidence = maxConfidence,
            weight = maxWeight
        )
    }
}

class PhraseDangerRule(
    override val id: String,
    val category: String,
    val phrases: List<String>,
    override val description: String,
    val baseConfidence: Int = 95,
    val baseScore: Int = 90,
    val weight: Int = 80
) : Rule {
    override val name: String = "Phrase Danger Rule: $id"

    private val safeExclusions = listOf(
        "do not share",
        "don't share",
        "never share",
        "do not disclose",
        "don't disclose",
        "never disclose",
        "do not forward",
        "don't forward",
        "never forward",
        "do not tell",
        "don't tell",
        "never tell",
        "should not be shared",
        "must not be shared",
        "warning: do not",
        "upi payment successful",
        "money received",
        "money sent",
        "payment completed",
        "refund credited",
        "recharge successful",
        "fastag recharge",
        "merchant payment successful",
        "payment receipt",
        "invoice generated",
        "kyc completed successfully",
        "aadhaar updated successfully",
        "pan linked successfully",
        "verification completed",
        "document uploaded successfully",
        "profile updated successfully",
        "your parcel has been dispatched",
        "out for delivery",
        "package delivered",
        "tracking updated",
        "shipment received",
        "order shipped",
        "refund processed",
        "return completed",
        "delivery completed",
        "courier collected",
        "official play store update",
        "google play update available",
        "system update available",
        "app updated successfully",
        "software updated successfully",
        "play protect scan completed",
        "official app installation confirmation",
        "salary credited",
        "interview reminder",
        "offer letter received",
        "application received",
        "meeting reminder",
        "attendance marked",
        "official hr notification",
        "official banking notification",
        "official loan statement",
        "official emi reminder"
    )

    override fun evaluate(text: String): RuleResult {
        val lowerText = text.lowercase()

        // False Positive Prevention: If text contains any high-risk safe exclusion keywords (e.g. warning to NOT share), immediately skip matching this danger rule.
        for (exclusion in safeExclusions) {
            if (lowerText.contains(exclusion)) {
                return RuleResult(isMatched = false, score = 0, confidence = 0, reason = "", weight = 0)
            }
        }

        // Search for phrase matches
        for (phrase in phrases) {
            if (lowerText.contains(phrase.lowercase())) {
                return RuleResult(
                    isMatched = true,
                    score = baseScore,
                    confidence = baseConfidence,
                    reason = "Matched dangerous phrase pattern: '$phrase' (Category: $category)",
                    weight = weight
                )
            }
        }

        return RuleResult(isMatched = false, score = 0, confidence = 0, reason = "", weight = 0)
    }
}

