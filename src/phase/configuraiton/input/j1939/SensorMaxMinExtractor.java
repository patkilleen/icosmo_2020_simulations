/*This product contains SAE International copyrighted intellectual property, 
which has been and is licensed with permission for use by Patrick Killeen, 
in this application only. No further sharing, duplicating, or transmitting is permitted.*/

package phase.configuraiton.input.j1939;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SensorMaxMinExtractor{


	private int bufferSize;

	FileWriter fr;
	BufferedWriter br;
	PrintWriter pr;
	StringBuilder strBuffer;
	int flushSize = 4096;
	public SensorMaxMinExtractor(String outFilePath) throws IOException {
		System.out.println("outputing mins and maxes of sensors to: "+outFilePath);

		//this.readings = new ArrayList<Double>(4096*8);
		//this.decodedValues = new ArrayList<String>(4096*8);
		File outFile = new File(outFilePath);		
		
		fr = new FileWriter(outFile, true);
		br = new BufferedWriter(fr);
		pr = new PrintWriter(br);
		pr.println("pgn,spn,opmin,opmax,min,max");
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
	

	public void readMinMax(int pgn, int spn,SensorDescription sd) {
		
	/*	if(spn == 174 && pgn ==65262){
			System.out.println("found!");
		}*/
		
		//ignored filtered pgn spn

		bufferSize ++;
		//System.out.print("*");
		//time to flush to file?
		if(bufferSize >flushSize){
			
			bufferSize = 0;
			pr.println(strBuffer.toString());
			strBuffer.delete(0,strBuffer.length());
			
		}else{//buffer the readigns
			strBuffer.append(pgn);
			strBuffer.append(',');
			strBuffer.append(spn);
			strBuffer.append(',');
			strBuffer.append(sd.getOppMin());
			strBuffer.append(',');
			strBuffer.append(sd.getOppMax());
			strBuffer.append(',');
			strBuffer.append(sd.getMin());
			strBuffer.append(',');
			Double max = sd.getMax();
			strBuffer.append(max==null?"null":max);
			strBuffer.append('\n');
		}
		//decodedValues.add(out);

	}
	
}
