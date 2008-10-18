package com.alcidesfonseca.xmpp

import com.alcidesfonseca.db._
import java.net.InetAddress

class Session(id:int,outc:OutChannel) {
	var init = false
	var logged = false
	var connected = false
	var active = false
	var user:User = new User("","")
	var resource = ""
	var out = outc
	var priority = 0
	
	def getId() = "c2s_"+ id.toString()
	
	def jid() = user.name + "@" + InetAddress.getLocalHost.getHostName + "/" + resource
	
	def shortJid() = user.name + "@" + InetAddress.getLocalHost.getHostName
	
	def close() = {
		SessionManager.destroySession(this)
	}
	
	def makeActive = {
		active = true
		user.setOnline(true)
	} 
	
	def setPriority(p:Int) {
		priority = p
	}
	
}
