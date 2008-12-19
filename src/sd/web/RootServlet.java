package sd.WEB;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.rmi.*;


public class RootServlet extends HttpServlet {

    public RootServlet() throws NamingException
    {
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hola</title>");
        out.println("</head>");
        out.println("<body bgcolor=\"white\">");

        out.println("<h1> HelloWorldEJB Says: </h1>");

        out.println("</body>");
        out.println("</html>");
    }
}