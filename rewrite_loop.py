import re

with open('app/src/main/java/com/skyorigin/threatshieldai/ScamExamplesData.kt', 'r') as f:
    text = f.read()

# Fix the id=15 missing translations first
replacements_15 = {
    "titleHi": '"Google Map Helpline Scam"',
    "messageHi": '"Kya aapko apni Google Map service ke saath technical problem aa rahi hai? Quick help ke liye is mobile number par hamare support executive se contact karein: +91-XXXXXXXXXX."',
    "dangerHi": '"Fake customer care aapko aisi process follow karne ko bolta hai jisse aapka password ya Bank balance Scam ho jata hai."',
    "redFlagsHi": 'listOf("Helpline ke naam par personal mobile number ka use karna", "OTP, UPI PIN ya Bank details share karne ko kehna", "Official app mein ticket ya support history na hona")',
    "safeResponseHi": '"Hamesha official in-app support ya verified Official Website portals ke through hi help maangein."'
}
pattern_15 = re.compile(r'id = 15,.*?safeResponseHi = [^\n]+', re.DOTALL)
match_15 = pattern_15.search(text)
if match_15:
    block = match_15.group(0)
    for key, value in replacements_15.items():
        key_pattern = re.compile(rf'{key}\s*=\s*(?:".*?"|listOf\([^)]+\))', re.DOTALL)
        block = key_pattern.sub(f'{key} = {value}', block)
    text = text[:match_15.start()] + block + text[match_15.end():]

# Now for the loop!
loop_replacements = {
    '"Phishing"': {
        "titleHi": '"$brand Security Alert Attempt #$i"',
        "messageHi": '"Dear User, humein aapke account mein suspicious login attempt mila hai. Please apni details Verify karein is Fake Link par click karke: http://secure-update-${brand.lowercase()}.com"',
        "dangerHi": '"Yeh Fake Link aapke account credentials capture kar leta hai, jisse Scammer aapka data chura kar fraud kar sakta hai."',
        "redFlagsHi": 'listOf("Unknown sender se urgent Security Alert", "Suspicious URL jo official brand jaisa dikhta ho", "Link par click karke login karne ka pressure")',
        "safeResponseHi": '"Aise messages par kabhi click na karein. Hamesha official app ya website manually open karke apna account check karein."'
    },
    '"OTP Fraud"': {
        "titleHi": '"Urgent $bank Reward Points OTP #$i"',
        "messageHi": '"Aapke $bank credit card mein 5,000 reward points expire hone wale hain. Points redeem karne ke liye OTP share karein jo aapke number par bheja gaya hai."',
        "dangerHi": '"OTP share karte hi Scammer aapke Bank account se transactions authorize kar leta hai."',
        "redFlagsHi": 'listOf("Reward points claim karne ke liye OTP maangna", "Call par urgent action lene ka pressure", "Unverified caller Bank representative ban kar call kare")',
        "safeResponseHi": '"OTP kabhi kisi ke saath share na karein. Bank ya official customer care kabhi OTP nahi maangte."'
    },
    '"UPI Scam"': {
        "titleHi": '"$app Cash Refund UPI Trap $i"',
        "messageHi": '"Aapka recent $app order cancel ho gaya hai. Refund process karne ke liye is UPI Link par click karein aur apna UPI PIN enter karein."',
        "dangerHi": '"UPI PIN daalte hi aapke account se paise kat jaate hain, refund aane ki bajaye."',
        "redFlagsHi": 'listOf("Refund receive karne ke liye UPI PIN maangna", "Unknown sender se payment link aana", "Customer care ka payment approve karne ko kehna")',
        "safeResponseHi": '"Paise receive karne ke liye UPI PIN ki zaroorat nahi hoti. Aise sabhi requests ko turant decline karein."'
    },
    '"QR Code Scam"': {
        "titleHi": '"Marketplace QR Code Voucher for $item"',
        "messageHi": '"Main aapka $item kharidne ke liye ready hoon. Maine advance payment ke liye ek QR code bheja hai, isko scan karke apna payment receive karein."',
        "dangerHi": '"QR code scan karte hi aapke account se paise debit ho jaate hain."',
        "redFlagsHi": 'listOf("Payment receive karne ke liye QR Code scan karne ko kehna", "Buyer ka bina soche advance payment offer karna", "UPI app mein \'Pay\' ki bajaye scan request aana")',
        "safeResponseHi": '"Paise receive karne ke liye kabhi QR Code scan na karein. QR Code sirf payment dene ke liye use hota hai."'
    },
    '"Fake Bank Call"': {
        "titleHi": '"$bank Suspicious Transaction Verification #$i"',
        "messageHi": '"Hum $bank se call kar rahe hain. Aapke account se 25,000 Rs ka suspicious transaction hua hai. Isko block karne ke liye apna card number aur OTP batayein."',
        "dangerHi": '"Card details aur OTP milte hi Scamsters aapke account se paise nikal lete hain."',
        "redFlagsHi": 'listOf("Call par card details ya OTP maangna", "Account block karne ka darr dikhana", "Personal number se Bank ka call aana")',
        "safeResponseHi": '"Aise calls ko cut karein aur apne Bank ke official customer care number par call karke apna account Verify karein."'
    },
    '"Fake KYC"': {
        "titleHi": '"Urgent $service KYC Suspension Notice $i"',
        "messageHi": '"Aapka $service KYC complete nahi hai. Aapki services aaj raat band ho jayengi. Apna KYC complete karne ke liye is link par click karein aur Rs. 10 payment karein."',
        "dangerHi": '"Jab aap small payment karte hain, Fake Link aapke card details save kar leta hai aur bada amount debit kar leta hai."',
        "redFlagsHi": 'listOf("KYC update ke liye urgent suspension warning", "Third-party link se payment ya details maangna", "Unverified sender SMS")',
        "safeResponseHi": '"Official app ya authorized store par jaakar apna KYC Verify karein. SMS links par kabhi click na karein."'
    },
    '"Fake Delivery"': {
        "titleHi": '"Failed $carrier Package Tracking Fee #$i"',
        "messageHi": '"Aapka $carrier parcel deliver nahi ho paaya kyunki address incomplete tha. Address update karne aur redelivery ke liye Rs. 5 fee pay karein: http://$carrier-tracking-update.com"',
        "dangerHi": '"Fee pay karne ke time aapke card details steal ho jaate hain aur aapke bank se fraud transaction hota hai."',
        "redFlagsHi": 'listOf("Package delivery ke liye small fee maangna", "Unexpected parcel ki notification", "Official website ke badle fake tracking link")',
        "safeResponseHi": '"Agar aapne kuch order nahi kiya, toh SMS ko ignore karein. Official delivery services aise links se payment nahi maangti."'
    },
    '"Job Scam"': {
        "titleHi": '"Work From Home $jobTitle Offer #$i"',
        "messageHi": '"Hum aapko $jobTitle ki post offer kar rahe hain. Rozana 2-3 ghante kaam karein aur 5,000 Rs kamayein. Registration ke liye Rs. 1,000 security deposit pay karein."',
        "dangerHi": '"Deposit lene ke baad recruiter gayab ho jaata hai, aur aapko koi kaam nahi milta."',
        "redFlagsHi": 'listOf("Job ke liye registration ya security deposit maangna", "Bina interview ke direct job offer", "Too good to be true salary offer")',
        "safeResponseHi": '"Real companies kabhi job dene ke liye paise nahi maangti. Aise offers ko turant block karein."'
    },
    '"Investment Scam"': {
        "titleHi": '"VIP Trading Signal Club Success $i"',
        "messageHi": '"Hamare VIP Trading Club se judein aur rozana 50% profit kamayein. Start karne ke liye sirf Rs. 2,000 invest karein. Guaranteed returns milenge!"',
        "dangerHi": '"Fake trading platform par aapka paisa block ho jaata hai aur profit nikalne ke liye aur fees maangi jaati hai."',
        "redFlagsHi": 'listOf("Guaranteed high returns ka wada", "Unverified trading platform par invest karne ko kehna", "Withdrawal ke time hidden fees maangna")',
        "safeResponseHi": '"Aise fake investment schemes se door rahein. Hamesha SEBI-registered brokers ke through invest karein."'
    },
    '"Lottery Scam"': {
        "titleHi": '"Sponsorship Sweepstakes Winner: $prize"',
        "messageHi": '"Congratulations! Aapne hamara Lucky Draw jeeta hai. Apna $prize claim karne ke liye tax aur processing fee Rs. 5,000 is account mein transfer karein."',
        "dangerHi": '"Aap fee transfer karte hain aur scammer paise lekar block kar deta hai, prize kabhi nahi milta."',
        "redFlagsHi": 'listOf("Bina participate kiye lucky draw jeetna", "Prize claim karne ke liye advance tax maangna", "Unprofessional message aur bank account details")',
        "safeResponseHi": '"Is message ko ignore aur delete karein. Asli lottery winners ko advance paise nahi dene padte."'
    },
    '"Tech Support Scam"': {
        "titleHi": '"$app Fake Refund Helpline Trap"',
        "messageHi": '"Agar aapko $app se related koi payment issue hai, toh hamare customer support executive se is number par call karein: +91-XXXXXXXXXX. Hum turant refund karenge."',
        "dangerHi": '"Scammer aapko screen sharing app install karne ko bolta hai aur aapke phone ka access lekar bank account khali kar deta hai."',
        "redFlagsHi": 'listOf("Social media par customer support numbers search karna", "Refund ke liye AnyDesk ya screen share app install karne ko bolna", "Executive ka UPI PIN ya OTP maangna")',
        "safeResponseHi": '"Customer support ke liye hamesha official app ka use karein. Google ya social media se mile numbers par call na karein."'
    },
    '"WhatsApp Scam"': {
        "titleHi": '"WhatsApp 6-Digit Verification Code Scam"',
        "messageHi": '"Ek relative ka hacked account message karta hai: \'Maine galti se apna WhatsApp verification code tumhare number par bhej diya hai. Kya tum mujhe woh 6-digit SMS code jaldi bhej sakte ho?\'"',
        "dangerHi": '"Jaise hi aap code share karte hain, scammer aapka WhatsApp account hack kar leta hai aur aapke contacts ko scam karta hai."',
        "redFlagsHi": 'listOf("WhatsApp verification code share karne ka request", "Family ya friends se achanak urgent help maangna", "SMS mein \'Do not share this code\' likha hona")',
        "safeResponseHi": '"Apna verification code kabhi kisi ke saath share na karein, chahe woh family hi kyun na ho. Unhein call karke Verify karein."'
    },
    '"Telegram Scam"': {
        "titleHi": '"Telegram Channel Investment Expert Setup"',
        "messageHi": '"Hamare exclusive Telegram channel se judein. Entry ke liye 999 Rs pay karein aur rozana stock trading signals paayein jo 400% profit guarantee karte hain."',
        "dangerHi": '"Registration fee pay karne ke baad channel aapko block kar deta hai ya aur paise invest karne ko bolta hai."',
        "redFlagsHi": 'listOf("Guaranteed massive profits from low-risk trades", "Channel admin ke paas legal financial advisor license na hona", "Private UPI id par payment maangna")',
        "safeResponseHi": '"Aise channels ko Report aur block karein. Real financial advisors apna SEBI registration code display karte hain."'
    },
    '"Instagram Scam"': {
        "titleHi": '"Instagram Brand Ambassador Sponsorship"',
        "messageHi": '"Ek fashion brand account DM karta hai: \'Humein aapki profile bohot pasand aayi! Hum aapko 3 free products sponsor karna chahte hain. Bas is link par click karein aur shipping fee pay karein.\'"',
        "dangerHi": '"Shipping page ek phishing trap hota hai jo aapke online credentials ya card details chura leta hai."',
        "redFlagsHi": 'listOf("Unsolicited brand ambassador offers", "Third-party site par shipping fee pay karne ko kehna", "Brand account ki poor grammar aur fake followers")',
        "safeResponseHi": '"Random DMs se aaye links par kabhi click na karein. Brand ki Official Website par jakar verify karein."'
    }
}

# The block matching logic inside the `when (category)` block
for category, data in loop_replacements.items():
    # Find the case block `category -> { ... }`
    pattern = re.compile(rf'{re.escape(category)}\s*->\s*\{{(.*?)\}}', re.DOTALL)
    match = pattern.search(text)
    if match:
        block = match.group(1)
        for key, value in data.items():
            key_pattern = re.compile(rf'{key}\s*=\s*(?:".*?"|listOf\([^)]+\))', re.DOTALL)
            block = key_pattern.sub(f'{key} = {value}', block)
        text = text[:match.start(1)] + block + text[match.end(1):]
    else:
        print(f"Failed to find block for {category}")

# Also find Fake Customer Care (the else -> block in the loop)
else_loop_pattern = re.compile(r'else -> \{ // Fake Customer Care(.*?)\}', re.DOTALL)
else_loop_match = else_loop_pattern.search(text)
if else_loop_match:
    block = else_loop_match.group(1)
    data = {
        "titleHi": '"Fake $brand Online Helpline Interception"',
        "messageHi": '"Kya aapko apni $brand service ke saath technical problem aa rahi hai? Quick help ke liye is mobile number par hamare support executive se contact karein: +91-XXXXXXXXXX."',
        "dangerHi": '"Fake customer care aapko aisi process follow karne ko bolta hai jisse aapka password ya Bank balance Scam ho jata hai."',
        "redFlagsHi": 'listOf("Helpline ke naam par personal mobile number ka use karna", "OTP, UPI PIN ya Bank details share karne ko kehna", "Official app mein ticket ya support history na hona")',
        "safeResponseHi": '"Hamesha $brand ke official in-app support ya verified Official Website portals ke through hi help maangein."'
    }
    for key, value in data.items():
        key_pattern = re.compile(rf'{key}\s*=\s*(?:".*?"|listOf\([^)]+\))', re.DOTALL)
        block = key_pattern.sub(f'{key} = {value}', block)
    text = text[:else_loop_match.start(1)] + block + text[else_loop_match.end(1):]

with open('app/src/main/java/com/skyorigin/threatshieldai/ScamExamplesData.kt', 'w') as f:
    f.write(text)

