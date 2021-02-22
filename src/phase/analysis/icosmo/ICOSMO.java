package phase.analysis.icosmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import common.Algorithm;
import common.Sensor;
import common.SensorInstance;
import common.SensorInstanceFactory;
import common.SensorMap;
import common.Vehicle;
import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;

public class ICOSMO  implements SensorInstanceFactory{


	public static final int DEFAULT_SENSOR_CHANGED_RESULT_LIST_SIZE = 8;
	
	private double stalnessThreshold;
	private double candicacyThreshold;
	
	private double contributionDecreaseMod;
	private double contributionIncreaseMod;
	
	private double potentialContrDecreaseMod;
	private double potentialContrIncreaseMod;
	
	private double defaultContribution;
	private double defaultPotentialContribution;
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
	
	
	public ICOSMO( double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution,int numFaultInvolvedSensorEstimation,int zValueWindowSize, int maxNumberAddedSensors, int maxNumberRemovedSensors) {
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

	protected void init(double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution, int numFaultInvolvedSensorEstimation,int zValueWindowSize, int maxNumberAddedSensors, int maxNumberRemovedSensors) {
		
		if( desiredRecall < 0 || desiredRecall > 1 || desiredPrecision < 0 || desiredPrecision > 1 || numFaultInvolvedSensorEstimation <=0 || zValueWindowSize <=0 || maxNumberAddedSensors<0 || maxNumberRemovedSensors< 0){
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
		int numPSensors = (int) Math.floor(desiredRecall * (double)p.size());
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

		//calculate number of actual fault-involved sensors that will be estimated
		//given the desired recall 
		int numPSensors = (int) Math.floor(desiredPrecision * numFaultInvolvedSensorEstimation);
		int numPNotSensors = numFaultInvolvedSensorEstimation - numPSensors;

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
					removeFromCOSMO(alg,faultInvolvedSensor);
					sensorSelectionChange = true;
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
					addToCOSMO(alg,faultInvolvedSensor);
					sensorSelectionChange = true;
				}
			}
			
		}//edn is cosmo sensor
		
		return sensorSelectionChange;
	}//end ajust sensor ranking function
	
	protected void addToCOSMO(Algorithm alg, Sensor sensorClass){
		
		if(sensorMap==null){
			throw new ConfigurationException("cannot add sensor to cosmo selected sensors, icosmo not initialized");
		}
		
		int sensorSelectionCount = this.getSensorSelectionCount(alg);
		//make sure not exceeding limit of sensor selection changes (can't add more cosmo sensors than max)
		if(sensorSelectionCount >= maxNumberAddedSensors){
			return;
		}
		
		addToCOSMOHelper(alg,sensorClass);
		this.incrementSensorSelectionCount(alg);
		
	}

	protected void addToCOSMOHelper(Algorithm alg, Sensor sensorClass) {
		List<SensorInstance> instances = sensorMap.getSensorInstances(alg,sensorClass);
		//add all sensor instances of given class to cosmo selected sensors
		for(SensorInstance _s : instances){
					
			ICOSMOSensorInstance s =(ICOSMOSensorInstance) _s;
			
			//already a cosmo sensor?
			if(s.isCosmoSensor()){
				return;//do nothing, all other instance are cosmo sensor as well
			}
			
			s.setCosmoSensor(true);
		//	throw new IllegalStateException("not done");
			//s.setHasSensorSelectionChanged(true);//this won't be required, just clear zvalue queue in instance 
			s.setContribution(defaultContribution);
			s.setPotentialContribution(defaultPotentialContribution);
		}
	}

	protected void removeFromCOSMO(Algorithm alg, Sensor sensorClass){
		
		if(sensorMap==null){
			throw new ConfigurationException("cannot remove sensor from cosmo selected sensors, icosmo not initialized");
		}
		
		int sensorSelectionCount = this.getSensorSelectionCount(alg);
		//make sure not exceeding limit of sensor selection changes (can't add more cosmo sensors than max)
		if(sensorSelectionCount <= (-maxNumberRemovedSensors)){
			return;
		}
		
		removeFromCOSMOHelper(alg,sensorClass);
		
		this.decrementSensorSelectionCount(alg);
	}

	protected void removeFromCOSMOHelper(Algorithm alg, Sensor sensorClass) {
		List<SensorInstance> instances = sensorMap.getSensorInstances(alg,sensorClass);
		//remove all sensor instances of given class from cosmo selected sensors
		for(SensorInstance _s : instances){
			ICOSMOSensorInstance s = (ICOSMOSensorInstance) _s;
			
			//already a non-cosmo sensor?
			if(!s.isCosmoSensor()){
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

