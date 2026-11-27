plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.dmd.pluginbuilder"
    compileSdk = 35
    defaultConfig { applicationId = "com.dmd.pluginbuilder"; minSdk = 23; targetSdk = 35; versionCode = 22; versionName = "22.0" }
}
