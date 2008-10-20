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
	
	def insertFriend(f:Friend) = synchronized {
		if ( getFriends.filter { fr => fr.name == f.name }.length > 0  )
			false
		else {
			Database.update("INSERT INTO friends VALUES (NULL, '"+f.jid+"', '"+f.name+"','"+name+"');")
			true
		}	
	}
	
	def removeFriend(f:Friend) = synchronized {
		if ( getFriends.filter { fr => fr.name == f.name }.length > 0  ) {
			Database.update("DELETE FROM friends WHERE jid='"+f.jid+"' AND user_name='"+name+"';")
			true
		} else false
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