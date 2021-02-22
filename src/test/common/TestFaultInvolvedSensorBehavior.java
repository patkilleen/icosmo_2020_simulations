package test.common;

import static org.junit.Assert.*;

import org.junit.Test;

import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.SensorBehavior;
import junit.framework.Assert;
import common.FaultInvolvedSensorBehavior.Type;

public class TestFaultInvolvedSensorBehavior {

	@Test
	public void test_constructor1() {
		//public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double []noisePValues,Type aAffectedBehaviorType, Sensor aAffectedSensor)
		
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,2);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		
		
	}
	
	@Test
	public void test_constructor2() {
		//public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double []noisePValues,Type aAffectedBehaviorType, Sensor aAffectedSensor)
		
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,2);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues[0],noisePValues[1],t,s);
		
		
	}
	
	@Test
	public void test_constructor1_invalid_argument() {
		boolean flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
			Sensor s = new Sensor(1,2);
			FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,null);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	
		//public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double []noisePValues,Type aAffectedBehaviorType, Sensor aAffectedSensor)
		
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
			
		
	}

	@Test
	public void test_constructor2_invalid_argument() {
		boolean flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			double[] noisePValues = new double[2];
			noisePValues[0] = 0;
			noisePValues[1] = 1;
			FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
			Sensor s = new Sensor(1,2);
			FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues[0],noisePValues[1],t,null);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	
		//public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double []noisePValues,Type aAffectedBehaviorType, Sensor aAffectedSensor)
		
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
			
		
	}
	
	@Test
	public void test_GettersSetters() {
		//public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double []noisePValues,Type aAffectedBehaviorType, Sensor aAffectedSensor)
		
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,2);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
		
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.MODIFY,sb.getAffectedBehaviorType());
		Assert.assertEquals(true,sb.isModifyType());
		sb.setAffectedBehaviorType(FaultInvolvedSensorBehavior.Type.NEW);
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,sb.getAffectedBehaviorType());
		Assert.assertEquals(true,sb.isNewType());
		
		sb.setModifyType();
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.MODIFY,sb.getAffectedBehaviorType());
		Assert.assertEquals(true,sb.isModifyType());
		
		sb.setNewType();
		Assert.assertEquals(s,sb.getAffectedSensor());
		
		Assert.assertEquals(FaultInvolvedSensorBehavior.Type.NEW,sb.getAffectedBehaviorType());
		Assert.assertEquals(true,sb.isNewType());
		
		s = new Sensor(1,5);
		sb.setAffectedSensor(s);
		Assert.assertEquals(s,sb.getAffectedSensor());
		
	}
}
