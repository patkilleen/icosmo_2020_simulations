package test.common.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.event.TimerEvent;
import common.exception.ConfigurationException;
import common.synchronization.MultiThreadedTimer;

public class TestMultiThreadedTimer extends MultiThreadedTimer<Integer>{

	private List<Integer> actualKeys;
	
	@Test
	public void test_random_numbers() {

		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		MultiThreadedTimer<Integer> t = new MultiThreadedTimer<Integer>(ints);

		for(int j = 0;j< 1000;j++){
		for(int i = 0; i < ints.size();i++){
			double value = t.nextRandomNumber(i);
			Assert.assertEquals(true, (value >=0)&&(value <=1));
		}
		}
		
	}
	
	@Test
	public void test_random_numbers_stream_size_defined() {

		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		MultiThreadedTimer<Integer> t = new MultiThreadedTimer<Integer>(ints,1000);

		for(int j = 0;j< 1000;j++){
		for(int i = 0; i < ints.size();i++){
			double value = t.nextRandomNumber(i);
			Assert.assertEquals(true, (value >=0)&&(value <=1));
		}
		}
		
	}
	@Test
	public void test_random_numbers_stream_set_patrition_key() {

		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		TestMultiThreadedTimer t = new TestMultiThreadedTimer();
		t.setParitionKeys(ints);
		t.initRandomGenerators();

		for(int j = 0;j< 1000;j++){
		for(int i = 0; i < ints.size();i++){
			double value = t.nextRandomNumber(i);
			Assert.assertEquals(true, (value >=0)&&(value <=1));
		}
		}
		
	}
	
	
	@Test
	public void test_random_numbers_stream_size_defined_set_patrition_key() {

		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		TestMultiThreadedTimer t = new TestMultiThreadedTimer();
		t.setParitionKeys(ints);
		t.initRandomGenerators(1000);

		for(int j = 0;j< 1000;j++){
		for(int i = 0; i < ints.size();i++){
			double value = t.nextRandomNumber(i);
			Assert.assertEquals(true, (value >=0)&&(value <=1));
		}
		}
		
	}
	
	@Test
	public void test_constructor_illegal_arg() {
		boolean flag = false;
		
		try{
			MultiThreadedTimer t = new MultiThreadedTimer(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			MultiThreadedTimer<Object> t = new MultiThreadedTimer<Object>(new ArrayList<Object>(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(3);
		ints.add(3);
		
		flag = false;
		
		try{
			MultiThreadedTimer<Integer> t = new MultiThreadedTimer<Integer>(ints);//non unique keys
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
	}

	@Test
	public void test_constructor() {
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		MultiThreadedTimer<Integer> t = new MultiThreadedTimer<Integer>(ints);
		
		List<Integer> actual = t.getThreadPartitionKeys();
		Assert.assertEquals(ints.size(),actual.size());
		
		for(int i = 0; i < ints.size();i++){
			Assert.assertEquals(ints.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test_tick() throws InterruptedException {
		
	
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		TestMultiThreadedTimer t = new TestMultiThreadedTimer();
		t.actualKeys = new ArrayList<Integer>(4);
		t.setParitionKeys(ints);
		
		t.tick(null);
		
		Assert.assertEquals(4, t.actualKeys.size());
		Assert.assertEquals(true, t.actualKeys.contains(0));
		Assert.assertEquals(true, t.actualKeys.contains(1));
		Assert.assertEquals(true, t.actualKeys.contains(2));
		Assert.assertEquals(true, t.actualKeys.contains(3));
	}
	
	protected void  threadTick(TimerEvent e, Integer key){
		synchronized(actualKeys){
			actualKeys.add(key);
		}
	}
	

}
