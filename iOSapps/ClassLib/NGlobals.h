//
//  NGlobals.h
//
//  Initial version, DJT on 6/22/12.
//

#define MAX_DELEGATES 10

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
    RES1 = 0,
    SEND_MESSAGE = 1,
    QUESTION_TYPE_YES_NO = 2,
    QUESTION_TYPE_ONE_TO_TEN = 3,
    QUESTION_TYPE_A_TO_E = 4,
    VOTE = 5, //command from Instructor Panel to PollStudent ****STK 6/18/12
    SYNTH_ENABLE = 6, //uGroove
    SYNTH_DISABLE = 7,//uGroove
    SYNTH_START = 8,//uGroove
    SYNTH_STOP = 9,//uGroove
    SEND_SPRITEYX = 10,  // SoundSwarm
} NGCommand;

//NDataType
typedef enum {
    FOO = 0,
    BYTE = 1,
    INT = 2,
    FLOAT = 3,
    DOUBLE = 4,
    NO_DATA = 5,
} NGDataType;

//Port Number
typedef enum {
    SERVER_PORT = 52910,
    SERVER_PORT_DT = 52911,
    SERVER_PORT_SK = 52912,
    SERVER_PORT_PT = 52913,
    SERVER_PORT_MB = 52914,
} NGPort;

//STK Todo: Add enum for debug printing

@interface NGlobals : NSObject
{   
    //    NGAppID appID,
    
}

//@property (nonatomic) NGAppID appID,


@end


