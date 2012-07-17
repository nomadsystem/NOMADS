//
//  SwarmCC.h
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/10/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//


// When you import this file, you import all the cocos2d classes
#import "cocos2d.h"
#import "CommonProtocols.h"
#import "Constants.h"
#import "Cursor.h"
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

// HelloWorldLayer
@interface SwarmCC : CCLayer
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    
    // CCLabelTTF *label;
    Cursor *cursorSprite;
    CCSpriteBatchNode *sceneSpriteBatchNode;
    NSInteger myDataInt[2];
    BOOL beingMoved;
    CGSize size, sizeJava;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;

// returns a CCScene that contains the HelloWorldLayer as the only child
+(CCScene *) scene;

@end
