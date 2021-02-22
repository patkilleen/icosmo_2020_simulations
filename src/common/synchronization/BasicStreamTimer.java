package common.synchronization;

import common.Timer;
import common.event.TimerEvent;
import common.event.stream.StreamManager;
import common.exception.ConfigurationException;

public class BasicStreamTimer extends Timer {

	
	
	@SuppressWarnings("rawtypes")
	private StreamManager streamManager;

	public BasicStreamTimer(StreamManager sm){
		
		streamManager = sm;
	}

	/**
	 * Contructor that's empty for subclass flexibility
	 */
	protected BasicStreamTimer(){
		
	}
	
	/**
	 * Called when a model's output should be transfered to the input of another model.
	 * @param e The event indicating output should be flushed to the input of another model
	 */
	protected void tick(TimerEvent e){
		streamManager.flush();
	}
}
