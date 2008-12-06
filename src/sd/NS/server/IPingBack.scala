package sd.NS.server;

import java.rmi._
import java.net._
import scala.collection.mutable.{HashMap,Map}


@remote
trait IPingBack extends Remote
{
    def ping:Boolean
	def deliver(jid:String,what:String)
	def updateSessions(foreignSession:Map[String,IPingBack])
}