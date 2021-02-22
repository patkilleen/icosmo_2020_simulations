package common.event.stream;

import java.util.List;

import common.Vehicle;
import common.event.RepairEvent;

public class RepairInputStream extends EventInputStream<RepairEvent,Vehicle> {

	public RepairInputStream(List<Vehicle> partitionKeys) {
		super(partitionKeys);
	}


	

}
