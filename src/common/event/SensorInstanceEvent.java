package common.event;

import java.io.Serializable;

import common.Sensor;
import common.Vehicle;

public class SensorInstanceEvent extends VehicleEvent implements Serializable{

	private Sensor sensor;

	public SensorInstanceEvent(Vehicle aVehicle, Sensor sensor) {
		super(aVehicle);
		this.sensor = sensor;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	

}
