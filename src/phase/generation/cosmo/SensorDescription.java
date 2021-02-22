package phase.generation.cosmo;

import java.io.Serializable;

import common.Sensor;
import common.exception.ConfigurationException;

public class SensorDescription extends Sensor implements Serializable{

	private MinMaxPair minMaxPair;

	
	public SensorDescription(int aPgn, int aSpn, MinMaxPair minMaxPair) {
		super(aPgn, aSpn);
		setMinMaxPair(minMaxPair);
	}

	public SensorDescription(Sensor aSensor,MinMaxPair minMaxPair) {
		super(aSensor);
		setMinMaxPair(minMaxPair);
	}

	public MinMaxPair getMinMaxPair() {
		return minMaxPair;
	}

	public void setMinMaxPair(MinMaxPair minMaxPair) {
		if(minMaxPair == null){
			throw new ConfigurationException("cannot create sensor description, min-max pair is null.");
		}
		this.minMaxPair = minMaxPair;
	}

}
