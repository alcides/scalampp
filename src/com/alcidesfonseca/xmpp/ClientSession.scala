package com.alcidesfonseca.xmpp

import com.alcidesfonseca.db._
import java.net.InetAddress

class ClientSession(var host:String,var out:OutChannel) {
	
	var stz = 0

	var status = 0
	var priority = 0
	var jid = ""
	var user = ""
	var pass = ""
	
	var requests:List[String] = List()
	
	def getStanzaId = synchronized { 
		stz+=1
		"cl_" + jid + "_" + stz 
	}
	
	def getStatus = synchronized { status }
	def setStatus(s:int) = synchronized { status=s }
	
	def getHost = synchronized { host }
	
	def getJID = synchronized { jid }
	def setJID(s:String) = synchronized { jid=s }

	def setLogin(u:String,p:String) = {
		user = u;
		pass = p;
	}

	def isLogged = (jid != "")
}
