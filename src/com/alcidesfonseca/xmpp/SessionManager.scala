package com.alcidesfonseca.xmpp

object SessionManager {
	var sessions:List[Session] = List()
	var cid = 0
	
	def createSession(out:OutChannel) = synchronized {
		var n = new Session(cid,out)
		sessions = sessions.::(n)
		cid += 1
		n
	}
	
	def destroySession(s:Session) = synchronized {
		sessions.remove { ses => (ses == s) }
	}
	
	def getOutChannels(jid:String):List[OutChannel] = synchronized {
		sessions.filter { s => s.jid().startsWith(jid) }.map{i => i.out}
	}
	
	def sendMessage(from:String,to:String,content:String) = synchronized {
		getOutChannels(to).foreach { c => c.write( XMLStrings.message_chat(from,to,content) ) }
	}
	
	def sendPresence(from:String,to:String,content:Any) = synchronized {
		getOutChannels(to).foreach { c => c.write( XMLStrings.presence(from,to,content) ) }
	}
	
}