package phase.generation.data;

import java.io.Serializable;

import common.Sensor;
import common.SensorBehavior;
import common.exception.ConfigurationException;

public class DataGenerationSensor extends Sensor  implements Serializable{

	public enum Status {FAULT_INVOLVED,NORMAL};
	private Status status;
	private SensorBehavior normalBehavior;
	transient private SensorBehavior faultInvolvedBehavior;
	
	public DataGenerationSensor(int aPgn, int aSpn, SensorBehavior normalBehavior, SensorBehavior faultInvolvedBehavior) {
		super(aPgn, aSpn);
		if(normalBehavior == null){
			throw new ConfigurationException("failed to create datagenerationsesnro, due to null normal behavior.");
		}
		status = Status.NORMAL; 
		
		
		this.normalBehavior = normalBehavior;
		this.faultInvolvedBehavior = faultInvolvedBehavior;
	}
	
	public DataGenerationSensor(int aPgn, int aSpn, SensorBehavior normalBehavior){
		this(aPgn,aSpn,normalBehavior,null);
	}
	
	public boolean isFaultInvolved(){
		return status == Status.FAULT_INVOLVED;
	}
	
	public boolean isNormal(){
		return status == Status.NORMAL;
	}
	
	public void setFaultInvolved(){
		status = Status.FAULT_INVOLVED;
	}
	
	public void setNormal(){
		status = Status.NORMAL;
	}
	
	public SensorBehavior getCurrentBehavior(){
		if(status == Status.NORMAL){
			return normalBehavior;
		}else{
			return faultInvolvedBehavior;
		}
	}

	public SensorBehavior getNormalBehavior() {
		return normalBehavior;
	}

	public void setNormalBehavior(SensorBehavior normalBehavior) {
		this.normalBehavior = normalBehavior;
	}

	public SensorBehavior getFaultInvolvedBehavior() {
		return faultInvolvedBehavior;
	}

	public void setFaultInvolvedBehavior(SensorBehavior faultInvolvedBehavior) {
		this.faultInvolvedBehavior = faultInvolvedBehavior;
	}

	@Override
	public String toString() {
		return "DataGenerationSensor [("+this.getPgn()+","+this.getSpn()+"), normalBehavior=" + normalBehavior + "]";
	}
	
	
	
}
