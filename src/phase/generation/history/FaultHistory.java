package phase.generation.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import common.FaultDescription;
import common.Sensor;
import common.Util;
import common.Vehicle;
import common.event.TimerEvent;
import common.exception.ConfigurationException;

public class FaultHistory extends History<Vehicle,FaultDescription>  implements Serializable{

	public static final int DEFAULT_FAULT_LIST_SIZE = 64;
	public static final int DEFAULT_SENSOR_HASHMAP_CAPACITY = 128;
	private HashMap<Vehicle, HashMap<Sensor,List<AnalysisFault>>> faultMap;
	
	public FaultHistory(List<Vehicle> vehicles) {
		super(vehicles);
		if( vehicles == null){
			throw new IllegalArgumentException("Could not create fault history, null vehicles");
		}
		
		faultMap = new HashMap<Vehicle, HashMap<Sensor,List<AnalysisFault>>>(vehicles.size());
		
		//create a innerHashmap and empty fault list for each vehicle
		for(Vehicle v : vehicles){
			HashMap<Sensor,List<AnalysisFault>>	innerMap = new HashMap<Sensor,List<AnalysisFault>>(DEFAULT_SENSOR_HASHMAP_CAPACITY);
			faultMap.put(v,innerMap);

		}
	}
	
	/**
	 * Repairs all faults that haven't been repaired yet.
	 * @param time The time of the repair (non-negative). 
	 */
	public void repairAllUnrepairedFaults(int time){
		
		if(time < 0){
			throw new ConfigurationException("cannot repair all unrepaired faults in FaultHistory, negative repair time.");
		}

		List<Vehicle> vehicles = this.getPartitionKeys();
		
		//iterate all vehicles (as keys)
		for(Vehicle v : vehicles){
		
			HashMap<Sensor,List<AnalysisFault>> innerMap = null;
			

			innerMap = faultMap.get(v);
			
			if(innerMap == null){
				continue; //no faults for this vehicle
			}
			
			Set<Sensor> sensorKeySet = innerMap.keySet();
			
			//iterate all sensors to find their fault list
			Iterator<Sensor> it = sensorKeySet.iterator();
			
			while(it.hasNext()){
				Sensor s = it.next();
				List<AnalysisFault> faults = innerMap.get(s);
				
				if(faults == null || faults.isEmpty()){
					continue;//no faults for this sensor
				}
				
				
				//find fault that aren't repaired
				for(AnalysisFault f : faults){
					
					//not repaired yet?
					if(!f.hasBeenRepaired()){
						//repair it
						f.setRepairedTime(time);
					}
				}
			}//end iterate sensors
		}//end iterate vehicles
	}
	@Override
	public void recordElement(Vehicle v, TimerEvent timerEvent, FaultDescription fd){
		//record the fault descriptions in history
		super.recordElement(v, timerEvent, fd);
		
		//record for fault date lookup
		int time = timerEvent.getTime();
		
		//null ptr?
		if(v  == null || fd == null ){
			throw new ConfigurationException("failed to record fault, due to null pointer.");
		}

		List<AnalysisFault> faults = null;
		HashMap<Sensor,List<AnalysisFault>> innerMap = null;
		

		innerMap = faultMap.get(v);
	
		List<Sensor> sensors = fd.getSensors();
		
		//iterate the sensors and add fault to their fault history
		for(Sensor s : sensors){
			
			//get fault history
			faults = innerMap.get(s);
			
			//no faults yet?
			if(faults == null){
				faults = new ArrayList<AnalysisFault>(DEFAULT_FAULT_LIST_SIZE);
				innerMap.put(s,faults);
			}
			
			AnalysisFault af = new AnalysisFault(time,fd);
			faults.add(af);
			
		}
	}
	
	public void recordRepair(Vehicle v, TimerEvent timerEvent, FaultDescription fd){
		int time = timerEvent.getTime();
		//null ptr?
		if(v  == null || fd == null ){
			throw new ConfigurationException("failed to record fault, due to null pointer.");
		}
		
		HashMap<Sensor,List<AnalysisFault>> innerMap = faultMap.get(v);

		List<AnalysisFault> faults = null;
		
		//first time this vehicle has a fault?
		if(innerMap == null){
			throw new ConfigurationException("failed to record fault repair time. Cannot repair fault that didn't occur.");
			
		}
		
		List<Sensor> sensors = fd.getSensors();
		
		//iterate the sensors and update the repair date in history
		for(Sensor s : sensors){
			
			//get fault history
			faults = innerMap.get(s);
			
			//no faults yet?
			if(faults == null){
				throw new ConfigurationException("failed to record fault repair time. internal configuration error.");
			}
			
			//iterate the faults to find the fault being repaired
			for(AnalysisFault af : faults){
				
				//the same fault can occur multiple times, so skip faults that already were repaired
				if(af.hasBeenRepaired()){
					continue;
				}
				FaultDescription fd2 = af.getFaultDescrition();
				
				//found it?
				if(fd.getId() == fd2.getId()){
					af.setRepairedTime(time);
					break;
				}
				
			}//end iterate analysis faults
			
		}//end iterate sensors involved in repair
	}
	
	/**
	 * Returns true if the given sensor for some vehicle was fault involved at a specific time.
	 * @param v The vehicle with the sensor.
	 * @param s The sensor to check if involved in fault. 
	 * @param time The time the window is based on.
	 * @return True when sensor was involved with a fault at a specific time, false otherwise.
	 */
	public boolean isSensorFaultInvolved(Vehicle v, Sensor s, int time){
		return isSensorFaultInvolved(v,s,time,0,0);
		
	}
	/**
	 * Returns true if the given sensor for some vehicle was fault involved at the given time window.
	 * @param v The vehicle with the sensor.
	 * @param s The sensor to check if involved in fault. 
 	* @param time The time the window is based on.
	 * @param leftTimeWindow The amount of time in the past the fault can occur in.
	 * @param rightTimeWindow The amount of time in the future the fault can occur in.
	 * @return True when sensor was involved with a fault at a given time window, false otherwise.
	 */
	public boolean isSensorFaultInvolved(Vehicle v, Sensor s, int time, int leftTimeWindow, int rightTimeWindow){
		
		//null ptr?
		if(v  == null || s == null){
			throw new ConfigurationException("failed to check if sensor involved in fault, due to null sensor or vehicle.");
		}
		
		HashMap<Sensor,List<AnalysisFault>> innerMap = null;
		
		//lock the access to fault map
		//synchronized(this){ no neeed for this, this function is only ever called when the fault history is used as read only
			innerMap = faultMap.get(v);
		//}
		
		if(innerMap == null){
			throw new ConfigurationException("cannot check if sensor is involved in fault since vehicle("+v+") doesn't exist.");
		}

		
		//find the faults of given sensor
		List<AnalysisFault> faults = innerMap.get(s);
		
		//no involvement with faults for this sensor?
		if((faults == null) || (faults.size() == 0)){
			return false;
		}
		
		boolean res = false;
		//check all faults to see if sensor was involved in one of them at given time window
		for(AnalysisFault f: faults){
			res = res || f.existsInTimeWindow(time, leftTimeWindow, rightTimeWindow);
			if(res){
				break;
			}
		}
		
		return res;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((faultMap == null) ? 0 : faultMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(!super.equals(obj)){
			return false;
		}
		
		if(!(obj instanceof FaultHistory)){
			return false;
		}
		FaultHistory other = (FaultHistory)obj;
		
		//now compare the other internal map
		if(!History.isMapEquals(this.faultMap,other.faultMap,new FaultAnalysisComparator())){
			return false;
		}
		
		return true;
	
	}

	@Override
	public boolean valueEquals(FaultDescription fd1, FaultDescription fd2) {
		
		
		
		if(fd1 != null){
			return fd1.equalsAllSensorBehavior(fd2);	
		}
		if(fd2 != null){
			return fd2.equalsAllSensorBehavior(fd1);	
		}
		
		return true;
		
		
	}
	
	private static class FaultAnalysisComparator implements HistoryValueComparator<AnalysisFault>{

		@Override
		public boolean valueEquals(AnalysisFault v1, AnalysisFault v2) {
			if(v1 != null){
				return v1.equals(v2);	
			}
			if(v2 != null){
				return v2.equals(v1);	
			}
			
			return true;
		}
		
	}
}
