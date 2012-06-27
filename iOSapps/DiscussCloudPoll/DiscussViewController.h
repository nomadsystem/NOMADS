//
//  DiscussViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import <UIKit/UIKit.h>
#import "NSand.h"


@interface DiscussViewController : UIViewController <UITableViewDelegate, UITableViewDataSource>
{


    NSand   *discussSand;
    
	UILabel         __weak *discussPromptLabel;
    UITableView		__weak *tableView;
	NSMutableArray	*messages;
}

@property (strong, retain) NSand *discussSand;
@property (weak, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) IBOutlet UILabel *discussPromptLabel;
@property (weak, nonatomic) IBOutlet UIButton *sendDiscussButton;

@property (nonatomic, retain) NSMutableArray *messages;



- (IBAction)sendDiscuss:(id)sender;
- (void) messageReceived:(NSString *)message;

@end
