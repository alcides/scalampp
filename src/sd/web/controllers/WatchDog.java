package sd.web.controllers;

import java.lang.Thread;
import java.util.Hashtable;
import java.util.Enumeration;

import sd.web.models.*;

public class WatchDog extends Thread
{
	Hashtable<String,ChatConnection> conns;
	
	WatchDog (Hashtable<String,ChatConnection> connections) {
		conns = connections;
	}
	
	public void run() {
		ChatConnection temp = null;
		String key = null;
		while (true) {
			try {
				Thread.sleep(10 * 1000);
			}
			catch ( InterruptedException e) { 
				e.printStackTrace();
			}

			for (Enumeration e = conns.keys(); e.hasMoreElements();) {
				key = (String) e.nextElement();
				temp = conns.get(key);
				if (temp.checkAndClose()) {
					conns.remove(key);
				}
			}
			
		}
	}
	
}
