//
//  NOMADS Poll Display
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
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.awt.RenderingHints;
import nomads.v210.*;

public class PollDisplay extends JApplet implements MouseListener, MouseMotionListener, ActionListener, Runnable {   


    public class sElt {
	int x, y, xOff, tW, tH;
    }    

    String imgPrefix;
    URL imgWebBase;

    int     MAX_THREADS = 100000;

    Image[] bgImages = new Image[50];
    Image bgImage;

    public enum pollType { 
	BLANK, YESNO, TEN;
    }
    pollType pType = pollType.BLANK;

    int i;
    int width,height,sHeight,twidth,theight,fontSize, centerX, centerY;
    Font textFont;
    int startFontSize, minFontSize, maxFontSize;

    int tRow, tCol,rows,cols,tRows, tCols;

    int x,y,w,h,dx,dy,dw,dh, sNum;
    int wait;
    int pbi;

    int stringLength;

    Color textColor,backGroundColor;

    Color[] colColors = new Color[11];
    Color[] nomadsColors = new Color[11];

    JButton	clearButton;
    String chatLines[];

    int numChatLines = 40;
    int activeChatLines = 0;

    int mx, my; // recent mouse coords
    boolean isMouseDraggingBox = false;

    String text;

    int typeOfQuestionSubmitted = 0;

    // used for totaling purposes for yes no format
    int yesTotal = 0;
    int noTotal = 0;
    double yPct, nPct;

    int totalYesAndNo = 0;
    double yesPer = 0, noPer = 0;
    double difference = 0, finAns = 0;

    //used for response averaging purposes 1 to 10 question format
    int resp = 0;
    double runningTotal = 0;
    double count = 0;
    double average = 0;
    DecimalFormat roundAverage;

    // List of all our elements
    sElt sprites[];
    //  Temporary placeholder
    sElt tSprite, cSprite;

    int nCols, nRows, cRow, cCol, cWidth, rHeight, colAvg, respAvg, colSum, numColors, colColorRatio, bgColorRatio;

    int tileX[];
    int tileY[];
    int colVals[]; 

    int sCenterX[];
    int sCenterY[];
    int sOff;

    Random randGen;
    BufferedImage offScreen;
    Graphics2D offScreenGrp;
    Image player;

    NSand mySand;

    // ###########################

    public static void main(String args[])
    {
	/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
	PollDisplay  applet = new PollDisplay();

	Frame appletFrame = new Frame("pollDisplay");

	appletFrame.add(applet);
	appletFrame.resize(600,600);
	appletFrame.show();
    }

    public void setColors(int alpha) {

	int i=0;
	nomadsColors[i++] = new Color(0,0,0); //dummy so array indices line up
	nomadsColors[i++] = new Color(94,41,28); //muddy red = 1
	nomadsColors[i++] = new Color(158,55,33);
	nomadsColors[i++] = new Color(145,86,65);
	nomadsColors[i++] = new Color(187,137,44);
	nomadsColors[i++] = new Color(191,140,44);
	nomadsColors[i++] = new Color(233,158,37);
	nomadsColors[i++] = new Color(242,197,126);
	nomadsColors[i++] = new Color(254,205,129);
	nomadsColors[i++] = new Color(249,241,131);
	nomadsColors[i++] = new Color(249,245,220); //light yellow = 10

	i=0;
	colColors[i++] = new Color(0,0,0); //dummy so array indices line up
	colColors[i++] = new Color(94,41,28); //muddy red = 1
	colColors[i++] = new Color(158,55,33);
	colColors[i++] = new Color(145,86,65);
	colColors[i++] = new Color(187,137,44);
	colColors[i++] = new Color(191,140,44);
	colColors[i++] = new Color(233,158,37);
	colColors[i++] = new Color(242,197,126);
	colColors[i++] = new Color(254,205,129);
	colColors[i++] = new Color(249,241,131);
	colColors[i++] = new Color(249,245,220); //light yellow = 10
	numColors = i-1;
	colColorRatio = 1+(int)Math.ceil(nRows/numColors);

    }

    // init () ============================================================================================

    public void init(NSand inSand)
    {   

	mySand = inSand;
		
	int i;

	NGlobals.cPrint("init() ...\n");

	count = 0;
	sOff = 0;
	sOff = 0;

	yPct = 0;
	nPct = 0;

	try { 
	    imgWebBase = new URL(imgPrefix); 
	} 
	catch (Exception e) {}

	width = 800;
	height = 800;
	sHeight = (int)(height*0.85);

	offScreen = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	offScreenGrp = (Graphics2D) offScreen.getGraphics();
	//	backgroundIce = getImage(imgWebBase,"BackgroundDisplay1.jpg");
		

	centerX = (width/2);
	centerY = (height/2); 

	sprites = new sElt[1000];  // maxSprites = 1000;

	for (i=0;i<1000;i++) {
	    sprites[i] = new sElt();
	}

	sCenterX = new int[10];  // maxSpriteSlots = 10;
	sCenterY = new int[10];

	NGlobals.cPrint("width = " + width + "height = " + height);

	sNum = 0;

	Color bgColor = new Color(84,31,18); //muddy red = 1
	//	getContentPane().setBackground(bgColor);	
	// getContentPane().setBackground(Color(145,86,65));
	// getContentPane().setBackground(Color.blue);	

	offScreenGrp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	x = width / 2 - 20;
	y = height / 2 - 20;

	nCols = 10;  // Width (x)
	nRows = 20;  // Height (y)

	colVals = new int[200];

	setColors(60);

	textFont = new Font("Helvetica", Font.PLAIN, 18);

	randGen = new Random();
	int randCVal;
	// FOR TESTING:  seed columns with random values
	colAvg = 1;
	respAvg = 0;
	colSum = 0;
	if (1 < 0) {
	    for (i=0;i<nCols+1;i++) {
		randCVal = randGen.nextInt(40);
		if (randCVal > nRows) {
		    nRows = randCVal; // xxx
		}
		colVals[i] = randCVal;
		colSum += randCVal;
		NGlobals.cPrint("randCVal = " + randCVal + "\n");
	    }
	}

	colColorRatio = 1+(int)Math.ceil(nRows/numColors);
	bgColorRatio = 1+(int)Math.ceil(nRows/numColors);

	// respAvg = (int)(colSum/(nCols));
	// colAvg = (int)respAvg/colColorRatio;

	colAvg = 1;

	if (colAvg >= numColors) {
	    colAvg = numColors;
	}
	if (colAvg < 1) {
	    colAvg = 1;
	}

	NGlobals.cPrint("respAvg = " + respAvg + " colAvg = " + colAvg);
	addMouseListener(this);
	addMouseMotionListener(this);


	//	getContentPane().setBackground(nomadsColors[1]);
	//	offScreenGrp.setBackground(Color.black);

	// byte d[] = new byte[1];
	// d[0] = 0;
	// mySand.sendGrain((byte)NAppID.DISPLAY_POLL, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );		

    }	

    public void reset()
    {   

	int i;

	NGlobals.cPrint("reset() ...\n");

	count = 0;
	sOff = 0;
	sOff = 0;

	yPct = 0;
	nPct = 0;

	width = getSize().width;
	height = getSize().height;
	sHeight = (int)(height*0.85);

	centerX = (width/2);
	centerY = (height/2); 

	for (i=0;i<1000;i++) {
	    sprites[i] = new sElt();
	}

	for (i=0; i<10; i++) {
	    sCenterX[i] = 0;  // maxSpriteSlots implied = 10
	    sCenterY[i] = 0;
	}

	sNum = 0;

	x = width / 2 - 20;
	y = height / 2 - 20;

	nCols = 10;  // Width (x)
	nRows = 20;  // Height (y)

	for (i=0; i<200; i++) {
	    colVals[i] = 0;
	}

	setColors(60);

	colAvg = 1;
	respAvg = 0;
	colSum = 0;

	colColorRatio = 1+(int)Math.ceil(nRows/numColors);
	bgColorRatio = 1+(int)Math.ceil(nRows/numColors);

	respAvg = (int)(colSum/(nCols));
	// colAvg = (int)respAvg/colColorRatio;

	colAvg = 1;

	repaint();

    }	



    // For swarm display

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
	mx = e.getX();
	my = e.getY();
	if (x < mx && mx < x + 40 && y < my && my < y + 40) {
	    isMouseDraggingBox = true;
	}
	e.consume();
    }

    public void mouseReleased(MouseEvent e) {
	/*if (isMouseDraggingBox) {
	  try {
	  double myx = (mx - (width / 2)) / ((double) width * 3);
	  double myy = (my - (height / 2)) / ((double) height * 3);
	  String towrite = "move" + " " + personnum + " " + myx + " "
	  + myy;
	  NGlobals.cPrint("Byte is " + app_id.STUDENT_SAND_POINTER + " and write is "
	  + towrite);
	  streamOut.writeByte(app_id.STUDENT_SAND_POINTER);
	  streamOut.writeUTF(towrite);
	  } catch (IOException ioe) {
	  NGlobals.cPrint("Error writing...");
	  }
	  }*/
	isMouseDraggingBox = false;
	e.consume();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if (isMouseDraggingBox) {
	    // get the latest mouse position
	    int new_mx = e.getX();
	    int new_my = e.getY();

	    // displace the box by the distance the mouse moved since the last
	    // event
	    // Note that "x += ...;" is just shorthand for "x = x + ...;"
	    if (new_mx < 5)
		new_mx = 5;
	    if (new_mx > 890)
		new_mx = 890;
	    if (new_my < 5)
		new_my = 5;
	    if (new_my > 590)
		new_my = 590;

	    x += new_mx - mx;
	    y += new_my - my;

	    // update our data
	    mx = new_mx;
	    my = new_my;


	    //	    repaint();
	    //  e.consume();
	}
    }

    // ------------------------------------------------------------------------------------------------
    // BEGIN handle()
    // ------------------------------------------------------------------------------------------------

    public void handle(NGrain inGrain) //bite, s
    {
	int i,j,fc,sc,x,y;
	String temp,thread,input, tTest;
	int THREAD_ID;
	float xput,yput;
	int tVal;

	NGrain grain;

	grain = inGrain;

	byte incAppID = grain.appID;
	byte incCmd = grain.command;
		

	NGlobals.cPrint("handle() =========================================================================================");
	NGlobals.dtPrint("handle() =========================================================================================");

	temp = "";
	resp = 0;

	NGlobals.cPrint("PD: Inside pollDisplay handle");
	NGlobals.dtPrint("PD: Inside pollDisplay handle");


	//get question from teacher poll app, and type of question submitted
	if (incAppID == NAppID.TEACHER_POLL)  {
	    NGlobals.dtPrint("PD: Got AppID TEACHER POLL");
	    NGlobals.dtPrint("PD: Inside pollDisplay handle");

	    //when a new question is asked, clear old result totals
	    // 1 to 10 number data variables
	    runningTotal = 0;
	    count = 0;
	    average = 0;

	    // yes no number data variables
	    yesTotal = 0;
	    noTotal = 0;
	    totalYesAndNo = 0;
	    yesPer = 0;
	    noPer = 0;
	    difference = 0;
	    finAns = 0;

	    //getContentPane().setBackground(nomadsColors[0]);	   	  	

	    typeOfQuestionSubmitted = incCmd; //Get question type from incoming command
	    if (typeOfQuestionSubmitted == NCommand.QUESTION_TYPE_YES_NO) {
		NGlobals.cPrint("PD: YES-NO Question");
		NGlobals.dtPrint("PD: YES-NO Question");
		reset(); 
		sCenterX[0] = (int)(width*0.25);
		sCenterY[0] = height/2;
		sCenterX[1] = (int)(width*0.75);
		sCenterY[1] = height/2;
		pType = pollType.YESNO;
	    }
	    if (typeOfQuestionSubmitted == NCommand.QUESTION_TYPE_ONE_TO_TEN) {
		reset();
		NGlobals.cPrint("PD: 1-10 Question");
		NGlobals.dtPrint("PD: 1-10 Question");
		pType = pollType.TEN;
	    }

	    NGlobals.cPrint("PD: ----------------------");

	    NGlobals.cPrint("PD: typeOfQuestionSubmitted " + typeOfQuestionSubmitted);
	    NGlobals.cPrint("PD: _--------------------");
	    //				response = s; // DISPLAY

	    NGlobals.cPrint("PD: setting poll question for POLL DISPLAY");
	    // Set question on display here
	    //	 Q.setText("<html><h2 style='color:black'>" + response + "</h2></html>");	// DISPLAY
	}

	//get results from student poll apps
	else if (incAppID == NAppID.STUDENT_POLL) {	 		 
	    NGlobals.dtPrint("PD: Getting Message from Student Poll");
	    int response =  grain.iArray[0];

	    // YESNO ============================================================----------
	    if (typeOfQuestionSubmitted == NCommand.QUESTION_TYPE_YES_NO) {
		NGlobals.dtPrint("PD: SP: YES-NO ----------------------------------------------------------");
		pType = pollType.YESNO;

		if (response == 1)  {
		    NGlobals.dtPrint("PD: yes came in");
		    yesTotal++;

		    float sScale=0;
		    if (yesTotal < 25) {
			sScale = (float)(yesTotal*1.1);
		    }
		    if (yesTotal < 50) {
			sScale = (float)(yesTotal);
		    }
		    if (yesTotal < 100) {
			sScale = (float)(yesTotal*0.9);
		    }
		    if (yesTotal < 200) {
			sScale = (float)(yesTotal*0.8);
		    }


		    int tLen = 10 + (int)(sScale + randGen.nextInt(10));
		    int tAng = (int)yesTotal + randGen.nextInt(6);

		    //			sprites[sNum].xOff = 10 - randGen.nextInt(20);
		    // sprites[sNum].tW = 20 + randGen.nextInt(20);
		    // sprites[sNum].tH = 20 + randGen.nextInt(20);
		    sprites[sNum].tW = 10 + randGen.nextInt(7);
		    sprites[sNum].tH = 10 + randGen.nextInt(2);

		    sprites[sNum].x = sCenterX[0] + (int)(Math.cos(tAng)*tLen);
		    sprites[sNum].y = sCenterY[0]+ (int)(Math.sin(tAng)*tLen);
		    x = sprites[sNum].x;
		    y = sprites[sNum].y;


		    if ((x > sCenterX[0]) && (y > sCenterY[0])) {
			sprites[sNum].xOff = 10;
			// sprites[sNum].xOff = 10+(int)((5.0 * (Math.cos(tAng))));
		    }
		    if ((x > sCenterX[0]) && (y < sCenterY[0])) {
			sprites[sNum].xOff = -10;
			// sprites[sNum].xOff = -10-(int)(-1*(5.0 * (Math.cos(tAng))));
		    }
		    if ((x < sCenterX[0]) && (y < sCenterY[0])) {
			sprites[sNum].xOff = 10;
			// sprites[sNum].xOff = 10+(int)(-1*(5.0 * (Math.cos(tAng))));
		    }
		    if ((x < sCenterX[0]) && (y > sCenterY[0])) {
			sprites[sNum].xOff = -10;
			// sprites[sNum].xOff = -10-(int)((5.0 * (Math.cos(tAng))));
		    }


		    NGlobals.dtPrint("setting sprite " + sNum + "at (" + x + "," + y + ")");
		    sNum++;
		    NGlobals.dtPrint("PD: yesTotal " + yesTotal);
		}

		if (response == 0) {
		    NGlobals.dtPrint("PD: no came in");
		    noTotal++;

		    float sScale=0;
		    if (noTotal < 25) {
			sScale = (float)(noTotal*1.1);
		    }
		    if (noTotal < 50) {
			sScale = (float)(noTotal);
		    }
		    if (noTotal < 100) {
			sScale = (float)(noTotal*0.9);
		    }
		    if (noTotal < 200) {
			sScale = (float)(noTotal*0.8);
		    }


		    int tLen = 10 + (int)(sScale + randGen.nextInt(10));
		    int tAng = (int)noTotal + randGen.nextInt(6);

		    //			sprites[sNum].xOff = 10 - randGen.nextInt(20);
		    // sprites[sNum].tW = 20 + randGen.nextInt(20);
		    // sprites[sNum].tH = 20 + randGen.nextInt(20);
		    sprites[sNum].tW = 10 + randGen.nextInt(7);
		    sprites[sNum].tH = 10 + randGen.nextInt(2);

		    sprites[sNum].x = sCenterX[1] + (int)(Math.cos(tAng)*tLen);
		    sprites[sNum].y = sCenterY[1]+ (int)(Math.sin(tAng)*tLen);
		    x = sprites[sNum].x;
		    y = sprites[sNum].y;


		    if ((x > sCenterX[1]) && (y > sCenterY[1])) {
			sprites[sNum].xOff = 10;
			// sprites[sNum].xOff = 10+(int)((5.0 * (Math.cos(tAng))));
		    }
		    if ((x > sCenterX[1]) && (y < sCenterY[1])) {
			sprites[sNum].xOff = -10;
			// sprites[sNum].xOff = -10-(int)(-1*(5.0 * (Math.cos(tAng))));
		    }
		    if ((x < sCenterX[1]) && (y < sCenterY[1])) {
			sprites[sNum].xOff = 10;
			// sprites[sNum].xOff = 10+(int)(-1*(5.0 * (Math.cos(tAng))));
		    }
		    if ((x < sCenterX[1]) && (y > sCenterY[1])) {
			sprites[sNum].xOff = -10;
			// sprites[sNum].xOff = -10-(int)((5.0 * (Math.cos(tAng))));
		    }


		    NGlobals.dtPrint("setting sprite " + sNum + "at (" + x + "," + y + ")");
		    sNum++;

		    NGlobals.dtPrint("PD: noTotal " + noTotal);
		}

		//convert yes no results to an average to map to color
		totalYesAndNo = yesTotal + noTotal;
		yesPer = (double) yesTotal / totalYesAndNo;
		noPer = (double) noTotal / totalYesAndNo;

		NGlobals.dtPrint("PD: totalYesAndNo " + totalYesAndNo);

		difference = yesPer - noPer;
		difference *= 10;


		NGlobals.cPrint("PD: difference " + difference);

		finAns = 5 + difference;


		NGlobals.cPrint("PD: finAns " + finAns);

		// if it exceeds a boundary, it is set to the boundary value
		if (finAns < 1)
		    finAns = 1;

		if (finAns > 10)
		    finAns = 10;


		NGlobals.cPrint("PD: finAns " + finAns);
		NGlobals.cPrint("PD: (int)Math.round(finAns) " + (int)Math.round(finAns));

		double yFact = ((double)(yesTotal*numColors)/(double)sNum);
		double nFact = ((double)(noTotal)/(double)sNum);

		colAvg = (int)(yFact + nFact);

		NGlobals.cPrint("colAvg = " + colAvg + "\n");	    
		if (colAvg > numColors) {
		    colAvg = numColors;
		}
		if (colAvg < 1) {
		    colAvg = 1;
		}

		//		getContentPane().setBackground(nomadsColors[colAvg]);

		//show yes no totals in applet too
		// results.setBackground(theColors[(int)Math.round(finAns)]);
		// results.setText("<html><center><h2 style='color:black'>Yes: " + yesTotal + " No: " + noTotal + 
		//	    " <br>(" + Math.round(yesTotal + noTotal) + ")</center></h2></html>");	

		//show results with color in bottom of applet
		// bottom.setBackground(theColors[(int)Math.round(finAns)]);   // DISPLAY

		// dispResults.setBackground(theColors[(int)Math.round(finAns)]);  // DISPLAY
		// eastResults.setBackground(theColors[(int)Math.round(finAns)]);  // DISPLAY
		// westResults.setBackground(theColors[(int)Math.round(finAns)]);  // DISPLAY
		// southResults.setBackground(theColors[(int)Math.round(finAns)]);  // DISPLAY

	    }

	    // 1 to 10 =================================================================-----------
	    if (typeOfQuestionSubmitted == NCommand.QUESTION_TYPE_ONE_TO_TEN) {
		pType = pollType.TEN;

		NGlobals.dtPrint("PD: SP: 1-to-10 ----------------------------------------------------------");
		resp = response; // DISPLAY
		tVal = colVals[resp];
		tVal++;
		colVals[resp] = tVal;

		NGlobals.cPrint("resp: " + resp);
		if (tVal >= nRows) {
		    nRows = tVal;
		}
		colSum += resp;
		NGlobals.cPrint("colSum: " + colSum);

		colColorRatio = 1+(int)Math.ceil(nRows/numColors);
		respAvg = (int)(colSum/(nCols));
		// colAvg = (int)respAvg/colColorRatio;

		NGlobals.cPrint("colColorRatio: " + colColorRatio);


		runningTotal += resp;
		count++;

		NGlobals.cPrint("PD: runningTotal / count " + (runningTotal / count));
		average = runningTotal / count;
		DecimalFormat roundAverage = new DecimalFormat("#.##");//use to round to 2 decimal places
		colAvg = (int)average;

		if (colAvg > numColors) {
		    colAvg = numColors;
		}
		if (colAvg < 1) {
		    colAvg = 1;
		}

		//		getContentPane().setBackground(nomadsColors[colAvg]);
		NGlobals.cPrint("Setting background to: colAvg: " + colAvg);

		// show results average in applet too
		// results.setBackground(theColors[(int)Math.round(average)]);
		// results.setText("<html><h2>WTF</html>");
		// results.setText("<html><center><h2 style='color:black'>Average: " + roundAverage.format(average) + " <br>(" + Math.round(count) + ")</center></h2></html>");	

		// show results with color in bottom of applet
		// bottom.setBackground(theColors[(int)Math.round(average)]);
		// dispResults.setBackground(theColors[(int)Math.round(average)]);
		// eastResults.setBackground(theColors[(int)Math.round(average)]);
		// westResults.setBackground(theColors[(int)Math.round(average)]);
		// southResults.setBackground(theColors[(int)Math.round(average)]);

	    }

	}
	else {
	    NGlobals.cPrint("PD: pollDisplay says Extraneous information");
	}


	NGlobals.cPrint("PD: resp " + resp);
	NGlobals.cPrint("PD: Running total " + runningTotal);
	NGlobals.cPrint("PD: count " + count);
	NGlobals.cPrint("PD: average " + average);
		

	//****STK 6/19/12 MONITOR app not currently implemented
	//		if (appID == NAppID.MONITOR) {
	//			if (text.equals("CHECK")) {
	//				try {
	//					streamOut.writeByte((byte)app_id.MONITOR);
	//					streamOut.writeUTF("PING");
	//				}
	//				catch(IOException ioe) {
	//					NGlobals.cPrint("Error writing to output stream: ");
	//				}
	//			}	 
	//		}
	if (incAppID == NAppID.TEACHER_POLL) {
	    repaint();
	}

	repaint();
    }


    // END handle()
    // ------------------------------------------------------------------------------------------------

    public void actionPerformed( ActionEvent ae )
    {
	Object obj = ae.getSource();
	//	if( obj == clearButton ) {
	//}

    }

    // DT 6/30/10:  not sure we need these anymore

    public void start() {
	NGlobals.cPrint("PD: start() ...\n");
	repaint();
    }
    public void run () {
	NGlobals.cPrint("PD: run() ...");
	if (i == 1) {
	}
    }

    public int mYS(int y) {
	return sHeight-y;
    }
    public int mY(int y) {
	return height-y;
    }

    // paint() ===================================================================================================

    public void paint(Graphics g) {
	int tx, ty, r,gr,b,a;
	int ksize = 20;
	int ssize = 10;
	int len1,len2;

	int i,j;

	int colW = width/(nCols+1);
	int rowH = sHeight/(nRows+2);
	int tilerW = (int)(colW*0.9);
	int tilerH = 40;
	int tilerO = (int)(tilerW*0.2);
	int xOff = tilerO;
	int xpoints[];
	int ypoints[];
	int tVal=0;
	int cSet=0;
	int tColor;

	super.paint(g);

	xpoints = new int[4];
	ypoints = new int[4];

	x = 0;
	y = 0;

	NGlobals.dtPrint("paint() ...\n");
	double dAvg = (average/10.0) * 20.0;
	NGlobals.dtPrint("average = " + average);
	int tAvg = (int)dAvg;
	NGlobals.dtPrint("tAvg = " + tAvg);
	offScreenGrp.drawImage(bgImages[tAvg], 0, 0, width, height, this);

	// offScreenGrp.setColor(nomadsColors[colAvg]);
	// offScreenGrp.fillRect (0, 0,width, height);
	//	g.dispose();

	// getContentPane().setBackground(Color.blue);	
	// getContentPane().setBackground(Color(145,86,65));
	offScreenGrp.setFont(textFont);

	// getContentPane().setBackground(Color.blue);	
	// getContentPane().setBackground(Color(145,86,65));

	// g.setColor(colColors[9]);	

	// for (cRow=1; cRow<16; cRow++) {
	//     x = cCol*colW;
	//     y = mY(cRow*rowH);

	//     g.fillRect(x,y,tilerW,tilerH);
	// }

	// 1 to 10 ====================================================================----------------


	if (pType == pollType.TEN) {

	    sNum = 0;
	    NGlobals.cPrint("rendering 1-10 columns");

	    // getContentPane().setBackground(nomadsColors[colAvg]);

	    for(cCol=1; cCol<=nCols; cCol++) {

		tVal = colVals[cCol];

		// NGlobals.cPrint("cCol = " + cCol);	    
		// NGlobals.cPrint("tVal = " + tVal);	    


		for (cRow=1; cRow<=tVal; cRow++) {
		    x = cCol*colW;
		    y = mYS(cRow*rowH);

		    xpoints[0] = x-(int)tilerW/2+xOff;
		    xpoints[1] = (x-(int)tilerW/2);
		    xpoints[2] = x+(int)(tilerW/2);
		    xpoints[3] = (x+(int)tilerW/2)+xOff;

		    // = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};

		    ypoints[0] = y-(int)tilerH/2;
		    ypoints[1] = y+(int)tilerH/2;
		    ypoints[2] = y+(int)tilerH/2;
		    ypoints[3] = y-(int)tilerH/2;

		    // int ypoints[4] = {y+(int)(ksize/1), y-(int)(ksize/1), y+(int)(ksize/1), y+(int)(ksize*3)};

		    cSet = 1+(int)(cRow/colColorRatio);
		    if (cSet > numColors) {
			cSet = numColors;
		    }
		    //  NGlobals.cPrint("cRow = " + cRow);
		    //  NGlobals.cPrint("tVal = " + tVal);	  
		    //  NGlobals.cPrint("colColorRatio = " + colColorRatio + "\n");	  
		    //  NGlobals.cPrint("cSet = " + cSet + "\n");	  

		    offScreenGrp.setColor(colColors[cSet]);
		    offScreenGrp.fillPolygon(xpoints, ypoints, xpoints.length);
		    offScreenGrp.setColor(Color.black);
		    offScreenGrp.drawPolygon(xpoints, ypoints, xpoints.length);
		    sNum++;
		}
		if (cCol == resp) {
		    offScreenGrp.setColor(Color.white);
		    offScreenGrp.fillPolygon(xpoints, ypoints, xpoints.length);
		}
	    }


	    offScreenGrp.setColor(Color.black);

	    for (i=1;i<=nCols;i++) {
		x = i*colW;
		offScreenGrp.setColor(Color.black);
		offScreenGrp.drawString(Integer.toString(i), x, mY(70));
	    }

	    DecimalFormat rounder = new DecimalFormat("#.##");//use to round to 2 decimal places
	    String tString = rounder.format(average);
	    NGlobals.dtPrint("average = " + average);
	    offScreenGrp.drawString("Average:  " + tString, centerX-150, mY(50));
	    offScreenGrp.drawString("Responses:  " + sNum, centerX+50, mY(50));

	    NGlobals.cPrint("colAvg = " + colAvg + "\n");	    

	    // g.setColor(Color.red);
	    // g.drawImage(backgroundIce, 0, 0, width, height, this);
	    // g.fillRect(10,10,50,50);
	    // g.setColor(colColors[avg]);
	    // g.fillRect(30,30,50,50);

	    // g.rotate();                     // Rotate 45 degrees

	    //g.dispose();

	    //	setBackground(Color.black);

	    // g.fillPolygon(xpoints, ypoints, 10);
	    //	    g.fillRect(tx, ty, 10, 10);



	}

	// yes/no ================================================================----------

	if (pType == pollType.YESNO) {

	    NGlobals.cPrint("rendering YESNO sprites");

	    tColor = 0;

	    for (i=0;i<sNum;i++) {

		x = sprites[i].x;
		y = sprites[i].y;

		xOff = sprites[i].xOff;
		tilerW = sprites[i].tW;
		tilerH = sprites[i].tH;

		// int xpoints[] = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};
		// int ypoints[] = {y+(int)(ksize/1), y-(int)(ksize/1), y+(int)(ksize/1), y+(int)(ksize*3)};


		xpoints[0] = x-(int)tilerW/2;
		xpoints[1] = (x+(int)tilerW/2);
		xpoints[2] = x+(int)(tilerW*1.5);
		xpoints[3] = (x+(int)tilerW/2);

		// = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};

		ypoints[0] = y+(int)tilerH/1;
		ypoints[1] = y-(int)tilerH/1;
		ypoints[2] = y+(int)tilerH/1;
		ypoints[3] = y+(int)tilerH*3;

		// int ypoints[4] = {y+(int)(ksize/1), y-(int)(ksize/1), y+(int)(ksize/1), y+(int)(ksize*3)};

		float tFact = (float)((float)i/(float)sNum);
		tColor = (int)((numColors * tFact) + 1);
		NGlobals.cPrint("rendering sprite at (" + x + ")(" + y + ") w cols " + tColor + "," + tFact);
		offScreenGrp.setColor(colColors[tColor]);
		offScreenGrp.fillPolygon(xpoints, ypoints, xpoints.length);
		offScreenGrp.setColor(Color.black);
		offScreenGrp.drawPolygon(xpoints, ypoints, xpoints.length);
	    }


	    DecimalFormat yrounder = new DecimalFormat("#.##");//use to round to 2 decimal places
	    if (sNum > 0) {
		yPct = ((double)yesTotal/(double)sNum) * 100.0;
		nPct = ((double)noTotal/(double)sNum) * 100.0;
	    }
	    else {
		yPct = 0;
		nPct = 0;
	    }

	    offScreenGrp.setColor(Color.black);

	    NGlobals.cPrint("yPct = " + yPct);
	    NGlobals.cPrint("nPct = " + nPct);
	    NGlobals.cPrint("sNum = " + sNum);
	    NGlobals.cPrint("yesTotal = " + yesTotal);
	    NGlobals.cPrint("noTotal = " + noTotal);

	    String yString = yrounder.format(yPct);
	    offScreenGrp.drawString("Yes " + yesTotal + " (" + yString + "%) ", centerX-150, mY(50));

	    DecimalFormat nrounder = new DecimalFormat("#.##");//use to round to 2 decimal places
	    String nString = nrounder.format(nPct);
	    offScreenGrp.drawString("No " + noTotal + " (" + nString + "%) ", centerX+100, mY(50));

	    offScreenGrp.drawString("Responses " + sNum, centerX-30, mY(30));


	    // g.setColor(Color.red);
	    // g.drawImage(backgroundIce, 0, 0, width, height, this);
	    // g.fillRect(10,10,50,50);
	    // g.setColor(colColors[avg]);
	    // g.fillRect(30,30,50,50);

	    // g.rotate();                     // Rotate 45 degrees

	    //g.dispose();

	    //	setBackground(Color.black);

	    // g.fillPolygon(xpoints, ypoints, 10);
	    //	    g.fillRect(tx, ty, 10, 10);
	} // **
		
		
	g.drawImage(offScreen, 0, 0, width, height, this);
    }	
    // END paint() ---------------------------------------------------------------------------------------
}