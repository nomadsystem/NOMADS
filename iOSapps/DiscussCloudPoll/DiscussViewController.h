//
//  DiscussViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "DiscussCloudAppDelegate.h"


@interface DiscussViewController : UIViewController <SandDelegate, UITableViewDelegate, UITableViewDataSource> // INPUT: DCAppDelegate needed to receive
{


    NSand   *appSand;
    DiscussCloudAppDelegate *appDelegate;
    
	UILabel         __weak *discussPromptLabel;
    UITableView		__weak *tableView;
	NSMutableArray	*messages;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) DiscussCloudAppDelegate *appDelegate;
@property (weak, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) IBOutlet UILabel *discussPromptLabel;
@property (weak, nonatomic) IBOutlet UIButton *sendDiscussButton;

@property (nonatomic, retain) NSMutableArray *messages;



- (IBAction)sendDiscuss:(id)sender;
- (void) messageReceived:(NSString *)message;
- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand


@end
