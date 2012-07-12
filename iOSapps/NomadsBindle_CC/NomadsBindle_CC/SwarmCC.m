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

// on "init" you need to initialize your instance
-(id) init
{
	// always call "super" init
	// Apple recommends to re-assign "self" with the "super" return value
	if( (self=[super init])) {
        
        NSLog(@"Init");
		
		// create and initialize a Label
		label = [CCLabelTTF labelWithString:@"*" fontName:@"Marker Felt" fontSize:64];
        
		// ask director the the window size
		CGSize size = [[CCDirector sharedDirector] winSize];
        
		// position the label on the center of the screen
		label.position =  ccp( size.width /2 , size.height/2 );
		
		// add the label as a child to this Layer
		[self addChild: label];
        
        // enable touch events
        self.isTouchEnabled = YES;
        
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
        // schedule update method for sending data
        [self schedule:@selector(sendPos:) interval:0.2];
	}
	return self;
}

-(void)ccTouchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    beingMoved = true;
	UITouch *touch = [touches anyObject];
	CGPoint touchLocation = [touch locationInView: [touch view]];	
	touchLocation = [[CCDirector sharedDirector] convertToGL: touchLocation];
	
	[label setPosition:touchLocation];
    myDataInt[0] = touchLocation.x;
    myDataInt[1] = touchLocation.y;
}

-(void)ccTouchesEnded:(UITouch *)touch withEvent:(UIEvent *)event
{
    beingMoved = false;
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
