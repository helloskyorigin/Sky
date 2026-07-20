import re

# 1. AuthManager.kt
with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "r") as f:
    am_content = f.read()

am_content = re.sub(
    r'suspend fun signInWithGoogle\(\s*onGoogleCredentialReceived: suspend \(String, String\) -> Unit\s*\)',
    r'suspend fun signInWithGoogle(\n        activityContext: android.content.Context,\n        onGoogleCredentialReceived: suspend (String, String) -> Unit\n    )',
    am_content
)

am_content = am_content.replace('val credentialManager = CredentialManager.create(context)', 'val credentialManager = CredentialManager.create(activityContext)')
am_content = am_content.replace('val result = credentialManager.getCredential(context, request)', 'val result = credentialManager.getCredential(activityContext, request)')

with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "w") as f:
    f.write(am_content)

# 2. AuthRepository.kt
with open("app/src/main/java/com/skyorigin/threatshieldai/AuthRepository.kt", "r") as f:
    ar_content = f.read()

ar_content = re.sub(
    r'suspend fun signInWithGoogle\(\s*onSuccess: suspend \(String, String\) -> Unit\s*\)',
    r'suspend fun signInWithGoogle(\n        activityContext: android.content.Context,\n        onSuccess: suspend (String, String) -> Unit\n    )',
    ar_content
)
ar_content = ar_content.replace('authManager.signInWithGoogle {', 'authManager.signInWithGoogle(activityContext) {')

with open("app/src/main/java/com/skyorigin/threatshieldai/AuthRepository.kt", "w") as f:
    f.write(ar_content)

# 3. ScamLensViewModel.kt
with open("app/src/main/java/com/skyorigin/threatshieldai/ScamLensViewModel.kt", "r") as f:
    vm_content = f.read()

vm_content = re.sub(
    r'fun signInWithGoogle\(onComplete: \(String, String\) -> Unit\)',
    r'fun signInWithGoogle(activityContext: android.content.Context, onComplete: (String, String) -> Unit)',
    vm_content
)
vm_content = vm_content.replace('authRepo.signInWithGoogle { emailVal, nameVal ->', 'authRepo.signInWithGoogle(activityContext) { emailVal, nameVal ->')

with open("app/src/main/java/com/skyorigin/threatshieldai/ScamLensViewModel.kt", "w") as f:
    f.write(vm_content)
    
# 4. GoogleSignInScreen.kt
with open("app/src/main/java/com/skyorigin/threatshieldai/GoogleSignInScreen.kt", "r") as f:
    ui_content = f.read()

ui_content = ui_content.replace('viewModel.signInWithGoogle { email, displayName', 'viewModel.signInWithGoogle(context) { email, displayName')

with open("app/src/main/java/com/skyorigin/threatshieldai/GoogleSignInScreen.kt", "w") as f:
    f.write(ui_content)

