package phase.generation.cosmo;

import java.util.ArrayList;
import java.util.List;

import cern.colt.list.DoubleArrayList;
import common.exception.SimulationException;
import phase.generation.cosmo.COSMO.CosmoPhase;

public class Model {

	public final static int SENSOR_READINGS_DEFAULT_SIZE = 256;

	private CosmoPhase state;

	/*
	 * used to accumulate daily data readins
	 */
	private DoubleArrayList sensorReadings;
	/*
	 * histograms below only used to calculate signal interestingnesss
	 */
	private Histogram histoDay1;
	private Histogram histoDay2;

	/*
	 * used to model sensor signal each day
	 */
	private List<Histogram> weeklyHistograms;

	public Model() {
		state=COSMO.CosmoPhase.SENSOR_SELECTION_DAY1;
		sensorReadings = new DoubleArrayList(SENSOR_READINGS_DEFAULT_SIZE);
		//will later be created when data arrives
		histoDay1=null;
		histoDay2=null;

		//will be created after the sensor selection complete
		weeklyHistograms=null;
	}

	public List<Histogram> getWeeklyHistograms(){
		return this.weeklyHistograms;
	}
	
	public int numberOfWeeklyHistograms(){
		
		if( this.weeklyHistograms == null){
			return -1;
		}
		return this.weeklyHistograms.size();
	}
	public double computeMeanNumberOfBins(){
		if(state != COSMO.CosmoPhase.DEVIATION_DETECTION){
			throw new SimulationException("Cannot compute mean bin size, since both daily histograms required, but they aren't available (wrong state).");
		}
		
		double res = histoDay1.size();
		res += histoDay2.size();
		
		return res/2.0;
	}

	/**
	 * Creates the histogram that will be used for all further daily readings.
	 * That is, the deviation detection phase histogram, which will share equal bin length/size 
	 * as all other sensor instances of same class. 
	 * 
	 * All sensor instances of a sensro class will share equally-sized histograms.
	 * 
	 * @param max maximum value of sensor by definition of j1939 specification document
	 * @param min minimum value of sensor by definition of j1939 specification document
	 * @param numberOfBins the number of bins for this type of sensor, throughout the fleet
	 * @param zscoreUpdateFrequency the number of histograms that will be stored before used to compute zscores 
	 */
	public void createFinalHistogram(double max, double min, int numberOfBins, int zscoreUpdateFrequency){
		if(state != COSMO.CosmoPhase.DEVIATION_DETECTION){
			throw new SimulationException("Cannot create final histogram since not in final deviation detection state.");
		}

		//can delete the other histograms at
		weeklyHistograms = new ArrayList<Histogram>(zscoreUpdateFrequency);
		
		//populate the list with empty histograms
		for(int i = 0;i < zscoreUpdateFrequency;i++){
			Histogram h = new Histogram(max,min,numberOfBins);
			weeklyHistograms.add(h);
		}
		
	}


	/**
	 * Returns the normalized entropy of this model (average NE of day1 and day2 histograms).
	 * @return normalized entropy
	 */
	public double computeNormalizedEntropy(){

		//no right phase?
		if(histoDay1 == null || histoDay2 == null){
			throw new SimulationException("Cannot compute normalized entropy since not in final deviation detection state.");
		}

		double ne = histoDay1.normalizedEntropy();
		ne += histoDay2.normalizedEntropy();

		//take average over 2 days
		return ne/2.0;
	}

	/**
	 * Returns the stability (or distance) between both day1 and day2 histograms,
	 * using the distance measure provided
	 * @return
	 */
	public double computeStability(Histogram.DistanceMeasure distanceMeasure){
		//no right phase?
		if(state == COSMO.CosmoPhase.SENSOR_SELECTION_DAY1 ){
			throw new SimulationException("Cannot compute stability since not in final deviation detection state.");
		}

		return histoDay1.computeDistance(distanceMeasure, histoDay2);
	}
	
	/**
	 * Adds a data sample to the current histogram model.
	 * 
	 * @param histIx index of weekly histogram to write to
	 * @param reading The data reading
	 */
	public void write(int histIx, double reading){
		switch(state){
		case SENSOR_SELECTION_DAY1:
		case SENSOR_SELECTION_DAY2:
			//add to readings list to later create the histgoram
			sensorReadings.add(reading);
			break;
		case DEVIATION_DETECTION:
			
			if(histIx < 0 || this.weeklyHistograms == null|| histIx >= this.weeklyHistograms.size()){
				throw new SimulationException("failed to write to weekly histogram at index: "+histIx+", index of of bounds or null weekly array");
			}
			Histogram h = weeklyHistograms.get(histIx);
			//fill histogram
			h.addSample(reading);
			break;
		default:
			throw new SimulationException("illegal state in model, cannot write.");

		}

	}

	public int getNumberRawReadings(){
		return this.sensorReadings.size();
	}
	/**
	 * Flushes the data readings into the proper histograms, depending on the state, and changes state if necessary.
	 * 
	 * For the daily histograms, the readings are used to create the histogram.
	 * 
	 * For the core histogram (final histogram), the histogram is cleared
	 * 
	 * @param time the time stampe of the flush
	 */
	public void flush(){
		switch(state){
		case SENSOR_SELECTION_DAY1:
			histoDay1 = new Histogram(sensorReadings);
			sensorReadings.clear();
			state = COSMO.CosmoPhase.SENSOR_SELECTION_DAY2;
			break;
		case SENSOR_SELECTION_DAY2:
			histoDay2 = new Histogram(sensorReadings);
			sensorReadings.clear();
			state = COSMO.CosmoPhase.DEVIATION_DETECTION;
			break;
		case DEVIATION_DETECTION:
			//this shouldn't be called
			break;
		default:
			throw new SimulationException("illegal state in model, cannot flush.");

		}
	}
	
	
	/**
	 * Removes the references to the raw readings. This can only be done 
	 * in the deviation detection state.
	 */
	public void deleteRawReadings(){
		if(state == COSMO.CosmoPhase.DEVIATION_DETECTION){
			this.sensorReadings = null;
		}else{
			throw new SimulationException("couldn't delete the raw reading array since not in deviation detection state");
		}
	}
	
	/**
	 * Clears weekly histograms at specified index
	 * @param histIx index of histogram to clear
	 */
	public void clearWeeklyHistogram(int histIx){
		if(histIx < 0 || weeklyHistograms == null|| histIx >= this.weeklyHistograms.size()){
			throw new SimulationException("failed to clear weekly histogram at index: "+histIx+", index of of bounds or null array.");
		}
		
		if(weeklyHistograms == null){
			throw new SimulationException("illegal state, cannot clear weekly histograms since they haven't been created yet.");
		}
		Histogram h  = weeklyHistograms.get(histIx);
		
		h.clear();
	}
}
