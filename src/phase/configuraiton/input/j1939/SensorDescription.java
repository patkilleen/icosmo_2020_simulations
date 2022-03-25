/*This product contains SAE International copyrighted intellectual property, 
which has been and is licensed with permission for use by Patrick Killeen, 
in this application only. No further sharing, duplicating, or transmitting is permitted.*/

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4359.01582bf35 modeling language!*/
package phase.configuraiton.input.j1939;

// line 9 "model.ump"
// line 70 "model.ump"
public class SensorDescription
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //SensorDescription Attributes
  private double m;
  private double b;
  private int offset;
  private int length;
  private int pgn;
  private int spn;
  private String name;
  private String pgName;
  private String unit;
  //opperational minimum
  private double oppMin;
//opperational maximum
  private double oppMax;
  
  private Double max;
  

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SensorDescription(double aM, double aB, int aOffset, int aLength, String aName, String aPgName)
  {
    m = aM;
    b = aB;
    offset = aOffset;
    length = aLength;
    name = aName;
    pgName = aPgName;
    max = null;
  }

  public SensorDescription()
  {
	  max = null;
  }
  
  //------------------------
  // INTERFACE
  //------------------------

  
  
  public boolean setM(double aM)
  {
    boolean wasSet = false;
    m = aM;
    wasSet = true;
    return wasSet;
  }

  public String getUnit() {
	return unit;
}

public void setUnit(String unit) {
	this.unit = unit;
}

public int getPgn() {
	return pgn;
}

public void setPgn(int pgn) {
	this.pgn = pgn;
}

public int getSpn() {
	return spn;
}

public void setSpn(int spn) {
	this.spn = spn;
}

public boolean setB(double aB)
  {
    boolean wasSet = false;
    b = aB;
    wasSet = true;
    return wasSet;
  }


  public Double getMax() {
	return max;
}

public void setMax(Double max) {
	this.max = max;
}

public double getMin() {
	return this.getB();
}


public boolean setOffset(int aOffset)
  {
    boolean wasSet = false;
    offset = aOffset;
    wasSet = true;
    return wasSet;
  }

  public boolean setLength(int aLength)
  {
    boolean wasSet = false;
    length = aLength;
    wasSet = true;
    return wasSet;
  }

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setPgName(String aPgName)
  {
    boolean wasSet = false;
    pgName = aPgName;
    wasSet = true;
    return wasSet;
  }

  public double getM()
  {
    return m;
  }

  public double getB()
  {
    return b;
  }

  public int getOffset()
  {
    return offset;
  }

  public int getLength()
  {
    return length;
  }

  public String getName()
  {
    return name;
  }

  public String getPgName()
  {
    return pgName;
  }

  
  public double getOppMin() {
	return oppMin;
}

public void setOppMin(double oppMin) {
	this.oppMin = oppMin;
}

public double getOppMax() {
	return oppMax;
}

public void setOppMax(double oppMax) {
	this.oppMax = oppMax;
}


/**
 * Returns true if this sensors has a range within which it should operate, and false otherwaise.
 * True indicates oppMax and oppMin represent the legal operational value the sensor should be reporting.
 * @return True when operational range available, false otherwise.
 */
public boolean hasOpperationalRange(){
	
	return (getOppMax() != 0) || (getOppMin() !=0);
}

/**
 * Returns true if the provided value is within operational range, or if no operational range is defined. False otherwise.
 * @param value Value to check if within range.
 * @return True when value in range. false otehrwise.
 */
public boolean inOperationalRange(double value){
	
	if(hasOpperationalRange()){
		return (value <= getOppMax()) && (value >= getOppMin());	
	}else{
		return true;
	}
	
}

public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "m" + ":" + getM()+ "," +
            "b" + ":" + getB()+ "," +
            "offset" + ":" + getOffset()+ "," +
            "length" + ":" + getLength()+ "," +
            "name" + ":" + getName()+ "," +
            "pgName" + ":" + getPgName()+ "]";
  }
}