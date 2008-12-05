package sd.NS

import sd.NS.server._

import java.util.GregorianCalendar
import java.net._
import java.rmi._

class OnlineServer(var serverAddress:InetSocketAddress,var pb:IPingBack, var sData:ServerData) {
	
	override def toString = serverAddress.toString
	
	var lastSeen = new GregorianCalendar().getTime
	
	def updateAlive = {
		lastSeen = new GregorianCalendar().getTime
	}
	
	def ping = {	
		try {
			pb.ping
		} 
		catch {
			case e : RemoteException => false
		}
	}	
}
