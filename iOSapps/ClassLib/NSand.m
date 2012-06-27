//
//  NSand.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NSand.h"
#import "NGrain.h"

@implementation NSand

@synthesize streamIn, streamOut;

- (id)init //initialization function
{
    self = [super init]; //Here to get initialization from parent class first
    if (self) { //if we get that initialization, override it
        serverName = @"nomads.music.virginia.edu";
        serverPort = 52912; //DT's server port
    }
    
    return self;
}

- (void) close
{
    
}

- (void)connect
{
    CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
	CFStreamCreatePairWithSocketToHost(NULL, (__bridge CFStringRef)@"nomads.music.virginia.edu", 52912, &readStream, &writeStream);
	
	streamIn = (__bridge NSInputStream *)readStream;
	streamOut = (__bridge NSOutputStream *)writeStream;
	[streamIn setDelegate:self];
	[streamOut setDelegate:self];
	[streamIn scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[streamOut scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[streamIn open];
	[streamOut open];
    NSLog(@"NSand: Connecting, Streams Open ");
}

- (NGrain *) getGrain 
{
    NSLog(@"NSand: getGrain: (NGrain) ");
}

- (NGrain *) getGrainElts_AppID: (Byte)appID
{
    
}

//- (void) sendWithGrain:(NGrain *) myGrain;
//{
//    NSLog(@"NSand: sendGrain: (NGrain) ");
//    int aNum = [streamOut write: &(myGrain->appID) maxLength:1];
//    
//    //    NSData *command_Data = c; //Store Command as NSData
//    int cNum = [streamOut write: &(myGrain->command) maxLength:1];
//    
//    //    NSData *dType_Data = dT; //Store DataType as NSData
//    int dTypeNum = [streamOut write: &(myGrain->dataType) maxLength:1];
//    
//    //   NSData *dLen_Data = dL; //Store DataLength as NSData
//    int dLenNum = [streamOut write: myGrain->dataLen maxLength:1]; //STK Syntax?
//    
//    int dArrayNum = [streamOut write: [myGrain->dataArray bytes] maxLength:myGrain->dataLen];
//    
//    
//    //Check and make sure data was sent out properly
//	if (-1 == aNum) {
//        NSLog(@"Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
//    }
//    else if (-1 == cNum) {
//        NSLog(@"Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
//    }
//    else if (-1 == dTypeNum) {
//        NSLog(@"Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
//    }
//    else if (-1 == dLenNum) {
//        NSLog(@"Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
//    }
//    else if (-1 == dArrayNum) {
//        NSLog(@"Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
//    }
//    else {
//        NSLog(@"Wrote %i bytes to stream %@.", dArrayNum, streamOut);
//    }
//    
//    
//	
//}

- (NSData*) convertToByteToStreamOut : (Byte*) myByte 
{ 
    NSMutableData *outData = [NSMutableData dataWithCapacity:1]; 
    [outData appendBytes:myByte length:1];
    return outData;
}
//Currently only Byte Array
- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL 
                       ByteArray:(NSData *)dA;
{
    NSLog(@"NSand: sendWithGrainElts:  ");
    //Write AppID as String to outputStream
    
    //   Byte appID = 40; 
    NSData *sDatum = [[NSData alloc] initWithBytes:&a length:1];
    sDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];
    if (-1 == aNum) {
        NSLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    sDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];
    if (-1 == cNum) {
        NSLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    sDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[sDatum bytes] maxLength: [sDatum length]];
    if (-1 == dTypeNum) {
        NSLog(@"NSand: Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", dTypeNum, streamOut);
    }
    
    //    dataLen = [inputMessageField.text length];
    int dataLenBE = CFSwapInt32HostToBig(dL);   // dataLength
    int dLenNum = [streamOut write:(uint8_t *)&dataLenBE maxLength:4];
    if (-1 == dLenNum) {
        NSLog(@"NSand: Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", dLenNum, streamOut);
    }
    
    // If the data is a string or array of bytes, send like this
    //   NSData *sData = [inputMessageField.text dataUsingEncoding:NSUTF8StringEncoding];  // String
    int dArrayNum = [streamOut write:(const uint8_t *)[dA bytes] maxLength: [dA length]];
    if (-1 == dArrayNum) {
        NSLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
    }
    
    //Check and make sure data was sent out properly
	
 
 
   
    
    
}



@end
