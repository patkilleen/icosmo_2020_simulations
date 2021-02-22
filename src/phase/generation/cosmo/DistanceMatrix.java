package phase.generation.cosmo;

import java.util.ArrayList;
import java.util.List;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import common.Vehicle;
import common.exception.ConfigurationException;
import common.exception.SimulationException;

public class DistanceMatrix {

	public final static int DEFAULT_NUMBER_ROWS = 128;
	private List<Row> rows;
	
	public DistanceMatrix() {
		rows = new ArrayList<Row>(DEFAULT_NUMBER_ROWS);
	}
	
	public DistanceMatrix(int numRows){
		
		if(numRows < 0){
			throw new ConfigurationException("cannot create distance matrix due to negative number of rows.");
		}
		rows = new ArrayList<Row>(numRows);
	}
	/**
	 * REturns the number of columns and rows in this matrix. (it is symetrical).
	 * @return the numer of rows and columns
	 */
	public int numberoOfRows(){
		return rows.size();
	}
	
	public int numberOfColumns(){
		
		if(rows.isEmpty()){
			return -1;
		}
		
		Row r = rows.get(0);
		return r.distances.size();
	}
	
	/**
	 * Returns the distance at cell i,j
	 * @param i index of row
	 * @param j index of column
	 * @return distance at the cell i,j
	 */
	public double getDistance(int i, int j){
		if(i < 0 || j < 0 || i >= numberoOfRows() || j >= numberoOfRows()){
			throw new ConfigurationException("index out of bounds, cannot get distance.");
		}
		
		Row row = rows.get(i);
		
		return row.distances.get(j);
	}
	
	/**
	 * Returns the distance of the cell specified
	 * @param rowId row id of desired cell distance
	 * @param colId column id of desired cell distance
	 * @return distance
	 */
	public double getDistance(Object rowId, Object colId){
		if(rowId == null || colId == null){
			throw new ConfigurationException("cannot get distance, null row ids");
		}
		
		int rowI = -1;
		int colJ = -1;
		
		//find row and column index of the cell intersecting row and column 
		for(int i = 0; i < numberoOfRows(); i++){
			
			Row row = rows.get(i);
			
			//skip undesired rows
			if(row.rowId != rowId){
				continue;
			}
			
			//iterate columsn (remember its a symmetric matrix so col == row)
			for(int j = 0; j < numberoOfRows(); j++){

				Row col = rows.get(j);
				
				//skip undesired rows
				if(col.rowId != colId){
					continue;
				}else{
					rowI = i;
					colJ = j;
					break;
				}
			}
		}
		
		if(rowI == -1 || colJ == -1){
			throw new SimulationException("could not get distance since row and/or column ids not found.");
		}
		return getDistance(rowI,colJ);
	}
	
	/**
	 * Returns a distance matrix with all row and columns associated to a vehicle removed.
	 * @param v Vehicle to filter from matrix
	 * @return filtered matrix, with vehicle row/columns removed
	 */
	protected DistanceMatrix filterVehicle(Vehicle v){
		if(v == null){
			throw new ConfigurationException("failed to filter matrix by vehicle, null argument");
		}
		
		DistanceMatrix newMatrix = new DistanceMatrix(this.numberoOfRows());
		
		boolean vehicleFoundInMatrix = false;
		
		
		//iterate all rows of matrix 
		for(int i = 0;i < this.numberoOfRows();i++){
			
			Row row = this.rows.get(i);
			
			Object rowIdCopy = row.rowId;
			Vehicle vehicleCopy = row.vehicle;
			//filtered vehicle distances?
			if(vehicleCopy.equals(v)){
				vehicleFoundInMatrix = true;
				continue;//skip this row
			}
				
			//the distances to add to result, which exlucded ddistances related to filtered vehicle
			DoubleArrayList newDistances = new DoubleArrayList(row.distances.size());
			
			//now iterate the cells of the row, ignoring cells accosiated filtered vehicle
			for(int j = 0;j < this.numberoOfRows();j++){
				Row col = this.rows.get(j);
				
				//not a filtered vehicle distance?
				if(!col.vehicle.equals(v)){
					
					double newDistance = row.distances.get(j);
					newDistances.add(newDistance);
				}
			}
			
			newMatrix.rows.add(new Row(rowIdCopy,vehicleCopy,newDistances));
			
			
		}
		/*
		//add all rows but filtered ones to result
		for(Row row : rows){
			
			//filter out desired vehicle
			if(!row.vehicle.equals(v)){
				
				Vehicle vehicleCopy = new Vehicle(row.vehicle.getVid());
				DoubleArrayList distCopy = row.distances.copy();
				Object rowIdCopy = row.rowId;
				
				
				newMatrix.rows.add(new Row(rowIdCopy,vehicleCopy,distCopy));	
			}else{
				vehicleFoundInMatrix = true;
			}
		}*/
		
		//didn't find vehicle?
		if(!vehicleFoundInMatrix){
			throw new ConfigurationException("cannot filter vehicle from matrix that doesn't contain any rows associated to vehicle.");
		}
		return newMatrix;
	}
	
	
	/**
	 * Adds a row of distances to the matrix
	 * @param rowId object used for referencing the rows more accuratly
	 * @param v vehicle of the distances
	 * @param distances vector of distances that should be of equal rank as other rows (number cells);
	 */
	public void addRow(Object rowId, Vehicle v , double [] distances){
		
		if(rowId == null || v == null || distances == null || distances.length == 0){
			throw new ConfigurationException("failed to add row, null or empty argument.");
		}
		//1st row added?
		/*if(!rows.isEmpty()){
			
			Row firstRow = rows.get(0);
			//make sure distances same rank as other rows
			if(firstRow.distances.size() != distances.length){
				throw new SimulationException("cannot add row that is different length as other rows.");
			}
		}*/
		 
		/*Row toAdd = this.getNextEmptyRow();
		
		//check to see if already allocated
		if(toAdd != null){
			toAdd.rowId=rowId;
			toAdd.isEmpty=false;
			toAdd.vehicle=v;
			DoubleArrayList rowDistances = toAdd.distances;
			for(double d : distances){
				rowDistances.add(d);
			}
		}else{//not allocated, create a new one
			toAdd = new Row(rowId,v,new DoubleArrayList(distances));
			rows.add(toAdd);	
		}*/
		
		
		DoubleArrayList rowDistances = this.addRow(rowId, v, distances.length);
		for(double d : distances){
			rowDistances.add(d);
		}
		
	}
	
	/**
	 * Adds a row and returns the array list that can later be populated by caller
	 * @param rowId id of row
	 * @param v vehicle 
	 * @param rowSize number of cells in row
	 * @return the list representing the distances row
	 */
	public  DoubleArrayList addRow(Object rowId, Vehicle v,int rowSize){
		
		if(rowId == null || v == null ){
			throw new ConfigurationException("failed to add row, null or empty argument.");
		}
		//1st row added?
		/*if(!rows.isEmpty()){
			
			Row firstRow = rows.get(0);
			//make sure distances same rank as other rows
			if(firstRow.distances.size() != distances.length){
				throw new SimulationException("cannot add row that is different length as other rows.");
			}
		}*/
		 DoubleArrayList distances = null;
		
		Row toAdd = this.getNextEmptyRow();
		
		//check to see if already allocated
		if(toAdd != null){
			toAdd.rowId=rowId;
			toAdd.isEmpty=false;
			toAdd.vehicle=v;
			distances = toAdd.distances;
		}else{//not allocated, create a new one
			distances =new DoubleArrayList(rowSize);
			toAdd = new Row(rowId,v,distances);
			rows.add(toAdd);	
		}
		return distances;
	}
	
	public Row getNextEmptyRow(){
		
		Row nextEmptyRow = null;
		
		for(Row r : rows){
			if(r.isEmpty){
				nextEmptyRow=r;
				break;
			}
		}
		
		return nextEmptyRow;
		
	}
	/**
	 * Finds the row with the minimum distance sum.
	 * @return minimum row
	 */
	protected Row findMinimumRow(){
		
		if(numberoOfRows() == 0){
			throw new ConfigurationException("cannot find minimum row of empty matrix");
		}
		
		Row minRow = rows.get(0);
		DoubleArrayList distances = minRow.distances;
		double sum =   Descriptive.sum(distances);
		
		for(int i = 1;i<numberoOfRows();i++){
			Row row = rows.get(i);
			DoubleArrayList tmpDistances = row.distances;
			double tmpSum =   Descriptive.sum(tmpDistances);
			
			//found smaller row?
			if(tmpSum < sum){
				minRow = row;
				sum = tmpSum;
			}
				
		}
		
		return minRow;
	}
	
	/**
	 * Computes the zscore of a model, provided a row id representing the model's distance row is proved.
	 * 
	 * @param testModelRowId the id of the row representing the model's distances
	 * @return zvalue of test model
	 */
	public double mostCentralPattern(Object testModelRowId){
		
		if(testModelRowId == null){
			throw new ConfigurationException("failed to compute MCP, cause row id is null");
		}
		
		//find target row of test model
		Row targetModelRow = null;
		
		for(Row row: rows){
			//found?
			if(row.rowId == testModelRowId){
				targetModelRow = row;
				break;
			}
		}
		
		//didn't find?
		if(targetModelRow == null){
			throw new SimulationException("can't compute MCP of row that doesnt' exist in matrix. row not found");
		}
		
		Vehicle testVehicle = targetModelRow.vehicle;
		
		//remove distances related to test vehicle 
		DistanceMatrix targetMatrix = this.filterVehicle(testVehicle);
		
		//find most central model, the most "normal" model, ie the minimum distance row
		Row c = targetMatrix.findMinimumRow();
		
		double zvalue = 0;
		
		int counter = 0;
		
		//distance of target model from the most central model
		double targetModelDistance = this.getDistance(targetModelRow.rowId, c.rowId);
		
		//count the number of models that are further away from central model than the testmodel  (skip the central model itself)
		for(Row row : targetMatrix.rows){
			
			//skip most central model
			if(row == c){
				continue;
			}
			
			double  otherModelDistance = targetMatrix.getDistance(row.rowId, c.rowId);
			
			if(otherModelDistance > targetModelDistance){
				counter++;
			}
		}
		
		//-1 to size of targetMatrix/submatrix since the most central model/row isn't included in the distribution
		zvalue = ((double)counter/(double)(targetMatrix.numberoOfRows() -1));
		return zvalue;
	}
	
	public void clear() {
		for(Row row: rows){
			row.distances.clear();
			row.vehicle = null;
			row.rowId= null;
			row.isEmpty=true;
		}
		
	}
	
	protected static class Row{
		public Vehicle vehicle;
		public DoubleArrayList distances;
		public Object rowId;
		public boolean isEmpty;
		public Row(Object rowId,Vehicle vehicle, DoubleArrayList distances) {
			super();
			this.vehicle = vehicle;
			this.distances = distances;
			this.rowId = rowId;
			this.isEmpty = false;
		}
		
	}
	
}
