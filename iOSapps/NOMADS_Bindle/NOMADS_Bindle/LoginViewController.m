//
//  LoginViewController.m
//  DiscussCloudPoll
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

@implementation LoginViewController
@synthesize loginTextField;
@synthesize connectStatusLabel;
@synthesize welcomeMessage;
@synthesize welcomeMessage2;

@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize loginButton;
@synthesize disconnectButton;

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


        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    connectStatusLabel.text = @"";
    welcomeMessage.text = @"";
    welcomeMessage2.text = @"";
    
    [disconnectButton setHidden:YES];
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
    
    
    if ([loginTextField.text length] > 0){
        [appDelegate->appSand connect];

        appDelegate->loginStatus = 1;
        // [appDelegate tabBarItemsEnabled:YES];
        

        [appDelegate->appSand sendWithGrainElts_AppID:BINDLE
                                              Command:LOGIN 
                                             DataType:CHAR 
                                              DataLen:[loginTextField.text length] 
                                               String:loginTextField.text];

        appDelegate->userName = [loginTextField.text stringByAppendingString:@": "];
        
        loginTextField.text = @"";

        [loginTextField setHidden:YES];
        [loginButton setHidden:YES];
        [disconnectButton setHidden:NO];
        
        connectStatusLabel.text = @"Welcome to NOMADS!";
        welcomeMessage.text = @"Begin by tapping one";
        welcomeMessage2.text = @"of the icons below";

        [appDelegate->tabBarController setSelectedIndex:0];
        
    }
    //If there's no text, connect with a "space" for now 
    //We want to revise this to generate a warning message to the user
    else {
        connectStatusLabel.text = @"Error connecting: Please enter username.";
        welcomeMessage.text = @"";
        welcomeMessage2.text = @"";

    }
    
    
    
    
}

- (IBAction)disconnectButton:(id)sender {
    connectStatusLabel.text = @"Leaving NOMADS (but not really)";
    welcomeMessage.text = @"";
    welcomeMessage2.text = @"";
    [disconnectButton setHidden:YES];
    [loginTextField setHidden:NO];
    [loginButton setHidden:NO];
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
