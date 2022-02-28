package phase.analysis.icosmo;

import common.Sensor;
import common.SensorInstance;
import common.Vehicle;
import phase.generation.cosmo.SensorInterest;

public class ICOSMOSensorInstance extends SensorInstance{

	//icosmo attributes
	private double contribution;
	private double potentialContribution;
	
	private SensorInterest interestingness;//the interestingness of the sensor isntance's sensro class
	
	
	/**
	 * Flag used to indicate whether this sensor was removed/added from 
	 * selected sensors by icosmo 
	 */
	private boolean hasSensorSelectionChanged;
	
	/**
	 * The timestamp (time) when the sensor's sensor selection status was change by icosmo 
	 */
	private int selectChangeTimeStamp;
	//sensor status attributes
	private boolean isCosmoSensor;
	private double zscore;
	
	public ICOSMOSensorInstance(int aPgn, int aSpn, Vehicle vehicle, int zvalueWindowSize) {
		super(aPgn, aSpn, vehicle,zvalueWindowSize);
		init();
	}
	
	public ICOSMOSensorInstance(Sensor s, Vehicle vehicle,int zvalueWindowSize) {
		super(s, vehicle,zvalueWindowSize);
		init();
	}

	public ICOSMOSensorInstance(Sensor s, Vehicle vehicle, double zscore, boolean isCosmoSensor,int zvalueWindowSize) {
		super(s, vehicle,zvalueWindowSize);
		init();
		this.zscore = zscore;
		this.isCosmoSensor = isCosmoSensor;
	}
	
	public void toggleIsCosmoSensor(){
		this.isCosmoSensor = !this.isCosmoSensor;
	}
	/**
	 * Sets sensor attribute to default values
	 */
	public void init(){
		
	}

	public SensorInterest getInterestingness() {
		return interestingness;
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

	public boolean isHasSensorSelectionChanged() {
		return hasSensorSelectionChanged;
	}

	public void setHasSensorSelectionChanged(boolean hasSensorSelectionChanged) {
		this.hasSensorSelectionChanged = hasSensorSelectionChanged;
	}

	public int getSelectChangeTimeStamp() {
		return selectChangeTimeStamp;
	}

	public void setSelectChangeTimeStamp(int selectChangeTimeStamp) {
		this.selectChangeTimeStamp = selectChangeTimeStamp;
	}

	public boolean isCosmoSensor() {
		return isCosmoSensor;
	}

	public void setCosmoSensor(boolean isCosmoSensor) {
		this.isCosmoSensor = isCosmoSensor;
	}

	public void setInterestingness(SensorInterest interestingness) {
		this.interestingness = interestingness;
	}
	
	@Override
	protected void clearZValues(){
		super.clearZValues();
	}

}
