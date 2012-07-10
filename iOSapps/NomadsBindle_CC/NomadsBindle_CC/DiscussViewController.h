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
    
	UILabel         __unsafe_unretained *discussPromptLabel;
    UITableView		__unsafe_unretained *tableView;
	NSMutableArray	*messages;
    
    @public UITabBarItem __unsafe_unretained *tbi;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@property (unsafe_unretained, nonatomic) IBOutlet UILabel *discussPromptLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *sendDiscussButton;
@property (unsafe_unretained, nonatomic) IBOutlet UITabBarItem *tbi;

@property (nonatomic, retain) NSMutableArray *messages;



- (IBAction)sendDiscuss:(id)sender;
- (void) messageReceived:(NSString *)message;
- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand


@end
#endif