package common.event.stream;

import common.event.HistoryEvent;

public class HistoryStreamManager extends StreamManager<HistoryEvent,Object>{

	public HistoryStreamManager(EventInputStream<HistoryEvent,Object> inStream, EventOutputStream<HistoryEvent,Object> outStream) {
		super(inStream, outStream);
	}
	
	public void flush(){
		super.flush();
	}
}
