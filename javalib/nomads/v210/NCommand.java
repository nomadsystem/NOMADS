package nomads.v210;

public class NCommand {
    public static byte NOOP = 0;
    public static byte SEND_MESSAGE = 1;

    public static byte REGISTER = 2;
    public static byte LOGIN_STATUS = 3;
    public static byte LOGIN = 4;

    public static byte MOD_STATE_START = 11; 
    public static byte SET_DISCUSS_STATUS = 11; 
    public static byte SET_CLOUD_STATUS = 12; 
    public static byte SET_POLL_STATUS = 13; 
    public static byte SET_MOSAIC_STATUS = 14; 
    public static byte SET_POINTER_STATUS = 15;  // DT: these still need to be fixed, much already cleaned 
    public static byte SET_SWARM_STATUS = 15; 
    public static byte SET_UGROOVE_STATUS = 16; 
    public static byte SET_SOUND_STATUS = 17; 

    public static byte MOD_STATE_END = 17; 
    
    public static byte SEND_DISCUSS_PROMPT = 20;
    public static byte SEND_CLOUD_PROMPT = 21;
    public static byte SEND_POLL_PROMPT = 22;

    public static byte QUESTION_TYPE_YES_NO = 31;
    public static byte QUESTION_TYPE_ONE_TO_TEN = 32;
    public static byte QUESTION_TYPE_A_TO_E = 33;
    public static byte VOTE = 34; //command from Instructor Panel to PollStudent ****STK 6/18/12
    public static byte VOTE_AGAIN = 35; 

    public static byte SYNTH_ENABLE_STATUS = 41; //uGroove
    public static byte SYNTH_START_STOP = 42;//uGroove

    public static byte SEND_SPRITE_XY = 51;  // SoundSwarm
    public static byte SEND_THREAD_ID = 52;

    public static byte GET_SOUNDFILE_LIST = 61;  // Sequencer/Mosaic

    public static byte SET_POINTER_BACKGROUND = 70;

   public static String printCmd (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}


	idList[NOOP] = new String("NOOP");
	idList[SEND_MESSAGE] = new String("SEND_MESSAGE");
	idList[REGISTER] = new String("REGISTER");
	idList[LOGIN_STATUS] = new String("LOGIN_STATUS");
	idList[LOGIN] = new String("LOGIN");
	idList[SET_DISCUSS_STATUS] = new String("SET_DISCUSS_STATUS");
	idList[SET_CLOUD_STATUS] = new String("SET_CLOUD_STATUS");
	idList[SET_POLL_STATUS] = new String("SET_POLL_STATUS");
	idList[SET_MOSAIC_STATUS] = new String("SET_MOSAIC_STATUS");
	idList[SET_POINTER_STATUS] = new String("SET_POINTER_STATUS");
	idList[SET_SWARM_STATUS] = new String("SET_SWARM_STATUS");
	idList[SET_SOUND_STATUS] = new String("SET_SOUND_STATUS");
	idList[SET_UGROOVE_STATUS] = new String("SET_UGROOVE_STATUS");
	idList[SEND_DISCUSS_PROMPT] = new String("SEND_DISCUSS_PROMPT");
	idList[SEND_CLOUD_PROMPT] = new String("SEND_CLOUD_PROMPT");
	idList[SEND_POLL_PROMPT] = new String("SEND_POLL_PROMPT");
	idList[QUESTION_TYPE_YES_NO] = new String("QUESTION_TYPE_YES_NO");
	idList[QUESTION_TYPE_ONE_TO_TEN] = new String("QUESTION_TYPE_ONE_TO_TEN");
	idList[QUESTION_TYPE_A_TO_E] = new String("QUESTION_TYPE_A_TO_E");
	idList[VOTE] = new String("VOTE");
	idList[VOTE_AGAIN] = new String("VOTE_AGAIN");
	idList[SYNTH_ENABLE_STATUS] = new String("SYNTH_ENABLE_STATUS");
	idList[SYNTH_START_STOP] = new String("SYNTH_START_STOP");
	idList[SEND_SPRITE_XY] = new String("SEND_SPRITE_XY");
	idList[SEND_THREAD_ID] = new String("SEND_THREAD_ID");
	idList[GET_SOUNDFILE_LIST] = new String("GET_SOUNDFILE_LIST");
	idList[SET_POINTER_BACKGROUND] = new String("SET_POINTER_BACKGROUND");

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
