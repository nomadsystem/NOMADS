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
#import "NAppIDMain.h"
#import "NCommandMain.h"

//@interface CloudViewController ()

//@end

@implementation CloudViewController

@synthesize cloudLabel;
@synthesize sendCloudButton;
@synthesize inputCloudField;
@synthesize messages;
@synthesize appSand;
@synthesize appDelegate;
@synthesize tbi;
@synthesize userCloudEntryLabel;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Cloud" image:[UIImage imageNamed:@"Cloud_30x30.png"] tag:2];
        
        tbi = [self tabBarItem];
        [tbi setTitle:@"Cloud"];
        // UIImage *i2 = [UIImage imageNamed:@"tCloud.png"];
        // [tbi setImage:i2];
        
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
    }
    //[cloudSand setDelegate:self];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    inputCloudField.text = @"";
    cloudLabel.text = @"Thought Cloud";
    userCloudEntryLabel.text = @"";
    
    messages = [[NSMutableArray alloc] init];
    UIImage * targetImage = [UIImage imageNamed:@"SandDunes1_960x640.png"];
    
    // redraw the image to fit |yourView|'s size
    UIGraphicsBeginImageContextWithOptions([self view].frame.size, NO, 0.f);
    [targetImage drawInRect:CGRectMake(0.f, 0.f, [self view].frame.size.width, [self view] .frame.size.height)];
    UIImage * resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];
	
}


- (IBAction)sendCloud:(id)sender {
    
    if ([inputCloudField.text length] > 1) {

        userCloudEntryLabel.text = inputCloudField.text;
        
        [inputCloudField resignFirstResponder];
        CLog(@"Entered sendCloud");
        
        //Ap    pID
        Byte myAppID = CLOUD_CHAT;
        CLog(@"myAppID =  %i\n", myAppID);
        
        //COMMAND
        Byte myCommand = SEND_MESSAGE;
        CLog(@"myCommand =  %i\n", myCommand);
        
        //DATA TYPE
        Byte myDataType = CHAR;
        CLog(@"myDataType =  %i\n", myDataType);
        
        //DATA LENGTH
        //****STK Currently set directly in sendWithGrainElts
        
        //DATA ARRAY (String from inputDiscussField)
        //****STK Currently set directly in sendWithGrainElts
        
        [appDelegate->appSand sendWithGrainElts_AppID:myAppID
                                              Command:myCommand
                                             DataType:myDataType
                                              DataLen:[inputCloudField.text length]
                                               String:inputCloudField.text];
        
        inputCloudField.text = @"";
        [inputCloudField setHidden:NO];
    }
}

- (BOOL)textFieldShouldReturn:(UITextField *) textField {
    
    if ([inputCloudField.text length] > 1) {
        

        userCloudEntryLabel.text = inputCloudField.text;
        
        [inputCloudField resignFirstResponder];
        CLog(@"Entered sendCloud");
        
        //Ap    pID
        Byte myAppID = CLOUD_CHAT;
        CLog(@"myAppID =  %i\n", myAppID);
        
        //COMMAND
        Byte myCommand = SEND_MESSAGE;
        CLog(@"myCommand =  %i\n", myCommand);
        
        //DATA TYPE
        Byte myDataType = CHAR;
        CLog(@"myDataType =  %i\n", myDataType);
        
        //DATA LENGTH
        //****STK Currently set directly in sendWithGrainElts
        
        //DATA ARRAY (String from inputDiscussField)
        //****STK Currently set directly in sendWithGrainElts
        
        [appDelegate->appSand sendWithGrainElts_AppID:myAppID
                                              Command:myCommand
                                             DataType:myDataType
                                              DataLen:[inputCloudField.text length]
                                               String:inputCloudField.text];
        
        inputCloudField.text = @"";
        [inputCloudField setHidden:NO];
        [inputCloudField resignFirstResponder];
        //            [[self view ] sendSubviewToBack:inputDiscussField];
    }
    else { //Dismisses keyboard if no text is entered but send button is pressed
        inputCloudField.text = @"";
        [inputCloudField setHidden:NO];
        [inputCloudField resignFirstResponder];
        //            [[self view ] sendSubviewToBack:inputDiscussField];
    }
    return YES;
}


- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"CVC: I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) {
        
        if(inGrain->appID == CLOUD_PROMPT)//Text from Discuss Prompt
        {
            CLog(@"Got prompt:  %@\n",inGrain->str);
            cloudLabel.text = inGrain->str;
        }
        else if(inGrain->appID == INSTRUCTOR_PANEL)//Text from Instructor Panel
        {
            if ([inGrain->str isEqualToString:@"DISABLE_CLOUD_BUTTON"])
            {
                [sendCloudButton setEnabled:NO];
                sendCloudButton.titleLabel.textColor = [UIColor grayColor];
                [inputCloudField setBackgroundColor:[UIColor grayColor]];
            }
            else if ([inGrain->str isEqualToString:@"ENABLE_CLOUD_BUTTON"])
            {
                [sendCloudButton setEnabled:YES];
                sendCloudButton.titleLabel.textColor = [UIColor colorWithRed:0.196 green:0.3098 blue:0.5216 alpha:1.0];
                [inputCloudField setBackgroundColor:[UIColor whiteColor]];
            }
        }
        else {
            CLog(@"No Data for Cloud App");
        }
    }
}


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
