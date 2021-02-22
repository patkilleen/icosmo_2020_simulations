package phase.generation.cosmo;

import java.io.Serializable;

public class MinMaxPair implements Serializable{

	private double max;
	private double min;
		
	public MinMaxPair(double max, double min) {
		super();
		this.max = max;
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	
	

}
