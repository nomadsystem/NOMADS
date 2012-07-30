// CCGlobals.h
// Paul Turowski. 2012.07.26.

#ifndef CCGLOBALS_H_
#define CCGLOBALS_H_

#define kCursorSpriteZValue 100
#define kCursorSpriteTagValue 10
#define kCursorIdleTimer 3.0f

typedef enum {
    kStateIdle,
    kStateActive
} ObjectState;

typedef enum {
    kObjectTypeNone,
    kTypeCursor
} GameObjectType;


#endif /* CCGLOBALS_H_ */

//=======================================>> AUDIO ITEMS

#define AUDIO_MAX_WAITTIME 150

typedef enum {
    kAudioManagerUninitialized=0,
    kAudioManagerFailed=1,
    kAudioManagerInitializing=2,
    kAudioManagerInitialized=100,
    kAudioManagerLoading=200,
    kAudioManagerReady=300

} GameManagerSoundState;

// Audio Constants
#define SFX_NOTLOADED NO
#define SFX_LOADED YES

//iOS macros to convert
//#define PLAYSOUNDEFFECT(...) \
//[[GameManager sharedGameManager] playSoundEffect:@#__VA_ARGS__]
//
//#define STOPSOUNDEFFECT(...) \
//[[GameManager sharedGameManager] stopSoundEffect:__VA_ARGS__]


#define TEST_SOUND "AuksalaqNomadsNote1.mp3"
