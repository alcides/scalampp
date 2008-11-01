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
		
		// send  presence as offline
		println("removing user "+ s.jid)
		
		
		/*if ( sessions.count { ses => ses.jid.startsWith(s.shortJid)} == 1) {
			s.user.getFriends.foreach { f =>
				try {
					sendOfflinePresence(s.jid,f.jid)
				} 
				catch {
					case e : Exception => // expression
				}
			}
		}*/
		
		s.out.close
		
		// remove
		sessions.remove { ses => (ses == s) }
		
	}
	
	def count(jid:String):int = synchronized {
		sessions.count { s => s.jid().startsWith(jid) }
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
	
	def sendOfflinePresence(from:String,to:String):Unit = synchronized {
		getOutChannels(to).foreach { c => c.write( XMLStrings.presence_unavailable(from,to) ) }
	}
	def sendOfflinePresence(from:String,to:String,content:Any):Unit = synchronized {
		getOutChannels(to).foreach { c => c.write( XMLStrings.presence_unavailable(from,to,content) ) }
	}
	def sendProbePresence(from:String,to:String) = synchronized {
		getOutChannels(to).foreach { c => c.write( XMLStrings.presence_probe(from,to) ) }
	}
	
}