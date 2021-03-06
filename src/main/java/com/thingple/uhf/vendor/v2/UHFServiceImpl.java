package com.thingple.uhf.vendor.v2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.thingple.uhf.Antenna;
import com.thingple.uhf.IUHFService;
import com.thingple.uhf.InventoryListener;
import com.thingple.uhf.TagInfo;
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
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("set power " + antenna + ":" + power);
		command.write(Register.Addr.antennaPower, power);
		int fromDevice = getAntennaPower(antenna);
		readerWorking = false;
		return power == fromDevice;
	}

	@Override
	public int getAntennaPower(Antenna antenna) {
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("read power " + antenna);
		command.read(Register.Addr.antennaPower, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		readerWorking = false;
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
					try {
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
					} catch (Exception e) {
						System.err.println(e);
					} finally {
						readerWorking = false;
					}
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
	public TagInfo inventoryOnce() {
		System.out.println("inventory once");
		long inventoryId = System.currentTimeMillis();
		System.out.println("Start inventory:" + inventoryId);
		PacketHandler handler = new PacketHandler(inventoryId, null);
		try {
			
			readerWorking = true;
			command.write(Register.Addr.inventory, 15);
			boolean running = true;
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
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			readerWorking = false;
		}
		if (handler.tags.size() > 0) {
			return handler.tags.get(0);
		} else {
			return null;
		}
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
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("set antenna status " + antenna + ":" + (enable ? "enable" : "disable"));
		command.write(Register.Addr.antennaStatus, enable ? 1 : 0);
		readerWorking = false;
		return antennaEnable(antenna);
	}

	@Override
	public boolean antennaEnable(Antenna antenna) {
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("get antenna status " + antenna);
		command.read(Register.Addr.antennaStatus, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		int enable = Util.bytesToInt(res[4], res[5], res[6], res[7]);
		readerWorking = false;
		return enable == 1;
	}

	@Override
	public boolean setAntennaWorkTime(Antenna antenna, int during) {
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("set work time " + antenna + ":" + during);
		command.write(Register.Addr.antennaWorkTime, during);
		int fromDevice = getAntennaWorkTime(antenna);
		readerWorking = false;
		return fromDevice == during;
	}

	@Override
	public int getAntennaWorkTime(Antenna antenna) {
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("get work time " + antenna);
		command.read(Register.Addr.antennaWorkTime, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		readerWorking = false;
		return Util.bytesToInt(res[4], res[5], res[6], res[7]);
	}

	@Override
	public int getAntennaNum(Antenna antenna) {
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("get antenna physical num " + antenna);
		command.read(Register.Addr.antennaNumber, 0);
		byte[] res = command.optResult(8);
		System.out.println(Util.printHexString(res));
		readerWorking = false;
		return Util.bytesToInt(res[4], res[5], res[6], res[7]);
	}

	@Override
	public boolean setAntennaNum(Antenna antenna) {
		readerWorking = true;
		selectAntenna(antenna);
		System.out.println("set antenna physical number " + antenna);
		command.write(Register.Addr.antennaNumber, antenna.getIndex());
		int fromDevice = getAntennaNum(antenna);
		readerWorking = false;
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
		boolean socketAvailable = false;
		if (socket != null && socket.isConnected() && !socket.isClosed()) {
			try {
				if (!inventoryRun && !readerWorking) {
					socket.sendUrgentData(0xff);
					socketAvailable = true;
				} else {
					socketAvailable = true;
				}
			} catch (IOException e) {
				socketAvailable = false;
				e.printStackTrace();
			}
		}
		return socketAvailable;
	}

	@Override
	public boolean isWorking() {
		return readerWorking;
	}
}
