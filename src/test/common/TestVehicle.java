package test.common;

import org.junit.Assert;
import org.junit.Test;

import common.Vehicle;
import common.exception.ConfigurationException;

public class TestVehicle {

	@Test
	public void test_constructor_getters() {
		Vehicle a = new Vehicle(0);
		a = new Vehicle(1);
		int expected = 1;
		int actual = a.getVid();
		Assert.assertEquals(expected,actual);
		
		
		
		a = new Vehicle(20000);
		expected = 20000;
		actual = a.getVid();
		Assert.assertEquals(expected,actual);

		
	}
	
	@Test
	public void test_constructor_InvalidInput() {
		
		boolean flag = false;
		try{
			Vehicle a = new Vehicle(-1);
			
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
		
		 flag = false;
			try{
				Vehicle a = new Vehicle(-1000);
				
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true,flag);
	}

	@Test
	public void testEquals(){
		Vehicle s1 = new Vehicle(1);
		Vehicle s2 = new Vehicle(1);
		Vehicle s3 = new Vehicle(2);
		Vehicle s4 = new Vehicle(3);
		Vehicle s5 = new Vehicle(4);
		
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
		Vehicle s1 = new Vehicle(1);
		Vehicle s2 = new Vehicle(1);
		
		boolean expected = true;
		boolean actual = s1.hashCode() == s2.hashCode();
		Assert.assertEquals(expected,actual);
		
	}
	
}
