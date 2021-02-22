package common.event;

import common.Sensor;
import common.Vehicle;

public class DatasetSensorSampleEvent extends Event {

	//SensorDataEvent Attributes
		private double value;
		private double noise;
		private Sensor sensor;
		
		//------------------------
		// CONSTRUCTOR
		//------------------------

		public DatasetSensorSampleEvent(Sensor aSensor, double aValue, double noise)
		{
			this.sensor = aSensor;
			this.value = aValue;
			this.noise = noise;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public double getNoise() {
			return noise;
		}

		public void setNoise(double noise) {
			this.noise = noise;
		}

		public Sensor getSensor() {
			return sensor;
		}

		public void setSensor(Sensor sensor) {
			this.sensor = sensor;
		}
		

}
