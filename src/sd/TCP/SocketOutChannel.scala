//
//  SocketOutChannel
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//

package sd.TCP

import com.alcidesfonseca.xmpp._
import java.io.DataOutputStream
import java.net.Socket

class SocketOutChannel(socket:Socket) extends OutChannel {
	
	var out:DataOutputStream = new DataOutputStream(socket.getOutputStream())
	
	def write(s:String):Unit = synchronized {
		if (Config.debug)  println("out: " + s)
		
		out.writeBytes(s)
		()
	}
	def close = socket.close
}