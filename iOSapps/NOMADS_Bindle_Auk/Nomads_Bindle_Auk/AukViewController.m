//
//  AukViewController.m
//  NOMADS_Bindle_Auk
//  
//
//
//  Created by Steven Kemper on 7/18/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AukViewController.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"



@implementation AukViewController
@synthesize loginNavBackButton;
@synthesize joinNomadsButton;
@synthesize leaveNomadsButton;

@synthesize loginNavTitle;
@synthesize loginNavBar;
@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize loginView;
@synthesize aukView;
@synthesize promptLabel;
@synthesize connectionLabel;
@synthesize joinTextField;
@synthesize aukToolbar;
@synthesize aukBarButtonDiscuss;
@synthesize aukBarButtonCloud;
@synthesize aukBarButtonSettings;
@synthesize inputDiscussField;
@synthesize inputCloudField;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate]; //Sets as delegate to BindleAppDelegate
        
        [appDelegate->appSand setDelegate:self]; // SAND:  set a pointer inside appSand so we get notified when network data is available
        
        [self.view bringSubviewToFront:loginView]; //Load the login view
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //Hides our "hidden" text fields for discuss and cloud
    inputDiscussField.hidden = YES; 
    inputCloudField.hidden = YES; 
    
    //Initially hides the navigation bar in the login view
    loginNavBar.hidden = YES;
    
    //hides the button in the login screen that will disconnect, and the connection status label
    [leaveNomadsButton setHidden:YES];
    connectionLabel.text = @"";
    
    //Initialize backgrounds 
    [[self loginView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    
    //Initialize toolbar in Auk view
    [[self aukToolbar] setTranslucent:YES];
    
    
    // Do any additional setup after loading the view from its nib.
}

// input data function ============================================
- (void)dataReadyHandle:(NGrain *)inGrain
{
    
    if (nil != inGrain) {
        CLog(@"AVC: Data Ready Handle\n");
    }
}

// Login view items ===============================================

- (IBAction)joinNomadsButton:(id)sender {
    [joinTextField resignFirstResponder];
    [appDelegate->appSand connect];
    
    if ([joinTextField.text length] > 0){
        [appDelegate->appSand sendWithGrainElts_AppID:LOGIN 
                                              Command:SEND_MESSAGE 
                                             DataType:BYTE 
                                              DataLen:[joinTextField.text length] 
                                               String:joinTextField.text];
        joinTextField.text = @"";
        [joinTextField setHidden:YES];
        [joinNomadsButton setHidden:YES];
        [leaveNomadsButton setHidden:NO];
        
        connectionLabel.text = @"Connected to NOMADS!";
        [self.view bringSubviewToFront:aukView];
        loginNavBar.hidden = NO;
    }
    //If there's no text, connect with a "space" for now 
    //We want to revise this to generate a warning message to the user
    else {
        connectionLabel.text = @"Error connecting: Please enter username!";
    }
}

- (IBAction)loginNavBackButton:(id)sender {
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
    [inputDiscussField setHidden:NO];
    [inputDiscussField becomeFirstResponder];
    
}

- (IBAction)cloudButton:(id)sender {
    [inputCloudField setHidden:NO];
    [inputCloudField becomeFirstResponder];
}

- (IBAction)settingsButton:(id)sender {
    [self.view bringSubviewToFront:loginView];
}


//Text Field Handler ===============================================
- (BOOL)textFieldShouldReturn:(UITextField *) textField
{   
    
    // Text field from Login screen 
    if (textField == joinTextField) 
        [self joinNomadsButton:(id)self];
    
    //---------------------------------------------------
    
    // Text field from Discuss Button 
    if (textField == inputDiscussField) {
        CLog(@"Entered sendDiscuss");
        
        //AppID
        Byte myAppID = WEB_CHAT;
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
        
        [appDelegate->appSand sendWithGrainElts_AppID:myAppID 
                                              Command:myCommand 
                                             DataType:myDataType 
                                              DataLen:[inputDiscussField.text length] 
                                               String:inputDiscussField.text];
        
        inputDiscussField.text = @"";
        [inputDiscussField setHidden:YES];
        [inputDiscussField resignFirstResponder];
    }
    
    //---------------------------------------------------
    
    // Text field from Cloud Button 
    if (textField == inputCloudField) {
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
        
        [appDelegate->appSand sendWithGrainElts_AppID:myAppID 
                                              Command:myCommand 
                                             DataType:myDataType 
                                              DataLen:[inputCloudField.text length] 
                                               String:inputCloudField.text];
        
        inputCloudField.text = @"";
        [inputCloudField setHidden:YES];
        [inputCloudField resignFirstResponder];
    }
    
    //---------------------------------------------------
    
    [textField resignFirstResponder];
    
    return YES;   
}
//iOS Stuff ==============================================================

- (void)viewDidUnload
{
    [self setPromptLabel:nil];
    promptLabel = nil;
    [self setConnectionLabel:nil];
    connectionLabel = nil;
    [self setJoinNomadsButton:nil];
    [self setJoinTextField:nil];
    [self setLoginView:nil];
    [self setAukView:nil];
    [self setAukToolbar:nil];
    [self setAukBarButtonDiscuss:nil];
    [self setAukBarButtonCloud:nil];
    [self setAukBarButtonSettings:nil];
    [self setJoinNomadsButton:nil];
    [self setLeaveNomadsButton:nil];
    [self setInputDiscussField:nil];
    [self setInputCloudField:nil];
    [self setLoginNavBar:nil];
    [self setLoginNavTitle:nil];
    [self setLoginNavBackButton:nil];
    [self setLoginNavBackButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end