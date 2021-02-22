package test.phase.generation.fault;

import java.util.List;

import org.junit.Test;

import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.fault.Fault;

public class TestFault {

	@Test
	public void test_constructor_illegal_argument() {
			
			//(int aMinDaysBeforeRepair, double aOccurencePValue, double aRepairPValue, FaultDescription aFaultDescription)
			
			boolean flag = false;
			try{
				FaultDescription fd = null ;//new FaultDescription(-1,null); 
				int minDayBeforeRepair=-1;
				double aOccurencePValue = -1;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=-1;
				double aOccurencePValue = -1;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=0;
				double aOccurencePValue = -1;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=10;
				double aOccurencePValue = -1;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=10;
				double aOccurencePValue = 0;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=10;
				double aOccurencePValue = 0.5;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=10;
				double aOccurencePValue = 1;
				double aRepairPValue = -1;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=10;
				double aOccurencePValue = 0.5;
				double aRepairPValue = 2;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);
			
			
			flag = false;
			try{
				FaultDescription fd = new FaultDescription(-1,null); 
				int minDayBeforeRepair=10;
				double aOccurencePValue = 2;
				double aRepairPValue = 0.5;
				Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
			}catch(ConfigurationException e){
				flag = true;
			}
			Assert.assertEquals(true,flag);	
		
	}

	@Test
	public void test_constructor() {
			
		//(int aMinDaysBeforeRepair, double aOccurencePValue, double aRepairPValue, FaultDescription aFaultDescription)
		Fault f = new Fault(0,0,0,new FaultDescription(-1,null));
		f = new Fault(1,0,0,new FaultDescription(-1,null));
		f = new Fault(1000,0,0,new FaultDescription(-1,null));
		f = new Fault(0,1,0,new FaultDescription(-1,null));
		f = new Fault(0,0,1,new FaultDescription(-1,null));
		f = new Fault(0,1,1,new FaultDescription(-1,null));
		
		
	}
	
	@Test
	public void test_settersgetters() {
			
		FaultDescription fd=new FaultDescription(-1,null);
		//(int aMinDaysBeforeRepair, double aOccurencePValue, double aRepairPValue, FaultDescription aFaultDescription)
		Fault f = new Fault(1,0.5,0.6,fd);
		
		Assert.assertEquals(true, fd == f.getFaultDescription());
		Assert.assertEquals(1,f.getMinDaysBeforeRepair());
		Assert.assertEquals(0.5,f.getOccurencePValue());
		Assert.assertEquals(0.6,f.getRepairPValue());
		
		 fd=new FaultDescription(-1,null);
		 f.setFaultDescription(fd);
		 Assert.assertEquals(true, fd == f.getFaultDescription());
			
	}
	
	@Test
	public void test_settersgetters_illegal_arg() {
			
		FaultDescription fd=new FaultDescription(-1,null);
		int minDayBeforeRepair=0;
		double aOccurencePValue = 0;
		double aRepairPValue = 0;
		Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
		boolean flag = false;
		try{	
			f.setFaultDescription(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
	}
	
	@Test
	public void test_getSensors() {
		FaultDescription fd=new FaultDescription(-1,null);
		int minDayBeforeRepair=0;
		double aOccurencePValue = 0;
		double aRepairPValue = 0;
		Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
		
		List<Sensor> list = f.getSensors();
		Assert.assertEquals(0,list.size());
		
		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s = new Sensor(1,2);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues[0],noisePValues[1],t,s);
		fd.addAffectedSensor(sb);
		
		list = f.getSensors();
		Assert.assertEquals(1,list.size());
		Assert.assertEquals(new Sensor(1,2),list.get(0));
		
		s = new Sensor(0,0);
		sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues[0],noisePValues[1],t,s);
		fd.addAffectedSensor(sb);
		
		list = f.getSensors();
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(new Sensor(1,2),list.get(0));
		Assert.assertEquals(new Sensor(0,0),list.get(1));
		
	}
	
	@Test
	public void test_activate_getOccurenceDay() {
		FaultDescription fd=new FaultDescription(-1,null);
		int minDayBeforeRepair=0;
		double aOccurencePValue = 0;
		double aRepairPValue = 0;
		Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
		
		f.activate(-1000);
		Assert.assertEquals(-1000,f.getOccurenceDay());
		
		f.activate(-1);
		Assert.assertEquals(-1,f.getOccurenceDay());
		
		
		f.activate(0);
		Assert.assertEquals(0,f.getOccurenceDay());
		
		f.activate(1);
		Assert.assertEquals(1,f.getOccurenceDay());
		
		f.activate(1000);
		Assert.assertEquals(1000,f.getOccurenceDay());
	}
	@Test
	public void test_activate_deactivate() {
		FaultDescription fd=new FaultDescription(-1,null);
		int minDayBeforeRepair=0;
		double aOccurencePValue = 0;
		double aRepairPValue = 0;
		Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
		
		Assert.assertEquals(true,f.isInactive());
		Assert.assertEquals(false,f.isActive());
		
		f.activate(0);
		Assert.assertEquals(false,f.isInactive());
		Assert.assertEquals(true,f.isActive());
		
		f.deactivate();
		Assert.assertEquals(true,f.isInactive());
		Assert.assertEquals(false,f.isActive());
		
		f.activate(0);
		Assert.assertEquals(false,f.isInactive());
		Assert.assertEquals(true,f.isActive());
		
		f.deactivate();
		Assert.assertEquals(true,f.isInactive());
		Assert.assertEquals(false,f.isActive());


	}
	
	@Test
	public void test_isReadyForRepair() {
		FaultDescription fd=new FaultDescription(-1,null);
		int minDayBeforeRepair=0;
		double aOccurencePValue = 0;
		double aRepairPValue = 0;
		Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);
		
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(-999);
		Assert.assertEquals(true,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(true,f.isReadyForRepair(0));
		Assert.assertEquals(true,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		
		f.activate(-500);
		Assert.assertEquals(true,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(true,f.isReadyForRepair(0));
		Assert.assertEquals(true,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(-2);
		Assert.assertEquals(true,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(true,f.isReadyForRepair(0));
		Assert.assertEquals(true,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(-1);
		Assert.assertEquals(true,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(true,f.isReadyForRepair(0));
		Assert.assertEquals(true,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(0);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(true,f.isReadyForRepair(0));
		Assert.assertEquals(true,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(1);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(true,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(500);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(999);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(1000);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(true,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(1001);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		
		f.activate(20001);
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
		f.deactivate();
		Assert.assertEquals(false,f.isReadyForRepair(-1));
		Assert.assertEquals(false,f.isReadyForRepair(-1000));
		Assert.assertEquals(false,f.isReadyForRepair(0));
		Assert.assertEquals(false,f.isReadyForRepair(1));
		Assert.assertEquals(false,f.isReadyForRepair(1000));
	}
	
}
