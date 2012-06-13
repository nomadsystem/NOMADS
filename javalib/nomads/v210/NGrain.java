package nomads.v210;

public class NGrain {

	public byte appID;
	public byte command;
	public byte dType;
	public int dataLen;

	public byte[] bArray;
	public int[] iArray;
	float[] fArray;
	double[] dArray;

	public NGrain(byte a, byte c, byte dT, int dL, byte[] bA) {
		appID = a;
		command = c;
		dType = dT;
		dataLen = dL;
		bArray = bA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, int[] iA) {
		appID = a;
		command = c;
		dType = dT;
		dataLen = dL;
		iArray = iA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, float[] fA) {
		appID = a;
		command = c;
		dType = dT;
		dataLen = dL;
		fArray = fA;
	}
	public NGrain(byte a, byte c, byte dT, int dL, double[] dA) {
		appID = a;
		command = c;
		dType = dT;
		dataLen = dL;
		dArray = dA;
	}

	public void print() {
		System.out.println("NGrain -> print()");
		System.out.println("appID =" + appID);
		System.out.println("commd =" + command);
		System.out.println("dType =" + dType);
		System.out.println("dLen  =" + dataLen);

		if (dType == 1) {
			for (int i=0;i<dataLen;i++) {
				System.out.println("BYTE: " + bArray[i]);
			}
		} 

		if (dType == NDataType.INT) {
			for (int i=0;i<dataLen;i++) {
				System.out.println("INT: " + iArray[i]);
			}
		} 

	}
}