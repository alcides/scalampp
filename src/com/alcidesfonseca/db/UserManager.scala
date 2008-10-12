package com.alcidesfonseca.db

class User(val name:String, val pass:String) {
	
	var online = false
	
	override def toString() = name
	
	def valid = {
		toString() != ""
	}
	
	def setOnline(b:Boolean) = synchronized {
		online = b
	}
	
}


object UserManager {
	var users = Database.getUsers()
	println(users.length)
		
	def auth(plain_auth_string:String) = {
		var result = users.filter { u => ( (u.name + u.pass) == plain_auth_string) }
		if (result.length == 0) {
			new User("","")
		} else {
			result(0)
		}
		
	}
	
	
}