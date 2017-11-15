/*
http://spadger.blog.com.cn
Original code Copyright (C) 2008 spadger <echo.xjtu@gmail.com>

This software is provided 'as-is', without any express or implied
warranty. In no event will the authors be held liable for any
damages arising from the use of this software.

Permission is granted to anyone to use this software for any
purpose, including commercial applications, and to alter it and
redistribute it freely, subject to the following restrictions:

1. The origin of this software must not be misrepresented; you must
not claim that you wrote the original software. If you use this
software in a product, an acknowledgment in the product documentation
would be appreciated but is not required.

2. Altered source versions must be plainly marked as such, and
must not be misrepresented as being the original software.

3. This notice may not be removed or altered from any source
distribution.
*/

#include <jni.h>
#include <android/log.h>

#define LOG_TAG "NATIVE.LOG"

#ifdef JNI_LOG
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__)
#else
#define LOGD(...) do{}while(0)
#define LOGI(...) do{}while(0)
#define LOGW(...) do{}while(0)
#define LOGE(...) do{}while(0)
#define LOGF(...) do{}while(0)
#endif


#ifndef _IP_LOCATOR_H_
#define _IP_LOCATOR_H_

#include <string>
#include <iostream>
#include <fstream>

using std::string;
using std::streamsize;

class IPLocator
{
public:
	IPLocator(const string& ipdb_name);
	~IPLocator();
	string getVersion();
	string getIpAddr(const string& ip);
	string getIpRange(const string& ip);
	unsigned int getTotal();
private:
	string getIpAddr(unsigned int ip);
	string getIpRange(unsigned int ip);
	static unsigned int getIpFromString(const string& ip);
	static string getIpString(unsigned int ip);
	static unsigned int bytes2integer(unsigned char *ip, int count);
	void readFromFile(streamsize offset, unsigned char *buf,int len);
	string readStringFromFile(streamsize offset);
	string getAddr(streamsize offset);
	string getAreaAddr(streamsize offset);
	void setIpRange(unsigned int rec_no);
private:
	std::ifstream ipdb;
	unsigned int first_index;
	unsigned int last_index;
	unsigned int index_count;
	unsigned int cur_start_ip;
	unsigned int cur_start_ip_offset;
	unsigned int cur_end_ip;
};

#endif