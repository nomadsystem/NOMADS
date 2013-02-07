//
//  CloudViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//
#ifndef CLOUDVIEWCONTROLLER
#define CLOUDVIEWCONTROLLER


#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface CloudViewController : UIViewController < SandDelegate, UITextFieldDelegate>
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;

    NSMutableArray	*messages;
    
    UILabel __weak *cloudPromptLabel;
    @public UITabBarItem __weak *tbi;
}

@property (weak, nonatomic) IBOutlet UILabel *userCloudEntryLabel;
@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (weak, nonatomic) IBOutlet UILabel *cloudLabel;
@property (weak, nonatomic) IBOutlet UITextField *inputCloudField;
@property (nonatomic, retain) NSMutableArray *messages;
@property (weak, nonatomic) IBOutlet UIButton *sendCloudButton;
@property (weak, nonatomic) UITabBarItem *tbi;

- (IBAction)sendCloud:(id)sender;
@end

#endif