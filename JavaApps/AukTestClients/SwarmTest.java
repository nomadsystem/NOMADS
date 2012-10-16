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

public class SwarmTest implements Runnable {   

    private class NomadsAppThread extends Thread {
	SwarmTest client; //Replace with current class name

	public NomadsAppThread(SwarmTest _client) {
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

    NSand swarmTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    public static void main(String args[])
    {

	SwarmTest  applet = null;
	applet = new SwarmTest();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("SwarmTest -> init()");

	swarmTestSand = new NSand(); 
	swarmTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	swarmTestSand.sendGrain((byte)NAppID.OPERA_CLIENT, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	



    }	


    public SwarmTest() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("SwarmTest -> start()");

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
	NGlobals.cPrint("SwarmTest -> run()");
	int i = 0;
	int startX = 10+randNum.nextInt(500);
	int startY = 10+randNum.nextInt(500);
	int xOff, yOff;

	while (true) {

	    int[] xy = new int[2];
	    xOff = randNum.nextInt(200)-100;
	    yOff = randNum.nextInt(200)-100;
	    xy[0] = startX+xOff;
	    xy[1] = startY+yOff;
	    

	    try {
		NGlobals.cPrint("SwarmTest -> NSand.send()");
		swarmTestSand.sendGrain((byte)NAppID.JOC_POINTER, (byte)NCommand.SEND_SPRITE_XY, (byte)NDataType.INT32, 2, xy );
		runner.sleep(100);
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("SwarmTest -> handle()");

	grain = swarmTestSand.getGrain();
	grain.print();
	System.out.println("SwarmTest handle()");

	    
    }



}
