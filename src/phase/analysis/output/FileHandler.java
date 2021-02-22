package phase.analysis.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import common.exception.ConfigurationException;
import common.log.Logger;
import common.log.LoggerFactory;

public class FileHandler {

	private final static LinkOption [] EXISTS_NO_FOLLOW_ARG = new LinkOption[]{ LinkOption.NOFOLLOW_LINKS};
	public FileHandler() {
	}
	
	/**
	 * Copies a file to another location. Does not overwrite files.
	 * @param srcFilePath File path of source file to copy.
	 * @param destFilePath File path of the destination of source file to be copied to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file arleady exists, or something went wrong copying the file.
	 * @return the path to the target file 
	 */
	public static Path copy(Path srcFilePath, Path destFilePath) throws IOException{
		//null ptr?
		if(srcFilePath == null || destFilePath == null){
			throw new ConfigurationException("cannot copy file due to null path.");
		}

		//make sure the source file exists
		if(!Files.exists(srcFilePath,EXISTS_NO_FOLLOW_ARG)){
			throw new FileNotFoundException("cannot copy file, since the source file "+srcFilePath.toString()+" doesn't exist.");
		}
		
		//make sure destination file dosn't already exist to avoid overwritting an existing file
		//by design the shouldn't be a file where another file is being copied to
		//in this simulation, so this is a precaution measure.
		if(!Files.exists(destFilePath,EXISTS_NO_FOLLOW_ARG)){
			return Files.copy(srcFilePath, destFilePath);
		}else{
			throw new IOException("cannot copy file to "+destFilePath.toString()+", since destination already exists");
		}
	}
	
	/**
	 * Copies a file to another location. Does not overwrite files.
	 * @param srcPath File path of source file to copy.
	 * @param destPath File path of the destination of source file to be copied to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file arleady exists, or something went wrong copying the file.
	 * @return the path to the target file
	 */
	public static Path copy(String srcPath, String destPath) throws IOException{
		//null ptr?
				if(srcPath == null || destPath == null){
					throw new ConfigurationException("cannot copy file due to null path.");
				}
				
				Path srcFilePath = Paths.get(srcPath);
				Path destFilePath = Paths.get(destPath);;
				return copy(srcFilePath,destFilePath);
	}
	
	/**
	 * Creates a file at a specific path. 
	 * @param filePath The path to create a new file
	 * @throws IOException Thrown when the file already exists or otherwise fails.
	 * @return the file
	 */
	public static Path createFile(Path filePath) throws IOException{
		if(filePath == null){
			throw new ConfigurationException("cannot create file due to null path.");
		}

		//make sure the file doesn't already exist before creating it
		if(!Files.exists(filePath,EXISTS_NO_FOLLOW_ARG)){
			Path newFile = Files.createFile(filePath);
			
			//double check to make sure it was crated
			if(!Files.exists(newFile,EXISTS_NO_FOLLOW_ARG)){
				throw new IOException("failed to create file "+filePath);
			}
			
			return newFile;
		}else{
			throw new IOException("cannot create file "+filePath+", it already exist.");
		}
	}
	
	/**
	 * Creates a file at a specific path. 
	 * @param path The path to create a new file
	 * @throws IOException Thrown when the file already exists or otherwise fails.
	 * @return the file
	 */
	public static Path createFile(String path) throws IOException{
		if(path == null){
			throw new ConfigurationException("cannot create file due to null path.");
		}
		
		Path filePath = Paths.get(path);
		return createFile(filePath);
	}
	
	/**
	 * Appends bytes to an already existing file.
	 * @param filePath The file path to append to
	 * @param bytes Bytes to append to file.
	 * @throws IOException thrown when the source file doesn't exists or otherwise failed.
	 * @return the path
	 */
	public static Path append(Path filePath, byte [] bytes) throws IOException{
		if(filePath == null || bytes == null){
			throw new ConfigurationException("cannot append to file due to null path or null bytes.");
		}
		
		//make sure the file already exist before appending to it
		if(Files.exists(filePath,EXISTS_NO_FOLLOW_ARG)){
			return Files.write(filePath, bytes, StandardOpenOption.APPEND);
		}else{
			throw new IOException("cannot append to file "+filePath+", it doesn't exist.");
		}
	}

	
	/**
	 * Appends bytes to an already existing file.
	 * @param path The file path to append to
	 * @param bytes Bytes to append to file.
	 * @throws IOException thrown when the source file doesn't exists or otherwise failed.
	 * @return the path
	 */
	public static Path append(String path, byte [] bytes) throws IOException{
		if(path == null){
			throw new ConfigurationException("cannot append to file due to null path.");
		}
		
		Path filePath = Paths.get(path);
		return append(filePath,bytes);
	}
	
	/**
	 * Moves a file to another location. Does not overwrite files.
	 * @param srcFilePath File path of source file to move.
	 * @param destFilePath File path of the destination of source file to be moved to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file already exists, or otherwise failed.
	 * @return the path to the target file
	 */
	public static Path move(Path srcFilePath, Path destFilePath) throws IOException{
		//null ptr?
		if(srcFilePath == null || destFilePath == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}
		
		//make sure the source file exists
		if(!Files.exists(srcFilePath,EXISTS_NO_FOLLOW_ARG)){
			throw new FileNotFoundException("cannot move file, since the source file "+srcFilePath.toString()+" doesn't exist.");
		}
		
		//since the StandardCopyOption.REPLACE_EXISTING arg is ommited, it won't overwrite files
		return Files.move(srcFilePath, destFilePath);
	}
	
	/**
	 * Moves a file to another location. Does not overwrite files.
	 * @param srcPath File path of source file to move.
	 * @param destPath File path of the destination of source file to be moved to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file already exists, or otherwise failed.
	 * @return the path to the target file
	 */
	public static Path move(String srcPath, String destPath) throws IOException{
		//null ptr?
		if(srcPath == null || destPath == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}
		
		Path srcFilePath = Paths.get(srcPath);
		Path destFilePath = Paths.get(destPath);
		return move(srcFilePath,destFilePath);
	}
	
	/**
	 * Creates a new directory.
	 * @param filePath The path of new directory.
	 * @throws IOException Thrown when the directory already exists or creating the directory otherwise failed.
	 * @return the directory
	 */
	public static Path mkdir(Path filePath ) throws IOException{
		if(filePath == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}
		
		
		//make sure directory dosn't already exist before creating it
		if(!Files.exists(filePath,EXISTS_NO_FOLLOW_ARG)){
			return Files.createDirectory(filePath);
		}else{
			throw new IOException("cannot create directory "+filePath+", it already exists");
		}
	}
	
	/**
	 * Creates a new directory.
	 * @param path The path of new directory.
	 * @throws IOException Thrown when the directory already exists or creating the directory otherwise failed.
	 * @return the directory
	 */
	public static Path mkdir(String path) throws IOException{
		if(path == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}
		
		Path filePath = Paths.get(path);
		return mkdir(filePath);
	}
	
	/**
	 * Copies a folder to a destination
	 * @param src the source folder
	 * @param dest the destination folder
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {

    	if(src.isDirectory()){
    		
    		//if directory not exists, create it
    		if(!dest.exists()){
    		   dest.mkdir();
    		   
    		   //System.out.println("Directory copied from " 
                     //         + src + "  to " + dest);
    		}
    		
    		//list all the directory contents
    		String files[] = src.list();
    		
    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive copy
    		   copyFolder(srcFile,destFile);
    		}
    	   
    	}else{
    		//if file, then copy it
    		
    		//try to copy it
    		try{
    		//Use bytes stream to support all file types
    		InputStream in = new FileInputStream(src);
    	        OutputStream out = new FileOutputStream(dest); 
    	                     
    	        byte[] buffer = new byte[1024];
    	    
    	        int length;
    	        //copy the file content in bytes 
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
    	        }
 
    	        in.close();
    	        out.close();
    	        //System.out.println("File copied from " + src + " to " + dest);
    	        
    		}catch(IOException e){
    			//ignore it, we just faied to copy 1 file
    			Logger log = LoggerFactory.getInstance();
    			log.log_warning("failed to copy input file ("+src.getAbsolutePath()+") to ("+dest.getAbsolutePath()+"), due to: "+e.getMessage());
    		}
    	}
    }
}
