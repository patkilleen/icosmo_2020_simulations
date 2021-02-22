package common.event.stream;

import java.util.ArrayList;
import java.util.List;

import common.event.HistoryEvent;

public class HistoryOutputStream extends EventOutputStream <HistoryEvent,Object>{

	public final static Object UNIQUE_HISTORY_KEY = null;
	public HistoryOutputStream()
	  {
super();
			
		//one history will be used, so just one null key needed
		List<Object> partitionKeys = new ArrayList<Object>(1);
		partitionKeys.add(UNIQUE_HISTORY_KEY);
		super.setParitionKeys(partitionKeys);
		super.init();
	  }
	

	public void writeHistoryEvent(HistoryEvent e){
		//null key, don't partition the history events, just use one key
		this.write(UNIQUE_HISTORY_KEY, e);
	}
}
