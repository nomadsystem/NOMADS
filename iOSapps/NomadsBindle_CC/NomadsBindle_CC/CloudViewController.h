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
    
    UILabel __unsafe_unretained *cloudLabel;
    @public UITabBarItem __unsafe_unretained *tbi;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;

@property (unsafe_unretained, nonatomic) IBOutlet UILabel *cloudLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *inputCloudField;
@property (nonatomic, retain) NSMutableArray *messages;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *sendCloudButton;
@property (unsafe_unretained, nonatomic) UITabBarItem *tbi;

- (IBAction)sendCloud:(id)sender;
@end

#endif