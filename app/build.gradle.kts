/*
 *     libchewingAndroidAppModule: libchewing Android App Module (AAR)
 *     Copyright (C) 2024.  YOU, Hui-Hong
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val projectName: String = "libchewing_android_app_module"
val versionName: String = "0.9.1.5"

android {
    namespace = "com.miyabi_hiroshi.app.${projectName}"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cFlags("-Wno-unused-function", "-Wno-unused-but-set-variable")
                cppFlags += ""
                targets("${projectName}")
            }
        }

        setProperty("archivesBaseName", "${projectName}_${versionName}")
    }

    buildTypes {
        release {
            isDefault = true
            // NOTICE: SHOULD ALWAYS be false here, because it's a library!
            isMinifyEnabled = false
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
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildToolsVersion = "35.0.0"
    ndkVersion = "28.1.13356709"

    val chewingLibraryPath: String = "${rootDir}/app/src/main/cpp/libs/libchewing"

    tasks.register<Exec>("prepareChewing") {
        workingDir(chewingLibraryPath)
        // This is just for task 'buildChewingData', other definitions are in cpp/CMakeLists.txt
        commandLine("cmake", "-B", "build/", "-DBUILD_INFO=false", "-DBUILD_TESTING=false", "-DWITH_SQLITE3=false", "-DCMAKE_BUILD_TYPE=Release")
    }

    val chewingDataFiles =
        listOf<String>("tsi.dat", "word.dat", "swkb.dat", "symbols.dat")

    tasks.register<Exec>("buildChewingData") {
        dependsOn("prepareChewing")
        workingDir("$chewingLibraryPath/build")
        commandLine("make", "data", "all_static_data")
    }

    tasks.register<Copy>("copyChewingDataFiles") {
        dependsOn("buildChewingData")
        for (chewingDataFile in chewingDataFiles) {
            from("$chewingLibraryPath/build/data/$chewingDataFile")
            into("$rootDir/app/src/main/assets")
        }
    }

    tasks.register<Exec>("rustupTargetAddAarch64LinuxAndroid") {
        onlyIf {
            try {
                val result = exec {
                    isIgnoreExitValue = true
                    commandLine("rustup", "-V")
                }
                result.exitValue != 0
            } catch (e: Exception) {
                return@onlyIf false
            }
        }
        commandLine("rustup", "target", "add", "aarch64-linux-android")
    }

    tasks.register<Exec>("rustupTargetAddArmv7LinuxAndroideabi") {
        onlyIf {
            try {
                val result = exec {
                    isIgnoreExitValue = true
                    commandLine("rustup", "-V")
                }
                result.exitValue != 0
            } catch (e: Exception) {
                return@onlyIf false
            }
        }
        commandLine("rustup", "target", "add", "armv7-linux-androideabi")
    }

    tasks.register<Exec>("rustupTargetAddI686LinuxAndroid") {
        onlyIf {
            try {
                val result = exec {
                    isIgnoreExitValue = true
                    commandLine("rustup", "-V")
                }
                result.exitValue != 0
            } catch (e: Exception) {
                return@onlyIf false
            }
        }
        commandLine("rustup", "target", "add", "i686-linux-android")
    }

    tasks.register<Exec>("rustupTargetAddX64LinuxAndroid") {
        onlyIf {
            try {
                val result = exec {
                    isIgnoreExitValue = true
                    commandLine("rustup", "-V")
                }
                result.exitValue != 0
            } catch (e: Exception) {
                return@onlyIf false
            }
        }
        commandLine("rustup", "target", "add", "x86_64-linux-android")
    }

    tasks.preBuild {
        dependsOn(
            "copyChewingDataFiles",
            "rustupTargetAddAarch64LinuxAndroid",
            "rustupTargetAddArmv7LinuxAndroideabi",
            "rustupTargetAddI686LinuxAndroid",
            "rustupTargetAddX64LinuxAndroid"
        )
    }

    tasks.register<Delete>("cleanChewingDataFiles") {
        for (chewingDataFile in chewingDataFiles) {
            file("$rootDir/app/src/main/assets/$chewingDataFile").delete()
        }
    }

    tasks.register<Exec>("execMakeClean") {
        onlyIf { file("$chewingLibraryPath/build/Makefile").exists() }
        workingDir("$chewingLibraryPath/build")
        commandLine("make", "clean")
        isIgnoreExitValue = true
    }

    tasks.register<Delete>("deleteChewingBuildDirectory") {
        onlyIf { file("$chewingLibraryPath/build/Makefile").exists() }
        delete("$chewingLibraryPath/build")
    }

    tasks.register<Delete>("deleteBuiltAarFile") {
        delete("$rootDir/app/build/outputs/aar/${projectName}-${versionName}.aar")
    }

    tasks.clean {
        dependsOn(
            "cleanChewingDataFiles",
            "execMakeClean",
            "deleteChewingBuildDirectory",
            "deleteBuiltAarFile"
        )
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