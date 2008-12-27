package sd.web.models;

import sd.ns.Connector;
import sd.ns.ILoadBalancer;
import sd.tcp.*;
import com.alcidesfonseca.xmpp.*;
import java.net.*;
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
	
	public boolean connect(String u, String p) throws java.rmi.RemoteException, sd.ns.NoServerAvailableException {
		if (session != null) {
			hc = new WebHumanChannel();
		}
	
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

	public JSONArray retrieveMessages() {
		return hc.retrieveMessages();
	}
	
	public JSONArray retrieveRoster() {
		return hc.retrieveRoster();
	}
	
	public int checkLogin() {
		if (hc == null || session == null) return 0;
		return session.isLogged();
	}
	
	public String getJid() {
		return session.getJID();
	}
	
}
