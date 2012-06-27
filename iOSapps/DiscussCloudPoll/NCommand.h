//
//  NCommand.h
//
//  Initial version, DJT on 6/22/12.
//

#import <Foundation/Foundation.h>

@interface NCommand : NSObject
{
    @public int RES1;
    @public int SEND_MESSAGE;
    @public int QUESTION_TYPE_YES_NO;
    @public int QUESTION_TYPE_ONE_TO_TEN;
    @public int QUESTION_TYPE_A_TO_E;
    @public int VOTE;
    @public int SYNTH_ENABLE;
    @public int SYNTH_DISABLE;
    @public int SYNTH_START;
    @public int SYNTH_STOP;
    @public int SEND_SPRITEYX;
}
@end
