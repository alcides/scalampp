import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

import sd._
import sd.TCP._
import sd.UDP._
import sd.NS._

import java.rmi._
import java.rmi.registry._

object BootClient {
	
	def requestData(s:String,d:String):String = {
		println(s)
		var v:String = Console.readLine
		if ( v.trim.isEmpty ) d else v
	}
	
	def main(args: Array[String]) {
		var lb:ILoadBalancer = null
		
		var reCount = 0;
		
		while (true) {
			try {
				lb = Naming.lookup("//localhost/lb1").asInstanceOf[ILoadBalancer]
				reCount = 0
				var address = lb.getServer

		 		var host = address.getHostName
		 		var port = address.getPort
		 		var username = requestData("Enter your username:","teste")
		 		var password = requestData("Enter your username:","teste")

				if ( Config.vers.equals("udp") )
					UDPClient.main(host,port,username,password)
				else 
					TCPClient.main(host,port,username,password)
			} 
			catch {
				case e : java.rmi.NotBoundException => {
					println("Connection failed")
					if (reCount > 10) {
						System.exit(0)	
					} else {
						reCount += 1
						Thread.sleep(5000)
					}
				}
			}
		}
	}
}