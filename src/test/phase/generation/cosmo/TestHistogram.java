package test.phase.generation.cosmo;

import org.junit.Test;

import cern.colt.list.DoubleArrayList;
import junit.framework.Assert;
import phase.generation.cosmo.Dataset;
import phase.generation.cosmo.Histogram;

public class TestHistogram extends Histogram{

	public TestHistogram(){
		super();
	}
	@Test
	public void testHistogramDataset() {
		
		DoubleArrayList values = new DoubleArrayList(13);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		
		
		
		
		values.add(3.0);
		values.add(4.0);
		
		values.add(3.0);
		
		//[1]
		Dataset ds = new Dataset(values);
		
		Histogram h = new Histogram(ds);
		
		Assert.assertEquals(13,h.countSamples());
		Assert.assertEquals(1.0,h.getMinValue());
		Assert.assertEquals(6.0,h.getMaxValue());
		Assert.assertEquals(1,h.getFrequencyAt(0));
		Assert.assertEquals(2,h.getFrequencyAt(2));
		Assert.assertEquals(4,h.getFrequencyAt(3));
	}

	@Test
	public void testHistogramListOfDouble() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		
		Histogram h = new Histogram(values);
		
		Assert.assertEquals(6,h.countSamples());
		Assert.assertEquals(1.0,h.getMinValue());
		Assert.assertEquals(6.0,h.getMaxValue());
	}

	@Test
	public void testHistogramDatasetDoubleDouble() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);
		Histogram h = new Histogram(ds,7.0,0.0);
		
		Assert.assertEquals(6,h.countSamples());
		Assert.assertEquals(0.0,h.getMinValue());
		Assert.assertEquals(7.0,h.getMaxValue());
	}

	@Test
	public void testComputeBinIndex() {
		DoubleArrayList values = new DoubleArrayList(4);
		for(int i = 0;i<100;i++){
		values.add((double)i);
		}
		
		
		Dataset ds = new Dataset(values);
		
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h = new TestHistogram();
		h.init(max, min, numBins);
		
		Assert.assertEquals(0,h.computeBinIndex(-1000.0));
		Assert.assertEquals(0,h.computeBinIndex(0.0));
		Assert.assertEquals(0,h.computeBinIndex(1.0));
		Assert.assertEquals(0,h.computeBinIndex(10.0));
		Assert.assertEquals(0,h.computeBinIndex(15.0));
		Assert.assertEquals(0,h.computeBinIndex(20.0));
		Assert.assertEquals(1,h.computeBinIndex(30.0));
		Assert.assertEquals(1,h.computeBinIndex(40.0));
		Assert.assertEquals(1,h.computeBinIndex(45.0));
		Assert.assertEquals(2,h.computeBinIndex(55.0));
		Assert.assertEquals(2,h.computeBinIndex(60.0));
		Assert.assertEquals(2,h.computeBinIndex(65.0));
		Assert.assertEquals(2,h.computeBinIndex(70.0));
		Assert.assertEquals(3,h.computeBinIndex(80.0));
		Assert.assertEquals(3,h.computeBinIndex(85.0));
		Assert.assertEquals(3,h.computeBinIndex(90.0));
		Assert.assertEquals(3,h.computeBinIndex(100.0));
		Assert.assertEquals(3,h.computeBinIndex(1000.0));
		

	}

	@Test
	public void testComputeBinIndex_1element() {
		DoubleArrayList values = new DoubleArrayList(1);
		values.add(1.0);
		
		
		Dataset ds = new Dataset(values);
		 
		
	}
	@Test
	public void testHistogramDoubleDoubleInt() {
		DoubleArrayList values = new DoubleArrayList(4);
		for(int i = 0;i<100;i++){
		values.add((double)i);
		}
		
		
		Dataset ds = new Dataset(values);
		
		int max = 100;
		int min = 0;
		int numBins = 4;
		Histogram h = new Histogram(max, min, numBins);
	}

	@Test
	public void testAddSample() {
		
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h = new TestHistogram();
		h.init(max, min, numBins);
		
		h.addSample(0.0);
		h.addSample(0.1);
		h.addSample(10.0);
		h.addSample(40.0);
		h.addSample(45.0);
		h.addSample(95.0);
		h.addSample(92.0);
		h.addSample(98.0);
		h.addSample(85.0);
		
		Assert.assertEquals(3.0 ,h.getFrequency(0));
		Assert.assertEquals(2.0 ,h.getFrequency(1));
		Assert.assertEquals(0.0 ,h.getFrequency(2));
		Assert.assertEquals(4.0 ,h.getFrequency(3));
	}

	@Test
	public void testClear() {
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h = new TestHistogram();
		h.init(max, min, numBins);
		
		h.addSample(0.0);
		h.addSample(0.1);
		h.addSample(10.0);
		h.addSample(40.0);
		h.addSample(45.0);
		h.addSample(95.0);
		h.addSample(92.0);
		h.addSample(98.0);
		h.addSample(85.0);
		
		h.clear();
		
		Assert.assertEquals(0.0 ,h.getFrequency(0));
		Assert.assertEquals(0.0 ,h.getFrequency(1));
		Assert.assertEquals(0.0 ,h.getFrequency(2));
	}

	@Test
	public void testSize() {
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h = new TestHistogram();
		h.init(max, min, numBins);
		
	
		Assert.assertEquals(4 ,h.size());
	}

	@Test
	public void testComputeNumberOfBins() {
		Assert.assertEquals(4,Histogram.computeNumberOfBins(100, 0, 25));
	}
	
	@Test
	public void testComputeNumberOfBins_min_max_equal() {
		Assert.assertEquals(1,Histogram.computeNumberOfBins(0, 0, 1));
	}

	@Test
	public void testComputeBinWidth() {
		
		DoubleArrayList values = new DoubleArrayList(4);
		for(int i = 0;i<100;i++){
		values.add((double)i);
		}
		
		
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(true,Histogram.computeBinWidth(ds) > 1);
	}

	@Test
	public void testNormalize() {
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h1 = new TestHistogram();
		
		
		h1.init(max, min, numBins);
		
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(10.0);
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(10.0);
		h1.addSample(40.0);
		h1.addSample(45.0);
		h1.addSample(40.0);
		h1.addSample(45.0);
		h1.addSample(95.0);
		h1.addSample(92.0);
		h1.addSample(98.0);
		h1.addSample(85.0);
		h1.addSample(95.0);
		h1.addSample(92.0);
		h1.addSample(98.0);
		h1.addSample(99.0);
		
		//[6,4,0,8]
		
		//so normalized would be
		//[0.3,0.2,0,0.4]
		
		NormalizedHistogram nh = h1.normalize();
		
		double [] bins = nh.getBins();
		Assert.assertEquals(0.333,bins[0],0.001);
		Assert.assertEquals(0.222,bins[1],0.001);
		Assert.assertEquals(0.0,bins[2],0.001);
		Assert.assertEquals(0.444,bins[3],0.001);
		
		
	}

	@Test
	public void testNormalizedEntropy() {
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h1 = new TestHistogram();
		
		
		h1.init(max, min, numBins);
		
		h1.addSample(0.0);
		h1.addSample(0.1);
		
		//[1,0,0,0]
		Assert.assertEquals(1.0, h1.normalizedEntropy(),0.001);
	
		h1.addSample(40.0);
		h1.addSample(45.0);
		
		//[0.5,0.5,0,0]
		Assert.assertEquals(0.0, h1.normalizedEntropy(),0.001);
		
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(40.0);
		h1.addSample(45.0);
		
		//[0.6,0.4,0,0]
		Assert.assertEquals(0.02897, h1.normalizedEntropy(),0.0001);
		

		
	
	

	}

	@Test
	public void testComputeDistance() {
		int max = 100;
		int min = 0;
		int numBins = 4;
		TestHistogram h1 = new TestHistogram();
		
		
		h1.init(max, min, numBins);
		
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(10.0);
		h1.addSample(40.0);
		h1.addSample(45.0);
		h1.addSample(95.0);
		h1.addSample(92.0);
		h1.addSample(98.0);
		h1.addSample(85.0);
		
		TestHistogram h2 = new TestHistogram();
		
		
		h2.init(max, min, numBins);
		
		h2.addSample(11.0);
		h2.addSample(0.5);
		h2.addSample(15.0);
		h2.addSample(42.0);
		h2.addSample(46.0);
		h2.addSample(97.0);
		h2.addSample(99.0);
		h2.addSample(96.0);
		h2.addSample(87.0);
		
		Assert.assertEquals(0.0 ,h1.computeDistance(Histogram.DistanceMeasure.HELLINGER,h2),0.001);
		
		h2.addSample(42.0);
		Assert.assertEquals(false, compareDouble(h1.computeDistance(Histogram.DistanceMeasure.HELLINGER,h2),0.0,0.001));
		
		h1.addSample(45.0);
		
		Assert.assertEquals(0.0 ,h1.computeDistance(Histogram.DistanceMeasure.HELLINGER,h2),0.001);
		
		
		double distCloser = h1.computeDistance(Histogram.DistanceMeasure.HELLINGER,h2);
		
		h1.addSample(45.0);
		
		double distFurther = h1.computeDistance(Histogram.DistanceMeasure.HELLINGER,h2);
		
		Assert.assertEquals(true,distCloser < distFurther);
		//Assert.assertEquals(3 ,h.getFrequency(0));
		//Assert.assertEquals(2 ,h.getFrequency(1));
		//Assert.assertEquals(4 ,h.getFrequency(2));
	}

	public static boolean compareDouble (double desiredValue, double actualValue, double tolerance){
		double diff = Math.abs(desiredValue - actualValue);         //  1000 - 950  = 50
	    tolerance = tolerance * desiredValue;  //  0.2*1000 = 200
	    return diff < tolerance;     //  50<200      = true
	}
}
