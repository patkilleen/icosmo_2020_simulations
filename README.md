This Java eclipse project implements the predictive maintenance fleet management simulations done in <a href="https://ruor.uottawa.ca/handle/10393/40086">my thesis</a>.

It simulates a fleet of buses using an input J1939 packet dataset (the dataset used in my thesis is private, so I only include a sample dataset). From this data, many buses are simulated and faults periodically occur. The COSMO approach and, my proposed approach, ICOSMO, are run to detect deviation in response to faults and are evaluated.

The project outputs evaluation metrics (true positives, false positives, etc.) and are fed into an R programming project to compute the area under the ROC curves.


Steps to run:


1. Make sure you have the parse J1939 specification document: 'j1939-spec-doc.csv' (if not, you need to buy it from <a href="https://www.sae.org/standards/content/j1939da_201710/">here</a> and parse the desired columns and rows into the desired format)

2. Prepare your J1939 data set to match the format found in the example dataset file named '20181123_102054', where the 'ellapsedTime' column is in milliseconds

3. configure the project using the input/config.xml file to desired behaviour  (specifying the sensor white-list and number of vehicles to simulate, for example)

4. Change the "global.simulation-loader.root-directory" entry to match the root directory of your project

5. To run the project, the main function expects 1 argument, the file path to your configuration file (input/config.xml)

65Make sure the input/run.bat file (the file to run the R script) is properly configured for your system so it can run the target R script

PS. the simulations may take up a lot of memory and time, so you will want to tune the simulations via the "global.vehicle.number", "global.number.time-ticks", and "global.simulation-loader.raw-sensor-reading.filter-pvalue" properties, defining the number of vehicles in the fleet, the number of simulated days, and the probability to exclude a data reading from the sampled dataset, respectively.