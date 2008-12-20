package sd.web;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

public class CometChat extends HttpServlet implements CometProcessor {

	    protected ArrayList<HttpServletResponse> connections = new ArrayList<HttpServletResponse>();
	    protected MessageSender messageSender = null;

	    public void init() throws ServletException {
	        messageSender = new MessageSender();
	        Thread messageSenderThread = 
	            new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
	        messageSenderThread.setDaemon(true);
	        messageSenderThread.start();
	    }

	    public void destroy() {
	        connections.clear();
	        messageSender.stop();
	        messageSender = null;
	    }

	    /**
	     * Process the given Comet event.
	     * 
	     * @param event The Comet event that will be processed
	     * @throws IOException
	     * @throws ServletException
	     */
	    public void event(CometEvent event)
	        throws IOException, ServletException {
	        HttpServletRequest request = event.getHttpServletRequest();
	        HttpServletResponse response = event.getHttpServletResponse();
	        if (event.getEventType() == CometEvent.EventType.BEGIN) {
	            log("Begin for session: " + request.getSession(true).getId());
	            PrintWriter writer = response.getWriter();
	            writer.println("<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">");
	            writer.println("<head><title>JSP Chat</title></head><body bgcolor=\"#FFFFFF\">");
	            writer.flush();
	            synchronized(connections) {
	                connections.add(response);
	            }
	        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
	            log("Error for session: " + request.getSession(true).getId());
	            synchronized(connections) {
	                connections.remove(response);
	            }
	            event.close();
	        } else if (event.getEventType() == CometEvent.EventType.END) {
	            log("End for session: " + request.getSession(true).getId());
	            synchronized(connections) {
	                connections.remove(response);
	            }
	            PrintWriter writer = response.getWriter();
	            writer.println("</body></html>");
	            event.close();
	        } else if (event.getEventType() == CometEvent.EventType.READ) {
	            InputStream is = request.getInputStream();
	            byte[] buf = new byte[512];
	            do {
	                int n = is.read(buf); //can throw an IOException
	                if (n > 0) {
	                    log("Read " + n + " bytes: " + new String(buf, 0, n) 
	                            + " for session: " + request.getSession(true).getId());
	                } else if (n < 0) {
	                    return;
	                }
	            } while (is.available() > 0);
	        }
	    }

	    public class MessageSender implements Runnable {

	        protected boolean running = true;
	        protected ArrayList<String> messages = new ArrayList<String>();

	        public MessageSender() {
	        }

	        public void stop() {
	            running = false;
	        }

	        /**
	         * Add message for sending.
	         */
	        public void send(String user, String message) {
	            synchronized (messages) {
	                messages.add("[" + user + "]: " + message);
	                messages.notify();
	            }
	        }

	        public void run() {

	            while (running) {

	                if (messages.size() == 0) {
	                    try {
	                        synchronized (messages) {
	                            messages.wait();
	                        }
	                    } catch (InterruptedException e) {
	                        // Ignore
	                    }
	                }

	                synchronized (connections) {
	                    String[] pendingMessages = null;
	                    synchronized (messages) {
	                        pendingMessages = messages.toArray(new String[0]);
	                        messages.clear();
	                    }
	                    // Send any pending message on all the open connections
	                    for (int i = 0; i < connections.size(); i++) {
	                        try {
	                            PrintWriter writer = connections.get(i).getWriter();
	                            for (int j = 0; j < pendingMessages.length; j++) {
	                                writer.println(pendingMessages[j] + "<br>");
	                            }
	                            writer.flush();
	                        } catch (IOException e) {
	                            log("IOExeption sending message", e);
	                        }
	                    }
	                }

	            }

	        }

	    }

	}
