package com.alcidesfonseca.xmpp

class Message(var from:String, var content:String) {
	override def toString = {
		"* " + from + " said: " + content
	}
	
	def toXML = <message from={ from }>{ content }</message>
}
