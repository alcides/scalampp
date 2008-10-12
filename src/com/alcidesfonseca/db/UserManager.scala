package com.alcidesfonseca.db

class User(val name:String, val pass:String) {
	override def toString() = name
}


object UserManager {
	var users = Database.getUsers()
		
	def auth(plain_auth_string:String) = {
		var result = users.filter { u => ( (u.name + u.pass) == plain_auth_string) }
		if (result.length == 0) {
			false
		} else {
			result(0)
		}
		
	}
	
	
}