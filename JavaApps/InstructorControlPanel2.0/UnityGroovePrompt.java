/*
  NOMADS Unity Groove Prompt v.210
  Revised/cleaned, 6/19/2012, Steven Kemper
  Integrating NSAND class
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.JToggleButton;
import javax.swing.DefaultButtonModel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.v210.*;

public class UnityGroovePrompt extends JPanel implements ChangeListener
{


    NSand mySand;

    private Boolean enabled = false;
    private Boolean started = false;
    JToggleButton   synthOn;
    JToggleButton	synthEnabled;
    JLabel		stateLabel;
	

	
    public void init(NSand inSand)
    {
	mySand = inSand;
	//	synthOn = false;

	//		Container content = getContentPane();
	//    	content.setBackground(Color.black);

	createButtons();
    }

    public void createButtons() 
    {

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));  // sets horizontal layout
	setBackground(Color.black);

	JPanel buttonWrapper = new JPanel();
	buttonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
	buttonWrapper.setBackground(Color.black);

	synthEnabled = new JToggleButton("Synth Enabled");
	synthEnabled.setContentAreaFilled(false);
	synthEnabled.setBackground(Color.black);
	synthEnabled.setOpaque(true);
	//		synthEnabled.setFont(buttonFont);
	synthEnabled.setContentAreaFilled(false);
	synthEnabled.addChangeListener(this);
	//synthEnabled.addChangeListener(changeListener);
	//synthEnabled.addActionListener(this);

	synthOn = new JToggleButton("Synth Start");
	synthOn.setContentAreaFilled(false);
	synthOn.setBackground(Color.black);
	synthOn.setOpaque(true);
	//		synthOn.setFont(buttonFont);
	synthOn.setContentAreaFilled(false);
	synthOn.addChangeListener(this);
	//synthOn.addChangeListener(changeListener);
	//	synthOn.addActionListener(this);



	buttonWrapper.add( synthEnabled );
	buttonWrapper.add( synthOn );

	JPanel labelWrapper = new JPanel();
	labelWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
	labelWrapper.setBackground(Color.black);

	// stateLabel = new JLabel("");
	// 		stateLabel.setForeground( Color.WHITE );
	// 		stateLabel.setBackground( Color.BLACK );
	// 		stateLabel.setOpaque(true);
	// 		stateLabel.setFont(new Font("Serif", Font.BOLD, 36));
	// 		System.out.println(synthOn);
	// 		if (synthOn) {
	// 			stateLabel.setText("Synth is On");
	// 		}
	// 		else {
	// 			stateLabel.setText("Synth is OFF");
	// 		}
	// 		
	// 		labelWrapper.add( stateLabel );

	this.add( buttonWrapper );
	//     	pane.add( labelWrapper );

    }

    public void handle(NGrain inGrain)
    {
	//Not currently doing anything ****STK 06/19/12
    }

    //	public void actionPerformed(java.awt.event.ActionEvent ae)
    //	{
    //		Object source = ae.getSource();
    //
    //		if (source == synthEnabled) {
    //			if (selected && !enabled) {
    //				enabled = true;
    //				//Sending data
    //				uGrooveSand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_ENABLE, (byte)NDataType.NO_DATA, 1, noByte );
    //				NGlobals.cPrint("UG: SYNTH_ENABLE");
    //			}
    //			else if (!selected && enabled) {
    //				enabled = false;
    //				uGrooveSand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_DISABLE, (byte)NDataType.NO_DATA, 1, noByte );
    //				NGlobals.cPrint("UG: SYNTH_DISABLE");
    //			}
    //		}
    //		else if (source == synthOn) {
    //			if (selected && !started) {
    //				started = true;
    //				uGrooveSand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_START, (byte)NDataType.NO_DATA, 1, noByte  );
    //				NGlobals.cPrint("UG: SYNTH_START");
    //			}
    //			else if (!selected && started) {
    //				started = false;
    //				uGrooveSand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_STOP, (byte)NDataType.NO_DATA, 1, noByte  );
    //				NGlobals.cPrint("UG: SYNTH_STOP");
    //			}
    //		}
    //	}


    public void stateChanged(ChangeEvent changeEvent) {
	NGlobals.cPrint("UG: Entering Change Listener");
	AbstractButton abstractButton = (AbstractButton) changeEvent.getSource();
	ButtonModel buttonModel = abstractButton.getModel();
	//		boolean armed = buttonModel.isArmed();
	//		boolean pressed = buttonModel.isPressed();
	boolean selected = buttonModel.isSelected();



	if (abstractButton == synthEnabled) {
	    if (selected && !enabled) {
		enabled = true;
		byte d[] = new byte[1];
		d[0] = 1;
		//Sending data
		mySand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_ENABLE_STATUS, (byte)NDataType.UINT8, 1, d );
		NGlobals.cPrint("UG: SYNTH_ENABLE");
	    }
	    else if (!selected && enabled) {
		enabled = false;
		byte d[] = new byte[1];
		d[0] = 0;
		mySand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_ENABLE_STATUS, (byte)NDataType.UINT8, 1, d );
		NGlobals.cPrint("UG: SYNTH_DISABLE");
	    }
	}
	else if (abstractButton == synthOn) {
	    if (selected && !started) {
		started = true;
		byte d[] = new byte[1];
		d[0] = 1;
		mySand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_START_STOP, (byte)NDataType.UINT8, 1, d  );
		NGlobals.cPrint("UG: SYNTH_START");
	    }
	    else if (!selected && started) {
		started = false;
		byte d[] = new byte[1];
		d[0] = 0;
		mySand.sendGrain((byte)NAppID.INSTRUCT_EMRG_SYNTH_PROMPT, (byte)NCommand.SYNTH_START_STOP, (byte)NDataType.UINT8, 1, d  );
		NGlobals.cPrint("UG: SYNTH_STOP");
	    }

	}
    };



    /* Process button hits. */
}