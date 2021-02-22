package common.event.stream;

import common.Algorithm;
import common.event.ROCCurvePointEvent;

public class ROCCurvePointStreamManager extends StreamManager<ROCCurvePointEvent,Algorithm>{

	public ROCCurvePointStreamManager(EventInputStream<ROCCurvePointEvent,Algorithm> inStream, EventOutputStream<ROCCurvePointEvent,Algorithm>outStream) {
		super(inStream, outStream);
	}
	
	@Override
	public void flush(){
		super.flush();
	}
}
