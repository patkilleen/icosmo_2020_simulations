/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4359.01582bf35 modeling language!*/

package phase.configuraiton.input.j1939;

import java.util.HashMap;
import java.util.Set;

// line 24 "model.ump"
// line 75 "model.ump"
//<pgn,<spn,desc>>
public class SensorDescriptionMap extends HashMap<Integer,HashMap<Integer,SensorDescription>>
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SensorDescriptionMap()
  {}

  //------------------------
  // INTERFACE
  //------------------------

  public void delete()
  {}
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 25 "model.ump"
  /**
   * Adds a sensor description to the sensor map.
   * @param pgn Paramater group number of sensor description (id of group of sensors)
   * @param spn Suspect parameter number of the sensor description (id of sensor)
   * @param sd Description of the sensor with meta data describing the sensor 
   * @return true when the description already existed (and was replaced) in map, and false if the description didn't exist
   */
  public boolean putSensorDescription(int pgn,int spn, SensorDescription sd){
	  
	  //pgn map already exists?
	  if(this.containsKey(pgn)){
		  
		  //add to pgn map of sensors
		  
		  HashMap<Integer,SensorDescription> pgSensors = this.get(pgn);
		  //add the sd to pg sensor map
		  if (pgSensors.put(spn, sd) != null){
			  //already existed, replace it
			  return true;
		  }else{
			  //new sensor added
			  return false;
		  }
	  }else{
		  //create pgn sensor map
		  HashMap<Integer,SensorDescription> pgSensors = new HashMap<Integer,SensorDescription>(); 
		  pgSensors.put(spn, sd);
		  this.put(pgn, pgSensors);
		  
		  //new sensor added
		  return false;
	  }
	  
  }
// line 26 "model.ump"
  /**
   * Finds the SensorDescription stored in the map.
   * @param pgn pgn Paramater group number of sensor description (id of group of sensors)
   * @param spn Suspect parameter number of the sensor description (id of sensor)
   * @return SensorDescription found or null if it doesn't exist in map.
   */
  public SensorDescription getSensorDescription(int pgn,int spn){ 
  
	  //pgn map already exists?
	  if(this.containsKey(pgn)){
		  HashMap<Integer,SensorDescription> pgSensors = this.get(pgn);
		  //sd exists?
		  if (pgSensors.containsKey(spn)){
			  return pgSensors.get(spn);
		  }else{
			  //
			  return null;
		  }
	  }else{
		  return null;
	  }
  }
// line 27 "model.ump"
  /**
   * Determines if a SensorDescription is stored in the map.
   * @param pgn pgn Paramater group number of sensor description (id of group of sensors)
   * @param spn Suspect parameter number of the sensor description (id of sensor)
   * @return true when it is found or false if it doesn't exist in map.
   */
  public boolean exists(int pgn, int spn){
	//pgn map already exists?
	  if(this.containsKey(pgn)){
		  HashMap<Integer,SensorDescription> pgSensors = this.get(pgn);
		  //sd exists?
		  if (pgSensors.containsKey(spn)){
			  return true;
		  }else{
			  //
			  return false;
		  }
	  }else{
		  return false;
	  }
  }

  /**
   * Get all the suspect parameter numbers (sensor ids) for a parameter group.
   * @param pgn Paramger group, id of the group of sensors
   * @return Set of suspect parameter numbers in parameter group
   */
  public  Set<Integer> getAllSPN(int pgn){
	//pgn map already exists?
	  if(this.containsKey(pgn)){
		  HashMap<Integer,SensorDescription> pgSensors = this.get(pgn);
		  return pgSensors.keySet();
	  }else{
		  return null;
	  }
  }
  
}