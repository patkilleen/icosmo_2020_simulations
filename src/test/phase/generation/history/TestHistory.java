package test.phase.generation.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Vehicle;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import phase.generation.history.History;

public class TestHistory {

	@Test
	public void test_constructor_illegal_arg_null(){
		boolean flag = false;

		try{
			History h = new History(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}
	@Test
	public void test_constructor_illegal_arg_empty_keys(){
		boolean flag = false;


		try{
			History<Object,Object> h = new History<Object,Object>(new ArrayList<Object>());
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_duplicate_keys(){

		boolean flag = false;

		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		try{
			History<Vehicle,Object> h = new History<Vehicle,Object>(arg);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_one_key(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		arg.add(new Vehicle(1));
		h = new History<Vehicle,Object>(arg);
	}
	@Test
	public void test_constructor_many_keys(){

		List<Vehicle> arg = new ArrayList<Vehicle>(4);

		arg.add(new Vehicle(2));
		arg.add(new Vehicle(3));
		arg.add(new Vehicle(4));
		arg.add(new Vehicle(5));

		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

	}


	@Test
	public void test_getPartitionKeys(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		List<Vehicle> actual = h.getPartitionKeys();

		Assert.assertEquals(1,actual.size());
		Assert.assertEquals(new Vehicle(0), actual.get(0));

		arg.add(new Vehicle(1));
		h = new History<Vehicle,Object>(arg);

		Assert.assertEquals(2,actual.size());
		Assert.assertEquals(new Vehicle(0), actual.get(0));
		Assert.assertEquals(new Vehicle(1), actual.get(1));

		arg.add(new Vehicle(3));
		arg.add(new Vehicle(4));
		arg.add(new Vehicle(5));
		arg.add(new Vehicle(6));

		h = new History<Vehicle,Object>(arg);

		Assert.assertEquals(6,actual.size());
		Assert.assertEquals(new Vehicle(0), actual.get(0));
		Assert.assertEquals(new Vehicle(1), actual.get(1));
		Assert.assertEquals(new Vehicle(3), actual.get(2));
		Assert.assertEquals(new Vehicle(4), actual.get(3));
		Assert.assertEquals(new Vehicle(5), actual.get(4));
		Assert.assertEquals(new Vehicle(6), actual.get(5));

	}


	@Test
	public void test_elementIterator_empty(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Iterator<Object> it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(512));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(512));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());
	}


	@Test
	public void test_elementIterator_non_found_key(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Iterator<Object> it = h.elementIterator(new Vehicle(2),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(512),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(512),new TimerEvent(512));
		Assert.assertEquals(false,it.hasNext());

	}

	@Test
	public void test_elementIterator_2_calls_and_recordElement(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Object o1 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(0), o1);

		Iterator<Object> it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());


	}

	@Test
	public void test_elementIterator_and_recordElement_many_objects_1_time_unit(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Object o1 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(0), o1);

		Iterator<Object> it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		Object o2 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(0), o2);

		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o2 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		Object o3 = new Object();
		h.recordElement(new Vehicle(1), new TimerEvent(0), o3);
		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o2 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o3 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());

		Object o4 = new Object();
		h.recordElement(new Vehicle(1), new TimerEvent(0), o4);

		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o2 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o3 == it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o4 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());

	}
	
	@Test
	public void test_recordElement_key_not_found(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Object o1 = new Object();
		
		boolean flag = false;
		
		try{
		h.recordElement(new Vehicle(2), new TimerEvent(0), o1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);

	}
	@Test
	public void test_elementIterator_and_recordElement_one_object_many_time_units(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Object o1 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(0), o1);

		Iterator<Object> it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		Object o2 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(1), o2);

		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());
		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o2 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(0),new TimerEvent(2));
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(false,it.hasNext());

		Object o3 = new Object();
		h.recordElement(new Vehicle(1), new TimerEvent(0), o3);

		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());
		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o2 == it.next());
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o3 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(1));
		Assert.assertEquals(false,it.hasNext());

		Object o4 = new Object();
		h.recordElement(new Vehicle(1), new TimerEvent(1), o4);
		it = h.elementIterator(new Vehicle(0),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o1 == it.next());
		Assert.assertEquals(false,it.hasNext());
		it = h.elementIterator(new Vehicle(0),new TimerEvent(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o2 == it.next());
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o3 == it.next());
		Assert.assertEquals(false,it.hasNext());
		it = h.elementIterator(new Vehicle(1),new TimerEvent(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o4 == it.next());
		Assert.assertEquals(false,it.hasNext());
	}

	@Test
	public void test_elementIterator_and_recordElement_many_object_many_time_units(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(0), o1);
		h.recordElement(new Vehicle(0), new TimerEvent(0), o2);
		h.recordElement(new Vehicle(0), new TimerEvent(1), o3);
		h.recordElement(new Vehicle(0), new TimerEvent(1), o4);

		Object o5 = new Object();
		Object o6 = new Object();
		Object o7 = new Object();
		Object o8 = new Object();
		h.recordElement(new Vehicle(1), new TimerEvent(0), o5);
		h.recordElement(new Vehicle(1), new TimerEvent(0), o6);
		h.recordElement(new Vehicle(1), new TimerEvent(1), o7);
		h.recordElement(new Vehicle(1), new TimerEvent(1), o8);

		Iterator<Object> it = h.elementIterator(new Vehicle(1),new TimerEvent(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o5 == it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o6 == it.next());
		Assert.assertEquals(false,it.hasNext());

		it = h.elementIterator(new Vehicle(1),new TimerEvent(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o7 == it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,o8 == it.next());
		Assert.assertEquals(false,it.hasNext());


		it = h.elementIterator(new Vehicle(0),new TimerEvent(3));
		Assert.assertEquals(false,it.hasNext());

	}

	@Test
	public void test_getElements_illegal_arg_null_key(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		boolean flag = false;

		try{
			h.getElements(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

	}

	@Test
	public void test_getElements_illegal_arg_non_existant_key(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		boolean flag = false;

		try{
			h.getElements(new Vehicle(5));
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

	}

	@Test
	public void test_getElements_empty(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		List<Object> actual = h.getElements(new Vehicle(0));

		Assert.assertEquals(0, actual.size());

	}

	@Test
	public void test_getElements(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
		h.recordElement(new Vehicle(0), new TimerEvent(0), o1);
		h.recordElement(new Vehicle(0), new TimerEvent(0), o2);
		h.recordElement(new Vehicle(0), new TimerEvent(1), o3);
		h.recordElement(new Vehicle(0), new TimerEvent(1), o4);

		Object o5 = new Object();
		Object o6 = new Object();
		Object o7 = new Object();
		Object o8 = new Object();
		h.recordElement(new Vehicle(1), new TimerEvent(0), o5);
		h.recordElement(new Vehicle(1), new TimerEvent(0), o6);
		h.recordElement(new Vehicle(1), new TimerEvent(1), o7);
		h.recordElement(new Vehicle(1), new TimerEvent(1), o8);

		List<Object> actual = h.getElements(new Vehicle(0));

		Assert.assertEquals(4,actual.size());
		
		Assert.assertEquals(true,o1 == actual.get(0) || o3 == actual.get(0));
		
		if(o1 == actual.get(0)){
			Assert.assertEquals(true,o2 == actual.get(1));
			Assert.assertEquals(true,o3 == actual.get(2));
			Assert.assertEquals(true,o4 == actual.get(3));
		}else{
			Assert.assertEquals(true,o3 == actual.get(0));
			Assert.assertEquals(true,o4 == actual.get(1));
			Assert.assertEquals(true,o1 == actual.get(2));
			Assert.assertEquals(true,o2 == actual.get(3));
		}
	
		actual = h.getElements(new Vehicle(1));

		Assert.assertEquals(4,actual.size());
		if(o5 == actual.get(0)){
			Assert.assertEquals(true,o6 == actual.get(1));
			Assert.assertEquals(true,o7 == actual.get(2));
			Assert.assertEquals(true,o8 == actual.get(3));
		}else{
			Assert.assertEquals(true,o7 == actual.get(0));
			Assert.assertEquals(true,o8 == actual.get(1));
			Assert.assertEquals(true,o5 == actual.get(2));
			Assert.assertEquals(true,o6 == actual.get(3));
		}

	}

	@Test
	public void test_timerEventIterator_empty(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);
		Iterator<TimerEvent> it = h.timerEventIterator();

		Assert.assertEquals(false,it.hasNext());

	}


	@Test
	public void test_timerEventIterator_no_arg(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);



		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());

		Iterator<TimerEvent> it = h.timerEventIterator();

		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());

		Assert.assertEquals(false,it.hasNext());

		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(2), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(3), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(2), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(3), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());

		it = h.timerEventIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());
		Assert.assertEquals(1,it.next().getTime());
		Assert.assertEquals(2,it.next().getTime());
		Assert.assertEquals(3,it.next().getTime());
		Assert.assertEquals(false,it.hasNext());


	}


	@Test
	public void test_timerEventIterator_no_arg_out_order_time(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);



		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());

		Iterator<TimerEvent> it = h.timerEventIterator();

		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());

		Assert.assertEquals(false,it.hasNext());

		h.recordElement(new Vehicle(0), new TimerEvent(3), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(2), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(3), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(2), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());

		it = h.timerEventIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());
		Assert.assertEquals(1,it.next().getTime());
		Assert.assertEquals(2,it.next().getTime());
		Assert.assertEquals(3,it.next().getTime());
		Assert.assertEquals(false,it.hasNext());


	}

	@Test
	public void test_timerEventIterator_with_arg(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);



		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());

		Iterator<TimerEvent> it = h.timerEventIterator(new Vehicle(0));

		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());

		Assert.assertEquals(false,it.hasNext());

		it = h.timerEventIterator(new Vehicle(1));

		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(5,it.next().getTime());

		Assert.assertEquals(false,it.hasNext());

		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(2), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(3), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(6), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(7), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(8), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());

		it = h.timerEventIterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());
		Assert.assertEquals(1,it.next().getTime());
		Assert.assertEquals(2,it.next().getTime());
		Assert.assertEquals(3,it.next().getTime());
		Assert.assertEquals(false,it.hasNext());

		it = h.timerEventIterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(5,it.next().getTime());
		Assert.assertEquals(6,it.next().getTime());
		Assert.assertEquals(7,it.next().getTime());
		Assert.assertEquals(8,it.next().getTime());
		Assert.assertEquals(false,it.hasNext());

	}

	@Test
	public void test_timerEventIterator_with_arg_out_order_time(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);



		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());

		Iterator<TimerEvent> it = h.timerEventIterator(new Vehicle(0));

		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());

		Assert.assertEquals(false,it.hasNext());

		it = h.timerEventIterator(new Vehicle(1));

		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(5,it.next().getTime());

		Assert.assertEquals(false,it.hasNext());

		h.recordElement(new Vehicle(0), new TimerEvent(3), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(2), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(8), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(7), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(6), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(5), new Object());

		it = h.timerEventIterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0,it.next().getTime());
		Assert.assertEquals(1,it.next().getTime());
		Assert.assertEquals(2,it.next().getTime());
		Assert.assertEquals(3,it.next().getTime());
		Assert.assertEquals(false,it.hasNext());

		it = h.timerEventIterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(5,it.next().getTime());
		Assert.assertEquals(6,it.next().getTime());
		Assert.assertEquals(7,it.next().getTime());
		Assert.assertEquals(8,it.next().getTime());
		Assert.assertEquals(false,it.hasNext());

	}

	@Test
	public void test_integrityCheck_empty(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);
		h.integrityCheck();

	}

	public void test_integrityCheck_1_key(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);
		h.integrityCheck();

		h.recordElement(new Vehicle(0), new TimerEvent(512), new Object());
		h.integrityCheck();
		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.integrityCheck();
		h.recordElement(new Vehicle(0), new TimerEvent(15), new Object());
		h.integrityCheck();
	}

	@Test
	public void test_integrityCheck_1_time(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.integrityCheck();

		h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(512), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(512), new Object());
		h.integrityCheck();

	}

	@Test
	public void test_integrityCheck_2_time(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(1), new Object());
		h.integrityCheck();

		h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(64), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(64), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(32), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(32), new Object());
		h.integrityCheck();

	}

	@Test
	public void test_integrityCheck_fail(){


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(1), new Object());



		boolean flag = false;

		try{
			h.integrityCheck();
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(4), new Object());



		flag = false;

		try{
			h.integrityCheck();
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		h = new History<Vehicle,Object>(arg);

		h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(0), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(0), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(1), new Object());
		h.recordElement(new Vehicle(1), new TimerEvent(3), new Object());



		flag = false;

		try{
			h.integrityCheck();
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		h = new History<Vehicle,Object>(arg);
	}

	@Test
	public void test_recordElement_illegal_arg_null(){
		boolean flag = false;
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		try{

			History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

			//h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
			h.recordElement(new Vehicle(0), new TimerEvent(0), null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{

			History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

			//h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
			h.recordElement(new Vehicle(0), null, new Object());
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{

			History<Vehicle,Object> h = new History<Vehicle,Object>(arg);

			//h.recordElement(new Vehicle(0), new TimerEvent(0), new Object());
			h.recordElement(null, null, new Object());
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

	}
	public static class Object extends java.lang.Object implements Serializable{
		public Object(){
			
		}
	}
}
