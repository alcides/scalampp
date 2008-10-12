//
//  Stream
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import scala.xml._
import org.publicdomain._

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

								if ( plain.equals("alcidestkhxbq") ) {
									out.write(XMLStrings.stream_auth_accepted.toString())
									session.logged = true
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
				}
				
				try {
					var xml = XML.loadString(x)

					xml match {
						case <iq><bind><resource>{ res @ _ * }</resource></bind></iq> => {
							println("resource: " + res)
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