package common.synchronization;

import common.event.PhaseBeginEvent;
import common.event.TimerEvent;
import common.event.stream.StreamManager;

public class PhaseStreamTimer extends StreamTimer {

	public PhaseStreamTimer(StreamManager[] stateMachine) {
		super(stateMachine);
	}
	
	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		//calls the function that flushes next stream manager and changes state
		super.nextState();
	}

	@Override
	protected void tick(TimerEvent e){
		//do nothing, since wont' get a timer event (work is done in phase statred function)
	}
}
