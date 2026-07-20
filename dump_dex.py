import zipfile

with zipfile.ZipFile('app/build/outputs/apk/debug/app-debug.apk', 'r') as apk:
    for name in apk.namelist():
        if name.endswith('.dex'):
            content = apk.read(name)
            if b'Landroidx/credentials/CredentialManager;' in content:
                print(f"Found CredentialManager in {name}")
