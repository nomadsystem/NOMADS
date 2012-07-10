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

@property (unsafe_unretained, nonatomic) IBOutlet UITextField *loginTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *connectStatusLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *loginButton;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *disconnectButton;


- (IBAction)loginButton:(id)sender;
- (IBAction)disconnectButton:(id)sender;




@end

#endif
