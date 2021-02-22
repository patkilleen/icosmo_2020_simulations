package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.ROCCurvePointEvent;

public class ROCCurvePointInputStream extends EventInputStream <ROCCurvePointEvent,Algorithm>{

	public ROCCurvePointInputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}


}
