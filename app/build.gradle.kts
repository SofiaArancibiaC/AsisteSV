plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp) // agregar
    alias(libs.plugins.room) // aplicar aquí el alias del plugin
}

android {
    namespace = "com.js.smartvolt.asiste"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.js.smartvolt.asiste"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // --- BLOQUE OBLIGATORIO PARA EL PLUGIN DE ROOM ---
    room {
        schemaDirectory("$projectDir/schemas")
    }
    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx) // Soporte para Corrutinas y Flow con Room
    ksp(libs.room.compiler)       // Procesador de anotaciones usando KSP
    // --- VIEWMODEL PARA COMPOSE ---
    implementation(libs.compose.viewmodel) // Permite usar 'viewModel()' en Compose
    // --- ICONOS EXTENDIDOS DE MATERIAL ---
    implementation(libs.compose.icons.extended)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kizitonwose.calendar.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

}