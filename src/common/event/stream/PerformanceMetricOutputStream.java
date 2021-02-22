package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.PerformanceMetricEvent;

public class PerformanceMetricOutputStream extends EventOutputStream <PerformanceMetricEvent,Algorithm>{

	public PerformanceMetricOutputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}

	

}
