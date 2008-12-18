package com.alcidesfonseca.xmpp

class XMPPEmptyParser() extends XMPPParser {
	override def parseXML(x:String):Boolean = false
}