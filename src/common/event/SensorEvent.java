/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/


package common.event;

import common.Sensor;
import common.Vehicle;

// line 16 "model.ump"
// line 100 "model.ump"
public class SensorEvent extends Event
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //SensorEvent Associations
  private Sensor sensor;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SensorEvent(Sensor aSensor)
  {
    super();
    if (!setSensor(aSensor))
    {
      throw new RuntimeException("Unable to create SensorEvent due to aSensor");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetOne */
  public Sensor getSensor()
  {
    return sensor;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setSensor(Sensor aNewSensor)
  {
    boolean wasSet = false;
    if (aNewSensor != null)
    {
      sensor = aNewSensor;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    sensor = null;
    super.delete();
  }

}