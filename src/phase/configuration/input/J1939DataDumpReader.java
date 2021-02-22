package phase.configuration.input;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.Noise;
import common.Sensor;
import common.event.stream.RawSensorDataInputStream;
import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;
import phase.configuraiton.input.j1939.J1939Parser;
import phase.configuraiton.input.j1939.J1939Reader;
import phase.configuraiton.input.j1939.SensorDescription;
import phase.configuraiton.input.j1939.SensorDescriptionMap;
import phase.generation.cosmo.MinMaxPair;

public class J1939DataDumpReader implements J1939Reader {

	private RawSensorDataInputStream dataStream;
	//private Noise noiseGenerator;
	private HashMap<Sensor,Noise> noiseGenerator; //map that has uniform noise from [sensor min,sensor max] 
	private HashMap<Sensor,Sensor> sensorWhiteList;
	
	//used to filter out a portion of data dump
	private Noise pValueGenerator;
	private double filterProbability;//prob 0.1 (10%) means filter 10% of readings, ie only include 90% of datadump, at random
	
	List<SensorDescription> sensorWhiteListDescritions;
	public J1939DataDumpReader(Path j1939SpecPath, Path j1939dataDumpPath, List<Sensor> whiteListSensors, double filterProbability) throws IOException {
		
		if(filterProbability <0 || filterProbability > 1){
			throw new ConfigurationException("filter pvalue excceeds expected range of [0,1], was "+filterProbability);
		}
		this.dataStream = new RawSensorDataInputStream(1024*1024);
		this.noiseGenerator = new HashMap<Sensor,Noise>();
		this.pValueGenerator = new Noise(0,0,0,1,Noise.Distribution.UNIFORM);
		this.filterProbability = filterProbability;
		sensorWhiteList = new HashMap<Sensor,Sensor>();
		sensorWhiteListDescritions = new ArrayList<SensorDescription>(whiteListSensors.size());
		
		//populate whitelist of sensors
		for(Sensor s : whiteListSensors){
			//already added?
			if(!sensorWhiteList.containsKey(s)){
				sensorWhiteList.put(s,s);
			}
		}
		
		Logger log = LoggerFactory.getInstance();
		log.log_debug("about to read j1939 data dump and j1939 specification file...");
		
		J1939Parser parser = new J1939Parser();
		parser.init(j1939SpecPath, j1939dataDumpPath);
		
		SensorDescriptionMap sdMap = parser.getSensorDefinitions();
		
		//create the noise generators for each sensor
		for(Sensor s : whiteListSensors){
			SensorDescription sd = sdMap.getSensorDescription(s.getPgn(), s.getSpn());
			
			Noise n = new Noise(0,0,sd.getMax(),sd.getMin(),Noise.Distribution.UNIFORM);
			this.noiseGenerator.put(s,n);
		}
		
		//iterate all the descriptions of whitelist sensors
		for(Sensor s : whiteListSensors){
			SensorDescription sd = sdMap.getSensorDescription(s.getPgn(),s.getSpn());
			sensorWhiteListDescritions.add(sd);
		}
		
		log.log_debug("about to decode j1939 data...");
		//this will analyze sensor data, calling the method 'readSensorValue'
		parser.analyzeData(this);
		
		//SensorDescriptionMap sdMap = parser.getSensorDefinitions();
		
		
		
	}

	
	public RawSensorDataInputStream getRawDataInputStream(){
		return this.dataStream;
	}
	
	public int sizeRawDataStream(){
		return this.dataStream.size();
	}
	
	public List<phase.generation.cosmo.SensorDescription> getWhiteListSensorDescriptions(){
		
		List<phase.generation.cosmo.SensorDescription> res = new ArrayList<phase.generation.cosmo.SensorDescription>(sensorWhiteListDescritions.size());
		
		for(SensorDescription sd: sensorWhiteListDescritions){
			phase.generation.cosmo.SensorDescription newSd = new phase.generation.cosmo.SensorDescription(sd.getPgn(),sd.getSpn(),new MinMaxPair(sd.getMax(),sd.getMin()));
			res.add(newSd);
		}
		return res;
	}
	@Override
	public void readSensorValue(long timerStamp, int pgn, int spn, double decodedValue, SensorDescription sd) {
		
		Sensor s = new Sensor(pgn,spn);
		
		//ignore non-white list sensors
		if(!sensorWhiteList.containsKey(s)){
			return;
		}
		
	
		//only add unfiltered readings to dump, or add the 1st instance of reading regardless (always want atleast one reading of a sensor)
		if(!skipReading() || !dataStream.containsKey(s)){
			Noise n = noiseGenerator.get(s);
			double noise = n.generateNoise();//generate uniform distribution within bounds of sensor max and min values
		
			dataStream.write(s,decodedValue,noise);
		}
	}

	/**
	 * Generates a boolean flag at random based on filter probability, indicating whether should skip reading or no
	 * @return true when reading should be skipped, false when reading should be included.
	 */
	private boolean skipReading() {
		//skip X % of the data dump
		double skipValue = this.pValueGenerator.generateNoise();
				
		if(skipValue < this.filterProbability){
			return true;
		}else{
			return false;
		}
	}

}
