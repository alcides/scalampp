package sd.NS;

import sd.NS.server._
import com.alcidesfonseca.db._


import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._
import scala.collection.mutable.HashMap


@serializable
class LoadBalancer extends UnicastRemoteObject with ILoadBalancer
{
	var serverList:List[OnlineServer] = List()
	
	override def ping:Boolean = true
	
	override def getServer:InetSocketAddress = {
		if (serverList.isEmpty)
			throw new NoServerAvailableException;
		if (sd.Config.debug) println(getAvailableServer + " given")
		getAvailableServer
	}
	
	override def join(w:InetSocketAddress,pb:IPingBack,sData:ServerData):Unit = {
		if ( serverList.count( s => s.serverAddress == w) == 0 ) {
			serverList = serverList.::( new OnlineServer(w,pb,sData) )
			if (sd.Config.debug) println("Servidor " + w.toString + " juntou-se.")
		}
	}
    override def withdraw(w:InetSocketAddress):Unit = {
		serverList = serverList.remove { server => (server.serverAddress == w) }
		if (sd.Config.debug) println("Servidor " + w.toString + " saiu.")
	}
    override def keepAlive(w:InetSocketAddress,sData:ServerData,sessions:List[String]):Boolean = {
		if (sd.Config.debug) println("Servidor " + w.toString + " actualizou.")
		var status = false
		serverList.filter { server => (server.serverAddress == w) }.foreach { server =>
			status = server.updateAlive(sData,sessions)
		}
		if (status) {
			if (sd.Config.debug) println("new user list")
			updateSessions
		}
		serverList.count { server => (server.serverAddress == w) } > 0
	}
	
	private def getAvailableServer = {
		serverList.reduceLeft {
			(x:OnlineServer,y:OnlineServer) => if (calcLoad(x) < calcLoad(y) ) x else y
		}.serverAddress
	}
	
	private def calcLoad(os:OnlineServer):Double = {
		Math.max(os.serverData.cpuLoad,Math.max(os.serverData.networkLoad,os.serverData.memoryLoad))
		os.sessions.length.toDouble
	}
	
	private def updateSessions = {
		serverList.foreach { s =>
			var h = HashMap[String,IPingBack]()
			serverList.filter{ s2 => s2 != s }.foreach { s2 =>
				s2.sessions.foreach { jid =>
					h.update( jid, s2.pb )
				}
			}
			s.pb.updateSessions(h)
		}
	}
	
	def database_update(sql:String) = Database.update(sql)
	def database_getUsers:List[User] = Database.getUsers 
	def database_getFriends(name:String):List[Friend] = Database.getFriends(name)
}