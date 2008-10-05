import java.lang.{Runnable,Thread}
import java.io._
import java.net._

import com.alcidesfonseca.xmpp._

class Connection(clientSocket:Socket) extends Thread {
	val in:BufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	val out:DataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
	var cstream = new Stream()
	val host = clientSocket.getInetAddress()
	val port = clientSocket.getPort()
	
	override def run = {
		try {
			var data_to_parse = ""
			var code = ""
			while ( clientSocket.isConnected() ) {				
				code = Helper.asciiDecoder(in.read())
				if (code != "") {
					data_to_parse += code
				}
				if (code == ">") {
					
					data_to_parse = cstream.parse(data_to_parse) match {
					    case true => ""
						case false => data_to_parse
					}
					if (cstream.init == 1) println(host + ":" + port + " initialized a valid stream")
				}
				
				()
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

object TCPserver {
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