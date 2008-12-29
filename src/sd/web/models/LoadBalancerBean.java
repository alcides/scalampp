package sd.web.models;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import scala.Array;
import sd.ns.*;
import sd.ns.server.*;
import com.alcidesfonseca.db.User;

import java.net.*;

public class LoadBalancerBean extends Object
{
	private ILoadBalancer _lb;
	
	public LoadBalancerBean() {
		connect();
	}
	
	private void connect() {
		
		do {
			_lb = (ILoadBalancer) Connector.getLoadBalancer();			
		} while ( _lb == null);
		
	}
	
	public OnlineServer[] getServers() {
		try {
			return _lb.getServers();
		} catch ( RemoteException e) { 
			connect();
			
			OnlineServer[] a = new OnlineServer[1];
			a[0] = new OnlineServer(new InetSocketAddress("localhost",5222),null,new ServerData(1,2,3));
			return a;
			
			//return new OnlineServer[0];
		}
	}

	public User[] getAccounts() {
		try {
			return _lb.getAccounts();
		} catch ( RemoteException e) { 
			connect();
			return new User[0];
		}
	}	
	
	public void setPassword(String u,String p) {
		try {
			_lb.setPassword(u,p);
		} catch ( RemoteException e) { 
			connect();
			return;
		}
	}
	
	public void createUser(String u,String p) {
		try {
			_lb.createUser(u,p);
		} catch ( RemoteException e) { 
			connect();
			return;
		}
	}
	
	public void deleteUser(String u) {
		try {
			_lb.deleteUser(u);
		} catch ( RemoteException e) { 
			connect();
			return;
		}
	}
	
	public String getNamingService() {
		return _lb.toString();
	}
	
}
