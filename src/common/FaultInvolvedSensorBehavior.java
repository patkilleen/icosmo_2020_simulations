/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/


package common;

import java.io.Serializable;

import common.exception.ConfigurationException;

// line 69 "model.ump"
// line 151 "model.ump"
public class FaultInvolvedSensorBehavior extends SensorBehavior implements Serializable
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Type { MODIFY, NEW }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //FaultInvolvedSensorBehavior Attributes
  private Type affectedBehaviorType;

  //FaultInvolvedSensorBehavior Associations
  private Sensor affectedSensor;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double []noisePValues,Type aAffectedBehaviorType, Sensor aAffectedSensor)
  {
	  this(whiteNoise,ampFactor,noiseAmpFactor,noisePValues[0],noisePValues[1],aAffectedBehaviorType,aAffectedSensor);   
  }
  
  public FaultInvolvedSensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double noisePValue1,double noisePValue2,Type aAffectedBehaviorType, Sensor aAffectedSensor){
	  super(whiteNoise,ampFactor,noiseAmpFactor,noisePValue1,noisePValue2);
	    affectedBehaviorType = aAffectedBehaviorType;
	    setAffectedSensor(aAffectedSensor);
	   
  }

  //------------------------
  // INTERFACE
  //------------------------
  public void setModifyType(){
	  affectedBehaviorType = Type.MODIFY;
  }
  
  public void setNewType(){
	  affectedBehaviorType = Type.NEW;
  }
  
  public boolean isNewType(){
	  return affectedBehaviorType == Type.NEW; 
  }
  
  public boolean isModifyType(){
	  return affectedBehaviorType == Type.MODIFY;
  }
  

  public void setAffectedBehaviorType(Type type){
	  this.affectedBehaviorType = type;
  }
  
  public Type getAffectedBehaviorType(){
	  return this.affectedBehaviorType;
  }
  
  /* Code from template association_GetOne */
  public Sensor getAffectedSensor()
  {
    return affectedSensor;
  }
  /* Code from template association_SetUnidirectionalOne */
  public void setAffectedSensor(Sensor aNewAffectedSensor)
  {
    
    if (aNewAffectedSensor == null)
    {
    	throw new ConfigurationException("failed to set affecte sensor, due to null ptr");
    }
      affectedSensor = aNewAffectedSensor;
    
    
    
  }

  public void delete()
  {
    affectedSensor = null;
    super.delete();
  }


  
  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((affectedBehaviorType == null) ? 0 : affectedBehaviorType.hashCode());
	result = prime * result + ((affectedSensor == null) ? 0 : affectedSensor.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	 
	if(!super.equals(obj)){
		return false;
	}
	
	if (!(obj instanceof FaultInvolvedSensorBehavior))
		return false;
	FaultInvolvedSensorBehavior other = (FaultInvolvedSensorBehavior) obj;
	if (affectedBehaviorType != other.affectedBehaviorType)
		return false;
	if (affectedSensor == null) {
		if (other.affectedSensor != null)
			return false;
	} else if (!affectedSensor.equals(other.affectedSensor))
		return false;
	return true;
}

public String toString()
  {
    return super.toString() + "["+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "affectedBehaviorType" + "=" + this.affectedBehaviorType.toString().replaceAll("  ","    ") + System.getProperties().getProperty("line.separator") +
            "  " + "affectedSensor = "+(getAffectedSensor()!=null?Integer.toHexString(System.identityHashCode(getAffectedSensor())):"null");
  }

}