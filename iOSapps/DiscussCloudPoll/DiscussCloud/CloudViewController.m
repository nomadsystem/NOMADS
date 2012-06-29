//
//  CloudViewController.m
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import "CloudViewController.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"

//@interface CloudViewController ()

//@end

@implementation CloudViewController
@synthesize cloudLabel;
@synthesize inputCloudField;
@synthesize messages;
@synthesize sendCloudButton;
@synthesize cloudSand;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        UITabBarItem *tbi = [self tabBarItem];
        [tbi setTitle:@"Thought Cloud"];
        cloudSand = [[NSand alloc] init]; 
        
     //   [cloudSand connect];
    }
    [cloudSand setDelegate:self];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    inputCloudField.text = @"";
    messages = [[NSMutableArray alloc] init];
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];

}


- (IBAction)sendCloud
{
    [inputCloudField resignFirstResponder];
    NSLog(@"Entered sendCloud");
    
    //AppID
    Byte myAppID = CLOUD_CHAT;
    NSLog(@"myAppID =  %i\n", myAppID);
    
    //COMMAND
    Byte myCommand = SEND_MESSAGE;
    NSLog(@"myCommand =  %i\n", myCommand);
    
    //DATA TYPE
    Byte myDataType = BYTE;
    NSLog(@"myDataType =  %i\n", myDataType);
    
    //DATA LENGTH
    //****STK Currently set directly in sendWithGrainElts
    
    //DATA ARRAY (String from inputDiscussField)
    //****STK Currently set directly in sendWithGrainElts
    
    [cloudSand sendWithGrainElts_AppID:myAppID 
                                 Command:myCommand 
                                DataType:myDataType 
                                 DataLen:[inputCloudField.text length] 
                                  String:inputCloudField.text];

    inputCloudField.text = @"";
    [inputCloudField setHidden:NO];
}
//- (void)dataReadyHandle:(NGrain *)inGrain
//{
//    NSLog(@"CVC: I GOT DATA FROM SAND!!!\n");
//    
//    if (nil != inGrain) { 
//        
//        if(inGrain->appID == CLOUD_PROMPT)//Text from Discuss Prompt
//        {
//            cloudLabel.text = inGrain->str;
//        }
//        else if(inGrain->appID == INSTRUCTOR_PANEL)//Text from Instructor Panel
//        {
//            if ([inGrain->str isEqualToString:@"DISABLE_CLOUD_BUTTON"])
//            {
//                [sendCloudButton setEnabled:NO];
//                sendCloudButton.titleLabel.textColor = [UIColor grayColor];
//                [inputCloudField setBackgroundColor:[UIColor grayColor]];
//            }
//            else if ([inGrain->str isEqualToString:@"ENABLE_CLOUD_BUTTON"])
//            {
//                [sendCloudButton setEnabled:YES];
//                sendCloudButton.titleLabel.textColor = [UIColor colorWithRed:0.196 green:0.3098 blue:0.5216 alpha:1.0]; 
//                [inputCloudField setBackgroundColor:[UIColor whiteColor]];
//            }
//        }
//        else {
//            NSLog(@"No Data for Discuss App");
//        }
//    }
//}


- (void) messageReceived:(NSString *)message {
	
	[self.messages addObject:message];
    
}

- (void)viewDidUnload
{
    [self setInputCloudField:nil];
    [self setCloudLabel:nil];
    [self setSendCloudButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

//- (BOOL)textFieldShouldReturn:(UITextField *) textField
//{   
//    if (textField == inputCloudField) 
//        [self sendCloud];
//    
//    [textField resignFirstResponder];
//    
//    return YES;   
//}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
