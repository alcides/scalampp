//
//  ClientStream
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import scala.xml._
import org.publicdomain._
import com.alcidesfonseca.db._
import java.lang.Long;

class Contact(var name:String, var jid:String) {
	var status = "offline"
}

object Roster {
	var contacts:List[Contact] = List()
	def addContact(c:Contact) = {
		contacts = contacts.remove { c2 => c2.jid == c.jid } // in case of replace
		contacts = contacts.::(c)
	}
	def removeContact(jid:String) = {
		contacts = contacts.remove(s => s.jid == jid)
	}
}

class XMPPClientParser(session:ClientSession,hc:HumanChannel) extends XMPPParser {
	var out = session.out
	
	
	def clientResource = {
		var r = new Random()
		Long.toString(Math.abs(r.nextLong()), 36)
	}
	
	
	def printRoster = hc.updateContacts(Roster.contacts)
	def printMessage(m:Message) = hc.insertMessage(m)
	
	def exit = hc.close
	
	def parseXML(x:String):Boolean = {
		if (XMLStrings.check_start(x)) {
			session.setStatus(  session.getStatus + 1 )
			true
		} else if ( x == XMLStrings.stream_end ) {
			println("Stream closed by remote server.")
			exit
			true
		} else {
			if ( !XMLValidator.validate(x) ) {
				false
			} else {
				var xml = XML.loadString(x)
				xml match {
				    case <stream:features>{ _ * }</stream:features> =>  {
						if (session.getStatus <= 1) 
							out.write( XMLStrings.stream_auth( session.user, session.pass) )
						else
							out.write( XMLStrings.session_bind_request(clientResource) )
					}
					
					case <success/> => {
						session.logged = 1
						out.write( XMLStrings.stream_start_to(session.getHost) )
					}
					
					case <failure><not-authorized>{  error @ _ * }</not-authorized></failure> => {
						session.logged = -1
						exit
					}
					
					case <iq><bind><jid>{  jid @ _ * }</jid></bind></iq> => {
						session.setJID(jid(0).toString)
						out.write( XMLStrings.session_request(session.getStanzaId) )
					}
					
					case <iq><session/></iq> => {
						out.write( XMLStrings.roster_request(session.getStanzaId) )
					}
					
					case <iq><query>{ roster @ _ * }</query></iq> => {
						if ( roster.length > 0 ) {

							roster(0).foreach { i => 
								if ((i \ "@jid").toString != "") {
									(xml \ "@type").toString match {
								    	case "set" => {
											var method = (i \ "@subscription").toString
											if (method == "remove") {
												Roster.removeContact((i \ "@jid").toString)
											} else {
												Roster.addContact(new Contact((i \ "@name").toString, (i \ "@jid").toString ))	
												out.write(XMLStrings.presence(session.jid))											
											}
										}
										case "result" => {
											Roster.addContact(new Contact((i \ "@name").toString, (i \ "@jid").toString ))	
											out.write(XMLStrings.presence(session.jid))
										}
									}
								}
							}
							printRoster
						}
					}
					
					case <message>{ content @ _ * }</message> => {
						printMessage(new Message((xml \ "@from").toString,content(0).text.toString))
					}
					
					case <presence>{ content @ _ * }</presence> => {
						
						var from = (xml \ "@from").text

						if ( (xml \ "@type").text == "probe") {
							out.write(XMLStrings.presence(session.jid,from))
						}

						if ( (xml \ "@type").text == "unavailable") {
							Roster.contacts.filter { c => from.startsWith(c.jid) }.foreach { c =>
								c.status = "offline"
							}
							printRoster
						} 
						
						if ( (xml \ "@type").text == "subscribe" ) {
							session.requests = session.requests.::(from)
						}

						if ( (xml \ "@type").length == 0 ) {
							
							Roster.contacts.filter { c => from.startsWith(c.jid) }.foreach { c =>
								c.status = if ( (xml \ "show").text == "" ) "online" else (xml \ "show").text
							}
							printRoster
						}
						
					}
					
					case _ => ()
				}
				true
				
			}
		}
	}
}