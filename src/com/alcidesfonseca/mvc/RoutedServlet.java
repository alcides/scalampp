package com.alcidesfonseca.mvc;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;

public class RoutedServlet extends HttpServlet {
	
	public RoutedServlet() throws NamingException {
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("get",request,response);
    }

	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("put",request,response);
    }

	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("delete",request,response);
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String m = request.getParameter("_method");
		if ( m != null ) {
			if ( m.equals("put") ) {
				route("put",request,response);
				return;
			}
			if ( m.equals("delete") ) {
				route("delete",request,response);
				return;
			}
		}
		route("post",request,response);
    }

	public void route(String method, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	}
	
	public String getPath(HttpServletRequest request, String prefix) {
		return request.getRequestURI().replaceAll(prefix + "/","");
	}
}