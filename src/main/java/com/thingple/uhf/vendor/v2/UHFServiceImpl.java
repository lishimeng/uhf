package com.thingple.uhf.vendor.v2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.thingple.uhf.Antenna;
import com.thingple.uhf.IUHFService;
import com.thingple.uhf.InventoryListener;
import com.thingple.uhf.Util;

public class UHFServiceImpl implements IUHFService {
	
	public String host;
	public int port;
	
	private Socket socket;
	
	private Command command;
	
	private Antenna currentSelectAntenna;
	
	private InventoryListener inventoryListener;
	
	private boolean inventoryRun = false;
	
	private boolean readerWorking = false;
	
	public UHFServiceImpl(String host, int port) {
		this.host = host;
		this.port = port;
		System.out.println("Init client");
	}

	@Override
	public boolean connectDev() {
		
		boolean success = false;
		try {
			System.out.println("Connect:" + host + ":" + port);
			socket = new Socket();
			
			SocketAddress endpoint = new InetSocketAddress(host, port);
			socket.connect(endpoint, 1000);
			command = new Command(socket.getInputStream(), socket.getOutputStream());
			success = true;
			init();
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		System.out.println("Connect " + (success ? "success" : "fail"));
		return success;
	}

	@Override
	public void disConnectDev() {
		try {
			System.out.println("closing connect...");
			while (readerWorking) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("client is shutdown.");
	}

	@Override
	public boolean setAntennaPower(Antenna antenna, int power) {
		selectAntenna(antenna);
		System.out.println("set power " + antenna + ":" + power);
		command.write(Register.Addr.antennaPower, power);
		int fromDevice = getAntennaPower(antenna);
		return power == fromDevice;
	}

	@Override
	public int getAntennaPower(Antenna antenna) {
		selectAntenna(antenna);
		System.out.println("read power " + antenna);
		command.read(Register.Addr.antennaPower, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		return Util.bytesToInt(res[4], res[5], res[6], res[7]);
	}

	@Override
	public void inventoryStart() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				inventoryRun = true;
				while (inventoryRun) {
					long inventoryId = System.currentTimeMillis();
					System.out.println("Start inventory:" + inventoryId);
					readerWorking = true;
					command.write(Register.Addr.inventory, 15);
					boolean running = true;
					PacketHandler handler = new PacketHandler(inventoryId, inventoryListener);
					while (running) {
						int n = command.readByte();
						running = handler.append(n);
						if (!running) {
							System.out.println("\nReceived full packets");
						}
					}
					System.out.println("Inventory:" + inventoryId + " complete\n");
					System.out.println("tags:" + handler.tagMap.size());
					System.out.println("size:" + handler.tags.size());
					readerWorking = false;
				}
				System.out.println("Inventory thread complete");
			}
		}).start();
	}

	@Override
	public void inventoryStop() {
		System.out.println("stop inventory");
		inventoryRun = false;
	}

	@Override
	public void setInventoryListener(InventoryListener listener) {

		this.inventoryListener = listener;
	}

	@Override
	public void inventoryOnce() {
		System.out.println("inventory once");
	}

	@Override
	public void selectAntenna(Antenna antenna) {
		if (currentSelectAntenna != antenna) {
			System.out.println("select antenna " + antenna);
			command.write(Register.Addr.selectAntenna, antenna.getIndex());
			this.currentSelectAntenna = antenna;
		}
	}

	@Override
	public boolean setAntennaEnable(Antenna antenna, boolean enable) {
		selectAntenna(antenna);
		System.out.println("set antenna status " + antenna + ":" + (enable ? "enable" : "disable"));
		command.write(Register.Addr.antennaStatus, enable ? 1 : 0);
		return antennaEnable(antenna);
	}

	@Override
	public boolean antennaEnable(Antenna antenna) {
		selectAntenna(antenna);
		System.out.println("get antenna status " + antenna);
		command.read(Register.Addr.antennaStatus, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		int enable = Util.bytesToInt(res[4], res[5], res[6], res[7]);
		return enable == 1;
	}

	@Override
	public boolean setAntennaWorkTime(Antenna antenna, int during) {
		selectAntenna(antenna);
		System.out.println("set work time " + antenna + ":" + during);
		command.write(Register.Addr.antennaWorkTime, during);
		int fromDevice = getAntennaWorkTime(antenna);
		return fromDevice == during;
	}

	@Override
	public int getAntennaWorkTime(Antenna antenna) {
		selectAntenna(antenna);
		System.out.println("get work time " + antenna);
		command.read(Register.Addr.antennaWorkTime, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		return Util.bytesToInt(res[4], res[5], res[6], res[7]);
	}

	@Override
	public int getAntennaNum(Antenna antenna) {
		selectAntenna(antenna);
		System.out.println("get antenna physical num " + antenna);
		command.read(Register.Addr.antennaNumber, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		return Util.bytesToInt(res[4], res[5], res[6], res[7]);
	}

	@Override
	public boolean setAntennaNum(Antenna antenna) {
		selectAntenna(antenna);
		System.out.println("set antenna physical number " + antenna);
		command.write(Register.Addr.antennaNumber, antenna.getIndex());
		int fromDevice = getAntennaNum(antenna);
		return fromDevice == antenna.getIndex();
	}
	
	private void init() {
		System.out.println("enable all antenna");
		int defaultWorkTime = 32;//ms
		setAntennaEnable(Antenna.LOGIC_FIRST, true);
		setAntennaNum(Antenna.LOGIC_FIRST);
		setAntennaWorkTime(Antenna.LOGIC_FIRST, defaultWorkTime);
		
		setAntennaEnable(Antenna.LOGIC_SECOND, true);
		setAntennaNum(Antenna.LOGIC_SECOND);
		setAntennaWorkTime(Antenna.LOGIC_SECOND, defaultWorkTime);
		
		setAntennaEnable(Antenna.LOGIC_THRID, true);
		setAntennaNum(Antenna.LOGIC_THRID);
		setAntennaWorkTime(Antenna.LOGIC_THRID, defaultWorkTime);
		
		setAntennaEnable(Antenna.LOGIC_FOURTH, true);
		setAntennaNum(Antenna.LOGIC_FOURTH);
		setAntennaWorkTime(Antenna.LOGIC_FOURTH, defaultWorkTime);
	}

	@Override
	public boolean isConnected() {
		return !socket.isClosed();
	}

	@Override
	public boolean isWorking() {
		return readerWorking;
	}
}
