package phase.generation.cosmo;

import java.util.List;

import cern.jet.stat.Descriptive;

import cern.colt.list.DoubleArrayList;
import common.exception.ConfigurationException;


/**
 * resources: https://dst.lbl.gov/ACSSoftware/colt/api/cern/jet/stat/Descriptive.html
 * https://dst.lbl.gov/ACSSoftware/colt/api/cern/colt/list/DoubleArrayList.html
 * @author Not admin
 *
 */
public class Dataset {

	private DoubleArrayList data;
	
	public Dataset(DoubleArrayList data) {
		if(data == null || data.isEmpty()){
			throw new ConfigurationException("cannot create empty dataset. Make sure that all sensor that are suppost to be outputing data are in-fact being simulated (no duplicate sensor outputing values).");
		}
	/*	
		this.data = new DoubleArrayList(data.size());
		
		for(Double d : data){
			this.data.add(d);
		}*/
		
		this.data = data;
	}

	/**
	 * Returns the maximum value in the dataset.
	 * @return The maximum value.
	 */
	public double max(){
		
		return Descriptive.max(data);
	}
	

	/**
	 * Returns the minimum value in the dataset.
	 * @return The minimum value.
	 */
	public double min(){
		
		return Descriptive.min(data);
	}
	
	/**
	 * Returns the mean of the data set.
	 * @return The mean
	 */
	public double mean(){
		return Descriptive.mean(data);
	}
	
	/**
	 * Returns the variance of dataset.
	 * @return Variance
	 */
	public double var(){
		double sumOfSquares = Descriptive.sumOfSquares(data);
		double sum = Descriptive.sum(data);
		return Descriptive.variance(data.size(),sum,sumOfSquares);
	}
	
	/**
	 * Returns the standard deviation of the dataset.
	 * @return statndard deviation
	 */
	public double std(){
		double var = this.var();
		return Descriptive.standardDeviation(var);
	}
	
	public double median(){
		return Descriptive.median(data);
	}
	
	/**
	 * Returns the inter-quantile range of the dataset.
	 * @return inter-quantile range
	 */
	public double iqr(){
		
		DoubleArrayList sortedData = data.copy();
		sortedData.sort();
		/*
		//index of median in 1st quartile
		double q1Ix = ((double)( sortedData.size()))/ 4.0;
		
		
		double rValue = sortedData.get((int)Math.ceil(q1Ix));
		double lValue = sortedData.get((int)Math.floor(q1Ix));
		//quartile 1
		double q1 = (rValue + lValue)/2.0;
		
		//index of median of 3rd quartil
		double q3Ix = ((double)3.0 * ( sortedData.size()))/ 4.0;
		
		rValue = sortedData.get((int)Math.ceil(q3Ix));
		lValue = sortedData.get((int)Math.floor(q3Ix));
		
		double q3 = (rValue + lValue)/2.0;
		
		return q3 - q1;*/
		
		return Descriptive.quantile(sortedData, 0.75) - Descriptive.quantile(sortedData, 0.25);
	}
	
	
	/**
	 * Returns the dataset as array of doubles
	 * @return values
	 */
	/*public double[] getData(){
		return this.data.elements();
	}*/
	
	public int size(){
		return data.size();
	}
	
	public double get(int i){
		return data.get(i);
	}
}
