/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package phase.generation.cosmo;

import java.io.Serializable;

import common.Sensor;
import common.exception.ConfigurationException;

// line 76 "model.ump"
// line 157 "model.ump"
public class SensorInterest implements Comparable<SensorInterest>, Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

 private Sensor sensor;
 private double normalizedEntropy;
 private double stability;
 
 public SensorInterest(Sensor s, double ne, double stability ){
	this.setNormalizedEntropy(ne);
	this.setSensor(s);
	this.setStability(stability);
 }
  //------------------------
  // INTERFACE
  //------------------------


public Sensor getSensor() {
	return sensor;
}



public void setSensor(Sensor sensor) {
	 if(sensor == null ){
		 throw new ConfigurationException("failed to set sensor, null sensor.");
	 }
	this.sensor = sensor;
}



public double getNormalizedEntropy() {
	return normalizedEntropy;
}



public void setNormalizedEntropy(double normalizedEntropy) {
	 if(normalizedEntropy < 0 || normalizedEntropy > 1 ){
		 throw new ConfigurationException("failed to set normalized entropy, out of bounds, expected [0,1] but was: "+normalizedEntropy);
	 }
	this.normalizedEntropy = normalizedEntropy;
}



public double getStability() {
	return stability;
}



public void setStability(double stability) {
	 if(stability < 0 || stability > 1 ){
		 throw new ConfigurationException("failed to set stability, out of bounds, expected [0,1] but was: "+stability);
	 }
	this.stability = stability;
}


	/**
	 * 1- ne: cause want to have smallest number represent more interestingess. ne is [0,1]
	 * here smallest is most interesting
	*ne of 1 is most interesting, 0 is not interesting
	*stability 1 is not interesting, 0 is most interesting
	*so (1-ne): 0 is most interesting, 1 is least inteesting
	*so (1-ne) + stability: 0 is most interesting and 2 least interesting
	 * @return interest value between 0 and 1
	 */
	//
	public double computeInterestValue() {
		return ((1-this.normalizedEntropy) + this.stability)/2.0;//divide by 2 to make the value of intereste between 0 and 1
	}

/**
   * Compares two sensor interests, having higher normalized entropy and lower stability  be the "smallest"
   * @param o The other sensor interest
   * @return A negative integer, zero, or a positive integer as this sensor interest
     *          is more interesting than, equal to, or less interesting than the supplied sensor object.
   */
  public int compareTo(SensorInterest o){
	 
	 
	  double thisInterest = computeInterestValue();
	  double otherInterest = o.computeInterestValue();
	  
	 if(thisInterest == otherInterest){
		 return 0;
	 } else if(thisInterest < otherInterest){
		 return -1;
	 }else{
		 return 1;
	 }
		
  }

}