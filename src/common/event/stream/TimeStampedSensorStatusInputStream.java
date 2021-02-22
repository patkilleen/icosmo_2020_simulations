package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.TimeStampedSensorStatusEvent;

public class TimeStampedSensorStatusInputStream extends EventInputStream <TimeStampedSensorStatusEvent,Algorithm>{

	public TimeStampedSensorStatusInputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}


}
