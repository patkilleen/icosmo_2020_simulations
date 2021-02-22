package common.event;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;

public class ICosmoSensorStatusEvent extends SensorStatusEvent {

	public ICosmoSensorStatusEvent(Vehicle aVehicle, Sensor aSensor, Algorithm algorithm, double zscore,
			boolean cosmoSensorFlag, double zvalue) {
		super(aVehicle, aSensor, algorithm, zscore, cosmoSensorFlag, zvalue);
		// TODO Auto-generated constructor stub
	}

}
