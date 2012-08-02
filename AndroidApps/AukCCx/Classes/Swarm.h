#ifndef __SWARM_SCENE_H__
#define __SWARM_SCENE_H__

#include "cocos2d.h"
#include "SimpleAudioEngine.h"
#include "Cursor.h"

// setup JNI for native -> Java
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform/android/jni/JniHelper.h"
#include <jni.h>
#define CLASS_OPEN_NAME "com/nomads/Join"
#endif

USING_NS_CC;
using namespace CocosDenshion;

class Swarm : public cocos2d::CCLayer
{
	CCPoint touchPoint;
	SimpleAudioEngine* soundEngine;

	void sendData();
	// Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
	virtual bool init();
	void loadAudio();
public:
	Cursor* cursorSprite;
	CCSprite* background;

    // there's no 'id' in cpp, so return the class pointer
    static cocos2d::CCScene* scene();
    
    // a selector callback
    void menuCloseCallback(CCObject* pSender);

    // implement the "static node()" method manually
    LAYER_CREATE_FUNC(Swarm);

    virtual void onEnter();
    virtual void onExit();
    virtual bool ccTouchBegan(CCTouch* touch, CCEvent* event);
    virtual void ccTouchMoved(CCTouch* touch, CCEvent* event);
	virtual void ccTouchEnded(CCTouch* touch, CCEvent* event);
    virtual void touchDelegateRetain();
    virtual void touchDelegateRelease();
};

#endif // __SWARM_SCENE_H__
