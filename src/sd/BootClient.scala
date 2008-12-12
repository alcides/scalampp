import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

import sd._
import sd.TCP._
import sd.UDP._
import sd.NS._

import java.rmi._
import java.rmi.registry._

object BootClient {
	var ns = 1	
	def requestData(s:String,d:String):String = {
		println(s)
		var v:String = Console.readLine
		if ( v.trim.isEmpty ) d else v
	}
	
	def fail(c:Int):Int = {
		if (c > 10) {
			ns = (ns + 1) % Config.nsNumber
			0
		} else {
			Thread.sleep( Config.retryNSTimeOut * 1000)
			c+1
		}
	}
	
	def makeUrl(i:Int):String = Config.registryURL + Config.namingServerPrefix + "_" + i.toString
	
	def main(args: Array[String]) {
		var lb:ILoadBalancer = null
		
		var reCount = 0;
		while (true) {
			try {
				lb = Naming.lookup(makeUrl(ns)).asInstanceOf[ILoadBalancer]
				var address = try {
								lb.getServer
							} 
							catch {
								case e : NoServerAvailableException => null
							}
				if (address == null) {
					println("No server available at this time...")
					reCount = fail(reCount)
				} else {
					reCount = 0
					println("Connecting to " + address.toString)
					var host = address.getHostName
			 		var port = address.getPort
			 		var username = requestData("Enter your username:","teste")
			 		var password = requestData("Enter your password:","teste")

					Config.vers match {
					    case "udp" => UDPClient.main(host,port,username,password)
						case _ => TCPClient.main(host,port,username,password)
					}
						
				}
			} 
			catch {
				case e : java.rmi.NotBoundException => {
					println("Connection failed")
					reCount = fail(reCount)
				}
				case e : java.rmi.ConnectException => {
					println("Connection failed")					
				}
			}
		}
	}
}