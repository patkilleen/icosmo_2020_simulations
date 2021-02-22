package test.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Sensor;
import common.SortedSensorList;
import common.exception.ConfigurationException;

public class TestSortedSensorList {

	@Test
	public void test_constructor() {
		SortedSensorList list = new SortedSensorList();
		Assert.assertEquals(0,list.size());
	}
	
	@Test
	public void test_list_add_null_argument() {
		SortedSensorList list = new SortedSensorList();
		List<Sensor> arg = null;
		boolean flag = false;
		
		try{
		list.add(arg);
		}catch(ConfigurationException e){
			flag = true;
		}
		Assert.assertEquals(true,flag);
	}
	
	@Test
	public void test_list_add_empty() {
		SortedSensorList list = new SortedSensorList();
		List<Sensor> arg = new ArrayList<Sensor>();

		list.add(arg);
		
	}
	
	@Test
	public void test_list_add_exists() {
		SortedSensorList list = new SortedSensorList();
		List<Sensor> arg = new ArrayList<Sensor>(8);
		arg.add(new Sensor(1,23));
		arg.add(new Sensor(4,56));
		arg.add(new Sensor(8,12));
		arg.add(new Sensor(2,33));

		list.add(arg);
		
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		Assert.assertEquals(true, list.exists(new Sensor(2,33)));
		Assert.assertEquals(false, list.exists(new Sensor(4,57)));
		Assert.assertEquals(false, list.exists(new Sensor(8,100)));
		Assert.assertEquals(false, list.exists(new Sensor(100,100)));
		Assert.assertEquals(false, list.exists(new Sensor(35,23)));
		Assert.assertEquals(4,list.size());
		
	}

	@Test
	public void test_list_add_exists_with_nulls() {
		SortedSensorList list = new SortedSensorList();
		List<Sensor> arg = new ArrayList<Sensor>(8);
		arg.add(new Sensor(1,23));
		arg.add(new Sensor(4,56));
		arg.add(new Sensor(8,12));
		arg.add(new Sensor(2,33));
		arg.add(null);
		arg.add(new Sensor(50,60));

		list.add(arg);
		
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		Assert.assertEquals(true, list.exists(new Sensor(2,33)));
		Assert.assertEquals(true, list.exists(new Sensor(50,60)));
		Assert.assertEquals(false, list.exists(new Sensor(4,57)));
		Assert.assertEquals(false, list.exists(new Sensor(8,100)));
		Assert.assertEquals(false, list.exists(new Sensor(100,100)));
		Assert.assertEquals(false, list.exists(new Sensor(35,23)));
		Assert.assertEquals(false, list.exists(null));
		
		Assert.assertEquals(5,list.size());
	}
	
	@Test
	public void test_add_null_argument() {
		SortedSensorList list = new SortedSensorList();
		Sensor arg = null;
		list.add(arg);
		Assert.assertEquals(0, list.size());
		
	}
	
	
	@Test
	public void test_add_exists() {
		SortedSensorList list = new SortedSensorList();
		
		list.add(new Sensor(1,23));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		Assert.assertEquals(1,list.size());
		
		list.add(new Sensor(4,56));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		
		list.add(new Sensor(8,12));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		
		list.add(new Sensor(2,33));
		Assert.assertEquals(4,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(2,33)));

		Assert.assertEquals(false, list.exists(new Sensor(4,57)));
		Assert.assertEquals(false, list.exists(new Sensor(8,100)));
		Assert.assertEquals(false, list.exists(new Sensor(100,100)));
		Assert.assertEquals(false, list.exists(new Sensor(35,23)));
		
	}

	@Test
	public void test_add_exists_with_nulls() {
		SortedSensorList list = new SortedSensorList();
		
		list.add(new Sensor(1,23));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		Assert.assertEquals(1,list.size());
		
		list.add(new Sensor(4,56));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		
		list.add(new Sensor(8,12));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		
		list.add((Sensor)null);
		Assert.assertEquals(false, list.exists(null));
		Assert.assertEquals(3,list.size());
		
		list.add(new Sensor(2,33));
		Assert.assertEquals(4,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(2,33)));

		Assert.assertEquals(false, list.exists(new Sensor(4,57)));
		Assert.assertEquals(false, list.exists(new Sensor(8,100)));
		Assert.assertEquals(false, list.exists(new Sensor(100,100)));
		Assert.assertEquals(false, list.exists(new Sensor(35,23)));
	}
	
	
	@Test
	public void test_add_exists_removes() {
		SortedSensorList list = new SortedSensorList();
		
		list.add(new Sensor(1,23));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		Assert.assertEquals(1,list.size());
		
		list.remove(new Sensor(1,23));
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
		Assert.assertEquals(0,list.size());
		
		list.add(new Sensor(1,23));
		list.add(new Sensor(4,56));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		
		list.remove(new Sensor(4,56));
		Assert.assertEquals(1,list.size());
		Assert.assertEquals(false, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		list.remove(new Sensor(1,23));
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
		Assert.assertEquals(0,list.size());
		
		list.add(new Sensor(8,12));
		list.add(new Sensor(4,56));
		list.add(new Sensor(1,23));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		
		list.remove(new Sensor(8,12));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(false, list.exists(new Sensor(8,12)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		
		list.remove(new Sensor(1,23));
		Assert.assertEquals(false, list.exists(new Sensor(8,12)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
		
		Assert.assertEquals(1,list.size());
		
		list.add(new Sensor(2,33));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(2,33)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		
		list.remove(new Sensor(2,33));
		Assert.assertEquals(1,list.size());
		Assert.assertEquals(false, list.exists(new Sensor(2,33)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		
		list.remove((Sensor)null);
		Assert.assertEquals(1,list.size());
		
		Assert.assertEquals(false, list.exists(new Sensor(4,57)));
		Assert.assertEquals(false, list.exists(new Sensor(8,100)));
		Assert.assertEquals(false, list.exists(new Sensor(100,100)));
		Assert.assertEquals(false, list.exists(new Sensor(35,23)));
		
	}
	
	@Test
	public void test_list_add_exists_removes() {
		SortedSensorList list = new SortedSensorList();
		List<Sensor> arg = new ArrayList<Sensor>(0);
		
		list.add(new Sensor(1,23));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		Assert.assertEquals(1,list.size());
		
		arg.add(new Sensor(1,23));
		list.remove(arg);
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
		Assert.assertEquals(0,list.size());
		
		list.add(new Sensor(1,23));
		list.add(new Sensor(4,56));
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		
		arg.add(new Sensor(4,56));
		arg.add(new Sensor(1,23));
		list.remove(arg);
		Assert.assertEquals(false, list.exists(new Sensor(4,56)));
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
		Assert.assertEquals(0,list.size());
		
		list.add(new Sensor(8,12));
		list.add(new Sensor(4,56));
		list.add(new Sensor(1,23));
		Assert.assertEquals(3,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		Assert.assertEquals(true, list.exists(new Sensor(4,56)));
		Assert.assertEquals(true, list.exists(new Sensor(1,23)));
		
		list.remove(arg);
		Assert.assertEquals(1,list.size());
		Assert.assertEquals(true, list.exists(new Sensor(8,12)));
		Assert.assertEquals(false, list.exists(new Sensor(4,56)));
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
	
		arg.add(new Sensor(8,12));
		list.remove(arg);
		Assert.assertEquals(0,list.size());
		Assert.assertEquals(false, list.exists(new Sensor(8,12)));
		Assert.assertEquals(false, list.exists(new Sensor(4,56)));
		Assert.assertEquals(false, list.exists(new Sensor(1,23)));
	
		
		
		Assert.assertEquals(false, list.exists(new Sensor(4,57)));
		Assert.assertEquals(false, list.exists(new Sensor(8,100)));
		Assert.assertEquals(false, list.exists(new Sensor(100,100)));
		Assert.assertEquals(false, list.exists(new Sensor(35,23)));
		
	}
}
