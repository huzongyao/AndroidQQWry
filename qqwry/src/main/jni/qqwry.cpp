
#include "IPLocator.hpp"

#ifdef __cplusplus
extern "C" {
#endif

using namespace std;

static jbyteArray string2jbyteArray(JNIEnv *env, string str);

IPLocator* ipLocator;

/*
 * Class:     com_hzy_qqwry_QQWryAnd
 * Method:    jniOpen
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_hzy_qqwry_QQWryAnd_jniOpen
		(JNIEnv *env, jobject, jstring path){
	const char* cpath = env->GetStringUTFChars(path, NULL);
	LOGD("open file:[%s]", cpath);
	ipLocator = new IPLocator(cpath);
	env->ReleaseStringUTFChars(path, cpath);
}

/*
 * Class:     com_hzy_qqwry_QQWryAnd
 * Method:    jniGetVersionBytes
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_hzy_qqwry_QQWryAnd_jniGetVersionBytes
		(JNIEnv *env, jobject){
	string version = ipLocator->getVersion();
	return string2jbyteArray(env, version);
}

/*
 * Class:     com_hzy_qqwry_QQWryAnd
 * Method:    jniGetIpAddrBytes
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL JNICALL Java_com_hzy_qqwry_QQWryAnd_jniGetIpAddrBytes
		(JNIEnv *env, jobject, jstring ip){
	const char* cip = env->GetStringUTFChars(ip, NULL);
	string addr = ipLocator->getIpAddr(cip);
	return string2jbyteArray(env, addr);
}

/*
 * Class:     com_hzy_qqwry_QQWryAnd
 * Method:    jniGetIpRangeBytes
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_hzy_qqwry_QQWryAnd_jniGetIpRangeBytes
		(JNIEnv *env, jobject, jstring ip){
	const char* cip = env->GetStringUTFChars(ip, NULL);
	string range = ipLocator->getIpRange(cip);
	return string2jbyteArray(env, range);
}

/*
 * Class:     com_hzy_qqwry_QQWryAnd
 * Method:    jniGetIpCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_hzy_qqwry_QQWryAnd_jniGetIpCount
		(JNIEnv *, jobject){
	return (int)ipLocator->getTotal();
}

/*
 * Class:     com_hzy_qqwry_QQWryAnd
 * Method:    jniClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_hzy_qqwry_QQWryAnd_jniClose(JNIEnv *, jobject){
	LOGD("jniClose()");
	delete ipLocator;
	ipLocator = 0;
}

static jbyteArray string2jbyteArray(JNIEnv *env, string str){
	const char* cstr = str.c_str();
	LOGD("string2jbyteArray(),string len[%ld]",strlen(cstr));
	jbyteArray ret = env->NewByteArray((jsize) strlen(cstr));
	env->SetByteArrayRegion(ret, 0, (jsize) strlen(cstr), (jbyte*)cstr);
	return ret;
}

#ifdef __cplusplus
}
#endif