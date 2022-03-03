This Java eclipse project implements the predictive maintenance fleet management simulations done in <a href="https://ruor.uottawa.ca/handle/10393/40086">my thesis</a>.

It simulates a fleet of buses using an input J1939 packet dataset (the dataset used in my thesis is private, so I only include a sample dataset). From this data, many buses are simulated and faults periodically occur. The COSMO approach and, my proposed approach, ICOSMO, are run to detect deviation in response to faults and are evaluated.

The project outputs evaluation metrics (true positives, false positives, etc.) and are fed into an R programming project to compute the area under the ROC curves.


### Steps to run:


- 1. Make sure you have the parse J1939 specification document: 'j1939-spec-doc.csv' (if not, you need to buy it from <a href="https://www.sae.org/standards/content/j1939da_201710/">here</a> and parse the desired columns and rows into the desired format)

- 2. Prepare your J1939 data set to match the format found in the example dataset file named '20181123_102054', where the 'ellapsedTime' column is in milliseconds

- 3. configure the project using the input/config.xml file to desired behaviour  (specifying the sensor white-list and number of vehicles to simulate, for example)

- 4. Change the "global.simulation-loader.root-directory" entry to match the root directory of your project

- 5. To run the project, the main function expects 1 argument, the file path to your configuration file (input/config.xml)

- 6. Make sure the input/run.bat file (the file to run the R script) is properly configured for your system so it can run the target R script

### Main Arguments

A list of classes that define a main function are listed here.

- core.Main: this is the core main class to run all the simulations. Arguments are file paths to configuration files. Each configuration file specified will be processed sequentially.
- phase.analysis.output.ResultAggregator aggregates all the results from multiple simulations into a single CSV
  - argument 1: '-ag' or  '-o' 
  - for argument '-ag':
    - argument 2: path to the output directory that contains the simulations results from multiple simulations. For example the path specified by the *output-timer.output.directory* property
    - argument 3: the path to the AUC CSV result file. For example, 'roc-curve.png.csv' if the *output-timer.output.roc-image-file-name* property was 'roc-curve.png'
	- argument 4: the file path to the output CSV that summarizes the results
  - for argument '-o':
    - argument 2: source directory path that contains the simulations results from multiple simulations. For example the path specified by the *output-timer.output.directory* property
    - argument 3: target directory path to organize all the results (subdirectories will be created in this directory and the results in directory from argument 2 will be copied over)
	- argument 4: the size of the bin (the number of output folders that should be contained in one subdirectory)
	- argument 5: the ouput sub directory prefix
	
	
#### Basic Example
- core.Main /home/username/icosmo_sim/input/config.xml: would run the simulations specified by the config file in the input directory of icosmo_sim project
- phase.analysis.output.ResultAggregator -ag /home/username/icosmo_sim/output roc-curve.png.csv /home/username/icosmo_sim/output/summary.csv: would summarized all the results in the output folder into a summary CSV file
- phase.analysis.output.ResultAggregator -o /home/username/icosmo_sim/output /home/username/icosmo-analysis/ 15 -run: would iterate over each output folder in /home/username/icosmo_sim/output, creating subdirectories named 1-run, 2-run, 3-run, etc. and filling these directories with 15 result output directries

#### Complete Example
Here we run all three different main  runs to create a simulation, organize the results, and then analyze the results in each sub directory. Future work is to automate this process so it is performed by simply running a simulation.

Below we run 3 sets of experiments, each creating 10 output folders
- core.Main /home/username/icosmo_sim/input/configs/config-1.xml core.Main /home/username/icosmo_sim/input/configs/config-2.xml core.Main /home/username/icosmo_sim/input/configs/config-3.xml
Below we copy the output over into a directory to organize the 30 output directories into 3 sub-directories (one for each experiment-set ran)
- phase.analysis.output.ResultAggregator -o /home/username/icosmo_sim/output /home/username/icosmo-analysis/ 10 -run
Below we analyze the results by aggregating results for each sub directory explicitly
- phase.analysis.output.ResultAggregator -ag /home/username/icosmo-analysis/1-run  roc-curve.png.csv /home/username/icosmo-analysis/1-run/summary.csv
- phase.analysis.output.ResultAggregator -ag /home/username/icosmo-analysis/2-run  roc-curve.png.csv /home/username/icosmo-analysis/2-run/summary.csv
- phase.analysis.output.ResultAggregator -ag /home/username/icosmo-analysis/3-run  roc-curve.png.csv /home/username/icosmo-analysis/3-run/summary.csv


### Configuration file properties
Below is a list all the properties in the XML configuration file, along with their name, values, and a description, with the following format: **property name**: (possibles values), a description of the property.

- **global.log.level**: (DEBUG,INFO,WARNING,ERROR,FATAL) used to filter the types (and frequency) of messages logged. DEBUG will log everything, so INFO is recommended.
- **global.simulation-loader.analysis.replay.number.iterations**: (integer > 0) when *global.simulation-loader.history.mode"* = CREATE, specifies how many histories/simulations to create. When *global.simulation-loader.history.mode"* = READ, specifies how many iterations of ICOSMO will be run on a single history (specified by *global.simulation-loader.history.input-file-path*).
- **global.simulation-loader.OS**: (LINUX,WINDOWS) LINUX: any '\' or '/' in file path config file property values will be replaced with '/'. WINDOWS: any '\' or '/' in file path properties will be replaced with '\'.
- **global.simulation-loader.root-directory**: (string) the directory path where the ICOSMO simulation java project is found
- **global.simulation-loader.history.mode**: (CREATE/READ/CONVERT_HISTORY_TO_CSV):
  - CREATE: creates a history
  - READ: reads a history specified by *global.simulation-loader.history.input-file-path* to run ICOSMO simulations
  - CONVERT_HISTORY_TO_CSV: no simulations are run. Converts a history history specified by *global.simulation-loader.history.input-file-path* to 3 CSV files with the same file path as read history, but with the following suffixes: "*.fault.csv","*.reparis.csv", and "*.summary.csv"
- **global.simulation-loader.history.input-file-path**: (string) file path to history to be read when **global.simulation-loader.history.mode** = READ or CONVERT_HISTORY_TO_CSV
- **global.simulation-loader.sensor-behavior.sensor-data-creation-mode**: () TODO: fill out this description
- **global.simulation-loader.sensor-behavior.white-noise-file-path**: () TODO: fill out this description
- **global.simulation-loader.sensor-behavior.amp-factor-file-path**: () TODO: fill out this description
- **global.simulation-loader.sensor-behavior.noise-amp-factor-file-path**: () TODO: fill out this description
- **global.simulation-loader.sensor-behavior.noise-pvalue1-file-path**: () TODO: fill out this description
- **global.simulation-loader.sensor-behavior.noise-pvalue2-file-path**: () TODO: fill out this description
- **global.simulation-loader.raw-sensor-reading.filter-pvalue**: (float between 0 and 1) the fraction of sensor data samples taken from the data dump file specified by *data-generation-timer.j1939-data-dump.file-path*. E.g., 0.92 means 92% of the dataset will be included in the simulated sensor data generation
- **global.sensors.i (where i = 0,1,2...,9)**: ((PGN,SPN)) list of sensors that are part of the simulation and found in *data-generation-timer.j1939-data-dump.file-path* dataset.
- **global.log.file-path**: (string) path to log file. If you run multiple ICOSMO simulations, make sure they output to different log files using this property
- **global.vehicle.number**: (integer > 0) number of vehicles in the simulation. The simulation execution duration, memory requirement, and size increases significantly by changing this parameter (48 vehicles may take days for a single simulation to end, for example), so be careful
- **global.algorithm.number**: (integer > 0) 
  - number of histograms distance measures used (number of COSMO instances and ICOSMO instances) to run.
  - It must match the number of algorithms specified in *cosmo.algorithm.distance-metrics.i*. 
  - A thread is created for histogram distance for both COSMO and ICOSMO (and Random COSMO, if it is defined (see *cosmo.algorithm.selected-sensor.random-enabled*)).
  - The simulation execution duration, memory requirement, and size increases significantly by changing this parameter 
- **global.number.time-ticks**: (integer > 0) number of days (time units) the simulation runs for. The simulation execution duration and size increase with this parameter (e.g., 60 could take a few minutes while 1400 could take many hours).
- **cosmo.algorithm.distance-metrics.i (where i = 0,1,2...,9)**: (HELLINGER,EUCLIDEAN,CITY_BLOCK,GOWER,SQUARED_EUCLIDEAN,INTERSECTION,FIDELITY,MATUSITA,COSINE,BHATTACHARYYA) the algorithm/histogram-distance to include in the simulation. The number of histogram distance must match the number specified by *global.algorithm.number*
- **cosmo.algorithm.selected-sensor.random-enabled**: (boolean) true when we enable Random COSMO, where an instance of COSMO for every hitogram distance defined by cosmo.algorithm.distance-metrics.i is included where the sensors are randomly selected, and false to not include Random COSMO.
- **cosmo.algorithm.selected-sensor.number**: (integer > 0) number of sensors initially selected by COSMO
- **cosmo.algorithm.zscore-update-frequency**: (integer > 0) number of days the histograms are gathered before creating the model distance matrix that is used by the Most Central Pattern method
- **fault-timer.faults.directory**: () TODO: fill out this description
- **fault.single-sensor-fault-black-list.sensor.i (where i = 0,1,2...)**: ((PGN,SPN)) list of sensors to prevent from having single-sensor faults
- **data-generation-timer.data-generation-sensor.directory**:() TODO: fill out this description
- **data-generation-timer.base-noise.config-file-path**:() TODO: fill out this description
- **data-generation-timer.j1939-specificiation-document.file-path**:(string) file path the J1939 specification document subset
- **data-generation-timer.j1939-data-dump.file-path**: (string) file path to J1939 dataset
- **anomaly-detection-timer.zscore-window.size**: (integer > 0) zscore moving average window size
- **performance-analysis-timer.left-window.size**:() TODO: fill out this description
- **performance-analysis-timer.right-window.size**:() TODO: fill out this description
- **performance-analysis-timer.threshold-decimal-precision**:(integer > 0) number of decimals used when considering all possible deviation thresholds when computing ROC curves.
  - Execution time for ICOSMO analysis increases signficantly as this is increased 
- **icosmo-timer.mode**: (PRECISION,RECALL) speficies if we are simulating BBIRA precision or recall.
  -PRECISION: we are simulating precision when estimating the fault-involved sensors, and precision is specified by *icosmo.desired-precision*
  -RECALL: we are simulating recall when estimating the fault-involved sensors, and recall is specified by *icosmo.desired-recall*
- **icosmo-timer.left-time-window**: (integer >= 0) specifies how far in the past ICOSMO looks for a deviation when a repair occurred
- **icosmo.staleness-theshold**: (float) a sensor class is considered stale if the average sensor contribution of its sensor instances is below this threshold
- **icosmo.candidacy-theshold**: (float) a sensor class is considered a candidate if the average sensor potential contribution of its sensor instances is above this threshold
- **icosmo.modifier.contribution-decrease**: (float) amount the sensor contribution of a COSMO sensor instance is decreased by ICOSMO when it missed a fault
- **icosmo.modifier.contribution-increase**: (float) amount the sensor contribution of a COSMO sensor instance is increased by ICOSMO when it detected a fault
- **icosmo.modifier.potential-contribution-decrease**: (float) amount the sensor potential contribution of a non-COSMO sensor instance is decreased by ICOSMO when another COSMO sensor detected a fault
- **icosmo.modifier.potential-contribution-increase**: (float) amount the sensor potential contribution of a non-COSMO sensor instance is increased by ICOSMO when no other COSMO sensor detected a fault
- **icosmo.desired-recall**: (float between 0 and 1) specifies the average recall of the simulated BBIRA when estimating fault-involved sensors (only used when  *icosmo-timer.mode* = RECALL)
- **icosmo.desired-precision**: (float between 0 and 1) specifies the average precision of the simulated BBIRA when estimating fault-involved sensors (only used when  *icosmo-timer.mode* = PRECISION)
- **icosmo.default.contribution**: (float) the default value for the sensor contribution of sensor instances
- **icosmo.default.potential-contribution**: (float) the default value for the sensor potential contribution of sensor instances
- **icosmo.number.fault-involved-sensors**: (integer > 0) the maximum number of fault-involved sensors to consider when running the BBIRA
- **icosmo.number.max-added-sensors**: (integer >= 0) the maximum number of candidate sensors that can be added by ICOSMO 
- **icosmo.number.max-removed-sensors**: (integer >= 0) the maximum number of stale sensors that can be removed by ICOSMO
- **icosmo.log.sensor-changes**: (boolean) true: any change to sensors select by ICOSMO will be logged. false: the sensors changes are not logged. 
  - This will signficantly increase the output size of the simulation's log file for big simulations
- **icosmo.sensor-interest-threshold**: (float between 0 and 1) candidate sensors are only added to COSMO sensors if their sensor interestingness is below this threshold. Lower values favor on interesting sensors be added, while higher values makes less interesting sensors able to be added. A value of 1 means all candidate sensors can be added.
- **fault-descriptions.directory**:() TODO: fill out this description
- **fault-description.single-sensor.behavior.config-path**:() TODO: fill out this description
- **output-timer.experiment-id**: (integer) the id added to the experiment id column when results are output
- **output-timer.input.roc-script-file-name**: (string) file path to R script that reads the output file specified by *output-timer.output.roc-csv-file-name* and creates the AUC results output into file specified by *output-timer.output.roc-image-file-name* with an additional '.csv' suffix
- **output-timer.output.directory**: (string) directory path where output files are created 
- **output-timer.output.log-file-name**: (string) the file name of the copy of the log output at end of simulation
- **output-timer.output.config-file-name**: (string) the file name of the copy of the config file output at end of simulation 
- **output-timer.output.roc-csv-file-name**: (string) file name of raw output results
- **output-timer.output.roc-image-file-name**: (string) file name of the AUC .csv results  (.csv will be appended to this name) 
- **output-timer.intput-file-directory**: (string) file path to direction of input files that will be copied in output if *output-timer.input.save-copy-of-input-folder-flag* is true
- **output-timer.history-output-file-name**: (string) file name of history created when *global.simulation-loader.history.mode* = CREATE
- **output-timer.data-analysis-batch-script-file-path**: (string) file path to Batch/shell script that runs the R script specified by *output-timer.input.roc-script-file-name*
- **output-timer.input.save-copy-of-input-folder-flag**: (boolean) true: a copy of the input directory specified by *output-timer.intput-file-directory* will be saved in output. false: the input directory isn't saved.