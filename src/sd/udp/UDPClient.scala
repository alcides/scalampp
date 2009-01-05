package sd.udp

import java.lang.{Runnable,Thread}
import java.io._
import java.net._

import sd._
import sd.cli._
import com.alcidesfonseca.xmpp._
import scala.xml._
import java.util.Scanner

class UDPClientListener(val s:DatagramSocket, val session:ClientSession,val hc:HumanChannel) extends Thread {
	var out = session.out
	
	var cstream = new XMPPClientParser(session, hc)
	
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
			case e : EOFException => {} //println("EOF: " + e)
			case e : IOException => {} //println("IO: " + e)
			case _ => println("wtf?")
		}
	}
}

object UDPClient {
	
	def main(host:String,port:Int,username:String,password:String) = {
		
		//Ignore STDERR
		//System.setErr(null)
		
		var cycle = true
		var kb = new Scanner(System.in)
		
		var aSocket = new DatagramSocket()
		val hostaddress = InetAddress.getByName(host);
	
		while (cycle) {
			try {
				var out = new DatagramOutChannel(aSocket,hostaddress,port)
				var session = new ClientSession(host,out)
				
				session.user = username
				session.pass = password
				
				// launch receiver
				var chc = new CLIHumanChannel()
				new UDPClientListener(aSocket,session,chc).start
				var cli = new XMPPClientCLI(out,session,chc)
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