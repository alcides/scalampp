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
	val status = "offline"
}

object Roster {
	var contacts:List[Contact] = List()
	def addContact(c:Contact) = {
		contacts = contacts.::(c)
	}
}

class ClientStream(session:ClientSession) {
	var out = session.out
	
	def parse(x:String):Boolean = {
		if (XMLStrings.check_start(x)) {
			session.setStatus(  session.getStatus + 1 )
			true
		} else {
			var xml =
				try {
					XML.loadString(x)
				} 
				catch {
					case e : org.xml.sax.SAXParseException => null
					case e : parsing.FatalError => null
				}
			
			if (xml != null) {
				println("in: " + xml)
				xml match {
				    case <stream:features>{ _ * }</stream:features> =>  {
						if (session.getStatus <= 1) 
							out.write( XMLStrings.stream_auth("alcides","tkhxbq") )
						else
							out.write( XMLStrings.session_bind_request("alcides_client") )
						true
					}
					
					case <iq><bind><jid>{  jid @ _ * }</jid></bind></iq> => {
						session.setJID(jid(0).toString)
						out.write( XMLStrings.session_request(session.getStanzaId) )
						true
					}
					
					case <iq><session/></iq> => {
						out.write( XMLStrings.roster_request(session.getStanzaId) )
						true
					}
					
					case <iq><query>{ roster @ _ * }</query></iq> => {
						roster(0).foreach { i => Roster.addContact(new Contact((i \ "@name").toString, (i \ "@jid").toString )) }
						Roster.contacts.foreach{ c => println( c.name + ":" + c.status ) }
						out.write(XMLStrings.presence)
						true
					}
					
					case <success/> => {
						out.write( XMLStrings.stream_start_to(session.getHost) )
						true
					}
					case _ => true
				}
				
			} else false
			
		}
	}
}