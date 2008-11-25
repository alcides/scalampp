package sd.NS;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._

@serializable
class LoadBalancer extends UnicastRemoteObject(10100) with ILoadBalancer
{
	var serverList:List[OnlineServer] = List()
	
	override def getServer:InetSocketAddress = {
		System.out.println("coiso")
		if (serverList.isEmpty)
			throw new NoServerAvailableException;
		serverList.first.serverAddress
	}
	
	override def join(w:InetSocketAddress):Unit = {
		serverList = serverList.::( new OnlineServer(w) )
		println("Servidor " + w.toString + " juntou-se.")
	}
    override def withdraw(w:InetSocketAddress):Unit = {
		serverList.remove { server => (server.serverAddress == w) }
		println("Servidor " + w.toString + " saiu.")
	}
    override def keepAlive(w:InetSocketAddress):Unit = {
		serverList.filter { server => (server.serverAddress == w) }.foreach { server =>
			server.updateAlive
		}
	}
}