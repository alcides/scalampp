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
		var l = List()
		val rs = query("SELECT * FROM users")
		while (rs.next()) {
			println(rs.getString("name"))
		 	//(new User(rs.getString("name"),rs.getString("pass"))) :: l
		}
		rs.close();
		l
	}
	
	def close = conn.close
	
}