/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/
package common.event.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.Util;
import common.event.Event;
import common.exception.ConfigurationException;

// line 42 "model.ump"
// line 125 "model.ump"
/**
 * 
 * @author Not admin
 *
 * @param <E> The type of object that represents an event.
 * @param <K> The type of object used to seperate streams by id, partition key.
 */
public class EventStream <E extends Event, K>
{

	public final static int DEFAULT_EVENT_BUFFER_SIZE = 1024*1024;
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//EventStream Associations
	private HashMap<K,List<E>> events;

	private List<K> partitionKeys;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public EventStream(List<K> partitionKeys)
	{
		setParitionKeys(partitionKeys);
		init();
	}

	/**
	 * Constructor to be used by subclasses that want more control over partition key configuration.
	 */
	protected EventStream(){
		
	}
	/**
	 * Initializes the event map.
	 */
	protected void init(){
		
		events = new HashMap<K,List<E>>(partitionKeys.size());
		
		//iterate all keys and create hashmap entry
		for(K key: partitionKeys){
			List<E> emptyList = new ArrayList<E>(DEFAULT_EVENT_BUFFER_SIZE);
			events.put(key,emptyList);
		}
	}
	
	protected void setParitionKeys(List<K> keys){
		if(keys == null || keys.size() == 0){
			throw new ConfigurationException("failed to create event stream, null partition stream.");
		}
		
		//make sure keys are unique
		if(!Util.allElementsUnique(keys)){
			throw new ConfigurationException("failed to create event stream, duplicate keys");
		}
		this.partitionKeys = keys;
	}
	
	protected List<K> getParitionKeys(){
		return this.partitionKeys;
	}
	
	protected void setEvents(HashMap<K,List<E>> newEventMap){
		
		this.events = newEventMap;
		
	}
	/**
	 * Retrieves the raw list of events associated to a partition key.
	 * @param t The object type used to fetch the list of events for.
	 * @return List of events associated to t.
	 */
	protected List<E> getEvents(K k){
		return events.get(k);
	}
	
	protected HashMap<K,List<E>> getEventMap(){
		return this.events;
	}
	/**
	 * Adds events to list of events.
	 * @param t The object type to add the event to.
	 * @param event Event to add.
	 */
	protected void addEvent(K k, E event){
		List<E> eventList = events.get(k);
		if(eventList == null){
			throw new ConfigurationException("failed to add event using key "+k+", since parition key doesn't exist");
		}
		eventList.add(event);
	}
	
	
	/**
	 * Removes all events from each vehicle's event list..
	 */
	public void delete()
	{
		//clear all events by creating new map
		init();

	}

}