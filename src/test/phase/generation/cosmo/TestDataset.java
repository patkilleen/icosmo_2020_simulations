package test.phase.generation.cosmo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cern.colt.list.DoubleArrayList;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.cosmo.Dataset;

public class TestDataset {

	@Test
	public void testDataset() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(0.0);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		new Dataset(values);
	}

	@Test
	public void testDataset_illegal_arg() {
		

		DoubleArrayList values = new DoubleArrayList(0);
 
		
		boolean flag = false;

		try{
			Dataset ds = new Dataset(values);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
		
		flag = false;

		try{
			Dataset ds = new Dataset(null);
		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true, flag);
	}

	
	@Test
	public void testMax() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(0.0);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(3.5);
		values.add(3.2);
		values.add(2.5);
		values.add(0.0);
		values.add(-3.0);
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(3.5,ds.max(),0.0001);
	}

	@Test
	public void testMax_1_element() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);

		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(1.0,ds.max(),0.0001);
	}


	@Test
	public void testMin_1_element() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);

		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(1.0,ds.min(),0.0001);
	}
	
	@Test
	public void testMin() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(0.0);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(3.5);
		values.add(3.2);
		values.add(2.5);
		values.add(0.0);
		values.add(-3.0);
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(-3.0,ds.min(),0.0001);
	}

	@Test
	public void testMean() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(3.5,ds.mean(),0.0001);
		
		values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		values.add(-5.0);
		
		ds = new Dataset(values);
		
		Assert.assertEquals(2.28571,ds.mean(),0.0001);
	}

	@Test
	public void testVar() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(2.9166,ds.var(),0.0001);
		
		values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		values.add(-5.0);
		
		ds = new Dataset(values);
		
		Assert.assertEquals(11.3469,ds.var(),0.0001);
	}

	@Test
	public void testStd() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(1.7078,ds.std(),0.0001);
		
		values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		values.add(-5.0);
		
		ds = new Dataset(values);
		
		Assert.assertEquals(3.3685,ds.std(),0.0001);
	}

	@Test
	public void testMedian() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(50.0);//median
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(50.0,ds.median(),0.0001);
		
		values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);//median
		values.add(5.0);
		values.add(6.0);
		values.add(-5.0);
		
		ds = new Dataset(values);
		
		Assert.assertEquals(4.0,ds.median(),0.0001);
		
		values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);//median
		values.add(5.0);//median
		values.add(6.0);
		values.add(-5.0);
		values.add(-10.0);
		
		ds = new Dataset(values);
		
		Assert.assertEquals(4.5,ds.median(),0.0001);
	}


	@Test
	public void testGetData() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);

		
		Assert.assertEquals(6,ds.size());
		Assert.assertEquals(1.0,ds.get(0),0.0001);
		Assert.assertEquals(2.0,ds.get(1),0.0001);
		Assert.assertEquals(3.0,ds.get(2),0.0001);
		Assert.assertEquals(4.0,ds.get(3),0.0001);
		Assert.assertEquals(5.0,ds.get(4),0.0001);
		Assert.assertEquals(6.0,ds.get(5),0.0001);
	}

	
	@Test
	public void testSize() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);
		
		Assert.assertEquals(6,ds.size());
	}

	@Test
	public void testGet() {
		DoubleArrayList values = new DoubleArrayList(4);
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		
		Dataset ds = new Dataset(values);

		Assert.assertEquals(1.0,ds.get(0),0.0001);
		Assert.assertEquals(2.0,ds.get(1),0.0001);
		
	}

}
