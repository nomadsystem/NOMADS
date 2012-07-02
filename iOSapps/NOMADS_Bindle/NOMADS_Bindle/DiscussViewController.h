//
//  DiscussViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//
#ifndef DISCUSSVIEWCONTROLLER
#define DISCUSSVIEWCONTROLLER

#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"


//    INPUT: DCAppDelegate needed to receive

@interface DiscussViewController : UIViewController <SandDelegate, UITableViewDelegate, UITableViewDataSource> {
    
    
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    
	UILabel         __weak *discussPromptLabel;
    UITableView		__weak *tableView;
	NSMutableArray	*messages;
    
    @public UITabBarItem __weak *tbi;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (weak, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) IBOutlet UILabel *discussPromptLabel;
@property (weak, nonatomic) IBOutlet UIButton *sendDiscussButton;
@property (weak, nonatomic) IBOutlet UITabBarItem *tbi;

@property (nonatomic, retain) NSMutableArray *messages;



- (IBAction)sendDiscuss:(id)sender;
- (void) messageReceived:(NSString *)message;
- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand


@end
#endif