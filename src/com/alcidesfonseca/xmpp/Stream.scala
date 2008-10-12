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
	
	var session = SessionManager.createSession
	
	
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
	
		println("in: " + x)
		
		if (session.init == false) {
			if (checkStart(x)) {
				out.write( XMLStrings.stream_start(session.getId) + XMLStrings.stream_auth_methods_alt )
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

						xml match {
							case <auth>{ inside @ _ * }</auth> => {

								var decoded = new String(Base64.decode( inside(0).toString() )) // ,"UTF-8")
								var plain = decoded.toList.filter{ i => i.isLetterOrDigit }.mkString("","","")

								var u = UserManager.auth(plain)
								
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
					
					try {
						var xml = XML.loadString(x)

						xml match {
							case <iq><bind><resource>{ res @ _ * }</resource></bind></iq> => {
								session.resource = res(0).toString
								out.write(XMLStrings.session_bind((xml \ "@id").toString,session.jid))
								true
							}
							case <iq><session /></iq> => {
								out.write(XMLStrings.session_set((xml \ "@id").toString))
								true
							}
							case <iq><query /></iq> => {
								println( xml(0) \ "@xmlns" )
								true
							}
						    case _ => false 
						}
					} 
					catch {
						case e : Exception => false
					}
				}
			}
			
		
		}

	}
	
}