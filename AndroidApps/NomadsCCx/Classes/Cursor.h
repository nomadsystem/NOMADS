//Cursor.h
//Paul Turowski. 2012.07.23

#ifndef CURSOR_H_
#define CURSOR_H_

#include "cocos2d.h"
#include "AnimatedObject.h"

USING_NS_CC;

class Cursor : public AnimatedObject
{
public:
	ObjectState currentState;
//	CCAnimation* swellAnim;
//	CCAnimation* shrinkAnim;
	bool initSprite();
	void initAnimations();
	void changeState(ObjectState newState);
	CCSpriteBatchNode* sceneSpriteBatchNode;
//	CCSpriteBatchNode* initSpriteBatchNode (Swarm s);

	virtual ~Cursor();
};

#endif /* CURSOR_H_ */
