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
    // 前32位存储type；后32存储data
    return ((long)value.dataType << 32) | value.data;
}