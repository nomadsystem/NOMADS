//Cursor.h
//Paul Turowski. 2012.07.23

#include "AnimatedObject.h"

#ifndef CURSOR_H_
#define CURSOR_H_

USING_NS_CC;

class Cursor : cocos2d::CCSprite
{
public:
	Cursor();
	virtual ~Cursor();
	virtual bool ccTouchBegan(CCTouch* touch, CCEvent* event);
	virtual void ccTouchMoved(CCTouch* touch, CCEvent* event);
	virtual void ccTouchEnded(CCTouch* touch, CCEvent* event);
	virtual void touchDelegateRetain();
	virtual void touchDelegateRelease();
};

#endif /* CURSOR_H_ */
