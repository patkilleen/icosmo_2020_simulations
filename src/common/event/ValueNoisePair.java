package common.event;

public class ValueNoisePair {
 
	private double value;
	private double noise;
	
	
	public ValueNoisePair(double value, double noise) {
		super();
		this.value = value;
		this.noise = noise;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getNoise() {
		return noise;
	}
	public void setNoise(double noise) {
		this.noise = noise;
	}
	
	

}
