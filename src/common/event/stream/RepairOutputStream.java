package common.event.stream;

import java.util.List;

import common.Vehicle;
import common.event.RepairEvent;

public class RepairOutputStream extends EventOutputStream<RepairEvent,Vehicle> {

	public RepairOutputStream(List<Vehicle> partitionKeys) {
		super(partitionKeys);
	}



}
