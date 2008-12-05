package sd.NS.server;

import sd.NS._;

import java.lang.{Runnable,Thread}
import java.rmi._
import java.rmi.registry._
import java.net._


class UpdateNSThread(var host:String, var port:Int) extends Thread {
	var lb:ILoadBalancer = null
	var socketAddress = new InetSocketAddress(host,port)
	
	override def run() = {
		getLoadBalancer
		lb.join(socketAddress, new PingBack, getInfo)
		while (true) {
			Thread.sleep(sd.Config.updateRate*1000)
			if (!lb.keepAlive(socketAddress,getInfo)) {
				lb.join(socketAddress, new PingBack, getInfo)
			}
		}
	}
	
	def getLoadBalancer = {
		while( lb == null) {
			try {
				lb = Naming.lookup("//localhost/lb1").asInstanceOf[ILoadBalancer]
			} 
			catch {
				case e : java.rmi.NotBoundException => {}
			}	
		}	
	}
	
	def getInfo = new ServerData(100,Runtime.getRuntime.freeMemory.asInstanceOf[Int],50)
	# Dummy data!
	
}
