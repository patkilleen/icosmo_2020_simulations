package phase.analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.Sensor;
import common.Util;
import common.Vehicle;
import common.event.HistoryEvent;
import common.event.PerformanceMetricEvent;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.ROCCurvePointEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.HistoryInputStream;
import common.event.stream.PerformanceMetricOutputStream;
import common.event.stream.ROCCurvePointInputStream;
import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;
import common.synchronization.MultiThreadedTimer;
import phase.generation.history.FaultHistory;
import phase.generation.history.SensorStatusHistory;

public class PerformanceAnalysisTimer extends MultiThreadedTimer<Algorithm>{

	public static final int DEFAULT_NUMBER_OF_ROC_POINTS=4096;
	//input
	private HistoryInputStream historyInputStream;
	private ROCCurvePointInputStream rocInputStream;

	//output
	private PerformanceMetricOutputStream performanceMetricOutputStream;

	//parameters
	//time windows to check for faults to determine if zscores are tp,fp,fn,tn
	private int leftTimeWindow;
	private int rightTimeWindow;
	private int thresholdNumberDecimalPrecision;
	//associations
	private List<Double> uniqueZScores;
	private SensorStatusHistory ssHist;
	private FaultHistory faultHistory;

	public PerformanceAnalysisTimer(List<Algorithm> algorithms,int leftTimeWindow, int rightTimeWindow,int thresholdNumberDecimalPrecision) {
		super(algorithms);	
		init(leftTimeWindow,rightTimeWindow,thresholdNumberDecimalPrecision);
	}
	
	/**
	 * empty constructor for sub class flexibility
	 */
	protected PerformanceAnalysisTimer(){
		
	}
	
	
	public int getLeftTimeWindow() {
		return leftTimeWindow;
	}

	public int getRightTimeWindow() {
		return rightTimeWindow;
	}

	protected void init(int leftTimeWindow, int rightTimeWindow,int thresholdNumberDecimalPrecision){
		
		if(leftTimeWindow < 0 || rightTimeWindow < 0 || thresholdNumberDecimalPrecision < 0){
			throw new ConfigurationException("cannot create PerformanceAnalysisTimer, negative time windows");
		}
		this.rightTimeWindow = rightTimeWindow;
		this.leftTimeWindow = leftTimeWindow;
		this.thresholdNumberDecimalPrecision = thresholdNumberDecimalPrecision;
	}

	protected void init(int leftTimeWindow, int rightTimeWindow){
		
		if(leftTimeWindow < 0 || rightTimeWindow < 0 || thresholdNumberDecimalPrecision < 0){
			throw new ConfigurationException("cannot create PerformanceAnalysisTimer, negative time windows");
		}
		this.rightTimeWindow = rightTimeWindow;
		this.leftTimeWindow = leftTimeWindow;
		this.thresholdNumberDecimalPrecision = 10;
	}

	public void initStreams(ROCCurvePointInputStream rocInputStream,PerformanceMetricOutputStream performanceMetricOutputStream,HistoryInputStream historyInputStream){
		if(rocInputStream == null || performanceMetricOutputStream == null  || historyInputStream == null){
			throw new ConfigurationException("failed to init streams AnalysisTimer timer. null pointer");
		}
		this.rocInputStream = rocInputStream;
		this.performanceMetricOutputStream = performanceMetricOutputStream;
		this.historyInputStream = historyInputStream;
	}


	public static PerformanceMetricEvent createPerformanceMetricEvent(Algorithm alg, int tp, int tn, int fp, int fn,
			Double deviationThreshold) {
		double fpr = computeFalsePositiveRate(fp,tp,fn,tn);
		double tpr = computeTruePositiveRate(fp,tp,fn,tn);
		double acc = computeAccuracy(fp,tp,fn,tn);
		double fscore =computeFScore(fp,tp,fn,tn);
			
		return new PerformanceMetricEvent(alg,tp,tn,fp,fn,deviationThreshold,fpr,tpr,acc,fscore);
	}

	public static double computeFalsePositiveRate(int fp,int tp, int fn, int tn){
		
		if(fp == 0){
			return 0;
		}
		return (double )fp / (double)(fp + tn);
	}
	
	public static double computeTruePositiveRate(int fp,int tp, int fn, int tn){
		
		if(tp == 0){
			return 0;
		}
		
		return (double )tp / (double)(fn + tp);
	}
	
	public static double computeAccuracy(int fp,int tp, int fn, int tn){
		
		double nominator =  (double )(tp+ tn);
		if(nominator == 0){
			return 0;
		}
		return nominator/ (double)(fp+tp+fn+tn);
	} 
	
	public static double computeFScore(int fp,int tp, int fn, int tn){
		double precision =computePrecision(fp,tp,fn,tn);
		double recall =computeRecall(fp,tp,fn,tn);
		return 2.0 / ((1/precision)+(1/recall));
	}
	
	public static double computeRecall(int fp,int tp, int fn, int tn){
		//avoid division by 0
		if(tp == 0){
			return 0.0;
		}
		return (double )(tp) / (double)(tp+fn);
	} 
	
	public static double computePrecision(int fp,int tp, int fn, int tn){
		//avoid division by 0
		if(tp == 0){
			return 0.0;
		}
		return (double )(tp) / (double)(tp+fp);
	} 
	
	/**
	 * Creates a set of points for a ROC curve. Reads the sensor status history and fault history
	 * to build the set of points
	 * @param alg algorithm to builts point set for
	 * @return set of roc points
	 */
	public List<PerformanceMetricEvent> computeCOSMORocCurvePoints(Algorithm alg){

		 List<PerformanceMetricEvent> result = new ArrayList<PerformanceMetricEvent>(uniqueZScores.size());
		 
		//iterate all possible zscores and use them to set the sensor deviation threshold
		for(Double deviationThreshold : uniqueZScores){

			int tp = 0;
			int tn = 0;
			int fp = 0;
			int fn = 0;
			Iterator<TimerEvent>  tit = ssHist.timerEventIterator(alg); 

			//iterate all time ticks
			while(tit.hasNext()){
				TimerEvent timerEvent = tit.next();

				int time = timerEvent.getTime();

				Iterator<SensorStatusEvent>  zit = ssHist.elementIterator(alg, timerEvent); 

				//iterate all the zscore events
				while(zit.hasNext()){
					SensorStatusEvent zscoreEvent = zit.next();

					//skip the non cosmo sensors
					if(!zscoreEvent.isCosmoSensorFlag()){
						continue;
					}
					Vehicle v = zscoreEvent.getVehicle();
					Sensor s = zscoreEvent.getSensor();
					
					//was there a fault whitin time window of zscore?
					if(faultHistory.isSensorFaultInvolved(v, s, time, leftTimeWindow,rightTimeWindow)){

						if(zscoreEvent.isDeviating(deviationThreshold)){
							tp++;
						}else{
							fn++;
						}
					}else{
						if(zscoreEvent.isDeviating(deviationThreshold)){
							fp++;
						}else{
							tn++;
						}
					}

				}//end iterate zscore for time tick
			}//end iterate time ticks


			PerformanceMetricEvent pmEventCOSMO  = createPerformanceMetricEvent(alg,tp,tn,fp,fn,deviationThreshold);
			result.add(pmEventCOSMO);
			//performanceMetricOutputStream.write(alg, pmEventCOSMO);


		}//end iterate all the therdhols
return result;
	}


	/**
	 * Creates a set of points for a ROC curve. Reads the timestamped sensor status event input stream, as well as the input fault histroy, and 
	 * creates a roc point [true positive rate, false positive rate] for each threshold.
	 * @param alg the algorithm to crate the points
	 * @return the roc points set
	 */
	public List<PerformanceMetricEvent> computeICOSMORocCurvePoints(Algorithm alg){

		

		 List<PerformanceMetricEvent> result = new ArrayList<PerformanceMetricEvent>(uniqueZScores.size());


		//iterator to all the icosmo sensor statues for chosen algorithm and count the
		//tp, fp, 
		Iterator<ROCCurvePointEvent> it = rocInputStream.iterator(alg);
		//iterate all zscores and record them in history for later use
		while(it.hasNext()){

			int tp = 0;
			int tn = 0;
			int fp = 0;
			int fn = 0;
			
			ROCCurvePointEvent rocEvent = it.next();
			double deviationThreshold = rocEvent.getCosmoDeviationThreshold();
			Iterator<TimeStampedSensorStatusEvent>  zit = rocEvent.getSensorStatuses().iterator(); 

			//iterate all the zscore events
			while(zit.hasNext()){
				TimeStampedSensorStatusEvent zscoreEvent = zit.next();


				//skip the non cosmo sensors
				if(!zscoreEvent.isCosmoSensorFlag()){
					continue;
				}


				Vehicle v = zscoreEvent.getVehicle();
				Sensor s = zscoreEvent.getSensor();
				int time = zscoreEvent.getTimerEvent().getTime();

				//was there a fault whitin time window of zscore?
				if(faultHistory.isSensorFaultInvolved(v, s, time, leftTimeWindow,rightTimeWindow)){

					if(zscoreEvent.isDeviating(deviationThreshold)){
						tp++;
					}else{
						fn++;
					}
				}else{
					if(zscoreEvent.isDeviating(deviationThreshold)){
						fp++;
					}else{
						tn++;
					}
				}

			}//end iterate zscore for time tick
			PerformanceMetricEvent outputEvent = createPerformanceMetricEvent(alg.toICOSMO(),tp,tn,fp,fn,deviationThreshold);
			//performanceMetricOutputStream.write(alg.toICOSMO(),outputEvent);
			result.add(outputEvent);
		}//end iterate roc curve points
		
		return result;
	}
	protected void threadTick(TimerEvent timerEvent,Algorithm alg){


		//ocmpute the metrics and output roc curve point for ICOSMO algorithms
		List<PerformanceMetricEvent> rocPoints = computeICOSMORocCurvePoints(alg);
		
		//convert id to represent icosmo version of original algorithm
		Algorithm iCosmoAlg = alg.toICOSMO();
		
		//output the points
		for(PerformanceMetricEvent pt : rocPoints){
			pt.setAlgorithm(iCosmoAlg);
			performanceMetricOutputStream.write(iCosmoAlg,pt);
		}

		
	}//end thread tick function




	/**
	 * Hook to be overridden by subclasses which is called when a new phase begins.
	 * @param e The event indicating a new phase began.
	 */
	protected void phaseStarted(PhaseBeginEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("PerformanceAnalysisTimer phase starting");
		//this starts the analysis
		HistoryEvent histEvent = this.historyInputStream.readHistoryEvent();

		ssHist = histEvent.getSensorStatusHistory();
		faultHistory = histEvent.getFaultHistory();

		//compute the unique zscores found in input of sensor status
		//uniqueZScores = ssHist.getUniqueZScores();
		//now we want to convert the unizque sccores to match desire floating point presicion (number of decimals after '.')
		
		List<Double> tmpThresholds = ssHist.getUniqueZScores();
		

		uniqueZScores = Util.convertDecimalPrecision(tmpThresholds,thresholdNumberDecimalPrecision);
		log.log_debug("history has "+uniqueZScores.size()+" thresholds with precision: "+thresholdNumberDecimalPrecision+", (was "+tmpThresholds.size()+" thresnolds)");
		
		createAllCOSMORocCurvePoints();
	}
	
	
	
	protected void createAllCOSMORocCurvePoints() throws InterruptedException{
		
		Logger log = LoggerFactory.getInstance();
		log.log_debug("computing roc curve points for cosmo...");
		
		//Thread [] workers = new Thread[this.getThreadPartitionKeys().size()];
		
		//start comuting roc curve points in parralelle
	//	for(int i = 0;i<workers.length;i++){
			
			//Algorithm alg = this.getThreadPartitionKeys().get(i);
			COSMORocCurvePointBuilder worker = new COSMORocCurvePointBuilder(this,this.getThreadPartitionKeys());
			worker.tick(new TimerEvent(0));//unused timer event
		//	Thread t = new Thread(worker);
		//	workers[i] = t;
		//	t.start();
			
	//	}
		
		//now wait for them all to finish computing roc poitns for cosmo
		
		//for(int i = 0;i<workers.length;i++){
			
			//Thread t = workers[i];
			//t.join();
		//}
		
	}
	
	/**
	 * Hook to be overridden by subclasses which is called when a phase completes.
	 * @param e The event indicating a phase finished.
	 */
	protected void phaseEnded(PhaseCompleteEvent e)throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_debug("PerformanceAnalysisTimer phase ended");
	}
	
	public  void setFaultHistory(FaultHistory fh){
		this.faultHistory = fh;
	}
	public HistoryInputStream getHistoryInputStream() {
		return historyInputStream;
	}



	public void setHistoryInputStream(HistoryInputStream historyInputStream) {
		this.historyInputStream = historyInputStream;
	}



	public ROCCurvePointInputStream getRocInputStream() {
		return rocInputStream;
	}



	public void setRocInputStream(ROCCurvePointInputStream rocInputStream) {
		this.rocInputStream = rocInputStream;
	}



	public PerformanceMetricOutputStream getPerformanceMetricOutputStream() {
		return performanceMetricOutputStream;
	}



	public void setPerformanceMetricOutputStream(PerformanceMetricOutputStream performanceMetricOutputStream) {
		this.performanceMetricOutputStream = performanceMetricOutputStream;
	}
	
	
	/*private static class COSMORocCurvePointBuilder implements Runnable{

		private PerformanceAnalysisTimer timer;
		private Algorithm algorithm;
		
		
		public COSMORocCurvePointBuilder(PerformanceAnalysisTimer timer, Algorithm algorithm) {
			super();
			this.timer = timer;
			this.algorithm = algorithm;
		}


		@Override
		public void run() {
			
			//compute the metrics for cosmos algorithms
			List<PerformanceMetricEvent> rocPoints = timer.computeCOSMORocCurvePoints(algorithm);
			
			PerformanceMetricOutputStream out = timer.getPerformanceMetricOutputStream();
			
			//output the points
			for(PerformanceMetricEvent pt : rocPoints){
				out.write(algorithm,pt);
			}
			
		}
		
	}*/

	private static class COSMORocCurvePointBuilder extends MultiThreadedTimer<Algorithm>{

		private PerformanceAnalysisTimer timer;
		
		
		public COSMORocCurvePointBuilder(PerformanceAnalysisTimer timer, List<Algorithm> algorithms) {
			super(algorithms);
			this.timer = timer;
		}

		
		public void tick(TimerEvent e) throws InterruptedException{
			super.tick(e);
		}
		@Override
		protected void threadTick(TimerEvent timerEvent,Algorithm alg){
			
			//compute the metrics for cosmos algorithms
			List<PerformanceMetricEvent> rocPoints = timer.computeCOSMORocCurvePoints(alg);
			
			PerformanceMetricOutputStream out = timer.getPerformanceMetricOutputStream();
			
			//output the points
			for(PerformanceMetricEvent pt : rocPoints){
				out.write(alg,pt);
			}
			
		}
		
	}
	
}
