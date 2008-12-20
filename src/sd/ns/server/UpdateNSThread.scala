package sd.ns.server;

import sd.ns._;
import com.alcidesfonseca.db._
import com.alcidesfonseca.xmpp._

import java.lang.{Runnable,Thread}
import java.rmi._
import java.rmi.registry._
import java.net._


class UpdateNSThread(var host:String, var port:Int) extends Thread {
	var lb:ILoadBalancer = null
	var socketAddress = new InetSocketAddress(host,port)
	var updating = true
	
	override def run() = {
		while (true) {
			try {
				getLoadBalancer
				lb.join(socketAddress, new PingBack, getInfo)
				while (true) {
					Thread.sleep(sd.Config.updateRate*1000)
					synchronized {
						if (updating) {
							if (!lb.keepAlive(socketAddress,getInfo,getSessions)) {
								lb.join(socketAddress, new PingBack, getInfo)
							}
						}
					}
				}
			} 
			catch {
				case e :java.rmi.ConnectException => {
					lb = null
					Thread.sleep(sd.Config.updateRate * 1000)
				}
				case e: RemoteException => {
					lb = null
					Thread.sleep(sd.Config.updateRate * 1000)
				}
				case e : NotBoundException => {
					lb = null
				}
			}
		}
		
	}
	
	def pauseOrResume = synchronized {
		updating = !updating
	}

	def getLoadBalancer = {
		while( lb == null || RemoteDatabase.lb == null ) {
			lb = Connector.getLoadBalancer
			RemoteDatabase.lb = lb
		}	
	}
	
	def getInfo = new ServerData(0,
		1 - (Runtime.getRuntime.freeMemory.asInstanceOf[Double] / Runtime.getRuntime.totalMemory.asInstanceOf[Double]),
		0)
	// Dummy data!
	
	def getSessions = SessionManager.exportSessions
	
}
