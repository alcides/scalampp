package sd.web;

import sd.ns.Connector;
import sd.ns.ILoadBalancer;
import sd.tcp.*;
import com.alcidesfonseca.xmpp.*;
import java.net.*;

public class ChatConnection extends Object{
	
	WebHumanChannel hc = null;
	Socket s = null;
	OutChannel out = null;
	ClientSession session = null;
	String host = "";
	int port = 0;
	Thread listener = null;
	
	ChatConnection () {
		hc = new WebHumanChannel();
	}
	
	public boolean connect(String u, String p) throws java.rmi.RemoteException, sd.ns.NoServerAvailableException {
		ILoadBalancer lb = Connector.getLoadBalancer();
		InetSocketAddress sa = lb.getServer();
		port = sa.getPort();
		host = sa.getHostName();
		try {
			s = new Socket(host,port);
		} catch (java.net.UnknownHostException e) {
			return false;
		} catch (java.io.IOException e) {
			return false;
		}
		out = new SocketOutChannel(s);
		session = new ClientSession(host,out);	
		session.setLogin(u,p);
		
		listener = new TCPClientListener(s,session,hc);
		listener.start();

		// starts to send		
		out.write( XMLStrings.stream_start_to(host) );
		return true;
	}
	
	public int checkMessages() {
		return hc.messageNumber();
	}
	
	public boolean checkLogin() {
		return hc.isOpen() && session.isLogged();
	}
	
}