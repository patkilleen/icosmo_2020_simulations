package common.event.stream;

import common.Algorithm;
import common.event.SensorStatusEvent;

public class SensorStatusStreamManager extends StreamManager<SensorStatusEvent,Algorithm>{

	public SensorStatusStreamManager(EventInputStream<SensorStatusEvent,Algorithm> inStream, EventOutputStream<SensorStatusEvent,Algorithm> outStream) {
		super(inStream, outStream);
	}
	
	public SensorStatusOutputStream getSensorStatusOutputStream(){
		return (SensorStatusOutputStream) this.getOutputStream();
	}
}
