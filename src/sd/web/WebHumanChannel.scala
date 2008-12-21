package sd.web

import com.alcidesfonseca.xmpp._
import java.io.PrintStream

class WebHumanChannel extends HumanChannel {
	
	var open = true
	var out:PrintStream = System.out
	var contacts:List[Contact] = List()
	var messages:List[Message] = List()

	def updateContacts(l:List[Contact]) = {
		contacts = l
	}
	
	def insertMessage(m:Message) = {
		messages=messages.::(m)
	}
	
	def isOpen = open
	def retrieveMessages = {
		var ms = messages
		messages = List()
		ms.toArray
	}
	def close = {
		open = false
	}
}