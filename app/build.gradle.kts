plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("kotlin-kapt")
	id("com.google.dagger.hilt.android")
}

android {
	namespace = "com.example.rageamp"
	compileSdk = 34
	
	defaultConfig {
		applicationId = "com.example.rageamp"
		minSdk = 24
		targetSdk = 33
		versionCode = 1
		versionName = "1.0"
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	
	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
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
	
	buildFeatures {
		viewBinding = true
		dataBinding = true
	}
}

dependencies {
	
	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.appcompat:appcompat:1.7.0")
	implementation("com.google.android.material:material:1.12.0")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
	
	// navigation component
	val nav_version = "2.7.7"
	implementation("androidx.navigation:navigation-fragment:$nav_version")
	implementation("androidx.navigation:navigation-ui:$nav_version")
	
	// Dagger Hilt
	implementation("com.google.dagger:hilt-android:2.52")
	kapt("com.google.dagger:hilt-android-compiler:2.52")
	
	// Kotlin Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
	
	// Lifecycle
	val lifecycle_version = "2.8.5"
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
	
	// Glide
	implementation("com.github.bumptech.glide:glide:4.15.1")
}

kapt {
	correctErrorTypes = true
}
