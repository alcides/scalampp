package sd.web;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;

import java.util.Hashtable;

public class RootServlet extends HttpServlet {
	
	static String prefix = "/chat";
	
	private Hashtable<String,ChatConnection> conns;
	
    public RootServlet() throws NamingException {
    }

	public void init() {
		conns = new Hashtable<String,ChatConnection>();
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		HttpSession s = request.getSession(true);
		ChatConnection conn = conns.get(s.getId());
		if ( conn == null ) {
			conn = new ChatConnection();
			conns.put(s.getId(), conn);
		}
	
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

		String req = request.getRequestURI().replaceAll(prefix + "/","");
		if ( req.equals("login") ) login(request,out,conn);
		else if ( req.equals("check") ) checkMessages(request,out,conn);
		else if ( req.equals("check_login") ) checkLogin(request,out,conn);
		else out.println("Hello");
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request,response);
    }

	private void login(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		HttpSession s = request.getSession(true);
		
		String u = request.getParameter("user");
		String p = request.getParameter("pwd");
		
		try	{
			if (con.connect(u,p)) {
				out.println("{ status:'ok', message:'Connection started.'}");
			} else {
				out.println("{ status:'error', message:'Error in connection.' }");
			}
		} catch (sd.ns.NoServerAvailableException e) {
			out.println("{ status:'error', message:'No server available' }");
		} catch (java.rmi.RemoteException e) {
			out.println("{ status:'error', message:'Error in connection' }");
		}		

	}


	public void checkMessages(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		out.println( con.checkMessages() );
	}
	
	public void checkLogin(HttpServletRequest request, PrintWriter out, ChatConnection con) {
		out.println( con.checkLogin() );
	}
}