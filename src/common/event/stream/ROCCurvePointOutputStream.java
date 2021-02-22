package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.ROCCurvePointEvent;

public class ROCCurvePointOutputStream extends EventOutputStream <ROCCurvePointEvent,Algorithm>{

	public ROCCurvePointOutputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}


}
