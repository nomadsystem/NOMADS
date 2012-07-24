package nomads.v210_auk;

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
		NGlobals.lPrint("creating new NGrain");
		NGlobals.lPrint("appID =" + appID);
		NGlobals.lPrint("commd =" + command);
		NGlobals.lPrint("dataType =" + dataType);
		NGlobals.lPrint("dLen  =" + dataLen);
		NGlobals.lPrint("data[] = BYTE/CHAR/UINT8Len  =" + dataLen);
		
		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		bArray = bA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, int[] iA) {
		NGlobals.lPrint("creating new NGrain");
		NGlobals.lPrint("appID =" + appID);
		NGlobals.lPrint("commd =" + command);
		NGlobals.lPrint("dataType =" + dataType);
		NGlobals.lPrint("dLen  =" + dataLen);
		NGlobals.lPrint("data[] = INT/INT32");

		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		iArray = iA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, float[] fA) {
		NGlobals.lPrint("creating new NGrain");
		NGlobals.lPrint("appID =" + appID);
		NGlobals.lPrint("commd =" + command);
		NGlobals.lPrint("dataType =" + dataType);
		NGlobals.lPrint("dLen  =" + dataLen);
		NGlobals.lPrint("data[] = FLOAT/FLOAT32");

		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		fArray = fA;
	}

	public void print() {
		NGlobals.lPrint("NGrain -> print()");
		NGlobals.lPrint("appID =" + appID);
		NGlobals.lPrint("commd =" + command);
		NGlobals.lPrint("dataType =" + dataType);
		NGlobals.lPrint("dLen  =" + dataLen);

		if (dataType == NDataType.CHAR) {
			for (int i=0;i<dataLen;i++) {
			    NGlobals.lPrint("CHAR: " + (char)bArray[i]);
			}
		} 

		else if (dataType == NDataType.UINT8) {
			for (int i=0;i<dataLen;i++) {
			    NGlobals.lPrint("UINT8: " + bArray[i]);
			}
		} 

		else if (dataType == NDataType.INT32) {
			for (int i=0;i<dataLen;i++) {
			    NGlobals.lPrint("INT32: " + iArray[i]);
			}
		} 

		else if (dataType == NDataType.FLOAT32) {
			for (int i=0;i<dataLen;i++) {
			    NGlobals.lPrint("FLOAT32: " + fArray[i]);
			}
		} 

		else {
		    NGlobals.lPrint("NGRAIN:  UNKNOWN DATA TYPE\n");
		}

	}
}