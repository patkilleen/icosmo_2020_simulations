package common.event.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import common.Sensor;
import common.event.DatasetSensorSampleEvent;
import common.event.ValueNoisePair;
import common.exception.ConfigurationException;

public class RawSensorDataInputStream{

	private HashMap<Sensor,List<ValueNoisePair>> rawSensorData;
	
	private int defaultSensorReadingsSize;
	//List<DatasetSensorSampleEvent> rawSensorData;
	
	public RawSensorDataInputStream(List<DatasetSensorSampleEvent> rawSensorData){
		this(rawSensorData.size());
		
		for(DatasetSensorSampleEvent e : rawSensorData){
			this.write(e.getSensor(),e.getValue(), e.getNoise());
		}
	}
/*
	public RawSensorDataInputStream(int size){
		this.rawSensorData = new ArrayList<DatasetSensorSampleEvent>(size);
		
	}M*/

	public RawSensorDataInputStream(int size){
		rawSensorData = new HashMap<Sensor,List<ValueNoisePair>>();
		this.defaultSensorReadingsSize = size;
		
	}
	
	public boolean containsKey(Sensor s){
		return rawSensorData.containsKey(s);
	}
	
	public void write(Sensor s, double reading, double noise){
		
		if(s == null){
			throw new ConfigurationException("null sensor, can't add to raw input data stream");
		}
		
		List<ValueNoisePair> readings = null;
		if(rawSensorData.containsKey(s)){
			readings = rawSensorData.get(s);
		}else{
			readings = new ArrayList<ValueNoisePair>(defaultSensorReadingsSize);
			rawSensorData.put(s, readings);
		}
			
		readings.add( new ValueNoisePair(reading,noise));
		
	}
	
	public Iterator<Sensor> keyIterator(){
		return rawSensorData.keySet().iterator();
	}
	
	public List<ValueNoisePair> getSensorReadings(Sensor key){
		if(key == null){
			throw new ConfigurationException("null sensor, cannot iterate raw input sensor data");
		}
		return rawSensorData.get(key);
	}
	
	/**
	 * returns the number of readings in to total in this stream
	 * @return number of readings in stream
	 */
	public int size(){
		int res = 0;
		Iterator<Sensor> it = keyIterator();
		while(it.hasNext()){
			Sensor key = it.next();
			List<ValueNoisePair> readings = getSensorReadings(key);
			res+= readings.size();
		}
		
		return res;
	}

}
