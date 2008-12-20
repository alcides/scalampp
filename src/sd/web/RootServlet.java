package sd.web;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;


public class RootServlet extends HttpServlet {
	
	static String prefix = "/chat";
	
    public RootServlet() throws NamingException {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		HttpSession s = request.getSession(true);
		if ( s.getAttribute("conn") == null) s.setAttribute("conn", new ChatConnection() );
	
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

		String req = request.getRequestURI().replaceAll(prefix + "/","");
		if ( req.equals("login") ) login(request,out);
		else out.println("Hello");
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request,response);
    }

	private void login(HttpServletRequest request, PrintWriter out) {
		HttpSession s = request.getSession(true);
		ChatConnection con = (ChatConnection)s.getAttribute("conn");
		
		String u = request.getParameter("user");
		String p = request.getParameter("pwd");
		
		try	{
			if (con.connect(u,p)) {
				out.println("{status:'ok', message:'logged in'}");
			} else {
				out.println("{ status:'error', message:'Wrong login.' }");
			}
		} catch (sd.ns.NoServerAvailableException e) {
			out.println("{ status:'error', message:'No server available' }");
		} catch (java.rmi.RemoteException e) {
			out.println("{ status:'error', message:'error in connection' }");
		}		

	}

}