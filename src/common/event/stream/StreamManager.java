package common.event.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import common.event.Event;
import common.exception.ConfigurationException;
/**
 * 
 * @author Not admin
 *
 * @param <E> The event type to be stored in this stream
 * @param <K> The partition key type used to split stream into peices, to facilitate concurrent event additon (one thread per partition key)
 */
public class StreamManager<E extends Event,K> {

	private EventInputStream<E,K> inStream;
	private EventOutputStream<E,K> outStream;
	public StreamManager(EventInputStream<E,K> inStream, EventOutputStream<E,K> outStream) {
		if(inStream == null || outStream == null){
			throw new ConfigurationException("Failed to create stream manager, since streams were null.");
		}
		this.inStream = inStream;
		this.outStream = outStream;
	}
	
	/**
	 * Flushes the input stream into the output stream. This empties the input stream into the output stream.
	 * That is, the input stream has its content transfered into the output stream.
	 */
	public void flush(){
		
		//copy output stream to input stream
		HashMap<K,List<E>> outEventsMap = outStream.getEventMap();
		HashMap<K,List<E>> inEventsMap = inStream.getEventMap();
		
		Iterator<K> keyIt = outStream.getParitionKeys().iterator();
		
		//iterate all keys that exist in output
		while(keyIt.hasNext()){
			
			K k = keyIt.next();
			
			//shouldn't have null entries
			List<E> outputEventBuffer = outStream.getEvents(k);
			
			//could have null entries?
			List<E> inputEventBuffer = inEventsMap.get(k);
			
			if(inputEventBuffer == null){
				//it may be null (1st flush), so create the buffer
				inputEventBuffer = new ArrayList<E>(outputEventBuffer.size());
				//set the 
			}else{
				//empty contents (keep buffer intact), dont need rebuild or garbage collect map
				inputEventBuffer.clear();
			}
			
			//copy output event (source) to input events (destination)
			for(E e : outputEventBuffer){
				inputEventBuffer.add(e);
			}
			
			inEventsMap.put(k,inputEventBuffer);
			
			//clear the output stream
			outputEventBuffer.clear();
		}
	}
	
	protected  EventOutputStream<E,K> getOutputStream(){
		return outStream;
	}
	
	protected  EventInputStream<E,K> getInputStream(){
		return inStream;
	}
}
