package sd.NS;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._

@serializable
class LoadBalancer extends ILoadBalancer
{
	override def getServer:InetSocketAddress = {
		new InetSocketAddress("localhost",5222)
	}
}