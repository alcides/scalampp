package sd.TCP

import java.io._
import java.net._

import sd._
import com.alcidesfonseca.xmpp._
import scala.xml._
import java.util.Scanner

class TCPClientListener(val s:Socket, val session:ClientSession) extends Thread {
	var txt:String = ""
	var out = session.out
	var in = new BufferedReader( new InputStreamReader( s.getInputStream() ))
	var cstream = new XMPPClientParser(session)
	var r:Int = 0
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(cstream)
			while ( s.isConnected ) {
				r = in.read	
				parser.parseInt(r)
			}
		} 
		catch {
			case e : EOFException => {} //println("EOF: " + e)
			case e : IOException => {} //println("IO: " + e)
			// case _ => println("wtf?")
		}
	}
}

object TCPClient {
	def main(host:String,port:Int,username:String,password:String) = {
				
		var s:Socket = null
		var out:SocketOutChannel = null
		
		var cycle = true
		var kb = new Scanner(System.in)
	
		def connect():Socket = {
			new Socket(host,port)
		}
	
		while (cycle) {
			try {
				s = connect()
				if (s == null)
					cycle = false
				else {
					out = new SocketOutChannel(s)
					var session = new ClientSession(host,out)
					
					session.user = username
					session.pass = password
					
					// launch receiver
					new TCPClientListener(s,session).start
					var cli = new XMPPClientCLI(out,session)
					cli.begin(host)
					while (s.isConnected) cli.parseInput(kb.nextLine)
			
				}
			 } 
			catch {
				case e : UnknownHostException => println("Some problems finding that host...")
				case e : EOFException => {
					//println("error")
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
			} 
		}
		()
	}	
}