//
//  AukViewController.m
//  NOMADS_Bindle_Auk

//  Created by Steven Kemper on 7/18/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Reachability.h"
#import "AukViewController.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "NAppIDAuk.h"
#import "NCommandAuk.h"

@implementation AukViewController

@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;

@synthesize infoViewNOMADS;
@synthesize settingsNavBackButton;
@synthesize settingsNavBarMoreInfoButton;
@synthesize joinNomadsButton;
@synthesize moreInfoButton;
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
@synthesize mySwarmDrawView;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self.view bringSubviewToFront:aukView]; //Load the aukView
        currentView = 0; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    //Handles first check of internet communcation status
    if (![self internetConnectionStatus]) {
            CLog("No internet connection");
    }
    
    
    //Init handling of network communications errors
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reachabilityChanged:)
                                                 name:kReachabilityChangedNotification
                                               object:nil];
    
    Reachability * reach = [Reachability reachabilityForInternetConnection];
    reach.reachableBlock = ^(Reachability * reachability)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            CLog("Block Says Reachable");
        });
    };
    reach.unreachableBlock = ^(Reachability * reachability)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            CLog("Block Says UN-Reachable");
        });
    };
    [reach startNotifier];
    //--END init handle network communcation errors

    appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate]; //Sets as delegate to BindleAppDelegate
    
    [appDelegate->appSand setDelegate:self]; // SAND:  set a pointer inside appSand so we get notified when network data is available
    
    
    //Hides our "hidden" text fields for discuss and cloud
    inputDiscussField.hidden = YES; 
    inputCloudField.hidden = YES;
    
    //Init settingsScreen
    [joinNomadsButton setHidden:YES];
    [moreInfoButton setHidden:NO];
    [joinTextField setHidden:YES];
    //Init connection label
    [connectionLabel setFont:[UIFont fontWithName:@"Helvetica-Bold" size:20]];
    [connectionLabel setTextColor:[UIColor whiteColor]];
    //Inits settingsNavBar
    settingsNavBar.hidden = NO;
    
    //Init backgrounds
    if ( UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad )
    {
        [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_768_1024_IpadPortrait.png"]]];
        [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_768_1024_IpadPortrait.png"]]];
    }
    else {
        [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
        [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
    }

    
    
    
    //Init aukToolbar and buttons
    [[self aukToolbar] setTranslucent:YES];
    [aukBarButtonDiscuss setEnabled:false];
    [aukBarButtonCloud setEnabled:false];
    
    
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
    fileNum = 1; //Selects AukNote file number
    cloudSoundIsEnabled = NO;
    cloudSoundVolume = 1.0;
    
    //Init for size of SwarmDrawView
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
    
    //Init SwarmDrawView
    mySwarmDrawView = [[SwarmDrawView alloc]initWithFrame:myCGRect];
    if ( UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad )
    {
        [mySwarmDrawView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_768_1024_IpadPortrait.png"]]];
    }
    else {
        [mySwarmDrawView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
    }
    
    //Add our instance of SwarmDrawView to swarmView
    [swarmView addSubview:mySwarmDrawView];
    //Set auto-resize parameters for SwarmDrawView
    [mySwarmDrawView setAutoresizingMask:UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight];
        
    //Connect to NOMADS via SAND
    [appDelegate->appSand connect];
    
    connectionLabel.text = @"Connected to NOMADS!";
    
    //Send REGISTER command 
    Byte c[1];
    c[0] = 1;
    [appDelegate->appSand sendWithGrainElts_AppID:OPERA_CLIENT  
                                          Command:REGISTER 
                                         DataType:UINT8 
                                          DataLen:1 
                                            Uint8:c];
}

// BEGIN Network Error Handling ==============================================

//Method to determine internet reachability (general network connections)
//Only called once on init
-(BOOL)internetConnectionStatus {
    Reachability *reachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus internetStatus = [reachability currentReachabilityStatus];
    
    //If network is NOT reachable
    if(internetStatus == NotReachable) {
        CLog("internet status == NotReachable");
        UIAlertView *errorView;
        
        errorView = [[UIAlertView alloc]
                     initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                     message: NSLocalizedString(@"No internet connection found, this application requires an internet connection.", @"Network error")
                     delegate: self
                     cancelButtonTitle: NSLocalizedString(@"Close", @"Network error") otherButtonTitles: nil];
        
        [errorView show];
        //Calls alertView didDismissWithButtonIndex: on button press
        return NO;
    }
    else {
        return YES;
    }
    
}

//Method to determine change in network reachability (general network connections)
-(void)reachabilityChanged:(NSNotification*)note
{
    Reachability * reach = [note object];
    
    
    if([reach isReachable]) //If network is reachable
    {
        CLog(@"Notification Says Reachable"); 
    }
    else //if Network is unreachable
    {
        CLog(@"Notification Says UnReachable");
        UIAlertView *errorView;
        //Create error view (pop up window) for error message
        errorView = [[UIAlertView alloc]
                     initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                     message: NSLocalizedString(@"No internet connection found, this application requires an internet connection.", @"Network error")
                     delegate: self
                     cancelButtonTitle: NSLocalizedString(@"Close", @"Network error") otherButtonTitles: nil];
        
        [errorView show];
        //Calls alertView didDismissWithButtonIndex: on button press
    }
}

//Method to handle networkConnectionError from NSand (delegate of NSand)
- (void)networkConnectionError:(NSString *)ErrStr
{
    CLog("internet status == NotReachable");
    UIAlertView *errorView;
    
    errorView = [[UIAlertView alloc]
                 initWithTitle: NSLocalizedString(ErrStr,ErrStr)
                 message: NSLocalizedString(ErrStr,ErrStr)
                 delegate: self
                 cancelButtonTitle: NSLocalizedString(@"Close", @"SAND Network error") otherButtonTitles: nil];
    
    [errorView show];
}

//Method handles what to do when network errors are dismissed with the button
-(void) alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    //u need to change 0 to other value(,1,2,3) if u have more buttons.then u can check which button was pressed.
    if (buttonIndex == 0) {
        CLog("Alert button %i pressed", buttonIndex);
        [moreInfoButton setHidden:YES]; //Hide the Leave button
        [joinNomadsButton setHidden:NO]; //Show the Join Button
        connectionLabel.text = @"Not Connected!";
        [self.view bringSubviewToFront:settingsView];
        currentView = 1; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
    }
}
// END Network Error Handling ==============================================



// input data function ============================================
- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"AVC: Data Ready Handle:  appID = %d  command = %d\n",inGrain->appID, inGrain->command);
    if (nil != inGrain) { //if we're getting a grain
        if(inGrain->appID == CONDUCTOR_PANEL) {
            
            if(inGrain->command == SET_DISCUSS_STATUS) {
                CLog("SET_DISCUSS_STATUS:  %d\n",(int)inGrain->bArray[0]);
                [aukBarButtonDiscuss setEnabled:(Boolean)inGrain->bArray[0]];
            }
            else if(inGrain->command == SET_CLOUD_STATUS) {
                [aukBarButtonCloud setEnabled:(Boolean)inGrain->bArray[0]];
                CLog("SET_CLOUD_STATUS:  %d\n",(int)inGrain->bArray[0]);
                
            }
            
            else if(inGrain->command == SET_CLOUD_SOUND_STATUS) {
                if (inGrain->bArray[0] == 1) {
                    cloudSoundIsEnabled = YES;
                     CLog("SET_CLOUD_SOUND_STATUS:  %d\n",(int)inGrain->bArray[0]);
                }
                else if (inGrain->bArray[0] == 0) {
                    cloudSoundIsEnabled = NO;
                    CLog("SET_CLOUD_SOUND_STATUS:  %d\n",(int)inGrain->bArray[0]);
                    fileNum = 1;
                }   
                
                //  CLog(@"AVC: Data Ready Handle\n");
            }
            
            else if (inGrain->command == SET_CLOUD_SOUND_VOLUME) {
                //   float noteVolume = (((inGrain->iArray[0])*0.01);//****STK data to be scaled from 0-1
                cloudSoundVolume =  (pow(10,(double)(inGrain->iArray[0])/20)/100000);
                CLog("SET_CLOUD_SOUND_VOLUME:  %d\n",(int)inGrain->iArray[0]);
                audioPlayer.volume = cloudSoundVolume;
            }
        }
    }
}
// Login button, currently disabled ****STK 7/30/12 ===============================================

- (IBAction)joinNomadsButton:(id)sender {
//    [joinTextField resignFirstResponder];
    [appDelegate->appSand connect];  
    
    Byte c[1];
    c[0] = 1;
    //****STK 7/25/12 Need to fix NSand to send UINT8 from iOS
    [appDelegate->appSand sendWithGrainElts_AppID:OPERA_CLIENT  
                                                   Command:REGISTER 
                                                  DataType:UINT8 
                                                   DataLen:1 
                                                    Uint8:c];
    
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
        [joinNomadsButton setHidden:YES];
        [moreInfoButton setHidden:NO];
        
        connectionLabel.text = @"Connected to NOMADS!";
//        
        [self.view bringSubviewToFront:aukView];
//        
//        settingsNavBar.hidden = NO;
//    }
//    //If there's no text, connect with a "space" for now 
//    //We want to revise this to generate a warning message to the user
//    else {
//        connectionLabel.text = @"Error connecting: Please enter username!";
//    }
}

// Back button in Settings display
- (IBAction)settingsNavBackButton:(id)sender {
    //If we're in settings view and need to go back to aukView
    if (currentView == 1) {
        [self.view bringSubviewToFront:aukView];
        currentView = 0; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
    }
    //If we're in infoView and need to go back to settings view
    else if (currentView == 2) {
        [self.view bringSubviewToFront:settingsView];
        currentView = 1; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
        [settingsNavBarMoreInfoButton setEnabled:YES];
    }
}

// Takes us to infoView (web page about NOMADS)
- (IBAction)settingsNavMoreInfoButton:(id)sender {
    [settingsNavBarMoreInfoButton setEnabled:NO];
    NSString *infoURL = @"http://nomads.music.virginia.edu";
    NSURL *url = [NSURL URLWithString:infoURL];
    NSURLRequest *myLoadRequest = [NSURLRequest requestWithURL:url];
    
    [self.view bringSubviewToFront:infoViewNOMADS];
  //  [infoViewNOMADS setUserInteractionEnabled:NO];
    currentView = 2; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
    
    [self.infoViewNOMADS loadRequest:myLoadRequest];
}

- (IBAction)moreInfoButton:(id)sender {
    [settingsNavBarMoreInfoButton setEnabled:NO];
    NSString *infoURL = @"http://nomads.music.virginia.edu";
    NSURL *url = [NSURL URLWithString:infoURL];
    NSURLRequest *myLoadRequest = [NSURLRequest requestWithURL:url];
    
    [self.view bringSubviewToFront:infoViewNOMADS];
    //  [infoViewNOMADS setUserInteractionEnabled:NO];
    currentView = 2; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
    
    [self.infoViewNOMADS loadRequest:myLoadRequest];
}

// Auk view items ===============================================

//Button for discuss entry
- (IBAction)discussButton:(id)sender {
    [aukView bringSubviewToFront:inputDiscussField];
    [inputDiscussField setHidden:NO];
    [inputDiscussField becomeFirstResponder];
    
}
//Button for cloud entry
- (IBAction)cloudButton:(id)sender {
    [aukView bringSubviewToFront:inputCloudField];
    [inputCloudField setHidden:NO];
    [inputCloudField becomeFirstResponder];
}

//Settings button
- (IBAction)settingsButton:(id)sender {
    [self.view bringSubviewToFront:settingsView];
    currentView = 1; //0=aukView, 1=settingsView, 2=infoView (UIWebView)
    
}

//***STK 8/7/12 NOT IMPLEMENTED
-(IBAction) backgroundTapDiscuss:(id)sender{
    [self.inputDiscussField resignFirstResponder];
}
//***STK 8/7/12 NOT IMPLEMENTED
-(IBAction) backgroundTapCloud:(id)sender{
    [self.inputCloudField resignFirstResponder];
}

//Method to play CloudSound (from Cloud entry at end of Opera)
-(void)playCloudSound {
    
    NSString *soundFile;
    
    soundFile = [NSString stringWithFormat:@"sounds/AuksalaqThoughtCloudSounds/AuksalaqThoughtCloud%d.mp3",fileNum];
    
    NSURL *url = [NSURL fileURLWithPath:[NSString stringWithFormat:@"%@/%@", [[NSBundle mainBundle] resourcePath], soundFile]];
    
    CLog("URL: %@", url);
    
    NSError *error;
    if (audioPlayer == nil) {
        audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];
        [audioPlayer setDelegate:self];
        audioPlayer.numberOfLoops = 0;
        if (audioPlayer == nil) {
            CLog("Playback error: %@",[error description]);
        }
        else {
            audioPlayer.volume = cloudSoundVolume;
            CLog("noteVolume = %f", cloudSoundVolume);
            [audioPlayer play];
        }
    }
    
    //****STK Other useful control parameters 
    //    audioPlayer.volume = 0.5; // 0.0 - no volume; 1.0 full volume
    //    Clog(@"%f seconds played so far", audioPlayer.currentTime);
    //    audioPlayer.currentTime = 10; // jump to the 10 second mark
    //    [audioPlayer pause]; 
    //    [audioPlayer stop]; // Does not reset currentTime; sending play resumes
}

//Method to advance filenumber after playback of AukNote
-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    CLog("AVC: Audio finished playing");
    if (fileNum == 18) {
        fileNum = 0;
    }
    else {
        fileNum++;
    }
    audioPlayer = nil;
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
        else { //Dismisses keyboard if no text is entered but send button is pressed 
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
            
            //Note playback
            if (cloudSoundIsEnabled) { //Only play back if note is enabled
                if (![audioPlayer isPlaying]) {
                    [self playCloudSound];
                }
                else {
                    CLog("AVC: cloudSound is already playing");
                }
            }
        }
        else { //Dismisses keyboard if no text is entered but send button is pressed
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

//Method to determine current rotation status
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)x
{
    return (x == UIInterfaceOrientationPortrait) || UIInterfaceOrientationIsLandscape(x);
}

//Method to animate transitions between different rotation states
- (void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    if (toInterfaceOrientation == UIInterfaceOrientationLandscapeLeft ||
        toInterfaceOrientation == UIInterfaceOrientationLandscapeRight)
    {
        if ( UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad )
        {
            //Load images for landscape view
            [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_1024_768_IpadLandscape.png"]]];
            [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_1024_768_IpadLandscape.png"]]];
            
            //Reset screen size of SwarmDrawView for landscape view
            CGRect screenRect = [mySwarmDrawView bounds];
            [mySwarmDrawView setFrame:screenRect];
            [mySwarmDrawView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_1024_768_IpadLandscape.png"]]];
        }
        else {
            
            //Load images for landscape view
            [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_480_320.png"]]];
            [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_480_320.png"]]];
            
            //Reset screen size of SwarmDrawView for landscape view
            CGRect screenRect = [mySwarmDrawView bounds];
            [mySwarmDrawView setFrame:screenRect];
            [mySwarmDrawView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_480_320.png"]]];
        }
        
    }
    else
    {
        if ( UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad )
        {
            //Load images for portrait view
            [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_768_1024_IpadPortrait.png"]]];
            [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_768_1024_IpadPortrait.png"]]];
            
            //Reset screen size of SwarmDrawView for portrait view
            CGRect screenRect = [mySwarmDrawView bounds];
            [mySwarmDrawView setFrame:screenRect];
            [mySwarmDrawView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_768_1024_IpadPortrait.png"]]];
        }
        else {
            //Load images for portrait view
            [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
            [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
            
            //Reset screen size of SwarmDrawView for portrait view
            CGRect screenRect = [mySwarmDrawView bounds];
            [mySwarmDrawView setFrame:screenRect];
            [mySwarmDrawView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
        }
    }
}

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
    [self setMoreInfoButton:nil];
    [self setInputDiscussField:nil];
    [self setInputCloudField:nil];
    [self setSettingsNavBar:nil];
    [self setSettingsNavTitle:nil];
    [self setSettingsNavBackButton:nil];
    [self setSwarmView:nil];
    [self setInfoViewNOMADS:nil];
    settingsNavMoreInfoButton = nil;
    [self setSettingsNavBarMoreInfoButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}





@end
