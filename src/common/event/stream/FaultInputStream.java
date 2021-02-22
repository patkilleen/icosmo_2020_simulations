package common.event.stream;

import java.util.List;

import common.Vehicle;
import common.event.FaultEvent;

public class FaultInputStream extends EventInputStream <FaultEvent,Vehicle>{

	public FaultInputStream(List<Vehicle> partitionKeys) {
		super(partitionKeys);
	}
	
}
