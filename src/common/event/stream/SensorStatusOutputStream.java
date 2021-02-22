package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.SensorStatusEvent;

public class SensorStatusOutputStream extends EventOutputStream <SensorStatusEvent,Algorithm>{

	public SensorStatusOutputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}


}
