package common.event.stream;

import common.Algorithm;
import common.Vehicle;
import common.event.SensorStatusEvent;
import common.event.TimeStampedSensorStatusEvent;

public class TimeStampedSensorStatusStreamManager extends StreamManager<TimeStampedSensorStatusEvent,Algorithm> {

	public TimeStampedSensorStatusStreamManager(EventInputStream<TimeStampedSensorStatusEvent,Algorithm> inStream, EventOutputStream<TimeStampedSensorStatusEvent,Algorithm> outStream) {
		super(inStream, outStream);
	}
}
