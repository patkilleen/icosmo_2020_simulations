package common;

import java.io.Serializable;

import common.exception.ConfigurationException;

public class Algorithm  implements Serializable{

	public final static int ICOSMO_ID_MOD = 64;
	private int id;
	private String name;
	public Algorithm(int id, String name) {
		if(id > ICOSMO_ID_MOD || id < 0){
			throw new ConfigurationException("failed to create algorithm, illegal id: "+id);
		}
		this.id = id;
		this.name = name;
	}
	
	public Algorithm(int id) {
		this(id,null);
	}
	
	/**
	 * empty constructor for sub class flexitibity
	 */
	protected Algorithm(){
		
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id without checking the id for validity.
	 * @param id
	 */
	protected void setId(int id) {
		if(id < 0){
			throw new ConfigurationException("failed to set id of algorithm, illegal id: "+id);
		}
		this.id = id;
	}
	
	public Algorithm toICOSMO(){
		Algorithm a =  new Algorithm();
		a.setId(this.getId()+ICOSMO_ID_MOD);
		a.setName(this.getName()+"-I");
		return a;
	}

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!( obj instanceof Algorithm))
			return false;
		Algorithm other = (Algorithm) obj;
		if (id != other.id)
			return false;
		return true;
	}
	  
}
