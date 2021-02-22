package common.synchronization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import common.RandomNumberStream;
import common.Timer;
import common.Util;
import common.event.TimerEvent;
import common.exception.ConfigurationException;

/**
 * 
 * @author Not admin
 *
 * @param <V> The type of object that will be used to created threads for each unique object
 */
public class MultiThreadedTimer <V> extends Timer {

	/**
	 * List of threads for each simulation partition.
	 */
	
	private ExecutorService executorService;
	/**
	 * List of vehicle objects specific to this timer.
	 */
	private List<V> threadPartitionKeys;
	
	private List<Callable<Object>> workers;
	
	
	private HashMap<V,RandomNumberStream> randomNumberStreams;
	
	
	//private HashMap<V,Random> randomNumberGenerators;
	
	public MultiThreadedTimer(List<V> threadPartitionKeys) {
		setParitionKeys(threadPartitionKeys);
		initRandomGenerators();
		
	}
	
	public MultiThreadedTimer(List<V> threadPartitionKeys, long randomNumberStreamSize) {
		setParitionKeys(threadPartitionKeys);
		initRandomGenerators(randomNumberStreamSize);	
	}
	
	/**
	 * Empty constructor to provide flexibility to sub classes
	 */
	protected MultiThreadedTimer(){
		super();
	}
	
	public void initRandomGenerators(){
		randomNumberStreams = new HashMap<V,RandomNumberStream>();
		
		//iterate all keys and create hashmap entry
		for(V key: threadPartitionKeys){
			
			RandomNumberStream keyUniqueStream = new RandomNumberStream();
			randomNumberStreams.put(key,keyUniqueStream);
			
		}
	}
		
	public void initRandomGenerators(long randomNumberStreamSize){
		randomNumberStreams = new HashMap<V,RandomNumberStream>();
		
		//iterate all keys and create hashmap entry
		for(V key: threadPartitionKeys){
			
			RandomNumberStream keyUniqueStream = new RandomNumberStream(randomNumberStreamSize);
			randomNumberStreams.put(key,keyUniqueStream);
			
		}
	}
	
	/*
	private void initRandomGenerators(){
		randomNumberGenerators = new HashMap<V,Random>();
		
		//iterate all keys and create hashmap entry
		for(V key: threadPartitionKeys){
			
			Random rand = new Random();
			randomNumberGenerators.put(key,rand);
			
		}
	}*/

	/**
	 * Generates a random event with a specific probability. Returns true when the event occurs, and false otherwise.  
	 * @param eventProbability The probability of the event occuring.
	 * @return True when the event occurs, false when it doesn't occur.
	 */
	public boolean randomEventOccured(V partitionKey,double eventProbability){
		
		//generate random number uniformly between (0,1), excluding 0 and 1
		//double r = randomizer.nextDouble();
		double r = nextRandomNumber(partitionKey);
		return this.randomEventOccured(eventProbability,r);
	}
	
	/**
	 * Generates a random event with a specific probability. Returns true when the event occurs, and false otherwise.  
	 * @param eventProbability The probability of the event occuring.
	 * @return True when the event occurs, false when it doesn't occur.
	 */
	public boolean randomEventOccured(double eventProbability, double randomNumber){

		//event occured ?
		if(randomNumber <= eventProbability){
			return true;
		}else{
			return false;
		}
	}

	
	/**
	 * Returns a random value between 0 and 1.
	 * @param partitionKey the vehicle used as keyto o reference stream of random numbers to access
	 * @return random value between 0 and 1
	 */
	public double nextRandomNumber(V partitionKey){
		
			RandomNumberStream stream = randomNumberStreams.get(partitionKey);
			return stream.nextRandomNumber();
	}

	
	protected void setParitionKeys(List<V> threadPartitionKeys){
		if(threadPartitionKeys == null || threadPartitionKeys.isEmpty()){
			throw new ConfigurationException("Failed to create timer due to null thread partition key list");
		}
		
		
		//make sure the keys are unique (otherwise threading issues if 2 threads have same key)
		if(!Util.allElementsUnique(threadPartitionKeys)){
			throw new ConfigurationException("Failed to create timer due to duplicate partition keys");
		}
		
		//threads = new Thread[threadPartitionKeys.size()];
		this.threadPartitionKeys = threadPartitionKeys;
		
		executorService = Executors.newFixedThreadPool(threadPartitionKeys.size());
		
		workers = new ArrayList<Callable<Object>>(threadPartitionKeys.size());
		
		for(V key : threadPartitionKeys){
			workers.add(new TimerWorker<V>(this,key,null));
		}
	}


	
	public List<V> getThreadPartitionKeys(){
		return threadPartitionKeys;
	}
	
	/**
	 * The hook function to be implemented by subclasses, giving access to a thread unique for each vehicle.
	 * @param e The time event that triggered this timer's tick which started each vehicle thread. 
	 * @param v The vehicle associated to the thread.
	 */
	protected void threadTick(TimerEvent e ,V v){
		//hook to be implemeted by subclassses
	}
	
	@Override
	protected void tick(TimerEvent e) throws InterruptedException{

	
		for(int i = 0;i < workers.size();i++){
			TimerWorker<V> w = (TimerWorker<V>)workers.get(i);
			
			w.setTimerEvent(e);
		}
		
		List<Future<Object>> taskCompletionStatus = executorService.invokeAll((Collection<? extends Callable<Object>>) workers);
		
		for(Future<Object> status : taskCompletionStatus){
			if(!status.isDone()){
				throw new IllegalStateException("failed to execute one of the timer tick threads");
			}
		}
	}
	public void delete(){
		executorService.shutdownNow();
		workers = null;
		executorService=null;
	}
	/**
	 * Class that is used to call the vehicleTick method of VehicleTimer objects in a thread for each vehicle.
	 * @author Not admin
	 *
	 * @param <V> Type of threading object.
	 */
	private static class TimerWorker<V> implements Callable<Object>,Runnable{

		private MultiThreadedTimer<V> timer;
		private V threadingObject;
		private TimerEvent event;
		public TimerWorker(MultiThreadedTimer<V> timer,V vehicle, TimerEvent event){
			this.timer = timer;
			this.threadingObject = vehicle;
			this.event=event;
		}

		@Override
		public void run() {
			//timer.threadTick(event,threadingObject);

		}

		
		public void setTimerEvent(TimerEvent e){
			this.event = e;
		}

		@Override
		public Object call() throws Exception {
			timer.threadTick(event,threadingObject);
			return null;
		}
	}
	

}
