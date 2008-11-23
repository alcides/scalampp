package sd.NS;

import java.rmi._
import java.net._

@remote
trait ILoadBalancer extends Remote
{
     def getServer:InetSocketAddress
}