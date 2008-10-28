import java.lang.{Runnable,Thread}
import java.io._
import java.net._

import com.alcidesfonseca.xmpp._

object UDPClient {
	
	val port = 5222
	val host = InetAddress.getByName("localhost");
	
	def main(args: Array[String]) {
		println("Hello, world!")
	
		var aSocket = new DatagramSocket()

		var buf = "teste".getBytes
		var request = new DatagramPacket(buf,buf.length,host,port)
		aSocket.send(request)
		println("sent")
		
		var b = new Array[Byte](1023)
		var reply = new DatagramPacket(b,b.length)
		aSocket.receive(reply)
		
		println(new String(reply.getData).trim())
	}
}