//
//  Stream
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import scala.xml._

class Stream {
	var init = 0
	def parse(x:String):Boolean = {
		
		if (init == 0) {
			// So here we look for a valid stream. Works with Adium at least
			try {
				var xml = XML.loadString(x + "</stream:stream>") 
				init = 1
				true
			} 
			catch {
				case e : Exception => false
			}
		} else {
			//stream is initialized, so here we should be looking for stanzas
			false
		}

	}
	
}