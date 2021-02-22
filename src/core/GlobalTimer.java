package core;

import java.util.ArrayList;
import java.util.List;

import common.Algorithm;
import common.Timer;
import common.Vehicle;
import common.event.MessageEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.TimerEvent;
import common.event.stream.EventInputStream;
import common.event.stream.EventOutputStream;
import common.event.stream.FaultInputStream;
import common.event.stream.FaultOutputStream;
import common.event.stream.FaultStreamManager;
import common.event.stream.HistoryInputStream;
import common.event.stream.HistoryOutputStream;
import common.event.stream.HistoryStreamManager;
import common.event.stream.PerformanceMetricInputStream;
import common.event.stream.PerformanceMetricOutputStream;
import common.event.stream.ROCCurvePointInputStream;
import common.event.stream.ROCCurvePointOutputStream;
import common.event.stream.ROCCurvePointStreamManager;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.RepairStreamManager;
import common.event.stream.SensorDataInputStream;
import common.event.stream.SensorDataOutputStream;
import common.event.stream.SensorDataStreamManager;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import common.event.stream.StreamManager;
import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.BasicStreamTimer;
import phase.analysis.PerformanceAnalysisTimer;
import phase.analysis.ReplayTimer;
import phase.analysis.output.OutputTimer;
import phase.generation.cosmo.AnomalyDetectionModelTimer;
import phase.generation.data.DataGenerationCOSMOTimer;
import phase.generation.data.DataGenerationTimer;
import phase.generation.fault.FaultTimer;
import phase.generation.history.HistoryTimer;

public class GlobalTimer extends Timer {

	public final static int NUMBER_OF_PHASES = 3;

	//not sure yet for this one, how many timers
	public final static int CONFIGURATION_PHASE_NUMBER_OF_TIMERS = 0;
	public final static int GENERATION_PHASE_NUMBER_OF_TIMERS = 6;
	public final static int PERFORMANCE_ANALYSIS_PHASE_NUMBER_OF_TIMERS = 3;


	/*
	 * the indexes for configuration phase (1st phase)
	 */

	/*
	 * the indexes for generation phase (2nd phase)
	 */
	public final static int GENERATION_PHASE_FAULT_TIMER_IX = 0;
	public final static int GENERATION_PHASE_STREAM_TIMER_IX1 = 1;
	public final static int GENERATION_PHASE_STREAM_TIMER_IX2 = 2;
	public final static int GENERATION_PHASE_DATA_GENERATION_TIMER_IX = 3;
	public final static int GENERATION_PHASE_STREAM_TIMER_IX3 = 4;
	public final static int GENERATION_PHASE_ANOMALY_DETECTION_TIMER_IX = 5;
	public final static int GENERATION_PHASE_STREAM_TIMER_IX4 = 6;
	public final static int GENERATION_PHASE_HITSORY_TIMER_IX = 7;
	
	/*
	 * stream manager index
	 * 
	 */
	//index for the stream manager state machine
			public final static int GENERATION_PHASE_NUMBER_OF_STREAM_MANAGERS = 4;
			public final static int GENERATION_PHASE_FAULT_STREAM_MAGR_IX = 0;
			public final static int GENERATION_PHASE_REPAIR_STREAM_MAGR_IX = 1;
			public final static int GENERATION_PHASE_SENSORDATA_STREAM_MAGR_IX = 2;
			public final static int GENERATION_PHASE_DEVIATION_STREAM_MAGR_IX = 3;

	/*
	 * the indexes for performance analysis phase (3rd phase)
	 */
			
			public final static int PERFORMANCE_ANALYSIS_PHASE_NUMBER_OF_STREAM_MANAGERS = 2;
	public final static int PERFORMANCE_ANALYSIS_PHASE_STREAM_TIMER_IX1 = 0;
	public final static int PERFORMANCE_ANALYSIS_PHASE_REPLAY_TIMER_IX = 1;
	public final static int PERFORMANCE_ANALYSIS_PHASE_ANALYSIS_TIMER_IX = 2;
	public final static int PERFORMANCE_ANALYSIS_PHASE_STREAM_TIMER_IX2 = 3;
	public final static int PERFORMANCE_ANALYSIS_PHASE_OUPPUT_TIMER_IX = 3;
	//public final static int PERFORMANCE_ANALYSIS_PHASE_STREAM_TIMER_IX3 = 5;

	
	
			
	private List<Timer []> stateMachine;
	
	private List<Vehicle> vehicles;
	private List<Algorithm> algorithms;
	
	private int maxTimeTicks;

	//private StreamManager outputStreamManager;
	//private OutputTimer outputTimer;
	/**
	 * The phase (index of state machine) that determines which set of timers are called.
	 */
	private int phase;

	/*
	 *stream managers that are flushed at end of phases
	 *ie at index 0, end of phase 0 SM to be flushed
	 *at index 1, end of phase 1 SM to be flushed 
	 */
	private List<List<StreamManager>> endOfPhaseFlushes;
	private ReplayTimer replayTimer;
	public GlobalTimer(FaultTimer faultTimer, 
			DataGenerationCOSMOTimer dataGenTimer,
			HistoryTimer historyTimer,
			ReplayTimer replayTimer,
			PerformanceAnalysisTimer analysisTimer,
			OutputTimer outputTimer, 
			List<Algorithm> algorithms,
			List<Vehicle> vehicles, int maxTimeTicks) {
		super();
		//null pointer check
		if(faultTimer == null || dataGenTimer == null  || historyTimer == null
				|| replayTimer == null || analysisTimer == null || outputTimer == null){
			throw new ConfigurationException("Failed to create GloabalTimer due to null timer.");
		}
		
		init(algorithms,vehicles,maxTimeTicks,replayTimer);
		
		/*
		 * connect the streams to timers
		 */
		
		connectPhase0Streams(faultTimer, dataGenTimer, historyTimer);
		connectPhase1Streams(historyTimer, replayTimer, analysisTimer, outputTimer);
		
	/*
	 * configures the state machine for passing messages to timers
	 */
		configureStateMachinePhase0(faultTimer,dataGenTimer,historyTimer,replayTimer,analysisTimer,outputTimer);
		configureStateMachinePhase1(historyTimer,replayTimer,analysisTimer,outputTimer);
		configureStateMachinePhase2(outputTimer);
		 
	/*
	 * configures the end of phases flushes
	 */
		configureEndPhase0Flushes(historyTimer,replayTimer);
		configureEndPhase1Flushes(analysisTimer,outputTimer);

	
	}

	

	public GlobalTimer(HistoryTimer historyTimer,
			ReplayTimer replayTimer,
			PerformanceAnalysisTimer analysisTimer,
			OutputTimer outputTimer, 
			List<Algorithm> algorithms,
			List<Vehicle> vehicles, int maxTimeTicks) {
		super();
		//null pointer check
		if( historyTimer == null || replayTimer == null || analysisTimer == null || outputTimer == null){
			throw new ConfigurationException("Failed to create GloabalTimer due to null timer.");
		}
		
		init(algorithms,vehicles,maxTimeTicks,replayTimer);
		
		/*
		 * connect the streams to timers
		 */
		
		connectPhase1Streams(historyTimer, replayTimer, analysisTimer, outputTimer);
		
	/*
	 * configures the state machine for passing messages to timers
	 */
		configureStateMachinePhase1(historyTimer,replayTimer,analysisTimer,outputTimer);
		configureStateMachinePhase2(outputTimer);
		 
	/*
	 * configures the end of phases flushes
	 */

		configureEndPhase1Flushes(analysisTimer,outputTimer);
	
	}
	private void init(List<Algorithm> algorithms, List<Vehicle> vehicles, int maxTimeTicks,
			ReplayTimer replayTimer) {
		if(algorithms == null || algorithms.isEmpty() || vehicles == null || vehicles.isEmpty()){
			throw new ConfigurationException("Failed to create GloabalTimer due to null algorithms or vehicles");
		}
		
		this.vehicles = vehicles;
		this.algorithms = algorithms;
		this.maxTimeTicks = maxTimeTicks;
		//create state machine
		stateMachine = new ArrayList<Timer[]>(NUMBER_OF_PHASES);
		
		endOfPhaseFlushes = new ArrayList<List<StreamManager>>(NUMBER_OF_PHASES);
		this.replayTimer = replayTimer;
		
	}

	private void configureEndPhase1Flushes(PerformanceAnalysisTimer analysisTimer, OutputTimer outputTimer) {
		List<StreamManager> phase2StreamsToFlush = createStreamsToFlush(
				outputTimer.getPerformanceMetricInputStream(),
				analysisTimer.getPerformanceMetricOutputStream());
		
		endOfPhaseFlushes.add(phase2StreamsToFlush);
		
	}

	private void configureEndPhase0Flushes(HistoryTimer historyTimer, ReplayTimer replayTimer) {
		List<StreamManager> phase1StreamsToFlush = createStreamsToFlush(
				replayTimer.getHistInputStream(),
				historyTimer.getHistOutputStream());				
		endOfPhaseFlushes.add(phase1StreamsToFlush);
	}

	
	private void connectPhase0Streams(FaultTimer faultTimer, DataGenerationCOSMOTimer dataGenTimer, HistoryTimer historyTimer){
		connectFaultStreams(faultTimer,dataGenTimer,historyTimer);
		connectRepairStreams(faultTimer,dataGenTimer,historyTimer);
		//connectSensorDataStreams(dataGenTimer,anomalyTimer);	
		connectSensorStatusStreams(historyTimer,dataGenTimer);
	}
	
	
	private void connectPhase1Streams(HistoryTimer historyTimer, ReplayTimer replayTimer, PerformanceAnalysisTimer analysisTimer, OutputTimer outputTimer){
		connectHistoryStreams(historyTimer,replayTimer,analysisTimer,outputTimer);
		connectROCPointStreams(replayTimer,analysisTimer);
		connectPerformanceMetricStreams(analysisTimer,outputTimer);
	}
	private void configureStateMachinePhase0(FaultTimer faultTimer, DataGenerationCOSMOTimer dataGenTimer,
			HistoryTimer historyTimer,ReplayTimer replayTimer, PerformanceAnalysisTimer analysisTimer,OutputTimer outputTimer) {

		//history and  data gen share the same input stream
		BasicStreamTimer faultStreamManagerTimer = new BasicStreamTimer(
				new FaultStreamManager(
						historyTimer.getFaultInputStream(),
						faultTimer.getFaultOutputStream()));
		BasicStreamTimer repairStreamManagerTimer = new BasicStreamTimer(
				new RepairStreamManager(
						historyTimer.getRepairInputStream(),
						faultTimer.getRepairOutputStream()));
		BasicStreamTimer sensorStatusStreamManagerTimer = new BasicStreamTimer(
				new SensorStatusStreamManager(
						historyTimer.getSensorStatusInputStream(),
						dataGenTimer.getSensorStatusOutputStream()));
		
		Timer [] phase0Timers = new Timer[GENERATION_PHASE_NUMBER_OF_TIMERS];

		phase0Timers[0] = faultTimer;
		phase0Timers[1] = faultStreamManagerTimer;//timer tick to flush fault events
		phase0Timers[2] = repairStreamManagerTimer;//timer tick to flush repair events
		phase0Timers[3] = dataGenTimer;
		phase0Timers[4] = sensorStatusStreamManagerTimer;//timer tick to flush sensor deviation status events
		phase0Timers[5] = historyTimer;
		
		stateMachine.add(phase0Timers);
		
	}

	private List<StreamManager> createStreamsToFlush(EventInputStream in,EventOutputStream out){
		List<StreamManager> streamsToFlush = new ArrayList<StreamManager>(1);
		HistoryStreamManager sm = new HistoryStreamManager(in,out);
		
		streamsToFlush.add(sm);//history stream manager should be flushed at end phase 0
		return streamsToFlush;
	}
	private void configureStateMachinePhase1( HistoryTimer historyTimer,ReplayTimer replayTimer, PerformanceAnalysisTimer analysisTimer,
			OutputTimer outputTimer) {
		
		
		ROCCurvePointInputStream rocin = analysisTimer.getRocInputStream();
		ROCCurvePointOutputStream rocout = replayTimer.getRocOutputStream();
		ROCCurvePointStreamManager replayAnalisysChannel = new ROCCurvePointStreamManager(rocin,rocout);
		//used to flush when tick occurs
		BasicStreamTimer rocPointsStreamManagerTimer = new BasicStreamTimer( replayAnalisysChannel);
			
		
		
		Timer [] phase1Timers = new Timer[PERFORMANCE_ANALYSIS_PHASE_NUMBER_OF_TIMERS];

		phase1Timers[0] = replayTimer;//replay zscores for icosmo
		phase1Timers[1] = rocPointsStreamManagerTimer;//flushes all the icosmo zscore bulk output for analysis
		phase1Timers[2] = analysisTimer;//analyze all, outputing roc curve points gradually
		
				
		stateMachine.add(phase1Timers);

		
	}

	private void configureStateMachinePhase2(OutputTimer outputTimer) {
		Timer [] phase2Timers = new Timer[1];

		phase2Timers[0] = outputTimer;//replay zscores for icosmo
			
		stateMachine.add(phase2Timers);
		
	}
	
	private void connectSensorStatusStreams(HistoryTimer historyTimer,
			DataGenerationCOSMOTimer anomalyTimer) {
		SensorStatusInputStream sdsin = new SensorStatusInputStream(algorithms);
		historyTimer.setSensorStatusInputStream(sdsin);
		SensorStatusOutputStream sdsout = new SensorStatusOutputStream(algorithms);
		anomalyTimer.setSensorStatusOutputStream(sdsout);
		
	}

	private void connectFaultStreams(FaultTimer faultTimer, DataGenerationCOSMOTimer dataGenTimer,
			HistoryTimer historyTimer) {
		
		//connect fault streams
		FaultInputStream fin =  new FaultInputStream(vehicles);
		dataGenTimer.setFaultInputStream(fin);
		historyTimer.setFaultInputStream(fin);
		FaultOutputStream fout = new FaultOutputStream(vehicles); 
		faultTimer.setFaultOutputStream(fout);
				
	}
	
	private void connectRepairStreams(FaultTimer faultTimer, DataGenerationCOSMOTimer dataGenTimer,
			HistoryTimer historyTimer) {
		//connect repair streams
				RepairInputStream rin = new RepairInputStream(vehicles); 
				dataGenTimer.setRepairInputStream(rin);
				historyTimer.setRepairInputStream(rin);
				RepairOutputStream rout = new RepairOutputStream(vehicles);
				faultTimer.setRepairOutputStream(rout);
				
	}


	private void connectHistoryStreams(HistoryTimer historyTimer, ReplayTimer replayTimer,
			PerformanceAnalysisTimer analysisTimer, OutputTimer outputTimer) {
		
		HistoryInputStream hin = replayTimer.getHistInputStream();
		
		//not created yet?
		if(hin == null){
			hin = new HistoryInputStream();
			replayTimer.setHistInputStream(hin);
		}
		
		analysisTimer.setHistoryInputStream(hin);
		outputTimer.setHistoryInputStream(hin);
		
		HistoryOutputStream hout = historyTimer.getHistOutputStream();
		
		//not created yet?
		if(hout == null){
			//create the outpustream
			 hout = new HistoryOutputStream();	
			 historyTimer.setHistOutputStream(hout);
		}	
		
	}

	private void connectROCPointStreams(ReplayTimer replayTimer, PerformanceAnalysisTimer analysisTimer) {
		ROCCurvePointInputStream rocin = new ROCCurvePointInputStream(algorithms);
		analysisTimer.setRocInputStream(rocin);
		ROCCurvePointOutputStream rocout = new ROCCurvePointOutputStream(algorithms);
		replayTimer.setRocOutputStream(rocout);
		
	}

	private void connectPerformanceMetricStreams(PerformanceAnalysisTimer analysisTimer, OutputTimer outputTimer) {
		//here make sure to add teh icosmo version of algorithsm are found in the performance metric streams
		List<Algorithm> algs2 = new ArrayList<Algorithm>(algorithms.size());
		for(Algorithm alg: algorithms){
			algs2.add(alg);
			algs2.add(alg.toICOSMO());
		}
		
		PerformanceMetricInputStream pmin = new PerformanceMetricInputStream(algs2);
		outputTimer.setPerformanceMetricInputStream(pmin);
		PerformanceMetricOutputStream pmout = new PerformanceMetricOutputStream(algs2);
		analysisTimer.setPerformanceMetricOutputStream(pmout);
		
	}

	
/*
	public PerformanceMetricStreamManager getOutputStreamManager(){
		return (PerformanceMetricStreamManager)this.outputStreamManager;
	}*/
	public boolean isFinishedAnalysisPhase(){
		return replayTimer.isDoneReplaying();
	}
	public int getMaxTimeTicks(){
		return this.maxTimeTicks;
	}
	
	/**
	 * Broadcasts an event to all the timers in current phase.
	 * @param e The event to send to each timer.
	 * @throws InterruptedException 
	 */
	private void timerBroadcast(MessageEvent e) throws InterruptedException{
		Timer [] currentStateMachine = this.stateMachine.get(phase);

		//iterate through all timers and trigger a time tick via state machine
		for(int state = 0; state < currentStateMachine.length; state++){

			Timer timer = currentStateMachine[state];
			timer.messageArrived(e);//call time tick
		}
	}
	/**
	 * Called when a new day occurs. Triggers a tick for all clocks in simulation in proper order.
	 * @param e The event indicating a new day occured.
	 * @throws InterruptedException 
	 */
	protected void tick(TimerEvent e) throws InterruptedException{
		timerBroadcast(e);
	}
	
	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("globalTimer, phase started.");
		timerBroadcast(e);

	}

	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 * @throws InterruptedException 
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		
		Logger log = LoggerFactory.getInstance();
		log.log_debug("globalTimer, phase ended.");
		
		timerBroadcast(e);
		
		//make sure not out of bounds
		if(phase < endOfPhaseFlushes.size()){
			
			
			List<StreamManager> toFlush = endOfPhaseFlushes.get(phase);
			
			//flush all end of phase stream managers
			for(StreamManager sm : toFlush){
				sm.flush();
			}

		}
		//go to next phase
		phase = (phase + 1) ;
		
	}

	
}
