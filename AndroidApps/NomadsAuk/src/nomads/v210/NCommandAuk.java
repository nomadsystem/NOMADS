package nomads.v210;

public class NCommandAuk {
    public static byte NOOP = 0;
    public static byte SEND_MESSAGE = 1;
    public static byte REGISTER = 2;
    
    public static byte SYNTH_ENABLE = 6; //uGroove
    public static byte SYNTH_DISABLE = 7;//uGroove
    public static byte SYNTH_START = 8;//uGroove
    public static byte SYNTH_STOP = 9;//uGroove

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
    public static byte SEND_SPRITE_THREAD_XY = 20;  // SoundSwarm
    public static byte SEND_SPRITE_XY = 21;  // SoundSwarm
    public static byte SET_SYNTH_VOLUME = 22;
    
    public static byte SET_CLOUD_SOUND_STATUS = 23;
    public static byte SET_CLOUD_SOUND_VOLUME = 24;
    public static byte SET_POINTER_TONE_STATUS = 25;
    public static byte SET_POINTER_TONE_VOLUME = 26;
    public static byte SEND_CACHED_DISCUSS_STRING = 27;
    
    public static byte SEND_PROMPT_ON = 30;
    public static byte SEND_PROMPT_OFF = 31;
}
