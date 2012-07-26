#ifndef __SWARM_SCENE_H__
#define __SWARM_SCENE_H__

#include "cocos2d.h"
#include "SimpleAudioEngine.h"
#include "Cursor.h"

USING_NS_CC;
using namespace CocosDenshion;

class Swarm : public cocos2d::CCLayer
{
	CCPoint touchPoint;
	SimpleAudioEngine* soundEngine;

	// Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
	virtual bool init();
	void loadAudio();
public:
	Cursor* cursorSprite;

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
