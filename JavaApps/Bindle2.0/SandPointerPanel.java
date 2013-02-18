import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import javax.swing.*;

import nomads.v210.*;

public class SandPointerPanel extends JPanel implements MouseListener, MouseMotionListener {   

// public class SandPointerPanel extends JApplet implements MouseListener, MouseMotionListener {

    int width, height;
    int x, y; // coords of the box
    int mx, my; // recent mouse coords
    int personnum;
    boolean isMouseDraggingBox = false;
    NSand mySand;

    public void init(NSand inSand) {
	mySand = inSand;

	byte d[] = new byte[1];
	d[0] = 0;
	// swarmSand.sendGrain((byte)NAppID.SOUND_SWARM, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d);

	width = 600;
	height = 600;
	// width = getSize().width;
	// height = getSize().height;
	// setBackground(Color.black);

	x = width / 2 - 20;
	y = height / 2 - 20;

	addMouseListener(this);
	addMouseMotionListener(this);
    }

    public void resetSand(NSand inSand)
    { 
	mySand = inSand;
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

	    if (new_mx < 1)
		new_mx = 1;
	    if (new_mx > 599)
		new_mx = 599;
	    if (new_my < 1)
		new_my = 1;
	    if (new_my > 599)
		new_my = 599;

	    x += new_mx - mx;
	    y += new_my - my;

	    // update our data
	    mx = new_mx;
	    my = new_my;
			
	    
	    int[] xy = new int[2];
	    double a,b,c,d;
	    a = x/599.0;
	    b = y/599.0;
	    c = a*1000;
	    d = b*1000;
	    xy[0] = (int)c;
	    xy[1] = (int)d;
	    
	    NGlobals.cPrint("a = " + a);
	    NGlobals.cPrint("b = " + b);
	    NGlobals.cPrint("c = " + c);
	    NGlobals.cPrint("d = " + d);


	    mySand.sendGrain((byte)NAppID.SOUND_SWARM, (byte)NCommand.SEND_SPRITE_XY, (byte)NDataType.INT32, 2, xy);
	    
	    NGlobals.cPrint(" -- sending x/y " + xy[0] + "," + xy[1]);
	    // xxx	streamOut.writeByte(NSand.appID.STUDENT_SAND_POINTER);
	    // xxx streamOut.writeUTF(towrite);
			
	    repaint();
	    e.consume();
	}
    }

    public void paint(Graphics g) {

	NGlobals.cPrint("SAND POINTER:  paint()");
	int ksize = 7;
	int xpoints[] = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};
	int ypoints[] = {y+(int)(ksize/2), y-(int)(ksize/2), y+(int)(ksize/2), y+(int)(ksize*1.5)};

	g.setColor(Color.BLACK);
	g.fillRect(0,0,width,height);
	g.setColor(Color.ORANGE);
	g.fillPolygon(xpoints, ypoints, xpoints.length);
	//		g.fillRect(x, y, 10, 10);
    }
	
	
	
    public void handle(NGrain inGrain) {

    	NGrain grain;
        
    	NGlobals.cPrint("SandPointerClient -> handle()");
        
    	grain = inGrain;

    }
	
}
