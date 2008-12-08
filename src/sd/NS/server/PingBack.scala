package sd.NS.server;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._
import com.alcidesfonseca.xmpp._
import scala.collection.mutable.{HashMap,Map}


@serializable
class PingBack extends UnicastRemoteObject with IPingBack
{
	override def ping:Boolean = {
		true
	}
	
	def deliver(jid:String,what:String):Boolean = {
		SessionManager.send(jid,what)
	}
	
	def updateSessions(foreignSessions:Map[String,IPingBack]) = {
		SessionManager.remoteSessions = foreignSessions
	}
}