/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common;
import java.io.Serializable;
import java.util.*;

import common.exception.ConfigurationException;

// line 56 "model.ump"
// line 140 "model.ump"
public class FaultDescription implements Serializable
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//FaultDescription Attributes
	private int id;
	private String label;

	//FaultDescription Associations
	private List<FaultInvolvedSensorBehavior> affectedSensors;
	private List<Sensor> nonFaultInvolvedSensors;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public FaultDescription(int aId, String aLabel)
	{
		id = aId;
		label = aLabel;
		affectedSensors = new ArrayList<FaultInvolvedSensorBehavior>();
		nonFaultInvolvedSensors = new ArrayList<Sensor>(0);
	}

	//------------------------
	// INTERFACE
	//------------------------

	
	public void init(List<Sensor> allSensors){
		if(allSensors == null || allSensors.size() == 0){
			return;
		}
		
		List<Sensor> affectedSensors = this.getSensors();
		
		//make sure to compute a list of sensors not affected by fault
		for(Sensor s: allSensors){
			//not fault involved?
			if(!affectedSensors.contains(s)){
				nonFaultInvolvedSensors.add(s);
			}
		}
		
	}
	public List<Sensor> getSensors(){


		if(affectedSensors == null){
			throw new ConfigurationException("Could not retrieve sensor list from fault, since it was poorly configured (null affected sensor list)");
		}

		List<Sensor> res = new ArrayList<Sensor>(affectedSensors.size());

		//get all sensor behavior sensors and add to result
		for(FaultInvolvedSensorBehavior sb : affectedSensors){

			//new active senosr involved in fault
			Sensor s = sb.getAffectedSensor();
			res.add(s);
		}

		return res;
	}


	public List<Sensor> getNonFaultInvolvedSensors(){
		return nonFaultInvolvedSensors;
	}
	public boolean setId(int aId)
	{
		boolean wasSet = false;
		id = aId;
		wasSet = true;
		return wasSet;
	}

	public boolean setLabel(String aLabel)
	{
		boolean wasSet = false;
		label = aLabel;
		wasSet = true;
		return wasSet;
	}

	public int getId()
	{
		return id;
	}

	public String getLabel()
	{
		return label;
	}
	/* Code from template association_GetMany */
	public FaultInvolvedSensorBehavior getAffectedSensor(int index)
	{
		FaultInvolvedSensorBehavior aAffectedSensor = affectedSensors.get(index);
		return aAffectedSensor;
	}

	public List<FaultInvolvedSensorBehavior> getAffectedSensors()
	{
		List<FaultInvolvedSensorBehavior> newAffectedSensors = Collections.unmodifiableList(affectedSensors);
		return newAffectedSensors;
	}

	public int numberOfAffectedSensors()
	{
		int number = affectedSensors.size();
		return number;
	}

	public boolean hasAffectedSensors()
	{
		boolean has = affectedSensors.size() > 0;
		return has;
	}

	public int indexOfAffectedSensor(FaultInvolvedSensorBehavior aAffectedSensor)
	{
		int index = affectedSensors.indexOf(aAffectedSensor);
		return index;
	}
	/* Code from template association_MinimumNumberOfMethod */
	public static int minimumNumberOfAffectedSensors()
	{
		return 0;
	}
	/* Code from template association_AddUnidirectionalMany */
	public boolean addAffectedSensor(FaultInvolvedSensorBehavior aAffectedSensor)
	{
		boolean wasAdded = false;
		if (affectedSensors.contains(aAffectedSensor)) { return false; }
		affectedSensors.add(aAffectedSensor);
		wasAdded = true;
		return wasAdded;
	}

	public boolean removeAffectedSensor(FaultInvolvedSensorBehavior aAffectedSensor)
	{
		boolean wasRemoved = false;
		if (affectedSensors.contains(aAffectedSensor))
		{
			affectedSensors.remove(aAffectedSensor);
			wasRemoved = true;
		}
		return wasRemoved;
	}
	/* Code from template association_AddIndexControlFunctions */
	public boolean addAffectedSensorAt(FaultInvolvedSensorBehavior aAffectedSensor, int index)
	{  
		boolean wasAdded = false;
		if(addAffectedSensor(aAffectedSensor))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfAffectedSensors()) { index = numberOfAffectedSensors() - 1; }
			affectedSensors.remove(aAffectedSensor);
			affectedSensors.add(index, aAffectedSensor);
			wasAdded = true;
		}
		return wasAdded;
	}

	public boolean addOrMoveAffectedSensorAt(FaultInvolvedSensorBehavior aAffectedSensor, int index)
	{
		boolean wasAdded = false;
		if(affectedSensors.contains(aAffectedSensor))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfAffectedSensors()) { index = numberOfAffectedSensors() - 1; }
			affectedSensors.remove(aAffectedSensor);
			affectedSensors.add(index, aAffectedSensor);
			wasAdded = true;
		} 
		else 
		{
			wasAdded = addAffectedSensorAt(aAffectedSensor, index);
		}
		return wasAdded;
	}

	public void delete()
	{
		affectedSensors.clear();
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FaultDescription))
			return false;
		FaultDescription other = (FaultDescription) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	/**
	 * Checks if the id are equal, and the fault behavior is also equal
	 * @param other other fault description to check
	 * @return true when they are equal, including sensor behavior
	 */
	public boolean equalsAllSensorBehavior(FaultDescription other){
		if(!other.equals(this)){
			return false;
		}

		
		if(!Arrays.equals(this.affectedSensors.toArray(), other.affectedSensors.toArray())){
			return false;
		}
		
		if(!Arrays.equals(this.nonFaultInvolvedSensors.toArray(), other.nonFaultInvolvedSensors.toArray())){
			return false;
		}
		
		return true;
		
	}

	public String toString()
	{
		return super.toString() + "["+
				"id" + ":" + getId()+ "," +
				"label" + ":" + getLabel()+ "]";
	}
}