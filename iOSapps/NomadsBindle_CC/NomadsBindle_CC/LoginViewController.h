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
    
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;

@property (weak, nonatomic) IBOutlet UITextField *loginTextField;
@property (weak, nonatomic) IBOutlet UILabel *connectStatusLabel;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UIButton *disconnectButton;


- (IBAction)loginButton:(id)sender;
- (IBAction)disconnectButton:(id)sender;




@end

#endif
