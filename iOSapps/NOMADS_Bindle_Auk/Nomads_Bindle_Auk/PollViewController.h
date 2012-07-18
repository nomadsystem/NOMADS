//
//  PollViewController.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/16/12.
//  Updated to use NSAND on 7/2/12
//

#ifndef POLLVIEWCONTROLLER
#define POLLVIEWCONTROLLER

#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface PollViewController : UIViewController <SandDelegate>
{
    UIView			*yesNoView;
	UIView			*aeView;
    UIView          *blankView;
    UIView          *oneToTenView;
    
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    
	NSMutableArray	*messages;
    
    @public UITabBarItem __weak *tbi;
}


@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (weak, nonatomic) UITabBarItem *tbi;



@property (strong, nonatomic) IBOutlet UIView *aeView;
@property (strong, nonatomic) IBOutlet UIView *yesNoView;
@property (strong, nonatomic) IBOutlet UIView *oneToTenView;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptYesNoLabel;
@property (strong, nonatomic) IBOutlet UIView *blankView;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptAeLabel;
@property (weak, nonatomic) IBOutlet UILabel *pollPromptBlankLabel;
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

#endif