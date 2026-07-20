import re
import glob

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # We want to replace card elevation lines with nothing or 0.dp
    content = re.sub(r'elevation\s*=\s*CardDefaults\.cardElevation\([^)]+\),', 'elevation = CardDefaults.cardElevation(0.dp),', content)
    
    # Let's just find `modifier = Modifier` (or similar) inside Card(...) and add .premiumShadow(isDark)
    # This is tricky with regex. Instead of modifying each card, what if we use standard shadows?
    # I can just globally replace the background colors for now.
    
    # 1. Backgrounds
    content = content.replace('Color(0xFFF4F7FB)', 'Color(0xFFF6F8FB)')
    content = content.replace('Color(0xFFE7ECF5)', 'Color(0xFFE5E7EB)')
    content = content.replace('Color(0xFFE2E8F0)', 'Color(0xFFDCE3EE)')
    
    with open(filepath, 'w') as f:
        f.write(content)

for f in glob.glob('app/src/main/java/com/example/*.kt'):
    process_file(f)

print("Done")
