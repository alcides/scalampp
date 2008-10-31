import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

object BootClient {
	def main(args: Array[String]) {
		
		if ( Config.vers.equals("udp") )
			UDPClient.main(args)
		else 
			TCPClient.main(args)

	}
}
