package phase.generation.history;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import common.FaultDescription;
import common.Util;
import common.Vehicle;
import common.event.RepairEvent;
import common.event.TimerEvent;

public class RepairHistory extends History<Vehicle,RepairEvent>  implements Serializable{

	public RepairHistory(List<Vehicle> vehicles) {
		super(vehicles);
	}

}
