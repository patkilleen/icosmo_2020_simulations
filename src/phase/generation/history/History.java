package phase.generation.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import common.Util;
import common.event.TimerEvent;
import common.exception.ConfigurationException;
import common.exception.SimulationException;

/**
 * Stores elements by lookup key and time of occurence
 * @author Not admin
 *
 * @param <K> The key that will partition the history
 * @param <V> The type of elements to be stored
 */
public class History<K extends Serializable,V extends Serializable>  implements Serializable,HistoryValueComparator<V>{

	public static final int DEFAULT_ELEM_LIST_SIZE = 1024;
	public static final int DEFAULT_ALL_ELEM_LIST_SIZE = 1024*1024;
	public static final int DEFAULT_UNIQUE_ELEM_LIST_SIZE = 1024*1024;

	public static final int DEFAULT_TIME_EVENT_COUNT = 1024;
	

	private HashMap<K, HashMap<TimerEvent,List<V>>> historyMap;

	private List<K> partitionKeys;

	/**
	 * Constructor
	 * @param partitionKeys Keys used to partition the history for concurent access (1 thread per key)
	 */
	public History(List<K> partitionKeys) {
		if( partitionKeys == null || partitionKeys.isEmpty()){
			throw new ConfigurationException("Could not create history, null or empty parition key");
		}

		if(!Util.allElementsUnique(partitionKeys)){
			throw new ConfigurationException("Could not create history, duplicate parition keys");
		}
		historyMap = new  HashMap<K, HashMap<TimerEvent,List<V>>>(partitionKeys.size());

		//create all the hashmaps for each algorithm
		for(K k : partitionKeys){
			HashMap<TimerEvent,List<V>> innerMap = new HashMap<TimerEvent,List<V>>(DEFAULT_TIME_EVENT_COUNT);
			historyMap.put(k,innerMap);
		}

		this.partitionKeys = partitionKeys;


	}


	public List<K> getPartitionKeys() {
		return partitionKeys;
	}


	/**
	 * Checks to make sure each partition share an identical timerevent key set.
	 * Throws an exception when the assumption isn't met.
	 * @throws IllegalStateException Thrown when the history was filled improperly, ie a partition has a missmatch in timerevent keys.
	 * @throws ConfigurationExcetion Thrown when the history wasn't properly created.
	 */
	public void integrityCheck() throws SimulationException,ConfigurationException{

		if(partitionKeys.isEmpty()){
			throw new ConfigurationException("Failed integrity check. No partitions were configured");
		}

		K firstKey = partitionKeys.get(0);
		Set<TimerEvent> timerEvents = this.historyMap.get(firstKey).keySet();
		//compare the first set of timerEvent keys to the rest. If it equals them all, they all equal each other,
		//as desired
		for(K key : partitionKeys){
			//ignore 1st key
			if(key ==firstKey){
				continue;
			}
			Set<TimerEvent> otherTimerEvents = this.historyMap.get(key).keySet();

			//missmatch in timer events, ie the partitions have a skew in simulated time?
			if(!otherTimerEvents.equals(timerEvents)){
				throw new SimulationException("Failed integrity check. Simulated time missmatch.");
			}
		}

		//all is okay at this point, don't do anything

	}

	/**
	 * Returns all sets of TimeEvent keys.
	 * @return List of TimerEvent sets
	 */
	private List<Set<TimerEvent>> getAllTimeEventKeysSets(){

		List<Set<TimerEvent>> res = new ArrayList<Set<TimerEvent>>(partitionKeys.size());

		//iterate all partitions to fetch their timeEvent sets
		for(K key : partitionKeys){

			Set<TimerEvent> timerEvents = historyMap.get(key).keySet();

			res.add(timerEvents);
		}

		return res;
	}

	/**
	 * Checks to make sure each history share the same timervent key set.
	 * Throws an exception when the assumption isn't met.
	 * @throws IllegalStateException Thrown when the history was filled improperly, ie a partition has a missmatch in timerevent keys.
	 * @throws ConfigurationExcetion Thrown when the history wasn't properly created.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void integrityCheck(History other) throws SimulationException,ConfigurationException{

		if(other == null){
			throw new ConfigurationException("null history, cannot check integrity.");
		}
		//make sure both history have identical timerevent sets for each of their partitions 
		this.integrityCheck();
		other.integrityCheck();

		//don't need to check this, but this makes the code complete
		if(partitionKeys.isEmpty()){
			throw new ConfigurationException("Failed integrity check. No partitions were configured");
		}

		//get set to compare to all other historie's timerevent sets
		K firstKey = partitionKeys.get(0);
		Set<TimerEvent> timerEvents = this.historyMap.get(firstKey).keySet();

		List<Set<TimerEvent>> otherTimerEventSets = other.getAllTimeEventKeysSets();
		//compare the first set of timerEvent keys to the rest. If it equals them all, they all equal each other,
		//as desired
		for(Set<TimerEvent> otherTimerEventSet : otherTimerEventSets){

			//missmatch in timer events, ie the partitions have a skew in simulated time?
			if(!otherTimerEventSet.equals(timerEvents)){
				throw new SimulationException("Failed integrity check. Simulated time missmatch.");
			}
		}
	}
	/**
	 * Returns an iterator to the timer events that occured in this history.
	 * This logic assume (with reason) that each key partition share and identical key set of timerevents 
	 * @return
	 */
	public Iterator<TimerEvent> timerEventIterator(){

		if(partitionKeys.isEmpty()){
			throw new ConfigurationException("cannot get timer event iterator from history. no partitions.");
		}
		//should be full
		K key = partitionKeys.get(0);
		//choose first partition to fetch timer events (all partitions should have same number of timer events)
		Iterator<TimerEvent> res = timerEventIterator(key);
		return res;
	}

	
	/**
	 * Stores a result into this history for a given algorithm and time.
	 * It is thread safe if there is at most one thread per algorithm. This
	 * way a thread can be associated to an algorithm and safely record elements 
	 * concurrently with other algorithm threads. 
	 * @param k Partition key of the element
	 * @param timerEvent The time the element occured
	 * @param e Element to add to history
	 */
	public void recordElement(K k, TimerEvent timerEvent, V e){
		//null ptr?
		if(timerEvent == null || k  == null || e == null ){
			throw new ConfigurationException("failed to record zscore, due to null pointer.");
		}

		HashMap<TimerEvent,List<V>> innerMap = historyMap.get(k);

		//no map for given key?
		if(innerMap == null){
			throw new ConfigurationException("Cannot record element, since key ("+k+") doesn't exist.");
		}
		List<V> elements = innerMap.get(timerEvent);

		//first time adding zscores for this timerevent?
		if(elements == null){
			elements = new ArrayList<V>(DEFAULT_ELEM_LIST_SIZE);
			innerMap.put(timerEvent,elements);
		}

		elements.add(e);


	}


	/**
	 * Returns all the elements for a partition key in this history in no particular chronological order.
	 * @param k The partition key  to  fetch elements from
	 * @return The element list for the partition.
	 */
	public List<V> getElements(K k){

		if(k == null){
			throw new ConfigurationException("cannot get elements for null key.");
		}
		List<V> result = new ArrayList<V>(DEFAULT_ALL_ELEM_LIST_SIZE);

		HashMap<TimerEvent,List<V>> innerMap = historyMap.get(k);

		if(innerMap == null){
			throw new ConfigurationException("can get elements for non-existant key");
		}
		//convert all the zscores of specified algorithm into a collection
		Collection<List<V>> elementLists = innerMap.values();

		Iterator<List<V>> it = elementLists.iterator();

		//iterate all the elements for all the timer events
		while(it.hasNext()){
			List<V> elements= it.next();
			result.addAll(elements);
		}//end iterate zscores for each timer event 


		return result;
	}

	/**
	 * Returns an iterator to all the timerEvents in chrononogical order
	 * @param k Partition key to get elements from.
	 * @return Chronologically ordered TimerEvent iterator.
	 */
	public Iterator<TimerEvent> timerEventIterator(K k){


		if(k == null){
			throw new ConfigurationException("cannot get elements for null key.");
		}

		HashMap<TimerEvent,List<V>> innerMap = historyMap.get(k);

		Set<TimerEvent> keySet = innerMap.keySet();

		List<TimerEvent> result = new ArrayList<TimerEvent>(keySet);

		Collections.sort(result);

		return result.iterator();
	}

	/**
	 * Returns an iterator to elements for an algorithm at a given time.
	 * @param timerEvent The timerEvent used to indicate the time elements shhould be retrieved
	 * @param alg The algorithm elements are from
	 * @return Iterator to the elements
	 */
	public 	Iterator<V> elementIterator(K k, TimerEvent timerEvent){

		if(k == null || timerEvent == null){
			throw new ConfigurationException("cannot get element iterator for null key or null timerEvent.");
		}
		HashMap<TimerEvent,List<V>> innerMap = historyMap.get(k);

		List<V> result = null;

		//make sure vehicel key exists
		if(innerMap != null){
			result = innerMap.get(timerEvent);
		}else{
			result = new ArrayList<V>(0);	
		}
		//make sure list isn't null
		if(result == null){
			result = new ArrayList<V>(0);
		}
		return result.iterator();
	}

	public void clear() {
		HashMap<K,HashMap<TimerEvent,List<V>>> map =this.historyMap;

		Iterator<K> ait = map.keySet().iterator();
		//go and empty all the sensor statuses
		while(ait.hasNext()){
			K a = ait.next();

			HashMap<TimerEvent,List<V>> innerMap = map.get(a);

			Iterator<TimerEvent> tit = innerMap.keySet().iterator();

			while(tit.hasNext()){
				TimerEvent e = tit.next();
				List<V> elems = innerMap.get(e);

				elems.clear();//empty the list
			}
		}

	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((historyMap == null) ? 0 : historyMap.hashCode());
		result = prime * result + ((partitionKeys == null) ? 0 : partitionKeys.hashCode());
		return result;
	}


	protected HashMap<K,HashMap<TimerEvent,List<V>>> getHistoryMap(){
		
		return this.historyMap;
	}
	
	public static <K1,K2,V> boolean isMapEquals(HashMap<K1,HashMap<K2,List<V>>> map,HashMap<K1,HashMap<K2,List<V>>> otherMap,HistoryValueComparator<V> valueComparator){
		
		if(valueComparator == null){
			throw new SimulationException("cannot compare maps, comparator was null");
		}
		//one is null, and the other isnt'?
		if(Util.oneObjectNullOtherIsNonNull(map,otherMap)){
			return false;
		}
	
				
		Iterator<K1> keyIt = map.keySet().iterator();
		Iterator<K1> otherKeyIt = otherMap.keySet().iterator();
		//go and empty all the sensor statuses
		while(keyIt.hasNext()){
			
			//different number of key entries?
			if(!otherKeyIt.hasNext()){
				return false;
			}
			K1 key = keyIt.next();
			K1 otherKey = otherKeyIt.next();

			HashMap<K2,List<V>> innerMap = map.get(key);
			HashMap<K2,List<V>> otherInnerMap = otherMap.get(otherKey);
			
			//one is null, and the other isnt'?
			if(Util.oneObjectNullOtherIsNonNull(innerMap,otherInnerMap)){
				return false;
			}
			
			Iterator<K2> timeIt = innerMap.keySet().iterator();
			Iterator<K2> otherTimeIt = otherInnerMap.keySet().iterator();

			while(timeIt.hasNext()){
				
				//different number of time entries?
				if(!otherTimeIt.hasNext()){
					return false;
				}
				
				K2 time = timeIt.next();
				K2 otherTime = otherTimeIt.next();
				
				//one is null, and the other isnt'?
				if(Util.oneObjectNullOtherIsNonNull(time,otherTime)){
					return false;
				}
				if(!time.equals(otherTime)){
					return false;
				}
				
				List<V> values = innerMap.get(time);
				List<V> otherValues = otherInnerMap.get(otherTime);
				

				//one is null, and the other isnt'?
				if(Util.oneObjectNullOtherIsNonNull(values,otherValues)){
					return false;
				}
				
				if(values.size() != otherValues.size()){
					return false;
				}
				
				for(int i = 0;i<values.size();i++){
					V v = values.get(i);
					V vOther = otherValues.get(i);
					
					if(!valueComparator.valueEquals(v,vOther)){
						return false;
					}
				}
				
				
			}//end iterate time events
			
			//different number of time entries?
			if(otherTimeIt.hasNext()){
				return false;
			}
		}
		
		//different number of key entries?
		if(otherKeyIt.hasNext()){
			return false;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof History))
			return false;
		
		History<K,V> other = null;
		
		//try to cast, if it fails, its not right type
		try{
			other = (History<K,V>) obj;
		}catch(Exception e ){
			return false;
		}
		
		
		if(!isMapEquals(this.getHistoryMap(),other.getHistoryMap(),this)){
			return false;
		}
	
		List<K> keys = this.partitionKeys;
		List<K> otherKeys = other.partitionKeys;
		
		//one is null, and the other isnt'?
		if(Util.oneObjectNullOtherIsNonNull(keys,otherKeys)){
			return false;
		}
		
		if(!Arrays.equals(keys.toArray(),otherKeys.toArray())){
			return false;
		}
		
		return true;
	}


	@Override
	public boolean valueEquals(V v1, V v2) {
		
		if(v1 != null){
			return v1.equals(v2);	
		}
		if(v2 !=null){
			return v2.equals(v1);
		}
		
		return true;
		
		
	}
	

}