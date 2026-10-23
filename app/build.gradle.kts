// build.gradle → Module (módulo :app), como visto na aula.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "br.com.seudominio.filmesfavoritos" // troque pelo seu pacote
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.seudominio.filmesfavoritos" // troque pelo seu pacote
        minSdk = 24
        // targetSdk 34 de propósito: a partir do 35 o Android 15 força o modo edge-to-edge,
        // que exige tratar "insets" das barras do sistema (assunto fora do conteúdo das aulas).
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        // Gera uma classe de binding para cada layout (ActivityMainBinding, ItemFilmeBinding...),
        // eliminando o findViewById e dando segurança de tipos em tempo de compilação.
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // RecyclerView: Adapter + ViewHolder + LayoutManager (aula 5)
    implementation(libs.androidx.recyclerview)

    // MVVM: ViewModel + LiveData, e by viewModels() na Activity
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    // Coil: carregamento de imagens por URL (import coil.load)
    implementation(libs.coil)
}
