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

    // TODO: FIX: for improved DEBUG

    public void printID (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}

	// Populate the list

	idList[SERVER] = new String("SERVER");
	idList[CONDUCTOR_PANEL] = new String("CONDUCTOR_PANEL");
	idList[OPERA_MAIN] = new String("OPERA_MAIN");
	idList[OPERA_CLIENT] = new String("OPERA_CLIENT");
	idList[OC_DISCUSS] = new String("OC_DISCUSS");
	idList[OC_CLOUD] = new String("OC_CLOUD");
	idList[OC_CLOUD] = new String("OC_LOGIN");
	idList[OC_CLOUD] = new String("OC_POINTER");
	idList[OC_CLOUD] = new String("DISCUSS_TOPIC");
	idList[OC_CLOUD] = new String("CLOUD_TOPIC");
	idList[OC_CLOUD] = new String("MONITOR");
	idList[OC_CLOUD] = new String("CENSOR");
	idList[OC_CLOUD] = new String("DEBUG");

	// Print out the id as a string

	System.out.println(idList[id]);

    }
}