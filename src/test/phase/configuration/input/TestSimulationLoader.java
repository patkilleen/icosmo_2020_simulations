package test.phase.configuration.input;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import common.Algorithm;
import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.SensorBehavior;
import common.SensorMap;
import common.Vehicle;
import common.event.HistoryEvent;
import common.event.PerformanceMetricEvent;
import common.event.ROCCurvePointEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.HistoryInputStream;
import common.event.stream.HistoryOutputStream;
import common.event.stream.HistoryStreamManager;
import common.event.stream.PerformanceMetricInputStream;
import common.event.stream.PerformanceMetricOutputStream;
import common.event.stream.PerformanceMetricStreamManager;
import common.event.stream.ROCCurvePointInputStream;
import common.event.stream.ROCCurvePointOutputStream;
import common.event.stream.ROCCurvePointStreamManager;
import junit.framework.Assert;
import phase.analysis.PerformanceAnalysisTimer;
import phase.analysis.icosmo.ICOSMO;
import phase.analysis.icosmo.ICOSMOTimer;
import phase.analysis.icosmo.ICOSMOTimer.Mode;
import phase.analysis.output.OutputTimer;
import phase.configuration.input.Configuration;
import phase.configuration.input.IConfig;
import phase.configuration.input.SimulationLoader;
import phase.generation.cosmo.AnomalyDetectionAlgorithm;
import phase.generation.cosmo.AnomalyDetectionModelTimer;
import phase.generation.cosmo.COSMO;
import phase.generation.cosmo.Histogram;
import phase.generation.cosmo.MinMaxPair;
import phase.generation.cosmo.SensorDescription;
import phase.generation.data.DataGenerationSensor;
import phase.generation.data.DataGenerationTimer;
import phase.generation.data.DataGenerationVehicle;
import phase.generation.fault.Fault;
import phase.generation.fault.FaultGenerationVehicle;
import phase.generation.fault.FaultTimer;
import phase.generation.history.FaultHistory;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class TestSimulationLoader extends SimulationLoader {


	@Test
	public void testBuildPerformanceAnalysisTimer() {

		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));

		FaultHistory fh = new FaultHistory(vehicles);
		RepairHistory rh = new RepairHistory(vehicles);

		Vehicle v0 = new Vehicle(0);
		Vehicle v1 = new Vehicle(1);

		fh.recordElement(v0,new TimerEvent(0), f1);
		fh.recordRepair(v0, new TimerEvent(5), f1);
		rh.recordElement(v0, new TimerEvent(5), new RepairEvent(v0,f1));

		fh.recordElement(v1,new TimerEvent(3), f1);
		fh.recordRepair(v1, new TimerEvent(7), f1);
		rh.recordElement(v1, new TimerEvent(7), new RepairEvent(v1,f1));

		//creates history as: [(vehicle 0,time: 0, zscore: 0.0), (vehicle 1: time: 0 zscore: 0.01),( vehile 0 time: 1, zscore 0.02), (vehicle 1 time 1: zscore 0.03)... (vehicle 0 time 49: zscore: 0.97), (vehicle 1 time: 49 zscore 0.98), (vehicle 0 time 50; 0.99, time 50)]


		HistoryInputStream hIn = new HistoryInputStream();
		HistoryOutputStream hOut = new HistoryOutputStream();
		HistoryStreamManager hManager = new HistoryStreamManager(hIn,hOut);

		ROCCurvePointOutputStream rocOut = new ROCCurvePointOutputStream(algs);
		ROCCurvePointInputStream rocIn = new ROCCurvePointInputStream(algs);
		ROCCurvePointStreamManager rocManager = new ROCCurvePointStreamManager(rocIn,rocOut);

		PerformanceMetricOutputStream performanceMetricOutputStream = new PerformanceMetricOutputStream(algs);
		PerformanceMetricInputStream performanceMetricInputStream = new PerformanceMetricInputStream(algs);
		PerformanceMetricStreamManager performanceMetricManager = new PerformanceMetricStreamManager(performanceMetricInputStream,performanceMetricOutputStream);


		hOut.writeHistoryEvent(new HistoryEvent(ssh,fh,rh));
		hManager.flush();

		TestConfig config = new TestConfig();
		/*
		 * int leftWindowSize = config.getIntProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_TIMER_LEFT_WINDOW_SIZE);
		int rightWindowSize = config.getIntProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_TIMER_RIGHT_WINDOW_SIZE);
		 */
		config.addProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_TIMER_LEFT_WINDOW_SIZE,"0");
		config.addProperty(IConfig.PROPERTY_PERFORMANCE_ANALYSIS_TIMER_RIGHT_WINDOW_SIZE,"0");
		PerformanceAnalysisTimer t = TestSimulationLoader.buildPerformanceAnalysisTimer(algs, config);
		//t.init(0, 0);
		//t.setParitionKeys(algs);
		t.initStreams(rocIn, performanceMetricOutputStream,hIn);

		for(Algorithm alg: algs){
			int time = -1;
			List<TimeStampedSensorStatusEvent> events = new ArrayList<TimeStampedSensorStatusEvent>(100);
			for(int i = 0; i < 100; i ++){
				double zscore = 0.01 * (double)i;

				if((i%2)== 0){
					time++;
				}

				//SensorStatusEvent e = new SensorStatusEvent(new Vehicle(i%2), new Sensor(0,0),alg,zscore,true,0.0);
				events.add(new TimeStampedSensorStatusEvent(new Vehicle(i%2),new Sensor(0,0),alg,zscore,true,0.00,new TimerEvent(time)));
				//ssh.recordElement(alg,new TimerEvent(time),e);	
			}

			for(int i = 0; i < 99; i ++){
				double zscore = 0.01 * (double)i;

				ROCCurvePointEvent res = new ROCCurvePointEvent(zscore,events);
				rocOut.write(alg, res);
			}
		}

		for(Algorithm alg: algs){
			int time = -1;
			List<TimeStampedSensorStatusEvent> events = new ArrayList<TimeStampedSensorStatusEvent>(100);
			for(int i = 0; i < 100; i ++){
				double zscore = 0.5;

				if((i%2)== 0){
					time++;
				}

				//SensorStatusEvent e = new SensorStatusEvent(new Vehicle(i%2), new Sensor(0,0),alg,zscore,true,0.0);
				events.add(new TimeStampedSensorStatusEvent(new Vehicle(i%2),new Sensor(0,0),alg,zscore,true,0.00,new TimerEvent(time)));
				//ssh.recordElement(alg,new TimerEvent(time),e);	
			}

			ROCCurvePointEvent res = new ROCCurvePointEvent(0.4,events);
			rocOut.write(alg, res);

		}
		rocManager.flush();


		//t.tick(new TimerEvent(0));
		t.setFaultHistory(fh);

		for(Algorithm alg : algs){
			List<PerformanceMetricEvent> events = t.computeICOSMORocCurvePoints(alg);
			Assert.assertEquals(100, events.size());

			//the first metric should be threshold 0
			PerformanceMetricEvent e = events.get(0);

			Assert.assertEquals(0.0,e.getVaryingThreshold());
			//only 1 zscore will be considered deviation, that's on day 0, which catches a  repair
			//so
			//vehicle 0
			//tp 1
			//fp 0
			//tn 44
			//fn 5

			//vehicle 1
			//tp 0
			//fp 0
			//tn 45
			//fn 5

			int expectedTP = 1;
			int expectedFP = 0;
			int expectedTN = 44+45;//all days after 5th, ie 6-50
			int expectedFN = 5+5;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(1);

			Assert.assertEquals(0.01,e.getVaryingThreshold(),0.0001);
			//only 1 zscore will be considered deviation, that's on day 0, which catches a  repair
			//so
			//vehicle 0
			//tp 1
			//fp 0
			//tn 44
			//fn 5

			//vehicle 1
			//tp 0
			//fp 1
			//tn 44
			//fn 5

			expectedTP = 1;
			expectedFP = 1;
			expectedTN = 44+44;//all days after 5th, ie 6-50
			expectedFN = 5+5;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(2);

			Assert.assertEquals(0.02,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 2
			//fp 0
			//tn 44
			//fn 4

			//vehicle 1
			//tp 0
			//fp 1
			//tn 44
			//fn 5

			expectedTP = 2;
			expectedFP = 1;
			expectedTN = 44+44;//all days after 5th, ie 6-50
			expectedFN = 5+4;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			//index 0, time 0, threshold 0.0
			//index 1, time 0, threshold 0.01
			//index 2, time 1, threshold 0.02
			//index 3, time 1, threshold 0.03
			//index 4, time 2, threshold 0.04
			//index 5, time 2, threshold 0.05
			//index 6, time 3, threshold 0.06
			//index 7, time 3, threshold 0.07
			//index 8, time 4, threshold 0.08
			//index 9, time 4, threshold 0.09
			//index 10, time 5, threshold 0.1
			//index 11, time 5, threshold 0.11
			//index 12, time 6, threshold 0.12
			e = events.get(11);
			Assert.assertEquals(0.11,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 6
			//fp 0
			//tn 44
			//fn 0

			//vehicle 1
			//tp 3
			//fp 3
			//tn 42
			//fn 2

			expectedTP = 6+3;
			expectedFP = 3;
			expectedTN = 42+44;//all days after 5th, ie 6-50
			expectedFN = 2;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(25);
			Assert.assertEquals(0.25,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 6
			//fp 6
			//tn 38
			//fn 0

			//vehicle 1
			//tp 5
			//fp 9
			//tn 36
			//fn 0

			expectedTP = 6+5;
			expectedFP = 6+9;
			expectedTN = 38+36;//all days after 5th, ie 6-50
			expectedFN = 0;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());

			e = events.get(98);
			Assert.assertEquals(0.98,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 6
			//fp 43
			//tn 1
			//fn 0

			//vehicle 1
			//tp 5
			//fp 45
			//tn 0
			//fn 0

			expectedTP = 6+5;
			expectedFP = 43+45;
			expectedTN = 1;//all days after 5th, ie 6-50
			expectedFN = 0;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());

			e = events.get(99);
			Assert.assertEquals(0.4,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 0
			//fp 0
			//tn 44
			//fn 6

			//vehicle 1
			//tp 0
			//fp 0
			//tn 45
			//fn 5

			expectedTP = 0;
			expectedFP = 0;
			expectedTN =45+44;//all days after 5th, ie 6-50
			expectedFN = 5+6;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());
		}

	}


	@Test
	public void testBuildICOSMOTimer_and_ICOSMO() {
		//String enumType = config.getProperty(IConfig.PROPERTY_ICOSMO_TIMER_MODE);

		//int leftTimeWindow = config.getIntProperty(IConfig.PROPERTY_ICOSMO_TIMER_LEFT_TIME_WINDOW);

		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;
		int numFaultInvolvedSensorEstimation=5;
		int zValueWindowSize = 30;
		int maxNumAddedSensor = 50;
		int maxNumRemovedSensor = 51;
		ICOSMO expectedIcosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,maxNumAddedSensor,maxNumRemovedSensor);

		List<Algorithm> algs =  new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));


		Mode expectedMode = ICOSMOTimer.Mode.RECALL;

		TestConfig config = new TestConfig();


		config.addProperty(IConfig.PROPERTY_ICOSMO_TIMER_MODE,"RECALL");
		config.addProperty(IConfig.PROPERTY_ICOSMO_TIMER_LEFT_TIME_WINDOW,"1");
		config.addProperty(IConfig.PROPERTY_ICOSMO_STALENESS_THRESHOLD,"10");
		config.addProperty(IConfig.PROPERTY_ICOSMO_CANDIDACY_THRESHOLD,"11");
		config.addProperty(IConfig.PROPERTY_ICOSMO_DEFAULT_CONTRIBUTION,"5");
		config.addProperty(IConfig.PROPERTY_ICOSMO_DEFAULT_POTENTIAL_CONTRIBUTION,"7");
		config.addProperty(IConfig.PROPERTY_ICOSMO_CONTRIBUTION_DECREASE_MODIFIER,"1");
		config.addProperty(IConfig.PROPERTY_ICOSMO_CONTRIBUTION_INCREASE_MODIFIER,"2");
		config.addProperty(IConfig.PROPERTY_ICOSMO_POTENTIAL_CONTRIBUTION_DECREASE_MODIFIER,"3");
		config.addProperty(IConfig.PROPERTY_ICOSMO_POTENTIAL_CONTRIBUTION_INCREASE_MODIFIER,"4");
		config.addProperty(IConfig.PROPERTY_ICOSMO_DESIRED_RECALL,"0.5");
		config.addProperty(IConfig.PROPERTY_ICOSMO_DESIRED_PRECISION,"0.6");
		config.addProperty(IConfig.PROPERTY_ICOSMO_NUMBER_FAULT_INVOLVED_SENSORS,"5");
		config.addProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE,"30");
		config.addProperty(IConfig.PROPERTY_ICOSMO_MAX_NUMBER_ADDED_SENSORS,"50");
		config.addProperty(IConfig.PROPERTY_ICOSMO_MAX_NUMBER_REMOVED_SENSORS,"51");

		ICOSMOTimer  t = TestSimulationLoader.buildICOSMOTimer(algs,vehicles,sensors,config);


		Assert.assertNotNull(t.getRepairInputStream());
		Assert.assertNotNull(t.getSensorStatusInputStream());
		Assert.assertNotNull(t.getTimeStampedSensorStatusOutputStream());
		Assert.assertEquals(expectedMode,t.getMode());
		Assert.assertEquals(1,t.getLeftTimeWindowDeviations());
		List<Sensor> actualSensors = t.getSensors();
		Assert.assertEquals(sensors.size(),actualSensors.size());
		for(Sensor s : sensors){
			Assert.assertEquals(true,actualSensors.contains(s));
		}

		List<Vehicle> actualVehicles = t.getVehicles();
		Assert.assertEquals(vehicles.size(),actualVehicles.size());
		for(Vehicle v : vehicles){
			Assert.assertEquals(true,actualVehicles.contains(v));
		}

		List<Algorithm> actualAlgs = t.getThreadPartitionKeys();
		Assert.assertEquals(algs.size(),actualAlgs.size());
		for(Algorithm a : algs){
			Assert.assertEquals(true,actualAlgs.contains(a));
		}


		SensorMap map = t.getSensorMap();
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s : sensors){
					Assert.assertNotNull(map.getSensorInstance(alg, v, s));
				}
			}
		}

		ICOSMO actualIcosmo = t.getIcosmo();
		Assert.assertEquals(expectedIcosmo.getCandicacyThreshold(),actualIcosmo.getCandicacyThreshold());
		Assert.assertEquals(expectedIcosmo.getContributionDecreaseMod(),actualIcosmo.getContributionDecreaseMod());
		Assert.assertEquals(expectedIcosmo.getContributionIncreaseMod(),actualIcosmo.getContributionIncreaseMod());
		Assert.assertEquals(expectedIcosmo.getDefaultContribution(),actualIcosmo.getDefaultContribution());
		Assert.assertEquals(expectedIcosmo.getDefaultPotentialContribution(),actualIcosmo.getDefaultPotentialContribution());
		Assert.assertEquals(expectedIcosmo.getDesiredPrecision(),actualIcosmo.getDesiredPrecision());
		Assert.assertEquals(expectedIcosmo.getDesiredRecall(),actualIcosmo.getDesiredRecall());
		Assert.assertEquals(expectedIcosmo.getMaxNumberAddedSensors(),actualIcosmo.getMaxNumberAddedSensors());
		Assert.assertEquals(expectedIcosmo.getMaxNumberRemovedSensors(),actualIcosmo.getMaxNumberRemovedSensors());
		Assert.assertEquals(expectedIcosmo.getNumFaultInvolvedSensorEstimation(),actualIcosmo.getNumFaultInvolvedSensorEstimation());
		Assert.assertEquals(expectedIcosmo.getPotentialContrDecreaseMod(),actualIcosmo.getPotentialContrDecreaseMod());
		Assert.assertEquals(expectedIcosmo.getPotentialContrIncreaseMod(),actualIcosmo.getPotentialContrIncreaseMod());
		Assert.assertEquals(expectedIcosmo.getStalnessThreshold(),actualIcosmo.getStalnessThreshold());
		Assert.assertEquals(expectedIcosmo.getzValueWindowSize(),actualIcosmo.getzValueWindowSize());

	}
/*
	@Test
	public void testBuildAnomalyDetectionModelTimer() {
		/*
		int zvalueWindowSize = config.getIntProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE);

		return new AnomalyDetectionModelTimer(algs, zvalueWindowSize);

		List<Algorithm> algs = new ArrayList<Algorithm>(2);


		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		algs.add(new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency,Histogram.DistanceMeasure.HELLIGNER.toString()));
		algs.add(new COSMO(1,vehicles,Histogram.DistanceMeasure.EUCLIDEAN,numSelectedSensors,updateFrequency,Histogram.DistanceMeasure.EUCLIDEAN.toString()));


		List<SensorDescription> sensorsDescriptions = new ArrayList<SensorDescription>(2);
		sensorsDescriptions.add(new SensorDescription(0,0,new MinMaxPair(15,10)));
		sensorsDescriptions.add(new SensorDescription(1,0,new MinMaxPair(10,-150)));
		sensorsDescriptions.add(new SensorDescription(0,1,new MinMaxPair(100,-15)));


		TestConfig config = new TestConfig();


		config.addProperty(IConfig.PROPERTY_ANOMALY_DETECTION_TIMER_ZSCORE_WINDOW_SIZE,"30");

		AnomalyDetectionModelTimer t = TestSimulationLoader.buildAnomalyDetectionModelTimer(algs, vehicles, sensorsDescriptions, config);

		List<AnomalyDetectionAlgorithm> actual = t.getThreadPartitionKeys();
		Assert.assertEquals(true, algs.get(0) == actual.get(0));
		Assert.assertEquals(true, algs.get(1) == actual.get(1));
		Assert.assertEquals(30, t.getZvalueWindowSize());

		List<Sensor> actualSensors = t.getSensors();
		Assert.assertEquals(sensors.size(),actualSensors.size());
		for(Sensor s : sensors){
			Assert.assertEquals(true,actualSensors.contains(s));
		}

		List<Vehicle> actualVehicles = t.getVehicles();
		Assert.assertEquals(vehicles.size(),actualVehicles.size());
		for(Vehicle v : vehicles){
			Assert.assertEquals(true,actualVehicles.contains(v));
		}

		List<AnomalyDetectionAlgorithm> actualAlgs = t.getThreadPartitionKeys();
		Assert.assertEquals(algs.size(),actualAlgs.size());
		for(Algorithm a : algs){
			Assert.assertEquals(true,actualAlgs.contains(a));
		}
		for(AnomalyDetectionAlgorithm a : actualAlgs){
			for(Vehicle v : vehicles){
				for(Sensor s : sensors){
					Assert.assertNotNull(a.getSensorInstance(v, s));
				}
			}
		}

		SensorMap map = t.getSensorMap();
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s : sensors){
					Assert.assertNotNull(map.getSensorInstance(alg, v, s));
				}
			}
		}



	}
*//*
	@Test
	public void testBuildDataGenerationTimer() throws IOException {
		/*	//get the config objects of all theconfigs in the data generation directory
		String dataGenerationSensorDirecotry = config.getProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_DATA_GENERATION_SENSOR_DIRECTORY);

			Noise whiteNoise =  SimulationLoader.buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_WHITE_NOISE_CONFIG_FILE_PATH);

			Noise ampFactorGenerator = SimulationLoader.buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH);
			Noise noiseAmpFactorGenerator = SimulationLoader.buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH);
			Noise noisePValue1Generator = SimulationLoader.buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE1_AVERAGE_CONFIG_FILE_PATH);
			Noise noisePValue2Generator = SimulationLoader.buildNoise(c,IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE2_AVERAGE_CONFIG_FILE_PATH);

			String sensorString =  c.getProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_SENSOR);

		TestConfig sensor0Config = new TestConfig();
		Noise n = new Noise(0,0,1.0,0.0,Noise.Distribution.UNIFORM);
		TestConfig wnConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,1.0,0.95,Noise.Distribution.UNIFORM);
		TestConfig ampFactConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,1.0,0.90,Noise.Distribution.UNIFORM);
		TestConfig noiseAmpFactConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.1,0.0,Noise.Distribution.UNIFORM);
		TestConfig p1Config = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.2,0.1,Noise.Distribution.UNIFORM);
		TestConfig p2Config = TestSimulationLoader.noiseToConfig(n);

		sensor0Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_WHITE_NOISE_CONFIG_FILE_PATH,wnConfig);
		sensor0Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH,ampFactConfig);
		sensor0Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH,noiseAmpFactConfig);
		sensor0Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE1_AVERAGE_CONFIG_FILE_PATH,p1Config);
		sensor0Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE2_AVERAGE_CONFIG_FILE_PATH,p2Config);
		sensor0Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_SENSOR,"(0,0)");

		TestConfig sensor1Config = new TestConfig();
		n = new Noise(0,0,2.0,0.1,Noise.Distribution.UNIFORM);
		wnConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,2.0,1.95,Noise.Distribution.UNIFORM);
		ampFactConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,2.0,1.90,Noise.Distribution.UNIFORM);
		noiseAmpFactConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.2,0.1,Noise.Distribution.UNIFORM);
		p1Config = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.3,0.2,Noise.Distribution.UNIFORM);
		p2Config = TestSimulationLoader.noiseToConfig(n);

		sensor1Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_WHITE_NOISE_CONFIG_FILE_PATH,wnConfig);
		sensor1Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH,ampFactConfig);
		sensor1Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH,noiseAmpFactConfig);
		sensor1Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE1_AVERAGE_CONFIG_FILE_PATH,p1Config);
		sensor1Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE2_AVERAGE_CONFIG_FILE_PATH,p2Config);
		sensor1Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_SENSOR,"(1,0)");


		TestConfig sensor2Config = new TestConfig();
		n = new Noise(0,0,3.0,0.2,Noise.Distribution.UNIFORM);
		wnConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,3.0,2.95,Noise.Distribution.UNIFORM);
		ampFactConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,3.0,2.90,Noise.Distribution.UNIFORM);
		noiseAmpFactConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.3,0.2,Noise.Distribution.UNIFORM);
		p1Config = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.4,0.3,Noise.Distribution.UNIFORM);
		p2Config = TestSimulationLoader.noiseToConfig(n);

		sensor2Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_WHITE_NOISE_CONFIG_FILE_PATH,wnConfig);
		sensor2Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH,ampFactConfig);
		sensor2Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_AMP_FACTOR_AVERAGE_CONFIG_FILE_PATH,noiseAmpFactConfig);
		sensor2Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE1_AVERAGE_CONFIG_FILE_PATH,p1Config);
		sensor2Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_NOISE_PVALUE2_AVERAGE_CONFIG_FILE_PATH,p2Config);
		sensor2Config.addProperty(IConfig.PROPERTY_DATA_GENERATION_SENSOR_SENSOR,"(0,1)");


		TestConfig  config = new TestConfig();
		config.addProperty(IConfig.PROPERTY_DATA_GENERATION_TIMER_DATA_GENERATION_SENSOR_DIRECTORY, "key.to.configs.");
		config.addProperty("key.to.configs.0", sensor0Config);
		config.addProperty("key.to.configs.1", sensor1Config);
		config.addProperty("key.to.configs.2", sensor2Config);
		config.addProperty(IConfig.PROPERTY_SIMULATION_LOADER_SENSOR_BEHAVIOR_CREATION_MODE, "READ_ALL_SENSOR_BEHAVIOR");


		List<Sensor> sensors = new ArrayList<Sensor>(3);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));
		
		int numberVehicles = 32;
		DataGenerationTimer t = TestSimulationLoader.buildDataGenerationTimer(numberVehicles,sensors, config);

		List<DataGenerationVehicle> vehicles = t.getThreadPartitionKeys();
		Assert.assertEquals(numberVehicles, vehicles.size());

		for(DataGenerationVehicle v : vehicles){
			List<DataGenerationSensor> dataGenSensors = v.getSensors();


			Assert.assertEquals(3, dataGenSensors.size());
			for(DataGenerationSensor s : dataGenSensors){

				if(s.equals(new Sensor(0,0))){
					Assert.assertEquals(new Sensor(0,0),s);
					double actual = s.getNormalBehavior().getAmpFactor();
					boolean inRange = (actual >= 0.95) && (actual <= 1.0);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoiseAmpFactor();
					inRange = (actual >= 0.90) && (actual <= 1.0);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoisePValues()[0];
					inRange = (actual >= 0.0) && (actual <= 0.1);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoisePValues()[1];
					inRange = (actual >= 0.1) && (actual <= 0.2);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getWhiteNoise().getNoiseMax();
					Assert.assertEquals(1.0,actual,0.0001);
					actual = s.getNormalBehavior().getWhiteNoise().getNoiseMin();
					Assert.assertEquals(0.0,actual,0.0001);
				}
				if(s.equals(new Sensor(1,0))){

					Assert.assertEquals(new Sensor(1,0),s);
					double actual = s.getNormalBehavior().getAmpFactor();
					boolean inRange = (actual >= 1.95) && (actual <= 2.0);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoiseAmpFactor();
					inRange = (actual >= 1.90) && (actual <= 2.0);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoisePValues()[0];
					inRange = (actual >= 0.1) && (actual <= 0.2);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoisePValues()[1];
					inRange = (actual >= 0.2) && (actual <= 0.3);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getWhiteNoise().getNoiseMax();
					Assert.assertEquals(2.0,actual,0.0001);
					actual = s.getNormalBehavior().getWhiteNoise().getNoiseMin();
					Assert.assertEquals(0.1,actual,0.0001);
				}
				if(s.equals(new Sensor(0,1))){
				
					Assert.assertEquals(new Sensor(0,1),s);
					double	actual = s.getNormalBehavior().getAmpFactor();
					boolean	inRange = (actual >= 2.95) && (actual <= 3.0);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoiseAmpFactor();
					inRange = (actual >= 2.90) && (actual <= 3.0);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoisePValues()[0];
					inRange = (actual >= 0.2) && (actual <= 0.3);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getNoisePValues()[1];
					inRange = (actual >= 0.3) && (actual <= 0.4);
					Assert.assertEquals(true,inRange);
					actual = s.getNormalBehavior().getWhiteNoise().getNoiseMax();
					Assert.assertEquals(3.0,actual,0.0001);
					actual = s.getNormalBehavior().getWhiteNoise().getNoiseMin();
					Assert.assertEquals(0.2,actual,0.0001);
				}
			}



		}


	}
*/
	@Test
	public void testLoadSensors() {
		List<Sensor> sensors = new ArrayList<Sensor>(3);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));
		sensors.add(new Sensor(100,6500));


		TestConfig config = new TestConfig();


		config.addProperty(IConfig.PROPERTY_SENSOR_WHITE_LIST+"0","(0,0)");
		config.addProperty(IConfig.PROPERTY_SENSOR_WHITE_LIST+"1","(1,0)");
		config.addProperty(IConfig.PROPERTY_SENSOR_WHITE_LIST+"2","(0,1)");
		config.addProperty(IConfig.PROPERTY_SENSOR_WHITE_LIST+"3","(100,6500)");

		List<Sensor> actualSensors = TestSimulationLoader.loadSensors(config);

		Assert.assertEquals(sensors.size(),actualSensors.size());

		for(Sensor s : sensors){
			Assert.assertEquals(true,	actualSensors.contains(s));
		}
	}

	@Test
	public void testLoadVehicles() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(50);
		for(int i =0 ;i<50;i++){
			vehicles.add(new Vehicle(i));
		}


		TestConfig config = new TestConfig();


		config.addProperty(IConfig.PROPERTY_NUMBER_OF_VEHICLES,"50");

		List<Vehicle> actualVehicles = TestSimulationLoader.loadVehicles(config);

		Assert.assertEquals(vehicles.size(),actualVehicles.size());

		for(Vehicle v : vehicles){
			Assert.assertEquals(true,	actualVehicles.contains(v));
		}
	}

	@Test
	public void testLoadAlgorithms() {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);


		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		algs.add(new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency,Histogram.DistanceMeasure.HELLIGNER.toString()));
		algs.add(new COSMO(1,vehicles,Histogram.DistanceMeasure.EUCLIDEAN,numSelectedSensors,updateFrequency,Histogram.DistanceMeasure.EUCLIDEAN.toString()));
		TestConfig config = new TestConfig();
		/*
		int numAlgorithms = config.getIntProperty(IConfig.PROPERTY_NUMBER_OF_ALGORITHMS);


		int numberOfSelectedSensors = config.getIntProperty(IConfig.PROPERTY_ALGORITHM_NUMBER_SELECTED_SENSORS);
		int zscoreUpdateFreq = config.getIntProperty(IConfig.PROPERTY_ALGORITHM_ZSCORE_UPDATE_FREQUENCY);

		List<String> distanceMetricStrings = config.getProperties(IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES);
		 */
		config.addProperty(IConfig.PROPERTY_NUMBER_OF_ALGORITHMS,"2");
		config.addProperty(IConfig.PROPERTY_ALGORITHM_NUMBER_SELECTED_SENSORS,"2");
		config.addProperty(IConfig.PROPERTY_ALGORITHM_ZSCORE_UPDATE_FREQUENCY,"7");
		config.addProperty(IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES+"0","HELLIGNER");
		config.addProperty(IConfig.PROPERTY_ALGORITHM_DISTANCE_MEASURES+"1","EUCLIDEAN");

		List<Algorithm> actualAlgorithsm = TestSimulationLoader.loadAlgorithms(vehicles, config);

		Assert.assertEquals(algs.size(),actualAlgorithsm.size());

		for(Algorithm a : algs){
			Assert.assertEquals(true,	actualAlgorithsm.contains(a));
		}

		COSMO cosmoActual = (COSMO) actualAlgorithsm.get(0);
		COSMO cosmoExpected = (COSMO) algs.get(0);

		Assert.assertEquals(cosmoExpected.getId(),cosmoActual.getId());
		Assert.assertEquals(cosmoExpected.getNumSensorSelect(),cosmoActual.getNumSensorSelect());
		Assert.assertEquals(cosmoExpected.getZscoreUpdateFrequency(),cosmoActual.getZscoreUpdateFrequency());
		Assert.assertEquals(cosmoExpected.getDistanceMeasure(),cosmoActual.getDistanceMeasure());

		List<Vehicle>actualVehicles = cosmoActual.getVehicles();
		Assert.assertEquals(vehicles.size(),actualVehicles.size());
		for(Vehicle v : vehicles){
			Assert.assertEquals(true,	actualVehicles.contains(v));
		}

		cosmoActual = (COSMO) actualAlgorithsm.get(1);
		cosmoExpected = (COSMO) algs.get(1);

		Assert.assertEquals(cosmoExpected.getId(),cosmoActual.getId());
		Assert.assertEquals(cosmoExpected.getNumSensorSelect(),cosmoActual.getNumSensorSelect());
		Assert.assertEquals(cosmoExpected.getZscoreUpdateFrequency(),cosmoActual.getZscoreUpdateFrequency());
		Assert.assertEquals(cosmoExpected.getDistanceMeasure(),cosmoActual.getDistanceMeasure());

		actualVehicles = cosmoActual.getVehicles();
		Assert.assertEquals(vehicles.size(),actualVehicles.size());
		for(Vehicle v : vehicles){
			Assert.assertEquals(true,	actualVehicles.contains(v));
		}
	}

	/*
	 * need to update this test
	 
	@Test
	public void testBuildFaultTimer() throws InvalidPropertiesFormatException, IOException {
		
		
		TestConfig fault0Config = new TestConfig();
		
		Noise n = new Noise(0,0,0.1,0.0,Noise.Distribution.UNIFORM);//0-10% fault occurence chance 
		TestConfig faultConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.4,0.1,Noise.Distribution.UNIFORM);//10-40% repair occurence chance
		TestConfig repairConfig = TestSimulationLoader.noiseToConfig(n);
		fault0Config.addProperty(IConfig.PROPERTY_FAULT_MINIMUM_DAYS_BEFORE_REPAIR, "5");
		fault0Config.addProperty(IConfig.PROPERTY_FAULT_FAULT_DESCRIPTION_ID, "0");
		fault0Config.addProperty(IConfig.PROPERTY_FAULT_FAULT_GENERATION_NOISE_CONFIG_FILE_PATH, faultConfig);
		fault0Config.addProperty(IConfig.PROPERTY_FAULT_REPAIR_GENERATION_NOISE_CONFIG_FILE_PATH, repairConfig);
		
		TestConfig fault1Config = new TestConfig();
		
		n = new Noise(0,0,0.2,0.0,Noise.Distribution.UNIFORM);//0-20% fault occurence chance 
		faultConfig = TestSimulationLoader.noiseToConfig(n);

		n = new Noise(0,0,0.2,0.05,Noise.Distribution.UNIFORM);//5-20% repair occurence chance
		repairConfig = TestSimulationLoader.noiseToConfig(n);
		fault1Config.addProperty(IConfig.PROPERTY_FAULT_MINIMUM_DAYS_BEFORE_REPAIR, "10");
		fault1Config.addProperty(IConfig.PROPERTY_FAULT_FAULT_DESCRIPTION_ID, "1");
		fault1Config.addProperty(IConfig.PROPERTY_FAULT_FAULT_GENERATION_NOISE_CONFIG_FILE_PATH, faultConfig);
		fault1Config.addProperty(IConfig.PROPERTY_FAULT_REPAIR_GENERATION_NOISE_CONFIG_FILE_PATH, repairConfig);
		
		
		TestConfig  config = new TestConfig();
		config.addProperty(IConfig.PROPERTY_FAULT_TIMER_FAULT_DIRECTORY, "key.to.configs.");
		config.addProperty("key.to.configs.0", fault0Config);
		config.addProperty("key.to.configs.1", fault1Config);
		
		
		List<FaultDescription> faultDescriptions = new ArrayList<FaultDescription>(2);
		
		FaultDescription fd0 = new FaultDescription(0,"test fault description 0");
		
		fd0.addAffectedSensor(createSensor(0,0));
		fd0.addAffectedSensor(createSensor(0,1));
		
		FaultDescription fd1 = new FaultDescription(1,"test fault description 1");
		fd1.addAffectedSensor(createSensor(0,0));
		fd1.addAffectedSensor(createSensor(1,0));
		
		faultDescriptions.add(fd0);
		faultDescriptions.add(fd1);
		
		int numberVehicles = 32;
		int timeTicks= 64;
		List<Sensor> sensors2 = new ArrayList<Sensor>(3);
		sensors2.add(new Sensor(0,0));
		sensors2.add(new Sensor(0,1));
		sensors2.add(new Sensor(1,0));
		FaultTimer t = TestSimulationLoader.buildFaultTimer(numberVehicles, faultDescriptions,sensors2, 64,config);
		
		List<FaultGenerationVehicle> vehicles = t.getThreadPartitionKeys();
		
		Assert.assertEquals(numberVehicles, vehicles.size());
		
		for(FaultGenerationVehicle v : vehicles){
			List<Fault> faults = v.getPotentialFaults();
			
			for(Fault f : faults){
				if(f.getFaultDescription().equals(fd0)){
					
					double actual = f.getOccurencePValue();
					boolean inRange = (actual >= 0) && (actual <= 0.1);
					Assert.assertEquals(true, inRange);
					actual = f.getRepairPValue();
					inRange = (actual >= 0.1) && (actual <= 0.4);
					Assert.assertEquals(true, inRange);					
					Assert.assertEquals(5,f.getMinDaysBeforeRepair());
					
					List<Sensor> sensors = f.getSensors();
					Assert.assertEquals(2,sensors.size());
					Assert.assertEquals(true, sensors.contains(new Sensor(0,0)));
					Assert.assertEquals(true, sensors.contains(new Sensor(0,1)));
					
				}
				
				if(f.getFaultDescription().equals(fd1)){
					double actual = f.getOccurencePValue();
					boolean inRange = (actual >= 0) && (actual <= 0.2);
					Assert.assertEquals(true, inRange);
					actual = f.getRepairPValue();
					inRange = (actual >= 0.05) && (actual <= 0.2);
					Assert.assertEquals(true, inRange);					
					Assert.assertEquals(10,f.getMinDaysBeforeRepair());
					
					List<Sensor> sensors = f.getSensors();
					Assert.assertEquals(2,sensors.size());
					Assert.assertEquals(true, sensors.contains(new Sensor(0,0)));
					Assert.assertEquals(true, sensors.contains(new Sensor(1,0)));
				}
			}
		}
	}
*/
	@Test
	public void testBuildFault() {
		//Fault buildFault(int minDaysBeforeRepair,Noise faultProbGenerator, Noise repairProbGenerator, FaultDescription fd){
		Noise faultGen = new Noise(0,0,0.1,0.0,Noise.Distribution.UNIFORM);//0-10% probability of occurence of fault
		Noise repairGen = new Noise(0,0,0.3,0.1,Noise.Distribution.UNIFORM);//10-30% probability of repairing
		FaultDescription fd = new FaultDescription(0,null);
		int minDaysBeforeRepair = 5;

		//generate faults 1000 times to be sure it follows the generation rules
		for(int i = 0; i<1000;i++){
			Fault actual = TestSimulationLoader.buildFault(minDaysBeforeRepair, faultGen, repairGen, fd);
			Assert.assertEquals(true,actual.getFaultDescription() == fd);
			Assert.assertEquals(minDaysBeforeRepair,actual.getMinDaysBeforeRepair());
			double actualPValue = actual.getOccurencePValue();
			boolean faultPValueInRange = (actualPValue >= 0.0 )&& (actualPValue <= 0.1);
			Assert.assertEquals(true,faultPValueInRange);
			actualPValue = actual.getRepairPValue();
			boolean repairPValueInRange = (actualPValue >= 0.1 )&& (actualPValue <= 0.3);
			Assert.assertEquals(true,repairPValueInRange);
		}
	}

	@Test
	public void testBuildSensorBehavior() {
		//SensorBehavior buildSensorBehavior(Noise whiteNoise, Noise ampFactorGenerator, Noise noiseAmpFactorGenerator,Noise noisePValue1Generator,Noise noisePValue2Generator){
		Noise wn = new Noise(0,1,0.0,0.0,Noise.Distribution.GAUSSIAN);
		Noise ampF = new Noise(0,0,1,0.95,Noise.Distribution.UNIFORM);// 0.95-1 amp factor
		Noise nAmpF = new Noise(0,0,1,0.90,Noise.Distribution.UNIFORM);// 0.90-1 noise amp factor
		Noise p1 = new Noise(0,0,0.2,0.0,Noise.Distribution.UNIFORM);// 0%-20% noise p1
		Noise p2 = new Noise(0,0,0.3,0.2,Noise.Distribution.UNIFORM);// 20%-30% noise p2

		//check this behavior generation 1000 times
		for(int i = 0; i < 1000;i++){
			SensorBehavior b = TestSimulationLoader.buildSensorBehavior(wn, ampF, nAmpF, p1, p2);

			Noise actualWn = b.getWhiteNoise();
			Assert.assertEquals(0.0, actualWn.getNoiseMean(),0.0001);
			Assert.assertEquals(1.0, actualWn.getNoiseSD(),0.0001);
			Assert.assertEquals(0.0, actualWn.getNoiseMax(),0.0001);
			Assert.assertEquals(0.0, actualWn.getNoiseMin(),0.0001);
			Assert.assertEquals(Noise.Distribution.GAUSSIAN, actualWn.getType());

			boolean inRange = (b.getAmpFactor() >= 0.95) && (b.getAmpFactor() <=1);
			Assert.assertEquals(true,inRange);
			inRange = (b.getNoiseAmpFactor() >= 0.90) && (b.getNoiseAmpFactor() <=1);
			Assert.assertEquals(true,inRange);
			inRange = (b.getNoisePValues()[0] >= 0.0) && (b.getNoisePValues()[0] <=0.2);
			Assert.assertEquals(true,inRange);
			inRange = (b.getNoisePValues()[1] >= 0.2) && (b.getNoisePValues()[1] <=0.3);
			Assert.assertEquals(true,inRange);
		}
	}
/*
	@Test
	public void testBuildFaultDescriptions() throws IOException {
		/*
		 * int id = config.getIntProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_ID);
		String label = config.getProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_LABEL);
		

		TestConfig nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		TestConfig config1 = new TestConfig();
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "0.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "1.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.15");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.455");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "NEW");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(0,0)");


		nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"-50.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"60.9");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"0.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-0.01");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"GAUSSIAN");


		TestConfig  config2 = new TestConfig();
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "1.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "2.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.20");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.655");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "MODIFY");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(1,0)");


		TestConfig  configFD1 = new TestConfig();
		configFD1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_DIRECTORY, "key.to.configs.");
		configFD1.addProperty("key.to.configs.0", config1);
		configFD1.addProperty("key.to.configs.1", config2);
		configFD1.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_ID, "51");
		configFD1.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_LABEL, "1ST test fault");

	
		nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"105");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-105");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		config1 = new TestConfig();
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "0.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "1.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.45");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.455");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "NEW");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(0,0)");


		nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"-50.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"61.9");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"0.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-0.01");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"GAUSSIAN");


		config2 = new TestConfig();
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "1.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "3.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.20");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.655");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "MODIFY");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(0,1)");


		TestConfig  configFD2 = new TestConfig();
		configFD2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_DIRECTORY, "key.to.configs.");
		configFD2.addProperty("key.to.configs.0", config1);
		configFD2.addProperty("key.to.configs.1", config2);
		configFD2.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_ID, "52");
		configFD2.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_LABEL, "2ND test fault");



		

		TestConfig  config = new TestConfig();
		config.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTIONS_DIRECTORY, "key.to.configs.");
		config.addProperty("key.to.configs.0", configFD1);
		config.addProperty("key.to.configs.1", configFD2);


		List<Sensor> sensors = new ArrayList<Sensor>(3);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		List<FaultDescription> faultDescriptions = TestSimulationLoader.buildFaultDescriptions(sensors,config);
		Assert.assertEquals(2, faultDescriptions.size());

		FaultDescription fd = faultDescriptions.get(0);

		List<Sensor> actualSensors = fd.getSensors();
		Assert.assertEquals(2, actualSensors.size());
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(0,0)));
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(1,0)));

		actualSensors = fd.getNonFaultInvolvedSensors();
		Assert.assertEquals(1, actualSensors.size());
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(0,1)));

		FaultInvolvedSensorBehavior b = fd.getAffectedSensor(0);
		Noise n = b.getWhiteNoise();

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(100.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-100.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());

		Assert.assertEquals(0.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(1.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.15,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.455,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(0,0),b.getAffectedSensor());

		b = fd.getAffectedSensor(1);

		n = b.getWhiteNoise();

		Assert.assertEquals(-50.1,n.getNoiseMean(),0.0001);
		Assert.assertEquals(60.9,n.getNoiseSD(),0.0001);
		Assert.assertEquals(0.1,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-0.01,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n.getType());

		Assert.assertEquals(1.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(2.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.20,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.655,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.MODIFY,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(1,0),b.getAffectedSensor());

		
		fd = faultDescriptions.get(1);

		actualSensors = fd.getSensors();
		Assert.assertEquals(2, actualSensors.size());
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(0,0)));
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(0,1)));

		actualSensors = fd.getNonFaultInvolvedSensors();
		Assert.assertEquals(1, actualSensors.size());
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(1,0)));

		b = fd.getAffectedSensor(0);
		n = b.getWhiteNoise();

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(105.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-105.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());

		Assert.assertEquals(0.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(1.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.45,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.455,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(0,0),b.getAffectedSensor());

		b = fd.getAffectedSensor(1);

		n = b.getWhiteNoise();

		Assert.assertEquals(-50.1,n.getNoiseMean(),0.0001);
		Assert.assertEquals(61.9,n.getNoiseSD(),0.0001);
		Assert.assertEquals(0.1,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-0.01,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n.getType());

		Assert.assertEquals(1.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(3.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.20,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.655,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.MODIFY,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(0,1),b.getAffectedSensor());
	}

	@Test
	public void testBuildFaultDescription() throws InvalidPropertiesFormatException, IOException {
		/*
		 * int id = config.getIntProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_ID);
		String label = config.getProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_LABEL);
		 

		TestConfig nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		TestConfig config1 = new TestConfig();
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "0.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "1.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.15");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.455");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "NEW");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(0,0)");


		nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"-50.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"60.9");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"0.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-0.01");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"GAUSSIAN");


		TestConfig  config2 = new TestConfig();
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "1.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "2.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.20");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.655");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "MODIFY");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(1,0)");


		TestConfig  config = new TestConfig();
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_DIRECTORY, "key.to.configs.");
		config.addProperty("key.to.configs.0", config1);
		config.addProperty("key.to.configs.1", config2);
		config.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_ID, "51");
		config.addProperty(IConfig.PROPERTY_FAULT_DESCRIPTION_LABEL, "a test fault");

		List<Sensor> sensors = new ArrayList<Sensor>(3);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		FaultDescription fd = TestSimulationLoader.buildFaultDescription(sensors, config);

		List<Sensor> actualSensors = fd.getSensors();
		Assert.assertEquals(2, actualSensors.size());
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(0,0)));
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(1,0)));

		actualSensors = fd.getNonFaultInvolvedSensors();
		Assert.assertEquals(1, actualSensors.size());
		Assert.assertEquals(true ,actualSensors.contains(new Sensor(0,1)));

		FaultInvolvedSensorBehavior b = fd.getAffectedSensor(0);
		Noise n = b.getWhiteNoise();

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(100.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-100.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());

		Assert.assertEquals(0.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(1.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.15,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.455,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(0,0),b.getAffectedSensor());

		b = fd.getAffectedSensor(1);

		n = b.getWhiteNoise();

		Assert.assertEquals(-50.1,n.getNoiseMean(),0.0001);
		Assert.assertEquals(60.9,n.getNoiseSD(),0.0001);
		Assert.assertEquals(0.1,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-0.01,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n.getType());

		Assert.assertEquals(1.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(2.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.20,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.655,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.MODIFY,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(1,0),b.getAffectedSensor());
	}


	@Test
	public void testBuildFaultInvovledSensorBehaviors() throws InvalidPropertiesFormatException, IOException {
		TestConfig nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		TestConfig config1 = new TestConfig();
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "0.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "1.05");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.15");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.455");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "NEW");
		config1.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(123,456)");


		nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"-50.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"60.9");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"0.1");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-0.01");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"GAUSSIAN");


		TestConfig  config2 = new TestConfig();
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "1.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "2.05");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.20");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.655");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "MODIFY");
		config2.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(1238,4567)");


		TestConfig  config = new TestConfig();
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_DIRECTORY, "key.to.configs.");
		config.addProperty("key.to.configs.0", config1);
		config.addProperty("key.to.configs.1", config2);

		List<FaultInvolvedSensorBehavior> behaviors = TestSimulationLoader.buildFaultInvolvedSensorBehaviors(config);

		Assert.assertEquals(2,behaviors.size());

		FaultInvolvedSensorBehavior b = behaviors.get(0);
		Noise n = b.getWhiteNoise();

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(100.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-100.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());

		Assert.assertEquals(0.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(1.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.15,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.455,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(123,456),b.getAffectedSensor());


		b = behaviors.get(1);
		n = b.getWhiteNoise();

		Assert.assertEquals(-50.1,n.getNoiseMean(),0.0001);
		Assert.assertEquals(60.9,n.getNoiseSD(),0.0001);
		Assert.assertEquals(0.1,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-0.01,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n.getType());

		Assert.assertEquals(1.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(2.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.20,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.655,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.MODIFY,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(1238,4567),b.getAffectedSensor());
	}

	@Test
	public void testBuildFaultInvolvedSensorBehavior() {

		TestConfig nosieConfigTarget = new TestConfig();

		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-100");
		nosieConfigTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		TestConfig config = new TestConfig();
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_CONFIG_FILE_PATH, nosieConfigTarget);
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_AMP_FACTOR, "0.05");
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_AMP_FACTOR, "1.05");
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE1, "0.15");
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_P_VALUE2, "0.455");
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_NOISE_TYPE, "NEW");
		config.addProperty(IConfig.PROPERTY_SENSOR_BEHAVIOR_SENSOR, "(123,456)");

		FaultInvolvedSensorBehavior b = TestSimulationLoader.buildFaultInvolvedSensorBehavior(config);

		Noise n = b.getWhiteNoise();

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(100.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-100.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());

		Assert.assertEquals(0.05,b.getAmpFactor(),0.00001);
		Assert.assertEquals(1.05,b.getNoiseAmpFactor(),0.00001);
		Assert.assertEquals(0.15,b.getNoisePValues()[0],0.00001);
		Assert.assertEquals(0.455,b.getNoisePValues()[1],0.00001);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,b.getAffectedBehaviorType());
		Assert.assertEquals(new Sensor(123,456),b.getAffectedSensor());
	}

	@Test
	public void testParseSensor() {
		Sensor s = TestSimulationLoader.parseSensor("(123,456)");
		Assert.assertEquals(new Sensor(123,456),s);
	}

	@Test
	public void testBuildNoiseIConfigString() {
		TestConfig configTarget = new TestConfig();

		configTarget.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		configTarget.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		configTarget.addProperty(IConfig.PROPERTY_NOISE_MAX,"100");
		configTarget.addProperty(IConfig.PROPERTY_NOISE_MIN,"-100");
		configTarget.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		TestConfig config = new TestConfig();
		config.addProperty("key.to.config.object", configTarget);

		Noise n = TestSimulationLoader.buildNoise(config,"key.to.config.object");

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(100.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-100.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());
	}

	@Test
	public void testBuildNoiseIConfig() {
		TestConfig config = new TestConfig();

		config.addProperty(IConfig.PROPERTY_NOISE_MEAN,"0.5");
		config.addProperty(IConfig.PROPERTY_NOISE_SD,"5");
		config.addProperty(IConfig.PROPERTY_NOISE_MAX,"100");
		config.addProperty(IConfig.PROPERTY_NOISE_MIN,"-100");
		config.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"NORMAL");

		Noise n = TestSimulationLoader.buildNoise(config);

		Assert.assertEquals(0.5,n.getNoiseMean(),0.0001);
		Assert.assertEquals(5.0,n.getNoiseSD(),0.0001);
		Assert.assertEquals(100.0,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-100.0,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());

		config = new TestConfig();

		config.addProperty(IConfig.PROPERTY_NOISE_MEAN,"-50.1");
		config.addProperty(IConfig.PROPERTY_NOISE_SD,"60.9");
		config.addProperty(IConfig.PROPERTY_NOISE_MAX,"0.1");
		config.addProperty(IConfig.PROPERTY_NOISE_MIN,"-0.01");
		config.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"GAUSSIAN");

		n = TestSimulationLoader.buildNoise(config);

		Assert.assertEquals(-50.1,n.getNoiseMean(),0.0001);
		Assert.assertEquals(60.9,n.getNoiseSD(),0.0001);
		Assert.assertEquals(0.1,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-0.01,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n.getType());


		config.addProperty(IConfig.PROPERTY_NOISE_MEAN,"-50.1");
		config.addProperty(IConfig.PROPERTY_NOISE_SD,"60.9");
		config.addProperty(IConfig.PROPERTY_NOISE_MAX,"0.1");
		config.addProperty(IConfig.PROPERTY_NOISE_MIN,"-0.01");
		config.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,"UNIFORM");

		n = TestSimulationLoader.buildNoise(config);

		Assert.assertEquals(-50.1,n.getNoiseMean(),0.0001);
		Assert.assertEquals(60.9,n.getNoiseSD(),0.0001);
		Assert.assertEquals(0.1,n.getNoiseMax(),0.0001);
		Assert.assertEquals(-0.01,n.getNoiseMin(),0.0001);
		Assert.assertEquals(Noise.Distribution.UNIFORM,n.getType());
	}

	@Test
	public void testbuildOutputTimer() {
		/*
		 * int experimentId=config.getIntProperty(IConfig.PROPERTY_OUTPUT_TIMER_EXPERIMENT_ID);
		String inputConfigFilePath=config.getConfigFilePath();
		 String inputLogFilePath=config.getProperty(IConfig.PROPERTY_LOG_FILE_PATH);
		 String outputFileDirectory=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_DIRECTORY);
		 String outputLogFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_LOG_FILE_NAME);
		 String outputConfigFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_CONFIG_FILE_NAME);
		 String outputRocCSVFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_CSV_FILE_NAME);
		 String inputRocRScriptFilePath=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_ROC_SCRIPT_FILE_NAME);		 
		 String outputRocCurveImageFileName=config.getProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_IMAGE_FILE_NAME);
	
		
		List<Algorithm> algs = new ArrayList<Algorithm>(2);


		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		
		TestConfig config = new TestConfig();
		config.setConfigFilePath("hello-world");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_EXPERIMENT_ID,"15");
		config.addProperty(IConfig.PROPERTY_LOG_FILE_PATH,"log-path");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_DIRECTORY,"output-dir");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_LOG_FILE_NAME,"output-log");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_CONFIG_FILE_NAME,"output-config");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_CSV_FILE_NAME,"output-csv");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_ROC_SCRIPT_FILE_NAME,"roc-script");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_OUTPUT_ROC_IMAGE_FILE_NAME,"roc-image");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_INPUT_FILES_DIRECTORY,"input-dir");
		config.addProperty(IConfig.PROPERTY_OUTPUT_TIMER_DATA_ANALYSIS_BATCH_SCRIPT_FILE_PATH,"input-batch.bat");
		
		OutputTimer t = TestSimulationLoader.buildOutputTimer(algs, config);
		algs = new ArrayList<Algorithm>(4);


		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		algs.add(new Algorithm(0).toICOSMO());
		algs.add(new Algorithm(1).toICOSMO());
		
		List<Algorithm> actualAlgs = t.getAlgorithms();
		Assert.assertEquals(algs.size(),actualAlgs.size());
		for(Algorithm alg : algs){
			Assert.assertEquals(true, actualAlgs.contains(alg));
		}
		Assert.assertEquals(15,t.getExperimentId());
		Assert.assertEquals("hello-world",t.getInputConfigFilePath());
		Assert.assertEquals("log-path",t.getInputLogFilePath());
		Assert.assertEquals("output-dir",t.getOutputDirectory());
		Assert.assertEquals("output-log",t.getOutputLogFileName());
		Assert.assertEquals("output-config",t.getOutputConfigFileName());
		Assert.assertEquals("output-csv",t.getOutputRocCSVFileName());
		Assert.assertEquals("roc-script",t.getInputRocRScriptFilePath());
		Assert.assertEquals("roc-image",t.getOutputRocCurveImageFileName());
		Assert.assertEquals("input-dir",t.getInputFilesDirectory());
		
		
	}
*/
	private static class TestConfig extends Configuration implements IConfig{


		public TestConfig(){
			Properties properties = new Properties();
			this.setEntries(properties);

		}

		public void addProperty(Object key, Object value){
			Properties properties = this.getEntries();
			properties.put(key, value);
		}

		@Override
		public IConfig loadTargetConfigFile(String key){

			return (IConfig) this.getEntries().get(key);

		}
		@Override
		public  List<IConfig> loadConfigFilesInDirectory(String directory) throws InvalidPropertiesFormatException, IOException{

			return this.loadTargetConfigFiles(directory);

		}
	}

	public static TestConfig noiseToConfig(Noise n){
		TestConfig config = new TestConfig();

		config.addProperty(IConfig.PROPERTY_NOISE_MEAN,""+n.getNoiseMean());
		config.addProperty(IConfig.PROPERTY_NOISE_SD,""+n.getNoiseSD());
		config.addProperty(IConfig.PROPERTY_NOISE_MAX,""+n.getNoiseMax());
		config.addProperty(IConfig.PROPERTY_NOISE_MIN,""+n.getNoiseMin());
		config.addProperty(IConfig.PROPERTY_NOISE_DISTRIBUTION,n.getType().toString());

		return config;

	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,true,0.0);
	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore,double zvalue){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,true,zvalue);
	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, double zscore){
		return createSensorStatusEvent(alg,0,0,zscore);
	}

	public static FaultInvolvedSensorBehavior createSensor(int pgn, int spn){
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(pgn,spn);
		return new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
	}

}
