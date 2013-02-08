//
//  LoginViewController.m
//  DiscuCLogCloudPoll
//
//  Created by Steven Kemper on 5/21/12.
//
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "NAppIDMain.h"
#import "NCommandMain.h"
#import "LoginViewController.h"
#import "DiscussViewController.h"
#import "CloudViewController.h"
#import "PollViewController.h"
#import "BindleAppDelegate.h"
#import "Reachability.h"

@implementation LoginViewController

@synthesize loginTextField;
@synthesize connectStatusLabel;
@synthesize userNameLabel;
@synthesize welcomeMessage;
@synthesize welcomeMessage2;

@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize loginButton;
@synthesize disconnectButton;
@synthesize moreInfoButton;
@synthesize pleaseEnterYourNameLabel;
@synthesize userNameIsLabel;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Login" image:[UIImage imageNamed:@"Login_30x30.png"] tag:0];
        tbi = [self tabBarItem];
        // [tbi setTitle:@"Login"];
        
        // UIImage *i0 = [UIImage imageNamed:@"tLogin.png"];
        // [tbi setImage:i0];
        
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];
        
        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
        
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



        
    }
    return self;
}

// BEGIN Network Error Handling ==============================================

//Method to determine internet reachability (general network connections)
//Only called once on init
-(BOOL)internetConnectionStatus {
    @autoreleasepool {
        Reachability *reachability = [Reachability reachabilityForInternetConnection];
        NetworkStatus internetStatus = [reachability currentReachabilityStatus];
        
        //If network is NOT reachable
        if(internetStatus == NotReachable) {
            [appDelegate->tabBarController setSelectedIndex:0];
            [appDelegate tabBarItemsEnabled:false];
            

            CLog("internet status == NotReachable");
            
            
            UIAlertView *errorView;
            
            errorView = [[UIAlertView alloc]
                         initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                         message: NSLocalizedString(@"No internet connection found, this application requires an internet connection.", @"Network error")
                         delegate: self
                         cancelButtonTitle: NSLocalizedString(@"Close", @"Network error") otherButtonTitles: nil];
            
            [errorView show];
            
            //Calls alertView didDismiCLogWithButtonIndex: on button preCLog
            return NO;
        }
        else {
            return YES;
        }
    }
}

//Method to determine change in network reachability (general network connections)
-(void)reachabilityChanged:(NSNotification*)note
{
    @autoreleasepool {
        
        Reachability * reach = [note object];
        
        
        if([reach isReachable]) //If network is reachable
        {
            CLog(@"Notification Says Reachable");
        }
        else //if Network is unreachable
        {
            [appDelegate->tabBarController setSelectedIndex:0];
            [appDelegate tabBarItemsEnabled:false];

            CLog(@"Notification Says UnReachable");
            UIAlertView *errorView;
            //Create error view (pop up window) for error meCLogage
            errorView = [[UIAlertView alloc]
                         initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                         message: NSLocalizedString(@"No internet connection found, this application requires an internet connection.", @"Network error")
                         delegate: self
                         cancelButtonTitle: NSLocalizedString(@"Reconnect to NOMADS", @"Network error") otherButtonTitles: nil];
            
            [errorView show];
            //Calls alertView didDismiCLogWithButtonIndex: on button preCLog
        }
    }
}


//Method handles what to do when network errors are dismiCLoged with the button
-(void) alertView:(UIAlertView *)alertView didDismiCLogWithButtonIndex:(NSInteger)buttonIndex {
    //u need to change 0 to other value(,1,2,3) if u have more buttons.then u can check which button was preCLoged.
    if (buttonIndex == 0) {
        CLog("Alert button %i preCLoged", buttonIndex);
        
        
        // Do any additional setup after loading the view.
        connectStatusLabel.text = @"Welcome to NOMADS";
        userNameLabel.text = @"";
        welcomeMessage.text = @"";
        welcomeMessage2.text = @"";
        
        [disconnectButton setHidden:YES];
        [loginTextField setHidden:NO];
        [loginButton setHidden:NO];
        [userNameIsLabel setHidden:YES];

        
    }
}
// END Network Error Handling ==============================================



- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    connectStatusLabel.text = @"Welcome to NOMADS";
    userNameLabel.text = @"";
    welcomeMessage.text = @"";
    welcomeMessage2.text = @"";
    
    [disconnectButton setHidden:YES];
    [userNameIsLabel setHidden:YES];
    
    UIImage * targetImage = [UIImage imageNamed:@"SandDunes1_960x640.png"];
    
    // redraw the image to fit |yourView|'s size
    UIGraphicsBeginImageContextWithOptions([self view].frame.size, NO, 0.f);
    [targetImage drawInRect:CGRectMake(0.f, 0.f, [self view].frame.size.width, [self view] .frame.size.height)];
    UIImage * resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];
     
}


- (IBAction)loginButton:(id)sender {
    [loginTextField resignFirstResponder];
    
    NSString *tString;
    if ([loginTextField.text length] > 0){
        [appDelegate->appSand connect];

        appDelegate->loginStatus = 1;
        // [appDelegate tabBarItemsEnabled:YES];
        

        [appDelegate->appSand sendWithGrainElts_AppID:BINDLE
                                              Command:LOGIN 
                                             DataType:CHAR 
                                              DataLen:[loginTextField.text length] 
                                               String:loginTextField.text];

        tString = loginTextField.text;
        
        appDelegate->userName = [loginTextField.text stringByAppendingString:@": "];
        
        loginTextField.text = @"";

        [loginTextField setHidden:YES];
        [loginButton setHidden:YES];
        [disconnectButton setHidden:NO];
        [pleaseEnterYourNameLabel setHidden:YES];
        [userNameIsLabel setHidden:NO];
        
        connectStatusLabel.text = @"Welcome to NOMADS";
        userNameLabel.text = tString;
        // welcomeMeCLogage.text = @"Start NOMADS interaction by";
        welcomeMessage2.text = @"Select from the active icons below";

        [appDelegate->tabBarController setSelectedIndex:0];
        
    }
    //If there's no text, connect with a "space" for now 
    //We want to revise this to generate a warning meCLogage to the user
    else {
        connectStatusLabel.text = @"Error connecting: Please enter username.";
        userNameLabel.text = @"";

        welcomeMessage.text = @"";
        welcomeMessage2.text = @"";

    }
    
    
    
    
}

- (IBAction)moreInfoButton:(id)sender {
    NSURL *url = [ [ NSURL alloc ] initWithString: @"http://nomads.music.virginia.edu" ];
    [[UIApplication sharedApplication] openURL:url];
}

- (IBAction)disconnectButton:(id)sender {
    connectStatusLabel.text = @"Welcome to NOMADS";
    userNameLabel.text = @"";
    welcomeMessage.text = @"";
    welcomeMessage2.text = @"";
    [disconnectButton setHidden:YES];
    [loginTextField setHidden:NO];
    [loginButton setHidden:NO];
    [pleaseEnterYourNameLabel setHidden:NO];
    [userNameIsLabel setHidden:YES];
    
    [appDelegate tabBarItemsEnabled:NO];
    
}
- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"LVC: I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) { 
        
        //To be implemented
        CLog(@"LVC: DataReadyHandle");
    }
    
}


- (BOOL)textFieldShouldReturn:(UITextField *) textField
{   
    
    
    if (textField == loginTextField) 
        [self loginButton:(id)self];
    
    [textField resignFirstResponder];
    
    return YES;   
}

- (void)viewDidUnload
{
    [self setLoginTextField:nil];
    [self setConnectStatusLabel:nil];
    [self setDisconnectButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
