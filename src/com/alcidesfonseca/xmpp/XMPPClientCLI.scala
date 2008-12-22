package com.alcidesfonseca.xmpp

class XMPPClientCLI(var out:OutChannel, var session:ClientSession) {
	
	var mode = 0
	var u:String = ""
	
	def begin(host:String) = {
		out.write( XMLStrings.stream_start_to(host) )
	}
	
	def parseInput(in:String) = {
		
		mode match {
		    case 0 => {

				var commands = in.split(" ")

				// Set status
				if ( commands(0).equals("quit") ) {
					out.write( XMLStrings.presence_unavailable(session.getJID) )
					out.write(XMLStrings.stream_end)
					out.close
					System.exit(0)
				}

				// Send message
				if ( commands(0).equals("send") ) {
					if (commands.length < 3) println("Erro: numero errado de parametros")
					else out.write( XMLStrings.message_chat(commands(1),commands.drop(2).mkString(" ") ))
				}
				
				// Add Contact
				if ( commands(0).equals("add") ) {
					if (commands.length < 2) println("Erro: numero errado de parametros")
					else {
						out.write( XMLStrings.roster_item_request( session.getStanzaId ,commands(1)) )
						out.write( XMLStrings.presence_subscribe( commands(1), session.getJID) )
					}
				} 

				// Remove Contact
				if ( commands(0).equals("del") ) {
					if (commands.length < 2) println("Erro: numero errado de parametros")
					else {
						out.write( XMLStrings.roster_item_remove( session.getStanzaId ,commands(1)) )
						out.write( XMLStrings.presence_unsubscribe( commands(1), session.getJID) )
					}
				}

				// Set status
				if ( commands(0).equals("set") ) {
					if (commands.length < 2) println("Erro: numero errado de parametros")
					else out.write( XMLStrings.presence_set(commands(1)) )
				}
				
				// Refresh
				if ( commands(0).equals("refresh") ) {
					out.write(XMLStrings.roster_request(session.getStanzaId))
				}
			}
			case 1 => {
				if ( in.equals("sim") ) {
					out.write(XMLStrings.presence_subscribed(u,session.getJID))
				} else {
					out.write(XMLStrings.presence_unsubscribed(u,session.getJID))					
				}
				u = ""
				mode = 0
			}	
		}
		
		if ( session.requests.length > 0 ) {
			u = session.requests.head
			session.requests = session.requests.drop(1)
			println( "O utilizador " + u + " deseja adiciona-lo. Quer aceita-lo?" )
			mode = 1
		}
		
		
	}
}
