//
//  NSand.m
//  DiscussCloudPoll
//
//  Initial version, Steven Kemper on 6/22/12.
//

#import "NSand.h"
#import "NGrain.h"
#import "NGlobals.h"

@implementation NSand

@synthesize streamIn, streamOut;
@synthesize grain;

// Modified accessor functions so we can be specific about delegate numbers ================

- (id<SandDelegate>) delegate:(int) delNum
{
    if (delNum > MAX_DELEGATES) {
        NSLog(@"ERROR AppSand:delegate: only MAX_DELEGATES allow SandDelegates\n");
        exit(1);
    }
    return delegate[delNum];
}

- (void) setDelegate: (id<SandDelegate>) inDelegate
{
    delegate[numDelegates] = inDelegate;
    numDelegates++;    
    if (numDelegates > MAX_DELEGATES) {
        NSLog(@"ERROR AppSand:setDelegate: only MAX_DELEGATES allowed SandDelegates\n");
        exit(1);
    }
}

//initialization function ===================================================================

- (id)init 
{
    self = [super init]; //Here to get initialization from parent class first
    if (self) { //if we get that initialization, override it
        serverName = @"nomads.music.virginia.edu";
        
        // SERVER_PORT_PT for cocos2d testing; change to SERVER_PORT eventually
        serverPort = SERVER_PORT_PT; 
        numDelegates = 0;
    }
    return self;
}

- (void) close
{
    
}

// Connect ===================================================================================

- (void)connect
{
    CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
	CFStreamCreatePairWithSocketToHost(NULL, (__bridge CFStringRef)serverName, serverPort, &readStream, &writeStream);
	streamIn = (__bridge NSInputStream *)readStream;
	streamOut = (__bridge NSOutputStream *)writeStream;
	[streamIn setDelegate:self];
	[streamOut setDelegate:self];
	[streamIn scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[streamOut scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[streamIn open];
	[streamOut open];
    NSLog(@"NSand: Connecting, Streams Open ");
    [self sendWithGrainElts_AppID:SOUND_SWARM Command:SEND_MESSAGE DataType:BYTE DataLen:1 String:@"a"];
}

// sendWithGrain_AppID (for strings) =========================================================


- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c
                        DataType:(Byte)dT
                         DataLen:(int)dL
                          String:(NSString *)str;
{
    NSData *appIDDatum, *cmdDatum, *dTypeDatum, *dDatum;
    
    //Byte appID 
    appIDDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[appIDDatum bytes] maxLength: [appIDDatum length]];
    if (-1 == aNum) {
        NSLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        NSLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
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
    dDatum = [str dataUsingEncoding:NSUTF8StringEncoding];  // String
    int dArrayNum = [streamOut write:(const uint8_t *)[dDatum bytes] maxLength: [dDatum length]];
    if (-1 == dArrayNum) {
        NSLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
    }
}

// sendWithGrain_AppID (for Ints) ====================================================


- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c
                        DataType:(Byte)dT
                         DataLen:(int)dL
                         Integer:(int *)myInt;
{
    NSData *appIDDatum, *cmdDatum, *dTypeDatum, *dDatum;
    
    NSLog(@"foo got i:  %d\n",a);
    
    //Byte appID 
    appIDDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[appIDDatum bytes] maxLength: [appIDDatum length]];
    if (-1 == aNum) {
        NSLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        NSLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        NSLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
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
    for (int i=0;i<dL;i++) {
        int intDataBE = CFSwapInt32HostToBig(myInt[i]); // dataLength
        int dArrayNum = [streamOut write:(uint8_t *)&intDataBE maxLength:4];
        if (-1 == dArrayNum) {
            NSLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
        }
        else {
            NSLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
        }
    }
    
    
}

//   stream function ==========================================================================

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
	NSLog(@"stream event %i", streamEvent);
	
	switch (streamEvent) {
			
		case NSStreamEventOpenCompleted:
			NSLog(@"Stream opened");
			break;
		case NSStreamEventHasBytesAvailable:
            if (theStream == streamIn) {
				
                Byte appID,aBuf[1];
                Byte cmd,cBuf[1];
                Byte dType,dTBuf[1];
                int dLen,dLBuf[1];
                
                unsigned int len = 0;
				
                // TODO : change this to read into a buffer and then parse out appID, cmd, etc...
                
                //This while statement seems redundant 
				if ([streamIn hasBytesAvailable]) {
                    
                    len = [streamIn read:aBuf maxLength:sizeof(aBuf)];
                    if (len > 0) {
                        appID = aBuf[0];
                    }
                    len = [streamIn read:cBuf maxLength:sizeof(cBuf)];
                    if (len > 0) {
                        cmd = cBuf[0];
                    }
                    len = [streamIn read:dTBuf maxLength:sizeof(dTBuf)];
                    if (len > 0) {
                        dType = dTBuf[0];
                    }
                    len = [streamIn read:(uint8_t *)dLBuf maxLength:sizeof(dLBuf)];  // DT:  typecast may be bad
                    if (len > 0) {
                        dLen = CFSwapInt32BigToHost(dLBuf[0]);
                        NSLog(@"NSAND:  dataLen = %d\n",dLen);
                    }
                    
                    // read in our string
                    
                    if (dType == BYTE) {
                        uint8_t *sBuffer = (uint8_t *)malloc(sizeof(uint8_t)*dLen); 
                        len = [streamIn read:(uint8_t *)sBuffer maxLength:sizeof(sBuffer)];
                        NSLog(@"NSAND:  string buf read = %d\n",len);
                        
                        NSMutableString *output = [[NSMutableString alloc] initWithBytes:sBuffer length:len encoding:NSUTF8StringEncoding];
                        
                        if  (len < dLen){
                            while ([streamIn hasBytesAvailable]) {
                                len = [streamIn read:(uint8_t *)sBuffer maxLength:sizeof(len)];
                                NSLog(@"read %d more\n",len);
                                NSMutableString *leftOver = [[NSMutableString alloc] initWithBytes:sBuffer length:len encoding:NSUTF8StringEncoding];
                                NSLog(@"output = %@\n",output);
                                NSLog(@"leftOver = %@\n",leftOver);
                                [output appendString:leftOver];
                                
                            }
                        } 
                        
                        
                        grain = [[NGrain alloc] init];
                        
                        [grain setGrainElts_AppID:appID 
                                          Command:(Byte)cmd
                                         DataType:(Byte)dType 
                                          DataLen:(int)dLen
                                           String:(NSString *)output];
                        
                        [grain print];
                        for (int x=0;x<numDelegates;x++) {
                            if (self->delegate[x] != nil) {
                                [self->delegate[x] dataReadyHandle:grain];
                            }
                        }
                    }
                    
                    if (dType == INT) {
                        int x=0; // int / byte counter for our iBuffer
                        
                        // just read one int here
                        int *iBuffer = (int *)malloc(sizeof(int)*dLen);
                        int tempI;
                        
                        while ([streamIn hasBytesAvailable] && (x < dLen)) {
                            len = [streamIn read:(uint8_t *)&tempI maxLength:sizeof(int)];
                            if ((len > 0) && (x < dLen)) {
                                iBuffer[x] = CFSwapInt32BigToHost(tempI);
                            }
                            NSLog(@"SAND:  read %d more bytes\n",len);
                            NSLog(@"SAND:  INT [%d] = %d\n",x,iBuffer[x]);
                            x++;
                        }
                        
                        
                        
                        
                        
                        
                        
                        grain = [[NGrain alloc] init];
                        
                        
                        [grain setGrainElts_AppID:appID 
                                          Command:(Byte)cmd
                                         DataType:(Byte)dType 
                                          DataLen:(int)dLen
                                          Integer:(int *)iBuffer];
                        
                        [grain print];
                        for (int x=0;x<numDelegates;x++) {
                            if (self->delegate[x] != nil) {
                                [self->delegate[x] dataReadyHandle:grain];
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

// not used (yet?) =========================================================================

- (NGrain *) getGrain 
{
    NSLog(@"NSand: getGrain: (NGrain) ");
}


- (NGrain *) getGrainElts_AppID:(Byte)appID 
{
    
}


- (NSData*) convertToByteToStreamOut : (Byte*) myByte 
{ 
    NSMutableData *outData = [NSMutableData dataWithCapacity:1]; 
    [outData appendBytes:myByte length:1];
    return outData;
}



@end
