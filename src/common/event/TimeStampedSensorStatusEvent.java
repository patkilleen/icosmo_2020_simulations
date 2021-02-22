package common.event;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;

public class TimeStampedSensorStatusEvent extends SensorStatusEvent {

	
	private TimerEvent timerEvent;
	public TimeStampedSensorStatusEvent(Vehicle aVehicle, Sensor aSensor, Algorithm algorithm, double zscore,
			boolean cosmoSensorFlag, double zvalue, TimerEvent timerEvent) {
		super(aVehicle, aSensor, algorithm, zscore, cosmoSensorFlag, zvalue);
		
		this.timerEvent = timerEvent;
	}

	public TimerEvent getTimerEvent() {
		return timerEvent;
	}
	public void setTimerEvent(TimerEvent timerEvent) {
		this.timerEvent = timerEvent;
	}

	
	
}
