package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.exception.ConfigurationException;
import phase.configuration.input.IConfig;


public class SensorMap implements Serializable{
	
	
	/**
	 * Map that is upadated as sensor status events occur, updating each sensor
	 */
	private HashMap<Algorithm,HashMap<Vehicle,HashMap<Sensor,SensorInstance>>> sensorInstanceMap;
	
	/**
	 * Map that is used to lookup all sensor instances of a sensor class
	 */
	private HashMap<Algorithm,SensorClassMap<SensorInstance>> sensorClassMap;
	
	private List<Algorithm> algorithms;
	
	private List<Sensor> sensors;
	
	private List<Vehicle> vehicles;
	
	/**
	 * Creates the sensor map. 
	 * @param algorithms Non-empty list of unique algorithms
	 */
	public SensorMap(List<Algorithm> algorithms) {
		
		if(algorithms == null || algorithms.size()==0){
			throw new ConfigurationException("could not create icosmomap, null algorithms.");
		}
		
		this.algorithms = algorithms;
		
		//initialize the maps
		sensorInstanceMap = new HashMap<Algorithm,HashMap<Vehicle,HashMap<Sensor,SensorInstance>>>();
		sensorClassMap = new HashMap<Algorithm,SensorClassMap<SensorInstance>>();
		
		//populate maps
		for(Algorithm alg : algorithms){
			
			HashMap<Vehicle,HashMap<Sensor,SensorInstance>> innerInstanceMap  = new HashMap<Vehicle,HashMap<Sensor,SensorInstance>>();
			
			//make sure there isn't a duplicate algorithm
			if(sensorInstanceMap.containsKey(alg)){
				throw new ConfigurationException("dupliate algorithm found, cannot crate sensor map ");
			}
			sensorInstanceMap.put(alg,innerInstanceMap);
			
			SensorClassMap<SensorInstance> innerClassMap = new SensorClassMap<SensorInstance>();
			
			//make sure there isn't a duplicate algorithm
			if(sensorClassMap.containsKey(alg)){
				throw new ConfigurationException("dupliate algorithm found, cannot crate sensor map ");
			}
			sensorClassMap.put(alg,innerClassMap);
		}
	}

	/**
	 * resets all sensor instance to default attributes, or creates new sensor instance given a factory.
	 * @param vehicles
	 * @param sensors
	 */
	public void init(List<Vehicle> vehicles, List<Sensor> sensors, SensorInstanceFactory factory){
		
		if(vehicles == null || vehicles.isEmpty()){
			throw new ConfigurationException("could not initiate SensorMap due to empty or null vehicles");
		}
		
		this.vehicles = vehicles; 
		
		if(sensors == null || sensors.isEmpty()){
			throw new ConfigurationException("could not initiate SensorMap due to empty or null sensors. Make sure that all sensors defined in "
					+ "white list ("+IConfig.PROPERTY_SENSOR_WHITE_LIST+") have a defined behavior in directory ("+IConfig.PROPERTY_DATA_GENERATION_TIMER_DATA_GENERATION_SENSOR_DIRECTORY+")");
		}
		
		this.sensors = sensors;
		
		if(factory == null){
			throw new ConfigurationException("could not initiate SensorMap due to null factory");
		}
		//populate maps
		for(Algorithm alg : algorithms){
			SensorClassMap<SensorInstance> classMap = sensorClassMap.get(alg);
			HashMap<Vehicle,HashMap<Sensor,SensorInstance>> innerVehicleMap = sensorInstanceMap.get(alg);
			//populate the maps
			for(Vehicle v : vehicles){
				
				HashMap<Sensor,SensorInstance> innerSensorMap = new 	HashMap<Sensor,SensorInstance>(); 
				
				innerVehicleMap.put(v,innerSensorMap);
				
				for(Sensor s: sensors){
					SensorInstance instance = factory.newInstance(v, s);
					innerSensorMap.put(s,instance);
					classMap.addSensorInstance(instance);
				}//end iteratee sensors
			}//end iterate vehicles
		}//end iterate algorithms
	}
	

	/**
	 * Fetches a sensor instance from map.
	 * @param alg The algorithm of the sensor instance
	 * @param v The vehicle of the sensor instance
	 * @param s The sensor class of the instance
	 * @return The sensor instance found in the map, or null if non existant
	 */
	public SensorInstance getSensorInstance(Algorithm alg, Vehicle v, Sensor s){
		
		if(alg == null){
			return null;
		}
		SensorInstance res = null;
		HashMap<Vehicle,HashMap<Sensor,SensorInstance>> innerVehicleMap = sensorInstanceMap.get(alg);
		
		//lookup the sensor map for vehicle
		HashMap<Sensor,SensorInstance> innerSensorMap = innerVehicleMap.get(v);
		
		//doesn't exist yet?
		if(innerSensorMap == null){
			return null;
		}else{
		
			//does the sensor not exist yet?
			if(!innerSensorMap.containsKey(s)){
				
				return null;
			}else{
				
				//fetch the icosmo sensor instance
				res = innerSensorMap.get(s);
				
				if(res == null){
					return null;
				}
			}
		}
		return res;
	}

	/**
	 * Returns a list of sensor instances given a class of sensor.
	 * @param alg the Algorithm
	 * @param sensorClass The class of sensor to return
	 * @return List of instances, or empty if not existant.
	 */
	public List<SensorInstance> getSensorInstances(Algorithm alg, Sensor sensorClass) {
		if(alg == null){
			return new ArrayList<SensorInstance>(0);
		}
		return sensorClassMap.get(alg).getSensorInstances(sensorClass);
	}

	/**
	 * Returns the vehicles from this map.
	 * @return the vehicles
	 */
	public List<Vehicle> getVehicles(){
		return vehicles;
	}
	

	/**
	 * Returns the sensors from this map.
	 * @return the sensors
	 */
	public List<Sensor> getSensors(){
		return sensors;
	}
	
	/**
	 * returns list of algorithms (keys)
	 * @return algorithms
	 */
	public List<Algorithm> getAlgorithms(){
		return algorithms;
	}
}
