package phase.analysis.icosmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cern.jet.random.Uniform;
import cern.jet.random.engine.DRand;
import common.Algorithm;
import common.Sensor;
import common.SensorInstance;
import common.SensorInstanceFactory;
import common.SensorMap;
import common.Vehicle;
import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;
import phase.generation.cosmo.AnomalyDetectionAlgorithm;
import phase.generation.cosmo.SensorInterest;

public class ICOSMO  implements SensorInstanceFactory{

	private static final double SLEEP_INTERVAL = 0.1;//sleep for up to 0.1 seconds for rng seed 
	
	public static final int DEFAULT_SENSOR_CHANGED_RESULT_LIST_SIZE = 8;
	
	private double stalnessThreshold;
	private double candicacyThreshold;
	
	private double contributionDecreaseMod;
	private double contributionIncreaseMod;
	
	private double potentialContrDecreaseMod;
	private double potentialContrIncreaseMod;
	
	private double defaultContribution;
	private double defaultPotentialContribution;
	
	private double sensorInterestThreshold;
	
	/*
	 *  The desired recall the set will have with respect to estimating fault-involved sensors
	 */
	private double desiredRecall;
	
	/*
	 * The desired precision the set will have with respect to estimating fault-involved sensors
	 */
	private double desiredPrecision;
	
	/*
	 * The number of sensors estimated in information retrieval algorithm (recall and precision)
	 */
	private int numFaultInvolvedSensorEstimation;
	
	/*
	 * the maximum relative number of sensors that can be added as selected sensors 
	 */
	private int maxNumberAddedSensors;
	
	/*
	 * the max relative number of sensors that can be removed from selected sensors 
	 */
	private int maxNumberRemovedSensors;
	

	/*
	 * the number of sensors added (positive) or removed (negative) from cosmo selected sensors
	 */
	
	private HashMap<Algorithm, IncrementableInteger> sensorSelectionChangesMap;
	
	private int zValueWindowSize;

	public enum Action{NO_ACTION,REMOVE_COSMO_SENSOR,ADD_COSMO_SENSOR}


	private SensorMap sensorMap;
	
	//random number generator
	private Uniform randomUniform;
	
	public ICOSMO( double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution,int numFaultInvolvedSensorEstimation,int zValueWindowSize, double sensorInterestThreshold,int maxNumberAddedSensors, int maxNumberRemovedSensors) {
		super();
		init( stalnessThreshold,  candicacyThreshold,  contributionDecreaseMod,
				 contributionIncreaseMod,  potentialContrDecreaseMod,  potentialContrIncreaseMod, desiredRecall, desiredPrecision,
				 defaultContribution,  defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,sensorInterestThreshold,maxNumberAddedSensors,maxNumberRemovedSensors);
	}

	/**
	 * constructor without the sensorInterestThreshold parameter to satisfy test cases
	 * @param stalnessThreshold
	 * @param candicacyThreshold
	 * @param contributionDecreaseMod
	 * @param contributionIncreaseMod
	 * @param potentialContrDecreaseMod
	 * @param potentialContrIncreaseMod
	 * @param desiredRecall
	 * @param desiredPrecision
	 * @param defaultContribution
	 * @param defaultPotentialContribution
	 * @param numFaultInvolvedSensorEstimation
	 * @param zValueWindowSize
	 * @param maxNumberAddedSensors
	 * @param maxNumberRemovedSensors
	 */
	public ICOSMO( double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution,int numFaultInvolvedSensorEstimation,int zValueWindowSize,int maxNumberAddedSensors, int maxNumberRemovedSensors) {
		super();
		init( stalnessThreshold,  candicacyThreshold,  contributionDecreaseMod,
				 contributionIncreaseMod,  potentialContrDecreaseMod,  potentialContrIncreaseMod, desiredRecall, desiredPrecision,
				 defaultContribution,  defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,maxNumberAddedSensors,maxNumberRemovedSensors);
	}
	/**
	 * empty constructor for subclass flexibility
	 */
	protected ICOSMO(){
		
	}
	
	
	public HashMap<Algorithm, IncrementableInteger> getSensorSelectionChangesMap() {
		return sensorSelectionChangesMap;
	}

	public int getzValueWindowSize() {
		return zValueWindowSize;
	}

	public double getContributionDecreaseMod() {
		return contributionDecreaseMod;
	}

	public double getContributionIncreaseMod() {
		return contributionIncreaseMod;
	}

	public double getPotentialContrDecreaseMod() {
		return potentialContrDecreaseMod;
	}

	public double getPotentialContrIncreaseMod() {
		return potentialContrIncreaseMod;
	}

	public double getDefaultContribution() {
		return defaultContribution;
	}

	public double getDefaultPotentialContribution() {
		return defaultPotentialContribution;
	}

	public double getDesiredRecall() {
		return desiredRecall;
	}

	public double getDesiredPrecision() {
		return desiredPrecision;
	}

	public int getNumFaultInvolvedSensorEstimation() {
		return numFaultInvolvedSensorEstimation;
	}

	public SensorMap getSensorMap() {
		return sensorMap;
	}

		
	public int getMaxNumberAddedSensors() {
		return maxNumberAddedSensors;
	}

	public int getMaxNumberRemovedSensors() {
		return maxNumberRemovedSensors;
	}
	
	
	public double getStalnessThreshold() {
		return stalnessThreshold;
	}


	public void setStalnessThreshold(double stalnessThreshold) {
		this.stalnessThreshold = stalnessThreshold;
	}


	public double getCandicacyThreshold() {
		return candicacyThreshold;
	}


	public void setCandicacyThreshold(double candicacyThreshold) {
		this.candicacyThreshold = candicacyThreshold;
	}
	
	public void setSensorInterestThreshold(double t) {
		if(t < 0 || t >1) {
			throw new IllegalArgumentException("threshold expected between 0 and 1 but was "+t);
		}
		sensorInterestThreshold = t;
		
	}
	
/**
 * Constructor without the _sensorInterestThreshold argument to make the test cases satisfied (default value used_)
 * @param stalnessThreshold
 * @param candicacyThreshold
 * @param contributionDecreaseMod
 * @param contributionIncreaseMod
 * @param potentialContrDecreaseMod
 * @param potentialContrIncreaseMod
 * @param desiredRecall
 * @param desiredPrecision
 * @param defaultContribution
 * @param defaultPotentialContribution
 * @param numFaultInvolvedSensorEstimation
 * @param zValueWindowSize
 * @param maxNumberAddedSensors
 * @param maxNumberRemovedSensors
 */
	protected void init(double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution, int numFaultInvolvedSensorEstimation,int zValueWindowSize,int maxNumberAddedSensors, int maxNumberRemovedSensors) {
		
		double _sensorInterestThreshold=0;
		init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod,
				contributionIncreaseMod, potentialContrDecreaseMod, potentialContrIncreaseMod,desiredRecall,desiredPrecision,
				defaultContribution, defaultPotentialContribution, numFaultInvolvedSensorEstimation,zValueWindowSize,_sensorInterestThreshold,  maxNumberAddedSensors,  maxNumberRemovedSensors);

	}
	protected void init(double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution, int numFaultInvolvedSensorEstimation,int zValueWindowSize,double sensorInterestThreshold, int maxNumberAddedSensors, int maxNumberRemovedSensors) {
		
		if( desiredRecall < 0 || desiredRecall > 1 || desiredPrecision < 0 || desiredPrecision > 1 || numFaultInvolvedSensorEstimation <=0 || zValueWindowSize <=0 || maxNumberAddedSensors<0 || maxNumberRemovedSensors< 0 || sensorInterestThreshold < 0 || sensorInterestThreshold > 1){
			throw new ConfigurationException("cannot create icosmo instance, illegal argument, either out of bounds or null.");
		}
		this.stalnessThreshold = stalnessThreshold;
		this.candicacyThreshold = candicacyThreshold;
		this.contributionDecreaseMod = contributionDecreaseMod;
		this.contributionIncreaseMod = contributionIncreaseMod;
		this.potentialContrDecreaseMod = potentialContrDecreaseMod;
		this.potentialContrIncreaseMod = potentialContrIncreaseMod;
		this.desiredRecall = desiredRecall;
		this.desiredPrecision = desiredPrecision;
		this.defaultContribution = defaultContribution;
		this.defaultPotentialContribution = defaultPotentialContribution;
		this.numFaultInvolvedSensorEstimation = numFaultInvolvedSensorEstimation;
		this.zValueWindowSize = zValueWindowSize;
		this.maxNumberAddedSensors = maxNumberAddedSensors;
		this.maxNumberRemovedSensors = maxNumberRemovedSensors;
		this.sensorInterestThreshold = sensorInterestThreshold;
		
		
		DRand uniformRandomEngine = null;
		
		//sleep for random duration so the seed is random
		try{Thread.sleep((long)(Math.random() * SLEEP_INTERVAL));}catch(InterruptedException e){}
		
		uniformRandomEngine = new DRand(new Date());
		randomUniform = new Uniform(0,1,uniformRandomEngine);
	}

	/**
	 * initializes the sensor map and the sensor selection changes map
	 * @param icosmoMap initialized sensor map
	 */
	public void init(SensorMap icosmoMap){
		this.sensorMap = icosmoMap;
		
		sensorSelectionChangesMap = new  HashMap<Algorithm, IncrementableInteger>();
		
		List<Algorithm> algs = sensorMap.getAlgorithms();
		
		for(Algorithm alg: algs){
			sensorSelectionChangesMap.put(alg, new IncrementableInteger());
		}
		
	}
	

	private void incrementSensorSelectionCount(Algorithm alg){
		if(alg == null){
			throw new ConfigurationException("algorithm was null, cannot increment number of cosmo sensors added.");
		}
		
		IncrementableInteger i = sensorSelectionChangesMap.get(alg);
		
		i.increment();
	}
		
	private void decrementSensorSelectionCount(Algorithm alg){
		if(alg == null){
			throw new ConfigurationException("algorithm was null, cannot increment number of cosmo sensors added.");
		}
		
		IncrementableInteger i = sensorSelectionChangesMap.get(alg);
		
		i.decrement();
	}
	
	public void resetSensorSelectionChangesMap(){
		List<Algorithm> algs = sensorMap.getAlgorithms();
		
		for(Algorithm alg: algs){
			IncrementableInteger i = sensorSelectionChangesMap.get(alg);
		
			i.reset();//set value back to 0
		}
		
	}
	private int getSensorSelectionCount(Algorithm alg){
		if(alg == null){
			throw new ConfigurationException("algorithm was null, cannot increment number of cosmo sensors added.");
		}
		
		IncrementableInteger i = sensorSelectionChangesMap.get(alg);
		return i.value();
	}
	
	protected static List<Sensor> iraHelper(List<Sensor> p, List<Sensor> pNot, int numPSensors, int numPNotSensors){
		int n = numPSensors + numPNotSensors;
		//estimate set of fault-involved sensors
		List<Sensor> x = new ArrayList<Sensor>(n);

		//make deep copy of the lists to shuffle them without affecting original list
		List<Sensor> pShuffled = new ArrayList<Sensor>(p);
		List<Sensor> pNotShuffled = new ArrayList<Sensor>(pNot);
		Collections.shuffle(pShuffled);
		Collections.shuffle(pNotShuffled);

		//add actual fault-involved sensors to result
		for(int i = 0;i<numPSensors;i++){
			Sensor s = pShuffled.get(i);
			x.add(s);
		}

		//add  non-fault-involved sensors to result
		for(int i = 0;i<numPNotSensors;i++){
			Sensor s = pNotShuffled.get(i);
			x.add(s);
		}
		return x;
	}
	/**
	 * Generates a list of estimated fault-involved sensors with a desired recall.
	 * @param p The list of fault-involved sensors
	 * @param pNot The list of non  fault-involved sensors
	 * @return
	 */
	public List<Sensor> iraRecall(List<Sensor> p, List<Sensor> pNot){

		//calculate number of actual fault-involved sensors that will be estimated
		//given the desired recall 
		int numPSensors = 0;
		
		double quotient = desiredRecall * (double)p.size();
		
		//does the recall perfectly partition the set of fault invovled sensors?
		//e.g., 0.3 recall, with 100 fault invnovled sensors would lead to 30
		//but 0.3 recall and 99 sensors woudl lead to 29.7, which isn't an int		
		if ((quotient % 1) == 0) {
			numPSensors = (int) quotient;//simple case where partition is perfect size
		}else {
			
			// in the case where we have a fraction of a fault-inovled sensor
			//we add all sensors (29 in the above example), and then add the last sensor
			//with some probability (0.7 in above example)
			numPSensors = (int) Math.floor(quotient);
			
			//pvalue of adding the extra fault invovled sensor
			double pvalue = quotient- numPSensors;
			
			double rng = randomUniform.nextDouble();
			
			if(rng >= pvalue){
				numPSensors++;
			}
		}
		
		int numPNotSensors = numFaultInvolvedSensorEstimation - numPSensors;

		return iraHelper(p,pNot,numPSensors,numPNotSensors);
	}

	/**
	 * Generates a list of estimated fault-involved sensors with a desired precision.
	 * @param p The list of fault-involved sensors
	 * @param pNot The list of non  fault-involved sensors
	 * @param n Number of sensors to return in estimation set.
	 * @return
	 */
	public List<Sensor> iraPrecision(List<Sensor> p, List<Sensor> pNot){

		//as precision has to do with the size of the dataset returned, we have to avoid
		//the case when the number of fault invovled sensors desired is larger than the number
		//of fauult invovled sensors
		int _numFaultInvolvedSensorEstimation = Math.min(p.size(), numFaultInvolvedSensorEstimation);
		//calculate number of actual fault-involved sensors that will be estimated
		//given the desired recall 
		//int numPSensors = (int) Math.floor(desiredPrecision * _numFaultInvolvedSensorEstimation);
		

		//calculate number of actual fault-involved sensors that will be estimated
		//given the desired recall 
		int numPSensors = 0;
		
		double quotient = desiredPrecision * _numFaultInvolvedSensorEstimation;
		
		//does the recall perfectly partition the set of fault invovled sensors?
		//e.g., 0.3 recall, with 100 fault invnovled sensors would lead to 30
		//but 0.3 recall and 99 sensors woudl lead to 29.7, which isn't an int		
		if ((quotient % 1) == 0) {
			numPSensors = (int) quotient;//simple case where partition is perfect size
		}else {
			
			// in the case where we have a fraction of a fault-inovled sensor
			//we add all sensors (29 in the above example), and then add the last sensor
			//with some probability (0.7 in above example)
			numPSensors = (int) Math.floor(quotient);
			
			//pvalue of adding the extra fault invovled sensor
			double pvalue = quotient- numPSensors;
			
			double rng = randomUniform.nextDouble();
			
			if(rng >= pvalue){
				numPSensors++;
			}
		}
		
		
		int numPNotSensors = _numFaultInvolvedSensorEstimation - numPSensors;

		return iraHelper(p,pNot,numPSensors,numPNotSensors);
	}
	


	/**
	 * Adjusts the ranking of the fault-involved sensor.
	 * @param faultDetected Flag, indicating atleast one sensor detected the fault when true, and false when no sensor detected the fault. 
	 * @param faultInvolvedSensor The fault-involved sensor.
	 * @param sensorDeviationOccured Flag indicating whether this sensor deviated due to the fault when true, and false otherwise.
	 */
	public boolean adjustSensorRanking(Algorithm alg, boolean faultDetected, ICOSMOSensorInstance faultInvolvedSensor,
			boolean sensorDeviationOccured) {
		if(faultInvolvedSensor == null){
			throw new ConfigurationException("cannot adjust sensor rank of null sensor instance");
		}
		boolean sensorSelectionChange = false;
		//cosmo sensro?
		if(faultInvolvedSensor.isCosmoSensor()){
			
			double contribution = faultInvolvedSensor.getContribution();
		
			//deviated due to fault?
			if(sensorDeviationOccured){
				//increase contribution, ability to detect fault
				contribution += contributionIncreaseMod;
				faultInvolvedSensor.setContribution(contribution);
			}else{//failed to detect fault
				//decrease contribution, ability to detect fault
				contribution -= contributionDecreaseMod;
				faultInvolvedSensor.setContribution(contribution);
				//sensor has become stale?
				if(isSensorStale(alg,faultInvolvedSensor)){
					sensorSelectionChange = removeFromCOSMO(alg,faultInvolvedSensor);					 
				}
			}
		
			
		}else{//non-comso sensor
			double potContribution = faultInvolvedSensor.getPotentialContribution();
			
			//did atleast one of the cosmo sensors detect the fault?
			if(faultDetected){
				//this sensor wasn't need, decrease potential contribution
				potContribution -= this.potentialContrDecreaseMod;
				faultInvolvedSensor.setPotentialContribution(potContribution);
			}else{
				//this sensor could potentially help find faults in futre, no cosmo sensor was successful
				//no deviations detecteed
				potContribution += this.potentialContrIncreaseMod;
				faultInvolvedSensor.setPotentialContribution(potContribution);
				//sensor has become a candidate?
				if(isSensorCandidate(alg,faultInvolvedSensor)){
					sensorSelectionChange = addToCOSMO(alg,faultInvolvedSensor);					
				}
			}
			
		}//edn is cosmo sensor
		
		return sensorSelectionChange;
	}//end ajust sensor ranking function
	
	/**
	 * adds a sensor to COSMO sensors, returning true when the sensor was added and false if it wasn't 
	 * due to maximum number of candidate sensor COSMO sensor additions reached  
	 * @param alg
	 * @param sensorClass
	 * @return
	 */
	protected boolean addToCOSMO(Algorithm alg, Sensor sensorClass){
		
		if(sensorMap==null){
			throw new ConfigurationException("cannot add sensor to cosmo selected sensors, icosmo not initialized");
		}
		
		int sensorSelectionCount = this.getSensorSelectionCount(alg);
		//make sure not exceeding limit of sensor selection changes (can't add more cosmo sensors than max)
		if(sensorSelectionCount >= maxNumberAddedSensors){
			return false;
		}
		
		addToCOSMOHelper(alg,sensorClass);
		this.incrementSensorSelectionCount(alg);
		
		return true;
		
	}

	protected void addToCOSMOHelper(Algorithm alg, Sensor sensorClass) {
		List<SensorInstance> instances = sensorMap.getSensorInstances(alg,sensorClass);

		//add all sensor instances of given class to cosmo selected sensors
		for(SensorInstance _s : instances){
					
			ICOSMOSensorInstance s =(ICOSMOSensorInstance) _s;
			
			//already a cosmo sensor?
			if(s.isCosmoSensor()){
				Logger log = LoggerFactory.getInstance();
				log.log_warning("adding a COSMO sensor to COSMO sensors");
				
				//could this happend because 2 repairs happend on same tick involving the same senosr, where a sensor was a candidate
				//then asdded as comso sensor, then next repair also flags it
				//make a) flagged as cadidate and added, then cleared contributions, b) another fault occured and then it was considered stale even if just added? c) then 3rd repair made it candidate again
				//likely only an issue if 1 repair triggers threshold of ICOSMO exceed?
				return;//do nothing, all other instance are cosmo sensor as well
			}
			
		
			s.setCosmoSensor(true);
		//	throw new IllegalStateException("not done");
			//s.setHasSensorSelectionChanged(true);//this won't be required, just clear zvalue queue in instance 
			s.setContribution(defaultContribution);
			s.setPotentialContribution(defaultPotentialContribution);
		}

	}

	/**
	 * removes a sensor from COSMO sensors, returning true when the sensor was removed and false if it wasn't 
	 * due to maximum number of stale COSMO sensor removals reached
	 * @param alg
	 * @param sensorClass
	 * @return
	 */
	protected boolean removeFromCOSMO(Algorithm alg, Sensor sensorClass){
		
		if(sensorMap==null){
			throw new ConfigurationException("cannot remove sensor from cosmo selected sensors, icosmo not initialized");
		}
		
		int sensorSelectionCount = this.getSensorSelectionCount(alg);
		//make sure not exceeding limit of sensor selection changes (can't add more cosmo sensors than max)
		if(sensorSelectionCount <= (-maxNumberRemovedSensors)){
			return false;
		}
		
		removeFromCOSMOHelper(alg,sensorClass);
		
		this.decrementSensorSelectionCount(alg);
		
		return true;
	}

	protected void removeFromCOSMOHelper(Algorithm alg, Sensor sensorClass) {
		List<SensorInstance> instances = sensorMap.getSensorInstances(alg,sensorClass);
		
		//remove all sensor instances of given class from cosmo selected sensors
		for(SensorInstance _s : instances){
			ICOSMOSensorInstance s = (ICOSMOSensorInstance) _s;
			
			//already a non-cosmo sensor?
			if(!s.isCosmoSensor()){
				Logger log = LoggerFactory.getInstance();

				log.log_warning("removing a candidate sensor from COSMO sensors");
				return;//do nothing, all other instance are not a cosmo sensor as well
			}
			
			s.setCosmoSensor(false);
			//throw new IllegalStateException("not done");
			//s.setHasSensorSelectionChanged(true);//this won't be required, just clear zvalue queue in instance
			//s.clearZValues();
			s.setPotentialContribution(defaultPotentialContribution);
			s.setContribution(defaultContribution);
		}
		
	}

	/**
	 * Determines whether the sensor class provided is stale.
	 * @param instances The sensor instances of a sensor class
	 * @return True when stale, false otherwise.
	 */
	public boolean isSensorStale(Algorithm alg, Sensor sensorClass){
		if(sensorMap==null){
			throw new ConfigurationException("cannot check sensor staleness to cosmo selected sensors, icosmo not initialized");
		}
		List<SensorInstance> instances = sensorMap.getSensorInstances(alg,sensorClass);
		//compute average and check thershold
		double sum = 0;
		for(SensorInstance _s : instances){
			ICOSMOSensorInstance s = (ICOSMOSensorInstance) _s;
			sum += s.getContribution();
		}
		
		double mean = sum / (double) instances.size();
		
		return mean <= this.stalnessThreshold;
		
	}
	
	/**
	 * Determines whether the sensor class provided is a candidate.
	@param instances The sensor instances of a sensor class
	 * @return True when a candidate, false otherwise.
	 */
	public boolean isSensorCandidate(Algorithm alg, Sensor sensorClass){
		
		if(sensorMap==null){
			throw new ConfigurationException("cannot check sensor candicacy to cosmo selected sensors, icosmo not initialized");
		}
		
		SensorInterest si = getSensorInterestingness(alg,sensorClass);
		
		if(si==null) {
			Logger log = LoggerFactory.getInstance();

			log.log_error("null sensor interestingness while checking for candidate sensor");
			return false;//do nothing, all other instance are not a cosmo sensor as well
		}
		
		//samller values is more interesting
		double interestingness = si.computeInterestValue();
		
		//sensor isn't interesting enough to consider as candidate?
		if (interestingness > sensorInterestThreshold) {
			return false;
		}
		
		List<SensorInstance> instances = sensorMap.getSensorInstances(alg,sensorClass);
		
		//compute average and check thershold
		double sum = 0;
		for(SensorInstance _s : instances){
			ICOSMOSensorInstance s = (ICOSMOSensorInstance) _s;
			sum += s.getPotentialContribution();
		}
		
		double mean = sum / (double) instances.size();
		
		return mean >= this.candicacyThreshold;
		
	}
	
	/**
	 * REturns the sensor interestginess.
	 * @param alg
	 * @param sensorClass
	 * @return
	 */
	public SensorInterest getSensorInterestingness(Algorithm alg, Sensor sensorClass) {
		
		if(! (alg instanceof AnomalyDetectionAlgorithm)) {
			return null;
		}
		AnomalyDetectionAlgorithm _alg = (AnomalyDetectionAlgorithm) alg;
		SensorMap _sensorMap = _alg.getSensorMap();
		List<SensorInstance> instances = _sensorMap.getSensorInstances(alg,sensorClass);
		
		
		//all instances of a class share the same interest, so just pick first in list
		SensorInstance s = instances.get(0);
		ICOSMOSensorInstance is = (ICOSMOSensorInstance) s;
		return  is.getInterestingness();
		
	}
	/**
	 * Resets the attributes of icosmo sensor instance to default
	 * @param i instance to reset
	 */
	protected void resetSensorInstanceToDefault(ICOSMOSensorInstance i){
		i.setContribution(this.getDefaultContribution());
		i.setPotentialContribution(this.getDefaultPotentialContribution());
		i.setCosmoSensor(false);
		i.clearZValues();
	}
	
	@Override
	public SensorInstance newInstance(Vehicle v, Sensor s) {
		ICOSMOSensorInstance i = new ICOSMOSensorInstance(s,v,zValueWindowSize);
		this.resetSensorInstanceToDefault(i);
		return i;
	}
	
	private static class IncrementableInteger{
		private int i;
		public IncrementableInteger(int i){
			this.i = i;
		}
		public void reset() {
			i=0;		
		}
		public IncrementableInteger(){
			reset();
		}
		public void increment(){
			i++;
		}
		
		public void decrement(){
			i--;
		}
		
		public int value(){
			return i;
		}
	}
}

