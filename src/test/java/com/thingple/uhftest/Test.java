package com.thingple.uhftest;

import com.thingple.uhf.IUHFService;
import com.thingple.uhf.InventoryListener;
import com.thingple.uhf.TagInfo;
import com.thingple.uhf.UHFManager;

public class Test {
	
	public void testCase001() throws InterruptedException {
		String host = "192.168.10.202";
		int port = IUHFService.defaultPort;
		IUHFService iuhfService = UHFManager.getUHFService(host, port);
		if (iuhfService.connectDev()) {

			iuhfService.setInventoryListener(new InventoryListener() {
				
				@Override
				public void onData(long inventoryId, TagInfo data) {
					System.out.println(inventoryId);
					System.out.println(data.shortInfo());
				}
			});
			
			iuhfService.inventoryStart();
			
			Thread.sleep(10 * 1000);
			iuhfService.inventoryStop();
			
			Thread.sleep(2 * 1000);
			iuhfService.disConnectDev();
		}
		System.out.println("test complete");
	}

	public static void main(String[] args) throws Exception {
		new Test().testCase001();
	}
}
