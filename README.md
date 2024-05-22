# libchewingAndroidAppModule: libchewing Android App Module (AAR)

# About 這是啥？

The libchewingAndroidAppModule is a byproduct of the [Guileless Bopomofo](https://github.com/hiroshiyui/GuilelessBopomofo) project. It packages the [Chewing Library (libchewing)](https://github.com/chewing/libchewing) into an Android App Module (AAR) format. This modular approach facilitates development and component reuse.

libchewingAndroidAppModule 是[樸實注音鍵盤](https://github.com/hiroshiyui/GuilelessBopomofo)專案的副產品，將[新酷音函式庫](https://github.com/chewing/libchewing)打包為 Android App Module (AAR) 格式，透過模組化途徑方便開發與元件再利用。

# Build 組建

1. `git clone --recursive https://github.com/hiroshiyui/libchewingAndroidAppModule.git`
2. `cd libchewingAndroidAppModule/`
3. `./gradlew tasks bundleDebugAar bundleReleaseAar`

The built AARs will be located at _app/build/outputs/aar/_

# Acknowledgements 感謝有您

* [Chewing contributors](http://chewing.im/about.html)
