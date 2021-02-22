package test.phase.generation.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.SensorBehavior;
import common.Vehicle;
import common.event.DatasetSensorSampleEvent;
import common.event.SensorDataEvent;
import common.event.TimerEvent;
import common.event.stream.FaultInputStream;
import common.event.stream.FaultOutputStream;
import common.event.stream.FaultStreamManager;
import common.event.stream.RawSensorDataInputStream;
import common.event.stream.RepairInputStream;
import common.event.stream.RepairOutputStream;
import common.event.stream.RepairStreamManager;
import common.event.stream.SensorDataInputStream;
import common.event.stream.SensorDataOutputStream;
import common.event.stream.SensorDataStreamManager;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import phase.generation.data.DataGenerationSensor;
import phase.generation.data.DataGenerationTimer;
import phase.generation.data.DataGenerationVehicle;

public class TestDataGenerationTimer extends DataGenerationTimer {

	private Boolean exceptionOccured = false;

	private Boolean generateNoise = false;
	@Override
	public boolean randomEventOccured(DataGenerationVehicle v,double eventProbability){

		return generateNoise;
	}

	@Override
	public boolean randomEventOccured(double eventProbability, double randomNumber){
		return generateNoise;
	}

	protected void tick(TimerEvent e) throws InterruptedException{
		exceptionOccured=false;
		super.tick(e);
	}
	protected void threadTick(TimerEvent e,DataGenerationVehicle v){
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
			DataGenerationTimer t = new  DataGenerationTimer(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

		try{
			DataGenerationTimer t = new  DataGenerationTimer(new ArrayList<DataGenerationVehicle>(0));
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);

		flag = false;

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


		try{
			List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
			vehicles.add(new DataGenerationVehicle(0,list));
			vehicles.add(new DataGenerationVehicle(1,list));
			vehicles.add(new DataGenerationVehicle(2,list));
			vehicles.add(new DataGenerationVehicle(2,list));
			DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
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

		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));
		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles.add(new DataGenerationVehicle(2,list));
		vehicles.add(new DataGenerationVehicle(3,list));
		DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
	}


	@Test
	public void test_initStreams_getters() {
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

		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));
		vehicles2.add(vehicles.get(0));
		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));
		vehicles.add(new DataGenerationVehicle(2,list));
		vehicles2.add(vehicles.get(2));
		vehicles.add(new DataGenerationVehicle(3,list));
		vehicles2.add(vehicles.get(3));
		DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);

		List<DatasetSensorSampleEvent> rawSensorData = new ArrayList<DatasetSensorSampleEvent>(1);
		DatasetSensorSampleEvent se = new DatasetSensorSampleEvent(new Sensor(0,1),123.0,0);
		rawSensorData.add(se);

		RawSensorDataInputStream rawSensorDataInputStream = new RawSensorDataInputStream(rawSensorData);
		SensorDataOutputStream sensorOutputStream = new SensorDataOutputStream(vehicles2);

		t.initStreams(faultInputStream , repairInputStream ,rawSensorDataInputStream,sensorOutputStream);

		Assert.assertEquals(true, faultInputStream ==t.getFaultInputStream());
		Assert.assertEquals(true, repairInputStream ==t.getRepairInputStream());
		Assert.assertEquals(true, rawSensorDataInputStream ==t.getSensorDataInputStream());
		Assert.assertEquals(true, sensorOutputStream ==t.getSensorDataOutputStream());
	}

	@Test
	public void test_invokeSensorFaultInvolvedBehavior() {

		double error = 0.0001;
		List<DataGenerationSensor> sv1 = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv1.add(s);

		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,sv1));
		vehicles2.add(vehicles.get(0));


		List<DataGenerationSensor> sv2 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv2.add(s);

		vehicles.add(new DataGenerationVehicle(1,sv2));
		vehicles2.add(vehicles.get(1));

		List<DataGenerationSensor> sv3 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv3.add(s);

		vehicles.add(new DataGenerationVehicle(2,sv3));
		vehicles2.add(vehicles.get(2));

		List<DataGenerationSensor> sv4 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv4.add(s);

		vehicles.add(new DataGenerationVehicle(3,sv4));
		vehicles2.add(vehicles.get(3));

		DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
		FaultDescription f = new FaultDescription(-1,null);

		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t2 = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s2 = new Sensor(1,1);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,50.0,60.0,noisePValues,t2,s2);
		f.addAffectedSensor(sb);
		s2 = new Sensor(1,2);
		sb = new FaultInvolvedSensorBehavior(n,40.0,45.0,noisePValues,t2,s2);
		f.addAffectedSensor(sb);

		t.invokeSensorFaultInvolvedBehavior(vehicles.get(0), f);
		//	Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		Assert.assertEquals(50.0,sv1.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(60.0,sv1.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv1.get(1).isFaultInvolved());

		Assert.assertEquals(40.0,sv1.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(45.0,sv1.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(true,sv1.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv4.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv4.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv4.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv4.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv4.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv4.get(2).isFaultInvolved());

		f = new FaultDescription(-1,null);


		s2 = new Sensor(0,1);
		sb = new FaultInvolvedSensorBehavior(n,50.0,60.0,noisePValues,t2,s2);
		f.addAffectedSensor(sb);
		s2 = new Sensor(1,3);
		sb = new FaultInvolvedSensorBehavior(n,40.0,45.0,noisePValues,t2,s2);
		f.addAffectedSensor(sb);

		t.invokeSensorFaultInvolvedBehavior(vehicles.get(3), f);

		Assert.assertEquals(50.0,sv1.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(60.0,sv1.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv1.get(1).isFaultInvolved());

		Assert.assertEquals(40.0,sv1.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(45.0,sv1.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv1.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(2).isFaultInvolved());

		Assert.assertEquals(50.0,sv4.get(0).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(60.0,sv4.get(0).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv4.get(0).isFaultInvolved());

		Assert.assertEquals(40.0,sv4.get(3).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(45.0,sv4.get(3).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv4.get(3).isFaultInvolved());


	}

	@Test
	public void test_repairSensorFaultInvolvedBehavior() {

		double error = 0.0001;
		List<DataGenerationSensor> sv1 = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv1.add(s);

		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,sv1));
		vehicles2.add(vehicles.get(0));


		List<DataGenerationSensor> sv2 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv2.add(s);

		vehicles.add(new DataGenerationVehicle(1,sv2));
		vehicles2.add(vehicles.get(1));

		List<DataGenerationSensor> sv3 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv3.add(s);

		vehicles.add(new DataGenerationVehicle(2,sv3));
		vehicles2.add(vehicles.get(2));

		List<DataGenerationSensor> sv4 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv4.add(s);

		vehicles.add(new DataGenerationVehicle(3,sv4));
		vehicles2.add(vehicles.get(3));

		DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
		FaultDescription f1 = new FaultDescription(-1,null);

		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t2 = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s2 = new Sensor(1,1);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,50.0,60.0,noisePValues,t2,s2);
		f1.addAffectedSensor(sb);
		s2 = new Sensor(1,2);
		sb = new FaultInvolvedSensorBehavior(n,40.0,45.0,noisePValues,t2,s2);
		f1.addAffectedSensor(sb);

		t.invokeSensorFaultInvolvedBehavior(vehicles.get(0), f1);

		FaultDescription f2 = new FaultDescription(-1,null);


		s2 = new Sensor(0,1);
		sb = new FaultInvolvedSensorBehavior(n,50.0,60.0,noisePValues,t2,s2);
		f2.addAffectedSensor(sb);
		s2 = new Sensor(1,3);
		sb = new FaultInvolvedSensorBehavior(n,40.0,45.0,noisePValues,t2,s2);
		f2.addAffectedSensor(sb);

		t.invokeSensorFaultInvolvedBehavior(vehicles.get(3), f2);

		t.repairSensorFaultInvolvedBehavior(vehicles.get(0), f1);

		//	Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		Assert.assertEquals(0.0,sv1.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv1.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(1,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(2,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(3,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(4,sv1.get(1).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(false,sv1.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv1.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv1.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(1,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(2,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(3,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(4,sv1.get(2).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(false,sv1.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(2).isFaultInvolved());

		Assert.assertEquals(50.0,sv4.get(0).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(60.0,sv4.get(0).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv4.get(0).isFaultInvolved());

		Assert.assertEquals(40.0,sv4.get(3).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(45.0,sv4.get(3).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(2.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(4.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(6.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(8.0,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(true,sv4.get(3).isFaultInvolved());

		t.repairSensorFaultInvolvedBehavior(vehicles.get(3), f2);

		Assert.assertEquals(0.0,sv1.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv1.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv1.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv1.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv1.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv1.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv2.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv2.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(1).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(1).isFaultInvolved());

		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv3.get(2).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(false,sv3.get(2).isFaultInvolved());

		Assert.assertEquals(0.0,sv4.get(0).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0.0,sv4.get(0).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(1,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(2,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(3,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(4,sv4.get(0).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(false,sv4.get(0).isFaultInvolved());

		Assert.assertEquals(0.0,sv4.get(3).getCurrentBehavior().getAmpFactor(),error);
		Assert.assertEquals(0,sv4.get(3).getCurrentBehavior().getNoiseAmpFactor(),error);
		Assert.assertEquals(1,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMean(),error);
		Assert.assertEquals(2,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseSD(),error);
		Assert.assertEquals(3,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMax(),error);
		Assert.assertEquals(4,sv4.get(3).getCurrentBehavior().getWhiteNoise().getNoiseMin(),error);
		Assert.assertEquals(false,sv4.get(3).isFaultInvolved());

	}

	@Test
	public void test_repairSensorFaultInvolvedBehavior_illegal_argument() {

		double error = 0.0001;
		List<DataGenerationSensor> sv1 = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv1.add(s);

		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,sv1));
		vehicles2.add(vehicles.get(0));


		List<DataGenerationSensor> sv2 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv2.add(s);

		vehicles.add(new DataGenerationVehicle(1,sv2));
		vehicles2.add(vehicles.get(1));

		List<DataGenerationSensor> sv3 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv3.add(s);

		vehicles.add(new DataGenerationVehicle(2,sv3));
		vehicles2.add(vehicles.get(2));

		List<DataGenerationSensor> sv4 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv4.add(s);

		vehicles.add(new DataGenerationVehicle(3,sv4));
		vehicles2.add(vehicles.get(3));

		DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
		boolean flag = false;
		try{
			t.repairSensorFaultInvolvedBehavior(null, null);
		}catch(SimulationException e){
			flag = true;
		}
		FaultDescription f1 = new FaultDescription(-1,null);
		Assert.assertEquals(true,flag);
		double []noisePValues = new double[2]; 
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t2 = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s2 = new Sensor(1,1);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,50.0,60.0,noisePValues,t2,s2);
		f1.addAffectedSensor(sb);
		s2 = new Sensor(1,2);
		sb = new FaultInvolvedSensorBehavior(n,40.0,45.0,noisePValues,t2,s2);
		f1.addAffectedSensor(sb);



		flag = false;
		try{
			t.repairSensorFaultInvolvedBehavior(vehicles.get(0), null);
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{
			t.repairSensorFaultInvolvedBehavior(null, f1);
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}


	@Test
	public void test_invokeSensorFaultInvolvedBehavior_illegal_argument() {

		double error = 0.0001;
		List<DataGenerationSensor> sv1 = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,0,0,0,1);
		SensorBehavior sb2 = new SensorBehavior(n,0,0,0,1);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv1.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv1.add(s);

		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,sv1));
		vehicles2.add(vehicles.get(0));


		List<DataGenerationSensor> sv2 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv2.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv2.add(s);

		vehicles.add(new DataGenerationVehicle(1,sv2));
		vehicles2.add(vehicles.get(1));

		List<DataGenerationSensor> sv3 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv3.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv3.add(s);

		vehicles.add(new DataGenerationVehicle(2,sv3));
		vehicles2.add(vehicles.get(2));

		List<DataGenerationSensor> sv4 = new ArrayList<DataGenerationSensor>(4);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,2,sb1,sb2);
		sv4.add(s);
		s = new DataGenerationSensor(1,3,sb1,sb2);
		sv4.add(s);

		vehicles.add(new DataGenerationVehicle(3,sv4));
		vehicles2.add(vehicles.get(3));

		DataGenerationTimer t = new DataGenerationTimer(new ArrayList<DataGenerationVehicle>(vehicles));
		boolean flag = false;
		try{
			t.invokeSensorFaultInvolvedBehavior(null, null);
		}catch(SimulationException e){
			flag = true;
		}
		FaultDescription f1 = new FaultDescription(-1,null);
		Assert.assertEquals(true,flag);
		double []noisePValues = new double[2]; 
		noisePValues[0] = 0;
		noisePValues[1] = 1;
		FaultInvolvedSensorBehavior.Type t2 = FaultInvolvedSensorBehavior.Type.MODIFY;
		Sensor s2 = new Sensor(1,1);
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(n,50.0,60.0,noisePValues,t2,s2);
		f1.addAffectedSensor(sb);
		s2 = new Sensor(1,2);
		sb = new FaultInvolvedSensorBehavior(n,40.0,45.0,noisePValues,t2,s2);
		f1.addAffectedSensor(sb);



		flag = false;
		try{
			t.invokeSensorFaultInvolvedBehavior(vehicles.get(0), null);
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{
			t.invokeSensorFaultInvolvedBehavior(null, f1);
		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}

	@Test
	public void test_threadTick_generate_identical_data() throws InterruptedException {
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,0,0,Noise.Distribution.GAUSSIAN);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));
		vehicles2.add(vehicles.get(0));
		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));
		TestDataGenerationTimer t = new TestDataGenerationTimer();
		t.setParitionKeys(new ArrayList<DataGenerationVehicle>(vehicles));
		t.initRandomGenerators();
		

		List<DatasetSensorSampleEvent> rawSensorData = new ArrayList<DatasetSensorSampleEvent>(8);
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),1.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),1.0,0));

		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);
		RawSensorDataInputStream rawSensorDataInputStream = new RawSensorDataInputStream(rawSensorData);
		SensorDataOutputStream sensorOutputStream = new SensorDataOutputStream(vehicles2);

		t.initStreams(faultInputStream , repairInputStream ,rawSensorDataInputStream,sensorOutputStream);

		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		SensorDataInputStream sensorInputStream = new SensorDataInputStream(vehicles2);



		FaultStreamManager smFaults= new FaultStreamManager(faultInputStream,faultOutputStream);
		RepairStreamManager smRepairs= new RepairStreamManager(repairInputStream,repairOutputStream);
		SensorDataStreamManager smSensorData = new SensorDataStreamManager(sensorInputStream,sensorOutputStream);

		TimerEvent e = new TimerEvent(0);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		Iterator<SensorDataEvent> it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		SensorDataEvent 	sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		
		Assert.assertEquals(true,it.hasNext());

	sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());
		//sde = it.next();
		
		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());
		//sde = it.next();
		e = new TimerEvent(1);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
	
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());

		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());
		
		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
	
	
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());

		sde = it.next();
		Assert.assertEquals(1,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

	}
	/*
	 * out of date, need to fix, but isn't useful at the moment (don't use the  class under test)
	 
	@Test
	public void test_threadTick_generate_data_with_uniform_noise() throws InterruptedException {
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,1,1,Noise.Distribution.UNIFORM);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,2,2,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));


		vehicles2.add(vehicles.get(0));

		list = new ArrayList<DataGenerationSensor>(4);
		n = new Noise(0,0,3,3,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,4,4,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);

		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));

		TestDataGenerationTimer t = new TestDataGenerationTimer();
		t.setParitionKeys(new ArrayList<DataGenerationVehicle>(vehicles));


		List<DatasetSensorSampleEvent> rawSensorData = new ArrayList<DatasetSensorSampleEvent>(8);
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),1.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),1.0,0));

		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);
		RawSensorDataInputStream rawSensorDataInputStream = new RawSensorDataInputStream(rawSensorData);
		SensorDataOutputStream sensorOutputStream = new SensorDataOutputStream(vehicles2);

		t.initStreams(faultInputStream , repairInputStream ,rawSensorDataInputStream,sensorOutputStream);

		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		SensorDataInputStream sensorInputStream = new SensorDataInputStream(vehicles2);



		FaultStreamManager smFaults= new FaultStreamManager(faultInputStream,faultOutputStream);
		RepairStreamManager smRepairs= new RepairStreamManager(repairInputStream,repairOutputStream);
		SensorDataStreamManager smSensorData = new SensorDataStreamManager(sensorInputStream,sensorOutputStream);

		TimerEvent e = new TimerEvent(0);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		Iterator<SensorDataEvent> it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		SensorDataEvent sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		e = new TimerEvent(1);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());		
	}*/
/*
 * out of date, need to fix, but isn't useful at the moment (don't use the  class under test)
 
	@Test
	public void test_threadTick_generate_data_with_fault_new_behavior() throws InterruptedException {
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,1,1,Noise.Distribution.UNIFORM);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,2,2,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));


		vehicles2.add(vehicles.get(0));

		list = new ArrayList<DataGenerationSensor>(4);
		n = new Noise(0,0,3,3,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,4,4,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);

		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));

		TestDataGenerationTimer t = new TestDataGenerationTimer();
		t.setParitionKeys(new ArrayList<DataGenerationVehicle>(vehicles));


		List<DatasetSensorSampleEvent> rawSensorData = new ArrayList<DatasetSensorSampleEvent>(8);
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),1.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),1.0,0));

		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);
		RawSensorDataInputStream rawSensorDataInputStream = new RawSensorDataInputStream(rawSensorData);
		SensorDataOutputStream sensorOutputStream = new SensorDataOutputStream(vehicles2);

		t.initStreams(faultInputStream , repairInputStream ,rawSensorDataInputStream,sensorOutputStream);

		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		SensorDataInputStream sensorInputStream = new SensorDataInputStream(vehicles2);



		FaultStreamManager smFaults= new FaultStreamManager(faultInputStream,faultOutputStream);
		RepairStreamManager smRepairs= new RepairStreamManager(repairInputStream,repairOutputStream);
		SensorDataStreamManager smSensorData = new SensorDataStreamManager(sensorInputStream,sensorOutputStream);

		TimerEvent e = new TimerEvent(0);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		Iterator<SensorDataEvent> it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		SensorDataEvent sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());



		FaultDescription fd=new FaultDescription(-1,null);

		Noise faultNoise = new Noise(0,0,5,5,Noise.Distribution.UNIFORM);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 0;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.NEW;
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(faultNoise,1,1,noisePValues[0],noisePValues[1],type,new Sensor(0,1));
		fd.addAffectedSensor(sb);

		faultOutputStream.write(new Vehicle(0), new FaultEvent(new Vehicle(0),fd));
		smFaults.flush();
		smRepairs.flush();
		e = new TimerEvent(2);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}

		smSensorData.flush();

		it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(10,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());		
	}

	@Test
	public void test_threadTick_generate_data_with_fault_modify_behavior() throws InterruptedException {
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,1,1,Noise.Distribution.UNIFORM);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,2,2,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));


		vehicles2.add(vehicles.get(0));

		list = new ArrayList<DataGenerationSensor>(4);
		n = new Noise(0,0,3,3,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,4,4,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);

		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));

		TestDataGenerationTimer t = new TestDataGenerationTimer();
		t.setParitionKeys(new ArrayList<DataGenerationVehicle>(vehicles));


		List<DatasetSensorSampleEvent> rawSensorData = new ArrayList<DatasetSensorSampleEvent>(8);
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),1.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),1.0,0));

		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);
		RawSensorDataInputStream rawSensorDataInputStream = new RawSensorDataInputStream(rawSensorData);
		SensorDataOutputStream sensorOutputStream = new SensorDataOutputStream(vehicles2);

		t.initStreams(faultInputStream , repairInputStream ,rawSensorDataInputStream,sensorOutputStream);

		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		SensorDataInputStream sensorInputStream = new SensorDataInputStream(vehicles2);



		FaultStreamManager smFaults= new FaultStreamManager(faultInputStream,faultOutputStream);
		RepairStreamManager smRepairs= new RepairStreamManager(repairInputStream,repairOutputStream);
		SensorDataStreamManager smSensorData = new SensorDataStreamManager(sensorInputStream,sensorOutputStream);

		TimerEvent e = new TimerEvent(0);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		Iterator<SensorDataEvent> it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		SensorDataEvent sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());



		FaultDescription fd=new FaultDescription(-1,null);

		Noise faultNoise = new Noise(0,0,5,5,Noise.Distribution.UNIFORM);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 0;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.MODIFY;
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(faultNoise,0,0,noisePValues[0],noisePValues[1],type,new Sensor(0,1));
		fd.addAffectedSensor(sb);

		faultOutputStream.write(new Vehicle(0), new FaultEvent(new Vehicle(0),fd));
		smFaults.flush();
		smRepairs.flush();
		e = new TimerEvent(2);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}

		smSensorData.flush();

		it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(10,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(11,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());		
	}
*/
	/*
	 * out of date, need to fix, but isn't useful at the moment (don't use the  class under test)
	 
	@Test
	public void test_threadTick_generate_data_with_fault_new_behavior_and_repair() throws InterruptedException {
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,1,1,Noise.Distribution.UNIFORM);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,2,2,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));


		vehicles2.add(vehicles.get(0));

		list = new ArrayList<DataGenerationSensor>(4);
		n = new Noise(0,0,3,3,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,4,4,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);

		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));

		TestDataGenerationTimer t = new TestDataGenerationTimer();
		t.setParitionKeys(new ArrayList<DataGenerationVehicle>(vehicles));


		List<DatasetSensorSampleEvent> rawSensorData = new ArrayList<DatasetSensorSampleEvent>(8);
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),1.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(0,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),5.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),4.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),3.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),2.0,0));
		rawSensorData.add(new DatasetSensorSampleEvent(new Sensor(1,1),1.0,0));

		FaultInputStream faultInputStream = new FaultInputStream(vehicles2);
		RepairInputStream repairInputStream = new RepairInputStream(vehicles2);
		RawSensorDataInputStream rawSensorDataInputStream = new RawSensorDataInputStream(rawSensorData);
		SensorDataOutputStream sensorOutputStream = new SensorDataOutputStream(vehicles2);

		t.initStreams(faultInputStream , repairInputStream ,rawSensorDataInputStream,sensorOutputStream);

		FaultOutputStream faultOutputStream = new FaultOutputStream(vehicles2);
		RepairOutputStream repairOutputStream = new RepairOutputStream(vehicles2);
		SensorDataInputStream sensorInputStream = new SensorDataInputStream(vehicles2);



		FaultStreamManager smFaults= new FaultStreamManager(faultInputStream,faultOutputStream);
		RepairStreamManager smRepairs= new RepairStreamManager(repairInputStream,repairOutputStream);
		SensorDataStreamManager smSensorData = new SensorDataStreamManager(sensorInputStream,sensorOutputStream);

		TimerEvent e = new TimerEvent(0);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}
		smFaults.flush();
		smRepairs.flush();
		smSensorData.flush();

		Iterator<SensorDataEvent> it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		SensorDataEvent sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());



		FaultDescription fd=new FaultDescription(-1,null);

		Noise faultNoise = new Noise(0,0,5,5,Noise.Distribution.UNIFORM);
		double[] noisePValues = new double[2];
		noisePValues[0] = 0;
		noisePValues[1] = 0;
		FaultInvolvedSensorBehavior.Type type = FaultInvolvedSensorBehavior.Type.NEW;
		FaultInvolvedSensorBehavior sb = new FaultInvolvedSensorBehavior(faultNoise,1,1,noisePValues[0],noisePValues[1],type,new Sensor(0,1));
		fd.addAffectedSensor(sb);

		faultOutputStream.write(new Vehicle(0), new FaultEvent(new Vehicle(0),fd));
		smFaults.flush();
		smRepairs.flush();
		e = new TimerEvent(2);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}

		smSensorData.flush();

		it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(10,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());



		repairOutputStream.write(new Vehicle(0), new RepairEvent(new Vehicle(0),fd));
		smFaults.flush();
		smRepairs.flush();
		e = new TimerEvent(2);
		t.tick(e);

		if(t.exceptionOccured){
			Assert.fail("exception occured in one of the threads.");
		}

		smSensorData.flush();

		it = sensorInputStream.iterator(new Vehicle(0));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(2,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(3,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());

		it = sensorInputStream.iterator(new Vehicle(1));
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(4,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(0,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(9,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(8,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(7,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(6,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(true,it.hasNext());
		sde = it.next();
		Assert.assertEquals(5,sde.getValue(),error);
		Assert.assertEquals(new Sensor(1,1),sde.getSensor());
		Assert.assertEquals(false,it.hasNext());
	}
*/


	@Test
	public void test_generateSensorData(){
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,1,1,Noise.Distribution.UNIFORM);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,2,2,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));


		vehicles2.add(vehicles.get(0));

		list = new ArrayList<DataGenerationSensor>(4);
		n = new Noise(0,0,3,3,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,4,4,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);

		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));

		TestDataGenerationTimer t = new TestDataGenerationTimer();
		t.setParitionKeys(vehicles);
		t.initRandomGenerators();
		
		double actual = t.generateSensorData(vehicles.get(0),new Sensor(1,1),10,20, 0);
		t.generateNoise  = false;
		Assert.assertEquals(12, actual,error);
		t.generateNoise  = true;
		actual = t.generateSensorData(vehicles.get(0),new Sensor(1,1),10,20, 0);
		
		Assert.assertEquals(20, actual,error);
		t.generateNoise  = false;
		actual = t.generateSensorData(vehicles.get(0),new Sensor(0,1),10,20, 0);
		
		Assert.assertEquals(11, actual,error);
		t.generateNoise  = true;
		actual = t.generateSensorData(vehicles.get(0),new Sensor(0,1),10,20, 0);
		
		Assert.assertEquals(20, actual,error);

		t.generateNoise  = false;
		actual = t.generateSensorData(vehicles.get(1),new Sensor(1,1),10,20, 0);
		
		Assert.assertEquals(14, actual,error);
		t.generateNoise  = true;
		actual = t.generateSensorData(vehicles.get(1),new Sensor(1,1),10,20, 0);
		
		Assert.assertEquals(20, actual,error);
		t.generateNoise  = false;
		actual = t.generateSensorData(vehicles.get(1),new Sensor(0,1),10,20, 0);
		
		Assert.assertEquals(13, actual,error);
		t.generateNoise  = true;
		actual = t.generateSensorData(vehicles.get(1),new Sensor(0,1),10,20, 0);
		
		Assert.assertEquals(20, actual,error);

	}

	@Test
	public void test_generateSensorData_illega_arg(){
		double error = 0.0001;
		List<DataGenerationSensor> list = new ArrayList<DataGenerationSensor>(4);

		Noise n = new Noise(0,0,1,1,Noise.Distribution.UNIFORM);
		SensorBehavior sb1 = new SensorBehavior(n,1,1,0,0);
		SensorBehavior sb2 = new SensorBehavior(n,1,1,0,0);

		DataGenerationSensor s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,2,2,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);


		List<DataGenerationVehicle> vehicles = new ArrayList<DataGenerationVehicle>(4);
		List<Vehicle> vehicles2 = new ArrayList<Vehicle>(4);
		vehicles.add(new DataGenerationVehicle(0,list));


		vehicles2.add(vehicles.get(0));

		list = new ArrayList<DataGenerationSensor>(4);
		n = new Noise(0,0,3,3,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);

		s = new DataGenerationSensor(0,1,sb1,sb2);
		list.add(s);
		n = new Noise(0,0,4,4,Noise.Distribution.UNIFORM);
		sb1 = new SensorBehavior(n,1,1,0,0);
		sb2 = new SensorBehavior(n,1,1,0,0);
		s = new DataGenerationSensor(1,1,sb1,sb2);
		list.add(s);

		vehicles.add(new DataGenerationVehicle(1,list));
		vehicles2.add(vehicles.get(1));

		TestDataGenerationTimer t = new TestDataGenerationTimer();

		boolean flag = false;
		try{
			t.generateSensorData(null, null, 0, 0, -1);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);

		flag = false;
		try{
			t.generateSensorData(null, null, 0, 0, -1000);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);

		flag = false;
		try{
			t.generateSensorData(null, null, 0, 0, SensorBehavior.NUMBER_NOISE_P_VALUES);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);

		flag = false;
		try{
			t.generateSensorData(null, null, 0, 0, SensorBehavior.NUMBER_NOISE_P_VALUES+1000);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		
		flag = false;
		try{
			t.generateSensorData(vehicles.get(0), null, 0, 0, SensorBehavior.NUMBER_NOISE_P_VALUES+1000);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		
		flag = false;
		try{
			t.generateSensorData(null, new Sensor(0,1), 0, 0, SensorBehavior.NUMBER_NOISE_P_VALUES+1000);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			t.generateSensorData(new DataGenerationVehicle(50,list), null, 0, 0, SensorBehavior.NUMBER_NOISE_P_VALUES+1000);
		}catch(SimulationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
	}
}

