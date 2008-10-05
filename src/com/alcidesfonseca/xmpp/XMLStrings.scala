//
//  XMLStrings
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import java.net.InetAddress;

object XMLStrings {
	def startString(id:String) = "<?xml version='1.0'?><stream:stream from='"+InetAddress.getLocalHost().getHostAddress()+"' id='"+id+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	val authenticationString = "<stream:features><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>PLAIN</mechanism></mechanisms></stream:features>"
}