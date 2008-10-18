package com.alcidesfonseca.xmpp

object SessionManager {
	var sessions:List[Session] = List()
	var cid = 0
	
	def createSession(out:OutChannel) = synchronized {
		var n = new Session(cid,out)
		sessions.::(n)
		cid += 1
		n
	}
	
	def destroySession(s:Session) = synchronized {
		sessions.remove { ses => (ses == s) }
	}
	
	def getOutChannel(jid:String):OutChannel = synchronized {
		sessions.filter{ s => s.shortJid().contains(jid) }(0).out
	}
	
}
