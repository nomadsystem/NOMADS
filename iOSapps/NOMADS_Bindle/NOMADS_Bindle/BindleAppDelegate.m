//
//  DiscussCloudAppDelegate.m
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import "BindleAppDelegate.h"
#import "LoginViewController.h"
#import "DiscussViewController.h"
#import "CloudViewController.h"
#import "PollViewController.h"
#import "SwarmViewController.h"
#import "NGlobals.h"
#import "NAppIDMain.h"
#import "NCommandMain.h"
#import "NGrain.h"
#import "NSand.h"
#import "Reachability.h"

@implementation BindleAppDelegate

@synthesize window = _window;
@synthesize appSand;
@synthesize userName;

LoginViewController *lvc;
bool prepareToDie;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    prepareToDie = false;
    
    // Override point for customization after application launch.
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationFade];
    appSand = [[NSand alloc] init]; 
    
    // SAND:  set a pointer inside appSand so we get notified when network data is available
    [appSand setDelegate:self];
    
    // SAND: Connect the network streams

    // [appSand connect];   // DT now done at login
    
    //lvc = [[LoginViewController alloc] init];
    // LoginViewController *lvc = [[LoginViewController alloc] init];
    //[[self window] setRootViewController:lvc];
    [self makeTabBar];

    
    
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

    //   self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    loginStatus = 0;
    return YES;
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
            [tabBarController setSelectedIndex:0];
            [self tabBarItemsEnabled:false];
            
            
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
            [tabBarController setSelectedIndex:0];
            [self tabBarItemsEnabled:false];
            
            CLog(@"Notification Says UnReachable");
            UIAlertView *errorView;
            //Create error view (pop up window) for error meCLogage
            errorView = [[UIAlertView alloc]
                         initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                         message: NSLocalizedString(@"No internet connection found, this application requires an internet connection.", @"Network error")
                         delegate: self
                         cancelButtonTitle: NSLocalizedString(@"Reconnect to NOMADS", @"Network error") otherButtonTitles: nil];
            
            [errorView show];
            [tabBarController setSelectedIndex:0];
            [lvc reinit];

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
//        connectStatusLabel.text = @"Welcome to NOMADS";
//        userNameLabel.text = @"";
//        welcomeMessage.text = @"";
//        welcomeMessage2.text = @"";
        
//    [disconnectButton setHidden:YES];
//        [loginTextField setHidden:NO];
//        [loginButton setHidden:NO];
//        [userNameIsLabel setHidden:YES];
        
        
    }
}
// END Network Error Handling ==============================================


//Method to handle networkConnectionError from NSand (delegate of NSand)
- (void)networkConnectionError:(NSString *)ErrStr
{

    UIAlertView *errorView;
    //Create error view (pop up window) for error meCLogage
    errorView = [[UIAlertView alloc]
                 initWithTitle: NSLocalizedString(@"Network error", @"Network error")
                 message: NSLocalizedString(@"Lost connection with NOMADS server.", @"Network error")
                 delegate: self
                 cancelButtonTitle: NSLocalizedString(@"Reconnect to NOMADS", @"Network error") otherButtonTitles: nil];
    
    [errorView show];

//    [errorView show]
     [tabBarController setSelectedIndex:0];
    [lvc reinit];
    // [self tabBarItemsEnabled:NO];
    
}


// input data function ===================================================================

- (void)dataReadyHandle:(NGrain *)inGrain;
{
    CLog(@"I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) {
        
        if (loginStatus == 1)  {
            if ((inGrain->appID == INSTRUCTOR_PANEL) || (inGrain->appID == SERVER)) {  // Control info from the IP
                if(inGrain->command == SET_DISCUSS_STATUS) {
                    [discussTBI setEnabled:inGrain->bArray[0]];
                }
                else if(inGrain->command == SET_CLOUD_STATUS) {
                    [cloudTBI setEnabled:inGrain->bArray[0]];
                }
                else if(inGrain->command == SET_POLL_STATUS) {
                    [pollTBI setEnabled:inGrain->bArray[0]];
                }
                else if(inGrain->command == SET_SWARM_STATUS) {
                    [swarmTBI setEnabled:inGrain->bArray[0]];
                }
            }
        }
    }
}


- (void)makeTabBar {

    lvc = [[LoginViewController alloc] init];
    loginTBI = lvc->tbi;
    
    DiscussViewController *dvc = [[DiscussViewController alloc] init];
    discussTBI = dvc->tbi;
   
    CloudViewController *cvc = [[CloudViewController alloc] init];
    cloudTBI = cvc->tbi;
    
    PollViewController *pvc = [[PollViewController alloc] init];
    pollTBI = pvc->tbi;
    
    SwarmViewController *svc = [[SwarmViewController alloc] init];
    swarmTBI = svc->tbi;
    
    tabBarController = [[UITabBarController alloc] init];
    NSArray *viewControllers = [NSArray arrayWithObjects:lvc, dvc, cvc, pvc, svc,  nil];
    [tabBarController setViewControllers:viewControllers];
    [tabBarController setSelectedIndex:0]; //Sets which tab to display initially, 3=lvc

    tabBarController.tabBar.selectedImageTintColor = [UIColor colorWithRed:(222/255.0)
                                                                     green:(199/255.0)
                                                                      blue:(132/255.0)
                                                                     alpha:1.0];
    
    [[self window] setRootViewController:tabBarController];
    
    [self tabBarItemsEnabled:NO];
}

- (void)tabBarItemsEnabled:(BOOL)val {
    [discussTBI setEnabled:val];
    [cloudTBI setEnabled:val];
    [pollTBI setEnabled:val];
    [swarmTBI setEnabled:val];
}


- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    exit(0);
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    exit(0);
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    exit(0);
}

@end
