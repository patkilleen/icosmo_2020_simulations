package phase.configuration.input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;

import common.Algorithm;
import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.SensorBehavior;
import common.Util;
import common.Vehicle;
import common.event.HistoryEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.HistoryInputStream;
import common.event.stream.HistoryOutputStream;
import common.event.stream.HistoryStreamManager;
import common.event.stream.RawSensorDataInputStream;
import common.event.stream.RepairInputStream;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusOutputStream;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import common.exception.XMLFormatException;
import common.log.Logger;
import common.log.LoggerFactory;
import core.GlobalTimer;
import phase.analysis.PerformanceAnalysisTimer;
import phase.analysis.ReplayTimer;
import phase.analysis.icosmo.ICOSMO;
import phase.analysis.icosmo.ICOSMOTimer;
import phase.analysis.output.FileHandler;
import phase.analysis.output.OutputTimer;
import phase.generation.cosmo.AnomalyDetectionAlgorithm;
import phase.generation.cosmo.COSMO;
import phase.generation.cosmo.Histogram;
import phase.generation.cosmo.RandomSensorCOSMO;
import phase.generation.cosmo.SensorDescription;
import phase.generation.data.DataGenerationCOSMOTimer;
import phase.generation.data.DataGenerationSensor;
import phase.generation.data.DataGenerationVehicle;
import phase.generation.fault.Fault;
import phase.generation.fault.FaultGenerationVehicle;
import phase.generation.fault.FaultTimer;
import phase.generation.history.FaultHistory;
import phase.generation.history.HistoryTimer;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class SimulationLoader {


	private IConfig config;
	private List<Algorithm> algorithms;
	private List<Vehicle> vehicles;
	private List<Sensor> sensors;
	private List<FaultDescription> faultDescriptions;
	private SimulationLoader.HistoryMode historyMode;
	private HistoryEvent histEvent;

	public enum HistoryMode{CREATE,READ,CONVERT_HISTORY_TO_CSV}
	public enum SensorBehaviorMode{DUPLICATE_SENSOR_BEHAVIOR,READ_ALL_SENSOR_BEHAVIOR}
	
	public final static String LINUX_OS = "LINUX";
	public final static String WINDOWS_OS = "WINDOWS";
	public final static char LINUX_PATH_SLASH = '/';
	public final static char WINDOWS_PATH_SLASH = '\\';

	public String rootDirPath;
	
	public String os;
	public SimulationLoader(String configFilePath) {

		initConfig(configFilePath);
		os = config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_OS);
		rootDirPath = parseOSSpecificFilePath(config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_ROOT_DIRECTORY));
		
	}


	/**
	 * empty constructor for subclass flexibility
	 */
	protected SimulationLoader() {

	}

	protected void initConfig(String configFilePath){
		try{

			config = new Configuration(configFilePath);

		}catch(Exception e ){
			System.out.println("failed to load config xml file: "+configFilePath);
			e.printStackTrace();
			System.exit(1);
		}

	}

	public int getNumberOfAnalysisReplayIterations(){
		try{
			return config.getIntProperty(IConfig.PROPERTY_SIMULATION_LOADER_ANALYSI_NUM_REPLAY_ITERATIONS);
		}catch(Exception e){
			return 1;
		}
	}
	/**
	 * Loads all the simulation from the configuration file, returning the initialized GlobalTimer.
	 * @return GlobalTimer to run simulation.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public GlobalTimer load() throws IOException, ClassNotFoundException{


		//String logFileOutputPath = config.getProperty(IConfig.PROPERTY_LOG_FILE_PATH);
		String logFileOutputPath = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_LOG_FILE_PATH));

		Logger log = LoggerFactory.getInstance();


		String logLevelStr = config.getProperty(IConfig.PROPERTY_LOG_LEVEL);
		int logLevel = 0;
		for(int i =0;i<Logger.LOG_LEVEL_LABLES.length;i++){
			if(Logger.LOG_LEVEL_LABLES[i].equals(logLevelStr)){
				logLevel=i;
				break;
			}
		}
		Logger.GLOBAL_LOG_LEVEL = logLevel;

		//just incase this  is called twice, we should clear log
		log.clearOutputStreams();


		FileOutputStream logFileOutStream = new FileOutputStream(logFileOutputPath);

		log.addOutputStream(logFileOutStream);
		log.addOutputStream(System.out);

		log.log_info("Starting configuration phase...");
		log.log_info("configuration file: "+config.getConfigFilePath());
		GlobalTimer result = null;
		FaultTimer faulTimer = null;
		DataGenerationCOSMOTimer dataGenerationTimer = null;
		HistoryTimer historyTimer = null;
		ReplayTimer replayTimer  = null;
		historyMode = SimulationLoader.HistoryMode.valueOf(config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_HISTORY_MODE));
		J1939DataDumpReader j1939Reader = null;

		int maxTimeTicks = config.getIntProperty(IConfig.PROPERTY_NUMBER_OF_TIME_TICKS);

		//reading previoyusly saved history?
		if(historyMode==SimulationLoader.HistoryMode.READ){

			/*	
				String inputFile = config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_HISTORY_INPUT_FILE);
				HistoryEvent histEvent = HistoryEvent.readHistoryEvent(inputFile);

				SensorStatusHistory ssHist = histEvent.getSensorStatusHistory();
				vehicles = ssHist.getUniqueVehicles();
				algorithms = ssHist.getPartitionKeys();
				sensors = ssHist.getUniqueSensors();

				replayTimer = buildReplayTimer(algorithms,vehicles,sensors,config);

				//create history timer and its output stream
				 historyTimer =  new HistoryTimer(algorithms,vehicles);	

				 //connect replay timer and history timer streams


				HistoryOutputStream hOut =  new HistoryOutputStream();
				HistoryInputStream hIn = new HistoryInputStream();

				historyTimer.setHistOutputStream(hOut);
				replayTimer.setHistInputStream(hIn);

				hOut.writeHistoryEvent(histEvent);

				//nwo flush it so at start of phase, history is available to analysis phase timers
				HistoryStreamManager sm = new HistoryStreamManager(hIn,hOut);
				sm.flush();
			 */


			//if loading multiple times, check if the history was already read last load
			if(histEvent  == null){
				//String inputFile = config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_HISTORY_INPUT_FILE);
				String inputFile = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_HISTORY_INPUT_FILE));
				histEvent = HistoryEvent.readHistoryEvent(inputFile);
			}	
			SensorStatusHistory ssHist = histEvent.getSensorStatusHistory();
			vehicles = ssHist.getUniqueVehicles();
			algorithms = ssHist.getPartitionKeys();
			sensors = ssHist.getUniqueSensors();

			replayTimer = buildReplayTimer(algorithms,vehicles,sensors,config);

			//create history timer and its output stream
			historyTimer =  new HistoryTimer(algorithms,vehicles);	

			//connect replay timer and history timer streams


			HistoryOutputStream hOut =  new HistoryOutputStream();
			HistoryInputStream hIn = new HistoryInputStream();

			historyTimer.setHistOutputStream(hOut);
			replayTimer.setHistInputStream(hIn);

			hOut.writeHistoryEvent(histEvent);

			//nwo flush it so at start of phase, history is available to analysis phase timers
			HistoryStreamManager sm = new HistoryStreamManager(hIn,hOut);
			sm.flush();

			//reading previoyusly saved history and converting it to CSV?
		}	else if(historyMode==SimulationLoader.HistoryMode.CONVERT_HISTORY_TO_CSV){


			//String inputFile = config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_HISTORY_INPUT_FILE);
			String inputFile = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_HISTORY_INPUT_FILE));

			log.log_info("Converting history '"+inputFile+"' to CSV");

			histEvent = HistoryEvent.readHistoryEvent(inputFile);

			String _faultHistCSVPath=inputFile+".faults.csv";
			String _repairHistCSVPath=inputFile+".repairs.csv";
			String _sensorStatusHistCSVPath=inputFile+".sensor-status.csv";
			String _histSummaryPath= inputFile +".summary.csv"; 
			historyEventToCSV(_faultHistCSVPath,_repairHistCSVPath,_sensorStatusHistCSVPath,_histSummaryPath,histEvent);

			log.log_info("Finished converting history '"+inputFile+"' to CSV in '"+_faultHistCSVPath+"', '"+_sensorStatusHistCSVPath+"', and  "+_histSummaryPath);
			//do nothing upon returning, this was just a run that converted histories to csv
			return null;


		}else if(historyMode==SimulationLoader.HistoryMode.CREATE){

			vehicles = loadVehicles(config);
			algorithms = loadAlgorithms(vehicles,config);
			sensors = loadSensors(config);
			
			List<Sensor> singleSensorFaultBlackList = loadSingleSensorFaultBlackList(config);
			
			faultDescriptions = buildFaultDescriptions(sensors,config);

			//gotta initialize the data found in data generation timer
			j1939Reader = readJ1939DataDump(sensors,config);

			faulTimer = buildFaultTimer(vehicles.size(),faultDescriptions,sensors,singleSensorFaultBlackList,maxTimeTicks,config);

			dataGenerationTimer = buildDataGenerationTimer(algorithms,vehicles,sensors,j1939Reader,maxTimeTicks,config);

			//anomalyDetectionTimer = buildAnomalyDetectionModelTimer(algorithms, vehicles,j1939Reader.getWhiteListSensorDescriptions(),config);

			historyTimer =  new HistoryTimer(algorithms,vehicles);	//don't make outputstream here, global timer will take care of it

			replayTimer = buildReplayTimer(algorithms,vehicles,sensors,config);

		}else{
			throw new SimulationException("illegal history creation state");
		}



		PerformanceAnalysisTimer performanceAnalysisTimer = buildPerformanceAnalysisTimer(algorithms,config);
		OutputTimer outputTimer = buildOutputTimer(algorithms,config);

		if(historyMode==SimulationLoader.HistoryMode.CREATE){
			outputTimer.setIsSavingHistory(true);//do output the history since we created it
		}else{
			outputTimer.setIsSavingHistory(false);//dont output history, we cdidnt change it
		}




		//loading previous history?
		if(historyMode==SimulationLoader.HistoryMode.READ){
			result = new GlobalTimer(historyTimer,replayTimer,performanceAnalysisTimer,outputTimer, algorithms, vehicles,maxTimeTicks);
		}else if(historyMode==SimulationLoader.HistoryMode.CREATE){//creating new hsitory?
			result = new GlobalTimer(faulTimer,dataGenerationTimer,historyTimer,replayTimer,performanceAnalysisTimer,outputTimer, algorithms, vehicles,maxTimeTicks);	
		}
		return result;

	}


	public HistoryMode getHistoryMode(){
		return this.historyMode;
	}

	private J1939DataDumpReader readJ1939DataDump(List<Sensor> sensors, IConfig config) throws IOException {
		//noise for stability

		//	Noise baseNoise = SimulationLoader.buildNoise(config,IConfig.PROPERTY_DATA_GENERATION_TIMER_BASE_NOISE_CONFIG_FILE_PATH);
		//String j1939SpecPath = config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_J1939_SPEC_DOC_FILE_PATH);
		String j1939SpecPath = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_J1939_SPEC_DOC_FILE_PATH));
		//String j1939DataDump = config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_J1939_DATA_DUMP_FILE_PATH);
		String j1939DataDump = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_J1939_DATA_DUMP_FILE_PATH));
		double filterProb = config.getDoubleProperty(IConfig.PROPERTY_SIMULATION_LOADER_RAW_SENSOR_READING_FILTER_PVALUE);


		try{
			//reads the data dump and readies timer to read from it.
			return new J1939DataDumpReader(Paths.get(j1939SpecPath),Paths.get(j1939DataDump),sensors,filterProb);
		}catch(IOException e){
			e.printStackTrace();
			throw new IOException("failed to load sensor data for data generation timer");
		}
	}


	protected OutputTimer buildOutputTimer(List<Algorithm> algorithms, IConfig config) {
		Logger log = LoggerFactory.getInstance();
		log.log_debug("loading OutputTimer");
		int experimentId=config.getIntProperty(IConfig.PROPERTY_OUTPUT_TIMER_EXPERIMENT_ID);

		/*String inputConfigFilePath=config.getConfigFilePath();
		 String inputLogFilePath=config.getProperty(IConfig.PROPERTY_LOG_FILE_PATH);
		 String outputFileDirectory=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_DIRECTORY);
		 String outputLogFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_LOG_FILE_NAME);
		 String outputConfigFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_CONFIG_FILE_NAME);
		 String outputRocCSVFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_CSV_FILE_NAME);
		 String inputRocRScriptFilePath=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_ROC_SCRIPT_FILE_NAME);		 
		 String outputRocCurveImageFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_IMAGE_FILE_NAME);
		 String inputFilesDirectory=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_FILES_DIRECTORY);
		 String histOutputFileName = config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_HISTORY_OUTPUT_FILE_NAME);

		 String inputBatchScript = config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_DATA_ANALYSIS_BATCH_SCRIPT_FILE_PATH);*/

		String inputConfigFilePath=config.getConfigFilePath();
		String inputLogFilePath=getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_LOG_FILE_PATH));
		String outputFileDirectory=getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_DIRECTORY));
		String outputLogFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_LOG_FILE_NAME);
		String outputConfigFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_CONFIG_FILE_NAME);
		String outputRocCSVFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_CSV_FILE_NAME);
		String inputRocRScriptFilePath=getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_ROC_SCRIPT_FILE_NAME));		 
		String outputRocCurveImageFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_IMAGE_FILE_NAME);
		String inputFilesDirectory=getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_FILES_DIRECTORY));
		String histOutputFileName = config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_HISTORY_OUTPUT_FILE_NAME);

		String inputBatchScript = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_DATA_ANALYSIS_BATCH_SCRIPT_FILE_PATH));
		
		boolean copyInputFolderInOutpufFlag = config.getBooleanProperty(IConfig.PROPERTY_OUTPUT_TIMER_SAVE_COPY_OF_INPUT_FOLDER_FLAG);

		//need to add the icosmo version of the algorithms as well
		List<Algorithm> allAlgorithms = new ArrayList<Algorithm>(algorithms.size()*2);
		for(Algorithm alg : algorithms){
			allAlgorithms.add(alg);
			allAlgorithms.add(alg.toICOSMO());
		}
		return new OutputTimer(null, experimentId,
				inputConfigFilePath, inputLogFilePath, outputFileDirectory, outputLogFileName,
				outputConfigFileName, 	outputRocCSVFileName, inputRocRScriptFilePath,
				outputRocCurveImageFileName,inputFilesDirectory, histOutputFileName,inputBatchScript,allAlgorithms,copyInputFolderInOutpufFlag);
	}


	protected static  PerformanceAnalysisTimer buildPerformanceAnalysisTimer(List<Algorithm> algorithms, IConfig config) {
		Logger log = LoggerFactory.getInstance();
		log.log_debug("loading PerformanceAnalysisTimer");
		int leftWindowSize = config.getIntProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_TIMER_LEFT_WINDOW_SIZE);
		int rightWindowSize = config.getIntProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_TIMER_RIGHT_WINDOW_SIZE);
		int thresholdNumberDecimalPrecision = config.getIntProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_THRESHOLD_DECIMAL_PRECISION);
		int zscoreMvAvgWindowSize = config.getIntProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE);
		return new PerformanceAnalysisTimer(algorithms,leftWindowSize,rightWindowSize,thresholdNumberDecimalPrecision,zscoreMvAvgWindowSize);
	}


	protected static  ReplayTimer buildReplayTimer(List<Algorithm> algorithms, List<Vehicle> vehicles, List<Sensor> sensors, IConfig config) {
		Logger log = LoggerFactory.getInstance();
		log.log_debug("loading ReplayTimer");
		int thresholdNumberDecimalPrecision = config.getIntProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_THRESHOLD_DECIMAL_PRECISION);
		ICOSMOTimer icosmoTimer = buildICOSMOTimer(algorithms,vehicles,sensors,config);
		return new ReplayTimer(icosmoTimer,thresholdNumberDecimalPrecision);

	}


	protected static ICOSMOTimer buildICOSMOTimer(List<Algorithm> algorithms, List<Vehicle> vehicles,
			List<Sensor> sensors, IConfig config) {

		Logger log = LoggerFactory.getInstance();
		log.log_debug("loading ICOSMOTimer");
		RepairInputStream repairInputStream = new RepairInputStream(vehicles);
		SensorStatusInputStream sensorStatusInputStream = new SensorStatusInputStream(algorithms);
		TimeStampedSensorStatusOutputStream sensorStatusOutputStream = new TimeStampedSensorStatusOutputStream(algorithms);

		ICOSMO icosmo = buildICOSMO(config);

		String enumType = config.getProperty(IConfig.PROPERTY_ICOSMO_TIMER_MODE);
		ICOSMOTimer.Mode mode = ICOSMOTimer.Mode.valueOf(enumType);

		ICOSMOTimer icosmoTimer = new ICOSMOTimer(algorithms,vehicles, repairInputStream,
				sensorStatusInputStream,sensorStatusOutputStream,icosmo,mode);
		icosmoTimer.init(sensors);


		int leftTimeWindow = config.getIntProperty(IConfig.PROPERTY_ICOSMO_TIMER_LEFT_TIME_WINDOW);
		icosmoTimer.setLeftTimeWindowDeviations(leftTimeWindow);
		

		boolean loggingSensorChanges = config.getBooleanProperty(IConfig.PROPERTY_ICOSMO_LOG_SENSOR_CHANGES);
		icosmoTimer.setLoggingSensorChanges(loggingSensorChanges);
		
		
		return icosmoTimer;
	}


	protected static ICOSMO buildICOSMO(IConfig config) {
		double stalnessThreshold = config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_STALENESS_THRESHOLD);
		double candicacyThreshold= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_CANDIDACY_THRESHOLD);

		double defaultContribution= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_DEFAULT_CONTRIBUTION);
		double defaultPotentialContribution= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_DEFAULT_POTENTIAL_CONTRIBUTION);

		double contributionDecreaseMod= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_CONTRIBUTION_DECREASE_MODIFIER);
		double contributionIncreaseMod= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_CONTRIBUTION_INCREASE_MODIFIER);
		double potentialContrDecreaseMod= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_POTENTIAL_CONTRIBUTION_DECREASE_MODIFIER);
		double potentialContrIncreaseMod= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_POTENTIAL_CONTRIBUTION_INCREASE_MODIFIER);

		double desiredRecall= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_DESIRED_RECALL);
		double desiredPrecision= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_DESIRED_PRECISION);


		int numFaultInvolvedSensorEstimation= config.getIntProperty(IConfig.PROPERTY_ICOSMO_NUMBER_FAULT_INVOLVED_SENSORS);
		int zValueWindowSize= config.getIntProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE);
		int maxNumberAddedSensors= config.getIntProperty(IConfig.PROPERTY_ICOSMO_MAX_NUMBER_ADDED_SENSORS);
		int maxNumberRemovedSensors= config.getIntProperty(IConfig.PROPERTY_ICOSMO_MAX_NUMBER_REMOVED_SENSORS);
		double sensorInterestThreshold= config.getDoubleProperty(IConfig.PROPERTY_ICOSMO_SENSOR_INTEREST_THRESHOLD);
		return new ICOSMO(stalnessThreshold,  candicacyThreshold,  contributionDecreaseMod,
				contributionIncreaseMod,  potentialContrDecreaseMod,  potentialContrIncreaseMod, desiredRecall, desiredPrecision,
				defaultContribution,  defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,sensorInterestThreshold,maxNumberAddedSensors,maxNumberRemovedSensors);

	}


	/*
	protected static  AnomalyDetectionModelTimer buildAnomalyDetectionModelTimer(List<Algorithm> algorithms, List<Vehicle> vehicles, List<SensorDescription> listSensorDescriptions, IConfig config) {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(algorithms.size());

		for(Algorithm a: algorithms){
			algs.add((AnomalyDetectionAlgorithm) a);
		}

		int zvalueWindowSize = config.getIntProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE);

		AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(algs, zvalueWindowSize);
		t.init(vehicles, listSensorDescriptions);

		return t;
	}
	 */




	protected   DataGenerationCOSMOTimer buildDataGenerationTimer(List<Algorithm> algorithms, List<Vehicle> vehicles,
			List<Sensor> sensors, J1939DataDumpReader j1939Reader,int maxTimeTicks,IConfig config) throws IOException{

		Logger log = LoggerFactory.getInstance();
		log.log_debug("loading DataGenerationCOSMOTimer");
		int numberVehicles = vehicles.size();

		if(numberVehicles < 0){
			throw new ConfigurationException("failed to load faulttimer, negative number of vehicles");
		}


		DataGenerationCOSMOTimer result = null;

		List<DataGenerationVehicle> dataGenerationVehicles = new ArrayList<DataGenerationVehicle>(numberVehicles);

		//create the vehicles
		for(int i = 0; i < numberVehicles;i++){
			DataGenerationVehicle v = new DataGenerationVehicle(i);
			dataGenerationVehicles.add(v);
		}

		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(algorithms.size());

		for(Algorithm a: algorithms){
			algs.add((AnomalyDetectionAlgorithm) a);
		}

		int zvalueWindowSize = config.getIntProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE);

		SensorBehaviorMode mode = SensorBehaviorMode.valueOf(config.getProperty(IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_CREATION_MODE));


		//compute number of random numbers required
		//each for each fault, for each vehicle, for each time tick

		long numVehicles = dataGenerationVehicles.size();
		long numSensorReadings = j1939Reader.sizeRawDataStream();
		long sizeRandomNumberStreams =  numVehicles*numSensorReadings* maxTimeTicks;
		sizeRandomNumberStreams= (long) ((double)sizeRandomNumberStreams * 1.2);//don't take up all memory, ct in half

		if(mode == SensorBehaviorMode.DUPLICATE_SENSOR_BEHAVIOR){
			result = buildDataGenerationTimer_duplicateAll(algs,dataGenerationVehicles,sensors,zvalueWindowSize,sizeRandomNumberStreams,config);
		}else{
			result = buildDataGenerationTimer_readAll(algs,dataGenerationVehicles,sensors,zvalueWindowSize,sizeRandomNumberStreams,config);
		}




		RawSensorDataInputStream dataInput = j1939Reader.getRawDataInputStream();
		result.setSensorDataInputStream(dataInput);


		List<SensorDescription> whiteListSensorDescriptions = j1939Reader.getWhiteListSensorDescriptions();

		result.init(vehicles, whiteListSensorDescriptions);


		//make sure all sensors read are in white list, and all white list in read
		List<Sensor> actualSensors = new ArrayList<Sensor>(sensors.size());
		Iterator<Sensor> it = dataInput.keyIterator();
		while(it.hasNext() ){
			Sensor s = it.next();
			actualSensors.add(s);
		}


		if(actualSensors.size() != sensors.size()){
			throw new SimulationException("failed to read all the sensor readings, missing expected sensors. Number of actual read sensors: "+actualSensors.size()+", but expected "+sensors.size());

		}

		for(Sensor s : actualSensors){
			if(!sensors.contains(s)){
				throw new SimulationException("sensor ("+s+") not found in expected sensor list.");
			}
		}

		if(!Util.allElementsUnique(actualSensors)){
			throw new SimulationException("duplicate sensors read from data dump");
		}
		return result;
	}



	private  DataGenerationCOSMOTimer buildDataGenerationTimer_duplicateAll( List<AnomalyDetectionAlgorithm> algs,
			List<DataGenerationVehicle> dataGenerationVehicles, List<Sensor> expectedSensors,int zvalueWindowSize ,long sizeRandomNumberStreams, IConfig config) {

		//make sure no duplicate sensor behaviors
		if(!Util.allElementsUnique(expectedSensors)){
			throw new ConfigurationException("Duplicate sensors");
		}

		DataGenerationCOSMOTimer result = null;

		Noise whiteNoise =  buildNoise(config,IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_WHITE_NOISE_FILE_PATH);

		Noise ampFactorGenerator = buildNoise(config,IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_AMP_FACTOR_FILE_PATH);
		Noise noiseAmpFactorGenerator = buildNoise(config,IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR_FILE_PATH);
		Noise noisePValue1Generator = buildNoise(config,IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_NOISE_PVALUE1_FILE_PATH);
		Noise noisePValue2Generator = buildNoise(config,IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_NOISE_PVALUE2_FILE_PATH);

		//actualSensors.add(s);
		Logger log = LoggerFactory.getInstance();
		for(Sensor s : expectedSensors){
			//create fault using the stats generations for each vehicle 
			for(DataGenerationVehicle v : dataGenerationVehicles){
				//generate data gen sensor object with random attributes
				SensorBehavior b = SimulationLoader.buildSensorBehavior(whiteNoise, ampFactorGenerator, noiseAmpFactorGenerator, noisePValue1Generator, noisePValue2Generator);
				DataGenerationSensor dataGenSensor = new DataGenerationSensor(s.getPgn(),s.getSpn(),b);
				v.addSensor(dataGenSensor);
				//log.log_debug("creating  "+dataGenSensor+", Vehicle("+v.getVid()+")");
			}
		}

		//result = new DataGenerationCOSMOTimer(dataGenerationVehicles, algs, zvalueWindowSize);
		result = new DataGenerationCOSMOTimer(dataGenerationVehicles, algs, zvalueWindowSize,sizeRandomNumberStreams);
		return result;
	}


	private  DataGenerationCOSMOTimer buildDataGenerationTimer_readAll( List<AnomalyDetectionAlgorithm> algs,
			List<DataGenerationVehicle> dataGenerationVehicles, List<Sensor> expectedSensors,int zvalueWindowSize , long sizeRandomNumberStreams ,IConfig config) throws InvalidPropertiesFormatException, IOException {

		Logger log = LoggerFactory.getInstance();

		DataGenerationCOSMOTimer result = null;


		//get the config objects of all theconfigs in the data generation directory
		//String dataGenerationSensorDirecotry = config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_DATA_GENERATION_SENSOR_DIRECTORY);
		String dataGenerationSensorDirecotry = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_DATA_GENERATION_SENSOR_DIRECTORY));

		//get all the config objects of data gen sensors
		List<IConfig> sensorConfigs = config.loadConfigFilesInDirectory(dataGenerationSensorDirecotry);

		//list of sensors with behaviors defined in configuration files
		List<Sensor> actualSensors = new ArrayList<Sensor>(expectedSensors.size());



		//each config object holds the necessary info to make resulting objects, ie, iterate all data generation sensors
		for(IConfig c : sensorConfigs){

			String sensorString =  c.getProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_SENSOR);
			Sensor s = parseSensor(sensorString);

			//skip this sensor if not in white list
			if(!expectedSensors.contains(s)){
				log.log_warning("Not adding sensor "+s+" to data generation, it wasn't found in white list (entry: "+IConfig.PROPERTY_SENSOR_WHITE_LIST+")");
				continue;
			}

			//alread included the sensor?
			if(actualSensors.contains(s)){
				throw new ConfigurationException("Duplicate sensor behavior: "+s+", config file: "+c.getConfigFilePath());
			}
			actualSensors.add(s);


			Noise whiteNoise =  buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_WHITE_NOISE_CONFIG_FILE_PATH);

			Noise ampFactorGenerator = buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH);
			Noise noiseAmpFactorGenerator = buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH);
			Noise noisePValue1Generator = buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE1_AVERAGE_CONFIG_FILE_PATH);
			Noise noisePValue2Generator = buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE2_AVERAGE_CONFIG_FILE_PATH);



			//create fault using the stats generations for each vehicle 
			for(DataGenerationVehicle v : dataGenerationVehicles){
				//generate data gen sensor object with random attributes
				SensorBehavior b = SimulationLoader.buildSensorBehavior(whiteNoise, ampFactorGenerator, noiseAmpFactorGenerator, noisePValue1Generator, noisePValue2Generator);
				DataGenerationSensor dataGenSensor = new DataGenerationSensor(s.getPgn(),s.getSpn(),b);
				v.addSensor(dataGenSensor);
				//log.log_debug("creating  "+dataGenSensor+", Vehicle("+v.getVid()+")");

			}


		}//end iterate sensor config files


		//the actual sensors  with behavior defined in configuration should all be part of white list
		if(actualSensors.size() != expectedSensors.size()){
			throw new ConfigurationException("Missing sensor behavior. Definitions in "+dataGenerationSensorDirecotry+" don't match white listed sensors defined in entry: "+IConfig.PROPERTY_SENSOR_WHITE_LIST);
		}

		//make sure no duplicate sensor behaviors
		if(!Util.allElementsUnique(actualSensors)){
			throw new ConfigurationException("Duplicate sensor behavior definitions in "+dataGenerationSensorDirecotry);
		}

		//result = new DataGenerationCOSMOTimer(dataGenerationVehicles, algs, zvalueWindowSize);
		result = new DataGenerationCOSMOTimer(dataGenerationVehicles, algs, zvalueWindowSize,sizeRandomNumberStreams);
		return result;

	}


	/**
	 * loads white list of sensors
	 * @return sensors
	 */
	protected static  List<Sensor> loadSensors(IConfig config) {
		return _loadSensors(config,IConfig.PROPERTY_SENSOR_WHITE_LIST);		
	}

	/**
	 * loads a list of sensors from a configuration file
	 * @return sensors
	 */
	protected static  List<Sensor> _loadSensors(IConfig config,String property) {
		List<Sensor> res = new ArrayList<Sensor>(128);

		List<String> unparsedSensors = config.getProperties(property);

		for(String str : unparsedSensors){
			Sensor s = parseSensor(str);
			res.add(s);
		}

		return res;
	}
	
	/**
	 * loads black list of sensors that should not be part of single sensor faults
	 * @return sensors
	 */
	protected static  List<Sensor> loadSingleSensorFaultBlackList(IConfig config) {
		return _loadSensors(config,IConfig.PROPERTY_FAULT_SINGLE_SENSOR_FAULT_BLACK_WHITE_LIST);
	}

	
	protected static  List<Vehicle> loadVehicles(IConfig config) {

		int numVehicles = config.getIntProperty(IConfig.PROPERTY_NUMBER_OF_VEHICLES);

		if(numVehicles <= 0){
			throw new ConfigurationException("cannot create vehicles, expected positive number of vehicles of entry ("+IConfig.PROPERTY_NUMBER_OF_VEHICLES+") but was ("+numVehicles+")");
		}

		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);

		for(int i = 0;i<numVehicles;i++){
			Vehicle v = new Vehicle(i);
			vehicles.add(v);
		}

		return vehicles;
	}

	protected static  List<Algorithm> loadAlgorithms(List<Vehicle> vehicles,IConfig config) {

		if(vehicles == null){
			throw new ConfigurationException("failed to load algorithms, vehicles are null");
		}

		int numAlgorithms = config.getIntProperty(IConfig.PROPERTY_NUMBER_OF_ALGORITHMS);

		if(numAlgorithms <= 0){
			throw new ConfigurationException("cannot create algorithms, expected positive number of algorithms of entry ("+IConfig.PROPERTY_NUMBER_OF_ALGORITHMS+") but was ("+numAlgorithms+")");
		}

		int numberOfSelectedSensors = config.getIntProperty(IConfig.PROPERTY_ALGORITHM_NUMBER_SELECTED_SENSORS);
		int zscoreUpdateFreq = config.getIntProperty(IConfig.PROPERTY_ALGORITHM_ZSCORE_UPDATE_FREQUENCY);


		//get the distannce metrix strings (example: "HellinGer") for each algorithm
		//there should be exatcly as many metrics as algorithms, and they must be unique
		List<String> distanceMetricStrings = config.getProperties(IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES);

		if(distanceMetricStrings.size() != numAlgorithms ){
			throw new XMLFormatException("key: "+IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES+", expected ("+numAlgorithms+") entries (defined by key"
					+ IConfig.PROPERTY_NUMBER_OF_ALGORITHMS +") but was ("+numAlgorithms+")");
		}

		if(!Util.allElementsUnique(distanceMetricStrings)){
			throw new XMLFormatException("key: "+IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES+", expected unique distance measures, but duplicates are found.");
		}

		

		List<Algorithm> algorithms = new ArrayList<Algorithm>(numAlgorithms);

		boolean addingRandomSensorSelectionBaseline = config.getBooleanProperty(IConfig.PROPERTY_ALGORITHM_RANDOM_SENSOR_SELECTION_FLAG);
		
		
		//create all the cosmo algorithms
		for(int i = 0;i<numAlgorithms;i++){

			String strDistMetric =  distanceMetricStrings.get(i);
			Histogram.DistanceMeasure distanceMetric;
			try{
				//convert to enum
				distanceMetric =  Histogram.DistanceMeasure.valueOf(strDistMetric);
			}catch(IllegalArgumentException e){
				throw new XMLFormatException("could not find the  Histogram.DistanceMeasure enum for the entry ("+IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES+""+i+") with value: "+strDistMetric);
			}

			Algorithm alg = new COSMO(i,vehicles,distanceMetric,numberOfSelectedSensors,zscoreUpdateFreq,distanceMetric.toString());
			//

			algorithms.add(alg);
		}
		
		/*
		 * wHEN random sensor selection baseline enabled 
		 * For each algorithm, create a copy but instacne a RandomCOSMO algorithm instance with ID = + 32 instead
		 * That way when doing snesor selection, just check instance type and randomly pick
		 */
		if(addingRandomSensorSelectionBaseline) {
			//create all the cosmo algorithms
			for(int i = 0;i<numAlgorithms;i++){

				String strDistMetric =  distanceMetricStrings.get(i);
				Histogram.DistanceMeasure distanceMetric;
				try{
					//convert to enum
					distanceMetric =  Histogram.DistanceMeasure.valueOf(strDistMetric);
				}catch(IllegalArgumentException e){
					throw new XMLFormatException("could not find the  Histogram.DistanceMeasure enum for the entry ("+IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES+""+i+") with value: "+strDistMetric);
				}	

				//the id's are offset to differentiate COSMO from RANDOM SELECTION SENSOR COSMO
				Algorithm alg = new RandomSensorCOSMO(RandomSensorCOSMO.ID_OFFSET+i,vehicles,distanceMetric,numberOfSelectedSensors,zscoreUpdateFreq,distanceMetric.toString());
				//

				algorithms.add(alg);
			}
			
		}
		
		
		return algorithms;
	}




	protected  FaultTimer buildFaultTimer(int numberVehicles,List<FaultDescription> faultDescriptions,List<Sensor> allSensors,List<Sensor> singleSensorFaultBlackList,int maxTimeTicks, IConfig config) throws InvalidPropertiesFormatException, IOException {
		Logger log = LoggerFactory.getInstance();
		log.log_debug("loading FaultTimer");


		if(numberVehicles < 0){
			throw new ConfigurationException("failed to load faulttimer, negative number of vehicles");
		}

		if(faultDescriptions == null){
			throw new ConfigurationException("failed to load faulttimer, fault descriptions are null");
		}

		FaultTimer result = null;

		List<FaultGenerationVehicle> faultGenerationVehicles = new ArrayList<FaultGenerationVehicle>(numberVehicles);

		//create the vehicles
		for(int i = 0; i < numberVehicles;i++){
			FaultGenerationVehicle v = new FaultGenerationVehicle(i);
			faultGenerationVehicles.add(v);
		}



		//get the config objects of all the fault configs in the fault directory for fault timer
		//String faultDirecotry = config.getProperty(IConfig.PROPERTY_FAULT_TIMER_FAULT_DIRECTORY);
		String faultDirecotry = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_FAULT_TIMER_FAULT_DIRECTORY));
		//get all the config objects of faults in the fault direcotry
		List<IConfig> fdConfigs = config.loadConfigFilesInDirectory(faultDirecotry);


		//each config object holds the necessary info to make resulting objects, ie, iterate all faults
		for(IConfig c : fdConfigs){

			int fdId = c.getIntProperty(IConfig.PROPERTY_FAULT_FAULT_DESCRIPTION_ID);
			//get index of target fd
			int ix = faultDescriptions.indexOf(new FaultDescription(fdId,null));
			FaultDescription fd = faultDescriptions.get(ix);

			if(fd == null){
				throw new XMLFormatException("cannot create Fault in buildFaultTimer since faultdescription id ("+fdId+") referring to non existant FD");
			}

			int minimumDaysBeforeRepair = c.getIntProperty(IConfig.PROPERTY_FAULT_MINIMUM_DAYS_BEFORE_REPAIR);

			Noise faultProbGenerator = buildNoise(c,IConfig.PROPERTY_FAULT_FAULT_GENERATION_NOISE_CONFIG_FILE_PATH);
			Noise repairProbGenerator = buildNoise(c,IConfig.PROPERTY_FAULT_REPAIR_GENERATION_NOISE_CONFIG_FILE_PATH);

			//create fault using the stats generations for each vehicle 
			for(FaultGenerationVehicle v : faultGenerationVehicles){
				//generate fault object with random attributes
				Fault f = SimulationLoader.buildFault(minimumDaysBeforeRepair, faultProbGenerator, repairProbGenerator, fd);
				v.addPotentialFault(f);

				log.log_info("creating "+f+", Vehicle("+v.getVid()+")");
			}


		}//end iterate fault config files


		//now at this ponit, populate vehicles with potential single sensor faults


		//IConfig c = config.loadTargetConfigFile(IConfig.PROPERTY_SENSOR_BEHAVIOR_SINGLE_SENSOR_FAILURE_CONFIG_DIRECTORY);

		IConfig c = loadTargetConfigFile(config,IConfig.PROPERTY_SENSOR_BEHAVIOR_SINGLE_SENSOR_FAILURE_CONFIG_DIRECTORY);

		int minimumDaysBeforeRepair = c.getIntProperty(IConfig.PROPERTY_FAULT_MINIMUM_DAYS_BEFORE_REPAIR);
		Noise faultProbGenerator =buildNoise(c,IConfig.PROPERTY_FAULT_FAULT_GENERATION_NOISE_CONFIG_FILE_PATH);
		Noise repairProbGenerator =buildNoise(c,IConfig.PROPERTY_FAULT_REPAIR_GENERATION_NOISE_CONFIG_FILE_PATH);
		
		
		//we extract the allowed sensors to fail by exlcuding those found in blacklist
		List<Sensor> singleSensorFaultsWhiteList = new ArrayList<Sensor>(allSensors.size());
		
		for(Sensor s: allSensors) {
			
			//not found in black list?
			if(! singleSensorFaultBlackList.contains(s)) {
				singleSensorFaultsWhiteList.add(s);
			}
		}
		
		List<FaultDescription> singleSensorFaultDescriptions =buildSingleSensorFaultDescriptions(singleSensorFaultsWhiteList,config);
		for(FaultDescription fd : singleSensorFaultDescriptions){

			//create fault using the stats generations for each vehicle 
			for(FaultGenerationVehicle v : faultGenerationVehicles){
				//generate fault object with random attributes
				Fault f = SimulationLoader.buildFault(minimumDaysBeforeRepair, faultProbGenerator, repairProbGenerator, fd);
				v.addPotentialFault(f);

				log.log_info("creating "+f+", Vehicle("+v.getVid()+")");
			}


		}//end iterate fault config files

		//compute number of random numbers required
		//each for each fault, for each vehicle, for each time tick
		//make sure its 20% just incase math is off by a few (to avoid recreating all the numbers again)	
		int sizeRandomNumberStreams = numberVehicles * (faultDescriptions.size()+singleSensorFaultDescriptions.size()) * maxTimeTicks;
		sizeRandomNumberStreams= (int) ((double)sizeRandomNumberStreams * 1.2);

		result = new FaultTimer(faultGenerationVehicles,sizeRandomNumberStreams);

		return result;
	}


	protected static Fault buildFault(int minDaysBeforeRepair,Noise faultProbGenerator, Noise repairProbGenerator, FaultDescription fd){
		double faultOccurencePValue = faultProbGenerator.generateNoise();
		double repaireOccurencePValue = repairProbGenerator.generateNoise();
		Fault f = new Fault(minDaysBeforeRepair,faultOccurencePValue,repaireOccurencePValue,fd);
		return f;
	}

	protected static SensorBehavior buildSensorBehavior(Noise whiteNoise, Noise ampFactorGenerator, Noise noiseAmpFactorGenerator,Noise noisePValue1Generator,Noise noisePValue2Generator){
		double ampFactor = ampFactorGenerator.generateNoise();
		double noiseAmpFactor = noiseAmpFactorGenerator.generateNoise();
		double nosiePvalue1 = noisePValue1Generator.generateNoise();
		double nosiePvalue2 = noisePValue2Generator.generateNoise();
		SensorBehavior b = new SensorBehavior(whiteNoise, ampFactor, noiseAmpFactor, nosiePvalue1, nosiePvalue2);
		return b;
	}

	protected   List<FaultDescription> buildFaultDescriptions(List<Sensor> allSensors, IConfig config) throws IOException  {

		//sensor white list need to be loaded
		if(allSensors == null){
			throw new ConfigurationException("cannot build fault descriptions with null sensor list.");
		}

		//String faultDirectoryPath = config.getProperty(IConfig.PROPERTY_FAULT_DESCRIPTIONS_DIRECTORY);
		String faultDirectoryPath = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_FAULT_DESCRIPTIONS_DIRECTORY));
		List<FaultDescription> result = new ArrayList<FaultDescription>(64);

		List<IConfig> fdConfigs = config.loadConfigFilesInDirectory(faultDirectoryPath);

		//each config object holds the necessary info to make a fault description
		for(IConfig c : fdConfigs){

			FaultDescription fd = buildFaultDescription(allSensors,c);

			//make sure the sensors affected by fault are part of whitelist
			List<Sensor> actualSensors = fd.getSensors();

			for(Sensor s : actualSensors){
				//sensor in fault definitions not found in white list?
				if(!allSensors.contains(s)){
					throw new ConfigurationException("sensor "+s+" defined as fault-invovled sensor in fault description: "+fd.getId()+", but wasn't found in expected sensor white list");
				}
			}

			actualSensors = fd.getNonFaultInvolvedSensors();

			for(Sensor s : actualSensors){
				//sensor in fault definitions not found in white list?
				if(!allSensors.contains(s)){
					throw new ConfigurationException("sensor "+s+" defined as non faut-involved sensor in fault description: "+fd.getId()+", but wasn't found in expected sensor white list");
				}
			}

			result.add(fd);

		}

		return result;
	}

	protected   List<FaultDescription> buildSingleSensorFaultDescriptions(List<Sensor> allSensors, IConfig config) throws IOException  {

		//IConfig singleSensorBehaviorconfig = config.loadTargetConfigFile(IConfig.PROPERTY_FAULT_DESCRIPTIONS_SINGLE_SENSORS_BEHAVIOR_CONFIG_FILE_PATH);

		IConfig singleSensorBehaviorconfig = loadTargetConfigFile(config,IConfig.PROPERTY_FAULT_DESCRIPTIONS_SINGLE_SENSORS_BEHAVIOR_CONFIG_FILE_PATH);
		List<FaultDescription> result = new ArrayList<FaultDescription>(64);
		//now create a fault description for each unique sensor, as a sensor failure fault
		for(Sensor s : allSensors){
			FaultInvolvedSensorBehavior sb = buildFaultInvolvedSensorBehavior(s,singleSensorBehaviorconfig);
			int id = result.size();
			FaultDescription fd = new FaultDescription(id,"Single sensor failure: sensor: ("+s.getPgn()+","+s.getSpn()+")");
			fd.addAffectedSensor(sb);

			fd.init(allSensors);
			result.add(fd);
		}

		return result;

	}
	//PROPERTY_SENSOR_BEHAVIOR_SINGLE_SENSOR_FAILURE_CONFIG_DIRECTORY

	protected   FaultDescription buildFaultDescription(List<Sensor> allSensors, IConfig config) throws InvalidPropertiesFormatException, IOException {
		int id = config.getIntProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_ID);
		String label = config.getProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_LABEL);

		FaultDescription fd = new FaultDescription(id,label);

		//populate the fault description with sensor behavior
		List<FaultInvolvedSensorBehavior> sensorBehaviors = buildFaultInvolvedSensorBehaviors(config);
		for(FaultInvolvedSensorBehavior b : sensorBehaviors){
			fd.addAffectedSensor(b);
		}

		//initialize the fault description

		fd.init(allSensors);

		return fd;
	}


	protected  List<FaultInvolvedSensorBehavior> buildFaultInvolvedSensorBehaviors(IConfig config) throws InvalidPropertiesFormatException, IOException {

		//directory with sensor behavior config files
		String sensorBehaviorDirectoryPath = getRelativePathToRootDirectory(config.getProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_DIRECTORY));

		List<FaultInvolvedSensorBehavior> result = new ArrayList<FaultInvolvedSensorBehavior>(16);

		List<IConfig> fdConfigs = config.loadConfigFilesInDirectory(sensorBehaviorDirectoryPath);

		//each config object holds the necessary info to make resulting objects
		for(IConfig c : fdConfigs){

			FaultInvolvedSensorBehavior b = buildFaultInvolvedSensorBehavior(c); 
			result.add(b);

		}
		return result;
	}


	protected  FaultInvolvedSensorBehavior buildFaultInvolvedSensorBehavior(IConfig config) {
		Noise noise = buildNoise(config,IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH);

		double ampFactor = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR);
		double noiseAmpFactor = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR);
		double noisePvalue1 = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1);
		double noisePvalue2 = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2);

		String typeEnum = config.getProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE);
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.valueOf(typeEnum);

		String sensorString =  config.getProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR);
		Sensor s = parseSensor(sensorString);


		FaultInvolvedSensorBehavior b = new FaultInvolvedSensorBehavior(noise,ampFactor,noiseAmpFactor,noisePvalue1,noisePvalue2,type,s);
		return b;

	}

	/**
	 * Builds sensor behavior given a sensor
	 * @param s
	 * @param config
	 * @return
	 */
	protected  FaultInvolvedSensorBehavior buildFaultInvolvedSensorBehavior(Sensor s, IConfig config) {
		Noise noise = buildNoise(config,IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH);

		double ampFactor = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR);
		double noiseAmpFactor = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR);
		double noisePvalue1 = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1);
		double noisePvalue2 = config.getDoubleProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2);

		String typeEnum = config.getProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE);
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.valueOf(typeEnum);


		FaultInvolvedSensorBehavior b = new FaultInvolvedSensorBehavior(noise,ampFactor,noiseAmpFactor,noisePvalue1,noisePvalue2,type,s);
		return b;

	}

	protected static Sensor parseSensor(String sensorString){
		//expected format "(pgn,spn)"
		String formatedString = sensorString.replaceAll("[\\(\\)]", " ");
		formatedString.trim();
		//expected fromat "pgn spn"
		String [] tokens = formatedString.split(",");

		int pgn = Integer.parseInt(tokens[0].trim());
		int spn = Integer.parseInt(tokens[1].trim());

		return new Sensor(pgn,spn);

	}

	protected Noise buildNoise(IConfig config, String configFilePathKey) {

		//IConfig targetConfig = config.loadTargetConfigFile(configFilePathKey);
		IConfig targetConfig = loadTargetConfigFile(config,configFilePathKey);
		return SimulationLoader.buildNoise(targetConfig);
	}

	protected static Noise buildNoise(IConfig config) {

		double mean = config.getDoubleProperty(IConfig.PROPERTY_NOISE_MEAN);
		double sd = config.getDoubleProperty(IConfig.PROPERTY_NOISE_SD);
		double max = config.getDoubleProperty(IConfig.PROPERTY_NOISE_MAX);
		double min = config.getDoubleProperty(IConfig.PROPERTY_NOISE_MIN);

		String typeEnum = config.getProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION);
		Noise.Distribution dist = Noise.Distribution.valueOf(typeEnum);

		return new Noise(mean, sd, max, min, dist);
	}

	public String getRelativePathToRootDirectory(String relativePath){
		

		//convert the path depending on operating system
		relativePath = parseOSSpecificFilePath(relativePath);
		//return Paths.get(rootDirPath,relativePath).toString();
		
		String resultPath =Paths.get(rootDirPath,relativePath).toString();
		return parseOSSpecificFilePath(resultPath); 
	}
	
	
	/**
	 * Converts a path to appropriate format depending on operating system
	 * The slashes are replaced with '/' for linux and '\' for windows
	 * @param path The path to be converted
	 * @return Converted path or the same path if os wasn't specified
	 */
	public String parseOSSpecificFilePath(String path) {
		
		//operating system property found?
		if(os != null) {
			
			//in windows we use \ to seperate folders
			//in linux its /
			// replace any / or \ with appropriate slash 
			
			if(os.equals(WINDOWS_OS)) {
				
				//replace forward slash with back slash
				path = path.replace(LINUX_PATH_SLASH, WINDOWS_PATH_SLASH);
				
				
			}else if(os.equals(LINUX_OS)) {
				//replace back slash with forward slash
				path = path.replace(WINDOWS_PATH_SLASH,LINUX_PATH_SLASH);
			
			}
		}//OTHERWISE we use raw given path (not recommended as won't be portable between windows and linux)
		
		return path;
	}
	public IConfig loadTargetConfigFile(IConfig config, String key){

		String targetConfigFilePath = getRelativePathToRootDirectory(config.getPropertyAndValidate(key));

		try {
			Configuration res = new Configuration(targetConfigFilePath);
			return res;
		} catch (IOException e) {
			throw new XMLFormatException("could not load a configuraiton file from file ("+targetConfigFilePath+") defined by key ("+key+") from config file: "+config.getConfigFilePath());
		}

	}

	public static void historyEventToCSV(String _faultHistCSVPath, String _repairHistCSVPath, String _sensorStatusHistCSVPath, String _histSummaryPath,HistoryEvent _histEvent) throws IOException {


		//IO object to efficiently append
		File faultOutCsv = new File(_faultHistCSVPath);
		/*
		 * FAULTS
		 */

		//FileHandler.createFile(_faultHistCSVPath);
		
		 if(!faultOutCsv.exists()){
			 faultOutCsv.createNewFile();
    	  }
		  FileWriter fw = new FileWriter(faultOutCsv,true);
		  BufferedWriter bw = new BufferedWriter(fw);
		  PrintWriter pw = new PrintWriter(bw);
    	  
		SensorStatusHistory ssHist = _histEvent.getSensorStatusHistory();	



		Iterator<TimerEvent> ssHistTimeIt =ssHist.timerEventIterator();
		FaultHistory fHist = _histEvent.getFaultHistory();

		RepairHistory rHist = _histEvent.getRepairHistory();

		
		
		
		
		
		//find the size (in time ticks) of simulation
		int lastSimulationTimeTick = -1;//need to comput this some how
		while(ssHistTimeIt.hasNext()) {
			TimerEvent e = ssHistTimeIt.next();
			
			int time = e.getTime();
			
			if(time >lastSimulationTimeTick) {
				lastSimulationTimeTick=time;
			}
			 
		}
		
		
		
		List<Vehicle> vehicles = fHist.getPartitionKeys();

		String fHistHeading = "occurence time tick,repaired time tick,vehicle,fault name,fault-involved sensors\n";
			

		//FileHandler.append(_faultHistCSVPath,fHistHeading.getBytes());
		pw.print(fHistHeading);
		

		//iterate keys of fault history (vehicles have their own set of faults that occured)
		for(Vehicle v : vehicles) {
				
			Iterator<TimerEvent> fHistTimeIt =fHist.timerEventIterator(v);
			
			//iterate over each time step that a fault occured for the vehicle
			while(fHistTimeIt.hasNext()) {
				TimerEvent timerEvent = fHistTimeIt.next();
				int time = timerEvent.getTime();

				
				Iterator<FaultDescription> fdIt = fHist.elementIterator(v, timerEvent);

				//iterate overt the faults
				while(fdIt.hasNext()) {

					FaultDescription fd = fdIt.next();
					
					//find the time it was repaired
					//by default -1, as if simulated ended before repair, -1 indicates not reparied 
					int tickRepaired = -1;
					
					

					
					int currRepairTickSearch = time;
					TimerEvent repairTimerEvent = new TimerEvent(currRepairTickSearch);
					while(tickRepaired == -1 && currRepairTickSearch <= lastSimulationTimeTick) {
						
						//it
						Iterator<RepairEvent> reIt = rHist.elementIterator(v, repairTimerEvent);
	
						//iterate overt the repairs
						while(reIt.hasNext()) {
	
							RepairEvent re = reIt.next();
	
							FaultDescription __fd = re.getFaultDescription();
							
							//found the repair log that repaired current fault?
							if(__fd.getId() ==fd.getId()) {
								tickRepaired=currRepairTickSearch;
								break;
							}
	
						}
						currRepairTickSearch++;
						repairTimerEvent.setTime(currRepairTickSearch);
						
					}//end iterate over all the repairs in search for repair of fault
					
				
					String row = time+","+tickRepaired+","+v.getVid()+","+fd.getLabel()+",";

					List<FaultInvolvedSensorBehavior> fiSensors = fd.getAffectedSensors();

					String fiSensorCSVEntry = "";
					//iterate over fault-involved sensors
					for(FaultInvolvedSensorBehavior fiSensor: fiSensors) {
						Sensor sensor = fiSensor.getAffectedSensor();
						//since it's a CSV file, can't seperate the PGN from SPN using ','.
						//instaed have following format: (PGN:SPN);(PGN:SPN);...;(PGN:SPN)
						fiSensorCSVEntry = fiSensorCSVEntry + "("+sensor.getPgn()+":"+sensor.getSpn()+");"; 
					}
					

					row = row +fiSensorCSVEntry + "\n";
					
					//FileHandler.append(_faultHistCSVPath,row.getBytes());
					pw.print(row);
				}
			}

		}//end iterate over timer ovents of faults

		pw.close();


		/*
		 * sensor status
		 */

	//	FileHandler.createFile(_sensorStatusHistCSVPath);

		//IO object to efficiently append
		//File ssOutCsv = new File(_sensorStatusHistCSVPath);
		

		//FileHandler.createFile(_faultHistCSVPath);
		
		 /*if(!ssOutCsv.exists()){
			 ssOutCsv.createNewFile();
    	  }
		  fw = new FileWriter(ssOutCsv,true);
		  bw = new BufferedWriter(fw);
		  pw = new PrintWriter(bw);

		*/  

		List<Algorithm> algorithms = ssHist.getPartitionKeys();
/*
		String ssHistHeading = "time tick,algorithm,vehicle,pgn,spn,avg zscore,zvalue,cosmo sensor flag\n";

		//FileHandler.append(_sensorStatusHistCSVPath,ssHistHeading.getBytes());
		pw.print(ssHistHeading);
		//iterate over each time step
		while(ssHistTimeIt.hasNext()) {
			TimerEvent timerEvent = ssHistTimeIt.next();
			int time = timerEvent.getTime();

			//iterate keys of sensor status history
			for(Algorithm a : algorithms) {
				Iterator<SensorStatusEvent> ssIt = ssHist.elementIterator(a, timerEvent);

				//iterate overt the sensor statuses
				while(ssIt.hasNext()) {


					SensorStatusEvent sse = ssIt.next();


					Vehicle v = sse.getVehicle();
					Sensor s = sse.getSensor();					
					Algorithm _a=sse.getAlgorithm();//although should be same as a, use the algorithm stored in event just in case					
					double zscore=sse.getZscore();				
					double zvalue=sse.getZvalue();					
					boolean cosmoSensorFlag=sse.isCosmoSensorFlag();



					String row = time+","+_a.getName()+","+v.getVid()+","+s.getPgn()+","+s.getSpn()+","+zscore+","+zvalue+","+cosmoSensorFlag+"\n";


					//FileHandler.append(_sensorStatusHistCSVPath,row.getBytes());
					pw.print(row);
				}
			}

		}//end iterate over timer ovents of repairs

		*/
		//  Summary stats of histories
		 

	//	pw.close();
	
		//FileHandler.createFile(_histSummaryPath);
		//IO object to efficiently append
		File summaryOutCsv = new File(_histSummaryPath);
		/*
		 * FAULTS
		 */

		//FileHandler.createFile(_faultHistCSVPath);
		
		 if(!summaryOutCsv.exists()){
			 summaryOutCsv.createNewFile();
    	  }
		  fw = new FileWriter(summaryOutCsv,true);
		  bw = new BufferedWriter(fw);
		  pw = new PrintWriter(bw);






		String summaryHistHeading = "algorithm,vehicle,number of faults, number of faults involving COMSO sensors, number of fault-involved sensors ,number of fault-involved COSMO sensors\n";

		//FileHandler.append(_histSummaryPath,summaryHistHeading.getBytes());
		pw.print(summaryHistHeading);


		//iterate keys of sensor status history
		for(Algorithm a : algorithms) {


			//iterate keys of fault history
			for(Vehicle v : vehicles) {

				//number of faults for this vehicle
				int totalNumFaults = 0;
				//number of times a fault occured and had a comso sensor in the fault-invovled sensors
				int totalNumFaultWithCOSMOSensor = 0;

				//the total number of sensors that were invovled in a fault 
				int totalNumFaultInvolvedSensors=0;
				//the total number of cosmo sensors that were fault involved
				int totalNumFaultInvolvedCOSMOSensors=0;
				


				Iterator<TimerEvent> fHistTimeIt =fHist.timerEventIterator(v);
				//iterate over each time step
				while(fHistTimeIt.hasNext()) {
					TimerEvent timerEvent = fHistTimeIt.next();

					Iterator<FaultDescription> fdIt = fHist.elementIterator(v, timerEvent);
					//iterate overt the faults
					while(fdIt.hasNext()) {

						totalNumFaults++;
						FaultDescription fd = fdIt.next();


						//String row = time+","+v.getVid()+","+fd.getLabel()+",";

						List<FaultInvolvedSensorBehavior> fiSensors = fd.getAffectedSensors();


						boolean faultInovledCosmoSensorPresent = false;
						//					String fiSensorCSVEntry = "";
						//iterate over fault-involved sensors
						for(FaultInvolvedSensorBehavior fiSensor: fiSensors) {
							totalNumFaultInvolvedSensors++;
							Sensor sensor = fiSensor.getAffectedSensor();
							//since it's a CSV file, can't seperate the PGN from SPN using ','.
							//instaed have following format: (PGN:SPN);(PGN:SPN);...;(PGN:SPN)





							Iterator<SensorStatusEvent> ssIt = ssHist.elementIterator(a, timerEvent); //use the time event from when fault occured


							//iterate overt the sensor statuses
							while(ssIt.hasNext()) {


								SensorStatusEvent sse = ssIt.next();


								Vehicle _v = sse.getVehicle();

								//only consider processing events for the given vehicle and algorithm
								if(!_v.equals(v)){
									continue;
								}

								Sensor s = sse.getSensor();

								//only consider processing events for the given vehicle and algorithm
								if(!s.equals(sensor)){
									continue;
								}


								boolean cosmoSensorFlag=sse.isCosmoSensorFlag();
								if (cosmoSensorFlag) {
									faultInovledCosmoSensorPresent = true;
									totalNumFaultInvolvedCOSMOSensors++;
								}
							}//end iterate the sensor status event to count number of cosmo sensors involved in fault



							//fiSensorCSVEntry = fiSensorCSVEntry + "("+sensor.getPgn()+":"+sensor.getSpn()+");"; 
						}//end iterate the fault invovled sensors

						if(faultInovledCosmoSensorPresent){
							totalNumFaultWithCOSMOSensor++;
						}



					}//end iterate faults

				}//end iterate over timer ovents of faults

				//""algorithm,vehicle,number of faults, number of faults involving COMSO sensors, number of fault-involved sensors ,number of fault-involved COSMO sensors\n";
				//number of faults for this vehicle
				//			int totalNumFaults = 0;
				//number of times a fault occured and had a comso sensor in the fault-invovled sensors
				//				int totalNumFaultWithCOSMOSensor = 0;

				//the total number of sensors that were invovled in a fault 
				//						int totalNumFaultInvolvedSensors=0;
				//the total number of cosmo sensors that were fault involved
				//int totalNumFaultInvolvedCOSMOSensors=0;

				String row = a.getName()+","+v.getVid()+","+totalNumFaults+","+totalNumFaultWithCOSMOSensor+","+totalNumFaultInvolvedSensors+","+totalNumFaultInvolvedCOSMOSensors+"\n";

				//FileHandler.append(_histSummaryPath,row.getBytes());
				pw.print(row);

			}//end iterate vehicles
		}//end iterate the algorithms
		
		pw.close();
	}
}
