/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package phase.generation.fault;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Vehicle;
import common.event.TimerEvent;

// line 14 "model.ump"
// line 81 "model.ump"
public class FaultGenerationVehicle extends Vehicle implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //FaultGenerationVehicle Associations
  private List<Fault> potentialFaults;

  private List<Fault> activeFaults;
  
  private List<Fault> inactiveFaults;
  
  //used to store temporary faults to activate or repair
  private List<Fault> temporaryActiveFaultBuffer;
  private List<Fault> temporaryInactiveFaultBuffer;
	
  //------------------------
  // CONSTRUCTOR
  //------------------------

  public FaultGenerationVehicle(int aVid)
  {
    super(aVid);
    potentialFaults = new ArrayList<Fault>(32);
    inactiveFaults = new ArrayList<Fault>(32);
    activeFaults = new ArrayList<Fault>(32);
    temporaryActiveFaultBuffer = new ArrayList<Fault>(32);
    temporaryInactiveFaultBuffer = new ArrayList<Fault>(32);
  }

  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetMany */
  public Fault getPotentialFault(int index)
  {
    Fault aPotentialFault = potentialFaults.get(index);
    return aPotentialFault;
  }

  public List<Fault> getPotentialFaults()
  {
    List<Fault> newPotentialFaults = Collections.unmodifiableList(potentialFaults);
    return newPotentialFaults;
  }

  public int numberOfPotentialFaults()
  {
    int number = potentialFaults.size();
    return number;
  }

  public boolean hasPotentialFaults()
  {
    boolean has = potentialFaults.size() > 0;
    return has;
  }

  public int indexOfPotentialFault(Fault aPotentialFault)
  {
    int index = potentialFaults.indexOf(aPotentialFault);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPotentialFaults()
  {
    return 0;
  }
  /* Code from template association_AddUnidirectionalMany */
  public boolean addPotentialFault(Fault aPotentialFault)
  {
    boolean wasAdded = false;
    if (potentialFaults.contains(aPotentialFault)) { return false; }
    potentialFaults.add(aPotentialFault);
    inactiveFaults.add(aPotentialFault);
    wasAdded = true;
    return wasAdded;
  }


  public void activateFault(Fault f, TimerEvent timerEvent){
	  f.activate(timerEvent.getTime());
	  
	  //activeFaults.add(f);
	 // inactiveFaults.remove(f);
	  temporaryActiveFaultBuffer.add(f);
  }
  
  public void flushActivatedFaults(){
	  activeFaults.addAll(temporaryActiveFaultBuffer);
	  inactiveFaults.removeAll(temporaryActiveFaultBuffer);
	  temporaryActiveFaultBuffer.clear();  
  }
  
  public void flushRepairedFaults(){
	  inactiveFaults.addAll(temporaryInactiveFaultBuffer);
	  activeFaults.removeAll(temporaryInactiveFaultBuffer);
	  temporaryInactiveFaultBuffer.clear();  
  }
  
  public void repairFault(Fault f){
	  
	  f.deactivate();
	  temporaryInactiveFaultBuffer.add(f);
	  //activeFaults.remove(f);
	  //inactiveFaults.add(f);
  }
  
  public List<Fault> getActiveFaults(){
	  return activeFaults;
  }
  
  public List<Fault> getInactiveFaults(){
	  return inactiveFaults;
  }

  public void delete()
  {
    potentialFaults.clear();
    super.delete();
  }


}