package nomads.v210_auk;


public class NDataType {
    public static byte NO_DATA = 0;
    public static byte BYTE = 1;   // TOFIX:  should become char - need to change in all java code
    public static byte CHAR = 1;   // temp fix
    public static byte UINT8 = 2;  // temp fix 
    public static byte INT = 3;    // TOFIX:  should become int32 - need to change in all java code
    public static byte INT32 = 3;  // temp fix
    public static byte FLOAT = 4;      // TOFIX:  should become int32 - need to change in all java code
    public static byte FLOAT32 = 4;    // temp fix

    public static String printDT (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}

	idList[NO_DATA] = new String("NO_DATA");
	idList[CHAR] = new String("CHAR (BYTE)");
	idList[UINT8] = new String("UINT8");
	idList[INT32] = new String("INT32 (INT)");
	idList[FLOAT32] = new String("FLOAD32 (FLOAT)");

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