package sd.ns;

import sd.ns.server._

import java.rmi._
import java.net._
import com.alcidesfonseca.db._


@remote
trait ILoadBalancer extends Remote
{
	
	var serverList:List[OnlineServer]
	
	def ping:Boolean
	def getServer:InetSocketAddress

	def join(w:InetSocketAddress,pb:IPingBack,sData:ServerData):Unit
	def withdraw(w:InetSocketAddress):Unit
	def keepAlive(w:InetSocketAddress,sData:ServerData,sessions:List[String]):Boolean
	
	def database_update(sql:String)
	def database_getUsers:List[User]
	def database_getFriends(name:String):List[Friend]
}