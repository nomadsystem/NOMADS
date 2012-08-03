//
//  AukViewController.m
//  NOMADS_Bindle_Auk

//  Created by Steven Kemper on 7/18/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Reachability.h"
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

@synthesize infoViewNOMADS;
@synthesize settingsNavBackButton;
@synthesize settingsNavBarMoreInfoButton;
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
    connectionLabel.text = @"Not Connected!";
    [joinTextField setHidden:YES];
    
    //Initialize backgrounds 
    [[self settingsView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
    [[self aukView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"blue_ice_bg_320_480.png"]]];
    [[self aukToolbar] setTranslucent:YES];
    [aukBarButtonDiscuss setEnabled:false];
    [aukBarButtonCloud setEnabled:false];
    [[self swarmView] setBackgroundColor:[UIColor whiteColor]];
    
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
    noteIsEnabled = NO;
    
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
    
    [self.view bringSubviewToFront:settingsView];

    
    if ([self internetConnectionStatus]) {
        
        //Initialize toolbar in Auk view
        
        [appDelegate->appSand connect];  
        connectionLabel.text = @"Connected to NOMADS!";
        
        Byte c[1];
        c[0] = 1;
        
        //****STK 7/25/12 Need to fix NSand to send UINT8 from iOS
        [appDelegate->appSand sendWithGrainElts_AppID:OPERA_CLIENT  
                                              Command:REGISTER 
                                             DataType:UINT8 
                                              DataLen:1 
                                                Uint8:c];
        
    }
    else {
        CLog("No internet connection");
        [joinNomadsButton setHidden:NO];
        [leaveNomadsButton setHidden:YES];
        [self.view bringSubviewToFront:settingsView];
    }
    // Do any additional setup after loading the view from its nib.
}

-(BOOL)internetConnectionStatus {
    //  Reachability *reachability = [Reachability reachabilityForInternetConnection];
    Reachability *reachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus internetStatus = [reachability currentReachabilityStatus];
    
    if(internetStatus == NotReachable) {
        CLog("internet status == NotReachable");
        UIAlertView *errorView;
        
        errorView = [[UIAlertView alloc]
                     initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                     message: NSLocalizedString(@"No internet connection found, this application requires an internet connection.", @"Network error")
                     delegate: self
                     cancelButtonTitle: NSLocalizedString(@"Close", @"Network error") otherButtonTitles: nil];
        
        [errorView show];
        return NO;

    }
    else {
        return YES;
    }
    
}

// input data function ============================================
- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"AVC: Data Ready Handle:  appID = %d  command = %d\n",inGrain->appID, inGrain->command);
    if (nil != inGrain) {
        if(inGrain->appID == CONDUCTOR_PANEL) {
            
            if(inGrain->command == SET_DISCUSS_STATUS) {
                CLog("SET_DISCUSS_STATUS:  %d\n",(int)inGrain->bArray[0]);
                [aukBarButtonDiscuss setEnabled:(Boolean)inGrain->bArray[0]];
            }
            else if(inGrain->command == SET_CLOUD_STATUS) {
                [aukBarButtonCloud setEnabled:(Boolean)inGrain->bArray[0]];
                CLog("SET_CLOUD_STATUS:  %d\n",(int)inGrain->bArray[0]);
                
            }
            
            else if(inGrain->command == SET_NOTE_STATUS) {
                if (inGrain->bArray[0] == 1) {
                    noteIsEnabled = YES;
                }
                else if (inGrain->bArray[0] == 0) {
                    noteIsEnabled = NO;
                    fileNum = 1;
                }   
                
                //  CLog(@"AVC: Data Ready Handle\n");
            }
            
            else if (inGrain->command == SET_NOTE_VOLUME) {
                //   float noteVolume = (((inGrain->iArray[0])*0.01);//****STK data to be scaled from 0-1
                noteVolume =  (pow(10,(double)(inGrain->iArray[0])/20)/100000);
                audioPlayer.volume = noteVolume;
            }
        }
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

- (IBAction)settingsNavMoreInfoButton:(id)sender {
    NSString *infoURL = @"http://nomads.music.virginia.edu";
    NSURL *url = [NSURL URLWithString:infoURL];
    NSURLRequest *myLoadRequest = [NSURLRequest requestWithURL:url];
    
   
    [self.view bringSubviewToFront:infoViewNOMADS];
    [self.infoViewNOMADS loadRequest:myLoadRequest];
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

-(void)playNote {
    
    NSString *soundFile;
    
    soundFile = [NSString stringWithFormat:@"sounds/AukNotes/AuksalaqNomadsNote%d.mp3",fileNum];
    
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
            audioPlayer.volume = noteVolume;
            CLog("dropletVolume = %f", noteVolume);
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

-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    CLog("AVC: Audio finished playing");
    if (fileNum == 17) {
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
            
            //Note playback
            if (noteIsEnabled) { //Only play back if note is enabled
                if (![audioPlayer isPlaying]) {
                    [self playNote];
                }
                else {
                    CLog("AVC: Note is already playing");
                }
            }
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
    [self setInfoViewNOMADS:nil];
    settingsNavMoreInfoButton = nil;
    [self setSettingsNavBarMoreInfoButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
