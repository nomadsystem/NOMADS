//
//  LoginViewController.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/21/12.
//
#ifndef LOGINVIEWCONTROLLER
#define LOGINVIEWCONTROLLER


#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"


@interface LoginViewController : UIViewController <SandDelegate>
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    @public UITabBarItem __weak *tbi;
    NSTimer* connectionTimeoutTimer;
    
}
@property (weak, nonatomic) IBOutlet UITextField *portField;
@property (weak, nonatomic) IBOutlet UILabel *groupIDLabel;
@property (weak, nonatomic) IBOutlet UILabel *userNameIsLabel;

@property (weak, nonatomic) IBOutlet UILabel *pleaseEnterYourNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;

@property (weak, nonatomic) IBOutlet UILabel *welcomeMessage2;
@property (weak, nonatomic) IBOutlet UILabel *welcomeMessage;
@property (weak, nonatomic) IBOutlet UILabel *instructions;

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;

@property (weak, nonatomic) IBOutlet UITextField *loginTextField;
@property (weak, nonatomic) IBOutlet UILabel *connectStatusLabel;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UIButton *disconnectButton;
@property (weak, nonatomic) IBOutlet UIButton *moreInfoButton;

@property (nonatomic, retain) NSTimer* connectionTimeoutTimer;


- (void) reinit;

- (IBAction)loginButton:(id)sender;
- (IBAction)disconnectButton:(id)sender;
- (IBAction)moreInfoButton:(id)sender;


@end

#endif
