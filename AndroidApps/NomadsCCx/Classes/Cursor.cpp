//Cursor.cpp
//Paul Turowski. 2012.07.24

#include "Cursor.h"

Cursor::~Cursor() {
	// TODO Auto-generated destructor stub
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

//	swellAnim = CCAnimation::create();
//	CCSpriteFrame* frame;
	// generate array of frames
//	for (int i=1; i<=12; i++){
//		char* tempFileName;
//		sprintf(tempFileName, "untitled_%i.png", i);
//		CCSpriteFrame* frame = CCSpriteFrame::frameWithTextureFilename("untitled_12.png");
//		swellAnim->addSpriteFrame(frame);
//	}

	//Add frames to animation
//	swellAnim = CCAnimation::create(spriteFrames, 0.05f);
	//	swellAnim->setDelayPerUnit(0.05f);

//	CCSpriteFrameCache::sharedSpriteFrameCache()->addSpriteFramesWithFile("rings.plist");
//    CCSpriteBatchNode* sceneSpriteBatchNode = CCSpriteBatchNode::batchNodeWithFile("ring.pvr.ccz");
//
//	swellAnim = CCAnimation::create();
//	char* frameName;
//	for (int i=1; i<12; i++) {
//	sprintf(frameName, "untitled_%d.png", i);
//	CCSpriteFrame* pFrame = CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName(frameName);
//	swellAnim->addSpriteFrame(pFrame);
//	}

}

void Cursor::changeState(ObjectState newState){
	this->stopAllActions();
	this->currentState = newState;

	CCAction* action = NULL;

	switch(newState){
	case kStateIdle:
		CCLog("Cursor.cpp -> Cursor is idle.");
//		action = CCAnimate::actionWithAnimation(swellAnim);
		break;
	case kStateActive:
		CCLog("Cursor.cpp -> Cursor is active.");
//		action = CCAnimate::actionWithAnimation(swellAnim);
		break;
	default:
		CCLog("Cursor.cpp -> not a valid state!");
		break;
	}
//	this->runAction(action);
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
