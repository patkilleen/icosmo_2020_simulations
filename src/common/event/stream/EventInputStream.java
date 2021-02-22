/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common.event.stream;

import java.util.Iterator;
import java.util.List;

import common.event.Event;
import common.exception.ConfigurationException;

// line 46 "model.ump"
// line 130 "model.ump"
public class EventInputStream <E extends Event, K> extends EventStream<E,K>
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // CONSTRUCTOR
  //------------------------


  //------------------------
  // INTERFACE
  //------------------------

  public EventInputStream(List<K> partitionKeys) {
		super(partitionKeys);
	}
  /**
	 * Constructor to be used by subclasses that want more control over partition key configuration.
	 */
  protected EventInputStream(){
	  
  }
public void delete()
  {
    super.delete();
  }
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 48 "model.ump"
  public Iterator<E> iterator(K t){
	  List<E> events = this.getEvents(t);
	  if(events == null){
		  throw new ConfigurationException("failed to get iterator for key: "+t+", since partition key doesn't exist.");
	  }
	  return events.iterator();
  }

  
}