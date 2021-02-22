package phase.generation.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;
import common.event.SensorStatusEvent;

public class SensorStatusHistory extends History<Algorithm,SensorStatusEvent> implements Serializable{
	
	public static final int UNIQUE_SENSOR_RESULT_DEFAULT_SIZE = 1024*64;

	public static final int UNIQUE_VEHICLE_RESULT_DEFAULT_SIZE = 32;

	public SensorStatusHistory(List<Algorithm> algorithms) {
		super(algorithms);
	}


	/**
	 * Returns a sorted unique zscore list of all the zscores found in the history. 
	 * @return The sorted unique zscore list.
	 */
	public List<Double> getUniqueZScores(){

		
		List<SensorStatusEvent> tmpResult = new ArrayList<SensorStatusEvent>(History.DEFAULT_UNIQUE_ELEM_LIST_SIZE);
		
		List<Algorithm> algorithms = this.getPartitionKeys();

		//iterate all algorithm keys
		for(Algorithm alg: algorithms ){
			
			//fetch all the zscoreevents of the algorith
			List<SensorStatusEvent> zscores = this.getElements(alg);
			tmpResult.addAll(zscores);

		}//end iterate the inner maps for each algorithm

		List<Double> result = new ArrayList<Double>(tmpResult.size());
		//add all zscore raw values to result list
		for(SensorStatusEvent zevent : tmpResult){
			double rawValue = zevent.getZscore();
			result.add(rawValue);
		}//end iterate zscore values
		
		//remove duplicate values
		result = result.stream().distinct().collect(Collectors.toList());
		//sort 
		Collections.sort(result);

		return result;
	}

	/**
	 * Returns list of unique vehicles found in the history.
	 * @return List of unique vehicles.
	 */
	public List<Vehicle> getUniqueVehicles(){
		List<Vehicle> res = new ArrayList<Vehicle>(UNIQUE_VEHICLE_RESULT_DEFAULT_SIZE);

		List<Algorithm> algs = this.getPartitionKeys();
		
		//iterate algorithm (keys) to go fetch all events for each one
		for(Algorithm alg: algs){
			
			List<SensorStatusEvent>  events = this.getElements(alg);

			//iterate events to extract relevant sensor
			for(SensorStatusEvent e : events){
				Vehicle v = e.getVehicle();
				
				//only add vehicle if not previously found
				if(!res.contains(v)){
					res.add(v);
				}
				
			}
			
		}
		
		//now remove duplicates
		return res;
		
	}

	/**
	 * Returns list of unique sensors found in the history.
	 * @return List of unique sensors.
	 */
	public List<Sensor> getUniqueSensors(){
		List<Sensor> res = new ArrayList<Sensor>(UNIQUE_SENSOR_RESULT_DEFAULT_SIZE);

		List<Algorithm> algs = this.getPartitionKeys();
		
		//iterate algorithm (keys) to go fetch all events for each one
		for(Algorithm alg: algs){
			
			List<SensorStatusEvent>  events = this.getElements(alg);

			//iterate events to extract relevant sensor
			for(SensorStatusEvent e : events){
				Sensor s = e.getSensor();
				if(!res.contains(s)){
					res.add(s);
				}
			}
			
		}
		
		//now remove duplicates
		return res;
	}
	

}