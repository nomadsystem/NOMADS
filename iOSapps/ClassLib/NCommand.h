//
//  NCommand.h
//
//  Initial version, DJT on 6/22/12.
//

#import <Foundation/Foundation.h>

@interface NCommand : NSObject
{
    @public Byte RES1;
    @public Byte SEND_MESSAGE;
    @public Byte QUESTION_TYPE_YES_NO;
    @public Byte QUESTION_TYPE_ONE_TO_TEN;
    @public Byte QUESTION_TYPE_A_TO_E;
    @public Byte VOTE;
    @public Byte SYNTH_ENABLE;
    @public Byte SYNTH_DISABLE;
    @public Byte SYNTH_START;
    @public Byte SYNTH_STOP;
    @public Byte SEND_SPRITEYX;
}
@end
