package sd.web;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;

import java.util.Hashtable;
import org.json.*;

public class RootServlet extends HttpServlet {
	
	static String prefix = "/chat";
	
	private Hashtable<String,ChatConnection> conns;
	
    public RootServlet() throws NamingException {
    }

	public void init() {
		conns = new Hashtable<String,ChatConnection>();
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("get",request,response);
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("post",request,response);
    }


	public void route(String method, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession s = request.getSession(true);
		ChatConnection conn = conns.get(s.getId());
		if ( conn == null ) {
			conn = new ChatConnection();
			conns.put(s.getId(), conn);
		}
	
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

		String req = request.getRequestURI().replaceAll(prefix + "/","");
		if ( req.equals("login") ) {
			if (method.equals("post"))
				login(request,out,conn);
			else 
				checkLogin(request,out,conn);
		} else if ( req.equals("messages") && method.equals("get")) {
			checkMessages(request,out,conn);
		} else {
			out.println("Hello");
		}
	}

	private void login(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		HttpSession s = request.getSession(true);
		
		String u = request.getParameter("user");
		String p = request.getParameter("pwd");
		
		try	{
			if (con.connect(u,p)) {
				out.println(new JsonMessage("ok","Connection started"));
			} else {
				out.println(new JsonMessage("error","Error in connection"));
			}
		} catch (sd.ns.NoServerAvailableException e) {
			out.println(new JsonMessage("error","No Server Available"));
		} catch (java.rmi.RemoteException e) {
			out.println(new JsonMessage("error","Error in connection"));
		}		

	}


	public void checkMessages(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		try {
			JSONObject j = new JSONObject();
			j.accumulate("status","ok");
			j.accumulate("messages",con.retrieveMessages());
			out.println(j);
		} catch (JSONException e) {}
	}
	
	public void checkLogin(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		int i = con.checkLogin();
		if ( i == 0) out.println(new JsonMessage("wait","Please wait"));
		if ( i < 0) out.println(new JsonMessage("fail","Wrong Login"));
		if ( i > 0) out.println(new JsonMessage("ok","logged"));		
	}
}