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

//@interface DiscussViewController ()

//@end

@implementation DiscussViewController 

@synthesize inputDiscussField;
@synthesize tableView;
@synthesize discussPromptLabel;
@synthesize sendDiscussButton;
@synthesize messages;
//@synthesize discussSand;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        discussSand = [NSand alloc];
        UITabBarItem *tbi = [self tabBarItem];
        [tbi setTitle:@"Group Discuss"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
  //  [self initNetworkCommunication];

    [discussSand connect];
    
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
    [inputDiscussField resignFirstResponder];
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
    int myDataLength = [inputDiscussField.text length];
    NSLog(@"myDataLength =  %i\n", myDataLength);
    
    //DATA ARRAY (String from inputDiscussField)
    NSData *sData = [inputDiscussField.text dataUsingEncoding:NSUTF8StringEncoding];  // String
    NSLog(@"myDataArray = %@\n", sData);
    
    [discussSand sendWithGrainElts_AppID:myAppID Command:myCommand DataType:myDataType DataLen:myDataLength DataArray:sData];

    inputDiscussField.text = @"";
    [inputDiscussField setHidden:NO];
}

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
	NSLog(@"stream event %i", streamEvent);
	
	switch (streamEvent) {
			
		case NSStreamEventOpenCompleted:
			NSLog(@"Stream opened");
			break;
		case NSStreamEventHasBytesAvailable:
            
            
            if (theStream == inputStream) {
				
				uint8_t buffer[1024];
				unichar data[1024];
                
                unsigned int len = 0;
                unsigned int sLen = 0;
                int appID;
				
                //This while statement seems redundant 
				while ([inputStream hasBytesAvailable]) {
					len = [inputStream read:buffer maxLength:sizeof(buffer)];
					if (len > 0) {
						
                        
						NSMutableString *output = [[NSMutableString alloc] initWithBytes:buffer length:len encoding:NSUTF8StringEncoding];
                        
                        sLen = [output length];
                        [output getCharacters:data range:NSMakeRange(0, sLen)];
                        
                        appID = (int) data[0];
                        NSLog(@"CURRENT APP_ID: %d", appID);
                        
                        const char *data2 = [output UTF8String];
                        char *cpy = calloc([output length]+1, 1);
                        strncpy(cpy, data2+3, [output length]-3);
                        printf("String %s\n",cpy);
                        
                        NSMutableString *textFromNOMADS = [[NSMutableString alloc] initWithCString:cpy encoding:NSUTF8StringEncoding];
                        
                        for (int i=0;i<sLen+1;i++) {
                            NSLog(@"data[%d]: %c\n", i,data[i]);
                            
                        }
                        
//                        for (int i=0;i<sLen;i++) {
//                            NSLog(@"data2[%d]: %c\n", i,data2[i]);
//                            
//                        }

                        printf("copy %s\n",cpy);
                        
                        if (nil != output) { 
                            
                            if(appID == 22)//Text from Discuss Prompt
                            {
                                //    NSLog(@"Filtering AppID 22");
                                //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
                                discussPromptLabel.text = textFromNOMADS;
                            }
                            else if(appID == 20)//Text from Student Discuss
                            { 
                                //    NSLog(@"Filtering AppID 20");
                                //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
                                [self messageReceived:textFromNOMADS];
                                NSLog(@"Got Discuss Data");
                            } 
                            else if(appID == 24)//Text from Instructor Discuss
                            { 
                                //    NSLog(@"Filtering AppID 20");
                                //    NSLog(@"textFromNOMADS %@",textFromNOMADS);
                                [self messageReceived:textFromNOMADS];
                            }
                            else if(appID == 1)//Text from Instructor Panel
                            {
                                if ([textFromNOMADS isEqualToString:@"DISABLE_DISCUSS_BUTTON"])
                                {
                                    [sendDiscussButton setEnabled:NO];
                                    sendDiscussButton.titleLabel.textColor = [UIColor grayColor];
                                    [inputDiscussField setEnabled:NO];
                                    [inputDiscussField setBackgroundColor:[UIColor grayColor]];
                                }
                                else if ([textFromNOMADS isEqualToString:@"ENABLE_DISCUSS_BUTTON"])
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
				}
			}

			break;
            
			
		case NSStreamEventErrorOccurred:
			
			NSLog(@"Can not connect to the host!");
			break;
			
		case NSStreamEventEndEncountered:
            
            [theStream close];
            [theStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        //    [theStream release];
            theStream = nil;
			
			break;
		default:
			NSLog(@"Unknown event");
	}
    
}
- (void) messageReceived:(NSString *)message {
	NSLog(@"Entering messageReceived");
	[self.messages addObject:message];
	[self.tableView reloadData];
	NSIndexPath *topIndexPath = [NSIndexPath indexPathForRow:messages.count-1 
												   inSection:0];
	[self.tableView scrollToRowAtIndexPath:topIndexPath 
					  atScrollPosition:UITableViewScrollPositionMiddle 
							  animated:YES];
    
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

- (BOOL)textFieldShouldReturn:(UITextField *) textField
{   
    if (textField == inputDiscussField) 
        [self sendDiscuss:(id)self];

    [textField resignFirstResponder];
    
    return YES;   
}

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
