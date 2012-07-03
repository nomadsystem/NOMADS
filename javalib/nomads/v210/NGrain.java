package nomads.v210;

public class NGrain {

	public byte appID;
	public byte command;
	public byte dataType; //For some reason, it wouldn't compile with "dataType" ***STK 6/15/12
	public int dataLen;

	public byte[] bArray;
	public int[] iArray;
	float[] fArray;
	double[] dArray;

	public NGrain(byte a, byte c, byte dT, int dL, byte[] bA) {
		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		bArray = bA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, int[] iA) {
		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		iArray = iA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, float[] fA) {
		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		fArray = fA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, double[] dA) {
		appID = a;
		command = c;
		dataType = dT;
		dataLen = dL;
		dArray = dA;
	}

	public void print() {
		NGlobals.lPrint("NGrain -> print()");
		NGlobals.lPrint("appID =" + appID);
		NGlobals.lPrint("commd =" + command);
		NGlobals.lPrint("dataType =" + dataType);
		NGlobals.lPrint("dLen  =" + dataLen);

		if (dataType == 1) {
			for (int i=0;i<dataLen;i++) {
			    NGlobals.lPrint("BYTE: " + (char)bArray[i]);
			}
		} 

		if (dataType == NDataType.INT) {
			for (int i=0;i<dataLen;i++) {
				NGlobals.lPrint("INT: " + iArray[i]);
			}
		} 

	}
}