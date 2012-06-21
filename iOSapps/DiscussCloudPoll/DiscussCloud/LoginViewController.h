//
//  LoginViewController.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/21/12.
//

#import <UIKit/UIKit.h>


@interface LoginViewController : UIViewController <NSStreamDelegate>
{

	NSInputStream	*inputStream;
	NSOutputStream	*outputStream;
    
}
@property (weak, nonatomic) IBOutlet UITextField *loginTextField;

@property (nonatomic, retain) NSInputStream *inputStream;
@property (nonatomic, retain) NSOutputStream *outputStream;



- (IBAction)loginButton:(id)sender;




@end
