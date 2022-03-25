/*PLEASE DO NOT EDIT THIS CODE*/
/*This product contains SAE International copyrighted intellectual property, 
which has been and is licensed with permission for use by Patrick Killeen, 
in this application only. No further sharing, duplicating, or transmitting is permitted.*/

/*This code was generated using the UMPLE 1.29.1.4359.01582bf35 modeling language!*/
package phase.configuraiton.input.j1939;
// line 20 "model.ump"
public interface J1939Reader
{
  
  // ABSTRACT METHODS 
/**
 * This function is called when a sensor reading is decoded and processed.
 * @param timerStamp The timestamp in milliseconds of the sensor readings, since the data acquisition session began.
 * @param pgn the J1939 parameter group number of the sensor
 * @param spn the J1939 suspect parameter number of the sensor
 * @param decodedValue the sensor reading
 * @param sd description of the sensor, which has meta data such as (max values, min value, sensor name, etc.)
 */
 public void readSensorValue(long timerStamp,int pgn,int spn,double decodedValue, SensorDescription sd);
}