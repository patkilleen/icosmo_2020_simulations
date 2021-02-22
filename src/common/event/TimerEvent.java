/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common.event;

import java.io.Serializable;

import common.Sensor;
import common.Vehicle;

// line 6 "model.ump"
// line 90 "model.ump"
public class TimerEvent extends MessageEvent implements Comparable<TimerEvent>, Serializable
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//TimerEvent Attributes
	private int time;


	//------------------------
	// CONSTRUCTOR
	//------------------------

	public TimerEvent(int aTime)
	{
		super();
		time = aTime;
	}

	//------------------------
	// INTERFACE
	//------------------------


	public boolean setTime(int aTime)
	{
		boolean wasSet = false;
		time = aTime;
		wasSet = true;
		return wasSet;
	}


	public int getTime()
	{
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TimerEvent))
			return false;
		TimerEvent other = (TimerEvent) obj;
		if (time != other.time)
			return false;
		return true;
	}
	
	 public int compareTo(TimerEvent o){
		 
		return this.getTime() - o.getTime();	
	  }
	 
	public void delete()
	{
		super.delete();
	}


	public String toString()
	{
		return super.toString() + "["+
				"time" + ":" + getTime()+ "]";
	}
}