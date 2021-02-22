package test.common;

import static org.junit.Assert.*;

import org.junit.Test;

import common.Noise;
import junit.framework.Assert;

public class TestNoise {

	@Test
	public void testContructor() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN); 
	}


	@Test
	public void testSettersGetters() {
		Noise n = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		Assert.assertEquals(1.0,n.getNoiseMean());
		Assert.assertEquals(2.0,n.getNoiseSD());
		Assert.assertEquals(3.0,n.getNoiseMax());
		Assert.assertEquals(4.0,n.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n.getType());

		n = new Noise(5,4,3,2,Noise.Distribution.UNIFORM);
		Assert.assertEquals(5.0,n.getNoiseMean());
		Assert.assertEquals(4.0,n.getNoiseSD());
		Assert.assertEquals(3.0,n.getNoiseMax());
		Assert.assertEquals(2.0,n.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.UNIFORM,n.getType());

		n = new Noise(5,4,3,2,Noise.Distribution.NORMAL);
		Assert.assertEquals(5.0,n.getNoiseMean());
		Assert.assertEquals(4.0,n.getNoiseSD());
		Assert.assertEquals(3.0,n.getNoiseMax());
		Assert.assertEquals(2.0,n.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.NORMAL,n.getType());
		//		fail("Not yet implemented");
	}

	@Test
	public void addWhiteNoise() {
		Noise n1 = new Noise(1,2,3,4,Noise.Distribution.GAUSSIAN);
		Noise n2 = n1.addWhiteNoise(n1);

		//n1 shouldn't change
		Assert.assertEquals(1.0,n1.getNoiseMean());
		Assert.assertEquals(2.0,n1.getNoiseSD());
		Assert.assertEquals(3.0,n1.getNoiseMax());
		Assert.assertEquals(4.0,n1.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n1.getType());

		Assert.assertEquals(2.0,n2.getNoiseMean());
		Assert.assertEquals(4.0,n2.getNoiseSD());
		Assert.assertEquals(6.0,n2.getNoiseMax());
		Assert.assertEquals(8.0,n2.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.GAUSSIAN,n2.getType());


		n1 = new Noise(1,2,3,4,Noise.Distribution.UNIFORM);
		n2 = n1.addWhiteNoise(n1);

		//n1 shouldn't change
		Assert.assertEquals(1.0,n1.getNoiseMean());
		Assert.assertEquals(2.0,n1.getNoiseSD());
		Assert.assertEquals(3.0,n1.getNoiseMax());
		Assert.assertEquals(4.0,n1.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.UNIFORM,n1.getType());

		Assert.assertEquals(2.0,n2.getNoiseMean());
		Assert.assertEquals(4.0,n2.getNoiseSD());
		Assert.assertEquals(6.0,n2.getNoiseMax());
		Assert.assertEquals(8.0,n2.getNoiseMin());
		Assert.assertEquals(Noise.Distribution.UNIFORM,n2.getType());
		//		fail("Not yet implemented");
	}


	@Test
	public void testGenerateNOise() {
		Noise n = new Noise(1,0,100,90,Noise.Distribution.GAUSSIAN);

		//repeat test 1000 times
		for(int i =0; i < 1000;i++){
			double actual = n.generateNoise();

			boolean expected = actual<80;
			Assert.assertEquals(true,expected);
		}

		n = new Noise(1,0,100,90,Noise.Distribution.NORMAL);

		//repeat test 1000 times
		for(int i =0; i < 1000;i++){
			double actual = n.generateNoise();

			boolean expected = actual<80;
			Assert.assertEquals(true,expected);
		}


		n = new Noise(1,0,100,90,Noise.Distribution.UNIFORM);

		//repeat test 1000 times
		for(int i =0; i < 1000;i++){
			double actual = n.generateNoise();

			boolean expected = (actual>=90) && (actual <= 100);
			Assert.assertEquals(true,expected);
		}
	}
}
