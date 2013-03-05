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
@synthesize groupIDLabel;
@synthesize portField;

@synthesize connectionTimeoutTimer;


// Timer infrastructure to help with invalid connection entries

// Call this when you initiate the connection
- (void)startConnectionTimeoutTimer
{
    // [self stopConnectionTimeoutTimer]; // Or make sure any existing timer is stopped before this method is called
    
    NSTimeInterval interval = 3.0; // Measured in seconds, is a double
    
    LLog(@"Starting connection timer");
    connectionTimeoutTimer = [NSTimer scheduledTimerWithTimeInterval:interval
                                                              target:self
                                                            selector:@selector(handleConnectionTimeout)
                                                            userInfo:nil
                                                             repeats:NO];
}

// Call this when you successfully connect
- (void)stopConnectionTimeoutTimer
{
    LLog(@"Stopping connection timer");

    if (connectionTimeoutTimer)
    {
        
        [connectionTimeoutTimer invalidate];
        connectionTimeoutTimer = nil;
    }
}

- (void)handleConnectionTimeout
{
    
//    sandErrorFlag = YES;
    LLog(@"Connection timed out");
    
//    for (int x=0;x<numDelegates;x++) {
//        if (self->delegate[x] != nil) {
//            SEL methodName = @selector(networkConnectionError:);
//            
//            if ([self->delegate[x] respondsToSelector:methodName]) {
//                [self->delegate[x] networkConnectionError:@"SAND Network Error"];
//            }
//        }
//    }
//	[streamIn close];
//	[streamOut close];
}

- (void)dealloc
{
    [self stopConnectionTimeoutTimer];
}


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
        // if (![appDelegate internetConnectionStatus]) {
        //     CLog("No internet connection");
        // }

        // PASTED INTO BindleAppDelegate.m
        // end PASTE
        
    }
    return self;
}


- (void) reinit {
    
    connectStatusLabel.text = @"Bindle";
    userNameLabel.text = @"";
    welcomeMessage.text = @"";
    welcomeMessage2.text = @"";
    [disconnectButton setHidden:YES];
    [loginTextField setHidden:NO];
    [loginButton setHidden:NO];
    [pleaseEnterYourNameLabel setHidden:NO];
    [userNameIsLabel setHidden:YES];
    [groupIDLabel setHidden:NO];
    [portField setHidden:NO];

    [appDelegate tabBarItemsEnabled:NO];
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    connectStatusLabel.text = @"Bindle";
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

    if ([loginTextField.text length] <= 1){
        UIAlertView *errorView;
        //Create error view (pop    up window) for error meCLogage
        errorView = [[UIAlertView alloc]
                     initWithTitle: NSLocalizedString(@"Invalid User Name", @"Network error")
                     message: NSLocalizedString(@"Unable to connect to NOMADS server.", @"Network error")
                     delegate: self
                     cancelButtonTitle: NSLocalizedString(@"Enter User Name", @"Network error") otherButtonTitles: nil];
        
        [errorView show];
        return;
    }

    int port = [[portField text] intValue];
    
    if ((port < 51000) ||
        ([portField.text length] != 5) ||
        (port > 54000)
        ) {
        UIAlertView *errorView;
        //Create error view (pop    up window) for error meCLogage
        errorView = [[UIAlertView alloc]
                     initWithTitle: NSLocalizedString(@"Invalid Group ID", @"Network error")
                     message: NSLocalizedString(@"Unable to connect to NOMADS server.", @"Network error")
                     delegate: self
                     cancelButtonTitle: NSLocalizedString(@"Enter Group ID", @"Network error") otherButtonTitles: nil];
        
        [errorView show];
        return;
    }

    
    if ([loginTextField.text length] > 1){
       
        [appDelegate->appSand setPort:port];
        
        [appDelegate->appSand connect];

        appDelegate->loginStatus = 1;
        // [appDelegate tabBarItemsEnabled:YES];


        // [self startConnectionTimeoutTimer];
        
        
        [appDelegate->appSand sendWithGrainElts_AppID:BINDLE
                                                  Command:LOGIN
                                                 DataType:CHAR
                                                  DataLen:[loginTextField.text length]
                                                            String:loginTextField.text];
        // [self stopConnectionTimeoutTimer];
        
        tString = loginTextField.text;
        
        appDelegate->userName = [loginTextField.text stringByAppendingString:@": "];
        
        loginTextField.text = @"";
        
        if (!appDelegate->appSand->sandErrorFlag) {
        
            [loginTextField setHidden:YES];
            [loginButton setHidden:YES];
            [disconnectButton setHidden:NO];
            [pleaseEnterYourNameLabel setHidden:YES];
            [userNameIsLabel setHidden:NO];
            [groupIDLabel setHidden:YES];
            [portField setHidden:YES];
            
            connectStatusLabel.text = @"Bindle";
            userNameLabel.text = tString;
            // welcomeMeCLogage.text = @"Start NOMADS interaction by";
            welcomeMessage2.text = @"Select from the active icons below";
            
            [appDelegate->tabBarController setSelectedIndex:0];
        }
    }
    //If there's no text, connect with a "space" for now 
    //We want to revise this to generate a warning meCLogage to the user
    else {
        // connectStatusLabel.text = @"WARNING:  invalid name";
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
    connectStatusLabel.text = @"Bindle";
    userNameLabel.text = @"";
    welcomeMessage.text = @"";
    welcomeMessage2.text = @"";
    [disconnectButton setHidden:YES];
    [loginTextField setHidden:NO];
    [loginButton setHidden:NO];
    [pleaseEnterYourNameLabel setHidden:NO];
    [userNameIsLabel setHidden:YES];
    [groupIDLabel setHidden:NO];
    [portField setHidden:NO];

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
