/*
  NOMADS Cloud Display v.210
  Revised/cleaned, 6/14/2012, Steven Kemper
  Integrating NOMADSApp class
 */

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import netscape.javascript.*;
import nomads.v210.*;

public class CloudDisplay extends Applet implements MouseListener, MouseMotionListener, ActionListener, Runnable {   

	private class NomadsAppThread extends Thread {
		CloudDisplay client; //Replace with current class name

		public NomadsAppThread(CloudDisplay _client) {
			client = _client;
		}
		public void run()    {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true)  {
				client.handle();
			}
		}
	}

	NSand cloudSand;
	private NomadsAppThread nThread;

	Random randNum;

	String imgPrefix;
	Image backgroundImg;
	URL imgWebBase;

	int t,i,j,clear, picker;
	int width,height,twidth,theight,fontSize, centerX, centerY;
	Font textFont;
	int startFontSize, minFontSize, maxFontSize;

	int tRow, tCol,rows,cols,tRows, tCols;
	int chatRows;

	int x,y,w,h,dx,dy,dw,dh,xVar,yVar;
	double xScale,yScale;

	double weightX, weightY, weight;
	int stringLength;
	int tPass, tNewFontSize;
	int wordFound;
	int tCloudColorNum;
	int maxCloudColors;
	int cloudA;

	Color textColor,backGroundColor;

	Color cloudColors[];

	JButton	clearButton;
	int mx, my; // recent mouse coords
	boolean isMouseDraggingBox = false;


	public class HistoElt {
		String text;  // The actual text we're printing
		int pass;  // What pass the word was entered (ie., time)
		int size, x, y, cols, quad;
		Font font;
		int r,g,b,a;
		Color color;
	}    

	// Number of times we've checked our global word list array
	int numPasses;

	// List of all our elements
	ArrayList<HistoElt> histoGram;
	//  Temporary placeholder
	HistoElt tHist;

	String text;
	int guesser,rGuess,cGuess;

	Image offScreen;
	Graphics2D offScreenGrp;
	Image player;

	public static void main(String args[])
	{
		/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
		CloudDisplay  applet = new CloudDisplay();

		Frame appletFrame = new Frame("pollDisplay");

		appletFrame.add(applet);
		appletFrame.resize(600,600);
		appletFrame.show();

	}

	// public void setChatColors(int alpha) {
	// 	chatColors[0] = new Color(158, 55, 33, alpha);
	//   	chatColors[1] = new Color(145, 86, 65, alpha);
	//   	chatColors[2] = new Color(187, 137, 44, alpha);
	//   	chatColors[3] = new Color(191, 140, 44, alpha);
	//   	chatColors[4] = new Color(233, 158, 37, alpha);
	//   	chatColors[5] = new Color(242, 197, 126, alpha);
	//   	chatColors[6] = new Color(254, 205, 129, alpha);
	//   	chatColors[7] = new Color(249, 241, 131, alpha);
	//     }
	// 
	//     public void setCloudColors(int alpha) {
	// 	cloudColors[0] = new Color(158, 55, 33, alpha);
	//   	cloudColors[1] = new Color(145, 86, 65, alpha);
	//   	cloudColors[2] = new Color(187, 137, 44, alpha);
	//   	cloudColors[3] = new Color(191, 140, 44, alpha);
	//   	cloudColors[4] = new Color(233, 158, 37, alpha);
	//   	cloudColors[5] = new Color(242, 197, 126, alpha);
	//   	cloudColors[6] = new Color(254, 205, 129, alpha);
	//   	cloudColors[7] = new Color(249, 241, 131, alpha);
	//     }
	// 
	//     public void setPointerColors(int alpha) {
	// 	pointerColors[0] = new Color(158, 55, 33, alpha);
	//   	pointerColors[1] = new Color(145, 86, 65, alpha);
	//   	pointerColors[2] = new Color(187, 137, 44, alpha);
	//   	pointerColors[3] = new Color(191, 140, 44, alpha);
	//   	pointerColors[4] = new Color(233, 158, 37, alpha);
	//   	pointerColors[5] = new Color(242, 197, 126, alpha);
	//   	pointerColors[6] = new Color(254, 205, 129, alpha);
	//   	pointerColors[7] = new Color(249, 241, 131, alpha);
	//     }

	public void setCloudColors(int alpha) {
		cloudColors[0] = new Color(158, 55, 33, alpha);
		cloudColors[1] = new Color(145, 86, 65, alpha);
		cloudColors[2] = new Color(187, 137, 44, alpha);
		cloudColors[3] = new Color(191, 140, 44, alpha);
		cloudColors[4] = new Color(233, 158, 37, alpha);
		cloudColors[5] = new Color(242, 197, 126, alpha);
		cloudColors[6] = new Color(254, 205, 129, alpha);
		cloudColors[7] = new Color(249, 241, 131, alpha);
		maxCloudColors = 7;
	}

	public void init()
	{  	

		int i;

		imgPrefix = "http://nomads.music.virginia.edu/images/";

		try { 
			imgWebBase = new URL(imgPrefix); 
		} 
		catch (Exception e) {}


		randNum = new Random();

		width = getSize().width;
		height = getSize().height;

		offScreen = createImage(width,height);
		offScreenGrp = (Graphics2D) offScreen.getGraphics();
		backgroundImg = getImage(imgWebBase,"SandDunes1_950x650.jpg");

		offScreenGrp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		centerX = (width/2);
		centerY = (height/2); 

		NGlobals.cPrint("width = " + width + "height = " + height);
		setBackground(Color.black);	

		cloudColors = new Color[8];
		cloudA = 230;
		setCloudColors(cloudA);
		int tCloudColorNum = 0;
		int maxCloudColors = 7;

		i = 0;
		j = 0;
		clear = 0;

		xScale = 0.3;
		yScale = 0.3;

		histoGram = new ArrayList<HistoElt>();

		wordFound = 0;
		numPasses = 0;
		startFontSize = 25;
		minFontSize = 10;
		maxFontSize = 200;

		x = width / 2 - 20;
		y = height / 2 - 20;

		addMouseListener(this);
		addMouseMotionListener(this);

		cloudSand = new NSand(); //Connects on init
		cloudSand.connect();

		nThread = new NomadsAppThread(this);
		nThread.start();
		
		byte d[] = new byte[1];
		d[0] = 0;

		cloudSand.sendGrain((byte)NAppID.CLOUD_DISPLAY, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	}	


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


			repaint();
			e.consume();
		}
	}

	// ------------------------------------------------------------------------------------------------
	// BEGIN handle()
	// ------------------------------------------------------------------------------------------------

	public void handle() {
		int i,j,fc,sc,x,y,quad,lastQuad;
		float freq,amp;
		String temp,tAlpha,thread,input, tTest, text;
		int THREAD_ID;
		float xput,yput;
		

		//Unused for Sand class ****STK 6/14/12
//		int incCmd, incNBlocks, incDType, incDLen;
//		int incIntData[] = new int[1000];
//		byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
		NGrain grain;
		NGlobals.cPrint("CloudDiscuss -> handle()");

		grain = cloudSand.getGrain();
		grain.print(); //prints grain data to console
		byte incAppID = grain.appID;
		byte incCmd = grain.command;
		text = new String(grain.bArray);

		quad=0;
		lastQuad=0;

		tTest = text; 

		if (text.length() >= 4){
			tTest = text.substring(0, 4);
			NGlobals.cPrint("tTest =" + tTest);
		}
		NGlobals.cPrint("...");
		NGlobals.cPrint("handle(" + text + "," + grain.appID + ") [CloudDisplay]\n");
		NGlobals.cPrint("...");



		// ========= CLOUD INPUT ============================================

		if (incAppID == NAppID.CLOUD_CHAT && incCmd == NCommand.SEND_MESSAGE) {

			NGlobals.cPrint("CLOUD_CHAT\n");

			stringLength = text.length(); 

			// Then check text locations to avoid collisions *************************

			wordFound = 0;

			// Check our histogram =============================================

			for (i=0;i<histoGram.size();i++) {
				tHist = histoGram.get(i);
				NGlobals.cPrint("...");
				NGlobals.cPrint("checking histogram ----- tHist.text = ||>> " + tHist.text + " <<||");
				NGlobals.cPrint("...");
				NGlobals.cPrint("  tHist.size = " + tHist.size);
				NGlobals.cPrint("...");
				NGlobals.cPrint(" histoGram.size() = " + histoGram.size());

				// 1.  Histogram element [i] matches incoming text -----

				x=tHist.x;
				y=tHist.y;	       	

				if (tHist.text.compareToIgnoreCase(text) == 0) {
					wordFound = 1;
					NGlobals.cPrint("...");
					NGlobals.cPrint(">>>FOUND " + tHist.text + " at [" + x + "]" + "[" + y + "]");
					NGlobals.cPrint("  INCreasing text size");

					// This will change to be a combination of rank v time (ie., numPasses);
					tHist.size += 4;

					quad = tHist.quad;
					NGlobals.cPrint("  quad = " + quad);
					// if (quad > 75) {
					// 	tHist.x-=8;
					// 	tHist.y-=5;
					// 	if (tHist.x < centerX)
					// 	    tHist.x = centerX;
					// 	if (tHist.y < centerY)
					// 	    tHist.y = centerY;
					// }
					// else if (quad > 50) {
					// 	tHist.x-=8;
					// 	tHist.y+=5;
					// 	if (tHist.x < centerX)
					// 	    tHist.x = centerX;
					// 	if (tHist.y > centerY)
					// 	    tHist.y = centerY;
					// }
					// else if (quad > 25) {
					// 	tHist.x+=6;
					// 	tHist.y+=5;
					// 	if (tHist.x > centerX)
					// 	    tHist.x = centerX;
					// 	if (tHist.y > centerY)
					// 	    tHist.y = centerY;
					// }
					// else {
					// 	tHist.x+=6;
					// 	tHist.y-=5;
					// 	if (tHist.x > centerX)
					// 	    tHist.x = centerX;
					// 	if (tHist.y < centerY)
					// 	    tHist.y = centerY;
					// }

					if (tHist.size > maxFontSize) {
						fontSize = maxFontSize;
					}
					else {
						fontSize = tHist.size;
					}
					tHist.font = new Font("Helvetica", Font.PLAIN, fontSize);

					//i = histoGram.size();  // exit the loop

				}

				// 2a.  Blank cell ... do nothing
				else if (tHist.text.compareToIgnoreCase("") == 0) {
					NGlobals.cPrint("|_|");
				}

				// 2.  Histogram element [i] DOES NOT match incoming text and is > min size -----

				else if (tHist.size > minFontSize) {  // Decrease size (if > min AND modulo 2)
					NGlobals.cPrint("...");
					NGlobals.cPrint("  DECreasing word size for " + tHist.text);
					NGlobals.cPrint("...");
					NGlobals.cPrint("  numPasses = " + numPasses);
					if (numPasses%2 == 0) {
						tHist.size--;
						// quad = tHist.quad;
						// if (quad > 75) {
						//     tHist.x+=2;
						//     tHist.y+=2;
						// }
						// else if (quad > 50) {
						//     tHist.x+=2;
						//     tHist.y-=2;
						// }
						// else if (quad > 25) {
						//     tHist.x-=2;
						//     tHist.y-=2;
						// }
						// else {
						//     tHist.x-=2;
						//     tHist.y+=2;
						// }
						// if (tHist.x < 10) 
						//     tHist.x = 10;
						// if (tHist.x > (width-10))
						//     tHist.x = width-10;
						// if (tHist.y < 10) 
						//     tHist.y = 10;
						// if (tHist.y > (height-10))
						//     tHist.y = height-10;
					}
					if (tHist.size > maxFontSize)
						tHist.size = maxFontSize;
					fontSize = tHist.size;
					tHist.font = new Font("Helvetica", Font.PLAIN, fontSize);

					NGlobals.cPrint("...");

					NGlobals.cPrint("  tHist.x,tHist.y = " + tHist.x + "," + tHist.y);
					NGlobals.cPrint("...");
					NGlobals.cPrint("  tHist.size= " + tHist.size);
				}

				// 3.  Histogram element [i] DOES NOT match and is < min size ... delete it

				else if (tHist.size <= minFontSize) {
					NGlobals.cPrint("...");
					NGlobals.cPrint("  tHist.x,tHist.y = " + tHist.x + "," + tHist.y);
					NGlobals.cPrint("...");
					NGlobals.cPrint("  tHist.size= " + tHist.size);
					NGlobals.cPrint("...");
					NGlobals.cPrint("  REMoving word: " + tHist.text + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

					fontSize = tHist.size;
					if (fontSize < minFontSize)
						fontSize = minFontSize;

					histoGram.remove(i);
					i--;
				}			  	
			}  // end for (i=0;i<histoGram.size();i++)


			// No words found, add new word and store relevant data
			if (wordFound == 0) {
				// Figure out where to put the text =============================================

				// Find a free cell
				picker = 1;

				NGlobals.cPrint("...");

				// figure out center, then expand range over time 

				x=y=0;

				tHist = new HistoElt();
				tHist.text = new String(text);

				int xMin = 0; // (int)(width * 0.2);
				int xVar = (int)(width * xScale);
				xScale += 0.1;
				if (xScale > 0.5) 
					xScale = 0.3;

				int yMin = 0; // (int)(height * 0.2);
				int yVar = (int)(height * yScale);
				yScale += 0.1;
				if (yScale > 0.5)
					yScale = 0.3;

				lastQuad = quad;
				quad = randNum.nextInt(5);
				while (quad == lastQuad) {
					quad = randNum.nextInt(5);
				}
				tHist.quad = quad;
				NGlobals.cPrint(">>>NEW WORD " + tHist.text + " at [" + x + "]" + "[" + y + "]");
				NGlobals.cPrint("setting quad = " + quad);

				int xRand = xMin + randNum.nextInt(xVar);
				int yRand = yMin + randNum.nextInt(yVar);

				if (quad > 3) {
					x = centerX + xRand;
					y = centerY + yRand;
				}
				else if (quad > 2) {
					x = centerX + xRand;
					y = centerY - yRand;
				}
				else if (quad > 1) {
					x = centerX - xRand;
					y = centerY - yRand;
				}
				else  {
					x = centerX - xRand;
					y = centerY + yRand;
				}

				// if ((x > centerX) && (y > centerY)) {
				//     quad = 3;
				// }
				// if ((x > centerX) && (y < centerY)) {
				//     quad = 2;
				// }
				// if ((x < centerX) && (y < centerY)) {
				//     quad = 1;
				// }
				// if ((x < centerX) && (y > centerY)) {
				//     quad = 0;
				// }
				// tHist.quad = quad;

				NGlobals.cPrint("<<<< ADDING new word: " + text + " at " + "[" + x + "]" + "[" + y + "]");


				tCloudColorNum++;
				if (tCloudColorNum > maxCloudColors)
					tCloudColorNum = 0;	
				tHist.color = cloudColors[tCloudColorNum]; 	
				tHist.color = Color.black;

				fontSize = tHist.size = startFontSize;
				tHist.pass = numPasses;

				tHist.x = x;
				tHist.y = y;

				tHist.font = new Font("Helvetica", Font.PLAIN, tHist.size);
				histoGram.add(tHist);

				// This will change to be a combination of rank v time (ie., numPasses);

				// DRAW THE TEXT ======================================

				NGlobals.cPrint("...");
				NGlobals.cPrint("Drawing word: " + text);

			}

			// CODE TO CLEAR THE SCREEN, NOT USED AS OF 2/15/2010 ============================
			//**** If we fill up the cells they clear SK 12/03/09
			//****Ultimately we should make a button that does this

			clear = 0;
			if (clear == 1) {
				NGlobals.cPrint("CLEAR!");
				histoGram.clear();
				clear = 0;
				i = 0;
				j = 0;
				clear = 0;
				guesser = 0;		
				NGlobals.cPrint("CLEAR:  clearing rows/cols");
			}

			// END CLEAR CODE ==================================================================

			numPasses++;
			NGlobals.cPrint("...");
			NGlobals.cPrint("END handle(" + text + ") numPasses = " + numPasses + " -----");
			repaint();
		}
		
		else {
			grain = null;
		}
		if (grain != null)
			grain = null;
		// END OC_CLOUD ------------------------------------------------------------------------------------


		//MONITOR app disabled until new implementation figured out with Sand class ****STK 6/14/12	
		//		if (grain.appID == NAppID.MONITOR) {
		//			if (text.equals("CHECK")) {
		//				try {
		//					streamOut.writeByte((byte)app_id.MONITOR);
		//					streamOut.writeUTF("PING");
		//				}
		//				catch(IOException ioe) {
		//					System.out.println("Error writing to output stream: ");
		//				}
		//			}	 
		//		}   
		//		NGlobals.cPrint ("-------------------------------------------------[OM]\n");
	}

	// ------------------------------------------------------------------------------------------------
	// END handle()
	// ------------------------------------------------------------------------------------------------

	public void actionPerformed( ActionEvent ae )
	{
		Object obj = ae.getSource();
		if( obj == clearButton ) {
			NGlobals.cPrint("You pressed clear");
			NGlobals.cPrint("...");
			NGlobals.cPrint("actionPerformed():  clearing histogram");
			for (i=0;i<histoGram.size();i++) {

				NGlobals.cPrint("...");
				String tempString = Integer.toString(histoGram.size());

				NGlobals.cPrint(tempString);
				tHist = histoGram.get(i);
				tHist.pass = 0;
				x=tHist.x;
				y=tHist.y;
				tHist.font = new Font("Helvetica", Font.PLAIN, startFontSize);
			}
			NGlobals.cPrint("CLEAR!");
			histoGram.clear();
			clear = 0;
			i = 0;
			j = 0;
			clear = 0;
			guesser = 0;		
			NGlobals.cPrint("...");
			NGlobals.cPrint("actionPerformed():  clearing rows/cols");
		}

	}


	// DT 6/30/10:  not sure we need these anymore

	public void start() {

	}

	public void run () {
		if (i == 1) {
		} 
	}

	public void paint(Graphics g) {
		int tx, ty, r,gr,b,a;
		Color tc;
		int len1,len2;
		int ksize = 7;
		int ssize = 5;

		// g.dispose();
		// setBackground(Color.black);
		// g.setColor(Color.black);
		super.paint(g);
		// setBackground(Color.black);
		// g.setColor(Color.black); //STK here's where to change the background color
		// g.drawImage(image, 0, 0, width, height, this);
		//g.drawImage(backgroundImg, 0, 0, width, height, this);
		offScreenGrp.drawImage(backgroundImg, 0, 0, width, height, this);

		//g.fillRect(0,0,width,height);

		//	setBackground(Color.black);

		for (i=0;i<histoGram.size();i++) {
			tHist = histoGram.get(i);

			r = tHist.color.getRed();
			gr = tHist.color.getGreen();
			b = tHist.color.getBlue();
			a = cloudA;

			tc = new Color(r,gr,b,a);

			offScreenGrp.setColor(tc);
			offScreenGrp.setFont(tHist.font);
			offScreenGrp.drawString(tHist.text, tHist.x, tHist.y);
			// g.setColor(tc);
			// 	    g. setFont(tHist.font);
			// 	    g.drawString(tHist.text, tHist.x, tHist.y);

		}
		g.drawImage(offScreen, 0, 0, width, height, this);

	}

}
