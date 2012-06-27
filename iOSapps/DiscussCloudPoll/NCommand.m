//
//  NCommand.m
//
//  Initial version, DJT on 6/22/12.
//

#import "NCommand.h"

@implementation NCommand
- (id)init
{
  self = [super init];
  if (self) {
    RES1 = 0;
    SEND_MESSAGE = 1;
    QUESTION_TYPE_YES_NO = 2;
    QUESTION_TYPE_ONE_TO_TEN = 3;
    QUESTION_TYPE_A_TO_E = 4;
    VOTE = 5; //command from Instructor Panel to PollStudent ****STK 6/18/12
    SYNTH_ENABLE = 6; //uGroove
    SYNTH_DISABLE = 7;//uGroove
    SYNTH_START = 8;//uGroove
    SYNTH_STOP = 9;//uGroove
    SEND_SPRITEYX = 10;  // SoundSwarm
  }
  return self;
}

@end
