package test.common.synchronization;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.event.Event;
import common.event.stream.EventInputStream;
import common.event.stream.EventOutputStream;
import common.event.stream.StreamManager;
import common.exception.ConfigurationException;
import common.synchronization.MultiThreadedTimer;
import common.synchronization.StreamTimer;

public class TestStreamTimer extends StreamTimer {

	

	@Test
	public void test_constructor_illegal_arg() {
		boolean flag = false;
		
		try{
			StreamTimer t = new StreamTimer(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			StreamTimer t = new StreamTimer(new StreamManager[0]);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_nextState() {
		List<Integer> ints = new ArrayList<Integer>(4);
		ints.add(0);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		EventInputStream<Event,Integer> in = new EventInputStream<Event,Integer>(ints);
		EventOutputStream<Event,Integer> out = new EventOutputStream<Event,Integer>(ints);
		StreamManager [] sms = new StreamManager[4];
		sms[0]= new TestStreamManager(in,out);
		sms[1]= new TestStreamManager(in,out);
		sms[2]= new TestStreamManager(in,out);
		sms[3]= new TestStreamManager(in,out);
		
		TestStreamTimer t = new TestStreamTimer();
		t.init(sms);

		TestStreamManager expected = (TestStreamManager)sms[0];
		for(int i = 0;i< sms.length;i++){
			TestStreamManager tmp = (TestStreamManager)sms[i];
			tmp.expected = expected;
		}
		
		t.nextState();
		
		expected = (TestStreamManager)sms[1];
		for(int i = 0;i< sms.length;i++){
			TestStreamManager tmp = (TestStreamManager)sms[i];
			tmp.expected = expected;
		}
		
		t.nextState();
		
		expected = (TestStreamManager)sms[2];
		for(int i = 0;i< sms.length;i++){
			TestStreamManager tmp = (TestStreamManager)sms[i];
			tmp.expected = expected;
		}
		
		t.nextState();
		
		expected = (TestStreamManager)sms[3];
		for(int i = 0;i< sms.length;i++){
			TestStreamManager tmp = (TestStreamManager)sms[i];
			tmp.expected = expected;
		}
		
		t.nextState();
		
		expected = (TestStreamManager)sms[0];
		for(int i = 0;i< sms.length;i++){
			TestStreamManager tmp = (TestStreamManager)sms[i];
			tmp.expected = expected;
		}
		
		t.nextState();
		
		expected = (TestStreamManager)sms[1];
		for(int i = 0;i< sms.length;i++){
			TestStreamManager tmp = (TestStreamManager)sms[i];
			tmp.expected = expected;
		}
		
		t.nextState();
	}
	
	private static class TestStreamManager extends StreamManager{
		public TestStreamManager(EventInputStream inStream, EventOutputStream outStream) {
			super(inStream, outStream);
			// TODO Auto-generated constructor stub
		}
		
		public TestStreamManager expected;
		
		@Override
		public void flush(){
			if(this != expected){
				fail("stream timer failed to call right stream manager");
			}
		}
	}
}
