import re
import glob

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # We want to replace "elevation = CardDefaults.cardElevation(...)," with nothing
    content = re.sub(r'elevation\s*=\s*CardDefaults\.cardElevation\([^)]+\)[^,]*,', '', content)
    # The above regex might still fail because of multiple parens. Let's do string replacement.
    content = re.sub(r'elevation\s*=\s*CardDefaults\.cardElevation[^\n]+,', '', content)
    
    # Add .premiumShadow(isDark) before .clickable or inside Modifier...
    # It's much simpler to just let it be without standard elevation, and maybe we can use sed for specific places if needed.
    
    with open(filepath, 'w') as f:
        f.write(content)

for f in glob.glob('app/src/main/java/com/example/*.kt'):
    process_file(f)
