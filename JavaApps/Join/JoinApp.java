/*
Password field exists in version 0.2 STK 12/11/09
Version 0.4 is going to try and implement server stuff STK 12/11/09
Version 0.4 moves arrayList to server
Version 0.5 changes so you connect to server when you press submit/hit enter
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.technosonics.*;

public class JoinApp extends JApplet implements ActionListener
{
 	private Socket socket              = null;
    private DataInputStream  console   = null;
    private DataOutputStream streamOut = null;
    private JoinAppThread client    = null;
    private String    serverName = globals.serverName;
    private int       serverPort = globals.serverPort;
 	
	String userID, tUserID, tLogin;
	JButton submit;
	JLabel usernameLabel, empty;
	JTextField login;
	Color bgColor;

	int i;
	
	public void init( )
	{
		Color bgColor = new Color(233, 158, 37);
		Container content = getContentPane();
		content.setBackground(bgColor);
		i = 0;
		setLayout( new GridLayout( 2, 2) );
		usernameLabel = new JLabel( "        Username:");
		empty = new JLabel();
    	submit = new JButton( "Submit" );
    	submit.addActionListener( this );
		login = new JTextField(15);
		login.addActionListener( this );
		add( usernameLabel);
		add( login);
		add(empty);
		add( submit);
		getParameters(); //Not sure what this does
	
	}
  
	
  
  public void connect(String serverName, int serverPort)
    {  
		System.out.println("Establishing connection. Please wait ...");
		try {  
		    socket = new Socket(serverName, serverPort);
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
  
  public void handle(byte bite, String text) {
	if (text.equals(".bye")) {
		System.out.println("Bye!");
	    client.close();
	    close();
	    client.stop();
	}
  else {

	}
}
    public void open()
    {  
		try {
		    streamOut = new DataOutputStream(socket.getOutputStream());
		    client = new JoinAppThread(this, socket);

		}
		catch(IOException ioe) { 
		    System.out.println("Error opening output stream: ");
		} 
    }
    public void close()
    {  
    	try {
    		streamOut.writeByte(app_id.LOGIN);
			streamOut.writeUTF(".bye");    
			streamOut.flush();
	    	if (streamOut != null)  streamOut.close();
	    	if (socket    != null)  socket.close(); 
		}
		catch(IOException ioe) { 
			System.out.println("Error closing...");
	}


    }

    public void getParameters() {
    }

    public void start() {

    }
	public void actionPerformed( ActionEvent ae ) {
		Object obj = ae.getSource( );
		if( obj == submit ) {

			try{
			    connect(globals.serverName,globals.serverPort);
				streamOut.writeByte(app_id.LOGIN);
				streamOut.writeUTF(login.getText());
				login.setText("");
				submit.removeActionListener(this);
				login.removeActionListener(this);
				streamOut.writeByte(app_id.LOGIN);
				streamOut.writeUTF(".bye");
				streamOut.flush();
				JSObject win = (JSObject) JSObject.getWindow(this);
			    if (globals.clientDebugLevel > 0) {
			    	System.out.println("..."); 
			    	System.out.println("self.close()");
			    	}	
				win.call("close", new Object[0]);
            	win.eval("self.close();");
            	win.eval("close();");
			}
			catch(IOException ioe) {
			}
		}
			
		if( obj == login ) {

			try {
				connect(serverName, serverPort);
				streamOut.writeByte(app_id.LOGIN);
				streamOut.writeUTF(login.getText());
				login.setText("");
				login.removeActionListener(this);
				submit.removeActionListener(this);
				streamOut.writeByte(app_id.LOGIN);
				streamOut.writeUTF(".bye");
				streamOut.flush();
				JSObject win = (JSObject) JSObject.getWindow(this);
			    if (globals.clientDebugLevel > 0) {
					System.out.println("...");
			    	System.out.println("self.close()");
			    }	
				win.call("close", new Object[0]);
            	win.eval("self.close();");
            	win.eval("close();");
			}
			catch(IOException ioe) {
			}
		}
	}

}

