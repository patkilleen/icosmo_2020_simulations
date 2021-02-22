package test.phase.generation.history;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.Vehicle;
import common.event.FaultEvent;
import common.event.HistoryEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedPhaseCompleteEvent;
import common.event.TimerEvent;
import common.event.stream.FaultInputStream;
import common.event.stream.FaultOutputStream;
import common.event.stream.FaultStreamManager;
import common.event.stream.HistoryInputStream;
import common.event.stream.HistoryOutputStream;
import common.event.stream.HistoryStreamManager;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.RepairStreamManager;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.history.FaultHistory;
import phase.generation.history.HistoryTimer;
import phase.generation.history.SensorStatusHistory;

public class TestHistoryTimer extends HistoryTimer{

	@Test
	public void test_constructor_illegal_arg_null() {
		boolean flag = false;

		try{
			HistoryTimer t = new HistoryTimer(null,null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;


		try{
			List<Algorithm> arg = new ArrayList<Algorithm>(1);
			arg.add(new Algorithm(0));
			HistoryTimer t = new HistoryTimer(arg,null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			List<Vehicle> arg = new ArrayList<Vehicle>(1);
			arg.add(new Vehicle(0));
			HistoryTimer t = new HistoryTimer(null,arg);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_empty() {
		boolean flag = false;
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		vehicles.add(new Vehicle(0));

		try{
			HistoryTimer t = new HistoryTimer(new ArrayList<Algorithm>(1),vehicles);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
		 flag = false;
		try{
			HistoryTimer t = new HistoryTimer(algs,new ArrayList<Vehicle>(1));
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
		
		 flag = false;
			try{
				HistoryTimer t = new HistoryTimer(new ArrayList<Algorithm>(1),new ArrayList<Vehicle>(1));
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
	}
	
	@Test
	public void test_constructor() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		vehicles.add(new Vehicle(0));
		
		HistoryTimer t = new HistoryTimer(algs,vehicles);
	}
	
	@Test
	public void test_stream_getters_null() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		vehicles.add(new Vehicle(0));
		
		HistoryTimer t = new HistoryTimer(algs,vehicles);
		
		Assert.assertEquals(null,t.getFaultInputStream());
		Assert.assertEquals(null,t.getHistOutputStream());
		Assert.assertEquals(null,t.getRepairInputStream());
		Assert.assertEquals(null,t.getSensorStatusInputStream());
		

	}
	

	@Test
	public void test_stream_getters_setters() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		vehicles.add(new Vehicle(0));
		
		HistoryTimer t = new HistoryTimer(algs,vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.setFaultInputStream(fis);
		t.setHistOutputStream(hos);
		t.setRepairInputStream(ris);
		t.setSensorStatusInputStream(ssis);
		
		Assert.assertEquals(true ,fis == t.getFaultInputStream());
		Assert.assertEquals(true ,hos==t.getHistOutputStream());
		Assert.assertEquals(true,ris==t.getRepairInputStream());
		Assert.assertEquals(true, ssis==t.getSensorStatusInputStream());
	}
	

	@Test
	public void test_initStreams() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		vehicles.add(new Vehicle(0));
		
		HistoryTimer t = new HistoryTimer(algs,vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		Assert.assertEquals(true ,fis == t.getFaultInputStream());
		Assert.assertEquals(true ,hos==t.getHistOutputStream());
		Assert.assertEquals(true,ris==t.getRepairInputStream());
		Assert.assertEquals(true, ssis==t.getSensorStatusInputStream());
	}
	


	@Test
	public void test_initStreams_illegal_arg_null() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		vehicles.add(new Vehicle(0));
		
		HistoryTimer t = new HistoryTimer(algs,vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		boolean flag = false;


			try{
				t.initStreams(null, fis, ris, hos);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);

			flag = false;


			try{
				t.initStreams(ssis,null, ris, hos);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
		
			flag = false;


			try{
				t.initStreams(ssis, fis, null, hos);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			flag = false;


			try{
				t.initStreams(ssis, fis, ris,null);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
	}
	
	@Test
	public void test_tick_fault_hist_1_fault() throws InterruptedException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
/*
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(10),f1);
		
		Emulating the above code but with tick
	*/	
		
		//get to time tick 5
		t.tick(new TimerEvent(0));
		faultMan.flush();
		t.tick(new TimerEvent(1));
		faultMan.flush();
		t.tick(new TimerEvent(2));
		faultMan.flush();
		t.tick(new TimerEvent(3));
		faultMan.flush();
		t.tick(new TimerEvent(4));
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		t.tick(new TimerEvent(5));
		faultMan.flush();
		t.tick(new TimerEvent(6));
		faultMan.flush();
		t.tick(new TimerEvent(7));
		faultMan.flush();
		t.tick(new TimerEvent(8));
		faultMan.flush();
		t.tick(new TimerEvent(9));
		
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		repairMan.flush();
		
			t.tick(new TimerEvent(10));
	
			faultMan.flush();
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(10));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		FaultHistory h = hevent.getFaultHistory();
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 4,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,10,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 14,3,2));
		
	}
	
	@Test
	public void test_tick_fault_hist_2_faults() throws InterruptedException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
/*
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(10),f1);
		
		Emulating the above code but with tick
	*/	
		
		//get to time tick 5
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(0));
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(3));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(4));
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(5));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(6));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(7));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(8));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(9));
		
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		repairMan.flush();
		faultMan.flush();
			t.tick(new TimerEvent(10));
	
			faultMan.flush();
			repairMan.flush();
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(10));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		FaultHistory h = hevent.getFaultHistory();
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 4,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,3,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,10,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 14,3,2));
		
	}

	
	@Test
	public void test_tick_fault_hist_2_faults_multiple_sensors() throws InterruptedException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
/*
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(10),f1);
		
		Emulating the above code but with tick
	*/	
		
		//get to time tick 5
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(0));
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(3));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(4));
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(5));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(6));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(7));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(8));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(9));
		
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		repairMan.flush();
		faultMan.flush();
			t.tick(new TimerEvent(10));
	
			faultMan.flush();
			repairMan.flush();
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(10));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		FaultHistory h = hevent.getFaultHistory();
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 4,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,3,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,10,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 14,3,2));
		
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 4,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,1,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,3,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,10,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 14,3,2));
		
	}

	
	@Test
	public void test_tick_fault_hist_2_different_faults_multiple_sensors() throws InterruptedException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
/*
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(10),f1);
		
		Emulating the above code but with tick
	*/	
		
		//get to time tick 5
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(0));
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(3));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(4));
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(5));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(6));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(7));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(8));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(9));
		
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f2));
		repairMan.flush();
		faultMan.flush();
			t.tick(new TimerEvent(10));
	
			faultMan.flush();
			repairMan.flush();
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(10));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		FaultHistory h = hevent.getFaultHistory();
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 4,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,3,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,10,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,1000));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1000,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,3));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,5));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,10));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,5));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 8,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 9,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 14,3,2));
		
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 4,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,1,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,10,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 14,3,2));
		
	}
	@Test
	public void test_tick_fault_hist_2_different_faults_multiple_sensors_multiple_vehicle() throws InterruptedException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
/*
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(10),f1);
		
		Emulating the above code but with tick
	*/	
		
		//get to time tick 5
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(0));
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(1));
		testFos.write(new Vehicle(1), new FaultEvent(new Vehicle(1),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(3));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(4));
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(5));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(6));
		testFos.write(new Vehicle(0), new FaultEvent(new Vehicle(0),f1));
		testRos.write(new Vehicle(1), new RepairEvent(new Vehicle(1),f1));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(7));
		testFos.write(new Vehicle(1), new FaultEvent(new Vehicle(1),f2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(8));
		testRos.write(new Vehicle(1), new RepairEvent(new Vehicle(1),f2));
		faultMan.flush();
		repairMan.flush();
		t.tick(new TimerEvent(9));
		
		testRos.write(new Vehicle(0), new RepairEvent(new Vehicle(0),f2));
		repairMan.flush();
		faultMan.flush();
			t.tick(new TimerEvent(10));
	
			faultMan.flush();
			repairMan.flush();
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(10));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		FaultHistory h = hevent.getFaultHistory();
		
		/*
		 * vehicle 0, fault 1
		 */
		List<Sensor> testSensors = new ArrayList<Sensor>(2);
		testSensors.add(new Sensor(0,0));
		testSensors.add(new Sensor(0,1));
		for(Sensor s : testSensors){
		Vehicle v = new Vehicle(0);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 0,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 4,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,1,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,3,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,10,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,1000));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,1,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,1000,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,10));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 6,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 14,3,2));
		}
		
		/*
		 * vehicle 1, fault 1
		 */

		for(Sensor s : testSensors){
		Vehicle v = new Vehicle(1);
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 0,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 1,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 3,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 4,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,1,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,3,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,10,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 7,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 8,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 9,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 11,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 12,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 12,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 14,3,2));
		
		}
		/*
		 * vehicle 0, fault 2
		 */
		testSensors = new ArrayList<Sensor>(2);
		testSensors.add(new Sensor(1,0));
		testSensors.add(new Sensor(1,1));
		
		for(Sensor s : testSensors){
		Vehicle v = new Vehicle(0);
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 0,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 4,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,1,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,10,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,1000,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,4));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,1000));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,1,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,2,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,10,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,1000,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 7,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 9,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 11,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 12,1,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 12,2,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 14,3,2));
		}
		
		/*
		 * vehicle 1, fault 2
		 */

		for(Sensor s : testSensors){
		Vehicle v = new Vehicle(1);
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 0,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 1,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 3,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 4,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,1,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,10,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,1000,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,2,3));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,2,4));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 2,2,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,10));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 2,2,1000));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,1,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,2,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,10,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,1000,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,0,1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 5,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,3));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 5,0,10));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 6,0,0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 6,0,1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 6,0,5));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 7,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 8,0,2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(v,s, 9,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 10,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 11,0,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 11,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 12,1,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 12,2,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 13,3,2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(v,s, 14,3,2));
		}
	
		
	}
	

	@Test
	public void test_tick_zscores_unique() throws InterruptedException {
		
		
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);


		/*
		 * time tick 0
		 */
		Algorithm alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1002));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1003));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1004));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1002));
		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2002));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2003));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2004));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2002));
		ssMan.flush();
		t.tick(new TimerEvent(0));

		/*
		 * time tick 1
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1011));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1012));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1013));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1014));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2011));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2012));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2013));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2014));
		ssMan.flush();
		t.tick(new TimerEvent(1));
		

		/*
		 * time tick 2
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1021));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1022));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1023));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1024));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1025));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1025));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1026));
		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2021));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2022));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2023));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2024));
		ssMan.flush();
		t.tick(new TimerEvent(2));
		
		
		/*
		 * time tick 3
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1031));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2031));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2032));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2033));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2034));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2035));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2036));
		ssMan.flush();
		t.tick(new TimerEvent(3));
		
		/*
		 * time tick 4
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1041));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2041));

		ssMan.flush();
		t.tick(new TimerEvent(4));
		
		/*
		 * end outputing to timerhistory
		 */
		
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(4));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		SensorStatusHistory h = hevent.getSensorStatusHistory();
		

		/*
		 * checking unique zscores
		 */
		List<Double> actual = h.getUniqueZScores();
		
		Assert.assertEquals(35, actual.size());
		
		Assert.assertEquals(true,actual.contains(0.1001));
		Assert.assertEquals(true,actual.contains(0.1002));
		Assert.assertEquals(true,actual.contains(0.1003));
		Assert.assertEquals(true,actual.contains(0.1004));
		
		Assert.assertEquals(true,actual.contains(0.2001));
		Assert.assertEquals(true,actual.contains(0.2002));
		Assert.assertEquals(true,actual.contains(0.2003));
		Assert.assertEquals(true,actual.contains(0.2004));
		
		Assert.assertEquals(true,actual.contains(0.1011));
		Assert.assertEquals(true,actual.contains(0.1012));
		Assert.assertEquals(true,actual.contains(0.1013));
		Assert.assertEquals(true,actual.contains(0.1014));
		
		Assert.assertEquals(true,actual.contains(0.2011));
		Assert.assertEquals(true,actual.contains(0.2012));
		Assert.assertEquals(true,actual.contains(0.2013));
		Assert.assertEquals(true,actual.contains(0.2014));
		
		Assert.assertEquals(true,actual.contains(0.1021));
		Assert.assertEquals(true,actual.contains(0.1022));
		Assert.assertEquals(true,actual.contains(0.1023));
		Assert.assertEquals(true,actual.contains(0.1024));
		Assert.assertEquals(true,actual.contains(0.1025));
		Assert.assertEquals(true,actual.contains(0.1026));
		
		Assert.assertEquals(true,actual.contains(0.2021));
		Assert.assertEquals(true,actual.contains(0.2022));
		Assert.assertEquals(true,actual.contains(0.2023));
		Assert.assertEquals(true,actual.contains(0.2024));
		
		Assert.assertEquals(true,actual.contains(0.1031));
		
		Assert.assertEquals(true,actual.contains(0.2031));
		Assert.assertEquals(true,actual.contains(0.2032));
		Assert.assertEquals(true,actual.contains(0.2033));
		Assert.assertEquals(true,actual.contains(0.2034));
		Assert.assertEquals(true,actual.contains(0.2035));
		Assert.assertEquals(true,actual.contains(0.2036));
		
		Assert.assertEquals(true,actual.contains(0.1041));
		Assert.assertEquals(true,actual.contains(0.2041));
	}
	
	
	@Test
	public void test_tick_zscore_unique_sensors() throws InterruptedException {
		
		
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);


		/*
		 * time tick 0
		 */
		Algorithm alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0,0,0.1001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1002));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1003));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1004));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1002));
		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,1,0,0.2001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2002));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2003));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2004));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2002));
		ssMan.flush();
		t.tick(new TimerEvent(0));

		/*
		 * time tick 1
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1011));
		testSsos.write(alg, createSensorStatusEvent(alg,0,1,0.1012));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1013));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1014));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2011));
		testSsos.write(alg, createSensorStatusEvent(alg,1,1,0.2012));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2013));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2014));
		ssMan.flush();
		t.tick(new TimerEvent(1));
		

		/*
		 * time tick 2
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1021));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1022));
		testSsos.write(alg, createSensorStatusEvent(alg,0,1,0.1023));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1024));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1025));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1025));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1026));
		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2021));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2022));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2023));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2024));
		ssMan.flush();
		t.tick(new TimerEvent(2));
		
		
		/*
		 * time tick 3
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1031));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2031));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2032));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2033));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2034));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2035));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2036));
		ssMan.flush();
		t.tick(new TimerEvent(3));
		
		/*
		 * time tick 4
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1041));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2041));

		ssMan.flush();
		t.tick(new TimerEvent(4));
		
		/*
		 * end outputing to timerhistory
		 */
		
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(4));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		SensorStatusHistory h = hevent.getSensorStatusHistory();
		

		/*
		 * checking unique zscores
		 */
		List<Sensor> actual = h.getUniqueSensors();
		
		Assert.assertEquals(4, actual.size());
		Assert.assertEquals(true,actual.contains(new Sensor(0,0)));
		Assert.assertEquals(true,actual.contains(new Sensor(1,0)));
		Assert.assertEquals(true,actual.contains(new Sensor(0,1)));
		Assert.assertEquals(true,actual.contains(new Sensor(1,1)));
		
	}
	

	
	@Test
	public void test_tick_zscore_check_time_zscores() throws InterruptedException {
		

		
		List<Vehicle> vehicles = new ArrayList<Vehicle>(1);
		List<Algorithm> algs = new ArrayList<Algorithm>(1);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		
		TestHistoryTimer t = new TestHistoryTimer();
		t.setParitionKeys(algs);
		t.initHistory(algs, vehicles);
		
		FaultInputStream fis = new FaultInputStream(vehicles);
		HistoryOutputStream hos = new HistoryOutputStream();
		RepairInputStream ris =  new RepairInputStream(vehicles);
		SensorStatusInputStream ssis = new SensorStatusInputStream(algs);
		
		t.initStreams(ssis, fis, ris, hos);
		
		//get hook to theses streams so can read/write
		FaultOutputStream testFos = new FaultOutputStream(vehicles);
		HistoryInputStream testHis = new HistoryInputStream();
		RepairOutputStream testRos =  new RepairOutputStream(vehicles);
		SensorStatusOutputStream testSsos = new SensorStatusOutputStream(algs);
		
		FaultStreamManager faultMan = new FaultStreamManager(fis,testFos);
		HistoryStreamManager histMan = new HistoryStreamManager(testHis,hos);
		RepairStreamManager repairMan = new RepairStreamManager(ris,testRos);
		SensorStatusStreamManager ssMan = new SensorStatusStreamManager(ssis,testSsos);


		/*
		 * time tick 0
		 */
		Algorithm alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1002));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1003));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1004));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1002));
		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2001));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2002));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2003));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2004));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2002));
		ssMan.flush();
		t.tick(new TimerEvent(0));

		/*
		 * time tick 1
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1011));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1012));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1013));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1014));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2011));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2012));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2013));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2014));
		ssMan.flush();
		t.tick(new TimerEvent(1));
		

		/*
		 * time tick 2
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1021));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1022));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1023));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1024));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1025));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1025));
		testSsos.write(alg, createSensorStatusEvent(alg,0.1026));
		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2021));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2022));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2023));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2024));
		ssMan.flush();
		t.tick(new TimerEvent(2));
		
		
		/*
		 * time tick 3
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1031));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2031));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2032));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2033));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2034));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2035));
		testSsos.write(alg, createSensorStatusEvent(alg,0.2036));
		ssMan.flush();
		t.tick(new TimerEvent(3));
		
		/*
		 * time tick 4
		 */
		alg = new Algorithm(0);
		testSsos.write(alg, createSensorStatusEvent(alg,0.1041));

		
		alg = new Algorithm(1);
		testSsos.write(alg, createSensorStatusEvent(alg,0.2041));

		ssMan.flush();
		t.tick(new TimerEvent(4));
		
		/*
		 * end outputing to timerhistory
		 */
		
		t.phaseEnded(new TimeStampedPhaseCompleteEvent(4));
		
		histMan.flush();
		
		HistoryEvent hevent = testHis.readHistoryEvent();
		
		SensorStatusHistory h = hevent.getSensorStatusHistory();
		
		alg = new Algorithm(0);
		Iterator<SensorStatusEvent> it = h.elementIterator(alg, new TimerEvent(0));
		Assert.assertEquals(0.1001,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1002,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1003,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1004,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1001,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1002,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		alg = new Algorithm(1);
		it = h.elementIterator(alg, new TimerEvent(0));
		Assert.assertEquals(0.2001,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2002,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2003,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2004,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2002,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		
		alg = new Algorithm(0);
		it = h.elementIterator(alg, new TimerEvent(1));
		Assert.assertEquals(0.1011,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1012,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1013,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1014,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		alg = new Algorithm(1);
		it = h.elementIterator(alg, new TimerEvent(1));
		Assert.assertEquals(0.2011,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2012,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2013,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2014,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		
		alg = new Algorithm(0);
		it = h.elementIterator(alg, new TimerEvent(2));
		Assert.assertEquals(0.1021,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1022,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1023,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1024,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1025,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1025,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.1026,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		alg = new Algorithm(1);
		it = h.elementIterator(alg, new TimerEvent(2));
		Assert.assertEquals(0.2021,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2022,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2023,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2024,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		
		alg = new Algorithm(0);
		it = h.elementIterator(alg, new TimerEvent(3));
		Assert.assertEquals(0.1031,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		alg = new Algorithm(1);
		it = h.elementIterator(alg, new TimerEvent(3));
		Assert.assertEquals(0.2031,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2032,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2033,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2034,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2035,it.next().getZscore(),0.00001);
		Assert.assertEquals(0.2036,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		
		alg = new Algorithm(0);
		it = h.elementIterator(alg, new TimerEvent(4));
		Assert.assertEquals(0.1041,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
		alg = new Algorithm(1);
		it = h.elementIterator(alg, new TimerEvent(4));
		Assert.assertEquals(0.2041,it.next().getZscore(),0.00001);
		Assert.assertEquals(false,it.hasNext());
	}
	

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,false,0.0);
	}
	
	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, double zscore){
		return createSensorStatusEvent(alg,0,0,zscore);
	}
	
	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn){
		return createSensorStatusEvent(alg,pgn,spn,0);
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
