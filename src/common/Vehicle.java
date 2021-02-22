/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/


package common;

import java.io.Serializable;

import common.exception.ConfigurationException;

// line 81 "model.ump"
// line 162 "model.ump"
public class Vehicle implements Comparable<Vehicle>,Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Vehicle Attributes
  private int vid;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Vehicle(int aVid)
  {
	  if(aVid < 0){
			throw new ConfigurationException("failed to create Vehicle, illegal id: "+aVid);
		}
    vid = aVid;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setVid(int aVid)
  {
	  if(aVid < 0){
			throw new ConfigurationException("failed to set Vehicle id, illegal id: "+aVid);
		}
    boolean wasSet = false;
    vid = aVid;
    wasSet = true;
    return wasSet;
  }

  public int getVid()
  {
    return vid;
  }

  public void delete()
  {}


  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + vid;
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (!( obj instanceof Vehicle))
		return false;
	Vehicle other = (Vehicle) obj;
	if (vid != other.vid)
		return false;
	return true;
}

  public int compareTo(Vehicle o){
	return this.getVid() - o.getVid();
		
  }
  public String toString()
  {
    return super.toString() + "["+
            "vid" + ":" + getVid()+ "]";
  }
}