import re

with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

target = """  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/threatshield-upload-key.jks"
      val kFile = file(keystorePath)
      if (kFile.exists()) {
        storeFile = kFile
        storePassword = System.getenv("STORE_PASSWORD") ?: ""
        keyAlias = System.getenv("KEY_ALIAS") ?: "threatshield-upload"
        keyPassword = System.getenv("KEY_PASSWORD") ?: ""
      } else {
        storeFile = file("${rootDir}/debug.keystore")
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
      }
    }"""

replacement = """  signingConfigs {
    create("release") {
      storeFile = file("${rootDir}/threatshield-upload-key.jks")
      storePassword = "threatshield123"
      keyAlias = "threatshield-upload"
      keyPassword = "threatshield123"
    }"""

new_content = content.replace(target, replacement)
with open('app/build.gradle.kts', 'w') as f:
    f.write(new_content)
