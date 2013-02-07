//
//  SwarmViewController.m
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SwarmViewController.h"
#import "SwarmDrawView.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "NAppIDMain.h"
#import "NCommandMain.h"

@implementation SwarmViewController

@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize tbi;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Pointer" image:[UIImage imageNamed:@"Pointer_30x30.png"] tag:5];
        tbi = [self tabBarItem];

        [tbi setEnabled:NO];
        // [tbi setTitle:@"Pointer"];
        // UIImage *i3 = [UIImage imageNamed:@"tPoll.png"];
        // [tbi setImage:i3];

        
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
    }
    
    return self;
}
- (void)viewDidLoad
{
    [super viewDidLoad];

	// Do any additional setup after loading the view.
    
    
//    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];

        UIImage * targetImage = [UIImage imageNamed:@"SandDunes1_960x640.png"];

        // redraw the image to fit |yourView|'s size
        UIGraphicsBeginImageContextWithOptions([self view].frame.size, NO, 0.f);
        [targetImage drawInRect:CGRectMake(0.f, 0.f, [self view].frame.size.width, [self view] .frame.size.height)];
        UIImage * resultImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
    
    [self setView:[[SwarmDrawView alloc] initWithFrame:CGRectZero]];

    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];



    //
    //    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];

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
- (void)viewDidUnload
{

    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
