//
//  DiscussViewController.m
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import "DiscussViewController.h"
#import "NSand.h"
#import "NGrain.h"
#import "NAppID.h"
#import "NCommand.h"
#import "NDataType.h"
#import "NGlobals.h"

//@interface DiscussViewController ()

//@end

@implementation DiscussViewController 

@synthesize inputDiscussField;
@synthesize tableView;
@synthesize discussPromptLabel;
@synthesize sendDiscussButton;
@synthesize messages;
@synthesize discussSand;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        UITabBarItem *tbi = [self tabBarItem];
        [tbi setTitle:@"Group Discuss"];
        discussSand = [[NSand alloc] init]; 
        
        [discussSand connect];
//        [[discussSand streamIn] setDelegate:self];
//        [[discussSand streamOut] setDelegate:self];
//        [[discussSand streamIn] scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
//        [[discussSand streamOut] scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
//        [[discussSand streamIn] open];
//        [[discussSand streamOut] open];

    }
    [discussSand setDelegate:self];
    
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
    NAppID *appID = [[NAppID alloc] init];
    Byte myAppID = appID->WEB_CHAT;
    NSLog(@"myAppID =  %i\n", myAppID);
    
    //COMMAND
    NCommand *command = [[NCommand alloc] init];
    Byte myCommand = command->SEND_MESSAGE;
    NSLog(@"myCommand =  %i\n", myCommand);
    
    //DATA TYPE
    NDataType *dataType = [[NDataType alloc] init];
    Byte myDataType = dataType->BYTE;
    NSLog(@"myDataType =  %i\n", myDataType);
    
    //DATA LENGTH
//    int myDataLength = [inputDiscussField.text length];
//    NSLog(@"myDataLength =  %i\n", myDataLength);

    
    //DATA ARRAY (String from inputDiscussField)
//    NSData *sData = [inputDiscussField.text dataUsingEncoding:NSUTF8StringEncoding];  // String
//    NSLog(@"myDataArray = %@\n", sData);
    
  // [discussSand fooWith_AppID:20 Command:1 DataType:1 DataLen:[inputDiscussField.text length]];
    [discussSand sendWithGrainElts_AppID:20 
                                 Command:1 
                                DataType:1 
                                 DataLen:[inputDiscussField.text length] 
                                  String:inputDiscussField.text];
    
//    [[self discussSand] sendWithGrainElts_AppID:myAppID Command:myCommand DataType:myDataType DataLen:myDataLength DataArray:sData];
    
    inputDiscussField.text = @"";
    [inputDiscussField setHidden:NO];
    [inputDiscussField resignFirstResponder];
}


- (void)dataReadyHandle:(NGrain *)inGrain;
{
    NSLog(@"I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) { 
        
        if(inGrain->appID == 22)//Text from Discuss Prompt
        {
            //    NSLog(@"Filtering AppID 22");
            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
            discussPromptLabel.text = inGrain->str;
        }
        else if(inGrain->appID == WEB_CHAT) //Text from Student Discuss
        { 
            //    NSLog(@"Filtering AppID 20");
            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
            [self messageReceived:inGrain->str];
            NSLog(@"Got Discuss Data");
        } 
        else if(inGrain->appID == 24)//Text from Instructor Discuss
        { 
            //    NSLog(@"Filtering AppID 20");
            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
            [self messageReceived:inGrain->str];
        }
        else if(inGrain->appID == 1)//Text from Instructor Panel
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
