// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
	repositories {
		google()
		mavenCentral()
	}
	
	dependencies {
		val nav_version = "2.5.3"
		classpath("com.android.tools.build:gradle:7.0.4")
		classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
	}
}

plugins {
	id("com.android.application") version "8.1.3" apply false
	id("org.jetbrains.kotlin.android") version "1.9.0" apply false
	id("com.google.dagger.hilt.android") version "2.52" apply false
}
