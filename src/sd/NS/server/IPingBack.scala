package sd.NS.server;

import java.rmi._
import java.net._

@remote
trait IPingBack extends Remote
{
     def ping:Boolean
}