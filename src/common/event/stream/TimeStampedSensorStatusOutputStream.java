package common.event.stream;

import java.util.List;

import common.Algorithm;
import common.event.TimeStampedSensorStatusEvent;

/**
 * 
 * the double is the cosmo devaition threshold, which has many zscores per iteration (a whole set of zscores) 
 *
 */
public class TimeStampedSensorStatusOutputStream extends EventOutputStream <TimeStampedSensorStatusEvent,Algorithm>{

	public TimeStampedSensorStatusOutputStream(List<Algorithm> partitionKeys) {
		super(partitionKeys);
	}


}
