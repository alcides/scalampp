import java.io._
import java.net._
import com.alcidesfonseca.xmpp._
import scala.xml._
import java.util.Scanner

class TCPClientListener(val s:Socket,val in:DataInputStream, val session:ClientSession) extends Thread {
	var txt:String = ""
	var out = session.out
	var cstream = new XMPPClientParser(session)
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(cstream.parse)
			while ( s.isConnected ) {				
				parser.parse(in.read)
			}
		} 
		catch {
			case e : EOFException => println("EOF: " + e)
			case e : IOException => println("IO: " + e)
		}
	}
}

object TCPClient {
	def main(args: Array[String]) = {
		
		//Ignore STDERR
		//System.setErr(null)
		
		var s:Socket = null
		var in:DataInputStream = null
		var out:SocketOutChannel = null
		
		var port = 5222
		var host = "localhost"
		var cycle = true
		var kb = new Scanner(System.in)
		var commands:Array[String] = null
	
		def connect():Socket = {
			new Socket(host,port)
		}
	
		while (cycle) {
			try {
				s = connect()
				if (s == null)
					cycle = false
				else {
					in = new DataInputStream( s.getInputStream() )
					out = new SocketOutChannel(s)
					var session = new ClientSession(host,out)
					
					if ( args.length >= 1 ) session.user = args(0)
					if ( args.length >= 2 ) session.pass = args(1)
					
					// launch receiver
					new TCPClientListener(s,in,session).start
					
					// start stream
					out.write( XMLStrings.stream_start_to(host) )
					
					
					while (s.isConnected) {
						commands = kb.nextLine().split(" ")
						
						if ( commands(0).equals("send") )
							out.write(XMLStrings.message_chat(commands(1),commands(2)))
						else if ( commands(0).equals("add") )
							out.write(XMLStrings.roster_item_request( session.getStanzaId ,commands(1)) )
					}
					
			
				}
			} 
			catch {
				case e : UnknownHostException => println("Some problems finding that host...")
				case e : EOFException => {
					if (out != null)
						out.write("</stream:stream>")
					if (s != null)
						s.close
					println("Connection terminated...")
				}
				case e : IOException => {
					try {
						println("Reconnecting in 10 seconds...")
						Thread.sleep(10 * 1000)
					} 
					catch {
						case e : InterruptedException => {}
					}
				}
				case _ => {
					if ( s != null ) {
						try {
							s.close
							if (out != null)
								out.write(XMLStrings.stream_end)
							cycle = false
						} 
						catch {
							case e : Exception => {}
						}

					}
				}
			}
		}
		()
	}	
}