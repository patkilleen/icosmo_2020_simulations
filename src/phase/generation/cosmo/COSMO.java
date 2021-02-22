package phase.generation.cosmo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Sensor;
import common.Vehicle;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.log.Logger;
import common.log.LoggerFactory;
import phase.generation.cosmo.Histogram.DistanceMeasure;

public class COSMO extends AnomalyDetectionAlgorithm implements Serializable{

	
	public enum CosmoPhase{SENSOR_SELECTION_DAY1,SENSOR_SELECTION_DAY2,MODEL_GATHERING_WEEK1,DEVIATION_DETECTION};
	
	private CosmoPhase state;
	
	private List<Vehicle> vehicles;
	
	private Histogram.DistanceMeasure distanceMeasure;
	
	private int numSensorSelect;
	
	//below are field to optimizae avoiding heap garbage colleciton when possible
	//transient, so it's not saved in hisotyr
	transient private DistanceMatrix matrix;
	transient private List<SensorStatusEvent> sensorStatusBuffer; 
	
	transient private int zscoreUpdateFrequency;
	
	transient private int week1DailyTimeCounter;
	
	transient private List<HistogramVehiclePair> allModels;
	transient private List<HistogramVehiclePair> targetModels;
	
	transient private double [] rowDistances;
	/*
	 * TODO: create a histogram map, so sensor and vehicle can fetch the histogram
	 * 
	 * phase 1: initialization pahse, the sensor data gatered, histograms all create form datasets, and after 2 days, average # bins for sensor isntance are used
	 * to create permanenet histogram (equal bin length) for all instances
	 * 
	 * maybe don't need to worry about different bin legnth: look into it in cosmo paper
	 * 
	 * phase 2: fill every day the histograms, then calculate zscores... etc.
	 */
	
	/**
	 * 
	 * @param id id of the algorithm 
	 * @param vehicles the vehicles in fleet
	 * @param distanceMeasure the enumeration representing which histogram distance measure to use
	 * @param numSensorsSelect the number of sensors that will be selected
	 * @param zscoreUpdateFrequency the frequency, in number of days, that the models are used to compute the zscores using Most central pattern method
	 */
	public COSMO(int id, List<Vehicle> vehicles,Histogram.DistanceMeasure distanceMeasure, int numSensorSelect, 
			int zscoreUpdateFrequency,String name) {
		super(id,name);
		
		init(id,vehicles,distanceMeasure,numSensorSelect,zscoreUpdateFrequency);
		
	}
	
	public COSMO(int id, List<Vehicle> vehicles,Histogram.DistanceMeasure distanceMeasure, int numSensorSelect, 
			int zscoreUpdateFrequency) {
		this(id,vehicles,distanceMeasure,numSensorSelect,zscoreUpdateFrequency,distanceMeasure.toString());
	}

	protected void init(int id, List<Vehicle> vehicles, DistanceMeasure distanceMeasure, int numSensorSelect,
			int zscoreUpdateFrequency) {
		if(vehicles == null || vehicles.isEmpty()){
			throw new ConfigurationException("cannot create COSMO algorithm, since null/empty vehicles argument.");
		}
		if(numSensorSelect <= 0){
			throw new ConfigurationException("cannot create COSMO algorithm, since number of selected sensors isn't positive");
		}
		
		if(zscoreUpdateFrequency < 0){
			throw new ConfigurationException("cannot create COSMO algorithm, since zscore update frequency is negative.");
		}
		this.setId(id);
		state=CosmoPhase.SENSOR_SELECTION_DAY1;
		this.vehicles = vehicles;
		this.distanceMeasure = distanceMeasure;
		this.numSensorSelect = numSensorSelect;
		this.zscoreUpdateFrequency = zscoreUpdateFrequency;
		week1DailyTimeCounter = 0;
		
		sensorStatusBuffer = new ArrayList<SensorStatusEvent>(64 * vehicles.size());
		
	}

	/**
	 * empty constructor for subclass flexibility
	 */
	protected COSMO(){
		super();
	}
	
	public List<Vehicle> getVehicles(){
		return this.vehicles;
	}

	public Histogram.DistanceMeasure getDistanceMeasure() {
		return distanceMeasure;
	}

	public int getNumSensorSelect() {
		return numSensorSelect;
	}

	public int getZscoreUpdateFrequency() {
		return zscoreUpdateFrequency;
	}

	protected CosmoPhase getState(){
		return this.state;
	}
	/**
	 * Hook for subclasses to override. Called before time tick logic occurs (before the day starts)
	 * 
	 * When gathering weekly histograms for zscore computations, clear the next weekly histograms to fill for
	 * the current day.
	 * @param e Timer event
	 */
	public void preTimeTick(TimerEvent e){
			
		//ready to fill weekly histograms to compute the daily zscores?
		if(week1DailyTimeCounter > this.zscoreUpdateFrequency){
				
			int time = e.getTime();
			//clear the weekly histogram of today
			clearWeeklyHistograms(time);
			
		}else if((state == CosmoPhase.MODEL_GATHERING_WEEK1)){ //gathering daily models for first week? 
			
			if((week1DailyTimeCounter == this.zscoreUpdateFrequency)){ //done gathering the 1st set of weekly models?
				state = CosmoPhase.DEVIATION_DETECTION;
			
			}//end if completed MODEL_GATHERING_WEEK1 phase
				
			week1DailyTimeCounter++;	
		}	
		
	}
	
	

	public void processSensorReading(int time, COSMOSensorInstance i, double reading) {
		//store data in model
		Model model = i.getModel();
		
		int histIx = time % model.numberOfWeeklyHistograms();
		//note that the index isn't used for the 2 daily histograms
		model.write(histIx,reading);	
		
	}

	/**
	 * Hook for subclasses to override. Called after the sensor readings were processed but before
	 * the sensor statuses are output to the sensorStatusOutputStream.
	 * 
	 * Computes the zscores for the day, returning sensor statuses event to represent the changes to sensor intances.
	 * @param e Timer event
	 * @return The sensor statuses created from the post processing of sensor readings.
	 */
	public List<SensorStatusEvent> postProcessSensorReading(TimerEvent e){
		
		List<SensorStatusEvent> res = new ArrayList<SensorStatusEvent>(0);
		if(state == CosmoPhase.DEVIATION_DETECTION){
			res = computeDailyZScores( e.getTime());
		}
		
		return res;
	}

	
	/**
	 * Hook for subclasses to override. Called after time tick logic occurs (before the next day starts, after the time tick processing)
	 * 
	 * in the sensor selection phase, flushes the proper histograms and selects sensors after the 2nd day.
	 * @param e Timer event
	 */
	public void postTimeTick(TimerEvent e){
		
		if( state == CosmoPhase.SENSOR_SELECTION_DAY1){
			
			flushAllDailyConcecutiveHistograms();
			state = COSMO.CosmoPhase.SENSOR_SELECTION_DAY2;

		}else if( state == CosmoPhase.SENSOR_SELECTION_DAY2){
			flushAllDailyConcecutiveHistograms();
			
			//no longer need the raw reading arrays, so can remove them
			removeAllRawReadingBuffers();
			
			//go ahead configure the final histogram for each 
			//sensor instance, by computing average bin sizes
			createWeeklyHistograms();
			
			//select most interesting sensors
			sensorSelection();
			
			state = COSMO.CosmoPhase.MODEL_GATHERING_WEEK1;
			
		}
		
	}
	
	/**
	 * Removes all the raw reading data buffers for each model of the sensor instances.
	 */
	protected void removeAllRawReadingBuffers() {
		List<Sensor> sensorClasses = this.getSensors();
		
		
		//iterate the sensor classes
		for(Sensor s: sensorClasses){
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);

			for(COSMOSensorInstance i : instances){
				Model model = i.getModel();
				model.deleteRawReadings();
			}
			
		}//end iterate sensor classes
		
		//suggest to JVM to garbage collect
		System.gc();
	}



	
	

	/**
	 * updates the zvalues of sensor isntances using MCP and returns the sensor statuses reflecting the changes.
	 * 
	 * the models used as the target models are the models gathered today, which will be compared to
	 * all the other models of the week.
	 * 
	 * @param time the time representing which weekly histogram to use to compute the zscores
	 * @return sensor status events that represent the state of the changed sensor instances
	 */
	private List<SensorStatusEvent> computeDailyZScores(int time) {
	
		
		List<Sensor> sensorClasses = this.getSensors();
		List<Vehicle> vehicles = this.getVehicles();
		sensorStatusBuffer.clear();
		
		//iterate the sensor classes
		for(Sensor s: sensorClasses){
			

			//get all histograms of the current sensor class
			//List<HistogramVehiclePair> allModels = getAllWeeklyModels(s);
			getAllWeeklyModels(s);
			
			//get only the histograms of today (were using them to compute zscore of today)
			//List<HistogramVehiclePair> targetModels = getWeeklyModels(s,time);
			getWeeklyModels(s,time);
			
			//create distance matrix using all models (note the target models are included in this)
			//DistanceMatrix matrix = buildMatrix(allModels);
			buildMatrix(allModels);
						
			
			//for each target model, compute zscore
			for(HistogramVehiclePair model : targetModels){
				Histogram hist = model.histogram;
				COSMOSensorInstance sensorInstance = model.sensorInstance;
				
				
				double zvalue = matrix.mostCentralPattern(hist);
				
				sensorInstance.addZvalue(zvalue);
				
				//compute zscore (moving average of zvalues)
				double zscore = sensorInstance.computeZScore();
					
				SensorStatusEvent outputEvent = new SensorStatusEvent(sensorInstance.getVehicle(),sensorInstance,this,zscore,sensorInstance.isCOSMOSensor(),zvalue);
				
				sensorStatusBuffer.add(outputEvent);
			}
		}//end iterate sensor classes
		
		return sensorStatusBuffer;
	}
	
	/**
	 * Creates a list of all the histograms for all weekly histograms of a sensor class
	 * @param s The sensor class
	 * @return models of each sensor instance
	 */
	protected List<HistogramVehiclePair> getAllWeeklyModels(Sensor s) {
		List<COSMOSensorInstance> instances = this.getSensorInstances(s);
		
		if(allModels == null){
			allModels = new ArrayList<HistogramVehiclePair>(instances.size() * this.zscoreUpdateFrequency);
		}else{
			allModels.clear();
		}
		//there are X models per vehicle, where X is zscoreUpdateFrequency
		//List<HistogramVehiclePair> models = new ArrayList<HistogramVehiclePair>(instances.size() * this.zscoreUpdateFrequency);
		
		//iterate all models for each vehicle, to create the distance matrix for sensor s
		for(COSMOSensorInstance i : instances){
			Model model = i.getModel();
			
			
			//iterate the weekly histograms
			for(Histogram hist : model.getWeeklyHistograms()){
				HistogramVehiclePair pair = new HistogramVehiclePair(i,hist);
				
				allModels.add(pair);
			}//end iterate weekly hist
		}//end iterate vehicle models
		
		return allModels;
	}

	
	/**
	 * Creates a list of the histograms for a specific day of a sensor class
	 * @param s The sensor class
	 * @return models of each sensor instance
	 */
	protected List<HistogramVehiclePair> getWeeklyModels(Sensor s, int time) {
		List<COSMOSensorInstance> instances = this.getSensorInstances(s);
		
		if(targetModels == null){
			targetModels = new ArrayList<HistogramVehiclePair>(instances.size());
		}else{
			targetModels.clear();
		}
		
		//there are X models per vehicle, where X is zscoreUpdateFrequency
		//List<HistogramVehiclePair> models = new ArrayList<HistogramVehiclePair>(instances.size());
		
		//iterate all models for each vehicle, to create the distance matrix for sensor s
		for(COSMOSensorInstance i : instances){
			Model model = i.getModel();
			
			//get histogram of day 'time'
			int histIx = time % model.numberOfWeeklyHistograms();
			
			List<Histogram> weeklyHists = model.getWeeklyHistograms(); 
			Histogram hist = weeklyHists.get(histIx);
			
			HistogramVehiclePair pair = new HistogramVehiclePair(i,hist);
				
			targetModels.add(pair);
			
		}//end iterate vehicle models
		
		return targetModels;
	}

	
	/**
	 * Builds a distance matrix with all the distances between provided models.
	 * @param models Models used to compute distances to populate matrix
	 * @return matrix of distances
	 */
	protected DistanceMatrix buildMatrix(List<HistogramVehiclePair> models) {
		
		//not already built?
		if(matrix == null){
			matrix = new DistanceMatrix(models.size());
		}else{
			matrix.clear();//no need to re allocate, always a matrix of same size
		}
		//DistanceMatrix matrix = new DistanceMatrix(models.size());
		for(HistogramVehiclePair row: models){
			int i = 0;
			//double [] distances = new double[models.size()];
			if(rowDistances == null){
				rowDistances =  new double[models.size()];
			}else{
				for(int k = 0;k<rowDistances.length;k++){
					rowDistances[k] = 0;
				}
			}
			
			Histogram rowHist = row.histogram;
			for(HistogramVehiclePair col: models){
				double dist = 0;
				
				
				Histogram colHist = col.histogram;
				
				//only calculte non-identical histogram distances
				if(col != row){
					
					dist = rowHist.computeDistance(distanceMeasure, colHist);
				}
				
				rowDistances[i] = dist;
				i++;
			}
			
			//add distance cells (row) to matrix
			matrix.addRow(rowHist, row.sensorInstance.getVehicle(), rowDistances);
		}
		
		return matrix;
	}


	/**
	 * Selects the most interesting sensor classes to be part
	 * of cosmo approach's selected sensors.
	 */
	protected void sensorSelection() {
		
		Logger log = LoggerFactory.getInstance();
		
		List<Sensor> sensorClasses = this.getSensors();
		
		List<SensorInterest> candidates = new ArrayList<SensorInterest>(sensorClasses.size());
		
		//iterate the sensor classes
		for(Sensor s: sensorClasses){
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);
			
			//compute average normalized entropy and stability for each sensor class
			double meanNE = 0.0;
			double meanStability = 0.0;
			
			for(COSMOSensorInstance i : instances){
				Model model = i.getModel();
				meanNE += model.computeNormalizedEntropy();
				meanStability = model.computeStability(distanceMeasure);
			}
			
			meanNE = (meanNE/(double)instances.size());
			meanStability = (meanStability/(double)instances.size());
			
			SensorInterest si = new SensorInterest(s,meanNE,meanStability);
			candidates.add(si);
			
		}//end iterate sensor classes
		
		//sort the sensor class interests by most interesting
		Collections.sort(candidates);
		
		//choose the top candidates sensors (most interesting) to add to cosmo model
		for(int j = 0; j <this.numSensorSelect;j++){
			SensorInterest si = candidates.get(j);
			Sensor s = si.getSensor();
			log.log_info("COSMO: ("+this.distanceMeasure.toString()+") selecting sensor: "+s);
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);
			
			//include all snesor instances of most interesting sensor classes in cosmo model (select them)
			for(COSMOSensorInstance i : instances){
				i.setCOSMOSensor(true);
			}
		}
		
	}

	/**
	 * Configures all the sensor instances' models' core histogram 
	 * by computing the average bin size and creating histograms of same-lengthed bins for each sensor instance of a sensor class.
	 */
	protected void createWeeklyHistograms() {
		List<Sensor> sensorClasses = this.getSensors();
		
		//iterate the sensor classes
		for(Sensor s: sensorClasses){
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);
			
			//compute average bin size
			double meanNumberOfBins = 0.0;
			
			for(COSMOSensorInstance i : instances){
				Model model = i.getModel();
				meanNumberOfBins += model.computeMeanNumberOfBins();
			}
			
			//round to nearest int
			meanNumberOfBins = Math.rint(meanNumberOfBins/(double)instances.size());
			
		
			//create final histograms for each instance, sharing the same lengthed histogram (same # bins)
			for(COSMOSensorInstance i : instances){
				
				MinMaxPair minMaxPair = i.getMinMaxPair();
				
				if(minMaxPair == null){
					throw new SimulationException("Cannot create final histograms for COSMO sensor instances, since min-max "
							+ "pairs not set in sensor instances. Check for coding bugs when creating MinMaxPairs in SensorDescription list."
							+ "That is, check the SensorInstanceFactory, it should be creating the min max pairs and assigning it to new instances.");
				}
				Model model = i.getModel();
				model.createFinalHistogram(minMaxPair.getMax(), minMaxPair.getMin(),(int) meanNumberOfBins,this.zscoreUpdateFrequency);
			}
			
		}//end iterate sensor classes
		
	}

	
	/**
	 * For all the vehicles and sensor instances, flush each of their model.
	 */
	public void flushAllDailyConcecutiveHistograms(){
		List<Sensor> sensorClasses = this.getSensors();
		Logger log = LoggerFactory.getInstance();

		//iterate the sensor classes
		for(Sensor s: sensorClasses){
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);
			
			//flush model of each instance
			for(COSMOSensorInstance i : instances){
				Model model = i.getModel();
				
				try{
					model.flush();
				}catch(Exception e){
					
					log.log_error("failed to flush model for sensor instance: "+i);
				
					throw e;
					
				}
			}
		}//end iterate sensor classes
	}
	
	
	/**
	 * For all the vehicles and sensor instances, flush each of their model for the histograms of a specified day.
	 * @param the time unit representing the which day to clear histograms
	 */
	public void clearWeeklyHistograms(int time){
		List<Sensor> sensorClasses = this.getSensors();
		
		//iterate the sensor classes
		for(Sensor s: sensorClasses){
			List<COSMOSensorInstance> instances = this.getSensorInstances(s);
			
			//flush model of each instance
			for(COSMOSensorInstance i : instances){
				Model model = i.getModel();
				
				//compute index of weekly histogram
				int histIx = time % model.numberOfWeeklyHistograms();
				model.clearWeeklyHistogram(histIx);
			}
		}//end iterate sensor classes
	}
	
	protected static class HistogramVehiclePair{
		public COSMOSensorInstance sensorInstance;
		public Histogram histogram;
		public HistogramVehiclePair(COSMOSensorInstance sensorInstance, Histogram histogram) {
			super();
			this.sensorInstance = sensorInstance;
			this.histogram = histogram;
		}
		
		
	}
}
