// NSand.java
// Paul Turowski. 2012.08.08

package nomads.v210;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import com.nomads.Join;
import com.nomads.NomadsApp;

import android.os.AsyncTask;
import android.util.Log;

//import java.applet.*;  // Maybe need

public class NSand
{
    // Not sure which of these need to be public_private
    //(Fields)
    private Socket socket              = null;
    private DataInputStream  streamIn   = null;
    private DataOutputStream streamOut = null;    
    private String    serverName = NGlobals.serverName;
    private int       serverPort = NGlobals.serverPortPT;

    // Maybe yes to these
    URL imgWebBase, webBase;

    public NSand () {
    }

    public NSand (Socket serverSock) {
	socket = serverSock;
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
    
 // for Settings.java
    public String getServerName () {
    	return serverName;
    }
    
    public int getServerPort () {
    	return serverPort;
    }
    
//==============================================================
//    getGrain()
//==============================================================

    //Returns Grain appID, cmd, dT, dLen, bA
    public  NGrain getGrain (byte aID) {
		Log.d("NSand.java", " getGrain: aID: ");
		byte cmd, dT, appID;
		int dLen;
		NGrain grain = null;
		try {  
		    // get appID
		    appID = aID;
	
		    // get command
		    cmd = streamIn.readByte();
		    // get data Type
		    dT = streamIn.readByte();
		    // get data Length
		    dLen = streamIn.readInt();
	
		    Log.d("NSand.java", "getGrain: appID =" + appID);
		    Log.d("NSand.java", "getGrain: commd =" + cmd);
		    Log.d("NSand.java", "getGrain: dataType =" + dT);
		    Log.d("NSand.java", "getGrain: dLen  =" + dLen);
	
		    //Detect array type in Grain
		    //Byte array
		    if (dT == NDataType.BYTE) {
				byte[] bA = new byte[dLen];
		
				for (int i=0; i< dLen; i++) {
				    bA[i] = streamIn.readByte();
				    Log.d("NSand.java", "getGrain: BYTE:  " + (char) bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with BYTEs (Deprecated, use CHAR or UINT8)");
		    }
	
		    //Byte array
		    else if (dT == NDataType.UINT8) {
				byte[] bA = new byte[dLen];
		
				for (int i=0; i< dLen; i++) {
				    bA[i] = streamIn.readByte();
				    Log.d("NSand.java", "getGrain: UINT8:  " + bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with UINT8s");
		    }
	
		    //Int Array
		    else if (dT == NDataType.INT) {
				int[] iA = new int[dLen];
		
				for (int i=0; i< dLen; i++) {
				    iA[i] = streamIn.readInt();
				    Log.d("NSand.java", "getGrain: INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with INTs (Deprecated, use INT32)");
	
		    }
	
	
		    //Int Array 32
		    else if (dT == NDataType.INT32) {
				int[] iA = new int[dLen];
		
				for (int i=0; i< dLen; i++) {
				    iA[i] = streamIn.readInt();
				    Log.d("NSand.java", "INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with INT32s");
		    }
	
		    //Float Array
		    else if (dT == NDataType.FLOAT) {
				float[] fA = new float[dLen];
		
				for (int i=0; i< dLen; i++) {
				    fA[i] = streamIn.readFloat();
				    Log.d("NSand.java", "getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with FLOATs (Deprecated, use FLOAT32)");
		    }
	
		    //Float Array 32
		    else if (dT == NDataType.FLOAT32) {
				float[] fA = new float[dLen];
		
				for (int i=0; i< dLen; i++) {
				    fA[i] = streamIn.readFloat();
				    Log.d("NSand.java", "getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with FLOAT32s");
	
		    }
		    else {
		    	Log.w("NSand.java", "getGrain: WARNING:  unknown SAND data type\n");
		    }
		}
		catch(IOException ioe) {  
		    Log.e("NSand.java", "SAND write error");
		}
		
		Log.d("NSand.java", "NSand:getGrain: returning grain\n");
	
		return grain;
    }

    //Returns Grain appID, cmd, dT, dLen, bA
    public  NGrain getGrain () {
		Log.d("NSand.java", "getGrain");
		byte appID, cmd, dT;
		int dLen;
		NGrain grain = null;
		try {  
		    // get app id
		    appID = streamIn.readByte();
		    // get command
		    cmd = streamIn.readByte();
		    // get data Type
		    dT = streamIn.readByte();
		    // get data Length
		    dLen = streamIn.readInt();
	
		    Log.d("NSand.java", "getGrain: appID =" + appID);
		    Log.d("NSand.java", "getGrain: commd =" + cmd);
		    Log.d("NSand.java", "getGrain: dataType =" + dT);
		    Log.d("NSand.java", "getGrain: dLen  =" + dLen);
	
		    //Detect array type in Grain
		    //Byte array
		    if (dT == NDataType.BYTE) {
				byte[] bA = new byte[dLen];
		
				for (int i=0; i< dLen; i++) {
				    bA[i] = streamIn.readByte();
				    Log.d("NSand.java", "getGrain: BYTE:  " + (char) bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with BYTEs (Deprecated, use CHAR or UINT8)");
		    }
	
		    //Byte array
		    else if (dT == NDataType.UINT8) {
				byte[] bA = new byte[dLen];
		
				for (int i=0; i< dLen; i++) {
				    bA[i] = streamIn.readByte();
				    Log.d("NSand.java", "getGrain: UINT8:  " + (char) bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with UINT8s");
		    }
	
		    //Int Array
		    else if (dT == NDataType.INT) {
				int[] iA = new int[dLen];
		
				for (int i=0; i< dLen; i++) {
				    iA[i] = streamIn.readInt();
				    Log.d("NSand.java", "getGrain: INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with INTs (Deprecated, use INT32)");
		    }
	
		    //Int Array 32
		    else if (dT == NDataType.INT32) {
				int[] iA = new int[dLen];
		
				for (int i=0; i< dLen; i++) {
				    iA[i] = streamIn.readInt();
				    Log.d("NSand.java", "getGrain: INT32:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with INT32s");
		    }
	
		    //Float Array
		    else if (dT == NDataType.FLOAT) {
				float[] fA = new float[dLen];
		
				for (int i=0; i< dLen; i++) {
				    fA[i] = streamIn.readFloat();
				    Log.d("NSand.java", "getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with FLOATs (Deprecated, use FLOAT32)");
		    }
	
		    //Float Array 32
		    else if (dT == NDataType.FLOAT32) {
				float[] fA = new float[dLen];
		
				for (int i=0; i< dLen; i++) {
				    fA[i] = streamIn.readFloat();
				    Log.d("NSand.java", "getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				Log.d("NSand.java", "NSand:getGrain: creating grain with FLOAT32s");
	
		    }
		    else {
		    	Log.w("NSand.java", "getGrain: WARNING:  unknown SAND data type\n");
		    }
	
		}
		catch(IOException ioe) {  
		    Log.e("NSand.java", "SAND write error");
		}
		return grain;
    }
    
    public void stop()
    {
    	Log.w("NSand.java", "NSand stop() called, not implemented...");
    }
 
    
//==============================================================
//----------------------------------------------
//  ASyncTask classes added by PAT. 2012.08.07
//----------------------------------------------   
//==============================================================
//  Connect()
//==============================================================

  	public class Connect extends AsyncTask<Object, Void, Boolean>
  	{
  		Join j;
  		NomadsApp app;
  		boolean connectStatus = false;
  		
  		@Override
  		protected void onPreExecute()
  		{
  			// setup progress bar here
  		}
  		
  		@Override
  		protected Boolean doInBackground(Object... params)
  		{
  			// set caller variable to the object that called ConnectAsync
  			j = (Join) params[0];
  			app = (NomadsApp) params[1];
  			
  			Log.d("NSand.java", "Establishing connection. Please wait ...");
  	  		try
  	  		{  
  	  			socket = new Socket(serverName, serverPort);
  	  			Log.d("NSand.java", "Connected");
  	  		}
  	  		catch(IOException ioe)
  	  		{
  	  			socket = null;
  	  			Log.e("NSand.java", "socket connect IOException");
  	  		}
  	  		
  	  		if (socket != null)
  	  		{
	  	  		try
				{
				    streamOut = new DataOutputStream(socket.getOutputStream());
				    streamIn = new DataInputStream(socket.getInputStream());
				    connectStatus = true;
				}
				catch(IOException ioe)
				{
				    Log.d("NSand.java", "Error opening output stream");
				}
  	  		}
  	  		
  	  		return connectStatus;
  		}
  		
//  		@Override
//  		protected void onProgressUpdate(Void v)
//  		{
//  			
//  		}
  		
  		@Override
  		protected void onPostExecute(Boolean connected)
  		{
  			app.setConnectionStatus(connected);
  			
  			if (connected)
  			{
  				j.register();
//  				app.startThread();
  				j.goToSwarm();
  			}
  		}
  	}
  	
//==============================================================
//  Send()
//==============================================================
  	
  	public class Send extends AsyncTask<Void, Void, Void>
  	{
  		NomadsApp app;
  		byte aID, cmd, dT;
  		int dLen;
  		byte[] bArray = null;
  		int[] iArray = null;
  		float[] fArray = null;
  		
  		public Send() {
  			super();
  			Log.w("NSand.java", "You need to pass appID, command, dataType, dataLength, and array (byte, int, or float)");
  		}
  		
  		public Send(byte _aID, byte _cmd, byte _dT, int _dLen, byte[] _bArray) {
  			super();
  			aID = _aID;
  			cmd = _cmd;
  			dT = _dT;
  			dLen = _dLen;
  			bArray = _bArray;
  		}
  		
  		public Send(byte _aID, byte _cmd, byte _dT, int _dLen, int[] _iArray) {
  			super();
  			aID = _aID;
  			cmd = _cmd;
  			dT = _dT;
  			dLen = _dLen;
  			iArray = _iArray;
  		}
  		
  		public Send(byte _aID, byte _cmd, byte _dT, int _dLen, float[] _fArray) {
  			super();
  			aID = _aID;
  			cmd = _cmd;
  			dT = _dT;
  			dLen = _dLen;
  			fArray = _fArray;
  		}
  		
  		@Override
  		protected Void doInBackground(Void... v)
  		{	
//  			Log.d("NSand.java", "sending grain (async)...");
  			try {  
  			    // send app id
  			    streamOut.writeByte(aID);
  			    // send command
  			    streamOut.writeByte(cmd);
  			    // send data Type
  			    streamOut.writeByte(dT);
  			    // send data Length
  			    streamOut.writeInt(dLen);
  		
  			    Log.d("NSand.java", "appID =" + aID);
  			    Log.d("NSand.java", "commd =" + cmd);
  			    Log.d("NSand.java", "dataType =" + dT);
  			    Log.d("NSand.java", "dLen  =" + dLen);
  			    if (bArray != null) {
	  			    for (int i=0; i<dLen; i++) {
	  			    	streamOut.writeByte(bArray[i]);
	  			    }
  			    }
  			    else if (iArray != null) {
	  			    for (int i=0; i<dLen; i++) {
	  			    	streamOut.writeInt(iArray[i]);
	  			    }
  			    }
  			    else if (iArray != null) {
	  			    for (int i=0; i<dLen; i++) {
	  			    	streamOut.writeFloat(iArray[i]);
	  			    }
			    }
  			}
  			catch(IOException ioe) {  
  			    Log.e("NSand.java", "SAND write error");
  			}

  	  		
  	  		return null;
  		}
  	}
  	
//==============================================================
//  Close()
//==============================================================
  	
  	public class Close extends AsyncTask<Void, Void, Void>
  	{	
  		@Override
  		protected Void doInBackground(Void... v)
  		{
  			
  			Log.d("NSand.java", "Attempting to close sand...");
  			try {
  			    if (streamOut != null)  streamOut.close();
  			    if (socket    != null)  socket.close();
  			}
  			catch(IOException ioe) {
  			    Log.e("NSand.java", "Error closing...");
  			}
  	  		
  	  		return null;
  		}
  	}

//=====================================================================
//    These methods have been replaced by AsyncTask classes above
//    in compliance with Android v.3.x+
//=====================================================================
    
//    //Connect, opens the socket, creates the streams
//  	// Altered by PT 2012.07.29
//  	public boolean connect()
//  	{
//  		boolean connectStatus = connectSocket();
//  		
//  		if (connectStatus) openSocketStreams();
//  		
//  		Log.d("NSand.java", "NSand.java -> connect() -> connectStatus: " + connectStatus);
//  		
//  		return connectStatus;
//  	}
//  	
//  	public boolean connectSocket()
//  	{  
//  		Log.d("NSand.java", "Establishing connection. Please wait ...");
//  		try
//  		{  
//  			socket = new Socket(serverName, serverPort);
//  			Log.d("NSand.java", "Connected");
//  		}
//  		catch(IOException ioe) {
//  			socket = null;
//  			Log.d("NSand.java", "socket connect exception");
//  		}
//  		
//  		if (socket != null) return true;
//  		
//  		return false;
//  	}
//
//    public void openSocketStreams()
//    {  
//		try
//		{
//		    streamOut = new DataOutputStream(socket.getOutputStream());
//		    streamIn = new DataInputStream(socket.getInputStream());
//		}
//		catch(IOException ioe)
//		{
//		    Log.d("NSand.java", "Error opening output stream");
//		}
//    }

//==============================================================
//  sendGrain()
//==============================================================

//    // SendGrain with Byte Array
//    public void sendGrain (NGrain myGrain) {
//		Log.d("NSand.java", "sendGrain: (NGrain) ");
//		try {  
//		    // send app id
//		    streamOut.writeByte(myGrain.appID);
//		    // send command
//		    streamOut.writeByte(myGrain.command);
//		    // send data Type
//		    streamOut.writeByte(myGrain.dataType);
//		    // send data Length
//		    streamOut.writeInt(myGrain.dataLen);
//		    Log.d("NSand.java", "appID =" + myGrain.appID);
//		    Log.d("NSand.java", "commd =" + myGrain.command);
//		    Log.d("NSand.java", "dataType =" + myGrain.dataType);
//		    Log.d("NSand.java", "dLen  =" + myGrain.dataLen);
//	
//	
//		    // send data
//		    if (myGrain.dataType == NDataType.BYTE) {
//				Log.d("NSand.java", "data[] = BYTE (Deprecated, use CHAR or UINT8)");
//				for (int i=0; i<myGrain.dataLen; i++) {
//				    streamOut.writeByte(myGrain.bArray[i]);
//				}
//		    }
//	
//		    else if (myGrain.dataType == NDataType.UINT8) {
//			Log.d("NSand.java", "data[] = UINT8");
//				for (int i=0; i<myGrain.dataLen; i++) {
//				    streamOut.writeByte(myGrain.bArray[i]);
//				}
//		    }
//	
//		    else if (myGrain.dataType == NDataType.INT) {
//			Log.d("NSand.java", "data[] = INT (Deprecated, use INT32)");
//				for (int i=0; i<myGrain.dataLen; i++) {
//				    streamOut.writeInt(myGrain.iArray[i]);
//				}
//		    }
//	
//		    else if (myGrain.dataType == NDataType.INT32) {
//			Log.d("NSand.java", "data[] = INT32");
//				for (int i=0; i<myGrain.dataLen; i++) {
//				    streamOut.writeInt(myGrain.iArray[i]);
//				}
//		    }
//	
//		    else if (myGrain.dataType == NDataType.FLOAT) {
//			Log.d("NSand.java", "data[] = FLOAT (Deprecated, use FLOAT32)");
//				for (int i=0; i<myGrain.dataLen; i++) {
//				    streamOut.writeFloat(myGrain.fArray[i]);
//				}
//		    }
//	
//		    else if (myGrain.dataType == NDataType.FLOAT32) {
//			Log.d("NSand.java", "data[] = FLOAT32");
//				for (int i=0; i<myGrain.dataLen; i++) {
//				    streamOut.writeFloat(myGrain.fArray[i]);
//				}
//		    }
//	
//		    else {
//			Log.d("NSand.java", "NSAND: sendGrain(nGrain): WARNING:  Unknown NDataType: " + myGrain.dataType);
//		    }
//	
//		}
//		catch(IOException ioe) {  
//		    Log.d("NSand.java", "SAND write error");
//		}
//    }
//
//
//    // SendGrain with Byte Array
//    public void sendGrain (byte aID, byte cmd, byte dType, int dLen, byte[] bArray) {
//		Log.d("NSand.java", "sendGrain: ... args ... bArray[]");
//		try {  
//		    // send app id
//		    streamOut.writeByte(aID);
//		    // send command
//		    streamOut.writeByte(cmd);
//		    // send data Type
//		    streamOut.writeByte(dType);
//		    // send data Length
//		    streamOut.writeInt(dLen);
//	
//		    Log.d("NSand.java", "appID =" + aID);
//		    Log.d("NSand.java", "commd =" + cmd);
//		    Log.d("NSand.java", "dataType =" + dType);
//		    Log.d("NSand.java", "dLen  =" + dLen);
//	
//	
//		    for (int i=0; i<dLen; i++) {
//			streamOut.writeByte(bArray[i]);
//			Log.d("NSand.java", "BYTE:  " + bArray[i]);
//		    }
//		}
//		catch(IOException ioe) {  
//		    Log.d("NSand.java", "SAND write error");
//		}
//    }
//	
//	    // SendGrain with Int Array
//    public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, int[] iArray) {
//		Log.d("NSand.java", "sendGrain: ... args ... iArray[]");
//		try {  
//		    // send app id
//		    streamOut.writeByte(aID);
//		    // send command
//		    streamOut.writeByte(cmd);
//		    // send data Type
//		    streamOut.writeByte(dType);
//		    // send data Length
//		    streamOut.writeInt(dLen);
//	
//		    Log.d("NSand.java", "appID =" + aID);
//		    Log.d("NSand.java", "commd =" + cmd);
//		    Log.d("NSand.java", "dataType =" + dType);
//		    Log.d("NSand.java", "dLen  =" + dLen);
//	
//		    for (int i=0; i<dLen; i++) {
//		    	streamOut.writeInt(iArray[i]);
//		    }
//		}
//		catch(IOException ioe) {  
//		    Log.d("NSand.java", "SAND write error");
//		}
//    }
//
//    // SendGrain with Float Array
//    public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, float[] fArray) {
//		Log.d("NSand.java", "sendGrain: ... args ... fArray[]");
//		try {  
//		    // send app id
//		    streamOut.writeByte(aID);
//		    // send command
//		    streamOut.writeByte(cmd);
//		    // send data Type
//		    streamOut.writeByte(dType);
//		    // send data Length
//		    streamOut.writeInt(dLen);
//	
//		    Log.d("NSand.java", "appID =" + aID);
//		    Log.d("NSand.java", "commd =" + cmd);
//		    Log.d("NSand.java", "dataType =" + dType);
//		    Log.d("NSand.java", "dLen  =" + dLen);
//	
//		    for (int i=0; i<dLen; i++) {
//		    	streamOut.writeFloat(fArray[i]);
//		    }
//		}
//		catch(IOException ioe) {  
//		    Log.d("NSand.java", "SAND write error");
//		}
//    }
//
//    public void close()
//    {  
//		try {
//		    if (streamOut != null)  streamOut.close();
//		    if (socket    != null)  socket.close();
//		}
//		catch(IOException ioe) {
//		    Log.d("NSand.java", "Error closing...");
//		}
//    }

}
