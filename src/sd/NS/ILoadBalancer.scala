package sd.NS;

import sd.NS.server._

import java.rmi._
import java.net._


@remote
trait ILoadBalancer extends Remote
{
	
	var serverList:List[OnlineServer]
	
	def getServer:InetSocketAddress

	def join(w:InetSocketAddress,pb:IPingBack,sData:ServerData):Unit
	def withdraw(w:InetSocketAddress):Unit
	def keepAlive(w:InetSocketAddress,sData:ServerData):Boolean
}