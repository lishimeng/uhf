package com.thingple.uhf.vendor.v2;

public interface Register {

	byte[] READ = {0x00, 0x00};
	byte[] WRITE = {0x01, 0x00};
	
	public interface Addr {
		/**
		 * 选择逻辑天线 0x0701
		 */
		byte[] selectAntenna = {0x01, 0x07};
		/**
		 * 逻辑天线状态 0x0702
		 */
		byte[] antennaStatus = {0x02, 0x07};
		
		/**
		 * 天线号 0x0704
		 */
		byte[] antennaNumber = {0x04, 0x07};
		
		/**
		 * 天线盘点持续时间 0x0705
		 */
		byte[] antennaWorkTime = {0x05, 0x07};
		
		/**
		 * 天线功率 0x0706
		 */
		byte[] antennaPower = {0x06, 0x07};
		
		/**
		 * 盘点 0xf000
		 */
		byte[] inventory = {0x00, (byte) 0xf0};
	}
}
