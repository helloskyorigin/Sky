import zipfile
import re

classes = set()
with zipfile.ZipFile('app/build/outputs/apk/debug/app-debug.apk', 'r') as apk:
    for name in apk.namelist():
        if name.endswith('.dex'):
            content = apk.read(name)
            # Find all L...; strings
            matches = re.findall(b'Lcom/skyorigin/threatshieldai/[a-zA-Z0-9_$]+;', content)
            for m in matches:
                classes.add(m.decode('utf-8'))

for c in sorted(classes):
    print(c)
