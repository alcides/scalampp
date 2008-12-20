package sd.udp

import java.lang.{Runnable,Thread}
import java.io._
import java.net._
import scala.collection.mutable.HashMap

import sd._
import com.alcidesfonseca.xmpp._

object UDPServer {
	def main(host:String, port:Int) {
		
		println("A escuta no porto " + port)
	
		var parsers = new HashMap[SocketAddress,XMLParser]
	
		var aSocket = new DatagramSocket(port)
		while (true) {
			var b = new Array[Byte](100000)
			var request = new DatagramPacket(b,b.length)
			
			try {
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
			catch {
				case e : Exception => {}
			}

			
		}
			
	}
}