import java.lang.{Runnable,Thread}
import java.io._
import java.net._

import com.alcidesfonseca.xmpp._
import scala.xml._
import java.util.Scanner

class UDPClientListener(val s:DatagramSocket, val session:ClientSession) extends Thread {
	var out = session.out
	
	var cstream = new XMPPClientParser(session)
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(cstream)
			while ( true ) {
				var b = new Array[Byte](100000)
				var reply = new DatagramPacket(b,b.length)
				s.receive(reply)
				
				var content = (new String(b)).trim
				
				parser.parseString(content)
			}
		} 
		catch {
			case e : EOFException => println("EOF: " + e)
			case e : IOException => println("IO: " + e)
			case _ => println("wtf?")
		}
	}
}

object UDPClient {
	val port = 5222
	val host = "localhost"
	val hostaddress = InetAddress.getByName(host);
	
	def main(args: Array[String]) {
		
		//Ignore STDERR
		//System.setErr(null)
		
		var cycle = true
		var kb = new Scanner(System.in)
		
		var aSocket = new DatagramSocket()
		
	
		while (cycle) {
			try {
				var out = new DatagramOutChannel(aSocket,hostaddress,port)
				var session = new ClientSession(host,out)
				
				if ( args.length >= 1 ) session.user = args(0)
				if ( args.length >= 2 ) session.pass = args(1)
				
				// launch receiver
				new UDPClientListener(aSocket,session).start
				var cli = new XMPPClientCLI(out,session)
				cli.begin(host)
				while (true) cli.parseInput(kb.nextLine)
			 } 
			catch {
				case e : UnknownHostException => println("Some problems finding that host...")
			} 
		}
		()
	}	
}