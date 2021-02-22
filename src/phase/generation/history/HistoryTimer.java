package phase.generation.history;

import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.FaultDescription;
import common.Vehicle;
import common.event.FaultEvent;
import common.event.HistoryEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedPhaseCompleteEvent;
import common.event.TimerEvent;
import common.event.stream.FaultInputStream;
import common.event.stream.HistoryOutputStream;
import common.event.stream.RepairInputStream;
import common.event.stream.SensorStatusInputStream;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.MultiThreadedTimer;

public class HistoryTimer extends MultiThreadedTimer<Algorithm> {

	
	//input streams
	private SensorStatusInputStream sensorStatusInputStream;
	private FaultInputStream faultInputStream;
	private RepairInputStream repairInputStream;
	
	//outputstreams
	private HistoryOutputStream histOutputStream;
	
	//histories
	private SensorStatusHistory sensorStatusHist;
	private FaultHistory fHist;
	private RepairHistory rHist;
	
	private List<Vehicle> vehicles;
	
	public HistoryTimer(List<Algorithm> algs, List<Vehicle> vehicles) {
		super(algs);
		
		initHistory(algs,vehicles);
		
	}

	protected void initHistory(List<Algorithm> algs, List<Vehicle> vehicles) {
		if(algs == null || vehicles == null){
			throw new ConfigurationException("failed to create history timer. null pointer vehicles or algorithms");
		}
		sensorStatusHist = new SensorStatusHistory(algs);
		fHist = new FaultHistory(vehicles);
		rHist = new RepairHistory(vehicles);
		this.vehicles = vehicles;
	}

	/**
	 * Empty constructor for flexibility to subclasses.
	 */
	protected HistoryTimer(){
		
	}

	public void initStreams(SensorStatusInputStream sensorStatusInputStream,
			FaultInputStream faultInputStream, RepairInputStream repairInputStream, HistoryOutputStream histOutputStream){

		if(sensorStatusInputStream == null || faultInputStream == null ||
				repairInputStream == null || histOutputStream == null){
			throw new ConfigurationException("failed to init streams history timer. null pointer");
		}
		this.sensorStatusInputStream = sensorStatusInputStream;
		this.faultInputStream = faultInputStream;
		this.repairInputStream = repairInputStream;
		this.histOutputStream = histOutputStream;
	}

	protected void threadTick(TimerEvent timerEvent,Algorithm alg){
		
		Iterator<SensorStatusEvent> zit = sensorStatusInputStream.iterator(alg);
		//iterate all zscores and record them in history for later use
		while(zit.hasNext()){
		
			SensorStatusEvent zscoreEvent = zit.next();

			sensorStatusHist.recordElement(alg,timerEvent, zscoreEvent);
		}

	}
	
	@Override
	protected void tick(TimerEvent e) throws InterruptedException{
		
		//since repairs and faults are rare, process them serially and add
		//them to the history
		
		//iterate all vehicles
		for(Vehicle v: vehicles){
			//handling serially the faults and repairs
			Iterator<FaultEvent> fit = faultInputStream.iterator(v);
			//iterate all new faults of vehicle v and record them in fault history for later
			//retrieval
			while(fit.hasNext()){
			
				FaultEvent faultEvent = fit.next();
				
				FaultDescription fd = faultEvent.getFaultDescription();
				
				int time = e.getTime();
				
				fHist.recordElement(v, e, fd);
				
			}
			
			Iterator<RepairEvent> rit = repairInputStream.iterator(v);
			//iterate all repairs of vehicle v and record them in fault history for later
			//retrieval
			while(rit.hasNext()){
			
				RepairEvent repairEvent = rit.next();
	
				FaultDescription fd = repairEvent.getFaultDescription();
				
				int time = e.getTime();
				
				//update the fault history (fix the fault)
				fHist.recordRepair(v, e,fd);
				rHist.recordElement(v,e, repairEvent);
				
			}
		}
		//this will start the multithreaded Zscore per Algorithm handling
		//ie, it will call threadTick function (above) with many threads, one for each algorithm
		//want to process zscore in parrallel to reduce time
		super.tick(e);
	}

	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e){
		
		//make sure to repair all unrepaired faults at end of this phase
		if(!(e instanceof TimeStampedPhaseCompleteEvent)){
			throw new SimulationException("cannot end phase since PhaseCompleteEvent was expected to be of type TimeStampedPhaseCompleteEvent, but was: "+e.getClass());
		}
		
		TimeStampedPhaseCompleteEvent timeEvent = (TimeStampedPhaseCompleteEvent) e;
		//iterate all analysis faults
		fHist.repairAllUnrepairedFaults(timeEvent.getTime());
		
		//once gathered all the history of a phase, output it
		histOutputStream.writeHistoryEvent(new HistoryEvent(sensorStatusHist,fHist,rHist));;
		
		Logger log = LoggerFactory.getInstance();
		log.log_debug("HistoryTimer phase ended");
		
		this.delete();
	}
	


	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("HistoryTimer phase started");
	}


	
	public SensorStatusInputStream getSensorStatusInputStream() {
		return sensorStatusInputStream;
	}



	public void setSensorStatusInputStream(SensorStatusInputStream sensorStatusInputStream) {
		this.sensorStatusInputStream = sensorStatusInputStream;
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



	public HistoryOutputStream getHistOutputStream() {
		return histOutputStream;
	}



	public void setHistOutputStream(HistoryOutputStream histOutputStream) {
		this.histOutputStream = histOutputStream;
	}
	
	
}
