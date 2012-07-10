//
//  SwarmCC.h
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/10/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//


// When you import this file, you import all the cocos2d classes
#import "cocos2d.h"

// HelloWorldLayer
@interface SwarmCC : CCLayer
{
    CCLabelTTF *label;
    NSString *currentCoords;
}

// returns a CCScene that contains the HelloWorldLayer as the only child
+(CCScene *) scene;

@end
