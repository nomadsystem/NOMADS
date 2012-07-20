import java.net.*;
import java.awt.image.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import javax.swing.*;
import java.util.Random;
import java.util.*;
import java.lang.*;
import nomads.v210_auk.*;

public class DiscussDisplayOnly extends JPanel {   
	
	
    JTextArea chatWindow;
    JScrollPane spane;
	DiscussDisplayImagePanel wholeThing;
    JPanel panel, panPanel, lTab, rTab, titleTopicPanel;
    JLabel title, topic, spa1, spa2, spa3, spa5;
    Font titleFont, topicFont;
    String tempString = "";   
    //background color for the whole applet
    Color BG = new Color(158,55,33);      
    
    //background color for chatwindow
    Color cWinColor = new Color(192,243,255);
    
    //color for chat window
    Color chatColor = new Color(0,0,0);
    
    Font chatFont = new Font("sansserif", Font.PLAIN, 10);

	Image backgroundIce;
	
	public void setImage(Image backgroundImage) {
		backgroundIce = backgroundImage;
	}
	

    public void init()
    { 
      // have to remove these when you make this a JLabel
//       Container content = getContentPane();
//       content.setBackground(Color.BLACK);
      
	  setLayout( new BorderLayout() );

           //initialize components
           
	
	  
	  chatWindow = new JTextArea(10,25);


	  //makes chat window autoscroll
	  DefaultCaret caret = (DefaultCaret)chatWindow.getCaret();
	  caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	  
	  //initialize stuff for chat window
	  chatWindow.setEnabled(false);
	  chatWindow.setBackground(cWinColor);
	  chatWindow.setFont(chatFont);
	  chatWindow.setDisabledTextColor(chatColor);
	  
	  spane = new JScrollPane(chatWindow);
	  
	  wholeThing = new DiscussDisplayImagePanel( backgroundIce );
	  wholeThing.setLayout( new BorderLayout() );
	  
	  panel = new JPanel();
	  panel.setBackground(BG);
	  panel.setOpaque(false);
	  
	  panPanel = new JPanel();
	  panPanel.setBackground(BG);
	  panPanel.setOpaque(false);

      titleTopicPanel = new JPanel( new GridLayout(2,1));
      titleTopicPanel.setBackground(BG);
      titleTopicPanel.setOpaque(false);

      titleFont = new Font("TimesRoman", Font.BOLD, 12);
	  title = new JLabel("   ", JLabel.CENTER);
	  title.setFont(titleFont);
	  topicFont = new Font("TimesRoman", Font.PLAIN, 20);
	  topic = new JLabel("", JLabel.CENTER);
	  topic.setFont(topicFont);
	  titleTopicPanel.add(title);
	  titleTopicPanel.add(topic);


	
      // buffer the sides of the applet  
// 	  lTab = new JPanel( new FlowLayout() );
// 	  rTab = new JPanel( new FlowLayout() );
// 	  lTab.setBackground(BG);
// 	  lTab.setOpaque(false);
// 	  rTab.setBackground(BG);
// 	  rTab.setOpaque(false);
          
//       spa1 = new JLabel("            ");
// 	  spa2 = new JLabel("            ");
	  spa3 = new JLabel("            ");
// 	  lTab.add(spa1);
// 	  rTab.add(spa2);

	     //add components to the applet
//	   panel.add(connect);
//	   panel.add(disconnect);
//	   panel.add(spa5);
//	   panel.add(input);
//	   panel.add(speak);   
	   panPanel.add(spa3);
//	   panPanel.add(panel);

       wholeThing.add(titleTopicPanel, BorderLayout.NORTH);
	   wholeThing.add(panPanel, BorderLayout.SOUTH);
	   wholeThing.add(spane, BorderLayout.CENTER);
//	   add(lTab, BorderLayout.WEST);
//	   add(rTab, BorderLayout.EAST);
		add(wholeThing);

		
    }
    
    
    public void handle(String msg)
    {	
		
		
		// if (bite == app_id.DISCUSS_PROMPT)
//       	 {	 
//       	 	topic.setText(msg);
//       	 	tempString = new String(msg);
// 	    	topic.setForeground(Color.BLACK);
// 	    	topicFont = new Font("TimesRoman", Font.PLAIN, 20);
      //	 }
      	
      //	 else
      //	 {
	    	chatWindow.append(msg + "\n");
	   // 	input.requestFocus();
	//	}		
		
	}
   
}
