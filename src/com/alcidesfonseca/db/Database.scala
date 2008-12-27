package com.alcidesfonseca.db

import java.sql.{DriverManager,ResultSet}


object Database {
	
	Class.forName("org.sqlite.JDBC")
	val conn = DriverManager.getConnection("jdbc:sqlite:db/dev.db")
	
	def query(sql:String):ResultSet = synchronized {
		val stat = conn.createStatement()
		stat.executeQuery(sql)
	}
	
	def update(sql:String) = synchronized {
		//println("SQL: " + sql)
		val stat = conn.createStatement()
		stat.executeUpdate(sql);
	}
	
	def getUsers(): List[User]= synchronized {
		var l = new java.util.ArrayList[User]
		val rs = query("SELECT * FROM users")
		var u:User = null
		while (rs.next()) {
			u = new User(rs.getString("name"),rs.getString("pass"))
			l.add(u)
		}
		rs.close();
		(for(i <- 0 until l.size) yield l.get(i)).toList
	}
	
	def getFriends(name:String):List[Friend] = synchronized {
		var l = new java.util.ArrayList[Friend]
		
		val rs = query("SELECT * FROM friends WHERE user_name = '" + name + "'")
		var u:Friend = null
		while (rs.next()) {
			u = new Friend(rs.getString("name"),rs.getString("jid"))
			u.subscription = rs.getString("subscription")
			l.add(u)
		}
		rs.close();
		
		(for(i <- 0 until l.size) yield l.get(i)).toList
	}
	
	def updateUser(user:String,pass:String) = {
		update("UPDATE users SET pass='"+pass+"' WHERE name='"+user+"'")
	}
	
	def createUser(user:String,pass:String) = {
		update("INSERT INTO users VALUES (NULL,'"+user+"','"+pass+"')");
	}
	
	def deleteUser(user:String) = {
		update("DELETE FROM friends WHERE user_name = '"+user+"'")
		update("DELETE FROM users WHERE name = '"+user+"'")
	}
	
	def close = conn.close
	
}