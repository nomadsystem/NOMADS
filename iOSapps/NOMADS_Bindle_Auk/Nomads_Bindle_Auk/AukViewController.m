//
//  AukViewController.m
//  NOMADS_Bindle_Auk

//  Created by Steven Kemper on 7/18/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AukViewController.h"
#import "SwarmDrawView.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "NAppIDAuk.h"
#import "NCommandAuk.h"

@implementation AukViewController

@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;

@synthesize settingsNavBackButton;
@synthesize joinNomadsButton;
@synthesize leaveNomadsButton;
@synthesize settingsNavTitle;
@synthesize settingsNavBar;
@synthesize settingsView;
@synthesize aukView;
@synthesize connectionLabel;
@synthesize joinTextField;
@synthesize aukToolbar;
@synthesize aukBarButtonDiscuss;
@synthesize aukBarButtonCloud;
@synthesize aukBarButtonSettings;
@synthesize inputDiscussField;
@synthesize inputCloudField;
@synthesize swarmView;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate]; //Sets as delegate to BindleAppDelegate
        
        [appDelegate->appSand setDelegate:self]; // SAND:  set a pointer inside appSand so we get notified when network data is available
        
        [self.view bringSubviewToFront:aukView]; //Load the aukView
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //Hides our "hidden" text fields for discuss and cloud
    inputDiscussField.hidden = YES; 
    inputCloudField.hidden = YES;
    
    //Inits settingsNavBar
    settingsNavBar.hidden = NO;
    
    //Init loginScreen
    [leaveNomadsButton setHidden:NO];
    connectionLabel.text = @"Connected to NOMADS!";
    
    //Initialize backgrounds 
    [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
    [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
    
    [[self swarmView] setBackgroundColor:[UIColor whiteColor]];
    
    //Initialization for size of SwarmDraw View
    CGRect screenRect = [swarmView bounds];
    CGFloat screenWidth = screenRect.size.width;
    CGFloat screenHeight = screenRect.size.height;
    
    CGPoint myCGPoint;
    myCGPoint.x = 0;
    myCGPoint.y = 0;
    
    CGSize myCGSize;
    myCGSize.height = screenHeight;
    myCGSize.width = screenWidth;
    
    CGRect myCGRect;
    myCGRect.origin = myCGPoint;
    myCGRect.size = myCGSize;
    
    [swarmView addSubview:[[SwarmDrawView alloc] initWithFrame:myCGRect]];    
    
    
    //Initialize toolbar in Auk view
    [[self aukToolbar] setTranslucent:YES];
    [appDelegate->appSand connect];  
    
    Byte c[1];
    c[0] = 1;
    //****STK 7/25/12 Need to fix NSand to send UINT8 from iOS
    [appDelegate->appSand sendWithGrainElts_AppID:OPERA_CLIENT  
                                          Command:REGISTER 
                                         DataType:UINT8 
                                          DataLen:1 
                                            Uint8:c];
    
    // Do any additional setup after loading the view from its nib.
}

// input data function ============================================
- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"AVC: Data Ready Handle\n");
    if (nil != inGrain) {
        //  CLog(@"AVC: Data Ready Handle\n");
    }
}

// Login button, currently disabled ****STK 7/30/12 ===============================================

//- (IBAction)joinNomadsButton:(id)sender {
//    [joinTextField resignFirstResponder];
//    [appDelegate->appSand connect];  
//    
//    Byte c[1];
//    c[0] = 1;
//    //****STK 7/25/12 Need to fix NSand to send UINT8 from iOS
//    [appDelegate->appSand sendWithGrainElts_AppID:OPERA_CLIENT  
//                                                   Command:REGISTER 
//                                                  DataType:UINT8 
//                                                   DataLen:1 
//                                                    Uint8:c];
//    
//    if ([joinTextField.text length] > 0){
//        
//        //****STK 7/25/12 Not currently checking settings, to be implemented
////        [appDelegate->appSand sendWithGrainElts_AppID:OC_LOGIN  
////                                              Command:SEND_MESSAGE 
////                                             DataType:CHAR 
////                                              DataLen:[joinTextField.text length] 
////                                               String:joinTextField.text];
//        joinTextField.text = @"";
//        [joinTextField setHidden:YES];
//        [joinNomadsButton setHidden:YES];
//        [leaveNomadsButton setHidden:NO];
//        
//        connectionLabel.text = @"Connected to NOMADS!";
//        
//        [self.view bringSubviewToFront:aukView];
//        
//        settingsNavBar.hidden = NO;
//    }
//    //If there's no text, connect with a "space" for now 
//    //We want to revise this to generate a warning message to the user
//    else {
//        connectionLabel.text = @"Error connecting: Please enter username!";
//    }
//}

- (IBAction)settingsNavBackButton:(id)sender {
    [self.view bringSubviewToFront:aukView];
}

- (IBAction)leaveNomadsButton:(id)sender {
    connectionLabel.text = @"Leaving NOMADS (but not really)";
    [leaveNomadsButton setHidden:YES];
    [joinTextField setHidden:NO];
    [joinNomadsButton setHidden:NO];
}

// Auk view items ===============================================

- (IBAction)discussButton:(id)sender {
    [aukView bringSubviewToFront:inputDiscussField];
    [inputDiscussField setHidden:NO];
    [inputDiscussField becomeFirstResponder];
    
}

- (IBAction)cloudButton:(id)sender {
    [aukView bringSubviewToFront:inputCloudField];
    [inputCloudField setHidden:NO];
    [inputCloudField becomeFirstResponder];
}

- (IBAction)settingsButton:(id)sender {
    [self.view bringSubviewToFront:settingsView];
}

-(IBAction) backgroundTapDiscuss:(id)sender{
    [self.inputDiscussField resignFirstResponder];
}
-(IBAction) backgroundTapCloud:(id)sender{
    [self.inputCloudField resignFirstResponder];
}


//Text Field Handler ===============================================
- (BOOL)textFieldShouldReturn:(UITextField *) textField
{   
    
    // Text field from settings screen 
    //    if (textField == joinTextField) 
    //        [self joinNomadsButton:(id)self];
    
    //---------------------------------------------------
    
    // Text field from Discuss Button 
    if (textField == inputDiscussField) {
        CLog(@"Entered sendDiscuss");
        if([inputDiscussField.text length]>0) { //Prevents null strings from being sent
            
            //AppID
            Byte myAppID = OC_DISCUSS;
            NSLog(@"myAppID =  %i\n", myAppID);
            
            //COMMAND
            Byte myCommand = SEND_MESSAGE;
            NSLog(@"myCommand =  %i\n", myCommand);
            
            //DATA TYPE
            Byte myDataType = CHAR;
            NSLog(@"myDataType =  %i\n", myDataType);
            
            //DATA LENGTH
            //****STK Currently set directly in sendWithGrainElts
            
            //DATA ARRAY (String from inputDiscussField)
            //****STK Currently set directly in sendWithGrainElts
            
            [appDelegate->appSand sendWithGrainElts_AppID:myAppID 
                                                  Command:myCommand 
                                                 DataType:myDataType 
                                                  DataLen:[inputDiscussField.text length] 
                                                   String:inputDiscussField.text];
            
            inputDiscussField.text = @"";
            [inputDiscussField setHidden:YES];
            [inputDiscussField resignFirstResponder];
            [aukView sendSubviewToBack:inputDiscussField];
        }
        else {
            inputDiscussField.text = @"";
            [inputDiscussField setHidden:YES];
            [inputDiscussField resignFirstResponder];
            [aukView sendSubviewToBack:inputDiscussField];
        }
    }
    
    //---------------------------------------------------
    
    // Text field from Cloud Button 
    if (textField == inputCloudField) {
        NSLog(@"Entered sendCloud");
        if([inputCloudField.text length]>0) { //Prevents null strings from being sent
            
            //AppID
            Byte myAppID = OC_CLOUD;
            NSLog(@"myAppID =  %i\n", myAppID);
            
            //COMMAND
            Byte myCommand = SEND_MESSAGE;
            NSLog(@"myCommand =  %i\n", myCommand);
            
            //DATA TYPE
            Byte myDataType = CHAR;
            NSLog(@"myDataType =  %i\n", myDataType);
            
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
            [inputCloudField setHidden:YES];
            [inputCloudField resignFirstResponder];
            [aukView sendSubviewToBack:inputCloudField];
        }
        else {
            inputCloudField.text = @"";
            [inputCloudField setHidden:YES];
            [inputCloudField resignFirstResponder];
            [aukView sendSubviewToBack:inputCloudField];  
        }
    }
    
    //---------------------------------------------------
    
    [textField resignFirstResponder];
    
    return YES;   
}



//iOS Stuff ==============================================================

- (void)viewDidUnload
{
    
    [self setConnectionLabel:nil];
    connectionLabel = nil;
    [self setJoinNomadsButton:nil];
    [self setJoinTextField:nil];
    [self setSettingsView:nil];
    [self setAukView:nil];
    [self setAukToolbar:nil];
    [self setAukBarButtonDiscuss:nil];
    [self setAukBarButtonCloud:nil];
    [self setAukBarButtonSettings:nil];
    [self setJoinNomadsButton:nil];
    [self setLeaveNomadsButton:nil];
    [self setInputDiscussField:nil];
    [self setInputCloudField:nil];
    [self setSettingsNavBar:nil];
    [self setSettingsNavTitle:nil];
    [self setSettingsNavBackButton:nil];
    [self setSwarmView:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
