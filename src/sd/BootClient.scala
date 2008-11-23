import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

object BootClient {
	
	def requestData(s:String,d:String):String = {
		println(s)
		var v:String = Console.readLine
		if ( v.trim.isEmpty ) d else v
	}
	
	def main(args: Array[String]) {
 		var host = requestData("Enter the host:","localhost")
 		var port = requestData("Enter the port:","5222").toInt
 		var username = requestData("Enter your username:","teste")
 		var password = requestData("Enter your username:","teste")
		
		if ( Config.vers.equals("udp") )
			UDPClient.main(host,port,username,password)
		else 
			TCPClient.main(host,port,username,password)
	}
}
