//
//  ChatClientViewController.m
//  ChatClient
//
//  Created by cesarerocchi on 5/27/11.
//  Copyright 2011 studiomagnolia.com. All rights reserved.
//

#import "ChatClientViewController.h"

@implementation ChatClientViewController

@synthesize joinView, chatView;
@synthesize inputStream, outputStream;
@synthesize inputNameField, inputMessageField;
@synthesize tView, messages;



- (void)viewDidLoad {
    [super viewDidLoad];
		
	[self initNetworkCommunication];
	
	inputNameField.text = @"";
	messages = [[NSMutableArray alloc] init];
	
	self.tView.delegate = self;
	self.tView.dataSource = self;
	
}

- (void) initNetworkCommunication {
	
	CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
	CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"nomads.music.virginia.edu", 52912, &readStream, &writeStream);
	
	inputStream = (NSInputStream *)readStream;
	outputStream = (NSOutputStream *)writeStream;
	[inputStream setDelegate:self];
	[outputStream setDelegate:self];
	[inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[inputStream open];
	[outputStream open];
	
}

//USING our convertToJavaUTF8 code :)
- (IBAction) joinChat {

	[self.view bringSubviewToFront:chatView]; //Enter the chat window
    int appID = 20; //Set AppID for communication with NOMADS

    
   // NSString *aString = [NSString stringWithFormat:@"%d",appID];   // Pack AppID
	NSString *response  = [NSString stringWithFormat:@"iam:%@", inputNameField.text]; //Pack Text

    NSData *aData = [self convertToJavaUTF8Int:&appID]; //Store AppID as NSData
    NSData *data = [self convertToJavaUTF8:response]; //Store text as NSData

    int aDataLength = [aData length]; //Get length of AppID
    int dataLength = [data length]; //Get text length
    
    //Write AppID as String to outputStream
//    int aNum = [outputStream write:(const uint8_t *)[aData bytes] maxLength:aDataLength];
    //Write text to outputStream
//    int num = [outputStream write:(const uint8_t *)[data bytes] maxLength:dataLength];
    
    //Check and make sure data was sent out properly
//	if (-1 == aNum) {
 //       NSLog(@"Error writing appID to stream %@: %@", outputStream, [outputStream streamError]);
  //  }
   // else {
   //     NSLog(@"Wrote %i bytes to stream %@.", num, outputStream);
   // }

//	if (-1 == num) {
//        NSLog(@"Error writing data to stream %@: %@", outputStream, [outputStream streamError]);
//    }
//    else {
//        NSLog(@"Wrote %i bytes to stream %@.", num, outputStream);
//    }
    
}

- (NSData*) convertToJavaUTF8Byte : (Byte*) appID 
    { 
   //     NSUInteger len = [appID lengthOfBytesUsingEncoding:NSUTF8StringEncoding]; 
   //     Byte buffer[2]; 
   //     buffer[0] = (0xff & (len >> 8)); 
   //     buffer[1] = (0xff & len); 
        NSMutableData *outData = [NSMutableData dataWithCapacity:1]; 
        [outData appendBytes:appID length:1];
        return outData;
    }

- (NSData*) convertToJavaUTF8Int : (int*) appID 
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


//USING our convertToJavaUTF8 code :)
- (IBAction)sendMessage
{
    
    // How to send the SAND header info (appID, command, dataType and dataLength)
    // NSData *sDatum;
    
    Byte appID = 40; 
    NSData *sDatum = [[NSData alloc] initWithBytes:&appID length:1];
     
    sDatum = [self convertToJavaUTF8Byte:&appID]; // appID
    [outputStream write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];

    Byte cmd = 1;
    sDatum = [self convertToJavaUTF8Byte:&cmd]; // command
    [outputStream write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];

    Byte dataType = 2;
    sDatum = [self convertToJavaUTF8Byte:&dataType]; // dataType
    [outputStream write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];

    int dataLen = 3;
    int dataLenBE = CFSwapInt32HostToBig(dataLen);   // dataLength
    [outputStream write:(uint8_t *)&dataLenBE maxLength:4];

    // If the data is an array of ints, send like this
    NSUInteger nInt[3];
    nInt[0] = CFSwapInt32HostToBig(5);
    nInt[1] = CFSwapInt32HostToBig(7);
    nInt[2] = CFSwapInt32HostToBig(9);
    NSData *intData = [NSData dataWithBytes:&nInt length:sizeof(nInt)];
    [outputStream write:(const uint8_t *)[intData bytes] maxLength: [intData length]];
                  

    // Header (again)
    appID = 40; 
    sDatum = [[NSData alloc] initWithBytes:&appID length:1];
    
    sDatum = [self convertToJavaUTF8Byte:&appID]; // appID
    [outputStream write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];
    
    cmd = 1;
    sDatum = [self convertToJavaUTF8Byte:&cmd]; // command
    [outputStream write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];
    
    dataType = 1;
    sDatum = [self convertToJavaUTF8Byte:&dataType]; // dataType
    [outputStream write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];

    dataLen = [inputMessageField.text length];
    dataLenBE = CFSwapInt32HostToBig(dataLen);   // dataLength
    [outputStream write:(uint8_t *)&dataLenBE maxLength:4];
    
    // If the data is a string or array of bytes, send like this
    NSData *sData = [inputMessageField.text dataUsingEncoding:NSUTF8StringEncoding];  // String
    [outputStream write:(const uint8_t *)[sData bytes] maxLength: [sData length]];

   
    inputMessageField.text = @"";
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
						
						if (nil != output) {

                            sLen = [output length];
                                
                            [output getCharacters:data range:NSMakeRange(0, sLen)];

                            const char *data2 = [output UTF8String];
                            char *cpy = calloc([output length]+1, 1);
                            strncpy(cpy, data2+2, [output length]-2);
                            // Do stuff with cpy
                            
                            appID = (int) data[0];
                            
                            for (int i=0;i<sLen+1;i++) {
                                NSLog(@"data[%d]: %c\n", i,data[i]);

                            }

                            for (int i=0;i<sLen;i++) {
                                NSLog(@"data2[%d]: %c\n", i,data2[i]);
                                
                           }
                            
                             
                            NSMutableString *outData = [[NSMutableString alloc] initWithBytes:data length:len encoding:NSUTF8StringEncoding];
                            
							
							NSLog(@"server(%d) appID: %d data: %@", sLen,appID,outData);
							NSLog(@"char: %c", appID);
                            NSLog(@"string: %s",(char *)data2);
                            printf("data2 = %s\n",data2);    
							NSLog(@"raw data: %@", outData);
                            NSMutableString *string2 = [NSMutableString stringWithString:@"This string is mutable"];
                            NSLog(@"raw data: %@", string2);

                            printf("%s\n", "Wahahaha");
                            printf("data %s\n", data);
                            printf("data2 %s\n", data2);
                            printf("copy %s\n",cpy);
                            
                            [self messageReceived:output];

                            
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
            [theStream release];
            theStream = nil;
			
			break;
		default:
			NSLog(@"Unknown event");
	}
		
}

- (void) messageReceived:(NSString *)message {
	
	[self.messages addObject:message];
	[self.tView reloadData];
	NSIndexPath *topIndexPath = [NSIndexPath indexPathForRow:messages.count-1 
												   inSection:0];
	[self.tView scrollToRowAtIndexPath:topIndexPath 
					  atScrollPosition:UITableViewScrollPositionMiddle 
							  animated:YES];

}

#pragma mark -
#pragma mark Table delegates

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	NSString *s = (NSString *) [messages objectAtIndex:indexPath.row];
	
    static NSString *CellIdentifier = @"ChatCellIdentifier";
    
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
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



- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {

	[joinView release];
	[chatView release];
	[inputStream release];
	[outputStream release];
	[inputNameField release];
	[inputMessageField release];
	[tView release];
    [super dealloc];
	
}


@end
