package nomads.v210;

import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;
import nomads.v210.*;

//import java.applet.*;  // Maybe need

public class NSand
{
	// Not sure which of these need to be public_private
	//(Fields)
	private Socket socket              = null;
	private DataInputStream  streamIn   = null;
	private DataOutputStream streamOut = null;    
        private String    serverName = NGlobals.serverName;
	private int       serverPort = NGlobals.serverPort;

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

	// SendGrain with Byte Array
	public void sendGrain (NGrain myGrain) {
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

			// send data
			if (myGrain.dataType == NDataType.BYTE) {
				for (int i=0; i<myGrain.dataLen; i++) {
					streamOut.writeByte(myGrain.bArray[i]);
				}
			}

			if (myGrain.dataType == NDataType.INT) {
				for (int i=0; i<myGrain.dataLen; i++) {
					streamOut.writeInt(myGrain.iArray[i]);
				}
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
	}


	// SendGrain with Byte Array
	public void sendGrain (byte aID, byte cmd, byte dType, int dLen, byte[] bArray) {
		NGlobals.lPrint("sendGrain: ");
		try {  
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			for (int i=0; i<dLen; i++) {
				streamOut.writeByte(bArray[i]);
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
	}

	// SendGrain with Int Array
	public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, int[] iArray) {
		try {  
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			for (int i=0; i<dLen; i++) {
				streamOut.writeInt(iArray[i]);
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
	}

	// SendGrain with Float Array
	public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, float[] fArray) {
		try {  
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			for (int i=0; i<dLen; i++) {
				streamOut.writeFloat(fArray[i]);
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
	}

	// SendGrain with Double Array
	public  void sendGrain (byte aID, byte cmd, byte dType, int dLen, double[] dArray) {

		try {  
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			for (int i=0; i<dLen; i++) {
				streamOut.writeDouble(dArray[i]);
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
	}

	//Returns Grain appID, cmd, dT, dL, bA
	public  NGrain getGrain (byte appID) {
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

			//Int Array
			else if (dT == NDataType.INT) {
				int[] iA = new int[dL];

				for (int i=0; i< dL; i++) {
					iA[i] = streamIn.readInt();
					NGlobals.lPrint("INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dL, iA);
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

			//Double Array
			else if (dT == NDataType.DOUBLE) {
				double[] dA = new double[dL];

				for (int i=0; i< dL; i++) {
					dA[i] = streamIn.readDouble();
					NGlobals.lPrint("DOUBLE:  " + dA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dL, dA);
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
		System.out.println("NSand:getGrain: returning grain");
		grain.print();
		return grain;
	}



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

			//Int Array
			else if (dT == NDataType.INT) {
				int[] iA = new int[dL];

				for (int i=0; i< dL; i++) {
					iA[i] = streamIn.readInt();
					NGlobals.lPrint("INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dL, iA);
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

			//Double Array
			else if (dT == NDataType.DOUBLE) {
				double[] dA = new double[dL];

				for (int i=0; i< dL; i++) {
					dA[i] = streamIn.readDouble();
					NGlobals.lPrint("DOUBLE:  " + dA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dL, dA);
			}
		}
		catch(IOException ioe) {  
			System.out.println("SAND write error");
		}
		return grain;
	}

	//Connect, opens the socket, creates the streams
	public void connect() {
		connectSocket();
		openSocketStreams();
		//client.start();
	}


	public void connectSocket()
	{  
		System.out.println("Establishing connection. Please wait ...");
		try {  
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected");
		}
		catch(IOException ioe) {  
			System.out.println("socket connect exception: ");
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
		}
	}


	public void close()
	{  
		try {
			if (streamOut != null)  streamOut.close();
			if (socket    != null)  socket.close();
		}
		catch(IOException ioe) {
			System.out.println("Error closing...");
		}

	}


	public void stop() {
		System.out.println("NSand stop() called, not implemented...");
	}


}
