//
//  NCommandAuk.h
//
//  Initial version, DJT on 6/22/12.
//

#ifndef NCOMMANDAUK_H
#define NCOMMANDAUK_H

//NAukCommands
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

} NCommands;

#endif