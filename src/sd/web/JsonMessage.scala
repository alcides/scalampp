package sd.web

import org.json._

class JsonMessage(status:String, content:String) {
	var j = new JSONObject()
	j.accumulate("status",status)
	j.accumulate("message",content)
	
	override def toString = j.toString
}
