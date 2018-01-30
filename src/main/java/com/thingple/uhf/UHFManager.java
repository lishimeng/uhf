package com.thingple.uhf;

import com.thingple.uhf.vendor.v2.UHFServiceImpl;

public class UHFManager {

	private UHFManager() {
		
	}
	
	public static IUHFService getUHFService(String host, int port) {
		return new UHFServiceImpl(host, port);
	}

}
