with open('app/src/main/java/com/example/DailyChallengeScreen.kt', 'r') as f:
    c = f.read()

c = c.replace('''                        Text(
                        text = if (isCompleted) {
                            if (isHindi) "आज की चुनौती पूरी हुई" else "Today's Challenge Completed"
                        } else {
                            if (isHindi) "आज का स्कैम चैलेंज" else "Today's Scam Challenge"
                        ),''', '''                        Text(
                        text = if (isCompleted) {
                            if (isHindi) "आज की चुनौती पूरी हुई" else "Today's Challenge Completed"
                        } else {
                            if (isHindi) "आज का स्कैम चैलेंज" else "Today's Scam Challenge"
                        },''')

with open('app/src/main/java/com/example/DailyChallengeScreen.kt', 'w') as f:
    f.write(c)

