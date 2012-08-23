// NSand.java
// Nomads Auksalaq

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

public class NSand {
	// Not sure which of these need to be public_private
	// (Fields)
	private Socket socket = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private String serverName = NGlobals.serverName;
	private int serverPort = NGlobals.serverPortPT;

	// Maybe yes to these
	URL imgWebBase, webBase;

	public NSand() {
	}

	public NSand(Socket serverSock) {
		socket = serverSock;
	}

	public DataInputStream getInStream() {
		return streamIn;
	}

	public DataOutputStream getOutStream() {
		return streamOut;
	}

	public void setSock(Socket serverSock) {
		socket = serverSock;
	}

	// for Settings.java
	public String getServerName() {
		return serverName;
	}

	public int getServerPort() {
		return serverPort;
	}
	
	public byte getAppID () {
		byte appID = 0;

		try {
			// get app id
			appID = streamIn.readByte();
		} catch (IOException ioe) {
			Log.e("NSand.java", "getAppID: streamIn.readByte() error");
			Log.e("NSand.java", ioe.toString());
		}
		catch (NullPointerException npe) {
			Log.e("NSand.java", "getAppID: streamIn.readByte() error");
			Log.e("NSand.java", npe.toString());
		}
		return appID; 
	}

	// ==============================================================
	// sendGrain()
	// ==============================================================

	// SendGrain with Byte Array
	public void sendGrain(NGrain myGrain) {
		NGlobals.lPrint("NSand.java: sendGrain: (NGrain) ");
		try {
			// send app id
			streamOut.writeByte(myGrain.appID);
			// send command
			streamOut.writeByte(myGrain.command);
			// send data Type
			streamOut.writeByte(myGrain.dataType);
			// send data Length
			streamOut.writeInt(myGrain.dataLen);
			NGlobals.lPrint("NSand.java: sendGrain: appID =" + myGrain.appID);
			NGlobals.lPrint("NSand.java: sendGrain: commd =" + myGrain.command);
			NGlobals.lPrint("NSand.java: sendGrain: dataType =" + myGrain.dataType);
			NGlobals.lPrint("NSand.java: sendGrain: dLen  =" + myGrain.dataLen);

			// send data
			if (myGrain.dataType == NDataType.BYTE) {
				Log.d("NSand.java",
						"sendGrain: data[] = BYTE (Deprecated, use CHAR or UINT8)");
				for (int i = 0; i < myGrain.dataLen; i++) {
					streamOut.writeByte(myGrain.bArray[i]);
				}
			}

			else if (myGrain.dataType == NDataType.UINT8) {
				NGlobals.lPrint("NSand.java: sendGrain: data[] = UINT8");
				for (int i = 0; i < myGrain.dataLen; i++) {
					streamOut.writeByte(myGrain.bArray[i]);
				}
			}

			else if (myGrain.dataType == NDataType.INT) {
				Log.d("NSand.java",
						"sendGrain: data[] = INT (Deprecated, use INT32)");
				for (int i = 0; i < myGrain.dataLen; i++) {
					streamOut.writeInt(myGrain.iArray[i]);
				}
			}

			else if (myGrain.dataType == NDataType.INT32) {
				NGlobals.lPrint("NSand.java: sendGrain: data[] = INT32");
				for (int i = 0; i < myGrain.dataLen; i++) {
					streamOut.writeInt(myGrain.iArray[i]);
				}
			}

			else if (myGrain.dataType == NDataType.FLOAT) {
				Log.d("NSand.java",
						"sendGrain: data[] = FLOAT (Deprecated, use FLOAT32)");
				for (int i = 0; i < myGrain.dataLen; i++) {
					streamOut.writeFloat(myGrain.fArray[i]);
				}
			}

			else if (myGrain.dataType == NDataType.FLOAT32) {
				NGlobals.lPrint("NSand.java: sendGrain: data[] = FLOAT32");
				for (int i = 0; i < myGrain.dataLen; i++) {
					streamOut.writeFloat(myGrain.fArray[i]);
				}
			}

			else {
				NGlobals.lPrint("NSand.java: sendGrain: WARNING:  Unknown NDataType: "
						+ myGrain.dataType);
			}

		} catch (IOException ioe) {
			NGlobals.lPrint("NSand.java: sendGrain: SAND write error");
		}
	}

	// SendGrain with Byte Array
	public void sendGrain(byte aID, byte cmd, byte dType, int dLen,
			byte[] bArray) {
		NGlobals.lPrint("NSand.java: sendGrain: ... args ... bArray[]");
		try {
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			NGlobals.lPrint("NSand.java: sendGrain: appID =" + aID);
			NGlobals.lPrint("NSand.java: sendGrain: commd =" + cmd);
			NGlobals.lPrint("NSand.java: sendGrain: dataType =" + dType);
			NGlobals.lPrint("NSand.java: sendGrain: dLen  =" + dLen);

			for (int i = 0; i < dLen; i++) {
				streamOut.writeByte(bArray[i]);
				NGlobals.lPrint("NSand.java: sendGrain: BYTE:  " + bArray[i]);
			}
		} catch (IOException ioe) {
			NGlobals.lPrint("NSand.java: sendGrain: SAND write error");
		}
	}

	// SendGrain with Int Array
	public void sendGrain(byte aID, byte cmd, byte dType, int dLen, int[] iArray) {
		NGlobals.lPrint("NSand.java: sendGrain: ... args ... iArray[]");
		try {
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			NGlobals.lPrint("NSand.java: sendGrain: appID =" + aID);
			NGlobals.lPrint("NSand.java: sendGrain: commd =" + cmd);
			NGlobals.lPrint("NSand.java: sendGrain: dataType =" + dType);
			NGlobals.lPrint("NSand.java: sendGrain: dLen  =" + dLen);

			for (int i = 0; i < dLen; i++) {
				streamOut.writeInt(iArray[i]);
			}
		} catch (IOException ioe) {
			NGlobals.lPrint("NSand.java: sendGrain: SAND write error");
		}
	}

	// SendGrain with Float Array
	public void sendGrain(byte aID, byte cmd, byte dType, int dLen,
			float[] fArray) {
		NGlobals.lPrint("NSand.java: sendGrain: ... args ... fArray[]");
		try {
			// send app id
			streamOut.writeByte(aID);
			// send command
			streamOut.writeByte(cmd);
			// send data Type
			streamOut.writeByte(dType);
			// send data Length
			streamOut.writeInt(dLen);

			NGlobals.lPrint("NSand.java: sendGrain: appID =" + aID);
			NGlobals.lPrint("NSand.java: sendGrain: commd =" + cmd);
			NGlobals.lPrint("NSand.java: sendGrain: dataType =" + dType);
			NGlobals.lPrint("NSand.java: sendGrain: dLen  =" + dLen);

			for (int i = 0; i < dLen; i++) {
				streamOut.writeFloat(fArray[i]);
			}
		} catch (IOException ioe) {
			NGlobals.lPrint("NSand.java: sendGrain: SAND write error");
		}
	}

	// ==============================================================
	// getGrain()
	// ==============================================================

	// Returns Grain appID, cmd, dT, dLen, bA
	public NGrain getGrain(byte aID) {
		NGlobals.lPrint("NSand.java:  getGrain: aID: ");
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

			NGlobals.lPrint("NSand.java: getGrain: appID =" + appID);
			NGlobals.lPrint("NSand.java: getGrain: commd =" + cmd);
			NGlobals.lPrint("NSand.java: getGrain: dataType =" + dT);
			NGlobals.lPrint("NSand.java: getGrain: dLen  =" + dLen);

			// Detect array type in Grain
			// Byte array
			if (dT == NDataType.BYTE) {
				byte[] bA = new byte[dLen];

				for (int i = 0; i < dLen; i++) {
					bA[i] = streamIn.readByte();
					NGlobals.lPrint("NSand.java: getGrain: BYTE:  " + (char) bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				Log.d("NSand.java",
						"getGrain: creating grain with BYTEs (Deprecated, use CHAR or UINT8)");
			}

			// Byte array
			else if (dT == NDataType.UINT8) {
				byte[] bA = new byte[dLen];

				for (int i = 0; i < dLen; i++) {
					bA[i] = streamIn.readByte();
					NGlobals.lPrint("NSand.java: getGrain: UINT8:  " + bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				NGlobals.lPrint("NSand.java: getGrain: creating grain with UINT8s");
			}

			// Int Array
			else if (dT == NDataType.INT) {
				int[] iA = new int[dLen];

				for (int i = 0; i < dLen; i++) {
					iA[i] = streamIn.readInt();
					NGlobals.lPrint("NSand.java: getGrain: INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				Log.d("NSand.java",
						"getGrain: creating grain with INTs (Deprecated, use INT32)");

			}

			// Int Array 32
			else if (dT == NDataType.INT32) {
				int[] iA = new int[dLen];

				for (int i = 0; i < dLen; i++) {
					iA[i] = streamIn.readInt();
					NGlobals.lPrint("NSand.java: INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				NGlobals.lPrint("NSand.java: getGrain: creating grain with INT32s");
			}

			// Float Array
			else if (dT == NDataType.FLOAT) {
				float[] fA = new float[dLen];

				for (int i = 0; i < dLen; i++) {
					fA[i] = streamIn.readFloat();
					NGlobals.lPrint("NSand.java: getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				Log.d("NSand.java",
						"getGrain: creating grain with FLOATs (Deprecated, use FLOAT32)");
			}

			// Float Array 32
			else if (dT == NDataType.FLOAT32) {
				float[] fA = new float[dLen];

				for (int i = 0; i < dLen; i++) {
					fA[i] = streamIn.readFloat();
					NGlobals.lPrint("NSand.java: getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				NGlobals.lPrint("NSand.java: getGrain: creating grain with FLOAT32s");

			} else {
				Log.w("NSand.java",
						"getGrain: WARNING:  unknown SAND data type\n");
			}
		} catch (IOException ioe) {
			Log.e("NSand.java", "getGrain: SAND write error");
		}

		NGlobals.lPrint("NSand.java: getGrain: returning grain\n");

		return grain;
	}

	// Returns Grain appID, cmd, dT, dLen, bA
	public NGrain getGrain() {
		NGlobals.lPrint("NSand.java: getGrain");
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

			NGlobals.lPrint("NSand.java: getGrain: appID =" + appID);
			NGlobals.lPrint("NSand.java: getGrain: commd =" + cmd);
			NGlobals.lPrint("NSand.java: getGrain: dataType =" + dT);
			NGlobals.lPrint("NSand.java: getGrain: dLen  =" + dLen);

			// Detect array type in Grain
			// Byte array
			if (dT == NDataType.BYTE) {
				byte[] bA = new byte[dLen];

				for (int i = 0; i < dLen; i++) {
					bA[i] = streamIn.readByte();
					NGlobals.lPrint("NSand.java: getGrain: BYTE:  " + (char) bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				Log.d("NSand.java",
						"getGrain: creating grain with BYTEs (Deprecated, use CHAR or UINT8)");
			}

			// Byte array
			else if (dT == NDataType.UINT8) {
				byte[] bA = new byte[dLen];

				for (int i = 0; i < dLen; i++) {
					bA[i] = streamIn.readByte();
					NGlobals.lPrint("NSand.java: getGrain: UINT8:  " + (char) bA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, bA);
				NGlobals.lPrint("NSand.java: getGrain: creating grain with UINT8s");
			}

			// Int Array
			else if (dT == NDataType.INT) {
				int[] iA = new int[dLen];

				for (int i = 0; i < dLen; i++) {
					iA[i] = streamIn.readInt();
					NGlobals.lPrint("NSand.java: getGrain: INT:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				Log.d("NSand.java",
						"getGrain: creating grain with INTs (Deprecated, use INT32)");
			}

			// Int Array 32
			else if (dT == NDataType.INT32) {
				int[] iA = new int[dLen];

				for (int i = 0; i < dLen; i++) {
					iA[i] = streamIn.readInt();
					NGlobals.lPrint("NSand.java: getGrain: INT32:  " + iA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, iA);
				NGlobals.lPrint("NSand.java: getGrain: creating grain with INT32s");
			}

			// Float Array
			else if (dT == NDataType.FLOAT) {
				float[] fA = new float[dLen];

				for (int i = 0; i < dLen; i++) {
					fA[i] = streamIn.readFloat();
					NGlobals.lPrint("NSand.java: getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				Log.d("NSand.java",
						"getGrain: creating grain with FLOATs (Deprecated, use FLOAT32)");
			}

			// Float Array 32
			else if (dT == NDataType.FLOAT32) {
				float[] fA = new float[dLen];

				for (int i = 0; i < dLen; i++) {
					fA[i] = streamIn.readFloat();
					NGlobals.lPrint("NSand.java: getGrain: FLOAT:  " + fA[i]);
				}
				grain = new NGrain(appID, cmd, dT, dLen, fA);
				NGlobals.lPrint("NSand.java: getGrain: creating grain with FLOAT32s");

			} else {
				Log.w("NSand.java",
						"getGrain: WARNING:  unknown SAND data type\n");
			}

		} catch (IOException ioe) {
			Log.e("NSand.java", "getGrain: SAND write error");
		}
		return grain;
	}

	public void stop() {
		Log.w("NSand.java", "NSand stop() called, not implemented...");
	}

	// ==============================================================
	// ----------------------------------------------
	// ASyncTask classes added by PAT. 2012.08.07
	// ----------------------------------------------
	// ==============================================================
	// Connect()
	// ==============================================================

	public class Connect extends AsyncTask<Object, Void, Boolean> {
		Join j;
//		NomadsApp app;
		boolean connectStatus = false;

		@Override
		protected void onPreExecute() {
			// setup progress bar here
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// set caller variable to the object that called ConnectAsync
			j = (Join) params[0];
//			app = (NomadsApp) params[1];
//			app = NomadsApp.getInstance();

			NGlobals.lPrint("NSand.java: Establishing connection. Please wait ...");
			try {
				socket = new Socket(serverName, serverPort);
				NGlobals.lPrint("NSand.java: Connected");
				if (socket.isConnected()) {
					streamOut = new DataOutputStream(socket.getOutputStream());
					streamIn = new DataInputStream(socket.getInputStream());
					connectStatus = true;
					NGlobals.lPrint("NSand.java: Streams opened");
				}
			} catch (IOException ioe) {
//				socket = null;
				Log.e("NSand.java", "Connect.doInBackground(): IOException");
				Log.e("NSand.java", ioe.toString());
			}
			return connectStatus;
		}

		// @Override
		// protected void onProgressUpdate(Void v)
		// {
		//
		// }

		@Override
		protected void onPostExecute(Boolean connected) {
//			app.setConnectionStatus(connected);
			NomadsApp.getInstance().setConnectionStatus(connected);
			j.setConnectionStatus(connected);
		}
	}

	// //==============================================================
	// // Close()
	// //==============================================================
	//
	// public class Close extends AsyncTask<Void, Void, Void>
	// {
	// @Override
	// protected Void doInBackground(Void... v)
	// {
	//
	// NGlobals.lPrint("NSand.java: Attempting to close sand...");
	// try {
	// if (streamOut != null) streamOut.close();
	// if (socket != null) socket.close();
	// }
	// catch(IOException ioe) {
	// Log.e("NSand.java", "Error closing...");
	// }
	//
	// return null;
	// }
	// }

	// =====================================================================
	// These methods have been replaced by AsyncTask classes above
	// in compliance with Android v.3.x+
	// =====================================================================

	// //Connect, opens the socket, creates the streams
	// // Altered by PT 2012.07.29
	// public boolean connect()
	// {
	// boolean connectStatus = connectSocket();
	//
	// if (connectStatus) openSocketStreams();
	//
	// NGlobals.lPrint("NSand.java: NSand.java -> connect() -> connectStatus: " +
	// connectStatus);
	//
	// return connectStatus;
	// }
	//
	// public boolean connectSocket()
	// {
	// NGlobals.lPrint("NSand.java: Establishing connection. Please wait ...");
	// try
	// {
	// socket = new Socket(serverName, serverPort);
	// NGlobals.lPrint("NSand.java: Connected");
	// }
	// catch(IOException ioe) {
	// socket = null;
	// NGlobals.lPrint("NSand.java: socket connect exception");
	// }
	//
	// if (socket != null) return true;
	//
	// return false;
	// }
	//
	// public void openSocketStreams()
	// {
	// try
	// {
	// streamOut = new DataOutputStream(socket.getOutputStream());
	// streamIn = new DataInputStream(socket.getInputStream());
	// }
	// catch(IOException ioe)
	// {
	// NGlobals.lPrint("NSand.java: Error opening output stream");
	// }
	// }
	//
	public void closeConnection() {
		try {
			// ???
			// if (streamOut != null) streamOut.close();
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			NGlobals.lPrint("NSand.java: Error closing...");
		}
	}

}
