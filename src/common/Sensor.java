/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common;

import java.io.Serializable;

import common.exception.ConfigurationException;

// line 76 "model.ump"
// line 157 "model.ump"
public class Sensor implements Comparable<Sensor>, Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Sensor Attributes
  private int pgn;
  private int spn;

 
  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Sensor(int aPgn, int aSpn)
  {
	  validateId(aPgn);
	  validateId(aSpn);
    pgn = aPgn;
    spn = aSpn;
  }

  public Sensor(Sensor aSensor){
	  if(aSensor == null){
		  throw new ConfigurationException("could not create sensor, null sensor argument.");
	  }
	  pgn = aSensor.getPgn();
	  spn = aSensor.getSpn();
  }
  
  
  private static void validateId(int id){
	  if(id < 0 ){
		  throw new ConfigurationException("illegal id, id is negative.");
	  }
  }
  //------------------------
  // INTERFACE
  //------------------------

  public boolean setPgn(int aPgn)
  {
	validateId(aPgn);
    boolean wasSet = false;
    pgn = aPgn;
    wasSet = true;
    return wasSet;
  }

  public boolean setSpn(int aSpn)
  {
	  validateId(aSpn);
    boolean wasSet = false;
    spn = aSpn;
    wasSet = true;
    return wasSet;
  }

  public int getPgn()
  {
    return pgn;
  }

  public int getSpn()
  {
    return spn;
  }

  
  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + pgn;
	result = prime * result + spn;
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (!( obj instanceof Sensor))
		return false;
	Sensor other = (Sensor) obj;
	if (pgn != other.pgn)
		return false;
	if (spn != other.spn)
		return false;
	return true;
}

/**
   * Compares two sensors.
   * @param o The other sensors
   * @return A negative integer, zero, or a positive integer as this sensor
     *          is less than, equal to, or greater than the supplied sensor object.
   */
  public int compareTo(Sensor o){
	  //mismatched parameter group numbers?
	if(this.getPgn() != o.getPgn()){
		return this.getPgn() - o.getPgn();
	}else if(this.getSpn() != o.getSpn()){
		return this.getSpn() - o.getSpn();
	}else{
		return 0;
	}
		
  }
  
public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "pgn" + ":" + getPgn()+ "," +
            "spn" + ":" + getSpn()+ "]";
  }
}