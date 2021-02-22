package common.event.stream;

import java.util.List;

import common.Vehicle;
import common.event.SensorDataEvent;

public class SensorDataInputStream extends EventInputStream <SensorDataEvent,Vehicle>{

	public SensorDataInputStream(List<Vehicle> partitionKeys) {
		super(partitionKeys);
	}


}
