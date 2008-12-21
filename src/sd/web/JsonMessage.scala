package sd.web

import scala.collection.mutable.HashMap

class Json() {
	var v = HashMap[String, Any]()
	
	def convert(i:Any) = {
		i match {
		    case e:String => "\"" + e + "\""
			case e:Int => e.toString
			case _ => i.toString
		}
	}
	
	override def toString = {
		"{" +  v.keys.map{ key => convert(key) + ":" + convert(v(key)) }.reduceLeft( (a:String,b:String) => a +", " + b ) + "}"
	}
	
	def update(k:String,e:Any) = v.update(k,e)
	def get(k:String) = v.get(k)
}


class JsonMessage(status:String, content:String) {
	var j = new Json()
	j.update("status",status)
	j.update("message",content)
	
	override def toString = j.toString
}
