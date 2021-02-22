package common.event.stream;

import common.Vehicle;
import common.event.FaultEvent;

public class FaultStreamManager extends StreamManager<FaultEvent,Vehicle>{

	public FaultStreamManager(EventInputStream<FaultEvent,Vehicle> inStream, EventOutputStream<FaultEvent,Vehicle> outStream) {
		super(inStream, outStream);
	}
}
