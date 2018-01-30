package com.thingple.uhf;

public class TagInfo {
	
	public static final int bytes = 2;
	public static final int word = bytes * 2;
	
	public int tidLength;//word
	public int epcLength;//word
	
	public boolean fastIDEnable;
	
	public int antennaIndex;
	
	public double rssi;
	
	public double nbrssi;
	
	public double wbrssi;
	
	public int phase;
	
	public String tid;
	
	public String epc;
	
	public byte[] epcData;
	
	public String pc;
	
	public String crc;
	
	public byte[] rawData;
	
	public TagInfo() {
	}
	
	@Override
	public String toString() {
		return "TID enable:\t" + fastIDEnable
				+ "\nTID length:\t" + tidLength + "(word)"
				+ "\nEPC length:\t" + epcLength + "(word)"
				+ "\nANT index:\t" + antennaIndex
				+ "\nTID:\t" + tid
				+ "\nEPC:\t" + epc
				+ "\nRSSI:\t" + rssi
				+ "\nWBRSSI:\t" + wbrssi
				+ "\nNBRSSI:\t" + nbrssi
				+ "\nPHASE:\t" + phase
				+ "\nPC:\t" + pc
				+ "\nCRC:\t" + crc;
	}
	
	public String shortInfo() {
		return "EPC:\t" + epc
				+ "\nANT index:\t" + antennaIndex;
	}
}
