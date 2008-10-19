package com.alcidesfonseca.db

class Friend(var name:String, val jid:String) {}

class User(val name:String, val pass:String) {
	
	var online = false
	
	override def toString() = name
	
	def valid = {
		toString() != ""
	}
	
	def setOnline(b:Boolean) = synchronized {
		online = b
	}
	
	def getFriends = {
		Database.getFriends(name)
	}
	
}


object UserManager {
	var users = Database.getUsers()
		
	def auth(username:String,pwd:String) = synchronized {
		var result = users.filter { u => ( (u.name == username) && (u.pass == pwd)) }
		if (result.length == 0) {
			new User("","")
		} else {
			result(0)
		}
	}
	
	def createUser(us:String,pw:String) = synchronized {
		if ( users.filter { u => u.name == us }.length > 0  )
			false
		else {
			Database.update("INSERT INTO users VALUES (NULL, '"+us+"', '"+pw+"');")
			users = users.::( new User(us,pw) )
			true
		}
		
	}
		
}