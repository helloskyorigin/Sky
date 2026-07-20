import re

with open("app/src/main/java/com/skyorigin/threatshieldai/GoogleSignInScreen.kt", "r") as f:
    content = f.read()

# Replace the button content
button_content_regex = re.compile(r'Row\(\s*verticalAlignment = Alignment\.CenterVertically,\s*horizontalArrangement = Arrangement\.Center\s*\)\s*\{\s*Image\([^}]+\)\s*Spacer\([^}]+\)\s*Text\([^}]+\)\s*\}', re.DOTALL)

new_button_content = """if (isSigningIn) {
                        CircularProgressIndicator(
                            color = primaryBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isConsentChecked) textPrimary else textSecondary.copy(alpha = 0.5f)
                            )
                        }
                    }"""
                    
content = button_content_regex.sub(new_button_content, content)

# Remove the animated visibility block
sheet_regex = re.compile(r'// Animated Simulated Account Picker Sheet \(Google Auth UI Simulation\).*?}\s*}\s*}\s*}\s*}\s*}\s*}', re.DOTALL)
content = sheet_regex.sub('', content)

# Add the error message below the button
spacer_after_button_regex = re.compile(r'Spacer\(modifier = Modifier.height\(24.dp\)\)\s*}\s*}\s*}\s*$', re.DOTALL)
error_ui = """if (authState is AuthUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (authState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
"""

content = spacer_after_button_regex.sub(error_ui, content)

with open("app/src/main/java/com/skyorigin/threatshieldai/GoogleSignInScreen.kt", "w") as f:
    f.write(content)
