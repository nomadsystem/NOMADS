//
//  SwarmDrawView.h
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef SWARM_DRAW_VIEW
#define SWARM_DRAW_VIEW

#import <AVFoundation/AVFoundation.h>
#import <Foundation/Foundation.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface SwarmDrawView : UIView <SandDelegate, AVAudioPlayerDelegate>
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
    NSMutableArray  *chatLines;
    int numChatLines;
    AVAudioPlayer *audioPlayer;
    float dropletVolume;
    
    NSTimer *dropletTimer;
    NSTimer *dotFlashTimer;
    
    NSTimer *promptFadeInTimer;
    NSTimer *promptFadeOutTimer;
    NSTimer *promptWaitTimer;

    
    int fileNum;
    float currentTimerVal;
    float lastTimerVal;
    AVAudioSession *session;
    float ellipseR, ellipseG, ellipseB, ellipseA;
    Boolean dropFlash;
    float dotSizeScaler;

    NSString *prompt;
    float promptAlpha;
    float promptFadeInVal;
    float promptWaitTick;
    float promptFadeOutTick;

    Boolean discussStatus;
    Boolean cloudStatus;
    Boolean pointerStatus;
    
    CGRect viewRect;
    CGFloat viewHeight;
    CGFloat viewWidth;
    
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (nonatomic) CGPoint myFingerPoint;
@property (strong, nonatomic) NSString *prompt;

- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand
- (void)clearAll;
- (void)endTouches:(NSSet *)touches;
- (void)playDroplet;
- (void)fadeInPrompt;
- (void)fadeOutPromt;
- (void)promptZeroTimer;

@end

#endif