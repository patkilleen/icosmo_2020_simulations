package common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.event.MessageEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.TimerEvent;
import common.log.Logger;
import common.log.LoggerFactory;

public class Timer {

	public Timer() {

	}

	
	/**
	 * Called when an message arrives from another module.
	 * @param e The message event that contains information about the message.
	 * @throws InterruptedException 
	 */
	public void messageArrived(MessageEvent e) throws InterruptedException{
		
		if(e == null){
			Logger log = LoggerFactory.getInstance();
			log.log_error("null message event");
		}
		
		if(e instanceof TimerEvent){
			this.tick((TimerEvent) e);
		}else if(e instanceof PhaseBeginEvent){
			this.phaseStarted((PhaseBeginEvent)e);
		}else if(e instanceof PhaseCompleteEvent){
			this.phaseEnded((PhaseCompleteEvent)e);
		}else{
			Logger log = LoggerFactory.getInstance();
			log.log_warning("unkown message event type: "+e.getClass().toString());
		}
	}
	
	/**
	 * Hook to be overridden by subclasses which is called each time tick.
	 * @param e The timer event with the timerstamp of time tick.
	 * @throws InterruptedException Called when threads are interrupted as main thread waits for workers to finish.
	 */
	protected void tick(TimerEvent e) throws InterruptedException{
		
	}
	
	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		
	}

	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		
	}
	
}
