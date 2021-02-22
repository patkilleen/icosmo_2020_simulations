package common.event.stream;

import java.util.List;

import common.Vehicle;
import common.event.FaultEvent;

public class FaultOutputStream extends EventOutputStream <FaultEvent,Vehicle>{

	public FaultOutputStream(List<Vehicle> partitionKeys) {
		super(partitionKeys);
	}
}
