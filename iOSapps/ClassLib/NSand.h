//
//  NSand.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSand : NSObject <NSStreamDelegate>
{
    NSInputStream	*streamIn;
	NSOutputStream	*streamOut;   
    NSString    *serverName; //= @"nomads.music.virginia.edu";
    int       serverPort; //52911; //DT's server port
}

@property (nonatomic, retain) NSInputStream *streamIn;
@property (nonatomic, retain) NSOutputStream *streamOut;

//- (void) sendWithGrain:(NGrain *) myGrain;

//- (void) sendWithGrainElts_AppID:(Byte)a 
//                 Command:(Byte)c 
//                DataType:(Byte)dT 
//                         DataLen:(int)dL; 
//             //  DataString:(NSString *)str;

//- (NGrain) getGrain;

//- (NGrain) getGrainElts_AppID: (Byte)appID;

- (void) connect;

- (void) close;
- (void) fooWith_AppID: (Byte) i Command:(Byte)c DataType:(Byte)dT DataLen:(int)dL DataString:(NSString *)str;

@end
