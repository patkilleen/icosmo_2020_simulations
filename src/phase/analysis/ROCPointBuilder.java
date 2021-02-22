package phase.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import common.Algorithm;
import common.event.ROCCurvePointEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.stream.ROCCurvePointOutputStream;
import common.event.stream.TimeStampedSensorStatusInputStream;

public class ROCPointBuilder {
	//the workers to run each algothims's sensor status output in a thread
	//List<ROCPointOutputWorker> flushWorkers;
	
	private ROCCurvePointOutputStream rocOutputStream;
	
	private TimeStampedSensorStatusInputStream localTimeStampedSensorStatusInputStream;
	

	private ExecutorService executorService;
	private List<Callable<Object>> workers;
	
	public ROCPointBuilder(List<Algorithm> algorithms, ROCCurvePointOutputStream rocOutputStream,TimeStampedSensorStatusInputStream localTimeStampedSensorStatusInputStream) {
		this.rocOutputStream = rocOutputStream;
		this.localTimeStampedSensorStatusInputStream = localTimeStampedSensorStatusInputStream;
		executorService = Executors.newFixedThreadPool(algorithms.size());
		workers = buildROCPointWorkers(algorithms);
		
	}


	public void outputROCPoints(double deviationThreshold) throws InterruptedException{
		/*//gather the icosmo output, and convertr to roc curve point, outputing to roc point stream
		for(ROCPointOutputWorker worker : flushWorkers){
			worker.createROCPoint(deviationThreshold);
		}//end iterate vehicles

		//wait for all simulated vehicle to finish this time-tick's simulation
		for(ROCPointOutputWorker worker : flushWorkers){
			worker.join();

		}
		*/
		for(int i = 0;i < workers.size();i++){
			ROCPointOutputWorker w = (ROCPointOutputWorker)workers.get(i);
			
			w.setDeviationThreshold(deviationThreshold);
		}
		
		
		List<Future<Object>> taskCompletionStatus = executorService.invokeAll((Collection<? extends Callable<Object>>) workers);
		
		for(Future<Object> status : taskCompletionStatus){
			if(!status.isDone()){
				throw new IllegalStateException("failed to build roc points history threads");
			}
		}
	}
	private List<Callable<Object>> buildROCPointWorkers(List<Algorithm> algorithms){
		
		List<Callable<Object>> workers = new ArrayList<Callable<Object>>(algorithms.size());
		//create all workers
		for(int i = 0; i < algorithms.size(); i++){
			Algorithm alg = algorithms.get(i);
			ROCPointOutputWorker worker = new ROCPointOutputWorker(alg,rocOutputStream,localTimeStampedSensorStatusInputStream);
			workers.add(worker);
		}
		
		return workers;
			
	}


	private static class ROCPointOutputWorker implements Callable<Object>{
		//config attributes
		private Thread thread;
		private Algorithm alg;
		private Algorithm algICOSMO;
		
		//output of this timer
		private ROCCurvePointOutputStream rocOutputStream;
		
		/*
		 * internal associations
		 */
		//internal icosmo input
		private TimeStampedSensorStatusInputStream timeSSInputStream;
		
		//the attribut that changes each run
		private double deviationThreshold;
		public ROCPointOutputWorker(Algorithm alg,ROCCurvePointOutputStream rocOutputStream,TimeStampedSensorStatusInputStream timeSSInputStream) {
			super();
			this.alg = alg;
			this.rocOutputStream = rocOutputStream;
			this.timeSSInputStream = timeSSInputStream;
			this.algICOSMO = alg.toICOSMO();
		}

		public void setDeviationThreshold(double deviationThreshold){
			this.deviationThreshold = deviationThreshold;
		}
		
		

		@Override
		public Object call() throws Exception {
			List<TimeStampedSensorStatusEvent> events = new ArrayList<TimeStampedSensorStatusEvent>(ReplayTimer.DEFAULT_SENSOR_STATUS_EVENT_RES_SIZE);
			
			//iterate all sensor statuses for the icosmo run
			Iterator<TimeStampedSensorStatusEvent> it = timeSSInputStream.iterator(alg);
			
			//fill the resulting list
			while(it.hasNext()){
				events.add(it.next());
			}
			
			ROCCurvePointEvent res = new ROCCurvePointEvent(deviationThreshold,events);
			
			rocOutputStream.write(alg, res);
			return null;
		}
		
	}
}
