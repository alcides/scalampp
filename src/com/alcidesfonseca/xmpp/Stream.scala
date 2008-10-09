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
	
		println(x)
		
		if (init == 0) {
			// So here we look for a valid stream. Works with Adium at least
			try {
				var xml = XML.loadString(x + XMLStrings.stream_end) 
				out.write(XMLStrings.stream_start(out.getId()) + XMLStrings.stream_auth_methods )
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
						
						out.write(XMLStrings.stream_auth_accepted.toString())
						
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