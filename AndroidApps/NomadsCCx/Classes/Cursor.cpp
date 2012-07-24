//Cursor.cpp
//Paul Turowski. 2012.07.24

#include "Cursor.h"

Cursor::~Cursor() {
	// TODO Auto-generated destructor stub
}

bool Cursor::initSprite(){
	if (AnimatedObject::init() != NULL){
		AnimatedObject::init();
	    return false;
	}
	initAnimations();
	return true;
}

void Cursor::initAnimations(){

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
