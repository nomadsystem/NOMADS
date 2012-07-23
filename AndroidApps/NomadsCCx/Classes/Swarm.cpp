#include "Swarm.h"

USING_NS_CC;

CCScene* Swarm::scene()
{
    // 'scene' is an autorelease object
    CCScene *scene = CCScene::create();
    
    // 'layer' is an autorelease object
    Swarm *layer = Swarm::create();

    // add layer as a child to scene
    scene->addChild(layer);

    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool Swarm::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !CCLayer::init() )
    {
        return false;
    }

    Swarm::setTouchEnabled(true);
    swarmState = kStateIdle;

    // set size of Java display

    //	get window size
	CCSize size = CCDirector::sharedDirector()->getWinSize();

    /////////////////////////////
    // 2. add a menu item with "X" image, which is clicked to quit the program
    //    you may modify it.

    // add a "close" icon to exit the progress. it's an autorelease object
    CCMenuItemImage *pCloseItem = CCMenuItemImage::create(
                                        "CloseNormal.png",
                                        "CloseSelected.png",
                                        this,
                                        menu_selector(Swarm::menuCloseCallback) );
    pCloseItem->setPosition( ccp(CCDirector::sharedDirector()->getWinSize().width - 20, 20) );

    // create menu, it's an autorelease object
    CCMenu* pMenu = CCMenu::create(pCloseItem, NULL);
    pMenu->setPosition( CCPointZero );
    this->addChild(pMenu, 1);

//    /////////////////////////////
//    // 3. add your codes below...
//
//    // add a label shows "Hello World"
//    // create and initialize a label
//    CCLabelTTF* pLabel = CCLabelTTF::create("Hello World", "Arial", 24);
//
//    // position the label on the center of the screen
//    pLabel->setPosition( ccp(size.width / 2, size.height - 50) );
//
//    // add the label as a child to this layer
//    this->addChild(pLabel, 1);
//
//    // add "HelloWorld" splash screen"
//    pSprite = CCSprite::create("HelloWorld.png");
//
//    // position the sprite on the center of the screen
//    pSprite->setPosition( ccp(size.width/2, size.height/2) );
//
//    // add the sprite as a child to this layer
//    this->addChild(pSprite, 0);

    CCTexture2D::PVRImagesHavePremultipliedAlpha(true);
    CCTexture2D::setDefaultAlphaPixelFormat(kCCTexture2DPixelFormat_RGBA4444);

    sceneSpriteBatchNode = CCSpriteBatchNode::batchNodeWithFile("ring.pvr.ccz");
    this->addChild(sceneSpriteBatchNode);

    CCSpriteFrameCache::sharedSpriteFrameCache()->addSpriteFramesWithFile("rings.plist");

    cursorSprite = CCSprite::spriteWithSpriteFrameName("untitled_1.png");
    cursorSprite->setPosition( ccp(size.width/2, size.height/2) );

    sceneSpriteBatchNode->addChild(cursorSprite, kCursorSpriteZValue, kCursorSpriteTagValue);
    
//    this->scheduleUpdate();

    return true;
}

void Swarm::menuCloseCallback(CCObject* pSender)
{
    CCDirector::sharedDirector()->end();

#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
}

bool Swarm::ccTouchBegan(CCTouch* touch, CCEvent* event)
{
	if(swarmState != kStateIdle) return false;

	swarmState = kStateActive;
	return true;
}

void Swarm::ccTouchMoved(CCTouch* touch, CCEvent* event)
{
	CCAssert(swarmState == kStateActive, L"Swarm: unexpected state!");
	CCPoint touchPoint = touch->locationInView();
	touchPoint = CCDirector::sharedDirector()->convertToGL( touchPoint );
	cursorSprite->setPosition( CCPointMake(touchPoint.x, touchPoint.y) );
}

void Swarm::ccTouchEnded(CCTouch* touch, CCEvent* event)
{
	CCAssert(swarmState == kStateActive, L"Swarm: unexpected state!");
	swarmState = kStateIdle;
}

void Swarm::onEnter()
{
    CCDirector* pDirector = CCDirector::sharedDirector();
    pDirector->getTouchDispatcher()->addTargetedDelegate(this, 0, true);
    CCLayer::onEnter();
}

void Swarm::onExit()
{
    CCDirector* pDirector = CCDirector::sharedDirector();
    pDirector->getTouchDispatcher()->removeDelegate(this);
    CCLayer::onExit();
}

void Swarm::touchDelegateRetain()
{
    this->retain();
}

void Swarm::touchDelegateRelease()
{
    this->release();
}
