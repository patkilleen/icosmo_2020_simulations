/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4441.5f0a342a8 modeling language!*/

package common.event.stream;

import java.util.List;

import common.event.Event;

// line 51 "model.ump"
// line 135 "model.ump"
public class EventOutputStream <E extends Event, T>extends EventStream<E,T>
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

  public EventOutputStream(List<T> partitionKeys) {
		super(partitionKeys);
		// TODO Auto-generated constructor stub
	}
  
  /**
   * Constructor to be used by subclasses that want more control over partition key configuration.
   */
  protected EventOutputStream(){
	  
  }

public void delete()
  {
    super.delete();
  }

  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 53 "model.ump"
  public void write(T t, E e){
	  this.addEvent(t,e);
  }

  
}