//
//  LoginViewController.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 5/21/12.
//
#import "DiscussCloudAppDelegate.h"
#import "LoginViewController.h"

@implementation LoginViewController
@synthesize inputStream, outputStream;
@synthesize loginTextField;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
//        UITabBarItem *tbi = [self tabBarItem];
//        [tbi setTitle:@"NOMADS Login"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    
    
    
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

- (IBAction)loginButton:(id)sender {
    [loginTextField resignFirstResponder];
    [self sendToNOMADSappID:40 sendToNOMADSoutMessage:loginTextField.text];
    loginTextField.text = @"";
    [loginTextField setHidden:NO];
    DiscussCloudAppDelegate *appDelegate = (DiscussCloudAppDelegate *)[[UIApplication sharedApplication] delegate];

    [[self view] removeFromSuperview];
    [appDelegate makeTabBar]; 
    
    
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
                            
                            
                            
                            NSLog(@"Logn App Handle");
                        
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


- (void) sendToNOMADSappID:(int)appID sendToNOMADSoutMessage:(NSString *)outMessage
{
    
    
    NSLog(@"outMessage = %@", outMessage);
    
    NSData *aData = [self convertToJavaUTF8Int:&appID]; //Store AppID as NSData
    NSData *data = [self convertToJavaUTF8:outMessage]; //Store text as NSData
    
    int aDataLength = [aData length]; //Get length of AppID
    int dataLength = [data length]; //Get text length
    [self initNetworkCommunication];
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

    
     if (textField == loginTextField) 
        [self loginButton:(id)self];
    
    [textField resignFirstResponder];
    
    return YES;   
}

- (void)viewDidUnload
{
    [self setLoginTextField:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}



@end
