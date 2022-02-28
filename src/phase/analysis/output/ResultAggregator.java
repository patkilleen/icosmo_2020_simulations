package phase.analysis.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ResultAggregator {

	
	public ResultAggregator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String [] args) throws IOException {
		if(args.length <= 1) {
			System.out.println("<ResultAggregator Usage>: -ag directory_to_search_in result_file_name outputAggPath");
			System.out.println("<ResultAggregator Usage>: -o sim_output_dir copyDestDirPath binSize outputDirSuffix");
			System.exit(0);
		}
		
		String option = args[0];
		if(option.equals("-ag")) {
			
			if(args.length != 4) {
				System.out.println("<ResultAggregator Usage>: -ag directory_to_search_in result_file_name outputAggPath");
				System.exit(0);
			}
			System.out.println("Searching in "+args[1]+" for sub directories with result aggregation files");
			aggregateResultFiles(args[1],args[2],args[3]);
			System.out.println("output aggregate of all result files in: "+args[3]);
		}else if(option.equals("-o")) {
			
			if(args.length != 5) {
				System.out.println("<ResultAggregator Usage>: -o sim_output_dir copyDestDirPath binSize outputDirSuffix");
				System.exit(0);
			}
			System.out.println("Organizing result files from in "+args[1]+" into bins of size "+args[3]+" in destination folder: "+args[2]);
			organizeResultFiles(args[1],args[2],Integer.parseInt(args[3]),args[4]);
			System.out.println("finished");
		}else {
			System.out.println("unexpected option: "+args[0]);
		}
		
		
		
	}
	
	
	/**
	 * Copies all the output files from a directory to destination directory and
	 * organizes the files into folders by experimental results batches.
	 * @param dirSearchPath The directory path (typically raw output folder) that has all the experiment output directories
	 * @param dstDirPath directory path to where the organiziation will take place (where folders will be created)
	 * @param batchSize Number of experimental output files per batch (typically the number of iterations for one experiment)
	 * @param outpuDirSuffix String to paste at end of created directory names
	 * @throws IOException
	 */
	public static void organizeResultFiles(String dirSearchPath,String dstDirPath,int batchSize, String outpuDirSuffix) throws IOException {
	
		File searchDir = new File(dirSearchPath);
		
		if(batchSize < 1) {
			throw new IllegalArgumentException("cannot have bins smaller than 1 file");
		}
		
		//empty and null string are treated the same
		if(outpuDirSuffix == null) {
			outpuDirSuffix = "";
		}
		
		
		//e.g.: bin 2, suffix '-sensor' = 2-sensor
		String outputBinDirName =null;
		
		//e.g.: dstDirPath = '/home/usr2/icosmo', outputBinDirName = '3-sensors'
		//      = /home/usr2/icosmo/3-sensors'
		String outputBinDirPath =null;

		
		
		int binIx = 0;
		int binSize = batchSize;
		//iterate over each  experimental result directory in the search directory
		File[] files = searchDir.listFiles();

		for (int fileCount = 0; fileCount < files.length; fileCount++) {

						
			File f = files[fileCount];
			
			
			
			//we only consider directories
			if(f.isDirectory()) {
				
				
				
				//filled the bin (or first time running?)
				if(binSize ==batchSize) {
					binIx++;
					binSize=1;
					
					//create bin
					//e.g.: bin 2, suffix '-sensor' = 2-sensor
					outputBinDirName =binIx+outpuDirSuffix;
					
					//e.g.: dstDirPath = '/home/usr2/icosmo', outputBinDirName = '3-sensors'
					//      = /home/usr2/icosmo/3-sensors'
					outputBinDirPath = Paths.get(dstDirPath,outputBinDirName).toString();
					
					System.out.println("create bin/batch folder: "+outputBinDirPath);
					//create bin to store some of result directories
					FileHandler.mkdir(outputBinDirPath);
					
				}else {
					binSize++;
				}
				
				//Paths.get(rootDirPath,relativePath).toString();

				String fName = f.getName();
				
				//the path to where we want to copy
				Path dstCopy =  Paths.get(outputBinDirPath,fName);
				
				//copy over the directory to destination
				FileHandler.copyFolder(f, dstCopy.toFile());
				
			}//end if directory
			
			
		}
		
		
	}
	/**
	 * For each row, aggregates summary statics of many result files into one result file.
	 * @param resFiles
	 * @return
	 * @throws IOException 
	 */
	public static void aggregateResultFiles(String dirSearch,String resulFileName, String outputPath) throws IOException {
		
		List<AUCResultFile> resFiles = findAllResultsFiles( dirSearch, resulFileName); 
				
		if(resFiles.isEmpty()) {
			return;
		}
		
        //now we 
        FileHandler.createFile(outputPath);
		
        String csvHeader = "algorithm id,";
        
        //POPULAT the column names as the name of result file
        for(AUCResultFile rf : resFiles) {
        	File f = new File(rf.filePath);
        	File dir = f.getParentFile();
        	String colName = dir.getName();
        	
        	csvHeader+=colName+",";
        }
        
        csvHeader+="mean,median,max,min,st.dev\n";
        
		FileHandler.append(outputPath,csvHeader.getBytes());
		//all the result files should have the same algorithm ids, so use first file as reference
		List<Integer> algIds = resFiles.get(0).algIds;
		
		
		List<Double> aucs = new ArrayList<Double>(32);
		
		
		//iterate over each algorithm (row) to compute the aggregate stats of that row
		for(int algIdIx  = 0; algIdIx < algIds.size();algIdIx++) {
			
			double max= Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			
			aucs.clear();
			
			String row = "";
			int algId = algIds.get(algIdIx);
			row += algId+",";
			
			//iterate over each results file (column) to create the aggregated file
			for(int resFileIx = 0;resFileIx < resFiles.size();resFileIx++) {
				AUCResultFile col = resFiles.get(resFileIx);
				double auc = col.getAUC(algIdIx);
				
				//STORE all auc of the row in a list
				aucs.add(auc);
				
				
				row += auc+",";
				
				//found new maximu AUC?
				if(max < auc) {
					max = auc;
				}
				//found new minimum AUC?
				if(min > auc) {
					min = auc;
				}
				
			}//end fill row of auc
			
			double mean=0.0;
			double stdv=0.0;
			double median=0.0;
			double sum = 0.0;
			//compute the mean, median, and standard deviation
		   
	        int length = aucs.size();

	        for(double num : aucs) {
	            sum += num;
	        }

	        mean = sum/length;

	        for(double num: aucs) {
	        	stdv += Math.pow(num - mean, 2);
	        }

	        stdv= Math.sqrt(stdv/length);
	        
	        Collections.sort(aucs);
	      
	        //compute median
	      
	        // check if total number of scores is even
	        if (length % 2 == 0) {
	           double sumOfMiddleElements = aucs.get((int)(length / 2.0)) +
	        		   aucs.get((int)(length / 2.0 - 1));
	           // calculate average of middle elements
	           median = sumOfMiddleElements / 2.0;
	        } else {
	           // get the middle element
	           median = aucs.get((int)(length / 2.0));
	        }
	        
	        

	        row+=mean+","+median+","+max+","+min+","+stdv;
	        
	        //add the new line until reach last row
	        if(algIdIx +1 < algIds.size()) {
	        	row += "\n";
	        }
	        
			
			FileHandler.append(outputPath, row.getBytes());
				
			
			
		
		}
		
		
	}
	
	public static double calculateSD(List<Double>numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.size();

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
	
	/**
	 * Looks in a directory for directories that may contain the result file and loads them.
	 * @param dirPath Directory path to search for directories that have the result file
	 * @param resulFileName Result file name to look for
	 * @return List of result files found
	 * @throws IOException Failed to read result file or list files in directory
	 */
	public static List<AUCResultFile> findAllResultsFiles(String dirPath, String resulFileName) throws IOException{
		File dir = new File(dirPath);
		List<AUCResultFile> res = new ArrayList<AUCResultFile>(16);
		
		//iterate over each search directory in the directory
		File[] files = dir.listFiles();
		
		for (int i = 0; i < files.length; i++) {

						
			File f = files[i];
			
			
			//we only consider directories
			if(f.isDirectory()) {
				
				//look for the result file inside the sub directory
				AUCResultFile resFile = findResultsFile(f.getAbsolutePath(),resulFileName);
				
				//result file loaded?
				if(resFile != null) {
					res.add(resFile);
				}
			}
		}
		
		return res;
			
	}
	
	/**
	 * Looks for a result file in a directory and loads it into memory
	 * @param dirPath Directory path to search for file
	 * @param resulFileName the name of the file to search for
	 * @return The loaded result file or null if it couldn't be found
	 * @throws IOException Failed to read result file or list files in directory
	 */
	public static AUCResultFile findResultsFile(String dirPath, String resulFileName) throws IOException{
		File dir = new File(dirPath);
		AUCResultFile res = null;
		
		//iterate over each file in the directory
		File[] files = dir.listFiles();
		
		for (int i = 0; i < files.length; i++) {

						
			File f = files[i];
			
			String fname = f.getName();
			
			//found the file?
			if(fname.equals(resulFileName)) {
				res = new AUCResultFile(f.getAbsolutePath());
				res.load();
				break;
			}
			
		}
		
		return res;
			
	}
		
	protected static class AUCResultFile{
		String filePath;
		List<Integer> algIds;
		List<Double>  aucs;
		
		public AUCResultFile(String filePath) {
			this.filePath = filePath;
			algIds = new ArrayList<Integer>(32);
			aucs = new ArrayList<Double>(32);
		}
		
		public AUCResultFile(String filePath,	List<Integer> algIds, List<Double>  aucs) {
			this.filePath = filePath;
			this.algIds = algIds ;
			this.aucs =aucs;
		}
		
		
		public double getAUC(int i) {
			return aucs.get(i);
		}
		//reads the file and loads its content into this object
		/* format:
		 * header:  "algorithm_id","auc"
			row:    "0",0.590923374277944
		 */
		public void load() throws IOException {
			 boolean firstLine = true;
			 
			 int rowId = 0;
			 //read the file line by line
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	
			    	//we skip the first line, as it only has header column names
			    	if(! firstLine) {
			    		
			    		//split the line in 2 (it's a 2-column CSV)
			    		String [] tokens = line.split(",");
			    		String algIdStr = tokens[0];
			    		String aucStr = tokens[1];
			    		int algId;
			    		
			    		try {
			    		//	parse the line into nubmers
			    			
			    			//remove any quotes
			    			algIdStr = algIdStr.replace("\"","");
			    			
			    			algId = Integer.parseInt(algIdStr);
			    		}catch(NumberFormatException e) {
			    			String errMsg = "Failed to parse algorithm id for line "+rowId+": '"+line+"'("+e.getMessage()+")";
			    			
			    			throw new NumberFormatException(errMsg);
			    		}
			    		
			    		double auc;
			    		
			    		try {
				    		
			    			auc = Double.parseDouble(aucStr);
				    	}catch(NumberFormatException e) {
				    		String errMsg = "Failed to parse the AUC for line "+rowId+": '"+line+"'("+e.getMessage()+")";
				    			
				    		throw new NumberFormatException(errMsg);
				    	}
				    		
			    		
			    		
			    		
			    		//store the numbers
			    		algIds.add(algId);
			    		aucs.add(auc);
			    		
			    	}
			    	
			    	firstLine = false;
			       // 
			    }//end iterate each line
			}
			
		}//end load

	}//end inner auc result class
}
