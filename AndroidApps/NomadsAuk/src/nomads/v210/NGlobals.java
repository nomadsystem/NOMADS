package nomads.v210;


public class NGlobals {
    public static int clientDebugLevel = 0;  // Use this for printout info
    public static int serverDebugLevel = 0;  // Use this for printout info
    public static int libraryDebugLevel = 0;  // Use this for printout info
    public static String serverName = "nomads.music.virginia.edu";
    public static int serverPort = 52920;
    public static int serverPortDT = 52921;
    public static int serverPortSK = 52922;
    public static int serverPortPT = 52923;
    public static int serverPortMB = 52924;
    public static int serverPort_Auk = 52910;
    public static int serverPortDT_Auk = 52911;
    public static int serverPortSK_Auk = 52912;
    public static int serverPortPT_Auk = 52913;
    public static int serverPortMB_Auk = 52914;

    public static void printit(String str) {
		if (clientDebugLevel > 0) {
		    System.out.println(str);
		}
    }

    public static void cPrint(String str) {
		if (clientDebugLevel > 0) {
		    System.out.println("CLIENT: " + str);
		}
    }

    public static void sPrint(String str) {
		if (serverDebugLevel > 0) {
		    System.out.println("SERVER: " + str);
		}
    }

    public static void lPrint(String str) {
		if (libraryDebugLevel > 0) {
		    System.out.println("LIB: "+ str);
		}
    }
    
    // added by PT. 2012.08.02
    public enum GrainTarget {
    	JOIN,
    	SWARM,
    	SETTINGS
    }
    
    public static int numChatLines = 15;
}
