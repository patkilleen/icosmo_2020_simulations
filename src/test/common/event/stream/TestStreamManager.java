package test.common.event.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.Sensor;
import common.SensorMap;
import common.Vehicle;
import common.event.Event;
import common.event.stream.EventInputStream;
import common.event.stream.EventOutputStream;
import common.event.stream.StreamManager;
import common.exception.ConfigurationException;
import junit.framework.Assert;


public class TestStreamManager{

	@Test
	public void test_constructor_invalid_arg() {
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(ints);
		EventOutputStream<Event,Integer> out = new EventOutputStream<Event,Integer>(ints);

		boolean flag = false;
		try{
			StreamManager<Event,Integer> sm = new StreamManager<Event,Integer>(null,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

		flag = false;
		try{

			StreamManager<Event,Integer> sm = new StreamManager<Event,Integer>(in,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true, flag);

		flag = false;
		try{

			StreamManager<Event,Integer> sm = new StreamManager<Event,Integer>(null,out);
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
		EventOutputStream<Event,Integer> out = new EventOutputStream<Event,Integer>(ints);

		StreamManager<Event,Integer> sm = new StreamManager<Event,Integer>(in,out);
	}

	@Test
	public void test_flush() {

		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventOutputStream<Event,Integer> out = new EventOutputStream<Event,Integer>(ints);
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(ints);
		StreamManager<Event,Integer> sm = new StreamManager<Event,Integer>(in,out);
		int size = 4;

		HashMap<Integer,List<Event>> map = new  HashMap<Integer,List<Event>>();

		for(int key = 0; key < ints.size();key++){
			List<Event> events = new ArrayList<Event>(size);
			for(int i = 0;i<size;i++){
				Event e = new Event();
				events.add(e);
				out.write(key,e);
			}
			map.put(key, events);
		}
		
		//flush, then make sure all there
		sm.flush();
		
		for(int key = 0; key < ints.size();key++){
			Iterator<Event> expected = map.get(key).iterator();
			Iterator<Event> actual = in.iterator(key);
			while(actual.hasNext()){
				if(!expected.hasNext()){
					String output = "";
					do{
						output+= actual.next().toString()+",";
					}while(actual.hasNext());
					Assert.fail("failed to flush all to input, or get the iterator of all objects,remaining: "+output);
				}
				Assert.assertEquals(true, expected.next() == actual.next());
				
			}
			
			if(expected.hasNext()){
				String output = "";
				do{
					output+= expected.next().toString()+",";
				}while(expected.hasNext());
				
				Assert.fail("failed to flush all to input, or get the iterator of all objects, remaining: "+output);
			}
		}
		
	}
	@Test
	public void testMultiThreadedAccess () throws InterruptedException{
		for(int iterations = 0; iterations < 4; iterations++){
			
			List<Integer> ints = new ArrayList<Integer>(4);
			ints.add(0);
			ints.add(1);
			ints.add(2);
			ints.add(3);
			EventOutputStream<TestEvent,Integer> out = new EventOutputStream<TestEvent,Integer>(ints);
			EventInputStream<TestEvent,Integer> in = new EventInputStream<TestEvent,Integer>(ints);
			StreamManager<TestEvent,Integer> sm = new StreamManager<TestEvent,Integer>(in,out);
			
			Thread[] threads = new Thread[ints.size()];
			List<TestWriteWorker> writeWorkers = new ArrayList<TestWriteWorker>(16);
			//simulate a vehicle in seperate threads
			for(int i = 0;i < ints.size();i++){

				TestWriteWorker helper = new TestWriteWorker();
				writeWorkers.add(helper);
				helper.key = ints.get(i);
				helper.out = out;
				Thread t = new Thread(helper);
				threads[i] = t;
				t.start();

			}//end iterate vehicles

			//wait for all simulated vehicle to finish this time-tick's simulation
			for(Thread t: threads){
				t.join();
			}
			
			sm.flush();
			
			for(TestWriteWorker w: writeWorkers){
				Assert.assertEquals(false,w.fail);
			}
			
			List<TestReadWorker> readWorkers = new ArrayList<TestReadWorker>(16);
			
			//simulate a vehicle in seperate threads
			for(int i = 0;i < ints.size();i++){

				TestReadWorker helper = new TestReadWorker();
				readWorkers.add(helper);
				helper.key = ints.get(i);
				helper.in = in;
				Thread t = new Thread(helper);
				threads[i] = t;
				t.start();

			}//end iterate vehicles

			//wait for all simulated vehicle to finish this time-tick's simulation
			for(Thread t: threads){
				t.join();

			}
			for(TestReadWorker w: readWorkers){
				Assert.assertEquals(false,w.fail);
			}
			
		}
	}
	
	private static class TestEvent extends Event{
		public int  counter;
		public TestEvent(int c){
			super();
			counter = c;
		}
	}
	
	private static class TestWriteWorker implements Runnable{
		public static final int COUNT_MAX = 1024;
		public Integer key;
		public EventOutputStream<TestEvent,Integer> out;
		public boolean fail = false;
		
		public void run(){
			try{
			for(int i = 0;i< COUNT_MAX;i++){
				out.write(key, new TestEvent(i));
			}
			}catch(Exception e){
				fail = true;
			}
			
		}
	}
	
	private static class TestReadWorker implements Runnable{
		public static final int COUNT_MAX = 1024;
		public Integer key;
		public EventInputStream<TestEvent,Integer> in;
		public boolean fail = false;
		public void run(){
		try{
			Iterator<TestEvent> it = in.iterator(key);
			for(int i = 0;i< COUNT_MAX;i++){
				TestEvent te = it.next();
				Assert.assertEquals(i, te.counter);
			}
		}catch(Exception e){
			fail = true;
		}
			
		}
	}
}
