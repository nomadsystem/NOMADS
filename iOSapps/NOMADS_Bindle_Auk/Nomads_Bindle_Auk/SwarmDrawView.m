//
//  SwarmDrawView.m
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SwarmDrawView.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"

@implementation SwarmDrawView
@synthesize myFingerPoint;
@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;

-(id)initWithFrame:(CGRect)r
{
    self = [super initWithFrame:r];
    if (self) {
        
        
        [self setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
        [self setMultipleTouchEnabled:YES];
        
        //Code to get the point to start at the center of the screen
        CGRect viewRect = [self bounds];
        CGFloat viewWidth = viewRect.size.width;
        CGFloat viewHeight = viewRect.size.height;
        myFingerPoint.x = (viewWidth * 0.5);
        myFingerPoint.y = (viewHeight * 0.5);
        
        maxTrails = 10;
        // earlier on in your code put this (below)
        
        xTrail = (int *)malloc(sizeof(int)*maxTrails);
        yTrail = (int *)malloc(sizeof(int)*maxTrails);
        
        for (int i=0;i<maxTrails;i++) {
            xTrail[i]=(viewWidth * 0.5);
            yTrail[i]=(viewHeight * 0.5);
        }
        decayColor = 1.0;
        decayColorChangeDelta = (decayColor/(float)maxTrails);
        //    NSLog(@" brightness = %f ", brightness);
        //    NSLog(@" brightDelta = %f ", brightDelta);
        
        //     NSLog(@"center point = %f , %f ",myFingerPoint.x, myFingerPoint.y);
        
    }
    return self;
}

//Data Ready Handle======================================================

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


//Drawing Stuff======================================================


-(void)drawRect:(CGRect)rect
{
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    for(int i=maxTrails;i>0;i--) {
        xTrail[i] = xTrail[i-1];
        yTrail[i] = yTrail[i-1];
    }
    
    xTrail[0] = myFingerPoint.x;
    yTrail[0] = myFingerPoint.y;
    
    decayColor = 1.0;
    for (int i=0; i<maxTrails; i++) {   
        NSLog(@" shape number (i) = %d", i);
        CGContextSetRGBFillColor(context, decayColor, touchColor, 1.0, decayColor);
        CGContextAddEllipseInRect(context,(CGRectMake (xTrail[i], yTrail[i], 44.0, 44.0)));        
        CGContextDrawPath(context, kCGPathFill);
        //     CGContextFillPath(context);
        CGContextStrokePath(context);
        decayColor = (decayColor - decayColorChangeDelta);        
    }
    
    
}

-(void)clearAll
{
    //Clear the Collections
    //    [linesInProcess removeAllObjects];
    //    [completeLines removeAllObjects];
    
    //Redraw
    [self setNeedsDisplay];
}

//Touch Input Stuff======================================================

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    for (UITouch *t in touches) {
        NSLog(@" Touches Began");
        //Is this a double tap?
        if ([t tapCount] > 1) {
            [self clearAll];
            return;
        }
        
        //Create a point for the value
        touchColor = 0.0;
        CGPoint loc = [t locationInView:self];
        NSLog(@"SWARM_X loc = %f", loc.x);
        NSLog(@"SWARM_Y loc = %f", loc.y);
        myFingerPoint.x = loc.x;
        myFingerPoint.y = loc.y;
        
        //Put pair in dictionary
        //        [linesInProcess setObject:newLine forKey:key];
    }
    [self setNeedsDisplay];
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    //Update linesInProcess with moved touches
    for (UITouch *t in touches) {
        
        //Find the line for this touch
        //   myLine = [linesInProcess objectForKey:key];
        
        //Update the point
        CGPoint loc = [t locationInView:self];
        myFingerPoint.x = loc.x;
        myFingerPoint.y = loc.y;
        NSLog(@"SWARM_X loc = %f", loc.x);
        NSLog(@"SWARM_Y loc = %f", loc.y);
        
        int xy[2];
        
        xy[0] = (int) loc.x;
        xy[1] = (int) loc.y;
        
        
        [appDelegate->appSand sendWithGrainElts_AppID:SOUND_SWARM Command:SEND_SPRITE_XY DataType:INT DataLen:2 Integer:xy];
    }
    //Redraw
}

- (void)endTouches:(NSSet *)touches
{
    //Remove ending touches from dictionary
    for (UITouch *t in touches) {
        touchColor = 0.6;
        
    }
    //Redraw
    [self setNeedsDisplay];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self endTouches:touches];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self endTouches:touches];
}

@end