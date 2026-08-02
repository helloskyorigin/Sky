import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
  alias(libs.plugins.firebase.crashlytics)
}

fun getSecret(key: String, defaultValue: String): String {
  val envValue = System.getenv(key)
  if (!envValue.isNullOrEmpty()) {
    return envValue.replace("\"", "").trim()
  }
  val envFile = file("${rootDir}/.env")
  val envExampleFile = file("${rootDir}/.env.example")
  val properties = Properties()
  if (envFile.exists()) {
    val stream = envFile.inputStream()
    try {
      properties.load(stream)
    } finally {
      stream.close()
    }
  } else if (envExampleFile.exists()) {
    val stream = envExampleFile.inputStream()
    try {
      properties.load(stream)
    } finally {
      stream.close()
    }
  }
  return properties.getProperty(key)?.replace("\"", "")?.trim() ?: defaultValue
}

fun getAdMobSecret(primaryKey: String, secondaryKey: String, defaultValue: String): String {
  val v1 = getSecret(primaryKey, "")
  if (v1.isNotEmpty()) return v1
  val v2 = getSecret(secondaryKey, "")
  if (v2.isNotEmpty()) return v2
  return defaultValue
}

android {
  namespace = "com.skyorigin.threatshieldai"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.skyorigin.threatshieldai"
    minSdk = 24
    targetSdk = 36
    versionCode = 3
    versionName = "1.0.2"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    buildConfigField("String", "URLSCAN_API_KEY", "\"\"")
    manifestPlaceholders["adMobAppId"] = "ca-app-pub-3940256099942544~3347511713"
  }

  signingConfigs {
    create("release") {
      storeFile = file("${rootDir}/threatshield-upload-key.jks")
      storePassword = "threatshield123"
      keyAlias = "threatshield-upload"
      keyPassword = "threatshield123"
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = true
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
      
      val releaseAppId = getAdMobSecret("RELEASE_ADMOB_APP_ID", "ADMOB_APP_ID", "ca-app-pub-9554102514624306~1748117938")
      val releaseBannerId = getAdMobSecret("RELEASE_BANNER_AD_UNIT_ID", "BANNER_AD_UNIT_ID", "ca-app-pub-9554102514624306/8741615708")
      val releaseRewardedId = getAdMobSecret("RELEASE_REWARDED_AD_UNIT_ID", "REWARDED_AD_UNIT_ID", "ca-app-pub-9554102514624306/6963564405")

      manifestPlaceholders["adMobAppId"] = releaseAppId
      buildConfigField("String", "BANNER_AD_UNIT_ID", "\"${releaseBannerId}\"")
      buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"${releaseRewardedId}\"")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
      manifestPlaceholders["adMobAppId"] = "ca-app-pub-3940256099942544~3347511713"
      buildConfigField("String", "BANNER_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
      buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  packaging {
    resources {
      excludes += "**/LICENSE.txt"
      excludes += "**/LICENSE"
      excludes += "**/NOTICE"
      excludes += "**/*.proto"
      excludes += "META-INF/*.kotlin_module"
    }
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.browser)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  // implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)
 
  // Firebase Auth with Google Sign-In
  // implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.crashlytics)
  implementation(libs.firebase.firestore)
  implementation(libs.play.review)
  implementation(libs.play.review.ktx)
  implementation(libs.play.services.ads)
  implementation(libs.user.messaging.platform)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  // implementation(libs.logging.interceptor)
  // implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  // implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  // "ksp"(libs.moshi.kotlin.codegen)
}
