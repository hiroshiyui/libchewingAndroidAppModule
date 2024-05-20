plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val versionName: String = "0.8.1"

android {
    namespace = "com.miyabi_hiroshi.app.libchewing_android_module"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cFlags("-Wno-unused-function", "-Wno-unused-but-set-variable")
                cppFlags("")
                targets("libchewing", "libchewing_android_module")
            }
        }

        setProperty("archivesBaseName", "${project.name}_${versionName}")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.24.0+"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    ndkVersion = "26.1.10909125"
    buildToolsVersion = "34.0.0"


    val chewingLibraryPath: String = "${rootDir}/libchewing-android-module/src/main/cpp/libs/libchewing"

    tasks.register<Exec>("prepareChewing") {
        workingDir(chewingLibraryPath)
        commandLine("cmake", "--preset", "c99-release", "-DBUILD_SHARED_LIBS=OFF", ".")
    }

    val chewingDataFiles =
        listOf<String>("dictionary.dat", "index_tree.dat", "pinyin.tab", "swkb.dat", "symbols.dat")

    tasks.register<Exec>("buildChewingData") {
        dependsOn("prepareChewing")
        workingDir("$chewingLibraryPath/build")
        commandLine("make", "data", "all_static_data")
    }

    tasks.register<Copy>("copyChewingDataFiles") {
        dependsOn("buildChewingData")
        for (chewingDataFile in chewingDataFiles) {
            from("$chewingLibraryPath/build/data/$chewingDataFile")
            into("$rootDir/libchewing-android-module/src/main/assets")
        }
    }

    tasks.preBuild {
        dependsOn("copyChewingDataFiles")
    }

    tasks.register<Delete>("cleanChewingDataFiles") {
        for (chewingDataFile in chewingDataFiles) {
            file("$rootDir/libchewing-android-module/src/main/assets/$chewingDataFile").delete()
        }
    }

    tasks.register<Exec>("execMakeClean") {
        onlyIf { file("$chewingLibraryPath/build/Makefile").exists() }
        workingDir("$chewingLibraryPath/build")
        commandLine("make", "clean")
        isIgnoreExitValue = true
    }

    tasks.clean {
        dependsOn("cleanChewingDataFiles", "execMakeClean")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}