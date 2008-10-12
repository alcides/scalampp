package com.alcidesfonseca.xmpp

import com.alcidesfonseca.db._

class Session(id:int) {
	var init = false
	var logged = false
	var connected = false
	var user:User = new User("","")
	
	
	def getId() = "c2s_"+ id.toString()
	
	
	def close() = {
		SessionManager.destroySession(this)
	}
}
