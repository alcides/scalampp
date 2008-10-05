//
//  Test
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

package com.alcidesfonseca.xmpp;

class Stream {
	
	def parse(x:String):Boolean = {
		x match {
		          case <stream> => true
		          case _ => false
		}
	}
	
}