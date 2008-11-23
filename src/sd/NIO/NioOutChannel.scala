//
//  SocketOutChannel
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

package sd.NIO

import com.alcidesfonseca.xmpp._
import java.net.Socket
import java.nio._
import java.nio.channels._

class NioOutChannel(var socket:SocketChannel) extends OutChannel {
	def write(x:String) = synchronized {
		if (Config.debug)  println("out: " + x)
		socket.write(ByteBuffer.wrap(x.getBytes))
	}
	def close = socket.close
}