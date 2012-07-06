//
//  SwarmDrawView.h
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef SWARM_DRAW_VIEW
#define SWARM_DRAW_VIEW

#import <Foundation/Foundation.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface SwarmDrawView : UIView <SandDelegate>
{

    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    CGPoint myFingerPoint;
    int maxTrails;
    int *xTrail;
    int *yTrail;
    int currentRedColor;
    float decayColor;
    float decayColorChangeDelta;
    float touchColor;
    
}
@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (nonatomic) CGPoint myFingerPoint;
- (void)clearAll;
- (void)endTouches:(NSSet *)touches;

@end

#endif