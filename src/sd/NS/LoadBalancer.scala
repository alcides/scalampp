package sd.NS;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._

@serializable
class LoadBalancer extends ILoadBalancer
{
	var serverList:List[OnlineServer] = List()
	
	override def getServer:InetSocketAddress = {
		serverList.first.serverAddress
	}
	
	override def join(w:InetSocketAddress):Unit = {
		serverList = serverList.::( new OnlineServer(w) )
	}
    override def withdraw(w:InetSocketAddress):Unit = {
		serverList.remove { server => (server.serverAddress == w) }
	}
    override def keepAlive(w:InetSocketAddress):Unit = {
		serverList.filter { server => (server.serverAddress == w) }.foreach { server =>
			server.updateAlive
		}
	}
}