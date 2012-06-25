//
//  DiscussViewController.m
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import "DiscussViewController.h"
#import "NSand.h"
#import "NGrain.h"

//@interface DiscussViewController ()

//@end

@implementation DiscussViewController 

@synthesize inputDiscussField;
@synthesize tableView;
@synthesize inputStream, outputStream;
@synthesize discussPromptLabel;
@synthesize sendDiscussButton;
@synthesize messages;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
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
//    mySand = [NSand alloc];
//    [mySand connect];
    NSLog(@"mySand connect");
    
    inputDiscussField.text = @"";
    discussPromptLabel.text = @"Discuss Prompt";
	messages = [[NSMutableArray alloc] init];
    
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
	
    tableView.backgroundColor = [UIColor clearColor];
    
	self.tableView.delegate = self;
	self.tableView.dataSource = self;
}

- (void) initNetworkCommunication {
	
	CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
	CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"nomads.music.virginia.edu", 52911, &readStream, &writeStream);
	
	inputStream = (__bridge NSInputStream *)readStream;
	outputStream = (__bridge NSOutputStream *)writeStream;
	[inputStream setDelegate:self];
	[outputStream setDelegate:self];
	[inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[inputStream open];
	[outputStream open];
    
    [self sendToNOMADSappID:20 sendToNOMADSoutMessage:@""];
}

- (NSData*) convertToJavaUTF8Int : (NSInteger*) appID 
{ 
    //     NSUInteger len = [appID lengthOfBytesUsingEncoding:NSUTF8StringEncoding]; 
    //     Byte buffer[2]; 
    //     buffer[0] = (0xff & (len >> 8)); 
    //     buffer[1] = (0xff & len); 
    NSMutableData *outData = [NSMutableData dataWithCapacity:1]; 
    [outData appendBytes:appID length:1];
    return outData;
}

- (NSData*) convertToJavaUTF8 : (NSString*) str { 
    NSUInteger len = [str lengthOfBytesUsingEncoding:NSUTF8StringEncoding]; 
    Byte buffer[2]; 
    buffer[0] = (0xff & (len >> 8)); 
    buffer[1] = (0xff & len); 
    NSMutableData *outData = [NSMutableData dataWithCapacity:2]; 
    [outData appendBytes:buffer length:2]; 
    [outData appendData:[str dataUsingEncoding:NSUTF8StringEncoding]]; 
    return outData;
}



- (IBAction)sendDiscuss:(id)sender
{
    NSLog(@"Entered sendDiscuss");
    [inputDiscussField resignFirstResponder];
    [self sendToNOMADSappID:20 sendToNOMADSoutMessage:inputDiscussField.text];
    NSLog(@"Sent Data from Discuss");
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

- (void) sendToNOMADSappID:(int)appID sendToNOMADSoutMessage:(NSString *)outMessage
{
    

    NSLog(@"outMessage = %@", outMessage);
    
    NSData *aData = [self convertToJavaUTF8Int:&appID]; //Store AppID as NSData
    NSData *data = [self convertToJavaUTF8:outMessage]; //Store text as NSData
    
    int aDataLength = [aData length]; //Get length of AppID
    int dataLength = [data length]; //Get text length
    
    //Write AppID as String to outputStream
    int aNum = [outputStream write:(const uint8_t *)[aData bytes] maxLength:aDataLength];
    //Write text to outputStream
    int num = [outputStream write:(const uint8_t *)[data bytes] maxLength:dataLength];
    
    //Check and make sure data was sent out properly
	if (-1 == aNum) {
        NSLog(@"Error writing appID to stream %@: %@", outputStream, [outputStream streamError]);
    }
    else {
        NSLog(@"Wrote %i bytes to stream %@.", num, outputStream);
    }
    
	if (-1 == num) {
        NSLog(@"Error writing data to stream %@: %@", outputStream, [outputStream streamError]);
    }
    else {
        NSLog(@"Wrote %i bytes to stream %@.", num, outputStream);
    }
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
