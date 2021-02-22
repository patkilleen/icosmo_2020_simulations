package test.common;

import org.junit.Assert;
import org.junit.Test;

import common.Sensor;
import common.exception.ConfigurationException;

public class TestSensor {


	@Test
	public void test_contructor_invalid_id() {
		boolean flag = false;
		
		try{
			Sensor s = new Sensor(-1,321);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			Sensor s = new Sensor(-1,-1);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			Sensor s = new Sensor(null);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			Sensor s = new Sensor(-1000,321);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
		
flag = false;
		
		try{
			Sensor s = new Sensor(321,-1000);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			Sensor s = new Sensor(-1000,-1000);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
	}
	@Test
	public void testSettersGetters() {
		Sensor s = new Sensor(123,321);
		Sensor s1 = new Sensor(s);
		int expected = 123;
		int actual = s.getPgn();
		Assert.assertEquals(expected,actual);
		
		expected = 321;
		actual = s.getSpn();
		Assert.assertEquals(expected,actual);
		
		
		expected = 123;
		actual = s1.getPgn();
		Assert.assertEquals(expected,actual);
		
		expected = 321;
		actual = s1.getSpn();
		Assert.assertEquals(expected,actual);
		
		s.setPgn(321);
		expected = 321;
		actual = s.getPgn();
		Assert.assertEquals(expected,actual);
		
		s.setSpn(123);
		expected = 123;
		actual = s.getSpn();
		Assert.assertEquals(expected,actual);
		//fail("Not yet implemented");
	}

	@Test
	public void testEquals(){
		Sensor s1 = new Sensor(123,321);
		Sensor s2 = new Sensor(123,321);
		Sensor s3 = new Sensor(123,123);
		Sensor s4 = new Sensor(321,123);
		Sensor s5 = new Sensor(321,321);
		
		boolean expected = true;
		boolean actual = s1.equals(s1);
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s1.equals(s2);
		Assert.assertEquals(expected,actual);
		
		expected = false;
		actual = s1.equals(s3);
		Assert.assertEquals(expected,actual);
		
		expected = false;
		actual = s1.equals(s4);
		Assert.assertEquals(expected,actual);
		
		expected = false;
		actual = s1.equals(s5);
		Assert.assertEquals(expected,actual);
		
		expected = false;
		actual = s1.equals(null);
		Assert.assertEquals(expected,actual);
		
		expected = false;
		actual = s1.equals(new Object());
		Assert.assertEquals(expected,actual);
		
		
	}
	
	@Test
	public void testHashcode(){
		Sensor s1 = new Sensor(123,321);
		Sensor s2 = new Sensor(123,321);
		
		boolean expected = true;
		boolean actual = s1.hashCode() == s2.hashCode();
		Assert.assertEquals(expected,actual);
		
	}
	
	@Test
	public void testCompareTo(){
		Sensor s1 = new Sensor(123,321);
		Sensor s2 = new Sensor(123,321);
		Sensor s3 = new Sensor(321,123);
		Sensor s4 = new Sensor(123,123);
		Sensor s5 = new Sensor(321,321);
		Sensor s6 = new Sensor(400,400);
		Sensor s7 = new Sensor(10,10);
		
		boolean expected = true;
		boolean actual = s7.compareTo(s1) < 0;
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s1.compareTo(s7) > 0;
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s1.compareTo(s4) > 0;
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s1.compareTo(s2) == 0;
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s1.compareTo(s3) < 0;
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s4.compareTo(s1) < 0;
		Assert.assertEquals(expected,actual);
		
		
		expected = true;
		actual = s5.compareTo(s3) > 0;
		Assert.assertEquals(expected,actual);
		
		expected = true;
		actual = s3.compareTo(s5) < 0;
		Assert.assertEquals(expected,actual);
	}
}
