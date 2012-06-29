//
//  CloudViewController.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import <UIKit/UIKit.h>
#import "NSand.h"

@interface CloudViewController : UIViewController < NSStreamDelegate, UITextFieldDelegate>
{
    NSand   *cloudSand;
    NSMutableArray	*messages;
    
    UILabel __weak *cloudLabel;
}

@property (strong, retain) NSand *cloudSand;
@property (weak, nonatomic) IBOutlet UILabel *cloudLabel;
@property (weak, nonatomic) IBOutlet UITextField *inputCloudField;
@property (nonatomic, retain) NSMutableArray *messages;
@property (weak, nonatomic) IBOutlet UIButton *sendCloudButton;

- (IBAction)sendCloud:(id)sender;
- (void) initNetworkCommunication;
@end
