// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    // id (")java-gradle-plugin(")
}
tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
