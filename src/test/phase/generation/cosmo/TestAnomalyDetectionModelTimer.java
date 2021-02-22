package test.phase.generation.cosmo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.Noise;
import common.Sensor;
import common.SensorInstance;
import common.Vehicle;
import common.event.SensorDataEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.SensorDataInputStream;
import common.event.stream.SensorDataOutputStream;
import common.event.stream.SensorDataStreamManager;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.cosmo.AnomalyDetectionAlgorithm;
import phase.generation.cosmo.AnomalyDetectionModelTimer;
import phase.generation.cosmo.COSMO;
import phase.generation.cosmo.COSMOSensorInstance;
import phase.generation.cosmo.Histogram;
import phase.generation.cosmo.MinMaxPair;
import phase.generation.cosmo.SensorDescription;

public class TestAnomalyDetectionModelTimer extends AnomalyDetectionModelTimer{

	
	private boolean threadFailedFlag = false;
	
	@Override
	protected void threadTick(TimerEvent e ,AnomalyDetectionAlgorithm v){
		try{
			super.threadTick(e, v);
		}catch(Throwable ex){
			ex.printStackTrace();
			threadFailedFlag=true;
		}
	}
	@Test
	public void testAnomalyDetectionModelTimer_illegal_arg() {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);


		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		algs.add(new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));
		algs.add(new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));


		boolean flag = false;
		try{

			AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(null,updateFrequency);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(new ArrayList<AnomalyDetectionAlgorithm>(2),updateFrequency);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(new ArrayList<AnomalyDetectionAlgorithm>(2),-1);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}


	@Test
	public void testAnomalyDetectionModelTimer() {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);


		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		algs.add(new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));
		algs.add(new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));


		AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(algs,updateFrequency);

		List<AnomalyDetectionAlgorithm> actual = t.getThreadPartitionKeys();
		Assert.assertEquals(true, algs.get(0) == actual.get(0));
		Assert.assertEquals(true, algs.get(1) == actual.get(1));
	}
	@Test
	public void testNewInstance() {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);


		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		algs.add(new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));
		algs.add(new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));


		AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(algs,updateFrequency);

		SensorInstance i = t.newInstance(new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(true,i.getVehicle().equals(new Vehicle(0)));
		Assert.assertEquals(true,i.equals(new Sensor(0,0)));
		Assert.assertEquals(true,i instanceof COSMOSensorInstance);

	}

	@Test
	public void testInit() {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		algs.add(new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));
		algs.add(new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency));


		AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(algs,updateFrequency);


		List<SensorDescription> descs = new ArrayList<SensorDescription>(3);
		descs.add(new SensorDescription(0,0,new MinMaxPair(100,0)));
		descs.add(new SensorDescription(0,1,new MinMaxPair(100,0)));
		descs.add(new SensorDescription(1,0,new MinMaxPair(100,0)));

		t.init(vehicles, descs);

		List<Vehicle> actualVehicles = t.getVehicles();
		List<Sensor> actualSensors = t.getSensors();

		Assert.assertEquals(2, actualVehicles.size());
		Assert.assertEquals(new Vehicle(0), actualVehicles.get(0));
		Assert.assertEquals(new Vehicle(1), actualVehicles.get(1));

		Assert.assertEquals(3, actualSensors.size());
		Assert.assertEquals(new Sensor(0,0), actualSensors.get(0));
		Assert.assertEquals(new Sensor(0,1), actualSensors.get(1));
		Assert.assertEquals(new Sensor(1,0), actualSensors.get(2));

		//make sure map populated

		for(AnomalyDetectionAlgorithm alg: algs){
			for(Vehicle v: vehicles){
				for(Sensor s : sensors){
					SensorInstance i = alg.getSensorInstance(v, s);
					Assert.assertEquals(true, i != null);
					Assert.assertEquals(true,i instanceof COSMOSensorInstance);
					COSMOSensorInstance ci = (COSMOSensorInstance)i;
					MinMaxPair p = ci.getMinMaxPair();
					Assert.assertEquals(true, p != null);

					Assert.assertEquals(100.0,p.getMax(),0.0001);
					Assert.assertEquals(0.0,p.getMin(),0.0001);

					Assert.assertEquals(v,ci.getVehicle());
					Assert.assertEquals(s.getPgn(),ci.getPgn());
					Assert.assertEquals(s.getSpn(),ci.getSpn());
				}
			}

		}	

	}

	@Test
	public void testThreadTickTimerEventAnomalyDetectionAlgorithm() throws InterruptedException {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);
		List<Algorithm> algs2 = new ArrayList<Algorithm>(2);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;


		COSMO c = new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		algs.add(c);
		algs2.add(c);
		c = new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		algs.add(c);
		algs2.add(c);
		
		
		TestAnomalyDetectionModelTimer t = new TestAnomalyDetectionModelTimer();
		t.setParitionKeys(algs);
		t.init(algs,updateFrequency);
		


		List<SensorDescription> descs = new ArrayList<SensorDescription>(3);
		descs.add(new SensorDescription(0,0,new MinMaxPair(100,0)));
		descs.add(new SensorDescription(0,1,new MinMaxPair(100,0)));
		descs.add(new SensorDescription(1,0,new MinMaxPair(100,0)));

		t.init(vehicles, descs);


		SensorStatusOutputStream sensorStatusOutputStream = new SensorStatusOutputStream(algs2);
		SensorDataInputStream sensorDataInputStream= new SensorDataInputStream(vehicles);

		SensorStatusInputStream localSensorStatusInputStream = new SensorStatusInputStream(algs2);
		SensorDataOutputStream localSensorDataOutputStream= new SensorDataOutputStream(vehicles);

		SensorStatusStreamManager sensorStatusStreamManager = new SensorStatusStreamManager(localSensorStatusInputStream,sensorStatusOutputStream);
		SensorDataStreamManager sensorDataStreamManager = new SensorDataStreamManager(sensorDataInputStream,localSensorDataOutputStream);

		t.initStreams(sensorStatusOutputStream, sensorDataInputStream);

		//make quite a few days to go by so cosmo algorithm output sensor statuses
		//7 days need to go by to get to deviation detection phase (and the initial sensor selection of 2 days)
		//(double noiseMean, double noiseSD, double noiseMax, double noiseMin, Distribution type) {
		Noise noiseN = new Noise(0,3,1,0,Noise.Distribution.GAUSSIAN);
		Noise noiseU = new Noise(0,3,10,-10,Noise.Distribution.UNIFORM);
		int time = 0;
		for(time = 0;time<10;time++){


			//generate random smaples here
			for(int sampleCounter = 0;sampleCounter< 100;sampleCounter++){

				for(Vehicle v : vehicles){
					for(int i = 0;i<100;i++){
					localSensorDataOutputStream.write(v,new SensorDataEvent(new Sensor(0,0),noiseN.generateNoise()));
					localSensorDataOutputStream.write(v,new SensorDataEvent(new Sensor(1,0),noiseN.generateNoise()));
					localSensorDataOutputStream.write(v,new SensorDataEvent(new Sensor(0,1),noiseU.generateNoise()));
					}
				}
				sensorDataStreamManager.flush();
				t.tick(new TimerEvent(time));
				
				Assert.assertEquals(false,t.threadFailedFlag);

			}

			for(;time<20;time++){
				t.tick(new TimerEvent(time));
				sensorStatusStreamManager.flush();

				for(AnomalyDetectionAlgorithm alg : algs){
					//there should be sensor status that are output
					Iterator<SensorStatusEvent> sensorStatuses = localSensorStatusInputStream.iterator(alg);

					boolean vehicle1Found = false;
					boolean vehicle2Found = false;
					boolean sensor_1_0_Found = false;
					boolean sensor_0_0_Found = false;
					boolean sensor_0_1_Found = false;
					boolean allSensorSelected = true;
					while(sensorStatuses.hasNext()){
						SensorStatusEvent e = sensorStatuses.next();

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
			}
			//Assert.assertEquals(COSMO.CosmoPhase.MODEL_GATHERING_WEEK1, cosmo.getState());
		}

		//now sensor status should be created
	}

	@Test
	public void testInitStreams() {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);
		List<Algorithm> algs2 = new ArrayList<Algorithm>(2);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		COSMO c = new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		algs.add(c);
		algs2.add(c);
		c = new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		algs.add(c);
		algs2.add(c);


		AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(algs,updateFrequency);

		SensorStatusOutputStream sensorStatusOutputStream = new SensorStatusOutputStream(algs2);
		SensorDataInputStream sensorDataInputStream= new SensorDataInputStream(vehicles);
		t.initStreams(sensorStatusOutputStream, sensorDataInputStream);

		Assert.assertEquals(true, t.getSensorStatusOutputStream() == sensorStatusOutputStream);
		Assert.assertEquals(true, t.getSensorDataInputStream() == sensorDataInputStream);
	}

	@Test
	public void testInitStreams_null_streams() {
		List<AnomalyDetectionAlgorithm> algs = new ArrayList<AnomalyDetectionAlgorithm>(2);
		List<Algorithm> algs2 = new ArrayList<Algorithm>(2);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		List<Sensor> sensors = new ArrayList<Sensor>(2);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(0,1));

		int updateFrequency = 7;
		int numSelectedSensors = 2;

		COSMO c = new COSMO(0,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		algs.add(c);
		algs2.add(c);
		c = new COSMO(1,vehicles,Histogram.DistanceMeasure.HELLIGNER,numSelectedSensors,updateFrequency);
		algs.add(c);
		algs2.add(c);

		AnomalyDetectionModelTimer t = new AnomalyDetectionModelTimer(algs,updateFrequency);

		SensorStatusOutputStream sensorStatusOutputStream = new SensorStatusOutputStream(algs2);
		SensorDataInputStream sensorDataInputStream= new SensorDataInputStream(vehicles);

		boolean flag = false;
		try{

			//t.initStreams(sensorStatusOutputStream, sensorDataInputStream);
			t.initStreams(sensorStatusOutputStream, null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			//t.initStreams(sensorStatusOutputStream, sensorDataInputStream);
			t.initStreams(null, sensorDataInputStream);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			t.initStreams(null, null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

	}

}
