package sd.NS

import java.util.GregorianCalendar
import java.net._

class OnlineServer(var serverAddress:InetSocketAddress) {
	var lastSeen = new GregorianCalendar().getTime
	
	def updateAlive = {
		lastSeen = new GregorianCalendar().getTime
	}
}
