package com.funshion.search.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUnzip {
	public static byte[] unGzip(byte[] input) throws IOException {
		return unGzip(new ByteArrayInputStream(input));
	}
	//	public static byte[] gzip(InputStream ips) throws IOException {
	//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//		GZIPOutputStream ops = new GZIPOutputStream(bos);
	//		byte[]buf = new byte[64 * 1024];
	//		while(true){
	//			int readed = ips.read(buf);
	//			if(readed == -1){
	//				break;
	//			}
	//			ops.write(buf, 0, readed);
	//		}
	//		ops.finish();
	//		ops.close();
	//		return bos.toByteArray();
	//	}
	public static byte[] gzip(byte[]input) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream ops = new GZIPOutputStream(bos);
		ops.write(input);
		ops.finish();
		ops.close();
		return bos.toByteArray();
	}
	public static byte[] unGzip(InputStream input) throws IOException{
		GZIPInputStream ips = new GZIPInputStream(input);
		ByteArrayOutputStream bios = new ByteArrayOutputStream();
		byte[]bs =new byte[4096];
		while(true){
			int count = ips.read(bs);
			if(count != -1){
				bios.write(bs, 0, count);
			}else{
				bios.close();
				ips.close();
				break;
			}
		}
		return bios.toByteArray();
	}
	public static void unGzip(InputStream source, OutputStream os, int[]retArr) throws IOException{
		retArr[0] = 0;
		GZIPInputStream ips = new GZIPInputStream(source);
		byte[]bs =new byte[2048];
		while(true){
			int count = ips.read(bs);
			if(count != -1){
				os.write(bs, 0, count);
				retArr[0] += count;
			}else{
				os.close();
				ips.close();
				break;
			}
		}
	}
	public static int unGzip(InputStream source, OutputStream os) throws IOException{
		GZIPInputStream ips = new GZIPInputStream(source);
		byte[]bs =new byte[4096];
		int readed = 0;
		while(true){
			int count = ips.read(bs);
			if(count != -1){
				os.write(bs, 0, count);
				readed += count;
			}else{
				os.close();
				ips.close();
				break;
			}
		}
		return readed;
	}

	public static byte[] deflate(byte[]input) {
		byte[] result = new byte[4096];
		int totDone = 3000;
		Deflater compresser = new Deflater();
		compresser.setInput(input);
		compresser.finish();
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		while(true) {
			int resultLength = compresser.deflate(result);
			if(resultLength < 1) {
				break;
			}
			bais.write(result, 0, resultLength);
			totDone -- ;
			if(totDone < 0) {
				System.out.println("ERROR totDone match!");
			}
		}
		return bais.toByteArray();
	}
	public static byte [] inflate(byte[]input) throws IOException{
		try {
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			// Decompress the bytes
			Inflater decompresser = new Inflater();
			decompresser.setInput(input);
			byte[] result = new byte[4096];
			int totDone = 3000;
			while(true) {
				int resultLength = decompresser.inflate(result);
				if(resultLength < 1) {
					break;
				}
				bais.write(result, 0, resultLength);
				totDone -- ;
				if(totDone < 0) {
					System.out.println("ERROR totDone match!");
				}
			}
			decompresser.end();

			return bais.toByteArray();
		}catch(Exception e) {
			throw new IOException(e);
		}
	}

	

	public static final int BUFFER_SIZE = 1024 * 2;
	static LogHelper log = new LogHelper("(un)zipFiles");
	public static boolean zipFiles(File[] tozip, File zipToFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipToFile));
		for(File dirObj : tozip){
			if(!dirObj.exists()){
				log.warn("file to zip does NOT exists: %s", dirObj.getAbsoluteFile());
			}
			addFile("", dirObj, out);
		}
		out.close();
		return true;
	}
	static void addFile(String prefix, File file, ZipOutputStream out) throws IOException {
		String pfix = (prefix.length() == 0 ? "" : prefix ) + file.getName();
		if(file.isDirectory()){
			pfix += "/";
			out.putNextEntry(new ZipEntry(pfix));
			File [] fs = file.listFiles();
			for(File f : fs){
				addFile(pfix, f, out);
			}
		}else{
			byte[] tmpBuf = new byte[BUFFER_SIZE];
			FileInputStream in = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(pfix));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}


	public static boolean unzipFiles(File sourceFile, 
			File destinationDirectory) throws IOException{
		if(!sourceFile.exists()){
			log.error("file to unzip is NOT exists:%s", sourceFile);
			return false;
		}
		if(!destinationDirectory.exists()){
			destinationDirectory.mkdirs();
		}else{
			if(!destinationDirectory.isDirectory()){
				log.error("destinationDirectory to unzip is NOT A FILE:%s", destinationDirectory);
				return false;
			}
		}
		FileInputStream fis = new FileInputStream(sourceFile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

		ZipEntry entry = null;

		while((entry = zis.getNextEntry()) != null){
			File outputFilename = new File(destinationDirectory, entry.getName());
			String eName = entry.getName();
			if(eName.endsWith("/")){
				createDirIfNeeded(outputFilename);
			}else{
				int count;
				byte data[] = new byte[BUFFER_SIZE];
				FileOutputStream fos = new FileOutputStream(outputFilename);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				while((count = zis.read(data, 0, BUFFER_SIZE)) != -1){
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}

		}

		zis.close();
		return true;
	}

	private static void createDirIfNeeded(File destDirectory){
		if(!destDirectory.exists()){
			destDirectory.mkdirs();
		}
	}

	public static void main(String[]args) throws Exception{
		File f = new File("D:\\workspace\\V3\\GIT\\search\\GammaAtdd\\thriftWorkspace\\gen-dir\\gen-java");
		File[] fs = f.listFiles();
		File to = new File("/ddd.jar");
		zipFiles(fs, to);
		unzipFiles(to, new File("/1"));
	}

}
