//
//  NGlobals.h
//
//  Initial version, DJT on 6/22/12.
//
#ifndef NGLOBALS_H
#define NGLOBALS_H
// Set max # of Sand delegates here
#define MAX_DELEGATES 10

// DEBUG vars, used by MACRO functions below
//      - comment out each to disable printout
// #define CLIENT_DEBUG
#define LIBRARY_DEBUG
// #define SERVER_DEBUG
//#define SOME_OTHER_DEBUG

// Use these in place of NSLog from now on

#ifdef CLIENT_DEBUG
#   define CLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
#   define CLog(...)
#endif

#ifdef LIBRARY_DEBUG
#   define LLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
#   define LLog(...)
#endif

#ifdef SERVER_DEBUG
#   define SLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
#   define SLog(...)
#endif


//NDataType
typedef enum {
    NODATA = 0,
    CHAR = 1,
    UINT8 = 2,
    INT32 = 3,
    FLOAT32 = 4,
} NGDataType;

//Port Number
typedef enum {
    SERVER_PORT = 52920,
    SERVER_PORT_DT = 52921,
    SERVER_PORT_SK = 52922,
    SERVER_PORT_PT = 52923,
    SERVER_PORT_MB = 52924,
} NGPort;

// Convenience
typedef enum {
    OFF=0,
    ON=1
} offOn;

//STK Todo: Add enum for debug printing

@interface NGlobals : NSObject
{   
    // We may or may not ever use these, C/L/SLog macros above are better
    //      - keeping for now to preserve similarity to Java SAND code
    BOOL serverDebugLevel;    
    BOOL libraryDebugLevel;    
    BOOL clientDebugLevel;
}

@end

#endif
