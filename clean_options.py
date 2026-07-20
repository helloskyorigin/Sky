import re
with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "r") as f:
    text = f.read()

text = re.sub(r'import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption\n', '', text)
text = re.sub(r'val signInOption = GetSignInWithGoogleOption.Builder\(webClientId\)\s*\.build\(\)\s*', '', text)
text = text.replace('.addCredentialOption(signInOption)', '')

with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "w") as f:
    f.write(text)
