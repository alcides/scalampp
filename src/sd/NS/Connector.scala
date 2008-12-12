package sd.NS

import sd.Config
import java.rmi._
import java.rmi.registry._

object Connector {
	
	var nsCounter = 0	
	
	def getNamingAddress = {
		try {
			var list = List.fromArray(Naming.list(Config.registryURL)).filter {
				 s => s.contains( Config.namingServerPrefix + "_" )
			}
			
			if (list.isEmpty) null
			else {
				list( nsCounter % list.length )
			}
		} 
		catch {
			case e : Exception => null
		}
	}
	
	
	def getLoadBalancer:ILoadBalancer = {
		var url = getNamingAddress
		var lb:ILoadBalancer = null
		try {
			if ( url != null) {
				println("Connecting to " + url)
				lb = Naming.lookup(url).asInstanceOf[ILoadBalancer]
				lb.ping
				lb
			} else null
		} 
		catch {
			case e:java.rmi.ConnectException => {
				Naming.unbind(url)
				println("Removed " + url)
				null
			}
			
		}
	}
}
