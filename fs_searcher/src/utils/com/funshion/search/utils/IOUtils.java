package com.funshion.search.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.funshion.search.utils.c2j.cTypes.U32;
import com.funshion.search.utils.c2j.cTypes.U64;

public class IOUtils {
	public static void writeBytes(OutputStream ops, byte[] bs) throws IOException {
		ops.write(bs, 0, bs.length);
	}
	public static void writeBytes(OutputStream ops, byte[] bs, int offset, int len) throws IOException{
		ops.write(bs, offset, len);
	}
	public static void mustFillBuffer(InputStream ips, byte[]bs) throws IOException{
		mustFillBuffer(ips, bs, 0, bs.length);
	}
	public static void mustFillBuffer(InputStream ips, byte[]bs, 
			int offset, int len) throws IOException{
		int readed = tryFillBuffer(ips, bs, offset, len);
		if(readed != len){
			throw new IOException("require to read " + len + ", but get " + readed + " from ips " + ips);
		}
	}
	/**
	 * try to fill the bs, return the bytes readed
	 * @param ips
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	public static int tryFillBuffer(InputStream ips,
			byte[]bs) throws IOException {
		return tryFillBuffer(ips, bs, 0, bs.length);
	}
	public static int tryFillBuffer(InputStream ips, byte[]bs, 
			int offset, int len) throws IOException {
		int readed = 0;

		while(true) {
			int tRead = ips.read(bs, readed + offset, len - readed);
			if (tRead == -1){
				if (readed != 0)
					break;
				readed = -1;
				break;
			}
			readed += tRead;
			if(readed >= len)
				break;
		}

		return readed;
	}
	
	public static void writeLong(OutputStream ops, long toWrite) throws IOException{
		U64 u = new U64(toWrite);
		ops.write(u.toBytes());
	}
	public static long readLong(InputStream ips) throws IOException{
		U64 v = new U64();
		byte[] buff = new byte[v.sizeOf()];
		mustFillBuffer(ips, buff);
		v.fromBytes(buff);
		return v.getValue();
	}
	public static void writeInt(OutputStream ops, int toWrite) throws IOException{
		U32 u32 = new U32(toWrite);
		ops.write(u32.toBytes());
	}
	public static int readInt(InputStream ips) throws IOException{
		U32 intValue = new U32();
		byte[] buff = new byte[intValue.sizeOf()];
		mustFillBuffer(ips, buff);
		intValue.fromBytes(buff);
		return intValue.intValue();
	}
	public static void writeString(OutputStream ops, String strToWrite) throws IOException{
		byte[]buff = strToWrite.getBytes();
		writeInt(ops, buff.length);
		writeBytes(ops, buff);
	}
	public static String readString(InputStream ips) throws IOException{
		int len = readInt(ips);
		byte []buff = new byte[len];
		mustFillBuffer(ips, buff);
		return new String(buff);
	}
}
