package sd.web.templates

import org.json._

class JSONMessage(status:String, content:String) {
	var j = new JSONObject()
	j.accumulate("status",status)
	j.accumulate("message",content)
	
	override def toString = j.toString
}
