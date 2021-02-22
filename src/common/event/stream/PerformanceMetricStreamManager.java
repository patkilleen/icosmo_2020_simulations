package common.event.stream;

import common.Algorithm;
import common.event.PerformanceMetricEvent;

public class PerformanceMetricStreamManager extends StreamManager<PerformanceMetricEvent,Algorithm>{

	public PerformanceMetricStreamManager(EventInputStream<PerformanceMetricEvent,Algorithm> inStream, EventOutputStream<PerformanceMetricEvent,Algorithm> outStream) {
		super(inStream, outStream);
	}
}
