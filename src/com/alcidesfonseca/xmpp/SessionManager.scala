package com.alcidesfonseca.xmpp

object SessionManager {
	var sessions:List[Session] = List()
	var cid = 0
	
	def createSession() = synchronized {
		var n = new Session(cid)
		sessions.::(n)
		cid += 1
		n
	}
	
	def destroySession(s:Session) = synchronized {
		sessions.remove { ses => (ses == s) }
	}
	
}
