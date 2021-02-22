package common.event.stream;

import common.Vehicle;
import common.event.SensorStatusEvent;

public class SensorZScoreStreamManager extends StreamManager<SensorStatusEvent,Vehicle>{

	public SensorZScoreStreamManager(EventInputStream<SensorStatusEvent,Vehicle> inStream, EventOutputStream<SensorStatusEvent,Vehicle> outStream) {
		super(inStream, outStream);
	}
}
