package sd.NS.server

@serializable
class ServerData(
	val cpuLoad:Int, val freeRam:Int, val networkLoad:Int
) {}