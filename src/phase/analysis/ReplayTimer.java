package phase.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.Sensor;
import common.Timer;
import common.Util;
import common.Vehicle;
import common.event.HistoryEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.RepairEvent;
import common.event.TimerEvent;
import common.event.stream.HistoryInputStream;
import common.event.stream.ROCCurvePointOutputStream;
import common.event.stream.ROCCurvePointStreamManager;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.RepairStreamManager;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import common.event.stream.TimeStampedSensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusOutputStream;
import common.event.stream.TimeStampedSensorStatusStreamManager;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.log.ProgressLogger;
import phase.analysis.icosmo.ICOSMOTimer;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class ReplayTimer extends Timer{

	public final static int DEFAULT_SENSOR_STATUS_EVENT_RES_SIZE = 1024;
	/*
	 * global input and output streams of this timer
	 */
	//input to this timer
	private HistoryInputStream histInputStream;

	//output of this timer
	private ROCCurvePointOutputStream rocOutputStream;

	private ROCCurvePointStreamManager replayAnalisysChannel;

	/*
	 * attributes and associations
	 */

	private List<Vehicle> vehicles;
	private List<Algorithm> algorithms;

	//the timer we will be replaying all the history as input into
	private ICOSMOTimer icosmoTimer;


	/*
	 * internal streams objects for reading and writing with icosmo timer
	 */

	//stream manager to flush output of history into target icosmo timer
	private RepairStreamManager repairStreamManager;
//	private SensorStatusStreamManager sensorStatusStreamManager;

	//stream manager to flush icosmo input into an input stream, to convert into roc curve poitns
	private TimeStampedSensorStatusStreamManager timeStampedSensorStatusStreamManager;

	//internal icosmo input
	private TimeStampedSensorStatusInputStream localTimeStampedSensorStatusInputStream;
	//output stream to target icosmotimer
	private RepairOutputStream localRepairOutputStream;
	private SensorStatusOutputStream localSensorStatusOutputStream;



	//these attributes will be populated once initialized
	private SensorStatusHistory sensorStatusHist;
	private RepairHistory repairHist;
	//outputs sensor status events of the history to icosmo's input stream 
	private  SensorStatusEventReplayer sensorStatusReplayer;

	//reads the time stamped status event from icosmo, and creates roc point outputs
	private  ROCPointBuilder rocPointBuilder;
	private boolean initializedFlag;
	private int thresholdNumberDecimalPrecision;
	
	//will be populated once phase starts
	private List<Double> thresholds;
	//true when done replaying
	private boolean finishedFlag;
	
	private List<TimerEvent> historyTimerEvents;
	
	//logs the replay progress
	
	private ProgressLogger progressLogger;
	
	public ReplayTimer(ICOSMOTimer icosmoTimer,int thresholdNumberDecimalPrecision) {
		init(icosmoTimer,thresholdNumberDecimalPrecision);
	}

	public ReplayTimer(ICOSMOTimer icosmoTimer) {
		init(icosmoTimer);
	}

	/**
	 * empty constructor for sub class support
	 */
	protected ReplayTimer(){

	}

	
	public void setReplayAnalisysChannel(ROCCurvePointStreamManager replayAnalisysChannel) {
		this.replayAnalisysChannel = replayAnalisysChannel;
	}

	protected void init(ICOSMOTimer icosmoTimer){
		init(icosmoTimer,10);
	}
	
	/**
	 * initializes the attributes of this timer. used by subclasses and the contructor
	 * @param icosmoTimer
	 */
	protected void init(ICOSMOTimer icosmoTimer,int thresholdNumberDecimalPrecision){

		if(icosmoTimer == null || thresholdNumberDecimalPrecision < 0){
			throw new ConfigurationException("cannot create replay timer, null icosmoTimer.");
		}
		this.thresholdNumberDecimalPrecision = thresholdNumberDecimalPrecision;
		this.icosmoTimer = icosmoTimer;
		this.algorithms = icosmoTimer.getThreadPartitionKeys();
		this.vehicles = icosmoTimer.getVehicles();

		localRepairOutputStream = new RepairOutputStream(vehicles);
		localSensorStatusOutputStream = new SensorStatusOutputStream(algorithms);
		localTimeStampedSensorStatusInputStream = new TimeStampedSensorStatusInputStream(algorithms);

		//icosmo timer inputstreams
		RepairInputStream localIcosmoRepairInputStream = icosmoTimer.getRepairInputStream();
		//SensorStatusInputStream localIcosmoSensorStatusInputStream = icosmoTimer.getSensorStatusInputStream();
		//icosmo output tsteam
		TimeStampedSensorStatusOutputStream localIcosmoTimeSensorStatusOutStream = icosmoTimer.getTimeStampedSensorStatusOutputStream();


		//link the icosmo in streams to this out streams
		repairStreamManager = new RepairStreamManager(localIcosmoRepairInputStream,localRepairOutputStream);
		//sensorStatusStreamManager = new SensorStatusStreamManager(localIcosmoSensorStatusInputStream,localSensorStatusOutputStream);
		//link input to icosmo's output
		timeStampedSensorStatusStreamManager = new TimeStampedSensorStatusStreamManager(localTimeStampedSensorStatusInputStream,localIcosmoTimeSensorStatusOutStream);

		//will be populated once initialized
		sensorStatusHist = null; 
		repairHist = null;
		sensorStatusReplayer = null;
		rocPointBuilder = null;
		initializedFlag = false;
	}

	public boolean isDoneReplaying(){
		return this.finishedFlag;
	}
	public HistoryInputStream getHistInputStream() {
		return histInputStream;
	}

	public ROCCurvePointOutputStream getRocOutputStream() {
		return rocOutputStream;
	}

	protected List<Vehicle> getVehicles() {
		return vehicles;
	}

	protected List<Algorithm> getAlgorithms() {
		return algorithms;
	}

	protected ICOSMOTimer getIcosmoTimer() {
		return icosmoTimer;
	}

	protected RepairStreamManager getRepairStreamManager() {
		return repairStreamManager;
	}

	protected TimeStampedSensorStatusStreamManager getTimeStampedSensorStatusStreamManager() {
		return timeStampedSensorStatusStreamManager;
	}

	protected TimeStampedSensorStatusInputStream getLocalTimeStampedSensorStatusInputStream() {
		return localTimeStampedSensorStatusInputStream;
	}

	protected RepairOutputStream getLocalRepairOutputStream() {
		return localRepairOutputStream;
	}

	protected SensorStatusOutputStream getLocalSensorStatusOutputStream() {
		return localSensorStatusOutputStream;
	}

	public void setHistInputStream(HistoryInputStream histInputStream) {
		this.histInputStream = histInputStream;
	}

	public void setRocOutputStream(ROCCurvePointOutputStream rocOutputStream) {
		this.rocOutputStream = rocOutputStream;
	}

	public void initStreams(HistoryInputStream histInputStream, ROCCurvePointOutputStream rocOutputStream){
		if(histInputStream == null || rocOutputStream == null ){
			throw new ConfigurationException("failed to init streams ReplayTimer. null pointer");
		}
		this.histInputStream = histInputStream;
		this.rocOutputStream = rocOutputStream;
	}

	
	/**
	 * returns true when ready to replay history to icosmo timer
	 * @return true when ready, false otherwise
	 */
	public boolean isInitialized(){
		return initializedFlag;
	}

	/**
	 * initializes the internal associations to enable history event replay.
	 * @param hevent event with the history to replay
	 */
	public void init(SensorStatusHistory ssHist, RepairHistory rHist){

		if(ssHist == null || rHist == null){
			throw new ConfigurationException("cannot initialize ReplayTimer, null histories.");
		}
		this.sensorStatusHist = ssHist;
		this.repairHist = rHist; 

		//make sure the sensor status history properly populated
		//note that repair history may not have entries each time tick
		ssHist.integrityCheck();

		List<Algorithm> algorithms = ssHist.getPartitionKeys();

		SensorStatusInputStream localIcosmoSensorStatusInputStream = icosmoTimer.getSensorStatusInputStream();
		SensorStatusStreamManager sensorStatusStreamManager = new SensorStatusStreamManager(localIcosmoSensorStatusInputStream,localSensorStatusOutputStream);
		
		//outputs sensor status events of the history to icosmo's input stream 
		sensorStatusReplayer = new SensorStatusEventReplayer(algorithms,ssHist,sensorStatusStreamManager);

		//reads the time stamped status event from icosmo, and creates roc point outputs
		rocPointBuilder = new ROCPointBuilder(algorithms,rocOutputStream,localTimeStampedSensorStatusInputStream);

		//get all unique sensors that exist
		List<Sensor> sensors = sensorStatusHist.getUniqueSensors();

		//initialize icosmo
		icosmoTimer.init(sensors);
				
		initializedFlag = true;
	}

	protected void phaseStarted(PhaseBeginEvent e) throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("ReplayTimer phase starting");

		HistoryEvent hevent = histInputStream.readHistoryEvent();

		this.init(hevent.getSensorStatusHistory(),hevent.getRepairHistory());

		//want to run icosmo for all possible threshold levels
		//thresholds = sensorStatusHist.getUniqueZScores();
		List<Double> tmpThresholds = sensorStatusHist.getUniqueZScores();
		thresholds = Util.convertDecimalPrecision(tmpThresholds,thresholdNumberDecimalPrecision);
		
		log.log_debug("replaying history for "+thresholds.size()+" thresholds with precision: "+thresholdNumberDecimalPrecision+", (was "+tmpThresholds.size()+" thresnolds)");
		
		historyTimerEvents = new ArrayList<TimerEvent>(256);
		

		Iterator<TimerEvent> tit = sensorStatusHist.timerEventIterator();
		
		//iterate all the timer events in sensor status history
		while(tit.hasNext()){
			TimerEvent timerEvent = tit.next();

			historyTimerEvents.add(timerEvent);
			
		}

		//log 5% intervals of completion
		progressLogger = new ProgressLogger((int)Math.floor(0.01 * (double)thresholds.size()),thresholds.size());
		
		

	}

	
	/**
	 * Hook to be overridden by subclasses which is called each time tick.
	 * @param e The timer event with the timerstamp of time tick.
	 * @throws InterruptedException Called when threads are interrupted as main thread waits for workers to finish.
	 */
	protected void tick(TimerEvent e) throws InterruptedException{

		
		int thresholdIx = e.getTime();
		
		//log 5% intervals of completion
		progressLogger.logProgress("replay thresholds completion: ");
		
		//finished outputing all thresholds?
		if(thresholdIx >= this.thresholds.size()){
			finishedFlag= true;	
			return;
		}
		//iterate all thesholds, to build roc points for each threshold
		double t = this.thresholds.get(thresholdIx);
	
		replayHistoryForICOSMO(t);

				
	//	replayAnalisysChannel.flush();
	}
	
	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("Replay phase ended");
	}
	
	
	
	/**
	 * replays the all the history of repairs and sensor status events to icosmo timer, for some cosmo deviation threshold
	 * outputs a roc point to this output stream
	 * @param threshold cosmo deviation threshold
	 * @throws InterruptedException
	 */
	protected void replayHistoryForICOSMO(double threshold) throws InterruptedException {

		if(!this.isInitialized()){
			throw new SimulationException("cannot replay history. ReplayTimer not initialized.");
		}

		//reset the sensor map of icosmo for a new run
		icosmoTimer.resetSensorMap();
		icosmoTimer.resetPartialHistory();
		icosmoTimer.resetSensorSelectionCounters();
		icosmoTimer.setDeviationThreshold(threshold);


		Iterator<TimerEvent> tit = historyTimerEvents.iterator();
		
		//iterate all the timer events in sensor status history
		while(tit.hasNext()){
			TimerEvent timerEvent = tit.next();

			replayTick(timerEvent);
			
		}
		
		//flushes icomos' output stream into this timer's inputstream for all time ticks
		timeStampedSensorStatusStreamManager.flush();
				
		//creates a roc point for each algorithm, using the timestamped sensor statuses
		//output for each icosmo algorithm run (1 point per algorithm for a whole simulation, for 1 thershold)
		rocPointBuilder.outputROCPoints(threshold);

	}

	/**
	 * Replays the history at a given time, ticks the icosmo timer, and flushes the icosmo timer's output to the 
	 * intput of this timer
	 * @param timerEvent the timer to replay
	 * @throws InterruptedException
	 */
	protected void replayTick(TimerEvent timerEvent) throws InterruptedException {
		
		if(!this.isInitialized()){
			throw new SimulationException("cannot replay tick, ReplayTimer not initialized.");
		}
		//replay the sensor status events for current time and the repairs
		sensorStatusReplayer.replay(timerEvent);
		replayRepairEvents(timerEvent);

		//tick the icosmo time
		icosmoTimer.messageArrived(timerEvent);

				
	}

	/**
	 * replays (outputs) the repairs found in a history to the local repair output stream
	 * @param timerEvent time to replay
	 * @param repairHist the history of repairs
	 */
	protected void replayRepairEvents(TimerEvent timerEvent) {
		
		if(!this.isInitialized()){
			throw new SimulationException("cannot replay repairs, ReplayTimer not initialized.");
		}
		
		//go through all the repairs serially and output them
		List<Vehicle> vehicles = repairHist.getPartitionKeys();
		
		//for each vehicle, find their repairs and output them to icosmo's repair input 
		for(Vehicle v : vehicles){
			Iterator<RepairEvent> rit = repairHist.elementIterator(v, timerEvent);
			while(rit.hasNext()){
				localRepairOutputStream.write(v, rit.next());
			}
		}
		repairStreamManager.flush();
	}

}
