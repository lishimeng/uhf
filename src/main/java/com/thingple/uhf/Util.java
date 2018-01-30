package com.thingple.uhf;

import java.util.List;

public class Util {

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			byte high = charToByte(hexChars[pos]);
			byte low = charToByte(hexChars[pos + 1]);
			d[i] = (byte) (high << 4 | low);
		}
		return d;
	}
	
	public static String printHexString( byte[] b) {
		String a = "";
		  for (int i = 0; i < b.length; i++) { 
		    String hex = byteToHex(b[i]);
		   
		    a = a+hex;
		  } 
		  
		       return a;
		}

	public static String byteToHex(byte b) {
		String hex = Integer.toHexString(b & 0xFF).toUpperCase();
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		return hex;
	}
	
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	public static byte[] intToBytes(int value)
	{
		byte[] src = new byte[4];
		src[3] = (byte) ((value>>24) & 0xFF);
		src[2] = (byte) ((value>>16) & 0xFF);
		src[1] = (byte) ((value>>8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}
	
	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (src[offset] & 0xFF)
				| ((src[offset+1] & 0xFF)<<8)
				| ((src[offset+2] & 0xFF)<<16)
				| ((src[offset+3] & 0xFF)<<24);
		return value;
	}
	
	public static int bytesToInt(byte... lowHigh) {
		int value = 0;
		for (int i = 0; i < lowHigh.length; i++) {
			if (i == 0) {
				value = lowHigh[0] & 0xFF;
			} else {
				value += (lowHigh[i] & 0xFF)<< (8 * i);
			}
		}
		return value;
	}
	
	public static void reverse(byte[] arr) {
		if (arr != null) {
			for (int i = 0; i < arr.length / 2; i++) {
				byte temp = arr[i];
				arr[i] = arr[arr.length - 1 - i];
				arr[arr.length - 1 - i] = temp;
			}
		}
	}
	
	public static byte[] readBuffer(List<Byte> buffer) {
		byte[] bs = new byte[buffer.size()];
		for (int i = 0; i < buffer.size(); i++) {
			bs[i] = buffer.get(i);
		}
		return bs;
	}
}
