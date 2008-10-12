//
//  Boot
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

object Boot {
	def main(args: Array[String]) {
		
		Database.getUsers.foreach( user => println(user) )
		
		TCPserver.main(args)
	}
}
