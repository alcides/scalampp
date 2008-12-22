package com.alcidesfonseca.db

@serializable
class Friend(var name:String, val jid:String) {
	var subscription:String = "none"
}

@serializable
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
		RemoteDatabase.getFriends(name)
	}
	
	def changeFriend(f:Friend,sub:String) = synchronized {
		if ( getFriends.exists { fr => fr.jid == f.jid }  ) {
			
			var s = RemoteDatabase.getFriends(name).filter { fri => fri.jid == f.jid }.first.subscription match {
			    case "none" => sub
			 	case "from" => if ( sub.equals("to") ) "both" else sub
			  	case "to" => if ( sub.equals("from") ) "both" else sub
				case _ => sub
			}
			
			RemoteDatabase.update("UPDATE friends SET subscription='"+s+"' where jid='"+f.jid+"'")
			true
		} else {
			RemoteDatabase.update("INSERT INTO friends VALUES (NULL, '"+f.jid+"', '"+f.name+"','"+name+"','"+sub+"');")
			true
		}
	}
	
	def insertFriend(f:Friend) = synchronized {
		if ( getFriends.exists { fr => fr.jid == f.jid }  )
			false
		else {
			RemoteDatabase.update("INSERT INTO friends VALUES (NULL, '"+f.jid+"', '"+f.name+"','"+name+"','none');")
			getFriends
			true
		}	
	}
	
	def removeFriend(f:Friend) = synchronized {
		if ( getFriends.exists { fr => fr.jid == f.jid }   ) {
			RemoteDatabase.update("DELETE FROM friends WHERE jid='"+f.jid+"' AND user_name='"+name+"';")
			getFriends
			true
		} else false
	}
	
}


object UserManager {
	var users = RemoteDatabase.getUsers()
		
	def auth(username:String,pwd:String) = synchronized {
		var result = users.filter { u => ( (u.name == username) && (u.pass == pwd)) }
		if (result.length == 0) {
			new User("","")
		} else {
			result(0)
		}
	}
	
	def createUser(us:String,pw:String) = synchronized {
		if ( users.exists { u => u.name == us }  )
			false
		else {
			RemoteDatabase.update("INSERT INTO users VALUES (NULL, '"+us+"', '"+pw+"');")
			users = users.::( new User(us,pw) )
			true
		}
	}
		
}