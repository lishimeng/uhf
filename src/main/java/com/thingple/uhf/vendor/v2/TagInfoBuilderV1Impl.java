package com.thingple.uhf.vendor.v2;

import java.util.Arrays;

import com.thingple.uhf.TagInfo;
import com.thingple.uhf.TagInfoBuilder;
import com.thingple.uhf.Util;

public class TagInfoBuilderV1Impl implements TagInfoBuilder {
	
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
	
	@Override
	public TagInfoBuilder data(byte[] raw) {
		this.rawData = raw;
		return this;
	}
	
	@Override
	public TagInfo build() {
		build(this.rawData);
		TagInfo taginfo = new TagInfo();
		taginfo.tidLength = tidLength;
		taginfo.epcLength = epcLength;
		taginfo.fastIDEnable = fastIDEnable;
		taginfo.antennaIndex = antennaIndex;
		taginfo.rssi = rssi;
		taginfo.nbrssi = nbrssi;
		taginfo.wbrssi = wbrssi;
		taginfo.phase = phase;
		taginfo.tid = tid;
		taginfo.epc = epc;
		taginfo.epcData = epcData;
		taginfo.pc = pc;
		taginfo.crc = crc;
		taginfo.rawData = rawData;
		
		return taginfo;
	}
	
	private void build(final byte[] rawData) {
		byte[] epcArea = Arrays.copyOfRange(rawData, 20, rawData.length);// packet header(20 byte)
		
		decodeHeader();
		decodePC(epcArea[0]);
		
		String temp = Util.printHexString(epcArea);
		pc = temp.substring(0, 1 * 4);// 1 (word)
		temp = temp.substring(1 * 4);
		
		
		if (temp.length() < epcLength * TagInfo.word) {
			System.out.println("err");
		}
		epc = temp.substring(0, epcLength * TagInfo.word);
		temp = temp.substring(epcLength * TagInfo.word);
		epcData = Arrays.copyOfRange(rawData, 2 * 2, epcLength * 2);
		
		crc = temp.substring(0, 1 * TagInfo.word);// 1(word)

		if (fastIDEnable && temp.length() > 1 * TagInfo.word) {
			tid = temp.substring(1 * TagInfo.word);// after CRC
		}
	}
	
	private void decodePC(byte pc) {
		byte lenByte = pc;
		int length = lenByte >>> 3;
		if (length == 0) {// 0 is 1
			epcLength = 1;
		} else if (length == 1) {// 1 is 2
			epcLength = 2;
		} else {
			epcLength = length;
		}
	}
	
	private void decodeHeader() {
		decodePhase();
		decodeFastId();
		
		// antenna
		decodeAntenna();
		
		decodeNbRssi();
		decodeRssi();
	}

	private void decodePhase() {
		int phaseInt = rawData[18] & 0xff;
		phase = phaseInt << 1 >>> 1;
	}

	private void decodeFastId() {
		int pktFlag = rawData[1] & 0xff;
		fastIDEnable = (pktFlag << 2 >>> 7) == 1;
		if (fastIDEnable) {
			tidLength = 12 /2;
		}
	}

	private void decodeAntenna() {
		int antennaByte = rawData[19] & 0xff;
		antennaIndex = antennaByte >>> 6;
	}

	private void decodeRssi() {
		rssi = Util.bytesToInt(rawData[16], rawData[17]);
	}

	private void decodeNbRssi() {
		int nbRssi = rawData[12] & 0xff;
		
		int mantissa = nbRssi << 5 >>> 5;
		int exponent = nbRssi >>> 3;
		nbrssi = 20 * Math.log10(Math.pow(2, exponent) * (1 + mantissa / Math.pow(2, 3)));
	}
	
}
