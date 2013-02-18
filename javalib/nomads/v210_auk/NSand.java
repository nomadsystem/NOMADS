package nomads.v210_auk;

import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;
import nomads.v210_auk.*;

//import java.applet.*;  // Maybe need

public class NSand
{
    // Not sure which of these need to be public_private
    //(Fields)
    private Socket socket              = null;
    private DataInputStream  streamIn   = null;
    private DataOutputStream streamOut = null;    
    private String    serverName = NGlobals.serverName;
    private int       serverPort = NGlobals.serverPortDT;

    private Boolean canRun;
    private Object outSandLock;
    private Object inSandLock;
    private Object runLock;


    // Maybe yes to these
    URL imgWebBase, webBase;

    public NSand () {
	outSandLock = new Object();
	inSandLock = new Object();
	runLock = new Object();
	canRun = true;
    }

    public NSand (Socket serverSock) {
	outSandLock = new Object();
	inSandLock = new Object();
	runLock = new Object();
	socket = serverSock;
	canRun = true;
    }

    public void setRun(Boolean b) {
	synchronized(runLock) {
	NGlobals.dtPrint(">> setRun( " + b +" ) passed runLock");
	    canRun = b;
	}
    }

    public Boolean getRun() {
	NGlobals.noPrint(">> getRun()");
	synchronized(runLock) {
	    NGlobals.noPrint("     passed runLick");
	    NGlobals.noPrint("     returning " + canRun);
	    return canRun;
	}
    }


    public DataInputStream getInStream() {
	return streamIn;
    }

    public DataOutputStream getOutStream() {
	return streamOut;
    }

    public void setSock (Socket serverSock) {
	socket = serverSock;
    }

    // 0.  Get single byte (used by server) ===============================
    public byte getByte () {
	byte b = -1;
	try {
	    b = streamIn.readByte();
	}
	catch(IOException ioe) {  
	    System.out.println("6: SAND write error");
	    setRun(false);
	}
	return b;
    }

    // 0.  Get single byte (used by server) ===============================
    public byte getByteL () {
	byte b = -1;
	synchronized (inSandLock) {
	    try {
		b =streamIn.readByte();
	    }
	    catch(IOException ioe) {  
		System.out.println("6: SAND write error");
		setRun(false);
	    }
	    return b;
	}
    }

    // SendGrain with GRAIN ===============================================

    public int sendGrain (NGrain myGrain) {
	int retCode = 0;
	NGlobals.lPrint("sendGrain: (NGrain) ");
	try {  
	    // send app id
	    streamOut.writeByte(myGrain.appID);
	    // send command
	    streamOut.writeByte(myGrain.command);
	    // send data Type
	    streamOut.writeByte(myGrain.dataType);
	    // send data Length
	    streamOut.writeInt(myGrain.dataLen);
	    NGlobals.lPrint("appID =" + myGrain.appID);
	    NGlobals.lPrint("commd =" + myGrain.command);
	    NGlobals.lPrint("dataType =" + myGrain.dataType);
	    NGlobals.lPrint("dLen  =" + myGrain.dataLen);

	    // send data
	    if (myGrain.dataType == NDataType.CHAR) {
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeByte(myGrain.bArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.UINT8) {
		NGlobals.lPrint("data[] = UINT8");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeByte(myGrain.bArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.INT) {
		NGlobals.lPrint("data[] = INT (Deprecated, use INT32)");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeInt(myGrain.iArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.INT32) {
		NGlobals.lPrint("data[] = INT32");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeInt(myGrain.iArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.FLOAT) {
		NGlobals.lPrint("data[] = FLOAT (Deprecated, use FLOAT32)");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeFloat(myGrain.fArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.FLOAT32) {
		NGlobals.lPrint("data[] = FLOAT32");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeFloat(myGrain.fArray[i]);
		}
	    }

	    else {
		NGlobals.lPrint("NSAND: sendGrain(nGrain): WARNING:  Unknown NDataType: " + myGrain.dataType);
	    }

	}
	catch(IOException ioe) {  
	    NGlobals.dtPrint("SAND write error 1");
	    setRun(false);
	    return -1;
	}
	return retCode;
    }

    // 1.  SendGrain with Grain (with LOCK, for servers) =================================

    public void sendGrainL (NGrain myGrain) {
	NGlobals.noPrint("preOSL");
	synchronized (outSandLock) {
	NGlobals.noPrint("postOSL");
	NGlobals.lPrint("sendGrain: (NGrain) ");
	try {  
	    // send app id
	    streamOut.writeByte(myGrain.appID);
	    // send command
	    streamOut.writeByte(myGrain.command);
	    // send data Type
	    streamOut.writeByte(myGrain.dataType);
	    // send data Length
	    streamOut.writeInt(myGrain.dataLen);
	    NGlobals.lPrint("appID =" + myGrain.appID);
	    NGlobals.lPrint("commd =" + myGrain.command);
	    NGlobals.lPrint("dataType =" + myGrain.dataType);
	    NGlobals.lPrint("dLen  =" + myGrain.dataLen);

	    // send data
	    if (myGrain.dataType == NDataType.CHAR) {
		NGlobals.lPrint("data[] = CHAR");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeByte(myGrain.bArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.UINT8) {
		NGlobals.lPrint("data[] = UINT8");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeByte(myGrain.bArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.INT32) {
		NGlobals.lPrint("data[] = INT32");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeInt(myGrain.iArray[i]);
		}
	    }

	    else if (myGrain.dataType == NDataType.FLOAT32) {
		NGlobals.lPrint("data[] = FLOAT32");
		for (int i=0; i<myGrain.dataLen; i++) {
		    streamOut.writeFloat(myGrain.fArray[i]);
		}
	    }

	    else {
		NGlobals.lPrint("NSAND: sendGrain(nGrain): WARNING:  Unknown NDataType: " + myGrain.dataType);
	    }


	}
	catch(IOException ioe) {  
	    System.out.println("1: SAND write error");
	    setRun(false);
	}
    }
    }


    // 2. SendGrain with Byte Array no lock ====================================================

    public void sendGrain (byte aID, byte cmd, byte dType, int dLen, byte[] bArray) {
	NGlobals.lPrint("sendGrain: ... args ... bArray[]");
	try {  
	    // send app id
	    streamOut.writeByte(aID);
	    // send command
	    streamOut.writeByte(cmd);
	    // send data Type
	    streamOut.writeByte(dType);
	    // send data Length
	    streamOut.writeInt(dLen);

	    NGlobals.lPrint("appID =" + aID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dType);
	    NGlobals.lPrint("dLen  =" + dLen);


	    for (int i=0; i<dLen; i++) {
		streamOut.writeByte(bArray[i]);
		NGlobals.lPrint("BYTE:  " + (char)bArray[i]);
	    }
	}
	catch(IOException ioe) {  
	    System.out.println("SAND write error 2");
	    setRun(false);
	}
    }

    // 2. SendGrain with Byte Array (with LOCK, for servers) ===============================
    public void sendGrainL (byte aID, byte cmd, byte dType, int dLen, byte[] bArray) {
	NGlobals.noPrint("preOSL");
	synchronized (outSandLock) {
	NGlobals.noPrint("postOSL");
	NGlobals.lPrint("sendGrain: ... args ... bArray[]");
	try {  
	    // send app id
	    streamOut.writeByte(aID);
	    // send command
	    streamOut.writeByte(cmd);
	    // send data Type
	    streamOut.writeByte(dType);
	    // send data Length
	    streamOut.writeInt(dLen);

	    NGlobals.lPrint("appID =" + aID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dType);
	    NGlobals.lPrint("dLen  =" + dLen);


	    for (int i=0; i<dLen; i++) {
		streamOut.writeByte(bArray[i]);
		NGlobals.lPrint("BYTE:  " + bArray[i]);
	    }
	}
	catch(IOException ioe) {  
	    System.out.println("2: SAND write error");
	    setRun(false);
	}
    }
    }


    // 3. SendGrain with Int Array (no locks, for apps) ========================================
    public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, int[] iArray) {
	NGlobals.lPrint("sendGrain: ... args ... iArray[]");
	try {  
	    // send app id
	    streamOut.writeByte(aID);
	    // send command
	    streamOut.writeByte(cmd);
	    // send data Type
	    streamOut.writeByte(dType);
	    // send data Length
	    streamOut.writeInt(dLen);

	    NGlobals.lPrint("appID =" + aID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dType);
	    NGlobals.lPrint("dLen  =" + dLen);

	    for (int i=0; i<dLen; i++) {
		streamOut.writeInt(iArray[i]);
	    }
	}
	catch(IOException ioe) {  
	    System.out.println("SAND write error 3");
	    setRun(false);
	}
    }

    // 3. SendGrain with Int Array (with LOCKS, for servers) ====================================
    public  void sendGrainL (byte aID, byte cmd, byte dType, int dLen, int[] iArray) {
	NGlobals.noPrint("preOSL");
	synchronized (outSandLock) {
	NGlobals.noPrint("postOSL");
	NGlobals.lPrint("sendGrain: ... args ... iArray[]");
	try {  
	    // send app id
	    streamOut.writeByte(aID);
	    // send command
	    streamOut.writeByte(cmd);
	    // send data Type
	    streamOut.writeByte(dType);
	    // send data Length
	    streamOut.writeInt(dLen);

	    NGlobals.lPrint("appID =" + aID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dType);
	    NGlobals.lPrint("dLen  =" + dLen);

	    for (int i=0; i<dLen; i++) {
		streamOut.writeInt(iArray[i]);
	    }
	}
	catch(IOException ioe) {  
	    System.out.println("4: SAND write error");
	    setRun(false);
	}
    }
    }

    // 4. SendGrain with Float Array (no locks, for apps) ===============================
    public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, float[] fArray) {
	NGlobals.lPrint("sendGrain: ... args ... fArray[]");
	try {  
	    // send app id
	    streamOut.writeByte(aID);
	    // send command
	    streamOut.writeByte(cmd);
	    // send data Type
	    streamOut.writeByte(dType);
	    // send data Length
	    streamOut.writeInt(dLen);

	    NGlobals.lPrint("appID =" + aID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dType);
	    NGlobals.lPrint("dLen  =" + dLen);

	    for (int i=0; i<dLen; i++) {
		streamOut.writeFloat(fArray[i]);
	    }
	}
	catch(IOException ioe) {  
	    setRun(false);
	    System.out.println("SAND write error 4");
	}
    }

    // 4. SendGrain with Float Array (with LOCKS, for servers) ===============================
    public  void sendGrainL (byte aID, byte cmd, byte dType, int dLen, float[] fArray) {
	NGlobals.noPrint("preOSL");
	synchronized (outSandLock) {
	NGlobals.noPrint("postOSL");
	NGlobals.lPrint("sendGrain: ... args ... fArray[]");
	try {  
	    // send app id
	    streamOut.writeByte(aID);
	    // send command
	    streamOut.writeByte(cmd);
	    // send data Type
	    streamOut.writeByte(dType);
	    // send data Length
	    streamOut.writeInt(dLen);

	    NGlobals.lPrint("appID =" + aID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dType);
	    NGlobals.lPrint("dLen  =" + dLen);

	    for (int i=0; i<dLen; i++) {
		streamOut.writeFloat(fArray[i]);
	    }
	}
	catch(IOException ioe) {  
	    System.out.println("5: SAND write error");
	    setRun(false);
	}
    }
    }

    // getGrain 1 (arg) LOCKS ===========================================================================================

    //Returns Grain appID, cmd, dT, dL, bA
    public  NGrain getGrainL (byte appID) {
	synchronized (inSandLock) {
	NGlobals.lPrint("getGrain");
	byte cmd, dT;
	int dL;
	NGrain grain = null;
	try {  
	    // get command
	    cmd = streamIn.readByte();
	    // get data Type
	    dT = streamIn.readByte();
	    // get data Length
	    dL = streamIn.readInt();

	    //Detect array type in Grain
	    //Byte array
	    if (dT == NDataType.BYTE) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("BYTE:  " + (char) bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
	    }

	    //Byte array
	    else if (dT == NDataType.UINT8) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("UINT8:  " + bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
		// System.out.println("NSand:getGrain: creating grain with UINT8s");
	    }
			
	    //Int Array
	    else if (dT == NDataType.INT) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
	    }

	    //Int Array 32
	    else if (dT == NDataType.INT32) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
		// System.out.println("NSand:getGrain: creating grain with INT32s");
	    }
			
	    //Float Array
	    else if (dT == NDataType.FLOAT) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
	    }
			
	    else if (dT == NDataType.FLOAT32) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
		// System.out.println("NSand:getGrain: creating grain with FLOAT32s");

	    }

	}
	catch(IOException ioe) {  
	    System.out.println("6: SAND write error");
	    setRun(false);
	}
	// System.out.println("NSand:getGrain: returning grain");
	// grain.print();
	return grain;
    }
    }

    // getGrain 1 (arg) no locks ===========================================================================================

    //Returns Grain appID, cmd, dT, dL, bA
    public  NGrain getGrain (byte aID) {
	NGlobals.lPrint(" getGrain: aID: ");
	byte cmd, dT, appID;
	int dL;
	NGrain grain = null;
	try {  
	    // get appID
	    appID = aID;

	    // get command
	    cmd = streamIn.readByte();
	    // get data Type
	    dT = streamIn.readByte();
	    // get data Length
	    dL = streamIn.readInt();

	    NGlobals.lPrint("appID =" + appID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dT);
	    NGlobals.lPrint("dLen  =" + dL);

	    //Detect array type in Grain
	    //Byte array
	    if (dT == NDataType.BYTE) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("BYTE:  " + (char) bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);

	    }

	    //Byte array
	    else if (dT == NDataType.UINT8) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("UINT8:  " + bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
		NGlobals.lPrint("NSand:getGrain: creating grain with UINT8s");
	    }

	    //Int Array
	    else if (dT == NDataType.INT) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
		NGlobals.lPrint("NSand:getGrain: creating grain with INTs (Deprecated, use INT32)");

	    }


	    //Int Array 32
	    else if (dT == NDataType.INT32) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
		NGlobals.lPrint("NSand:getGrain: creating grain with INT32s");
	    }

	    //Float Array
	    else if (dT == NDataType.FLOAT) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
		NGlobals.lPrint("NSand:getGrain: creating grain with FLOATs (Deprecated, use FLOAT32)");
	    }

	    //Float Array 32
	    else if (dT == NDataType.FLOAT32) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
		NGlobals.lPrint("NSand:getGrain: creating grain with FLOAT32s");

	    }
	    else {
		NGlobals.lPrint("WARNING:  unknown SAND data type\n");
	    }
	}
	catch(IOException ioe) {  
	    System.out.println("SAND write error 5");
	    setRun(false);
	}
	NGlobals.lPrint("NSand:getGrain: returning grain\n");

	return grain;
    }

    // getGrain() 2 no args LOCKS ===========================================================================================

    //Returns Grain appID, cmd, dT, dL, bA
    public  NGrain getGrainL () {
	synchronized (inSandLock) {

	NGlobals.lPrint("getGrain");
	byte appID, cmd, dT;
	int dL;
	NGrain grain = null;
	try {  
	    // get app id
	    appID = streamIn.readByte();
	    // get command
	    cmd = streamIn.readByte();
	    // get data Type
	    dT = streamIn.readByte();
	    // get data Length
	    dL = streamIn.readInt();

	    //Detect array type in Grain
	    //Byte array
	    if (dT == NDataType.BYTE) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("BYTE:  " + (char) bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
	    }
			
	    //Byte array
	    else if (dT == NDataType.UINT8) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("UINT8:  " + (char) bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
		// System.out.println("NSand:getGrain: creating grain with UINT8s");
	    }

	    //Int Array
	    else if (dT == NDataType.INT) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
	    }

	    //Int Array 32
	    else if (dT == NDataType.INT32) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT32:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
		// System.out.println("NSand:getGrain: creating grain with INT32s");
	    }
			
	    //Float Array
	    else if (dT == NDataType.FLOAT) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
	    }

	    //Float Array 32
	    else if (dT == NDataType.FLOAT32) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
		// System.out.println("NSand:getGrain: creating grain with FLOAT32s");

	    }
	    else {
		NGlobals.lPrint("WARNING:  unknown SAND data type\n");
	    }
			
	}
	catch(IOException ioe) {  
	    // System.out.println("7: SAND write error");
	    setRun(false);
	}
	return grain;
    }
    }

    // getGrain() 2 no args no locks ===========================================================================================

    //Returns Grain appID, cmd, dT, dL, bA
    public  NGrain getGrain () {
	NGlobals.lPrint("getGrain");
	byte appID, cmd, dT;
	int dL;
	NGrain grain = null;
	try {  
	    // get app id
	    appID = streamIn.readByte();
	    // get command
	    cmd = streamIn.readByte();
	    // get data Type
	    dT = streamIn.readByte();
	    // get data Length
	    dL = streamIn.readInt();

	    NGlobals.lPrint("appID =" + appID);
	    NGlobals.lPrint("commd =" + cmd);
	    NGlobals.lPrint("dataType =" + dT);
	    NGlobals.lPrint("dLen  =" + dL);

	    //Detect array type in Grain
	    //Byte array
	    if (dT == NDataType.CHAR) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("CHAR:  " + (char) bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
		NGlobals.lPrint("NSand:getGrain: creating grain with BYTEs (Deprecated, use CHAR or UINT8)");

	    }

	    //Byte array
	    else if (dT == NDataType.UINT8) {
		byte[] bA = new byte[dL];

		for (int i=0; i< dL; i++) {
		    bA[i] = streamIn.readByte();
		    NGlobals.lPrint("UINT8:  " + (char) bA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, bA);
		NGlobals.lPrint("NSand:getGrain: creating grain with UINT8s");
	    }

	    //Int Array
	    else if (dT == NDataType.INT) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
		NGlobals.lPrint("NSand:getGrain: creating grain with INTs (Deprecated, use INT32)");

	    }

	    //Int Array 32
	    else if (dT == NDataType.INT32) {
		int[] iA = new int[dL];

		for (int i=0; i< dL; i++) {
		    iA[i] = streamIn.readInt();
		    NGlobals.lPrint("INT32:  " + iA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, iA);
		NGlobals.lPrint("NSand:getGrain: creating grain with INT32s");
	    }

	    //Float Array
	    else if (dT == NDataType.FLOAT) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
		NGlobals.lPrint("NSand:getGrain: creating grain with FLOATs (Deprecated, use FLOAT32)");
	    }

	    //Float Array 32
	    else if (dT == NDataType.FLOAT32) {
		float[] fA = new float[dL];

		for (int i=0; i< dL; i++) {
		    fA[i] = streamIn.readFloat();
		    NGlobals.lPrint("FLOAT:  " + fA[i]);
		}
		grain = new NGrain(appID, cmd, dT, dL, fA);
		NGlobals.lPrint("NSand:getGrain: creating grain with FLOAT32s");

	    }
	    else {
		NGlobals.lPrint("WARNING:  unknown SAND data type\n");
	    }

	}
	catch(IOException ioe) {  
	    System.out.println("SAND write error 6");
	    setRun(false);
	}
	return grain;
    }

    public void connect() {
	connectSocket();
	openSocketStreams();
    }


    public void disconnect() {
	closeSocketStreams();
	disConnectSocket();
    }

    // These are called by the two above

    public void connectSocket()
    {  
	System.out.println("Establishing connection. Please wait ...");
	try {  
	    socket = new Socket(serverName, serverPort);
	    System.out.println("Connected");
	}
	catch(IOException ioe) {  
	    System.out.println("socket connect exception: ");
	    setRun(false);
	}
    }

    public void disConnectSocket()
    {  
	System.out.println("Disconnecting Please wait ...");
	try {  
	    socket.close();
	    System.out.println("Disconnected");
	}
	catch(IOException ioe) {  
	    System.out.println("socket discconnect exception: ");
	}
    }


    public void openSocketStreams()
    {  
	try {
	    streamOut = new DataOutputStream(socket.getOutputStream());
	    streamIn = new DataInputStream(socket.getInputStream());
	}
	catch(IOException ioe) {
	    System.out.println("Error opening output stream: ");
	    setRun(false);
	}
    }


    public void closeSocketStreams()
    {  
	try {
	    if (streamOut != null)  streamOut.close();
	    if (socket    != null)  socket.close();
	}
	catch(IOException ioe) {
	    System.out.println("Error closing...");
	}
    }


    public void close()
    {  
	closeSocketStreams();
    }


    public void stop() {
	System.out.println("NSand stop() called, not implemented...");
    }


}
