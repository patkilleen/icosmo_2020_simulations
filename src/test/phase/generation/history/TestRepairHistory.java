package test.phase.generation.history;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.Vehicle;
import common.event.RepairEvent;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import phase.generation.history.RepairHistory;

public class TestRepairHistory {

	@Test
	public void test_constructor_illegal_arg_null() {
		boolean flag = false;

		try{
			new RepairHistory(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_empty() {
		boolean flag = false;

		try{
			new RepairHistory(new ArrayList<Vehicle>(0));
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_duplicate_key() {
		boolean flag = false;

		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		try{
			new RepairHistory(arg);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(2));
		arg.add(new Vehicle(3));

		new RepairHistory(arg);
	}
	@Test
	public void test_equals() {
		//run 1000 times
		for(int j = 0;j<10;j++){
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		List<RepairHistory> hists = new ArrayList<RepairHistory>(3);
		
		FaultInvolvedSensorBehavior s0_0 = createSensor(0,0);
		FaultInvolvedSensorBehavior s0_1 = createSensor(0,1);
		FaultInvolvedSensorBehavior s1_0 = createSensor(1,0);
		FaultInvolvedSensorBehavior s1_1 = createSensor(1,1);
		for(int i = 0;i<3;i++){
			RepairHistory h = new RepairHistory(arg);
			hists.add(h);
			FaultDescription f1 = new FaultDescription(0,null);
			f1.addAffectedSensor(s0_0);
			f1.addAffectedSensor(s0_1);
			
			FaultDescription f2 = new FaultDescription(1,null);
			f2.addAffectedSensor(s1_0);	
			f2.addAffectedSensor(s1_1);
			
			if(i == 2){
				f2.addAffectedSensor(createSensor(0,0));
			}
			
			h.recordElement(new Vehicle(0),new TimerEvent(0),new RepairEvent(new Vehicle(0),f1));
			//h.recordRepair(new Vehicle(0),new TimerEvent(1),f1);	
		
			h.recordElement(new Vehicle(0),new TimerEvent(0),new RepairEvent(new Vehicle(0),f2));
			//h.recordRepair(new Vehicle(0),new TimerEvent(1),f2);
	
			
			h.recordElement(new Vehicle(1),new TimerEvent(1),new RepairEvent(new Vehicle(1),f1));
			//h.recordRepair(new Vehicle(1),new TimerEvent(2),f1);
			if(i == 2){
				h.recordElement(new Vehicle(1),new TimerEvent(3),new RepairEvent(new Vehicle(1),f1));
			}
		}
		
		RepairHistory h1 = hists.get(0);
		RepairHistory h2 = hists.get(1);
		RepairHistory h3 = hists.get(2);
		Assert.assertEquals(true,h1.equals(h1));
		Assert.assertEquals(true,h1.equals(h2));
		Assert.assertEquals(true,h2.equals(h1));
		Assert.assertEquals(true,h2.equals(h2));
		
		Assert.assertEquals(false,h1.equals(h3));
		Assert.assertEquals(false,h3.equals(h1));
		Assert.assertEquals(false,h2.equals(h3));
		Assert.assertEquals(false,h3.equals(h2));
		Assert.assertEquals(true,h3.equals(h3));
		
		RepairHistory h4 = null;
		
		Assert.assertEquals(false,h1.equals(h4));
		Assert.assertEquals(false,h2.equals(h4));
		Assert.assertEquals(false,h3.equals(h4));
		}

}
	
	public static FaultInvolvedSensorBehavior createSensor(int pgn, int spn){
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
				Noise gen = new Noise(1,2,0,1,Noise.Distribution.UNIFORM);
				Noise n = new Noise(gen.generateNoise(),gen.generateNoise(),gen.generateNoise(),gen.generateNoise(),Noise.Distribution.UNIFORM);
				double[] noisePValues = new double[2];
				noisePValues[0] = 0;
				noisePValues[1] = 1;
				FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
				Sensor s = new Sensor(pgn,spn);
				return new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
	}
}
