//
//  AukViewController.h
//  NOMADS_Bindle_Auk
//
//  Created by Steven Kemper on 7/18/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef AUK_VIEWCONTROLLER
#define AUK_VIEWCONTROLLER

#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface AukViewController : UIViewController <SandDelegate>
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    __weak UILabel *promptLabel;
    __weak UILabel *connectionLabel;
    __weak UIView *loginView;
    __weak UIView *aukView;
    __weak UITextField *inputDiscussField;
    __weak UITextField *inputCloudField;
    __weak UIBarButtonItem *loginNavBackButton;
    __weak UINavigationItem *loginNavTitle;
    __weak UINavigationBar *loginNavBar;
    __weak UIToolbar *aukToolbar;
}

@property (weak, nonatomic) IBOutlet UIBarButtonItem *loginNavBackButton;
@property (weak, nonatomic) IBOutlet UINavigationItem *loginNavTitle;
@property (weak, nonatomic) IBOutlet UINavigationBar *loginNavBar;
@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (weak, nonatomic) IBOutlet UIButton *joinNomadsButton;
@property (weak, nonatomic) IBOutlet UIButton *leaveNomadsButton;
@property (weak, nonatomic) IBOutlet UIView *loginView;
@property (weak, nonatomic) IBOutlet UIView *aukView;
@property (weak, nonatomic) IBOutlet UILabel *promptLabel;
@property (weak, nonatomic) IBOutlet UILabel *connectionLabel;
@property (weak, nonatomic) IBOutlet UITextField *joinTextField;
@property (weak, nonatomic) IBOutlet UIToolbar *aukToolbar;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *aukBarButtonDiscuss;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *aukBarButtonCloud;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *aukBarButtonSettings;
@property (weak, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (weak, nonatomic) IBOutlet UITextField *inputCloudField;
- (IBAction)discussButton:(id)sender;
- (IBAction)cloudButton:(id)sender;
- (IBAction)settingsButton:(id)sender;
- (IBAction)leaveNomadsButton:(id)sender;
- (IBAction)joinNomadsButton:(id)sender;
- (IBAction)loginNavBackButton:(id)sender;

@end
#endif