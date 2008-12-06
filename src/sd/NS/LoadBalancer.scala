package sd.NS;

import sd.NS.server._
import com.alcidesfonseca.db._


import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._

@serializable
class LoadBalancer extends UnicastRemoteObject with ILoadBalancer
{
	var serverList:List[OnlineServer] = List()
	
	override def getServer:InetSocketAddress = {
		if (serverList.isEmpty)
			throw new NoServerAvailableException;
		getAvailableServer
	}
	
	override def join(w:InetSocketAddress,pb:IPingBack,sData:ServerData):Unit = {
		if ( serverList.count( s => s.serverAddress == w) == 0 ) {
			serverList = serverList.::( new OnlineServer(w,pb,sData) )
			println("Servidor " + w.toString + " juntou-se.")
		}
	}
    override def withdraw(w:InetSocketAddress):Unit = {
		serverList = serverList.remove { server => (server.serverAddress == w) }
		println("Servidor " + w.toString + " saiu.")
	}
    override def keepAlive(w:InetSocketAddress,sData:ServerData):Boolean = {
		println("Servidor " + w.toString + " actualizou.")
		serverList.filter { server => (server.serverAddress == w) }.foreach { server =>
			server.updateAlive
		}
		serverList.count { server => (server.serverAddress == w) } > 0
	}
	
	private def getAvailableServer = {
		serverList.reduceLeft {
			(x:OnlineServer,y:OnlineServer) => if (calcLoad(x) < calcLoad(y) ) x else y
		}.serverAddress
	}
	
	private def calcLoad(os:OnlineServer):Double = {
		Math.min(os.serverData.cpuLoad,Math.min(os.serverData.networkLoad,os.serverData.memoryLoad))
	}
	
	def database_update(sql:String) = Database.update(sql)
	def database_getUsers:List[User] = Database.getUsers 
	def database_getFriends(name:String):List[Friend] = Database.getFriends(name)
}