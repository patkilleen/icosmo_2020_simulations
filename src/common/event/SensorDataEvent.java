/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/
package common.event;

import common.Sensor;
import common.Vehicle;

// line 32 "model.ump"
// line 115 "model.ump"
public class SensorDataEvent extends SensorEvent
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//SensorDataEvent Attributes
	private double value;
	private double noise;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public SensorDataEvent( Sensor aSensor, double aValue)
	{
		super(aSensor);
		value = aValue;
	}

	//------------------------
	// INTERFACE
	//------------------------


	public boolean setValue(double aValue)
	{
		boolean wasSet = false;
		value = aValue;
		wasSet = true;
		return wasSet;
	}

	public double getNoise() {
		return noise;
	}

	public void setNoise(double noise) {
		this.noise = noise;
	}

	public double getValue()
	{
		return value;
	}

	public void delete()
	{
		super.delete();
	}


	public String toString()
	{
		return super.toString() + "["+
				"value" + ":" + getValue()+ "]";
	}
}