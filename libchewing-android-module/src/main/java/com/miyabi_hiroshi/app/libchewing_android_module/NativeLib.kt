package com.miyabi_hiroshi.app.libchewing_android_module

class NativeLib {

    /**
     * A native method that is implemented by the 'libchewing_android_module' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'libchewing_android_module' library on application startup.
        init {
            System.loadLibrary("libchewing_android_module")
        }
    }
}