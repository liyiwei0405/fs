package test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class TestJetty {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server(80);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new HelloServlet()), "/*");
		context.addServlet(new ServletHolder(new HelloServlet("TYPE1 Request")), "/TYPE1/*");
		context.addServlet(new ServletHolder(new HelloServlet("TYPE2 Request")), "/TYPE2/*");

		server.start();
		System.out.println("jetty start");
		server.join();
	}

//	public static class HelloHandler extends AbstractHandler {
//
//		@Override
//		public void handle(String target, Request baseRequest, HttpServletRequest request,
//				HttpServletResponse response) throws IOException, ServletException {
//
//			response.setContentType("text/html;charset=utf-8");
//			response.setStatus(HttpServletResponse.SC_OK);
//			baseRequest.setHandled(true);
//			response.getWriter().println("</pre><h1>Hello World</h1><pre>");
//		}			
//	}

	public static class HelloServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
		String greeting = "Hello";
		
		public HelloServlet() {
		}

		public HelloServlet(String hi) {
			greeting = hi;
		}

		@Override
		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("</pre><h1>" + greeting + "</h1><pre>");
		}

		@Override
		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			doGet(request, response);
		}
	}
}
