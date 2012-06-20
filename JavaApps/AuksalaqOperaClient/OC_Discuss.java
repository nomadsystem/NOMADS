import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import javax.swing.*;
import java.util.Random;
import java.util.*;
import java.lang.*;
import nomads.v210_Auk.*;

public class OC_Discuss extends JPanel {   
	

    	
    private String dispMsg = "", temp = "", tID = "";
    public String lastInput = "";
    //    JTextArea chatWindow;
    JScrollPane spane;
    JButton speak, connect, disconnect;
   	public JTextField input;
    JPanel panPanel, lTab, rTab, panel;
    DiscussImagePanel wholeThing;
    JLabel title, topic, spa1, spa2, spa3, spa5;
    String topicString = "";
    Font titleFont, topicFont;
    String tempString = "";
    public String tInput = "";
    
    Image backgroundIce;
        
    //background color for the whole applet
    Color BG = new Color(255,0,0);      
    
    //background color for input text field
    Color inputColor = new Color(192,243,255);
    
    //color for chat window
    Color chatColor = new Color(0,0,0);
    
    Font chatFont = new Font("sansserif", Font.BOLD, 18);
    int wait;

	 public void setImage(Image backgroundImage) {
		backgroundIce = backgroundImage;
	}

    public void init()
    { 

	setLayout( new BorderLayout() );

	//applet it will be added to the topmost container  
	//this is done for the purposes of color
	wholeThing = new DiscussImagePanel( backgroundIce );
//	wholeThing.setBackground(BG);
	wholeThing.setLayout( new BorderLayout() );
//	wholeThing.setOpaque(true);
	
	//initialize components
	speak = new JButton("Speak");
	speak.setEnabled(false);

	input = new JTextField("", 25);
	input.setBackground(inputColor);
	panel = new JPanel( new FlowLayout() ); //holds buttons and textfield 
	panel.setOpaque(false);

//	panel.setBackground(BG);		  
	  
	titleFont = new Font("TimesRoman", Font.BOLD, 20);
	title = new JLabel("Discussion", JLabel.CENTER);
	title.setFont(titleFont);
	topicFont = new Font("TimesRoman", Font.PLAIN, 16);
	topic = new JLabel("", JLabel.CENTER);
	topic.setFont(topicFont);

	
	//key listener stuff
	//	  input.setFocusable(true);
	//input.addKeyListener(this); 
	
	panel.add(input);
	panel.add(speak);   


	wholeThing.add(panel, BorderLayout.SOUTH);
	wholeThing.add(title, BorderLayout.NORTH);
	wholeThing.add(topic, FlowLayout.CENTER);
 

	add(wholeThing);

    }
    
   
	
	// public void paint(Graphics g) {
// 	    g.drawImage(backgroundIce, 0, 0, (int)getSize().getWidth(), (int)getSize().getHeight(), this);
//     }

    public void handle(String msg)
    {	
	
       	 	title.setText(msg); //Gets Discuss prompt from OperaClient

	} 
    
}
