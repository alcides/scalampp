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
				view_post_login(request,out,conn);
			else 
				view_get_login(request,out,conn);
		} else if ( req.equals("messages")) {
			if (method.equals("post")) {
				view_post_messages(request,out,conn);
			} else {
				view_get_messages(request,out,conn);
			}
		} else if ( req.equals("status") && method.equals("post")) {
			view_post_status(request,out,conn);
		} else if ( req.equals("roster") && method.equals("get")) {
			view_get_roster(request,out,conn);
		} else if ( req.equals("updates") && method.equals("get")) {
			view_get_updates(request,out,conn);
		} else {
			out.println("Hello");
		}
	}

	private void view_post_login(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		HttpSession s = request.getSession(true);
		
		String u = request.getParameter("user");
		String p = request.getParameter("pwd");
		
		if ( u.length() + p.length() > 0 ) {
		
		try	{
			if (con.connect(u,p)) {
				out.println(new JsonMessage("ok","Connection started"));
			} else {
				out.println(new JsonMessage("error","Error in connection"));
			}
		} catch (sd.ns.NoServerAvailableException e) {
			out.println(new JsonMessage("error","No Server Available"));
		} catch (java.rmi.RemoteException e) {
			
		}
	} else {
		out.println(new JsonMessage("error","Data cannot be blank"));
	}

	}
	
	public void view_post_messages(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String to = request.getParameter("to");
		String what = request.getParameter("content");		
		if ( to.length() + what.length() > 0) {
			con.sendMessage(to,what);
			out.println(new JsonMessage("ok","Message Sent"));
		} else {
			out.println(new JsonMessage("error","Can't be blank."));
		}
	}
	
	public void view_post_status(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String sta = request.getParameter("presence");		
		if (sta != null && con != null) {
			con.sendPresence(sta);
			out.println(new JsonMessage("ok","Status updated"));
		} else {
			out.println(new JsonMessage("error","Can't be blank."));
		}
	}

	public void view_get_messages(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		try {
			JSONObject j = new JSONObject();
			j.put("status","ok");
			j.put("messages",con.retrieveMessages());
			out.println(j);
		} catch (JSONException e) {
			out.println(new JsonMessage("error","Weird error."));
		}
	}
	
	public void view_get_roster(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		try {
			JSONObject j = new JSONObject();
			j.put("status","ok");
			j.put("roster",con.retrieveRoster());
			j.put("myJid",con.getJid());
			out.println(j);
		} catch (JSONException e) {}
	}
	
	public void view_get_login(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		int i = con.checkLogin();
		if ( i == 0) out.println(new JsonMessage("wait","Please wait"));
		if ( i < 0) out.println(new JsonMessage("fail","Wrong Login"));
		if ( i > 0) out.println(new JsonMessage("ok","logged"));		
	}
	
	public void view_get_updates(HttpServletRequest request, PrintWriter out, ChatConnection con) {
			try {
				JSONObject j = new JSONObject();
				j.put("status","ok");
				j.put("messages",con.retrieveMessages());
				j.put("roster",con.retrieveRoster());
				j.put("myJid",con.getJid());
				out.println(j);
			} catch (JSONException e) {
				out.println(new JsonMessage("error","Weird error."));
			}
	}
}