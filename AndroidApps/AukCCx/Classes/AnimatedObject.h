//AnimatedObject.h
//Paul Turowski. 2012.07.24


#ifndef ANIMATEDOBJECT_H_
#define ANIMATEDOBJECT_H_

#include "cocos2d.h"
// move somewhere else
#include "CCGlobals.h"

USING_NS_CC;

class AnimatedObject : public cocos2d::CCSprite
{
public:
	void initAnimatedObject();
	virtual ~AnimatedObject();
};

#endif /* ANIMATEDOBJECT_H_ */
