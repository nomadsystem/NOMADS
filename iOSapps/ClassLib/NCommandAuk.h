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
    REGISTER = 2,
    
    SYNTH_ENABLE = 6, //uGroove
    SYNTH_DISABLE = 7,//uGroove
    SYNTH_START = 8,//uGroove
    SYNTH_STOP = 9,//uGroove
    
    SET_DISCUSS_STATUS = 10,
    SET_CLOUD_STATUS = 11,
    SET_POINTER_STATUS = 12,
    SET_DROPLET_STATUS = 13,
    SET_DROPLET_VOLUME = 14,
    SET_DISCUSS_DISPLAY_STATUS = 15,
    SET_CLOUD_DISPLAY_STATUS = 16,
    SET_DISCUSS_ALPHA = 17,
    SET_CLOUD_ALPHA = 18,
    SET_POINTER_ALPHA = 19,
    
    SEND_SPRITE_THREAD_XY = 20,  // SoundSwarm
    SEND_SPRITE_XY = 21,
    
    
    SET_MAIN_VOLUME = 22,
    
    SET_NOTE_STATUS = 23,
    SET_NOTE_VOLUME = 24,
    
    SEND_PROMPT_ON = 30,
    SEND_PROMPT_OFF = 31,
    
    
    
    
} NCommands;

#endif