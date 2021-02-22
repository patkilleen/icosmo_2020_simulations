package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.SensorStatusEvent;

public class SensorStatusInputStream extends EventInputStream <SensorStatusEvent,Algorithm>{

	public SensorStatusInputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}



}
