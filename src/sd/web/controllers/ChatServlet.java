package sd.web.controllers;

import java.io.*;
import java.net.InetSocketAddress;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;

import java.util.Hashtable;
import org.json.*;
import com.alcidesfonseca.mvc.RoutedServlet;
import sd.web.models.*;
import sd.web.templates.JSONMessage;

public class ChatServlet extends RoutedServlet {
	
	static String prefix = "/chat";
	
	private Hashtable<String,ChatConnection> conns;

	public ChatServlet() throws NamingException { 
		super(); 
	}

	public void init() {
		conns = new Hashtable<String,ChatConnection>();
		Thread wd = new WatchDog(conns);
		wd.start();
	}
	
	public void route(String method, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession s = request.getSession(true);
		ChatConnection conn = conns.get(s.getId());
		if ( conn == null ) {
			conn = new ChatConnection();
			conns.put(s.getId(), conn);
		}
		
		conn.ping();
	
        response.setContentType("application/x-json");
        PrintWriter out = response.getWriter();

		String req = getPath(request,prefix);
		if ( req.equals("login") ) {
			if (method.equals("post"))
				view_post_login(request,out,conn);
			else 
				view_get_login(request,out,conn);
		} else if ( req.equals("messages")) {
			if (method.equals("put")) {
				view_put_messages(request,out,conn);
			} else {
				view_get_messages(request,out,conn);
			}
		} else if ( req.equals("status") && method.equals("post")) {
			view_post_status(request,out,conn);
		} else if ( req.equals("roster")) {
			if (method.equals("get")) {
				view_get_roster(request,out,conn);
			} else if ( method.equals("put")) {
				view_put_roster(request,out,conn);
			} else if ( method.equals("delete")) {
				view_delete_roster(request,out,conn);
			} else if ( method.equals("post")) {
				view_post_roster(request,out,conn);
			}
		} else if ( req.equals("updates") && method.equals("get")) {
			view_get_updates(request,out,conn);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,"Page not found.");
		}
	}

	private void view_post_login(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		HttpSession s = request.getSession(true);
		boolean r;
		
		String u = request.getParameter("user");
		String p = request.getParameter("pwd");
		String serv = request.getParameter("server");
		
		
		if ( u.length() + p.length() > 0 ) {
		
			try	{
				
				if ( serv.equals("") ) {
					r = con.connect(u,p);
				} else {
					r = con.connect(u,p,new InetSocketAddress("jabber.org",5222));
				}
				
				if (r) {
					out.println(new JSONMessage("ok","Connection started"));
				} else {
					out.println(new JSONMessage("error","Error in connection"));
				}
				
			} catch (sd.ns.NoServerAvailableException e) {
				out.println(new JSONMessage("error","No Server Available"));
			} catch (java.rmi.RemoteException e) {
				out.println(new JSONMessage("error","Error in Connection"));			
			}
		} else {
			out.println(new JSONMessage("error","Data cannot be blank"));
		}

	}
	
	public void view_put_messages(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String to = request.getParameter("to");
		String what = request.getParameter("content");		
		if ( to != null && what != null) {
			con.sendMessage(to,what);
			out.println(new JSONMessage("ok","Message Sent"));
		} else {
			out.println(new JSONMessage("error","Can't be blank."));
		}
	}
	
	public void view_post_status(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String sta = request.getParameter("presence");		
		if (sta != null) {
			con.sendPresence(sta);
			out.println(new JSONMessage("ok","Status updated"));
		} else {
			out.println(new JSONMessage("error","Can't be blank."));
		}
	}
	
	public void view_put_roster(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String jid = request.getParameter("jid");
		if ( jid != null) {
			con.rosterAdd(jid);
			out.println(new JSONMessage("ok","Request Sent"));
		} else {
			out.println(new JSONMessage("error","Can't be blank."));
		}
	}
	
	public void view_post_roster(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String acc = request.getParameter("accepted");
		String jid = request.getParameter("jid");
		if ( acc != null && jid != null) {
			con.rosterAccept(jid, acc.equals("true") );
			out.println(new JSONMessage("ok","Request accepted"));
		} else {
			out.println(new JSONMessage("error","Can't be blank."));
		}
	}
	
	public void view_delete_roster(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		String jid = request.getParameter("jid");
		if ( jid != null) {
			con.rosterDel(jid);
			out.println(new JSONMessage("ok","Request Sent"));
		} else {
			out.println(new JSONMessage("error","Can't be blank."));
		}
	}

	public void view_get_messages(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		try {
			JSONObject j = new JSONObject();
			j.put("status","ok");
			j.put("messages",con.retrieveMessages());
			out.println(j);
		} catch (JSONException e) {
			out.println(new JSONMessage("error","Weird error."));
		}
	}
	
	public void view_get_roster(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		try {
			JSONObject j = new JSONObject();
			j.put("status","ok");
			j.put("roster",con.retrieveRoster());
			j.put("myJid",con.getJid());
			out.println(j);
		} catch (JSONException e) {
			out.println(new JSONMessage("error","Weird error."));
		}
	}
	
	public void view_get_login(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		int i = con.checkLogin();
		if ( i == 0) out.println(new JSONMessage("wait","Please wait"));
		if ( i < 0) out.println(new JSONMessage("fail","Wrong Login"));
		if ( i > 0) out.println(new JSONMessage("ok","logged"));		
	}
	
	public void view_get_updates(HttpServletRequest request, PrintWriter out, ChatConnection con) {
			try {
				JSONObject j = new JSONObject();
				j.put("status","ok");
				j.put("messages",con.retrieveMessages());
				j.put("roster",con.retrieveRoster());
				j.put("requests",con.retrieveRequests());
				j.put("myJid",con.getJid());
				out.println(j);
			} catch (JSONException e) {
				out.println(new JSONMessage("error","Weird error."));
			}
	}
}