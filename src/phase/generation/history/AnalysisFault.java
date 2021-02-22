package phase.generation.history;

import java.io.Serializable;

import common.FaultDescription;
import common.exception.ConfigurationException;
import common.exception.SimulationException;

public class AnalysisFault implements Serializable{

	public static final int NO_REPAIR_TIME = -1;
	private int occurenceTime;
	private int repairedTime;
	private FaultDescription fd;

	/**
	 * Creates an analysis fault.
	 * @param occurenceTime The time fault occured.
	 * @param fd Fault description
	 */
	public AnalysisFault(int occurenceTime, FaultDescription fd ) {

		
		if(occurenceTime < 0 || fd == null){
			throw new ConfigurationException("couldn't create AnalysisFault, because of negative time argument or null pointer.");
		}
		
		repairedTime= NO_REPAIR_TIME;
		this.occurenceTime = occurenceTime;
		this.fd = fd;
	}
	
	/**
	 * Creates an analysis Fault
	 * @param occurenceTime Time fault occured
	 * @param repairedTime Time fault repaired
	 * @param fd Fault description
	 */
	public AnalysisFault(int occurenceTime, int repairedTime, FaultDescription fd) {
		this(occurenceTime,fd);
		
		//make sure repair time is atleast one day later (cannot occur on same day)
		if(repairedTime <= occurenceTime){
			throw new ConfigurationException("couldn't set repair time, because repair time argument occured before or same day as fault.");
		}
		
		this.repairedTime = repairedTime;
	
	}

	/**
	 * Returns true when this fault occured at a precises time (see {@code existsInTimeWindow} function, with 0-sized windows)
	 * @param time The time to check if the fault occured in.
	 * @return True when the fault occured at the given time, false otherwise.
	 */
	public boolean existsInTimeWindow(int time){
		

		return existsInTimeWindow(time,0,0);
	}
		
	/**
	 * Returns true if the fault occured during the provided time window. An occurence of a fault
	 * is from the time recorded until the time repaired, or if not repair then it is assumed the
	 * fault is ongoing and the occurence window is from its occurence time until the present (right time window away from time provided)
	 * @param time The time the window is based on.
	 * @param leftTimeWindow The amount of time in the past the fault can occur in.
	 * @param rightTimeWindow The amount of time in the future the fault can occur in.
	 * @return True when the fault occured inside the window, false otherwise.
	 */
	public boolean existsInTimeWindow(int time, int leftTimeWindow, int rightTimeWindow){

		if(time < 0 || leftTimeWindow < 0 || rightTimeWindow < 0){ 
			throw new ConfigurationException("couldn't check whether a fault existed within a time window, because of negative time argument.");
		}

		
		//all the analysis fault objects will have been created and repaired
		//by the time this function should be called.
		if(!hasBeenRepaired()){
			throw new SimulationException("A fault should be repaired before checking when it occured. Ilformed logic. Fault not repaired.");
		}
		
		
		//left extremity (minimum 0)
		int lx = time - leftTimeWindow;
		if(lx < 0){
			lx = 0;
		}

		//right extremity
		int rx = time + rightTimeWindow;
		if(rx < 0){
			rx = 0;
		}

	
		
		
		//check whether this fault is found within the time window
		//only two cases that the search window isn't partially touching period of time 
		//that the fault occured
		//when the search window is completly outidie, on left or right
			
			
		//CASE 1: fault occurs after the window provided is (ie window is before fault), left of fault window
		if(rx < this.getOccurenceTime() ){
			return false;
		}

		//CASE 2: the serach window is on right of fault window
		if(lx > this.getRepairedTime()){
			return false;
		}
		
		//all other configurations (by logic) infer the window serached is in fault time
		return true;

	

	}
	
	public FaultDescription getFaultDescrition(){
		return fd;
	}
	
	public int getOccurenceTime() {
		return occurenceTime;
	}

	public void setOccurenceTime(int occurenceTime) {
		if(occurenceTime < 0){
			throw new ConfigurationException("couldn't set occurence time, because of negative time argument");
		}
		this.occurenceTime = occurenceTime;
	}

	/**
	 * Returns the repair time or <code>NO_REPAIR_TIME</code> if it wasn't set. 
	 * @return The repair time.
	 */
	public int getRepairedTime() {
		return repairedTime;
	}

	public void setRepairedTime(int repairedTime) {
		//make sure repair time is atleast one day later (cannot occur on same day)
		if(repairedTime < occurenceTime){
			throw new ConfigurationException("couldn't set repair time, because repair time argument occured before day of fault.");
		}

		this.repairedTime = repairedTime;
	}

	public boolean hasBeenRepaired(){
		return this.getRepairedTime() != NO_REPAIR_TIME;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fd == null) ? 0 : fd.hashCode());
		result = prime * result + occurenceTime;
		result = prime * result + repairedTime;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AnalysisFault))
			return false;
		AnalysisFault other = (AnalysisFault) obj;
		if (fd == null) {
			if (other.fd != null)
				return false;
		} else if (!fd.equalsAllSensorBehavior(other.fd))
			return false;
		if (occurenceTime != other.occurenceTime)
			return false;
		if (repairedTime != other.repairedTime)
			return false;
		return true;
	}

	
	
}
