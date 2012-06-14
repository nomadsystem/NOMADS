import java.net.*;
import java.io.*;
import nomads.v200.*;

public class TextClient implements Runnable
{  private Socket socket              = null;
    private Thread thread              = null;
    private DataInputStream  console   = null;
    private DataInputStream  streamIn   = null;
    private DataOutputStream streamOut = null;
    private TextClientThread client    = null;

    public TextClient(int serverPort)
    {  System.out.println("Establishing connection. Please wait ...");
	try
	    {  socket = new Socket(globals.serverName, serverPort);
		System.out.println("Connected: " + socket);
		start();
	    }
	catch(UnknownHostException uhe)
	    {  System.out.println("Host unknown: " + uhe.getMessage()); }
	catch(IOException ioe)
	    {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
    }

    public void run()
    {  while (thread != null)
	    {  
		try   {   

		    String tString = console.readLine();
		    // This will be your app id

		    streamOut.writeByte(appID.DEBUG);
		    
		    // Command
		    streamOut.writeInt(command.SEND_MESSAGE);
		    
		    // Number of blocks, 1 in this case
		    streamOut.writeInt(1);
		    
		    // Data type of first block
		    streamOut.writeInt(dataType.BYTE);

		    // Number (how many) of this data type
		    int tLen = tString.length();
		    streamOut.writeInt(tLen);
		    
		    // The data 
		    globals.cPrint("sending:  (" + tLen + ") of this data type");

		    for (int i=0; i<tLen; i++) {
 			globals.cPrint("sending:  " + tString.charAt(i));
 			streamOut.writeByte(tString.charAt(i));
 		    }

		    globals.cPrint("sending: (" + tString + ")");

		    // Flush it
		    streamOut.flush();
		    
		}
		catch(IOException ioe)
		    {  System.out.println("TextClient socket writing error: " + ioe.getMessage());
			stop();
		    }
	    }
    }



    public void handle(byte incAppID)
    {  
	int incCmd, incNBlocks, incDType, incDLen;
	int i,j;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings

	try   {   
	    // Read in the COMMAND
	    incCmd = (int) streamIn.readByte();
	    incNBlocks = (int) streamIn.readByte();
	    incDType = (int) streamIn.readByte();
	    incDLen = (int) streamIn.readInt();

	    globals.cPrint("received:  [" + incAppID + "]" + "[" + incCmd + "]" + "[" + incNBlocks + "]" + "[" + incDType + "]" + "[" + incDLen + "]");

	    // Read each specific BLOCK
	    for (j = 0; j < incDLen; j++) {
		
		// For reference, not used by this app (since it's text based)
		if (incDType == dataType.INT) {
		    incIntData[j] = (int) streamIn.readInt();
		    globals.sPrint("received INT [" + j + "] " + incIntData[j]);
		}
		
		if (incDType == dataType.BYTE) {
		    byte tByte = streamIn.readByte();
		    incByteData[j] = tByte;

		    globals.sPrint("received tByte: " + tByte);
		    globals.sPrint("received incByteData [" + j + "]" + (char) incByteData[j]);

		}
	    }

	    if (incDType == dataType.BYTE) {
	     	String dataString = new String(incByteData);
	     	// globals.cPrint("received data: " + incByteData);
	     	globals.cPrint("received string " + dataString);
	     }
	    
	}
	catch(IOException ioe) {
	    System.out.println("TextClient socket reading error: " + ioe.getMessage());
	    stop();
	}
	
    }

    public void start() throws IOException
    {  console   = new DataInputStream(System.in);
	streamOut = new DataOutputStream(socket.getOutputStream());
	streamIn = new DataInputStream(socket.getInputStream());

	if (thread == null)
	    {  client = new TextClientThread(this, socket);
		thread = new Thread(this);                   
		thread.start();
	    }
    }

    public void stop()
    {  if (thread != null)
	    {  thread.stop();  
		thread = null;
	    }
	try
	    {  if (console   != null)  console.close();
		if (streamOut != null)  streamOut.close();
		if (streamIn != null)  streamIn.close();
		if (socket    != null)  socket.close();

	    }
	catch(IOException ioe)
	    {  System.out.println("Error closing ..."); }
	client.close();  
	client.stop();
    }

    public static void main(String args[])
    {  TextClient client = null;
	if (args.length != 1)
	    System.out.println("Usage: java TextClient port");
	else
	    client = new TextClient(Integer.parseInt(args[0]));
    }
}
