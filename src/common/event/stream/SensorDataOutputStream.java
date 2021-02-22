package common.event.stream;

import java.util.List;

import common.Vehicle;
import common.event.SensorDataEvent;

public class SensorDataOutputStream extends EventOutputStream <SensorDataEvent,Vehicle>{

	public SensorDataOutputStream(List<Vehicle> partitionKeys) {
		super(partitionKeys);
	}



}
