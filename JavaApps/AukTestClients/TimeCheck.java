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
import nomads.v210_auk.*;

public class TimeCheck implements Runnable {   

    private class NomadsAppThread extends Thread {
	TimeCheck client; //Replace with current class name

	public NomadsAppThread(TimeCheck _client) {
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

    NSand discussTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    public static void main(String args[])
    {

	TimeCheck  applet = null;
	applet = new TimeCheck();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("TimeCheck -> init()");

	discussTestSand = new NSand(); 
	discussTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	discussTestSand.sendGrain((byte)NAppID.OPERA_CLIENT, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	



    }	


    public TimeCheck() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("TimeCheck -> start()");

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
	NGlobals.cPrint("TimeCheck -> run()");
	int i = 0;

	int day, month, year;
	int second, minute, hour;
	GregorianCalendar date;

	while (true) {
	    String tString;

	    date = new GregorianCalendar();
	    
	    day = date.get(Calendar.DAY_OF_MONTH);
	    month = date.get(Calendar.MONTH);
	    year = date.get(Calendar.YEAR);
	    
	    second = date.get(Calendar.SECOND);
	    minute = date.get(Calendar.MINUTE);
	    hour = date.get(Calendar.HOUR);
	    
	    tString = new String("[TIMECHECK]  "+(month+1)+" (month)  "+day+" (day)  "+hour+" (hr)  "+minute+" (min)");

	    int tLen = tString.length();
	    byte[] tBytes = tString.getBytes();

	    try {
		NGlobals.cPrint("TimeCheck -> NSand.send()");
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
				runner.sleep(1000*60*60);
				//runner.sleep(1000*30);
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("TimeCheck -> handle()");

	grain = discussTestSand.getGrain();
	grain.print();
	System.out.println("TimeCheck handle()");

	    
    }



}
