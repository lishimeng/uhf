package com.thingple.uhf;

public interface InventoryListener {

	void onData(long inventoryId, TagInfo data);
	
}
