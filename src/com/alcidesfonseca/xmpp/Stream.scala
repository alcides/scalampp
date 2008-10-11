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
	var init = 0
	def parse(x:String):Boolean = {
	
		println("in: " + x)
		
		if (init == 0) {
			// So here we look for a valid stream. Works with Adium at least
			try {
				var xml = XML.loadString(x + XMLStrings.stream_end)
				
				out.write("<?xml version=\"1.0\" ?><stream:stream from=\"gmail.com\" id=\"559CC06030C3637E\" version=\"1.0\" xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns=\"jabber:client\"><stream:features><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><mechanism>PLAIN</mechanism></mechanisms></stream:features>") //XMLStrings.stream_start("id") + XMLStrings.stream_auth_methods_alt )
				init = 1
				true
			} 
			catch {
				case e : Exception => false
			}
		} else {
			
			if (x == XMLStrings.stream_end) false // Connection closed TODO
			
			//stream is initialized, so here we should be looking for stanzas
			try {
				var xml = XML.loadString(x)
				
				xml match {
					case <auth>{ inside @ _ * }</auth> => {
						println("test")
						
						//var decoded = new String(Base64.decode( inside(0).toString() ))
						
						//println("decoded: " + decoded)
						
						//out.write(XMLStrings.stream_auth_accepted.toString())
						
						// out.write(XMLStrings.stream_auth_failed + XMLStrings.stream_end)
						
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