package test.phase.generation.data;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Noise;
import common.Sensor;
import common.SensorBehavior;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.data.DataGenerationSensor;
import phase.generation.data.DataGenerationVehicle;

public class TestDataGenerationVehicle {

	@Test
	public void test_constructor_illegal_argument() {
		boolean flag = false;
		try{
		DataGenerationVehicle v = new DataGenerationVehicle(-1,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
		DataGenerationVehicle v = new DataGenerationVehicle(0,new ArrayList<DataGenerationSensor>());
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		list.add(s);
		
		
		flag = false;
		try{

			DataGenerationVehicle v = new DataGenerationVehicle(0,list); //duplicate sensors
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		flag = false;
	}

	
	@Test
	public void test_constructor() {
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		list.add(s);
		
		DataGenerationVehicle v = new DataGenerationVehicle(0,list);
	}
	
	@Test
	public void test_setters_illegal_arg() {
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		list.add(s);
		
		DataGenerationVehicle v = new DataGenerationVehicle(0,list); 
		
		list = new ArrayList<DataGenerationSensor>(list);
		s = new DataGenerationSensor(1,3,sb1,sb2);//duplicate sensors
		list.add(s);
		
		
		boolean flag = false;
		try{

			v.setSensors(list);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{

			v.setSensors(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{

			v.setSensors(new ArrayList<DataGenerationSensor>(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
	}
	
	@Test
	public void test_getters_setters() {
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		list.add(s);
		
		DataGenerationVehicle v = new DataGenerationVehicle(0,list);
		List<DataGenerationSensor> actual = v.getSensors();
		
		Assert.assertEquals(list.size(),actual.size());
		for(int i =0;i< list.size();i++){
			Assert.assertEquals(list.get(i),actual.get(i));
		}
		
		list = new ArrayList<DataGenerationSensor>(list);
		list.add(new DataGenerationSensor(1,4,sb1,sb2));
		
		v.setSensors(list);
		actual = v.getSensors();
		
		Assert.assertEquals(list.size(),actual.size());
		for(int i =0;i< list.size();i++){
			Assert.assertEquals(list.get(i),actual.get(i));
		}
		
	}
	
	@Test
	public void test_findSensor() {
List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		list.add(s);
		
		DataGenerationVehicle v = new DataGenerationVehicle(0,list);
		
		Assert.assertEquals(true, list.get(0) == v.findSensor(new Sensor(0,1)));
		Assert.assertEquals(true, list.get(1) == v.findSensor(new Sensor(1,1)));
		Assert.assertEquals(true, list.get(2) == v.findSensor(new Sensor(1,2)));
		Assert.assertEquals(true, list.get(3) == v.findSensor(new Sensor(1,3)));
		Assert.assertEquals(null, v.findSensor(new Sensor(1,4)));
		Assert.assertEquals(null, v.findSensor(new Sensor(50,4)));
		Assert.assertEquals(null, v.findSensor(null));
	}
}
