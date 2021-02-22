package test.phase.generation.history;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Algorithm;
import common.FaultDescription;
import common.FaultInvolvedSensorBehavior;
import common.Noise;
import common.Sensor;
import common.Vehicle;
import common.event.HistoryEvent;
import common.event.RepairEvent;
import common.event.SensorStatusEvent;
import common.event.TimerEvent;
import phase.generation.history.FaultHistory;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class TestHistorySerialization {

	@Test
	public void test() throws IOException, ClassNotFoundException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(4);
		vehicles.add(new Vehicle(0));
		vehicles.add(new Vehicle(1));

		FaultHistory faultHistory = new FaultHistory(vehicles);
		
		FaultDescription f1 = new FaultDescription(0,"fd0");
		f1.addAffectedSensor(createSensor(0,0));
		f1.addAffectedSensor(createSensor(0,1));
		
		FaultDescription f2 = new FaultDescription(1,"fd1");
		f2.addAffectedSensor(createSensor(1,0));
		f2.addAffectedSensor(createSensor(1,1));
		
		faultHistory.recordElement(new Vehicle(0),new TimerEvent(0),f1);
		faultHistory.recordRepair(new Vehicle(0),new TimerEvent(0),f1);
		faultHistory.recordElement(new Vehicle(1),new TimerEvent(0),f2);
		faultHistory.recordRepair(new Vehicle(1),new TimerEvent(0),f2);
		faultHistory.recordElement(new Vehicle(0),new TimerEvent(1),f1);
		faultHistory.recordRepair(new Vehicle(0),new TimerEvent(1),f1);
		faultHistory.recordElement(new Vehicle(1),new TimerEvent(1),f2);
		faultHistory.recordRepair(new Vehicle(1),new TimerEvent(1),f2);
		

		
		List<Algorithm> algs = new ArrayList<Algorithm>(4);
		algs.add(new Algorithm(0));
		algs.add(new Algorithm(1));

		List<Sensor> sensors = new ArrayList<Sensor>(3);
		sensors.add(new Sensor(0,0));
		sensors.add(new Sensor(0,1));
		sensors.add(new Sensor(1,0));

		SensorStatusHistory sensorStatusHistory = new SensorStatusHistory(algs);
		
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,0));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,1));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,2));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),0,3));
		
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,0));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,1));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,2));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),1,3));
		
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,0));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,1));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,2));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(1), createSensorStatusEvent(new Algorithm(0),2,3));
		
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(1), createSensorStatusEvent(new Algorithm(1),3,0));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),3,1));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(3), createSensorStatusEvent(new Algorithm(1),3,2));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(4), createSensorStatusEvent(new Algorithm(1),3,3));
		
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(5), createSensorStatusEvent(new Algorithm(0),0,0));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(7), createSensorStatusEvent(new Algorithm(0),0,1));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(8), createSensorStatusEvent(new Algorithm(0),0,2));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(9), createSensorStatusEvent(new Algorithm(0),0,3));
		
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(10), createSensorStatusEvent(new Algorithm(1),1,0));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(2), createSensorStatusEvent(new Algorithm(1),1,1));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(100), createSensorStatusEvent(new Algorithm(1),1,2));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(50), createSensorStatusEvent(new Algorithm(1),1,3));
		
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(22), createSensorStatusEvent(new Algorithm(0),2,0));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),2,1));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),2,2));
		sensorStatusHistory.recordElement(new Algorithm(0),new TimerEvent(0), createSensorStatusEvent(new Algorithm(0),2,3));
		
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,0));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,1));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,2));
		sensorStatusHistory.recordElement(new Algorithm(1),new TimerEvent(0), createSensorStatusEvent(new Algorithm(1),3,3));
		
		
		RepairHistory repairHist = new RepairHistory(vehicles);

		repairHist.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f2));
		repairHist.recordElement(new Vehicle(0),new TimerEvent(0), new RepairEvent(new Vehicle(0),f2));
		repairHist.recordElement(new Vehicle(0),new TimerEvent(10), new RepairEvent(new Vehicle(0),f1));
		repairHist.recordElement(new Vehicle(0),new TimerEvent(10), new RepairEvent(new Vehicle(0),f2));
		
		repairHist.recordElement(new Vehicle(1),new TimerEvent(1), new RepairEvent(new Vehicle(0),f1));
		repairHist.recordElement(new Vehicle(1),new TimerEvent(2), new RepairEvent(new Vehicle(0),f2));
		repairHist.recordElement(new Vehicle(1),new TimerEvent(12), new RepairEvent(new Vehicle(0),f1));
		repairHist.recordElement(new Vehicle(1),new TimerEvent(14), new RepairEvent(new Vehicle(0),f1));
		
		HistoryEvent hEvent = new HistoryEvent(sensorStatusHistory, faultHistory, repairHist);
		
		File tmpFile = File.createTempFile("testHist2",".ser");
		
		HistoryEvent.writeHistoryEvent(tmpFile.toString(), hEvent);
		
		HistoryEvent actualHEvent = HistoryEvent.readHistoryEvent(tmpFile.toString());
		
		tmpFile.delete();
		FaultHistory actualFaultHistory = actualHEvent.getFaultHistory();
		RepairHistory actualRepairHistory = actualHEvent.getRepairHistory();
		SensorStatusHistory actualSensorStatusHistory = actualHEvent.getSensorStatusHistory();
		
		
		Assert.assertEquals(true, actualFaultHistory.equals(faultHistory));
		Assert.assertEquals(true, actualRepairHistory.equals(repairHist));
		Assert.assertEquals(true, actualSensorStatusHistory.equals(sensorStatusHistory));
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

	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn, double zscore){
		return new SensorStatusEvent(new Vehicle(0), new Sensor(pgn,spn),alg,zscore,false,0.0);
	}
	
	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, double zscore){
		return createSensorStatusEvent(alg,0,0,zscore);
	}
	
	public static SensorStatusEvent createSensorStatusEvent(Algorithm alg, int pgn, int spn){
		return createSensorStatusEvent(alg,pgn,spn,0);
	}
	
}
