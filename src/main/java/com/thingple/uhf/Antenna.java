package com.thingple.uhf;

public enum Antenna {

	LOGIC_FIRST(0),
	LOGIC_SECOND(1),
	LOGIC_THRID(2),
	LOGIC_FOURTH(3);
	
	private int index;
	
	private Antenna(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
