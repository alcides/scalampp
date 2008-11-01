//
//  OutChannel
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

abstract class OutChannel {
	def write(s:String):Unit
	def write(s:Any):Unit = synchronized {
		write(s.toString)
	}
	def close():Unit
}