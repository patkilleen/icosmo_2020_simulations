package test.phase.analysis2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;
import common.event.ROCCurvePointEvent;
import common.event.TimeStampedSensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.ROCCurvePointInputStream;
import common.event.stream.ROCCurvePointOutputStream;
import common.event.stream.ROCCurvePointStreamManager;
import common.event.stream.TimeStampedSensorStatusInputStream;
import common.event.stream.TimeStampedSensorStatusOutputStream;
import common.event.stream.TimeStampedSensorStatusStreamManager;
import junit.framework.Assert;
import phase.analysis.ROCPointBuilder;

public class TestROCPointBuilder {

	@Test
	public void test() throws InterruptedException{
		
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		
		ROCCurvePointOutputStream rocOut = new ROCCurvePointOutputStream(algs);
		ROCCurvePointInputStream rocIn = new ROCCurvePointInputStream(algs);
		ROCCurvePointStreamManager rocManager = new ROCCurvePointStreamManager(rocIn,rocOut);
		
		TimeStampedSensorStatusInputStream sensorStatusIn = new TimeStampedSensorStatusInputStream(algs);
		TimeStampedSensorStatusOutputStream sensorStatusOut = new TimeStampedSensorStatusOutputStream(algs);
		TimeStampedSensorStatusStreamManager sesnroStatusManager = new TimeStampedSensorStatusStreamManager(sensorStatusIn,sensorStatusOut);
		
		ROCPointBuilder builder = new ROCPointBuilder(algs, rocOut,sensorStatusIn); 
		
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.01,false,0.11,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.02,true,0.12,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.03,false,0.13,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.04,true,0.14,new TimerEvent(1)));
		
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.05,false,0.15,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.06,true,0.16,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.07,false,0.17,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.08,true,0.18,new TimerEvent(1)));
		
		sesnroStatusManager.flush();
		
		builder.outputROCPoints(0.15);
		
		rocManager.flush();
		
		Iterator<ROCCurvePointEvent> it = rocIn.iterator(new Algorithm(0));
		ROCCurvePointEvent e = it.next();
		Assert.assertEquals(0.15,e.getCosmoDeviationThreshold(),0.00001);
		
		List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();
		Assert.assertEquals(4,events.size());
		TimeStampedSensorStatusEvent e2 = events.get(0);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(new Vehicle(0),e2.getVehicle());
		Assert.assertEquals(new Sensor(0,0),e2.getSensor());
		Assert.assertEquals(0.01,e2.getZscore(),0.00001);
		Assert.assertEquals(false,e2.isCosmoSensorFlag());
		Assert.assertEquals(0.11,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());
		

		 e2 = events.get(1);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(new Vehicle(1),e2.getVehicle());
		Assert.assertEquals(new Sensor(0,0),e2.getSensor());
		Assert.assertEquals(0.02,e2.getZscore(),0.00001);
		Assert.assertEquals(true,e2.isCosmoSensorFlag());
		Assert.assertEquals(0.12,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());
		

		 e2 = events.get(2);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(new Vehicle(0),e2.getVehicle());
		Assert.assertEquals(new Sensor(0,0),e2.getSensor());
		Assert.assertEquals(0.03,e2.getZscore(),0.00001);
		Assert.assertEquals(false,e2.isCosmoSensorFlag());
		Assert.assertEquals(0.13,e2.getZvalue(),0.00001);
		Assert.assertEquals(1,e2.getTimerEvent().getTime());
		
		 e2 = events.get(3);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(1),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.04,e2.getZscore(),0.00001);
			Assert.assertEquals(true,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.14,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());
			
			
			it = rocIn.iterator(new Algorithm(1));
			 e = it.next();
			Assert.assertEquals(0.15,e.getCosmoDeviationThreshold(),0.00001);
			
		 events = e.getSensorStatuses();
			Assert.assertEquals(4,events.size());
			 e2 = events.get(0);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(0),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.05,e2.getZscore(),0.00001);
			Assert.assertEquals(false,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.15,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());
			

			 e2 = events.get(1);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(1),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.06,e2.getZscore(),0.00001);
			Assert.assertEquals(true,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.16,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());
			

			 e2 = events.get(2);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(0),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.07,e2.getZscore(),0.00001);
			Assert.assertEquals(false,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.17,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());
			
			 e2 = events.get(3);
				Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
				Assert.assertEquals(new Vehicle(1),e2.getVehicle());
				Assert.assertEquals(new Sensor(0,0),e2.getSensor());
				Assert.assertEquals(0.08,e2.getZscore(),0.00001);
				Assert.assertEquals(true,e2.isCosmoSensorFlag());
				Assert.assertEquals(0.18,e2.getZvalue(),0.00001);
				Assert.assertEquals(1,e2.getTimerEvent().getTime());
	}

	@Test
	public void tes2() throws InterruptedException{
		
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));
		
		ROCCurvePointOutputStream rocOut = new ROCCurvePointOutputStream(algs);
		ROCCurvePointInputStream rocIn = new ROCCurvePointInputStream(algs);
		ROCCurvePointStreamManager rocManager = new ROCCurvePointStreamManager(rocIn,rocOut);
		
		TimeStampedSensorStatusInputStream sensorStatusIn = new TimeStampedSensorStatusInputStream(algs);
		TimeStampedSensorStatusOutputStream sensorStatusOut = new TimeStampedSensorStatusOutputStream(algs);
		TimeStampedSensorStatusStreamManager sesnroStatusManager = new TimeStampedSensorStatusStreamManager(sensorStatusIn,sensorStatusOut);
		
		ROCPointBuilder builder = new ROCPointBuilder(algs, rocOut,sensorStatusIn); 
		
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.01,false,0.11,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.02,true,0.12,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(0),0.03,false,0.13,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(0),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(0),0.04,true,0.14,new TimerEvent(1)));
		
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.05,false,0.15,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.06,true,0.16,new TimerEvent(0)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(0),new Sensor(0,0),new Algorithm(1),0.07,false,0.17,new TimerEvent(1)));
		sensorStatusOut.write(new Algorithm(1),new TimeStampedSensorStatusEvent(new Vehicle(1),new Sensor(0,0),new Algorithm(1),0.08,true,0.18,new TimerEvent(1)));
		
		sesnroStatusManager.flush();
		
		builder.outputROCPoints(0.15);
		builder.outputROCPoints(0.16);
		builder.outputROCPoints(0.50);
		
		rocManager.flush();
		
	
		Iterator<ROCCurvePointEvent> it = rocIn.iterator(new Algorithm(0));
		double [] expectedThresholds = new double[3]; 
		expectedThresholds[0]= 0.15;
		expectedThresholds[1]= 0.16;
		expectedThresholds[2]= 0.50;
		for(double t : expectedThresholds){
			ROCCurvePointEvent e = it.next();
		Assert.assertEquals(t,e.getCosmoDeviationThreshold(),0.00001);
		
		List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();
		Assert.assertEquals(4,events.size());
		TimeStampedSensorStatusEvent e2 = events.get(0);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(new Vehicle(0),e2.getVehicle());
		Assert.assertEquals(new Sensor(0,0),e2.getSensor());
		Assert.assertEquals(0.01,e2.getZscore(),0.00001);
		Assert.assertEquals(false,e2.isCosmoSensorFlag());
		Assert.assertEquals(0.11,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());
		

		 e2 = events.get(1);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(new Vehicle(1),e2.getVehicle());
		Assert.assertEquals(new Sensor(0,0),e2.getSensor());
		Assert.assertEquals(0.02,e2.getZscore(),0.00001);
		Assert.assertEquals(true,e2.isCosmoSensorFlag());
		Assert.assertEquals(0.12,e2.getZvalue(),0.00001);
		Assert.assertEquals(0,e2.getTimerEvent().getTime());
		

		 e2 = events.get(2);
		Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
		Assert.assertEquals(new Vehicle(0),e2.getVehicle());
		Assert.assertEquals(new Sensor(0,0),e2.getSensor());
		Assert.assertEquals(0.03,e2.getZscore(),0.00001);
		Assert.assertEquals(false,e2.isCosmoSensorFlag());
		Assert.assertEquals(0.13,e2.getZvalue(),0.00001);
		Assert.assertEquals(1,e2.getTimerEvent().getTime());
		
		 e2 = events.get(3);
			Assert.assertEquals(new Algorithm(0),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(1),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.04,e2.getZscore(),0.00001);
			Assert.assertEquals(true,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.14,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());
		}	
		
		for(double t : expectedThresholds){	
			it = rocIn.iterator(new Algorithm(1));
			ROCCurvePointEvent	 e = it.next();
			Assert.assertEquals(0.15,e.getCosmoDeviationThreshold(),0.00001);
			
			List<TimeStampedSensorStatusEvent> events = e.getSensorStatuses();
			Assert.assertEquals(4,events.size());
			TimeStampedSensorStatusEvent e2 = events.get(0);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(0),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.05,e2.getZscore(),0.00001);
			Assert.assertEquals(false,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.15,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());
			

			 e2 = events.get(1);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(1),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.06,e2.getZscore(),0.00001);
			Assert.assertEquals(true,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.16,e2.getZvalue(),0.00001);
			Assert.assertEquals(0,e2.getTimerEvent().getTime());
			

			 e2 = events.get(2);
			Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
			Assert.assertEquals(new Vehicle(0),e2.getVehicle());
			Assert.assertEquals(new Sensor(0,0),e2.getSensor());
			Assert.assertEquals(0.07,e2.getZscore(),0.00001);
			Assert.assertEquals(false,e2.isCosmoSensorFlag());
			Assert.assertEquals(0.17,e2.getZvalue(),0.00001);
			Assert.assertEquals(1,e2.getTimerEvent().getTime());
			
			 e2 = events.get(3);
				Assert.assertEquals(new Algorithm(1),e2.getAlgorithm());
				Assert.assertEquals(new Vehicle(1),e2.getVehicle());
				Assert.assertEquals(new Sensor(0,0),e2.getSensor());
				Assert.assertEquals(0.08,e2.getZscore(),0.00001);
				Assert.assertEquals(true,e2.isCosmoSensorFlag());
				Assert.assertEquals(0.18,e2.getZvalue(),0.00001);
				Assert.assertEquals(1,e2.getTimerEvent().getTime());
				
		}
	}

}
