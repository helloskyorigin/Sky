import re

with open('app/src/main/java/com/skyorigin/threatshieldai/ScamExamplesData.kt', 'r') as f:
    text = f.read()

replacements = {
    1: {
        "titleHi": '"Netflix Account Suspension Alert"',
        "messageHi": '"Dear Member, aapka subscription Payment fail ho gaya hai. Agar aap apni billing details update nahi karte, toh 24 ghante ke andar aapki membership suspend kar di jayegi. Reactivate karne ke liye yahan click karein: http://netflix-billing-update.com"',
        "dangerHi": '"Fake websites aapke Credit Card details, CVV aur login password chura leti hain, jisse unauthorized charges hone lagte hain."',
        "redFlagsHi": 'listOf("24 ghante mein suspension ki urgent warning", "netflix.com ke bajaye non-secure Fake Link netflix-billing-update.com", "Generic greeting \'Dear Member\'")',
        "safeResponseHi": '"SMS ya email Alert mein diye Fake Link par kabhi click na karein. Apni billing status check karne ke liye Official Website ya Netflix app alag se open karein."'
    },
    2: {
        "titleHi": '"Aadhaar-Linked Bank Activation OTP"',
        "messageHi": '"Ek caller government official bankar claim karta hai ki aapke Aadhaar ki verification baaki hai. \'Maine Verify karne ke liye SMS code bheja hai. Turant OTP batayein warna aapka account lock ho jayega.\'"',
        "dangerHi": '"OTP share karte hi Scammer ko aapke Bank account se paise nikalne ya apna number link karne ka pura access mil jata hai."',
        "redFlagsHi": 'listOf("Caller ka verification ke liye OTP maangna", "Account turant lock hone ki Warning", "Bina soche jaldi Safe Action lene ka pressure")',
        "safeResponseHi": '"Call cut karein. Koi bhi Bank ya official authority kabhi phone call par verification OTP nahi maangti."'
    },
    3: {
        "titleHi": '"OLX Buyer Double Payment UPI Request"',
        "messageHi": '"OLX par ek buyer claim karta hai ki usne galti se aapko double Payment bhej di hai. Woh ek UPI Request bhej kar aapse \'Approve\' karne aur extra paise wapas karne ke liye UPI PIN type karne ko kehta hai."',
        "dangerHi": '"UPI collect request ko apna PIN daal kar approve karne se aapke Bank account se paise turant kat jaate hain."',
        "redFlagsHi": 'listOf("Paise receive karne ke liye UPI PIN daalne ka request", "Credit ke bajaye \'Collect\' tag wali UPI notification", "Buyer ki taraf se bohot jaldbazi dikhana")',
        "safeResponseHi": '"Is request ko turant decline karein. Yaad rakhein, UPI se paise receive karne ke liye kabhi bhi apna PIN daalne ki zaroorat nahi hoti."'
    },
    4: {
        "titleHi": '"Lucky Draw Reward Scratch Card QR"',
        "messageHi": '"Aapko WhatsApp par ya physical pamphlet mein ek QR Code milta hai jo claim karta hai \'Scratch karne aur 2,000 Rs ka cash prize apne account mein receive karne ke liye yeh code scan karein.\'"',
        "dangerHi": '"QR Code scan karte hi aapko ek Fake Payment page par bheja jata hai jahan PIN daalte hi aapke account se paise kat jaate hain."',
        "redFlagsHi": 'listOf("Cash prize claim karne ke liye QR Code scan karne ko kehna", "Unsolicited reward announcements", "Payment PIN ya credit details maangne wala webpage")',
        "safeResponseHi": '"Paise receive karne ke liye kabhi bhi QR Code scan na karein. QR Code sirf Payment karne ke liye hote hain."'
    },
    5: {
        "titleHi": '"Credit Card Limit Increase Trap"',
        "messageHi": '"Aapke Bank ka representative bankar ek caller bina documents ke aapki credit limit double karne ka offer deta hai. Woh aapke card details, CVV, aur OTP maangta hai."',
        "dangerHi": '"In details ki madad se Scammer online shopping karke aapka poora credit balance empty kar dete hain."',
        "redFlagsHi": 'listOf("Call par CVV ya expiry date maangna", "Bina documents ke instant limit badhane ka wada", "Regular 10-digit mobile number se Bank call aana")',
        "safeResponseHi": '"Call cut karein aur bole ki aap apni credit limit directly banking app ya official Bank branch se badha lenge."'
    },
    6: {
        "titleHi": '"SIM Card Deactivation Threat KYC"',
        "messageHi": '"Aapko SMS milta hai: \'Dear Customer, aapka Airtel SIM KYC expire ho gaya hai. Aapki outgoing services aaj suspend ho jayengi. Verify karne ke liye turant +91-XXXXXXXXXX par call karein.\'"',
        "dangerHi": '"Scammers aapko screen sharing tools (jaise AnyDesk) install karne ko kehte hain taaki woh aapki banking session dekh kar paise chura sakein."',
        "redFlagsHi": 'listOf("Kuch ghanton mein SIM block hone ki urgent Warning", "Support ke liye personal 10-digit number par call karne ka request", "SMS kisi personal number se aana, official brand code se nahi")',
        "safeResponseHi": '"Apna KYC sirf official telecom app ya authorized operator outlet par jaakar Verify karein."'
    },
    7: {
        "titleHi": '"Post Office Address Correction Scam"',
        "messageHi": '"Ek SMS claim karta hai ki address incomplete hone ki wajah se aapka India Post package deliver nahi ho saka. \'Address update karein aur 5 Rs ki fee dekar delivery schedule karein: http://indiapost-delivery-tracking.info\'"',
        "dangerHi": '"Jab aap minor 5 Rs fee Payment karne ki koshish karte hain, toh woh aapke card details capture karke aapke account se bada amount kaat lete hain."',
        "redFlagsHi": 'listOf("Unexpected parcel delivery Alert", "Package release karne ke liye choti si Payment maangna", "Official .gov.in ke bajaye unofficial Fake Link URL")',
        "safeResponseHi": '"Unknown parcels par kabhi respond na karein. Official delivery services package release karne ke liye SMS Link se payment nahi maangti."'
    },
    8: {
        "titleHi": '"Part-Time YouTube Video Liking Job"',
        "messageHi": '"Ek WhatsApp recruiter aapko ek attractive Job Scam offer bhejta hai: \'Rozana 3,000 se 8,000 Rs kamayein, bas hamare YouTube videos ko like karein.\'"',
        "dangerHi": '"Shuru mein aapko thode paise dekar trust banaya jata hai. Phir woh aapko bade returns ka lalach dekar \'premium tasks\' mein paise invest karne ko bolte hain, aur paise lekar gayab ho jaate hain."',
        "redFlagsHi": 'listOf("Basic tasks (like aur subscribe) ke liye abnormally high salary", "Telegram groups ke through work operate karna", "High-level tasks unlock karne ke liye Security deposit ki Warning")',
        "safeResponseHi": '"Recruiter ko turant block karein. Yaad rakhein, koi bhi asli company videos like karne ke paise nahi deti."'
    },
    9: {
        "titleHi": '"High-Return Crypto Mining Scam"',
        "messageHi": '"Aapko ek Telegram group mein add kiya jata hai jahan ek Crypto Expert charts share karke promise karta hai: \'Foreign cloud mining mein 5,000 Rs invest karein aur 48 hours mein 25,000 Rs payein.\'"',
        "dangerHi": '"Investment platform ek Fake app hota hai. Jab aap apni dikhayi hui \'earnings\' nikalne ki koshish karte hain, toh Scamsters withdrawal tax ke naam par aur paise maangte hain."',
        "redFlagsHi": 'listOf("Bina risk ke short-term mein huge guaranteed returns ka wada", "Unknown foreign Crypto platforms par invest karne ka bolna", "Bina pooche random Telegram groups mein add karna")',
        "safeResponseHi": '"Aise sabhi Fake offers ko block karein aur sirf verified aur regulated Indian Investment apps ka use karein."'
    },
    10: {
        "titleHi": '"KBC Crorepati Lucky Draw Winner"',
        "messageHi": '"Aapko WhatsApp par ek voice note aur poster milta hai jisme claim hota hai ki aapke number ne 25 Lakh ki KBC Lottery jeeti hai. \'Claim karne ke liye 15,000 Rs Bank registration fee transfer karein.\'"',
        "dangerHi": '"Ek baar fee transfer karne ke baad, Scamsters custom clearing ke naam par aur extra paise maangte hain aur phir block kar dete hain."',
        "redFlagsHi": 'listOf("Bina ticket kharide Lottery jeetne ka Fake Alert", "Paise bhejne se pehle advance processing fees maangna", "Highly unprofessional language mein record kiye gaye voice notes")',
        "safeResponseHi": '"Ignore karke delete karein. Koi bhi real Lottery Scam winner se advance tax ya processing fee nahi maangti."'
    },
    11: {
        "titleHi": '"Microsoft Windows Virus Firewall Alert"',
        "messageHi": '"Ek full-screen browser pop-up loud audio ke saath aapke PC ko freeze kar deta hai aur claim karta hai: \'Windows Virus Alert. Credit details leak ho rahe hain. Microsoft Support ko turant call karein.\'"',
        "dangerHi": '"Tech Support Scammers basic disk cleaner tool chalane ya Malware install karne ke hazaro rupaye charge karte hain."',
        "redFlagsHi": 'listOf("Loud alarm aur pop-ups jo aapke browser tab ko lock kar de", "Emergency support helpline par turant call karne ka Fake Alert", "Repair cover karne ke liye gift cards kharidne ki maang")',
        "safeResponseHi": '"Task Manager use karke apna browser band karein. Apne PC mein trusted Anti-Virus scanner install karein."'
    },
    12: {
        "titleHi": '"Friend in Emergency Money Request"',
        "messageHi": '"Aapko apne close friend ki profile picture ke saath ek unknown number se message aata hai: \'Maine apna number change kar liya hai. Main emergency mein hospital mein hoon, please is doctor ke number par turant 10,000 Rs GPay kar de.\'"',
        "dangerHi": '"Aap Scammer ke burner wallet mein paise transfer kar dete hain, jise turant nikal liya jata hai aur track nahi kiya ja sakta."',
        "redFlagsHi": 'listOf("Naye number se paise ka urgent request aana", "Medical emergency ka hawala dekar emotional panic create karna", "Direct phone call lene se bachna ya excuse dena")',
        "safeResponseHi": '"Koi bhi Payment karne se pehle Verify karne ke liye apne dost ke original number par call karein."'
    },
    13: {
        "titleHi": '"Telegram Stock Trading Tips Scam"',
        "messageHi": '"Ek Telegram channel \'India Bulls VIP Tips\' promote karta hai: \'Register karne ke liye 999 Rs Payment karein, aur rozana stock trading ki guaranteed tips paayein jisme 400% profit ka wada ho.\'"',
        "dangerHi": '"Channel nakli receipts dikhata hai, aur registration ya initial investment Payment karne ke baad woh aapko block kar dete hain."',
        "redFlagsHi": 'listOf("Low-risk daily trades se massive guaranteed profits ka lalach", "Channel admin ke paas koi legal financial advisor credentials na hona", "Private UPI handle ke through Payment maangna")',
        "safeResponseHi": '"Channel ko Report aur block karein. Asli financial advisors ke paas hamesha official SEBI registration code hota hai."'
    },
    14: {
        "titleHi": '"Instagram Brand Ambassador Sponsorship"',
        "messageHi": '"Ek fashion brand account aapko DM karta hai: \'Humein aapki profile bohot pasand aayi! Hum aapko 3 free products sponsor karna chahte hain. Bas is registration Link par click karein aur shipping Payment karein.\'"',
        "dangerHi": '"Shipping page ek Phishing trap hota hai jo aapke online credentials ya debit card details chura leta hai, aur aapka Bank account khali kar deta hai."',
        "redFlagsHi": 'listOf("Unsolicited influencer collaboration ke offers aana", "Shipping payment ke liye kisi third-party non-secure Fake Link par bhejna", "Brand DMs mein poor grammar ya copy-paste messages hona")',
        "safeResponseHi": '"Brand ki Official Website ko independently Verify karein. Kisi random DM se aaye hue Fake Link par kabhi click na karein."'
    }
}

# Add 15 which is the else block (Fake Customer Care)
# We will use regex to find the else block and replace the properties.

for scam_id, data in replacements.items():
    # Find the block for this id
    pattern = re.compile(rf'id = {scam_id},.*?safeResponseHi = [^\n]+', re.DOTALL)
    match = pattern.search(text)
    if match:
        block = match.group(0)
        # Replace the fields inside this block
        for key, value in data.items():
            # Match key = "..." or key = listOf(...)
            key_pattern = re.compile(rf'{key}\s*=\s*(?:".*?"|listOf\([^)]+\))', re.DOTALL)
            block = key_pattern.sub(f'{key} = {value}', block)
        text = text[:match.start()] + block + text[match.end():]
    else:
        print(f"Failed to find block for id {scam_id}")

# Handle else block for id 15 (Fake Customer Care)
else_pattern = re.compile(r'else -> \{ // Fake Customer Care.*?safeResponseHi = [^\n]+', re.DOTALL)
else_match = else_pattern.search(text)
if else_match:
    block = else_match.group(0)
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
    text = text[:else_match.start()] + block + text[else_match.end():]
else:
    print("Failed to find else block")

with open('app/src/main/java/com/skyorigin/threatshieldai/ScamExamplesData.kt', 'w') as f:
    f.write(text)

