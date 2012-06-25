//
//  NSand.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"

@implementation NSand

@synthesize streamIn, streamOut;

- (id)init //initialization function
{
    self = [super init]; //Here to get initialization from parent class first
    if (self) { //if we get that initialization, override it
        NGlobals *myGlobals;
       // serverPort = myGlobals->serverPortSK; 
        serverPort = 52912;
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
	CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"nomads.music.virginia.edu", 52912, &readStream, &writeStream);
	
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

- (void) sendWithGrain:(NGrain *) myGrain;
{
    NSLog(@"NSand: sendGrain: (NGrain) ");
    int aNum = [streamOut write: &(myGrain->appID) maxLength:1];
    
    //    NSData *command_Data = c; //Store Command as NSData
    int cNum = [streamOut write: &(myGrain->command) maxLength:1];
    
    //    NSData *dType_Data = dT; //Store DataType as NSData
    int dTypeNum = [streamOut write: &(myGrain->dataType) maxLength:1];
    
    //   NSData *dLen_Data = dL; //Store DataLength as NSData
    int dLenNum = [streamOut write: myGrain->dataLen maxLength:1]; //STK Syntax?
    
    int dArrayNum = [streamOut write: [myGrain->dataArray bytes] maxLength:myGrain->dataLen];
    
    
    //Check and make sure data was sent out properly
	if (-1 == aNum) {
        NSLog(@"Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == cNum) {
        NSLog(@"Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == dTypeNum) {
        NSLog(@"Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == dLenNum) {
        NSLog(@"Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == dArrayNum) {
        NSLog(@"Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"Wrote %i bytes to stream %@.", dArrayNum, streamOut);
    }
    
    
	
}

- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL 
                       DataArray:(NSData *)dA;
{
    NSLog(@"NSand: sendWithGrainElts:  ");
    //Write AppID as String to outputStream
    
 //   NSData *appID_Data = a; //Store AppID as NSData--Do I need this?? STK
  //  int appID_Length = [appID_Data length]; //Don't need since we know it's 1 byte
    int aNum = [streamOut write: &a maxLength:1];
    
//    NSData *command_Data = c; //Store Command as NSData
    int cNum = [streamOut write: &c maxLength:1];
    
//    NSData *dType_Data = dT; //Store DataType as NSData
    int dTypeNum = [streamOut write: &dT maxLength:1];
    
 //   NSData *dLen_Data = dL; //Store DataLength as NSData
    int dLenNum = [streamOut write: dL maxLength:1];
    
    int dArrayNum = [streamOut write: [dA bytes] maxLength:dL];
    
    
    //Check and make sure data was sent out properly
	if (-1 == aNum) {
        NSLog(@"Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == cNum) {
        NSLog(@"Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == dTypeNum) {
        NSLog(@"Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == dLenNum) {
        NSLog(@"Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else if (-1 == dArrayNum) {
        NSLog(@"Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"Wrote %i bytes to stream %@.", dArrayNum, streamOut);
    }
}



@end
