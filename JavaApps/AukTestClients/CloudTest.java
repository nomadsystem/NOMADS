//
//  TEST CLIENT - CLOUD
//

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
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

    String cloudString[] = new String[31];

    int tNum, tNumL;

    public static void main(String args[])
    {

	CloudTest  applet = null;
	applet = new CloudTest();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("CloudTest -> init()");


	i=0;
	cloudString[i++] = new String("snow");
	cloudString[i++] = new String("ice");
	cloudString[i++] = new String("wind");
	cloudString[i++] = new String("water");
	cloudString[i++] = new String("alaska");
	cloudString[i++] = new String("climate change");
	cloudString[i++] = new String("children");
	cloudString[i++] = new String("polar bears");
	cloudString[i++] = new String("rising sea levels");
	cloudString[i++] = new String("hurricanes");
	cloudString[i++] = new String("extreme weather");
	cloudString[i++] = new String("renewable energy");
	cloudString[i++] = new String("solar");
	cloudString[i++] = new String("wind power");
	cloudString[i++] = new String("green energy");
	cloudString[i++] = new String("ocean temperature");
	cloudString[i++] = new String("we can fix this");
	cloudString[i++] = new String("floods");
	cloudString[i++] = new String("climate");
	cloudString[i++] = new String("famine");
	cloudString[i++] = new String("CO2");
	cloudString[i++] = new String("act local");
	cloudString[i++] = new String("think global");
	cloudString[i++] = new String("drive less");
	cloudString[i++] = new String("public transport");
	cloudString[i++] = new String("science");
	cloudString[i++] = new String("bicycles");
	cloudString[i++] = new String("clean air");
	cloudString[i++] = new String("clean water");
	cloudString[i++] = new String("pollution");
	cloudString[i++] = new String("knowledge = power");


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
	    String tString;
	    // if (i>500) {
	    // 	tString = new String("RESETTING CLOUD TEST STRING");
	    // 	i=0;
	    // }
	    // else {
	    // 	tString = new String("CLOUD TEST " + i++);
	    // }

	    tNumL = tNum;
	    System.out.println("tNumL = " + tNumL);
	    tNum = randNum.nextInt(15)+randNum.nextInt(16);
	    while (tNum == tNumL) {
		tNum = randNum.nextInt(15)+randNum.nextInt(16);
		System.out.println("tNum = " + tNum);
	    }
	    System.out.println("#tNum = " + tNum);
	    tString = new String(cloudString[tNum]);

	    int tLen = tString.length();
	    byte[] tBytes = tString.getBytes();

	    try {
		NGlobals.cPrint("CloudTest -> NSand.send()");
		cloudTestSand.sendGrain((byte)NAppID.OC_CLOUD, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(500);
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
