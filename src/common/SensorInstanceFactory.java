package common;

public interface SensorInstanceFactory {
	public SensorInstance newInstance(Vehicle v, Sensor s);
}
