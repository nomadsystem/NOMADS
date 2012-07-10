//
//  SwarmCC.m
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/10/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//

#import "SwarmCC.h"

@implementation SwarmCC
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

// on "init" you need to initialize your instance
-(id) init
{
	// always call "super" init
	// Apple recommends to re-assign "self" with the "super" return value
	if( (self=[super init])) {
        
        NSLog(@"Init");
        
        // initialize string
        currentCoords = @"0";
		
		// create and initialize a Label
		label = [CCLabelTTF labelWithString:currentCoords fontName:@"Marker Felt" fontSize:64];
        
		// ask director the the window size
		CGSize size = [[CCDirector sharedDirector] winSize];
        
		// position the label on the center of the screen
		label.position =  ccp( size.width /2 , size.height/2 );
		
		// add the label as a child to this Layer
		[self addChild: label];
        
        // enable touch events
        self.isTouchEnabled = YES;
	}
	return self;
}

-(void)ccTouchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
	UITouch *touch = [touches anyObject];
	CGPoint touchLocation = [touch locationInView: [touch view]];	
	touchLocation = [[CCDirector sharedDirector] convertToGL: touchLocation];
	
	[label setPosition:touchLocation];
    // ???
    //currentCoords = [NSString stringWithFormat:@"%@", NSStringFromCGPoint(touchLocation)];
    
    // Should be in ViewController?
    /*
    NSLog(@"Entered sendDiscuss");
     
    //AppID
    Byte myAppID = SOUND_SWARM;
    NSLog(@"myAppID =  %i\n", myAppID);

    //COMMAND
    Byte myCommand = SEND_SPRITE_XY;
    NSLog(@"myCommand =  %i\n", myCommand);

    //DATA TYPE
    Byte myDataType = BYTE;
    NSLog(@"myDataType =  %i\n", myDataType);

    //DATA LENGTH
    //****STK Currently set directly in sendWithGrainElts

    //DATA ARRAY
    //****STK Currently set directly in sendWithGrainElts

    [appDelegate->appSand sendWithGrainElts_AppID:myAppID
                                          Command:myCommand 
                                         DataType:myDataType 
                                          DataLen:2
                                           String:xyCoords;
    */
}


// on "dealloc" you need to release all your retained objects
- (void) dealloc
{
	// in case you have something to dealloc, do it in this method
	// in this particular example nothing needs to be released.
	// cocos2d will automatically release all the children (Label)
	
	// don't forget to call "super dealloc"
	[super dealloc];
}
@end
