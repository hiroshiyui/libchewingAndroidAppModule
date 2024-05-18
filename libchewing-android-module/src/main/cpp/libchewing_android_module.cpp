#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_miyabi_1hiroshi_app_libchewing_1android_1module_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}