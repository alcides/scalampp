package sd.web

class JsonMessage(status:String,content:String) {
	override def toString = "{ \"status\":\""+status+"\", \"message\":\""+ content +"\" }"
}
