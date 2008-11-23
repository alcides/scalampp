package sd.NS;

import java.rmi._
import java.net._

@remote
trait ILoadBalancer extends Remote
{
     def getServer:InetSocketAddress

     def join(w:InetSocketAddress):Unit
     def withdraw(w:InetSocketAddress):Unit
     def keepAlive(w:InetSocketAddress):Unit
}