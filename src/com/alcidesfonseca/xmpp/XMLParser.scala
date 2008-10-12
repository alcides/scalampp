package com.alcidesfonseca.xmpp

class XMLParser(cstream:Stream) {
	var code = ""
	var data_to_parse = ""
	
	def parse(in:int):Unit = {
		code = Helper.asciiDecoder(in)
		if (code != "") {
			data_to_parse += code
		}
		if (code == ">") {
			data_to_parse = cstream.parse(data_to_parse) match {
			    case true => ""
				case false => data_to_parse
			}
		}
	}
}