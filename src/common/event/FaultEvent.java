/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common.event;

import common.FaultDescription;
import common.Vehicle;

// line 21 "model.ump"
// line 105 "model.ump"
public class FaultEvent extends VehicleEvent
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //FaultEvent Associations
  private FaultDescription faultDescription;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public FaultEvent(Vehicle aVehicle, FaultDescription aFaultDescription)
  {
    super(aVehicle);
    if (!setFaultDescription(aFaultDescription))
    {
      throw new RuntimeException("Unable to create FaultEvent due to aFaultDescription");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetOne */
  public FaultDescription getFaultDescription()
  {
    return faultDescription;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setFaultDescription(FaultDescription aNewFaultDescription)
  {
    boolean wasSet = false;
    if (aNewFaultDescription != null)
    {
      faultDescription = aNewFaultDescription;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    faultDescription = null;
    super.delete();
  }

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((faultDescription == null) ? 0 : faultDescription.hashCode());
	return result;
}
  
}