//
//  DatagramOutChannel
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

import com.alcidesfonseca.xmpp._
import java.net._
import java.io._

class DatagramOutChannel(var sock:DatagramSocket,var req:DatagramPacket) extends OutChannel {
	def write(x:String) = synchronized {
		println("out: " + x)
		var m = x.getBytes()
		
		var reply = new DatagramPacket(m,m.length,req.getAddress(), req.getPort())
		sock.send(reply)
		
	}
}