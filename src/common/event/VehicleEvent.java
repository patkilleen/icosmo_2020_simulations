/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common.event;

import java.io.Serializable;

import common.Vehicle;

// line 11 "model.ump"
// line 95 "model.ump"
public class VehicleEvent extends Event implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //VehicleEvent Associations
  private Vehicle vehicle;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public VehicleEvent(Vehicle aVehicle)
  {
    super();
    if (!setVehicle(aVehicle))
    {
      throw new RuntimeException("Unable to create VehicleEvent due to aVehicle");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetOne */
  public Vehicle getVehicle()
  {
    return vehicle;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setVehicle(Vehicle aNewVehicle)
  {
    boolean wasSet = false;
    if (aNewVehicle != null)
    {
      vehicle = aNewVehicle;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    vehicle = null;
    super.delete();
  }

}