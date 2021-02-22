package test.phase.analysis2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.event.stream.SensorStatusInputStream;
import common.event.stream.SensorStatusOutputStream;
import common.event.stream.SensorStatusStreamManager;
import phase.analysis.SensorStatusEventReplayer;
import phase.generation.history.SensorStatusHistory;

public class TestSensorStatusEventReplayer {

	@Test
	public void testReplay() throws InterruptedException {
		List<Algorithm> algs = new ArrayList<Algorithm>(2);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));


		SensorStatusHistory h = new SensorStatusHistory(algs);
		
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.01));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.02));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.03));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.04));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.05));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.06));
		
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.11));
		h.recordElement(new Algorithm(0), new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.12));
		h.recordElement(new Algorithm(0), new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.13));
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.14));
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.15));
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.16));
		
		SensorStatusInputStream in = new SensorStatusInputStream(algs);
		SensorStatusOutputStream out = new SensorStatusOutputStream(algs);
		SensorStatusStreamManager streamManager = new SensorStatusStreamManager(in,out);
		
		SensorStatusEventReplayer replayer = new SensorStatusEventReplayer(algs,h,streamManager);
		
		replayer.replay(new TimerEvent(0));
		
		Iterator<SensorStatusEvent> it = in.iterator(new Algorithm(0));
		SensorStatusEvent e = it.next();
		Assert.assertEquals(0.01,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.02,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.03,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(false,it.hasNext());
		
		it = in.iterator(new Algorithm(1));
		e = it.next();
		Assert.assertEquals(0.04,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.05,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.06,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(false,it.hasNext());
		
		replayer.replay(new TimerEvent(0));
		
		it = in.iterator(new Algorithm(0));
		 e = it.next();
		Assert.assertEquals(0.01,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.02,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.03,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(false,it.hasNext());
		
		it = in.iterator(new Algorithm(1));
		e = it.next();
		Assert.assertEquals(0.04,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.05,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.06,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		Assert.assertEquals(false,it.hasNext());
		
		replayer.replay(new TimerEvent(1));
		
		it = in.iterator(new Algorithm(0));
		e = it.next();
		Assert.assertEquals(0.11,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.12,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.13,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(0),e.getAlgorithm());
		Assert.assertEquals(false,it.hasNext());
		
		it = in.iterator(new Algorithm(1));
		e = it.next();
		Assert.assertEquals(0.14,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.15,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
		e = it.next();
		Assert.assertEquals(0.16,e.getZscore(),0.00001);
		Assert.assertEquals(new Algorithm(1),e.getAlgorithm());
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
}
