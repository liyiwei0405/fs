package com.funshion.rpcserver;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CounterServlet extends HttpServlet {
	public final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final long serialVersionUID = 1L;
	private static TaskCounterRefresher instance = TaskCounterRefresher.instance;
	private static ObjectMapper objectMapper = new ObjectMapper();

	public CounterServlet() {
	}
	
	public static String writeValueAsString(Object obj) throws JsonProcessingException{
		return objectMapper.writeValueAsString(obj);
	}
	
	public static void writeValue(Object obj ) throws JsonGenerationException, JsonMappingException, IOException{
		objectMapper.writeValue(System.out, obj);
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf8");
		response.setStatus(HttpServletResponse.SC_OK);
		try{
			response.getWriter().println(writeValueAsString(new CounterEntity(instance.getMap(), instance.getVars_long(), instance.getSysInfo(), sdf.format(System.currentTimeMillis()))));
		}catch(Exception e){
			e.printStackTrace();
			response.getWriter().println("Server Error.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}