package phase.generation.cosmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Sensor;
import common.Vehicle;
import common.log.Logger;
import common.log.LoggerFactory;
import phase.generation.cosmo.Histogram.DistanceMeasure;

public class RandomSensorCOSMO extends COSMO {
	
	final public static int ID_OFFSET = 32;
	
	final private static String NAME_PREFIX = "RS-";
	public RandomSensorCOSMO(int id, List<Vehicle> vehicles, DistanceMeasure distanceMeasure, int numSensorSelect,
			int zscoreUpdateFrequency, String name) {
		super(id, vehicles, distanceMeasure, numSensorSelect, zscoreUpdateFrequency, NAME_PREFIX+name);
		//the only difference is  a prefix is added to name
		//e.g., 'Hellinger' with id 32, would be id 32 and name 'RS-Hellinger'
	}

	public RandomSensorCOSMO(int id, List<Vehicle> vehicles, DistanceMeasure distanceMeasure, int numSensorSelect,
			int zscoreUpdateFrequency) {
		super(id, vehicles, distanceMeasure, numSensorSelect, zscoreUpdateFrequency);
	}

	public RandomSensorCOSMO() {
	
	}
	
	/**
	 * Override the parent class sensor selection based on sensor interest
	 * to instead pick sensors randomly
	 */
	@Override
	protected void sensorSelection() {
		
		Logger log = LoggerFactory.getInstance();
		
		List<Sensor> sensorClasses = this.getSensors();
		
		
		//create a duplicate list to shuffle without shuffling the list of sensors in this algorithm instance
		List<Sensor> sensorClassesRNG = new ArrayList<Sensor>(sensorClasses.size());
		
		for(Sensor sc : sensorClasses) {
			sensorClassesRNG.add(sc);
		}
		
		//we sort the senosrs randomly to pick them arbitrarily to act asa base line that should perform poorly
		Collections.shuffle(sensorClassesRNG);
		
		//choose the top candidates sensors (most interesting) to add to cosmo model
		for(int j = 0; j <this.getNumSensorSelect();j++){
	
			Sensor s = sensorClassesRNG.get(j);
			
			log.log_info("RandomSensorCOSMO: ("+this.getDistanceMeasure().toString()+") selecting sensor: "+s);
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);
			
			//include all snesor instances of most interesting sensor classes in cosmo model (select them)
			for(COSMOSensorInstance i : instances){
				i.setCOSMOSensor(true);
			}
		}
		
	}


}
