import java.io._
import java.net._
import com.alcidesfonseca.xmpp._

class TCPClientListener(val s:Socket,val in:DataInputStream) extends Thread {
	var txt:String = ""
	override def run = {
		while (s.isConnected) {
			txt += in.read()
			if (XMLStrings.check_start(txt)) {
				txt = ""
				println("started")
			}
		}
	}
}

object TCPClient {
	def main(args: Array[String]) = {
		var s:Socket = null
		var in:DataInputStream = null
		var out:DataOutputStream = null
		
		var port = 5222
		var host = "localhost"
		var cycle = true
		var status = 1
	
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
					out = new DataOutputStream( s.getOutputStream() )
					
					// launch receiver
					new TCPClientListener(s,in).start
					
					while (true) {
						status = status match {
						    case 1 => {
								out.writeBytes( XMLStrings.stream_start("1",host) )
								println("Connecting...")
								0
							}
							case 2 => {
								out.writeBytes( XMLStrings.stream_auth("alcides","thkhxbq").toString )
								println("Authenticating...")
								0
							}
							case 3 => {
								println("Logged!")
								0
							}
							case _ => 0
						}
					}
					
			
				}
			} 
			catch {
				case e : UnknownHostException => println("Some problems finding that host...")
				case e : EOFException => {
					if (out != null)
						out.writeBytes("</stream:stream>")
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
								out.writeBytes(XMLStrings.stream_end)
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