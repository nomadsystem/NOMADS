//
//  NDataType.m
//
//  Initial version, DJT on 6/22/12.
//

#import "NDataType.h"

@implementation NDataType
- (id)init
{
  self = [super init];
  if (self) {
     FOO = 0;
     BYTE = 1;
     INT = 2;
     FLOAT = 3;
     DOUBLE = 4;
     NO_DATA = 5;
            
            }
  return self;
}

@end
