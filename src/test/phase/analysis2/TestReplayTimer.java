package test.phase.analysis2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.FaultDescription;
import common.Sensor;
import common.SensorCollection;
import common.Vehicle;
import common.event.HistoryEvent;
import common.event.PhaseBeginEvent;
import common.event.ROCCurvePointEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.HistoryInputStream;
import common.event.stream.HistoryOutputStream;
import common.event.stream.HistoryStreamManager;
import common.event.stream.ROCCurvePointInputStream;
import common.event.stream.ROCCurvePointOutputStream;
import common.event.stream.ROCCurvePointStreamManager;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusOutputStream;
import common.event.stream.TimeStampedSensorStatusStreamManager;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.analysis.ReplayTimer;
import phase.analysis.icosmo.ICOSMO;
import phase.analysis.icosmo.ICOSMOTimer;
import phase.analysis.icosmo.ICOSMOTimer.Mode;
import phase.generation.history.FaultHistory;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class TestReplayTimer extends ReplayTimer {

	@Test
	public void testTick2() throws InterruptedException {

		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new TestAlgorithm(0));
		algs.add(new TestAlgorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);

		//ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.01));

		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),0,0,0.01,0.01));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),0,1,0.02,0.02));

		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),1,0,0.05,0.05));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),0,1,0.06,0.06));
		
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),1,0,0.03,0.03));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),0,0,0.04,0.04));

		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),1,0,0.07,0.07));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),0,0,0.08,0.08));

		double [] expectedThresholds = new double[8]; 
		expectedThresholds[0]= 0.01;
		expectedThresholds[1]= 0.02;
		expectedThresholds[2]= 0.03;
		expectedThresholds[3]= 0.04;
		expectedThresholds[4]= 0.05;
		expectedThresholds[5]= 0.06;
		expectedThresholds[6]= 0.07;
		expectedThresholds[7]= 0.08;

		/*
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.02,true,0.12,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.03,false,0.13,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.04,true,0.14,new TimerEvent(1)));

		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.05,false,0.15,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.06,true,0.16,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.07,false,0.17,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.08,true,0.18,new TimerEvent(1)));
		 */


		FaultDescription f1 = new FaultDescription(0,null);
		FaultDescription f2 = new FaultDescription(1,null);
		FaultDescription f3 = new FaultDescription(2,null);

		FaultDescription f4 = new FaultDescription(3,null);
		FaultDescription f5 = new FaultDescription(4,null);
		FaultDescription f6 = new FaultDescription(5,null);

		RepairHistory rh = new RepairHistory(vehicles);


		ICOSMOTimer icosmoTimer = createICOSMOTimer2();
		TestReplayTimer t = new TestReplayTimer();


		HistoryInputStream hIn = new HistoryInputStream();
		HistoryOutputStream hOut = new HistoryOutputStream();
		HistoryStreamManager hManager = new HistoryStreamManager(hIn,hOut);
		
		hOut.writeHistoryEvent(new HistoryEvent(ssh,new FaultHistory(vehicles),rh));
		hManager.flush();
		ROCCurvePointOutputStream rocOut = new ROCCurvePointOutputStream(algs);
		ROCCurvePointInputStream rocIn = new ROCCurvePointInputStream(algs);
		ROCCurvePointStreamManager rocManager = new ROCCurvePointStreamManager(rocIn,rocOut);
		t.initStreams(hIn, rocOut);
		t.init(icosmoTimer);
		t.init(ssh, rh);
		t.phaseStarted(new PhaseBeginEvent());
		
		
		t.tick(new TimerEvent(0));
		

		rocManager.flush();
		Iterator<ROCCurvePointEvent>	it = rocIn.iterator(new Algorithm(0));
	
			double threshold = expectedThresholds[0];
			ROCCurvePointEvent e = it.next();

			Assert.assertEquals(threshold,e.getCosmoDeviationThreshold(),0.00001);

			List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();

			;
			Assert.assertEquals(4,events.size());
			TimeStampedSensorStatusEvent e2 = events.get(0);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.01,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());


			e2 = events.get(1);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.02,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());


			e2 = events.get(2);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.03,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());

			e2 = events.get(3);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.04,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());
			

			t.tick(new TimerEvent(1));
			

			rocManager.flush();
				it = rocIn.iterator(new Algorithm(0));
		
				threshold = expectedThresholds[1];
				 e = it.next();

				Assert.assertEquals(threshold,e.getCosmoDeviationThreshold(),0.00001);

				 events = e.getSensorStatuses();

				;
				Assert.assertEquals(4,events.size());
				 e2 = events.get(0);
				Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
				Assert.assertEquals(0.01,e2.getZvalue(),0.00001);
				Assert.assertEquals(0,e2.getTimerEvent().getTime());


				e2 = events.get(1);
				Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
				Assert.assertEquals(0.02,e2.getZvalue(),0.00001);
				Assert.assertEquals(0,e2.getTimerEvent().getTime());


				e2 = events.get(2);
				Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
				Assert.assertEquals(0.03,e2.getZvalue(),0.00001);
				Assert.assertEquals(1,e2.getTimerEvent().getTime());

				e2 = events.get(3);
				Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
				Assert.assertEquals(0.04,e2.getZvalue(),0.00001);
				Assert.assertEquals(1,e2.getTimerEvent().getTime());

		
	}
	@Test
	public void testTick() throws InterruptedException {

		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new TestAlgorithm(0));
		algs.add(new TestAlgorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);

		//ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.01));

		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),0,0,0.01,0.01));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),0,1,0.02,0.02));

		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),1,0,0.05,0.05));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(0),createSensorStatusEvent(new TestAlgorithm(0),0,1,0.06,0.06));
		
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),1,0,0.03,0.03));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),0,0,0.04,0.04));

		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),1,0,0.07,0.07));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(1),createSensorStatusEvent(new TestAlgorithm(0),0,0,0.08,0.08));

		double [] expectedThresholds = new double[8]; 
		expectedThresholds[0]= 0.01;
		expectedThresholds[1]= 0.02;
		expectedThresholds[2]= 0.03;
		expectedThresholds[3]= 0.04;
		expectedThresholds[4]= 0.05;
		expectedThresholds[5]= 0.06;
		expectedThresholds[6]= 0.07;
		expectedThresholds[7]= 0.08;

		/*
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.02,true,0.12,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.03,false,0.13,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.04,true,0.14,new TimerEvent(1)));

		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.05,false,0.15,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.06,true,0.16,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.07,false,0.17,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.08,true,0.18,new TimerEvent(1)));
		 */


		FaultDescription f1 = new FaultDescription(0,null);
		FaultDescription f2 = new FaultDescription(1,null);
		FaultDescription f3 = new FaultDescription(2,null);

		FaultDescription f4 = new FaultDescription(3,null);
		FaultDescription f5 = new FaultDescription(4,null);
		FaultDescription f6 = new FaultDescription(5,null);

		RepairHistory rh = new RepairHistory(vehicles);


		ICOSMOTimer icosmoTimer = createICOSMOTimer2();
		TestReplayTimer t = new TestReplayTimer();


		HistoryInputStream hIn = new HistoryInputStream();
		HistoryOutputStream hOut = new HistoryOutputStream();
		HistoryStreamManager hManager = new HistoryStreamManager(hIn,hOut);
		
		hOut.writeHistoryEvent(new HistoryEvent(ssh,new FaultHistory(vehicles),rh));
		hManager.flush();
		ROCCurvePointOutputStream rocOut = new ROCCurvePointOutputStream(algs);
		ROCCurvePointInputStream rocIn = new ROCCurvePointInputStream(algs);
		ROCCurvePointStreamManager rocManager = new ROCCurvePointStreamManager(rocIn,rocOut);
		t.initStreams(hIn, rocOut);
		t.init(icosmoTimer);
		t.init(ssh, rh);
		t.phaseStarted(new PhaseBeginEvent());
		
		for(int time = 0; time < expectedThresholds.length;time++){
		t.tick(new TimerEvent(time));
		}

		rocManager.flush();
		Iterator<ROCCurvePointEvent>	it = rocIn.iterator(new Algorithm(0));
		for(double threshold : expectedThresholds){
			
			ROCCurvePointEvent e = it.next();

			Assert.assertEquals(threshold,e.getCosmoDeviationThreshold(),0.00001);

			List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();

			;
			Assert.assertEquals(4,events.size());
			TimeStampedSensorStatusEvent e2 = events.get(0);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.01,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());


			e2 = events.get(1);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.02,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());


			e2 = events.get(2);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.03,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());

			e2 = events.get(3);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(0.04,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());

		}
	it = rocIn.iterator(new Algorithm(1));
		for(double threshold : expectedThresholds){
			
			ROCCurvePointEvent e = it.next();

			Assert.assertEquals(threshold,e.getCosmoDeviationThreshold(),0.00001);
			List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();

			Assert.assertEquals(4,events.size());
			TimeStampedSensorStatusEvent	 e2 = events.get(0);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(0.05,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());


			e2 = events.get(1);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(0.06,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());


			e2 = events.get(2);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(0.07,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());

			e2 = events.get(3);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(0.08,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());
		}
	}

	@Test
	public void testReplayTimer_getters() {
		ICOSMOTimer icosmoTimer = createICOSMOTimer();
		TestReplayTimer t = new TestReplayTimer();
		t.init(icosmoTimer);
		List<Algorithm> algs = t.getAlgorithms();
		Assert.assertEquals(2,algs.size());
		Assert.assertEquals(new Algorithm(0),algs.get(0));
		Assert.assertEquals(new Algorithm(1),algs.get(1));

		List<Vehicle> vehicles = t.getVehicles();
		Assert.assertEquals(2,vehicles.size());
		Assert.assertEquals(new Vehicle(0),vehicles.get(0));
		Assert.assertEquals(new Vehicle(1),vehicles.get(1));

		Assert.assertEquals(true,t.getIcosmoTimer() == icosmoTimer);

		Assert.assertEquals(true,t.getRepairStreamManager() != null);
		Assert.assertEquals(true,t.getTimeStampedSensorStatusStreamManager() != null);

		Assert.assertEquals(true,t.getLocalRepairOutputStream() != null);
		Assert.assertEquals(true,t.getLocalSensorStatusOutputStream() != null);
		Assert.assertEquals(true,t.getLocalTimeStampedSensorStatusInputStream() != null);

	}

	@Test
	public void testReplayTimer_illegal_arg(){

		boolean flag = false;
		try{
			ReplayTimer t = new ReplayTimer(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
	}

	@Test
	public void testInitStreams() {
		ICOSMOTimer icosmoTimer = createICOSMOTimer();
		TestReplayTimer t = new TestReplayTimer();
		t.init(icosmoTimer);

		HistoryInputStream histInputStream = new HistoryInputStream();
		ROCCurvePointOutputStream rocOutputStream = new ROCCurvePointOutputStream(t.getAlgorithms());

		t.initStreams(histInputStream, rocOutputStream);
		Assert.assertEquals(true,t.getHistInputStream() == histInputStream);
		Assert.assertEquals(true,t.getRocOutputStream() == rocOutputStream);
	}

	@Test
	public void testInitStreams_illegal_arg(){
		ICOSMOTimer icosmoTimer = createICOSMOTimer();
		TestReplayTimer t = new TestReplayTimer();
		t.init(icosmoTimer);
		HistoryInputStream histInputStream = new HistoryInputStream();
		ROCCurvePointOutputStream rocOutputStream = new ROCCurvePointOutputStream(t.getAlgorithms());

		boolean flag = false;
		try{

			t.initStreams(null, rocOutputStream);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);

		flag = false;
		try{

			t.initStreams(histInputStream,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);

		flag = false;
		try{

			t.initStreams(null,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
	}

	@Test
	public void testReplayRepairEvents() throws InterruptedException {
		List<Algorithm> algs =  new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		FaultDescription f1 = new FaultDescription(0,null);
		FaultDescription f2 = new FaultDescription(1,null);
		FaultDescription f3 = new FaultDescription(2,null);

		FaultDescription f4 = new FaultDescription(3,null);
		FaultDescription f5 = new FaultDescription(4,null);
		FaultDescription f6 = new FaultDescription(5,null);

		RepairHistory h = new RepairHistory(vehicles);

		h.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f1));
		h.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f2));
		h.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f3));
		h.recordElement(new Vehicle(1),new TimerEvent(0), new RepairEvent(new Vehicle(1),f4));
		h.recordElement(new Vehicle(1),new TimerEvent(0), new RepairEvent(new Vehicle(1),f5));
		h.recordElement(new Vehicle(1),new TimerEvent(0), new RepairEvent(new Vehicle(1),f6));

		h.recordElement(new Vehicle(0),new TimerEvent(1), new RepairEvent(new Vehicle(0),f6));
		h.recordElement(new Vehicle(0),new TimerEvent(1), new RepairEvent(new Vehicle(0),f5));
		h.recordElement(new Vehicle(0),new TimerEvent(1), new RepairEvent(new Vehicle(0),f4));
		h.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(1),f3));
		h.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(1),f2));
		h.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(1),f1));




		SensorStatusHistory ssh = new SensorStatusHistory(algs);

		ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.01));
		ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.02));
		ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.03));
		ssh.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.04));
		ssh.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.05));
		ssh.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.06));

		ssh.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.11));
		ssh.recordElement(new Algorithm(0), new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.12));
		ssh.recordElement(new Algorithm(0), new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.13));
		ssh.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.14));
		ssh.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.15));
		ssh.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.16));


		ICOSMOTimer icosmoTimer = createICOSMOTimer();
		TestReplayTimer t = new TestReplayTimer();
		t.init(icosmoTimer);

		RepairInputStream in = icosmoTimer.getRepairInputStream();
		RepairOutputStream out = new RepairOutputStream(vehicles);

		t.init(ssh, h);

		t.replayRepairEvents(new TimerEvent(0));


		Iterator<RepairEvent> it = in.iterator(new Vehicle(0));
		RepairEvent e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f1);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f2);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f3);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		Assert.assertEquals(false,it.hasNext());

		it = in.iterator(new Vehicle(1));
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f4);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f5);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f6);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		Assert.assertEquals(false,it.hasNext());

		t.replayRepairEvents(new TimerEvent(0));

		it = in.iterator(new Vehicle(0));
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f1);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f2);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f3);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		Assert.assertEquals(false,it.hasNext());

		it = in.iterator(new Vehicle(1));
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f4);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f5);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f6);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		Assert.assertEquals(false,it.hasNext());

		t.replayRepairEvents(new TimerEvent(1));

		it = in.iterator(new Vehicle(0));
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f6);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f5);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f4);
		Assert.assertEquals(new Vehicle(0),e.getVehicle());
		Assert.assertEquals(false,it.hasNext());

		it = in.iterator(new Vehicle(1));
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f3);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f2);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		e = it.next();
		Assert.assertEquals(true,e.getFaultDescription() == f1);
		Assert.assertEquals(new Vehicle(1),e.getVehicle());
		Assert.assertEquals(false,it.hasNext());
	}

	@Test
	public void testReplayTick() throws InterruptedException {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);

		ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.01));
		ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.02));
		ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.03));
		ssh.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(1),0.04));
		ssh.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(1),0.05));
		ssh.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(1),0.06));

		ssh.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent2(new Algorithm(0),0.11));
		ssh.recordElement(new Algorithm(0), new TimerEvent(1), createSensorStatusEvent2(new Algorithm(0),0.12));
		ssh.recordElement(new Algorithm(0), new TimerEvent(1), createSensorStatusEvent2(new Algorithm(0),0.13));
		ssh.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent2(new Algorithm(1),0.14));
		ssh.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent2(new Algorithm(1),0.15));
		ssh.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent2(new Algorithm(1),0.16));





		FaultDescription f1 = new FaultDescription(0,null);
		FaultDescription f2 = new FaultDescription(1,null);
		FaultDescription f3 = new FaultDescription(2,null);

		FaultDescription f4 = new FaultDescription(3,null);
		FaultDescription f5 = new FaultDescription(4,null);
		FaultDescription f6 = new FaultDescription(5,null);

		RepairHistory rh = new RepairHistory(vehicles);

		/*		rh.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f1));
		rh.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f2));
		rh.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f3));
		rh.recordElement(new Vehicle(1),new TimerEvent(0), new RepairEvent(new Vehicle(1),f4));
		rh.recordElement(new Vehicle(1),new TimerEvent(0), new RepairEvent(new Vehicle(1),f5));
		rh.recordElement(new Vehicle(1),new TimerEvent(0), new RepairEvent(new Vehicle(1),f6));

		rh.recordElement(new Vehicle(0),new TimerEvent(1), new RepairEvent(new Vehicle(0),f6));
		rh.recordElement(new Vehicle(0),new TimerEvent(1), new RepairEvent(new Vehicle(0),f5));
		rh.recordElement(new Vehicle(0),new TimerEvent(1), new RepairEvent(new Vehicle(0),f4));
		rh.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(1),f3));
		rh.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(1),f2));
		rh.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(1),f1));
		 */

		ICOSMOTimer icosmoTimer = createICOSMOTimer();
		TestReplayTimer t = new TestReplayTimer();
		t.init(icosmoTimer);

		t.init(ssh, rh);

		TimeStampedSensorStatusInputStream sensorStatusIn = t.getLocalTimeStampedSensorStatusInputStream();
		TimeStampedSensorStatusOutputStream sensorStatusOut = icosmoTimer.getTimeStampedSensorStatusOutputStream();
		TimeStampedSensorStatusStreamManager sesnroStatusManager = new TimeStampedSensorStatusStreamManager(sensorStatusIn,sensorStatusOut);

		//TimeStampedSensorStatusOutputStream sensorStatusOut = icosmoTimer.getTimeStampedSensorStatusOutputStream();
		//TimeStampedSensorStatusStreamManager sesnroStatusManager = new TimeStampedSensorStatusStreamManager(sensorStatusIn,sensorStatusOut);

		t.replayTick(new TimerEvent(0));

		sesnroStatusManager.flush();

		Iterator<	TimeStampedSensorStatusEvent> it = sensorStatusIn.iterator(new Algorithm(0));
		TimeStampedSensorStatusEvent e = it.next();

		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.01,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.02,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.03,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());
		Assert.assertEquals(false,it.hasNext());

		it = sensorStatusIn.iterator(new Algorithm(1));
		e = it.next();

		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.04,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.05,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.06,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());
		Assert.assertEquals(false,it.hasNext());


		t.replayTick(new TimerEvent(0));
		t.replayTick(new TimerEvent(1));

		sesnroStatusManager.flush();
		it = sensorStatusIn.iterator(new Algorithm(0));


		e = it.next();

		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.01,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.02,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.03,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());
		e = it.next();

		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.11,e.getZvalue(),0.00001);
		Assert.assertEquals(1,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.12,e.getZvalue(),0.00001);
		Assert.assertEquals(1,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(0.13,e.getZvalue(),0.00001);
		Assert.assertEquals(1,e.getTimerEvent().getTime());

		Assert.assertEquals(false,it.hasNext());

		it = sensorStatusIn.iterator(new Algorithm(1));
		e = it.next();

		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.04,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.05,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.06,e.getZvalue(),0.00001);
		Assert.assertEquals(0,e.getTimerEvent().getTime());

		e = it.next();

		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.14,e.getZvalue(),0.00001);
		Assert.assertEquals(1,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.15,e.getZvalue(),0.00001);
		Assert.assertEquals(1,e.getTimerEvent().getTime());

		e = it.next();
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(0.16,e.getZvalue(),0.00001);
		Assert.assertEquals(1,e.getTimerEvent().getTime());
		Assert.assertEquals(false,it.hasNext());


	}

	@Test
	public void testReplayHistoryForICOSMO() throws InterruptedException {


		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new TestAlgorithm(0));
		algs.add(new TestAlgorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);

		//ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.01));

		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(0),createSensorStatusEvent2(new TestAlgorithm(0),0.01));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(0),createSensorStatusEvent2(new TestAlgorithm(0),0.02));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(1),createSensorStatusEvent2(new TestAlgorithm(0),0.03));
		ssh.recordElement(new TestAlgorithm(0),new TimerEvent(1),createSensorStatusEvent2(new TestAlgorithm(0),0.04));

		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(0),createSensorStatusEvent2(new TestAlgorithm(0),0.05));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(0),createSensorStatusEvent2(new TestAlgorithm(0),0.06));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(1),createSensorStatusEvent2(new TestAlgorithm(0),0.07));
		ssh.recordElement(new TestAlgorithm(1),new TimerEvent(1),createSensorStatusEvent2(new TestAlgorithm(0),0.08));
		/*
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.02,true,0.12,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.03,false,0.13,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.04,true,0.14,new TimerEvent(1)));

		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.05,false,0.15,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.06,true,0.16,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.07,false,0.17,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.08,true,0.18,new TimerEvent(1)));
		 */


		FaultDescription f1 = new FaultDescription(0,null);
		FaultDescription f2 = new FaultDescription(1,null);
		FaultDescription f3 = new FaultDescription(2,null);

		FaultDescription f4 = new FaultDescription(3,null);
		FaultDescription f5 = new FaultDescription(4,null);
		FaultDescription f6 = new FaultDescription(5,null);

		RepairHistory rh = new RepairHistory(vehicles);


		ICOSMOTimer icosmoTimer = createICOSMOTimer2();
		TestReplayTimer t = new TestReplayTimer();


		HistoryInputStream hIn = new HistoryInputStream();
		HistoryOutputStream hOut = new HistoryOutputStream();
		HistoryStreamManager hManager = new HistoryStreamManager(hIn,hOut);
		
		ROCCurvePointOutputStream rocOut = new ROCCurvePointOutputStream(algs);
		ROCCurvePointInputStream rocIn = new ROCCurvePointInputStream(algs);
		ROCCurvePointStreamManager rocManager = new ROCCurvePointStreamManager(rocIn,rocOut);
		
		t.initStreams(hIn, rocOut);
		t.init(icosmoTimer);
		//t.init(ssh, rh);
		HistoryEvent hEvent = new HistoryEvent(ssh, null, rh);
		hOut.writeHistoryEvent(hEvent);
		hManager.flush();
		
		t.phaseStarted(new PhaseBeginEvent());
		t.replayHistoryForICOSMO(0.15);

		rocManager.flush();

		Iterator<ROCCurvePointEvent> it = rocIn.iterator(new Algorithm(0));
		ROCCurvePointEvent e = it.next();
		Assert.assertEquals(0.15,e.getCosmoDeviationThreshold(),0.00001);

		List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();
		Assert.assertEquals(4,events.size());
		TimeStampedSensorStatusEvent e2 = events.get(0);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(0.01,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());


		e2 = events.get(1);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(0.02,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());


		e2 = events.get(2);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(0.03,e2.getZvalue(),0.00001);
		Assert.assertEquals(1,e2.getTimerEvent().getTime());

		e2 = events.get(3);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(0.04,e2.getZvalue(),0.00001);
		Assert.assertEquals(1,e2.getTimerEvent().getTime());


		it = rocIn.iterator(new Algorithm(1));
		e = it.next();
		Assert.assertEquals(0.15,e.getCosmoDeviationThreshold(),0.00001);

		events = e.getSensorStatuses();
		Assert.assertEquals(4,events.size());
		e2 = events.get(0);
		Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
		Assert.assertEquals(0.05,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());


		e2 = events.get(1);
		Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
		Assert.assertEquals(0.06,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());


		e2 = events.get(2);
		Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
		Assert.assertEquals(0.07,e2.getZvalue(),0.00001);
		Assert.assertEquals(1,e2.getTimerEvent().getTime());

		e2 = events.get(3);
		Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
		Assert.assertEquals(0.08,e2.getZvalue(),0.00001);
		Assert.assertEquals(1,e2.getTimerEvent().getTime());
	}


	protected static ICOSMOTimer createICOSMOTimer(){
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

		ICOSMOTimer  t = new ICOSMOTimer(algs,vehicles,rin,ssin,tsssout,icosmo,mode);
		t.setDeviationThreshold(0.28);

		t.setLeftTimeWindowDeviations(0);
		t.init(sensors);

		return t;
	}


	protected static ICOSMOTimer createICOSMOTimer2(){
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
		ICOSMO icosmo = new ICOSMO(stalnessThreshold, candicacyThreshold, contributionDecreaseMod, contributionIncreaseMod,
				potentialContrDecreaseMod, potentialContrIncreaseMod, desiredRecall, desiredPrecision, defaultContribution, defaultPotentialContribution,numFaultInvolvedSensorEstimation,zValueWindowSize,100,100);

		List<Algorithm> algs =  new ArrayList<Algorithm>(2);
		algs.add(new TestAlgorithm(0));
		algs.add(new TestAlgorithm(1));

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
		t.setDeviationThreshold(0.28);

		t.setLeftTimeWindowDeviations(0);
		t.init(sensors);

		return t;
	}
	private static class TestAlgorithm extends Algorithm implements SensorCollection{

		public TestAlgorithm(int id){
			super(id);
		}

		@Override
		public List<Sensor> getSelectedSensors() {

			List<Sensor> dummyList = new ArrayList<Sensor>(0);
			dummyList.add(new Sensor(0,0));
			// TODO Auto-generated method stub
			return dummyList;
		}

	}
	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,false,0.0);
	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore,double zvalue){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,false,zvalue);
	}
	
	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, double zscore){
		return createSensorStatusEvent(alg,0,0,zscore);
	}


	public static SensorStatusEvent createSensorStatusEvent2(Algorithm alg, double zvalue){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(0,0),alg,0.0,false,zvalue);
	}
	
	public static SensorStatusEvent createSensorStatusEvent2(Algorithm alg, double zvalue,Sensor s){
		return new SensorStatusEvent(new Vehicle(0), s,alg,0.0,false,zvalue);
	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn){
		return createSensorStatusEvent(alg,pgn,spn,0);
	}
}
