//
//  Boot
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

import sd._
import sd.NIO._
import sd.TCP._
import sd.UDP._
import sd.NS.server._

import java.net._

object BootServer {
	def main(args: Array[String]) {
		
		var host = InetAddress.getByName("localhost").getHostAddress()
		var port:Int = if (args.length > 1) Integer.parseInt(args(1)) else 5222;
		
		
		var nsUpdateThread = new UpdateNSThread(host,port)
		nsUpdateThread.start
		
		if ( Config.vers.equals("tcp") )
			TCPServer.main(host,port)
		if ( Config.vers.equals("nio") )
			NioServer.main(host,port)
		if ( Config.vers.equals("udp") )
			UDPServer.main(host,port)
	}
}
