package test.phase.generation.cosmo;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import common.exception.ConfigurationException;
import common.exception.SimulationException;
import junit.framework.Assert;
import phase.generation.cosmo.Histogram;
import phase.generation.cosmo.Model;

public class TestModel {

	@Test
	public void testModel() {
		Model model = new Model();
	}

	@Test
	public void testClearWeeklyHistograms_illegal_state() {
		Model model = new Model();
		
		boolean flag = false;
		try{
		model.clearWeeklyHistogram(0);
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
	}


	@Test
	public void testClearWeeklyHistograms_index_out_bounds() {
		Model model = new Model();
		model.write(0, 3.0);
		model.flush();
		model.write(0, 3.0);
		model.flush();
		model.createFinalHistogram(10, 0, 10, 10);
		
		
		boolean flag = false;
		try{
		model.clearWeeklyHistogram(-1);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
		model.clearWeeklyHistogram(-10);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		
		flag = false;
		try{
		model.clearWeeklyHistogram(10);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
		model.clearWeeklyHistogram(11);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		
		flag = false;
		try{
		model.clearWeeklyHistogram(1000);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	}
	
	@Test
	public void testClearWeeklyHistograms() {
		Model model = new Model();
		model.write(0, 3.0);
		model.flush();
		model.write(0, 3.0);
		model.flush();
		model.createFinalHistogram(10, 0, 10, 10);
		
		for(int i = 0; i < 10; i ++){
		model.clearWeeklyHistogram(i);
		}
		
	}
	
	

	@Test
	public void testGetWeeklyHistograms_null() {
		Model model = new Model();
		Assert.assertEquals(null,model.getWeeklyHistograms());		
		
	}
	
	@Test
	public void testGetWeeklyHistograms_and_createFinalHistograms() {
		
		Model model = new Model();
model.write(0, 6.0);
		
		model.flush();
model.write(0, 6.0);
		
		model.flush();
		model.createFinalHistogram(10, 0, 10, 10);
		
		List<Histogram> hists = model.getWeeklyHistograms();
		
		Assert.assertEquals(10,hists.size());
		
		for(Histogram h : hists){
			Assert.assertEquals(10.0,h.getMaxValue());
			Assert.assertEquals(0.0,h.getMinValue());
			Assert.assertEquals(10,h.size());
		}
				
		
	}
	
	@Test
	public void testNumberOfWeeklyHistograms() {
		Model model = new Model();
		model.write(0, 3.0);
		model.flush();
		model.write(0, 3.0);
		model.flush();
		model.createFinalHistogram(10, 0, 10, 15);
		Assert.assertEquals(15,model.numberOfWeeklyHistograms());
	}

	
	@Test
	public void testNumberOfWeeklyHistograms_illegal_state() {
		Model model = new Model();
		
		Assert.assertEquals(-1,model.numberOfWeeklyHistograms());
	}
	
	
	@Test
	public void testComputeMeanNumberOfBins() {
		Model model = new Model();
		
		model.write(0, 1.0);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 4.0);
		model.write(0, 5.0);
		model.write(0, 6.0);
		
		model.flush();
		
		model.write(0, 1.0);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 4.0);
		model.write(0, 5.0);
		model.write(0, 6.0);
		
		model.flush();
		
		Assert.assertEquals(2.0, model.computeMeanNumberOfBins());
		
		model = new Model();
		
		model.write(0, 1.0);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 4.0);
		model.write(0, 5.0);
		model.write(0, 6.0);//bins 2
		
		model.flush();
		
		model.write(0, 1.0);
		model.write(0, 1.0);
		model.write(0, 1.0);
		model.write(0, 1.0);
		
		//average (1 + 2)/ 2 = 3/2
		model.flush();
		
		Assert.assertEquals(1.5, model.computeMeanNumberOfBins(),0.001);
	}

	@Test
	public void testComputeMeanNumberOfBins_illegal_state() {
		
		
		boolean flag = false;
		try{
			Model model = new Model();
			model.computeMeanNumberOfBins();
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag); 
		
		
		 flag = false;
			try{
				Model model = new Model();
				model.write(0, 1.0);
				model.write(0, 2.0);
				model.write(0, 3.0);
				model.write(0, 4.0);
				model.write(0, 5.0);
				model.write(0, 6.0);
				
				model.flush();
				
				 model.computeMeanNumberOfBins();
			
			}catch(SimulationException e){
				flag = true;
			}
			
			Assert.assertEquals(true,flag); 
		
			 flag = false;
				try{
					Model model = new Model();
					model.write(0, 1.0);
					model.write(0, 2.0);
					model.write(0, 3.0);
					model.write(0, 4.0);
					model.write(0, 5.0);
					model.write(0, 6.0);
					
					model.flush();
					
					model.write(0, 4.0);
					model.write(0, 5.0);
					model.write(0, 6.0);
					
					 model.computeMeanNumberOfBins();
				
				}catch(SimulationException e){
					flag = true;
				}
				
				Assert.assertEquals(true,flag); 
		
	}
	@Test
	public void testCreateFinalHistogram_illegal_arg() {
		Model model = new Model();
		
		boolean flag = false;
		try{
			model.createFinalHistogram(5, 6, -1, -1);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag); 
		
		flag = false;
		try{
			model.createFinalHistogram(5, 6, -1, 15);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			model.createFinalHistogram(5, 6, 15, 15);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			model.createFinalHistogram(5, 6, 15, -1);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			model.createFinalHistogram(10, 0, 10, 10);//here daily hists not flushed
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	}

	@Test
	public void testComputeStability() {

		Model model = new Model();
		model.write(0, 1.0);
		model.write(0, 1.1);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 2.0);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 2.0);
		model.write(0, 2.0);
		
		model.flush();

		
		model.write(0, 1.0);
		model.write(0, 1.1);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 2.0);
		model.write(0, 2.0);
		model.write(0, 3.0);
		model.write(0, 2.0);
		model.write(0, 2.0);
		
		model.flush();
		Assert.assertEquals(0.0 ,model.computeStability(Histogram.DistanceMeasure.HELLIGNER),0.001);
		
		
		 model = new Model();
			model.write(0, 1.0);
			model.write(0, 1.1);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			
			model.flush();

			
			model.write(0, 1.0);
			model.write(0, 1.1);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 45.0);
			model.flush();
			
		Assert.assertEquals(false, compareDouble(model.computeStability(Histogram.DistanceMeasure.HELLIGNER),0.0,0.001));
		
		 model = new Model();
			model.write(0, 1.0);
			model.write(0, 1.1);
			model.write(0, 45.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			
			model.flush();

			
			model.write(0, 1.0);
			model.write(0, 1.1);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 45.0);
			model.flush();
		
			Assert.assertEquals(0.0 ,model.computeStability(Histogram.DistanceMeasure.HELLIGNER),0.001);
			 model = new Model();
				model.write(0, 1.0);
				model.write(0, 1.1);
				model.write(0, 2.0);
				model.write(0, 3.0);
				model.write(0, 2.0);
				model.write(0, 2.0);
				model.write(0, 3.0);
				model.write(0, 2.0);
				model.write(0, 2.0);
				
				model.flush();

				
				model.write(0, 1.0);
				model.write(0, 1.1);
				model.write(0, 2.0);
				model.write(0, 3.0);
				model.write(0, 2.0);
				model.write(0, 2.0);
				model.write(0, 3.0);
				model.write(0, 2.0);
				model.write(0, 2.0);
				model.write(0, 45.0);
				model.flush();
			
		
		double distCloser =model.computeStability(Histogram.DistanceMeasure.HELLIGNER);
		
		 model = new Model();
			model.write(0, 1.0);
			model.write(0, 1.1);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			
			model.flush();

			
			model.write(0, 1.0);
			model.write(0, 1.1);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 3.0);
			model.write(0, 3.0);
			model.write(0, 3.0);
			model.write(0, 3.0);
			model.write(0, 3.0);
			model.write(0, 3.0);
			model.write(0, 3.0);
			model.write(0, 2.0);
			model.write(0, 2.0);
			model.write(0, 45.0);
			model.write(0, 48.0);
			model.write(0, 46.0);
			model.write(0, 46.0);
			model.flush();

		double distFurther =model.computeStability(Histogram.DistanceMeasure.HELLIGNER);
		
		Assert.assertEquals(true,distCloser < distFurther);
		//Assert.assertEquals(3 ,h.getFrequency(0));
	}
	@Test
	public void testComputeNormalizedEntropy_illega_state() {
		Model model = new Model();
		boolean flag = false;
		try{
			model.computeNormalizedEntropy();
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		 model = new Model();
		
		 model.write(0,0.0);
			model.write(0,0.0);
		flag = false;
		try{
			model.computeNormalizedEntropy();
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		
		model = new Model();
		
		 model.write(0,0.0);
			model.write(0,0.0);
			
			model.flush();
		flag = false;
		try{
			model.computeNormalizedEntropy();
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		
		model = new Model();
		
		 model.write(0,0.0);
			model.write(0,0.0);
			
			model.flush();
			
			 model.write(0,0.0);
				model.write(0,0.0);
		flag = false;
		try{
			model.computeNormalizedEntropy();
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
 
	}
	@Test
	public void testComputeNormalizedEntropy_write() {
		Model model = new Model();

		
		model.write(0,0.0);
		model.write(0,0.0);
		//1 ne
		
		model.flush();
		//[1,0,0,0]
		//Assert.assertEquals(1.0, h1.normalizedEntropy(),0.001);
	
		
		//0 ne
		model.write(0,0.0);
		model.write(0,100.0);
		//model.write(0,40.0);
		//model.write(0,45.0);
		
		model.flush();
		
		Assert.assertEquals(0.5, model.computeNormalizedEntropy(),0.001);
		
		 model = new Model();

		 //1 ne
			model.write(0,0.0);
			model.write(0,0.0);
			
			
			model.flush();
			//[1,0,0,0]
			//Assert.assertEquals(1.0, h1.normalizedEntropy(),0.001);
		
			
			//1ne
			model.write(0,0.0);
			model.write(0,0.0);
			
			model.flush();
			//model.write(0,40.0);
			Assert.assertEquals(1.0, model.computeNormalizedEntropy(),0.001);
			
			 model = new Model();

				model.write(0,0.0);
				model.write(0,100.0);
				//0 ne
				
				model.flush();
				//[1,0,0,0]
				//Assert.assertEquals(1.0, h1.normalizedEntropy(),0.001);
			
				
				//0 ne
				model.write(0,0.0);
				model.write(0,100.0);
				
				model.flush();
				//model.write(0,40.0);
				Assert.assertEquals(0.0, model.computeNormalizedEntropy(),0.001);
		/*
		//[0.5,0.5,0,0]
		Assert.assertEquals(0.0, h1.normalizedEntropy(),0.001);
		
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(0.0);
		h1.addSample(0.1);
		h1.addSample(40.0);
		h1.addSample(45.0);
		
		//[0.6,0.4,0,0]
		Assert.assertEquals(0.02897, h1.normalizedEntropy(),0.0001);*/
	}

	@Test
	public void testWrite_illegal_arg() {
	Model model = new Model();

		
		model.write(0,0.0);
		model.write(0,0.0);
		
		model.flush();
		
		model.write(0,0.0);
		model.write(0,0.0);
		
		model.flush();
		
		boolean flag = false;
		try{
			model.write(1000,0);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		flag = false;
		try{
			model.write(-1,0);
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
		
		
	}

	@Test
	public void testFlush_no_readings() {
	Model model = new Model();
	boolean flag = false;
	try{
		model.flush();
	
	}catch(ConfigurationException e){
		flag = true;
	}
	
	Assert.assertEquals(true,flag);


	}

	@Test
	public void testFlush() {
	Model model = new Model();

		
		model.write(0, 3.0);
		model.flush();
		model.write(0, 3.0);
		model.flush();
		
	}
	
	@Test
	public void testDeleteRawReadings_illegal_state() {
		Model model = new Model();
		boolean flag = false;
		try{
			model.deleteRawReadings();
		
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
		
	}

	@Test
	public void testDeleteRawReadings() {
		Model model = new Model();
		model.write(0,1.0);
		model.flush();
		model.write(0,1.0);
		model.flush();
			model.deleteRawReadings();
				
	}

	public static boolean compareDouble (double desiredValue, double actualValue, double tolerance){
		double diff = Math.abs(desiredValue - actualValue);         //  1000 - 950  = 50
	    tolerance = tolerance * desiredValue;  //  0.2*1000 = 200
	    return diff < tolerance;     //  50<200      = true
	}
}
