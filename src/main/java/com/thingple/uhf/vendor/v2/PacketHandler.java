package com.thingple.uhf.vendor.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thingple.uhf.InventoryListener;
import com.thingple.uhf.TagInfo;
import com.thingple.uhf.TagInfoBuilder;
import com.thingple.uhf.Util;

public class PacketHandler{
	
	private static final int packetHeaderLength = 8;
	
	private List<Byte> buffer = new ArrayList<>();
	private int packetLength = 0;
	
	public byte[] header;
	
	public byte[] footer;
	
	public final List<TagInfo> tags = new ArrayList<>();
	public final Map<String, Long> tagMap = new HashMap<>();
	
	int printIndex = 0;
	
	private boolean showLog = false;
	
	private InventoryListener inventoryListener;
	
	private long inventoryId;
	
	public PacketHandler(long inventoryId, InventoryListener inventoryListener) {
		this.inventoryId = inventoryId;
		this.inventoryListener = inventoryListener;
	}
	
	public boolean append(int n) {
		
		byte b = (byte) n;
		String data = Util.byteToHex(b);
		
		// log
		if (showLog) {
			System.out.print(data + " ");
			if (++printIndex % 8 == 0) {
				System.out.println();
			}
		}
		buffer.add(b);
		if (buffer.size() == packetHeaderLength) {
			int length = Util.bytesToInt(buffer.get(4), buffer.get(5));
			packetLength = length * 4 + 8;
			if (showLog) {
				System.out.println("Packet length:" + packetLength);
			}
		}
		if (packetLength > 0 && buffer.size() == packetLength) {
			
			byte[] packet = popBuffer();
			int packetType = Util.bytesToInt(packet[2], packet[3]);
			if (packetType == 0) {
				header = packet;
			} else if (packetType == 5) {
				receiveTag(packet);
			} else if (packetType == 1) {
				footer = packet;
				return false;
			}
			if (showLog) {
				System.out.println("\n");
			}
		}
		return true;
	}
	
	private byte[] popBuffer() {
		
		byte[] bs = Util.readBuffer(buffer);
		buffer.clear();
		packetLength = 0;
		return bs;
	}
	
	public void receiveTag(byte[] raw) {
		
		TagInfoBuilder builder = new TagInfoBuilderV1Impl();
		TagInfo tag = builder.data(raw).build();
		String epc = tag.epc;
		if (epc != null) {
			Long lastTime = tagMap.get(epc);
			
			if (lastTime == null || (System.currentTimeMillis() - lastTime) > 300) {
				tagMap.put(epc, System.currentTimeMillis());
			}
		}
		tags.add(tag);
		if (inventoryListener != null) {
			inventoryListener.onData(inventoryId, tag);
		}

	}
	
}
