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
#import "NAppIDMain.h"
#import "NCommandMain.h"

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
        self.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Discuss" image:[UIImage imageNamed:@"Discuss_30x30.png"] tag:1];
        
        tbi = [self tabBarItem];
        //     [tbi setEnabled:NO];
        //        [tbi setTitle:@"Discuss"];
        
        // UIImage *i1 = [UIImage imageNamed:@"tDiscuss.png"];
        // [tbi setImage:i1];
        
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
    discussPromptLabel.text = @"Group Discussion";
    
	messages = [[NSMutableArray alloc] init];
    
    UIImage * targetImage = [UIImage imageNamed:@"SandDunes1_960x640.png"];
    
    // redraw the image to fit |yourView|'s size
    UIGraphicsBeginImageContextWithOptions([self view].frame.size, NO, 0.f);
    [targetImage drawInRect:CGRectMake(0.f, 0.f, [self view].frame.size.width, [self view] .frame.size.height)];
    UIImage * resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:resultImage]];
	
    tableView.backgroundColor = [UIColor clearColor];
    
	self.tableView.delegate = self;
	self.tableView.dataSource = self;
}

- (IBAction)sendDiscuss:(id)sender
{
    NSLog(@"Entered sendDiscuss");
    
    
    if([inputDiscussField.text length]>0) { //Prevents null strings from being sent
        
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
        
        NSString *cText;
        
        cText = [appDelegate->userName stringByAppendingString:inputDiscussField.text];
        
        NSLog(@"cText = %@\n",cText);
        
        [appDelegate->appSand sendWithGrainElts_AppID:myAppID
                                              Command:myCommand
                                             DataType:myDataType
                                              DataLen:[cText length]
                                               String:cText];
        
        inputDiscussField.text = @"";
        [inputDiscussField setHidden:NO];
        [inputDiscussField resignFirstResponder];
    }
}

// input data function ============================================

- (void)dataReadyHandle:(NGrain *)inGrain
{
    CLog(@"I GOT DATA FROM SAND!!!\n");
    
    if (nil != inGrain) {
        
        if(inGrain->appID == WEB_CHAT || inGrain->appID == INSTRUCTOR_DISCUSS) //Text from Student Discuss
        {
            //    NSLog(@"Filtering AppID 20");
            //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
            [self messageReceived:inGrain->str];
            CLog(@"Got Discuss Data");
        }
        
        else if(inGrain->appID == DISCUSS_PROMPT) //Text from Instructor Panel
        {
            if(inGrain->command == SEND_DISCUSS_PROMPT)//Text from Discuss Prompt
            {
                CLog(@"GOT DISCUSS PROMPT %@\n", inGrain->str);
                discussPromptLabel.text = inGrain->str;
                
            }
        }
        else if(inGrain->appID == INSTRUCTOR_PANEL) //Text from Instructor Panel
        {
            if ((inGrain->command == SET_DISCUSS_STATUS) &&
                (inGrain->bArray[0] == 0)) {
                [sendDiscussButton setEnabled:NO];
                sendDiscussButton.titleLabel.textColor = [UIColor grayColor];
                [inputDiscussField setEnabled:NO];
                [inputDiscussField setBackgroundColor:[UIColor grayColor]];
            }
            else if ((inGrain->command == SET_DISCUSS_STATUS) &&
                     (inGrain->bArray[0] == 1)) {
                {
                    [sendDiscussButton setEnabled:YES];
                    //Sets color to current default value
                    sendDiscussButton.titleLabel.textColor = [UIColor colorWithRed:0.196 green:0.3098 blue:0.5216 alpha:1.0];                                    [inputDiscussField setEnabled:YES];
                    [inputDiscussField setBackgroundColor:[UIColor whiteColor]];
                }
            }
        }
        else {
            CLog(@"No Data for Discuss App");
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
    
    // UIFont *cellFont = [UIFont fontWithName:@"Optima" size:17.0];
    // CGSize constraintSize = CGSizeMake(280.0f, MAXFLOAT);
    // CGSize labelSize = [s sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];
    
	UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        // cell.textLabel.lineBreakMode = UILineBreakModeWordWrap;
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.font = [UIFont fontWithName:@"Optima-Bold" size:17.0];
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

- (BOOL)textFieldShouldReturn:(UITextField *) textField {
    
    if (textField == inputDiscussField) {
        if([inputDiscussField.text length]>0) { //Prevents null strings from being sent
            
            //AppID
            Byte myAppID = WEB_CHAT;
            NSLog(@"myAppID =  %i\n", myAppID);
            
            //COMMAND
            Byte myCommand = SEND_MESSAGE;
            NSLog(@"myCommand =  %i\n", myCommand);
            
            //DATA TYPE
            Byte myDataType = CHAR;
            NSLog(@"myDataType =  %i\n", myDataType);
            
            NSString *cText;
            
            cText = [appDelegate->userName stringByAppendingString:inputDiscussField.text];
            
            NSLog(@"cText = %@\n",cText);
            
            [appDelegate->appSand sendWithGrainElts_AppID:myAppID
                                                  Command:myCommand
                                                 DataType:myDataType
                                                  DataLen:[cText length]
                                                   String:cText];
            
            inputDiscussField.text = @"";
            [inputDiscussField setHidden:NO];
            [inputDiscussField resignFirstResponder];
//            [[self view ] sendSubviewToBack:inputDiscussField];
        }
        else { //Dismisses keyboard if no text is entered but send button is pressed
            inputDiscussField.text = @"";
            [inputDiscussField setHidden:NO];
            [inputDiscussField resignFirstResponder];
//            [[self view ] sendSubviewToBack:inputDiscussField];
        }
    }
    return YES;
}


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
