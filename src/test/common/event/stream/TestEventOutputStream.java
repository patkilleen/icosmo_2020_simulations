package test.common.event.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.event.Event;
import common.event.stream.EventInputStream;
import common.event.stream.EventOutputStream;
import common.exception.ConfigurationException;
import junit.framework.Assert;

public class TestEventOutputStream{

	@Test
	public void test_constructor_invalid_arg() {
		boolean flag = false;
		try{
		EventOutputStream<Event,Integer> in = new EventOutputStream<Event,Integer>(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
		EventOutputStream<Event,Integer> in = new EventOutputStream<Event,Integer>(new ArrayList<Integer>(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
			List<Integer> ints = new ArrayList<Integer>(4);
			ints.add(0);
			ints.add(1);
			ints.add(2);
			ints.add(2);
			EventOutputStream<Event,Integer> in = new EventOutputStream<Event,Integer>(ints);
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
		EventOutputStream<Event,Integer> in = new EventOutputStream<Event,Integer>(ints);
	}
	
	@Test
	public void test_write() {
		
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventOutputStream<Event,Integer> out = new EventOutputStream<Event,Integer>(ints);
		
		out.write(0, null);
		out.write(0, new Event());

		
		
		out.write(1, null);
		out.write(1, new Event());		

		
		out.write(2, null);
		out.write(2, new Event());
		
		
		out.write(3, null);
		out.write(3, new Event());
	}
	@Test
	public void test_iterator_key_not_found() {
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventOutputStream<Event,Integer> out = new EventOutputStream<Event,Integer>(ints);
		

		boolean flag = false;
		try{
		out.write(4, new Event());
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		flag = false;
		try{
			out.write(1000, new Event());
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);;
	}
}
