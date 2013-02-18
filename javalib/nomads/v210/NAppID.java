package nomads.v210;

// Panel apps < 10;
// Client apps mod 10, ie., 10,20,30,40,50
//
// usage:
//        writeByte(app_id.WEB_CHAT)

public class NAppID {
    public static byte SERVER = 0;
    public static byte INSTRUCTOR_PANEL = 1;
    public static byte BINDLE = 7;

    public static byte DEBUG = 10;

    public static byte DISCUSS = 20;
    public static byte DISCUSS_PROMPT = 22;
    public static byte INSTRUCTOR_DISCUSS = 24;

    public static byte TEXT_CHAT = 30;
    
    public static byte CLOUD_DISPLAY = 50;
    public static byte CLOUD_CHAT = 52;
    public static byte CLOUD_PROMPT = 54;

    public static byte TEACHER_POLL = 60;
    public static byte STUDENT_POLL = 62;
    public static byte DISPLAY_POLL = 64;
    //    public static byte POLL_PROMPT = 65;  // DT:  TODO change this to POLL_PROMPT

    public static byte INSTRUCTOR_SEQUENCER = 80;
    public static byte STUDENT_SEQUENCER = 82;

    public static byte STUDENT_SAND_POINTER = 90;
    public static byte INSTRUCTOR_SAND_POINTER = 92;

    public static byte SOUND_SWARM = 100;
    public static byte SOUND_SWARM_DISPLAY = 101;
    public static byte SPHERE_MOVER = 103;

    public static byte STUD_EMRG_SYNTH = 110;
    public static byte INSTRUCT_EMRG_SYNTH_PROMPT = 112;

    public static String printID (int i) {
	Byte b = (byte)i;
	return printID(b);
    }



    public static String printID (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}

	idList[SERVER] = new String("SERVER");
	
	idList[SERVER] = new String("SERVER");
	idList[INSTRUCTOR_PANEL] = new String("INSTRUCTOR_PANEL");
	idList[BINDLE] = new String("BINDLE");
	idList[DEBUG] = new String("DEBUG");
	idList[DISCUSS] = new String("DISCUSS");
	idList[ DISCUSS_PROMPT] = new String(" DISCUSS_PROMPT");
	idList[INSTRUCTOR_DISCUSS] = new String("INSTRUCTOR_DISCUSS");
	idList[TEXT_CHAT] = new String("TEXT_CHAT");
	idList[CLOUD_DISPLAY] = new String("CLOUD_DISPLAY");
	idList[CLOUD_CHAT] = new String("CLOUD_CHAT");
	idList[CLOUD_PROMPT] = new String("CLOUD_PROMPT");
	idList[TEACHER_POLL] = new String("TEACHER_POLL");
	idList[STUDENT_POLL] = new String("STUDENT_POLL");
	idList[DISPLAY_POLL] = new String("DISPLAY_POLL");
	idList[INSTRUCTOR_SEQUENCER] = new String("INSTRUCTOR_SEQUENCER");
	idList[STUDENT_SEQUENCER] = new String("STUDENT_SEQUENCER");
	idList[STUDENT_SAND_POINTER] = new String("STUDENT_SAND_POINTER");
	idList[INSTRUCTOR_SAND_POINTER] = new String("INSTRUCTOR_SAND_POINTER");
	idList[SOUND_SWARM] = new String("SOUND_SWARM");
	idList[SOUND_SWARM_DISPLAY] = new String("SOUND_SWARM_DISPLAY");
	idList[SPHERE_MOVER] = new String("SPHERE_MOVER");
	idList[STUD_EMRG_SYNTH] = new String("STUD_EMRG_SYNTH");
	idList[INSTRUCT_EMRG_SYNTH_PROMPT] = new String("INSTRUCT_EMRG_SYNTH_PROMPT");
	
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
