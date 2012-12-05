//
//  TEST CLIENT - DISCUSS
//

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import nomads.v210.*;

public class crasher implements Runnable {   

    int crashCount = 0;
    int maxCrashes = 10;

    private class NomadsAppThread extends Thread {
	crasher client; //Replace with current class name

	public NomadsAppThread(crasher _client) {
	    client = _client;
	}
	public void run()    {			
	    NGlobals.lPrint("NomadsAppThread -> run()");
	    while (true)  {
		client.handle();
		try {
		    nThread.sleep(1000);
		}
		catch (InterruptedException ie) {}
		crashCount++;
		if (crashCount > maxCrashes)  {
		    try {
			nThread.sleep(10000);
		    }
		    catch (InterruptedException ie) {}

		}
	    }
	}
    }

    private Thread       thread = null;

    NSand crasherTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    String crasherString[] = new String[31];

    int tNum, tNumL;

    public static void main(String args[])
    {

	crasher  applet = null;
	applet = new crasher();

    }

    public void exit() {
	nThread.stop();
	nThread = null;
	runner.stop();
	runner = null;
	System.exit(1);

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("crasher -> init()");

	crasherTestSand = new NSand(); 
	crasherTestSand.connect();


	i=0;
	crasherString[i++] = new String("Hello everyone");
	crasherString[i++] = new String("This is wonderful");
	crasherString[i++] = new String("This is cool");
	crasherString[i++] = new String("Will this be on the final?");
	crasherString[i++] = new String("I love techno");
	crasherString[i++] = new String("I love metal");
	crasherString[i++] = new String("I love Radiohead");
	crasherString[i++] = new String("I love DEADMAU5");
	crasherString[i++] = new String("I like Matmos");
	crasherString[i++] = new String("I like the Beatles");
	crasherString[i++] = new String("I hate when people play loud music");
	crasherString[i++] = new String("Hello from the back of the room");
	crasherString[i++] = new String("Hello from the front of the room");
	crasherString[i++] = new String("Hello from the library");
	crasherString[i++] = new String("Hello from my dorm room");
	crasherString[i++] = new String("This is giving me a lot to think about");
	crasherString[i++] = new String("Hi");
	crasherString[i++] = new String("Hello");
	crasherString[i++] = new String("Hi");
	crasherString[i++] = new String("Hello");
	crasherString[i++] = new String("Yo!");
	crasherString[i++] = new String("Woohoo");
	crasherString[i++] = new String("I feel bad for people who do not study");
	crasherString[i++] = new String("I like computers");
	crasherString[i++] = new String("Will this help us?");
	crasherString[i++] = new String("Will I get an A in this class?");
	crasherString[i++] = new String("How can I help?");
	crasherString[i++] = new String("What can we do?");
	crasherString[i++] = new String("Hmmm...");
	crasherString[i++] = new String("Ok");
	crasherString[i++] = new String("I think I understand");


	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	crasherTestSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

	String tString = new String("crasherX");
	int tLen = tString.length();

	byte[] tStringAsBytes = tString.getBytes();

	crasherTestSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.LOGIN, (byte)NDataType.UINT8, tLen, tStringAsBytes );

    }	

    public crasher() {  	    
	randNum = new Random();
	init();
	start();
    }

    public void start()  {  
	NGlobals.cPrint("crasher -> start()");

	runner = new Thread(this);
	runner.start();

    }

    public void stop() {  
	if (thread != null)  {  
	    thread.stop(); 
	    thread = null;
	}
    }

    public void run () {
	NGlobals.cPrint("crasher -> run()");
	int i = 0;

	while (true) {
	    String tString;
	    // if (i>500) {
	    // 	tString = new String("RESETTING CRASHER TEXT TAG TO 0");
	    // 	i=0;
	    // }
	    // else {
	    // 	tString = new String("CRASHERION TEST " + i++);
	    // }
	    // xxx
	    tNumL = tNum;
	    // System.out.println("tNumL = " + tNumL);
	    tNum = randNum.nextInt(31);
	    while (tNum == tNumL) {
		tNum = randNum.nextInt(31);
		// System.out.println("tNum = " + tNum);
	    }
	    // System.out.println("#tNum = " + tNum);
	    String tString2 = new String("crasherX ("+ crashCount + "): " );
	    crashCount++;


	    tString = new String(tString2 + crasherString[tNum]);

	    int tLen = tString.length();
	    byte[] tBytes = tString.getBytes();

	    try {
		if (crashCount < maxCrashes) {
		    NGlobals.csvPrint("crasher -> NSand.send()");
		    crasherTestSand.sendGrain((byte)NAppID.DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		    runner.sleep(1000);
		}
		else {
		    NGlobals.csvPrint("crasher -> NSand.sendC()");
		    crasherTestSand.sendGrainC((byte)NAppID.DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		    runner.sleep(10000);
		}
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.csvPrint("crasher -> handle()");

	grain = crasherTestSand.getGrain();

	byte incAppID = grain.appID;
	byte incCmd = grain.command;
	int len = grain.dataLen;
	//	String msg = new String(grain.bArray);

	//	System.out.println("CRASHER GOT:  +  " + msg);
	// System.out.println("CRASHER READ:  +  " + len);
	    
    }



}
