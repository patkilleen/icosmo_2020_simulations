package phase.generation.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.SensorBehavior;
import common.SensorInstance;
import common.SensorInstanceFactory;
import common.SensorMap;
import common.Vehicle;
import common.event.FaultEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.event.ValueNoisePair;
import common.event.stream.FaultInputStream;
import common.event.stream.RawSensorDataInputStream;
import common.event.stream.RepairInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.MultiThreadedTimer;
import phase.generation.cosmo.AnomalyDetectionAlgorithm;
import phase.generation.cosmo.COSMOSensorInstance;
import phase.generation.cosmo.MinMaxPair;
import phase.generation.cosmo.SensorDescription;

public class DataGenerationCOSMOTimer extends MultiThreadedTimer<DataGenerationVehicle> implements SensorInstanceFactory{

	//data gen part
	private FaultInputStream faultInputStream;
	private RepairInputStream repairInputStream;
	private RawSensorDataInputStream sensorDataInputStream;
	//private SensorDataOutputStream sensorDataOutputStream;
	
	
	//cosmo part
	
	//output
	private SensorStatusOutputStream sensorStatusOutputStream;

	//association
	private SensorMap sensorMap;

	private List<Vehicle> vehicles;
	//unique sensors that exist
	private List<Sensor> sensors;

	//number of zvalues to take moving average (30 days in cosmo paper)
	private int zvalueWindowSize;
	
	private List<AnomalyDetectionAlgorithm> cosmoAlgorithms;
	public DataGenerationCOSMOTimer(List<DataGenerationVehicle> vehicles,List<AnomalyDetectionAlgorithm> cosmoAlgorithms, int zvalueWindowSize) {
		super(vehicles);
	
		init(cosmoAlgorithms,zvalueWindowSize);

	}
	
	public DataGenerationCOSMOTimer(List<DataGenerationVehicle> vehicles,List<AnomalyDetectionAlgorithm> cosmoAlgorithms, int zvalueWindowSize, long sizeRandomNumberStream) {
		super(vehicles,sizeRandomNumberStream);
	
		init(cosmoAlgorithms,zvalueWindowSize);

	}
	
	/**
	 * Empty constructor to provide flexibility to sub classes
	 */
	protected  DataGenerationCOSMOTimer(){
		super();
	}
	
	protected void init(List<AnomalyDetectionAlgorithm> cosmoAlgorithms, int zvalueWindowSize) {
	
		if(zvalueWindowSize < 0){
			throw new ConfigurationException("cannot create DataGenerationCOSMOTimer, negative size of moving average zvalue window");
		}
		this.zvalueWindowSize = zvalueWindowSize;
		this.cosmoAlgorithms = cosmoAlgorithms;
		List<Algorithm> sensorMapList = new ArrayList<Algorithm>(cosmoAlgorithms.size());

		for(AnomalyDetectionAlgorithm alg: cosmoAlgorithms){
			sensorMapList.add(alg);
		}
		//initialize the algorithms
		sensorMap = new SensorMap(sensorMapList);
	}
	
	
	public void init(List<Vehicle> vehicles, List<SensorDescription> sensorDescs) {

		if(vehicles == null || vehicles.isEmpty() || sensorDescs == null || sensorDescs.isEmpty()){
			throw new ConfigurationException("cannot initialize anaomaly detection timer due to null/empty arguments.");
		}
		List<Sensor> sensors = new ArrayList<Sensor>(sensorDescs);

		//List<AnomalyDetectionAlgorithm> algs = this.getThreadPartitionKeys();

		sensorMap.init(vehicles, sensors, this);


		//go through the map and add minMaxPair reference to all instances
		for(SensorDescription sd: sensorDescs){
			
			//the min and max values for the sensor class of sd
			MinMaxPair minMaxPair = sd.getMinMaxPair();
			
			for(AnomalyDetectionAlgorithm alg: cosmoAlgorithms){

				List<SensorInstance> instances = sensorMap.getSensorInstances(alg, sd);

				for(SensorInstance i : instances){
					COSMOSensorInstance cosmoInstance = (COSMOSensorInstance)i;
					cosmoInstance.setMinMaxPair(minMaxPair);
				}//end iterate instances
			}//end iterate algorithms
		}//end iterate sensor descriptions

		//link the sensorMap to anomaly detection algorithms
		for(AnomalyDetectionAlgorithm alg: cosmoAlgorithms){
			alg.init(sensorMap);
		}

		this.vehicles = vehicles;
		this.sensors= sensors;
	}

	
	
	public void initStreams(FaultInputStream faultInputStream, RepairInputStream repairInputStream,
			RawSensorDataInputStream sensorDataInputStream,SensorStatusOutputStream sensorStatusOutputStream){
		
		if(faultInputStream == null || repairInputStream == null || sensorDataInputStream == null || sensorStatusOutputStream == null){
			throw new ConfigurationException("Could not init streams of DataGenerationTimer. null streams");
		}
		this.faultInputStream = faultInputStream;
		this.repairInputStream = repairInputStream;
		this.sensorDataInputStream = sensorDataInputStream;
		//this.sensorDataOutputStream = sensorDataOutputStream;
		this.sensorStatusOutputStream = sensorStatusOutputStream;
	}

	protected void threadTick(TimerEvent e,DataGenerationVehicle v){
	
		int time = e.getTime();
		Iterator<FaultEvent> fit = faultInputStream.iterator(v);
		//iterate all new faults of vehicle v to update sensor behavior
		while(fit.hasNext()){
		
			FaultEvent faultEvent = fit.next();
			
			FaultDescription fd = faultEvent.getFaultDescription();
			
			invokeSensorFaultInvolvedBehavior(v,fd);
		}
		
		Iterator<RepairEvent> rit = repairInputStream.iterator(v);
		//iterate all repairs to update sensor behavior
		while(rit.hasNext()){
		
			RepairEvent repairEvent = rit.next();

			FaultDescription fd = repairEvent.getFaultDescription();
			
			repairSensorFaultInvolvedBehavior(v,fd);
		}
		
		//iterate all sensors
		Iterator<Sensor> sensorIt = sensorDataInputStream.keyIterator();
		while(sensorIt.hasNext()){
			Sensor sensorKey = sensorIt.next();
			
			List<ValueNoisePair> readings = sensorDataInputStream.getSensorReadings(sensorKey);
			
			/*
			//all threads share the same constant raw 
			Iterator<DatasetSensorSampleEvent> sit = sensorDataInputStream.iterator();
			//iterate all the sensor input data
			while(sit.hasNext()){
	
				*/
			
			//iterate cosmo algorithms and add sensor reading to their model, for this vehicle
			for(AnomalyDetectionAlgorithm alg : cosmoAlgorithms){
				
				COSMOSensorInstance i = alg.getSensorInstance(v, sensorKey);
				
				for(ValueNoisePair pair : readings){
				
					double sensorValue = pair.getValue();
					double sensorNoise = pair.getNoise();//noise to output if pvalue is achieived
	
					
					//the pvalue for adding noise alternates each day, so every day the 
					//pavue isn't the same, so the stability is manipulated with this
					int pValueIndex = time % SensorBehavior.NUMBER_NOISE_P_VALUES;
					double generateSensorValue = generateSensorData(v,sensorKey,sensorValue,sensorNoise,pValueIndex);
					
								
					alg.processSensorReading(time,i,generateSensorValue);
					//provided subclasses hook to new day 
					//alg.processSensorReading(time, v, sensorKey,generateSensorValue);
				}
			}
			
		}
	}
	
	@Override
	protected void tick(TimerEvent e) throws InterruptedException{
		//serially pre time tick the comso algorithms
		for(AnomalyDetectionAlgorithm alg : cosmoAlgorithms){
			//provided subclasses hook to new day 
			alg.preTimeTick(e);
		}
		
		//this starts threads for each vehicles (see threadTick), generating data for each vehicle
		super.tick(e);
		
		//once data generation done for the data, if the time is right, generate sensor statuses
		
		for(AnomalyDetectionAlgorithm alg : cosmoAlgorithms){
			//provided subclasses hook to new day 
			
			//provide subclasses with hook to handle to post processing of
			//sensor readings(before the sensor statuses are output)
			List<SensorStatusEvent> sensorStatuses = alg.postProcessSensorReading(e);

			//output all sensor statuses (if exist) created by post processing phase
			if(sensorStatuses != null){
				//outupt status of all sensor instance

					for(SensorStatusEvent outputEvent: sensorStatuses){
						sensorStatusOutputStream.write(alg, outputEvent);
					}//end iterate sensors
			}

			//provided subclasses hook to end of day 
			alg.postTimeTick(e);
	
		}
	}
	
	/**
	 * Generates a sensor reading for a specific vehicle.
	 * @param v Vehicle to generate data for.
	 * @param sensorKey Used to lookup the sensor that will be generating the data.
	 * @param sensorValue The sensor value that will have noise added to it.
	 * @param sensorNoise The noise value from another distribution that may be output instead.
	 * @param pValueIndex The index of the noise p value found in the DataGenerationSensor obtained using the sensorKey. 
	 */
	public double generateSensorData(DataGenerationVehicle v, Sensor sensorKey, double sensorValue,double sensorNoise, int pValueIndex){

			if(v == null || sensorKey == null){
				throw new SimulationException("cannot generate sensor data due to null vehicle or sensor key");
			}
			
			
			//find the sensor for this vehicle
			DataGenerationSensor sensor = v.findSensor(sensorKey);
			
			if(sensor == null){
				throw new SimulationException("cannot generate sensor data due sensor ("+sensorKey.toString()+") not found");
			}
				
			SensorBehavior b = sensor.getCurrentBehavior();
			double generatedSensorData;
			
			
			//get the noise p value (alternates daily)
			double [] noisePValues = b.getNoisePValues();
			
			if(pValueIndex < 0 || pValueIndex >= noisePValues.length){
				throw new SimulationException("cannot generate sensor data due to null vehicle");
			}
			
			if(noisePValues == null){
				throw new ConfigurationException("cannot generate sensor data due to null base noise p values");
			}
			
			double noisePValue = noisePValues[pValueIndex];
			
			Noise wn = b.getWhiteNoise();
			
			double randomNum = this.nextRandomNumber(v);
			//are we sampling from the noise distribution
			if(this.randomEventOccured(noisePValue,randomNum)){
				generatedSensorData = b.getNoiseAmpFactor() * sensorNoise;//sample from noise distribution
			}else{
				generatedSensorData = sensorValue * b.getAmpFactor() + wn.generateNoise();//sample from sensor data distribution
			}
					
			return generatedSensorData;
			

	}
	/**
	 * Updates the behavior of sensors involved in a fault for a vehicle.
	 * @param vehicleKey The vehicle lookup key used to find which vehicle instance is affected by fault. 
	 * @param fd The fault describing what effects it will have on some of the vehicle's sensors.
	 */
	public void invokeSensorFaultInvolvedBehavior(DataGenerationVehicle v, FaultDescription fd){
		
		if(v == null || fd == null){
			throw new SimulationException("cannot invoke fault bahvior, due to null vehicle or fautl descrption");
		}
		//for each sensor in the fault, find the equivalent sensor of vehicle
		for(FaultInvolvedSensorBehavior b: fd.getAffectedSensors()){
			
			Sensor sensorKey = b.getAffectedSensor();
			//find the sensor
			DataGenerationSensor sensor = v.findSensor(sensorKey);
			
			sensor.setFaultInvolved();
			
			//change the snesor's faultinvolved behavior depending on fault type
			if(b.isNewType()){
				//replace the behavior
				sensor.setFaultInvolvedBehavior(b);
			}else{//modify the behvior
				SensorBehavior newBehavior = sensor.getNormalBehavior();
				newBehavior = newBehavior.addSensorBehavior(b);
				sensor.setFaultInvolvedBehavior(newBehavior);
			}
		}
	}
	
	/**
	 * Removes the fault behavior of sensors involved in a repair for a vehicle.
	 * @param vehicleKey The vehicle lookup key used to find which vehicle instance is repaired. 
	 * @param fd The fault describing what effects it will have on some of the vehicle's sensors.
	 */
	public void repairSensorFaultInvolvedBehavior(DataGenerationVehicle v, FaultDescription fd){
		
		if(v == null || fd == null){
			throw new SimulationException("failed to repair sensor behavior due to null vehicle or fault description");
		}
		//for each sensor in the fault, find the equivalent sensor of vehicle
		for(Sensor s: fd.getSensors()){
			
			//find the sensor
			DataGenerationSensor dgs = v.findSensor(s);
			
			dgs.setNormal();
		}
	}

	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("DataGenerationTimer phase started");
	}

	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("DataGenerationTimer phase ended");
		
		this.delete();
	}
	
	
	public FaultInputStream getFaultInputStream() {
		return faultInputStream;
	}


	public void setFaultInputStream(FaultInputStream faultInputStream) {
		this.faultInputStream = faultInputStream;
	}


	public RepairInputStream getRepairInputStream() {
		return repairInputStream;
	}


	public void setRepairInputStream(RepairInputStream repairInputStream) {
		this.repairInputStream = repairInputStream;
	}


	public RawSensorDataInputStream getSensorDataInputStream() {
		return sensorDataInputStream;
	}


	public void setSensorDataInputStream(RawSensorDataInputStream sensorDataInputStream) {
		this.sensorDataInputStream = sensorDataInputStream;
	}
	
	public SensorStatusOutputStream getSensorStatusOutputStream() {
		return sensorStatusOutputStream;
	}
	public void setSensorStatusOutputStream(SensorStatusOutputStream sensorStatusOutputStream) {
		this.sensorStatusOutputStream = sensorStatusOutputStream;
	}

	
	public SensorMap getSensorMap() {
		return sensorMap;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}



	public int getZvalueWindowSize() {
		return zvalueWindowSize;
	}

	@Override
	public SensorInstance newInstance(Vehicle v, Sensor s) {
		return new COSMOSensorInstance(s,v,zvalueWindowSize);
	}

	
	
}
