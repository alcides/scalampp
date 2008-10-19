import java.io._
import java.net._
import com.alcidesfonseca.xmpp._
import scala.xml._

class Contact(var name:String, var jid:String) {
	val status = "offline"
}

object Roster {
	var contacts:List[Contact] = List()
	def addContact(c:Contact) = {
		contacts = contacts.::(c)
	}
}

class TCPClientListener(val s:Socket,val in:DataInputStream, val session:ClientSession) extends Thread {
	var txt:String = ""
	var out = session.out
	
	def parse(x:String):Boolean = {
		if (XMLStrings.check_start(x)) {
			session.setStatus(  session.getStatus + 1 )
			true
		} else {
			var xml =
				try {
					XML.loadString(x)
				} 
				catch {
					case e : org.xml.sax.SAXParseException => null
					case e : parsing.FatalError => null
				}
			
			if (xml != null) {
				println("in: " + xml)
				xml match {
				    case <stream:features>{ _ * }</stream:features> =>  {
						if (session.getStatus <= 1) 
							out.write( XMLStrings.stream_auth("alcides","tkhxbq") )
						else
							out.write( XMLStrings.session_bind_request("alcides_client") )
						true
					}
					
					case <iq><bind><jid>{  jid @ _ * }</jid></bind></iq> => {
						session.setJID(jid(0).toString)
						out.write( XMLStrings.session_request(session.getStanzaId) )
						true
					}
					
					case <iq><session/></iq> => {
						out.write( XMLStrings.roster_request(session.getStanzaId) )
						true
					}
					
					case <iq><query>{ roster @ _ * }</query></iq> => {
						roster.foreach { i => println(i) }
							//Roster.addContact(new Contact(i \ "@name", i \ "@jid")) }
						Roster.contacts.foreach{ c => println( c.name + ":" + c.status ) }
						true
					}
					
					case <success/> => {
						out.write( XMLStrings.stream_start_to(session.getHost) )
						true
					}
					case _ => true
				}
				
			} else false
			
		}
	}
	
	override def run = {
		try {
			val parser:XMLParser = new XMLParser(parse)
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
					
					// launch receiver
					new TCPClientListener(s,in,session).start
					
					// start stream
					out.write( XMLStrings.stream_start_to(host) )
					
					
					while (s.isConnected) {
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