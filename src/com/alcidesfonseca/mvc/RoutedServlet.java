package com.alcidesfonseca.mvc;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;

public class RoutedServlet extends HttpServlet {
	String prefix;
	
	public RoutedServlet() throws NamingException {
		prefix = "/";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("get",request,response);
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		route("post",request,response);
    }

	public void route(String method, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	}
	
	public String getPath(HttpServletRequest request) {
		return request.getRequestURI().replaceAll(prefix + "/","");
	}
}