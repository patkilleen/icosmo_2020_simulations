package test.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.Sensor;
import common.SensorInstance;
import common.SensorInstanceFactory;
import common.SensorMap;
import common.Vehicle;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import common.synchronization.MultiThreadedTimer;
import junit.framework.Assert;

public class TestSensorMap implements SensorInstanceFactory{

	@Test
	public void test_constructor() {
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		SensorMap map = new SensorMap(algs);

		algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}

		map = new SensorMap(algs);
	}


	@Test
	public void test_constructor_invalid_arg() {
		boolean flag = false;
		List<Algorithm> algs = new ArrayList<Algorithm>(0);

		try{
			SensorMap map = new SensorMap(algs);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;
		try{
			SensorMap map = new SensorMap(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		algs.add(new Algorithm(1));
		algs.add(new Algorithm(1));
		flag = false;
		try{
			SensorMap map = new SensorMap(algs);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}


	@Test
	public void test_getSensorInstance_not_initialized() {

		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		SensorMap map = new SensorMap(algs);

		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			Vehicle v = new Vehicle(i);
			Sensor s = new Sensor(i,i);

			SensorInstance si = map.getSensorInstance(new Algorithm(i), v, s);

			Assert.assertEquals(null, si);
		}



	}

	@Test
	public void test_getSensorInstance_null_argument() {

		int numSensors = 64;
		int numVehicles = 16;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, this);

		for(int i =0;i<algs.size();i++){
			Vehicle v = new Vehicle(i);
			SensorInstance si = map.getSensorInstance(algs.get(i), v, null);
			Assert.assertEquals(null,si);
		}

		for(int i =0;i<algs.size();i++){
			Sensor s = new Sensor(i,i);
			SensorInstance si = map.getSensorInstance(algs.get(i), null,s);
			Assert.assertEquals(null,si);
		}

		for(int i =0;i<algs.size();i++){
			Sensor s = new Sensor(i,i);
			SensorInstance si = map.getSensorInstance(algs.get(i), null,null);
			Assert.assertEquals(null,si);
		}

		for(int i =0;i<algs.size();i++){
			Vehicle v = new Vehicle(i);
			SensorInstance si = map.getSensorInstance(null, v, null);
			Assert.assertEquals(null,si);
		}

		for(int i =0;i<algs.size();i++){
			Sensor s = new Sensor(i,i);
			SensorInstance si = map.getSensorInstance(null, null,s);
			Assert.assertEquals(null,si);
		}

		for(int i =0;i<algs.size();i++){
			Sensor s = new Sensor(i,i);
			SensorInstance si = map.getSensorInstance(null, null,null);
			Assert.assertEquals(null,si);
		}

		for(int i =0;i<algs.size();i++){
			Sensor s = new Sensor(i,i);
			Vehicle v = new Vehicle(i);
			SensorInstance si = map.getSensorInstance(null, v,s);
			Assert.assertEquals(null,si);
		}
	}

	@Test
	public void test_getSensorInstance() {

		int numSensors = 64;
		int numVehicles = 18;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, this);

		int zvalueWindowSize = 16;//unused in this test
		for(int i =0;i<algs.size();i++){
			Algorithm alg = algs.get(i);
			for(int j =0;j<vehicles.size();j++){
				Vehicle v = vehicles.get(j);
				for(int k =0;k<sensors.size();k++){
					Sensor s = sensors.get(k);

					SensorInstance si = map.getSensorInstance(alg, v,s);
					SensorInstance expected = new SensorInstance(s,v,zvalueWindowSize);
					Assert.assertEquals(expected,si);

					//now make sure the sensor instance istn' equal (reference wize) to any other algorithms's instance
					for(int x =0;x<algs.size();x++){
						//skip the current alg
						if(i==x){
							continue;
						}
						Algorithm alg2 = algs.get(x);
						SensorInstance other = map.getSensorInstance(alg2, v,s);
						Assert.assertEquals(false,si == other);
					}

				}
			}

		}


	}



	@Test
	public void test_getSensorInstances_not_initialized() {

		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		SensorMap map = new SensorMap(algs);
		for(int i =0;i<algs.size();i++){
			Sensor s = new Sensor(i,i);

			List<SensorInstance> si = map.getSensorInstances(algs.get(i), s);

			Assert.assertEquals(0, si.size());
		}


	}


	@Test
	public void test_getSensorInstances_null_argument() {

		int numSensors = 64;
		int numVehicles = 16;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, this);

		for(int i =0;i<algs.size();i++){
			List<SensorInstance> si = map.getSensorInstances(algs.get(i), null);
			Assert.assertEquals(0,si.size());
		}

		for(int i =0;i<sensors.size();i++){
			List<SensorInstance> si = map.getSensorInstances(null,sensors.get(i));
			Assert.assertEquals(0,si.size());
		}

	}

	@Test
	public void test_getSensorInstances() {

		int numSensors = 64;
		int numVehicles = 18;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, this);

		for(int i =0;i<algs.size();i++){
			Algorithm alg = algs.get(i);
			for(int k =0;k<sensors.size();k++){
				Sensor s = sensors.get(k);

				List<SensorInstance> si = map.getSensorInstances(alg,s);

				//shouldn't be empty
				Assert.assertEquals(false,si.isEmpty());

				//should be an instance for each vehicle

				Assert.assertEquals(vehicles.size(),si.size());

				HashMap<Vehicle,Vehicle> vmap = new HashMap<Vehicle,Vehicle>();
				//now make sure that a sensor instance exists for each vehicle
				for(SensorInstance instance : si){

					Vehicle v = instance.getVehicle();

					//make sure its unique
					Assert.assertEquals(false,vmap.containsKey(v));

					vmap.put(v,v);
				}

			}

		}


	}

	@Test
	public void test_getSensorInstance_shared_references() {

		int numSensors = 64;
		int numVehicles = 18;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, this);

		for(int i =0;i<algs.size();i++){
			Algorithm alg = algs.get(i);
			for(int k =0;k<sensors.size();k++){
				Sensor s = sensors.get(k);

				List<SensorInstance> siList = map.getSensorInstances(alg, s);

				for(int j =0;j<vehicles.size();j++){
					Vehicle v = vehicles.get(j);


					SensorInstance si = map.getSensorInstance(alg, v,s);

					boolean actualFlag = false;
					//now make sure the isntance retreived is in the list of sensor instance retrived with sensor class
					for(SensorInstance other : siList){

						if(other == si){
							actualFlag = true;
						}
					}

					//make sure it was in list
					Assert.assertEquals(true, actualFlag);

				}
			}

		}


	}

	
	@Test
	public void test_getSensors_getVehicles() {


		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		List<Sensor> sensors = new ArrayList<Sensor>(2);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(3);
		
			algs.add(new Algorithm(0));
			algs.add(new Algorithm(1));

			sensors.add(new Sensor(0,0));
			sensors.add(new Sensor(0,1));
		
			vehicles.add(new Vehicle(0));
			vehicles.add(new Vehicle(1));
			vehicles.add(new Vehicle(2));
		
		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, this);
		
		List<Sensor> sensors2 = map.getSensors();
		
		Assert.assertEquals(2, sensors2.size());
		Assert.assertEquals(new Sensor(0,0), sensors2.get(0));
		Assert.assertEquals(new Sensor(0,1), sensors2.get(1));
		
		
		List<Vehicle> vehicles2 = map.getVehicles();
		
		Assert.assertEquals(3, vehicles2.size());
		Assert.assertEquals(new Vehicle(0), vehicles2.get(0));
		Assert.assertEquals(new Vehicle(1), vehicles2.get(1));
		Assert.assertEquals(new Vehicle(2), vehicles2.get(2));
	}
	@Test
	public void test_init_null_arguments() {

		int numSensors = 64;
		int numVehicles = 18;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}
		SensorMap map = new SensorMap(algs);

		boolean flag = false;
		try{
			map.init(vehicles, null, this);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

		flag = false;
		try{
			map.init(null, sensors, this);

		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

		flag = false;
		try{
			map.init(vehicles, sensors,null);

		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

		flag = false;
		try{
			map.init(null, null,null);

		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

	}


	@Test
	public void test_init_illegal_arguments() {

		int numSensors = 64;
		int numVehicles = 18;
		List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
		List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
		List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
		for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
			algs.add(new Algorithm(i));	
		}
		for(int i =0;i<numSensors;i++){
			sensors.add(new Sensor(i,i));
		}

		for(int i =0;i<numVehicles;i++){
			vehicles.add(new Vehicle(i));
		}


		SensorMap map = new SensorMap(algs);

		boolean flag = false;
		try{
			map.init(vehicles, new ArrayList<Sensor>(numSensors), this);

		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

		flag = false;
		try{
			map.init( new ArrayList<Vehicle>(numVehicles), new ArrayList<Sensor>(numSensors), this);

		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);


		flag = false;
		try{
			map.init( new ArrayList<Vehicle>(numVehicles), sensors, this);

		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);


	}
	@Override
	public SensorInstance newInstance(Vehicle v, Sensor s) {
		return new TestSensorInstance(s,v);
	}

	private static class TestSensorInstance extends SensorInstance{

		public int counter;
		public TestSensorInstance(int aPgn, int aSpn, Vehicle vehicle) {
			super(aPgn, aSpn, vehicle,16);//dummy value window size
			// TODO Auto-generated constructor stub
		}
		public TestSensorInstance(Sensor s, Vehicle vehicle) {
			super(s, vehicle,16);//dummy value window size
			// TODO Auto-generated constructor stub
		}


	}


	@Test
	public void testMultiThreadedAccess () throws InterruptedException{
		for(int iterations = 0; iterations < 4; iterations++){
			int numSensors = 64;
			int numVehicles = 18;
			List<Algorithm> algs = new ArrayList<Algorithm>(Algorithm.ICOSMO_ID_MOD);
			List<Sensor> sensors = new ArrayList<Sensor>(numSensors);
			List<Vehicle> vehicles = new ArrayList<Vehicle>(numVehicles);
			for(int i =0;i<Algorithm.ICOSMO_ID_MOD;i++){
				algs.add(new Algorithm(i));	
			}
			for(int i =0;i<numSensors;i++){
				sensors.add(new Sensor(i,i));
			}

			for(int i =0;i<numVehicles;i++){
				vehicles.add(new Vehicle(i));
			}
			SensorMap map = new SensorMap(algs);
			map.init(vehicles, sensors, this);


			Thread[] threads = new Thread[algs.size()];
			//simulate a vehicle in seperate threads
			for(int i = 0;i < algs.size();i++){

				TestWorker helper = new TestWorker(algs.get(i),map,sensors,vehicles);
				Thread t = new Thread(helper);
				threads[i] = t;
				t.start();

			}//end iterate vehicles

			//wait for all simulated vehicle to finish this time-tick's simulation
			for(Thread t: threads){
				t.join();

			}


			//now make sure that all the sensor instances counters are the same (no race condition)

			for(int i =0;i<algs.size();i++){
				Algorithm alg = algs.get(i);
				for(int j =0;j<vehicles.size();j++){
					Vehicle v = vehicles.get(j);
					for(int k =0;k<sensors.size();k++){
						Sensor s = sensors.get(k);

						TestSensorInstance si = (TestSensorInstance) map.getSensorInstance(alg, v,s);
						Assert.assertEquals(TestWorker.COUNTER_MAX,si.counter);

					}
				}
			}
		}//end do the threads test multiple times
	}

	private static class TestWorker implements Runnable{

		public final static int COUNTER_MAX = 1000;
		public Algorithm alg;
		public SensorMap map;
		public List<Sensor> sensors;
		public List<Vehicle> vehicles;


		public TestWorker(Algorithm alg, SensorMap map, List<Sensor> sensors, List<Vehicle> vehicles) {
			super();
			this.alg = alg;
			this.map = map;
			this.sensors = sensors;
			this.vehicles = vehicles;
		}


		@Override
		public void run() {
			//increment all sensor instance conunters to 10000
			for(int i = 0;i<COUNTER_MAX;i++){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						TestSensorInstance si = (TestSensorInstance)map.getSensorInstance(alg, v, s);
						si.counter++;
					}
				}
			}

		}

	}
}
