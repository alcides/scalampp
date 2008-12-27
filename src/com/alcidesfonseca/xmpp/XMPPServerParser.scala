//
//  Stream
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import scala.xml._
import java.io.IOException
import org.publicdomain._
import com.alcidesfonseca.db._

class XMPPServerParser(val out:OutChannel) extends XMPPParser {
	
	var session = SessionManager.createSession(out)

	
	
	def parseXML(x:String):Boolean = {
		
		if (session.init == false) {
			if (XMLStrings.check_start(x)) {
				out.write( XMLStrings.stream_start(session.getId) + XMLStrings.stream_auth_methods )
				session.init = true
				true
			} else {
				false
			}
			
		} else {
			
			if (x == XMLStrings.stream_end) {
				SessionManager.destroySession(session)
				true
			}

			if (session.logged == false) {
					//stream is initialized, so here we should be looking for stanzas
					
					if ( XMLValidator.validate(x) ) {

						try {
							var xml = XML.loadString(x)
					
							xml match {
						
								case <iq><query><username>{ us @ _ * }</username><password>{ pw @ _ * }</password></query></iq> => {
									var usern = us(0).toString
									var passw = pw(0).toString
							
									if (UserManager.createUser(usern,passw)) {
										out.write(XMLStrings.register_success( (xml \ "@id").toString ))
									} else {
										out.write( XMLStrings.register_error( (xml \ "@id").toString,usern,passw ))
									}
									true
								}
						
								case <iq><query /></iq> => {
									if ( (xml \ "query").first.namespace == "jabber:iq:register")
										out.write(XMLStrings.register_info((xml \ "@id").toString))
									true
								}
						
								case <auth>{ inside @ _ * }</auth> => {
							
									var decoded = ""
									var splitted = Array[String]()
							
									try {	
										decoded = new String(Base64.decode( inside(0).toString() ))
										splitted = decoded.split("\0") // \0 + username + \0 + pass
										()
									} 
									catch {
										case e : Exception => out.write(XMLStrings.stream_auth_incorrect_encoding)
									}

									var u = UserManager.auth(splitted(1),splitted(2))
							
									if ( u.valid )  {
										out.write(XMLStrings.stream_auth_accepted.toString())
										session.logged = true
										session.user = u
									} else {
										out.write(XMLStrings.stream_auth_failed + XMLStrings.stream_end)
									}

									true
								}
							    case _ => false 
							}
						} 
						catch {
							case e : org.xml.sax.SAXParseException => false
							case e : parsing.FatalError => false
						}
					} else false
			} else {
				// logged in
				if (XMLStrings.check_start(x)) {
					out.write( XMLStrings.stream_start(session.getId) + XMLStrings.stream_features)
					true
				} else {
					if ( XMLValidator.validate(x) ) {
						var xml =
							try {
								XML.loadString(x)
							} 
							catch {
								case e : org.xml.sax.SAXParseException => null
								case e : parsing.FatalError => null
							}
					
						if (xml != null) {
							xml match {
								case <iq><bind><resource>{ res @ _ * }</resource></bind></iq> => {
									session.resource = res(0).toString
									out.write(XMLStrings.session_bind((xml \ "@id").toString,session.jid))
								}
								case <iq><session /></iq> => {
									session.makeActive
									out.write(XMLStrings.session_set((xml \ "@id").toString))
								}
								case <iq><query /></iq> => {
									if ( (xml \ "query").first.namespace == "jabber:iq:roster" && (xml \ "@type").toString == "get") {
										out.write(XMLStrings.roster( (xml \ "@id").toString,session.user.getFriends ))
									}
								}
								case <iq><query>{ items @ _ * }</query></iq> => {
									if ( (xml \ "query").first.namespace == "jabber:iq:roster" && (xml \ "@type").toString == "set") {
										var f:Friend = null
										var fname = ""
										var method = ""
										items(0).foreach { i =>
										
											fname = (i \ "@name").toString
											if ( fname.equals("") ) fname = (i \ "@jid").toString
										
											f = new Friend(fname, (i \ "@jid").toString)
										
											method = (i \ "@subscription").toString
											if (method == "remove") {
												session.user.removeFriend(f) // Deletes from database
											} else {
												method = "none"
												session.user.insertFriend(f) // Inserts in database
											}
											// Broadcasts
											SessionManager.send(session.shortJid,XMLStrings.roster_set(List(f),method))
										}
									
										// Confirmation
										out.write(XMLStrings.roster_item_sent( (xml \ "@id").toString,session.jid ))
										
									}
								}
								
								case <presence>{ content @ _ * }</presence> => {
									
									if ( (xml \ "@type").length == 0 ) {
										// general
									
										if (session.priority == 0) {
											// initial session probing
											session.user.getFriends.filter { j => 
													j.subscription.equals("both") || j.subscription.equals("to")  
												}.foreach { j =>
													// Commented to be implemented with multiple servers
													//SessionManager.sendProbePresence(session.jid,j.jid)
													if ( SessionManager.count(j.jid) > 0)
														SessionManager.sendPresence(j.jid,session.jid, <status>online</status>)
												
												}
										}
									
										if ( xml.descendant.count( e => e == <priority /> ) > 0 )
											session.setPriority( (xml \ "priority").text.toInt)
									
										if ((xml \ "@to").length == 0) {
											// Broadcasts
											session.user.getFriends.filter { f => 
													f.subscription.equals("from") || f.subscription.equals("both")
												}.foreach { f =>
													SessionManager.sendPresence(session.jid,f.jid, content)
												}	
										} else {
											SessionManager.sendPresence(session.jid,( xml \ "@to" ).toString,content)
										}
									
									
									} else if ( (xml \ "@type").toString == "subscribe" ) {
										
										var to = (xml \ "@to").toString
										SessionManager.send(to,XMLStrings.presence_subscribe(to,session.shortJid))
										
									} else if ((xml \ "@type").toString == "subscribed") {
										var to = (xml \ "@to").toString
									
										session.user.changeFriend(new Friend("",to),"from") // changing in the contact that accepted
									
										UserManager.users.filter{ us => us.jid == to }.foreach { 
											// changing in the contact that requested
											u => u.changeFriend(new Friend("",session.shortJid),"to") 
										}
									
										SessionManager.send(to,XMLStrings.presence_subscribed(to,session.shortJid))
										SessionManager.send(to,XMLStrings.presence(to,session.shortJid))

									} else if ((xml \ "@type").toString == "unsubscribed") {
										var to = (xml \ "@to").toString
									
										UserManager.users.filter{ us => us.jid == to }.foreach { 
											// changing in the contact that requested
											u => u.changeFriend(new Friend("",session.shortJid),"none") 
										}
									
										SessionManager.send(to,XMLStrings.presence_unsubscribed(to,session.shortJid))

									} else if ((xml \ "@type").toString == "unavailable") {
										if ((xml \ "@to").length == 0) {
											// Broadcasts
											session.user.getFriends.filter { f => 
													f.subscription.equals("from") || f.subscription.equals("both")
												}.foreach { f =>
													SessionManager.sendOfflinePresence(session.jid,f.jid, content)
												}	
										} else {
											SessionManager.sendOfflinePresence(session.jid,( xml \ "@to" ).toString,content)
										}
										
										// Removes the session from the server
										SessionManager.destroySession(session)
									}
									
									
								}
							
								case <message>{ content @ _ * }</message> => {
									SessionManager.sendMessage(session.jid,
										(xml \ "@to").toString,
										content(0).text)
								}
								case _ => {
									// println("ignored")
								}
							}
							true
						} else false
					} else false
				}
			}
			
		
		}

	}
	
}