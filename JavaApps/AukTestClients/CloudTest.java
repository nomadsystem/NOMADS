//
//  Nomads Opera Main v.210
//

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.v210_auk.*;

public class CloudTest implements Runnable {   

    private class NomadsAppThread extends Thread {
	CloudTest client; //Replace with current class name

	public NomadsAppThread(CloudTest _client) {
	    client = _client;
	}
	public void run()    {			
	    NGlobals.lPrint("NomadsAppThread -> run()");
	    while (true)  {
		client.handle();
	    }
	}
    }

    private Thread       thread = null;

    NSand cloudTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    public static void main(String args[])
    {

	CloudTest  applet = null;
	applet = new CloudTest();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("CloudTest -> init()");

	cloudTestSand = new NSand(); 
	cloudTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	cloudTestSand.sendGrain((byte)NAppID.OPERA_CLIENT, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	



    }	


    public CloudTest() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("CloudTest -> start()");

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
	NGlobals.cPrint("CloudTest -> run()");
	int i = 0;

	while (true) {
	    String tString = new String("CLOUD TEST " + i++);
	    int tLen = tString.length();
	    byte[] tBytes = tString.getBytes();

	    try {
		NGlobals.cPrint("CloudTest -> NSand.send()");
		cloudTestSand.sendGrain((byte)NAppID.OC_CLOUD, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(10);
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("CloudTest -> handle()");

	grain = cloudTestSand.getGrain();
	grain.print();
	System.out.println("CloudTest handle()");

	    
    }



}
