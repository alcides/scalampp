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
	var nsCounter = -1	
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
					Thread.sleep(sd.Config.updateRate * 1000)
				}
			}
		}
		
	}
	def makeUrl:String = {
		nsCounter += 1
		var list = List.fromArray(Naming.list(Config.registryURL)).filter {
			 s => s.contains( Config.namingServerPrefix + "_" )
		}
		if (list.isEmpty) null
		else {
			list( nsCounter % list.length )
		}
	}	

	def getLoadBalancer = {

		while( lb == null || RemoteDatabase.lb == null ) {
			try {
				var url = makeUrl
				if ( url != null) {
					println("Trying to connect to " + url)
					lb = Naming.lookup(url).asInstanceOf[ILoadBalancer]
					RemoteDatabase.lb = lb
				}
			} 
			catch {
				case e : NotBoundException => ()
			}	
		}	
	}
	
	def getInfo = new ServerData(0,
		1 - (Runtime.getRuntime.freeMemory.asInstanceOf[Double] / Runtime.getRuntime.totalMemory.asInstanceOf[Double]),
		0)
	// Dummy data!
	
	def getSessions = SessionManager.exportSessions
	
}
