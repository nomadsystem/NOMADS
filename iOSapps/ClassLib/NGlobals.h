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
#define CLIENT_DEBUG
#define LIBRARY_DEBUG
#define SERVER_DEBUG
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


//AppIDs
typedef enum {
    SERVER = 0,
    INSTRUCTOR_PANEL = 1,
    C_INSTRUCTOR_PANEL = 2,
    STUDENT_PANEL = 3,
    C_STUDENT_PANEL = 4,
    CENSOR = 5,
    MONITOR = 6,
    
    //DEBUG = 10,
    
    WEB_CHAT = 20,
    C_WEB_CHAT = 21,
    DISCUSS_PROMPT = 22,
    C_DISCUSS_PROMPT = 23,
    INSTRUCTOR_DISCUSS = 24,
    C_INSTRUCTOR_DISCUSS = 25,
    
    TEXT_CHAT = 30,
    C_TEXT_CHAT = 31,
    
    LOGIN = 40,
    C_LOGIN = 41,
    
    CLOUD_DISPLAY = 50,
    C_CLOUD_DISPLAY = 51,
    CLOUD_CHAT = 52,
    C_CLOUD_CHAT = 53,
    CLOUD_PROMPT = 54,
    C_CLOUD_PROMPT = 55,
    
    TEACHER_POLL = 60,
    C_TEACHER_POLL = 61,
    STUDENT_POLL = 62,
    C_STUDENT_POLL = 63,
    DISPLAY_POLL = 64,
    C_DISPLAY_POLL = 65,
    
    SNAKE_GAME = 70,
    C_SNAKE_GAME = 71,
    
    INSTRUCTOR_SEQUENCER = 80,
    C_INSTRUCTOR_SEQUENCER = 81,
    STUDENT_SEQUENCER = 82,
    C_STUDENT_SEQUENCER = 83,
    
    STUDENT_SAND_POINTER = 90,
    C_STUDENT_SAND_POINTER = 91,
    INSTRUCTOR_SAND_POINTER = 92,
    C_INSTRUCTOR_SAND_POINTER = 93,
    
    SOUND_SWARM = 100,
    C_SOUND_SWARM = 101,
    SPHERE_MOVER = 102,
    
    STUD_EMRG_SYNTH = 110,
    C_STUD_EMRG_SYNTH = 111,
    INSTRUCT_EMRG_SYNTH_PROMPT = 112,
    C_INSTRUCT_EMRG_SYNTH_PROMPT = 113,
    
} NGAppID;

//NCommand 
typedef enum {
    NOOP = 0,

    SEND_MESSAGE = 1,

    QUESTION_TYPE_YES_NO = 2,
    QUESTION_TYPE_ONE_TO_TEN = 3,
    QUESTION_TYPE_A_TO_E = 4,
    VOTE = 5, //command from Instructor Panel to PollStudent ****STK 6/18/12

    SYNTH_ENABLE = 6, //uGroove
    SYNTH_DISABLE = 7,//uGroove
    SYNTH_START = 8,//uGroove
    SYNTH_STOP = 9,//uGroove
    REGISTER = 10,
    LOGIN_STATUS = 11,
    SEND_SPRITE_XY = 20,  // SoundSwarm

} NGCommand;

//NDataType
typedef enum {
    NO_DATA = 0,
    CHAR = 1,
    UINT8 = 2,
    INT32 = 3,
    FLOAT32 = 4,
} NGDataType;

//Port Number
typedef enum {
    SERVER_PORT = 52910,
    SERVER_PORT_DT = 52911,
    SERVER_PORT_SK = 52912,
    SERVER_PORT_PT = 52913,
    SERVER_PORT_MB = 52914,
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
