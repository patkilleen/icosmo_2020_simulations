package test.phase.generation.cosmo;

import static org.junit.Assert.fail;

import org.junit.Test;

import junit.framework.Assert;
import phase.generation.cosmo.MinMaxPair;

public class TestMinMaxPair {

	@Test
	public void testMinMaxPair() {
		
		MinMaxPair p = new MinMaxPair(0,0);
	}

	@Test
	public void testGetMax() {
		MinMaxPair p = new MinMaxPair(1.0,1.5);
		Assert.assertEquals(1.0,p.getMax(),0.0001);
		
		p = new MinMaxPair(5.0,1.5);
		Assert.assertEquals(5.0,p.getMax(),0.0001);
		
		p = new MinMaxPair(-50.0,1.5);
		Assert.assertEquals(-50.0,p.getMax(),0.0001);
	}

	@Test
	public void testSetMax() {
		MinMaxPair p = new MinMaxPair(1.0,1.5);
		p.setMax(10.5);
		Assert.assertEquals(10.5,p.getMax(),0.0001);
		
		p.setMax(5.0);
		Assert.assertEquals(5.0,p.getMax(),0.0001);
		
		p.setMax(-50.0);
		Assert.assertEquals(-50.0,p.getMax(),0.0001);
	}

	@Test
	public void testGetMin() {
		MinMaxPair p = new MinMaxPair(1.0,1.5);
		Assert.assertEquals(1.5,p.getMin(),0.0001);
		
		p = new MinMaxPair(1.5,5.0);
		Assert.assertEquals(5.0,p.getMin(),0.0001);
		
		p = new MinMaxPair(20,-50.0);
		Assert.assertEquals(-50.0,p.getMin(),0.0001);
	}

	@Test
	public void testSetMin() {
		MinMaxPair p = new MinMaxPair(1.0,1.5);
		p.setMin(10.5);
		Assert.assertEquals(10.5,p.getMin(),0.0001);
		
		p.setMin(5.0);
		Assert.assertEquals(5.0,p.getMin(),0.0001);
		
		p.setMin(-50.0);
		Assert.assertEquals(-50.0,p.getMin(),0.0001);
	}

}
