package test.phase.generation.history;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import phase.generation.history.SensorStatusHistory;

public class TestSensorStatusHistory {

	@Test
	public void test_constructor_illegal_arg_null() {
		boolean flag = false;

		try{
			new SensorStatusHistory(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_empty() {
		boolean flag = false;

		try{
			new SensorStatusHistory(new ArrayList<Algorithm>(0));
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_duplicate_key() {
		boolean flag = false;

		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(1));
		try{
			new SensorStatusHistory(arg);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor() {


		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(2));
		arg.add(new Algorithm(3));

		new SensorStatusHistory(arg);
	}
	
	@Test
	public void test_getUniqueSensors_empty() {
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(2));
		arg.add(new Algorithm(3));

		SensorStatusHistory h = new SensorStatusHistory(arg);
		
		List<Sensor> actual = h.getUniqueSensors();
		
		Assert.assertEquals(0, actual.size());
	}
	
	@Test
	public void test_getUniqueZscore_empty() {
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(2));
		arg.add(new Algorithm(3));

		SensorStatusHistory h = new SensorStatusHistory(arg);
		
		List<Double> actual = h.getUniqueZScores();
		
		Assert.assertEquals(0, actual.size());
	}
	
	@Test
	public void test_getUniqueSensors_no_duplicates() {
		
		
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(1));


		SensorStatusHistory h = new SensorStatusHistory(arg);
		
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,0));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,1));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,2));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,3));
		
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,0));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,1));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,2));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,3));
		
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,0));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,1));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,2));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,3));
		
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),3,0));
		h.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),3,1));
		h.recordElement(new Algorithm(1),new TimerEvent(3), createSensorStatusEvent(new Algorithm(1),3,2));
		h.recordElement(new Algorithm(1),new TimerEvent(4), createSensorStatusEvent(new Algorithm(1),3,3));
		
		List<Sensor> actual = h.getUniqueSensors();
		
		Assert.assertEquals(16, actual.size());
		
		for(int pgn = 0; pgn < 3; pgn ++){
			for(int spn = 0; spn < 3; spn ++){
				Assert.assertEquals(true, actual.contains(new Sensor(pgn,spn)));
			}	
		}
		
		
	}
	
	
	@Test
	public void test_getUniqueSensors_with_duplicates() {
		
		
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(1));


		SensorStatusHistory h = new SensorStatusHistory(arg);
		
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,0));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,1));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,2));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,3));
		
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,0));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,1));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,2));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,3));
		
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,0));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,1));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,2));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,3));
		
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),3,0));
		h.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),3,1));
		h.recordElement(new Algorithm(1),new TimerEvent(3), createSensorStatusEvent(new Algorithm(1),3,2));
		h.recordElement(new Algorithm(1),new TimerEvent(4), createSensorStatusEvent(new Algorithm(1),3,3));
		
		h.recordElement(new Algorithm(0),new TimerEvent(5), createSensorStatusEvent(new Algorithm(0),0,0));
		h.recordElement(new Algorithm(0),new TimerEvent(7), createSensorStatusEvent(new Algorithm(0),0,1));
		h.recordElement(new Algorithm(0),new TimerEvent(8), createSensorStatusEvent(new Algorithm(0),0,2));
		h.recordElement(new Algorithm(0),new TimerEvent(9), createSensorStatusEvent(new Algorithm(0),0,3));
		
		h.recordElement(new Algorithm(1),new TimerEvent(10), createSensorStatusEvent(new Algorithm(1),1,0));
		h.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),1,1));
		h.recordElement(new Algorithm(1),new TimerEvent(100), createSensorStatusEvent(new Algorithm(1),1,2));
		h.recordElement(new Algorithm(1),new TimerEvent(50), createSensorStatusEvent(new Algorithm(1),1,3));
		
		h.recordElement(new Algorithm(0),new TimerEvent(22), createSensorStatusEvent(new Algorithm(0),2,0));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),2,1));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),2,2));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),2,3));
		
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,0));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,1));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,2));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,3));
		
		List<Sensor> actual = h.getUniqueSensors();
		
		Assert.assertEquals(16, actual.size());
		
		for(int pgn = 0; pgn < 3; pgn ++){
			for(int spn = 0; spn < 3; spn ++){
				Assert.assertEquals(true, actual.contains(new Sensor(pgn,spn)));
			}	
		}
		
		
	}
	
	@Test
	public void test_getUniqueZscores_no_duplicates() {
		
		
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(1));


		SensorStatusHistory h = new SensorStatusHistory(arg);
		
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.01));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.02));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.03));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.04));
		
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.05));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.06));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.07));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.08));
		
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.09));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.1));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.11));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.12));
		
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.13));
		h.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),0.14));
		h.recordElement(new Algorithm(1),new TimerEvent(3), createSensorStatusEvent(new Algorithm(1),0.15));
		h.recordElement(new Algorithm(1),new TimerEvent(4), createSensorStatusEvent(new Algorithm(1),0.16));
		
		List<Double> actual = h.getUniqueZScores();
		
		Assert.assertEquals(16, actual.size());
		
		Assert.assertEquals(true,actual.contains(0.01));
		Assert.assertEquals(true,actual.contains(0.02));
		Assert.assertEquals(true,actual.contains(0.03));
		Assert.assertEquals(true,actual.contains(0.04));
		
		Assert.assertEquals(true,actual.contains(0.05));
		Assert.assertEquals(true,actual.contains(0.06));
		Assert.assertEquals(true,actual.contains(0.07));
		Assert.assertEquals(true,actual.contains(0.08));
		
		Assert.assertEquals(true,actual.contains(0.09));
		Assert.assertEquals(true,actual.contains(0.1));
		Assert.assertEquals(true,actual.contains(0.11));
		Assert.assertEquals(true,actual.contains(0.12));
		
		Assert.assertEquals(true,actual.contains(0.13));
		Assert.assertEquals(true,actual.contains(0.14));
		Assert.assertEquals(true,actual.contains(0.15));
		Assert.assertEquals(true,actual.contains(0.16));
		
		
		
	}
	
	@Test
	public void test_getUniqueZscores_with_duplicates() {
		
		
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(1));


		SensorStatusHistory h = new SensorStatusHistory(arg);
		
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.01));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.02));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.03));
		h.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0.04));
		
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.05));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.06));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.07));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.08));
		
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.09));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.1));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.11));
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.12));
		
		h.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),0.13));
		h.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),0.14));
		h.recordElement(new Algorithm(1),new TimerEvent(3), createSensorStatusEvent(new Algorithm(1),0.15));
		h.recordElement(new Algorithm(1),new TimerEvent(4), createSensorStatusEvent(new Algorithm(1),0.16));
		
		
		h.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),0.01));
		h.recordElement(new Algorithm(0),new TimerEvent(3), createSensorStatusEvent(new Algorithm(0),0.02));
		h.recordElement(new Algorithm(0),new TimerEvent(10), createSensorStatusEvent(new Algorithm(0),0.03));
		h.recordElement(new Algorithm(0),new TimerEvent(11), createSensorStatusEvent(new Algorithm(0),0.04));
		
		h.recordElement(new Algorithm(1),new TimerEvent(15), createSensorStatusEvent(new Algorithm(1),0.05));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.06));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.07));
		h.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),0.08));
		
		h.recordElement(new Algorithm(0),new TimerEvent(12), createSensorStatusEvent(new Algorithm(0),0.09));
		h.recordElement(new Algorithm(0),new TimerEvent(3), createSensorStatusEvent(new Algorithm(0),0.1));
		h.recordElement(new Algorithm(0),new TimerEvent(13), createSensorStatusEvent(new Algorithm(0),0.11));
		h.recordElement(new Algorithm(0),new TimerEvent(14), createSensorStatusEvent(new Algorithm(0),0.12));
		
		h.recordElement(new Algorithm(1),new TimerEvent(15), createSensorStatusEvent(new Algorithm(1),0.13));
		h.recordElement(new Algorithm(1),new TimerEvent(26), createSensorStatusEvent(new Algorithm(1),0.14));
		h.recordElement(new Algorithm(1),new TimerEvent(39), createSensorStatusEvent(new Algorithm(1),0.15));
		h.recordElement(new Algorithm(1),new TimerEvent(410), createSensorStatusEvent(new Algorithm(1),0.16));
		
		List<Double> actual = h.getUniqueZScores();
		
		Assert.assertEquals(16, actual.size());
		
		Assert.assertEquals(true,actual.contains(0.01));
		Assert.assertEquals(true,actual.contains(0.02));
		Assert.assertEquals(true,actual.contains(0.03));
		Assert.assertEquals(true,actual.contains(0.04));
		
		Assert.assertEquals(true,actual.contains(0.05));
		Assert.assertEquals(true,actual.contains(0.06));
		Assert.assertEquals(true,actual.contains(0.07));
		Assert.assertEquals(true,actual.contains(0.08));
		
		Assert.assertEquals(true,actual.contains(0.09));
		Assert.assertEquals(true,actual.contains(0.1));
		Assert.assertEquals(true,actual.contains(0.11));
		Assert.assertEquals(true,actual.contains(0.12));
		
		Assert.assertEquals(true,actual.contains(0.13));
		Assert.assertEquals(true,actual.contains(0.14));
		Assert.assertEquals(true,actual.contains(0.15));
		Assert.assertEquals(true,actual.contains(0.16));
		
		
		
	}
	
	
	@Test
	public void test_equals() {
		
		
		List<Algorithm> arg = new ArrayList<Algorithm>(4);
		arg.add(new Algorithm(0));
		arg.add(new Algorithm(1));

		
		List<SensorStatusHistory> hists = new ArrayList<SensorStatusHistory>(3);
		for(int i = 0;i<3;i++){
			SensorStatusHistory h = new SensorStatusHistory(arg);
			hists.add(h);
			h.recordElement(new Algorithm(0),new TimerEvent(0), new SensorStatusEvent(new Vehicle(0), new Sensor(0,0),new Algorithm(0),1.0/10.0,false,1.01));
			h.recordElement(new Algorithm(0),new TimerEvent(0), new SensorStatusEvent(new Vehicle(0), new Sensor(1,0),new Algorithm(0),1.0/100.0,true,1.02));
			h.recordElement(new Algorithm(0),new TimerEvent(0), new SensorStatusEvent(new Vehicle(1), new Sensor(0,1),new Algorithm(0),1.0/100.0,false,1.03));
			h.recordElement(new Algorithm(0),new TimerEvent(0), new SensorStatusEvent(new Vehicle(1), new Sensor(1,1),new Algorithm(0),1.0/1000.0,true,1.04));
			
			h.recordElement(new Algorithm(1),new TimerEvent(0), new SensorStatusEvent(new Vehicle(0), new Sensor(0,0),new Algorithm(1),1.0/10000.0,false,1.05));
			h.recordElement(new Algorithm(1),new TimerEvent(0), new SensorStatusEvent(new Vehicle(0), new Sensor(1,0),new Algorithm(1),1.0/100000.0,true,1.06));
			h.recordElement(new Algorithm(1),new TimerEvent(0), new SensorStatusEvent(new Vehicle(1), new Sensor(0,1),new Algorithm(1),1.0/1000000.0,false,1.07));
			h.recordElement(new Algorithm(1),new TimerEvent(0), new SensorStatusEvent(new Vehicle(1), new Sensor(1,1),new Algorithm(1),1.0/10000000.0,true,1.08));
			
			h.recordElement(new Algorithm(0),new TimerEvent(1), new SensorStatusEvent(new Vehicle(0), new Sensor(0,0),new Algorithm(0),2.0/10000000.0,false,1.01));
			h.recordElement(new Algorithm(0),new TimerEvent(2), new SensorStatusEvent(new Vehicle(0), new Sensor(1,0),new Algorithm(0),21.0/10000000.0,true,1.02));
			h.recordElement(new Algorithm(0),new TimerEvent(3), new SensorStatusEvent(new Vehicle(1), new Sensor(0,1),new Algorithm(0),3.0/10000000.0,false,1.03));
			h.recordElement(new Algorithm(0),new TimerEvent(4), new SensorStatusEvent(new Vehicle(1), new Sensor(1,1),new Algorithm(0),41.0/10000000.04,true,1.04));
			
			h.recordElement(new Algorithm(1),new TimerEvent(1), new SensorStatusEvent(new Vehicle(0), new Sensor(0,0),new Algorithm(1),51.0/10000000.0,false,1.05));
			h.recordElement(new Algorithm(1),new TimerEvent(1), new SensorStatusEvent(new Vehicle(0), new Sensor(1,0),new Algorithm(1),61.0/10000000.0,true,1.06));
			h.recordElement(new Algorithm(1),new TimerEvent(1), new SensorStatusEvent(new Vehicle(1), new Sensor(0,1),new Algorithm(1),71.0/10000000.0,false,1.07));
			h.recordElement(new Algorithm(1),new TimerEvent(1), new SensorStatusEvent(new Vehicle(1), new Sensor(1,1),new Algorithm(1),81.0/10000000.0,false,1.08));
			
			if(i == 2){
				h.recordElement(new Algorithm(1),new TimerEvent(1), new SensorStatusEvent(new Vehicle(1), new Sensor(1,1),new Algorithm(1),81.0/10000005.0,false,1.08));
			}
		}
		
		SensorStatusHistory h1 = hists.get(0);
		SensorStatusHistory h2 = hists.get(1);
		SensorStatusHistory h3 = hists.get(2);
		Assert.assertEquals(true,h1.equals(h1));
		Assert.assertEquals(true,h1.equals(h2));
		Assert.assertEquals(true,h2.equals(h1));
		Assert.assertEquals(true,h2.equals(h2));
		
		Assert.assertEquals(false,h1.equals(h3));
		Assert.assertEquals(false,h3.equals(h1));
		Assert.assertEquals(false,h2.equals(h3));
		Assert.assertEquals(false,h3.equals(h2));
		Assert.assertEquals(true,h3.equals(h3));
		
		SensorStatusHistory h4 = null;
		
		Assert.assertEquals(false,h1.equals(h4));
		Assert.assertEquals(false,h2.equals(h4));
		Assert.assertEquals(false,h3.equals(h4));
		
		
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
