with open('app/src/main/java/com/example/LegalConsentScreen.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "verticalAlignment = Alignment.CenterVertically" in line and "PremiumIconContainer(" in lines[i+2]:
        lines.insert(i, "        Row(\n            modifier = Modifier.padding(16.dp),\n")
        break

with open('app/src/main/java/com/example/LegalConsentScreen.kt', 'w') as f:
    f.writelines(lines)
