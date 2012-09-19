package nomads.v210;

public class NCommand {
    public static byte NOOP = 0;
    public static byte SEND_MESSAGE = 1;

    public static byte REGISTER = 2;
    public static byte LOGIN_STATUS = 3;
    public static byte LOGIN = 4;

    public static byte SET_DISCUSS_STATUS = 11; 
    public static byte SET_CLOUD_STATUS = 12; 
    public static byte SET_POLL_STATUS = 13; 
    public static byte SET_MOSAIC_STATUS = 14; 
    public static byte SET_SWARM_STATUS = 15; 
    public static byte SET_UGROOVE_STATUS = 16; 
    
    public static byte SEND_DISCUSS_PROMPT = 20;
    public static byte SEND_CLOUD_PROMPT = 21;
    public static byte SEND_POLL_PROMPT = 22;

    public static byte QUESTION_TYPE_YES_NO = 31;
    public static byte QUESTION_TYPE_ONE_TO_TEN = 32;
    public static byte QUESTION_TYPE_A_TO_E = 33;
    public static byte VOTE = 34; //command from Instructor Panel to PollStudent ****STK 6/18/12

    public static byte SYNTH_ENABLE = 41; //uGroove
    public static byte SYNTH_DISABLE = 42;//uGroove
    public static byte SYNTH_START = 43;//uGroove
    public static byte SYNTH_STOP = 44;//uGroove

    public static byte SEND_SPRITE_XY = 51;  // SoundSwarm
    public static byte SEND_THREAD_ID = 52;
}
