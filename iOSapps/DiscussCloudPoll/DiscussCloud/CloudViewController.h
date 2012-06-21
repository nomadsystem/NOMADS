//
//  CloudViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import <UIKit/UIKit.h>

@interface CloudViewController : UIViewController <NSStreamDelegate, UITextFieldDelegate>
{
    
    NSInputStream	*inputStream;
    NSOutputStream	*outputStream;
    NSMutableArray	*messages;
    
    UILabel __weak *cloudLabel;
}
@property (weak, nonatomic) IBOutlet UILabel *cloudLabel;
@property (weak, nonatomic) IBOutlet UITextField *inputCloudField;
@property (nonatomic, retain) NSInputStream *inputStream;
@property (nonatomic, retain) NSOutputStream *outputStream;
@property (nonatomic, retain) NSMutableArray *messages;
@property (weak, nonatomic) IBOutlet UIButton *sendCloudButton;

- (IBAction)sendCloud:(id)sender;
- (void) initNetworkCommunication;
@end
