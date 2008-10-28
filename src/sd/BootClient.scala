import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

object BootClient {
	def main(args: Array[String]) {
		TCPClient.main(args)
		//UDPClient.main(args)
	}
}
