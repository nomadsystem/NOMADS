//
//  DiscussViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import <UIKit/UIKit.h>
#import "NSand.h"


@interface DiscussViewController : UIViewController <NSStreamDelegate, UITableViewDelegate, UITableViewDataSource>
{
    
	NSInputStream	*inputStream;
	NSOutputStream	*outputStream;

    NSand *mySand;
    
	UILabel         __weak *discussPromptLabel;
    UITableView		__weak *tableView;
	NSMutableArray	*messages;
}

@property (weak, nonatomic) IBOutlet UITextField *inputDiscussField;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic, retain) NSInputStream *inputStream;
@property (nonatomic, retain) NSOutputStream *outputStream;
@property (weak, nonatomic) IBOutlet UILabel *discussPromptLabel;
@property (weak, nonatomic) IBOutlet UIButton *sendDiscussButton;

@property (nonatomic, retain) NSMutableArray *messages;



- (IBAction)sendDiscuss:(id)sender;
- (void) initNetworkCommunication;
- (void) messageReceived:(NSString *)message;

@end
