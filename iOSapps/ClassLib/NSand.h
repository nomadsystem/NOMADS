//
//  NSand.h
//  DiscussCloudPoll
//
//  Initial version, Steven Kemper on 6/22/12.
//
#ifndef NSAND_H
#define NSAND_H


#import <Foundation/Foundation.h>
#import "NGrain.h"

@protocol SandDelegate <NSObject>
@required
- (void) dataReadyHandle:(NGrain *)grain;  // OUTPUT:  function we'll call from our delegate
@optional
- (void) networkConnectionError:(NSString *)ErrStr;  // NETWORK ERRORS
@end

@interface NSand : NSObject <NSStreamDelegate>
{
    NSInputStream	*streamIn;
	NSOutputStream	*streamOut;   
    NSString    *serverName; //= @"nomads.music.virginia.edu";
    int       serverPort; //52911; //DT's server port
    id <SandDelegate> delegate[10];  // OUTPUT:  where we send our signal
    NGrain *grain;  
    int numDelegates;
    @public Boolean sandErrorFlag;
}

@property (nonatomic, retain) NSInputStream *streamIn;
@property (nonatomic, retain) NSOutputStream *streamOut;
@property (retain) NGrain *grain;

// Modified accessor functions so we can be specific about delegate numbers =======================
// NB:  we don't want to use @synthesize for these any more

- (void) setDelegate: (id<SandDelegate>) inDelegate;
- (id<SandDelegate>) delegate:(int) delNum;

// - (void) sendWithGrain:(NGrain *) myGrain;

- (void) setPort:(int)port;

// Network data sending functions =================================================================

- (void) sendWithGrainElts_AppID:(Byte)a
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL
                          String:(NSString*)str;


- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL
                         Uint8:(Byte *)myUint8s;

- (void) sendWithGrainElts_AppID:(Byte)a
                         Command:(Byte)c
                        DataType:(Byte)dT
                         DataLen:(int)dL
                            Char: (Byte *)myChars;

- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL
                          Int32:(int *)myInt32s;

- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL
                         Float32:(float *)myFloat32s;


//- (NGrain) getGrain;

//- (NGrain) getGrainElts_AppID: (Byte)appID;

- (void) connect;

@end

#endif
