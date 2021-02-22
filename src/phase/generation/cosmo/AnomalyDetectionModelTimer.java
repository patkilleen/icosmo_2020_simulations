package phase.generation.cosmo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.Sensor;
import common.SensorInstance;
import common.SensorInstanceFactory;
import common.SensorMap;
import common.Vehicle;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.SensorDataEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.FaultOutputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.SensorDataInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.MultiThreadedTimer;

public class AnomalyDetectionModelTimer extends MultiThreadedTimer<AnomalyDetectionAlgorithm> implements SensorInstanceFactory{

	//output
	private SensorStatusOutputStream sensorStatusOutputStream;

	//input
	private SensorDataInputStream sensorDataInputStream;


	//association
	private SensorMap sensorMap;

	private List<Vehicle> vehicles;
	//unique sensors that exist
	private List<Sensor> sensors;

	//number of zvalues to take moving average (30 days in cosmo paper)
	private int zvalueWindowSize;
	public AnomalyDetectionModelTimer(List<AnomalyDetectionAlgorithm> algs, int zvalueWindowSize) {
		super(algs);
		init(algs,zvalueWindowSize);

	}

	protected void init(List<AnomalyDetectionAlgorithm> algs, int zvalueWindowSize) {
		if(zvalueWindowSize < 0){
			throw new ConfigurationException("cannot create AnomalyDetectionModelTimer, negative size of moving average zvalue window");
		}
		this.zvalueWindowSize = zvalueWindowSize;
		
		List<Algorithm> sensorMapList = new ArrayList<Algorithm>(algs.size());

		for(AnomalyDetectionAlgorithm alg: algs){
			sensorMapList.add(alg);
		}
		//initialize the algorithms
		sensorMap = new SensorMap(sensorMapList);
	}
	
	/**
	 * empty constructor for subclass flexibility
	 */
	protected AnomalyDetectionModelTimer(){
	
	}
	public void initStreams(SensorStatusOutputStream sensorStatusOutputStream, SensorDataInputStream sensorDataInputStream){
		//null ptr?
		if(sensorStatusOutputStream == null || sensorDataInputStream == null){
			throw new ConfigurationException("Could not init stream of AnomalyDetectionModelTimer. null streams");
		}
		this.sensorStatusOutputStream = sensorStatusOutputStream;
		this.sensorDataInputStream = sensorDataInputStream;
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

	public void init(List<Vehicle> vehicles, List<SensorDescription> sensorDescs) {

		if(vehicles == null || vehicles.isEmpty() || sensorDescs == null || sensorDescs.isEmpty()){
			throw new ConfigurationException("cannot initialize anaomaly detection timer due to null/empty arguments.");
		}
		List<Sensor> sensors = new ArrayList<Sensor>(sensorDescs);

		List<AnomalyDetectionAlgorithm> algs = this.getThreadPartitionKeys();

		sensorMap.init(vehicles, sensors, this);


		//go through the map and add minMaxPair reference to all instances
		for(SensorDescription sd: sensorDescs){
			
			//the min and max values for the sensor class of sd
			MinMaxPair minMaxPair = sd.getMinMaxPair();
			
			for(AnomalyDetectionAlgorithm alg: algs){

				List<SensorInstance> instances = sensorMap.getSensorInstances(alg, sd);

				for(SensorInstance i : instances){
					COSMOSensorInstance cosmoInstance = (COSMOSensorInstance)i;
					cosmoInstance.setMinMaxPair(minMaxPair);
				}//end iterate instances
			}//end iterate algorithms
		}//end iterate sensor descriptions

		//link the sensorMap to anomaly detection algorithms
		for(AnomalyDetectionAlgorithm alg: algs){
			alg.init(sensorMap);
		}

		this.vehicles = vehicles;
		this.sensors= sensors;
	}

	
	protected void threadTick(TimerEvent e ,AnomalyDetectionAlgorithm alg){

		//provided subclasses hook to new day 
		alg.preTimeTick(e);

		//iterate all sensor values for each vehicle
		for(Vehicle v : vehicles){
			//all threads share the same constant raw 
			Iterator<SensorDataEvent> sit = sensorDataInputStream.iterator(v);

			//give sensor data readings access to subclasses via hook
			alg.processSensorReadings(e, v, sit);

		}//end iterate all vehicles


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

	

	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("AnomalyDetectionTimer phase started");
	}

	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("AnomalyDetectionTimer phase ended");
	}
	
	
	public SensorStatusOutputStream getSensorStatusOutputStream() {
		return sensorStatusOutputStream;
	}
	public void setSensorStatusOutputStream(SensorStatusOutputStream sensorStatusOutputStream) {
		this.sensorStatusOutputStream = sensorStatusOutputStream;
	}
	public SensorDataInputStream getSensorDataInputStream() {
		return sensorDataInputStream;
	}
	public void setSensorDataInputStream(SensorDataInputStream sensorDataInputStream) {
		this.sensorDataInputStream = sensorDataInputStream;
	}





}
