import java.lang.{Runnable,Thread}
import java.io._
import java.net._

import com.alcidesfonseca.xmpp._

class Connection(clientSocket:Socket) extends Thread {
	val in:BufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	var cstream = new Stream(new SocketOutChannel(clientSocket))
	val host = clientSocket.getInetAddress()
	val port = clientSocket.getPort()
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(cstream)

			while ( clientSocket.isConnected() ) {				
				
				parser.parse(in.read)
			}
		} 
		catch {
			case e : EOFException => println("EOF: " + e)
			case e : IOException => println("IO: " + e)
		}
	}
	println("Received connection from: " + host  + ":" + port )
	start()
}

object TCPServer {
	def main(args: Array[String]) {
		var port:int = if (args.length > 1) Integer.parseInt(args(1)) else 5222;
		println("A escuta no porto " + port)
		var listenSocket:ServerSocket = new ServerSocket(port);
		while (true) {
			var client:Socket = listenSocket.accept()
			var c:Connection = new Connection(client)
			()
		}
	}
}