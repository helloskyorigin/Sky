with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "r") as f:
    text = f.read()

# Revert activityContext back to context EXCEPT in signInWithGoogle
import re

# Find signInWithGoogle block
match = re.search(r'suspend fun signInWithGoogle\(.*?\).*?_authState.value = AuthUiState.Loading', text, re.DOTALL)
if match:
    # replace activityContext with context everywhere
    text = text.replace('activityContext', 'context')
    # then ONLY put activityContext back in signInWithGoogle
    text = re.sub(
        r'suspend fun signInWithGoogle\(\s*context: android.content.Context,\s*onGoogleCredentialReceived: suspend \(String, String\) -> Unit\s*\)\s*\{',
        r'suspend fun signInWithGoogle(\n        activityContext: android.content.Context,\n        onGoogleCredentialReceived: suspend (String, String) -> Unit\n    ) {',
        text
    )
    # We need to replace credentialManager = CredentialManager.create(context) with activityContext IN signInWithGoogle
    
    parts = text.split('suspend fun signInWithGoogle(')
    part0 = parts[0]
    part1 = 'suspend fun signInWithGoogle(' + parts[1]
    
    part1_split = part1.split('suspend fun signOut')
    
    sign_in_block = part1_split[0]
    sign_out_and_rest = 'suspend fun signOut' + part1_split[1]
    
    sign_in_block = sign_in_block.replace('CredentialManager.create(context)', 'CredentialManager.create(activityContext)')
    sign_in_block = sign_in_block.replace('credentialManager.getCredential(context', 'credentialManager.getCredential(activityContext')
    
    text = part0 + sign_in_block + sign_out_and_rest
    
    with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "w") as f:
        f.write(text)
