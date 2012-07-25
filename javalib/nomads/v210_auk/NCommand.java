package nomads.v210_auk;

public class NCommand {
    public static byte NOOP = 0;
    public static byte SEND_MESSAGE = 1;
    public static byte REGISTER = 2;

    public static byte SET_DISCUSS_STATUS = 10;
    public static byte SET_CLOUD_STATUS = 11;
    public static byte SET_POINTER_STATUS = 12;
    public static byte SET_DROPLET_STATUS = 13;
    public static byte SET_DROPLET_VOLUME = 14;
    public static byte SET_DISCUSS_DISPLAY_STATUS = 15;
    public static byte SET_CLOUD_DISPLAY_STATUS = 16;
    public static byte SET_DISCUSS_ALPHA = 17;
    public static byte SET_CLOUD_ALPHA = 18;
    public static byte SET_POINTER_ALPHA = 19;
    public static byte SET_SYNTH_VOLUME = 22;
    

    public static byte SYNTH_ENABLE = 6; //uGroove
    public static byte SYNTH_DISABLE = 7;//uGroove
    public static byte SYNTH_START = 8;//uGroove
    public static byte SYNTH_STOP = 9;//uGroove

    public static byte SEND_SPRITE_THREAD_XY = 20;  // SoundSwarm
    
}
