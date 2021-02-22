package test.common;

import org.junit.Test;

import common.Noise;
import common.SensorBehavior;
import junit.framework.Assert;

public class TestSensorBehavior {


	
	@Test
	public void test_constructor1_invalid_arguments(){

		/*public SensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double [] noisePValues)
		{*/
		
		boolean flag = false;
		
		try{
			SensorBehavior sb = new SensorBehavior(null,0,0,null);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			SensorBehavior sb = new SensorBehavior(null,0,0,new double[2]);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,null);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			
			double []pvalues = new double[2];
			pvalues[0]=-1;
			pvalues[1]= 1000;
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,pvalues);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			
			double []pvalues = new double[0];
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,pvalues);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			
			double []pvalues = new double[1];
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,pvalues);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			
			double []pvalues = new double[3];
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,pvalues);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			
			double []pvalues = new double[1000];
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,pvalues);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	}
	
	
	@Test
	public void test_constructor2_invalid_arguments(){

		/*
		public SensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double noisePValue1, double noisePValue2) {*/
		
		boolean flag = false;
		
		try{
			SensorBehavior sb = new SensorBehavior(null,0,0,0,0);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
flag = false;
		
		try{
			SensorBehavior sb = new SensorBehavior(null,0,0,-1,0);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,-1,0);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,0,2);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,-1,1);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
	flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,-1000,0);
		}catch(Exception e){
			flag = true;
		}
		
		
	flag = false;
		
		try{
			Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
			SensorBehavior sb = new SensorBehavior(n,0,0,0,1000);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	}
	
	@Test
	public void test_constructor(){
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb = new SensorBehavior(n,0,0,0,1);
		sb = new SensorBehavior(n,0,0,0.9,0.4);
		sb = new SensorBehavior(n,0,0,0.4,0.9);
		sb = new SensorBehavior(n,0,0,1,0);
		
		double []pvalues = new double[2];
		pvalues[0]=0;
		pvalues[1]= 1;
		sb = new SensorBehavior(n,0,0,pvalues);
		pvalues[0]=1;
		pvalues[1]= 0;
		sb = new SensorBehavior(n,0,0,pvalues);
		
		pvalues[0]=0.5;
		pvalues[1]= 0.4;
		sb = new SensorBehavior(n,0,0,pvalues);
		
		pvalues[0]=0.5;
		pvalues[1]= 0.5;
		sb = new SensorBehavior(n,0,0,pvalues);
		
		pvalues[0]=0.9;
		pvalues[1]= 0.4;
		sb = new SensorBehavior(n,0,0,pvalues);
		
		pvalues[0]=0.2;
		pvalues[1]= 1;
		sb = new SensorBehavior(n,0,0,pvalues);
	}
	@Test
	public void testSettersGetters() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		
		SensorBehavior sb = new SensorBehavior(n,1,2,0.3,0.4);
		Assert.assertEquals(1.0,sb.getAmpFactor());
		Assert.assertEquals(2.0,sb.getNoiseAmpFactor());
		Assert.assertEquals(true, n == sb.getWhiteNoise());
		
		double [] actual = sb.getNoisePValues();
		
		Assert.assertEquals(0.3,actual[0]);
		Assert.assertEquals(0.4,actual[1]);

		
		double [] pvalues = new double[2];
		pvalues[0]=0.3;
		pvalues[1]= 0.4;
		sb = new SensorBehavior(n,10,20,pvalues);
		Assert.assertEquals(10.0,sb.getAmpFactor());
		Assert.assertEquals(20.0,sb.getNoiseAmpFactor());
		Assert.assertEquals(true, n == sb.getWhiteNoise());
		
		actual = sb.getNoisePValues();
		
		Assert.assertEquals(pvalues[0],actual[0]);
		Assert.assertEquals(pvalues[1],actual[1]);
		Assert.assertEquals(false,pvalues == actual);
		
		n = new Noise(3,2,1,4,Noise.Distribution.GAUSSIAN);
		pvalues[0]=0.3;
		pvalues[1]= 0.4;
		sb.setAmpFactor(100.0);
		sb.setNoiseAmpFactor(1000.0);
		sb.setNoisePValues(pvalues);
		sb.setWhiteNoise(n);
		
		Assert.assertEquals(100.0,sb.getAmpFactor());
		Assert.assertEquals(1000.0,sb.getNoiseAmpFactor());
		Assert.assertEquals(true, n == sb.getWhiteNoise());
		
		actual = sb.getNoisePValues();
		
		Assert.assertEquals(pvalues[0],actual[0]);
		Assert.assertEquals(pvalues[1],actual[1]);
		Assert.assertEquals(false,pvalues == actual);
		
	}
	
	@Test
	public void testSettersGetters_invalid_argument() {
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb = new SensorBehavior(n,0,0,0,1);
		boolean flag = false;
		
		try{
			sb.setWhiteNoise(null);
		}catch(Exception e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		
		
		 flag = false;
			
			try{
				sb.setNoisePValues(null);
			}catch(Exception e){
				flag = true;
			}
			
			Assert.assertEquals(true,flag);
			
			 flag = false;
				
				try{
					sb.setNoisePValues(new double[0]);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
			
flag = false;
				
				try{
					sb.setNoisePValues(new double[1]);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
				
				
flag = false;
				
				try{
					sb.setNoisePValues(new double[3]);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
				
				
				
				
flag = false;
				
				try{
					sb.setNoisePValues(new double[100]);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
				
flag = false;
				
				try{
					double [] pvalues = new double[2];
					pvalues[0]=0.3;
					pvalues[1]= 1.1;
					sb.setNoisePValues(pvalues);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
				
flag = false;
				
				try{
					double [] pvalues = new double[2];
					pvalues[0]=-1;
					pvalues[1]= 1;
					sb.setNoisePValues(pvalues);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
				
				
flag = false;
				
				try{
					double [] pvalues = new double[2];
					pvalues[0]=-100;
					pvalues[1]= 1.1;
					sb.setNoisePValues(pvalues);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);
				
				
				
flag = false;
				
				try{
					double [] pvalues = new double[2];
					pvalues[0]=0.3;
					pvalues[1]= 1000;
					sb.setNoisePValues(pvalues);
				}catch(Exception e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag);		
	}
	@Test
	public void testAddSensorBehavior() {
		Noise n1 = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		Noise n2 = new Noise(3,4,5,6,Noise.Distribution.GAUSSIAN);
		
		SensorBehavior sb1 = new SensorBehavior(n1,1,2,0.3,0.4);
		SensorBehavior sb2 = new SensorBehavior(n2,3,4,0.5,0.6);
		SensorBehavior sb3 = sb2.addSensorBehavior(sb1);
		
		Assert.assertEquals(4.0, sb3.getAmpFactor());
		Assert.assertEquals(6.0, sb3.getNoiseAmpFactor());
		Assert.assertEquals(0.5,sb3.getNoisePValues()[0]);
		Assert.assertEquals(0.6,sb3.getNoisePValues()[1]);
		Assert.assertEquals(4.0,sb3.getWhiteNoise().getNoiseMean());
		Assert.assertEquals(6.0,sb3.getWhiteNoise().getNoiseSD());
		Assert.assertEquals(8.0,sb3.getWhiteNoise().getNoiseMax());
		Assert.assertEquals(10.0,sb3.getWhiteNoise().getNoiseMin());
	}
}
