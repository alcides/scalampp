object XMPP {
	
	val xmlInit = "<?xml version='1.0'?>"
	
	def stream_start(id:String,host:String) = xmlInit + "<stream:stream to='"+host+"' id='"+id+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	
	def auth(user:String,pass:String) = <auth>{ Base64.encodeBytes( new String( "\0" + user + "\0" + pass ).getBytes ) }</auth>
}