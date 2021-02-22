package test.phase.analysis.icosmo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.Sensor;
import common.SensorMap;
import common.Vehicle;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.analysis.icosmo.ICOSMO;
import phase.analysis.icosmo.ICOSMOSensorInstance;

public class TestICOSMO extends ICOSMO{

	@Test
	public void testICOSMO_getters() {
		/*
		 * public ICOSMO(double stalnessThreshold, double candicacyThreshold, double contributionDecreaseMod,
			double contributionIncreaseMod, double potentialContrDecreaseMod, double potentialContrIncreaseMod,double desiredRecall,double desiredPrecision,
			double defaultContribution, double defaultPotentialContribution) {
		super();
		 */
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
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,105);

		Assert.assertEquals(10.0, icosmo.getStalnessThreshold());
		Assert.assertEquals(11.0, icosmo.getCandicacyThreshold());
		Assert.assertEquals(1.0,icosmo.getContributionDecreaseMod());
		Assert.assertEquals(2.0,icosmo.getContributionIncreaseMod());
		Assert.assertEquals(3.0,icosmo.getPotentialContrDecreaseMod());
		Assert.assertEquals(4.0,icosmo.getPotentialContrIncreaseMod());
		Assert.assertEquals(0.5,icosmo.getDesiredRecall());
		Assert.assertEquals(0.6,icosmo.getDesiredPrecision());
		Assert.assertEquals(5.0,icosmo.getDefaultContribution());
		Assert.assertEquals(7.0,icosmo.getDefaultPotentialContribution());
		Assert.assertEquals(5,icosmo.getNumFaultInvolvedSensorEstimation());
		Assert.assertEquals(100,icosmo.getMaxNumberAddedSensors());
		Assert.assertEquals(105,icosmo.getMaxNumberRemovedSensors());
	}

	@Test
	public void testICOSMO_getters_illegal_arg() {

		boolean flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=-1;
			double desiredPrecision=0.6;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=5;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=1.5;
			double desiredPrecision=0.6;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=5;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=1.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=5;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=0.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=5;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,-1,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=0.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=5;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,-1);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=-1;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=5;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=0.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=0;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=1.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=-1;
			int zValueWindowSize = 30;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=1.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=-1;
			int zValueWindowSize = 0;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			double stalnessThreshold=10;
			double 	candicacyThreshold=11;
			double contributionDecreaseMod=1;
			double contributionIncreaseMod=2;
			double potentialContrDecreaseMod=3;
			double potentialContrIncreaseMod=4;
			double desiredRecall=0.5;
			double desiredPrecision=1.5;
			double defaultContribution=5;
			double defaultPotentialContribution=7;
			int numFaultInvolvedSensorEstimation=-1;
			int zValueWindowSize = -1;
			ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);


		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
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
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		Assert.assertEquals(true,icosmo.getSensorMap() == null);
		icosmo.init(map);

		Assert.assertEquals(true,icosmo.getSensorMap() != null);

		//make sure all the sensor instances have default contribution and potential contribution
		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s : sensors){
					ICOSMOSensorInstance i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					Assert.assertEquals(7.0, i.getPotentialContribution());
					Assert.assertEquals(5.0, i.getContribution());
					Assert.assertEquals(false, i.isCosmoSensor());
					Assert.assertEquals(false, i.isHasSensorSelectionChanged());
				}
			}
		}

	}

	@Test
	public void testIraRecall_precision_numberEstimatedSensors() {
		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredRecall=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;


		for(int k = 1;k<50;k++){
			int numFaultInvolvedSensorEstimation= k;
			for(int j = 0;j< 100;j++){
				double desiredPresicion=((double)j) * 0.01;


				List<Sensor> faultInvolvedSensors = new ArrayList<Sensor>(100);
				for(int i = 0;i<100;i++){
					faultInvolvedSensors.add(new Sensor(0,i));
				}

				List<Sensor> nonfaultInvolvedSensors = new ArrayList<Sensor>(100);
				for(int i = 0;i<100;i++){
					nonfaultInvolvedSensors.add(new Sensor(1,i));
				}

				TestICOSMO icosmo = new TestICOSMO();
				icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
						potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPresicion, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

				List<Sensor> estiamtedFaultInvolved = icosmo.iraPrecision(faultInvolvedSensors, nonfaultInvolvedSensors);


				Assert.assertEquals(k,estiamtedFaultInvolved.size());

			}
		}

	}


	@Test
	public void testIraRecall_recall_numberEstimatedSensors() {
		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;


		for(int k = 5;k<50;k++){
			int numFaultInvolvedSensorEstimation= k;
			for(int j = 0;j< 100;j++){
				double desiredRecall=((double)j) * 0.01;


				List<Sensor> faultInvolvedSensors = new ArrayList<Sensor>(100);
				for(int i = 0;i<5;i++){
					faultInvolvedSensors.add(new Sensor(0,i));
				}

				List<Sensor> nonfaultInvolvedSensors = new ArrayList<Sensor>(100);
				for(int i = 0;i<100;i++){
					nonfaultInvolvedSensors.add(new Sensor(1,i));
				}

				TestICOSMO icosmo = new TestICOSMO();
				icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
						potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

				List<Sensor> estiamtedFaultInvolved = icosmo.iraRecall(faultInvolvedSensors, nonfaultInvolvedSensors);


				Assert.assertEquals(k,estiamtedFaultInvolved.size());

			}
		}

	}

	@Test
	public void testIraRecall_recall_100() {
		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;


		int numFaultInvolvedSensorEstimation= 10;
		double desiredRecall=1.0;


		List<Sensor> faultInvolvedSensors = new ArrayList<Sensor>(100);
		for(int i = 0;i<100;i++){
			faultInvolvedSensors.add(new Sensor(0,i));
		}

		List<Sensor> nonfaultInvolvedSensors = new ArrayList<Sensor>(100);
		for(int i = 0;i<100;i++){
			nonfaultInvolvedSensors.add(new Sensor(1,i));
		}

		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

		List<Sensor> estiamtedFaultInvolved = icosmo.iraRecall(faultInvolvedSensors, nonfaultInvolvedSensors);

		//they all should have pgn0, indicating fault involved (100%recall)
		for(Sensor s : estiamtedFaultInvolved){
			Assert.assertEquals(0, s.getPgn());
		}

	}

	@Test
	public void testIraRecall_recall_75() {
		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;


		int numFaultInvolvedSensorEstimation= 20;
		double desiredRecall=0.75;


		List<Sensor> faultInvolvedSensors = new ArrayList<Sensor>(100);
		for(int i = 0;i<10;i++){
			faultInvolvedSensors.add(new Sensor(0,i));
		}

		List<Sensor> nonfaultInvolvedSensors = new ArrayList<Sensor>(100);
		for(int i = 0;i<100;i++){
			nonfaultInvolvedSensors.add(new Sensor(1,i));
		}

		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

		List<Sensor> estiamtedFaultInvolved = icosmo.iraRecall(faultInvolvedSensors, nonfaultInvolvedSensors);

		//with 75% recall, there should be 7 faultinvolved sensors
		int count = 0;
		for(Sensor s : estiamtedFaultInvolved){
			if(s.getPgn() == 0){
				count++;
			}
		}
		Assert.assertEquals((int)Math.floor(10.0*0.75),count);

	}

	@Test
	public void testIraRecall_recall_x() {
		/*
		 * note that this test fails when number faultInvolvedSensors is 100
		 */
		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;


		int numFaultInvolvedSensorEstimation= 20;
		for(int k = 0;k<100;k++){


			double desiredRecall=((double)k) * 0.01;


			List<Sensor> faultInvolvedSensors = new ArrayList<Sensor>(100);
			for(int i = 0;i<10;i++){
				faultInvolvedSensors.add(new Sensor(0,i));
			}

			List<Sensor> nonfaultInvolvedSensors = new ArrayList<Sensor>(100);
			for(int i = 0;i<100;i++){
				nonfaultInvolvedSensors.add(new Sensor(1,i));
			}

			TestICOSMO icosmo = new TestICOSMO();
			icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

			List<Sensor> estiamtedFaultInvolved = icosmo.iraRecall(faultInvolvedSensors, nonfaultInvolvedSensors);

			//with 75% recall, there should be 7 faultinvolved sensors
			int count = 0;
			for(Sensor s : estiamtedFaultInvolved){
				if(s.getPgn() == 0){
					count++;
				}
			}
			Assert.assertEquals((int)Math.floor(10.0*desiredRecall),count);
		}

	}

	@Test
	public void testIraRecall_precision_x() {

		/*
		 * note that this test fails when numFaultInvolvedSensorEstimation is 20
		 */
		double stalnessThreshold=10;
		double 	candicacyThreshold=11;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=2;
		double potentialContrDecreaseMod=3;
		double potentialContrIncreaseMod=4;
		double desiredRecall=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=7;


		int numFaultInvolvedSensorEstimation= 100;
		for(int k = 0;k<100;k++){


			double desiredPrecision=((double)k) * 0.01;


			List<Sensor> faultInvolvedSensors = new ArrayList<Sensor>(100);
			for(int i = 0;i<100;i++){
				faultInvolvedSensors.add(new Sensor(0,i));
			}

			List<Sensor> nonfaultInvolvedSensors = new ArrayList<Sensor>(100);
			for(int i = 0;i<100;i++){
				nonfaultInvolvedSensors.add(new Sensor(1,i));
			}

			TestICOSMO icosmo = new TestICOSMO();
			icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
					potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,100,100);

			List<Sensor> estiamtedFaultInvolved = icosmo.iraPrecision(faultInvolvedSensors, nonfaultInvolvedSensors);

			//with 75% recall, there should be 7 faultinvolved sensors
			int count = 0;
			for(Sensor s : estiamtedFaultInvolved){
				if(s.getPgn() == 0){
					count++;
				}
			}
			Assert.assertEquals((int)Math.floor(100.0*desiredPrecision),count);
		}

	}

	@Test
	public void testAdjustSensorRanking_potential_contribution_increase() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		boolean atLeastOneSensorDetectedDeviation = false;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(false);
		boolean sensorDeviationOccured = false;

		//this should increase potential contribution by 1 (all sensors non-cosmo sensors by default), since no deviation occured and this sensor is fault involved 
		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(6.0,faultInvolvedSensor.getPotentialContribution());
		faultInvolvedSensor.setCosmoSensor(false);
		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(7.0,faultInvolvedSensor.getPotentialContribution());

	}

	@Test
	public void testAdjustSensorRanking_potential_contribution_decrease() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		boolean atLeastOneSensorDetectedDeviation = true;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(false);
		boolean sensorDeviationOccured = false;

		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(4.0,faultInvolvedSensor.getPotentialContribution());
		faultInvolvedSensor.setCosmoSensor(false);
		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(3.0,faultInvolvedSensor.getPotentialContribution());

	}

	@Test
	public void testAdjustSensorRanking_contribution_increase() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		boolean atLeastOneSensorDetectedDeviation = true;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(true);
		boolean sensorDeviationOccured = true;

		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(6.0,faultInvolvedSensor.getContribution());
		faultInvolvedSensor.setCosmoSensor(true);
		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(7.0,faultInvolvedSensor.getContribution());

	}

	@Test
	public void testAdjustSensorRanking_contribution_decrease() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		boolean atLeastOneSensorDetectedDeviation = true;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(true);
		boolean sensorDeviationOccured = false;

		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(4.0,faultInvolvedSensor.getContribution());
		faultInvolvedSensor.setCosmoSensor(true);
		icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured);
		Assert.assertEquals(3.0,faultInvolvedSensor.getContribution());

	}
	@Test
	public void testAdjustSensorRanking_change_sensor_selection_1_vehicle_affected_contribution() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		//both vehicles neeed an average of 0 contribution (default is 5, so one vehicle needs to be -5)
		boolean atLeastOneSensorDetectedDeviation = true;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));		
		ICOSMOSensorInstance otherfaultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(true);
		otherfaultInvolvedSensor.setCosmoSensor(true);
		ICOSMOSensorInstance tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		tmp.setCosmoSensor(true);
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		tmp.setCosmoSensor(true);
		boolean sensorDeviationOccured = false;


		for(int i = 0;i<9;i++){//decrease instace contributino to -4
			Assert.assertEquals(false,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));
			Assert.assertEquals(true, faultInvolvedSensor.isCosmoSensor());
			Assert.assertEquals(true, otherfaultInvolvedSensor.isCosmoSensor());
		}

		Assert.assertEquals(false,icosmo.isSensorStale(new Algorithm(0), faultInvolvedSensor));
		Assert.assertEquals(true, faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(true, otherfaultInvolvedSensor.isCosmoSensor());

		//selection change done
		Assert.assertEquals(true,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));

		//remove from cosmo
		Assert.assertEquals(false, faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(false, otherfaultInvolvedSensor.isCosmoSensor());

		//the contribution should be set to the default value now, same as potential contribution
		Assert.assertEquals(defaultContribution,faultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultContribution,otherfaultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,faultInvolvedSensor.getPotentialContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,otherfaultInvolvedSensor.getPotentialContribution(),0.0001);

		//other algorithms not affected
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(true, tmp.isCosmoSensor());
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(true, tmp.isCosmoSensor());
	}

	@Test
	public void testAdjustSensorRanking_change_sensor_selection_2_vehicle_affected_contribution() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		//both vehicles neeed an average of 0 contribution (default is 5, so one vehicle needs to be -5)
		boolean atLeastOneSensorDetectedDeviation = true;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(true);
		ICOSMOSensorInstance otherfaultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		otherfaultInvolvedSensor.setCosmoSensor(true);
		ICOSMOSensorInstance tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		tmp.setCosmoSensor(true);
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		tmp.setCosmoSensor(true);
		boolean sensorDeviationOccured = false;


		for(int i = 0;i<5;i++){//decrease instace contributino to 0
			Assert.assertEquals(false,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));
			Assert.assertEquals(true, faultInvolvedSensor.isCosmoSensor());
			Assert.assertEquals(true, otherfaultInvolvedSensor.isCosmoSensor());
		}


		for(int i = 0;i<4;i++){//decrease instace contributino to 1
			Assert.assertEquals(false,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,otherfaultInvolvedSensor , sensorDeviationOccured));
			Assert.assertEquals(true, faultInvolvedSensor.isCosmoSensor());
			Assert.assertEquals(true, otherfaultInvolvedSensor.isCosmoSensor());
		}


		Assert.assertEquals(true, faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(true, otherfaultInvolvedSensor.isCosmoSensor());

		//removed from cosmo
		Assert.assertEquals(true,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,otherfaultInvolvedSensor , sensorDeviationOccured));
		Assert.assertEquals(false, faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(false, otherfaultInvolvedSensor.isCosmoSensor());


		//the contribution should be set to the default value now, same as potential contribution
		Assert.assertEquals(defaultContribution,faultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultContribution,otherfaultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,faultInvolvedSensor.getPotentialContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,otherfaultInvolvedSensor.getPotentialContribution(),0.0001);

		//other algorithms not affected
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(true, tmp.isCosmoSensor());
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(true, tmp.isCosmoSensor());
	}

	@Test
	public void testAdjustSensorRanking_change_sensor_selection_1_vehicle_affected_potential_contribution() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		//both vehicles neeed an average of 0 contribution (default is 5, so one vehicle needs to be -5)
		boolean atLeastOneSensorDetectedDeviation = false;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(false);
		ICOSMOSensorInstance otherfaultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		otherfaultInvolvedSensor.setCosmoSensor(false);
		ICOSMOSensorInstance tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		tmp.setCosmoSensor(false);
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		tmp.setCosmoSensor(false);
		boolean sensorDeviationOccured = false;


		for(int i = 0;i<9;i++){//decrease instace contributino to -4
			Assert.assertEquals(false,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));
			Assert.assertEquals(false, faultInvolvedSensor.isCosmoSensor());
			Assert.assertEquals(false, otherfaultInvolvedSensor.isCosmoSensor());
		}

		Assert.assertEquals(false, faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(false, otherfaultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(true,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));
		//added to cosmo
		Assert.assertEquals(true, faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(true, otherfaultInvolvedSensor.isCosmoSensor());


		//the contribution should be set to the default value now, same as potential contribution
		Assert.assertEquals(defaultContribution,faultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultContribution,otherfaultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,faultInvolvedSensor.getPotentialContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,otherfaultInvolvedSensor.getPotentialContribution(),0.0001);
		//shouldn't afffect algorithm 1
		//other algorithms not affected
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(false, tmp.isCosmoSensor());
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(false, tmp.isCosmoSensor());
	}

	@Test
	public void testAdjustSensorRanking_change_sensor_selection__2_vehicle_affected_potential_contribution() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		//both vehicles neeed an average of 0 contribution (default is 5, so one vehicle needs to be -5)
		boolean atLeastOneSensorDetectedDeviation = false;	
		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		ICOSMOSensorInstance otherfaultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));
		faultInvolvedSensor.setCosmoSensor(false);
		otherfaultInvolvedSensor.setCosmoSensor(false);
		ICOSMOSensorInstance tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		tmp.setCosmoSensor(false);
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		tmp.setCosmoSensor(false);
		boolean sensorDeviationOccured = false;


		for(int i = 0;i<5;i++){//decrease instace contributino to 0
			Assert.assertEquals(false,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));
			Assert.assertEquals(false,faultInvolvedSensor.isCosmoSensor());
			Assert.assertEquals(false,otherfaultInvolvedSensor.isCosmoSensor());
		}


		for(int i = 0;i<4;i++){//decrease instace contributino to 1
			Assert.assertEquals(false,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,otherfaultInvolvedSensor , sensorDeviationOccured));
			Assert.assertEquals(false,faultInvolvedSensor.isCosmoSensor());
			Assert.assertEquals(false,otherfaultInvolvedSensor.isCosmoSensor());
		}

		Assert.assertEquals(false,faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(false,otherfaultInvolvedSensor.isCosmoSensor());


		Assert.assertEquals(true,icosmo.adjustSensorRanking(new Algorithm(0), atLeastOneSensorDetectedDeviation,faultInvolvedSensor , sensorDeviationOccured));

		Assert.assertEquals(true,faultInvolvedSensor.isCosmoSensor());
		Assert.assertEquals(true,otherfaultInvolvedSensor.isCosmoSensor());

		//the contribution should be set to the default value now, same as potential contribution
		Assert.assertEquals(defaultContribution,faultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultContribution,otherfaultInvolvedSensor.getContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,faultInvolvedSensor.getPotentialContribution(),0.0001);
		Assert.assertEquals(defaultPotentialContribution,otherfaultInvolvedSensor.getPotentialContribution(),0.0001);

		//shouldn't afffect algorithm 1
		//other algorithms not affected
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(0), new Sensor(0,0));
		Assert.assertEquals(false, tmp.isCosmoSensor());
		tmp = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), new Vehicle(1), new Sensor(0,0));
		Assert.assertEquals(false, tmp.isCosmoSensor());


	}

	@Test
	public void testIsSensorStale() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		//both vehicles neeed an average of 0 contribution (default is 5, so one vehicle needs to be -5)

		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		ICOSMOSensorInstance otherfaultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));

		faultInvolvedSensor.setCosmoSensor(true);
		otherfaultInvolvedSensor.setCosmoSensor(true);

		for(int i = -20; i < 20; i++){
			for(int j = -20; j < 20; j++){

				boolean expected =  (((double)(i+j))/2.0) <= stalnessThreshold;
				faultInvolvedSensor.setContribution(i);
				otherfaultInvolvedSensor.setContribution(j);
				Assert.assertEquals(expected,icosmo.isSensorStale(new Algorithm(0), faultInvolvedSensor));
				Assert.assertEquals(expected,icosmo.isSensorStale(new Algorithm(0), otherfaultInvolvedSensor));

				//shouldn't consider the instances of other algorithms
				Assert.assertEquals(false,icosmo.isSensorStale(new Algorithm(1), faultInvolvedSensor));
				Assert.assertEquals(false,icosmo.isSensorStale(new Algorithm(1), otherfaultInvolvedSensor));
				//shouldn't affect any attributes of sensor instance
				Assert.assertEquals((double)i,faultInvolvedSensor.getContribution());
				Assert.assertEquals((double)j,otherfaultInvolvedSensor.getContribution());
				Assert.assertEquals(true,faultInvolvedSensor.isCosmoSensor());
				Assert.assertEquals(true,otherfaultInvolvedSensor.isCosmoSensor());
			}	
		}

	}


	@Test
	public void testIsSensorCandidate() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		//both vehicles neeed an average of 0 contribution (default is 5, so one vehicle needs to be -5)

		ICOSMOSensorInstance faultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(0), new Sensor(0,0));
		ICOSMOSensorInstance otherfaultInvolvedSensor = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), new Vehicle(1), new Sensor(0,0));

		faultInvolvedSensor.setCosmoSensor(false);
		otherfaultInvolvedSensor.setCosmoSensor(false);

		for(int i = -20; i < 20; i++){
			for(int j = -20; j < 20; j++){

				boolean expected = (((double)(i+j))/2.0) >= candicacyThreshold;
				faultInvolvedSensor.setPotentialContribution(i);
				otherfaultInvolvedSensor.setPotentialContribution(j);
				Assert.assertEquals(expected,icosmo.isSensorCandidate(new Algorithm(0), faultInvolvedSensor));
				Assert.assertEquals(expected,icosmo.isSensorCandidate(new Algorithm(0), otherfaultInvolvedSensor));

				Assert.assertEquals(false,icosmo.isSensorCandidate(new Algorithm(1), faultInvolvedSensor));
				Assert.assertEquals(false,icosmo.isSensorCandidate(new Algorithm(1), otherfaultInvolvedSensor));

				//shouldn't affect any attributes of sensor instance
				Assert.assertEquals((double)i,faultInvolvedSensor.getPotentialContribution());
				Assert.assertEquals((double)j,otherfaultInvolvedSensor.getPotentialContribution());
				Assert.assertEquals(false,faultInvolvedSensor.isCosmoSensor());
				Assert.assertEquals(false,otherfaultInvolvedSensor.isCosmoSensor());
			}	
		}

	}

	@Test
	public void testAdd_Remove_COSMOSensor() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		/*
		 * ADD SENSOR TO COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
		}



		/*
		 * REMOVE SENSOR FROM
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}


		/*
		 * ADD 2 SENSORS TO COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
		}

		//2ND sensor
		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
		}

		/*
		 * REMOVE 2 SENSORS FROM COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));

			i.setContribution(9);
			i.setPotentialContribution(9);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));

			i.setContribution(9);
			i.setPotentialContribution(9);
		}



		//2ND sensor

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
		}

		/*
		 * ADD/REMOVE SENSORS FROM COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));

			i.setContribution(9);
			i.setPotentialContribution(9);


		}



		//2ND sensor

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(1), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));


		for(Vehicle v: vehicles){

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));

			i.setContribution(9);
			i.setPotentialContribution(9);

		}


		for(Vehicle v: vehicles){

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}
	}


	@Test
	public void testAdd_Remove_COSMOSensor_already_sensor_selection() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		/*
		 * ADD SENSOR TO COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
		}


		/*
		 * ADD SENSOR TO COSMO
		 */



		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}



		/*
		 * REMOVE SENSOR FROM
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}


		/*
		 * REMOVE SENSOR FROM
		 */


		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}


		/*
		 * ADD 2 SENSORS TO COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
		}

		//2ND sensor
		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
		}


		/*
		 * ADD 2 SENSORS TO COSMO
		 */


		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));


		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
		}

		//2ND sensor

		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));
		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			i.setContribution(9);
			i.setPotentialContribution(9);
		}


		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
		}

		/*
		 * REMOVE 2 SENSORS FROM COSMO
		 */

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));

			i.setContribution(9);
			i.setPotentialContribution(9);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));

			i.setContribution(9);
			i.setPotentialContribution(9);
		}



		//2ND sensor

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
		}


		/*
		 * REMOVE 2 SENSORS FROM COSMO
		 */





		//2ND sensor

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));

			i.setContribution(9);
			i.setPotentialContribution(9);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));

			i.setContribution(9);
			i.setPotentialContribution(9);
		}

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);

			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(1), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_addition_cap() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_addition_cap_multi_alg() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		icosmo.addToCOSMO(new Algorithm(1), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(1), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(1), new Sensor(0,1));

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(0,0));
				Assert.assertEquals(true,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(1,0));
				Assert.assertEquals(true,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(0,1));
				Assert.assertEquals(false,i.isCosmoSensor());
				Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(9.0,i.getContribution(),0.0001);
			}
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_removal_cap() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_removal_cap_multi_alg() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		icosmo.removeFromCOSMO(new Algorithm(1), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(1), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(1), new Sensor(0,1));

		for(Algorithm alg : algs){

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(0,0));
				Assert.assertEquals(false,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(1,0));
				Assert.assertEquals(false,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(alg, v, new Sensor(0,1));
				Assert.assertEquals(true,i.isCosmoSensor());
				Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(9.0,i.getContribution(),0.0001);
			}
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_1() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_2() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_3() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_4() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}
	}


	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_5() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_6() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,1));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_7() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.addToCOSMO(new Algorithm(0), new Sensor(1,0));

		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(1,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,0));
		icosmo.removeFromCOSMO(new Algorithm(0), new Sensor(0,1));


		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
			Assert.assertEquals(false,i.isCosmoSensor());
			Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
		}

		for(Vehicle v: vehicles){
			i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
			Assert.assertEquals(true,i.isCosmoSensor());
			Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
			Assert.assertEquals(9.0,i.getContribution(),0.0001);
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_cap_multi_alg() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,2,2);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		for(Algorithm alg : algs){
			//can only add 2 so, 2 first sensor should be added, not 3rd
			icosmo.removeFromCOSMO(alg, new Sensor(0,0));
			icosmo.removeFromCOSMO(alg, new Sensor(1,0));
			icosmo.addToCOSMO(alg, new Sensor(0,0));
			icosmo.addToCOSMO(alg, new Sensor(1,0));

			icosmo.removeFromCOSMO(alg, new Sensor(1,0));
			icosmo.removeFromCOSMO(alg, new Sensor(0,1));
			icosmo.removeFromCOSMO(alg, new Sensor(0,0));


			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,0));
				Assert.assertEquals(true,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(1,0));
				Assert.assertEquals(false,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}

			for(Vehicle v: vehicles){
				i = (ICOSMOSensorInstance) map.getSensorInstance(new Algorithm(0), v, new Sensor(0,1));
				Assert.assertEquals(false,i.isCosmoSensor());
				Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
				Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
			}
		}
	}

	@Test
	public void testAdd_Remove_COSMOSensor_remove_0_sensor_capacity() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,0,0);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(true);
				}
			}
		}

		Algorithm alg = new Algorithm(0);
		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.removeFromCOSMO(alg, new Sensor(0,0));
		icosmo.removeFromCOSMO(alg, new Sensor(1,0));		
		icosmo.removeFromCOSMO(alg, new Sensor(0,1));

		for(Algorithm alg2 : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg2,v,s);
					Assert.assertEquals(true,i.isCosmoSensor());
					Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
					Assert.assertEquals(9.0,i.getContribution(),0.0001);
				}
		}
		}
	}
	
	@Test
	public void testAdd_Remove_COSMOSensor_add_0_sensor_capacity() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,0,0);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(false);
				}
			}
		}

		Algorithm alg = new Algorithm(0);
		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(alg, new Sensor(0,0));
		icosmo.addToCOSMO(alg, new Sensor(1,0));		
		icosmo.addToCOSMO(alg, new Sensor(0,1));

		for(Algorithm alg2 : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg2,v,s);
					Assert.assertEquals(false,i.isCosmoSensor());
					Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
					Assert.assertEquals(9.0,i.getContribution(),0.0001);
				}
			}
		}
	}
	
	@Test
	public void testAdd_Remove_COSMOSensor_add_remove_sensor_capacity_1() {
		double stalnessThreshold=0;
		double 	candicacyThreshold=10;
		double contributionDecreaseMod=1;
		double contributionIncreaseMod=1;
		double potentialContrDecreaseMod=1;
		double potentialContrIncreaseMod=1;
		double desiredRecall=0.5;
		double desiredPrecision=0.6;
		double defaultContribution=5;
		double defaultPotentialContribution=5;
		int numFaultInvolvedSensorEstimation=5;
		TestICOSMO icosmo = new TestICOSMO();
		icosmo.init(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,30,1,1);

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

		SensorMap map = new SensorMap(algs);
		map.init(vehicles, sensors, icosmo);

		icosmo.init(map);

		ICOSMOSensorInstance i = null;

		for(Algorithm alg : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg,v,s);
					i.setContribution(9);
					i.setPotentialContribution(9);
					i.setCosmoSensor(false);
				}
			}
		}

		Algorithm alg = new Algorithm(0);
		//can only add 2 so, 2 first sensor should be added, not 3rd
		icosmo.addToCOSMO(alg, new Sensor(0,0));
		icosmo.removeFromCOSMO(alg, new Sensor(0,0));
		
		
		for(Algorithm alg2 : algs){
			for(Vehicle v: vehicles){
				for(Sensor s: sensors){
					
					if(alg2.equals(new Algorithm(0)) && s.equals(new Sensor(0,0))){
						i = (ICOSMOSensorInstance) map.getSensorInstance(alg2,v,s);
						Assert.assertEquals(false,i.isCosmoSensor());
						Assert.assertEquals(defaultPotentialContribution,i.getPotentialContribution(),0.0001);
						Assert.assertEquals(defaultContribution,i.getContribution(),0.0001);
					}else{
					i = (ICOSMOSensorInstance) map.getSensorInstance(alg2,v,s);
					Assert.assertEquals(false,i.isCosmoSensor());
					Assert.assertEquals(9.0,i.getPotentialContribution(),0.0001);
					Assert.assertEquals(9.0,i.getContribution(),0.0001);
					}
				}
			}
		}
	}
}
