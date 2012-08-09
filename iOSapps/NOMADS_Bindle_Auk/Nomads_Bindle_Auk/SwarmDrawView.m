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
@synthesize prompt;

-(id)initWithFrame:(CGRect)r
{
    self = [super initWithFrame:r];
    if (self) {
        
        toneVolScaler = 1.0;
        tonePlayer = 0;
        toneVolDone = false;
        numRunTonePlayers = 0;
        
        // Graphics setup
        viewRect = [self bounds];
        viewHeight = viewRect.size.height;
        viewWidth = viewRect.size.width;
        
        CLog(@"viewHeight = %f", viewHeight);
        CLog(@"viewWidth = %f", viewWidth);
        
        //Scale for pointer output between 0-1000 (To become 0-1)
        viewHeightScale = (1000/viewHeight);
        viewWidthScale = (1000/viewWidth);
        
        CLog(@"viewHeightScale = %f", viewHeightScale);
        CLog(@"viewWidthScale = %f", viewWidthScale);
        
        // Prompt text
        prompt = [NSString stringWithFormat:@"Auksalaq NOMADS"];
        promptAlpha = 0;
        
        if ( UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad )
        {
            promptTextSize = 50;
            discussTextSize = 18;
        }
        else {
            promptTextSize = 30;
            discussTextSize = 10;
        }
        
        // chat lines
        numChatLines = 15; //Initialize number of chat lines to display
        chatLines = [[NSMutableArray alloc] init];
        
        //Code to get the point to start at the center of the screen
        [self setMultipleTouchEnabled:YES];
        
        myFingerPoint.x = (CGRectGetMidX(viewRect));
        myFingerPoint.y = (CGRectGetMidY(viewRect));
        
        maxTrails = 10;
        
        xTrail = (int *)malloc(sizeof(int)*maxTrails);
        yTrail = (int *)malloc(sizeof(int)*maxTrails);
        
        for (int i=0;i<maxTrails;i++) {
            xTrail[i]=(viewWidth * 0.5);
            yTrail[i]=(viewHeight * 0.5);
        }
        decayColor = 1.0;
        decayColorChangeDelta = (decayColor/(float)maxTrails);
        dropFlash = NO;
        
        ellipseR = decayColor;
        ellipseG = touchColor;
        ellipseB = 1.0;
        ellipseA = decayColor;
        
        dotSizeScaler = 1;
        
        //    CLog(@" brightness = %f ", brightness);
        //    CLog(@" brightDelta = %f ", brightDelta);
        
        //     CLog(@"center point = %f , %f ",myFingerPoint.x, myFingerPoint.y);
        
        
        //Set up our Audio Session
        
        NSError *activationError = nil;
        session = [AVAudioSession sharedInstance];
        [session setActive:YES error:&activationError];
        NSError *setCategoryError = nil;
        
        //This category should prevent our audio from being interrupted by incoming calls, etc.
        [session setCategory:AVAudioSessionCategoryPlayback error:&setCategoryError];
        if (setCategoryError) {
            CLog("Error initializing Audio Session Category");
        }
        dropletVolume = 1.0;
        toneVolume = 1.0;
        toneCntrlOn = NO;
        maxNumOfTonePlayers = 19;
        
        promptWaitTick = 0;
        promptWaitTimer =[NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(zeroPrompt) userInfo:nil repeats:YES];
        
        cloudStatus = 0;
        discussStatus = 0;
        pointerStatus = 0;
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        [appDelegate->appSand setDelegate:self];
        
    }
    return self;
}

//Data Ready Handle======================================================

- (void)dataReadyHandle:(NGrain *)inGrain
{
    //This delegate not being
    CLog(@"SwarmDrawView: Data Ready Handle\n");
    
    if (nil != inGrain) {
        
        // Set respective STATUS of various app components
        
        if(inGrain->appID == CONDUCTOR_PANEL) {
            if(inGrain->command == SET_DROPLET_STATUS) {
                if (inGrain->bArray[0] == 1) {
                    currentTimerVal = 4.0;
                    lastTimerVal = 4.0;
                    fileNumDroplets = 1;
                    dropletTimer = [NSTimer scheduledTimerWithTimeInterval:currentTimerVal target:self selector:@selector(playDroplet) userInfo:nil repeats:YES];
                    CLog("SET_DROPLET_STATUS = ON");
                    
                }
                else if (inGrain->bArray[0] == 0) {
                    if (dropletTimer) {
                        [dropletTimer invalidate];
                    }
                    CLog("SET_DROPLET_STATUS = OFF");
                }
                
            }
            else if(inGrain->command == SET_POINTER_TONE_STATUS) {
                if (inGrain->bArray[0] == 1) {
                    fileNumTones = 1;
                    toneCntrlOn = YES;
   //                 [self playTone];
                    CLog("SET_POINTER_TONE_STATUS = ON");
                    
                }
                else if (inGrain->bArray[0] == 0) {
                    toneCntrlOn = NO;
                    if (toneTimer)
                    [toneTimer invalidate];
                    CLog("SET_DROPLET_STATUS = OFF");
                }
                
                
            }
            else if (inGrain->command == SET_DROPLET_VOLUME) {
                //   float noteVolume = (((inGrain->iArray[0])*0.01);//****STK data to be scaled from 0-1
                dropletVolume =  (inGrain->iArray[0]*0.01);
                CLog("inGrain setting dropletVolume = %f", dropletVolume);
                
            }
            else if (inGrain->command == SET_POINTER_TONE_VOLUME) {
                //   float noteVolume = (((inGrain->iArray[0])*0.01);//****STK data to be scaled from 0-1
                toneVolume =  (inGrain->iArray[0]*0.01);
                CLog("inGrain setting pointerTone volume = %f", toneVolume);
                
            }
            else if(inGrain->command == SET_DISCUSS_STATUS) {
                discussStatus = (Boolean)inGrain->bArray[0];
            }
            else if(inGrain->command == SET_CLOUD_STATUS) {
                cloudStatus = (Boolean)inGrain->bArray[0];
            }
            else if(inGrain->command == SET_POINTER_STATUS) {
                pointerStatus = (Boolean)inGrain->bArray[0];
                [self setNeedsDisplay];
            }
            
            
            else if(inGrain->command == SEND_PROMPT_ON) {
                // xxx
                prompt = inGrain->str;
                promptFadeInVal = 0.05;
                promptFadeInTimer =[NSTimer scheduledTimerWithTimeInterval:promptFadeInVal target:self selector:
                                    @selector(fadeInPrompt) userInfo:nil repeats:YES];
                
            }
            else if(inGrain->command == SEND_PROMPT_OFF) {
                // xxx
                promptFadeOutTimer =[NSTimer scheduledTimerWithTimeInterval:promptFadeOutTick target:self selector:@selector(fadeOutPrompt) userInfo:nil repeats:YES];
                
            }
            
            
        }
        
        
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
    // Need to recheck bounds in case of device rotation
    viewRect = [self bounds];
    viewHeight = viewRect.size.height;
    viewWidth = viewRect.size.width;
    
    
    //Scale for pointer output between 0-1000 (To become 0-1)
    viewHeightScale = (int)(1000/viewHeight);
    viewWidthScale = (int)(1000/viewWidth);
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetCharacterSpacing (context, 1);
    CGContextSetTextDrawingMode (context, kCGTextFillStroke);
    CGContextSetRGBFillColor (context, 1, 1, 1, promptAlpha);
    CGContextSetRGBStrokeColor (context, 1, 1, 1, promptAlpha);
    CGContextSetTextMatrix(context, CGAffineTransformMakeScale(1.0f, -1.0f));
    
    CGContextSetAllowsAntialiasing(context, YES);
    CGContextSetShouldAntialias(context, YES);
    CGContextSetShouldSmoothFonts(context, YES);
    
    
    // Display Prompt Text
    const char *str = [prompt cStringUsingEncoding:NSUTF8StringEncoding]; //convert to c-string
    int len = strlen(str); //get length of string
    
    CGContextSelectFont (context,
                         "Papyrus",
                         promptTextSize,
                         kCGEncodingMacRoman);
    
    CGContextShowTextAtPoint (context, (viewWidth * 0.1), (viewHeight * 0.1), str, len);
    
    // Display discussion text
    
    if (discussStatus) {
        CGContextSelectFont (context,
                             "Helvetica-Light",
                             discussTextSize,
                             kCGEncodingMacRoman);
        CGContextSetRGBFillColor (context, 0.7, 0.7, 0.8, 1);
        CGContextSetRGBStrokeColor (context, 0.7, 0.7, 0.8, 1);
        
        CGFloat chatSpace = (viewHeight/numChatLines) * 0.5;
        CGFloat chatYLoc = ((viewHeight-chatSpace) - (viewHeight * 0.1));
        CGFloat chatXLoc = (viewWidth * 0.1);
        
        
        for (int i=0;i<[chatLines count];i++) {
            
            NSString *nsstr = [chatLines objectAtIndex:i];; //Incoming NSString
            const char *str = [nsstr cStringUsingEncoding:NSUTF8StringEncoding]; //convert to c-string
            int len = strlen(str);
            printf("My String %s\n", str);
            CGContextShowTextAtPoint (context, chatXLoc, chatYLoc, str, len);
            CLog("chatXLoc = %f, chatYLoc = %f", chatXLoc, chatYLoc);
            
            chatYLoc -= chatSpace;
        }
    }
    
    // The Dot ======================================================
    
    if (pointerStatus) {
        for(int i=maxTrails;i>0;i--) {
            xTrail[i] = xTrail[i-1];
            yTrail[i] = yTrail[i-1];
        }
        
        xTrail[0] = myFingerPoint.x;
        yTrail[0] = myFingerPoint.y;
        
        decayColor = 1.0;
        float dotSize;
        if ( UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad )
        {
             dotSize = 50;
        }
        else {
            dotSize = 20;
        }
                
        for (int i=0; i<maxTrails; i++) {
            if (i<(maxTrails-1)) {
                //            ellipseR = decayColor;
                //            ellipseG = touchColor;
                //            ellipseB = 1.0;
                //            ellipseA = decayColor;
                int xDiff = xTrail[i]-xTrail[i+1];
                int yDiff = yTrail[i]-yTrail[i+1];
                
                if (i==0) {
                    if (dropFlash) {
                        CLog("dropFlash!");
                        CGContextSetRGBFillColor(context, ellipseR, ellipseG, ellipseB, ellipseA);
                        CLog("R = %f G = %f B = %f A = %F", ellipseR, ellipseG, ellipseB, ellipseA);
                        CGContextAddEllipseInRect(context,(CGRectMake (xTrail[0]-xDiff/2, yTrail[0]-yDiff/2, (dotSize*dotSizeScaler), (dotSize*dotSizeScaler))));
                        CGContextDrawPath(context, kCGPathFill);
                        //     CGContextFillPath(context);
                        CGContextStrokePath(context);
                    }
                }
                
                if ((abs(xDiff) > 6) || (abs(yDiff) > 6)) {
                    CGContextSetRGBFillColor(context, ellipseR, ellipseG, ellipseB, ellipseA);
                    
                    CGContextAddEllipseInRect(context,(CGRectMake (xTrail[i]-xDiff/2, yTrail[i]-yDiff/2, dotSize, dotSize)));
                    CGContextDrawPath(context, kCGPathFill);
                    //     CGContextFillPath(context);
                    CGContextStrokePath(context);
                    decayColor = (decayColor - decayColorChangeDelta);
                    ellipseR = decayColor;
                    ellipseG = touchColor;
                    ellipseA = decayColor;
                }
                
                
                CGContextSetRGBFillColor(context, ellipseR, ellipseG, ellipseB, ellipseA);
                CGContextAddEllipseInRect(context,(CGRectMake (xTrail[i], yTrail[i], dotSize, dotSize)));
                CGContextDrawPath(context, kCGPathFill);
                //     CGContextFillPath(context);
                CGContextStrokePath(context);
                decayColor = (decayColor - decayColorChangeDelta);
                dotSize *= 0.9;
                ellipseR = decayColor;
                ellipseG = touchColor;
                ellipseA = decayColor;
            }
        }
    }
    
    //Sounds ======================================================
    
    //Droplets
    int numOfDroplets = 203;
    float viewXGrid  = (viewWidth / 5);
    float viewYGrid  = (viewHeight / numOfDroplets);
    fileNumDroplets = (int)(myFingerPoint.y/viewYGrid);
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
    
    
    if (pointerStatus) {
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
        
        //Pointer Tones
        if (toneCntrlOn) {
            int numOfTones = 17;
            float viewYGrid  = (viewHeight / numOfTones);
            
            if (((int)(myFingerPoint.y/viewYGrid)) < 1) {
                fileNumTones = 1;
            }
            else {
                fileNumTones = (int)(myFingerPoint.y/viewYGrid);
            }
            toneTimer = [NSTimer scheduledTimerWithTimeInterval:0.25 target:self selector:@selector(playTone) userInfo:nil repeats:YES];
        }
        
        [self setNeedsDisplay];


//        [self playTone];

    }
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    
    
    
    
    if (pointerStatus) {
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
                CLog(@"SWARM_X loc = %f", loc.x);
                CLog(@"SWARM_Y loc = %f", loc.y);
                
                
                
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
            CLog(@"SWARM_X loc = %f", loc.x);
            CLog(@"SWARM_Y loc = %f", loc.y);
            
            
            
            //            float screenScaleX = 5.9;
            //            float screenScaleY = 3.33;
            //            int screenMinusX = 1000;
            //            int screenMinusY = 800;
            
            
            //            xy[0] = (int) ((loc.x * screenScaleX)- screenMinusX);
            //            xy[1] = (int) ((loc.y * screenScaleY)- screenMinusY);
            
            //STK Send out scaled values between 0-1000 (to become 0-1)
            
            int tIntX = (int) (loc.x * viewWidthScale);
            int tIntY = (int) (loc.y * viewHeightScale);
            CLog(@"SWARM_X SCALED = %d", tIntX);
            CLog(@"SWARM_Y SCALED = %d", tIntY);
            
            xy[0] = (int) (loc.x * viewWidthScale);
            xy[1] = (int) (loc.y * viewHeightScale);
            
            
            
        }
        [appDelegate->appSand sendWithGrainElts_AppID:OC_POINTER Command:SEND_SPRITE_XY DataType:INT32 DataLen:2 Int32:xy];
        
    }
    
}

- (void)endTouches:(NSSet *)touches
{
    maxTrails = 2;
    //Remove ending touches from dictionary
    for (UITouch *t in touches) {
        //      touchColor = 0.6;
        
    }
    numRunTonePlayers = 0;
    toneVolScaler = 1;
    toneVolDone = false;
    [toneTimer invalidate];
    //Redraw
    //[self setNeedsDisplay];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self endTouches:touches];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self endTouches:touches];
}

//Method called from dotFlashTimer--resets dot size/color
- (void)flashDot
{
    CLog("flashDot");
    ellipseR = decayColor;
    ellipseG = touchColor;
    ellipseB = 1.0;
    ellipseA = decayColor;
    dotSizeScaler = 1.0;
    [self setNeedsDisplay];
    dropFlash = NO;
}


- (void)zeroPrompt {
    
    CLog("zeroPrompt %2.2f\n",promptWaitTick);
    
    promptWaitTick += 1;
    if (promptWaitTick > 1) {
        [promptWaitTimer invalidate];
        CLog("deleting promptWaitTimer\n");
        promptFadeInVal = 0.05;
        promptFadeInTimer =[NSTimer scheduledTimerWithTimeInterval:promptFadeInVal target:self selector:
                            @selector(fadeInPrompt) userInfo:nil repeats:YES];
    }
}

- (void)fadeInPrompt
{
    //    CLog("fadeIntPrompt %2.2f\n",promptAlpha);
    
    if (promptAlpha == 0) {
        promptAlpha = 0.05;
    }
    promptAlpha *= 1.1;
    if (promptAlpha > 0.95) {
        if (promptFadeInTimer) {
            [promptFadeInTimer invalidate];
            CLog("deleting promptFadeInTimer\n");
            
        }
        //    CLog("fadeInPrompt calling -> fadeOutPrompt %2.2f\n",promptFadeOutTick);
        
        promptFadeOutTick = 1;
        //        promptFadeOutTimer =[NSTimer scheduledTimerWithTimeInterval:promptFadeOutTick target:self selector:@selector(fadeOutPrompt) userInfo:nil repeats:YES];
        promptAlpha = 1;
    }
    [self setNeedsDisplay];
}



- (void)fadeOutPrompt
{
    CLog("fadeOutPrompt %2.2f\n",promptFadeOutTick);
    
    if (promptAlpha > 0.95) {
        if (promptFadeOutTimer) {
            CLog("restarting promptFadeOutTimer\n");
            [promptFadeOutTimer invalidate];
        }
        promptFadeOutTick = 0.05;
        promptFadeOutTimer =[NSTimer scheduledTimerWithTimeInterval:promptFadeOutTick target:self selector:@selector(fadeOutPrompt) userInfo:nil repeats:YES];
    }
    promptAlpha *= 0.9;
    if (promptAlpha < 0.05) {
        CLog("deleting promptFadeOutTimer\n");
        
        [promptFadeOutTimer invalidate];
        promptAlpha = 0;
    }
    [self setNeedsDisplay];
}

-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    CLog("SDV: Audio finished playing");
    audioPlayerDroplet = nil;
 //   audioPlayerTone[tonePlayer] = nil;
}

// Play the sound

- (void)playDroplet
{
    
    NSString *soundFile;
    
    soundFile = [NSString stringWithFormat:@"sounds/GlacierSounds/%d.mp3",fileNumDroplets];
    
    NSURL *url = [NSURL fileURLWithPath:[NSString stringWithFormat:@"%@/%@", [[NSBundle mainBundle] resourcePath], soundFile]];
    
    CLog("URL: %@", url);

    
	NSError *error;
    if (audioPlayerDroplet == nil) {
        audioPlayerDroplet = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];
        [audioPlayerDroplet setDelegate:self];
        audioPlayerDroplet.numberOfLoops = 0;
        if (audioPlayerDroplet == nil) {
            CLog("SDV: Playback error: %@",[error description]);
        }
        else {
            audioPlayerDroplet.volume = dropletVolume;
            CLog("SDV: dropletVolume = %f", dropletVolume);
            [audioPlayerDroplet play];
        }
    }
    
    //****STK Other useful control parameters
    //    audioPlayer.volume = 0.5; // 0.0 - no volume; 1.0 full volume
    //    Clog(@"%f seconds played so far", audioPlayer.currentTime);
    //    audioPlayer.currentTime = 10; // jump to the 10 second mark
    //    [audioPlayer pause];
    //    [audioPlayer stop]; // Does not reset currentTime; sending play resumes
    
    dropFlash = YES;
    ellipseR = 1.0;
    ellipseG = 1.0;
    ellipseB = 1.0;
    ellipseA = 1.0;
    dotSizeScaler = 1.5;
    [self setNeedsDisplay];
    
    dotFlashTimer = [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(flashDot) userInfo:nil repeats:NO];
	
	
    if(currentTimerVal != lastTimerVal) {
        [dropletTimer invalidate];
        dropletTimer = [NSTimer scheduledTimerWithTimeInterval:currentTimerVal target:self selector:@selector(playDroplet) userInfo:nil repeats:YES];
        lastTimerVal = currentTimerVal;
    }
    
}

-(void)playTone
{
    NSString *soundFile;
    
    soundFile = [NSString stringWithFormat:@"sounds/AuksalaqSandPointerTones/tones%d.mp3",fileNumTones];
    
    NSURL *url = [NSURL fileURLWithPath:[NSString stringWithFormat:@"%@/%@", [[NSBundle mainBundle] resourcePath], soundFile]];
    
    CLog("URL: %@", url);
 
    CLog("SDV: toneVolScaler = %f", toneVolScaler);

	NSError *error;
    numRunTonePlayers++;
    if (numRunTonePlayers > 10) {
        toneVolDone = true;
    }
    
    if (tonePlayer > maxNumOfTonePlayers) {
        tonePlayer = 0;
        
    }
    else 
        tonePlayer++;
    
    if (!toneVolDone && (toneVolScaler > 0.25)) 
        toneVolScaler = (float)(1.0/(tonePlayer + 1.0));
    else 
        toneVolScaler = 0.25;

    CLog("SDV: toneVolScaler = %f", toneVolScaler);
    CLog("SDV: tonePlayer = %d", tonePlayer);

    
    audioPlayerTone[tonePlayer] = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];

   [audioPlayerTone[tonePlayer] setDelegate:self];
    
    audioPlayerTone[tonePlayer].numberOfLoops = 1;

    if (audioPlayerTone[tonePlayer] == nil) {
        CLog("SDV: Playback error: %@",[error description]);
    }
    else {
        CLog("SDV: tonVolume = %f", (toneVolume * toneVolScaler * 3));
        audioPlayerTone[tonePlayer].volume = toneVolume * toneVolScaler * 3;

        [audioPlayerTone[tonePlayer] play];

    }
}




@end
