package phase.generation.cosmo;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import common.Sensor;
import common.SensorInstance;
import common.Vehicle;
import common.exception.ConfigurationException;

public class COSMOSensorInstance extends SensorInstance {

	private boolean isCOSMOSensor;

	private MinMaxPair minMaxPair;

	transient private Model model;
	
	public COSMOSensorInstance(int aPgn, int aSpn, Vehicle vehicle, int zvalueWindowSize) {
		super(aPgn, aSpn, vehicle,zvalueWindowSize);	
		//creates data structures/model to store/represent sensor data
		this.model = new Model();
	}

	public COSMOSensorInstance(Sensor s, Vehicle vehicle, int zvalueWindowSize) {
		super(s, vehicle,zvalueWindowSize);
		//creates data structures/model to store/represent sensor data
		this.model = new Model();
	}
	
	//note that zscore not used in cosmo algorithms (they computed and output, without storing)

	public boolean isCOSMOSensor() {
		return isCOSMOSensor;
	}

	public void setCOSMOSensor(boolean isCOSMOSensor) {
		this.isCOSMOSensor = isCOSMOSensor;
	}


	public Model getModel() {
		return model;
	}

	public MinMaxPair getMinMaxPair() {
		return minMaxPair;
	}

	public void setMinMaxPair(MinMaxPair minMaxPair) {
		this.minMaxPair = minMaxPair;
	}

	
	
}
