package common.event.stream;

import common.Vehicle;
import common.event.RepairEvent;

public class RepairStreamManager extends StreamManager<RepairEvent,Vehicle>{

	public RepairStreamManager(EventInputStream<RepairEvent,Vehicle> inStream, EventOutputStream<RepairEvent,Vehicle> outStream) {
		super(inStream, outStream);
	}
}
