package com.alcidesfonseca.xmpp

class XMLParser(var s:XMPPParser) {
	var code = ""
	var data_to_parse = ""
	
	def doParse() {
		if (sd.Config.debug) println("in: " + data_to_parse)
		data_to_parse = s.parseXML(data_to_parse) match {
		    case true => ""
			case false => data_to_parse
		}
	}
	
	def parseInt(in:Int):Unit = {
		code = filter(in)
		if (code != "") {
			data_to_parse += code
		}
		
		if ( code == ">" ) doParse
	}
	
	def parseString(in:String):Unit = {
		in.foreach { c =>
			data_to_parse += filter(c)
			if ( c == '>' ) doParse
		}
	}
	
	def filter(i:Int):String = {
		if ( i >= 32 && i <=126 )
			i.asInstanceOf[Char].toString();
		else
			""
	}
	
	def filter(c:Char):String = filter(c.asInstanceOf[Int])
}