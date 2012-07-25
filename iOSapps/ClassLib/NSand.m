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
        LLog(@"ERROR AppSand:delegate: only MAX_DELEGATES allow SandDelegates\n");
        exit(1);
    }
    return delegate[delNum];
}

- (void) setDelegate: (id<SandDelegate>) inDelegate
{
    delegate[numDelegates] = inDelegate;
    numDelegates++;    
    if (numDelegates > MAX_DELEGATES) {
        LLog(@"ERROR AppSand:setDelegate: only MAX_DELEGATES allowed SandDelegates\n");
        exit(1);
    }
}

//initialization function ===================================================================

- (id)init 
{
    self = [super init]; //Here to get initialization from parent class first
    if (self) { //if we get that initialization, override it
        serverName = @"nomads.music.virginia.edu";
        serverPort = SERVER_PORT_DT; 
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
    LLog(@"NSand: Connecting, Streams Open ");
    // [self sendWithGrainElts_AppID:SOUND_SWARM Command:SEND_MESSAGE DataType:CHAR DataLen:1 String:@"a"];
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
        LLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        LLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
    if (-1 == dTypeNum) {
        LLog(@"NSand: Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dTypeNum, streamOut);
    }
    
    //    dataLen = [inputMessageField.text length];
    int dataLenBE = CFSwapInt32HostToBig(dL);   // dataLength
    int dLenNum = [streamOut write:(uint8_t *)&dataLenBE maxLength:4];
    if (-1 == dLenNum) {
        LLog(@"NSand: Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dLenNum, streamOut);
    }
    
    // If the data is a string or array of bytes, send like this
    dDatum = [str dataUsingEncoding:NSUTF8StringEncoding];  // String
    int dArrayNum = [streamOut write:(const uint8_t *)[dDatum bytes] maxLength: [dDatum length]];
    if (-1 == dArrayNum) {
        LLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
    }
}


- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c
                        DataType:(Byte)dT
                         DataLen:(int)dL
                          Uint8: (Byte *)myUint8s;
{
    NSData *appIDDatum, *cmdDatum, *dTypeDatum, *dDatum;
    
    //Byte appID 
    appIDDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[appIDDatum bytes] maxLength: [appIDDatum length]];
    if (-1 == aNum) {
        LLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        LLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
    if (-1 == dTypeNum) {
        LLog(@"NSand: Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dTypeNum, streamOut);
    }
    
    //    dataLen = [inputMessageField.text length];
    int dataLenBE = CFSwapInt32HostToBig(dL);   // dataLength
    int dLenNum = [streamOut write:(uint8_t *)&dataLenBE maxLength:4];
    if (-1 == dLenNum) {
        LLog(@"NSand: Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dLenNum, streamOut);
    }
    
    // If the data is a string or array of bytes, send like this
    for (int i=0;i<dL;i++) {
        Byte myByte = myUint8s[i]; // dataLength
        int dArrayNum = [streamOut write:(uint8_t *)&myByte maxLength:1];
        if (-1 == dArrayNum) {
            LLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
        }
        else {
            LLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
        }
    }
    
}

- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c
                        DataType:(Byte)dT
                         DataLen:(int)dL
                           Char: (Byte *)myChars;
{
    NSData *appIDDatum, *cmdDatum, *dTypeDatum, *dDatum;
    
    //Byte appID 
    appIDDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[appIDDatum bytes] maxLength: [appIDDatum length]];
    if (-1 == aNum) {
        LLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        LLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
    if (-1 == dTypeNum) {
        LLog(@"NSand: Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dTypeNum, streamOut);
    }
    
    //    dataLen = [inputMessageField.text length];
    int dataLenBE = CFSwapInt32HostToBig(dL);   // dataLength
    int dLenNum = [streamOut write:(uint8_t *)&dataLenBE maxLength:4];
    if (-1 == dLenNum) {
        LLog(@"NSand: Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dLenNum, streamOut);
    }
    
    // If the data is a string or array of bytes, send like this
    for (int i=0;i<dL;i++) {
        Byte myByte = myChars[i]; // dataLength
        int dArrayNum = [streamOut write:(uint8_t *)&myByte maxLength:1];
        if (-1 == dArrayNum) {
            LLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
        }
        else {
            LLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
        }
    }
}


// sendWithGrain_AppID (for Ints) ====================================================


- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c
                        DataType:(Byte)dT
                         DataLen:(int)dL
                         Int32:(int *)myInt32s;
{
    NSData *appIDDatum, *cmdDatum, *dTypeDatum, *dDatum;
    
    LLog(@"foo got i:  %d\n",a);
    
    //Byte appID 
    appIDDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[appIDDatum bytes] maxLength: [appIDDatum length]];
    if (-1 == aNum) {
        LLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        LLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
    if (-1 == dTypeNum) {
        LLog(@"NSand: Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dTypeNum, streamOut);
    }
    
    //    dataLen = [inputMessageField.text length];
    int dataLenBE = CFSwapInt32HostToBig(dL);   // dataLength
    int dLenNum = [streamOut write:(uint8_t *)&dataLenBE maxLength:4];
    if (-1 == dLenNum) {
        LLog(@"NSand: Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dLenNum, streamOut);
    }
    
    // If the data is a string or array of bytes, send like this
    for (int i=0;i<dL;i++) {
        int int32BE = CFSwapInt32HostToBig(myInt32s[i]); // dataLength
        int dArrayNum = [streamOut write:(uint8_t *)&int32BE maxLength:4];
        if (-1 == dArrayNum) {
            LLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
        }
        else {
            LLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
        }
    }
    
    
}
- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c
                        DataType:(Byte)dT
                        Dat7aLen:(int)dL
                           Float32:(float *)myFloat32s;
{
    NSData *appIDDatum, *cmdDatum, *dTypeDatum, *dDatum;
    
    LLog(@"foo got i:  %d\n",a);
    
    //Byte appID 
    appIDDatum = [self convertToByteToStreamOut:&a]; // appID
    int aNum = [streamOut write:(const uint8_t *)[appIDDatum bytes] maxLength: [appIDDatum length]];
    if (-1 == aNum) {
        LLog(@"NSand: Error writing appID to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", aNum, streamOut);
    }
    
    //    Byte cmd = 1;
    cmdDatum = [self convertToByteToStreamOut:&c]; // command
    int cNum = [streamOut write:(const uint8_t *)[cmdDatum bytes] maxLength: [cmdDatum length]];
    if (-1 == cNum) {
        LLog(@"NSand: Error writing Command to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", cNum, streamOut);
    }
    
    //    Byte dataType = 1;
    dTypeDatum = [self convertToByteToStreamOut:&dT]; // dataType
    int dTypeNum = [streamOut write:(const uint8_t *)[dTypeDatum bytes] maxLength: [dTypeDatum length]];
    if (-1 == dTypeNum) {
        LLog(@"NSand: Error writing Data Type to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dTypeNum, streamOut);
    }
    
    //    dataLen = [inputMessageField.text length];
    int dataLenBE = CFSwapInt32HostToBig(dL);   // dataLength
    int dLenNum = [streamOut write:(uint8_t *)&dataLenBE maxLength:4];
    if (-1 == dLenNum) {
        LLog(@"NSand: Error writing Data Length to stream %@: %@", streamOut, [streamOut streamError]);
    }
    else {
        LLog(@"NSand: Wrote %i bytes to stream %@.", dLenNum, streamOut);
    }
    
    // If the data is a string or array of bytes, send like this
    for (int i=0;i<dL;i++) {
        float float32BE = CFSwapInt32HostToBig(myFloat32s[i]); // dataLength
        int dArrayNum = [streamOut write:(uint8_t *)&float32BE maxLength:4];
        if (-1 == dArrayNum) {
            LLog(@"NSand: Error writing Data Array to stream %@: %@", streamOut, [streamOut streamError]);
        }
        else {
            LLog(@"NSand: Wrote %i bytes to stream %@.", dArrayNum, streamOut);
        }
    }
    
    
}


//   stream function ==========================================================================

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
	LLog(@"stream event %i", streamEvent);
	
	switch (streamEvent) {
			
		case NSStreamEventOpenCompleted:
			LLog(@"NSAND: NSStreamEventOpenCompleted\n");
			break;
            
		case NSStreamEventHasBytesAvailable:
			LLog(@"NSAND: NSStreamHasBytesAvailable\n");

            if (theStream == streamIn) {
				
				
                // Some counters and logic vars for our buffer reading schema below
                
                Byte appID,cmd,dType;
                int dLen=0;
                int dLenT=0;
                
				Boolean doneReading = false;
                unsigned int len = 0;
                int readMode = 0;
                int cachedNextReadMode = 0;
                int readPtr = 0;
                int writePtr = 0;
                int remBufRead = 0;
                int remSandRead = 0;
                int bufSandDiff = 0;
                
                Byte readBuf[1024];
                uint8_t *cBuffer = NULL;   // char
                uint8_t *uBuffer = NULL;   // uint8
                int *iBuffer = NULL;       // int
                uint32_t tInt=0;
                float *fBuffer = NULL;     // float
                float tFloat=0;
                
                if ([streamIn hasBytesAvailable]) {
                    
                    while (doneReading == false) {
                        
                        switch (readMode) {
                                
                                // MODE 0:  read a new buffer from the network
                                
                            case 0:
                                len = [streamIn read:readBuf maxLength:sizeof(readBuf)];
                                LLog(@"NSAND:  streamIn read %d bytes\n",len);
                                remBufRead = len;
                                
                                readMode = 0;
                                readPtr = 0;
                                
                                // We didn't read in anything, so we must be done (DT:  may need to double check this)
                                if (len < 1) {
                                    doneReading = true;
                                }
                                
                                // We read in something, figure out what to do with it
                                else if (remBufRead > 0) {
                                    
                                    // It's a fresh new buffer, start from the top
                                    if (cachedNextReadMode == 0) {   
                                        readMode = 1;
                                    }
                                    // It's bytes left over from a previous SAND grain read, pick up where we left off
                                    else {
                                        readMode = cachedNextReadMode;  
                                    }
                                }
                                break;
                                
                                // MODE 1:  read the appId next 
                                
                            case 1:                              
                                appID = readBuf[readPtr++];
                                LLog(@"NSAND:  streamin read appID = %d\n",appID);
                                remBufRead--;
                                cachedNextReadMode = 2;    // store the next read mode
                                readMode = 0;        // if the following isn't true, it means we need to read in another buffer
                                if (remBufRead > 1) {   // if there's more in the readBuffer, continue to the next mode (cmd)
                                    readMode = 2;
                                }
                                break;
                                
                                // MODE 2:  read the cmd next
                                
                            case 2:                                                              
                                cmd = readBuf[readPtr++];
                                LLog(@"NSAND:  streamin read cmd = %d\n",cmd);
                                
                                remBufRead--;
                                cachedNextReadMode = 3;    // store the next read mode
                                readMode = 0;        // if the following isn't true, it means we need to read in another buffer
                                if (remBufRead > 1) {
                                    readMode = 3;
                                }
                                break;
                                
                                // MODE 3:  read the dType next
                                
                            case 3:
                                dType = readBuf[readPtr++];
                                LLog(@"NSAND:  streamin read dType = %d\n",dType);
                                
                                remBufRead--;
                                cachedNextReadMode = 4;    // store the next read mode
                                readMode = 0;        // if the following isn't true, it means we need to read in another buffer
                                if (remBufRead > 1) {
                                    readMode = 4;
                                }
                                break;
                                
                                // MODE 4:  read the dLen next
                                
                            case 4:                              
                                dLenT = (readBuf[readPtr++] << 24) & 0xFF;
                                dLenT += (readBuf[readPtr++] << 16) & 0xFF;
                                dLenT += (readBuf[readPtr++] << 8) & 0xFF;
                                dLenT += (readBuf[readPtr++]) & 0xFF;
                                
                                dLen = dLenT;  // was CFSwapInt32BigToHost(dLenT);
                                LLog(@"NSAND:  streamin read dLen = %d\n",dLen);
                                
                                remBufRead -= 4;
                                cachedNextReadMode = 5;    // store the current read mode
                                readMode = 0;        // if the following isn't true, it means we need to read in another buffer
                                if (remBufRead > 1) {
                                    readMode = 5;
                                }
                                break;
                                
                                // MODE 5:  read the main data
                                
                            case 5:                                                              
                                
                                // CHAR -----------------------------------------------------------------------------------------
                                
                                if (dType == CHAR) {
                                    
                                    // Allocate our byte buffer if we haven't already
                                    if (cBuffer == NULL) {
                                        cBuffer = (uint8_t *)malloc(sizeof(uint8_t)*dLen);   // allocate temp string buf
                                        writePtr = 0;
                                    }
                                    
                                    remSandRead = dLen * sizeof(Byte);
                                    bufSandDiff = remBufRead - remSandRead;
                                    
                                    LLog(@"NSAND:  reading CHARS\n");
                                    
                                    // There's more data to read but we didn't get it all in this network read buffer, 
                                    //   read in _remBufRead_ bytes
                                    if (bufSandDiff < 0) {
                                        LLog(@"NSAND:  bufSandDiff < 0\n");
                                        
                                        while (remBufRead > 0) {
                                            cBuffer[writePtr] = readBuf[readPtr];    // copy from network read buffer into temp string buff
                                            writePtr++;
                                            readPtr++;
                                            remSandRead--;
                                            remBufRead--;
                                        }
                                        
                                        cachedNextReadMode = readMode;
                                        readMode = 0;
                                    }
                                    
                                    // There's enough (or more than enough) data to read in this network read buffer
                                    //   read in _remSandRead_ bytes
                                    else if (bufSandDiff >= 0) {
                                        LLog(@"NSAND:  bufSandDiff >= 0\n");
                                        
                                        while (remSandRead > 0) {
                                            cBuffer[writePtr] = readBuf[readPtr];    // copy from network read buffer into temp string buff
                                            writePtr++;
                                            readPtr++;
                                            remSandRead--;
                                            remBufRead--;
                                        }
                                        
                                        // We've read in all our string data
                                        //    create NSMutableString from temp string buf
                                        NSMutableString *output = [[NSMutableString alloc] initWithBytes:cBuffer length:dLen encoding:NSUTF8StringEncoding];
                                        
                                        LLog(@"NSAND:  read CHARS = %@\n",output);
                                        
                                        grain = [[NGrain alloc] init];
                                        [grain setGrainElts_AppID:appID 
                                                          Command:(Byte)cmd
                                                         DataType:(Byte)dType 
                                                          DataLen:(int)dLen
                                                           String:(NSString *)output];
                                        [grain print];
                                        cBuffer = NULL;
                                        
                                        // See if there's more data in the buffer to read through
                                        bufSandDiff = remBufRead - remSandRead;
                                        
                                        // We've reached the end of the buffer, reset the read mode
                                        if (bufSandDiff == 0) {
                                            LLog(@"NSAND:  bufSandDiff == 0 ... done reading\n");
                                            doneReading = true;
                                            readMode = 0;
                                            cachedNextReadMode = 0;
                                        }
                                        
                                        // We've reached the end of our data for this SAND grain, but there's more in the buffer
                                        //     set read mode to appID and read in the rest
                                        else {
                                            readMode = 1;
                                        }
                                        
                                    }
                                }
                                
                                // UINT8 -----------------------------------------------------------------------------------------
                                
                                else if (dType == UINT8) {
                                    
                                    // Allocate our byte buffer if we haven't already
                                    if (uBuffer == NULL) {
                                        uBuffer = (uint8_t *)malloc(sizeof(uint8_t)*dLen);   // allocate temp uint buf
                                        writePtr = 0;
                                    }
                                    
                                    remSandRead = dLen * sizeof(Byte);
                                    bufSandDiff = remBufRead - remSandRead;
                                    
                                    LLog(@"NSAND:  reading UINT8s\n");
                                    
                                    // There's more data to read but we didn't get it all in this network read buffer, 
                                    //   read in _remBufRead_ bytes
                                    if (bufSandDiff < 0) {
                                        LLog(@"NSAND:  bufSandDiff < 0\n");
                                        
                                        while (remBufRead > 0) {
                                            uBuffer[writePtr] = readBuf[readPtr];    // copy from network read buffer into temp string buff
                                            writePtr++;
                                            readPtr++;
                                            remSandRead--;
                                            remBufRead--;
                                        }
                                        
                                        cachedNextReadMode = readMode;
                                        readMode = 0;
                                    }
                                    
                                    // There's enough (or more than enough) data to read in this network read buffer
                                    //   read in _remSandRead_ bytes
                                    else if (bufSandDiff >= 0) {
                                        LLog(@"NSAND:  bufSandDiff >= 0\n");
                                        
                                        while (remSandRead > 0) {
                                            uBuffer[writePtr] = readBuf[readPtr];    // copy from network read buffer into temp string buff
                                            writePtr++;
                                            readPtr++;
                                            remSandRead--;
                                            remBufRead--;
                                        }
                                        
                                        // We've read in all our uint8 data
                                        
                                        LLog(@"NSAND:  read UINT8s\n");
                                        
                                        grain = [[NGrain alloc] init];
                                        [grain setGrainElts_AppID:appID 
                                                          Command:(Byte)cmd
                                                         DataType:(Byte)dType 
                                                          DataLen:(int)dLen
                                                            Uint8:(uint8_t *)uBuffer];
                                        [grain print];
                                        uBuffer = NULL;
                                        
                                        // See if there's more data in the buffer to read through
                                        bufSandDiff = remBufRead - remSandRead;
                                        
                                        // We've reached the end of the buffer, reset the read mode
                                        if (bufSandDiff == 0) {
                                            LLog(@"NSAND:  bufSandDiff == 0 ... done reading\n");
                                            doneReading = true;
                                            readMode = 0;
                                            cachedNextReadMode = 0;
                                        }
                                        
                                        // We've reached the end of our data for this SAND grain, but there's more in the buffer
                                        //     set read mode to appID and read in the rest
                                        else {
                                            readMode = 1;
                                        }
                                        
                                    }
                                }
                                
                                // INT -----------------------------------------------------------------------------------------
                                
                                else if (dType == INT32) {
                                    
                                    // Allocate our int buffer if we haven't already
                                    if (iBuffer == NULL) {
                                        iBuffer = (int *)malloc(sizeof(int)*dLen);   // allocate temp int buf
                                        writePtr = 0;
                                    }
                                    
                                    remSandRead = dLen * sizeof(int);
                                    bufSandDiff = remBufRead - remSandRead;
                                    
                                    LLog(@"NSAND:  reading CHARS\n");
                                    
                                    // There's more data to read but we didn't get it all in this network read buffer, 
                                    //   read in _remBufRead_ bytes
                                    if (bufSandDiff < 0) {
                                        LLog(@"NSAND:  bufSandDiff < 0\n");
                                        
                                        while (remBufRead > 0) {
                                            
                                            tInt = (readBuf[readPtr++] << 24) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 16) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 8) & 0xFF;
                                            tInt += (readBuf[readPtr++]) & 0xFF;
                                            
                                            LLog(@"NSAND:  streamin read int = %d\n",tInt);
                                            
                                            iBuffer[writePtr] = tInt;    
                                            writePtr++;
                                            remSandRead -= 4;
                                            remBufRead -= 4;
                                        }
                                        
                                        cachedNextReadMode = readMode;
                                        readMode = 0;
                                    }
                                    
                                    // There's enough (or more than enough) data to read in this network read buffer
                                    //   read in _remSandRead_ bytes
                                    else if (bufSandDiff >= 0) {
                                        LLog(@"NSAND:  bufSandDiff >= 0\n");
                                        
                                        while (remSandRead > 0) {
                                            
                                            tInt = (readBuf[readPtr++] << 24) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 16) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 8) & 0xFF;
                                            tInt += (readBuf[readPtr++]) & 0xFF;
                                            
                                            LLog(@"NSAND:  streamin read int = %d\n",tInt);
                                            
                                            iBuffer[writePtr] = tInt;    
                                            writePtr++;
                                            remSandRead -= 4;
                                            remBufRead -= 4;
                                            
                                        }
                                        
                                        // We've read in all our char data
                                        
                                        grain = [[NGrain alloc] init];
                                        [grain setGrainElts_AppID:appID 
                                                          Command:(Byte)cmd
                                                         DataType:(Byte)dType 
                                                          DataLen:(int)dLen
                                                            Int32:(int *)iBuffer];
                                        [grain print];
                                        iBuffer = NULL;
                                        
                                        // See if there's more data in the buffer to read through
                                        bufSandDiff = remBufRead - remSandRead;
                                        
                                        // We've reached the end of the buffer, reset the read mode
                                        if (bufSandDiff == 0) {
                                            LLog(@"NSAND:  bufSandDiff == 0 ... done reading\n");
                                            doneReading = true;
                                            readMode = 0;
                                            cachedNextReadMode = 0;
                                        }
                                        
                                        // We've reached the end of our data for this SAND grain, but there's more in the buffer
                                        //     set read mode to appID and read in the rest
                                        else {
                                            readMode = 1;
                                        }
                                        
                                    }
                                }
                                
                                // FLOAT -----------------------------------------------------------------------------------------
                                
                                else if (dType == FLOAT32) {
                                    
                                    // Allocate our int buffer if we haven't already
                                    if (fBuffer == NULL) {
                                        fBuffer = (float *)malloc(sizeof(float)*dLen);   // allocate temp float buf
                                        writePtr = 0;
                                    }
                                    
                                    remSandRead = dLen * sizeof(int);
                                    bufSandDiff = remBufRead - remSandRead;
                                    
                                    LLog(@"NSAND:  reading CHARS\n");
                                    
                                    // There's more data to read but we didn't get it all in this network read buffer, 
                                    //   read in _remBufRead_ bytes
                                    if (bufSandDiff < 0) {
                                        LLog(@"NSAND:  bufSandDiff < 0\n");
                                        
                                        while (remBufRead > 0) {
                                            
                                            tInt = (readBuf[readPtr++] << 24) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 16) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 8) & 0xFF;
                                            tInt += (readBuf[readPtr++]) & 0xFF;
                                            
                                            LLog(@"NSAND:  streamin read int = %d\n",tInt);
                                            
                                            fBuffer[writePtr] = (float)tInt;    
                                            writePtr++;
                                            remSandRead -= 4;
                                            remBufRead -= 4;
                                        }
                                        
                                        cachedNextReadMode = readMode;
                                        readMode = 0;
                                    }
                                    
                                    // There's enough (or more than enough) data to read in this network read buffer
                                    //   read in _remSandRead_ bytes
                                    else if (bufSandDiff >= 0) {
                                        LLog(@"NSAND:  bufSandDiff >= 0\n");
                                        
                                        while (remSandRead > 0) {
                                            
                                            tInt = (readBuf[readPtr++] << 24) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 16) & 0xFF;
                                            tInt += (readBuf[readPtr++] << 8) & 0xFF;
                                            tInt += (readBuf[readPtr++]) & 0xFF;
                                            
                                            LLog(@"NSAND:  streamin read int = %d\n",tInt);
                                            
                                            fBuffer[writePtr] = tInt;    
                                            writePtr++;
                                            remSandRead -= 4;
                                            remBufRead -= 4;
                                            
                                        }
                                        
                                        // We've read in all our char data
                                        
                                        grain = [[NGrain alloc] init];
                                        [grain setGrainElts_AppID:appID 
                                                          Command:(Byte)cmd
                                                         DataType:(Byte)dType 
                                                          DataLen:(int)dLen
                                                          Float32:(int *)fBuffer];
                                        [grain print];
                                        fBuffer = NULL;
                                        
                                        // See if there's more data in the buffer to read through
                                        bufSandDiff = remBufRead - remSandRead;
                                        
                                        // We've reached the end of the buffer, reset the read mode
                                        if (bufSandDiff == 0) {
                                            LLog(@"NSAND:  bufSandDiff == 0 ... done reading\n");
                                            doneReading = true;
                                            readMode = 0;
                                            cachedNextReadMode = 0;
                                        }
                                        
                                        // We've reached the end of our data for this SAND grain, but there's more in the buffer
                                        //     set read mode to appID and read in the rest
                                        else {
                                            readMode = 1;
                                        }
                                        
                                    }
                                }
                                
                                break;

                            default:
                                LLog(@"NSAND read WARNING - readMode:  %d\n",readMode);
                                break;
                                
                        }     // switch (readMode)
                        
                    }  // while (doneReading == false)

                    //  Send out notifications to all our delegates =============================================================
                    
                    for (int x=0;x<numDelegates;x++) {
                        if (self->delegate[x] != nil) {
                            [self->delegate[x] dataReadyHandle:grain];
                        }
                    }

                } // if [streamIn hasBytesAvailable]
            } 
            break;

        case NSStreamEventErrorOccurred:
            LLog(@"NSAND : ERROR - NSStreamEventError\n");
            break;
            
        case NSStreamEventEndEncountered:
            LLog(@"NSAND : NSStreamEventEndEncountered\n");
            
            [theStream close];
            [theStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
            //    [theStream release];
            theStream = nil;
            break;

        case NSStreamEventHasSpaceAvailable:
            LLog(@"NSAND : NSStreamEventHasSpaceAvailable\n");
            break;
            
        default:
            LLog(@"NSand WARNING - Unknown stream event");
            break;
    }

}


- (NSData*) convertToByteToStreamOut : (Byte*) myByte 
{ 
    NSMutableData *outData = [NSMutableData dataWithCapacity:1]; 
    [outData appendBytes:myByte length:1];
    return outData;
}



@end
