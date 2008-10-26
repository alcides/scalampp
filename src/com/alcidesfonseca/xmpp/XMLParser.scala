package com.alcidesfonseca.xmpp

class XMLParser(p: String=>Boolean) {
	var code = ""
	var data_to_parse = ""
	
	def doParse() {
		println("going to parse" + data_to_parse)
		data_to_parse = p(data_to_parse) match {
		    case true => ""
			case false => data_to_parse
		}
	}
	
	def parse(in:int):Unit = {
		code = filter(in)
		if (code != "") {
			data_to_parse += code
		}
		
		if ( code == ">" ) doParse
	}
	
	def parse(in:String):Unit = {
		in.foreach { c =>
			data_to_parse += filter(c)
			if ( c == '>' ) doParse
		}
	}
	
	def filter(i:Int):String = {
		if ( i >= 32 && i <=126 )
			i.asInstanceOf[Char].toString();
		else
			return "";
	}
	
	def filter(c:Char):String = filter(c.asInstanceOf[Int])
}