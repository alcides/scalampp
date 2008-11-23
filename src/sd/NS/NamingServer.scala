package sd.NS;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

object NamingServer {
     def main(args: Array[String])
    {
           // Here is a declaration of an object from our implementation
           var lb = new LoadBalancer
           // This is the magic of binding this object with the name and the IP address of the server.
           Naming.rebind("//localhost/lb1", lb)
           println("Server Ready")
    }
}