package common.event;

public class TimeStampedPhaseCompleteEvent extends PhaseCompleteEvent {

	private int time;
	public TimeStampedPhaseCompleteEvent(int time) {
		this.time = time;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	

}
