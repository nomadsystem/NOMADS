import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.technosonics.*;
import java.text.DecimalFormat;

public class pollDisplay extends JApplet implements MouseListener, MouseMotionListener, ActionListener, Runnable {   

    private Socket socket           = null;
    private DataInputStream  console   = null;
    private DataOutputStream streamOut = null;
    private pollDisplayThread client    = null;
    private String    serverName = "nomads.music.virginia.edu";

    private int       serverPort = 52807;

    public class sElt {
	int x, y, xOff, tW, tH;
    }    

    String imgPrefix;
    URL imgWebBase;

    int     MAX_THREADS = 100000;
    
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

    String response = "";  // DISPLAY
    String typeOfQuestionSubmitted = "";
	
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
    Image offScreen;
    Graphics2D offScreenGrp;
    Image player;

    // ###########################

    public static void main(String args[])
    {
	/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
	pollDisplay  applet = new pollDisplay();

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

    public void init()
    {   

	int i;

	System.out.println("init() ...\n");
	imgPrefix = "http://nomads.music.virginia.edu/images/";
	
	count = 0;
	sOff = 0;
	sOff = 0;

	yPct = 0;
	nPct = 0;

	try { 
	    imgWebBase = new URL(imgPrefix); 
	} 
        catch (Exception e) {}

	width = getSize().width;
	height = getSize().height;
	sHeight = (int)(height*0.85);
    
    offScreen = createImage(width,height);
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

	globals.cPrint("width = " + width + "height = " + height);

	sNum = 0;

        Color bgColor = new Color(84,31,18); //muddy red = 1
	//	getContentPane().setBackground(bgColor);	
	// getContentPane().setBackground(Color(145,86,65));
	// getContentPane().setBackground(Color.blue);	

	x = width / 2 - 20;
	y = height / 2 - 20;

	nCols = 10;  // Width (x)
	nRows = 20;  // Height (y)
	
	colVals = new int[200];

	setColors(60);

	textFont = new Font("Helvetica", Font.PLAIN, 22);

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
		globals.cPrint("randCVal = " + randCVal + "\n");
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

	globals.cPrint("respAvg = " + respAvg + " colAvg = " + colAvg);
	addMouseListener(this);
	addMouseMotionListener(this);

	getContentPane().setBackground(nomadsColors[1]);

	connect("nomads.music.virginia.edu", 52807);
    }	

    public void reset()
    {   

	int i;

	System.out.println("reset() ...\n");
	
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

	
    public void connect(String serverName, int serverPort)
    {  
	System.out.println("Establishing connection. Please wait ...");
	try {  
	    socket = new Socket("nomads.music.virginia.edu", 52807);	

	    System.out.println("Connected");
	    open();
	}

	catch(UnknownHostException uhe) {  
	    System.out.println("How unknown");
	}
	catch(IOException ioe) {  
	    System.out.println("Unexpected exception: ");
	} 
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
	  System.out.println("Byte is " + app_id.STUDENT_SAND_POINTER + " and write is "
	  + towrite);
	  streamOut.writeByte(app_id.STUDENT_SAND_POINTER);
	  streamOut.writeUTF(towrite);
	  } catch (IOException ioe) {
	  System.out.println("Error writing...");
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

    public void handle(byte bite, String s)
    {
	int i,j,fc,sc,x,y;
	String temp,thread,input, tTest;
	int THREAD_ID;
	float xput,yput;
	int tVal;

	globals.cPrint("handle() =========================================================================================");

	if (!(s.equalsIgnoreCase(""))) {
	    temp = "";
	    response = s;  // DISPLAY
	    resp = 0;
	    
	    System.out.println("PD: Inside pollDisplay handle");
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: s " + s);
	    System.out.println("PD: response " + s);
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: bite " + bite);
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: bite " + bite);
	    
	    //get question from teacher poll app, and type of question submitted
	    if (bite == app_id.TEACHER_POLL)  {
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
	    	
		System.out.println("PD: Poll display handling teacher poll stuff");
		for (i = 0; i < s.length(); i++) {		
		    temp = s.substring(i, i+1);
		    if (temp.equalsIgnoreCase(";")) {
			//bite = s.substring(i+2, i+4);
			typeOfQuestionSubmitted = s.substring(0,i);
			if (typeOfQuestionSubmitted.equalsIgnoreCase("Yes-No")) {
			    reset();
			    sCenterX[0] = (int)(width*0.25);
			    sCenterY[0] = height/2;
			    sCenterX[1] = (int)(width*0.75);
			    sCenterY[1] = height/2;
			    pType = pollType.YESNO;
			}
			if (typeOfQuestionSubmitted.equalsIgnoreCase("Scale of 1 to 10")) {
			    reset();
			    pType = pollType.TEN;
			}
		    
			System.out.println("PD: ----------------------");
			if (globals.clientDebugLevel > 0) {
			    System.out.println("PD: ...");
			}
			System.out.println("PD: typeOfQuestionSubmitted " + typeOfQuestionSubmitted);
			System.out.println("PD: _--------------------");
			response = s.substring(i+1); // DISPLAY
			break;
		    }
		}
		System.out.println("PD: setting poll question for POLL DISPLAY");
		// Set question on display here
		// Q.setText("<html><h2 style='color:black'>" + response + "</h2></html>");	// DISPLAY
	    }
	    
	    //get results from student poll apps
	    else if (bite == app_id.STUDENT_POLL) {	 		 
		System.out.println("PD: Poll display handling student poll stuff");

		// YESNO ============================================================----------
		if (typeOfQuestionSubmitted.equalsIgnoreCase("Yes-No")) {
		    pType = pollType.YESNO;

		    if (response.equalsIgnoreCase("yes"))  {
			System.out.println("PD: yes came in");
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


			globals.cPrint("setting sprite " + sNum + "at (" + x + "," + y + ")");
			sNum++;
			if (globals.clientDebugLevel > 0) {
			    System.out.println("PD: ...");
			}
			System.out.println("PD: yesTotal " + yesTotal);
		    }

		    if (response.equalsIgnoreCase("no")) {
			System.out.println("PD: no came in");
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


			globals.cPrint("setting sprite " + sNum + "at (" + x + "," + y + ")");
			sNum++;
			if (globals.clientDebugLevel > 0) {
			    System.out.println("PD: ...");
			}
			System.out.println("PD: noTotal " + noTotal);
		    }
	    		
		    //convert yes no results to an average to map to color
		    totalYesAndNo = yesTotal + noTotal;
		    yesPer = (double) yesTotal / totalYesAndNo;
		    noPer = (double) noTotal / totalYesAndNo;
					
		    if (globals.clientDebugLevel > 0) {
			System.out.println("PD: ...");
		    }
		    System.out.println("PD: totalYesAndNo " + totalYesAndNo);
				
		    difference = yesPer - noPer;
		    difference *= 10;
					
		    if (globals.clientDebugLevel > 0) {
			System.out.println("PD: ...");
		    }
		    System.out.println("PD: difference " + difference);
					
		    finAns = 5 + difference;
					
		    if (globals.clientDebugLevel > 0) {
			System.out.println("PD: ...");
		    }
		    System.out.println("PD: finAns " + finAns);
				
		    // if it exceeds a boundary, it is set to the boundary value
		    if (finAns < 1)
			finAns = 1;
				
		    if (finAns > 10)
			finAns = 10;
					
		    if (globals.clientDebugLevel > 0) {
			System.out.println("PD: ...");
		    }
		    System.out.println("PD: finAns " + finAns);
		    System.out.println("PD: (int)Math.round(finAns) " + (int)Math.round(finAns));
	    	
		    double yFact = ((double)(yesTotal*numColors)/(double)sNum);
		    double nFact = ((double)(noTotal)/(double)sNum);
		    
		    colAvg = (int)(yFact + nFact);

		    globals.cPrint("colAvg = " + colAvg + "\n");	    
		    if (colAvg > numColors) {
			colAvg = numColors;
		    }
		    if (colAvg < 1) {
			colAvg = 1;
		    }

		    getContentPane().setBackground(nomadsColors[colAvg]);

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
		if (typeOfQuestionSubmitted.equalsIgnoreCase("Scale of 1 to 10")) {
		    pType = pollType.TEN;

		    globals.cPrint("1-to-10 ----------------------------------------------------------");
		    resp = Integer.parseInt(response); // DISPLAY
		    tVal = colVals[resp];
		    tVal++;
		    colVals[resp] = tVal;

		    globals.cPrint("resp: " + resp);
		    if (tVal >= nRows) {
			nRows = tVal;
		    }
		    colSum += resp;
		    globals.cPrint("colSum: " + colSum);

		    colColorRatio = 1+(int)Math.ceil(nRows/numColors);
		    respAvg = (int)(colSum/(nCols));
		    // colAvg = (int)respAvg/colColorRatio;

		    globals.cPrint("colColorRatio: " + colColorRatio);
		    globals.cPrint("colAvg: " + colAvg);

		    runningTotal += resp;
		    count++;
		    if (globals.clientDebugLevel > 0) {
			System.out.println("PD: ...");
		    }

		    System.out.println("PD: runningTotal / count " + (runningTotal / count));
		    average = runningTotal / count;
		    DecimalFormat roundAverage = new DecimalFormat("#.##");//use to round to 2 decimal places
		    colAvg = (int)average;

		    if (colAvg > numColors) {
			colAvg = numColors;
		    }
		    if (colAvg < 1) {
			colAvg = 1;
		    }

		    getContentPane().setBackground(nomadsColors[colAvg]);
		
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
		System.out.println("PD: pollDisplay says Extraneous information");
	    }
	    
	    
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: bite " + bite);
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: response " + response);  // DISPLAY
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: resp " + resp);
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: Running total " + runningTotal);
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: count " + count);
	    if (globals.clientDebugLevel > 0) {
		System.out.println("PD: ...");
	    }
	    System.out.println("PD: average " + average);
	}
	else {
	    System.out.println("PD:s was empty string. some app was initially casting its byte to the server. ignoring for polldisplay handle");
	}

	if (bite == app_id.TEACHER_POLL) {
	    // repaint();
	}
	
	if (bite == app_id.MONITOR) {
	    if (text.equals("CHECK")) {
		try {
		    streamOut.writeByte((byte)app_id.MONITOR);
		    streamOut.writeUTF("PING");
		}
		catch(IOException ioe) {
		    System.out.println("Error writing to output stream: ");
		}
	    }	 
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
        

    
    public void open()
    {  
	globals.cPrint("...");
	globals.cPrint("open()");
	try {
	    globals.cPrint("Sending app_id byte and null string.");
	    streamOut = new DataOutputStream(socket.getOutputStream());
	    client = new pollDisplayThread(this, socket);
	    streamOut.writeByte((byte)app_id.DISPLAY_POLL);
	    streamOut.writeUTF("PING");
	}
	catch(IOException ioe) {
	    System.out.println("Error opening output stream: ");
	} 
    }

    public void close()
    {  
	try {
	    streamOut.writeByte(app_id.DISPLAY_POLL);
	    streamOut.writeUTF(".bye");    
	    streamOut.flush();
	    if (streamOut != null)  streamOut.close();
	    if (socket    != null)  socket.close(); 
	}
	catch(IOException ioe) {
	    System.out.println("Error closing...");
	}
	client.close();  
	client.stop(); 
    }
	

    public void getParameters() {
	serverName = "nomads.music.virginia.edu";
	serverPort = 52807; 
    }
    

    // DT 6/30/10:  not sure we need these anymore

    public void start() {
	System.out.println("PD: start() ...\n");
	repaint();
    }
    public void run () {
	globals.cPrint("PD: run() ...");
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
	Color tc;
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

	System.out.println("paint() ...\n");

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
	    globals.cPrint("rendering 1-10 columns");

	    // getContentPane().setBackground(nomadsColors[colAvg]);

	    for(cCol=1; cCol<=nCols; cCol++) {
		
		tVal = colVals[cCol];
		
		// globals.cPrint("cCol = " + cCol);	    
		// globals.cPrint("tVal = " + tVal);	    
		

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
		    //  globals.cPrint("cRow = " + cRow);
		    //  globals.cPrint("tVal = " + tVal);	  
		    //  globals.cPrint("colColorRatio = " + colColorRatio + "\n");	  
		    //  globals.cPrint("cSet = " + cSet + "\n");	  
	    
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
	    DecimalFormat rounder = new DecimalFormat("#.##");//use to round to 2 decimal places
	    String tString = rounder.format(average);
	    offScreenGrp.drawString("Average:  " + tString, centerX-100, mY(20));
	    offScreenGrp.drawString("Responses:  " + sNum, centerX+100, mY(20));
	    
	    globals.cPrint("colAvg = " + colAvg + "\n");	    
	
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

	    for (i=1;i<=nCols;i++) {
		x = i*colW;
		offScreenGrp.setColor(Color.black);
		offScreenGrp.drawString(Integer.toString(i), x, mY(50));
	    }

	    	    	    
	}

	// yes/no ================================================================----------

	if (pType == pollType.YESNO) {

	    globals.cPrint("rendering YESNO sprites");
	    
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
		globals.cPrint("rendering sprite at (" + x + ")(" + y + ") w cols " + tColor + "," + tFact);
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

	    globals.cPrint("yPct = " + yPct);
	    globals.cPrint("nPct = " + nPct);
	    globals.cPrint("sNum = " + sNum);
	    globals.cPrint("yesTotal = " + yesTotal);
	    globals.cPrint("noTotal = " + noTotal);
	    
	    String yString = yrounder.format(yPct);
	    offScreenGrp.drawString("Yes " + yesTotal + " (" + yString + "%) ", centerX-150, mY(50));

    	    DecimalFormat nrounder = new DecimalFormat("#.##");//use to round to 2 decimal places
	    String nString = nrounder.format(nPct);
	    offScreenGrp.drawString("No " + noTotal + " (" + nString + "%) ", centerX+100, mY(50));

	    offScreenGrp.drawString("Responses " + sNum, centerX-30, mY(20));


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