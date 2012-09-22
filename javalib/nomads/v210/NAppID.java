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
    public static byte INSTRUCTOR_DISCUSS = 24;
    public static byte DISCUSS_PROMPT = 22;

    public static byte TEXT_CHAT = 30;
    
    public static byte CLOUD_DISPLAY = 50;
    public static byte CLOUD_CHAT = 52;
    public static byte CLOUD_PROMPT = 54;

    public static byte TEACHER_POLL = 60;
    public static byte STUDENT_POLL = 62;
    public static byte DISPLAY_POLL = 64;

    public static byte INSTRUCTOR_SEQUENCER = 80;
    public static byte STUDENT_SEQUENCER = 82;

    public static byte STUDENT_SAND_POINTER = 90;
    public static byte INSTRUCTOR_SAND_POINTER = 92;

    public static byte SOUND_SWARM = 100;
    public static byte SOUND_SWARM_DISPLAY = 101;
    public static byte SPHERE_MOVER = 103;

    public static byte STUD_EMRG_SYNTH = 110;
    public static byte INSTRUCT_EMRG_SYNTH_PROMPT = 112;

}
