package com.alcidesfonseca.db

import java.sql.{DriverManager,ResultSet}


object Database {
	
	Class.forName("org.sqlite.JDBC")
	val conn = DriverManager.getConnection("jdbc:sqlite:db/dev.db")
	
	def query(sql:String):ResultSet = synchronized {
		val stat = conn.createStatement()
		stat.executeQuery(sql)
	}
	
	def getUsers(): List[User]= {
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
	
	def close = conn.close
	
}