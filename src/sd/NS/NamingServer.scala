package sd.NS;

import java.rmi._
import java.rmi.registry._
import java.rmi.server._

import java.util.{Calendar,GregorianCalendar}
import java.lang.{Runnable,Thread}
import java.util.Scanner
import java.lang.InterruptedException

object NamingServer {
	var lb = new LoadBalancer
	var url:String = null
	
    def main(args: Array[String])
    {
		var sc = new ServerChecker(lb)
		sc.start
		register
		
		var kb = new Scanner(System.in)
		while (true) {
			kb.nextLine match {
			    case "halt" => unregister
				case "restart" => register
				case "exit" => {
					unregister
					System.exit(0)
				}
			}
		}
		
		
    }

	def register = {
		try {
			url = getURL
			Naming.rebind(url, lb)
			println("Server Ready on " + url )
		} 
		catch {
			case e : java.rmi.ConnectException => {
				println("No registry at this time...")
				System.exit(1)
			}
		}
	}
	
	def unregister = {
		if (url != null) Naming.unbind(url)
	}

	def getURL = Config.registryURL + Config.namingServerPrefix + "_" + getOrder

	def getOrder = {
		val results = List.fromArray(Naming.list(Config.registryURL)).map{ x => Integer.parseInt(x.split('_')(1)) }
		if (results.length == 0) 0
		else results.reduceLeft{
				(x:Int,y:Int) => if (x < y) y else x
			} + 1
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
				s => println("> " + s + ":" + s.sessions.length)
			}
			
		}
	}
}