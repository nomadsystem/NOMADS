package nomads.v210;

// Panel apps < 10;
// Client apps mod 10, ie., 10,20,30,40,50
//
// usage:
//        writeByte(app_id.WEB_CHAT)

public class NAppID {
    public static byte SERVER = 0;
    public static byte INSTRUCTOR_PANEL = 1;
    public static byte C_INSTRUCTOR_PANEL = 2;
    public static byte STUDENT_PANEL = 3;
    public static byte C_STUDENT_PANEL = 4;
    public static byte CENSOR = 5;
    public static byte MONITOR = 6;
    public static byte BINDLE = 7;

    public static byte DEBUG = 10;

    public static byte WEB_CHAT = 20;
    public static byte C_WEB_CHAT = 21;
    public static byte DISCUSS_PROMPT = 22;
    public static byte C_DISCUSS_PROMPT = 23;
    public static byte INSTRUCTOR_DISCUSS = 24;
    public static byte C_INSTRUCTOR_DISCUSS = 25;

    public static byte TEXT_CHAT = 30;
    public static byte C_TEXT_CHAT = 31;
    
    public static byte LOGIN = 40;
    public static byte C_LOGIN = 41;

    public static byte CLOUD_DISPLAY = 50;
    public static byte C_CLOUD_DISPLAY = 51;
    public static byte CLOUD_CHAT = 52;
    public static byte C_CLOUD_CHAT = 53;
    public static byte CLOUD_PROMPT = 54;
    public static byte C_CLOUD_PROMPT = 55;

    public static byte TEACHER_POLL = 60;
    public static byte C_TEACHER_POLL = 61;
    public static byte STUDENT_POLL = 62;
    public static byte C_STUDENT_POLL = 63;
    public static byte DISPLAY_POLL = 64;
    public static byte C_DISPLAY_POLL = 65;

    public static byte SNAKE_GAME = 70;
    public static byte C_SNAKE_GAME = 71;

    public static byte INSTRUCTOR_SEQUENCER = 80;
    public static byte C_INSTRUCTOR_SEQUENCER = 81;
    public static byte STUDENT_SEQUENCER = 82;
    public static byte C_STUDENT_SEQUENCER = 83;

    public static byte STUDENT_SAND_POINTER = 90;
    public static byte C_STUDENT_SAND_POINTER = 91;
    public static byte INSTRUCTOR_SAND_POINTER = 92;
    public static byte C_INSTRUCTOR_SAND_POINTER = 93;

    public static byte SOUND_SWARM = 100;
    public static byte SOUND_SWARM_DISPLAY = 101;
    public static byte C_SOUND_SWARM = 102;
    public static byte SPHERE_MOVER = 103;

    public static byte STUD_EMRG_SYNTH = 110;
    public static byte C_STUD_EMRG_SYNTH = 111;
    public static byte INSTRUCT_EMRG_SYNTH_PROMPT = 112;
    public static byte C_INSTRUCT_EMRG_SYNTH_PROMPT = 113;


}
