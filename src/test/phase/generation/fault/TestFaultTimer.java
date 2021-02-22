package test.phase.generation.fault;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.Vehicle;
import common.event.FaultEvent;
import common.event.RepairEvent;
import common.event.TimerEvent;
import common.event.stream.FaultInputStream;
import common.event.stream.FaultOutputStream;
import common.event.stream.FaultStreamManager;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.RepairStreamManager;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import phase.generation.data.DataGenerationVehicle;
import phase.generation.fault.Fault;
import phase.generation.fault.FaultGenerationVehicle;
import phase.generation.fault.FaultTimer;

public class TestFaultTimer extends FaultTimer {

	private Integer counter = 0;
	private Boolean exceptionOccured = false;
	@Override
	public boolean randomEventOccured(FaultGenerationVehicle v,double eventProbability){
		

			if(counter % 2 ==0 ){
				return true;
			}else
				return false;
	}
	
	@Override
	public boolean randomEventOccured(double eventProbability, double randomNumber){
		

			if(counter % 2 ==0 ){
				return true;
			}else
				return false;
	}
	


	protected void tick(TimerEvent e) throws InterruptedException{
		exceptionOccured=false;
		super.tick(e);
	}
	protected void threadTick(TimerEvent e,FaultGenerationVehicle v){
		try{
		super.threadTick(e, v);
		}catch(Exception ex){
			ex.printStackTrace();
			synchronized(exceptionOccured){
				exceptionOccured = true;
			}
		}
	}
	
	@Test
	public void test_constructor_illegal_arg() {
		boolean flag = false;
		
		try{
			FaultTimer t = new FaultTimer(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
	flag = false;
		
		try{
			List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
			vehicles.add(new FaultGenerationVehicle(0));
			vehicles.add(new FaultGenerationVehicle(1));
			vehicles.add(new FaultGenerationVehicle(2));
			vehicles.add(new FaultGenerationVehicle(2));
			FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}

	@Test
	public void test_constructor() {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		vehicles.add(new FaultGenerationVehicle(0));
		vehicles.add(new FaultGenerationVehicle(1));
		vehicles.add(new FaultGenerationVehicle(2));
		vehicles.add(new FaultGenerationVehicle(3));
		FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
	}
	
	@Test
	public void test_addfaultInvolvedSensors_illegal_arg() {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		vehicles.add(new FaultGenerationVehicle(0));
		vehicles.add(new FaultGenerationVehicle(1));
		vehicles.add(new FaultGenerationVehicle(2));
		vehicles.add(new FaultGenerationVehicle(3));
		FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
		
		List<Sensor> sensors = new ArrayList<Sensor>(4);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(0,1));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(1,1));
		

		List<Sensor> sensors2 = new ArrayList<Sensor>(4);
		sensors2.add(new Sensor(0,0));
	
		t.addActiveFaultInvolvedSensors(new FaultGenerationVehicle(0),sensors);//adding sensors already in fault
		boolean flag = false;
		try{
			t.addActiveFaultInvolvedSensors(new FaultGenerationVehicle(0),sensors);//adding sensors already in fault
		}catch(ConfigurationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
			t.addActiveFaultInvolvedSensors(new FaultGenerationVehicle(0),sensors2);//adding sensors already in fault
		}catch(ConfigurationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
			t.addActiveFaultInvolvedSensors(new FaultGenerationVehicle(0),null);
		}catch(SimulationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
			t.addActiveFaultInvolvedSensors(null,null);
		}catch(SimulationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);
		
		flag = false;
		try{
			t.addActiveFaultInvolvedSensors(new FaultGenerationVehicle(0),new ArrayList<Sensor>(0));
		}catch(SimulationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);
		
	}
	
	@Test
	public void test_removefaultInvolvedSensors_illegal_arg() {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		vehicles.add(new FaultGenerationVehicle(0));
		vehicles.add(new FaultGenerationVehicle(1));
		vehicles.add(new FaultGenerationVehicle(2));
		vehicles.add(new FaultGenerationVehicle(3));
		FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
	
		boolean flag = false;
		try{
			t.removeActiveFaultInvolvedSensors(new FaultGenerationVehicle(0),null);
		}catch(SimulationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);

		
	}
	
	@Test
	public void test_sensorsAlreadyfaultInvolvedSensors_illegal_arg() {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		vehicles.add(new FaultGenerationVehicle(0));
		vehicles.add(new FaultGenerationVehicle(1));
		vehicles.add(new FaultGenerationVehicle(2));
		vehicles.add(new FaultGenerationVehicle(3));
		FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
	
		boolean flag = false;
		try{
			t.sensorsAlreadyInvolvedInActiveFault(new FaultGenerationVehicle(0),null);
		}catch(SimulationException e){
			flag = true;
			
			
		}
		Assert.assertEquals(true, flag);

		
	}
	
	
	@Test
	public void test_faultInvolvedSensors() {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		vehicles.add(new FaultGenerationVehicle(0));
		vehicles.add(new FaultGenerationVehicle(1));
		vehicles.add(new FaultGenerationVehicle(2));
		vehicles.add(new FaultGenerationVehicle(3));
		FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
		
		List<Sensor> sensors = new ArrayList<Sensor>(4);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(0,1));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(1,1));
		Fault f1 = createTestFault(sensors,0);
		sensors = new ArrayList<Sensor>(4);
		sensors.add(new Sensor(1,1));
		sensors.add(new Sensor(1,3));
		Fault f2 = createTestFault(sensors,1);
		sensors = new ArrayList<Sensor>(4);
		sensors.add(new Sensor(1,4));
		Fault f3 = createTestFault(sensors,2);
		
		for(FaultGenerationVehicle v: vehicles){
		t.addActiveFaultInvolvedSensors(v,f1.getSensors());
		
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f1.getSensors()));
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f2.getSensors()));
		Assert.assertEquals(false,t.sensorsAlreadyInvolvedInActiveFault(v,f3.getSensors()));
		
		t.removeActiveFaultInvolvedSensors(v,f1.getSensors());
		
		Assert.assertEquals(false,t.sensorsAlreadyInvolvedInActiveFault(v,f1.getSensors()));
		Assert.assertEquals(false,t.sensorsAlreadyInvolvedInActiveFault(v,f2.getSensors()));
		Assert.assertEquals(false,t.sensorsAlreadyInvolvedInActiveFault(v,f3.getSensors()));
		
		t.addActiveFaultInvolvedSensors(v,f2.getSensors());
		
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f1.getSensors()));
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f2.getSensors()));
		Assert.assertEquals(false,t.sensorsAlreadyInvolvedInActiveFault(v,f3.getSensors()));
		
		t.addActiveFaultInvolvedSensors(v,f3.getSensors());
		
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f1.getSensors()));
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f2.getSensors()));
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f3.getSensors()));
		
		t.removeActiveFaultInvolvedSensors(v,f3.getSensors());
		
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f1.getSensors()));
		Assert.assertEquals(true,t.sensorsAlreadyInvolvedInActiveFault(v,f2.getSensors()));
		Assert.assertEquals(false,t.sensorsAlreadyInvolvedInActiveFault(v,f3.getSensors()));
		}

	}
	
	@Test
	public void test_initStreams_getters() {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		List<Vehicle> vehicles2 =  new ArrayList<Vehicle>(4);
		FaultGenerationVehicle v = new FaultGenerationVehicle(0);
		vehicles.add(v);
		vehicles2.add(v);
		 v = new FaultGenerationVehicle(1);
		vehicles.add(v);
		vehicles2.add(v);
		v = new FaultGenerationVehicle(2);
		vehicles.add(v);
		vehicles2.add(v);
		 v = new FaultGenerationVehicle(3);
		vehicles.add(v);
		vehicles2.add(v);
		FaultTimer t = new FaultTimer(new ArrayList<FaultGenerationVehicle>(vehicles));
		
		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		t.initStreams(faultOutputStream , repairOutputStream );
		
		Assert.assertEquals(true, faultOutputStream ==t.getFaultOutputStream());
		Assert.assertEquals(true, repairOutputStream ==t.getRepairOutputStream());
	}

	
	@Test
	public void test_threadTick() throws InterruptedException {
		List<FaultGenerationVehicle> vehicles = new ArrayList<FaultGenerationVehicle>(4);
		List<Vehicle> vehicles2 =  new ArrayList<Vehicle>(4);
		
		List<Sensor> sensors2 = new ArrayList<Sensor>(4);
		sensors2.add(new Sensor(5,0));
		sensors2.add(new Sensor(5,1));
		sensors2.add(new Sensor(5,2));
		sensors2.add(new Sensor(5,3));
		
		List<Sensor> sensors = new ArrayList<Sensor>(4);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(0,1));
		sensors.add(new Sensor(1,0));
		sensors.add(new Sensor(1,1));
		
		Fault f1 = createTestFault(sensors,1,0);
		Fault f2 = createTestFault(sensors2,3,1);
		Fault f3 = createTestFault(sensors,6,2);
		FaultGenerationVehicle v = new FaultGenerationVehicle(0);
		
		v.addPotentialFault(f1);
		v.addPotentialFault(f2);
		v.addPotentialFault(f3);
		vehicles.add(v);
		vehicles2.add(v);
		 v = new FaultGenerationVehicle(1);
		 
		Fault f4 = createTestFault(sensors,2,3);
		Fault f5 = createTestFault(sensors2,4,4);
		Fault f6 = createTestFault(sensors,5,5);
		 v.addPotentialFault(f4);
			v.addPotentialFault(f5);
			v.addPotentialFault(f6);
		vehicles.add(v);
		vehicles2.add(v);
		v = new FaultGenerationVehicle(2);
		
		Fault f7 = createTestFault(sensors,6,6);
		Fault f8 = createTestFault(sensors2,4,7);
		Fault f9 = createTestFault(sensors,10,8);
		v.addPotentialFault(f7);
		v.addPotentialFault(f8);
		v.addPotentialFault(f9);
		vehicles.add(v);
		vehicles2.add(v);
		 v = new FaultGenerationVehicle(3);

			Fault f10 = createTestFault(sensors,8,9);
			Fault f11 = createTestFault(sensors2,9,10);
			Fault f12 = createTestFault(sensors,2,11);
		 v.addPotentialFault(f10);
			v.addPotentialFault(f11);
			v.addPotentialFault(f12);
		vehicles.add(v);
		vehicles2.add(v);
		TestFaultTimer t = new TestFaultTimer();
		t.setParitionKeys(vehicles);
		t.initRandomGenerators();
		
		
	
		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		
		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);
		
		t.initStreams(faultOutputStream , repairOutputStream );
		
		FaultStreamManager smFaults= new FaultStreamManager(faultInputStream,faultOutputStream);
		RepairStreamManager smRepairs= new RepairStreamManager(repairInputStream,repairOutputStream);
		t.counter++;
		TimerEvent e = new TimerEvent(0);
		t.tick(e);
		
		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		
		for(Vehicle vtmp : vehicles2){
			Iterator<FaultEvent> fit = faultInputStream.iterator(vtmp);
			Iterator<RepairEvent> rit = repairInputStream.iterator(vtmp);
			//no faults this run
			Assert.assertEquals(false,fit.hasNext());
			Assert.assertEquals(false,rit.hasNext());
		}
		
		t.counter++;//this will make all faults happen
		
		/*
		 *time: day 1 (0th day since faults started) 
		 *Fault f1 = createTestFault(sensors,1,0);
		Fault f2 = createTestFault(sensors2,3,1);
		Fault f3 = createTestFault(sensors,6,2);
		
		Fault f4 = createTestFault(sensors,2,3);
		Fault f5 = createTestFault(sensors2,4,4);
		Fault f6 = createTestFault(sensors,5,5);
		
			Fault f7 = createTestFault(sensors,6,6);
		Fault f8 = createTestFault(sensors2,4,7);
		Fault f9 = createTestFault(sensors,10,8);
		
			Fault f10 = createTestFault(sensors,8,9);
			Fault f11 = createTestFault(sensors2,9,10);
			Fault f12 = createTestFault(sensors,2,11);
		 */
		e = new TimerEvent(1);
		t.tick(e);
		
		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		
			Iterator<FaultEvent> fit = faultInputStream.iterator(vehicles2.get(0));
			Iterator<RepairEvent> rit = repairInputStream.iterator(vehicles2.get(0));
			Assert.assertEquals(false,rit.hasNext());
			
			
			Assert.assertEquals(true,fit.hasNext());
			
			Assert.assertEquals(0,fit.next().getFaultDescription().getId());
			Assert.assertEquals(1,fit.next().getFaultDescription().getId());
			Assert.assertEquals(false,fit.hasNext());
			
			fit = faultInputStream.iterator(vehicles2.get(1));
			 rit = repairInputStream.iterator(vehicles2.get(1));
			Assert.assertEquals(false,rit.hasNext());
			
			
			Assert.assertEquals(true,fit.hasNext());
			
			Assert.assertEquals(3,fit.next().getFaultDescription().getId());
			Assert.assertEquals(4,fit.next().getFaultDescription().getId());
			Assert.assertEquals(false,fit.hasNext());
			
			fit = faultInputStream.iterator(vehicles2.get(2));
			 rit = repairInputStream.iterator(vehicles2.get(2));
			Assert.assertEquals(false,rit.hasNext());
			
			
			Assert.assertEquals(true,fit.hasNext());
			
			Assert.assertEquals(6,fit.next().getFaultDescription().getId());
			Assert.assertEquals(7,fit.next().getFaultDescription().getId());
			Assert.assertEquals(false,fit.hasNext());
			
			fit = faultInputStream.iterator(vehicles2.get(3));
			 rit = repairInputStream.iterator(vehicles2.get(3));
			Assert.assertEquals(false,rit.hasNext());
			
			
			Assert.assertEquals(true,fit.hasNext());
			
			Assert.assertEquals(9,fit.next().getFaultDescription().getId());
			Assert.assertEquals(10,fit.next().getFaultDescription().getId());
			Assert.assertEquals(false,fit.hasNext());
			
		
		t.counter++;
		
		 e = new TimerEvent(2);
		t.tick(e);
		
		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		
		for(Vehicle vtmp : vehicles2){
			 fit = faultInputStream.iterator(vtmp);
			 rit = repairInputStream.iterator(vtmp);
			//no faults this run
			Assert.assertEquals(false,fit.hasNext());
			Assert.assertEquals(false,rit.hasNext());
		}
		
		t.counter++;//this will make all faults happen
		
		 e = new TimerEvent(3);
		t.tick(e);
		
		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		

		 rit = repairInputStream.iterator(vehicles2.get(0));
		Assert.assertEquals(true,rit.hasNext());
		
		
		
		Assert.assertEquals(0,rit.next().getFaultDescription().getId());
		Assert.assertEquals(false,rit.hasNext());
		

		 rit = repairInputStream.iterator(vehicles2.get(1));
		Assert.assertEquals(true,rit.hasNext());
		
		
		Assert.assertEquals(3,rit.next().getFaultDescription().getId());
		Assert.assertEquals(false,rit.hasNext());
		

		 rit = repairInputStream.iterator(vehicles2.get(2));
		Assert.assertEquals(false,rit.hasNext());
		

		 rit = repairInputStream.iterator(vehicles2.get(3));
		Assert.assertEquals(false,rit.hasNext());
		
		
		t.counter++;
		
		 e = new TimerEvent(4);
			t.tick(e);
			
			if(t.exceptionOccured){
				Assert.fail("exception occured in one of the threads.");
			}
			
			smFaults.flush();
			smRepairs.flush();
			
			for(Vehicle vtmp : vehicles2){
				 fit = faultInputStream.iterator(vtmp);
				 rit = repairInputStream.iterator(vtmp);
				//no faults this run
				Assert.assertEquals(false,fit.hasNext());
				Assert.assertEquals(false,rit.hasNext());
			}
			
			t.counter++;//this will make all faults happen
			
			/*
			 
			 *Fault f1 = createTestFault(sensors,1,0);
			Fault f2 = createTestFault(sensors2,3,1);
			Fault f3 = createTestFault(sensors,6,2);
			
			Fault f4 = createTestFault(sensors,2,3);
			Fault f5 = createTestFault(sensors2,4,4);
			Fault f6 = createTestFault(sensors,5,5);
			
				Fault f7 = createTestFault(sensors,6,6);
			Fault f8 = createTestFault(sensors2,4,7);
			Fault f9 = createTestFault(sensors,10,8);
			
				Fault f10 = createTestFault(sensors,8,9);
				Fault f11 = createTestFault(sensors2,9,10);
				Fault f12 = createTestFault(sensors,2,11);
			 */
			 e = new TimerEvent(5);
				t.tick(e);
				if(t.exceptionOccured){
					Assert.fail("exception occured in one of the threads.");
				}
				
				smFaults.flush();
				smRepairs.flush();
				

				 rit = repairInputStream.iterator(vehicles2.get(0));
				Assert.assertEquals(true,rit.hasNext());
				Assert.assertEquals(1,rit.next().getFaultDescription().getId());
				Assert.assertEquals(false,rit.hasNext());
				
				
				

				 rit = repairInputStream.iterator(vehicles2.get(1));
				Assert.assertEquals(true,rit.hasNext());
				
				
				Assert.assertEquals(4,rit.next().getFaultDescription().getId());
				Assert.assertEquals(false,rit.hasNext());
				

				 rit = repairInputStream.iterator(vehicles2.get(2));
				Assert.assertEquals(true,rit.hasNext());
				
				
				Assert.assertEquals(7,rit.next().getFaultDescription().getId());
				Assert.assertEquals(false,rit.hasNext());
				

				 rit = repairInputStream.iterator(vehicles2.get(3));
				Assert.assertEquals(false,rit.hasNext());
				
	}
	
	private static Fault createTestFault(List<Sensor> sensors,int id){
		return createTestFault(sensors,0,id);
	}
	
	private static Fault createTestFault(List<Sensor> sensors, int minDayBeforeOccurence, int id){
		FaultDescription fd=new FaultDescription(id,null);
		int minDayBeforeRepair=minDayBeforeOccurence;
		double aOccurencePValue = 0;
		double aRepairPValue = 0;
		Fault f = new Fault(minDayBeforeRepair,aRepairPValue,aOccurencePValue,fd);		
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		
		for(Sensor s: sensors){
			FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,1,2,noisePValues[0],noisePValues[1],type,s);
			fd.addAffectedSensor(sb);
		}
		
		return f;
	}
}
