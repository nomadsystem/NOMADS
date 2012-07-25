//Cursor.cpp
//Paul Turowski. 2012.07.24

#include "Cursor.h"

Cursor::~Cursor() {
	swellAnim->release();
}

bool Cursor::initSprite(){
	CCLog("initSprite called.");

	// initialize object state
	currentState = kStateIdle;

	// initialize parent class
//	initAnimatedObject();

	// initialize animation sequences
	initAnimations();
	return true;
}

void Cursor::initAnimations(){
	CCLog("initAnimations called.");

	swellAnim = CCAnimation::create();
	swellAnim->retain();

	char frameName[50] = {0};

	for (int i=1; i<=12; i++) {
		frameName = {0};
		sprintf(frameName, "untitled_%d.png", i);
		CCSpriteFrame* pFrame = CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName(frameName);
//		pFrame = CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("untitled_12.png");
		swellAnim->addSpriteFrame(pFrame);
	}

//	swellAnim->addSpriteFrameWithFileName("CloseNormal.png");
//	swellAnim->addSpriteFrameWithFileName("CloseSelected.png");

	swellAnim->setDelayPerUnit(0.05f);
	swellAnim->setRestoreOriginalFrame(true);
}

void Cursor::changeState(ObjectState newState){
	this->stopAllActions();
	this->currentState = newState;

	CCAnimate* action = NULL;

//	CCAction* action = NULL;
//	action = CCAnimate::create(swellAnim);

	switch(newState){
	case kStateIdle:
		CCLog("Cursor.cpp -> Cursor is idle.");
//		action = CCAnimate::actionWithAnimation(swellAnim);
		action = CCAnimate::create(swellAnim);
		this->runAction(action);
		break;
	case kStateActive:
		CCLog("Cursor.cpp -> Cursor is active.");
//		action = CCAnimate::actionWithAnimation(swellAnim);
//		action = CCAnimate::create(swellAnim);
		break;
	default:
		CCLog("Cursor.cpp -> not a valid state!");
		break;
	}
	if (action != NULL){
		this->runAction(action);
//		this->runAction(CCSequence::create(anim, anim->reverse(), NULL));
	}
}

//CCSpriteBatchNode* Cursor::initSpriteBatchNode (Swarm s) {
//	CCTexture2D::PVRImagesHavePremultipliedAlpha(true);
//	CCTexture2D::setDefaultAlphaPixelFormat(kCCTexture2DPixelFormat_RGBA4444);
//
//	CCSpriteFrameCache::sharedSpriteFrameCache()->addSpriteFramesWithFile("rings.plist");
//	sceneSpriteBatchNode = CCSpriteBatchNode::batchNodeWithFile("ring.pvr.ccz");
//
//	this = CCSprite::spriteWithSpriteFrameName("untitled_6.png");
//	this->setPosition( ccp(size.width/2, size.height/2) );
//
//	sceneSpriteBatchNode->addChild(this, kCursorSpriteZValue, kCursorSpriteTagValue);
//
//	return sceneSpriteBatchNode;
//}
