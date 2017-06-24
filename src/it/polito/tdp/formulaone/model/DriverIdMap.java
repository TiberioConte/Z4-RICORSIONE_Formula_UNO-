package it.polito.tdp.formulaone.model;

import java.util.HashMap;
import java.util.Map;

public class DriverIdMap {
	private Map<Integer,Driver> map;

	public DriverIdMap() {
		map = new HashMap<Integer,Driver>();
	}
	
	public Driver get(int  id){
		return map.get(id);
	}
	
	public void put(Driver t){
		 map.put(t.getDriverId(),t);
		 
	}
}
