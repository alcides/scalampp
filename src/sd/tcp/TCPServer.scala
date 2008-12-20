package sd.tcp

import java.lang.{Runnable,Thread}
import java.io._
import java.net._

import sd._
import com.alcidesfonseca.xmpp._

class Connection(clientSocket:Socket) extends Thread {
	val in:BufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	var cstream = new XMPPServerParser(new SocketOutChannel(clientSocket))
	val host = clientSocket.getInetAddress()
	val port = clientSocket.getPort()
	
	def shutdown = {
		SessionManager.destroySession(cstream.session)
	}
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(cstream)

			while ( clientSocket.isConnected() ) {				
				
				parser.parseInt(in.read)
			}
		} 
		catch {
			case e : EOFException => {
				println("EOF: " + e)
			}
			case e : IOException => {
				println("IO: " + e)
			 }
		}
	}
	println("Received connection from: " + host  + ":" + port )
	start()
}

object TCPServer {
	def main(host:String,port:Int) {
		
		//System.setErr(null)
			
		println("A escuta no porto " + port)
		var listenSocket:ServerSocket = new ServerSocket(port);
		while (true) {
			var client:Socket = listenSocket.accept()
			var c:Connection = new Connection(client)
			()
		}
	}
}