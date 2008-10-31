import java.lang.{Runnable,Thread}
import java.io._
import java.net._
import scala.collection.mutable.HashMap

import com.alcidesfonseca.xmpp._

object UDPServer {
	def main(args: Array[String]) {
		var port:int = if (args.length > 1) Integer.parseInt(args(1)) else 5222;
		println("A escuta no porto " + port)
	
		var parsers = new HashMap[SocketAddress,XMLParser]
	
		var aSocket = new DatagramSocket(port)
		while (true) {
			var b = new Array[Byte](100000)
			var request = new DatagramPacket(b,b.length)
			aSocket.receive(request)
			
			if ( !parsers.contains(request.getSocketAddress) ) {
				// first connection
				parsers.put( request.getSocketAddress,  
					new XMLParser(
						new XMPPServerParser(
							new DatagramOutChannel(aSocket,request.getAddress, request.getPort)
						)
					)
				)
			}
			
			var s:String = new String(b)
			parsers(request.getSocketAddress).parseString(s)
			
		}
			
	}
}