//
//  main.c
//  ImSelect
//
//  Created by 柯瑞阳 on 2017/5/18.
//  Copyright © 2017年 柯瑞阳. All rights reserved.
//


#include <Carbon/Carbon.h>

typedef unsigned int InputSourceType;

#define BY_ID 1
#define BY_NAME 2

void selectInputSource(char*);
void listAllInputSource();
TISInputSourceRef getInputSource(char*);
void showCurrentInputSource();
char* CFStringToChars(CFStringRef);

int
main (int argc, char * argv[])
{
    int opt;
    char* input = argv[argc-1];
    int handled = 0;
    while ((opt = getopt(argc, argv, "slc")) != -1)
    {
        switch (opt)
        {
            case 's':
                selectInputSource(input);
                break;
            case 'l':
                listAllInputSource();
                break;
            default:
                showCurrentInputSource();
                break;
        }
        handled = 1;
    }
    
    if(!handled){
//        showCurrentInputSource();
        listAllInputSource();

    }
    
}



void
selectInputSource(char* sourceId){
    
    TISInputSourceRef tISInputSource = getInputSource(sourceId);
    
    if (tISInputSource)
    {
        CFBooleanRef enabled = TISGetInputSourceProperty(tISInputSource, kTISPropertyInputSourceIsEnabled);
        if (enabled == kCFBooleanFalse)
            TISEnableInputSource(tISInputSource);
        TISSelectInputSource(tISInputSource);
        CFRelease(tISInputSource);
    }
}

void
listAllInputSource(){
    CFArrayRef allImesArray = TISCreateInputSourceList(NULL, false);
    CFIndex ImesCount = CFArrayGetCount(allImesArray);
    if (ImesCount >= 1) {
        for (int i=0; i<ImesCount; i++) {
            
            TISInputSourceRef tisr = (TISInputSourceRef) CFArrayGetValueAtIndex(allImesArray, i);
            
            CFStringRef originalSourceId;
            CFStringRef originalName;
            
            originalSourceId = TISGetInputSourceProperty(tisr, kTISPropertyInputSourceID);
            originalName = TISGetInputSourceProperty(tisr, kTISPropertyLocalizedName);
            
            int targetSourceLength = (int)CFStringGetLength(originalSourceId) * 4 + 1;
            char targetSourceStr[targetSourceLength];
            CFStringGetCString(originalSourceId, targetSourceStr, targetSourceLength, kCFStringEncodingUTF8);
            
            int targetNameLength = (int)CFStringGetLength(originalName) * 4 + 1;
            char targetNameStr [targetNameLength];
            CFStringGetCString(originalName, targetNameStr, targetNameLength, kCFStringEncodingUTF8);
            printf("%s:%s|",targetSourceStr,targetNameStr);
            CFRelease(originalSourceId);
            CFRelease(originalName);
            CFRelease(tisr);
        }
    }
}

void
showCurrentInputSource(){
    CFStringRef name;
    TISInputSourceRef tISInputSource = TISCopyCurrentKeyboardInputSource();
    name = TISGetInputSourceProperty(tISInputSource, kTISPropertyInputSourceID);
    char* out = CFStringToChars(name);
    printf("%s",out);
}

char* CFStringToChars(CFStringRef cfstring){
    
    CFIndex cfstringLen = CFStringGetLength(cfstring);
    CFIndex sourceMaxLen = CFStringGetMaximumSizeForEncoding(cfstringLen, kCFStringEncodingUTF8) + 1;
    char *stringBuf = (char *)malloc(sourceMaxLen);
    if (!CFStringGetCString(cfstring, stringBuf, sourceMaxLen,kCFStringEncodingUTF8)) {
        free(stringBuf);
        return NULL;
    }
    return stringBuf;
}


TISInputSourceRef
getInputSource(char *cname)
{
    CFStringRef name = CFStringCreateWithCString(kCFAllocatorDefault, cname, kCFStringEncodingUTF8);
    
    CFStringRef keys[1] = {};
    keys[0] = kTISPropertyInputSourceID;
    
    CFStringRef values[] = { name };
    CFDictionaryRef dict = CFDictionaryCreate(kCFAllocatorDefault, (const void **)keys, (const void **)values, 1, NULL, NULL);
    CFArrayRef array = TISCreateInputSourceList(dict, true);
    
    CFRelease(dict);
    CFRelease(name);
    
    if (!array)
    {
        fprintf(stderr, "No such text input source: %s\n", cname);
        return NULL;
    }
    
    TISInputSourceRef tis = (TISInputSourceRef) CFArrayGetValueAtIndex(array, 0);
    
    
    CFRetain(tis);
    CFRelease(array);
    
    return tis;
}
