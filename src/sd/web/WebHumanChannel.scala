package sd.web

import com.alcidesfonseca.xmpp._
import java.io.PrintStream
import org.json._

class WebHumanChannel extends HumanChannel {
	
	var open = true
	var out:PrintStream = System.out
	var contacts:List[Contact] = List()
	var messages:List[Message] = List()

	def updateContacts(l:List[Contact]) = {
		contacts = for ( c <- l) yield c
	}
	
	def insertMessage(m:Message) = {
		messages=messages.::(m)
	}
	
	def retrieveMessages = {
		var ms = messages
		messages = List()
		var arr = new JSONArray()
		for ( m <- ms ) {
			var o = new JSONObject()
			o.put("from",m.from)
			o.put("content",m.content)
			arr.put(o)
		}
		arr
	}
	
	def retrieveRoster = {
		var arr = new JSONArray()
		for ( c <- contacts ) {
			if ( c.jid != "") {
				var o = new JSONObject()
				o.put("jid",c.jid)
				o.put("status",c.status)
				arr.put(o)
			}
		}
		arr
	}
	
	def isOpen = open
	def close = {
		open = false
	}
}