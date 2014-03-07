package com.funshion.rpcserver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funshion.rpcserver.TaskCounterRefresher.ConfigFile;

public class VarsServlet  extends HttpServlet{
	public final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	class SysConfigs{
		public Map<String, String>vars;
		public List<ConfigFile>configs;
		public Map<String, String>sysProp;
		public String currentTime;
	}

	private static final long serialVersionUID = 1L;
	private static TaskCounterRefresher instance = TaskCounterRefresher.instance;
	private static ObjectMapper objectMapper = new ObjectMapper();

	public VarsServlet() {
	}

	protected String jsonStr() throws ServletException, IOException {
		SysConfigs conf = new SysConfigs();
		conf.vars = instance.vars_string;
		conf.configs = instance.getConfigs();
		Map<String, String>pairs = new HashMap<String, String>();
		java.util.Properties props = System.getProperties();
		Set<Entry<Object, Object>> en = props.entrySet();
		for(Entry<Object, Object> e : en){
			pairs.put(e.getKey().toString(), e.getValue().toString());
		}
		conf.sysProp = pairs;
		
		objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		conf.currentTime = sdf.format(System.currentTimeMillis());
		return objectMapper.writeValueAsString(conf);
	}
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf8");
		response.setStatus(HttpServletResponse.SC_OK);
		try{
			response.getWriter().println(jsonStr());
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
	public static void main(String[] args) throws Exception {
		instance.init(8002, null, new File("./config").listFiles());
		VarsServlet sev = new VarsServlet();
		System.out.println(sev.jsonStr());

	}
}
