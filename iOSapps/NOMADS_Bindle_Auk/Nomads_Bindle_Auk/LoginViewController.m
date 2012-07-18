//
//  LoginViewController.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/21/12.
//
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "LoginViewController.h"
#import "DiscussViewController.h"
#import "CloudViewController.h"
#import "PollViewController.h"

@implementation LoginViewController
@synthesize loginTextField;
@synthesize connectStatusLabel;
@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize loginButton;
@synthesize disconnectButton;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        UITabBarItem *tbi = [self tabBarItem];
        [tbi setTitle:@"Join NOMADS"];
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
    connectStatusLabel.text = @"";
    [disconnectButton setHidden:YES];
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    
}


- (IBAction)loginButton:(id)sender {
    [loginTextField resignFirstResponder];
    [appDelegate->appSand connect];
    
    if ([loginTextField.text length] > 0){
        [appDelegate->appSand sendWithGrainElts_AppID:LOGIN 
                                              Command:SEND_MESSAGE 
                                             DataType:BYTE 
                                              DataLen:[loginTextField.text length] 
                                               String:loginTextField.text];
        loginTextField.text = @"";
        [loginTextField setHidden:YES];
        [loginButton setHidden:YES];
        [disconnectButton setHidden:NO];
        [appDelegate tabBarItemsEnabled:YES];
        [appDelegate->tabBarController setSelectedIndex:1]; //Switch the tab viewer to Discuss
        
        connectStatusLabel.text = @"Connected to NOMADS!";
    }
    //If there's no text, connect with a "space" for now 
    //We want to revise this to generate a warning message to the user
    else {
        connectStatusLabel.text = @"Error connecting: Please enter username!";
    }
    
    
    
    
}

- (IBAction)disconnectButton:(id)sender {
    connectStatusLabel.text = @"Leaving NOMADS (but not really)";
    [disconnectButton setHidden:YES];
    [loginTextField setHidden:NO];
    [loginButton setHidden:NO];
    [appDelegate tabBarItemsEnabled:NO];
    
}
- (void)dataReadyHandle:(NGrain *)inGrain
{   
    CLog(@"LVC: DataReadyHandle");
    if (nil != inGrain) { 
        if(inGrain->appID == SERVER && inGrain->command == LOGIN_STATUS) {
            CLog(@"Login is ready for LOGIN STATUS data");
            CLog(@"appID = %d", inGrain->appID);
            CLog(@"command = %d", inGrain->command);
            CLog(@"DataType = %d", inGrain->dataType);
            CLog(@"DataLen = %d", inGrain->dataLen);
            CLog(@"Data = %d", (int)inGrain->str);
        }  
        //To be implemented
        
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
