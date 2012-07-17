//
//  SwarmCC.m
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/10/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//

#import "SwarmCC.h"
#import "NGlobals.h"

@implementation SwarmCC

@synthesize appSand;
@synthesize appDelegate;

+(CCScene *) scene
{
	// 'scene' is an autorelease object.
	CCScene *scene = [CCScene node];
	
	// 'layer' is an autorelease object.
	SwarmCC *layer = [SwarmCC node];
	
	// add layer as a child to scene
	[scene addChild: layer];
	
	// return the scene
	return scene;
}

#pragma mark –
#pragma mark Update Method
-(void) update:(ccTime)deltaTime {
    CCArray *listOfGameObjects = 
    [sceneSpriteBatchNode children];                     // 1
    for (MovableObject *tempChar in listOfGameObjects) {         // 2
        [tempChar updateStateWithDeltaTime:deltaTime andListOfGameObjects:listOfGameObjects];                         // 3
    }
}

// on "init" you need to initialize your instance
-(id) init
{
	// always call "super" init
	// Apple recommends to re-assign "self" with the "super" return value
	if( (self=[super init])) {
        
        NSLog(@"Init");
        
        // enable touches
        self.isTouchEnabled = YES; 
        
        // set Java display window size
        sizeJava.width = 500;
        sizeJava.height = 400;
        
        // ask director the the window size
		size = [[CCDirector sharedDirector] winSize];
		
		// create and initialize a Label
		// label = [CCLabelTTF labelWithString:@"*" fontName:@"Marker Felt" fontSize:64];
        
        // Set texture format (created with TexturePacker.app)
        [CCTexture2D PVRImagesHavePremultipliedAlpha:YES];
        [CCTexture2D setDefaultAlphaPixelFormat:kCCTexture2DPixelFormat_RGBA4444];
        
        
        
        // Seeds the random number generator
        // srandom(time(NULL));
        
        // Eventually make two different versions of spritesheet for iphone & ipad
        if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {         
            sceneSpriteBatchNode = [CCSpriteBatchNode batchNodeWithFile:@"ring.pvr.ccz"];
            [self addChild:sceneSpriteBatchNode];    
            [[CCSpriteFrameCache sharedSpriteFrameCache] addSpriteFramesWithFile:@"ring.plist"];
        }
        else {
            sceneSpriteBatchNode = [CCSpriteBatchNode batchNodeWithFile:@"ring.pvr.ccz"];
            [self addChild:sceneSpriteBatchNode];    
            [[CCSpriteFrameCache sharedSpriteFrameCache] addSpriteFramesWithFile:@"ring.plist"];
        }
        
        // initialize joystick
        //[self initJoystickAndButtons];
        
        NSString *firstImg = @"untitled_1.png";
        
        cursorSprite = [[[Cursor alloc] init] initWithSpriteFrameName:firstImg];
        
        [cursorSprite setPosition:ccp(size.width / 2, size.height / 2)]; 
        
        [sceneSpriteBatchNode 
         addChild:cursorSprite 
         z:kCursorSpriteZValue 
         tag:kCursorSpriteTagValue];                     // 6
        
        /*        [self createObjectOfType:kEnemyTypeRadarDish 
         withHealth:100 
         atLocation:ccp(screenSize.width * 0.878f,
         screenSize.height * 0.13f) 
         withZValue:10];                            // 7
         */
        
        [self scheduleUpdate];

		// position the label on the center of the screen
		//label.position =  ccp( size.width /2 , size.height/2 );
		
		// add the label as a child to this Layer
		//[self addChild: label];
        
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
        // schedule update method for sending data
        // Change interval variable to 
        [self schedule:@selector(sendPos:) interval:0.2];
	}
	return self;
}

-(void)ccTouchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    beingMoved = true;
    [cursorSprite changeState:kStateStretch];
}

-(void)ccTouchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
	UITouch *touch = [touches anyObject];
	CGPoint touchLocation = [touch locationInView: [touch view]];
	touchLocation = [[CCDirector sharedDirector] convertToGL: touchLocation];
	
	[cursorSprite setPosition:touchLocation];
    // scale touch range to SoundSwarmDisplay (Java) window size
    myDataInt[0] = (touchLocation.x * (1/size.width)) * sizeJava.width;
    // Y needs to be flipped
    myDataInt[1] = ((size.height - touchLocation.y) * (1/size.height)) * sizeJava.height;
}

-(void)ccTouchesEnded:(UITouch *)touch withEvent:(UIEvent *)event
{
    beingMoved = false;
    [cursorSprite changeState:kStateIdle];
}

- (void)sendPos:(ccTime)delta
{
    if (beingMoved) {
        //NSLog(@"SendPos()");
        //AppID
        Byte myAppID = SOUND_SWARM;
        //NSLog(@"myAppID =  %i\n", myAppID);
        
        //COMMAND
        Byte myCommand = SEND_SPRITE_XY;
        //NSLog(@"myCommand =  %i\n", myCommand);
        
        //DATA TYPE
        Byte myDataType = INT;
        //NSLog(@"myDataType =  %i\n", myDataType);
        
        //DATA LENGTH
        //STK Currently set directly in sendWithGrainElts

        //DATA ARRAY
        //myDataInt is updated in ccTouchesMoved
        
        [appDelegate->appSand sendWithGrainElts_AppID:myAppID
                                              Command:myCommand 
                                             DataType:myDataType 
                                              DataLen:2
                                              Integer:myDataInt];
    }

}


- (void)dataReadyHandle:(NGrain *)inGrain
{
    //This delegate not being 
    // CLog(@"I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) { 
        
        //        if(inGrain->appID == SOUND_SWARM)//Text from Discuss Prompt
        //        {
        //            //    NSLog(@"Filtering AppID 22");
        //            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
        //            //do something
        //        }
        
        //        else {
        //            NSLog(@"No Data for Swarm App");
        //        }
    }
    
}


// on "dealloc" you need to release all your retained objects
- (void) dealloc {}
@end
