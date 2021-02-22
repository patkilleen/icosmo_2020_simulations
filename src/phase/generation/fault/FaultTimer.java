package phase.generation.fault;

import java.util.HashMap;
import java.util.List;

import common.FaultDescription;
import common.Sensor;
import common.Vehicle;
import common.event.FaultEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.RepairEvent;
import common.event.TimerEvent;
import common.event.stream.FaultOutputStream;
import common.event.stream.RepairOutputStream;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.MultiThreadedTimer;

public class FaultTimer extends MultiThreadedTimer<FaultGenerationVehicle>{


	private FaultOutputStream faultOutputStream;
	private RepairOutputStream repairOutputStream;
	//private SortedSensorList faultInvolvedSensors;
	private  HashMap<Vehicle,HashMap<Sensor,Sensor>> faultInvolvedSensors;
	
	public FaultTimer(
			List<FaultGenerationVehicle> vehicles) {
		super(vehicles);
		//null ptr?
		if(vehicles == null){
			throw new NullPointerException("Could not create FaultTimer. null  vehicles");
		}

		initFaultSensorMap(vehicles);	
		
	}
	
	public FaultTimer(
			List<FaultGenerationVehicle> vehicles, long sizeRandomNumberStream) {
		super(vehicles,sizeRandomNumberStream);
		//null ptr?
		if(vehicles == null){
			throw new NullPointerException("Could not create FaultTimer. null  vehicles");
		}

		initFaultSensorMap(vehicles);	
	}

	private void initFaultSensorMap(List<FaultGenerationVehicle> vehicles) {
		faultInvolvedSensors = new HashMap<Vehicle,HashMap<Sensor,Sensor>>();

		//initialize sensor hashmap for each vehicle
		for(FaultGenerationVehicle v: vehicles){

			HashMap<Sensor,Sensor> innerMap = new HashMap<Sensor,Sensor>();
			faultInvolvedSensors.put(v,innerMap);
		}
		
	}

	@Override
	protected void setParitionKeys(List<FaultGenerationVehicle> threadPartitionKeys){
		super.setParitionKeys(threadPartitionKeys);
		initFaultSensorMap(threadPartitionKeys);
	}
	
	/**
	 * Empty constructor to provide flexibility to sub classes
	 */
	protected FaultTimer(){
		super();
		faultInvolvedSensors = new HashMap<Vehicle,HashMap<Sensor,Sensor>>();
	}

	public void initStreams(FaultOutputStream faultOutputStream, RepairOutputStream repairOutputStream){
		//null ptr?
		if(faultOutputStream == null || repairOutputStream == null){
			throw new NullPointerException("Could not init stream of FaultTimer. null streams");
		}
		this.faultOutputStream = faultOutputStream;
		this.repairOutputStream = repairOutputStream;
	}
	
	
	protected void tick(TimerEvent e) throws InterruptedException{
		/*//cosmo requires 2 days for sensor selection, make sure no faults occur
		if(e.getTime()<2){
			return;
		}*/
		super.tick(e);	
	}
	protected void threadTick(TimerEvent e,FaultGenerationVehicle v){
		
		Logger log = LoggerFactory.getInstance();
		//iterate the vehicles and check whether a fault or repair occured
		/*List<Fault> potentialFaults = v.getPotentialFaults();
		//iterate the potential faults
		for(Fault f: potentialFaults){

			//check wethere the fault has occured already
			if(f.isActive()){


				//repair occured today?
				if(this.generateRandomEvent(f.getRepairPValue())){

					//make sure enough time passed before repairing fault
					if(f.isReadyForRepair(e.getTime())){
						//f.deactivate();

						removeActiveFaultInvolvedSensors(v,f.getSensors());

						FaultDescription fd = f.getFaultDescription();
						RepairEvent re = new RepairEvent(v,fd);
						repairOutputStream.write(v,re);
						log.log_debug("repair occured (id: "+fd.getId()+") for vehicle ("+v.getVid()+")");
					}
				}

			}else if(this.generateRandomEvent(f.getOccurencePValue())){//fault occured today and the fault is inavtive?

					List<Sensor> sensors = f.getSensors();
					//are all the sensors uniquely involved in this fault alone (no other fault active with these sensors)?
					if(!sensorsAlreadyInvolvedInActiveFault(v,sensors)){
						f.activate(e.getTime());

						addActiveFaultInvolvedSensors(v,sensors);
						FaultDescription fd = f.getFaultDescription();
						FaultEvent fe = new FaultEvent(v,fd);
						faultOutputStream.write(v,fe);
						log.log_debug("fault (id: "+fd.getId()+") occured ("+fd.getLabel()+") for vehicle ("+v.getVid()+")");
						
						
					}//end if all sensors involved in fault not involved with another active fault

			}//end if fault occured
		}//end iterate vehicle faults
		*/
			
		List<Fault> activeFaults = v.getActiveFaults();
		
		boolean repairedFaults = false;
		
		//iterate the active faults, check if need repair
		for(Fault f: activeFaults){

			
			double randomNum = this.nextRandomNumber(v);
			
				//repair occured today?
				if(this.randomEventOccured(f.getRepairPValue(),randomNum)){

					//make sure enough time passed before repairing fault
					if(f.isReadyForRepair(e.getTime())){
						//f.deactivate();
						v.repairFault(f);
						removeActiveFaultInvolvedSensors(v,f.getSensors());

						FaultDescription fd = f.getFaultDescription();
						RepairEvent re = new RepairEvent(v,fd);
						repairOutputStream.write(v,re);
						log.log_debug("time ("+e.getTime()+") repair occured (id: "+fd.getId()+") for vehicle ("+v.getVid()+")");
					}
					repairedFaults=true;
				}
		}
		
	
		List<Fault> inactiveFaults = v.getInactiveFaults();
		
		boolean faultOccured = false;
		
		//iterate all inactive faults
		for(Fault f: inactiveFaults){

			double randomNum = this.nextRandomNumber(v);
			
			 if(this.randomEventOccured(f.getOccurencePValue(),randomNum)){//fault occured today and the fault is inavtive?

					List<Sensor> sensors = f.getSensors();
					//are all the sensors uniquely involved in this fault alone (no other fault active with these sensors)?
					if(!sensorsAlreadyInvolvedInActiveFault(v,sensors)){
						//f.activate(e.getTime());
						v.activateFault(f, e);
						addActiveFaultInvolvedSensors(v,sensors);
						FaultDescription fd = f.getFaultDescription();
						FaultEvent fe = new FaultEvent(v,fd);
						faultOutputStream.write(v,fe);
						log.log_debug("time ("+e.getTime()+") fault (id: "+fd.getId()+") occured ("+fd.getLabel()+") for vehicle ("+v.getVid()+")");
						faultOccured=true;
						
					}//end if all sensors involved in fault not involved with another active fault
	
				}//end if fault occured
		}//end iterate vehicle faults
		
		//move all newly repaired faults to inactive buffer
		if(repairedFaults){
			v.flushRepairedFaults();
		}
		
		
		if(faultOccured){
			v.flushActivatedFaults();
		}
	}
	/**
	 * Checks wether sensors of a fault are already involved in another active fault.
	 * If atleast one sensor of the provided list is part of an active fault, true is return. Otherwise, if no sensor is found in active fault, false returned.
	 * @param f The fault to check if its sensors are involved. 
	 * @return True when the sensors are already involved with an active fault, and false when no other active fault involves the provided sensors. 
	 */
	public  boolean sensorsAlreadyInvolvedInActiveFault(Vehicle v,List<Sensor> sensors ){



		//synchronized(faultInvolvedSensors){
		if(sensors == null || sensors.size() == 0){
			throw new SimulationException("Failed to check if sensor was already involved in an active fault, Fault is null");
		}

		HashMap<Sensor,Sensor> innerMap = faultInvolvedSensors.get(v);

		if(innerMap == null){
			throw new ConfigurationException("failed to find map assiciated to vehicle.");
		}

		//	List<Sensor> sensors = f.getSensors();

		//make sure no other sensor is affected by a fault
		for(Sensor lookupSensor : sensors){

			//sensor already involved with another fault? 
			//if(faultInvolvedSensors.exists(lookupSensor)){

			boolean sensorInMap = innerMap.containsKey(lookupSensor);

			if(sensorInMap){
				return true;
			}

		}//end iterate all sensor behavior

		return false;
		//}
	}

	/**
	 * Adds sensors to active fault involved sensors.
	 * @param f Fault with the sensors to add to active fault involved sensors.
	 */
	public void addActiveFaultInvolvedSensors(Vehicle v,List<Sensor> sensors){


		if(sensors == null || sensors.size() == 0){
			throw new SimulationException("failed to add fault involved sensors of fault, due to null/empty sensor list");
		}

		HashMap<Sensor,Sensor> innerMap = faultInvolvedSensors.get(v);

		if(innerMap == null){
			throw new ConfigurationException("failed to find map assiciated to vehicle.");
		}

		//List<Sensor> sensors = f.getSensors();

		//record the sensor involved in fault
		for(Sensor s : sensors){

			//by design, a sensor can only be involved with one fault
			if(innerMap.containsKey(s)){
				throw new ConfigurationException("failed to add fault involved sensors of fault, since a sensor is already involved in a fault. Duplicate sensor. Check the FaultDescriptions sensors definition for duplicate sensors.");
			}
			//new active sensor involved in fault
			//faultInvolvedSensors.add(s);
			innerMap.put(s,s);
		}

	}
	/**
	 * Removes the sensor that are currently involved in an active fault.
	 * @param f Fault which its sensors will be removed from active fault involved sensors
	 */
	public  void removeActiveFaultInvolvedSensors(Vehicle v,List<Sensor> sensors){

		if(sensors == null || sensors.size() == 0){
			throw new SimulationException("failed to remove fault involved sensors of fault, due to null fault.");
		}

		HashMap<Sensor,Sensor> innerMap = faultInvolvedSensors.get(v);

		if(innerMap == null){
			throw new ConfigurationException("failed to find map assiciated to vehicle.");
		}
		//List<Sensor> sensors = f.getSensors();

		//faultInvolvedSensors.remove(sensors);
		for(Sensor s : sensors){
			//sensor didn't exist?
			if(innerMap.remove(s) == null){
				throw new SimulationException("failed to remove all fault involved sensors of fault, due to non-existant fault involved sensor");
			}
		}

	}

	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("FaultTimer phase started");
	}

	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("FaultTimer phase ended");
		this.delete();
	}
	

	public FaultOutputStream getFaultOutputStream() {
		return faultOutputStream;
	}

	public void setFaultOutputStream(FaultOutputStream faultOutputStream) {
		this.faultOutputStream = faultOutputStream;
	}

	public RepairOutputStream getRepairOutputStream() {
		return repairOutputStream;
	}

	public void setRepairOutputStream(RepairOutputStream repairOutputStream) {
		this.repairOutputStream = repairOutputStream;
	}


}
