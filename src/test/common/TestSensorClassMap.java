package test.common;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import common.Sensor;
import common.SensorClassMap;
import common.SensorInstance;
import common.Vehicle;
import junit.framework.Assert;

public class TestSensorClassMap {

	@Test
	public void test_constructor() {
		SensorClassMap<SensorInstance> map = new SensorClassMap<SensorInstance>();
	}
	
	@Test
	public void test_addSensorInstance() {
		SensorClassMap<SensorInstance> map = new SensorClassMap<SensorInstance>();
		
		SensorInstance si = new SensorInstance(0,1,new Vehicle(0),16);
		
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(0,1,new Vehicle(1),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(0,1,new Vehicle(2),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		
		si = new SensorInstance(1,1,new Vehicle(0),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(1,1,new Vehicle(1),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(1,1,new Vehicle(2),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(1,1,new Vehicle(3),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(1,0,new Vehicle(0),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(1,0,new Vehicle(1),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		si = new SensorInstance(1,0,new Vehicle(2),16);
		Assert.assertEquals(true,map.addSensorInstance(si));
		Assert.assertEquals(false,map.addSensorInstance(si));
		
		Assert.assertEquals(false,map.addSensorInstance(null));
		
	}
	
	@Test
	public void test_getSensorClasses() {
		SensorClassMap<SensorInstance> map = new SensorClassMap<SensorInstance>();
		Sensor s = null;
		
		SensorInstance si = new SensorInstance(0,1,new Vehicle(0),16);
		map.addSensorInstance(si);
		Iterator<Sensor> it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(new Sensor(0,1),s);
		
		
		si = new SensorInstance(0,1,new Vehicle(1),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(new Sensor(0,1),s);
		
		
		si = new SensorInstance(0,1,new Vehicle(2),16);
		map.addSensorInstance(si);	
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(new Sensor(0,1),s);
		
		si = new SensorInstance(1,1,new Vehicle(0),16);
		map.addSensorInstance(si);
		
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		boolean flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		si = new SensorInstance(1,1,new Vehicle(1),16);
		map.addSensorInstance(si);		
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		
		si = new SensorInstance(1,1,new Vehicle(2),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		si = new SensorInstance(1,1,new Vehicle(3),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		si = new SensorInstance(1,0,new Vehicle(0),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		
		si = new SensorInstance(1,0,new Vehicle(1),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		
		si = new SensorInstance(1,0,new Vehicle(2),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		
		si = new SensorInstance(55,1000,new Vehicle(10),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0)) || s.equals(new Sensor(55,1000));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		si = new SensorInstance(55,1000,new Vehicle(1100),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0)) || s.equals(new Sensor(55,1000));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		si = new SensorInstance(500,100,new Vehicle(2000),16);
		map.addSensorInstance(si);
		it = map.getSensorClasses();
		Assert.assertEquals(true,it.hasNext());
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0)) || s.equals(new Sensor(55,1000)) || s.equals(new Sensor(500,100));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000))|| s.equals(new Sensor(500,100));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000))|| s.equals(new Sensor(500,100));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000))|| s.equals(new Sensor(500,100));
		Assert.assertEquals(true,it.hasNext());
		Assert.assertEquals(true,flag);
		s= it.next();
		flag = s.equals(new Sensor(0,1)) || s.equals(new Sensor(1,1)) || s.equals(new Sensor(1,0))|| s.equals(new Sensor(55,1000))|| s.equals(new Sensor(500,100));
		Assert.assertEquals(false,it.hasNext());
		Assert.assertEquals(true,flag);
		
		
	}

	@Test
	public void test_getSensorInstances() {
		SensorClassMap<SensorInstance> map = new SensorClassMap<SensorInstance>();
		Sensor s = null;
		
		List<SensorInstance> list = null;
		SensorInstance si = new SensorInstance(0,1,new Vehicle(0),16);
		map.addSensorInstance(si);
		list = map.getSensorInstances(new Sensor(0,1));
		Assert.assertEquals(1,list.size());
		Assert.assertEquals(new Sensor(0,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		
		
		si = new SensorInstance(0,1,new Vehicle(1),16);
		map.addSensorInstance(si);
		list = map.getSensorInstances(new Sensor(0,1));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(new Sensor(0,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		Assert.assertEquals(new Sensor(0,1),list.get(1));
		Assert.assertEquals(new Vehicle(1),list.get(1).getVehicle());
		
		si = new SensorInstance(0,1,new Vehicle(2),16);
		map.addSensorInstance(si);	
		list = map.getSensorInstances(new Sensor(0,1));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(new Sensor(0,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		Assert.assertEquals(new Sensor(0,1),list.get(1));
		Assert.assertEquals(new Vehicle(1),list.get(1).getVehicle());
		Assert.assertEquals(new Sensor(0,1),list.get(2));
		Assert.assertEquals(new Vehicle(2),list.get(2).getVehicle());
		
		si = new SensorInstance(1,1,new Vehicle(0),16);
		map.addSensorInstance(si);
		
		//make sure other classes' isntance don't change
		list = map.getSensorInstances(new Sensor(0,1));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(new Sensor(0,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		Assert.assertEquals(new Sensor(0,1),list.get(1));
		Assert.assertEquals(new Vehicle(1),list.get(1).getVehicle());
		Assert.assertEquals(new Sensor(0,1),list.get(2));
		Assert.assertEquals(new Vehicle(2),list.get(2).getVehicle());	
		list = map.getSensorInstances(new Sensor(1,1));
		Assert.assertEquals(1,list.size());
		Assert.assertEquals(new Sensor(1,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		
	
		si = new SensorInstance(1,1,new Vehicle(1),16);
		map.addSensorInstance(si);			
		list = map.getSensorInstances(new Sensor(1,1));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(new Sensor(1,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		Assert.assertEquals(new Sensor(1,1),list.get(1));
		Assert.assertEquals(new Vehicle(1),list.get(1).getVehicle());
		
		si = new SensorInstance(1,1,new Vehicle(2),16);
		map.addSensorInstance(si);
		list = map.getSensorInstances(new Sensor(1,1));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(new Sensor(1,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		Assert.assertEquals(new Sensor(1,1),list.get(1));
		Assert.assertEquals(new Vehicle(1),list.get(1).getVehicle());
		Assert.assertEquals(new Sensor(1,1),list.get(2));
		Assert.assertEquals(new Vehicle(2),list.get(2).getVehicle());
		
		si = new SensorInstance(1,1,new Vehicle(3),16);
		map.addSensorInstance(si);
		list = map.getSensorInstances(new Sensor(1,1));
		Assert.assertEquals(4,list.size());
		Assert.assertEquals(new Sensor(1,1),list.get(0));
		Assert.assertEquals(new Vehicle(0),list.get(0).getVehicle());
		Assert.assertEquals(new Sensor(1,1),list.get(1));
		Assert.assertEquals(new Vehicle(1),list.get(1).getVehicle());
		Assert.assertEquals(new Sensor(1,1),list.get(2));
		Assert.assertEquals(new Vehicle(2),list.get(2).getVehicle());
		Assert.assertEquals(new Sensor(1,1),list.get(2));
		Assert.assertEquals(new Vehicle(3),list.get(3).getVehicle());
		
		
		list = map.getSensorInstances(null);
		Assert.assertEquals(0,list.size());
	}

}
