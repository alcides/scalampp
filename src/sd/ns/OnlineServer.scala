package sd.ns

import sd.ns.server._

import java.util.GregorianCalendar
import java.net._
import java.rmi._

@serializable
class OnlineServer(var serverAddress:InetSocketAddress,var pb:IPingBack, var serverData:ServerData) {
	
	override def toString = serverAddress.toString
	
	var lastSeen = new GregorianCalendar().getTime
	var sessions = List[String]()
	
	def updateAlive(sData:ServerData,ses:List[String]) = {
		lastSeen = new GregorianCalendar().getTime
		serverData = sData
		if (sessions != ses) {
			sessions = ses
			true
		} else false
	}
	
	def ping = {	
		try {
			pb.ping
		} 
		catch {
			case e : RemoteException => false
		}
	}
	
	def getCPU = serverData.cpuLoad
	def getMemory = serverData.memoryLoad
	def getNetwork = serverData.networkLoad
	def getClients = sessions.toArray
	
}
