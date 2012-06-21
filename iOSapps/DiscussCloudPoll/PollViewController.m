//
//  PollViewController.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/16/12.
//

#import "PollViewController.h"

int oneToTenVoteVal;

@implementation PollViewController
@synthesize oneToTenView;
@synthesize aeView;
@synthesize yesNoView;
@synthesize pollPromptYesNoLabel;
@synthesize blankView;
@synthesize pollPromptAeLabel;
@synthesize pollPromptBlankLabel;

@synthesize inputStream, outputStream;
@synthesize pollPromptOneToTenLabel;
@synthesize pollOneToTenValLabel;
@synthesize messages;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        UITabBarItem *tbi = [self tabBarItem];
        [tbi setTitle:@"Poll Aura"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self initNetworkCommunication];
    messages = [[NSMutableArray alloc] init];
    [[self blankView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self aeView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self yesNoView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    [[self oneToTenView] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];
    pollOneToTenValLabel.text = @"5";
    [self.view bringSubviewToFront:blankView];
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"yes"];
}

- (void) initNetworkCommunication {
	
	CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
	CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"nomads.music.virginia.edu", 52807, &readStream, &writeStream);
	
	inputStream = (__bridge NSInputStream *)readStream;
	outputStream = (__bridge NSOutputStream *)writeStream;
	[inputStream setDelegate:self];
	[outputStream setDelegate:self];
	[inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[inputStream open];
	[outputStream open];
    	
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

- (IBAction)pollSendYes:(id)sender //I'm not sure about this "id sender" business
{

    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"yes"];
}
- (IBAction)pollSendNo:(id)sender
{
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"no"];
}
- (IBAction)pollSendA:(id)sender {
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"A"];
}

- (IBAction)pollSendB:(id)sender {
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"B"];
}

- (IBAction)pollSendC:(id)sender {
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"C"];
}

- (IBAction)pollSendD:(id)sender {
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"D"];
}

- (IBAction)pollSendE:(id)sender {
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:@"E"];
}

- (IBAction)pollOneToTenSliderChanged:(id)sender {
    UISlider *slider = (UISlider *)sender;
    oneToTenVoteVal = (int)slider.value;
    NSString *newText = [[NSString alloc] initWithFormat:@"%d", oneToTenVoteVal];
    pollOneToTenValLabel.text = newText;
}

- (IBAction)pollOneToTenVoteButton:(id)sender {
    NSString *voteValString = [NSString stringWithFormat:@"%d", oneToTenVoteVal];
    [self sendToNOMADSappID:62 sendToNOMADSoutMessage:(@"%@",voteValString)];
    NSLog(@"VOTE VAL STRING: %@",voteValString);

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
                
                
				int len, sLen;
                int appID;
				
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
                        
                        if(appID == 1)//Text from Instructor Panel
                        {
                            if ([textFromNOMADS isEqualToString:@"DISABLE_POLL_BUTTON"])
                            {
                                [self.view bringSubviewToFront:blankView];
                            }
//                            else if ([textFromNOMADS isEqualToString:@"ENABLE_POLL_BUTTON"])
//                            {
//                                Have it come back to the regular view??
//                            }
                        }
                        
                        else if(appID == 60) //Text from Teacher Poll
                        { 
                            NSLog(@"Enterng appID 60 if statement");
                            NSRange range = [textFromNOMADS rangeOfString:@";"];
                            
                            if(range.location != NSNotFound) {
                                
                            
                                NSLog(@"Filtering AppID 60");
                                NSLog(@"textFromNOMADS %@",textFromNOMADS);
                                
                                NSMutableArray *parsedStrings = [[NSMutableArray alloc] initWithArray:[textFromNOMADS componentsSeparatedByString:@";"]];
                                
                                NSLog(@"parsedStringsArray %@",parsedStrings);
                                
                                //Type of Question
                                NSString *questionType = [parsedStrings objectAtIndex:0];
                                NSLog(@"questionType = %@",questionType);
                                
                                //The question asked by the poll
                                NSString *toBePosed = [parsedStrings objectAtIndex:1];
                                NSLog(@"toBePosed = %@",toBePosed);
                                
                                
                                if([questionType isEqualToString:@"Yes-No"]){
                                    NSLog(@"We got a Yes-No Question");
                                    pollPromptYesNoLabel.text = toBePosed;
                                    [self.view bringSubviewToFront:yesNoView];
                                    
                                }
                                if([questionType isEqualToString:@"A through E"]){
                                    NSLog(@"We got an A-E Question");
                                    pollPromptAeLabel.text = toBePosed;
                                    [self.view bringSubviewToFront:aeView];
                                    
                                }
                                if([questionType isEqualToString:@"Scale of 1 to 10"]){
                                    NSLog(@"We got an 1-10 Question");
                                    pollPromptOneToTenLabel.text = toBePosed;
                                    [self.view bringSubviewToFront:oneToTenView];
                                }
                    
                            }
                            
                            else {
                                NSLog(@"Didn't contain ';' textFromNOMADS %@",textFromNOMADS);
                            }
                            
                        } 
                        
//                        else if(appID == 22)//Text from Discuss Prompt
//                        {
//                            NSLog(@"Filtering AppID 22");
//                            NSLog(@"textFromNOMADS %@",textFromNOMADS);
//                            pollPromptLabel.text = textFromNOMADS;
//                        }
                        
                        
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
	
	[self.messages addObject:message];
    
}

- (void) sendToNOMADSappID:(int)appID sendToNOMADSoutMessage:(NSString *)outMessage
{

    
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

- (void)viewDidUnload
{
    [self setPollPromptYesNoLabel:nil];
    [self setYesNoView:nil];
    [self setAeView:nil];
    [self setPollPromptAeLabel:nil];
    [self setBlankView:nil];
    [self setPollPromptBlankLabel:nil];
    [self setPollPromptYesNoLabel:nil];
    [self setOneToTenView:nil];
    [self setPollPromptOneToTenLabel:nil];
    [self setPollOneToTenValLabel:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end

