package com.alcidesfonseca.db

class Friend(var name:String, val jid:String) {
	var subscription:String = "none"
}

class User(val name:String, val pass:String) {
	
	var online = false
	
	override def toString() = name
	
	def valid = {
		toString() != ""
	}
	
	def setOnline(b:Boolean) = synchronized {
		online = b
	}
	
	def jid() = { name + "@" + "localhost" }
	
	def getFriends = {
		Database.getFriends(name)
	}
	
	def changeFriend(f:Friend,sub:String) = synchronized {
		if ( getFriends.filter { fr => fr.jid == f.jid }.length > 0  ) {
			
			var s = Database.getFriends(name).filter { fri => fri.jid == f.jid }.first.subscription match {
			    case "none" => sub
			 	case "from" => if ( sub == "to" ) "both" else sub
			  	case "to" => if ( sub == "from" ) "both" else sub
				case _ => sub
			}
			
			Database.update("UPDATE friends SET subscription='"+sub+"' where jid='"+f.jid+"'")
			true
		} else {
			Database.update("INSERT INTO friends VALUES (NULL, '"+f.jid+"', '"+f.name+"','"+name+"','"+sub+"');")
			true
		}
	}
	
	def insertFriend(f:Friend) = synchronized {
		if ( getFriends.filter { fr => fr.jid == f.jid }.length > 0  )
			false
		else {
			Database.update("INSERT INTO friends VALUES (NULL, '"+f.jid+"', '"+f.name+"','"+name+"','none');")
			true
		}	
	}
	
	def removeFriend(f:Friend) = synchronized {
		if ( getFriends.filter { fr => fr.jid == f.jid }.length > 0  ) {
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