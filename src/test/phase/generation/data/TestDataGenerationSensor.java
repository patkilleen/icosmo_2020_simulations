package test.phase.generation.data;

import org.junit.Assert;
import org.junit.Test;

import common.Noise;
import common.SensorBehavior;
import common.exception.ConfigurationException;
import phase.generation.data.DataGenerationSensor;

public class TestDataGenerationSensor {

	@Test
	public void test_constructor() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,null);
		 s = new DataGenerationSensor(0,1,sb1,sb2);
		
	}
	

	@Test
	public void test_constructor_illegal_argument() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		
		boolean flag = false;
		try{
		DataGenerationSensor s = new DataGenerationSensor(0,1,null,sb2);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			DataGenerationSensor s = new DataGenerationSensor(0,1,null,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		
	}

	@Test
	public void test_getters_setters() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);
		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		Assert.assertEquals(true,sb1 == s.getNormalBehavior());
		Assert.assertEquals(true,sb2 == s.getFaultInvolvedBehavior());
		
		s = new DataGenerationSensor(0,1,sb1,null);
		Assert.assertEquals(true,sb1 == s.getNormalBehavior());
		Assert.assertEquals(null,s.getFaultInvolvedBehavior());
		
		s.setFaultInvolvedBehavior(sb1);
		s.setNormalBehavior(sb2);
		
		Assert.assertEquals(true,sb2 == s.getNormalBehavior());
		Assert.assertEquals(true,sb1 == s.getFaultInvolvedBehavior());
		
	}
	
	@Test
	public void test_status() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior normBehavior = new SensorBehavior(n,1,2,0,1);
		SensorBehavior faultBehavior = new SensorBehavior(n,3,4,0,1);
		DataGenerationSensor s = new DataGenerationSensor(0,1,normBehavior,faultBehavior);
		Assert.assertEquals(true,s.isNormal());
		Assert.assertEquals(false,s.isFaultInvolved());
		Assert.assertEquals(true,normBehavior == s.getCurrentBehavior());
		
		s.setFaultInvolved();
		Assert.assertEquals(false,s.isNormal());
		Assert.assertEquals(true,s.isFaultInvolved());
		Assert.assertEquals(true,faultBehavior == s.getCurrentBehavior());
		
		s.setNormal();
		
		Assert.assertEquals(true,s.isNormal());
		Assert.assertEquals(false,s.isFaultInvolved());
		Assert.assertEquals(true,normBehavior == s.getCurrentBehavior());
		
		
	}
	

	@Test
	public void test_getCurrentBehavior() {
		double error = 0.0001;
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior normBehavior = new SensorBehavior(n,1,2,0,1);
		SensorBehavior faultBehavior = new SensorBehavior(n,3,4,0,1);
		DataGenerationSensor s = new DataGenerationSensor(0,1,normBehavior,faultBehavior);
		Assert.assertEquals(1.0,s.getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(2.0,s.getCurrentBehavior().getNoiseAmpFactor(),error);

		
		s.setFaultInvolved();
		Assert.assertEquals(false,s.isNormal());
		Assert.assertEquals(true,s.isFaultInvolved());
		Assert.assertEquals(true,faultBehavior == s.getCurrentBehavior());
		
		Assert.assertEquals(3.0,s.getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(4.0,s.getCurrentBehavior().getNoiseAmpFactor(),error);
		
		s.setNormal();
		
		Assert.assertEquals(true,s.isNormal());
		Assert.assertEquals(false,s.isFaultInvolved());
		Assert.assertEquals(true,normBehavior == s.getCurrentBehavior()); 
		
		Assert.assertEquals(1.0,s.getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(2.0,s.getCurrentBehavior().getNoiseAmpFactor(),error);
		
		
	}
}
