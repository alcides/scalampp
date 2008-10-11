package com.alcidesfonseca.xmpp

object SessionManager {
	var sessions:List[Session] = List()
	var cid = 0
	
	def createSession() = {
		var n = new Session(cid)
		sessions.::(n)
		cid += 1
		n
	}
	
	def destroySession(s:Session) = {
		sessions.remove { ses => (ses == s) }
	}
	
}
