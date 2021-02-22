package phase.generation.history;

import common.Sensor;
import common.Vehicle;
import common.exception.ConfigurationException;

public class AnalysisSensor extends Sensor {

	private boolean sensorSelectionChange;
	private boolean isCosmoSensor;
	private double zscore;
	private int timeSensorSelectionChange;
	private double contribution;
	private double potentialContribution;
	private Vehicle vehicle;
	public AnalysisSensor(int aPgn, int aSpn, boolean sensorSelectionChange, boolean isCosmoSensor, double zscore,
			int timeSensorSelectionChange, double contribution,double potentialContribution, Vehicle vehicle) {
		super(aPgn, aSpn);
		this.sensorSelectionChange = sensorSelectionChange;
		this.isCosmoSensor = isCosmoSensor;
		this.setZscore(zscore);
		this.setTimeSensorSelectionChange(timeSensorSelectionChange);
		this.contribution = contribution;
		this.potentialContribution = potentialContribution;
		this.setVehicle(vehicle);
	}

	public AnalysisSensor(int aPgn, int aSpn, boolean sensorSelectionChange, boolean isCosmoSensor, double zscore,double contribution,double potentialContribution, Vehicle vehicle) {
		this(aPgn, aSpn,sensorSelectionChange,isCosmoSensor,zscore,0,contribution,potentialContribution,vehicle);//note: setting 0 to time sensor selection change temporarily
		this.timeSensorSelectionChange = -1;
		
	}

	public double getContribution() {
		return contribution;
	}

	public void setContribution(double contribution) {
		this.contribution = contribution;
	}

	public double getPotentialContribution() {
		return potentialContribution;
	}

	public void setPotentialContribution(double potentialContribution) {
		this.potentialContribution = potentialContribution;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		if(vehicle == null){
			throw new ConfigurationException("couldn't set vehicle due to null pointer.");
		}
		this.vehicle = vehicle;
	}

	public boolean isSensorSelectionChange() {
		return sensorSelectionChange;
	}
	public void setSensorSelectionChange(boolean sensorSelectionChange) {
		this.sensorSelectionChange = sensorSelectionChange;
	}
	public boolean isCosmoSensor() {
		return isCosmoSensor;
	}
	public void setCosmoSensor(boolean isCosmoSensor) {
		this.isCosmoSensor = isCosmoSensor;
	}
	public double getZscore() {
		return zscore;
	}
	public void setZscore(double zscore) {
		
		if(zscore < 0 || zscore > 1){
			throw new ConfigurationException("couldn't set zscore, illegal argument. Expected range [0,1] but was: ("+zscore+").");
		}
		this.zscore = zscore;
	}
	
	/**
	 * returns the time sensor selection changed or -1 if time not set.
	 * @return
	 */
	public int getTimeSensorSelectionChange() {
		
		return timeSensorSelectionChange;
	}
	public void setTimeSensorSelectionChange(int timeSensorSelectionChange) {
		
		if(timeSensorSelectionChange < 0){
			throw new ConfigurationException("couldn't set time sensor selection change, due to negative time");
		}
		this.timeSensorSelectionChange = timeSensorSelectionChange;
	}
	
	

}
