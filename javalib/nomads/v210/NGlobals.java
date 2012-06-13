package nomads.v210;


public class NGlobals {
    public static int clientDebugLevel = 1;  // Use this for printout info
    public static int serverDebugLevel = 1;  // Use this for printout info
    public static int libraryDebugLevel = 1;  // Use this for printout info
    public static String    serverName = "nomads.music.virginia.edu";
    public static int serverPort = 52807;

    public static void printit(String str) {
	if (clientDebugLevel > 0) {
	    System.out.println(str);
	}
    }

    public static void cPrint(String str) {
	if (clientDebugLevel > 0) {
	    System.out.println(str);
	}
    }

    public static void sPrint(String str) {
	if (serverDebugLevel > 0) {
	    System.out.println(str);
	}
    }

    public static void lPrint(String str) {
	if (libraryDebugLevel > 0) {
	    System.out.println(str);
	}
    }
    

}
