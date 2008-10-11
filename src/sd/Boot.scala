//
//  Boot
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

import com.alcidesfonseca.xmpp._
import java.sql.{DriverManager}

object Boot {
	def main(args: Array[String]) {
		Class.forName("org.sqlite.JDBC")
	    val conn = DriverManager.getConnection("jdbc:sqlite:db/dev.db")
		
		TCPserver.main(args)
	}
}
