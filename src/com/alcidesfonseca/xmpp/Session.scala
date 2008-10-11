package com.alcidesfonseca.xmpp

class Session(id:int) {
	var init = false
	var logged = false
	def getId() = "c2s_"+ id.toString()
	
	
	def close() = {
		SessionManager.destroySession(this)
	}
}
