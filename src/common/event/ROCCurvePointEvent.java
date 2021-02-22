package common.event;

import java.util.HashMap;
import java.util.List;

public class ROCCurvePointEvent extends Event {

	//point of roc curve
	private double cosmoDeviationThreshold;
	
	//all sensors statuses of the roc point 
	private List<TimeStampedSensorStatusEvent> sensorStatuses;

	
	
	public ROCCurvePointEvent(double cosmoDeviationThreshold, List<TimeStampedSensorStatusEvent> sensorStatuses) {
		super();
		this.cosmoDeviationThreshold = cosmoDeviationThreshold;
		this.sensorStatuses = sensorStatuses;
	}

	public double getCosmoDeviationThreshold() {
		return cosmoDeviationThreshold;
	}

	public void setCosmoDeviationThreshold(double cosmoDeviationThreshold) {
		this.cosmoDeviationThreshold = cosmoDeviationThreshold;
	}

	public List<TimeStampedSensorStatusEvent> getSensorStatuses() {
		return sensorStatuses;
	}

	public void setSensorStatuses(List<TimeStampedSensorStatusEvent> sensorStatuses) {
		this.sensorStatuses = sensorStatuses;
	} 
	
	
}
