package test.phase.generation.cosmo;

import java.util.Iterator;

import org.junit.Test;
import junit.framework.Assert;
import common.Sensor;
import common.Vehicle;
import common.exception.ConfigurationException;

import phase.generation.cosmo.COSMOSensorInstance;
import phase.generation.cosmo.MinMaxPair;
import phase.generation.cosmo.Model;

public class TestCOSMOSensorInstance {

	@Test
	public void testCOSMOSensorInstanceIntIntVehicleInt_illegal_arg() {
		boolean flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(-1,-1,null,-1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
		
		
		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(0,-1,null,-1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(-1,0,null,-1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(0,0,null,5);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(-1,0,new Vehicle(0),5);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(0,-1,new Vehicle(0),5);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);



		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(0,0,new Vehicle(0),-1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		
		flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(0,0,new Vehicle(0),0);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}

	@Test
	public void testCOSMOSensorInstanceIntIntVehicleInt_getters() {
		COSMOSensorInstance i= new COSMOSensorInstance(1,2,new Vehicle(0),5);
		Assert.assertEquals(1,i.getPgn());
		Assert.assertEquals(2,i.getSpn());
		Assert.assertEquals(new Vehicle(0),i.getVehicle());
		Assert.assertEquals(0,i.numberOfZValues());
		Assert.assertEquals(5,i.getZValueWindow());
	}
	@Test
	public void testCOSMOSensorInstanceSensorVehicleInt() {
		COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),new Vehicle(0),5);
		Assert.assertEquals(1,i.getPgn());
		Assert.assertEquals(2,i.getSpn());
		Assert.assertEquals(new Vehicle(0),i.getVehicle());
		Assert.assertEquals(0,i.numberOfZValues());
		Assert.assertEquals(5,i.getZValueWindow());
	}
	
	@Test
	public void testCOSMOSensorInstanceSensorVehicleInt_illegal_arg() {
		
		boolean flag = false;
		try{
			COSMOSensorInstance i= new COSMOSensorInstance(null,new Vehicle(0),5);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		
		 flag = false;
			try{
				COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),null,5);

			}catch(ConfigurationException e){
				flag = true;
			}

			Assert.assertEquals(true,flag);

			
			 flag = false;
				try{
					COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),new Vehicle(0),-1);

				}catch(ConfigurationException e){
					flag = true;
				}

				Assert.assertEquals(true,flag);

			
		
					 flag = false;
						try{
							COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),new Vehicle(0),0);

						}catch(ConfigurationException e){
							flag = true;
						}

						Assert.assertEquals(true,flag);
	}


	@Test
	public void testIsCOSMOSensor_and_setCOSMOSensor() {
		COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),new Vehicle(0),5);
		Assert.assertEquals(false,i.isCOSMOSensor());
		i.setCOSMOSensor(true);
		Assert.assertEquals(true,i.isCOSMOSensor());
		i.setCOSMOSensor(false);
		Assert.assertEquals(false,i.isCOSMOSensor());
	}

	

	@Test
	public void testGetModel() {
		COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),new Vehicle(0),5);
		
		Model model = i.getModel();
		Assert.assertEquals(false,null == model);
	}


	@Test
	public void testGetSetMinMaxPair() {
		COSMOSensorInstance i= new COSMOSensorInstance(new Sensor(1,2),new Vehicle(0),5);
		Assert.assertEquals(null, i.getMinMaxPair());
		
		MinMaxPair pair =new MinMaxPair(1,2);
		i.setMinMaxPair(pair);
		Assert.assertEquals(true,pair == i.getMinMaxPair());
	}


}
