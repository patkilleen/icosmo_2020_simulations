package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SensorClassMap<S extends SensorInstance> implements Serializable {
 
	private HashMap<Sensor,List<S>> sensorInstanceMap;
	
	public SensorClassMap() {
		sensorInstanceMap = new HashMap<Sensor,List<S>>();
	}
	
	/**
	 * Adds a sensor instance the the map, if it isn't already found in the map.
	 * A valid sensor instance to is a sensor instance not already found in the map (doesn't share 
	 * a vehicle with another sensor instance of the same class.), and non-null 
	 * @param instance The sensor instance to add to the map.
	 * @return True when the instance was succefully added, and false when the sensor instance wasn't valid. 
	 */
	public boolean addSensorInstance(S instance){
		
		if(instance == null){
			return false;
		}
		boolean successFlag = false;
		//using the instance istelf to act as the sensor class key
		//using polymorphism (the id of sensor is used as key)
		Sensor sensorClass = (Sensor) instance;
		
		List<S> instances = getSensorInstances(sensorClass);
		
		//new sensor class?
		if(instances.isEmpty()){
			//new sensor classs, no need to check existing instance constraints
			successFlag = true;
			instances.add(instance);
			sensorInstanceMap.put(sensorClass, instances);
		}else{
			
			//meets constrains?
			if(SensorClassMap.<S>checkConstraints(instances,instance)){
				successFlag = true;
				instances.add(instance);
			}else{
				successFlag = false;
			}
		}
		
		return successFlag;
	}
	/**
	 * Returns the sensor instances associated to a sensor class.
	 * @param sensorClass The sensor class of the desired sensor intances.
	 * @return Sensor instances found.
	 */
	public List<S> getSensorInstances(Sensor sensorClass){
		List<S> res = null;
		
		//sensor class exists?
		if(sensorInstanceMap.containsKey(sensorClass)){
			res = sensorInstanceMap.get(sensorClass);
		}else{
			res = new ArrayList<S>(0);
		}
		
		return res;
	}

	public Iterator<Sensor> getSensorClasses(){
		return sensorInstanceMap.keySet().iterator();
	}
	/**
	 * Returns true if a new sensor instance is of the same class as a list of 
	 * other instances, and that they don't share a vehicle in common.
	 * @param instances The list of instances to compare the new instance to.
	 * @param newInstance The candidate instance to check constraints.
	 * @return True when the new instance doesn't share a vehicle and is of same class as all other instance, false otherwise.
	 */
	private static <E extends SensorInstance> boolean checkConstraints(List<E> instances, E newInstance){
		boolean res = true;
		
		if(newInstance == null || instances == null){
			return false;
		}
		
		//iterate each instance
		for(E s : instances){
			
			Vehicle v1 = s.getVehicle();
			Vehicle v2 = newInstance.getVehicle();
			
			//unique vehicle constaint check
			if(v1.equals(v2)){
				//didn't meet vehicle constraint
				res = false;
				break;
			}
			
			//sensor instances all have same class
			if(!newInstance.equals(s)){
				//didn't meet identical sensor class constraint
				res = false;
				break;
			}
		}
		
		return res;
	}

}
