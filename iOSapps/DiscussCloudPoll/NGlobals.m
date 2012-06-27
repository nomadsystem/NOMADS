//
//  NGlobals.m
//
//  Initial version, DJT on 6/22/12.
//

#import "NGlobals.h"

@implementation NGlobals
- (id)init
{
  self = [super init];
  if (self) {
    clientDebugLevel = 1;  // Use this for printout info
    serverDebugLevel = 1;  // Use this for printout info
    libraryDebugLevel = 1;  // Use this for printout info
    serverName = @"nomads.music.virginia.edu";
    serverPort = 52910;
    serverPortDT = 52911;
    serverPortSK = 52912;
    serverPortPT = 52913;
    serverPortMB = 52914;
  }
  return self;
}

@end
