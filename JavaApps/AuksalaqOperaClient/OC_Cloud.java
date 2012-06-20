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

public class OC_Cloud extends JPanel {   
	
	

	private String dispMsg = "", temp = "", tID = "";
	public String lastInput = "";
	//    JTextArea chatWindow;
	JScrollPane spane;
	public String tInput;
	JButton speak, connect, disconnect;
	public JTextField input;
	CloudImagePanel wholeThing;
	JPanel panel, panPanel, lTab, rTab;
	JLabel title, topic, spa1, spa2, spa3, spa5;
	String topicString = "";
	Font titleFont, topicFont;
	String tempString = "";

	Image backgroundIce;


	//background color for the whole applet
	Color BG = new Color(0,0,204);      

	//background color for chatwindow
	//    Color cWindy = new Color(242,197,126);

	//background color for input text field
	Color inputColor = new Color(192,243,255);

	//color for chat window
	Color chatColor = new Color(0,0,0);

	Font chatFont = new Font("sansserif", Font.BOLD, 18);

	public void setImage(Image backgroundImage) {
		backgroundIce = backgroundImage;
	}

	public void init()
	{ 
		//topmost container. It will hold wholeThing, which is the applet
		// Container content = getContentPane();
		// 	content.setBackground(BG);
		setLayout( new BorderLayout() );

		//applet it will be added to the topmost container  
		//this is done for the purposes of color
		wholeThing = new CloudImagePanel( backgroundIce);
		wholeThing.setLayout( new BorderLayout() );

		//initialize components
		speak = new JButton("Add to the Cloud");
		speak.setEnabled(false);

		input = new JTextField("", 15);
		input.setBackground(inputColor);
		panel = new JPanel( new FlowLayout() ); //holds buttons and textfield 
		panel.setOpaque(false);

		//	panel.setBackground(BG);		  

		titleFont = new Font("TimesRoman", Font.BOLD, 20);
		title = new JLabel("Cloud", JLabel.CENTER);
		title.setFont(titleFont);
		topicFont = new Font("TimesRoman", Font.PLAIN, 16);
		topic = new JLabel("", JLabel.CENTER);
		topic.setFont(topicFont);

		panel.add(input);
		panel.add(speak);   


		wholeThing.add(panel, BorderLayout.SOUTH);
		wholeThing.add(title, BorderLayout.NORTH);
		wholeThing.add(topic, FlowLayout.CENTER);


		add(wholeThing);

	}



	public void handle(String msg)
	{	
		//--------------------------------------- STK this is where we get the cloud discussion topic
		title.setText(msg);

	}



}
