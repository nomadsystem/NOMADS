package nomads.v210;

public class NGrain {

	public byte appID;
	public byte command;
	public byte dataType; //For some reason, it wouldn't compile with "dataType" ***STK 6/15/12
	public int dataLen;

	public byte[] uArray;
	public byte[] bArray;
	public int[] iArray;
	public float[] fArray;

	public NGrain(byte a, byte c, byte dT, int dL, byte[] bA) {
	        appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		bArray = bA;
		    // NGlobals.lPrint("NGrain: creating new NGrain");
		    // NGlobals.lPrint("NGrain: appID =" + appID);
		    // NGlobals.lPrint("NGrain: commd =" + command);
		    // NGlobals.lPrint("NGrain: dataType =" + dataType);
		    // NGlobals.lPrint("NGrain: dLen  =" + dataLen);
		    // NGlobals.lPrint("NGrain: data[] = BYTE/CHAR/UINT8Len  =" + dataLen);
	}

	public NGrain(byte a, byte c, byte dT, int dL, int[] iA) {

		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		iArray = iA;
		// NGlobals.lPrint("NGrain: creating new NGrain");
		// NGlobals.lPrint("NGrain: appID =" + appID);
		// NGlobals.lPrint("NGrain: commd =" + command);
		// NGlobals.lPrint("NGrain: dataType =" + dataType);
		// NGlobals.lPrint("NGrain: dLen  =" + dataLen);
		// NGlobals.lPrint("NGrain: data[] = INT/INT32");

	}
	public NGrain(byte a, byte c, byte dT, int dL, float[] fA) {
		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		fArray = fA;
		// NGlobals.lPrint("NGrain: creating new NGrain");
		// NGlobals.lPrint("NGrain: appID =" + appID);
		// NGlobals.lPrint("NGrain: commd =" + command);
		// NGlobals.lPrint("NGrain: dataType =" + dataType);
		// NGlobals.lPrint("NGrain: dLen  =" + dataLen);
		// NGlobals.lPrint("NGrain: data[] = FLOAT/FLOAT32");
	}

	public void print() {
		// NGlobals.lPrint("NGrain: NGrain -> print()");
		// NGlobals.lPrint("NGrain: appID =" + appID);
		// NGlobals.lPrint("NGrain: commd =" + command);
		// NGlobals.lPrint("NGrain: dataType =" + dataType);
		// NGlobals.lPrint("NGrain: dLen  =" + dataLen);

		if (dataType == NDataType.BYTE) {
			for (int i=0;i<dataLen;i++) {
			    // NGlobals.lPrint("NGrain: BYTE: " + (char)bArray[i]);
			}
		} 

		else if (dataType == NDataType.CHAR) {
			for (int i=0;i<dataLen;i++) {
			    // NGlobals.lPrint("NGrain: CHAR: " + (char)bArray[i]);
			}
		} 

		else if (dataType == NDataType.UINT8) {
			for (int i=0;i<dataLen;i++) {
			    // NGlobals.lPrint("NGrain: UINT8: " + bArray[i]);
			}
		} 

		else if (dataType == NDataType.INT32) {
			for (int i=0;i<dataLen;i++) {
			    // NGlobals.lPrint("NGrain: INT32: " + iArray[i]);
			}
		} 

		else if (dataType == NDataType.FLOAT32) {
			for (int i=0;i<dataLen;i++) {
			    // NGlobals.lPrint("NGrain: FLOAT32: " + fArray[i]);
			}
		} 

		else {
		    // NGlobals.lPrint("NGrain: NGRAIN:  UNKNOWN DATA TYPE\n");
		}

	}
}