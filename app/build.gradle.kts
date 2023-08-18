plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
// 添加自动打包脚本
apply("${project.projectDir}/auto-skin-pack.gradle")

android {
    compileSdk = 32

    defaultConfig {

        minSdk = 23
        targetSdk = 32
        // versionCode(1)
        // versionName("1.0")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildConfigField("int", "age", "1")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":App:SkinCore"))
    implementation(project(":ViewDebug"))
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("com.squareup.leakcanary:leakcanary-android:2.9.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.2")
}
val f = (ext.get("buildSkinModule") as org.codehaus.groovy.runtime.MethodClosure).invoke(
    "skinPack-cartoon",
    "7",
)
println(f)
// var skinPack = buildSkinModule("skinPack-cartoon", "7")
// skinPack.skinInput = "intermediates/apk/debug/skinPack-cartoon-debug.apk"
