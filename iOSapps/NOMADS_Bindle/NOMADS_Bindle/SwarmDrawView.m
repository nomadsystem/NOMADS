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
#import "NAppIDMain.h"
#import "NCommandMain.h"

@implementation SwarmDrawView
@synthesize myFingerPoint;
@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;

-(id)initWithFrame:(CGRect)r
{
    self = [super initWithFrame:r];
    if (self) {
        
        //     [self setBackgroundColor:[UIColor whiteColor]];
        
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
        [self setMultipleTouchEnabled:YES];
        
        //Code to get the point to start at the center of the screen
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        CGFloat screenWidth = screenRect.size.width;
        CGFloat screenHeight = screenRect.size.height;
        sWidth = (int)screenWidth;
        sHeight = (int)screenHeight;
        
        myFingerPoint.x = (screenWidth * 0.5);
        myFingerPoint.y = (screenHeight * 0.5);
        
        maxTrails = 1;
        // earlier on in your code put this (below)
        
        xTrail = (int *)malloc(sizeof(int)*maxTrails);
        yTrail = (int *)malloc(sizeof(int)*maxTrails);
        
        for (int i=0;i<maxTrails;i++) {
            xTrail[i]=(screenWidth * 0.5);
            yTrail[i]=(screenHeight * 0.5);
        }
        decayColor = 1.0;
        decayColorChangeDelta = 0.2;

        // decayColorChangeDelta = decayColor/(float)maxTrails;
    //    CLog(@" brightness = %f ", brightness);
    //    CLog(@" brightDelta = %f ", brightDelta);
        
        //     CLog(@"center point = %f , %f ",myFingerPoint.x, myFingerPoint.y);
        
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
        //            //    CLog(@"Filtering AppID 22");
        //            //    CLog(@"textFromNOMADS %@",textFromNOMADS);
        //            //do something
        //        }
        
        //        else {
        //            CLog(@"No Data for Swarm App");
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
        CGContextSetRGBFillColor(context, (94.0/255.0), (41.0/255.0), (28.0/255.0), decayColor);
        //  CGContextSetRGBFillColor(context, decayColor, touchColor, 1.0, decayColor);

        CGContextAddEllipseInRect(context,(CGRectMake (xTrail[i], yTrail[i], 22.0, 22.0)));
        CGContextDrawPath(context, kCGPathFill);
        //     CGContextFillPath(context);
        CGContextStrokePath(context);
        
        decayColor = (decayColor - decayColorChangeDelta);
        CLog(@" Brightness = %f", decayColor);
        
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
        CLog(@" Touches Began");
        //Is this a double tap?
        if ([t tapCount] > 1) {
            [self clearAll];
            return;
        }
        
        //Create a point for the value
        touchColor = 0.0;
        CGPoint loc = [t locationInView:self];
        CLog(@"SWARM_X loc = %f", loc.x);
        CLog(@"SWARM_Y loc = %f", loc.y);
        myFingerPoint.x = loc.x;
        myFingerPoint.y = loc.y;
        
        //Put pair in dictionary
        //        [linesInProcess setObject:newLine forKey:key];
    }
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
        CLog(@"SWARM_X loc = %f", loc.x);
        CLog(@"SWARM_Y loc = %f", loc.y);
        
        int xy[2];
        
        xy[0] = (int)((double)(loc.x/sWidth)*1000.0);
        xy[1] = (int)((double)(loc.y/(sHeight-50))*1000.0);
        
        
        [appDelegate->appSand sendWithGrainElts_AppID:SOUND_SWARM Command:SEND_SPRITE_XY DataType:INT32 DataLen:2 Int32:xy];
    }
    //Redraw
    dispatch_async(dispatch_get_main_queue(), ^(void) {
        [self setNeedsDisplay];
    });
}

- (void)endTouches:(NSSet *)touches
{
    //Remove ending touches from dictionary
    for (UITouch *t in touches) {
        
        touchColor = 0.0;
        //If this is a double tap, point will be nil,
        //Do stuff here when touch ends
        
        
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
