package nomads.v210_auk;

public class NCommand {
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
    public static byte SET_CLIENT_COUNT = 33;

    public static byte DELETE_SPRITE = 99;

    public static String printCmd (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}

	idList[NOOP] = new String("NOOP");
	idList[SEND_MESSAGE] = new String("SEND_MESSAGE");
	idList[REGISTER] = new String("REGISTER");
	
	idList[SYNTH_ENABLE] = new String("SYNTH_ENABLE");
	idList[SYNTH_DISABLE] = new String("SYNTH_DISABLE");
	idList[SYNTH_START] = new String("SYNTH_START");
	idList[SYNTH_STOP] = new String("SYNTH_STOP");

	idList[SET_DISCUSS_STATUS] = new String("SET_DISCUSS_STATUS");
	idList[SET_CLOUD_STATUS] = new String("SET_CLOUD_STATUS");
	idList[SET_POINTER_STATUS] = new String("SET_POINTER_STATUS");
	idList[SET_DROPLET_VOLUME] = new String("SET_DROPLET_VOLUME");
	idList[SET_DISCUSS_DISPLAY_STATUS] = new String("SET_DISCUSS_DISPLAY_STATUS");
	idList[SET_CLOUD_DISPLAY_STATUS] = new String("SET_CLOUD_DISPLAY_STATUS");
	idList[SET_DISCUSS_ALPHA] = new String("SET_DISCUSS_ALPHA");
	idList[SET_CLOUD_ALPHA] = new String("SET_CLOUD_ALPHA");
	idList[SET_POINTER_ALPHA] = new String("SET_POINTER_ALPHA");
	idList[SEND_SPRITE_THREAD_XY] = new String("SEND_SPRITE_THREAD_XY");
	idList[SEND_SPRITE_XY] = new String("SEND_SPRITE_XY");
	idList[SET_SYNTH_VOLUME] = new String("SET_SYNTH_VOLUME");

	idList[SET_CLOUD_SOUND_STATUS] = new String("SET_CLOUD_SOUND_STATUS");
	idList[SET_CLOUD_SOUND_VOLUME] = new String("SET_CLOUD_SOUND_VOLUME");
	idList[SET_POINTER_TONE_STATUS] = new String("SET_POINTER_TONE_STATUS");
	idList[SET_POINTER_TONE_VOLUME] = new String("SET_POINTER_TONE_VOLUME");
	idList[SEND_CACHED_DISCUSS_STRING] = new String("SEND_CACHED_DISCUSS_STRING");

	idList[SEND_PROMPT_ON] = new String("SEND_PROMPT_ON");
	idList[SEND_PROMPT_OFF] = new String("SEND_PROMPT_OFF");
	idList[SET_CLIENT_COUNT] = new String("SET_CLIENT_COUNT");

	idList[DELETE_SPRITE] = new String("DELETE_SPRITE");

	// Print out the id as a string
	if (idList[id] != null) {
	    String rString = new String(idList[id] + "[" + id + "]");
	    return rString;	
	}
	else {
	    String rString = new String("UNKNOWN[" + id + "]");
	    return rString;	
	}
    }
}
