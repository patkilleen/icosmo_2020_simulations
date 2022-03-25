/*This product contains SAE International copyrighted intellectual property, 
which has been and is licensed with permission for use by Patrick Killeen, 
in this application only. No further sharing, duplicating, or transmitting is permitted.*/

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4359.01582bf35 modeling language!*/

package phase.configuraiton.input.j1939;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

// line 41 "model.ump"
// line 87 "model.ump"
public class SensorDiscovery implements J1939Reader
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  private SensorDescriptionMap existingSensors;
  private long existingSensorsNum;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SensorDiscovery(SensorDescriptionMap aExistingSensors)
  {
    if (!setExistingSensors(aExistingSensors))
    {
      throw new RuntimeException("Unable to create Main due to aExistingSensors");
    }
  }

  public SensorDiscovery()
  {
	  existingSensors = new SensorDescriptionMap();
  }
  
  
  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetOne */

  /* Code from template association_GetOne */
  public SensorDescriptionMap getExistingSensors()
  {
    return existingSensors;
  }

  /* Code from template association_SetUnidirectionalOne */
  public boolean setExistingSensors(SensorDescriptionMap aNewExistingSensors)
  {
    boolean wasSet = false;
    if (aNewExistingSensors != null)
    {
      existingSensors = aNewExistingSensors;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    existingSensors = null;
  }

  @Override
  public void readSensorValue(long timeStamp,int pgn,int spn, double value, SensorDescription sd){
	  //new sensro?
	  if(!this.existingSensors.exists(pgn, spn)){
		  //record existance
		  this.existingSensors.putSensorDescription(pgn, spn, sd);
		 
	  }
	  existingSensorsNum++;
  }
  
  
  public long getExistingSensorsNum(){
	  return existingSensorsNum;
  }
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 43 "model.ump"
  void  displayExistingSensors(){
	  
	  Set<Integer> pgnSet = this.existingSensors.keySet();
	  Iterator<Integer> it = pgnSet.iterator();
	  
	  System.out.println("pgn,spn,pgn label, spn label");
	  //iterate pgns
	  while(it.hasNext()){
		
		  int pgn = it.next();
		  Set<Integer> spnSet = this.existingSensors.getAllSPN(pgn);
		  if(spnSet == null){
			  continue;
		  }
		  Iterator<Integer> itspn = spnSet.iterator();
		  
		  //iterate pgns
		  while(itspn.hasNext()){
			  int spn = itspn.next();
			  
			  SensorDescription sd = this.existingSensors.getSensorDescription(pgn, spn);
			  System.out.println(pgn+","+spn+","+sd.getPgName()+","+sd.getName());
		  }//end iterate spns
		  
	  }//end iterate pgn
  }
  
// line 44 "model.ump"
  int logExistingSensors(Path outputFile) {
	  return -1;
  }

}