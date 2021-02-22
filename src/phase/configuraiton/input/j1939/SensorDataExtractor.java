package phase.configuraiton.input.j1939;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SensorDataExtractor implements J1939Reader {

	private int pgn;
	private int spn;
	private int bufferSize;

	FileWriter fr;
	BufferedWriter br;
	PrintWriter pr;
	StringBuilder strBuffer;
	int flushSize = 4096;
	public SensorDataExtractor(String outFilePath,int pgn, int spn) throws IOException {
		System.out.println("outputing decoded values of pgn("+pgn+") and spn("+spn+") to: "+outFilePath);
		this.spn = spn;
		this.pgn = pgn;
		//this.readings = new ArrayList<Double>(4096*8);
		//this.decodedValues = new ArrayList<String>(4096*8);
		File outFile = new File(outFilePath);		
		
		fr = new FileWriter(outFile, true);
		br = new BufferedWriter(fr);
		pr = new PrintWriter(br);
		pr.println("timestamp,pgn,spn,decodedValue");
		strBuffer = new StringBuilder();
		bufferSize = 0;
	}
	
	
	public void close(){
		
		try {
			pr.println(strBuffer.toString());
			strBuffer.delete(0,strBuffer.length());
			pr.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns true if a provided parameter group number should be filtered, and false if it shouldn't be filtered.
	 * @param pgn Parameter group number to check whether to be filtered or not.
	 * @return True when it should be filtered, false otherwise.
	 */
	private boolean isPGNFiltered(int pgn){
		if(this.pgn == -1){
			return false;
		}else{
			return this.pgn!=pgn;
		}
	}
	
	
	/**
	 * Returns true if a provided suspect number should be filtered, and false if it shouldn't be filtered.
	 * @param spn Suspect parameter number to check whether to be filtered or not.
	 * @return True when it should be filtered, false otherwise.
	 */
	private boolean isSPNFiltered(int spn){
		if(this.spn == -1){
			return false;
		}else{
			return this.spn!=spn;
		}
	}
	
	private boolean isFiltered(int pgn, int spn){
		
		//should we filter the pgn?
		if(isPGNFiltered(pgn)){
			
			return true; //filter this pgn-spn pair
			
		}
		
		return isSPNFiltered(spn);
	}
	@Override
	public void readSensorValue(long timeStamp,int pgn, int spn, double decodedValue, SensorDescription sd) {
		
	/*	if(spn == 174 && pgn ==65262){
			System.out.println("found!");
		}*/
		
		//ignored filtered pgn spn
		if(isFiltered(pgn,spn)){
			return;
		}
	
		//filter sensors that are out of operational range (most likely noise)
		if(!sd.inOperationalRange(decodedValue)){
			return;
		}
		bufferSize ++;
		//System.out.print("*");
		//time to flush to file?
		if(bufferSize >flushSize){
			
			bufferSize = 0;
			pr.println(strBuffer.toString());
			strBuffer.delete(0,strBuffer.length());
			
		}else{//buffer the readigns
			strBuffer.append(timeStamp);
			strBuffer.append(',');
			strBuffer.append(pgn);
			strBuffer.append(',');
			strBuffer.append(spn);
			strBuffer.append(',');
			strBuffer.append(decodedValue);
			strBuffer.append('\n');
		}
		//decodedValues.add(out);

	}
	
}
