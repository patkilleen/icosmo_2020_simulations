/*This product contains SAE International copyrighted intellectual property, 
which has been and is licensed with permission for use by Patrick Killeen, 
in this application only. No further sharing, duplicating, or transmitting is permitted.*/

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4359.01582bf35 modeling language!*/
package phase.configuraiton.input.j1939;

import java.util.*;

// line 2 "model.ump"
// line 65 "model.ump"
public class J1939Packet
{

	
  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //SensorReading Attributes
  private byte[] bytes;
  private int pgn;
  private long timestamp;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public J1939Packet(int aPgn)
  {
    bytes = new byte[J1939Parser.J1939_PACKET_DATA_LENGTH];
    pgn = aPgn;
  }

  public J1939Packet()
  {
    bytes = new byte[J1939Parser.J1939_PACKET_DATA_LENGTH];
  }
  //------------------------
  // INTERFACE
  //------------------------
  
  /* Code from template attribute_SetMany */
  public boolean addByte(byte aByte, int i)
  {
	if(i <0 || i >= bytes.length){
		throw new IndexOutOfBoundsException("Cannot add byte to index: "+i);
	}
    boolean wasAdded = false;
    bytes[i]=aByte;
    wasAdded = true;
    return wasAdded;
  }

  public long getTimestamp() {
	return timestamp;
}

public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
}

public boolean setPgn(int aPgn)
  {
    boolean wasSet = false;
    pgn = aPgn;
    wasSet = true;
    return wasSet;
  }
  /* Code from template attribute_GetMany */
  public byte getByte(int index)
  {
    byte aByte = bytes[index];
    return aByte;
  }

  public byte[] getBytes()
  {
    return bytes;
  }

  public int numberOfBytes()
  {
    int number = bytes.length;
    return number;
  }


  public int getPgn()
  {
    return pgn;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "pgn" + ":" + getPgn()+ "]";
  }
}