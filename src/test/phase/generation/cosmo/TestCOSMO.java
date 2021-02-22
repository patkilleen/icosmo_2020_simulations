package test.phase.generation.cosmo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.Noise;
import common.Sensor;
import common.SensorInstance;
import common.SensorInstanceFactory;
import common.SensorMap;
import common.Vehicle;
import common.event.SensorDataEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.cosmo.COSMO;
import phase.generation.cosmo.COSMOSensorInstance;
import phase.generation.cosmo.DistanceMatrix;
import phase.generation.cosmo.Histogram;
import phase.generation.cosmo.MinMaxPair;

public class TestCOSMO extends COSMO implements SensorInstanceFactory{

	@Test
	public void testCOSMO_getters() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		COSMO cosmo = new COSMO(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		Assert.assertEquals(1, cosmo.getId());
		Assert.assertEquals(7,cosmo.getZscoreUpdateFrequency());
		Assert.assertEquals(2,cosmo.getNumSensorSelect());
		Assert.assertEquals(Histogram.DistanceMeasure.HELLIGNER,cosmo.getDistanceMeasure());
	}



	@Test
	public void testCOSMO_illegal_arg() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));


		boolean flag = false;
		try{

			int id = -1;
			int updateFrequency = 7;
			int numSelectedSensors = 2;
			COSMO cosmo = new COSMO(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			int id = 1;
			int updateFrequency = -1;
			int numSelectedSensors = 2;
			COSMO cosmo = new COSMO(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			int id = 1;
			int updateFrequency = 7;
			int numSelectedSensors = -1;
			COSMO cosmo = new COSMO(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			int id = -1;
			int updateFrequency = 7;
			int numSelectedSensors = 2;
			COSMO cosmo = new COSMO(id,null,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			int id = 1;
			int updateFrequency = 7;
			int numSelectedSensors = 2;
			COSMO cosmo = new COSMO(id,new ArrayList<Vehicle>(0),Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}

	@Test
	public void testProcessSensorReading_sensor_selection_1sensor_stability_based() {
		List<Algorithm> algs =  new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 1;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);




		/*
		 * h1.addSample(0.0);
		h1.addSample(0.1);

		//[1,0,0,0]
		Assert.assertEquals(1.0, h1.normalizedEntropy(),0.001);

		h1.addSample(40.0);
		h1.addSample(45.0);

		//[0.5,0.5,0,0]
		Assert.assertEquals(0.0, h1.normalizedEntropy(),0.001);

		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(40.0);
		h1.addSample(45.0);

		//[0.6,0.4,0,0]
		Assert.assertEquals(0.02897, h1.normalizedEntropy(),0.0001);
		 */
		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),40.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),45.0));//sensor (0,0)normalized entropy 0

		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(1,0),0.1));// sensor (1,0 )normalized entropy 1
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());//day 1 of consectuive model gathering

		cosmo.postTimeTick(new TimerEvent(0));

		data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),10.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),10.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),20.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),20.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),30.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),30.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),30.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),30.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),30.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),30.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),40.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),45.0));//sensor (0,0) not stable

		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(1,0),0.1));// sensor (1,0 ) quite stable
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());//day2
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());

		cosmo.postTimeTick(new TimerEvent(0));// this completes the sensor selection

		//sensor( 1, 0) should be selectecd

		COSMOSensorInstance i = cosmo.getSensorInstance(new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(false,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(false,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(0), new Sensor(1,0));
		Assert.assertEquals(true,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(true,i.isCOSMOSensor());


	}


	@Test
	public void testProcessSensorReading_sensor_selection_2sensors_ne_based() {
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);




		/*
		 * h1.addSample(0.0);
		h1.addSample(0.1);

		//[1,0,0,0]
		Assert.assertEquals(1.0, h1.normalizedEntropy(),0.001);

		h1.addSample(40.0);
		h1.addSample(45.0);

		//[0.5,0.5,0,0]
		Assert.assertEquals(0.0, h1.normalizedEntropy(),0.001);

		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(40.0);
		h1.addSample(45.0);

		//[0.6,0.4,0,0]
		Assert.assertEquals(0.02897, h1.normalizedEntropy(),0.0001);
		 */
		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),2.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),3.1));//sensor (0,0) okay normalized entropy
		data1.add(new SensorDataEvent(new Sensor(0,0),1.5));
		data1.add(new SensorDataEvent(new Sensor(0,0),1.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),40.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),45.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),42.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),43.0));

		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(1,0),0.1));// sensor (1,0 ) good normalized entropy
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),2.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),3.1));
		data1.add(new SensorDataEvent(new Sensor(0,0),1.5));
		data1.add(new SensorDataEvent(new Sensor(0,0),1.1));


		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.1));// sensor (0,1 ) bad normalized entropy 
		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),10.0));
		for(int i = 0;i < 100;i++){//100 different values
			for(int j = 0;j < 3;j++){//3 isntances of same value
				data1.add(new SensorDataEvent(new Sensor(0,1),(double)i));	
			}
		}

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());//day 1 of consectuive model gathering

		cosmo.postTimeTick(new TimerEvent(0));


		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());//day2
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());

		cosmo.postTimeTick(new TimerEvent(0));// this completes the sensor selection

		//sensor( 1, 0) should be selectecd

		COSMOSensorInstance i = cosmo.getSensorInstance(new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(true,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(true,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(0), new Sensor(1,0));
		Assert.assertEquals(true,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(true,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(false,i.isCOSMOSensor());
		i = cosmo.getSensorInstance(new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(false,i.isCOSMOSensor());

	}


	@Test
	public void testPreTimeTick() {
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);

		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));	
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.1));

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());//day 1 of consectuive model gathering

		cosmo.postTimeTick(new TimerEvent(0));


		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(1), data1.iterator());

		cosmo.postTimeTick(new TimerEvent(1));// this completes the sensor selection

		Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());

		//7 days need to go by to get to deviation detection phase

		for(int i = 0;i<7;i++){
			cosmo.preTimeTick(new TimerEvent(2+i));
			cosmo.processSensorReadings(new TimerEvent(2+i), new Vehicle(0), data1.iterator());
			cosmo.processSensorReadings(new TimerEvent(2+i), new Vehicle(1), data1.iterator());
			cosmo.postTimeTick(new TimerEvent(2+i));
			Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());
		}

		cosmo.preTimeTick(new TimerEvent(9));
		cosmo.processSensorReadings(new TimerEvent(9), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(9), new Vehicle(1), data1.iterator());
		cosmo.postTimeTick(new TimerEvent(9));

		Assert.assertEquals(COSMO.CosmoPhase.DEVIATION_DETECTION, cosmo.getState());
	}


	@Test
	public void testGetAllWeeklyModels() {
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);

		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));	
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.1));

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());//day 1 of consectuive model gathering

		cosmo.postTimeTick(new TimerEvent(0));


		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(1), data1.iterator());

		cosmo.postTimeTick(new TimerEvent(1));// this completes the sensor selection

		List<HistogramVehiclePair> pairs = cosmo.getAllWeeklyModels(new Sensor(0,0));
		Assert.assertEquals(14, pairs.size());//14 cause 2 vehicles, and both have 1 week worth of daily models
		for(HistogramVehiclePair p : pairs){
			Assert.assertEquals(true, p != null);
			Assert.assertEquals(true, p.histogram != null);
			Assert.assertEquals(true, p.sensorInstance != null);
			Assert.assertEquals(true, p.sensorInstance.equals(new Sensor(0,0)));
		}

		pairs = cosmo.getAllWeeklyModels(new Sensor(1,0));
		Assert.assertEquals(14, pairs.size());
		for(HistogramVehiclePair p : pairs){
			Assert.assertEquals(true, p != null);
			Assert.assertEquals(true, p.histogram != null);
			Assert.assertEquals(true, p.sensorInstance != null);
			Assert.assertEquals(true, p.sensorInstance.equals(new Sensor(1,0)));
		}

		pairs = cosmo.getAllWeeklyModels(new Sensor(0,1));
		Assert.assertEquals(14, pairs.size());
		for(HistogramVehiclePair p : pairs){
			Assert.assertEquals(true, p != null);
			Assert.assertEquals(true, p.histogram != null);
			Assert.assertEquals(true, p.sensorInstance != null);
			Assert.assertEquals(true, p.sensorInstance.equals(new Sensor(0,1)));
		}


	}

	@Test
	public void testGetWeeklyModels() {
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);

		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));	
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.1));

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());//day 1 of consectuive model gathering

		cosmo.postTimeTick(new TimerEvent(0));


		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(1), data1.iterator());

		cosmo.postTimeTick(new TimerEvent(1));// this completes the sensor selection

		for(int i = 0; i < 7; i ++){
			List<HistogramVehiclePair> pairs = cosmo.getWeeklyModels(new Sensor(0,0),i);
			Assert.assertEquals(2, pairs.size());

			for(HistogramVehiclePair p : pairs){

				Assert.assertEquals(true, p != null);
				Assert.assertEquals(true, p.histogram != null);
				Assert.assertEquals(true, p.sensorInstance != null);
				Assert.assertEquals(true, p.sensorInstance.equals(new Sensor(0,0)));
			}
		}

		for(int i = 0; i < 7; i ++){
			List<HistogramVehiclePair> pairs = cosmo.getWeeklyModels(new Sensor(1,0),i);
			Assert.assertEquals(2, pairs.size());

			for(HistogramVehiclePair p : pairs){

				Assert.assertEquals(true, p != null);
				Assert.assertEquals(true, p.histogram != null);
				Assert.assertEquals(true, p.sensorInstance != null);
				Assert.assertEquals(true, p.sensorInstance.equals(new Sensor(1,0)));
			}
		}

		for(int i = 0; i < 7; i ++){
			List<HistogramVehiclePair> pairs = cosmo.getWeeklyModels(new Sensor(0,1),i);
			Assert.assertEquals(2, pairs.size());

			for(HistogramVehiclePair p : pairs){

				Assert.assertEquals(true, p != null);
				Assert.assertEquals(true, p.histogram != null);
				Assert.assertEquals(true, p.sensorInstance != null);
				Assert.assertEquals(true, p.sensorInstance.equals(new Sensor(0,1)));
			}
		}
	}

	@Test
	public void testBuildMatrix() {
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;

		//run this test a few times
		for(int testCounter = 0;testCounter < 10;testCounter++){
			TestCOSMO cosmo = new TestCOSMO();
			cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

			SensorMap map = new SensorMap(algs);
			map.init(vehicles, sensors, cosmo);
			cosmo.init(map);

			List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);


			//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());

			//7 days need to go by to get to deviation detection phase (and the initial sensor selection of 2 days)
			//(double noiseMean, double noiseSD, double noiseMax, double noiseMin, Distribution type) {
			Noise noiseN = new Noise(0,3,1,0,Noise.Distribution.GAUSSIAN);
			Noise noiseU = new Noise(0,3,10,-10,Noise.Distribution.UNIFORM);
			for(int i = 0;i<10;i++){
				cosmo.preTimeTick(new TimerEvent(i));

				//generate random smaples here
				for(int sampleCounter = 0;sampleCounter< 100;sampleCounter++){
					data1.add(new SensorDataEvent(new Sensor(0,0),noiseN.generateNoise()));
					data1.add(new SensorDataEvent(new Sensor(1,0),noiseN.generateNoise()));
					data1.add(new SensorDataEvent(new Sensor(0,1),noiseU.generateNoise()));
				}

				cosmo.processSensorReadings(new TimerEvent(i), new Vehicle(0), data1.iterator());
				cosmo.processSensorReadings(new TimerEvent(i), new Vehicle(1), data1.iterator());
				cosmo.postTimeTick(new TimerEvent(i));
				//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());
			}


			//Assert.assertEquals(COSMO.CosmoPhase.DEVIATION_DETECTION, cosmo.getState());

			for(Sensor s : sensors){
				List<HistogramVehiclePair> models = cosmo.getAllWeeklyModels(s);
				DistanceMatrix matrix = cosmo.buildMatrix(models);
				//should be 14 rows and columsn, (7 models for 2 vehicles)
				Assert.assertEquals(14, matrix.numberoOfRows());
				Assert.assertEquals(14, matrix.numberOfColumns());

				//the diagonal from top left to bottom right should all be 0
				for(int i = 0;i<14;i++){
					double dist = matrix.getDistance(i, i);
					Assert.assertEquals(0.0, dist,0.00001);	
				}

				//all distances should be in range [0,1]

				for(int i = 0;i<14;i++){
					for(int j = 0;j<14;j++){
						double dist = matrix.getDistance(i, j);
						Assert.assertEquals(true, (dist >= 0 )&&(dist<=1));	
					}	
				}

				//matrix should be symettric


				for(int i = 0;i<14;i++){
					for(int j = 0;j<14;j++){
						double dist1 = matrix.getDistance(i, j);
						double dist2 = matrix.getDistance(j, i);
						Assert.assertEquals(dist1,dist2,0.0001);	
					}	
				}
			}
		}
	}



	@Test
	public void testClearWeeklyHistograms() {
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);

		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);
		data1.add(new SensorDataEvent(new Sensor(0,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,0),0.1));	
		data1.add(new SensorDataEvent(new Sensor(1,0),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.0));
		data1.add(new SensorDataEvent(new Sensor(0,1),0.1));

		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(0), new Vehicle(1), data1.iterator());//day 1 of consectuive model gathering

		cosmo.postTimeTick(new TimerEvent(0));


		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(1), new Vehicle(1), data1.iterator());

		cosmo.postTimeTick(new TimerEvent(1));// this completes the sensor selection

		//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());

		//7 days need to go by to get to deviation detection phase

		for(int i = 0;i<7;i++){
			cosmo.preTimeTick(new TimerEvent(2+i));
			cosmo.processSensorReadings(new TimerEvent(2+i), new Vehicle(0), data1.iterator());
			cosmo.processSensorReadings(new TimerEvent(2+i), new Vehicle(1), data1.iterator());
			cosmo.postTimeTick(new TimerEvent(2+i));
			//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());
		}

		cosmo.preTimeTick(new TimerEvent(9));
		cosmo.processSensorReadings(new TimerEvent(9), new Vehicle(0), data1.iterator());
		cosmo.processSensorReadings(new TimerEvent(9), new Vehicle(1), data1.iterator());
		cosmo.postTimeTick(new TimerEvent(9));

		//Assert.assertEquals(COSMO.CosmoPhase.DEVIATION_DETECTION, cosmo.getState());

		for(Sensor s : sensors){
			for(int i = 0; i < 7; i++){
				List<HistogramVehiclePair> pairs = cosmo.getWeeklyModels(s,i);


				for(HistogramVehiclePair p : pairs){
					Histogram h = p.histogram;
					Assert.assertEquals(true,0 != h.countSamples());//non-empty histograms initially 

				}
			}
		}

		//start empty bins each day
		for(int day = 0; day < 7; day ++){
			//empty bins of day MOD 0
			cosmo.clearWeeklyHistograms(day);

			//make sure cleared days all histograms empty
			for(Sensor s : sensors){
				//for(int i = 0; i < 7; i++){
				List<HistogramVehiclePair> pairs = cosmo.getWeeklyModels(s,day);


				for(HistogramVehiclePair p : pairs){
					Histogram h = p.histogram;
					Assert.assertEquals(true,0 == h.countSamples());//non-empty histograms initially 

				}
				//}
			}



			//make sure non-cleared days all histograms non-empty
			for(Sensor s : sensors){
				for(int i = day+1; i < 7; i++){
					List<HistogramVehiclePair> pairs = cosmo.getWeeklyModels(s,i);


					for(HistogramVehiclePair p : pairs){
						Histogram h = p.histogram;
						Assert.assertEquals(true,0 != h.countSamples());//non-empty histograms initially 

					}
				}
			}
		}

	}

	//public List<SensorStatusEvent> postProcessSensorReading(TimerEvent e){
	@Test
	public void testpostProcessSensorReading(){
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);

		List<SensorDataEvent> data1 = new ArrayList<SensorDataEvent>(6);


		//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());

		//7 days need to go by to get to deviation detection phase (and the initial sensor selection of 2 days)
		//(double noiseMean, double noiseSD, double noiseMax, double noiseMin, Distribution type) {
		Noise noiseN = new Noise(0,3,1,0,Noise.Distribution.GAUSSIAN);
		Noise noiseU = new Noise(0,3,10,-10,Noise.Distribution.UNIFORM);
		for(int i = 0;i<10;i++){
			cosmo.preTimeTick(new TimerEvent(i));

			//generate random smaples here
			for(int sampleCounter = 0;sampleCounter< 100;sampleCounter++){
				data1.add(new SensorDataEvent(new Sensor(0,0),noiseN.generateNoise()));
				data1.add(new SensorDataEvent(new Sensor(1,0),noiseN.generateNoise()));
				data1.add(new SensorDataEvent(new Sensor(0,1),noiseU.generateNoise()));
			}

			cosmo.processSensorReadings(new TimerEvent(i), new Vehicle(0), data1.iterator());
			cosmo.processSensorReadings(new TimerEvent(i), new Vehicle(1), data1.iterator());
			cosmo.postTimeTick(new TimerEvent(i));
			//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());
		}

		for(int i = 0;i<7;i++){
			List<SensorStatusEvent> sensorStatuses = cosmo.postProcessSensorReading(new TimerEvent(i));
			
			boolean vehicle1Found = false;
			boolean vehicle2Found = false;
			boolean sensor_1_0_Found = false;
			boolean sensor_0_0_Found = false;
			boolean sensor_0_1_Found = false;
			boolean allSensorSelected = true;
			for(SensorStatusEvent e : sensorStatuses){

				Assert.assertEquals(true, e.getAlgorithm() == cosmo);
				Assert.assertEquals(true, e.getAlgorithm().equals(cosmo));

				if(e.getSensor().equals(new Sensor(0,0))){
					sensor_0_0_Found=true;
				}else if(e.getSensor().equals(new Sensor(0,1))){
					sensor_0_1_Found=true;
				}else if(e.getSensor().equals(new Sensor(1,0))){
					sensor_1_0_Found=true;
				}
				
				if(e.getVehicle().equals(new Vehicle(0))){
					vehicle1Found=true;
				}else if(e.getVehicle().equals(new Vehicle(1))){
					vehicle2Found=true;
				}
				
				//make sure z value in range[0,1]
				Assert.assertEquals(true, (e.getZvalue() >= 0) &&(e.getZvalue()<=1));
				Assert.assertEquals(true, (e.getZscore() >= 0) &&(e.getZscore()<=1));
				
				allSensorSelected =  allSensorSelected && e.isCosmoSensorFlag();
			}
			Assert.assertEquals(false,allSensorSelected);
			Assert.assertEquals(true,vehicle1Found);
			Assert.assertEquals(true,vehicle2Found);
			Assert.assertEquals(true,sensor_1_0_Found);
			Assert.assertEquals(true,sensor_0_0_Found);
			Assert.assertEquals(true,sensor_0_1_Found);
			
			
		}
		//Assert.assertEquals(COSMO.CosmoPhase.DEVIATION_DETECTION, cosmo.getState());

	
		
	}
	
	@Test
	public void testGetSelectedSensors_1(){
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

		int id = 1;
		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo = new TestCOSMO();
		cosmo.init(id,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo);
		cosmo.init(map);
		
		List<Sensor> actual = cosmo.getSelectedSensors();		
		Assert.assertEquals(0, actual.size());
		
		COSMOSensorInstance i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(true);
		actual = cosmo.getSelectedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(0,0), actual.get(0));
		
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(false);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(false);
		actual = cosmo.getSelectedSensors();
		Assert.assertEquals(0, actual.size());
		
		
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(0), new Sensor(1,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(1), new Sensor(1,0));
		i.setCOSMOSensor(true);
		actual = cosmo.getSelectedSensors();
		Assert.assertEquals(2, actual.size());
		Assert.assertEquals(true,actual.contains(new Sensor(1,0)));
		Assert.assertEquals(true,actual.contains(new Sensor(0,0)));
		
		
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(false);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(id), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(false);
		actual = cosmo.getSelectedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(1,0), actual.get(0));
		
	}

	@Test
	public void testGetSelectedSensors_2(){
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


		int updateFrequency = 7;
		int numSelectedSensors = 2;
		TestCOSMO cosmo0 = new TestCOSMO();
		cosmo0.init(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);

		TestCOSMO cosmo1 = new TestCOSMO();
		cosmo1.init(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, cosmo0);
		cosmo0.init(map);
		cosmo1.init(map);
		
		List<Sensor> actual = cosmo0.getSelectedSensors();		
		Assert.assertEquals(0, actual.size());
		
		actual = cosmo1.getSelectedSensors();		
		Assert.assertEquals(0, actual.size());
		
		COSMOSensorInstance i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(true);
		actual = cosmo1.getSelectedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(0,0), actual.get(0));
		
		 actual = cosmo0.getSelectedSensors();		
			Assert.assertEquals(0, actual.size());
			
		
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(false);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(false);
		actual = cosmo1.getSelectedSensors();
		Assert.assertEquals(0, actual.size());
		
		 actual = cosmo0.getSelectedSensors();		
			Assert.assertEquals(0, actual.size());
			
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(1,0));
		i.setCOSMOSensor(true);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(1,0));
		i.setCOSMOSensor(true);
		actual = cosmo1.getSelectedSensors();
		Assert.assertEquals(2, actual.size());
		Assert.assertEquals(true,actual.contains(new Sensor(1,0)));
		Assert.assertEquals(true,actual.contains(new Sensor(0,0)));
		
		 actual = cosmo0.getSelectedSensors();		
			Assert.assertEquals(0, actual.size());
		
		
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		i.setCOSMOSensor(false);
		i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		i.setCOSMOSensor(false);
		actual = cosmo1.getSelectedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(1,0), actual.get(0));
	
		 actual = cosmo0.getSelectedSensors();		
			Assert.assertEquals(0, actual.size());
			
			 i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
				i.setCOSMOSensor(true);
				i = (COSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
				i.setCOSMOSensor(true);
				actual = cosmo0.getSelectedSensors();
				Assert.assertEquals(1, actual.size());
				Assert.assertEquals(new Sensor(0,0), actual.get(0));
				
				actual = cosmo1.getSelectedSensors();
				Assert.assertEquals(1, actual.size());
				Assert.assertEquals(new Sensor(1,0), actual.get(0));
	}
	
	@Override
	public SensorInstance newInstance(Vehicle v, Sensor s) {
		COSMOSensorInstance res =  new COSMOSensorInstance(s,v,this.getZscoreUpdateFrequency());
		res.setMinMaxPair(new MinMaxPair(100.0,0));//here the test sensor will have max 100 and min 0
		return res;
	}
}
