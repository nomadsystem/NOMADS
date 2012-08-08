//
//  AukViewController.h
//  NOMADS_Bindle_Auk
//
//  Created by Steven Kemper on 7/18/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef AUK_VIEWCONTROLLER
#define AUK_VIEWCONTROLLER

#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"
#import "SwarmDrawView.h"


@interface AukViewController : UIViewController <SandDelegate, AVAudioPlayerDelegate, UIWebViewDelegate, UIAlertViewDelegate>
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
        
    __weak UILabel *connectionLabel;
    UIView *settingsView;
    UIView *aukView;
    UIView *swarmView;
    UIWebView *infoViewNOMADS;
    __weak UITextField *inputDiscussField;
    __weak UITextField *inputCloudField;
    __weak UIBarButtonItem *settingsNavBackButton;
    UIBarButtonItem *settingsNavMoreInfoButton;
    __weak UINavigationItem *settingsNavTitle;
    __weak UINavigationBar *settingsNavBar;
    __weak UIToolbar *aukToolbar;
    AVAudioPlayer *audioPlayer;
    AVAudioSession *session;
    int fileNum;
    float cloudSoundVolume;
    Boolean cloudSoundIsEnabled;
    int currentView;
    SwarmDrawView *mySwarmDrawView;
    
}

@property (retain, nonatomic) IBOutlet UIWebView *infoViewNOMADS;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *settingsNavBackButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *settingsNavBarMoreInfoButton;
@property (weak, nonatomic) IBOutlet UINavigationItem *settingsNavTitle;
@property (weak, nonatomic) IBOutlet UINavigationBar *settingsNavBar;

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;

@property (weak, nonatomic) IBOutlet UIButton *joinNomadsButton;
@property (weak, nonatomic) IBOutlet UIButton *leaveNomadsButton;
@property (strong, nonatomic) IBOutlet UIView *settingsView;
@property (strong, nonatomic) IBOutlet UIView *aukView;
@property (strong, nonatomic) IBOutlet SwarmDrawView *mySwarmDrawView;
@property (weak, nonatomic) IBOutlet UILabel *connectionLabel;
@property (weak, nonatomic) IBOutlet UITextField *joinTextField;
@property (weak, nonatomic) IBOutlet UIToolbar *aukToolbar;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *aukBarButtonDiscuss;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *aukBarButtonCloud;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *aukBarButtonSettings;
@property (weak, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (weak, nonatomic) IBOutlet UITextField *inputCloudField;
@property (strong, nonatomic) IBOutlet UIView *swarmView;


- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand

- (IBAction)discussButton:(id)sender;
- (IBAction)cloudButton:(id)sender;
- (IBAction)settingsButton:(id)sender;
- (IBAction)leaveNomadsButton:(id)sender;
- (IBAction)joinNomadsButton:(id)sender;
- (IBAction)settingsNavBackButton:(id)sender;
- (IBAction)settingsNavMoreInfoButton:(id)sender;

- (IBAction)backgroundTapDiscuss:(id)sender; //Tap to resign text entry
- (IBAction)backgroundTapCloud:(id)sender; //Tap to resign text entry

- (void)playCloudSound;

-(void)reachabilityChanged:(NSNotification*)note;

@end
#endif