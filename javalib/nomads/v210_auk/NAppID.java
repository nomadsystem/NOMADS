package nomads.v210_auk;

// Panel apps < 10;
// Client apps mod 10, ie., 10,20,30,40,50
//
// usage:
//        writeByte(app_id.WEB_CHAT)

public class NAppID {
    public static Byte SERVER = 99;
    public static Byte CONDUCTOR_PANEL = 1;
    public static Byte OPERA_MAIN = 2;
    public static Byte OPERA_CLIENT = 3;
    public static Byte OC_DISCUSS = 4; //OC= Opera Client
    public static Byte OC_CLOUD = 5;
    public static Byte OC_LOGIN = 6;
    public static Byte OC_POINTER = 7;
    // public static Byte CLOUD_PROMPT = 8;
    // public static Byte DISCUSS_PROMPT = 9;
    public static Byte DISCUSS_TOPIC = 10;
    public static Byte CLOUD_TOPIC = 11;

    public static Byte MONITOR = 50;
    public static Byte CENSOR = 51;
    public static Byte DEBUG = 52;

}

// TODO: FIX: for improved DEBUG

//public byte printID (byte id) {
//}