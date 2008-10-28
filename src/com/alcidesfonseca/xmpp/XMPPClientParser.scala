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

class Contact(var name:String, var jid:String) {
	var status = "offline"
}

object Roster {
	var contacts:List[Contact] = List()
	def addContact(c:Contact) = {
		contacts = contacts.remove { c2 => c2.jid == c.jid } // in case of replace
		contacts = contacts.::(c)
	}
}

class XMPPClientParser(session:ClientSession) extends XMPPParser {
	var out = session.out
	
	def parseXML(x:String):Boolean = {
		println("in: " + x)
		if (XMLStrings.check_start(x)) {
			session.setStatus(  session.getStatus + 1 )
			true
		} else {
			
			if ( !XMLValidator.validate(x) ) {
				false
			} else {
				var xml = XML.loadString(x)
			
				println("in: " + xml)
				xml match {
				    case <stream:features>{ _ * }</stream:features> =>  {
						if (session.getStatus <= 1) 
							out.write( XMLStrings.stream_auth( session.user, session.pass) )
						else
							out.write( XMLStrings.session_bind_request("alcides_client") )
					}
					
					case <iq><bind><jid>{  jid @ _ * }</jid></bind></iq> => {
						session.setJID(jid(0).toString)
						out.write( XMLStrings.session_request(session.getStanzaId) )
					}
					
					case <iq><session/></iq> => {
						out.write( XMLStrings.roster_request(session.getStanzaId) )
					}
					
					case <iq><query>{ roster @ _ * }</query></iq> => {
						roster(0).foreach { i => Roster.addContact(new Contact((i \ "@name").toString, (i \ "@jid").toString )) }
						Roster.contacts.foreach{ c => println( c.name + ":" + c.status ) }
						out.write(XMLStrings.presence)
					}
					
					case <success/> => {
						out.write( XMLStrings.stream_start_to(session.getHost) )
					}
					
					case <message>{ content @ _ * }</message> => {
						println( "* " + (xml \ "@from").toString + " says: " + content(0).text )
					}
					
					case <presence>{ content @ _ * }</presence> => {
						
						var from = (xml \ "@from").text
						
						if ( (xml \ "@type").length == 0 ) {
							Roster.contacts.filter { c => c.jid == from }.foreach { c =>
								c.status = "online"
							}
						} else  if ( (xml \ "@type").text == "unavailable") {
							Roster.contacts.filter { c => c.jid == from }.foreach { c =>
								c.status = "offline"
							}
						} 
						
						Roster.contacts.foreach{ c => println( c.name + ":" + c.status ) }
						
					}
					
					case _ => ()
				}
				true
				
			}
			
		}
	}
}