package common.event;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;

import phase.generation.history.FaultHistory;
import phase.generation.history.RepairHistory;
import phase.generation.history.SensorStatusHistory;

public class HistoryEvent extends Event implements Serializable{
		//histories
		private SensorStatusHistory sensorStatusHistory;
		private FaultHistory faultHistory;
		private RepairHistory repairHistory;
		public HistoryEvent(SensorStatusHistory sensorStatusHistory, FaultHistory faultHistory,
				RepairHistory repairHistory) {
			super();
			this.sensorStatusHistory = sensorStatusHistory;
			this.faultHistory = faultHistory;
			this.repairHistory = repairHistory;
		}
		public SensorStatusHistory getSensorStatusHistory() {
			return sensorStatusHistory;
		}
		public void setSensorStatusHistory(SensorStatusHistory sensorStatusHistory) {
			this.sensorStatusHistory = sensorStatusHistory;
		}
		public FaultHistory getFaultHistory() {
			return faultHistory;
		}
		public void setFaultHistory(FaultHistory faultHistory) {
			this.faultHistory = faultHistory;
		}
		public RepairHistory getRepairHistory() {
			return repairHistory;
		}
		public void setRepairHistory(RepairHistory repairHistory) {
			this.repairHistory = repairHistory;
		}
	
		/**
		 * Serializes the hisotry event found in the history input stream and saves it to a file 
		 * @param outputDir The directory to save the history event.
		 * @throws IOException
		 */
		public static void writeHistoryEvent(String outputFile, HistoryEvent histEvent) throws IOException{
			
			 FileOutputStream fileOut =
			         new FileOutputStream(outputFile);
			 
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
			 out.writeObject(histEvent);
	         out.close();
	         fileOut.close();
		}
		
		 
		public static HistoryEvent readHistoryEvent(String inputFile) throws IOException, ClassNotFoundException{
				HistoryEvent res = null;  
				FileInputStream fileIn = new FileInputStream(inputFile);
			      ObjectInputStream in = new ObjectInputStream(fileIn);
			      res = (HistoryEvent) in.readObject();
		         in.close();
		         fileIn.close();
		         
		         return res;
		}

}
