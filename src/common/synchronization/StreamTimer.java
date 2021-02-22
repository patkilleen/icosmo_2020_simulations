package common.synchronization;

import common.Timer;
import common.event.TimerEvent;
import common.event.stream.StreamManager;
import common.exception.ConfigurationException;

public class StreamTimer extends Timer {

	
	
	public final static int DEFAULT_STREAM_MAGR_IX =0;
	
	@SuppressWarnings("rawtypes")
	private StreamManager [] stateMachine;
	 
	private int state;
	
	public StreamTimer(StreamManager [] stateMachine){
		
		init(stateMachine);
	}

	protected void init(StreamManager [] stateMachine){
		if(stateMachine == null || stateMachine.length ==0){
			throw new ConfigurationException("failed to build stream manager, null state machine;");
		}
		this.stateMachine = stateMachine;
		state = DEFAULT_STREAM_MAGR_IX;
	}
	/**
	 * Contructor that's empty for subclass flexibility
	 */
	protected StreamTimer(){
		
	}
	/**
	 * Flushes the current stream manager and changes state to next stream manager; 
	 */
	protected void nextState(){
		//transfer the input to output of a model
		StreamManager sm = stateMachine[state];
		
		sm.flush();
		
		//cycle state through all the states, one by one
		state = (state + 1) %  stateMachine.length;
	}
	/**
	 * Called when a model's output should be transfered to the input of another model.
	 * @param e The event indicating output should be flushed to the input of another model
	 */
	protected void tick(TimerEvent e){
		nextState();
	}
}
