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

- (void) sendWithGrain: (NGrain) myGrain;

@end
