package test.common;

import org.junit.Assert;
import org.junit.Test;

import common.Algorithm;
import common.exception.ConfigurationException;

public class TestAlgorithm {

	@Test
	public void test_constructor_getters() {
		Algorithm a = new Algorithm(0);
		a = new Algorithm(1);
		int expected = 1;
		int actual = a.getId();
		Assert.assertEquals(expected,actual);
		
		
		
		a = new Algorithm(2);
		expected = 2;
		actual = a.getId();
		Assert.assertEquals(expected,actual);

		a = new Algorithm(Algorithm.ICOSMO_ID_MOD);
		expected = Algorithm.ICOSMO_ID_MOD;
		actual = a.getId();
		Assert.assertEquals(expected,actual);
		
	}
	
	@Test
	public void test_constructor_InvalidInput() {
		
		boolean flag = false;
		try{
			Algorithm a = new Algorithm(Algorithm.ICOSMO_ID_MOD + 1);
			
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			Algorithm a = new Algorithm(Algorithm.ICOSMO_ID_MOD + 10000);
			
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		 flag = false;
		try{
			Algorithm a = new Algorithm(-1);
			
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
		
		 flag = false;
			try{
				Algorithm a = new Algorithm(-1000);
				
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true,flag);
	}

	@Test
	public void testEquals(){
		Algorithm s1 = new Algorithm(1);
		Algorithm s2 = new Algorithm(1);
		Algorithm s3 = new Algorithm(2);
		Algorithm s4 = new Algorithm(3);
		Algorithm s5 = new Algorithm(4);
		
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
		Algorithm s1 = new Algorithm(1);
		Algorithm s2 = new Algorithm(1);
		
		boolean expected = true;
		boolean actual = s1.hashCode() == s2.hashCode();
		Assert.assertEquals(expected,actual);
		
	}
	
	@Test
	public void testToICOSMO(){
		Algorithm s1 = new Algorithm(1);
		
		
		int expected = (Algorithm.ICOSMO_ID_MOD + 1);
		int actual = s1.toICOSMO().getId();
		Assert.assertEquals(expected,actual);
		
		
		 s1 = new Algorithm(Algorithm.ICOSMO_ID_MOD);
		
		
		expected = (Algorithm.ICOSMO_ID_MOD + Algorithm.ICOSMO_ID_MOD);
		actual = s1.toICOSMO().getId();
		Assert.assertEquals(expected,actual);
		
		 s1 = new Algorithm(0);
			
			
			expected = (Algorithm.ICOSMO_ID_MOD);
			actual = s1.toICOSMO().getId();
			Assert.assertEquals(expected,actual);
		
	}
	
}
