//
//  PollViewController.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/16/12.
//
#import "PollViewController.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "NAppIDMain.h"
#import "NCommandMain.h"

int oneToTenVoteVal;

@implementation PollViewController

@synthesize oneToTenView;
@synthesize aeView;
@synthesize yesNoView;
@synthesize pollPromptYesNoLabel;
@synthesize blankView;
@synthesize pollPromptAeLabel;
@synthesize pollPromptBlankLabel;
@synthesize pollPromptOneToTenLabel;
@synthesize pollOneToTenValLabel;
@synthesize messages;

@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize tbi;



- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Poll" image:[UIImage imageNamed:@"Poll_30x30.png"] tag:0];
        
        tbi = [self tabBarItem];
        // [tbi setTitle:@"Poll"];
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
    messages = [[NSMutableArray alloc] init];
    
    UIImage * targetImage = [UIImage imageNamed:@"SandDunes1_960x640.png"];
    
    // redraw the image to fit |yourView|'s size
    UIGraphicsBeginImageContextWithOptions([self view].frame.size, NO, 0.0f);
    [targetImage drawInRect:CGRectMake(0.f, 0.f, [self view].frame.size.width, [self view] .frame.size.height)];
    UIImage * resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    
    [[self blankView] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];
    [[self aeView] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];
    [[self yesNoView] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];
    [[self oneToTenView] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];

    pollOneToTenValLabel.text = @"5";
    [self.view bringSubviewToFront:blankView];
}

- (IBAction)pollSendYes:(id)sender //I'm not sure about this "id sender" business
{
    int x[1];
    x[0] = 1;
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_YES_NO
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];
    [buttonYOut setEnabled:false];
    [buttonNOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];

}
- (IBAction)pollSendNo:(id)sender
{
    int x[1];
    x[0] = 0;
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_YES_NO
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];

    [buttonYOut setEnabled:false];
    [buttonNOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];

}
- (IBAction)pollSendA:(id)sender {
    int x[1];
    x[0] = 1;
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_A_TO_E
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];
    [buttonAOut setEnabled:false];
    [buttonBOut setEnabled:false];
    [buttonCOut setEnabled:false];
    [buttonDOut setEnabled:false];
    [buttonEOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];
    
}

- (IBAction)pollSendB:(id)sender {
    int x[1];
    x[0] = 2;
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_A_TO_E
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];
    
    [buttonAOut setEnabled:false];
    [buttonBOut setEnabled:false];
    [buttonCOut setEnabled:false];
    [buttonDOut setEnabled:false];
    [buttonEOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];
    
}

- (IBAction)pollSendC:(id)sender {
    int x[1];
    x[0] = 3;
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_A_TO_E
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];
    
    [buttonAOut setEnabled:false];
    [buttonBOut setEnabled:false];
    [buttonCOut setEnabled:false];
    [buttonDOut setEnabled:false];
    [buttonEOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];
    
}

- (IBAction)pollSendD:(id)sender {
    int x[1];
    x[0] = 4;
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_A_TO_E
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];
    
    [buttonAOut setEnabled:false];
    [buttonBOut setEnabled:false];
    [buttonCOut setEnabled:false];
    [buttonDOut setEnabled:false];
    [buttonEOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];
    
}

- (IBAction)pollSendE:(id)sender {
    int x[1];
    x[0] = 5;
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_A_TO_E
                                         DataType:INT32
                                          DataLen:1
                                            Int32:x];
    
    [buttonAOut setEnabled:false];
    [buttonBOut setEnabled:false];
    [buttonCOut setEnabled:false];
    [buttonDOut setEnabled:false];
    [buttonEOut setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];
    
}

- (IBAction)pollOneToTenSliderChanged:(id)sender {
    UISlider *slider = (UISlider *)sender;

    oneToTenVoteVal = (int)slider.value;
    NSString *newText = [[NSString alloc] initWithFormat:@"%d", oneToTenVoteVal];
    pollOneToTenValLabel.text = newText;

}

- (IBAction)pollOneToTenVoteButton:(id)sender {
    
    int x[1];
    x[0] = oneToTenVoteVal;
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL
                                          Command:QUESTION_TYPE_ONE_TO_TEN
                                         DataType:INT32
                                          DataLen:1
                                           Int32:x];
    
    CLog(@"VOTE VAL STRING: %@",voteValString);
    [sender setEnabled:false];
    [sender setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                               green:((float) 0.0f)
                                                blue:((float) 0.0f)
                                               alpha:1.0f]];
    [oneToTenSlider setEnabled:false];
}

// input data function ============================================

- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"PVC: I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) {
        
        if(inGrain->appID == INSTRUCTOR_PANEL)
        {
            
            //Instructor Panel Commands NOT YET IMPLEMENTED!
        }
        else if(inGrain->appID == TEACHER_POLL)
        {
            //The question asked by the poll
            NSString *toBePosed = inGrain->str;
            CLog(@"PVC: toBePosed = %@",toBePosed);
            
            if (inGrain->command == QUESTION_TYPE_A_TO_E)
            {
                CLog(@"PVC: We got an A-E Question");
                pollPromptAeLabel.text = toBePosed;
                [self.view bringSubviewToFront:aeView];
                [buttonAOut setEnabled:true];
                [buttonAOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                              green:((float) 0.0f)
                                                               blue:((float) 0.0f)
                                                              alpha:0.0f]];
                [buttonBOut setEnabled:true];
                [buttonBOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];
                [buttonCOut setEnabled:true];
                [buttonCOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];
                [buttonDOut setEnabled:true];
                [buttonDOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];
                [buttonEOut setEnabled:true];
                [buttonEOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];

            }
            
            if (inGrain->command == QUESTION_TYPE_ONE_TO_TEN)
            {
                CLog(@"PVC: We got an 1-10 Question");
                
                pollPromptOneToTenLabel.text = toBePosed;
                [self.view bringSubviewToFront:oneToTenView];
                [voteButton setEnabled:true];
                [voteButton setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];
                [oneToTenSlider setEnabled:true];

                oneToTenSlider.minimumTrackTintColor = [UIColor whiteColor];
                oneToTenSlider.maximumTrackTintColor = [UIColor whiteColor];
                oneToTenSlider.thumbTintColor = [UIColor grayColor];

            }
            if (inGrain->command == QUESTION_TYPE_YES_NO)
            {
                CLog(@"PVC: We got a Yes-No Question");
                pollPromptYesNoLabel.text = toBePosed;
                [self.view bringSubviewToFront:yesNoView];
                [buttonYOut setEnabled:true];
                [buttonNOut setEnabled:true];
                [buttonYOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];
                [buttonNOut setBackgroundColor:[UIColor colorWithRed:((float) 0.0f)
                                                               green:((float) 0.0f)
                                                                blue:((float) 0.0f)
                                                               alpha:0.0f]];

                
            }
            
        }
        
        else {
            CLog(@"No Data for Poll App");
        }
    }
}



- (void)viewDidUnload
{
    [self setPollPromptYesNoLabel:nil];
    [self setYesNoView:nil];
    [self setAeView:nil];
    [self setPollPromptAeLabel:nil];
    [self setBlankView:nil];
    [self setPollPromptBlankLabel:nil];
    [self setPollPromptYesNoLabel:nil];
    [self setOneToTenView:nil];
    [self setPollPromptOneToTenLabel:nil];
    [self setPollOneToTenValLabel:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end

