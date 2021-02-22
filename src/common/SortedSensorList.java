package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.exception.ConfigurationException;

public class SortedSensorList {
	
	private List<Sensor> sensors;
	
	public SortedSensorList() {
		sensors = new ArrayList<Sensor>();
	}
	
	/**
	 * Adds a list of sensors to the sorted sensor list.
	 * @param sensors List of sensors to add to the sensor list.
	 */
	public void add(List<Sensor> sensors){
		if(sensors== null){
			throw new ConfigurationException("could not add sensors to sorted list, due to null pointeur");
		}
		for(Sensor s : sensors){
			if(s == null){
				continue;
			}
			this.sensors.add(s);
		}
		
		Collections.sort(this.sensors);
	}
	
	/**
	 * Adds sensor to the sorted sensor list
	 * @param s Sensor to add.
	 */
	public void add(Sensor s){
		if(s == null){
			return;
		}
		this.sensors.add(s);
		Collections.sort(sensors);
	}
	
	/**
	 * Returns true when the sensor provided is found in the sorted sensor list.
	 * @param s The sensor to check if it exists. 
	 * @return True when it is found in the list, false otherwise.
	 */
	public boolean exists(Sensor s){
		if(s == null){
			return false;
		}
		return Collections.binarySearch(sensors,s) >=0;
	}
	
	/**
	 * Removes the provided sensors from the sorted list of sensors.
	 * @param sensors List of sensors to remove.
	 */
	public void remove(List<Sensor> sensors){
		if(sensors == null){
			return;
		}
		for(Sensor s : sensors){
			if(s == null){
				continue;
			}
			this.sensors.remove(s);
		}
		Collections.sort(this.sensors);
	}
	
	/**
	 * Removes a provided sensor from the list.
	 * @param s Sensor to remove from list.
	 */
	public void remove(Sensor s){
		if(s == null){
			return;
		}
		sensors.remove(s);
	}

	/**
	 * Returns the number of sensors in list. 
	 * @return Numver os sensors in list.
	 */
	public int size(){
		return sensors.size();
	}
}
