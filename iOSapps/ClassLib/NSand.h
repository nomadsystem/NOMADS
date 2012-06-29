//
//  NSand.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NGrain.h"

@protocol SandDelegate <NSObject>
@required
- (void) dataReadyHandle:(NGrain *)grain;  // OUTPUT:  function we'll call from our delegate
@end

@interface NSand : NSObject <NSStreamDelegate>
{
    NSInputStream	*streamIn;
	NSOutputStream	*streamOut;   
    NSString    *serverName; //= @"nomads.music.virginia.edu";
    int       serverPort; //52911; //DT's server port
    id <SandDelegate> delegate;  // OUTPUT:  where we send our signal
    NGrain *grain;
}

@property (nonatomic, retain) NSInputStream *streamIn;
@property (nonatomic, retain) NSOutputStream *streamOut;
@property (retain) id delegate;  // OUTPUt:  where we send our signal
@property (retain) NGrain *grain;

// - (void) sendWithGrain:(NGrain *) myGrain;

- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL
                          String:(NSString*)str;

- (void) sendWithGrainElts_AppID:(Byte)a 
                         Command:(Byte)c 
                        DataType:(Byte)dT 
                         DataLen:(int)dL
                          Number:(NSNumber*)num;

//- (NGrain) getGrain;

//- (NGrain) getGrainElts_AppID: (Byte)appID;

- (void) connect;
- (void) close;

@end

