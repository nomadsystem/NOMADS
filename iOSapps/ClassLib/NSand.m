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

- (id)init //initialization function
{
    self = [super init]; //Here to get initialization from parent class first
    if (self) { //if we get that initialization, override it
        serverName = @"nomads.music.virginia.edu";
        serverPort = 52911; //DT's server port
    }
    
    return self;
}

@end
