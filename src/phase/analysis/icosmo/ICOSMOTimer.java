package phase.analysis.icosmo;


import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.FaultDescription;
import common.Sensor;
import common.SensorCollection;
import common.SensorInstance;
import common.SensorMap;
import common.Vehicle;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.RepairInputStream;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusOutputStream;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.MultiThreadedTimer;
import phase.generation.cosmo.COSMOSensorInstance;
import phase.generation.history.SensorStatusHistory;


public class ICOSMOTimer extends MultiThreadedTimer<Algorithm> {



	//input to timer
	private RepairInputStream repairInputStream;
	private SensorStatusInputStream sensorStatusInputStream;

	//output
	private TimeStampedSensorStatusOutputStream timeStampedSensorStatusOutputStream;

	//parameters
	private double deviationThreshold;
	private int leftTimeWindowDeviations;
	private boolean loggingSensorChanges;

	//associations
	private SensorMap sensorMap;
	private SensorStatusHistory partialSensorStatusHistory;
	private List<Vehicle> vehicles;
	private List<Sensor> sensors;
	private ICOSMO icosmo;
	
	
	public enum Mode {RECALL,PRECISION};

	/**
	 * Used to indicate if precision is being specified or recall for the 
	 * icosmo information retrieval algorithm
	 */
	private Mode mode;

	public ICOSMOTimer(List<Algorithm> algorithms, List<Vehicle> vehicles, RepairInputStream repairInputStream,
			SensorStatusInputStream sensorStatusInputStream,TimeStampedSensorStatusOutputStream sensorStatusOutputStream,ICOSMO icosmo, Mode mode) {
		super(algorithms);
		this.init(algorithms, vehicles, repairInputStream, sensorStatusInputStream, sensorStatusOutputStream, icosmo, mode);
	}


	/**
	 * empty constructor for subclass flexibility
	 */
	protected ICOSMOTimer(){
		leftTimeWindowDeviations=-1;
	}
	
	public void setLeftTimeWindowDeviations(int leftTimeWindowDeviations){
		if(leftTimeWindowDeviations<0){
			throw new ConfigurationException("can't set left time window. negative value");
		}
		this.leftTimeWindowDeviations = leftTimeWindowDeviations;
	}
	protected void init(List<Algorithm> algorithms, List<Vehicle> vehicles, RepairInputStream repairInputStream,
			SensorStatusInputStream sensorStatusInputStream,TimeStampedSensorStatusOutputStream sensorStatusOutputStream,ICOSMO icosmo, Mode mode) {
		if(algorithms == null || algorithms.isEmpty()){
			throw new ConfigurationException("can't create icosmoTimer, missing algorithms (null or empty)");
		}
		
		if(vehicles == null || vehicles.isEmpty()){
			throw new ConfigurationException("can't create icosmoTimer, missing vehicles (null or empty)");
		}
		
		if(repairInputStream==null ||sensorStatusInputStream == null ||  sensorStatusOutputStream == null){
			throw new ConfigurationException("can't create icosmoTimer, null streams");
		}
		
		if(icosmo == null){
			throw new ConfigurationException("can't create icosmoTimer, null icosmo");
		}
		this.repairInputStream = repairInputStream;
		this.sensorStatusInputStream = sensorStatusInputStream;
		this.timeStampedSensorStatusOutputStream = sensorStatusOutputStream;
		this.vehicles = vehicles;
		this.icosmo = icosmo;
		this.mode = mode;
		leftTimeWindowDeviations = -1;
	}
	public void init(List<Sensor> sensors){
		List<Algorithm> algorithms = this.getThreadPartitionKeys();
		partialSensorStatusHistory = new SensorStatusHistory(algorithms);
		sensorMap = new SensorMap(algorithms);
		sensorMap.init(vehicles, sensors,icosmo);//add this to give  interface function to create icosmosensor isntances
		icosmo.init(sensorMap);
		this.sensors = sensors;
	}

	
	
	public double getDeviationThreshold() {
		return deviationThreshold;
	}


	public int getLeftTimeWindowDeviations() {
		return leftTimeWindowDeviations;
	}

	public boolean isLoggingSensorChanges() {
		return loggingSensorChanges;
	}



	public SensorStatusHistory getPartialSensorStatusHistory() {
		return partialSensorStatusHistory;
	}


	public List<Sensor> getSensors() {
		return sensors;
	}


	public ICOSMO getIcosmo() {
		return icosmo;
	}


	public Mode getMode() {
		return mode;
	}


	public SensorMap getSensorMap(){
		return this.sensorMap;
	}

	public RepairInputStream getRepairInputStream() {
		return repairInputStream;
	}

	public void setRepairInputStream(RepairInputStream repairInputStream) {
		this.repairInputStream = repairInputStream;
	}

	public SensorStatusInputStream getSensorStatusInputStream() {
		return sensorStatusInputStream;
	}

	public void setSensorStatusInputStream(SensorStatusInputStream sensorStatusInputStream) {
		this.sensorStatusInputStream = sensorStatusInputStream;
	}

	public void setDeviationThreshold(double t){
		this.deviationThreshold = t;
	}
	

	public void setLoggingSensorChanges(boolean loggingSensorChanges) {
		this.loggingSensorChanges = loggingSensorChanges;
	}

	
	public TimeStampedSensorStatusOutputStream getTimeStampedSensorStatusOutputStream() {
		return timeStampedSensorStatusOutputStream;
	}


	public void setAnalysisSensorStatusOutputStream(TimeStampedSensorStatusOutputStream analysisSensorStatusOutputStream) {
		this.timeStampedSensorStatusOutputStream = analysisSensorStatusOutputStream;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}


	/**
	 * resets all sensors instances to default values, and reselects selected sensors.
	 * The selected sensors are obtained, assuming the algorithms of this timer are of the class "SensorCollection",
	 * by casting the algorithm instances and fetching the selected sensors for that algorithm
	 * @param defaultSelectedSensors the sensor classes that should be selected 
	 */
	public void resetSensorMap() {
		
		//unselect all sensors and set atrributes to default
		
		List<Algorithm> algorithms = this.getThreadPartitionKeys();
		for(Algorithm alg : algorithms){
			
			//
			for(Sensor s : sensors){
				
				List<SensorInstance> instances = sensorMap.getSensorInstances(alg, s);
				
				for(SensorInstance i : instances){
					
					ICOSMOSensorInstance i2  = (ICOSMOSensorInstance) i;
					icosmo.resetSensorInstanceToDefault(i2);
				}
				
			}
			
		}//end reset instance to default
		
		//select the default selected sensors	
		for(Algorithm alg : algorithms){
			
			if(!(alg instanceof SensorCollection)){
				throw new ConfigurationException("cannot reset sensor map since the partition key algorithms aren't of the class 'SensorCollection'."
						+ " Make sure when this timer is built, the right algorithm instances are provided as partition keys");
			}
			
			//it is assumed the algorithms provided as partition keys are "AnomalyDetectionAlgorithm"
			SensorCollection algTmp = (SensorCollection) alg;
			
			List<Sensor> defaultSelectedSensors = algTmp.getSelectedSensors();
			
			for(Sensor selectedSensorClass : defaultSelectedSensors){
				List<SensorInstance> instances = sensorMap.getSensorInstances(alg, selectedSensorClass);
			
				for(SensorInstance i : instances){
					ICOSMOSensorInstance i2  = (ICOSMOSensorInstance) i;
					i2.setCosmoSensor(true);
						
				}
			}
		}//end select cosmo sensors
		
	}

	protected void threadTick(TimerEvent timerEvent,Algorithm alg){

		Iterator<SensorStatusEvent> zit = sensorStatusInputStream.iterator(alg);
		//iterate all sensor statuses and record them in history for later use
		while(zit.hasNext()){

			SensorStatusEvent ssEvent = zit.next();
			Vehicle v = ssEvent.getVehicle();
			Sensor s = ssEvent.getSensor();
			double zvalue = ssEvent.getZvalue();
			
			//so logic here, no need for if or anything, just get cosmo flag, and compute zscore from the zvalue queue that is
			//conditionally emptied by handle repair when sensor selected
			
			//updates the zscore of icosmo sensor isntance, returning a time-stamped sensor status event with
			//the new (or original) zscore and zvalue
			ICOSMOSensorInstance sensorInstance = (ICOSMOSensorInstance) sensorMap.getSensorInstance(alg, v, s);
			
			sensorInstance.addZvalue(zvalue);
			
			double newZscore = sensorInstance.computeZScore();
			
			TimeStampedSensorStatusEvent timeStampedEvent = new TimeStampedSensorStatusEvent(v,s,alg,newZscore,sensorInstance.isCosmoSensor(),zvalue,timerEvent);
			

			partialSensorStatusHistory.recordElement(alg,timerEvent, timeStampedEvent);
			
			
			timeStampedSensorStatusOutputStream.write(alg, timeStampedEvent);
			
			

		}//end iterate sensor status events

		//check for repairs, adjsut sensor ranking, re-organize sensors selected
		checkForRepairs(alg, timerEvent);
	}

	
	protected void phaseStarted(PhaseBeginEvent e) throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("ICOSMO Tiemr phase started");
	}
	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("ICOSMO Tiemr phase ended");
	}
	
	/**
	 * Processes the input repair stream, and adjust the sensor instances performacne 
	 * at detection deviations, at times removing/adding sensor selected.
	 * @param alg
	 * @param timerEvent
	 */
	public void checkForRepairs(Algorithm alg,TimerEvent timerEvent){

		/*
		 * here could spawn a thread for each vehicle, and at end of each thread execution
		 * the thread returns sensor classes to check for candicay and staleness
		 * then in this thread (alg threadd), we rebalance all the selected sensors
		 * 
		 * 
		 * spawn vehicle thread
		 * 
		 * 		sensors to check for selection changes = null
		 * 		for each repair
		 * 			sensors = add (signalRepairOccured)
		 * 
		 *join
		 *
		 * for each potention sensor class change
		 * 		check is stale
		 * 		check is cnadidate		
		 */
		//iterate all vehicles
		for(Vehicle v: vehicles){
			//handling repairs serially in each thread (no extra threads per vehicle)

			Iterator<RepairEvent> rit = repairInputStream.iterator(v);

			//iterate all repairs of vehicle v and record them in fault history for later
			//retrieval
			while(rit.hasNext()){

				RepairEvent repairEvent = rit.next();

				FaultDescription fd = repairEvent.getFaultDescription();

				//adjust the ranking of sensor (and possibly remove/add select sensors)
				signalRepairOccured(alg,timerEvent,v,fd);

			
			}//end iterate repairs
		}//end iterate vehicles


	}


	/**
	 * Handles the fault occurences, adjusting the icosmo sensor selection attributes, and removing/adding 
	 * stale and candidate sensors to/from cosmo's selected sensors.
	 * @param alg
	 * @param icosmoPartialZHist
	 * @param repairTimeEvent
	 * @param v
	 * @param fd
	 * @param mode
	 * @param sensorDeviationThreshold
	 */
	protected void signalRepairOccured(Algorithm alg, TimerEvent repairTimeEvent, Vehicle v, FaultDescription fd){


		//flag indicating whether a deviation was linked to fault that occured
		boolean faultDetected = false;
		
		List<Sensor> estimatedFaultInvolvedSensors = simulateIRA(fd);

		faultDetected = atleastOneDeviationOccured(alg, repairTimeEvent, v,estimatedFaultInvolvedSensors);

		//adjust the rank of fault invovled sensors
		for(Sensor s: estimatedFaultInvolvedSensors){

			handleFaultInvolvedSensor(alg,v,s,repairTimeEvent,faultDetected);
			
		}
	}


	/**
	 * called when a repair occurs. Adjusts the ranking of fault invovled sensors, removing or adding the fault-invovled
	 * sensor to/from cosmo sensors, in the case that the fault-invovled sensor's contribution or potential contribution
	 * has exceeded a threshold 
	 * @param alg algorithm 
	 * @param v vehicle of fault-involved sensor
	 * @param s sensor class of fault involved sensor
	 * @param repairTimeEvent time of repair 
	 * @param faultDetected flag indicating that a fault was detected by the COSMO approaching (true when fault was detected, false otherwise)
	 */
	protected void handleFaultInvolvedSensor(Algorithm alg, Vehicle v, Sensor s, TimerEvent repairTimeEvent,
			boolean faultDetected) {
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) sensorMap.getSensorInstance(alg, v, s);
		boolean sensorDeviationOccured = false;

		//we check here as after adjusting ranking it might be change
		boolean wasCOSMOSensor=faultInvolvedSensor.isCosmoSensor();
		
		//ignore checking for deviations of non-cosmo sensors, zscore aren't recorded in real system for these sensors
		//so ignoring them in the simulation is realistic
		if(faultInvolvedSensor.isCosmoSensor()){

			//sensor deviated in the past (past lookup time limit is cap'ped by leftTimeWindowDeviation
			sensorDeviationOccured = deviationExists(alg,repairTimeEvent,v,faultInvolvedSensor);


		}//end if comso snesor

		/*
		 * make sure this is thread safe (no 2 threads affecting same instace)
		 */
		boolean sensorSelectionChange = icosmo.adjustSensorRanking(alg,faultDetected, faultInvolvedSensor,sensorDeviationOccured);
		
		
		if(sensorSelectionChange && wasCOSMOSensor && faultInvolvedSensor.isCosmoSensor()) {
			Logger log = LoggerFactory.getInstance();
			log.log_error("sensor instance was a cosmo sensor, flagged as changed, but is still cosmo sensor: "+s.toString());
		}else if (sensorSelectionChange && ! wasCOSMOSensor && !faultInvolvedSensor.isCosmoSensor()) {
			Logger log = LoggerFactory.getInstance();
			log.log_error("sensor instance wasn't cosmo sensor, flagged as changed, but is isn't cosmo sensor: "+s.toString());
		}
		/*
		 * adjust sensor ranking could avoid checking all staleness and cnadidacy of the other vehicles, 
		 * it just returns a flag indicating whether its owrth checking for stale sensos
		 */
		//was a stale sensor removed from sensors selected, or a candidate sensor added?
		if(sensorSelectionChange){
			
			
			/*
			 * 
			 */
			//clear the zvalue moving average window of all instance of sensor class selected/unselected
			List<SensorInstance> instances = sensorMap.getSensorInstances(alg, faultInvolvedSensor);
			
			for(SensorInstance i : instances){
				ICOSMOSensorInstance ic = (ICOSMOSensorInstance)i;
				ic.clearZValues();
			}
			
			
			if(isLoggingSensorChanges()) {
				//log the change
				Logger log = LoggerFactory.getInstance();
				if(wasCOSMOSensor){
					
					log.log_info("removed stale sensor: (alg: "+alg.getName()+", vehicle: "+v.getVid()+", sensor: ("+s.getPgn()+","+s.getSpn()+"), repair time"+ repairTimeEvent.getTime());
				}else {
					log.log_info("added candidate sensor: (alg: "+alg.getName()+", vehicle: "+v.getVid()+", sensor: ("+s.getPgn()+","+s.getSpn()+"), repair time"+ repairTimeEvent.getTime());
				}
			}//end log sensor changes
			
		}
		
	}


	/**
	 * estimates fault involved sensors of a fault by simulating information retrieval algorithm.
	 * @param fd Fault description with actual fault-involved sensors and non-fault-involved sensors.
	 * @return Estimated list of sensors involved in fault.
	 */
	protected List<Sensor> simulateIRA(FaultDescription fd) {
		
		if(fd == null){
			throw new ConfigurationException("cannot simulate information retrieval algorithm on null fault");
		}
		//p
		List<Sensor> faultInvovledSensors = fd.getSensors();
		//pNOt
		List<Sensor> nonFaultInvovledSensors = fd.getNonFaultInvolvedSensors();

				
		//estimate the fault involved sensors
		List<Sensor> estimatedFaultInvolvedSensors = null;

		if(mode == Mode.RECALL){
			estimatedFaultInvolvedSensors = icosmo.iraRecall(faultInvovledSensors, nonFaultInvovledSensors);
		}else{//precision
			estimatedFaultInvolvedSensors = icosmo.iraPrecision(faultInvovledSensors, nonFaultInvovledSensors);
		}
		
		return estimatedFaultInvolvedSensors;
	}


	/**
	 * Returns true if a deviation occured for any of the sensor instances in the past (bounded).
	 * @param alg 
	 * @param repairTimeEvent Time repair occured
	 * @param v Vehicle to check if its sensor instance deviated
	 * @param sensorInstances The sensor classes of the  instances to check if they deviated
	 * @return True when atleast one sensor deviated within period, false otherwise.
	 */
	protected boolean atleastOneDeviationOccured(Algorithm alg, TimerEvent repairTimeEvent, Vehicle v, List<Sensor> sensorInstances) {
		
		boolean res = false;
		//determine if the fault was detected by one of the fault-involved cosmo sensors
		//that is, did one of the cosmo sensors' zscore deviatie due to the fault?
		for(Sensor s: sensorInstances){
			ICOSMOSensorInstance aSensor = (ICOSMOSensorInstance) sensorMap.getSensorInstance(alg, v, s);
			
			//deveation aren't monitored for non-cosmo sensors. only considered selected sensors
			if(aSensor.isCosmoSensor()){

				//sensor deviated in the past (past lookup time limit is cap'ped by leftTimeWindowDeviation
				if(deviationExists(alg,repairTimeEvent,v,s)){
					res = true;
					break;
				}

			}//end if comso snesor
		}//end iterate estimated fault-involved sensors

		return res;
	}


	/**
	 * Returns true if during a time period, a sensor instance's zscore deviated, and false if it never deviated during the period.
	 * The time period is searched backward, starting from the future (the end time) to the past (leftTimeWindowDeviation attribute)
	 * @param alg algorithm of sensor instance
	 * @param endTime starting time (in future) from which to look for deviations progressively in pasat
	 * @param v vehicle of sensor instance
	 * @param sensor sensor class of sensor instance
	 * @return true when atleast one deviation exists, false otherwise (no deviations) 
	 */
	protected boolean deviationExists(Algorithm alg,TimerEvent endTime, Vehicle v, Sensor sensor) {
		if(leftTimeWindowDeviations == -1){
			throw new SimulationException("cannot check if deviation exists, forgot set set left time window value");
		}
		
		boolean deviationFlag = false;
		int time = endTime.getTime();

		int targetEndTime = time - leftTimeWindowDeviations;
		//go through the sensor status history, from when a repair occured to past
		for(int t = time; ((t >= targetEndTime) && (t>=0)); t--){
			TimerEvent tEvent = new TimerEvent(t);
			
			//since repairs will be generally rare, linearily searching the list of sensors status should be fine
			//cause this function only called when repairs occur
			Iterator<SensorStatusEvent> events = partialSensorStatusHistory.elementIterator(alg, tEvent);
			
			SensorStatusEvent targetEvent = null;
			//search for sensor status of desired sensor intance
			while(events.hasNext()){
				SensorStatusEvent e = events.next();
				
				//found the sensor status for time : t?
				if(e.getSensor().equals(sensor) && e.getVehicle().equals(v)){
					targetEvent = e;
					break;
				}
				
			}
			
			if(targetEvent == null){
				throw new SimulationException("could not check if deviation exists for a sensor, gap in time (no sensor record) "
						+ "for time: "+t+" in icosmo partially sensor status history. make sure sensor status outputing each day for each sensor instance");
			}
			
			//has it deviated?
			if(targetEvent.isDeviating(deviationThreshold)){
				deviationFlag = true;
				break;
			}
			

		}//end iterate through the past to find deviation of sensor
		return deviationFlag;
	}

	public void resetPartialHistory() {
		partialSensorStatusHistory.clear();
		
	}


	public void resetSensorSelectionCounters() {
		icosmo.resetSensorSelectionChangesMap();
		
	}
}
