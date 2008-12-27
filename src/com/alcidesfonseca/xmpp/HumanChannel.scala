package com.alcidesfonseca.xmpp

abstract class HumanChannel {
	def updateContacts(l:List[Contact])
	def insertMessage(m:Message)
	def insertRequest(jid:String)
	def popRequest:String
	def hasRequest:Boolean	
	def close:Unit
}