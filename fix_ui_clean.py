with open("app/src/main/java/com/skyorigin/threatshieldai/GoogleSignInScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if "Animated Simulated Account Picker Sheet" in line:
        break
    new_lines.append(line)

new_lines.append("""
        if (authState is AuthUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp)
            )
        }
    }
}
""")

with open("app/src/main/java/com/skyorigin/threatshieldai/GoogleSignInScreen.kt", "w") as f:
    f.writelines(new_lines)
