package test.phase.generation.cosmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import common.Sensor;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.cosmo.SensorInterest;

public class TestSensorInterest {

	@Test
	public void testSensorInterest_constructor() {
		new SensorInterest(new Sensor(0,0),0,0);
	}

	@Test
	public void testSensorInterest_constructor_illegal_arg() {
		boolean flag = false;

		try{
			new SensorInterest(new Sensor(0,0),-1,0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),-0.5,0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),-1000,0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),1.1,0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),1000,0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),0,-0.5);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),0,-1000);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),0,1.1);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			new SensorInterest(new Sensor(0,0),0,1000);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			new SensorInterest(null,0,0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void testGetSensor() {
		Sensor s = new Sensor(0,0);
		SensorInterest si = new SensorInterest(s,0,0);
		Assert.assertEquals(true,s == si.getSensor());
	}

	@Test
	public void testSetSensor_illegal_argument() {
		boolean flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setSensor(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void testSetSensor() {
		Sensor s = new Sensor(0,0);
		SensorInterest si = new SensorInterest(s,0,0);
		s = new Sensor(1,0);
		si.setSensor(s);
		Assert.assertEquals(true,s == si.getSensor());
	}

	@Test
	public void testGetNormalizedEntropy() {
		Sensor s = new Sensor(0,0);
		SensorInterest si = new SensorInterest(s,0.5,0);
		Assert.assertEquals(0.5,si.getNormalizedEntropy(),0.0001);
	}

	@Test
	public void testSetNormalizedEntropy() {
		Sensor s = new Sensor(0,0);
		SensorInterest si = new SensorInterest(s,0.5,0);
		si.setNormalizedEntropy(0.6);
		Assert.assertEquals(0.6,si.getNormalizedEntropy(),0.0001);
	}


	@Test
	public void testSetNormalizedEntropy_illegal_argument() {
		boolean flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setNormalizedEntropy(1000);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setNormalizedEntropy(1.1);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setNormalizedEntropy(-0.5);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setNormalizedEntropy(-1000);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void testGetStability() {
		Sensor s = new Sensor(0,0);
		SensorInterest si = new SensorInterest(s,0,0.5);
		Assert.assertEquals(0.5,si.getStability(),0.0001);
	}

	@Test
	public void testSetStability() {
		Sensor s = new Sensor(0,0);
		SensorInterest si = new SensorInterest(s,0,0.5);
		si.setStability(0.6);
		Assert.assertEquals(0.6,si.getStability(),0.0001);
	}


	@Test
	public void testSetStability_illegal_arg() {
		boolean flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setStability(1000);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setStability(1.1);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setStability(-0.5);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;

		try{
			SensorInterest si = new SensorInterest(new Sensor(0,0),0,0);
			si.setStability(-1000);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void testSort(){
		List<SensorInterest> sensors = new ArrayList<SensorInterest>();
		sensors.add(new SensorInterest(new Sensor(0,0),1.0,0.0));//interest: 0
		sensors.add(new SensorInterest(new Sensor(0,1),0.4,0.0));//interest: 0.6
		sensors.add(new SensorInterest(new Sensor(0,2),0.0,0.1));//interest: 1.1
		sensors.add(new SensorInterest(new Sensor(0,3),0.0,0.5));//interest: 1.5
		sensors.add(new SensorInterest(new Sensor(0,4),0.0,1.0));//interest: 2
		sensors.add(new SensorInterest(new Sensor(0,5),1.0,1.0));//interest: 1
		sensors.add(new SensorInterest(new Sensor(0,6),0.6,0.8));//interest: 1.2
		
		Collections.sort(sensors);
		
		Assert.assertEquals(new Sensor(0,0),sensors.get(0).getSensor());//0
		Assert.assertEquals(new Sensor(0,1),sensors.get(1).getSensor());//0.6
		Assert.assertEquals(new Sensor(0,5),sensors.get(2).getSensor());//1
		Assert.assertEquals(new Sensor(0,2),sensors.get(3).getSensor());//1.1
		Assert.assertEquals(new Sensor(0,6),sensors.get(4).getSensor());//1.2
		Assert.assertEquals(new Sensor(0,3),sensors.get(5).getSensor());//1.5
		Assert.assertEquals(new Sensor(0,4),sensors.get(6).getSensor());//2
	}
	@Test
	public void testCompareTo() {
		
		//s1: (1-0.5) + 0.5 = 0.5 +0.5 = 1 
		//s2: (1-0.6) + 0.5 = 0.4 +0.5 = 0.9
		SensorInterest si1 = new SensorInterest(new Sensor(0,0),0.5,0.5);
		SensorInterest si2 = new SensorInterest(new Sensor(0,0),0.6,0.5);
		//si2 is more interesting
		int actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual > 0);
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual < 0);


		//s1: (1-0.5) + 0.6 = 0.5 +0.6 = 1.1 
		//s2: (1-0.6) + 0.5 = 0.4 +0.5 = 0.9
		si1 = new SensorInterest(new Sensor(0,0),0.5,0.6);
		si2 = new SensorInterest(new Sensor(0,0),0.6,0.5);
		//si2 is more interesting
		actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual > 0);
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual < 0);

		//s1: (1-0.5) + 0.6 = 0.5 +0.6 = 1.1 
		//s2: (1-0.6) + 0.7 = 0.4 +0.7 = 1.1
		si1 = new SensorInterest(new Sensor(0,0),0.5,0.6);
		si2 = new SensorInterest(new Sensor(0,0),0.6,0.7);
		//both same interest
		actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual == 0);
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual == 0);

		
		//s1: (1-0.6) + 0.5 = 0.4 +0.5 = 0.9 
		//s2: (1-0.5) + 0.6 = 0.5 +0.6 = 1.1
		si1 = new SensorInterest(new Sensor(0,0),0.6,0.5);
		si2 = new SensorInterest(new Sensor(0,0),0.5,0.6);
		//si1 is more interesting
		actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual < 0);
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual > 0);
		
		
		//s1: (1-0.5) + 0.5 = 0.5 +0.5 = 1 
		//s2: (1-0.5) + 0.5 = 0.5 +0.5 = 1
		si1 = new SensorInterest(new Sensor(0,0),0.5,0.5);
		si2 = new SensorInterest(new Sensor(0,0),0.5,0.5);
		//both same interest
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual == 0);
		actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual == 0);
		
		
		//s1: (1-0.7) + 0.7 = 0.3 +0.7 = 1 
		//s2: (1-0.6) + 0.7 = 0.4 +0.7 = 1.1
		si1 = new SensorInterest(new Sensor(0,0),0.7,0.7);
		si2 = new SensorInterest(new Sensor(0,0),0.6,0.7);

		//si1 is more interesting
		actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual < 0);
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual > 0);

		
		//s1: (1-0.6) + 0.7 = 0.4 +0.7 = 1.1 
		//s2: (1-0.7) + 0.7 = 0.3 +0.7 = 1
		si1 = new SensorInterest(new Sensor(0,0),0.6,0.7);
		si2 = new SensorInterest(new Sensor(0,0),0.7,0.7);
		//si2 is more interesting
		actual = si1.compareTo(si2);
		Assert.assertEquals(true,actual > 0);
		actual = si2.compareTo(si1);
		Assert.assertEquals(true,actual < 0);

	}

}
