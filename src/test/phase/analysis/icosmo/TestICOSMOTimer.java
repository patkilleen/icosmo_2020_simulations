package test.phase.analysis.icosmo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Algorithm;
import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.SensorCollection;
import common.SensorMap;
import common.Vehicle;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.RepairStreamManager;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import common.event.stream.StreamManager;
import common.event.stream.TimeStampedSensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusOutputStream;
import phase.analysis.icosmo.ICOSMO;
import phase.analysis.icosmo.ICOSMOSensorInstance;
import phase.analysis.icosmo.ICOSMOTimer;

public class TestICOSMOTimer extends ICOSMOTimer{

	@Test
	public void testICOSMOTimer() {
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		ICOSMOTimer  t = new ICOSMOTimer(algs,vehicles,rin,ssin,tsssout,icosmo,mode);

		Assert.assertEquals(true,t.getRepairInputStream() == rin);
		Assert.assertEquals(true,t.getSensorStatusInputStream() == ssin);
		Assert.assertEquals(true,t.getTimeStampedSensorStatusOutputStream() == tsssout);

	}

	@Test
	public void testInit() {
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();

		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance)map.getSensorInstance(alg, v, s);
					Assert.assertEquals(s.getPgn(),i.getPgn());
					Assert.assertEquals(s.getSpn(),i.getSpn());
					Assert.assertEquals(v,i.getVehicle());
				}
			}
		}
	}


	@Test
	public void testTick() throws InterruptedException{
		double stalnessThreshold=0;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();

		double zscore = 1.0;//shouldn't affect results, regardless of value
		double zvalue = 0.6;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


		int eventOutputCounter = 0;
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					SensorStatusEvent e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
					localSsOut.write(alg, e);
					eventOutputCounter++;
				}
			}
		}

		sensorStatusManager.flush();

		t.tick(new TimerEvent(0));

		tsSensorStatusManager.flush();

		int eventOutputCounterActual = 0;
		for(Algorithm alg : algs){
			Iterator<TimeStampedSensorStatusEvent> it = localTsssIn.iterator(alg);

			Assert.assertEquals(true, it.hasNext());
			while(it.hasNext()){
				TimeStampedSensorStatusEvent e = it.next();
				Vehicle v = e.getVehicle();
				Algorithm actualAlg = e.getAlgorithm();
				Sensor s = e.getSensor();
				double zscoreActual = e.getZscore();
				double zvalueActual = e.getZvalue();

				eventOutputCounterActual++;
				Assert.assertEquals(true, vehicles.contains(v));
				Assert.assertEquals(true, sensors.contains(s));
				Assert.assertEquals(true, actualAlg.equals(alg));
				Assert.assertEquals(zvalueActual,zscoreActual,0.0001);
				Assert.assertEquals(zvalue,zvalueActual,0.0001);
				Assert.assertEquals(0,e.getTimerEvent().getTime());
			}
		}
		Assert.assertEquals(eventOutputCounter,eventOutputCounterActual);
	}

	@Test
	public void testDeviationExists_leftTimeWindow_0() throws InterruptedException {
		double stalnessThreshold=0;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();

		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations
		int count = 0;
		//int time = 0;

		for(int time = 0; time < 30; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && time == 10){
							e = new SensorStatusEvent(v,s,alg,0,true,0);
							count ++;
						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}

						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						//make sure no exception occurs
						t.deviationExists(alg,new TimerEvent(time), v, s);
					}
				}
			}
		}

		Assert.assertEquals(1,count);

		//deviation doesn't exist
		//[0-9]
		//so deviation exists
		//[10-14]
		//deviation doesn't exist
		//[15-29]

		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));
					}
				}
			}
		}


		for(int i =10;i< 15;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						//deviating sensor?
						if(s.equals(new Sensor(0,0)) && v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){

							Assert.assertEquals(true,t.deviationExists(alg,new TimerEvent(i), v, s));
						}else{
							Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));	
						}
					}
				}
			}
		}

		for(int i =15;i< 30;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));
					}
				}
			}
		}

	}


	@Test
	public void testDeviationExists_leftTimeWindow_1() throws InterruptedException {
		double stalnessThreshold=0;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(1);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();

		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations
		int count = 0;
		//int time = 0;

		for(int time = 0; time < 30; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && time == 10){
							e = new SensorStatusEvent(v,s,alg,0,true,0);
							count ++;
						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}

						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						//make sure no exception occurs
						t.deviationExists(alg,new TimerEvent(time), v, s);
					}
				}
			}
		}

		Assert.assertEquals(1,count);

		//deviation doesn't exist
		//[0-9]
		//so deviation exists
		//[10-14]
		//deviation doesn't exist
		//[15-29]

		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));
					}
				}
			}
		}


		for(int i =10;i< 16;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						//deviating sensor?
						if(s.equals(new Sensor(0,0)) && v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){

							Assert.assertEquals(true,t.deviationExists(alg,new TimerEvent(i), v, s));
						}else{
							Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));	
						}
					}
				}
			}
		}

		for(int i =16;i< 30;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));
					}
				}
			}
		}

	}

	@Test
	public void testDeviationExists_leftTimeWindow_x() throws InterruptedException {
		int totalTicks = 100;
		for(int leftTimeWindow = 0; leftTimeWindow < totalTicks; leftTimeWindow++){	
			double stalnessThreshold=0;
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
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);

			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();

			double zscore = 0.3;
			double zvalue = 0.3;
			boolean isCOSMOSensor = true;


			t.setLeftTimeWindowDeviations(leftTimeWindow);
			RepairOutputStream localRout = new RepairOutputStream (vehicles);
			SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
			TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

			RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
			SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
			StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


			//for time 0-9, no deviations
			int count = 0;
			//int time = 0;

			for(int time = 0; time < totalTicks; time++){

				for(Algorithm alg : algs){
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){

							SensorStatusEvent e = null;
							//desired time and sensor to change zscore/value to 0?
							if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && time == 10){
								e = new SensorStatusEvent(v,s,alg,0,true,0);
								count ++;
							}else{
								e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
							}

							localSsOut.write(alg, e);
						}
					}
				}

				sensorStatusManager.flush();

				t.tick(new TimerEvent(time));

				tsSensorStatusManager.flush();
				
				for(Algorithm alg : algs){
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){
							//make sure no exception occurs
							t.deviationExists(alg,new TimerEvent(time), v, s);
						}
					}
				}
			}

			Assert.assertEquals(1,count);

			//deviation doesn't exist
			//[0-9]
			//so deviation exists
			//[10-14]
			//deviation doesn't exist
			//[15-29]

			for(int i =0;i< 10;i++){
				for(Algorithm alg : algs){
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){
							Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));
						}
					}
				}
			}


			for(int i =10;i< 15+leftTimeWindow && i < totalTicks;i++){
				for(Algorithm alg : algs){
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){
							//deviating sensor?
							if(s.equals(new Sensor(0,0)) && v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){

								Assert.assertEquals(true,t.deviationExists(alg,new TimerEvent(i), v, s));
							}else{
								Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));	
							}
						}
					}
				}
			}

			for(int i =15+leftTimeWindow;i< totalTicks;i++){
				for(Algorithm alg : algs){
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){
							Assert.assertEquals(false,t.deviationExists(alg,new TimerEvent(i), v, s));
						}
					}
				}
			}
		}		
	}
	@Test
	public void testatLeastOneDeviationExists_1()throws InterruptedException { {


		double stalnessThreshold=-1000;
		double 	candicacyThreshold=1000;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}


		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 60; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && (time == 10 || time == 35)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && (time == 35)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);
						}else if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && time == 36){
							e = new SensorStatusEvent(v,s,alg,1,true,1);//put big zvalue to make sure not deviation at tiem 36

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && time == 36){
							e = new SensorStatusEvent(v,s,alg,1,true,1);//put big zvalue to make sure not deviation at tiem 36
						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}

						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
						//make sure no exception occurs
						t.atleastOneDeviationOccured(alg,new TimerEvent(time), v, sensors);
				}
			}
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-35]
		//[36] deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[36-60]//no deviation


		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){

					Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));

				}
			}
		}


		for(int i =10;i< 15;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if(v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){

						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	
					}

				}
			}
		}

		for(int i =15;i< 60;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if(i == 35 && v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	//breaking here at time 36 for some reason 
					}
				}
			}
		}

	}

	}

	@Test
	public void testatLeastOneDeviationExists_2()throws InterruptedException { {


		double stalnessThreshold=-1000;
		double 	candicacyThreshold=1000;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}


		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}

						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
						//make sure no exception occurs
						t.atleastOneDeviationOccured(alg,new TimerEvent(time), v, sensors);
				}
			}
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation


		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){

					Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));

				}
			}
		}


		for(int i =10;i< 15;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if(v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){

						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	
					}

				}
			}
		}

		for(int i =15;i< 90;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if((i >= 51 && i <= 79) && v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	//breaking here at time 36 for some reason 
					}
				}
			}
		}

	}

	}


	@Test
	public void testatLeastOneDeviationExists_3()throws InterruptedException { {


		double stalnessThreshold=-1000;
		double 	candicacyThreshold=1000;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}


		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(0))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}

						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
						//make sure no exception occurs
						t.atleastOneDeviationOccured(alg,new TimerEvent(time), v, sensors);
				}
			}
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation


		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){

					Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));

				}
			}
		}


		for(int i =10;i< 15;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if(v.equals(new Vehicle(0))&& alg.equals(new Algorithm(0))){

						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	
					}

				}
			}
		}

		for(int i =15;i< 90;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if((i >= 51 && i <= 79) && (v.equals(new Vehicle(0)) || v.equals(new Vehicle(1)))&& alg.equals(new Algorithm(0))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	//breaking here at time 36 for some reason 
					}
				}
			}
		}

	}

	}

	@Test
	public void testatLeastOneDeviationExists_4()throws InterruptedException { {


		double stalnessThreshold=-1000;
		double 	candicacyThreshold=1000;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}


		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}

						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
						//make sure no exception occurs
						t.atleastOneDeviationOccured(alg,new TimerEvent(time), v, sensors);
				}
			}
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation


		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){

					Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));

				}
			}
		}


		for(int i =10;i< 15;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if(v.equals(new Vehicle(0))&& alg.equals(new Algorithm(1))){

						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	
					}

				}
			}
		}

		for(int i =15;i< 90;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if((i >= 51 && i <= 79) && (v.equals(new Vehicle(1)))&& alg.equals(new Algorithm(0))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else if((i >= 51 && i <= 79) && (v.equals(new Vehicle(0)))&& alg.equals(new Algorithm(1))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	//breaking here at time 36 for some reason 
					}
				}
			}
		}

	}

	}

	@Test
	public void testatLeastOneDeviationExists_5()throws InterruptedException { {


		double stalnessThreshold=-1000;
		double 	candicacyThreshold=1000;
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}


		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}


						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
						//make sure no exception occurs
						t.atleastOneDeviationOccured(alg,new TimerEvent(time), v, sensors);
				}
			}
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation


		for(int i =0;i< 10;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){

					Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));

				}
			}
		}


		for(int i =10;i< 15;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					//deviating sensor?
					if(v.equals(new Vehicle(0))&& alg.equals(new Algorithm(1))){

						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	
					}

				}
			}
		}

		for(int i =15;i< 90;i++){
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					if((v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (i == 60 )){
						//sensor gets unselected, so deviations don't count
						ICOSMOSensorInstance i2 = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(1,0));
						i2.setCosmoSensor(false);
					}
					//deviating sensor?
					if((i >= 51 && i <= 59) && (v.equals(new Vehicle(1)))&& alg.equals(new Algorithm(0))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else if((i >= 51 && i <= 79) && (v.equals(new Vehicle(0)))&& alg.equals(new Algorithm(1))){
						Assert.assertEquals(true,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));
					}else{
						Assert.assertEquals(false,t.atleastOneDeviationOccured(alg,new TimerEvent(i), v, sensors));	//breaking here at time 36 for some reason 
					}

				}
			}
		}

	}

	}
	@Test
	public void testCheckForRepairs() throws InterruptedException {
		double stalnessThreshold=0;
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
		double sensorInterestThreshold  = 0.4;
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		Vehicle expectedVehicle = new Vehicle(1);
		TimerEvent expectedTime = new TimerEvent(10);

		FaultDescription expectedFD= new FaultDescription(-1,null);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		expectedFD.init(sensors);
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));



		TestICOSMOTimerCheckRepairs  t = new TestICOSMOTimerCheckRepairs(expectedVehicle,expectedTime,expectedFD);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();

		double zscore = 1.0;//shouldn't affect results, regardless of value
		double zvalue = 0.6;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);

		for(int time = 0; time < expectedTime.getTime();time++){
			int eventOutputCounter = 0;
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						SensorStatusEvent e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						localSsOut.write(alg, e);
						eventOutputCounter++;
					}
				}
			}

			sensorStatusManager.flush();

			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();

		}
	
		//create repair at time 10
		RepairEvent repairEvent = new RepairEvent(expectedVehicle,expectedFD);
		localRout.write(expectedVehicle, repairEvent);
		repairManager.flush();
		t.tick(new TimerEvent(expectedTime.getTime()));
	}

	@Test
	public void testHandleFaultInvolvedSensor_contribution_1() throws InterruptedException {

		double stalnessThreshold=-0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=1;
		double desiredPrecision=1;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=2;
		int zValueWindowSize = 30;
		double sensorInterestThreshold  = 0.4;
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}

/*		ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(1,0));
		i.setCosmoSensor(false);
*/
		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


		FaultDescription expectedFD= new FaultDescription(-1,null);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		expectedFD.init(sensors);
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
		
		RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				if(time == 10 && alg.equals(new Algorithm(1))){
					localRout.write(new Vehicle(0), repairEvent);
				}
				/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
					localRout.write(new Vehicle(1), repairEvent);
				}*/
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}


						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();
			repairManager.flush();
			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
		ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(6.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
	
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}

	@Test
	public void testHandleFaultInvolvedSensor_contribution_2() throws InterruptedException {

		double stalnessThreshold=-0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=1;
		double desiredPrecision=1;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=2;
		int zValueWindowSize = 30;
		double sensorInterestThreshold  = 0.4;
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}

/*		ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(1,0));
		i.setCosmoSensor(false);
*/
		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


		FaultDescription expectedFD= new FaultDescription(-1,null);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		expectedFD.init(sensors);
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
		
		RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				if(time == 10 && alg.equals(new Algorithm(1))){
					localRout.write(new Vehicle(0), repairEvent);
				}
				/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
					localRout.write(new Vehicle(1), repairEvent);
				}*/
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if(((s.equals( new Sensor(0,0))) || s.equals( new Sensor(0,1))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}


						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();
			repairManager.flush();
			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
		ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(6.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(6.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
	
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}

	
	@Test
	public void testHandleFaultInvolvedSensor_potential_contribution_1() throws InterruptedException {

		double stalnessThreshold=-0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=1;
		double desiredPrecision=1;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=2;
		int zValueWindowSize = 30;
		double sensorInterestThreshold  = 0.4;
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}

		ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		i.setCosmoSensor(false);
/*		ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(1,0));
		i.setCosmoSensor(false);
*/
		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


		FaultDescription expectedFD= new FaultDescription(-1,null);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		expectedFD.init(sensors);
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
		
		RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){

			for(Algorithm alg : algs){
				if(time == 20 && alg.equals(new Algorithm(1))){
					localRout.write(new Vehicle(0), repairEvent);
				}
				/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
					localRout.write(new Vehicle(1), repairEvent);
				}*/
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}


						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();
			repairManager.flush();
			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
		}


		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);
		
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);

		
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}
	

	@Test
	public void testHandleFaultInvolvedSensor_potential_contribution_2() throws InterruptedException {

		double stalnessThreshold=-0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=1;
		double desiredPrecision=1;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=2;
		int zValueWindowSize = 30;
		double sensorInterestThreshold  = 0.4;
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

		RepairInputStream rin = new RepairInputStream (vehicles);
		SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
		TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
		Mode mode = ICOSMOTimer.Mode.RECALL;

		TestICOSMOTimer  t = new TestICOSMOTimer();
		t.setDeviationThreshold(0.28);
		t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setLeftTimeWindowDeviations(0);
		t.setParitionKeys(algs);
		t.init(sensors);

		SensorMap map = t.getSensorMap();


		//make sure they all cosmo sensors to consider for deviation checks
		for(Algorithm alg : algs){
			for(Vehicle v : vehicles){
				for(Sensor s: sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
					i.setCosmoSensor(true);
				}
			}
		}

		ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		i.setCosmoSensor(false);
/*		ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		i.setCosmoSensor(false);
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(1,0));
		i.setCosmoSensor(false);
*/
		double zscore = 0.3;
		double zvalue = 0.3;
		boolean isCOSMOSensor = true;

		RepairOutputStream localRout = new RepairOutputStream (vehicles);
		SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
		TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

		RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
		SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
		StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


		FaultDescription expectedFD= new FaultDescription(-1,null);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		expectedFD.init(sensors);
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
		expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
		
		RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
		//for time 0-9, no deviations

		//int time = 0;

		for(int time = 0; time < 90; time++){
			if(time == 20 ){
				localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
			}
			for(Algorithm alg : algs){
				
				/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
					localRout.write(new Vehicle(1), repairEvent);
				}*/
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){

						SensorStatusEvent e = null;
						//desired time and sensor to change zscore/value to 0?
						if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
							e = new SensorStatusEvent(v,s,alg,0,true,0);

						}else{
							e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
						}


						localSsOut.write(alg, e);
					}
				}
			}

			sensorStatusManager.flush();
			repairManager.flush();
			t.tick(new TimerEvent(time));

			tsSensorStatusManager.flush();
			
		}
		}

		@Test
		public void testHandleFaultInvolvedSensor_remove_cosmo_sensor_1() throws InterruptedException {

			double stalnessThreshold=3;
			double 	candicacyThreshold=7;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=1;
			double potentialContrDecreaseMod=1;
			double potentialContrIncreaseMod=1;
			double desiredRecall=1;
			double desiredPrecision=1;
			double defaultContribution=5;
			double defaultPotentialContribution=5;
			int numFaultInvolvedSensorEstimation=2;
			int zValueWindowSize = 30;
			double sensorInterestThreshold  = 0.4;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
			t.setLeftTimeWindowDeviations(0);
			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();


			//make sure they all cosmo sensors to consider for deviation checks
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						i.setCosmoSensor(true);
					}
				}
			}

	/*		ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
			i.setCosmoSensor(false);
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
			i.setCosmoSensor(false);*/
	
			double zscore = 0.3;
			double zvalue = 0.3;
			boolean isCOSMOSensor = true;

			RepairOutputStream localRout = new RepairOutputStream (vehicles);
			SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
			TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

			RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
			SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
			StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


			FaultDescription expectedFD= new FaultDescription(-1,null);

			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
			expectedFD.init(sensors);
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
			
			RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
			//for time 0-9, no deviations

			//int time = 0;

			for(int time = 0; time < 90; time++){
				if(time == 20 ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 21){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				if(time == 31){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 30 ){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				for(Algorithm alg : algs){
					
					/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
						localRout.write(new Vehicle(1), repairEvent);
					}*/
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){

							SensorStatusEvent e = null;
							//desired time and sensor to change zscore/value to 0?
							if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else{
								e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
							}


							localSsOut.write(alg, e);
						}
					}
				}

				sensorStatusManager.flush();
				repairManager.flush();
				t.tick(new TimerEvent(time));

				tsSensorStatusManager.flush();
				
			}


			

		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
			ICOSMOSensorInstance	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
	
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}
	
	
		@Test
		public void testHandleFaultInvolvedSensor_remove_cosmo_sensor_2() throws InterruptedException {

			double stalnessThreshold=3;
			double 	candicacyThreshold=8;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=1;
			double potentialContrDecreaseMod=1;
			double potentialContrIncreaseMod=1;
			double desiredRecall=1;
			double desiredPrecision=1;
			double defaultContribution=5;
			double defaultPotentialContribution=5;
			int numFaultInvolvedSensorEstimation=2;
			int zValueWindowSize = 30;
			double sensorInterestThreshold  = 0.4;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
			t.setLeftTimeWindowDeviations(0);
			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();


			//make sure they all cosmo sensors to consider for deviation checks
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						i.setCosmoSensor(true);
					}
				}
			}

			ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
			i.setCosmoSensor(false);
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
			i.setCosmoSensor(false);
	/*		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
			i.setCosmoSensor(false);*/
	
			double zscore = 0.3;
			double zvalue = 0.3;
			boolean isCOSMOSensor = true;

			RepairOutputStream localRout = new RepairOutputStream (vehicles);
			SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
			TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

			RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
			SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
			StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


			FaultDescription expectedFD= new FaultDescription(-1,null);

			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
			expectedFD.init(sensors);
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
			
			RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
			//for time 0-9, no deviations

			//int time = 0;

			for(int time = 0; time < 90; time++){
				if(time == 20 ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 21){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				if(time == 31){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 30 ){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				for(Algorithm alg : algs){
					
					/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
						localRout.write(new Vehicle(1), repairEvent);
					}*/
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){

							SensorStatusEvent e = null;
							//desired time and sensor to change zscore/value to 0?
							if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else{
								e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
							}


							localSsOut.write(alg, e);
						}
					}
				}

				sensorStatusManager.flush();
				repairManager.flush();
				t.tick(new TimerEvent(time));

				tsSensorStatusManager.flush();
				
			}


			

		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(7.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(7.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(false,i.isCosmoSensor());
	
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}
	
		

		@Test
		public void testHandleFaultInvolvedSensor_add_cosmo_sensor_1() throws InterruptedException {

			double stalnessThreshold=2;
			double 	candicacyThreshold=7;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=1;
			double potentialContrDecreaseMod=1;
			double potentialContrIncreaseMod=1;
			double desiredRecall=1;
			double desiredPrecision=1;
			double defaultContribution=5;
			double defaultPotentialContribution=5;
			int numFaultInvolvedSensorEstimation=2;
			int zValueWindowSize = 30;
			double sensorInterestThreshold  = 0.4;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
			t.setLeftTimeWindowDeviations(0);
			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();


			//make sure they all cosmo sensors to consider for deviation checks
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						i.setCosmoSensor(true);
					}
				}
			}

			ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
			i.setCosmoSensor(false);
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
			i.setCosmoSensor(false);
	/*		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
			i.setCosmoSensor(false);*/
	
			double zscore = 0.3;
			double zvalue = 0.3;
			boolean isCOSMOSensor = true;

			RepairOutputStream localRout = new RepairOutputStream (vehicles);
			SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
			TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

			RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
			SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
			StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


			FaultDescription expectedFD= new FaultDescription(-1,null);

			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
			expectedFD.init(sensors);
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
			
			RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
			//for time 0-9, no deviations

			//int time = 0;

			for(int time = 0; time < 90; time++){
				if(time == 20 ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 21){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				if(time == 31 ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 30){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				for(Algorithm alg : algs){
					
					/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
						localRout.write(new Vehicle(1), repairEvent);
					}*/
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){

							SensorStatusEvent e = null;
							//desired time and sensor to change zscore/value to 0?
							if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else{
								e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
							}


							localSsOut.write(alg, e);
						}
					}
				}

				sensorStatusManager.flush();
				repairManager.flush();
				t.tick(new TimerEvent(time));

				tsSensorStatusManager.flush();
				
			}


			

		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}
	
		@Test
		public void testHandleFaultInvolvedSensor_add_cosmo_sensor_2() throws InterruptedException {

			double stalnessThreshold=2;
			double 	candicacyThreshold=7;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=1;
			double potentialContrDecreaseMod=1;
			double potentialContrIncreaseMod=1;
			double desiredRecall=1;
			double desiredPrecision=1;
			double defaultContribution=5;
			double defaultPotentialContribution=5;
			int numFaultInvolvedSensorEstimation=2;
			int zValueWindowSize = 30;
			double sensorInterestThreshold  = 0.4;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
			t.setLeftTimeWindowDeviations(0);
			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();


			//make sure they all cosmo sensors to consider for deviation checks
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						i.setCosmoSensor(true);
					}
				}
			}

			ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
			i.setCosmoSensor(false);
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
			i.setCosmoSensor(false);
	
			double zscore = 0.3;
			double zvalue = 0.3;
			boolean isCOSMOSensor = true;

			RepairOutputStream localRout = new RepairOutputStream (vehicles);
			SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
			TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

			RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
			SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
			StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


			FaultDescription expectedFD= new FaultDescription(-1,null);

			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
			expectedFD.init(sensors);
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
			
			RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
			//for time 0-9, no deviations

			//int time = 0;

			for(int time = 0; time < 90; time++){
				if(time == 20){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 21 ){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				if(time == 31 ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 30){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				for(Algorithm alg : algs){
					
					/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
						localRout.write(new Vehicle(1), repairEvent);
					}*/
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){

							SensorStatusEvent e = null;
							//desired time and sensor to change zscore/value to 0?
							if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else{
								e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
							}


							localSsOut.write(alg, e);
						}
					}
				}

				sensorStatusManager.flush();
				repairManager.flush();
				t.tick(new TimerEvent(time));

				tsSensorStatusManager.flush();
				
			}


			

		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(3.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
	
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}
		
		@Test
		public void testHandleFaultInvolvedSensor_add_cosmo_sensor_3() throws InterruptedException {

			double stalnessThreshold=2;
			double 	candicacyThreshold=7;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=1;
			double potentialContrDecreaseMod=1;
			double potentialContrIncreaseMod=1;
			double desiredRecall=1;
			double desiredPrecision=1;
			double defaultContribution=5;
			double defaultPotentialContribution=5;
			int numFaultInvolvedSensorEstimation=2;
			int zValueWindowSize = 30;
			double sensorInterestThreshold  = 0.4;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

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

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
			t.setLeftTimeWindowDeviations(0);
			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();


			//make sure they all cosmo sensors to consider for deviation checks
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						i.setCosmoSensor(true);
					}
				}
			}

			/*ICOSMOSensorInstance 	i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
			i.setCosmoSensor(false);
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,1));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
			i.setCosmoSensor(false);
			*/
			ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
			i.setCosmoSensor(false);
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
			i.setCosmoSensor(false);
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
			i.setCosmoSensor(false);
	
			double zscore = 0.3;
			double zvalue = 0.3;
			boolean isCOSMOSensor = true;

			RepairOutputStream localRout = new RepairOutputStream (vehicles);
			SensorStatusOutputStream localSsOut = new SensorStatusOutputStream(algs); 
			TimeStampedSensorStatusInputStream localTsssIn = new TimeStampedSensorStatusInputStream(algs);

			RepairStreamManager repairManager = new RepairStreamManager(rin,localRout);
			SensorStatusStreamManager sensorStatusManager = new SensorStatusStreamManager(ssin,localSsOut);
			StreamManager<TimeStampedSensorStatusEvent,Algorithm> tsSensorStatusManager = new StreamManager<TimeStampedSensorStatusEvent,Algorithm>(localTsssIn,tsssout);


			FaultDescription expectedFD= new FaultDescription(-1,null);

			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
			expectedFD.init(sensors);
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,0)));
			expectedFD.addAffectedSensor(new FaultInvolvedSensorBehavior(n,1,2,noisePValues,type,new Sensor(0,1)));
			
			//RepairEvent repairEvent = new RepairEvent(new Vehicle(0),expectedFD);
			//for time 0-9, no deviations

			//int time = 0;

			for(int time = 0; time < 90; time++){
				if(time == 20  ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 21 ){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				if(time == 31 ){
					localRout.write(new Vehicle(0), new RepairEvent(new Vehicle(0),expectedFD));
				}
				if(time == 30 ){
					localRout.write(new Vehicle(1), new RepairEvent(new Vehicle(1),expectedFD));
				}
				for(Algorithm alg : algs){
					
					/*if(time == 20 && alg.equals(new Algorithm(0))){//no deviation
						localRout.write(new Vehicle(1), repairEvent);
					}*/
					for(Vehicle v : vehicles){
						for(Sensor s: sensors){

							SensorStatusEvent e = null;
							//desired time and sensor to change zscore/value to 0?
							if((s.equals( new Sensor(0,0))) && (v.equals(new Vehicle(0))) && (alg.equals(new Algorithm(1))) && (time == 10 || time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else if((s.equals( new Sensor(1,0))) && (v.equals(new Vehicle(1))) && (alg.equals(new Algorithm(0))) && (time == 50 || time == 51)){
								e = new SensorStatusEvent(v,s,alg,0,true,0);

							}else{
								e = new SensorStatusEvent(v,s,alg,zscore,true,zvalue);
							}


							localSsOut.write(alg, e);
						}
					}
				}

				sensorStatusManager.flush();
				repairManager.flush();
				t.tick(new TimerEvent(time));

				tsSensorStatusManager.flush();
				
			}


			

		//sensor 0,0, vehiel 0, alg 0
		//deviation doesn't exist
		//[0-9]
		//so deviation exists 
		//[10-14]
		//deviation doesn't exist
		//[15-50]
		// deviation for sensor 0,1 and 0,0 for vehicle 0 and alg 0
		//[51-79]//deviation
		//[79-90] no deviation
		
		//make sure repair evvent occurs when deviates
		//sensor (0,0) should have contribution 6, since it succeeded in finding a fault at time 10
			
			//below there wasn't a deviation, and no sensor picked it up, so potential contribution goes down (3)
			//and not cosmo sensors
			
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
			Assert.assertEquals(3.0,i.getContribution(),0.0001);
			Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(true,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
			Assert.assertEquals(3.0,i.getContribution(),0.0001);
			Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(true,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,1));
			Assert.assertEquals(3.0,i.getContribution(),0.0001);
			Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(true,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
			Assert.assertEquals(3.0,i.getContribution(),0.0001);
			Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(true,i.isCosmoSensor());
				
			
			//below sensor got added to cosmo
			
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		Assert.assertEquals(true,i.isCosmoSensor());
			
	/*	
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,1));
		Assert.assertEquals(4.0,i.getContribution(),0.0001);
		Assert.assertEquals(5.0,i.getPotentialContribution(),0.0001);
		
		i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(1,0));
		Assert.assertEquals(5.0,i.getContribution(),0.0001);
		Assert.assertEquals(6.0,i.getPotentialContribution(),0.0001);*/
	}
		
		@Test
		public void testResetSensorMap(){
			double stalnessThreshold=2;
			double 	candicacyThreshold=7;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=1;
			double potentialContrDecreaseMod=1;
			double potentialContrIncreaseMod=1;
			double desiredRecall=1;
			double desiredPrecision=1;
			double defaultContribution=5;
			double defaultPotentialContribution=5;
			int numFaultInvolvedSensorEstimation=2;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

			List<Sensor> selectedSensors = new ArrayList<Sensor>(3);
			selectedSensors.add(new Sensor(0,0));
			selectedSensors.add(new Sensor(0,1));
			List<Algorithm> algs =  new ArrayList<Algorithm>(2);
			algs.add(new TestSensorCollection(0,selectedSensors));
			selectedSensors = new ArrayList<Sensor>(3);
			selectedSensors.add(new Sensor(0,0));
			algs.add(new TestSensorCollection(1,selectedSensors));

			List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
			vehicles.add(new Vehicle(0));
			vehicles.add(new Vehicle(1));

			List<Sensor> sensors = new ArrayList<Sensor>(2);
			sensors.add(new Sensor(0,0));
			sensors.add(new Sensor(1,0));
			sensors.add(new Sensor(0,1));

			RepairInputStream rin = new RepairInputStream (vehicles);
			SensorStatusInputStream ssin = new SensorStatusInputStream(algs); 
			TimeStampedSensorStatusOutputStream tsssout = new TimeStampedSensorStatusOutputStream(algs);
			Mode mode = ICOSMOTimer.Mode.RECALL;

			TestICOSMOTimer  t = new TestICOSMOTimer();
			t.setDeviationThreshold(0.28);
			t.init(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
			t.setLeftTimeWindowDeviations(0);
			t.setParitionKeys(algs);
			t.init(sensors);

			SensorMap map = t.getSensorMap();


			//make sure they all cosmo sensors to consider for deviation checks
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						i.setCosmoSensor(true);
						i.addZvalue(0.1);
						i.addZvalue(0.2);
						i.addZvalue(0.3);
						i.setContribution(100);
						i.setPotentialContribution(100);
					}
				}
			}
			t.resetSensorMap();
			
			for(Algorithm alg : algs){
				for(Vehicle v : vehicles){
					for(Sensor s: sensors){
						ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, s);
						Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
						Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
						Assert.assertEquals(0,i.numberOfZValues());
					}
				}
			}
			
			Algorithm alg = new Algorithm(0);
			Vehicle v =  new Vehicle(0);
			Sensor s = new Sensor(0,0);
			ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(true,i.isCosmoSensor());
			
			alg = new Algorithm(0);
			v =  new Vehicle(1);
			s = new Sensor(0,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(true,i.isCosmoSensor());
			
			alg = new Algorithm(0);
			v =  new Vehicle(0);
			s = new Sensor(0,1);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(true,i.isCosmoSensor());
			
			alg = new Algorithm(0);
			v =  new Vehicle(1);
			s = new Sensor(0,1);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(true,i.isCosmoSensor());
			
			alg = new Algorithm(0);
			v =  new Vehicle(0);
			s = new Sensor(1,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(false,i.isCosmoSensor());
			
			alg = new Algorithm(0);
			v =  new Vehicle(1);
			s = new Sensor(1,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(false,i.isCosmoSensor());
			
			alg = new Algorithm(1);
			v =  new Vehicle(0);
			s = new Sensor(0,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(true,i.isCosmoSensor());
			
			alg = new Algorithm(1);
			v =  new Vehicle(1);
			s = new Sensor(0,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(true,i.isCosmoSensor());
			
			alg = new Algorithm(1);
			v =  new Vehicle(0);
			s = new Sensor(0,1);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(false,i.isCosmoSensor());
			
			alg = new Algorithm(1);
			v =  new Vehicle(1);
			s = new Sensor(0,1);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(false,i.isCosmoSensor());
			
			alg = new Algorithm(1);
			v =  new Vehicle(0);
			s = new Sensor(1,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(false,i.isCosmoSensor());
			
			alg = new Algorithm(1);
			v =  new Vehicle(1);
			s = new Sensor(1,0);
			i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v, s);
			Assert.assertEquals(false,i.isCosmoSensor());
		}
			private static class TestICOSMOTimerCheckRepairs extends TestICOSMOTimer{

		public Vehicle expectedVehicle;
		public TimerEvent expectedTime;
		public FaultDescription expectedFD;
		public TestICOSMOTimerCheckRepairs(Vehicle expectedVehicle, TimerEvent expectedTime,
				FaultDescription expectedFD) {
			super();
			this.expectedVehicle = expectedVehicle;
			this.expectedTime = expectedTime;
			this.expectedFD = expectedFD;
		}

		protected void signalRepairOccured(Algorithm alg, TimerEvent repairTimeEvent, Vehicle v, FaultDescription fd){
			Assert.assertEquals(expectedVehicle,v);
			Assert.assertEquals(expectedTime.getTime(),repairTimeEvent.getTime());
			Assert.assertEquals(true, expectedFD == fd);
		}

	}
			
			private static class TestSensorCollection extends Algorithm implements SensorCollection{

				List<Sensor> selectedSensors;
				
				public TestSensorCollection(int id, List<Sensor> selectedSensors) {
					super(id);
					this.selectedSensors = selectedSensors;
				}

				@Override
				public List<Sensor> getSelectedSensors() {
					// TODO Auto-generated method stub
					return selectedSensors;
				}
				
			}
}
