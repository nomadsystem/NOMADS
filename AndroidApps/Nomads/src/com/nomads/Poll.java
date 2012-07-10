// Poll Client
// Nomads - University of Virginia
// 2012.06.26, revised by Paul Turowski

package com.nomads;

import nomads.v210.*;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class Poll extends Activity {
	private NSand sand;
	private NGrain grain;

	TextView question, textOut, textMin, textMax;
	SeekBar seekBar;
	Button buttonSend;
	
	byte currentQuestionType = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.poll);
	    question = (TextView)findViewById(R.id.promptIn);
	    textMin = (TextView)findViewById(R.id.min);
	    textMax = (TextView)findViewById(R.id.max);
	    seekBar = (SeekBar)findViewById(R.id.pollBar);
	    seekBar.setOnSeekBarChangeListener(seekListener);
		textOut = (TextView)findViewById(R.id.pbValue);
		buttonSend = (Button)findViewById(R.id.send);
		buttonSend.setOnClickListener(buttonListener);
		buttonSend.setVisibility(View.GONE);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Poll", "is resumed");
//		startThread();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Poll", "is paused");
//		stopThread();
	}

//update poll information from teacherpoll app
    public void parseGrain(NGrain _grain){
    	grain = _grain;
    	
    	Log.i("Poll", "setUI()");

		String toBePosed = new String(grain.bArray); //the incoming question ****STK 6/18/12

		NGlobals.cPrint("****************");
		NGlobals.cPrint("Student Poll handle method");
		NGlobals.cPrint("Student Poll Command = " + grain.command);
		NGlobals.cPrint("Student Poll toBePosed = " + toBePosed);


// allow students to vote again on previous question
    	if (grain.appID == NAppID.INSTRUCTOR_PANEL) {
    		if (grain.command == NCommand.VOTE) {
    			// unlock Send button
    			buttonSend.setVisibility(View.VISIBLE);
    		}
    	}

    	//if this string was sent from the teacher poll
    	else if (grain.appID == NAppID.TEACHER_POLL){
			//set the question type
			if (grain.command == NCommand.QUESTION_TYPE_YES_NO){
				if (currentQuestionType != NCommand.QUESTION_TYPE_YES_NO){
					changeRange(0, 1);
					Log.i("Poll", "yes/no question: " + toBePosed);
					textMin.setText("NO");
					textMax.setText("YES");
					seekBar.setProgress(0);
					textOut.setText("NO");
					buttonSend.setVisibility(View.VISIBLE);
				}
		    }
			else if (grain.command == NCommand.QUESTION_TYPE_A_TO_E){
				if (currentQuestionType != NCommand.QUESTION_TYPE_A_TO_E){
					changeRange(0, 4);
					Log.i("Poll", "A-E question: " + toBePosed);
					textMin.setText("A");
					textMax.setText("E");
					seekBar.setProgress(0);
					textOut.setText("A");
					buttonSend.setVisibility(View.VISIBLE);
				}
			}
			else if (grain.command == NCommand.QUESTION_TYPE_ONE_TO_TEN){
				if (currentQuestionType != NCommand.QUESTION_TYPE_ONE_TO_TEN){
					changeRange(0, 10);
					Log.i("Poll", "0-10 question: " + toBePosed);
					textMin.setText("0");
					textMax.setText("10");
					seekBar.setProgress(0);
					textOut.setText("0");
					buttonSend.setVisibility(View.VISIBLE);
				}
		    }
			
			currentQuestionType = grain.command;
      	 	 
		//set the question to be posed
		question.setText(toBePosed);
	      	 
	    }
    	else {
			grain = null;
		}
		if (grain != null)
			grain = null;    	 
    }
    
	public void changeRange(int _offset, int _range){
		seekBar.setThumbOffset(_offset);
		seekBar.setMax(_range);
	}
	 
	Button.OnClickListener buttonListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			String tString = textOut.getText().toString();
			int tLen = tString.length();
			byte[] tStringAsBytes = tString.getBytes();

			sand.sendGrain(NAppID.STUDENT_POLL, currentQuestionType, NDataType.BYTE, tLen, tStringAsBytes);
			
			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			buttonSend.setVisibility(View.GONE);
		}
	};
	
	SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (currentQuestionType == NCommand.QUESTION_TYPE_YES_NO){
				switch (progress){
					case 0:
						textOut.setText("No");
						break;
					case 1:
						textOut.setText("Yes");
						break;
					default:
						break;
				}
			}
			else if (currentQuestionType == NCommand.QUESTION_TYPE_A_TO_E){
				switch (progress){
					case 0:
						textOut.setText("A");
						break;
					case 1:
						textOut.setText("B");
						break;
					case 2:
						textOut.setText("C");
						break;
					case 3:
						textOut.setText("D");
						break;
					case 4:
						textOut.setText("E");
						break;
					default:
						break;
				}
			}
			else if (currentQuestionType == NCommand.QUESTION_TYPE_ONE_TO_TEN){
				textOut.setText("" + progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
//			Log.i("Poll", "onStartTrackingTouch");
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
//			Log.i("Poll", "onStopTrackingTouch:");
		}
	};
	
	public void setSand(NSand _sand) {
		sand = _sand;
		Log.i("Discuss", "setSand()");
	}
}