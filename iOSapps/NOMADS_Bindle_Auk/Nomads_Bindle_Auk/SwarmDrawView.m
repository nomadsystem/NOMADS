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
#import "NAppIDAuk.h"
#import "NCommandAuk.h"

@implementation SwarmDrawView
@synthesize myFingerPoint;
@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;


-(id)initWithFrame:(CGRect)r
{
    self = [super initWithFrame:r];
    if (self) {
        
        [self setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
        numChatLines = 15; //Initialize number of chat lines to display
        
        chatLines = [[NSMutableArray alloc] initWithCapacity:numChatLines];
        
        
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
        
        
        currentTimerVal = 4.0;
        lastTimerVal = 4.0;
        fileNum = 1;
        dropletTimer = [NSTimer scheduledTimerWithTimeInterval:currentTimerVal target:self selector:@selector(playDroplet) userInfo:nil repeats:YES];
    }
    return self;
}

//Data Ready Handle======================================================

- (void)dataReadyHandle:(NGrain *)inGrain
{
    //This delegate not being 
    CLog(@"SwarmDrawView: Data Ready Handle\n");
    
    if (nil != inGrain) { 
        if(inGrain->appID == OC_DISCUSS) //Text from Student Discuss
        { 
            
            [chatLines addObject:inGrain->str]; //Add the new text to the array
            
            
            if ([chatLines count] > numChatLines) {
                [chatLines removeObjectAtIndex:0]; //if the array is bigger than numChatLines
                                                   //remove the first item
            }
                        
            CLog(@"chatLines = %@", chatLines);
            [self setNeedsDisplay];
            
        } 
        
    }
    
}


//Drawing Stuff======================================================


-(void)drawRect:(CGRect)rect
{
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    // The Text =====================================================
    
    // Prompt text
    
    NSString *nsstr = @"NOMADS Bindle"; //Incoming NSString 
    const char *str = [nsstr cStringUsingEncoding:NSUTF8StringEncoding]; //convert to c-string 
    
    int len = strlen(str); //get length of string
    
    CGRect viewRect = [self bounds];
    CGFloat viewHeight = viewRect.size.height;
    CGFloat viewWidth = viewRect.size.width;
    
    
    CGContextSelectFont (context, 
                         "Papyrus",
                         30,
                         kCGEncodingMacRoman);
    CGContextSetCharacterSpacing (context, 1);
    CGContextSetTextDrawingMode (context, kCGTextFillStroke);
    CGContextSetRGBFillColor (context, 1, 1, 1, 1);
    CGContextSetRGBStrokeColor (context, 1, 1, 1, 1); 
    CGContextSetTextMatrix(context, CGAffineTransformMakeScale(1.0f, -1.0f));

    CGContextSetAllowsAntialiasing(context, YES);
    CGContextSetShouldAntialias(context, YES); 
    CGContextSetShouldSmoothFonts(context, YES);

    CGContextShowTextAtPoint (context, 40, 40, str, len); 
    
    
    // Display discussion text
    
    CGContextSelectFont (context, 
                         "Helvetica-Light",
                         10,
                         kCGEncodingMacRoman);
    CGContextSetRGBFillColor (context, 0.7, 0.7, 0.8, 1);
    CGContextSetRGBStrokeColor (context, 0.7, 0.7, 0.8, 1); 

    CGFloat tH = (int)(viewHeight);
    CGFloat chatSpace = (tH/numChatLines) * 0.4;
    CGFloat chatYLoc = (viewHeight-chatSpace);
    CGFloat chatXLoc = 20;
    
    for (int i=0;i<[chatLines count];i++) {
        
        NSString *nsstr = [chatLines objectAtIndex:i];; //Incoming NSString 
        const char *str = [nsstr cStringUsingEncoding:NSUTF8StringEncoding]; //convert to c-string
        int len = strlen(str);
        printf("My String %s\n", str);
        CGContextShowTextAtPoint (context, chatXLoc, chatYLoc, str, len);
        chatYLoc -= chatSpace;
    }
    // The Dot ======================================================
    
    for(int i=maxTrails;i>0;i--) {
        xTrail[i] = xTrail[i-1];
        yTrail[i] = yTrail[i-1];
    }
    
    xTrail[0] = myFingerPoint.x;
    yTrail[0] = myFingerPoint.y;
    
    decayColor = 1.0;
    float size = 20;

    for (int i=0; i<maxTrails; i++) {   
        if (i<(maxTrails-1)) {
            int xDiff = xTrail[i]-xTrail[i+1];
            int yDiff = yTrail[i]-yTrail[i+1];
            if ((abs(xDiff) > 6) || (abs(yDiff) > 6)) {
                CGContextSetRGBFillColor(context, decayColor, touchColor, 1.0, decayColor);
                CGContextAddEllipseInRect(context,(CGRectMake (xTrail[i]-xDiff/2, yTrail[i]-yDiff/2, size, size)));        
                CGContextDrawPath(context, kCGPathFill);
                //     CGContextFillPath(context);
                CGContextStrokePath(context);
                decayColor = (decayColor - decayColorChangeDelta);        
            }
            
            CGContextSetRGBFillColor(context, decayColor, touchColor, 1.0, decayColor);
            CGContextAddEllipseInRect(context,(CGRectMake (xTrail[i], yTrail[i], size, size)));        
            CGContextDrawPath(context, kCGPathFill);
            //     CGContextFillPath(context);
            CGContextStrokePath(context);
            decayColor = (decayColor - decayColorChangeDelta);        
            size *= 0.9;
        }
    }        
    //Sounds ======================================================
    

    
    int numOfDroplets = 203;
    float viewXGrid  = (viewWidth / 5);
    float viewYGrid  = (viewHeight / numOfDroplets);
    
    
    int soundFileNum = (int)(myFingerPoint.y/viewYGrid);
    
    fileNum = soundFileNum;
    currentTimerVal = (myFingerPoint.x/viewXGrid) + 4.0;
    
    
    
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
    maxTrails = 2;
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

    maxTrails = 10;

    //This loop is for display purposes
    dispatch_async(dispatch_get_main_queue(), ^(void) {

    int xy[2];
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
        
        
        
        xy[0] = (int) loc.x;
        xy[1] = (int) loc.y;
        
        

    }
    [self setNeedsDisplay];
    });

    //This one sends the data
    int xy[2];
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
        
        float screenScaleX = 5.9;
        float screenScaleY = 3.33;
        int screenMinusX = 1000;
        int screenMinusY = 800;
        
        
        xy[0] = (int) ((loc.x * screenScaleX)- screenMinusX);
        xy[1] = (int) ((loc.y * screenScaleY)- screenMinusY);
        
        
        
    }
    
    
    [appDelegate->appSand sendWithGrainElts_AppID:OC_POINTER Command:SEND_SPRITE_XY DataType:INT32 DataLen:2 Int32:xy];
    

    
}

- (void)endTouches:(NSSet *)touches
{
    maxTrails = 2;
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

- (void)playDroplet
{
    
    NSString *soundFile;
     
    soundFile = [NSString stringWithFormat:@"sounds/GlacierSounds/%d.mp3",fileNum];
    
    NSURL *url = [NSURL fileURLWithPath:[NSString stringWithFormat:@"%@/%@", [[NSBundle mainBundle] resourcePath], soundFile]];
    
    CLog("URL: %@", url);
	
	NSError *error;
	audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];
	audioPlayer.numberOfLoops = 0;
    
    
    //****STK Other useful control parameters 
    //    audioPlayer.volume = 0.5; // 0.0 - no volume; 1.0 full volume
    //    NSLog(@"%f seconds played so far", audioPlayer.currentTime);
    //    audioPlayer.currentTime = 10; // jump to the 10 second mark
    //    [audioPlayer pause]; 
    //    [audioPlayer stop]; // Does not reset currentTime; sending play resumes
	
	if (audioPlayer == nil)
		NSLog([error description]);				
	else 
		[audioPlayer play];
    if(currentTimerVal != lastTimerVal) {
        [dropletTimer invalidate];
        dropletTimer = [NSTimer scheduledTimerWithTimeInterval:currentTimerVal target:self selector:@selector(playDroplet) userInfo:nil repeats:YES];
        lastTimerVal = currentTimerVal;
    }
    
}

@end
