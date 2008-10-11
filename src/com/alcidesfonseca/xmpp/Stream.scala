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
	
	def parse(x:String):Boolean = {
	
		println("in: " + x)
		
		if (session.init == false) {
			// So here we look for a valid stream. Works with Adium at least
			try {
				var xml = XML.loadString(x + XMLStrings.stream_end)
				
				out.write( XMLStrings.stream_start("id") + XMLStrings.stream_auth_methods_alt )
				session.init = true
				true
			} 
			catch {
				case e : Exception => false
			}
		} else {
			
			if (x == XMLStrings.stream_end) {
				session.close
				false
			}
			
			//stream is initialized, so here we should be looking for stanzas
			try {
				var xml = XML.loadString(x)
				
				xml match {
					case <auth>{ inside @ _ * }</auth> => {
						
						var decoded = new String(Base64.decode( inside(0).toString() ))
						
						println("decoded: " + decoded)
						
						out.write(XMLStrings.stream_auth_accepted.toString())
						
						//out.write(XMLStrings.stream_auth_failed + XMLStrings.stream_end)
						
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