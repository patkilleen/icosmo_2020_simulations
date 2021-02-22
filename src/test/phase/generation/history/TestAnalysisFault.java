package test.phase.generation.history;

import org.junit.Assert;
import org.junit.Test;

import common.FaultDescription;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import phase.generation.history.AnalysisFault;

public class TestAnalysisFault {

	@Test
	public void test_consctructor1_illegal_argument() {
		boolean flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(-1,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
		flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(-1000,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(-1,new FaultDescription(-1,null));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}


	@Test
	public void test_consctructor2_illegal_argument() {
		boolean flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(-1,-1,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
		flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(-1000,-1,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(-1,-1,new FaultDescription(-1,null));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			AnalysisFault f = new AnalysisFault(5,4,new FaultDescription(-1,null));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}
	
	@Test
	public void test_consctructor1() {
		
			AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
	}
	

	@Test
	public void test_consctructor2() {
		
			AnalysisFault f = new AnalysisFault(0,1,new FaultDescription(-1,null));
	}
	
	@Test
	public void test_existsInTimeWindow1_repair_same_day() {

		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		f.setRepairedTime(0);
		
		Assert.assertEquals(true,f.existsInTimeWindow(0));
		Assert.assertEquals(false,f.existsInTimeWindow(1));
		Assert.assertEquals(false,f.existsInTimeWindow(1000));
		
		f.setOccurenceTime(10);
		f.setRepairedTime(10);
		Assert.assertEquals(true,f.existsInTimeWindow(10));
		Assert.assertEquals(false,f.existsInTimeWindow(9));
		Assert.assertEquals(false,f.existsInTimeWindow(2));
		Assert.assertEquals(false,f.existsInTimeWindow(11));
		Assert.assertEquals(false,f.existsInTimeWindow(20));
		Assert.assertEquals(false,f.existsInTimeWindow(1000));
	
	}
	
	@Test
	public void test_existsInTimeWindow2_repair_same_day() {

		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		f.setRepairedTime(0);
		
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(1,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(2,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,100));
		
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,1000));
		
		Assert.assertEquals(false,f.existsInTimeWindow(500,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(1000,1,1000));
		Assert.assertEquals(true,f.existsInTimeWindow(0,100,1));
		Assert.assertEquals(true,f.existsInTimeWindow(0,1000,1));
		
		Assert.assertEquals(false,f.existsInTimeWindow(5,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,1000));
		
		
		f.setOccurenceTime(10);
		f.setRepairedTime(10);
		
		Assert.assertEquals(false,f.existsInTimeWindow(0,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(1,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,100));
		
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(500,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(1000,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(0,100,1));
		Assert.assertEquals(false,f.existsInTimeWindow(0,1000,1));
		
		Assert.assertEquals(false,f.existsInTimeWindow(5,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,1000));
	
	}
	
	@Test
	public void test_existsInTimeWindow1_repair_1_day() {

		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		f.setRepairedTime(1);
		
		Assert.assertEquals(true,f.existsInTimeWindow(0));
		Assert.assertEquals(true,f.existsInTimeWindow(1));
		Assert.assertEquals(false,f.existsInTimeWindow(2));
		Assert.assertEquals(false,f.existsInTimeWindow(1000));
		
		f.setOccurenceTime(10);
		f.setRepairedTime(11);
		Assert.assertEquals(true,f.existsInTimeWindow(11));
		Assert.assertEquals(true,f.existsInTimeWindow(10));
		Assert.assertEquals(false,f.existsInTimeWindow(9));
		Assert.assertEquals(false,f.existsInTimeWindow(2));
		Assert.assertEquals(false,f.existsInTimeWindow(20));
		Assert.assertEquals(false,f.existsInTimeWindow(1000));
	
	}
	
	@Test
	public void test_existsInTimeWindow2_repair_1_day() {

		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		f.setRepairedTime(1);
		
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(1,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(2,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(3,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(3,2,2));
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,100));
		
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,1000));
		
		Assert.assertEquals(false,f.existsInTimeWindow(500,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(1000,1,1000));
		Assert.assertEquals(true,f.existsInTimeWindow(0,100,1));
		Assert.assertEquals(true,f.existsInTimeWindow(0,1000,1));
		
		Assert.assertEquals(false,f.existsInTimeWindow(5,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,1000));
		
		
		f.setOccurenceTime(10);
		f.setRepairedTime(11);
		
		Assert.assertEquals(false,f.existsInTimeWindow(0,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(1,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(2,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(10,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(11,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(12,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(13,1,1));
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,100));
		
		Assert.assertEquals(true,f.existsInTimeWindow(0,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(500,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(1000,1,1000));
		Assert.assertEquals(false,f.existsInTimeWindow(0,100,1));
		Assert.assertEquals(false,f.existsInTimeWindow(0,1000,1));
		
		Assert.assertEquals(false,f.existsInTimeWindow(5,1,1));
		Assert.assertEquals(false,f.existsInTimeWindow(50,1,100));
		Assert.assertEquals(false,f.existsInTimeWindow(100,1,1000));
	
	}
	
	@Test
	public void test_existsInTimeWindow1_repair_5_days() {

		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		f.setRepairedTime(4);
		
		Assert.assertEquals(true,f.existsInTimeWindow(0));
		Assert.assertEquals(true,f.existsInTimeWindow(1));
		Assert.assertEquals(true,f.existsInTimeWindow(2));
		Assert.assertEquals(true,f.existsInTimeWindow(3));
		Assert.assertEquals(true,f.existsInTimeWindow(4));
		Assert.assertEquals(false,f.existsInTimeWindow(5));
		Assert.assertEquals(false,f.existsInTimeWindow(1000));
		
		f.setOccurenceTime(10);
		f.setRepairedTime(14);
		Assert.assertEquals(false,f.existsInTimeWindow(15));
		Assert.assertEquals(true,f.existsInTimeWindow(14));
		Assert.assertEquals(true,f.existsInTimeWindow(13));
		Assert.assertEquals(true,f.existsInTimeWindow(12));
		Assert.assertEquals(true,f.existsInTimeWindow(11));
		Assert.assertEquals(true,f.existsInTimeWindow(10));
		Assert.assertEquals(false,f.existsInTimeWindow(9));
		Assert.assertEquals(false,f.existsInTimeWindow(2));
		Assert.assertEquals(false,f.existsInTimeWindow(20));
		Assert.assertEquals(false,f.existsInTimeWindow(1000));
	
	}

	@Test
	public void existsInTimeWindow_illegal_state_not_repaired_yet() {
		
		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		boolean flag = false;
		
		try{
			f.existsInTimeWindow(10);
		}catch(SimulationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
	}
	@Test
	public void existsInTimeWindow_illegal_argument() {
		
		AnalysisFault f = new AnalysisFault(0,new FaultDescription(-1,null));
		boolean flag = false;
		
		try{
			f.existsInTimeWindow(-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			f.existsInTimeWindow(-1000);
		}catch(ConfigurationException e){
			flag = true;
		}
		

		Assert.assertEquals(true, flag);
		
		flag = false;
		
		try{
			f.existsInTimeWindow(-1,-1,-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);

		flag = false;
		
		try{
			f.existsInTimeWindow(0,-1,-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			f.existsInTimeWindow(-1,0,-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			f.existsInTimeWindow(-1,-1,0);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			f.existsInTimeWindow(0,0,-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			f.existsInTimeWindow(0,-1,0);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			f.existsInTimeWindow(0,-1,-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
	}
	@Test
	public void test_getters_setters() {
		FaultDescription fd = new FaultDescription(-1,null);
		AnalysisFault f = new AnalysisFault(0,fd);
		
		Assert.assertEquals(true,fd == f.getFaultDescrition());
		
		Assert.assertEquals(0,f.getOccurenceTime());
		
		f.setOccurenceTime(50);
		
		Assert.assertEquals(50,f.getOccurenceTime());
		
		Assert.assertEquals(AnalysisFault.NO_REPAIR_TIME,f.getRepairedTime());
		f.setRepairedTime(55);
		
		Assert.assertEquals(55,f.getRepairedTime());
		Assert.assertEquals(50,f.getOccurenceTime());
		
	}
	

	@Test
	public void test_getters_setters_illegal_argument() {
		boolean flag = false;
		FaultDescription fd = new FaultDescription(-1,null);
		AnalysisFault f = new AnalysisFault(0,fd);
		try{
			f.setOccurenceTime(-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
		flag = false;
		
		
		try{
			f.setOccurenceTime(-1000);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
		f.setOccurenceTime(50);
		flag = false;
		
		try{
			f.setRepairedTime(49);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
		flag = false;
		
		flag = false;
		
		try{
			f.setRepairedTime(5);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
		flag = false;
	}
	
	@Test
	public void test_hasBeenRepaired() {
		
		FaultDescription fd = new FaultDescription(-1,null);
		AnalysisFault f = new AnalysisFault(0,fd);
		
		Assert.assertEquals(false, f.hasBeenRepaired());
		
		f.setRepairedTime(0);
		Assert.assertEquals(true, f.hasBeenRepaired());
		
		f.setRepairedTime(1);
		Assert.assertEquals(true, f.hasBeenRepaired());
		
		f.setRepairedTime(100);
		Assert.assertEquals(true, f.hasBeenRepaired());
	}
}
