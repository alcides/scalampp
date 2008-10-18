import java.io._
import java.net._

object TCPClient {
	def main(args: Array[String]) = {
		var s:Socket = null
		var in:DataInputStream = null
		var out:DataOutputStream = null
		
		var port = 5222
		var host = "localhost"
		var cycle = true
		var status = 0
	
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
					
					while (true) {
						status match {
						    case 0 => {
								out.writeBytes( XMPP.stream_start("1",host) )
								println("Connecting...")
								status = 1
							}
							case 1 => {
								out.writeBytes( XMPP.auth("alcides","thkhxbq").toString )
								println("Authenticating...")
								status = 2
							}
							case _ => println("Logged!")
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
								out.writeBytes("</stream:stream>")
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

TCPClient.main(null)