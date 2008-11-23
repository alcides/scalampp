//
//  Boot
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

import com.alcidesfonseca.xmpp._
import com.alcidesfonseca.db._

import sd._
import sd.NIO._
import sd.TCP._
import sd.UDP._

object Boot {
	def main(args: Array[String]) {
		
		if ( Config.vers.equals("tcp") )
			TCPServer.main(args)
		if ( Config.vers.equals("nio") )
			NioServer.main(args)
		if ( Config.vers.equals("udp") )
			UDPServer.main(args)
	}
}
