package com.alcidesfonseca.xmpp

import scala.collection.mutable.HashMap
import sd.NS.server.IPingBack

import java.net.InetAddress

object SessionManager {
	var sessions:List[Session] = List()
	var remoteSessions = HashMap[String, IPingBack]()
	var cid = 0
	
	def exportSessions:List[String] = sessions.filter { s => s.logged }.map { s => s.jid }
	
	def createSession(out:OutChannel) = synchronized {
		var n = new Session(cid,out)
		sessions = sessions.::(n)
		cid += 1
		n
	}
	
	def closeSession(out:OutChannel) = synchronized {
		sessions.filter { s => s.out == out }.foreach {
			s => destroySession(s)
		}
	}
	
	def destroySession(s:Session) = synchronized {
		
		sessions = sessions.remove { ses => (ses == s) }
		if ( sessions.count { ses => ses.jid.startsWith(s.shortJid)} == 1) {
			s.user.getFriends.foreach { f =>
				try {
					sendOfflinePresence(s.jid,f.jid)
				} 
				catch {
					case e : Exception => ()
				}
			}
		}
		s.out.close
				
	}
	
	def count(jid:String):int = synchronized {
		sessions.count { s => s.jid().startsWith(jid) }
	}
	
	def getOutChannels(jid:String):List[OutChannel] = synchronized {
		sessions.filter { s => s.jid().startsWith(jid) }.map{i => i.out}
	}
	
	def getForeignChannels(jid:String):List[IPingBack] = synchronized {
		remoteSessions.filterKeys { j => j.startsWith(jid) }.values.toList
	}
	
	def send(jid:String,content:Any):Boolean = send(jid,content.toString)
	
	def send(jid:String,content:String):Boolean = send(jid,content,false,"")
		
	def send(jid:String,content:Any,errorNotification:Boolean,from:String):Boolean = send(jid,content.toString,errorNotification,from)
		
	def send(jid:String,content:String,errorNotification:Boolean,from:String):Boolean = synchronized {
		var sent = 0
		var errors = 0
		getOutChannels(jid).foreach { o => 
			try {
				o.write(content) 
			 	sent += 1
			} 
			catch {
				case e : Exception => {
					closeSession(o)
					if (errorNotification) send(from, XMLStrings.message_error(from, jid).toString ,false,"")
			 		errors += 1
				}
			}
		}
		getForeignChannels(jid).foreach { pb => 
			try {
				pb.deliver(jid,content)
				sent += 1
			} 
			catch {
				case e : Exception => {
					if (errorNotification) send(from, XMLStrings.message_error(from, jid).toString ,false,"")					
			 		errors += 1
				}
			}
		}
		return sent > 0
	}
	
	def sendMessage(from:String,to:String,content:String) = synchronized {
		send(to,XMLStrings.message_chat(from,to,content),true,from)
	}
	
	def sendPresence(from:String,to:String,content:Any) = synchronized {
		send(to,XMLStrings.presence(from,to,content))
	}
	
	def sendOfflinePresence(from:String,to:String):Unit = synchronized {
		send(to,XMLStrings.presence_unavailable(from,to))
	}
	def sendOfflinePresence(from:String,to:String,content:Any):Unit = synchronized {
		send(to,XMLStrings.presence_unavailable(from,to,content))
	}
	def sendProbePresence(from:String,to:String) = synchronized {
		send(to,XMLStrings.presence_probe(from,to))
	}
	
}