package test.phase.generation.history;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import common.Vehicle;
import common.exception.ConfigurationException;
import phase.generation.history.AnalysisSensor;

public class TestAnalysisSensor {

	@Test
	public void test_constructor_illegal_arg(){
boolean flag = false;
		
		try{
			AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,0.0,0.0,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			AnalysisSensor s = new AnalysisSensor(0,0,true,true,-1,0.0,0.0,new Vehicle(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			AnalysisSensor s = new AnalysisSensor(0,0,true,true,1.5,0.0,0.0,new Vehicle(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,-1,0.0,0.0,new Vehicle(0));
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
			AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
	}
	@Test
	public void test_constructor1() {
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
	}

	@Test
	public void test_constructor2() {
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,0.0,0.0,new Vehicle(0));
	}

	@Test
	public void testSetContribution() {
		double error = 0.0001;
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		s.setContribution(5);
		Assert.assertEquals(5,s.getContribution(),error);
	}

	@Test
	public void testSetPotentialContribution() {
		double error = 0.0001;
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		s.setPotentialContribution(5);
		Assert.assertEquals(5,s.getPotentialContribution(),error);
	}

	@Test
	public void testSetVehicle() {
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		Vehicle v = new Vehicle(1);
		s.setVehicle(v);
		Assert.assertEquals(true,s.getVehicle() == v);
	}

	@Test
	public void testSetVehicleillega_arg() {
		
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		
boolean flag = false;
		
		try{
		s.setVehicle(null);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}
	@Test
	public void testSetSensorSelectionChange() {
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		
		s.setSensorSelectionChange(true);
		Assert.assertEquals(true,s.isSensorSelectionChange());
		
		s.setSensorSelectionChange(false);
		Assert.assertEquals(false,s.isSensorSelectionChange());
	}

	@Test
	public void testSetCosmoSensor() {
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		
		s.setCosmoSensor(true);
		Assert.assertEquals(true,s.isCosmoSensor());
		
		s.setCosmoSensor(false);
		Assert.assertEquals(false,s.isCosmoSensor());
	}

	@Test
	public void testSetZscore() {
		double error = 0.0001;
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		s.setZscore(0.55);
		Assert.assertEquals(0.55,s.getZscore(),error);
	}
	@Test
	public void testSetZscore_illega_arg() {
		
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		
boolean flag = false;
		
		try{
		s.setZscore(-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
		s.setZscore(-1000);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
flag = false;
		
		try{
		s.setZscore(-0.001);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
		
flag = false;
		
		try{
		s.setZscore(1.001);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
		s.setZscore(2);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
		
flag = false;
		
		try{
		s.setZscore(1000);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}

	
	@Test
	public void testSetTimeSensorSelectionChange() {
		
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		s.setTimeSensorSelectionChange(5);
		Assert.assertEquals(5,s.getTimeSensorSelectionChange());
	}
	
	@Test
	public void testSetTimeSensorSelectionChange_illega_arg() {
		
		AnalysisSensor s = new AnalysisSensor(0,0,true,true,0.0,1,0.0,0.0,new Vehicle(0));
		
boolean flag = false;
		
		try{
			s.setTimeSensorSelectionChange(-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true, flag);
	}

}
