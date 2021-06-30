#include <jni.h>
#include <string>
#include <iostream>
//getDbUrl
extern "C" JNIEXPORT jstring JNICALL
Java_in_cricketexchange_app_cricketexchange_MyApplication_a(
        JNIEnv *env,
        jobject /* this */,
        jint r) {
    std::string key = "https://cricket-exchange-";
    r+=2;
    key += std::to_string(r);
    key+=".firebaseio.com";
    return env->NewStringUTF(key.c_str());
}

// testingAppCheck
extern "C" JNIEXPORT jstring JNICALL
Java_in_cricketexchange_app_cricketexchange_GetLiveMatches2Firebase_a(
        JNIEnv *env,
        jobject /* this */) {
    std::string key = "testingAppCheck";
    return env->NewStringUTF(key.c_str());
}