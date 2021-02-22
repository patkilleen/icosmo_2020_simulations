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

public class TestEventInputStream{

	@Test
	public void test_constructor_invalid_arg() {
		boolean flag = false;
		try{
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(new ArrayList<Integer>(0));
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
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(ints);
	}
	
	@Test
	public void test_iterator() {
		
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(ints);
		
		Iterator<Event> it = in.iterator(0);
		Assert.assertEquals(false, it.hasNext());
		
		it = in.iterator(1);
		Assert.assertEquals(false, it.hasNext());
		
		 it = in.iterator(2);
		Assert.assertEquals(false, it.hasNext());
		
		 it = in.iterator(3);
		Assert.assertEquals(false, it.hasNext());
	}
	@Test
	public void test_iterator_key_not_found() {
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(ints);
		boolean flag = false;
		try{
		Iterator<Event> it = in.iterator(4);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		flag = false;
		try{
		Iterator<Event> it = in.iterator(4);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);;
	}
}
