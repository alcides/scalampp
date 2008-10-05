//
//  Stream
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import scala.xml._

class Stream(out:OutChannel) {
	var init = 0
	def parse(x:String):Boolean = {
		
		if (init == 0) {
			// So here we look for a valid stream. Works with Adium at least
			try {
				var xml = XML.loadString(x + "</stream:stream>") 
				out.write(XMLStrings.startString(out.getId()) + XMLStrings.authenticationString )
				init = 1
				true
			} 
			catch {
				case e : Exception => false
			}
		} else {
			
			if (x == "</stream:stream>") false // Connection closed TODO
			
			//stream is initialized, so here we should be looking for stanzas
			try {
				var xml = XML.loadString(x)
				
				xml match {
					case <auth>{ inside @ _ * }</auth> => {
						println("SECRET: " + inside(0))
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