package test.phase.generation.fault;

import org.junit.Test;

import common.FaultDescription;
import common.exception.ConfigurationException;
import junit.framework.Assert;
import phase.generation.fault.Fault;
import phase.generation.fault.FaultGenerationVehicle;

public class TestFaultGenerationVehicle {

	@Test
	public void test_constructor_illegal_argument() {

		
		boolean flag = false;
		try{
		FaultGenerationVehicle v = new FaultGenerationVehicle(-1);
		}catch(ConfigurationException e){
			flag = true;
		}
		
		Assert.assertEquals(true,flag);
	}
	
	@Test
	public void test_constructor() {
		Fault f = new Fault(0,0,0,new FaultDescription(-1,null));

		FaultGenerationVehicle v = new FaultGenerationVehicle(0);
		v = new FaultGenerationVehicle(1);
		v = new FaultGenerationVehicle(1000);
	}
	
	@Test
	public void test_addPotentialFault() {
		Fault f = new Fault(0,0,0,new FaultDescription(0,null));

		FaultGenerationVehicle v = new FaultGenerationVehicle(0);
		Assert.assertEquals(0, v.numberOfPotentialFaults());
		v.addPotentialFault(f);
		Assert.assertEquals(1, v.numberOfPotentialFaults());
		Assert.assertEquals(true, v.getPotentialFault(0) == f);
		
		Fault f2 = new Fault(0,0,0,new FaultDescription(1,null));
		
		v.addPotentialFault(f2);
		Assert.assertEquals(2, v.numberOfPotentialFaults());
		Assert.assertEquals(true, v.getPotentialFault(0) == f);
		Assert.assertEquals(true, v.getPotentialFault(1) == f2);
	}

}
