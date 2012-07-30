//Cursor.h
//Paul Turowski. 2012.07.23

#ifndef CURSOR_H_
#define CURSOR_H_

#include "cocos2d.h"
#include "AnimatedObject.h"

USING_NS_CC;

class Cursor : public AnimatedObject
{
	CCAnimation* swellAnim;
	CCAnimation* shrinkAnim;
	CCAction* action;
//	CCSpriteBatchNode* sceneSpriteBatchNode;

public:
	ObjectState currentState;

	bool initSprite();
	void initAnimations();
	void changeState(ObjectState newState);
//	CCSpriteBatchNode* initSpriteBatchNode (Swarm s);

	virtual ~Cursor();
};

#endif /* CURSOR_H_ */
