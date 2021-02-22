package phase.generation.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Sensor;
import common.Util;
import common.Vehicle;
import common.exception.ConfigurationException;


public class DataGenerationVehicle extends Vehicle implements Serializable{

	private List<DataGenerationSensor> sensors;
	
	public DataGenerationVehicle(int aVid,  List<DataGenerationSensor> sensors) {
		super(aVid);
		validityCheck(sensors);
		this.sensors = sensors;
		
		//sort the sensors to enable binary search
		Collections.sort(sensors);
	}

	public DataGenerationVehicle(int aVid){
		super(aVid);
		sensors = new ArrayList<DataGenerationSensor>(128);
	}
			
	public void addSensor(DataGenerationSensor s){
		if(s == null){
			throw new ConfigurationException("cannot add null sensor to data generation vehicle");
		}
		sensors.add(s);
		//sort the sensors to enable binary search
		Collections.sort(sensors);
	}
	
	private static void validityCheck(List<DataGenerationSensor> sensors) {
		//sensors need to be unique, non-null, and atleast one sensors
		if(sensors == null || sensors.isEmpty() || !Util.allElementsUnique(sensors)){
			throw new ConfigurationException("could not create data generation vehicle due to null or empty sensor list. ");
		}
		
	}

	//private static void 
	public List<DataGenerationSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<DataGenerationSensor> sensors) {
		validityCheck(sensors);
		this.sensors = sensors;
	}

	/**
	 * Given a sensors, finds the corresponding sensor of this vehicle.
	 * @param s The sensor used as the lookup key.
	 * @return The DatagenerationSensor found, or null if it doesn't exist.
	 */
	public DataGenerationSensor findSensor(Sensor s){
		if(s == null){
			return null;
		}
		int ix = Collections.binarySearch(sensors,s);
		
		if(ix < 0){
			return null;
		}else{
			return sensors.get(ix);
		}
	}
}
