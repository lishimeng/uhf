package com.thingple.uhf.vendor.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thingple.uhf.Util;

public class Command {

	private InputStream in;
	
	private OutputStream out;
	
	private boolean logEnabled = true;
	
	public Command(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public void write(byte[] addr, int val) {
		byte[] value = Util.intToBytes(val);
		write(addr, value);
	}
	
	public void read(byte[] addr, int val) {
		byte[] value = Util.intToBytes(val);
		read(addr, value);
	}
	
	public void write(byte[] addr, byte[] val) {
		try {
			out.write(Register.WRITE);
			out.write(addr);
			out.write(val);
			out.flush();
			if (logEnabled) {
				System.out.print("req:");
				System.out.print(Util.byteToHex(Register.WRITE[0]) + " ");
				System.out.print(Util.byteToHex(Register.WRITE[1]) + " ");
				for (byte b : addr) {
					System.out.print(Util.byteToHex(b) + " ");
				}
				for (byte b : val) {
					System.out.print(Util.byteToHex(b) + " ");
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void read(byte[] addr, byte[] val) {
		try {
			out.write(Register.READ);
			out.write(addr);
			out.write(val);
			out.flush();
			if (logEnabled) {
				System.out.print("req:");
				System.out.print(Util.byteToHex(Register.READ[0]) + " ");
				System.out.print(Util.byteToHex(Register.READ[1]) + " ");
				for (byte b : addr) {
					System.out.print(Util.byteToHex(b) + " ");
				}
				for (byte b : val) {
					System.out.print(Util.byteToHex(b) + " ");
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] optResult() {
		byte[] res = new byte[10];
		
		int i = 0;
		try {
			int n;
			while ((n = in.read()) != -1 && i < 10) {
				res[i++] = (byte) n;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Arrays.copyOf(res, i);
	}
	
	public byte[] optResult(boolean untilEOF) {
		byte[] res = null;
		List<Byte> bs = new ArrayList<>();
		
		int i = 0;
		try {
			int n;
			while ((n = in.read()) != -1) {
				bs.add((byte) n);
				System.out.println(Util.printHexString(Util.readBuffer(bs)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Arrays.copyOf(res, i);
	}
	
	public byte[] optResult(int expectBytes) {
		byte[] res = new byte[expectBytes];
		
		int i = 0;
		try {
			while (i < expectBytes) {
				res[i++] = (byte) in.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print("resp:");
		System.out.println(Util.printHexString(res));
		return res;
	}
	
	public int readByte() {
		int n = -1;
		try {
			n = in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
	
}
