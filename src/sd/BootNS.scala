import sd._
import sd.NS._

import java.lang.Thread

class InputThread extends Thread {
	override def run() = {
		Console.readLine	
	}
}


object BootNS {
	def main(args: Array[String]) {
		
		var it = new InputThread
		it.start
		
 		NamingServer.main(args)
	}
}
