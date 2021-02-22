package common.event.stream;

import common.Vehicle;
import common.event.SensorDataEvent;

public class SensorDataStreamManager extends StreamManager<SensorDataEvent,Vehicle>{

	public SensorDataStreamManager(EventInputStream<SensorDataEvent,Vehicle> inStream, EventOutputStream<SensorDataEvent,Vehicle> outStream) {
		super(inStream, outStream);
	}
}
