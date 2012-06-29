//
//  PollViewController.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/16/12.
//

#import <UIKit/UIKit.h>

@interface PollViewController : UIViewController <NSStreamDelegate>
{
    UIView			*yesNoView;
	UIView			*aeView;
    UIView          *blankView;
    UIView          *oneToTenView;
    
	NSInputStream	*inputStream;
	NSOutputStream	*outputStream;
	NSMutableArray	*messages;
}


@property (strong, nonatomic) IBOutlet UIView *aeView;
@property (strong, nonatomic) IBOutlet UIView *yesNoView;
@property (strong, nonatomic) IBOutlet UIView *oneToTenView;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptYesNoLabel;
@property (strong, nonatomic) IBOutlet UIView *blankView;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptAeLabel;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptBlankLabel;
@property (nonatomic, retain) NSInputStream *inputStream;
@property (nonatomic, retain) NSOutputStream *outputStream;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptOneToTenLabel;
@property (weak, nonatomic) IBOutlet UILabel *pollOneToTenValLabel;


@property (nonatomic, retain) NSMutableArray *messages;

- (IBAction)pollSendNo:(id)sender;
- (IBAction)pollSendYes:(id)sender;
- (IBAction)pollSendA:(id)sender;
- (IBAction)pollSendB:(id)sender;
- (IBAction)pollSendC:(id)sender;
- (IBAction)pollSendD:(id)sender;
- (IBAction)pollSendE:(id)sender;
- (IBAction)pollOneToTenSliderChanged:(id)sender; 
- (IBAction)pollOneToTenVoteButton:(id)sender;

- (void) initNetworkCommunication;
- (void) messageReceived:(NSString *)message;

@end
