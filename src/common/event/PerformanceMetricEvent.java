package common.event;

import common.Algorithm;

public class PerformanceMetricEvent extends Event{

	private Algorithm algorithm;
	private int truePositiveCount;
	private int trueNegativeCount;
	private int falsePositiveCount;
	private int falseNegativeCount;
	private double varyingThreshold;
	private double falsePositiveRate;
	private double truePositiveRate;
	private double accuracy;
	private double fscore;
	public PerformanceMetricEvent(Algorithm algorithm, int truePositiveCount, int trueNegativeCount,
			int falsePositiveCount, int falseNegativeCount, double varyingThreshold, double falsePositiveRate,
			double truePositiveRate, double accuracy, double fscore) {
		super();
		this.algorithm = algorithm;
		this.truePositiveCount = truePositiveCount;
		this.trueNegativeCount = trueNegativeCount;
		this.falsePositiveCount = falsePositiveCount;
		this.falseNegativeCount = falseNegativeCount;
		this.varyingThreshold = varyingThreshold;
		this.falsePositiveRate = falsePositiveRate;
		this.truePositiveRate = truePositiveRate;
		this.accuracy = accuracy;
		this.fscore = fscore;
	}



	public int getTruePositiveCount() {
		return truePositiveCount;
	}



	public void setTruePositiveCount(int truePositiveCount) {
		this.truePositiveCount = truePositiveCount;
	}



	public int getTrueNegativeCount() {
		return trueNegativeCount;
	}



	public void setTrueNegativeCount(int trueNegativeCount) {
		this.trueNegativeCount = trueNegativeCount;
	}



	public int getFalsePositiveCount() {
		return falsePositiveCount;
	}



	public void setFalsePositiveCount(int falsePositiveCount) {
		this.falsePositiveCount = falsePositiveCount;
	}



	public int getFalseNegativeCount() {
		return falseNegativeCount;
	}



	public void setFalseNegativeCount(int falseNegativeCount) {
		this.falseNegativeCount = falseNegativeCount;
	}



	public Algorithm getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}



	public double getVaryingThreshold() {
		return varyingThreshold;
	}



	public void setVaryingThreshold(double varyingThreshold) {
		this.varyingThreshold = varyingThreshold;
	}



	public double getFalsePositiveRate() {
		return falsePositiveRate;
	}



	public void setFalsePositiveRate(double falsePositiveRate) {
		this.falsePositiveRate = falsePositiveRate;
	}



	public double getTruePositiveRate() {
		return truePositiveRate;
	}



	public void setTruePositiveRate(double truePositiveRate) {
		this.truePositiveRate = truePositiveRate;
	}



	public double getAccuracy() {
		return accuracy;
	}



	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}



	public double getFscore() {
		return fscore;
	}



	public void setFscore(double fscore) {
		this.fscore = fscore;
	}

	
}
