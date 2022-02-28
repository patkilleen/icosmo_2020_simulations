package phase.configuration.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

/**
 * All the configuration file keys are defined here as constants. It is
 * an interface to the for read values from the configration xml file. 
 * The ConfigFactory implements privately an implementation of IConfig.
 * @author Patrick Killeen
 *
 */
public interface IConfig {
	
	public static final String PROPERTY_SIMULATION_LOADER_ROOT_DIRECTORY="global.simulation-loader.root-directory";
	public static final String PROPERTY_SIMULATION_LOADER_OS="global.simulation-loader.OS"; //LINUX or WINDOWS
	public static final String PROPERTY_SIMULATION_LOADER_ANALYSI_NUM_REPLAY_ITERATIONS="global.simulation-loader.analysis.replay.number.iterations";
	public static final String PROPERTY_SIMULATION_LOADER_HISTORY_MODE="global.simulation-loader.history.mode";//CREATE/READ/CONVERT_HISTORY_TO_CSV
	public static final String PROPERTY_SIMULATION_LOADER_HISTORY_INPUT_FILE="global.simulation-loader.history.input-file-path";
	public static final String PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_CREATION_MODE="global.simulation-loader.sensor-behavior.sensor-data-creation-mode";//DUPLICATE_PARAMETERS/READ_ALL_SENSOR_BEHAVIOR
	public static final String PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_WHITE_NOISE_FILE_PATH="global.simulation-loader.sensor-behavior.white-noise-file-path";
	public static final String PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_AMP_FACTOR_FILE_PATH="global.simulation-loader.sensor-behavior.amp-factor-file-path";
	public static final String PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR_FILE_PATH="global.simulation-loader.sensor-behavior.noise-amp-factor-file-path";
	public static final String PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_NOISE_PVALUE1_FILE_PATH="global.simulation-loader.sensor-behavior.noise-pvalue1-file-path";
	public static final String PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_NOISE_PVALUE2_FILE_PATH="global.simulation-loader.sensor-behavior.noise-pvalue2-file-path";
	public static final String PROPERTY_SIMULATION_LOADER_RAW_SENSOR_READING_FILTER_PVALUE="global.simulation-loader.raw-sensor-reading.filter-pvalue";//[value between 0 and 1]
	
	public static final String PROPERTY_SENSOR_WHITE_LIST="global.sensors.";
	
	public static final String PROPERTY_LOG_FILE_PATH="global.log.file-path";
	public static final String PROPERTY_LOG_LEVEL="global.log.level";
	
	public static final String PROPERTY_NUMBER_OF_VEHICLES="global.vehicle.number";
	public static final String PROPERTY_NUMBER_OF_ALGORITHMS="global.algorithm.number";
	public static final String PROPERTY_NUMBER_OF_TIME_TICKS="global.number.time-ticks";
	
	public static final String PROPERTY_ALGORITHM_DISTANCE_MEASURES="cosmo.algorithm.distance-metrics.";
	public static final String PROPERTY_ALGORITHM_NUMBER_SELECTED_SENSORS="cosmo.algorithm.selected-sensor.number";
	public static final String PROPERTY_ALGORITHM_RANDOM_SENSOR_SELECTION_FLAG="cosmo.algorithm.selected-sensor.random-enabled";
	public static final String PROPERTY_ALGORITHM_ZSCORE_UPDATE_FREQUENCY="cosmo.algorithm.zscore-update-frequency";
	
	public static final String PROPERTY_FAULT_TIMER_FAULT_DIRECTORY="fault-timer.faults.directory";
	
	public static final String PROPERTY_FAULT_FAULT_GENERATION_NOISE_CONFIG_FILE_PATH="fault.fault-generation-noise.config-file-path";
	public static final String PROPERTY_FAULT_REPAIR_GENERATION_NOISE_CONFIG_FILE_PATH="fault.repair-generation-noise.config-file-path";
	public static final String PROPERTY_FAULT_MINIMUM_DAYS_BEFORE_REPAIR="fault.min-day-before-repair";
	public static final String PROPERTY_FAULT_FAULT_DESCRIPTION_ID="fault.fault-description.id";
	
	public static final String PROPERTY_FAULT_SINGLE_SENSOR_FAULT_BLACK_WHITE_LIST="fault.single-sensor-fault-black-list.sensor.";
	
	public static final String PROPERTY_DATA_GENERATION_TIMER_DATA_GENERATION_SENSOR_DIRECTORY="data-generation-timer.data-generation-sensor.directory";
	public static final String PROPERTY_DATA_GENERATION_TIMER_BASE_NOISE_CONFIG_FILE_PATH="data-generation-timer.base-noise.config-file-path";
	public static final String PROPERTY_DATA_GENERATION_TIMER_J1939_SPEC_DOC_FILE_PATH="data-generation-timer.j1939-specificiation-document.file-path";
	public static final String PROPERTY_DATA_GENERATION_TIMER_J1939_DATA_DUMP_FILE_PATH="data-generation-timer.j1939-data-dump.file-path";
	
	public static final String PROPERTY_DATA_GENERATION_SENSOR_WHITE_NOISE_CONFIG_FILE_PATH="data-generation-sensor.white-noise.config-file-path";
	public static final String PROPERTY_DATA_GENERATION_SENSOR_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH="data-generation-sensor.amp-factor-fleet-average.config-file-path";
	public static final String PROPERTY_DATA_GENERATION_SENSOR_NOISE_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH="data-generation-sensor.noise-amp-factor-fleet-average.config-file-path";
	public static final String PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE1_AVERAGE_CONFIG_FILE_PATH="data-generation-sensor.noise-pvalue1-fleet-average.config-file-path";
	public static final String PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE2_AVERAGE_CONFIG_FILE_PATH="data-generation-sensor.noise-pvalue2-fleet-average.config-file-path";
	public static final String PROPERTY_DATA_GENERATION_SENSOR_SENSOR="data-generation-sensor.sensor";
	
	public static final String PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE="anomaly-detection-timer.zscore-window.size";
	
	public static final String PROPERTY_PERFORMANCE_ANALYSIS_TIMER_LEFT_WINDOW_SIZE="performance-analysis-timer.left-window.size";
	public static final String PROPERTY_PERFORMANCE_ANALYSIS_TIMER_RIGHT_WINDOW_SIZE="performance-analysis-timer.right-window.size";
	public static final String PROPERTY_PERFORMANCE_ANALYSIS_THRESHOLD_DECIMAL_PRECISION="performance-analysis-timer.threshold-decimal-precision";
	
	public static final String PROPERTY_ICOSMO_TIMER_MODE="icosmo-timer.mode";
	public static final String PROPERTY_ICOSMO_TIMER_LEFT_TIME_WINDOW="icosmo-timer.left-time-window";
	
	public static final String PROPERTY_ICOSMO_STALENESS_THRESHOLD="icosmo.staleness-theshold";
	public static final String PROPERTY_ICOSMO_CANDIDACY_THRESHOLD="icosmo.candidacy-theshold";
	public static final String PROPERTY_ICOSMO_CONTRIBUTION_DECREASE_MODIFIER="icosmo.modifier.contribution-decrease";
	public static final String PROPERTY_ICOSMO_CONTRIBUTION_INCREASE_MODIFIER="icosmo.modifier.contribution-increase";
	public static final String PROPERTY_ICOSMO_POTENTIAL_CONTRIBUTION_DECREASE_MODIFIER="icosmo.modifier.potential-contribution-decrease";
	public static final String PROPERTY_ICOSMO_POTENTIAL_CONTRIBUTION_INCREASE_MODIFIER="icosmo.modifier.potential-contribution-increase";
	public static final String PROPERTY_ICOSMO_DESIRED_RECALL="icosmo.desired-recall";
	public static final String PROPERTY_ICOSMO_DESIRED_PRECISION="icosmo.desired-precision";
	public static final String PROPERTY_ICOSMO_DEFAULT_CONTRIBUTION="icosmo.default.contribution";
	public static final String PROPERTY_ICOSMO_DEFAULT_POTENTIAL_CONTRIBUTION="icosmo.default.potential-contribution";
	public static final String PROPERTY_ICOSMO_NUMBER_FAULT_INVOLVED_SENSORS="icosmo.number.fault-involved-sensors";
	public static final String PROPERTY_ICOSMO_MAX_NUMBER_ADDED_SENSORS="icosmo.number.max-added-sensors";
	public static final String PROPERTY_ICOSMO_MAX_NUMBER_REMOVED_SENSORS="icosmo.number.max-removed-sensors";
	public static final String PROPERTY_ICOSMO_LOG_SENSOR_CHANGES="icosmo.log.sensor-changes";
	public static final String PROPERTY_ICOSMO_SENSOR_INTEREST_THRESHOLD="icosmo.sensor-interest-threshold";
	
	public static final String PROPERTY_FAULT_DESCRIPTIONS_DIRECTORY="fault-descriptions.directory";
	public static final String PROPERTY_FAULT_DESCRIPTION_ID="fault-description.id";
	public static final String PROPERTY_FAULT_DESCRIPTION_LABEL="fault-description.label";
	public static final String PROPERTY_FAULT_DESCRIPTIONS_SINGLE_SENSORS_BEHAVIOR_CONFIG_FILE_PATH = "fault-description.single-sensor.behavior.config-path";
	
	public static final String PROPERTY_SENSOR_BEHAVIOR_DIRECTORY="sensor-behavior.directory";
	public static final String PROPERTY_SENSOR_BEHAVIOR_SINGLE_SENSOR_FAILURE_CONFIG_DIRECTORY="sensor-behavior.single-sensor-failure.config-path";
	public static final String PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH="sensor-behavior.noise.config-path";
	public static final String PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR="sensor-behavior.amplification-factor";
	public static final String PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR="sensor-behavior.noise-amplification-factor";
	public static final String PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1="sensor-behavior.noise-pvalue-1";
	public static final String PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2="sensor-behavior.noise-pvalue-2";
	public static final String PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE="sensor-behavior.noise-type";
	public static final String PROPERTY_SENSOR_BEHAVIOR_SENSOR="sensor-behavior.sensor";
	
	public static final String PROPERTY_NOISE_MEAN="noise.mean";
	public static final String PROPERTY_NOISE_SD="noise.sd";
	public static final String PROPERTY_NOISE_MAX="noise.max";
	public static final String PROPERTY_NOISE_MIN="noise.min";
	public static final String PROPERTY_NOISE_DISTRIBUTION="noise.distribution";
	
	public static final String PROPERTY_OUTPUT_TIMER_EXPERIMENT_ID="output-timer.experiment-id";
	public static final String PROPERTY_OUTPUT_TIMER_INPUT_LOG_FILE_PATH="output-timer.input.log-file-path";
	public static final String PROPERTY_OUTPUT_TIMER_INPUT_ROC_SCRIPT_FILE_NAME="output-timer.input.roc-script-file-name";
	public static final String PROPERTY_OUTPUT_TIMER_OUTPUT_DIRECTORY="output-timer.output.directory";
	public static final String PROPERTY_OUTPUT_TIMER_OUTPUT_LOG_FILE_NAME="output-timer.output.log-file-name";
	public static final String PROPERTY_OUTPUT_TIMER_OUTPUT_CONFIG_FILE_NAME="output-timer.output.config-file-name";
	public static final String PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_CSV_FILE_NAME="output-timer.output.roc-csv-file-name";
	public static final String PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_IMAGE_FILE_NAME="output-timer.output.roc-image-file-name";
	public static final String PROPERTY_OUTPUT_TIMER_INPUT_FILES_DIRECTORY = "output-timer.intput-file-directory";
	public static final String PROPERTY_OUTPUT_TIMER_HISTORY_OUTPUT_FILE_NAME = "output-timer.history-output-file-name";
	public static final String PROPERTY_OUTPUT_TIMER_DATA_ANALYSIS_BATCH_SCRIPT_FILE_PATH = "output-timer.data-analysis-batch-script-file-path";
	public static final String PROPERTY_OUTPUT_TIMER_SAVE_COPY_OF_INPUT_FOLDER_FLAG = "output-timer.input.save-copy-of-input-folder-flag";
	
	
	
	public Properties getEntries();

	public void setEntries(Properties properties);

	public String getConfigFilePath();

	public void setConfigFilePath(String configFilePath);
	
	/**
	 * Returns the value of the entry with a given key. If the entry doesn't exist
	 * an {@code IllegalArgumentException} is thrown.
	 * @param key The key the entry has in the configuration File.
	 * @return The value of the specified entry.
	 */
	public String getPropertyAndValidate(String key);
	
	/**
	 * Returns the value of the entry with a given key.
	 * @param key The key the entry has in the configuration File.
	 * @return The value of the specified entry.
	 */
	public String getProperty(String key);
	
	public void setProperty(String key,String value);
	

	/**
	 * Returns a variable number of entries.
	 * @param key The key that all entries start with. Example {@code "many.values."}.
	 * @return List of values.
	 */
	public List<String> getProperties(String key);
	
	/**
	 * prases a double from the configuration xml properties file of a specific entry.
	 * @param key the key to property to convert to double
	 * @return double representation of property's value in configuration file. 
	 */
	public double getDoubleProperty(String key);
		
	/**
	 * Reads all the double string entries and converts to doubles
	 * @param key the key to property to convert to doubles (see getProperties)
	 * @return doubles of property's value in configuration file. 
	 */
	public List<Double> getDoubleProperties(String key);
	
	
	/**
	 * prases an int from the configuration xml properties file of a specific entry.
	 * @param key the key to property to convert to int
	 * @return integer representation of property's value in configuration file. 
	 */
	public int getIntProperty(String key);
	
	
	/**
	 * prases a boolean from the configuration xml properties file of a specific entry.
	 * @param key the key to property to convert to boolean
	 * @return boolean representation of property's value in configuration file. 
	 */
	public boolean getBooleanProperty(String key);
	
	/**
	 * 
	 * Reads all the int string entries and converts to integers
	 * @param key the key to property to convert to ints (see getProperties)
	 * @return integers of property's value in configuration file. 
	 */
	public List<Integer> getIntProperties(String key);
	
	/**
	 * Loads a configuration object by retrieving the target config file 
	 * path found in this config properties. Then loads the configuration 
	 * file from the path and returns the config object.
	 * @param key the key to target config file path found in this config's properties 
	 * @return target configuration object
	 */
	public IConfig loadTargetConfigFile(String key);
	
	
	/**
	 * Reads all the config file path entries and converts to configuration object
	 * @param key the key to property to convert to ints (see getProperties)
	 * @return configuration files
	 */
	public List<IConfig> loadTargetConfigFiles(String key);
	
	
	/**
	 * Creates a list of configuration objects by loading all xml files in a directory
	 * @param directory the directory to search for xml files
	 * @return list of configuration objects of xml files
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public  List<IConfig> loadConfigFilesInDirectory(String directory) throws InvalidPropertiesFormatException, IOException;
}
