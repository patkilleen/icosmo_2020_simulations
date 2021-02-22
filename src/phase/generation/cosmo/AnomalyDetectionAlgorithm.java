package phase.generation.cosmo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.Sensor;
import common.SensorCollection;
import common.SensorInstance;
import common.SensorMap;
import common.Vehicle;
import common.event.SensorDataEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import common.exception.SimulationException;

public class AnomalyDetectionAlgorithm extends Algorithm implements SensorCollection, Serializable{

	//transient: don't serialize this in history, it won't be needed (only required when creating history)
	private SensorMap sensorMap;
	
	
	/**
	 * 
	 * @param id id of the algorithm
	 */
	public AnomalyDetectionAlgorithm(int id,String name) {
		super(id,name);
	}

	/**
	 * empty constructor for sub class flexibility
	 */
	protected AnomalyDetectionAlgorithm(){
		
	}
	/**
	 * REturns sensors found in the sensor map.
	 * @return sensors
	 */
	public List<Sensor> getSensors(){
		return sensorMap.getSensors();
	}
	
	/**
	 * returns the vehicles found in the sensor map
	 * @return vehicles
	 */
	public List<Vehicle> getVehicles(){
		return sensorMap.getVehicles();
	}
	
	/**
	 * Initializes the algorithm. 
	 * @param sensorMap Sensor map that is expected to host COSMOSensorInstances.
	 */
	public void init(SensorMap sensorMap){
		if(sensorMap == null){
			throw new ConfigurationException("failed to create anomaly detection algorithm due to null sensor map");
		}
		this.sensorMap = sensorMap;
	}
	
	/**
	 * Fetches sensors instance from map, from the map partition of this algorithm.
	 * @param v The vehicle the instance is found on
	 * @param s The sensor class of the instance
	 * @return The sensor instance
	 */
	final public COSMOSensorInstance getSensorInstance( Vehicle v, Sensor s) {
		return (COSMOSensorInstance)sensorMap.getSensorInstance(this, v, s);
	}

	/**
	 * Fetches the sensor instances of a sensor class from the sensor map, from the map partition of
	 * this algorithm. That is, each vehicle's sensor instance
	 * of the provided class. 
	 * @param sensorClass The sensor class of the instances to return. 
	 * @return Sensor instances
	 */
	final public List<COSMOSensorInstance> getSensorInstances(Sensor sensorClass) {
		
		List<SensorInstance> instances = sensorMap.getSensorInstances(this, sensorClass);
		List<COSMOSensorInstance> res = new ArrayList<COSMOSensorInstance>(instances.size());
		
		for(SensorInstance i : instances){
			
			//not a cosmo sensor?
			if(!(i instanceof COSMOSensorInstance)){
				throw new SimulationException("Could not fetch COSMO sensor instances from sensor map, since SensorMap didn't have the right instances in it. Expected of class (COSMOSensorInstance)");
			}
			COSMOSensorInstance cosmoInstance = (COSMOSensorInstance)i;
			res.add(cosmoInstance);
		}
		return res;
	}
	
	
	/**
	 * Calls the function {@code processSensorReading} for each sensor Data event
	 * 
	 * @param timerEvent the event indicating the current time of this reading
	 * @param v Vehicle of sensor reading
	 * @param it Iterator to all the sensor data readings for vehicle v
	 */
	public void processSensorReadings(TimerEvent timerEvent,Vehicle v, Iterator<SensorDataEvent> it){
		
		if(timerEvent == null || v == null || it == null){
			throw new ConfigurationException("cannot process sensor reading, due to null arguments.");
		}
	
		int time = timerEvent.getTime();
		
		//process all readings for the vehicle, even if sensor not selected, since will be analyzing all zscores
		//regardless of sensors selected
		while(it.hasNext()){
			
			
			SensorDataEvent e = it.next();
			processSensorReading(time,v, e.getSensor(),e.getValue());
						
		}
		
	}

	/**
	 * Hook to be implemented by subclasses to process sensor readings.
	 * 
	 * Creates histograms from the sensor readings, incrementing the proper buckets frequency
	 * depending on the reading.
	 * 
	 * @param time the time value of reading
	 * @param v the vehicle 
	 * @param s the sensor class
	 * @param reading sensor sample
	 */
	public void processSensorReading(int time, Vehicle v, Sensor s, double reading) {
		//fetch instance of sensor
				COSMOSensorInstance i = this.getSensorInstance(v, s);
					
				processSensorReading(time,i,reading);
	}
	
	public void processSensorReading(int time, COSMOSensorInstance i, double reading) {
		
	}
	
	

	/**
	 * Hook for subclasses to override. Called before time tick logic occurs (before the day starts)
	 * @param e Timer event
	 */
	public void preTimeTick(TimerEvent e){
		
	}
	
	/**
	 * Hook for subclasses to override. Called after time tick logic occurs (before the next day starts, after the time tick processing)
	 * @param e Timer event
	 */
	public void postTimeTick(TimerEvent e){
		
	}
	
	
	
	/**
	 * Hook for subclasses to override. Called after the sensor readings were processed but before
	 * the sensor statuses are output to the sensorStatusOutputStream.
	 * @param e Timer event
	 * @return The sensor statuses created from the post processing of sensor readings.
	 */
	public  List<SensorStatusEvent> postProcessSensorReading(TimerEvent e){
		return null;
	}
	
	/**
	 * finds all the sensor classes that are selected.
	 * @return list of sensors classes that are selected (cosmo sensors)
	 */
	public List<Sensor> getSelectedSensors(){
		
		List<Sensor> sensors =  this.getSensors();
		List<Sensor> result = new ArrayList<Sensor>(sensors.size());
		
		//iterate all sensor classes
		for(Sensor sensorClass :sensors){
			
			//get all instances of the class
			List<COSMOSensorInstance> instances = this.getSensorInstances(sensorClass);
			
			//only check the first instance, since all instances of a sensor class
			//are either select or all not selected
			COSMOSensorInstance i = instances.get(0);
			
			
			if(i.isCOSMOSensor()){
				result.add(sensorClass);
			}
		}//end iterate all classes
		
		return result;
	}
	

	
}
