/*
 * Cursor.cpp
 *
 *  Created on: Jul 23, 2012
 *      Author: TRock
 */

#include "/Users/TRock/Nomads/AndroidApps/NomadsCCx/Classes/Cursor.h"

Cursor::Cursor() {
	// TODO Auto-generated constructor stub

}

Cursor::~Cursor() {
	// TODO Auto-generated destructor stub
}

bool Swarm::ccTouchBegan(CCTouch* touch, CCEvent* event)
{
	if(swarmState != kStateIdle) return false;

	swarmState = kStateActive;
	return true;
}

void Cursor::ccTouchMoved(CCTouch* touch, CCEvent* event)
{
	CCAssert(swarmState == kStateActive, L"Swarm: unexpected state!");
	CCPoint touchPoint = touch->locationInView();
	touchPoint = CCDirector::sharedDirector()->convertToGL( touchPoint );
//	pSprite->setPosition( CCPointMake(touchPoint.x, getPosition().y) );
}

void Cursor::ccTouchEnded(CCTouch* touch, CCEvent* event)
{
	CCAssert(swarmState == kStateActive, L"Swarm: unexpected state!");
	swarmState = kStateIdle;
}

void Cursor::touchDelegateRetain()
{
    this->retain();
}

void Cursor::touchDelegateRelease()
{
    this->release();
}

