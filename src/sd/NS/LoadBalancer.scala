package sd.NS;

import sd.NS.server._

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
		serverList.first.serverAddress
	}
	
	override def join(w:InetSocketAddress,pb:IPingBack):Unit = {
		if ( serverList.count( s => s.serverAddress == w) == 0 ) {
			serverList = serverList.::( new OnlineServer(w,pb) )
			println("Servidor " + w.toString + " juntou-se.")
		}
	}
    override def withdraw(w:InetSocketAddress):Unit = {
		serverList = serverList.remove { server => (server.serverAddress == w) }
		println("Servidor " + w.toString + " saiu.")
	}
    override def keepAlive(w:InetSocketAddress):Boolean = {
		println("Servidor " + w.toString + " actualizou.")
		serverList.filter { server => (server.serverAddress == w) }.foreach { server =>
			server.updateAlive
		}
		serverList.count { server => (server.serverAddress == w) } > 0
	}
}