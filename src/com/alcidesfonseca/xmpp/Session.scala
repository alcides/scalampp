package com.alcidesfonseca.xmpp

import com.alcidesfonseca.db._
import java.net.InetAddress

class Session(id:int) {
	var init = false
	var logged = false
	var connected = false
	var active = false
	var user:User = new User("","")
	var resource = ""
	
	
	def getId() = "c2s_"+ id.toString()
	
	def jid() = user.name + "@" + InetAddress.getLocalHost.getHostName + "/" + resource
	
	def close() = {
		SessionManager.destroySession(this)
	}
	
	def makeActive = {
		active = true
		user.setOnline(true)
	} 
	
}
