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
    [[self blankView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self aeView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self yesNoView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self oneToTenView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    pollOneToTenValLabel.text = @"5";
    [self.view bringSubviewToFront:blankView];
}

- (IBAction)pollSendYes:(id)sender //I'm not sure about this "id sender" business
{
    NSString *answerString = @"yes"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_YES_NO 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}
- (IBAction)pollSendNo:(id)sender
{
    NSString *answerString = @"no"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_YES_NO 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}
- (IBAction)pollSendA:(id)sender {
    NSString *answerString = @"A"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_A_TO_E 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}

- (IBAction)pollSendB:(id)sender {
    NSString *answerString = @"B"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_A_TO_E 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}

- (IBAction)pollSendC:(id)sender {
    NSString *answerString = @"C"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_A_TO_E 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}

- (IBAction)pollSendD:(id)sender {
    NSString *answerString = @"D"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_A_TO_E 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}

- (IBAction)pollSendE:(id)sender {
    NSString *answerString = @"E"; 
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_A_TO_E 
                                         DataType:CHAR 
                                          DataLen:[answerString length] 
                                           String:answerString];
}

- (IBAction)pollOneToTenSliderChanged:(id)sender {
    UISlider *slider = (UISlider *)sender;
    oneToTenVoteVal = (int)slider.value;
    NSString *newText = [[NSString alloc] initWithFormat:@"%d", oneToTenVoteVal];
    pollOneToTenValLabel.text = newText;
}

- (IBAction)pollOneToTenVoteButton:(id)sender {
    NSString *voteValString = [NSString stringWithFormat:@"%d", oneToTenVoteVal];
    
    
    [appDelegate->appSand sendWithGrainElts_AppID:STUDENT_POLL 
                                          Command:QUESTION_TYPE_ONE_TO_TEN 
                                         DataType:CHAR 
                                          DataLen:[voteValString length] 
                                           String:voteValString];
    NSLog(@"VOTE VAL STRING: %@",voteValString);
    
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
            }
            
            if (inGrain->command == QUESTION_TYPE_ONE_TO_TEN)
            {
                CLog(@"PVC: We got an 1-10 Question");
                pollPromptOneToTenLabel.text = toBePosed;
                [self.view bringSubviewToFront:oneToTenView];
            }
            if (inGrain->command == QUESTION_TYPE_YES_NO)
            {
                CLog(@"PVC: We got a Yes-No Question");
                pollPromptYesNoLabel.text = toBePosed;
                [self.view bringSubviewToFront:yesNoView];
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

