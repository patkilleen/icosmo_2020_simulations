package common;

import java.io.Serializable;
import java.util.Date;

import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.DRand;

public class Noise implements Serializable{

	public static int SLEEP_INTERVAL=2;
	public enum Distribution{
		GAUSSIAN,UNIFORM,NORMAL
	};
	
	private double noiseMean;
	private double noiseSD;
	private double noiseMax;
	private double noiseMin;
	
	private Normal randomNormal;
	private Uniform randomUniform;
	
	private DRand normalRandomEngine;
	private DRand uniformRandomEngine;
	
	private Distribution type;
	
	
	public Noise(double noiseMean, double noiseSD, double noiseMax, double noiseMin, Distribution type) {
		this( noiseMean,  noiseSD,  noiseMax,  noiseMin,  type,createRandomGenerator(),createRandomGenerator()) ;
	}

	
	private Noise(double noiseMean, double noiseSD, double noiseMax, double noiseMin, Distribution type,DRand normRandom, DRand uniRandom) {
		super();
		this.noiseMean = noiseMean;
		this.noiseSD = noiseSD;
		this.noiseMax = noiseMax;
		this.noiseMin = noiseMin;
		this.type = type;
		
		
		normalRandomEngine = normRandom;
		randomNormal = new Normal(noiseMean,noiseSD,normalRandomEngine);
		uniformRandomEngine=uniRandom;
		randomUniform = new Uniform(noiseMin,noiseMax,uniformRandomEngine);
	}

	
	private static DRand createRandomGenerator(){
		try{Thread.sleep((long)(Math.random() * SLEEP_INTERVAL));}catch(InterruptedException e){}
		return new DRand(new Date());
	}
	public Noise addWhiteNoise(Noise other){
		double noiseMean = this.getNoiseMean() + other.getNoiseMean();
		double noiseSD = this.getNoiseSD() + other.getNoiseSD();
		double noiseMax = this.getNoiseMax() + other.getNoiseMax();
		double noiseMin = this.getNoiseMin() + other.getNoiseMin();
		return new Noise(noiseMean,noiseSD,noiseMax,noiseMin,this.getType(),other.normalRandomEngine,other.uniformRandomEngine);
	}
		
	/**
	 * Generates a random number depending on the noise's distribution.
	 * @return Random number within specified distribution.
	 */
	public double generateNoise(){
		if(type == Distribution.GAUSSIAN || type == Distribution.NORMAL){
			return randomNormal.nextDouble();
		}else{
			return randomUniform.nextDouble();
		}
	}
	public double getNoiseMean() {
		return noiseMean;
	}

	public void setNoiseMean(double noiseMean) {
		this.noiseMean = noiseMean;
	}

	public double getNoiseSD() {
		return noiseSD;
	}

	public void setNoiseSD(double noiseSD) {
		this.noiseSD = noiseSD;
	}

	public double getNoiseMax() {
		return noiseMax;
	}

	public void setNoiseMax(double noiseMax) {
		this.noiseMax = noiseMax;
	}

	public double getNoiseMin() {
		return noiseMin;
	}

	public void setNoiseMin(double noiseMin) {
		this.noiseMin = noiseMin;
	}

	public Distribution getType() {
		return type;
	}

	public void setType(Distribution type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(noiseMax);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(noiseMean);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(noiseMin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(noiseSD);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Noise))
			return false;
		Noise other = (Noise) obj;
		if (Double.doubleToLongBits(noiseMax) != Double.doubleToLongBits(other.noiseMax))
			return false;
		if (Double.doubleToLongBits(noiseMean) != Double.doubleToLongBits(other.noiseMean))
			return false;
		if (Double.doubleToLongBits(noiseMin) != Double.doubleToLongBits(other.noiseMin))
			return false;
		if (Double.doubleToLongBits(noiseSD) != Double.doubleToLongBits(other.noiseSD))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Noise [noiseMean=" + noiseMean + ", noiseSD=" + noiseSD + ", noiseMax=" + noiseMax + ", noiseMin="
				+ noiseMin + ", type=" + type + "]";
	}
	


}
