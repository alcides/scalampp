package sd.web.controllers;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;

import java.util.Hashtable;
import org.json.*;
import com.alcidesfonseca.mvc.RoutedServlet;
import com.alcidesfonseca.db.User;
import sd.ns.*;
import sd.web.models.*;

public class AdminServlet extends RoutedServlet {
	
	static String prefix = "/admin";

	static LoadBalancerBean lbb;

	public AdminServlet() throws NamingException { 
		super(); 
	}

	public void init() {
		lbb = new LoadBalancerBean();
	}
	
	public void route(String method, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
        response.setContentType("application/x-json");
        PrintWriter out = response.getWriter();

		String req = getPath(request,prefix);
		if ( req.equals("servers") && method.equals("get") ) {
			view_get_servers(request,out);
		} else if ( req.equals("accounts")) { 
			if ( method.equals("get") ) {
				view_get_accounts(request,out);
			} else if ( method.equals("post") ) {
				view_post_accounts(request,out);
			} else if ( method.equals("put") ) {
				view_put_accounts(request,out);
			} else if ( method.equals("delete") ) {
				view_delete_accounts(request,out);
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,"Page not found.");
		}
	}

	private void view_get_servers(HttpServletRequest request, PrintWriter out) {
		try {
			JSONObject j = new JSONObject();
			j.put("status","ok");
			JSONArray a = new JSONArray();
			OnlineServer[] ar = lbb.getServers();
			for(int i = 0; i<ar.length; i++ ) {
				JSONObject o = new JSONObject();
				o.put("address", ar[i].toString());
				o.put("cpu", ar[i].getCPU());
				o.put("memory", ar[i].getMemory());
				o.put("network", ar[i].getNetwork());
				o.put("jids",ar[i].getClients());
				a.put(o);
			}
			j.put("servers",a);
			out.println(j);
		} catch (JSONException e) {
			out.println(new JsonMessage("error","Weird error."));
		}
	}
	
	private void view_get_accounts(HttpServletRequest request, PrintWriter out) {
		try {
			JSONObject j = new JSONObject();
			j.put("status","ok");
			JSONArray a = new JSONArray();
			User[] ar = lbb.getAccounts();
			for(int i = 0; i<ar.length; i++ ) {
				a.put(ar[i].toString());
			}
			j.put("accounts",a);
			out.println(j);
		} catch (JSONException e) {
			out.println(new JsonMessage("error","Weird error."));
		}
	}
	
	private void view_post_accounts(HttpServletRequest request, PrintWriter out) {
		String uname = request.getParameter("username");
		String pwd = request.getParameter("password");
		if ( uname != null && pwd != null) {
			lbb.setPassword(uname,pwd);
			out.println(new JsonMessage("ok","Password changed."));
		} else {
			out.println(new JsonMessage("error","Can't be blank."));
		}
	}
	private void view_put_accounts(HttpServletRequest request, PrintWriter out) {
		String uname = request.getParameter("username");
		String pwd = request.getParameter("password");
		if ( uname != null && pwd != null) {
			lbb.createUser(uname,pwd);
			out.println(new JsonMessage("ok","User Created."));
		} else {
			out.println(new JsonMessage("error","Can't be blank."));
		}
	}
	private void view_delete_accounts(HttpServletRequest request, PrintWriter out) {
		String uname = request.getParameter("username");
		if ( uname != null) {
			lbb.deleteUser(uname);
			out.println(new JsonMessage("ok","User Deleted"));
		} else {
			out.println(new JsonMessage("error","Can't be blank."));
		}
	}
}