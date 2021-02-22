/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common;

import java.io.Serializable;
import java.util.Arrays;

import common.exception.ConfigurationException;

// line 63 "model.ump"
// line 146 "model.ump"
public class SensorBehavior implements Serializable
{

	public static final int NUMBER_NOISE_P_VALUES = 2;
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//SensorBehavior Attributes
	private Noise whiteNoise;

	private double ampFactor;
	private double noiseAmpFactor;
	private double [] noisePValues;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	

	//------------------------
	// INTERFACE
	//------------------------


	public Noise getWhiteNoise() {
		return whiteNoise;
	}

	public SensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double [] noisePValues) {
		this(whiteNoise,ampFactor,noiseAmpFactor,validateNoisePValues(noisePValues,0),validateNoisePValues(noisePValues,1));
	}
	public SensorBehavior(Noise whiteNoise, double ampFactor, double noiseAmpFactor, double noisePValue1, double noisePValue2) {
		super();
		double [] noisePValues = new double[NUMBER_NOISE_P_VALUES];
		noisePValues[0]=noisePValue1;
		noisePValues[1]=noisePValue2;
		if(whiteNoise == null ){
			throw new ConfigurationException("could not create SensorBehavior due to null arguments or illformed noise p values");
		}
		
		
		this.whiteNoise = whiteNoise;
		this.ampFactor = ampFactor;
		this.noiseAmpFactor = noiseAmpFactor;
		this.setNoisePValues(noisePValues);
	}
	

	/**
	 * Validates the noisePValue array, and returns desired pvalue given index.
	 * @param noisePValues Pvalue array to check validity
	 * @param returnIx Index of double in noisePValues to return
	 * @return The desired pValue
	 */
	private static double validateNoisePValues(double[] noisePValues, int returnIx){
		validateNoisePValues(noisePValues);
		
		return noisePValues[returnIx];
	}
	
	private static void validateNoisePValues(double[] noisePValues){
		if( noisePValues == null){
			throw new ConfigurationException("illformed noise p values, null p values");
		}
		if( noisePValues.length != NUMBER_NOISE_P_VALUES){
			throw new ConfigurationException("illformed noise p values, expected ("+NUMBER_NOISE_P_VALUES+") but found ("+noisePValues.length+")");
		}
		
		//make sure they are infact p values ([0,1])
		for(int i = 0;i<noisePValues.length;i++){
			if((noisePValues[i] > 1) || noisePValues[i] < 0){
				throw new ConfigurationException("illformed noise p values, expected in range [0,1] but was ("+noisePValues[i]+")"); 
			}
		}
		
	}
	
	/**
	 * Adds sensor behavior to this sensor behavior. All attributes are added togehther, but the base noise p values are unchanged.
	 * @param other Sensor behavior to add to this sensor behavior
	 * @return Sum of the sensor behavior
	 */
	public SensorBehavior addSensorBehavior(SensorBehavior other){
		
		Noise wn1 = this.getWhiteNoise();
		Noise wn2 = other.getWhiteNoise();
		Noise whiteNoise = wn1.addWhiteNoise(wn2);
		double ampFactor = this.getAmpFactor() + other.getAmpFactor();
		double noiseAmpFactor = this.getNoiseAmpFactor() + other.getNoiseAmpFactor();
		
		return new SensorBehavior(whiteNoise,ampFactor,noiseAmpFactor,noisePValues[0],noisePValues[1]);
	}

	
	public double[] getNoisePValues() {
		double [] res = new double[noisePValues.length];
		for(int i = 0;i<noisePValues.length;i++){
			res[i] = noisePValues[i];
		}
		return res;
	}

	public void setNoisePValues(double[] noisePValues) {
		validateNoisePValues(noisePValues);
		
		
		this.noisePValues = new double[noisePValues.length];
		for(int i = 0;i<noisePValues.length;i++){
			this.noisePValues[i] = noisePValues[i];
		}
	}

	public void setWhiteNoise(Noise whiteNoise) {
		if(whiteNoise == null){
			throw new ConfigurationException("failed to set white noise due to null pointeur");
		}
		this.whiteNoise = whiteNoise;
	}

	public double getAmpFactor() {
		return ampFactor;
	}

	public void setAmpFactor(double ampFactor) {
		this.ampFactor = ampFactor;
	}

	public double getNoiseAmpFactor() {
		return noiseAmpFactor;
	}

	public void setNoiseAmpFactor(double noiseAmpFactor) {
		this.noiseAmpFactor = noiseAmpFactor;
	}

	public void delete()
	{}

	@Override
	public String toString() {
		return "SensorBehavior [whiteNoise=" + whiteNoise + ", ampFactor=" + ampFactor + ", noiseAmpFactor="
				+ noiseAmpFactor + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(ampFactor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(noiseAmpFactor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(noisePValues);
		result = prime * result + ((whiteNoise == null) ? 0 : whiteNoise.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SensorBehavior))
			return false;
		SensorBehavior other = (SensorBehavior) obj;
		if (Double.doubleToLongBits(ampFactor) != Double.doubleToLongBits(other.ampFactor))
			return false;
		if (Double.doubleToLongBits(noiseAmpFactor) != Double.doubleToLongBits(other.noiseAmpFactor))
			return false;
		if (!Arrays.equals(noisePValues, other.noisePValues))
			return false;
		if (whiteNoise == null) {
			if (other.whiteNoise != null)
				return false;
		} else if (!whiteNoise.equals(other.whiteNoise))
			return false;
		return true;
	}


	
	//------------------------
	// DEVELOPER CODE - PROVIDED AS-IS
	//------------------------

	// line 64 "model.ump"


}