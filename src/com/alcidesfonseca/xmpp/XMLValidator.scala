package com.alcidesfonseca.xmpp

import java.io._
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation._
import javax.xml.parsers._
import org.xml.sax.SAXException
import org.xml.sax._


object XMLValidator {
	var factory = SAXParserFactory.newInstance()
	factory.setValidating(true)
	factory.setNamespaceAware(true)

	var parser = factory.newSAXParser()

	var reader = parser.getXMLReader()
	reader.setErrorHandler(null)
	
	def validate(x:String):Boolean = {
		var s = new InputSource(new StringReader(x))
		try {
			reader.parse(s)
			true
		} 
		catch {
			case e : SAXParseException => false
		}
	}
}








