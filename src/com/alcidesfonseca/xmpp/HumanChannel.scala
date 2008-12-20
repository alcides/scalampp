package com.alcidesfonseca.xmpp

abstract class HumanChannel {
	def updateContacts(l:List[Contact])
	def insertMessage(m:Message)
	def close:Unit
}