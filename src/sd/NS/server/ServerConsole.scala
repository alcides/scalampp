package sd.NS.server

import java.util.Scanner

class ServerConsole(var upThread:UpdateNSThread) extends Thread {
	override def run = {
		
		var kb = new Scanner(System.in)
		while (true) {
			try {
				kb.nextLine match {
				    case "halt" => upThread.pauseOrResume
					case "restart" => upThread.pauseOrResume
					case "exit" => System.exit(0)
					case _ => println("Invalid command.")
				}
			} 
			catch {
				case e : java.io.EOFException => System.exit(0)
				case e : java.util.NoSuchElementException => System.exit(0)
			}
		}
	}
	
	
	
}

