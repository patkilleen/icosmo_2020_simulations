package common.event;

import common.Algorithm;
import common.Sensor;
import common.Vehicle;

public class SensorStatusEvent extends SensorInstanceEvent{

	
	private Algorithm algorithm;
	//the moving average
	private double zscore;
	
	//the current zvalue
	private double zvalue;
	/**
	 * Flag indicating whether the sensor is a cosmo sensor or not
	 */
	private boolean cosmoSensorFlag;

	public SensorStatusEvent(Vehicle aVehicle, Sensor aSensor, Algorithm algorithm, double zscore, boolean cosmoSensorFlag, double zvalue) {
		super(aVehicle, aSensor);
		
		if(zscore < 0 || zscore >1){
			throw new IllegalArgumentException("failed to create ZScoreEvent, due to zscore not in range : [0,1]");
		}
		this.algorithm = algorithm;
		this.zscore = zscore;
		this.cosmoSensorFlag = cosmoSensorFlag;
		this.zvalue = zvalue;
	}

	public boolean isDeviating(double threshold){
		
		return this.getZscore() <= threshold;
	
	}


	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public double getZscore() {
		return zscore;
	}

	public void setZscore(double zscore) {
		this.zscore = zscore;
	}

	public boolean isCosmoSensorFlag() {
		return cosmoSensorFlag;
	}

	public void setCosmoSensorFlag(boolean cosmoSensorFlag) {
		this.cosmoSensorFlag = cosmoSensorFlag;
	}

	public double getZvalue() {
		return zvalue;
	}

	public void setZvalue(double zvalue) {
		this.zvalue = zvalue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + (cosmoSensorFlag ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(zscore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(zvalue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SensorStatusEvent))
			return false;
		SensorStatusEvent other = (SensorStatusEvent) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (cosmoSensorFlag != other.cosmoSensorFlag)
			return false;
		if (Double.doubleToLongBits(zscore) != Double.doubleToLongBits(other.zscore))
			return false;
		if (Double.doubleToLongBits(zvalue) != Double.doubleToLongBits(other.zvalue))
			return false;
		return true;
	}
	
	
	
	

}
