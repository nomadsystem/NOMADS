package nomads.v210;

public class NCommand {
    public static byte RES1 = 0;
    public static byte SEND_MESSAGE = 1;
    public static byte QUESTION_TYPE_YES_NO = 2;
    public static byte QUESTION_TYPE_ONE_TO_TEN = 3;
    public static byte QUESTION_TYPE_A_TO_E = 4;
    public static byte VOTE = 5; //command from Instructor Panel to PollStudent ****STK 6/18/12
    public static byte SYNTH_ENABLE = 6; //uGroove
    public static byte SYNTH_DISABLE = 7;//uGroove
    public static byte SYNTH_START = 8;//uGroove
    public static byte SYNTH_STOP = 9;//uGroove
    public static byte LOGIN = 10;

    public static byte SEND_SPRITE_XY = 20;  // SoundSwarm
    public static byte SEND_THREAD_ID = 21;
}
