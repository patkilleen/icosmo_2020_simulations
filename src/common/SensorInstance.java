package common;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import common.exception.ConfigurationException;

public class SensorInstance extends Sensor implements Serializable{

	private Vehicle vehicle;
	
	private Queue<Double> zvalueWindow;
	private int zvalueWindowSize;
	
	public SensorInstance(int aPgn, int aSpn, Vehicle vehicle, int zscoreWindowSize) {
		this(new Sensor(aPgn,aSpn),vehicle,zscoreWindowSize);
	}
	
	public SensorInstance(Sensor s, Vehicle vehicle, int zscoreWindowSize) {
		super(s);
		if(zscoreWindowSize <= 0){
			throw new ConfigurationException("cannot create sensor isntance, because zscore window size is not positive.");
		}
		
		if(vehicle == null){
			throw  new ConfigurationException("cannot create sensor isntance, because null vehicle");
		}
		//creates data structures/model to store/represent sensor data
		this.zvalueWindowSize = zscoreWindowSize;
		this.zvalueWindow = new ArrayDeque<Double>(zscoreWindowSize);
		this.vehicle = vehicle;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public Iterator<Double> zvalueIterator() {
		return zvalueWindow.iterator();
	}

	public void addZvalue(double zvalue) {

		//zvalue window reached capacity?
		if(zvalueWindow.size() == zvalueWindowSize){
			//remove oldest zvalue
			zvalueWindow.poll();
		}
		zvalueWindow.add(zvalue);
		
	}
	
	/**
	 * takes the average of the zvalues in zvalue moving window average
	 * @return zscore
	 */
	public double computeZScore(){
		//compute zscore (moving average of zvalues)
		double zscore = 0;
		
		Iterator<Double> it = this.zvalueIterator();
		
		while(it.hasNext()){
			Double zvalueTmp = it.next();
			
			zscore += zvalueTmp;
		}
		
		zscore = zscore / (double) this.numberOfZValues();
		return zscore;
	}
	protected void clearZValues(){
		zvalueWindow.clear();
	}
	
	public int numberOfZValues(){
		return zvalueWindow.size();
	}

	public int getZValueWindow(){
		return this.zvalueWindowSize;
	}

	@Override
	public String toString() {
		return "SensorInstance ["+super.toString()+", vehicle=" + vehicle + "]";
	}
	
	
}
