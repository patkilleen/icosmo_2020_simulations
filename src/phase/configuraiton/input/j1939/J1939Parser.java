/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4359.01582bf35 modeling language!*/
package phase.configuraiton.input.j1939;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import common.log.Logger;
import common.log.LoggerFactory;

// line 30 "model.ump"
// line 81 "model.ump"
public class J1939Parser
{

	/*
	 *	number of bytes in the data section of j1939 packet
	 */
	final public static int J1939_PACKET_DATA_LENGTH = 8;


	/*
	 *	number of bits in a long
	 */

	final public static int LONG_SIZE = 64;


				
	public static byte [] ALL_ONES = new byte[J1939_PACKET_DATA_LENGTH];
	
	static{
		for(int i = 0;i<ALL_ONES.length;i++){
			//0xfffff
			ALL_ONES[i] = (byte) ~0x00;
		}
	}
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//J1939Parser Associations
	private List<J1939Packet> readings;
	private SensorDescriptionMap sensorDefinitions;
	private HashMap<Integer,Object> failedSensorDefinitions;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public J1939Parser(SensorDescriptionMap aSensorDefinitions)
	{
		readings = new ArrayList<J1939Packet>();
		if (!setSensorDefinitions(aSensorDefinitions))
		{
			throw new RuntimeException("Unable to create J1939Parser due to aSensorDefinitions");
		}
	}
	
	public J1939Parser()
	{
		failedSensorDefinitions = new HashMap<Integer,Object>();
	}

	public SensorDescriptionMap getSensorDescriptionMap(){
		
		return this.sensorDefinitions;
	}
	//------------------------
	// INTERFACE
	//------------------------
	/* Code from template association_GetMany */
	public J1939Packet getReading(int index)
	{
		J1939Packet aReading = readings.get(index);
		return aReading;
	}

	public List<J1939Packet> getReadings()
	{
		List<J1939Packet> newReadings = Collections.unmodifiableList(readings);
		return newReadings;
	}

	public int numberOfReadings()
	{
		int number = readings.size();
		return number;
	}

	public boolean hasReadings()
	{
		boolean has = readings.size() > 0;
		return has;
	}

	public int indexOfReading(J1939Packet aReading)
	{
		int index = readings.indexOf(aReading);
		return index;
	}
	/* Code from template association_GetOne */
	public SensorDescriptionMap getSensorDefinitions()
	{
		return sensorDefinitions;
	}
	/* Code from template association_MinimumNumberOfMethod */
	public static int minimumNumberOfReadings()
	{
		return 0;
	}
	/* Code from template association_AddUnidirectionalMany */
	public boolean addReading(J1939Packet aReading)
	{
		boolean wasAdded = false;
		if (readings.contains(aReading)) { return false; }
		readings.add(aReading);
		wasAdded = true;
		return wasAdded;
	}

	public boolean removeReading(J1939Packet aReading)
	{
		boolean wasRemoved = false;
		if (readings.contains(aReading))
		{
			readings.remove(aReading);
			wasRemoved = true;
		}
		return wasRemoved;
	}
	/* Code from template association_AddIndexControlFunctions */
	public boolean addReadingAt(J1939Packet aReading, int index)
	{  
		boolean wasAdded = false;
		if(addReading(aReading))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfReadings()) { index = numberOfReadings() - 1; }
			readings.remove(aReading);
			readings.add(index, aReading);
			wasAdded = true;
		}
		return wasAdded;
	}

	public boolean addOrMoveReadingAt(J1939Packet aReading, int index)
	{
		boolean wasAdded = false;
		if(readings.contains(aReading))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfReadings()) { index = numberOfReadings() - 1; }
			readings.remove(aReading);
			readings.add(index, aReading);
			wasAdded = true;
		} 
		else 
		{
			wasAdded = addReadingAt(aReading, index);
		}
		return wasAdded;
	}
	/* Code from template association_SetUnidirectionalOne */
	public boolean setSensorDefinitions(SensorDescriptionMap aNewSensorDefinitions)
	{
		boolean wasSet = false;
		if (aNewSensorDefinitions != null)
		{
			sensorDefinitions = aNewSensorDefinitions;
			wasSet = true;
		}
		return wasSet;
	}

	public void delete()
	{
		readings.clear();
		sensorDefinitions = null;
	}

	/**
	 * Parse 8 bytes of j1939 data and retreives the encoded value starting at first bit.
	 * @param length the number of bits the value is encoded with. 
	 * @param data 8 bytes of data 
	 * @return encoded value found in the data.
	 */
	private static long getEncodedValue(int length,byte[] data){
		//0 for start index
		return getEncodedValue(0, length,data);
	}

	/**
	 * Parse 8 bytes of j1939 data and retreives the encoded value given bit offets.
	 * @param offset The bit index the value begins at.
	 * @param length the number of bits the value is encoded with. 
	 * @param data 8 bytes of data 
	 * @return encoded value found in the data.
	 */
	private static long getEncodedValue(int offset, int length, byte[] data){

		if(data.length !=J1939_PACKET_DATA_LENGTH){
			throw new IllegalArgumentException("Excepted byte array of length 8 but was: "+data.length);
		}

		long word64Bits = ByteBuffer.wrap(data).getLong();

		long res = word64Bits << offset;
		res = res >>> LONG_SIZE-length;
		return res;

	}

	/**
	 * Decodes a sensor value from a J1939 packet.
	 * @param offset The bit index that the sensor value begins at.
	 * @param length The number of bits the sensor's value is encoded in. 
	 * @param m The resolution
	 * @param b The offset
	 * @param data The 8 bytes of data
	 * @return Decoded sensor value.
	 */
	private static double decodeSensorValue(int offset, int length, double m, double b, byte [] data){

		return decodeSensorValue(m,b,getEncodedValue(offset,length,data));
	}
	
	/**
	 * Decodes a sensor value from a J1939 packet.
	 * @param m The resolution
	 * @param b The offset
	 * @param encodedValue raw j1939 value found in packet
	 * @return Decoded sensor value.
	 */
	private static double decodeSensorValue(double m, double b, double encodedValue){

		double x;
		x = m*encodedValue + b;

		return x;

	}
	
	private static boolean isReadingNull(long encodedValue, int offset, int length){
		boolean res = true;
		
		//0xfffff..ff buffer
		ByteBuffer maskBuf=ByteBuffer.allocate(8);
		long nullLong = ~0x00;
		//put 0xffff
		maskBuf.putLong(nullLong);
		
		//get subset of mask
		nullLong = getEncodedValue(offset,length,maskBuf.array());
		
		res = (nullLong == encodedValue);
		return res;
	}
	//------------------------
	// DEVELOPER CODE - PROVIDED AS-IS
	//------------------------

	// line 31 "model.ump"
	
	public static SensorDescriptionMap parseSpecDoc(Path csvFile) throws IOException{
		long lines = 0;
		SensorDescriptionMap res = new SensorDescriptionMap();

	    BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					csvFile.toString()));
			String currentLine = null;
		      //skip first few line, header of csv
		      currentLine = reader.readLine();
		      while((currentLine = reader.readLine()) != null){//while there is content on the current line
		    	  SensorDescription sd = parseSensorDescription(currentLine);
		    	  
		    	  //make sure it was parsed correctly
		    	  if(sd!=null){
		    		  
		    		  res.putSensorDescription(sd.getPgn(),sd.getSpn(), sd);
		    	  }else{
		    		//  System.out.println("Error near line: "+lines);
		    	  }
		    	  
		    	  lines++;
		      }
		} catch (IOException e) {
			e.printStackTrace(); //handle an exception here
		      throw new IOException("Problem reading J1939 specification csv file: "+csvFile.toString()+". Error near line: "+lines);
		}finally{
			try{reader.close();}catch(Exception e){}
		}
		return res;
	}
	
	// line 32 "model.ump"
	private static SensorDescription parseSensorDescription(String specDocLine){
		//used to count elements
		int elemCounter=0;
		
		SensorDescription res = new SensorDescription();
		try{
			StringTokenizer csElems = new StringTokenizer(specDocLine,",");
			
			//parse pgn
			res.setPgn(Integer.parseInt(csElems.nextToken()));
			
			elemCounter++;
			//parse pgn name
			res.setPgName(csElems.nextToken());
			
			elemCounter++;
			
			//parse the starting bit index of encoded sensor value
			int offset = parseSPNOffset(csElems.nextToken());
			res.setOffset(offset);
			elemCounter++;
			//parse spn
			res.setSpn(Integer.parseInt(csElems.nextToken()));
			
			elemCounter++;
			//parse spn name
			res.setName(csElems.nextToken());
			
			elemCounter++;
			//parse the length (format: digit bits/bytes
			int length = parseSPNLength(csElems.nextToken());
			res.setLength(length);
			elemCounter++;
			
			
			 
			String resTmp = csElems.nextToken();
			//now parse M(resolution)
			StringTokenizer resolutionElems = new StringTokenizer(resTmp," ");
			
			//is form of 1/num ... or 1 unit?
			if(resTmp.substring(0,2).equals("1/")){
				resTmp = resolutionElems.nextToken();
				int divisor = Integer.parseInt(resTmp.substring(2,resTmp.length()));
				res.setM((1.0/(double)divisor));
			}else{
				res.setM(Double.parseDouble(resolutionElems.nextToken()));	
			}
			elemCounter++;
			
			
			//parse b(offset of decoding value)
			resTmp=csElems.nextToken();
			
			//is there a space (numer unit?) or is it just an number?
			int ixSpace = resTmp.indexOf(' ');
			
			//has space?
			if(ixSpace != -1){
				resTmp = resTmp.substring(0,ixSpace);
				res.setB(Double.parseDouble(resTmp));
			}else{
				res.setB(Double.parseDouble(resTmp));
			}
			elemCounter++;
			
			
			//get the first number
			res.setUnit(csElems.nextToken().trim());
			
			
			
			
			
			elemCounter++;
			
			
			
			
			String rangeToken =  null;
			boolean errorOccured = false;
			try{
				//compute range
				if(csElems.hasMoreTokens()){
					rangeToken =  csElems.nextToken().trim();
					rangeToken = rangeToken.replaceAll("\"","");
					rangeToken = rangeToken.replaceAll(",","");
					String [] tmpSplit = rangeToken.split("\\s+");
					
					//tokens of form
					// min to max unit
					//[0] min
					//[1] to
					//[2] max
					//....
					//rangeToken =
					//String tokenMax = 
					res.setMax(Double.parseDouble(tmpSplit[2]));
				}else{
					//System.out.println("["+res.getSpn()+"]could not find data range feild");
					errorOccured = true;
				}
				
				
			}catch(Exception e){
				
				errorOccured=true;
				
			}
			
			if(errorOccured){
				double maxEncodedValue = J1939Parser.getEncodedValue(res.getOffset(),res.getLength(),ALL_ONES)-1;
				
				//attempt to compute the max manually
				if(!res.getUnit().equals("bit")){
					maxEncodedValue = J1939Parser.decodeSensorValue(res.getM(),res.getB(), maxEncodedValue);
				}
				
				//ignore
				res.setMax(maxEncodedValue);
				//System.out.println("["+res.getSpn()+ "]: failed to parse max from :"+rangeToken+", recomputed to: "+maxEncodedValue);
			}
	
			//compute opperational range
			if(csElems.hasMoreTokens()){
				MinMaxPair mmP = parseMinMax( csElems.nextToken().trim());
				//parse succesfully?
				if((mmP != null) && (mmP.min != null) && (mmP.max != null)){
					
					//make sure max>min, otherwise something is wrong
					if(mmP.max > mmP.min){
						res.setOppMax(mmP.max );
						res.setOppMin(mmP.min);
					}
				}
			}
			
		}catch(Exception e){
			//e.printStackTrace();
			//System.out.println("Malformed j1939 spec document line, near element "+elemCounter+" while parsing line: "+specDocLine);
			return null;
		}
		
		return res;
	}
	
	private static MinMaxPair parseMinMax(String token){
		try{
			
	
		
				//has operational range?
				if(token.length()>0){
					
					//possible formats of interest
					//-100 to 100%
					//-100 to 100 %
					//-100 to 100
					//-100 to +100
					//-100 to +100%
					//0 to 100
					//0.0 to 100
					//-100.0 to 100.0
					//-100.0% to 100.0%
					//-100.0 % to 100.0%
					//0 to 65,333

					token = token.replaceAll("\"","");
					
					token = token.replaceAll(",","");
					token = token.replaceAll("%","");
					token = token.replaceAll("\\+","");
					
					//split into two peices
					String [] opRangeTokens = token.split("to");
					
					//make sure we have 2 peices
					if(opRangeTokens.length >= 2){
						
						String minToken = opRangeTokens[0];
						
						String maxToken = opRangeTokens[1];

						//now tokens of form
						//-100.....
						//100....
						//100...
						//1000...
						//1000.0...
						//-10.0...
						//10.0...
						
						Double minOpRange = parseOperationRangeDouble(minToken);
						Double maxOpRange = parseOperationRangeDouble(maxToken);
						
						return new MinMaxPair(minOpRange,maxOpRange);
							
					}
				}
				
				
				
			return null;
			}catch(Exception e){
				return null;
			}
	}
	private static Double parseOperationRangeDouble(String s){
		try{
			
			String[] tokens = s.trim().split(" ");
			
			//numer should be first
			return Double.parseDouble(tokens[0]);
		}catch(Exception e){
			
			return null;
		}
	}
	private static int parseSPNLength(String s){
		
		if(s.length() == 0){
			return -1;
		}
		
		int len = -1;
		
		
		StringTokenizer csElems = new StringTokenizer(s," ");
		
		//get the first number
		len = Integer.parseInt(csElems.nextToken());
		
		if (s.contains("bit")){
			//do nothing, len is number of bits
		}else if (s.contains("byte")){
			//convert bytes to bits
			len = J1939_PACKET_DATA_LENGTH*len;
		}else{
			//unexpected format
			return -1;
		}
		return len;
	}
	/**
	 * Parses the J1939 SPN PGN position entry into an offset (bit start index).
	 * @param s The SPN PGN position entry string:  Of the following formats: digit, digit:digit, or digit-digit
	 * @return index of begining of sensor encoded value index, or -1 if an unknown format is provided.
	 */
	private static int parseSPNOffset(String s){
		
		try{
		//empty?
		if (s.length()==0){
			return -1;
		}
		
		//working with bytes positions?
		//single digit (byte)
		if (s.length() ==1){
			
			//the single digit defnies the byte (1-8) starting index
			int byteix = Integer.parseInt(s) -1 ;
			//bit index is 8 times index of 
			return byteix*J1939_PACKET_DATA_LENGTH;
			
		}else{//bits: 5.7 or 4-5 , 01-03, 19-23,...for example
			
			boolean isDash = false;
			boolean isDot = false;
			
			//look for index in string of the '.' or '-' splitter. it may be case there is none
			int splitSymboleIx = s.indexOf('-');
			if(splitSymboleIx == -1){
				splitSymboleIx = s.indexOf('.');
				if(splitSymboleIx != -1){
					isDot = true;
				}
			}else{
				isDash = true;
			}
			 
			String ix1Str = s.substring(0, splitSymboleIx).trim();
			String ix2Str = s.substring(splitSymboleIx+1, s.length()).trim();
			int ix1 = Integer.parseInt(ix1Str) -1 ;
			int ix2 = Integer.parseInt(ix2Str) -1 ;
			
			//bits or bytes?
			if(isDash){
				
				//bytes
				return ix1*J1939_PACKET_DATA_LENGTH;
			}else if(isDot){
				
				//bits
				return ix1*J1939_PACKET_DATA_LENGTH + ix2;
			}else{
				return -1; //unknown format
			}
			
			/*int lenInts = Math.floorDiv(s.length(),2); 
			String ix1Str = s.substring(0, lenInts);
			String ix2Str = s.substring(lenInts+1, s.length());
			int ix1 = Integer.parseInt(ix1Str) -1 ;
			int ix2 = Integer.parseInt(ix2Str) -1 ;
			
			//bits or bytes?
			if(s.charAt(lenInts+1) == '-'){
				
				//bytes
				return ix1*J1939_PACKET_DATA_LENGTH;
			}else if(s.charAt(lenInts+1) == '.'){
				
				//bits
				return ix1*J1939_PACKET_DATA_LENGTH + ix2;
			}else{
				return -1; //unknown format
			}
			*/
		}
		
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException("Failed to parse spn offset position from string: "+s);
		}
		

	}
	// line 33 "model.ump"
	/**
	 * Parses a string of the form: ellapsedtime ms (long),pgn,hex-byte0,hex-byte1,hex-byte2,...,hex-byte7
	 * example: 1234,65293,ff,ab,cd,ef,01,91,a9,19
	 * @param dumpOutputLine a string that is exapected to be formatted such as entry in j1939 dump file.
	 * @return Parse SensorReading object created from reading the data dump line.
	 */
	private static J1939Packet parseSensorReading(String dumpOutputLine){
		
		//used to count elements
		int elemCounter=0;
		
		J1939Packet res = new J1939Packet();
		try{
			StringTokenizer csElems = new StringTokenizer(dumpOutputLine,",");
			
			//1st time elapsed
			String timeStampStr = csElems.nextToken();
			
			res.setTimestamp(Long.parseLong(timeStampStr));
			
			//parse pgn
			res.setPgn(Integer.parseInt(csElems.nextToken().trim()));
			
			int bytesAdded = 0;
			//iterate each byte
			while(csElems.hasMoreTokens()){
				
				//parse hex string to byte
				byte b = (byte) Integer.parseInt(csElems.nextToken().trim(),16);
				res.addByte(b, bytesAdded);
				bytesAdded++;
				elemCounter++;
			}
			
				
		}catch(Exception e){
			//e.printStackTrace();
		//	System.out.println("Malformed j1939 data dump, near element "+elemCounter+": "+dumpOutputLine);
			return null;
		}
		
		return res;
	}
	
	/**
	 * Parse a 2-hex-character string into a byte 
	 * @param hexString The string with 2 hexadecimal characters
	 * @return byte representing value of the hexadecimal represented by the string. 
	 */
	private static byte hexToByte(String hexString) {
	    int firstDigit = toDigit(hexString.charAt(0));
	    int secondDigit = toDigit(hexString.charAt(1));
	    return (byte) ((firstDigit << 4) + secondDigit);
	}
	
	/**
	 * Converts a hexadecimal character to digit.
	 * @param hexChar The hexadecimal character.
	 * @return int representing the number represented by hex character.
	 */
	private static int toDigit(char hexChar) {
	    int digit = Character.digit(hexChar, 16);
	    if(digit == -1) {
	        throw new IllegalArgumentException(
	          "Invalid Hexadecimal Character: "+ hexChar);
	    }
	    return digit;
	}
	
	// line 34 "model.ump"
	/**
	 * Reads the j1939 data dump CSV file and parse it into raw j1939 packets.
	 * @param csvFile The file path the j1939 data dump.
	 * @return List of sensor readings
	 * @throws IOException Failed to read the file and parse each line into data readings
	 */
	private static List<J1939Packet> parseDumpFile(Path csvFile)throws IOException{
		
		long lines = 0;
		List<J1939Packet> res = new ArrayList<J1939Packet>(64*256*1024);
	    try(BufferedReader reader = Files.newBufferedReader(csvFile, Charset.forName("UTF-8"))){

	      
	      String currentLine = null;
	      //skip first few lines
	      currentLine = reader.readLine();
	      currentLine = reader.readLine();
	      while((currentLine = reader.readLine()) != null){//while there is content on the current line
	    	  J1939Packet sr = parseSensorReading(currentLine);
	    	  if(sr!=null){
	    		  res.add(sr);
	    	  }else{
	    		 // System.out.println("Error near line: "+lines);
	    	  }
	    	  lines++;
	      }
	    }catch(Exception ex){
	      ex.printStackTrace(); //handle an exception here
	      throw new IOException("Problem reading J1939 dumpe file: "+csvFile.toString()+". Error near line: "+lines);
	    }
	    
	    return res;
	    
	}
	// line 37 "model.ump"
	/**
	 * Initializes this object, getting it ready to analyze the data.
	 * @param j1939SpecDocFile The J1939 Specification Document .csv file path, containing all sensor definitions and other metadata.
	 * @param j1939DataDumpFile The J1939 data dump csv file, expected to have a first line describing the data, and 2nd line providing csv headers.
	 * @throws IOException
	 */
	public void init(Path j1939SpecDocFile, Path j1939DataDumpFile) throws IOException
	{
		Logger log = LoggerFactory.getInstance();
		this.sensorDefinitions = parseSpecDoc(j1939SpecDocFile);
		log.log_debug("finished parsing j1939 specifiction document: "+j1939SpecDocFile);
		this.readings = parseDumpFile(j1939DataDumpFile);
		log.log_debug("finished parsing j1939 dump file: "+j1939DataDumpFile);
		
	}
	
	// line 38 "model.ump"
	/**
	 * Analyzes the j1939 data dump, providing a hook for user-defined sensor value manipulation. 
	 * @param consummer The userdefined object that will manipulate each sensor reading
	 */
	public void analyzeData(J1939Reader consummer)
	{
		//iterate all the packets (each packet has one pgn)
		for(J1939Packet r : this.readings){
			
			Set<Integer> spns = this.sensorDefinitions.getAllSPN(r.getPgn());
			
			if(spns == null){
				failedSensorDefinitions.put(r.getPgn(),null);
				continue;
			}
			
			Iterator<Integer> it = spns.iterator();
			
			
			//iterate all potential sensor readings (some may be empty) in the packet (each reading has an spn)
			while(it.hasNext()){
				
				int spn = it.next();
				
				//find meta info on packet
				SensorDescription sd = this.sensorDefinitions.getSensorDescription(r.getPgn(),spn);
				
				//0xfff...fff
				double sensorValue = ~(0x0);
				//0xfff...ff
				long encodedSensorValue = ~(0x0);
				
				//raw bit/state units?
				if(sd.getUnit().equals("bit")){
					//no need to decode value, it is already decoded in packet, raw binary value
					sensorValue	= encodedSensorValue= getEncodedValue(sd.getOffset(),sd.getLength(),r.getBytes());
				}else{
					encodedSensorValue = getEncodedValue(sd.getOffset(),sd.getLength(),r.getBytes());
				//	decode
					sensorValue = decodeSensorValue(sd.getM(),sd.getB(),encodedSensorValue);
				}
				
				//not null sensor value, ie not (0xfff...ff) ? ignore null readings (sensor doesn't exist in)
			//	if(encodedSensorValue!=(~0x00)){
				if(!isReadingNull(encodedSensorValue,sd.getOffset(),sd.getLength())){
				//	let the interface handle the sensor value
					consummer.readSensorValue(r.getTimestamp(),r.getPgn(), spn,sensorValue,sd);
				}
			}//end iterate SPNs in a packet
		}//end iterate packets
		
	}


}