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
      serverDebugLevel = ON;
      clientDebugLevel = ON;
      libraryDebugLevel = ON;
  }
  return self;
}

@end
