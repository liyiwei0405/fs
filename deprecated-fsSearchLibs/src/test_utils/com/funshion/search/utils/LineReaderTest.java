package com.funshion.search.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LineReaderTest {
	static private LineReader lr;
	static private File file;
	static FileOutputStream fos;

	@BeforeClass
	public static void init() throws IOException{
		file= new File("testFile");
		file.createNewFile();
		fos = new FileOutputStream(file, true);
		lr = new LineReader(file);

	}
	
	@Test
	public void testGetPathAndName() throws IOException{
		String path = lr.getPath();
		assertEquals("getPath wrong", file.getAbsolutePath(), path);
		String fileName = lr.getFileName();
		assertEquals("getFileName wrong", file.getName(), fileName);
	}
	
	@Test
	public void testReadFile() throws IOException{
		String line = "abcdef";
		fos.write(line.getBytes());
		fos.flush();
		
		assertEquals("readLine wrong", line, lr.readLine());
		assertFalse(lr.hasNext());
	}
	
	@AfterClass
	public static void destroy() throws InterruptedException, IOException{
		fos.close();
		lr.close();
		System.out.println(file.delete());
	}
}
