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
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import phase.generation.history.SensorStatusHistory;

public class SensorStatusEventReplayer {
	//the workers to run each algothims's sensor status output in a thread
	//private List<ReplayWorker> replayWorkers;
	
	private SensorStatusOutputStream localSensorStatusOutputStream;
	
	private SensorStatusStreamManager sensorStatusStreamManager;
	
	private ExecutorService executorService;
	private List<Callable<Object>> workers;
	
	public SensorStatusEventReplayer(List<Algorithm> algorithms, SensorStatusHistory ssHist,SensorStatusStreamManager sensorStatusStreamManager) {
		this.localSensorStatusOutputStream = sensorStatusStreamManager.getSensorStatusOutputStream();
		this.sensorStatusStreamManager = sensorStatusStreamManager;
		//replayWorkers = buildReplayWorkers(algorithms,ssHist);
		executorService = Executors.newFixedThreadPool(algorithms.size());
		workers = buildReplayWorkers(algorithms,ssHist);
		
		
	}


	public void replay(TimerEvent timerEvent) throws InterruptedException{
		//run all the workes in a thread for each algorith
		//they will all output the sensor status to the
		//proper partition in sensor status outputstream
		//simulate a algorithm in seperate threads
		/*for(ReplayWorker worker : replayWorkers){
			worker.startOutputing(timerEvent);
		}//end iterate vehicles

		//wait for all simulated vehicle to finish this time-tick's simulation
		for(ReplayWorker worker : replayWorkers){
			worker.join();

		}*/
		
		for(int i = 0;i < workers.size();i++){
			ReplayWorker w = (ReplayWorker)workers.get(i);
			
			w.setTimerEvent(timerEvent);
		}
		
		
		List<Future<Object>> taskCompletionStatus = executorService.invokeAll((Collection<? extends Callable<Object>>) workers);
		
		for(Future<Object> status : taskCompletionStatus){
			if(!status.isDone()){
				throw new IllegalStateException("failed to replay history threads");
			}
		}
		
		sensorStatusStreamManager.flush();
	}
	
	private List<Callable<Object>> buildReplayWorkers(List<Algorithm> algorithms,SensorStatusHistory ssHist){
		/*
		List<ReplayWorker> workers = new ArrayList<ReplayWorker>(algorithms.size());
		//create all workers
		for(int i = 0; i < algorithms.size(); i++){
			Algorithm alg = algorithms.get(i);
			ReplayWorker worker = new ReplayWorker(alg,ssHist,localSensorStatusOutputStream);
			workers.add(worker);
		}
		
		return workers;
		*/

		
		List<Callable<Object>> workers = new ArrayList<Callable<Object>>(algorithms.size());
		
		for(int i = 0; i < algorithms.size(); i++){
			Algorithm alg = algorithms.get(i);
			ReplayWorker worker = new ReplayWorker(alg,ssHist,localSensorStatusOutputStream);
			workers.add(worker);
		}
		
		return workers;
			
	}


private static class ReplayWorker implements Callable<Object>{
	//config attributes
	//private Thread thread;
	private Algorithm alg;
	private SensorStatusHistory ssHist;
	
	//output stream to target timer
	private SensorStatusOutputStream sensorStatusStream;
	
	//the attribut that changes each run
	private TimerEvent timerEvent;
	
	public ReplayWorker(Algorithm alg, SensorStatusHistory ssHist,
			SensorStatusOutputStream sensorStatusStream) {
		super();
		this.alg = alg;
		this.ssHist = ssHist;
		this.sensorStatusStream = sensorStatusStream;
		
	}

	public void setTimerEvent(TimerEvent e){
		this.timerEvent = e;
	}
	
/*	@Override
	public void run() {
		
			Iterator<SensorStatusEvent> ssit = ssHist.elementIterator(alg, timerEvent);
			//iterate all sensor status events
			while(ssit.hasNext()){
				SensorStatusEvent sensorStatusEvent = ssit.next();
				sensorStatusStream.write(alg, sensorStatusEvent);
			}
		
	}
*/
	@Override
	public Object call() throws Exception {
		Iterator<SensorStatusEvent> ssit = ssHist.elementIterator(alg, timerEvent);
		//iterate all sensor status events
		while(ssit.hasNext()){
			sensorStatusStream.write(alg, ssit.next());
		}
		
		return null;
	}
	
}
}
