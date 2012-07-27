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

    //===============================JNI Setup for Java method calls
    // Get JNI Environment

    //test
    this->sendData();

    // load audio files
    this->loadAudio();

    Swarm::setTouchEnabled(true);

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
	// change to init
    #if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    JniMethodInfo methodInfo;
    if (! JniHelper::getStaticMethodInfo(methodInfo, CLASS_OPEN_NAME, "locationUpdate", "()V"))
    {
        CCLog("Can't not find static method locationUpdate");
        return;
    }

    // keep
    methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID);

    // change to on exit
    methodInfo.env->DeleteLocalRef(methodInfo.classID);
    #else
    CCLog("Swarm.cpp->Platform is not Android");
    #endif
}

//extern "C"{
//	void SendData (const char * pszMsg){
//		// test for null data
////		if (! <data>)
////		{
////			return;
////		}
//
//		JniMethodInfo t;
//
//		if (JniHelper::getStaticMethodInfo(t, SEND_DATA_CLASS, "locationUpdate","(Ljava/lang/String;Ljava/lang/String;)V"))
//		{
//			CCLog("JniMethodInfo getStaticMethodInfo got data");
//			t.env->callStaticVoidMethod()
////			jstring stringArg1;
////
////			if (! pszTitle)
////			{
////				stringArg1 = t.env->NewStringUTF("");
////			}
////			else
////			{
////				stringArg1 = t.env->NewStringUTF(pszTitle);
////			}
////
////			jstring stringArg2 = t.env->NewStringUTF(pszMsg);
////			t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2);
////
////			t.env->DeleteLocalRef(stringArg1);
////			t.env->DeleteLocalRef(stringArg2);
////			t.env->DeleteLocalRef(t.classID);
//		}
//	}
//}

//
//JNIEnv* getJNIEnv(void)
//{
//
//	JavaVM* jvm = cocos2d::JniHelper::getJavaVM();
//	if (NULL == jvm) {
////            LOGD("Failed to get JNIEnv. JniHelper::getJavaVM() is NULL");
//		return NULL;
//	}
//
//	JNIEnv* env = NULL;
//	// get jni environment
//	jint ret = jvm->GetEnv((void**)&env, JNI_VERSION_1_4);
//
//	switch (ret) {
//		case JNI_OK :
//			// Success!
//			return env;
//
//		case JNI_EDETACHED :
//			// Thread not attached
//
//			// TODO : If calling AttachCurrentThread() on a native thread
//			// must call DetachCurrentThread() in future.
//			// see: http://developer.android.com/guide/practices/design/jni.html
//
//			if (jvm->AttachCurrentThread(&env, NULL) < 0)
//			{
////                    LOGD("Failed to get the environment using AttachCurrentThread()");
//				return NULL;
//			} else {
//				// Success : Attached and obtained JNIEnv!
//				return env;
//			}
//
//		case JNI_EVERSION :
//			// Cannot recover from this error
////                LOGD("JNI interface version 1.4 not supported");
//		default :
////                LOGD("Failed to get the environment using GetEnv()");
//			return NULL;
//	}
//}
//
