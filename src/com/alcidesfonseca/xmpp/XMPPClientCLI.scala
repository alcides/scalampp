package com.alcidesfonseca.xmpp

class XMPPClientCLI(var out:OutChannel, var session:ClientSession) {
	
	def begin(host:String) = {
		out.write( XMLStrings.stream_start_to(host) )
	}
	
	def parseInput(in:String) = {
		var commands = in.split(" ")
		
		if ( commands(0).equals("send") )
			out.write( XMLStrings.message_chat(commands(1),commands(2)) )
		else if ( commands(0).equals("add") )
			out.write( XMLStrings.roster_item_request( session.getStanzaId ,commands(1)) )
		else if ( commands(0).equals("set") )
			out.write( XMLStrings.presence_set(commands(1)) )
	}
}
