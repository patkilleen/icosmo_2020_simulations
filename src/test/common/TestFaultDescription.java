package test.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.exception.ConfigurationException;
import junit.framework.Assert;

public class TestFaultDescription {

	@Test
	public void test_constructor() {
		FaultDescription f = new FaultDescription(-1,null);
		f = new FaultDescription(-1000,"");
		f = new FaultDescription(0,"abc");
		f = new FaultDescription(1,"abc");
		f = new FaultDescription(1000,"abc");
	}
	
	@Test
	public void test_settersGetters_affectedSensors() {
		FaultDescription f = new FaultDescription(-1,null);
		Assert.assertEquals(0,f.numberOfAffectedSensors());
		f.addAffectedSensor(null);
		Assert.assertEquals(1,f.numberOfAffectedSensors());
		
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,2);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		f.addAffectedSensor(sb);
		
		Assert.assertEquals(2,f.numberOfAffectedSensors());
		
		Assert.assertEquals(null,f.getAffectedSensor(0));
		Assert.assertEquals(true,sb == f.getAffectedSensor(1));
		
	}
	
	@Test
	public void test_settersGetters_id() {
		FaultDescription f = new FaultDescription(-1,null);
		Assert.assertEquals(-1,f.getId());
		
		f = new FaultDescription(-1000,null);
		Assert.assertEquals(-1000,f.getId());
		
		f = new FaultDescription(0,null);
		Assert.assertEquals(0,f.getId());
		
		f = new FaultDescription(1,null);
		Assert.assertEquals(1,f.getId());
		
		f = new FaultDescription(1000,null);
		Assert.assertEquals(1000,f.getId());
		
		f.setId(-1);
		Assert.assertEquals(-1,f.getId());
		
		f.setId(-100);
		Assert.assertEquals(-100,f.getId());
		
		f.setId(100);
		Assert.assertEquals(100,f.getId());
	}

	@Test
	public void test_settersGetters_label() {
		FaultDescription f = new FaultDescription(-1,null);
		Assert.assertEquals(null,f.getLabel());
		
		f.setLabel(null);
		Assert.assertEquals(null,f.getLabel());
		
		f.setLabel("");
		Assert.assertEquals("",f.getLabel());
		
		f.setLabel("a");
		Assert.assertEquals("a",f.getLabel());
		
		f.setLabel("abcd");
		Assert.assertEquals("abcd",f.getLabel());
	}
	
	@Test
	public void test_getNonFaultInvolvedSensors_illegal_state() {
		FaultDescription f = new FaultDescription(-1,null);
	
		Assert.assertEquals(0,f.getNonFaultInvolvedSensors().size());
		
	}
	
	@Test
	public void test_init() {
		FaultDescription f = new FaultDescription(-1,null);
		f.init(null);
		f.init(new ArrayList<Sensor>(0));
		
		
	}
	@Test
	public void test_getNonFaultInvolvedSensors() {
		FaultDescription f = new FaultDescription(-1,null);
		List<Sensor> list = new ArrayList<Sensor>(4);
		list.add(new Sensor(0,0));
		
		f.init(list);
		
		List<Sensor> actual = f.getNonFaultInvolvedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(0,0), actual.get(0));
		
		actual.clear();
		
		list.add(new Sensor(1,0));
		
		f.init(list);
		
		actual = f.getNonFaultInvolvedSensors();
		Assert.assertEquals(2, actual.size());
		Assert.assertEquals(new Sensor(0,0), actual.get(0));
		Assert.assertEquals(new Sensor(1,0), actual.get(1));
		actual.clear();

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,0);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		
		
		//now add fault involved sensors
		f.addAffectedSensor(sb);
		f.init(list);
		
		actual = f.getNonFaultInvolvedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(0,0), actual.get(0));
		actual.clear();
		
		s = new Sensor(1,1);
		sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		f.addAffectedSensor(sb);
		
		f.init(list);
		
		actual = f.getNonFaultInvolvedSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(0,0), actual.get(0));
		
		actual.clear();
		s = new Sensor(0,0);
		sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		f.addAffectedSensor(sb);
		
		f.init(list);
		
		actual = f.getNonFaultInvolvedSensors();
		Assert.assertEquals(0, actual.size());	
		
	}
	
	@Test
	public void test_getSensors() {
		FaultDescription f = new FaultDescription(-1,null);
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,0);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		
		List<Sensor> actual = f.getSensors();
		Assert.assertEquals(0, actual.size());
		
		f.addAffectedSensor(sb);
		
		actual = f.getSensors();
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(new Sensor(1,0), actual.get(0));
		
		s = new Sensor(1,1);
		sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		
		actual = f.getSensors();
		Assert.assertEquals(1, actual.size());
		
		f.addAffectedSensor(sb);
		
		actual = f.getSensors();
		Assert.assertEquals(2, actual.size());
		Assert.assertEquals(new Sensor(1,0), actual.get(0));
		Assert.assertEquals(new Sensor(1,1), actual.get(1));
		
	}
}
