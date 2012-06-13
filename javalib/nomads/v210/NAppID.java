package nomads.v210;

// Panel apps < 10;
// Client apps mod 10, ie., 10,20,30,40,50
//
// usage:
//        writeByte(app_id.WEB_CHAT)

public class NAppID {
    public static int SERVER = 0;
    public static int INSTRUCTOR_PANEL = 1;
    public static int C_INSTRUCTOR_PANEL = 2;
    public static int STUDENT_PANEL = 3;
    public static int C_STUDENT_PANEL = 4;
    public static int CENSOR = 5;
    public static int MONITOR = 6;

    public static int DEBUG = 10;

    public static int WEB_CHAT = 20;
    public static int C_WEB_CHAT = 21;
    public static int DISCUSS_PROMPT = 22;
    public static int C_DISCUSS_PROMPT = 23;
    public static int INSTRUCTOR_DISCUSS = 24;
    public static int C_INSTRUCTOR_DISCUSS = 25;

    public static int TEXT_CHAT = 30;
    public static int C_TEXT_CHAT = 31;
    
    public static int LOGIN = 40;
    public static int C_LOGIN = 41;

    public static int CLOUD_DISPLAY = 50;
    public static int C_CLOUD_DISPLAY = 51;
    public static int CLOUD_CHAT = 52;
    public static int C_CLOUD_CHAT = 53;
    public static int CLOUD_PROMPT = 54;
    public static int C_CLOUD_PROMPT = 55;

    public static int TEACHER_POLL = 60;
    public static int C_TEACHER_POLL = 61;
    public static int STUDENT_POLL = 62;
    public static int C_STUDENT_POLL = 63;
    public static int DISPLAY_POLL = 64;
    public static int C_DISPLAY_POLL = 65;

    public static int SNAKE_GAME = 70;
    public static int C_SNAKE_GAME = 71;

    public static int INSTRUCTOR_SEQUENCER = 80;
    public static int C_INSTRUCTOR_SEQUENCER = 81;
    public static int STUDENT_SEQUENCER = 82;
    public static int C_STUDENT_SEQUENCER = 83;

    public static int STUDENT_SAND_POINTER = 90;
    public static int C_STUDENT_SAND_POINTER = 91;
    public static int INSTRUCTOR_SAND_POINTER = 92;
    public static int C_INSTRUCTOR_SAND_POINTER = 93;

    public static int SOUND_SWARM = 100;
    public static int C_SOUND_SWARM = 101;
    public static int SPHERE_MOVER = 102;

    public static int STUD_EMRG_SYNTH = 110;
    public static int C_STUD_EMRG_SYNTH = 111;
    public static int INSTRUCT_EMRG_SYNTH_PROMPT = 112;
    public static int C_INSTRUCT_EMRG_SYNTH_PROMPT = 113;


}
