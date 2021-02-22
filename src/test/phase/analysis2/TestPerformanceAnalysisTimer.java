package test.phase.analysis2;

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
import common.event.HistoryEvent;
import common.event.PerformanceMetricEvent;
import common.event.PhaseBeginEvent;
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
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.analysis.PerformanceAnalysisTimer;
import phase.generation.history.FaultHistory;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class TestPerformanceAnalysisTimer extends PerformanceAnalysisTimer {

	@Test
	public void testTick() throws InterruptedException {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);
		
		
		//ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.01));
				for(Algorithm alg: algs){
					int time = -1;
					for(int i = 0; i < 100; i ++){
						double zscore = 0.01 * (double)i;

						if((i%2)== 0){
							time++;
						}

						SensorStatusEvent e = new SensorStatusEvent(new Vehicle(i%2), new Sensor(0,0),alg,zscore,true,0.0);

						ssh.recordElement(alg,new TimerEvent(time),e);	
					}
				}
				
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

		 List<Algorithm>algs2 = new ArrayList<Algorithm>(4);
			algs2.add(new Algorithm(0));
			algs2.add(new Algorithm(1));
			algs2.add(new Algorithm(0).toICOSMO());
			algs2.add(new Algorithm(1).toICOSMO());
			
		PerformanceMetricOutputStream performanceMetricOutputStream = new PerformanceMetricOutputStream(algs2);
		PerformanceMetricInputStream performanceMetricInputStream = new PerformanceMetricInputStream(algs2);
		PerformanceMetricStreamManager performanceMetricManager = new PerformanceMetricStreamManager(performanceMetricInputStream,performanceMetricOutputStream);


		hOut.writeHistoryEvent(new HistoryEvent(ssh,fh,rh));
		hManager.flush();

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(0, 0);
		t.setParitionKeys(algs);
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

			for(int i = 0; i < 100; i ++){
				double zscore = 0.01 * (double)i;

				ROCCurvePointEvent res = new ROCCurvePointEvent(zscore,events);
				rocOut.write(alg, res);
			}
		}

		rocManager.flush();
		t.phaseStarted(new PhaseBeginEvent());
		t.tick(new TimerEvent(0));
		
		performanceMetricManager.flush();
		
		
		for(int i = 0; i< 1; i++){
			
			
		for(Algorithm alg : algs2){
			
			
			Iterator<PerformanceMetricEvent> it = performanceMetricInputStream.iterator(alg);

			//the first metric should be threshold 0
			PerformanceMetricEvent e = it.next();

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


			e = e = it.next();

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


			e = e = it.next();

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

		}
		}
	}

	@Test
	public void testPerformanceAnalysisTimer_getters() {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		int leftTimeWindow = 1;
		int rightTimeWindow = 2;

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(leftTimeWindow, rightTimeWindow);
		t.setParitionKeys(algs);

		Assert.assertEquals(1, t.getLeftTimeWindow());
		Assert.assertEquals(2, t.getRightTimeWindow());

	}

	@Test
	public void testPerformanceAnalysisTimer_illegal_arg() {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		boolean flag = true;

		try{
			int leftTimeWindow = -1;
			int rightTimeWindow = 2;

			TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
			t.init(leftTimeWindow, rightTimeWindow);
			t.setParitionKeys(algs);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);

		flag = true;

		try{
			int leftTimeWindow = 1;
			int rightTimeWindow = -1;

			TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
			t.init(leftTimeWindow, rightTimeWindow);
			t.setParitionKeys(algs);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);

		flag = true;

		try{
			int leftTimeWindow = 1;
			int rightTimeWindow = -1;

			TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
			t.init(leftTimeWindow, rightTimeWindow);
			t.setParitionKeys(algs);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);

	}
	@Test
	public void testInitStreams() {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		int leftTimeWindow = 1;
		int rightTimeWindow = 2;

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(leftTimeWindow, rightTimeWindow);
		t.setParitionKeys(algs);

		ROCCurvePointInputStream rocInputStream = new ROCCurvePointInputStream(algs);
		PerformanceMetricOutputStream performanceMetricOutputStream = new PerformanceMetricOutputStream(algs);
		HistoryInputStream hIn = new HistoryInputStream();
		t.initStreams(rocInputStream, performanceMetricOutputStream,hIn);

		Assert.assertEquals(true, rocInputStream == t.getRocInputStream());
		Assert.assertEquals(true, performanceMetricOutputStream == t.getPerformanceMetricOutputStream());
		Assert.assertEquals(true, hIn == t.getHistoryInputStream());
	}


	@Test
	public void testInitStreams_illegal_args() {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		int leftTimeWindow = 1;
		int rightTimeWindow = 2;

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(leftTimeWindow, rightTimeWindow);
		t.setParitionKeys(algs);

		ROCCurvePointInputStream rocInputStream = new ROCCurvePointInputStream(algs);
		PerformanceMetricOutputStream performanceMetricOutputStream = new PerformanceMetricOutputStream(algs);
		HistoryInputStream hIn = new HistoryInputStream();

		boolean flag = true;

		try{
			t.initStreams(rocInputStream, null,hIn);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);


		flag = true;

		try{
			t.initStreams(null, performanceMetricOutputStream,hIn);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);


		flag = true;

		try{
			t.initStreams(null, null,hIn);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);

		flag = true;

		try{
			t.initStreams(null, null,null);

		}catch(ConfigurationException e){
			flag = true;
		}


		Assert.assertEquals(true,flag);
	}
	@Test
	public void testCreatePerformanceMetricEvent() {
		int fp = 20;
		int tp = 40;
		int tn = 50;
		int fn = 60;
		PerformanceMetricEvent e = PerformanceAnalysisTimer.createPerformanceMetricEvent(new Algorithm(0), tp, tn, fp, fn, 0.15);
		Assert.assertEquals(90.0/170.0, e.getAccuracy(),0.0001);
		Assert.assertEquals(0.5, e.getFscore(),0.0001);
		Assert.assertEquals(0.15, e.getVaryingThreshold(),0.0001);
		Assert.assertEquals(0.285714, e.getFalsePositiveRate(),0.0001);
		Assert.assertEquals(0.4, e.getTruePositiveRate(),0.0001);
		Assert.assertEquals(60, e.getFalseNegativeCount());
		Assert.assertEquals(20, e.getFalsePositiveCount());
		Assert.assertEquals(50, e.getTrueNegativeCount());
		Assert.assertEquals(40, e.getTruePositiveCount());
	}

	@Test
	public void testComputeFalsePositiveRate() {
		int fp = 20;
		int tp = 20;
		int tn = 40;
		int fn = 40;

		Assert.assertEquals(0.33333333, PerformanceAnalysisTimer.computeFalsePositiveRate(fp, tp, fn, tn),0.0001);
	}

	@Test
	public void testComputeTruePositiveRate() {
		int fp = 20;
		int tp = 40;
		int tn = 40;
		int fn = 60;

		Assert.assertEquals(0.4, PerformanceAnalysisTimer.computeTruePositiveRate(fp, tp, fn, tn),0.0001);
	}

	@Test
	public void testComputeAccuracy() {
		int fp = 20;
		int tp = 40;
		int tn = 50;
		int fn = 60;
		//expecte (40 + 50) / (20 + 40 + 50 + 60) = 90 / 170 =
		Assert.assertEquals(90.0/170.0, PerformanceAnalysisTimer.computeAccuracy(fp, tp, fn, tn),0.0001);
	}

	@Test
	public void testComputeFScore() {
		//2.0 / ((1/precision)+(1/recall));
		//recall 0.4
		//precission 0.666
		int fp = 20;
		int tp = 40;
		int tn = 50;
		int fn = 60;
		//expecte  2 / ( 1 / 0.666) + (1 / 0.4) = 2 / (1.5) + 2.5 =  2/ 4 = 1/2 = 0.5
		Assert.assertEquals(0.5, PerformanceAnalysisTimer.computeFScore(fp, tp, fn, tn),0.0001);
	}

	@Test
	public void testComputeRecall() {
		int fp = 20;
		int tp = 40;
		int tn = 50;
		int fn = 60;
		//expecte (40) / (40 + 60) = 40 / 100 = 0.4
		Assert.assertEquals(0.4, PerformanceAnalysisTimer.computeRecall(fp, tp, fn, tn),0.0001);
	}

	@Test
	public void testComputePrecision() {
		int fp = 20;
		int tp = 40;
		int tn = 50;
		int fn = 60;
		//expecte (40) / (40 + 20) = 40 / 60 = 0.666
		Assert.assertEquals(0.666666, PerformanceAnalysisTimer.computePrecision(fp, tp, fn, tn),0.0001);
	}

	@Test
	public void testComputeCOSMORocCurvePoints1() throws InterruptedException {
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

		TestPerformanceAnalysisTimer t = createPerformanceTimerWithPopulatedHistory(rh,fh);

		for(Algorithm alg : algs){
			List<PerformanceMetricEvent> events = t.computeCOSMORocCurvePoints(alg);
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
			Assert.assertEquals(0.99,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 6
			//fp 44
			//tn 0
			//fn 0

			//vehicle 1
			//tp 5
			//fp 45
			//tn 0
			//fn 0

			expectedTP = 6+5;
			expectedFP = 44+45;
			expectedTN = 0;//all days after 5th, ie 6-50
			expectedFN = 0;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());
		}
	}
	@Test
	public void testComputeCOSMORocCurvePoints2() throws InterruptedException {
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

		int leftTimeWindow=0;
		int rightTimeWindow=1;
		TestPerformanceAnalysisTimer t = createPerformanceTimerWithPopulatedHistory(rh,fh,leftTimeWindow,rightTimeWindow);

		for(Algorithm alg : algs){
			List<PerformanceMetricEvent> events = t.computeCOSMORocCurvePoints(alg);
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
			//tn 44
			//fn 6

			int expectedTP = 1;
			int expectedFP = 0;
			int expectedTN = 44+44;//all days after 5th, ie 6-50
			int expectedFN = 5+6;//from day 1-5 there was a fault, but not detected

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
			//tn 43
			//fn 6

			expectedTP = 1;
			expectedFP = 1;
			expectedTN = 44+43;//all days after 5th, ie 6-50
			expectedFN = 5+6;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(2);

			Assert.assertEquals(0.02,e.getVaryingThreshold(),0.0001);

			//so
			//vehicle 0
			//tp 2
			//fp 0
			//tn 44
			//fn 4

			//vehicle 1
			//tp 0
			//fp 1
			//tn 43
			//fn 6

			expectedTP = 2;
			expectedFP = 1;
			expectedTN = 44+43;//all days after 5th, ie 6-50
			expectedFN = 4+6;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(5);

			Assert.assertEquals(0.05,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 3
			//fp 0
			//tn 44
			//fn 3

			//vehicle 1
			//tp 1
			//fp 2
			//tn 42
			//fn 5

			expectedTP = 4;
			expectedFP =2;
			expectedTN = 44+42;//all days after 5th, ie 6-50
			expectedFN = 5+3;//from day 1-5 there was a fault, but not detected

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

		}
	}


	@Test
	public void testComputeICOSMORocCurvePoints_1() throws InterruptedException {

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

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(0, 0);
		t.setParitionKeys(algs);
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

			for(int i = 0; i < 100; i ++){
				double zscore = 0.01 * (double)i;

				ROCCurvePointEvent res = new ROCCurvePointEvent(zscore,events);
				rocOut.write(alg, res);
			}
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
			Assert.assertEquals(0.99,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 6
			//fp 44
			//tn 0
			//fn 0

			//vehicle 1
			//tp 5
			//fp 45
			//tn 0
			//fn 0

			expectedTP = 6+5;
			expectedFP = 44+45;
			expectedTN = 0;//all days after 5th, ie 6-50
			expectedFN = 0;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());
		}
	}


	@Test
	public void testComputeICOSMORocCurvePoints_2() throws InterruptedException {
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

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(0, 1);
		t.setParitionKeys(algs);
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

			for(int i = 0; i < 100; i ++){
				double zscore = 0.01 * (double)i;

				ROCCurvePointEvent res = new ROCCurvePointEvent(zscore,events);
				rocOut.write(alg, res);
			}
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
			//tn 44
			//fn 6

			int expectedTP = 1;
			int expectedFP = 0;
			int expectedTN = 44+44;//all days after 5th, ie 6-50
			int expectedFN = 5+6;//from day 1-5 there was a fault, but not detected

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
			//tn 43
			//fn 6

			expectedTP = 1;
			expectedFP = 1;
			expectedTN = 44+43;//all days after 5th, ie 6-50
			expectedFN = 5+6;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(2);

			Assert.assertEquals(0.02,e.getVaryingThreshold(),0.0001);

			//so
			//vehicle 0
			//tp 2
			//fp 0
			//tn 44
			//fn 4

			//vehicle 1
			//tp 0
			//fp 1
			//tn 43
			//fn 6

			expectedTP = 2;
			expectedFP = 1;
			expectedTN = 44+43;//all days after 5th, ie 6-50
			expectedFN = 4+6;//from day 1-5 there was a fault, but not detected

			Assert.assertEquals(expectedTP,e.getTruePositiveCount());
			Assert.assertEquals(expectedFP,e.getFalsePositiveCount());
			Assert.assertEquals(expectedTN,e.getTrueNegativeCount());
			Assert.assertEquals(expectedFN,e.getFalseNegativeCount());


			e = events.get(5);

			Assert.assertEquals(0.05,e.getVaryingThreshold(),0.0001);

			//vehicle 0
			//tp 3
			//fp 0
			//tn 44
			//fn 3

			//vehicle 1
			//tp 1
			//fp 2
			//tn 42
			//fn 5

			expectedTP = 4;
			expectedFP =2;
			expectedTN = 44+42;//all days after 5th, ie 6-50
			expectedFN = 5+3;//from day 1-5 there was a fault, but not detected

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

		}
	}


	@Test
	public void testComputeICOSMORocCurvePoints_3() throws InterruptedException {
		
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

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(0, 0);
		t.setParitionKeys(algs);
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

	

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,true,0.0);
	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore,double zvalue){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,true,zvalue);
	}

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, double zscore){
		return createSensorStatusEvent(alg,0,0,zscore);
	}


	public static TestPerformanceAnalysisTimer createPerformanceTimerWithPopulatedHistory(RepairHistory rh, FaultHistory fh) throws InterruptedException{
		return createPerformanceTimerWithPopulatedHistory(rh,fh,0,0);
	}

	public static TestPerformanceAnalysisTimer createPerformanceTimerWithPopulatedHistory(RepairHistory rh, FaultHistory fh, int leftTimeWindow, int rightTimeWindow) throws InterruptedException {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		List<Vehicle> vehicles = new ArrayList<Vehicle>(2);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));
		SensorStatusHistory ssh = new SensorStatusHistory(algs);

		//ssh.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent2(new Algorithm(0),0.01));
		for(Algorithm alg: algs){
			int time = -1;
			for(int i = 0; i < 100; i ++){
				double zscore = 0.01 * (double)i;

				if((i%2)== 0){
					time++;
				}

				SensorStatusEvent e = new SensorStatusEvent(new Vehicle(i%2), new Sensor(0,0),alg,zscore,true,0.0);

				ssh.recordElement(alg,new TimerEvent(time),e);	
			}
		}
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

		TestPerformanceAnalysisTimer t = new TestPerformanceAnalysisTimer(); 
		t.init(leftTimeWindow, rightTimeWindow);
		t.setParitionKeys(algs);
		t.initStreams(rocIn, performanceMetricOutputStream,hIn);

		t.phaseStarted(new PhaseBeginEvent());
		return t;

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
