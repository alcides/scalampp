//
//  XMLStrings
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import java.net.InetAddress;

object XMLStrings {
	
	val xmlInit = "<?xml version='1.0'?>"
	
	def stream_start(id:String) = xmlInit + "<stream:stream from='localhost' id='"+id+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	
	val stream_end = "</stream:stream>"
	
	val stream_auth_methods = <stream:features>
			<mechanisms xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
				<mechanism>PLAIN</mechanism>
			</mechanisms>
		</stream:features>
		
	val stream_auth_methods_alt = "<stream:features><starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'><required/></starttls><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>X-GOOGLE-TOKEN</mechanism></mechanisms></stream:features>"
		
	val stream_auth_accepted =  <success xmlns="urn:ietf:params:xml:ns:xmpp-sasl"/>
	
	val stream_auth_rejected = <failure xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
	     <not-authorized/>
	   </failure>
		
		
}