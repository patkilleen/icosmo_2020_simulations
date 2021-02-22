package phase.generation.cosmo;

import cern.colt.list.DoubleArrayList;
import common.exception.ConfigurationException;
import common.exception.SimulationException;

public class Histogram {

	private int [] bins;
	private double maxValue;
	private double minValue;
	private double binWidth;
	
	public enum DistanceMeasure{HELLIGNER,EUCLIDEAN,CITY_BLOCK,GOWER,SQUARED_EUCLIDEAN,INTERSECTION,FIDELITY,MATUSITA,COSINE,BHATTACHARYYA};
	
	/**
	 * Constructor that creates a histogram, which computes the max and min values from dataset.
	 * @param data The dataset
	 */
	public Histogram(Dataset data) {
		this(data,data.max(),data.min());
	}
	
	/**
	 * Constructor that creates a histogram, which computes the max and min values from dataset.
	 * @param data the data readings
	 */
	public Histogram(DoubleArrayList data){
		this(new Dataset(data));
	}
	/**
	 * Constructor that creates a histogram
	 * @param data The dataset
	 * @param max Desired maximum
	 * @param min Desired minimum
	 */
	public Histogram(Dataset data, double max, double min) {
		if(data == null){
			throw new ConfigurationException("cannot create histogram of null dataset.");
		}
		maxValue = max;
		minValue = min;
		binWidth = computeBinWidth(data);
		int numBins = computeNumberOfBins(maxValue,minValue,binWidth);
		
		bins = new int[numBins];
		
		//fill the histogram
		for(int i = 0; i < data.size(); i ++){
			
			double sample = data.get(i);
			
			int binIx = computeBinIndex(sample);
			bins[binIx]++;
			
		}//end fill histo
	}
	
	/**
	 * Constructor that creates an empty histogram, which is expected to be filled gradually.
	 * This is ideal for sensors that min and max values are known before hand, and the average number of bins
	 * has been already computed.
	 * 
	 * That is, once multiple histograms of sensor data signals have been built, their number of bins can be
	 * condisered, and an average can be taken, such that most signals will have that many bins, so this type
	 * of histogram can be used to model any sensor of a class, given its average bin number.
	 * @param max maximum value of data to consider
	 * @param min minimum value of data to consider
	 * @param numberOfBins number of bins in histogram
	 */
	public Histogram(double max, double min, int numberOfBins){
		init(max,min,numberOfBins);
	}
	
	/**
	 * empty constructor for subclass flexibility
	 */
	protected Histogram(){
		
	}
	
	
	public int getFrequencyAt(int binIx) {
		return bins[binIx];
	}

	public double getMaxValue() {
		return maxValue;
	}



	public double getMinValue() {
		return minValue;
	}


	public double getBinWidth() {
		return binWidth;
	}


	/**
	 * returns the number of samples in the histogram
	 * @return
	 */
	public int countSamples(){
		int sum = 0;
		for(int i = 0; i < bins.length;i++){
			sum += bins[i];
		}
		
		return sum;
	}
	protected void init(double max, double min, int numberOfBins){
		this.maxValue = max;
		this.minValue = min;
		this.bins = new int[numberOfBins];
		this.binWidth = (max - min)/(double)numberOfBins;
	}
	
	/**
	 * fetches the frequency at a given bin
	 * @param binIx index of bin 
	 * @return frequency
	 */
	protected double getFrequency(int binIx){
		return this.bins[binIx];
	}
	
	/**
	 * Computes the bin index that a given sample belongs to.
	 * @param sample Sample to consider which bin it belogns to
	 * @return The index of bin to add in bins
	 */
	protected int computeBinIndex(double sample) {
		if(sample >= maxValue){
			return bins.length-1;
		}else if(sample <= minValue){
			return 0;
		}else{
			//round to nearest int
			int resultIx =  (int) Math.floor(sample/this.binWidth);
			
			
		//	return resultIx;  this doesn't break histogram code when uncommmented, below does but doesn't break model code when uncommented below 
			//throw new illegalstateexception
			if(resultIx < 0){
				return 0;
			}else if(resultIx > (bins.length-1)) {
				return bins.length-1;	
			}else{
				return resultIx;
			}
		}
	}

	

	/**
	 * Adds a sample to the histogram. The appropriate bin frequency will be incremented. 
	 * @param sample the sample to add to histogram
	 */
	public void addSample(double sample){
		int binIx = computeBinIndex(sample);
		bins[binIx]++;
	}
	
	
	/**
	 * empties all the bins (sets the values to 0)[
	 */
	public void clear(){
		
		for(int i = 0;i<bins.length;i++){
			bins[i] = 0;
		}
	}
	
	/**
	 * Number of bins in histogram
	 * @return number of bins
	 */
	public int size(){
		return bins.length;
	}
	/**
	 * Calculates the number of bins require to discretize a given dataset.
	 * see //https://stats.stackexchange.com/questions/798/calculating-optimal-number-of-bins-in-a-histogram
	 * @param data The dataset
	 * @param binWidth the width of the bins (@see computeBinWidth)
	 * @return Number of bins required to bin the dataset.
	 */
	protected static int computeNumberOfBins(double max, double min, double binWidth){
	
		int res = (int)Math.ceil((max - min)/binWidth);
		
		//don't wan't empty binned histogram, atleasat one bin
		if(res == 0){
			return 1;
		}else{
			return res;
		}
		
		
	}
	
	/**
	 * Computes the width of the bins for a dataset.
	 * @param data The dataset
	 * @return bin width
	 */
	protected static double computeBinWidth(Dataset data){
		if(data == null ){
			throw new ConfigurationException("cannot compute number of bins for empty/null data set");
		}
		
		double iqr = data.iqr();
		int n = data.size();
		
		double res = (2.0 * iqr) / Math.cbrt(n);
		
		//almost 0?
		if(res < 1){
			return 1;
		}else{
			return res;
		}
	}
	/**
	 * Converts this histogram into a normalize frequency histogram. The histgram is unaffected.
	 * @return The normalized version of this histogram.
	 */
	protected NormalizedHistogram normalize(){
		int binFreqSum = 0;
		
		//sum all bins
		for(int bin : bins){
			binFreqSum += bin;
		}
		
		double [] normBins = new double[bins.length];
		
		//normalize each bin and copy into result
		for(int i = 0;i< bins.length;i++){
			normBins[i] = bins[i]/(double)binFreqSum;
		}
		
		return new NormalizedHistogram(normBins);
	}

	/**
	 * computes the normalized entropy of this histogram
	 * @return normalized entropy
	 */
	public double normalizedEntropy(){
		NormalizedHistogram nHist = normalize();
		
		double ne = 0;
		int nonEmptyBins = 0;
		
		//iterate bins
		for(double nFreq : nHist.getBins()){
			
			//not empty bin?
			if(nFreq > 0){
				double tmp = nFreq * Math.log10(nFreq);
				ne += tmp;
				nonEmptyBins++;
			}
		}//end iterate normalized bin frequencies
		
		if(nonEmptyBins == 1){
			return 1.0;
		}
		ne = 1.0 + (1.0/Math.log10(nonEmptyBins)) * ne;
		return ne;
	}
	
	/**
	 * Given a distance metric, the distance between histograms is computed.
	 * @param alg The distance metric to use for distance comparison
	 * @param other A histogram of same length as this histogram
	 * @return The distance
	 */
	public double computeDistance(DistanceMeasure alg, Histogram other){
		
		
		if(other == null){
			throw new ConfigurationException("cannot compute distance between");
		}

		NormalizedHistogram h1 = normalize();
		NormalizedHistogram h2 = other.normalize();
		
		double []h1Bins = h1.getBins();
		double []h2Bins = h2.getBins();
		
		//assign h1Bins to smallest lengther array
		double [] tmp = h1Bins;
		
		double tmpValue1=0;
		double tmpValue2=0;
		
		if(h1Bins.length > h2Bins.length){
			h1Bins = h2Bins;
			h2Bins = tmp;
		}
		
		switch(alg){
		
		/*
		 * TODO: implement all the other distances from the paper I found.
		 */
		case HELLIGNER:
			double res = 0;
			int i = 0;
			for(i = 0; i < h1Bins.length; i++){
				double sq = Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]);
				sq = sq * sq;
				res += sq;
			}
			
			//there may be bins remaining in longer histogram, consider the bins
			//that don't exist as empty
			
			while(i < h2Bins.length){
				//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
				double sq = Math.sqrt(h2Bins[i]);
				sq = sq * sq;
				res += sq;
				i++;
			}
			res = res * 0.5;//ACCORDING TO ANOTHER PAPER ITS * 2?
			return Math.sqrt(res);
			
		case EUCLIDEAN:
			 res = 0;
			i = 0;
			for(i = 0; i < h1Bins.length; i++){
				double sq = Math.abs(h1Bins[i] - h2Bins[i]);
				sq = sq * sq;
				res += sq;
			}
			
			//there may be bins remaining in longer histogram, consider the bins
			//that don't exist as empty
			
			while(i < h2Bins.length){
				//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
				double sq = Math.abs(h2Bins[i]);
				sq = sq * sq;
				res += sq;
				i++;
			}
			
			return Math.sqrt(res);
			
		case CITY_BLOCK:
			 res = 0;
				i = 0;
				for(i = 0; i < h1Bins.length; i++){
					double sq = Math.abs(h1Bins[i] - h2Bins[i]);
					res += sq;
				}
				
				//there may be bins remaining in longer histogram, consider the bins
				//that don't exist as empty
				
				while(i < h2Bins.length){
					//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
					double sq = Math.abs(h2Bins[i]);
					res += sq;
					i++;
				}
				
				return res;
			case GOWER:
					 res = 0;
						i = 0;
						for(i = 0; i < h1Bins.length; i++){
							double sq = Math.abs(h1Bins[i] - h2Bins[i]);
							res += sq;
						}
						
						//there may be bins remaining in longer histogram, consider the bins
						//that don't exist as empty
						
						while(i < h2Bins.length){
							//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
							double sq = Math.abs(h2Bins[i]);
							res += sq;
							i++;
						}
						
						return res*(1.0/(double)h2Bins.length);
						
			case SQUARED_EUCLIDEAN:
				 res = 0;
					i = 0;
					for(i = 0; i < h1Bins.length; i++){
						double sq = h1Bins[i] - h2Bins[i];
						sq = sq * sq;
						res += sq;
					}
					
					//there may be bins remaining in longer histogram, consider the bins
					//that don't exist as empty
					
					while(i < h2Bins.length){
						//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
						double sq = h2Bins[i] * h2Bins[i];
						res += sq;
						i++;
					}
					
					return res;
			case INTERSECTION:
				 res = 0;
					i = 0;
					for(i = 0; i < h1Bins.length; i++){
						if(h1Bins[i]<h2Bins[i]){
							res += h1Bins[i];	
						}else{
							res += h2Bins[i];
						}
						
					}
					
					//there may be bins remaining in longer histogram, consider the bins
					//that don't exist as empty
					//can skip 
					/*the loop, since we assume the other histogram has 0 bins, so 0 is minimum
					while(i < h2Bins.length){
						//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
						double sq = h2Bins[i] * h2Bins[i];
						res += sq;
						i++;
					}*/
					
					return res;
					
		
		case FIDELITY:
			 res = 0;
				i = 0;
				for(i = 0; i < h1Bins.length; i++){
				
					double sq = h1Bins[i] * h2Bins[i];
					sq = Math.sqrt(sq);
					res += sq;
				}
				
				
				/*
				while(i < h2Bins.length){
					//Math.sqrt(h1Bins[i]) - Math.sqrt(h2Bins[i]) == 0 - Math.sqrt(h2Bins[i]) == Math.sqrt(h2Bins[i]): empty bins h1 
					double sq = h2Bins[i] * h2Bins[i];
					res += sq;
					i++;
				}*/
				
				return res;
				
		case MATUSITA:
			 res = 0;
				i = 0;
				for(i = 0; i < h1Bins.length; i++){
					double sq = h1Bins[i]*h2Bins[i];
					sq = Math.sqrt(sq);
					res += sq;
				}
				
				res = res * 2;
				res = 2 - res;
				
				
				return Math.sqrt(res);
				

		case COSINE:
			 res = 0;
			 tmpValue1=0;
			 tmpValue2=0;
			 //calculate tmp value 1, the nominato
			 i = 0;
				for(i = 0; i < h1Bins.length; i++){
					double sq = h1Bins[i]*h2Bins[i];
					tmpValue1 += sq;
				}
				
				//calculate denominator 1
				for(i = 0; i < h1Bins.length; i++){
					double sq = h1Bins[i]*h1Bins[i];
					tmpValue2 += sq;
				}
				
				tmpValue2 = Math.sqrt(tmpValue2);
				
				//calculate denominator 2
				for(i = 0; i < h2Bins.length; i++){
					double sq = h2Bins[i]*h2Bins[i];
					res += sq;
				}
				
				res = Math.sqrt(res);
				
				
				return tmpValue1/(tmpValue2*res);
				

		case BHATTACHARYYA:
			 res = 0;
			
			 //calculate tmp value 1, the nominato
			 i = 0;
				for(i = 0; i < h1Bins.length; i++){
					double sq = h1Bins[i]*h2Bins[i];
					res += Math.sqrt(sq);
				}
		
				return - Math.log(res);
			
		default:
				throw new SimulationException("unknown histogram distance measure.");
		}
	}
	
	protected static class NormalizedHistogram {
		
		private double [] bins;

		public NormalizedHistogram(double [] bins) {
			this.bins = bins;
		}

		public double[] getBins() {
			return bins;
		}

		public void setBins(double[] bins) {
			this.bins = bins;
		}

		
	}

}
