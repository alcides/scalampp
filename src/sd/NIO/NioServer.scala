import java.nio._
import java.nio.channels._
import java.net._
import java.io._
import java.nio.charset._


import com.alcidesfonseca.xmpp._
import scala.collection.mutable.HashMap

object NioServer {
	def main(args: Array[String]) {
		var port:int = if (args.length > 1) Integer.parseInt(args(1)) else 5222;
		println("A escuta no porto " + port)
		
		var parsers = new HashMap[SocketChannel,XMLParser]
		
		//System.setErr(null)
		
		var server:ServerSocketChannel = ServerSocketChannel.open()
		server.configureBlocking(false)

		server.socket().bind(new InetSocketAddress("localhost",port))

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
						parsers.put(client, new XMLParser(new XMPPServerParser(new NioOutChannel(client))))
						
					} else if ( key.isReadable ) {

						var client:SocketChannel = key.channel.asInstanceOf[SocketChannel]

						var buffer = ByteBuffer.allocate(256)
						var nread = 
							try {
								client.read(buffer)
							} 
							catch {
								case e : Exception => -1
							}
						if (nread == -1) {
						    client.close()
						} else {
							buffer.flip

							var s = convertToString(buffer)
							
							parsers(client).parseString(s)
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