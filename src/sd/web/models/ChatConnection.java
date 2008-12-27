package sd.web.models;

import sd.ns.Connector;
import sd.ns.ILoadBalancer;
import sd.tcp.*;
import com.alcidesfonseca.xmpp.*;
import pt.uc.dei.sd.SecurityHelper;

import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.json.*;


public class ChatConnection extends Object{
	
	WebHumanChannel hc = null;
	Socket s = null;
	OutChannel out = null;
	ClientSession session = null;
	String host = "";
	int port = 0;
	Thread listener = null;
	GregorianCalendar lastSeen = null;
	
	public ChatConnection () {
		hc = new WebHumanChannel();
		ping();
	}
	
	public void ping() {
		lastSeen = new GregorianCalendar();
	}
	
	public void close() {
		listener.interrupt();
		listener = null;
		
		out.write( XMLStrings.presence_unavailable(session.getJID()) );
		out.write( XMLStrings.stream_end() );
		out.close();
	}
	
	public boolean checkAndClose() {
		
		GregorianCalendar acceptableTime = new GregorianCalendar();
		acceptableTime.add(Calendar.SECOND, -1 * sd.Config.getWebTimeOut());
		
		if ( !hc.isOpen() || lastSeen.getTime().compareTo(acceptableTime.getTime()) < 0 ) {
			close();
			return true;
		}
		
		return false;
	}
	
	public boolean connect(String u, String p, InetSocketAddress sa) throws java.rmi.RemoteException, sd.ns.NoServerAvailableException {
		port = sa.getPort();
		host = sa.getHostName();
		try {
			s = new Socket(host,port);
			if (host.equals("jabber.org")) {
				s = changeToTLS(s,host,port);
			}
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
	
	public boolean connect(String u, String p) throws java.rmi.RemoteException, sd.ns.NoServerAvailableException {
		if (session != null) {
			hc = new WebHumanChannel();
		}
	
		ILoadBalancer lb = Connector.getLoadBalancer();
		InetSocketAddress sa = lb.getServer();
		return connect(u,p,sa);
	}
	
	public void sendMessage(String to, String content) {
		out.write(XMLStrings.message_chat(to,content));
	}

	public void sendPresence(String s) {
		out.write(XMLStrings.presence_set(s));
	}
	
	public void rosterAdd(String jid) {
		out.write( XMLStrings.roster_item_request( session.getStanzaId() , jid ) );
		out.write( XMLStrings.presence_subscribe( jid, session.getJID()) );
	}

	public void rosterDel(String jid) {
		out.write( XMLStrings.roster_item_remove( session.getStanzaId() ,jid) );
		out.write( XMLStrings.presence_unsubscribe( jid, session.getJID()) );
	}
	
	public void rosterRequest() {
		out.write(XMLStrings.roster_request(session.getStanzaId()));
	}
	
	public void rosterAccept(String jid, Boolean a) {
		if ( a ) {
			out.write(XMLStrings.presence_subscribed(jid,session.getJID()));
		} else {
			out.write(XMLStrings.presence_unsubscribed(jid,session.getJID()));	
		}
	}

	public JSONArray retrieveMessages() {
		return hc.retrieveMessages();
	}
	
	public JSONArray retrieveRoster() {
		return hc.retrieveRoster();
	}
	
	public JSONArray retrieveRequests() {
		return hc.getAndCleanRequests();
	}
	
	public int checkLogin() {
		if (hc == null || session == null) return 0;
		return session.isLogged();
	}
	
	public String getJid() {
		return session.getJID();
	}
	
	private Socket changeToTLS(Socket s,String host, int port) {
		SocketOutChannel out = new SocketOutChannel(s);
		int r = 0;
		
		try	{	
			BufferedReader in = new BufferedReader( new InputStreamReader( s.getInputStream() ));
	
			out.write(XMLStrings.stream_start_to(host));
			out.write(XMLStrings.start_tls());
	
			XMLParser parser = new XMLParser(new XMPPEmptyParser());
	
			while ( !parser.getTemp().contains("<proceed") ) {
				r = in.read();
				parser.parseInt(r);
			}
			
			try {
				return SecurityHelper.executeTLSNegotiation(s,host,port);}
			catch ( java.security.NoSuchAlgorithmException e) { 
				return s;
			}
			catch ( java.security.KeyManagementException e) { 
				return s;
			}
			
		} catch (IOException e) {
			return s;
		}
		
	}
	
}
