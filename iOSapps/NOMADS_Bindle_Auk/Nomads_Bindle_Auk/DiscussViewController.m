//
//  DiscussViewController.m
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//
#import "DiscussViewController.h"
#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"
#import "NAppIDAuk.h"
#import "NCommandAuk.h"

//@interface DiscussViewController ()

//@end

@implementation DiscussViewController

@synthesize inputDiscussField;
@synthesize tableView;
@synthesize discussPromptLabel;
@synthesize sendDiscussButton;
@synthesize messages;
@synthesize appSand; //Our implementation of NSand
@synthesize appDelegate;
@synthesize tbi;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        tbi = [self tabBarItem];
   //     [tbi setEnabled:NO];
        [tbi setTitle:@"Group Discuss"];
        appDelegate = (BindleAppDelegate *)[[UIApplication sharedApplication] delegate];

        // SAND:  set a pointer inside appSand so we get notified when network data is available
        [appDelegate->appSand setDelegate:self];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    //  [self initNetworkCommunication];
    
    inputDiscussField.text = @"";
    discussPromptLabel.text = @"Discuss Prompt";
	messages = [[NSMutableArray alloc] init];
    
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
	
    tableView.backgroundColor = [UIColor clearColor];
    
	self.tableView.delegate = self;
	self.tableView.dataSource = self;
}



- (IBAction)sendDiscuss:(id)sender
{
    NSLog(@"Entered sendDiscuss");
    
    //AppID
    Byte myAppID = WEB_CHAT;
    NSLog(@"myAppID =  %i\n", myAppID);
    
    //COMMAND
    Byte myCommand = SEND_MESSAGE;
    NSLog(@"myCommand =  %i\n", myCommand);
    
    //DATA TYPE
    Byte myDataType = CHAR;
    NSLog(@"myDataType =  %i\n", myDataType);
    
    //DATA LENGTH
    //****STK Currently set directly in sendWithGrainElts
    
    //DATA ARRAY (String from inputDiscussField)
    //****STK Currently set directly in sendWithGrainElts
    
    [appDelegate->appSand sendWithGrainElts_AppID:myAppID 
                                          Command:myCommand 
                                         DataType:myDataType 
                                          DataLen:[inputDiscussField.text length] 
                                           String:inputDiscussField.text];
        
    inputDiscussField.text = @"";
    [inputDiscussField setHidden:NO];
    [inputDiscussField resignFirstResponder];

}

// input data function ============================================

- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) { 
        
        if(inGrain->appID == DISCUSS_PROMPT)//Text from Discuss Prompt
        {
            //    NSLog(@"Filtering AppID 22");
            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
            discussPromptLabel.text = inGrain->str;
        }
        else if(inGrain->appID == WEB_CHAT || inGrain->appID == INSTRUCTOR_DISCUSS) //Text from Student Discuss
        { 
            //    NSLog(@"Filtering AppID 20");
            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
            [self messageReceived:inGrain->str];
            NSLog(@"Got Discuss Data");
        } 
        else if(inGrain->appID == INSTRUCTOR_PANEL)//Text from Instructor Panel****STK This will need to be changed to Commands
        {
            if ([inGrain->str isEqualToString:@"DISABLE_DISCUSS_BUTTON"])
            {
                [sendDiscussButton setEnabled:NO];
                sendDiscussButton.titleLabel.textColor = [UIColor grayColor];
                [inputDiscussField setEnabled:NO];
                [inputDiscussField setBackgroundColor:[UIColor grayColor]];
            }
            else if ([inGrain->str isEqualToString:@"ENABLE_DISCUSS_BUTTON"])
            {
                [sendDiscussButton setEnabled:YES];
                //Sets color to current default value
                sendDiscussButton.titleLabel.textColor = [UIColor colorWithRed:0.196 green:0.3098 blue:0.5216 alpha:1.0];                                    [inputDiscussField setEnabled:YES];
                [inputDiscussField setBackgroundColor:[UIColor whiteColor]];
            }
        }
        else {
            NSLog(@"No Data for Discuss App");
        }
    }

}

- (void) messageReceived:(NSString *)message {
	NSLog(@"Entering messageReceived");
    if (message != nil) {
        [self.messages addObject:message];
        [self.tableView reloadData];
        NSIndexPath *topIndexPath = [NSIndexPath indexPathForRow:messages.count-1 
                                                       inSection:0];
        [self.tableView scrollToRowAtIndexPath:topIndexPath 
                              atScrollPosition:UITableViewScrollPositionMiddle 
                                      animated:YES];
    }    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	NSLog(@"Entering Table View");
	NSString *s = (NSString *) [messages objectAtIndex:indexPath.row];
	
    static NSString *CellIdentifier = @"ChatCellIdentifier";
    
    UIFont *cellFont = [UIFont fontWithName:@"Helvetica-Bold" size:25.0];
    CGSize constraintSize = CGSizeMake(280.0f, MAXFLOAT);
    CGSize labelSize = [s sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];
    
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.textLabel.lineBreakMode = UILineBreakModeWordWrap;
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:17.0];
        
        
    }
	
	cell.textLabel.text = s;
	return cell;
    
	
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return messages.count;
    
}
//****STK Enter key not implemented (it was causing problems. . .)
//- (BOOL)textFieldShouldReturn:(UITextField *) textField
//{   
//    if (textField == inputDiscussField) 
//        [self sendDiscuss:(id)self];
//    
//    [textField resignFirstResponder];
//    
//    return YES;   
//}

- (void)viewDidUnload
{
    [self setInputDiscussField:nil];
    [self setDiscussPromptLabel:nil];
    [self setSendDiscussButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
