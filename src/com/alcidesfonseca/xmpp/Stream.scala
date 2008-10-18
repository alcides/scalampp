//
//  Stream
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import scala.xml._
import org.publicdomain._
import com.alcidesfonseca.db._

class Stream(out:OutChannel) {
	
	var session = SessionManager.createSession(out)
	
	
	def checkStart(x:String):Boolean = {
		// Verifies if the stream has started
		try {
			var xml = XML.loadString(x + XMLStrings.stream_end)
			true
		} 
		catch {
			case e : Exception => false
		}
	}
	
	
	def parse(x:String):Boolean = {
		
		if (session.init == false) {
			if (checkStart(x)) {
				out.write( XMLStrings.stream_start(session.getId) + XMLStrings.stream_auth_methods )
				session.init = true
				true
			} else {
				false
			}
			
		} else {
			
			if (x == XMLStrings.stream_end) {
				session.close
				false
			}

			if (session.logged == false) {
					//stream is initialized, so here we should be looking for stanzas
					try {
						var xml = XML.loadString(x)
						println("in: " + x)
						
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
						case e : Exception => false
					}
			} else {
				// logged in
				if (checkStart(x)) {
					out.write( XMLStrings.stream_start(session.getId) + XMLStrings.stream_features)
					true
				} else {
					
					var xml =
						try {
							XML.loadString(x)
						} 
						catch {
							case e : Exception => null
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
								if ( (xml \ "query").first.namespace == "jabber:iq:roster") {
									out.write(XMLStrings.roster( (xml \ "@id").toString ))
								}
							}		
							case <presence>{ content @ _ * }</presence> => {
								if ( xml.descendant.count( e => e == <priority /> ) > 0 )
									session.setPriority( (xml \ "priority").text.toInt)
							}
							case <message>{ content @ _ * }</message> => {
								SessionManager.sendMessage(session.jid,
									(xml \ "@to").toString,
									content(0).text)
							}
							case _ => {}
						}
						true
					} else false
				}
			}
			
		
		}

	}
	
}