package com.thingple.uhf;

public interface IUHFService {
	
	int defaultPort = 4196;

	/**
	 * 打开连接
	 */
	boolean connectDev();
	
	/**
	 * 关闭连接
	 */
	void disConnectDev();
	
	/**
	 * 选择天线
	 * @param antennaId
	 */
	void selectAntenna(Antenna antenna);
	
	/**
	 * 设置天线是否启用
	 * @param antennaId 天线号
	 * @param enable true:启用 false:禁用
	 */
	boolean setAntennaEnable(Antenna antenna, boolean enable);
	
	/**
	 * 查看天线状态
	 * @param antenna
	 * @return
	 */
	boolean antennaEnable(Antenna antenna);
	
	/**
	 * 设置功率
	 * @param power
	 */
	boolean setAntennaPower(Antenna antenna, int power);
	
	/**
	 * 读取天线物理号
	 * @return
	 */
	int getAntennaNum(Antenna antenna);
	
	/**
	 * 设置天线物理号
	 * @param power (0-3)
	 */
	boolean setAntennaNum(Antenna antenna);
	
	/**
	 * 读取功率
	 * @return
	 */
	int getAntennaPower(Antenna antenna);
	
	/**
	 * 设置功率
	 * @param power
	 */
	boolean setAntennaWorkTime(Antenna antenna, int during);
	
	/**
	 * 读取功率
	 * @return
	 */
	int getAntennaWorkTime(Antenna antenna);
	
	/**
	 * 开始盘点
	 */
	void inventoryStart();
	
	/**
	 * 盘点一次
	 */
	TagInfo inventoryOnce();
	
	/**
	 * 停止盘点
	 */
	void inventoryStop();
	
	/**
	 * 接收盘点数据
	 * @param listener
	 */
	void setInventoryListener(InventoryListener listener);
	
	/**
	 * 当前连接状态
	 * @return
	 */
	boolean isConnected();
	
	/**
	 * 是否在inventory操作中
	 * @return
	 */
	boolean isWorking();
}
