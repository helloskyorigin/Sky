import re

with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "r") as f:
    text = f.read()

text = text.replace(
"""                val updates = hashMapOf<String, Any>(
                    "lastLoginAt" to System.currentTimeMillis(),
                    "deviceModel" to android.os.Build.MODEL,
                    "androidVersion" to android.os.Build.VERSION.RELEASE,
                    "appVersion" to appVersion
                )""",
"""                val updates = hashMapOf<String, Any>(
                    "lastLoginAt" to System.currentTimeMillis(),
                    "language" to currentLanguage,
                    "theme" to currentTheme,
                    "deviceModel" to android.os.Build.MODEL,
                    "androidVersion" to android.os.Build.VERSION.RELEASE,
                    "appVersion" to appVersion
                )"""
)

with open("app/src/main/java/com/skyorigin/threatshieldai/AuthManager.kt", "w") as f:
    f.write(text)
