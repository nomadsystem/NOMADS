
/** 
 * Play a file by streaming off of disk.
 * Output the synthesized data using SampleQueueOutputStream
 *
 * @author (C) 2003 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.util.SampleQueueOutputStream;
import com.softsynth.jsyn.view11x.PortFader;

class SampleFileStreamer extends SynthCircuit implements Runnable {
	public SynthSample mySamp;
	SampleReader mySampler;
	final static int FRAMES_PER_BLOCK = 400;
	// number of frames to synthesize at one time
	final static int FRAMES_IN_BUFFER = 8 * 1024;
	int numChannels = 1;
	int samplesPerBlock;
	short[] data;
	SampleQueueOutputStream outStream;
	Thread thread;
	SynthInput amplitude;
	File sampleFile;
	InputStream inStream;
	int numFrames;
	int frameCursor;

	/** Create a streamer for the specified file.
	 * File header will be read for info needed for streaming.
	 */
	public SampleFileStreamer(File file) throws IOException {
		super();
		sampleFile = file;
		InputStream stream = new FileInputStream(file);
		setup(stream);
	}

	/** Analyse sample header and create appropriate player. */
	private void setup(InputStream stream) throws IOException {
		BufferedInputStream bufStream = new BufferedInputStream(stream);
		mySamp = new SynthSampleWAV();

		// Preload to get offset to data and other sample info.
		// The false flag means do not load the actual WAV data at this time.
		mySamp.load(bufStream, false);
		mySamp.dump();

		bufStream.close();

		numChannels = mySamp.getChannelsPerFrame();
		numFrames = mySamp.getNumFrames();

		//	Create SynthUnits to play sample data.
		if (numChannels == 1) {
			mySampler = new SampleReader_16F1();
		} else if (numChannels == 2) {
			mySampler = new SampleReader_16F2();
		} else {
			throw new RuntimeException("This example only supports mono or stereo!");
		}
		add(mySampler);

		samplesPerBlock = FRAMES_PER_BLOCK * numChannels;
		data = new short[samplesPerBlock];
		addPort(amplitude = mySampler.amplitude);

		// Create a stream that we can write to.
		outStream =
			new SampleQueueOutputStream(
				mySampler.samplePort,
				FRAMES_IN_BUFFER,
				numChannels);
	}

	public SynthOutput getOutput() {
		return mySampler.output;
	}

	/** Thread task that plays a file from disk. */
	public void run() {
		try {
			try {
				while (thread != null) {
					if( sendBuffer() <= 0 ) break;
				}
			} finally {
				outStream.flush();
				inStream.close();
			}
		} catch (IOException e) {
			System.out.println("run() caught " + e);
		} catch (SynthException e) {
			System.out.println("run() caught " + e);
		}
	}

	/** Read data from file and write it to the audio output stream.
	 * @return framesLeft;
	 */
	int sendBuffer() throws IOException {
		int samplesToRead = samplesPerBlock;
		int framesLeft = numFrames - frameCursor;
		int samplesLeft = framesLeft * numChannels;
		if (samplesToRead > samplesLeft)
			samplesToRead = samplesLeft;
		int i = 0;

		// Read sample data from file as bytes and assemble into 16 bit samples.
		while (i < samplesToRead) {
			int sample = inStream.read(); // get LSB
			if (sample < 0) {
				throw new IOException("Premature EOF");
			}
				sample = (inStream.read() << 8) // MSB
	| (sample & 0x00FF);
			data[i++] = (short) sample;
		}

		if (i > 0) {
			int framesRead = i / numChannels;
			frameCursor += framesRead;
			// Write data to the stream.
			// Will block if there is not enough room so run in a thread.
			outStream.write(data, 0, framesRead);
		}
		return framesLeft;
	}

	/** Start playing the stream in the background thread. */
	void startStream() {
		try {
			FileInputStream fileStream = new FileInputStream(sampleFile);
			inStream = new BufferedInputStream(fileStream);

			// Skip to where the sample data starts.
			inStream.skip(mySamp.getOffset());

			frameCursor = 0;
			// Prefill output stream buffer so that it starts out full.
			while (outStream.available() > FRAMES_PER_BLOCK)
				sendBuffer();
			// Start slightly in the future so everything is synced.
			int time = Synth.getTickCount() + 4;
			mySampler.start(time);
			outStream.start(time);

			// launch a thread to keep stream supplied with data
			thread = new Thread(this);
			thread.start();
		} catch (IOException exc) {
			System.err.println(exc.toString());
		}
	}

	/** Stop playing the stream and wait for data to flush. */
	void stopStream() {
		Thread myThread = thread;
		if (thread != null) {
			thread = null; // tells thread to stop!
			try {
				myThread.join(1000);
			} catch (InterruptedException e) {
			}
			int time = Synth.getTickCount();
			outStream.stop(time);
			mySampler.stop(time);
		}
	}
}

public class TJ_StreamSampleFile extends Applet {
	Button startButton, stopButton;
	SampleFileStreamer streamer;
	LineOut myOut;
	String fileName =
		"E:\\nomad\\jwork\\website\\classes\\samples\\LoudAndVerySoft.wav";

	/* Can be run as either an application or as an applet. */
	public static void main(String args[]) {
		TJ_StreamSampleFile applet = new TJ_StreamSampleFile();
		AppletFrame frame = new AppletFrame("Stream a Sample File", applet);
		frame.resize(600, 300);
		frame.show();
		/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

	/*
	 * Setup synthesis.
	 */
	public void start() {
		setLayout(new GridLayout(0, 1));

		try {
			// Make sure we are using the necessary version of JSyn
			Synth.requestVersion(142);

			// Start synthesis engine.
			Synth.startEngine(0);

			try {
				// Load sample from a file.
				File sampleFile = new File(fileName);
				streamer = new SampleFileStreamer(sampleFile);
			} catch (IOException exc) {
				exc.printStackTrace(System.err);
				throw new RuntimeException(exc.getMessage());
			}

			myOut = new LineOut();

			// Connect streamer to output.
			streamer.getOutput().connect(0, myOut.input, 0);
			if (streamer.getOutput().getNumParts() > 1) {
				streamer.getOutput().connect(1, myOut.input, 1);
			} else {
				streamer.getOutput().connect(0, myOut.input, 1);
			}

			// Show faders so we can manipulate sound parameters.
			add(new PortFader(streamer.amplitude, 0.7, 0.0, 1.0));

			// Start execution of units.
			myOut.start();

		} catch (SynthException e) {
			SynthAlert.showError(this, e);
		}

		add(startButton = new Button("Start"));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				streamer.startStream();
			}
		});

		add(stopButton = new Button("Stop"));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				streamer.stopStream();
			}
		});

		// Synchronize Java display.
		getParent().validate();
		getToolkit().sync();
	}

	public void stop() {
		try {
			streamer.stopStream();

			// Delete unit peers.
			streamer.delete();
			streamer = null;
			myOut.delete();
			myOut = null;
			removeAll(); // remove portFaders
			// Turn off tracing.
			Synth.verbosity = Synth.SILENT;
			// Stop synthesis engine.
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this, e);
		}
	}

}
