package test.common.event.stream;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.Sensor;
import common.event.ValueNoisePair;
import common.event.stream.RawSensorDataInputStream;

public class TestRawSensorDataInputStream {

	@Test
	public void test() {
		RawSensorDataInputStream in = new RawSensorDataInputStream(4);
		
		in.write(new Sensor(0,0),0.0,1.0);
		in.write(new Sensor(0,0),0.1,1.1);
		in.write(new Sensor(0,0),0.2,1.2);
		
		in.write(new Sensor(1,0),0.4,1.4);
		in.write(new Sensor(1,0),0.5,1.5);
		in.write(new Sensor(1,0),0.6,1.6);
		
		in.write(new Sensor(0,1),0.7,1.7);
		in.write(new Sensor(0,1),0.8,1.8);
		in.write(new Sensor(0,1),0.9,1.9);
		in.write(new Sensor(0,1),1.0,2.0);
		
		List<ValueNoisePair> pairs = in.getSensorReadings(new Sensor(0,0));
		Assert.assertEquals(3,pairs.size());
		Assert.assertEquals(0.0,pairs.get(0).getValue(),0.00001);
		Assert.assertEquals(0.1,pairs.get(1).getValue(),0.00001);
		Assert.assertEquals(0.2,pairs.get(2).getValue(),0.00001);
		
		Assert.assertEquals(1.0,pairs.get(0).getNoise(),0.00001);
		Assert.assertEquals(1.1,pairs.get(1).getNoise(),0.00001);
		Assert.assertEquals(1.2,pairs.get(2).getNoise(),0.00001);
		
		
		
		pairs = in.getSensorReadings(new Sensor(0,1));
		Assert.assertEquals(4,pairs.size());
		Assert.assertEquals(0.7,pairs.get(0).getValue(),0.00001);
		Assert.assertEquals(0.8,pairs.get(1).getValue(),0.00001);
		Assert.assertEquals(0.9,pairs.get(2).getValue(),0.00001);
		Assert.assertEquals(1.0,pairs.get(3).getValue(),0.00001);
		
		Assert.assertEquals(1.7,pairs.get(0).getNoise(),0.00001);
		Assert.assertEquals(1.8,pairs.get(1).getNoise(),0.00001);
		Assert.assertEquals(1.9,pairs.get(2).getNoise(),0.00001);
		Assert.assertEquals(2.0,pairs.get(3).getNoise(),0.00001);
		
		pairs = in.getSensorReadings(new Sensor(1,0));
		Assert.assertEquals(3,pairs.size());
		Assert.assertEquals(0.4,pairs.get(0).getValue(),0.00001);
		Assert.assertEquals(0.5,pairs.get(1).getValue(),0.00001);
		Assert.assertEquals(0.6,pairs.get(2).getValue(),0.00001);
		
		Assert.assertEquals(1.4,pairs.get(0).getNoise(),0.00001);
		Assert.assertEquals(1.5,pairs.get(1).getNoise(),0.00001);
		Assert.assertEquals(1.6,pairs.get(2).getNoise(),0.00001);
		
	}

}
