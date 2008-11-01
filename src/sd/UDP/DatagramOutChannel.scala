//
//  DatagramOutChannel
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

import com.alcidesfonseca.xmpp._
import java.net._
import java.io._

class DatagramOutChannel(var sock:DatagramSocket,address:InetAddress,port:int) extends OutChannel {
	def write(x:String) = synchronized {
		
		if (Config.debug) println("out: " + x)
		
		var m = x.getBytes()
		
		var reply = new DatagramPacket(m,m.length,address,port)
		sock.send(reply)
		
	}
	def close = sock.close
}