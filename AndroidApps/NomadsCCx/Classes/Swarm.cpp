#include "Swarm.h"

// setup JNI for native -> Java
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform/android/jni/JniHelper.h"
#include <jni.h>
#define CLASS_OPEN_NAME "com/nomads/Swarm"
#endif

USING_NS_CC;
using namespace CocosDenshion;


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

// Load audio
void Swarm::loadAudio () {
//	CDSoundEngine::setMixerSampleRate(CD_SAMPLE_RATE_MID);
	soundEngine = SimpleAudioEngine::sharedEngine();
	soundEngine->preloadBackgroundMusic(CCFileUtils::sharedFileUtils()->fullPathFromRelativePath(TEST_SOUND));
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

    // load audio files
    this->loadAudio();

    Swarm::setTouchEnabled(true);

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

    // Load sprite images
    CCTexture2D::PVRImagesHavePremultipliedAlpha(true);
    CCTexture2D::setDefaultAlphaPixelFormat(kCCTexture2DPixelFormat_RGBA4444);

    CCSpriteFrameCache::sharedSpriteFrameCache()->addSpriteFramesWithFile("rings.plist");
    CCSpriteBatchNode* sceneSpriteBatchNode = CCSpriteBatchNode::create("ring.pvr.ccz", 12);

    cursorSprite = new Cursor();
    cursorSprite->initSprite();
	cursorSprite->initWithSpriteFrameName("untitled_1.png");
	cursorSprite->setPosition( ccp(size.width/2, size.height/2) );

	sceneSpriteBatchNode->addChild(cursorSprite, kCursorSpriteZValue, kCursorSpriteTagValue);
	this->addChild(sceneSpriteBatchNode);
//    this->scheduleUpdate();

    return true;
}

void Swarm::menuCloseCallback(CCObject* pSender)
{
    CCDirector::sharedDirector()->end();

//#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
//    exit(0);
//#endif
}

bool Swarm::ccTouchBegan(CCTouch* touch, CCEvent* event)
{
	if(cursorSprite->currentState != kStateIdle) return false;

	cursorSprite->changeState(kStateActive);
	touchPoint = touch->locationInView();
	touchPoint = CCDirector::sharedDirector()->convertToGL( touchPoint );
	cursorSprite->setPosition( CCPointMake(touchPoint.x, touchPoint.y) );

//	touchPos = touchPoint;

//	touchPos[0] = (int)touchPoint.x;
//	touchPos[1] = (int)touchPoint.y;
//
//	CCLog("This is the touchPos: " + touchPos[0] + " " + touchPos[1]);

//	int test = (int)touchPoint.x;
//
//	touchPos[1] = 112;

	// send touch location to Swarm.java via JNI
	this->sendData();

	// play audio file
	soundEngine->playBackgroundMusic(TEST_SOUND, true);

	return true;
}

void Swarm::ccTouchMoved(CCTouch* touch, CCEvent* event)
{
	CCAssert(cursorSprite->currentState == kStateActive, "Swarm: unexpected state!");
	touchPoint = touch->locationInView();
	touchPoint = CCDirector::sharedDirector()->convertToGL( touchPoint );
	cursorSprite->setPosition( CCPointMake(touchPoint.x, touchPoint.y) );

//	touchPos[0] = (int)touchPoint.x;
//	touchPos[1] = (int)touchPoint.y;

	// send touch location to Swarm.java via JNI
	this->sendData();
}

void Swarm::ccTouchEnded(CCTouch* touch, CCEvent* event)
{
	CCAssert(cursorSprite->currentState == kStateActive, "Swarm: unexpected state!");
	cursorSprite->changeState(kStateIdle);

	// stop audio
	soundEngine->stopBackgroundMusic(true);
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

void Swarm::sendData()
{
	touchPos[1] = 111;
	// change to init
    #if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    JniMethodInfo methodInfo;

    // JNI parameters. (*)V, where * is:
    // B = byte
    // C = char
    // I = int
    // F = float
    // [I = int[], etc.

    if (! JniHelper::getStaticMethodInfo(methodInfo, CLASS_OPEN_NAME, "locationUpdate", "(I)V"))
    {
        CCLog("Can't not find static method locationUpdate");
        return;
    }

    methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID, (int)touchPoint.x);

    // change to on exit
    methodInfo.env->DeleteLocalRef(methodInfo.classID);
    #else
    CCLog("Swarm.cpp->Platform is not Android");
    #endif
}
