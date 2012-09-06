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
#import <QuartzCore/QuartzCore.h>
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
    NSMutableArray  *chatLines;
    int numChatLines;
    AVAudioPlayer *audioPlayerDroplet;
    AVAudioPlayer *audioPlayerTone[21];
    int maxNumOfTonePlayers;
    Boolean toneCntrlOn;
    float toneVolScaler;
    
    float dropletVolume;
    float toneVolume;
    
    NSTimer *dropletTimer;
    NSTimer *dotFlashTimer;
    NSTimer *toneFadeOutTimer;
    NSTimer *toneFadeInTimer;

    NSTimer *toneTimer;
    
    NSTimer *promptFadeInTimer;
    NSTimer *promptFadeOutTimer;
    NSTimer *promptWaitTimer;

    
    int fileNumDroplets;
    int fileNumTones;
    
    float currentTimerVal;
    float lastTimerVal;
    AVAudioSession *session;
    float ellipseR, ellipseG, ellipseB, ellipseA;
    float dotFlashEllipseR, dotFlashEllipseG, dotFlashEllipseB, dotFlashEllipseA;
    float toneMovementVol;
    
    Boolean dropFlash;
    float dotSizeScaler;

    NSString *prompt;
    int promptTextSize;
    float promptAlpha;
    float promptFadeInVal;
    float promptFadeOutVal;
    float promptWaitTick;
    
    int discussTextSize;

    @public Boolean discussStatus;
    Boolean cloudStatus;
    Boolean pointerStatus;
    
    CGRect viewRect;
    CGFloat viewHeight;
    CGFloat viewWidth;
    float viewWidthScale; //STK: scales between 0-1000 (eventually between 0-1)
    float viewHeightScale;
    
    int tonePlayer;
    Boolean toneVolDone;
    int numRunTonePlayers;
    
    Boolean discussChanged;
    Boolean discussHasText;
    
    CGContextRef myLayerContext1;
    CGContextRef myLayerContext2;

    CGLayerRef   discussLayer;
    CGLayerRef   pointerLayer;
    CADisplayLink *displayLink;
    
    Boolean tonesTouchDone;
    Boolean firstFade;
    Boolean drawing;
    
    UIColor *dotColor;
    int dotMethod;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (nonatomic) CGPoint myFingerPoint;
@property (strong, nonatomic) NSString *prompt;


- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand
- (void)clearAll;
- (void)endTouches:(NSSet *)touches;
- (void)playDroplet;
- (void)playTone;
- (void)fadeInPrompt;
- (void)fadeOutPromt;
- (void)promptZeroTimer;

@end

#endif