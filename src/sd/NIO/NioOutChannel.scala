import com.alcidesfonseca.xmpp._
import java.io.DataOutputStream
import java.net.Socket
import java.nio._

class SocketOutChannel(socket:Socket) extends OutChannel {
	
	val out:DataOutputStream = new DataOutputStream(socket.getOutputStream())
	
	def write(s:String):Unit = synchronized {
		out.writeBytes(s)
		println("out: " + s)
		()
	}

}