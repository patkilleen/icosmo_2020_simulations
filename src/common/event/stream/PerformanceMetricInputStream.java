package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.PerformanceMetricEvent;

public class PerformanceMetricInputStream extends EventInputStream <PerformanceMetricEvent,Algorithm>{

	public PerformanceMetricInputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	
	}


}
