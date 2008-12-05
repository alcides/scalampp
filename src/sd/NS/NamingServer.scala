package sd.NS;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.util._
import java.lang.{Runnable,Thread}

object NamingServer {
     def main(args: Array[String])
    {
           var lb = new LoadBalancer

			new ServerChecker(lb).start

           Naming.rebind("//localhost/lb1", lb)
           println("Server Ready")
    }
}


class ServerChecker(var lb:ILoadBalancer) extends Thread {
	override def run = {
		while (true) {
			Thread.sleep( sd.Config.checkServerRate * 1000 )
			var acceptableTime = new GregorianCalendar()
			acceptableTime.add(Calendar.SECOND, -2 * sd.Config.checkServerRate) 
			// Maximum of 2 times the refresh rate if assynch.
			
			lb.serverList = lb.serverList.remove { s => s.lastSeen.compareTo(acceptableTime.getTime) < 0 && !s.ping }
			
			println("---")
			lb.serverList.foreach {
				s => println("> " + s + ":" + s.serverData.memoryLoad)
			}
			
		}
	}
}