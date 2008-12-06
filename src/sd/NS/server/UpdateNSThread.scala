package sd.NS.server;

import sd.NS._;
import com.alcidesfonseca.db._
import com.alcidesfonseca.xmpp._

import java.lang.{Runnable,Thread}
import java.rmi._
import java.rmi.registry._
import java.net._


class UpdateNSThread(var host:String, var port:Int) extends Thread {
	var lb:ILoadBalancer = null
	var socketAddress = new InetSocketAddress(host,port)
	
	override def run() = {
		while (true) {
			try {
				getLoadBalancer
				lb.join(socketAddress, new PingBack, getInfo)
				while (true) {
					Thread.sleep(sd.Config.updateRate*1000)
					if (!lb.keepAlive(socketAddress,getInfo,getSessions)) {
						lb.join(socketAddress, new PingBack, getInfo)
					}
				}
			} 
			catch {
				case e : java.rmi.ConnectException => {
					Thread.sleep(5000)
				}
			}
		}
		
	}
	
	def getLoadBalancer = {
		while( lb == null) {
			try {
				lb = Naming.lookup("//localhost/lb1").asInstanceOf[ILoadBalancer]
				RemoteDatabase.lb = lb
			} 
			catch {
				case e : java.rmi.NotBoundException => {}
			}	
		}	
	}
	
	def getInfo = new ServerData(1,
		Runtime.getRuntime.freeMemory.asInstanceOf[Double] / Runtime.getRuntime.totalMemory.asInstanceOf[Double],
		1)
	// Dummy data!
	
	def getSessions = SessionManager.exportSessions
	
}
