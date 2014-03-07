package com.funshion.gamma.atdd.healthWatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funshion.utils.http.HttpCrawler;

public class JsonParserTest {
	
	public static void main(String[] args) throws IOException {
		String baseUrl = "http://192.168.16.95/configcenter/v1/getRpcConfig?local=healthWatcher&port=8002&push=configNotify&remote=";
		URL url = new URL(baseUrl + "mediaservice");
		System.out.println(url);
		HttpCrawler crl = new HttpCrawler(url);
		crl.connect();
		byte [] bs = crl.getBytes();
		crl.close();
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);

		ObjectMapper objectMapper = new ObjectMapper();    
		RpcLocationsFromConfigCenter retInfo = objectMapper.readValue(bais, RpcLocationsFromConfigCenter.class);
		System.out.println(retInfo);

		
	}
}
