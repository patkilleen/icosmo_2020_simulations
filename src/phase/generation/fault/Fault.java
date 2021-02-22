/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package phase.generation.fault;

import java.io.Serializable;
import java.util.List;

import common.FaultDescription;
import common.Sensor;
import common.exception.ConfigurationException;

// line 2 "model.ump"
// line 74 "model.ump"
public class Fault implements Serializable
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Status { ACTIVE, INACTIVE }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Fault Attributes
  private int minDaysBeforeRepair;
  private double occurencePValue;
  private double repairPValue;
  private Status status;
  private int occurenceDay;

  //Fault Associations
  private FaultDescription faultDescription;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Fault(int aMinDaysBeforeRepair, double aOccurencePValue, double aRepairPValue, FaultDescription aFaultDescription)
  {
	  if(aMinDaysBeforeRepair < 0 || aOccurencePValue < 0 || aOccurencePValue > 1 || aRepairPValue < 0 || aRepairPValue > 1 ){
		  throw new ConfigurationException("illegal argument, cannot create fault.");
	  }
	  setFaultDescription(aFaultDescription);
    minDaysBeforeRepair = aMinDaysBeforeRepair;
    occurencePValue = aOccurencePValue;
    repairPValue = aRepairPValue;
    status = Status.INACTIVE;
    
 
  }

  //------------------------
  // INTERFACE
  //------------------------

  /**
   * Returns a list of fault involved sensors by iterating all the sensor behavior of this fault's description.
   * @return List of fault invovled sensors.
   */
	public List<Sensor> getSensors(){
		

		FaultDescription fd = getFaultDescription();
		if(fd == null){
			throw new ConfigurationException("Could not retrieve sensor list from fault, since it was poorly configured (null FaultDescription)");
		}
		
		return fd.getSensors();
		
	}
	
	
	public boolean isReadyForRepair(int day){
		if(this.isInactive()){
			return false;
		}
		return (day - this.getOccurenceDay()) >= this.getMinDaysBeforeRepair();
	}
  public boolean isActive(){
	  return status == Status.ACTIVE;
  }
  
  public boolean isInactive(){
	  return status == Status.INACTIVE;
  }
  
  /**
   * Activates the fault.
   * @param aOccurenceDay The timestamp when the fault occured.
   */
  public void activate(int aOccurenceDay){
	  this.setOccurenceDay(aOccurenceDay);
	  status = Status.ACTIVE;
  }
  
  public void deactivate(){
	  status = Status.INACTIVE;
  }
  

  private boolean setOccurenceDay(int aOccurenceDay)
  {
    boolean wasSet = false;
    occurenceDay = aOccurenceDay;
    wasSet = true;
    return wasSet;
  }

  public int getMinDaysBeforeRepair()
  {
    return minDaysBeforeRepair;
  }

  public double getOccurencePValue()
  {
    return occurencePValue;
  }

  public double getRepairPValue()
  {
    return repairPValue;
  }

  private Status getStatus()
  {
    return status;
  }

  public int getOccurenceDay()
  {
    return occurenceDay;
  }
  /* Code from template association_GetOne */
  public FaultDescription getFaultDescription()
  {
    return faultDescription;
  }
  /* Code from template association_SetUnidirectionalOne */
  public void setFaultDescription(FaultDescription aNewFaultDescription)
  {
    
    if (aNewFaultDescription != null)
    {
      faultDescription = aNewFaultDescription;
    
    }else{
    	throw new ConfigurationException("cannot set fault descrption due to null ptr");
    }
    
  }


@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (!(obj instanceof Fault))
		return false;
	Fault other = (Fault) obj;
	if (faultDescription == null) {
		if (other.faultDescription != null)
			return false;
	} else if (!faultDescription.equals(other.faultDescription))
		return false;
	return true;
}

public String toString()
  {
    return super.toString() + "["+
            "minDaysBeforeRepair" + ":" + getMinDaysBeforeRepair()+ "," +
            "occurencePValue" + ":" + getOccurencePValue()+ "," +
            "repairPValue" + ":" + getRepairPValue()+ "," +
            "occurenceDay" + ":" + getOccurenceDay()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "status" + "=" + (getStatus() != null ? !getStatus().equals(this)  ? getStatus().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "faultDescription = "+ faultDescription.getId();
  }
}