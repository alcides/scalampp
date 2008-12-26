package sd.web;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import scala.Array;
import sd.ns.*;
import com.alcidesfonseca.db.User;

public class LoadBalancerBean extends Object
{
	private ILoadBalancer _lb;
	
	public LoadBalancerBean() {
		_lb = (ILoadBalancer) Connector.getLoadBalancer();
	}
	
	protected ILoadBalancer getLoadBalancer() {
		return _lb;
	}
	
	public OnlineServer[] getServers() {
		try {
			return _lb.getServers();
		} catch ( RemoteException e) { 
			return new OnlineServer[0];
		}
	}

	public User[] getAccounts() {
		try {
			return _lb.getAccounts();
		} catch ( RemoteException e) { 
			return new User[0];
		}
	}	
	
	public void setPassword(String u,String p) {
		try {
			_lb.setPassword(u,p);
		} catch ( RemoteException e) { 
			return;
		}
	}
	
	public void createUser(String u,String p) {
		try {
			_lb.createUser(u,p);
		} catch ( RemoteException e) { 
			return;
		}
	}
	
	public void deleteUser(String u) {
		try {
			_lb.deleteUser(u);
		} catch ( RemoteException e) { 
			return;
		}
	}
	
}
