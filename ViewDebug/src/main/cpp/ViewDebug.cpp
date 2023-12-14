// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("surfaceproject");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("surfaceproject")
//      }
//    }

#include <EGL/egl.h>
#include <jni.h>
#include <cstdio>
#include <string>
#include <iostream>
#include "Res_value.h"
#include <jni.h>
#include "StringToCompile.h"
#include <android/log.h>


/*
extern "C"
JNIEXPORT jlong

Java_com_example_viewdebug_xml_struct_writer_helper_ExternalFunction_stringToFloat(JNIEnv *env, jstring name) {
    auto *t = new jchar[5];
    return env->NewStringUTF("sdfa我");
}
*/



extern "C"
JNIEXPORT jlong
Java_com_example_viewdebug_xml_struct_writer_helper_ExternalFunction_stringToFloat(JNIEnv * env, jobject thiz, jstring string
) {


    const char * str = env->GetStringUTFChars(string, 0);
    size_t len = strlen(str);
    Res_value value{};
    stringToFloat(str, len, &value);
    // __android_log_print(ANDROID_LOG_INFO, "native-log", "type=%s",to_string(value.dataType).c_str());
    // __android_log_print(ANDROID_LOG_INFO, "native-log", "data=%s",to_string(value.data).c_str());
    // 前32位存储type；后32存储data
    // 必须转换为64位的数值类型，否则导致armeabi-v7a类型的so移位异常（32位的cpu）
    ::int64_t dataType = value.dataType;
    return (dataType << 32) | value.data;
}