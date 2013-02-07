//
//  NCommandAuk.h
//
//  Initial version, DJT on 6/22/12.
//

#ifndef NCOMMANDMAIN_H
#define NCOMMANDMAIN_H

//NAukCommands
typedef enum {
    NOOP = 0,

    SEND_MESSAGE = 1,
    REGISTER = 2,
    LOGIN_STATUS = 3,
    LOGIN = 4,
    
    MOD_STATE_START = 11,
    SET_DISCUSS_STATUS = 11,
    SET_CLOUD_STATUS = 12,
    SET_POLL_STATUS = 13,
    SET_MOSAIC_STATUS = 14,
    SET_POINTER_STATUS = 15,  // DT: these still need to be fixed, much already cleaned
    SET_SWARM_STATUS = 15,
    SET_UGROOVE_STATUS = 16,
    MOD_STATE_END = 16,
    
    SEND_DISCUSS_PROMPT = 20,
    SEND_CLOUD_PROMPT = 21,
    SEND_POLL_PROMPT = 22,
    
    QUESTION_TYPE_YES_NO = 31,
    QUESTION_TYPE_ONE_TO_TEN = 32,
    QUESTION_TYPE_A_TO_E = 33,
    VOTE = 34, //command from Instructor Panel to PollStudent ****STK 6/18/12
    
    SYNTH_ENABLE_STATUS = 41, //uGroove
    SYNTH_START_STOP = 42,//uGroove
    
    SEND_SPRITE_XY = 51,  // SoundSwarm
    SEND_THREAD_ID = 52,
    
    GET_SOUNDFILE_LIST = 61,  // Sequencer/Mosaic

} NCommands;

#endif