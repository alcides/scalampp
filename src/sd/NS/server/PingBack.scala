package sd.NS.server;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.net._

@serializable
class PingBack extends UnicastRemoteObject with IPingBack
{
	override def ping:Boolean = {
		true
	}
}