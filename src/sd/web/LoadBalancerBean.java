package sd.web;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import scala.Array;
import sd.ns.*;

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
	
	
}
