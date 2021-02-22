package test.common;

import java.util.Iterator;

import org.junit.Test;
import junit.framework.Assert;
import common.Sensor;
import common.SensorInstance;
import common.Vehicle;
import common.exception.ConfigurationException;
import phase.generation.cosmo.COSMOSensorInstance;

public class TestSensorInstance {


	@Test
	public void test_contructor_invalid_id() {
		boolean flag = false;
		
		try{
			Sensor s = new Sensor(-1,321);
			SensorInstance si = new SensorInstance(s,null,16);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
flag = false;
		
		try{
			Sensor s = new Sensor(-1,321);
			SensorInstance si = new SensorInstance(null,null,16);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			Sensor s = new Sensor(-1,321);
			SensorInstance si = new SensorInstance(null,new Vehicle(1),16);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{

			SensorInstance si = new SensorInstance(-1,2,new Vehicle(1),16);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{

			SensorInstance si = new SensorInstance(-1000,-2,new Vehicle(1),16);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
		
		flag = false;
		
		try{

			SensorInstance si = new SensorInstance(1,-2,new Vehicle(1),16);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{

			SensorInstance si = new SensorInstance(1,1,new Vehicle(1),0);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{

			SensorInstance si = new SensorInstance(1,1,new Vehicle(1),-1);
		}catch(ConfigurationException e){
			flag = true;
		}
	
		Assert.assertEquals(true,flag);
	}
	@Test
	public void testSettersGetters() {
		SensorInstance si = new SensorInstance(1,4,new Vehicle(2),16);
		Assert.assertEquals(1,si.getPgn());
		Assert.assertEquals(4,si.getSpn());
		Assert.assertEquals(new Vehicle(2),si.getVehicle());
	}
	

	@Test
	public void testZvalueIterator_and_addZValue() {
		SensorInstance i= new SensorInstance(new Sensor(1,2),new Vehicle(0),5);
		
		Iterator<Double> it = i.zvalueIterator();
		Assert.assertEquals(false,it.hasNext());
		
		i.addZvalue(0.0);
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0.0,(Double)it.next());
		Assert.assertEquals(false,it.hasNext());
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0.0,it.next());
		Assert.assertEquals(false,it.hasNext());
		
		i.addZvalue(1.0);
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0.0,it.next());
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(1.0,it.next());
		Assert.assertEquals(false,it.hasNext());
		
		i.addZvalue(2.0);
		i.addZvalue(3.0);
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0.0,it.next());
		Assert.assertEquals(1.0,it.next());
		Assert.assertEquals(2.0,it.next());
		Assert.assertEquals(3.0,it.next());
		Assert.assertEquals(false,it.hasNext());
		
		
		//reaching queue limit here
		i.addZvalue(4.0);
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0.0,it.next());
		Assert.assertEquals(1.0,it.next());
		Assert.assertEquals(2.0,it.next());
		Assert.assertEquals(3.0,it.next());
		Assert.assertEquals(4.0,it.next());
		Assert.assertEquals(false,it.hasNext());
		
		
		//reached queue limit here, the window moves by one, dopping 1st entry
				i.addZvalue(5.0);
				
				it = i.zvalueIterator();
				Assert.assertEquals(true,it.hasNext());
				Assert.assertEquals(1.0,it.next());
				Assert.assertEquals(2.0,it.next());
				Assert.assertEquals(3.0,it.next());
				Assert.assertEquals(4.0,it.next());
				Assert.assertEquals(5.0,it.next());
				Assert.assertEquals(false,it.hasNext());

				i.addZvalue(6.0);
				
				it = i.zvalueIterator();
				Assert.assertEquals(true,it.hasNext());
				Assert.assertEquals(2.0,it.next());
				Assert.assertEquals(3.0,it.next());
				Assert.assertEquals(4.0,it.next());
				Assert.assertEquals(5.0,it.next());
				Assert.assertEquals(6.0,it.next());
				Assert.assertEquals(false,it.hasNext());

	}

	
	@Test
	public void testZvalueIterator_and_addZValue_window_size_1() {
		SensorInstance i= new SensorInstance(new Sensor(1,2),new Vehicle(0),1);
		
		Iterator<Double> it = i.zvalueIterator();
		Assert.assertEquals(false,it.hasNext());
		
		i.addZvalue(0.0);
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(0.0,it.next());
		Assert.assertEquals(false,it.hasNext());
		
		i.addZvalue(1.0);
		
		it = i.zvalueIterator();
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(1.0,it.next());
		Assert.assertEquals(false,it.hasNext());
	}

}
