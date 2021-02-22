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
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import phase.generation.history.FaultHistory;
import phase.generation.history.SensorStatusHistory;

public class TestFaultHistory {

	@Test
	public void test_constructor_illegal_arg_null() {
		boolean flag = false;

		try{
			new FaultHistory(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor_illegal_arg_empty() {
		boolean flag = false;

		try{
			new FaultHistory(new ArrayList<Vehicle>(0));
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
			new FaultHistory(arg);
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

		new FaultHistory(arg);
	}

	@Test
	public void test_isSensorFaultInvolved_illegal_arg() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(2));

		FaultHistory h = new FaultHistory(arg);

		boolean flag = false;
		try{
			h.isSensorFaultInvolved(null, new Sensor(0,0), 0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;
		try{
			h.isSensorFaultInvolved(null, null, 0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);


		flag = false;
		try{
			h.isSensorFaultInvolved(new Vehicle(0),null, 0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

	}

	@Test
	public void test_isSensorFaultInvolved_non_existant_vehicle() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(2));

		FaultHistory h = new FaultHistory(arg);

		boolean flag = false;

		try{
			h.isSensorFaultInvolved(new Vehicle(5),new Sensor(0,0), 0);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

	}

	@Test
	public void test_isSensorFaultInvolved_no_faults() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(2));

		FaultHistory h = new FaultHistory(arg);

		boolean flag = false;
		for(int pgn = 0;pgn<100;pgn++){
			for(int spn = 0;spn<100;spn++){
				for(int time = 0;time<100;time++){
					Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(pgn,spn), time));
					Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(2),new Sensor(pgn,spn), time));
				}
			}

		}

	}
	
	@Test
	public void test_isSensorFaultInvolved_recordElement_repairBeforeFault() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		boolean flag = false;

		try{

		h.recordElement(new Vehicle(0),new TimerEvent(1),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(0),f1);
		
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}
		
	@Test
	public void test_isSensorFaultInvolved_recordRepair_recordElemen_0_day_repair() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(0),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(0),f1);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));

		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));

		
		h.recordElement(new Vehicle(0),new TimerEvent(1),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(1),f1);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(1),new TimerEvent(0),f2);
		h.recordRepair(new Vehicle(1),new TimerEvent(0),f2);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(1),new TimerEvent(1),f2);
		h.recordRepair(new Vehicle(1),new TimerEvent(1),f2);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(1),new TimerEvent(0),f1);
		h.recordRepair(new Vehicle(1),new TimerEvent(0),f1);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(1),new TimerEvent(1),f1);
		h.recordRepair(new Vehicle(1),new TimerEvent(1),f1);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
	h.recordElement(new Vehicle(0),new TimerEvent(0),f2);
	h.recordRepair(new Vehicle(0),new TimerEvent(0),f2);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(1),f2);
		h.recordRepair(new Vehicle(0),new TimerEvent(1),f2);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
	}
	
		@Test
	public void test_isSensorFaultInvolved_recordRepair_recordElemen_1_day_repair() {


		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));

		FaultHistory h = new FaultHistory(arg);
		
		FaultDescription f1 = new FaultDescription(0,null);
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(0,null);
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(0),f1);
		h.recordRepair(new Vehicle(0),new TimerEvent(1),f1);
		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 2));

		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(0),new TimerEvent(0),f2);
		h.recordRepair(new Vehicle(0),new TimerEvent(1),f2);

		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 2));

		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		
		h.recordElement(new Vehicle(1),new TimerEvent(1),f1);
		h.recordRepair(new Vehicle(1),new TimerEvent(2),f1);

		
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 2));

		
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 1));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 2));
		Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 2));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 3));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 3));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 1));
		Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 1));

	}
		
		@Test
		public void test_isSensorFaultInvolved_recordRepair_recordElemen_big_windows() {


			List<Vehicle> arg = new ArrayList<Vehicle>(4);
			arg.add(new Vehicle(0));
			arg.add(new Vehicle(1));

			FaultHistory h = new FaultHistory(arg);
			
			FaultDescription f1 = new FaultDescription(0,null);
			f1.addAffectedSensor(createSensor(0,0));
			f1.addAffectedSensor(createSensor(0,1));
			
			FaultDescription f2 = new FaultDescription(0,null);
			f2.addAffectedSensor(createSensor(1,0));
			f2.addAffectedSensor(createSensor(1,1));
			
			h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
			h.recordRepair(new Vehicle(0),new TimerEvent(10),f1);
			
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 1,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 3,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1,1));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,2));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,3,2));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,10,2));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,1000,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,3));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,4));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,5));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,10));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 2,2,1000));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,2,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,10,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,1000,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,1));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,3));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,5));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,10));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,1));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 6,0,5));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 8,0,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 9,0,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 10,0,2));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,0,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 11,1,2));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,1,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 12,2,2));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 13,3,2));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 14,3,2));
			
			
		}
		
		@Test
		public void test_repairAllUnrepairedFaults_illegal_arg_negative_time() {


			List<Vehicle> arg = new ArrayList<Vehicle>(4);
			arg.add(new Vehicle(0));
			arg.add(new Vehicle(2));

			FaultHistory h = new FaultHistory(arg);

			boolean flag = false;

			try{
				h.repairAllUnrepairedFaults(-1);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(-2);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(-100);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);

		}
		
		@Test
		public void test_repairAllUnrepairedFaults_illegal_arg_repair_before_fault() {


			List<Vehicle> arg = new ArrayList<Vehicle>(4);
			arg.add(new Vehicle(0));
			arg.add(new Vehicle(1));

			FaultHistory h = new FaultHistory(arg);

			
			FaultDescription f1 = new FaultDescription(0,null);
			f1.addAffectedSensor(createSensor(0,0));
			f1.addAffectedSensor(createSensor(0,1));
			
			FaultDescription f2 = new FaultDescription(0,null);
			f2.addAffectedSensor(createSensor(1,0));
			f2.addAffectedSensor(createSensor(1,1));
			
			h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
			h.recordElement(new Vehicle(0),new TimerEvent(10),f2);
			h.recordElement(new Vehicle(1),new TimerEvent(15),f1);
			h.recordElement(new Vehicle(1),new TimerEvent(20),f2);
			boolean flag = false;

			try{
				h.repairAllUnrepairedFaults(4);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(6);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(10);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(15);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(16);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);
			
			flag = false;

			try{
				h.repairAllUnrepairedFaults(19);
			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true, flag);

		}
		
		
		@Test
		public void test_repairAllUnrepairedFaults_no_faults() {


			List<Vehicle> arg = new ArrayList<Vehicle>(4);
			arg.add(new Vehicle(0));
			arg.add(new Vehicle(1));

			FaultHistory h = new FaultHistory(arg);

			
			FaultDescription f1 = new FaultDescription(0,null);
			f1.addAffectedSensor(createSensor(0,0));
			f1.addAffectedSensor(createSensor(0,1));
			
			FaultDescription f2 = new FaultDescription(0,null);
			f2.addAffectedSensor(createSensor(1,0));
			f2.addAffectedSensor(createSensor(1,1));
			
			
			h.repairAllUnrepairedFaults(25);
		}
		@Test
		public void test_repairAllUnrepairedFaults() {


			List<Vehicle> arg = new ArrayList<Vehicle>(4);
			arg.add(new Vehicle(0));
			arg.add(new Vehicle(1));

			FaultHistory h = new FaultHistory(arg);

			
			FaultDescription f1 = new FaultDescription(0,null);
			f1.addAffectedSensor(createSensor(0,0));
			f1.addAffectedSensor(createSensor(0,1));
			
			FaultDescription f2 = new FaultDescription(0,null);
			f2.addAffectedSensor(createSensor(1,0));
			f2.addAffectedSensor(createSensor(1,1));
			
			h.recordElement(new Vehicle(0),new TimerEvent(5),f1);
			h.recordElement(new Vehicle(0),new TimerEvent(10),f2);
			h.recordElement(new Vehicle(1),new TimerEvent(15),f1);
			h.recordElement(new Vehicle(1),new TimerEvent(20),f2);
			
			h.repairAllUnrepairedFaults(25);
			
			
			
			/*
			 * vehicle 0
			 */

			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 4,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 5,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 9,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 10,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 14,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 15,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,0), 26,0,0));
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 4,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 5,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 9,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 10,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 14,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 15,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(0,1), 26,0,0));
			
			
			/*
			 * vehicle 0
			 */
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 5,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 9,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 10,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 14,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 15,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,0), 26,0,0));
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 5,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 9,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 10,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 14,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 15,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(0),new Sensor(1,1), 26,0,0));
			
			
			
			/*
			 * vehicle 1
			 */

			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 5,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 9,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 10,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 14,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 15,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,0), 26,0,0));
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 5,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 9,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 10,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 14,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 15,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(0,1), 26,0,0));
			
			
			/*
			 * vehicle 1
			 */
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 5,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 9,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 10,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 14,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 15,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,0), 26,0,0));
			
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 0,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 4,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 5,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 9,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 10,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 14,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 15,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 19,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 20,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 24,0,0));
			Assert.assertEquals(true,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 25,0,0));
			Assert.assertEquals(false,h.isSensorFaultInvolved(new Vehicle(1),new Sensor(1,1), 26,0,0));
			
			
		}
		
		@Test
		public void test_equals() {	
		List<Vehicle> arg = new ArrayList<Vehicle>(4);
		arg.add(new Vehicle(0));
		arg.add(new Vehicle(1));
		List<FaultHistory> hists = new ArrayList<FaultHistory>(3);
		for(int i = 0;i<3;i++){
			FaultHistory h = new FaultHistory(arg);
			hists.add(h);
			FaultDescription f1 = new FaultDescription(0,null);
			f1.addAffectedSensor(createSensor(0,0));
			f1.addAffectedSensor(createSensor(0,1));
			
			FaultDescription f2 = new FaultDescription(0,null);
			f2.addAffectedSensor(createSensor(1,0));
			f2.addAffectedSensor(createSensor(1,1));
			
			h.recordElement(new Vehicle(0),new TimerEvent(0),f1);
			h.recordRepair(new Vehicle(0),new TimerEvent(1),f1);	
		
			h.recordElement(new Vehicle(0),new TimerEvent(0),f2);
			h.recordRepair(new Vehicle(0),new TimerEvent(1),f2);
	
			
			h.recordElement(new Vehicle(1),new TimerEvent(1),f1);
			h.recordRepair(new Vehicle(1),new TimerEvent(2),f1);
			if(i == 2){
				h.recordElement(new Vehicle(1),new TimerEvent(3),f1);
				h.recordRepair(new Vehicle(1),new TimerEvent(4),f1);
			}
		}
		
		FaultHistory h1 = hists.get(0);
		FaultHistory h2 = hists.get(1);
		FaultHistory h3 = hists.get(2);
		Assert.assertEquals(true,h1.equals(h1));
		Assert.assertEquals(true,h1.equals(h2));
		Assert.assertEquals(true,h2.equals(h1));
		Assert.assertEquals(true,h2.equals(h2));
		
		Assert.assertEquals(false,h1.equals(h3));
		Assert.assertEquals(false,h3.equals(h1));
		Assert.assertEquals(false,h2.equals(h3));
		Assert.assertEquals(false,h3.equals(h2));
		Assert.assertEquals(true,h3.equals(h3));
		
		FaultHistory h4 = null;
		
		Assert.assertEquals(false,h1.equals(h4));
		Assert.assertEquals(false,h2.equals(h4));
		Assert.assertEquals(false,h3.equals(h4));

}
		@Test
		public void test_equals2() {	
			List<Vehicle> arg = new ArrayList<Vehicle>(4);
			arg.add(new Vehicle(0));
			arg.add(new Vehicle(1));
			List<FaultHistory> hists = new ArrayList<FaultHistory>(3);
			for(int i = 0;i<3;i++){
				FaultHistory h = new FaultHistory(arg);
				hists.add(h);
				FaultDescription f1 = new FaultDescription(0,null);
				f1.addAffectedSensor(createSensor(0,0));
				f1.addAffectedSensor(createSensor(0,1));
				
				FaultDescription f2 = new FaultDescription(0,null);
				f2.addAffectedSensor(createSensor(1,0));
				f2.addAffectedSensor(createSensor(1,1));
				
				h.recordElement(new Vehicle(0),new TimerEvent(0),f1);
				h.recordRepair(new Vehicle(0),new TimerEvent(1),f1);	
			
				h.recordElement(new Vehicle(0),new TimerEvent(0),f2);
				h.recordRepair(new Vehicle(0),new TimerEvent(1),f2);
		
				
				h.recordElement(new Vehicle(1),new TimerEvent(1),f1);
				h.recordRepair(new Vehicle(1),new TimerEvent(2),f1);
				if(i == 2){
					h.recordElement(new Vehicle(1),new TimerEvent(3),f1);
				}
			}
			
			FaultHistory h1 = hists.get(0);
			FaultHistory h2 = hists.get(1);
			FaultHistory h3 = hists.get(2);
			Assert.assertEquals(true,h1.equals(h1));
			Assert.assertEquals(true,h1.equals(h2));
			Assert.assertEquals(true,h2.equals(h1));
			Assert.assertEquals(true,h2.equals(h2));
			
			Assert.assertEquals(false,h1.equals(h3));
			Assert.assertEquals(false,h3.equals(h1));
			Assert.assertEquals(false,h2.equals(h3));
			Assert.assertEquals(false,h3.equals(h2));
			Assert.assertEquals(true,h3.equals(h3));
			
			FaultHistory h4 = null;
			
			Assert.assertEquals(false,h1.equals(h4));
			Assert.assertEquals(false,h2.equals(h4));
			Assert.assertEquals(false,h3.equals(h4));

	}
	public static FaultInvolvedSensorBehavior createSensor(int pgn, int spn){
		//SensorBehavior sb = new SensorBehavior(n,0,0,-1000,1000);
				Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
				double[] noisePValues = new double[2];
				noisePValues[0] = 0;
				noisePValues[1] = 1;
				FaultInvolvedSensorBehavior.Type t = FaultInvolvedSensorBehavior.Type.MODIFY;
				Sensor s = new Sensor(pgn,spn);
				return new FaultInvolvedSensorBehavior(n,1,2,noisePValues,t,s);
	}
}
