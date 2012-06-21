//
//  CloudViewController.m
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#import "CloudViewController.h"

//@interface CloudViewController ()

//@end

@implementation CloudViewController
@synthesize cloudLabel;
@synthesize inputCloudField;
@synthesize inputStream, outputStream;
@synthesize messages;
@synthesize sendCloudButton;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        UITabBarItem *tbi = [self tabBarItem];
        [tbi setTitle:@"Thought Cloud"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    inputCloudField.text = @"";
    [self initNetworkCommunication];
    messages = [[NSMutableArray alloc] init];
    [[self view] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"SandDunes1_960x640.png"]]];

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
    
    [self sendToNOMADSappID:52 sendToNOMADSoutMessage:@" "];

	
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
- (IBAction)sendCloud
{
    [inputCloudField resignFirstResponder];
        
    [self sendToNOMADSappID:52 sendToNOMADSoutMessage:inputCloudField.text];

    inputCloudField.text = @"";
    [inputCloudField setHidden:NO];
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
                        
                        
                        if(appID == 54)//Text from Discuss Prompt
                        {
                            NSLog(@"Filtering AppID 54");
                            NSLog(@"textFromNOMADS %@",textFromNOMADS);
                            cloudLabel.text = textFromNOMADS;
                        }
                        else if(appID == 1)//Text from Instructor Panel
                        {
                            if ([textFromNOMADS isEqualToString:@"DISABLE_CLOUD_BUTTON"])
                            {
                                [sendCloudButton setEnabled:NO];
                                sendCloudButton.titleLabel.textColor = [UIColor grayColor];
                                [inputCloudField setBackgroundColor:[UIColor grayColor]];
                            }
                            else if ([textFromNOMADS isEqualToString:@"ENABLE_CLOUD_BUTTON"])
                            {
                                [sendCloudButton setEnabled:YES];
                                sendCloudButton.titleLabel.textColor = [UIColor colorWithRed:0.196 green:0.3098 blue:0.5216 alpha:1.0]; 
                                [inputCloudField setBackgroundColor:[UIColor whiteColor]];
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
    [self setInputCloudField:nil];
    [self setCloudLabel:nil];
    [self setSendCloudButton:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)textFieldShouldReturn:(UITextField *) textField
{   
    if (textField == inputCloudField) 
        [self sendCloud];
    
    [textField resignFirstResponder];
    
    return YES;   
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end
