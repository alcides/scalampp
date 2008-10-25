import java.nio._
import java.nio.channels._
import java.net._
import java.io._
import java.nio.charset._


import com.alcidesfonseca.xmpp._





/*
class Connection(clientSocket:Socket) extends Thread {
	val in:BufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	var cstream = new XMPPServerParser(new SocketOutChannel(clientSocket))
	val host = clientSocket.getInetAddress()
	val port = clientSocket.getPort()
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(cstream.parse)

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
*/
object TCPServer {
	def main(args: Array[String]) {
		
		//System.setErr(null)
		
		var server:ServerSocketChannel = ServerSocketChannel.open()
		server.configureBlocking(false)

		server.socket().bind(new InetSocketAddress("localhost",8000))
		println("Server a correr na porta 8000")

		var selector = Selector.open(); 
		server.register(selector,SelectionKey.OP_ACCEPT)

		while (true) {
			selector.select

			var keys = selector.selectedKeys
			var iter = keys.iterator
			while (iter.hasNext) {
				var key = iter.next.asInstanceOf[SelectionKey]
				iter.remove

				if (key.isValid) {

					if ( key.isAcceptable ) {
						var client:SocketChannel = server.accept
						client.configureBlocking(false)
						client.register(selector,SelectionKey.OP_READ)
					} else if ( key.isReadable ) {

						var client:SocketChannel = key.channel.asInstanceOf[SocketChannel]

						var buffer = ByteBuffer.allocate(32)
						var nread = 
							try {
								client.read(buffer)
							} 
							catch {
								case e : Exception => -1
							}
						if (nread == -1) {
						    client.close()
							println("Cliente desligou")
						} else {
							buffer.flip

							var s = convertToPrintable(buffer)
							println(s)
						}


					}
				}
			}
		}
	}

	def convertToString(b:ByteBuffer) = {
		var charset = Charset.forName("ISO-8859-1")
		var decoder = charset.newDecoder 
		var charBuffer = decoder.decode(b)
		charBuffer.toString
	}
}