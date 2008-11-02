import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

object BootClient {
	def main(args: Array[String]) {
		
		if ( args.length == 2) {		
			if ( Config.vers.equals("udp") )
				UDPClient.main(args)
			else 
				TCPClient.main(args)
		} else {
			println("Usage: java -jar <jarfile> <username> <password>")
		}

	}
}
