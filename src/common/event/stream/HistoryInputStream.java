package common.event.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.event.HistoryEvent;

public class HistoryInputStream extends EventInputStream <HistoryEvent,Object>{
	
	public final static Object UNIQUE_HISTORY_KEY = null;
	public HistoryInputStream() {
		super();
		
		//one history will be used, so just one null key needed
		List<Object> partitionKeys = new ArrayList<Object>(1);
		partitionKeys.add(UNIQUE_HISTORY_KEY);
		super.setParitionKeys(partitionKeys);
		super.init();
	}

	public HistoryEvent readHistoryEvent(){
		Iterator<HistoryEvent> it = this.iterator(UNIQUE_HISTORY_KEY);
		
		if(it.hasNext()){
			
			//its assumed only 1 history will be added to the stream
			return it.next();
		}else{
			return null;
		}
			
	}

}
