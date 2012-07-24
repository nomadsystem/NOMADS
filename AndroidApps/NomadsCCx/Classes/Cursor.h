//Cursor.h
//Paul Turowski. 2012.07.23

#include "cocos2d.h"
#include "AnimatedObject.h"
#include "Swarm.h"

#ifndef CURSOR_H_
#define CURSOR_H_

USING_NS_CC;

class Cursor : public AnimatedObject
{
public:
	bool initSprite();
	void initAnimations();
	CCSpriteBatchNode* sceneSpriteBatchNode;
//	CCSpriteBatchNode* initSpriteBatchNode (Swarm s);


	virtual ~Cursor();
};

#endif /* CURSOR_H_ */
