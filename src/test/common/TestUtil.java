package test.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Util;
import junit.framework.Assert;

public class TestUtil {

	@Test
	public void test() {
		List<Integer> ints = new ArrayList<Integer>(0);
		Assert.assertEquals(0,Util.unique(ints).size());
		Assert.assertEquals(null,Util.unique(null));
		
		ints.add(1);
		ints.add(1);
		ints.add(1);
		ints.add(2);
		ints.add(3);
		ints.add(4);
		ints.add(4);
		
		List<Integer> actual = Util.unique(ints);
		Assert.assertEquals(4,actual.size());
		Assert.assertEquals((Integer)1,(Integer)actual.get(0));
		Assert.assertEquals((Integer)2,(Integer)actual.get(1));
		Assert.assertEquals((Integer)3,(Integer)actual.get(2));
		Assert.assertEquals((Integer)4,(Integer)actual.get(3));
		
		Assert.assertEquals(false,Util.allElementsUnique(ints));
		
		ints = new ArrayList<Integer>(0);

		
		ints.add(1);
		ints.add(2);
		ints.add(3);
		ints.add(4);
		
		Assert.assertEquals(true,Util.allElementsUnique(ints));
		
		ints = new ArrayList<Integer>(0);

		
		ints.add(1);
		
		Assert.assertEquals(true,Util.allElementsUnique(ints));
	}

}
