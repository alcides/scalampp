package sd.cli

import com.alcidesfonseca.xmpp._
import java.io._

class CLIHumanChannel extends HumanChannel {
	
	var out:PrintStream = System.out
	var contacts:List[Contact] = List()
	var messages:List[Message] = List()

	def updateContacts(l:List[Contact]) = {
		contacts = l
		println("----------")
		contacts.foreach {
			c => println( c.jid + ": " + c.status)
		}
		println("----------")
	}
	
	def insertMessage(m:Message) = {
		messages=messages.::(m)
		println(m.toString)
	}

	def close = System.exit(0)
}