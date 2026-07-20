import re

with open('app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt', 'r') as f:
    content = f.read()

# Add the import
if 'GetSignInWithGoogleOption' not in content:
    content = content.replace(
        'import com.google.android.libraries.identity.googleid.GetGoogleIdOption',
        'import com.google.android.libraries.identity.googleid.GetGoogleIdOption\nimport com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption'
    )

# Replace the GetGoogleIdOption usage with GetSignInWithGoogleOption and GetGoogleIdOption fallback
old_code = """            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                
                .build()"""

new_code = """            // Force clearing credential state to avoid pre-selected accounts and force the chooser
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.d(TAG, "clearCredentialState error or ignored", e)
            }

            // Use GetSignInWithGoogleOption to force the full account chooser rather than the auto-select bottom sheet
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()"""

content = content.replace(old_code, new_code)

with open('app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt', 'w') as f:
    f.write(content)

